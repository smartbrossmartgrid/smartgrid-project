package thesmartbros.sagilbe.classes.agregador;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.tools.PrinterTools;
import thesmartbros.sagilbe.tools.SocketTools;
import thesmartbros.sagilbe.tools.VariablesGlobales;

public class Agregador {
	/* Esta funcion hace de agregador en un entorno de Smart Grid. Las
	 * caracteristicas del agregador son: 1. El mismo detecta cuantos contadores
	 * sirve. Para ello, cuando se inicia espera a que todos los contadores
	 * envien su informacion. En la primera ronda pone que todos los valores son
	 * viejos (para no enviar datos equivocos al proveedor). Para cuando reciba
	 * una nueva muestra, todos los demas contadores tendran que haber enviado
	 * sus consumos. Entonces sera cuando ya actue de forma normal y ya sepa a
	 * cuantos contadores sirve. */

	/* variables principales, de socket */
	public int _CONTADORES_EN_CIUDAD = 1;
	private ServerSocket serverSocket = null;

	/* variables secundarias, de smart grid */
	private int zona = 0;
	private int time = 0;
	private ArrayList<ConjuntoCasas> listaCasas = new ArrayList<ConjuntoCasas>(); // Campo de la clase

	public Agregador(int zonaId) {
		this.zona = zonaId;
	}

