package thesmartbros.sagilbe.classes.proveedor;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.tools.PrinterTools;
import thesmartbros.sagilbe.tools.SocketTools;
import thesmartbros.sagilbe.tools.VariablesGlobales;

//clase perteneciente al objeto proveedor

public class Proveedor {

	/* variables principales, de socket */
	private ServerSocket serverSocket = null;

	/* variables secundarias, de smart grid */
	private ArrayList<Zona> zonasList = new ArrayList<Zona>();
	private int consumoTotal = 0;
	private float preciokWh = 0.09f;

	int tecnicos = 10;

	public void start() {
		final boolean listening = true;
		try {
			serverSocket = new ServerSocket(VariablesGlobales._DEFAULT_PROVIDER_PORT);
		} catch (IOException e) {
			PrinterTools.errorsLog("Could not listen on port: " + VariablesGlobales._DEFAULT_PROVIDER_PORT);
			System.exit(-1);
		}
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					while (listening && serverSocket != null)
						startProviderFunctions(serverSocket.accept());
					serverSocket.close();
					serverSocket = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private void startProviderFunctions(Socket socket) {
				PrinterTools.socketLog("Client connected to Provider... OK (" + socket + ")");
				String jsonMessageFromAgregador = SocketTools.getJSON(socket); // desde el socket conseguir el JSON
				Container c = parseJSON(jsonMessageFromAgregador); //parsear el JSON
				if (c.type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_CONSUMO_AGREGADO) {
					synchronized (this) {
						consumoTotal = 0;
						for (int i = 0; i < zonasList.size(); i++)
							consumoTotal += zonasList.get(i).getGasto_energetico();
						preciokWh = VariablesGlobales._MIN_PRICE + ((float) consumoTotal / VariablesGlobales._MAX_ENERGY_GENERATED) * (VariablesGlobales._MAX_PRICE - VariablesGlobales._MIN_PRICE);
						PrinterTools.log("[-------------------------------------------------]");
						PrinterTools.log("[--  PROVIDER received total consumption ---------]");
						PrinterTools.log("[- Consumo: " + consumoTotal + "/" + VariablesGlobales._MAX_ENERGY_GENERATED);
						PrinterTools.log("[- Precio: " + Float.toString(preciokWh) + " EUR/kWh   ");
						PrinterTools.log("[-------------------------------------------------]");
						PrinterTools.log("[-------------------------------------------------]");
					}
					sendPrecioToAgregadores();
				} else if (c.type == VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR) {
					sendPaillierParameters(Integer.valueOf((Integer) c.objeto));

				} else if (c.type == VariablesGlobales._MESSAGE_TYPE_REQUEST_TECNICO) {
					Tiempo tiempo = new Tiempo();
					if (tecnicos == 0) {
						PrinterTools.log("[---------No tenemos tecnicos disponibles--------]");
					} else {
						PrinterTools.log("[Quedan " + tecnicos + " tecnicos, te enviamos uno :) ]");
						tecnicos--;
						tiempo.Contar();
						String calle = getStreet(c.latitud, c.longitud);
						PrinterTools.log("[---------Enviar tecnico a: " + calle + "--------]");
					}
				}

			}
		});
		t.start();
	}

	private void sendPrecioToAgregadores() {
		String jsonMessage = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_ENVIAR_PRECIO_PROVIDER + ", \"price\": \"" + Float.toString(preciokWh) + "\"}";
		for (int i = 0; i < zonasList.size(); i++) {
			int zonaId = zonasList.get(i).getId();
			int port = VariablesGlobales._DEFAULT_AGREGADOR_PORT + zonaId;
			if (SocketTools.send(VariablesGlobales._IP_AGREGADOR, port, jsonMessage))
				PrinterTools.log("[Provider sends data to " + VariablesGlobales._IP_AGREGADOR + ":" + port + ": price is now " + Float.toString(preciokWh) + "]");
			else
				PrinterTools.log("ERROR [Provider sends data to " + VariablesGlobales._IP_AGREGADOR + ":" + port + ": price is now " + Float.toString(preciokWh) + "]");
		}
	}

	private void sendPaillierParameters(int zona) {
		int port = VariablesGlobales._DEFAULT_AGREGADOR_PORT + zona;
		String jsonMessageToAgregador = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_PROVIDER + ", \"g\": \"" + Paillier.getInstance().g.toString() + "\", \"n\": \"" + Paillier.getInstance().n.toString() + "\"}";
		if (SocketTools.send(VariablesGlobales._IP_AGREGADOR, port, jsonMessageToAgregador)) {
			PrinterTools.log("[Provider sends PAILLIER data to AGREGADOR " + VariablesGlobales._IP_AGREGADOR + ":" + port + "]");
		} else
			PrinterTools.log("ERROR [Provider sends PAILLIER data to AGREGADOR " + VariablesGlobales._IP_AGREGADOR + ":" + port + "]");

	}

	private Container parseJSON(String jsonMessage) {
		Container c = new Container();
		Object objeto = null;
		float longitud = 0;
		float latitud = 0;
		JSONObject jsonObject = null;
		int type = -1;
		try { // parsear los datos
			jsonObject = new JSONObject(jsonMessage);
			type = jsonObject.getInt("messageType");
			if (type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_CONSUMO_AGREGADO) {
				int zonaid = jsonObject.getInt("zona");
				// buscar la zona en el arraylist
				int position = 0;
				boolean encontrado = false;
				while (position < zonasList.size() && zonasList.size() != 0 && !encontrado) {
					if (zonasList.get(position).getId() == zonaid)
						encontrado = true;
					else
						position++;
				}
				Zona zonaActual;
				if (encontrado)
					zonaActual = zonasList.get(position);
				else {
					zonaActual = new Zona();
					zonasList.add(zonaActual);
				}
				zonaActual.setId(zonaid);
				zonaActual.setGasto_energetico((Paillier.getInstance().Decryption(new BigInteger(jsonObject.getString("consum"))).intValue()));
				zonaActual.setNumero_viviendas(jsonObject.getInt("viviendas"));
			} else if (type == VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR) {
				objeto = jsonObject.getInt("zona");
			} else if (type == VariablesGlobales._MESSAGE_TYPE_REQUEST_TECNICO) {
				longitud = Float.parseFloat(jsonObject.getString("longitud"));
				latitud = Float.parseFloat(jsonObject.getString("latitud"));
			}
		} catch (NumberFormatException | JSONException e) {
			e.printStackTrace();
		}
		c.type = type;
		c.objeto = objeto;
		c.longitud = longitud;
		c.latitud = latitud;
		return c;
	}

	private String getStreet(float latitud, float longitud) {
		return ToolsMap.getLocationFromName(latitud, longitud);
	}

	private class Container {
		public int type = 0;
		public Object objeto = null;
		public float longitud = 0;
		public float latitud = 0;
	}

	public class Tiempo {
		private Timer timer = new Timer();
		private int segundos = 0;

		//Clase interna que funciona como contador
		class Contador extends TimerTask {
			public void run() {
				segundos++;
				if (segundos == thesmartbros.sagilbe.classes.casa.Contador._THREAD_TIME_INTERVAL*2) {
					tecnicos++;
					timer.cancel();
				}
			}
		}

		//Crea un timer, inicia segundos a 0 y comienza a contar
		public void Contar() {
			this.segundos = 0;
			timer = new Timer();
			timer.schedule(new Contador(), 0, 1000);
		}

	}

}
