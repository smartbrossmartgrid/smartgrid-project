package thesmartbros.sagilbe.classes.casa;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.tools.Paillier;
import thesmartbros.sagilbe.tools.PrinterTools;
import thesmartbros.sagilbe.tools.SocketTools;
import thesmartbros.sagilbe.tools.VariablesGlobales;

public class Contador {

	/* Esta clase representa el contador. Las caracteristicas son: 1. Cada
	 * _THREAD_TIME_INTERVAL lanza un mensaje de tipo 1 indicando el consumo del
	 * contador y su identificacion */

	private float precio_acumulado = 0.0f;
	private float precio_actual;
	private int energiaConsumidaMensual = 0;
	private int contadorId = 0;
	private int zonaId = 0;

	/* map */
	private float latitud;
	private float longitud;

	/* hour */
	private int time = 0; /* from 0 to 23 */
	public final static int _THREAD_TIME_INTERVAL = 10000; /* ms */
	public final static int _DEFAULT_DELAY = 300; /* ms */

	/* variables principales, de socket */
	private ServerSocket serverSocket = null;

	private ElectrodomesticoResource casa;

	public Contador(int contadorId, int zonaId) {
		this.contadorId = contadorId;
		this.zonaId = zonaId;
		this.casa = new ElectrodomesticoResource();
	}

	public void work() {
		startSocketServer();
		waitSomeTime();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if (time < 0 || time > 23)
					time = 0;
				enviarConsumoInstantaneo(time++);
			}
		}, 0, _THREAD_TIME_INTERVAL);
	}

	private void waitSomeTime() {
		try {
			Thread.sleep(_DEFAULT_DELAY * this.contadorId);                 //1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	private void startSocketServer() {
		boolean listening = true;
		int port = VariablesGlobales._DEFAULT_CONTADOR_PORT + contadorId;
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
						startContadorFunctions(serverSocket.accept());
					serverSocket.close();
					serverSocket = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private void startContadorFunctions(Socket socket) {
				PrinterTools.socketLog("Client connected to Contador... OK");
				String jsonMessageFromAgregador = SocketTools.getJSON(socket); // desde el socket conseguir el JSON
				Container c = parseJSON(jsonMessageFromAgregador); //parsear el JSON
				if (c.type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_PRECIO_CONTADOR) {
					Float preciokWh = (Float) c.objeto;
					precio_actual = preciokWh.floatValue();
				}
				/* try { socket.close(); } catch (IOException e) {
				 * e.printStackTrace(); } */
			}
		});
		t.start();
	}

	private void enviarConsumoInstantaneo(int time) {
		int consumoInstantaneo = casa.getConsumoTotal(time);
		/* cargar precio */
		precio_acumulado += consumoInstantaneo * precio_actual * VariablesGlobales._RATIO_TIME;
		/* enviar */
		int port = VariablesGlobales._DEFAULT_AGREGADOR_PORT + zonaId;
		this.energiaConsumidaMensual += consumoInstantaneo;
		BigInteger consumoInstantaneoPaillier;
		do {
			consumoInstantaneoPaillier = PaillierContador.getInstance().Encryption(BigInteger.valueOf(consumoInstantaneo));
			if (consumoInstantaneoPaillier == BigInteger.ZERO)
				retrievePaillierParameters();
		} while (consumoInstantaneoPaillier == BigInteger.ZERO); //los parametros Paillier no estan disponibles

		String jsonMessage = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_ENVIAR_CONSUMO + ", \"consum\": \"" + consumoInstantaneoPaillier.toString() + "\", \"contadorId\":" + this.contadorId + ", \"zonaId\":" + this.zonaId + ", \"time\":" + this.time + "}";
		PrinterTools.printJSON(jsonMessage);
		if (SocketTools.send(VariablesGlobales._IP_AGREGADOR, port, jsonMessage)) {
			PrinterTools.log("[Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " sends data; time=" + this.time + " to " + VariablesGlobales._IP_AGREGADOR + ":" + port + "]");
		} else
			PrinterTools.log("ERROR [Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " sends data; time=" + this.time + " to " + VariablesGlobales._IP_AGREGADOR + ":" + port + "]");
	}

	private void retrievePaillierParameters() {
		int port = VariablesGlobales._DEFAULT_AGREGADOR_PORT + zonaId;
		Socket connection = null;
		Container result = null;
		boolean boolResult = false;
		do {
			String jsonMessage = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS + ", \"contadorId\": " + this.contadorId + ", \"zonaId\": " + this.zonaId + "}";
			PrinterTools.printJSON(jsonMessage);
			connection = SocketTools.sendSynchronized(VariablesGlobales._IP_AGREGADOR, port, jsonMessage);
			if (connection != null) {
				PrinterTools.log("[Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " asks for Paillier; time=" + this.time + " to " + VariablesGlobales._IP_AGREGADOR + ":" + port + "]");
				if (!connection.isClosed()) {
					jsonMessage = SocketTools.getJSON(connection);
					result = parseJSON(jsonMessage);
				}
			} else
				PrinterTools.log("ERROR [Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " asks for Paillier; time=" + this.time + " to " + VariablesGlobales._IP_AGREGADOR + ":" + port + "]");
			boolResult = result != null && result.type != VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR;
			if (!boolResult)
				try {
					Thread.sleep(500000);
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

	private Container parseJSON(String jsonMessage) {
		Container c = new Container();
		Object objeto = null;
		JSONObject jsonObject = null;
		int type = -1;
		try { // parsear los datos
			jsonObject = new JSONObject(jsonMessage);
			type = jsonObject.getInt("messageType");
			if (type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_PRECIO_CONTADOR) {
				Float price = Float.parseFloat(jsonObject.getString("price"));
				objeto = price;
			} else if (type == VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR) {
				PaillierContador.getInstance().g = new BigInteger(jsonObject.getString("g"));
				PaillierContador.getInstance().n = new BigInteger(jsonObject.getString("n"));
			}
		} catch (NumberFormatException | JSONException e) {
			e.printStackTrace();
		}
		c.type = type;
		c.objeto = objeto;
		return c;
	}

	public String toString() {
		return casa.toString();
	}

	private class Container {
		public int type = 0;
		public Object objeto = null;
	}
}