	public void start() {
		boolean listening = true;
		int port = VariablesGlobales._DEFAULT_AGREGADOR_PORT + zona;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
			e.printStackTrace();
			System.exit(-1);
		}
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					while (listening && serverSocket != null)
						startAgregadorFunctions(serverSocket.accept());
					serverSocket.close();
					serverSocket = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private void startAgregadorFunctions(Socket socket) {
				PrinterTools.socketLog("Client connected to Agregador... OK");
				String jsonMessageFromContador = SocketTools.getJSON(socket); //desde el socket conseguir el JSON
				//System.out.println(jsonMessageFromContador);
				Container c = parseJSON(jsonMessageFromContador); //parsear el JSON
				if (c.type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_CONSUMO) {
					ConjuntoCasas casa = (ConjuntoCasas) c.objeto;
					/* comprobar que los valores del contador recibido no
					 * existen ya */

					int position = 0;
					boolean existe = false, nuevo = false;
					while (position < listaCasas.size() && !existe && listaCasas.size() != 0) { // buscar contador
						if (casa.getIdcasa() == listaCasas.get(position).getIdcasa()) {
							existe = true;
							nuevo = listaCasas.get(position).isNuevo();
						} else
							position++;
					}
					/* si es la primera vez que la casa entra en la lista,
					 * ponemos isNuevo = false para que no envie nada, ya que el
					 * sistema no estara en regimen permanente */
					if (existe)
						listaCasas.remove(position);
					else
						casa.setNuevo(false);
					listaCasas.add(casa);

					/* comprobamos cuantos contadores tenemos en la zona */
					if (_CONTADORES_EN_CIUDAD < listaCasas.size())
						_CONTADORES_EN_CIUDAD = listaCasas.size();

					/* comprobamos que si estan todos, el estado de todos debe
					 * ser nuevo */
					int size = 0;
					for (int i = 0; i < listaCasas.size(); i++) {
						if (listaCasas.get(i).isNuevo())
							size++;
						if (listaCasas.get(i).getTime() > time)
							time = listaCasas.get(i).getTime();
					}
					if (size == _CONTADORES_EN_CIUDAD) { //si todos son nuevos, enviamos consumos
						enviarConsumosAlProvider();
					} else { // no todos son nuevos, pero... puede que algun contador no funcione...
						// 1. averiguar si mi contador tenia datos nuevos
						if (nuevo) // tenia datos nuevos
							enviarConsumosAlProvider();
						// si tenia datos viejos, ya he hecho antes lo que debia (sustituir valores)
					}
				} else if (c.type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_PRECIO_PROVIDER) {
					Float preciokWh = (Float) c.objeto;
					sendPrecioToContadores(preciokWh);
				} else if (c.type == VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS) {
					retrievePaillierParameters();
					sendPaillierParameters(socket);
				}
				/* try { socket.close(); } catch (IOException e) {
				 * e.printStackTrace(); } */
			}
		});
		t.start();
	}

	private Container parseJSON(String jsonMessage) {
		Container c = new Container();
		Object objeto = null;
		JSONObject jsonObject = null;
		int type = -1;
		try { // parsear los datos
			jsonObject = new JSONObject(jsonMessage);
			type = jsonObject.getInt("messageType");
			if (type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_CONSUMO) {
				objeto = new ConjuntoCasas();
				((ConjuntoCasas) objeto).setConsuma_enc(new BigInteger(jsonObject.getString("consum")));
				((ConjuntoCasas) objeto).setIdcasa(jsonObject.getInt("contadorId"));
				((ConjuntoCasas) objeto).setZonaid(jsonObject.getInt("zonaId"));
				((ConjuntoCasas) objeto).setTime(jsonObject.getInt("time"));
			} else if (type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_PRECIO_PROVIDER) {
				objeto = Float.parseFloat(jsonObject.getString("price"));
			} else if (type == VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_PROVIDER) {
				PaillierAgregador.getInstance().g = new BigInteger(jsonObject.getString("g"));
				PaillierAgregador.getInstance().n = new BigInteger(jsonObject.getString("n"));
			}
		} catch (NumberFormatException | JSONException e) {
			e.printStackTrace();
		}
		c.type = type;
		c.objeto = objeto;
		return c;
	}

	private void enviarConsumosAlProvider() {
		int port = VariablesGlobales._DEFAULT_PROVIDER_PORT;
		BigInteger consumoTotal = calcularConsumoTotal();
		String jsonMessageToProvider = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_ENVIAR_CONSUMO_AGREGADO + ", \"consum\": \"" + consumoTotal.toString() + "\", \"zona\":" + zona + ", \"viviendas\":" + _CONTADORES_EN_CIUDAD + ", \"time\":" + time + "}";
		PrinterTools.printJSON(jsonMessageToProvider);
		if (SocketTools.send(VariablesGlobales._IP_PROVIDER, port, jsonMessageToProvider))
			PrinterTools.log("[ZonaAgregador= " + this.zona + " sends data to " + VariablesGlobales._IP_PROVIDER + ":" + port + "]");
		else
			PrinterTools.log("ERROR [ZonaAgregador=" + this.zona + "sends data to " + VariablesGlobales._IP_PROVIDER + ":" + port + "]");
	}

	private BigInteger calcularConsumoTotal() {
		PaillierAgregador p = PaillierAgregador.getInstance();
		BigInteger[] consumosMatrix = new BigInteger[listaCasas.size()];
		for (int i = 0; i < listaCasas.size(); i++) {
			consumosMatrix[i] = listaCasas.get(i).getConsuma_enc();
			if (!listaCasas.get(i).isNuevo()) // si es viejo, significa que no ha enviado el suyo
				listaCasas.get(i).incrementarNotFound();
			listaCasas.get(i).setNuevo(false); // los valores usados son viejos
		}
		return p.AgreggatorFunction(p.nsquare, consumosMatrix);
	}

	private void sendPrecioToContadores(Float preciokWh) {
		String jsonMessage = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_ENVIAR_PRECIO_CONTADOR + ", \"price\": \"" + Float.toString(preciokWh) + "\"}";
		PrinterTools.printJSON(jsonMessage);
		for (int i = 0; i < listaCasas.size(); i++) {
			int contadorId = listaCasas.get(i).getIdcasa();
			int port = VariablesGlobales._DEFAULT_CONTADOR_PORT + contadorId;
			if (SocketTools.send(VariablesGlobales._IP_CONTADOR, port, jsonMessage))
				PrinterTools.log("[Agregador sends data to " + VariablesGlobales._IP_CONTADOR + ":" + port + ": price is now " + Float.toString(preciokWh) + "]");
			else
				PrinterTools.log("ERROR [Agregador sends data to " + VariablesGlobales._IP_CONTADOR + ":" + port + ": price is now " + Float.toString(preciokWh) + "]");
		}
	}

	private void retrievePaillierParameters() {
		int port = VariablesGlobales._DEFAULT_PROVIDER_PORT;
		Container result = null;
		Socket connection = null;
		boolean boolResult = false;
		do {
			String jsonMessageToProvider = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR + ", \"zona\": " + zona + "}";
			PrinterTools.printJSON(jsonMessageToProvider);
			connection = SocketTools.sendSynchronized(VariablesGlobales._IP_PROVIDER, port, jsonMessageToProvider);
			if (connection != null) {
				PrinterTools.log("[ZonaAgregador= " + this.zona + " gets PAILLIER data from " + connection + "]");
				String jsonMessage = SocketTools.getJSON(connection, false);
				if (!connection.isClosed()) {
					PrinterTools.printJSON(jsonMessage);
					result = parseJSON(jsonMessage);
				}
			} else
				PrinterTools.log("ERROR [ZonaAgregador= " + this.zona + " gets PAILLIER data from " + connection + "]");
			boolResult = result != null && result.type != VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR;
			if (!boolResult)
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		} while (boolResult);
		if (connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendPaillierParameters(Socket socket) {
		String jsonMessageToProvider = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR + ", \"g\": \"" + PaillierAgregador.getInstance().g.toString() + "\", \"n\": \"" + PaillierAgregador.getInstance().n.toString() + "\"}";
		PrinterTools.printJSON(jsonMessageToProvider);
		if (SocketTools.sendSynchronized(socket, jsonMessageToProvider)) {
			PrinterTools.log("[ZonaAgregador= " + this.zona + " sends PAILLIER data to CONTADOR via established socket.");
		} else
			PrinterTools.log("ERROR [ZonaAgregador=" + this.zona + " sends PAILLIER data to CONTADOR via established socket.");

	}

	private class Container {
		public int type = 0;
		public Object objeto = null;
	}

}