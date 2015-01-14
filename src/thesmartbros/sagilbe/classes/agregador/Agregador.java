package thesmartbros.sagilbe.classes.agregador;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.tools.PrinterTools;
import thesmartbros.sagilbe.tools.Sign;
import thesmartbros.sagilbe.tools.SocketTools;
import thesmartbros.sagilbe.tools.VariablesGlobales;
import thesmartbros.sagilbe.tools.Encrip_Decrip;

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
	private int time = 0; //time mas actualizado
	private ArrayList<ConjuntoCasas> listaCasas = new ArrayList<ConjuntoCasas>(); // Campo de la clase
	private PaillierAgregador paillierAgregador = new PaillierAgregador();

	public Agregador(int zonaId) {
		this.zona = zonaId;
	}

	public void start() {
		final boolean listening = true;
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
				PrinterTools.socketLog("Client connected to Agregador... OK (" + socket + ")");
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
					if (existe) {
						listaCasas.get(position).setConsuma_enc(casa.getConsuma_enc());
						listaCasas.get(position).setZonaid(casa.getZonaid());
						listaCasas.get(position).setTime(casa.getTime());
						listaCasas.get(position).setNuevo(true);
					} else
						listaCasas.add(casa);

					/* comprobamos cuantos contadores tenemos en la zona */
					if (_CONTADORES_EN_CIUDAD < listaCasas.size())
						_CONTADORES_EN_CIUDAD = listaCasas.size();

					/* funcion de deteccion de que no envia el consumo --> mas
					 * adelante la explicacion */
					for (int i = 0; i < listaCasas.size(); i++) {
						if (casa.getIdcasa() == listaCasas.get(i).getIdcasa())
							listaCasas.get(i).setNot_found(0);
						else
							listaCasas.get(i).incrementarNotFound();

						/* if (listaCasas.get(position).getIdcasa() == 5) {
						 * System
						 * .out.print("ID:"+listaCasas.get(i).getIdcasa()+" --> "
						 * +listaCasas.get(i).getLongitud()+" ..");
						 * System.out.println
						 * ("NOT_FOUND="+listaCasas.get(i).getNot_found()); } */

					}
					/* comprobamos que si estan todos, el estado de todos debe
					 * ser nuevo si queremos enviar datos */
					int size_nuevos = 0;
					for (int i = 0; i < listaCasas.size(); i++) {
						if (listaCasas.get(i).isNuevo()) {
							size_nuevos++;
							time = listaCasas.get(i).getTime();
						}
						/* deteccion de que no envia el consumo: Cuando llega
						 * hasta aqui (ha llegado un consumo), a las demas casas
						 * se les sube +1 en not_found. Si el not_found supera
						 * el NUM_CONTADORES, significa que ese no ha enviado.
						 * Es la unica manera que he encontrado para que
						 * funcione */
						if (listaCasas.get(i).getNot_found() >= _CONTADORES_EN_CIUDAD * VariablesGlobales._TIEMPO_ESPERA_PARA_ENVIAR_TECNICO && !listaCasas.get(i).isReparando()) {
							ConjuntoCasas contadorAveriado = listaCasas.get(i);
							pedirTecnico(contadorAveriado.getLongitud(), contadorAveriado.getLatitud());
							listaCasas.get(i).setReparando(true);
						}
					}
					if (size_nuevos == _CONTADORES_EN_CIUDAD) { //si todos son nuevos, enviamos consumos
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
					// anadir la casa a la lista de casas disponibles con datos a 0 y marcados como viejos
					ConjuntoCasas casa = new ConjuntoCasas();
					casa.setNuevo(false);
					casa.setIdcasa(((requestPaillierObject) c.objeto).contadorId);
					casa.setLatitud(((requestPaillierObject) c.objeto).latitud);
					casa.setLongitud(((requestPaillierObject) c.objeto).longitud);
					listaCasas.add(casa);
					if (paillierAgregador.g == null) {//no tengo los parametros, los pido
						requestPaillierParameters();
					} else
						// si los tengo, se los mando
						sendPaillieParameters(casa.getIdcasa());
				}
			}
		});
		t.start();
	}

	private Container parseJSON(String jsonMessage) {
		Container c = new Container();
		Object objeto = null;
		JSONObject jsonObject = null;
		int type = -1;
		//String jsonMessage = Encrip_Decrip.getInstance().decrypt(jsonMessage1);
		try { // parsear los datos
			jsonObject = new JSONObject(jsonMessage);
			type = jsonObject.getInt("messageType");
			if (type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_CONSUMO) {
				objeto = new ConjuntoCasas();
				((ConjuntoCasas) objeto).setConsuma_enc(new BigInteger(jsonObject.getString("consum")));
				((ConjuntoCasas) objeto).setIdcasa(jsonObject.getInt("contadorId"));
				((ConjuntoCasas) objeto).setZonaid(jsonObject.getInt("zonaId"));
				((ConjuntoCasas) objeto).setTime(jsonObject.getInt("time"));

				//Comprobar firma
				String signature = jsonObject.getString("signature");
				String[] args = new String[2];
				args[0] = "\"messageType\": " + VariablesGlobales._MESSAGE_TYPE_ENVIAR_CONSUMO + ", \"consum\": \"" + (jsonObject.getString("consum")) + "\", \"contadorId\":" + (jsonObject.getInt("contadorId")) + ", \"zonaId\":" + (jsonObject.getInt("zonaId")) + ", \"time\":" + (jsonObject.getInt("time"));
				args[1] = signature;
				Sign.getInstance().VerSig(args);
			} else if (type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_PRECIO_PROVIDER) {
				objeto = Float.parseFloat(jsonObject.getString("price"));
			} else if (type == VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_PROVIDER) {
				paillierAgregador.g = new BigInteger(jsonObject.getString("g"));
				paillierAgregador.n = new BigInteger(jsonObject.getString("n"));
				sendPaillierParameters(); //a todos los contadores
			} else if (type == VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS) {
				objeto = new requestPaillierObject();
				((requestPaillierObject) objeto).contadorId = jsonObject.getInt("contadorId");
				((requestPaillierObject) objeto).zonaid = jsonObject.getInt("zonaId");
				((requestPaillierObject) objeto).latitud = Float.parseFloat(jsonObject.getString("latitud"));
				((requestPaillierObject) objeto).longitud = Float.parseFloat(jsonObject.getString("longitud"));

				//Comprobar firma
				String signature = jsonObject.getString("signature");
				String[] args = new String[2];
				args[0] = "\"messageType\": " + VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS + ", \"contadorId\": " + (jsonObject.getInt("contadorId")) + ", \"zonaId\": " + (jsonObject.getInt("zonaId")) + ", \"latitud\": \"" + Float.parseFloat(jsonObject.getString("latitud")) + "\", \"longitud\": \"" + Float.parseFloat(jsonObject.getString("longitud")) + "\"";
				args[1] = signature;
				Sign.getInstance().VerSig(args);
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
			PrinterTools.log("ERROR [ZonaAgregador=" + this.zona + " sends data to " + VariablesGlobales._IP_PROVIDER + ":" + port + "]");
	}

	private void pedirTecnico(float longitud, float latitud) {
		//enviar mensaje de quiero un tecnico para contador
		int port = VariablesGlobales._DEFAULT_PROVIDER_PORT;
		String jsonMessageToProvider = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_REQUEST_TECNICO + ", \"longitud\": \"" + Float.toString(longitud) + "\", \"latitud\": \"" + Float.toString(latitud) + "\"}";
		PrinterTools.printJSON(jsonMessageToProvider);
		if (SocketTools.send(VariablesGlobales._IP_PROVIDER, port, jsonMessageToProvider))
			PrinterTools.log("[ !!!! ZonaAgregador= " + this.zona + " sends an alert to " + VariablesGlobales._IP_PROVIDER + ":" + port + "]");
		else
			PrinterTools.log("ERROR [ZonaAgregador=" + this.zona + " sends an alert to " + VariablesGlobales._IP_PROVIDER + ":" + port + "]");
	}

	private BigInteger calcularConsumoTotal() {
		BigInteger[] consumosMatrix = new BigInteger[listaCasas.size()];
		for (int i = 0; i < listaCasas.size(); i++) {
			consumosMatrix[i] = listaCasas.get(i).getConsuma_enc();
			if (!listaCasas.get(i).isNuevo()) // si es viejo, significa que no ha enviado el suyo
				listaCasas.get(i).incrementarNotFound();
			listaCasas.get(i).setNuevo(false); // los valores usados son viejos
		}
		return paillierAgregador.AgreggatorFunction(paillierAgregador.nsquare, consumosMatrix);
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

	private void requestPaillierParameters() {
		int port = VariablesGlobales._DEFAULT_PROVIDER_PORT;
		String jsonMessage = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR + ", \"zona\": " + zona + "}";
		PrinterTools.printJSON(jsonMessage);
		if (SocketTools.send(VariablesGlobales._IP_PROVIDER, port, jsonMessage)) {
			PrinterTools.log("[ZonaAgregador= " + this.zona + " requests PAILLIER data to PROVIDER]");
		} else
			PrinterTools.log("ERROR [ZonaAgregador= " + this.zona + " requests PAILLIER data to PROVIDER]");
	}

	private void sendPaillierParameters() {
		for (int i = 0; i < listaCasas.size(); i++) {
			sendPaillieParameters(listaCasas.get(i).getIdcasa());
		}
	}

	private void sendPaillieParameters(int contadorId) {
		String jsonMessageToContador = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR + ", \"g\": \"" + paillierAgregador.g.toString() + "\", \"n\": \"" + paillierAgregador.n.toString() + "\"}";
		int port = VariablesGlobales._DEFAULT_CONTADOR_PORT + contadorId;
		PrinterTools.printJSON(jsonMessageToContador);
		if (SocketTools.send(VariablesGlobales._IP_CONTADOR, port, jsonMessageToContador)) {
			PrinterTools.log("[ZonaAgregador= " + this.zona + " sends PAILLIER data to CONTADOR " + VariablesGlobales._IP_CONTADOR + ":" + port + "]");
		} else
			PrinterTools.log("ERROR [ZonaAgregador=" + this.zona + " sends PAILLIER data to CONTADOR via established socket.");
	}

	private class Container {
		public int type = 0;
		public Object objeto = null;
	}

	private class requestPaillierObject {
		public int contadorId = 0;
		public float latitud = 0.0f;
		public float longitud = 0.0f;
		public int zonaid = 0;
	}

}