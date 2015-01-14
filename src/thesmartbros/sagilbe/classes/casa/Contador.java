package thesmartbros.sagilbe.classes.casa;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.tools.Sign;
import thesmartbros.sagilbe.tools.PrinterTools;
import thesmartbros.sagilbe.tools.SocketTools;
import thesmartbros.sagilbe.tools.VariablesGlobales;
import thesmartbros.sagilbe.tools.Encrip_Decrip;

public class Contador {

	/* Esta clase representa el contador. Las caracteristicas son: 1. Cada
	 * _THREAD_TIME_INTERVAL lanza un mensaje de tipo 1 indicando el consumo del
	 * contador y su identificacion */

	private float precio_acumulado = 0.0f;
	private float precio_actual = 0.0f;
	private int energiaConsumidaMensual = 0;
	private int contadorId = 0;
	private int zonaId = 0;

	/* map */
	private float latitud;
	private float longitud;

	/* hour */
	private int time = 0; /* from 0 to 23 */
	private int day = 1; /* from 1 to 365 */
	private int year = 2015; /* from 2015 */
	public final static int _THREAD_TIME_INTERVAL = 5000; /* ms */
	public final static int _DEFAULT_DELAY = 100; /* ms */

	/* variables principales, de socket */
	private ServerSocket serverSocket = null;

	/* variable de test */
	private int stopWorkingTime = Integer.MAX_VALUE;

	/* variables contador y paillier */
	private ElectrodomesticoResource casa;
	private PaillierContador paillierContador = new PaillierContador();

	public Contador(int contadorId, int zonaId, float latitud, float longitud, int perfil) {
		this.contadorId = contadorId;
		this.zonaId = zonaId;
		this.latitud = latitud;
		this.longitud = longitud;
		this.casa = new ElectrodomesticoResource(perfil);
	}

	public Contador(int contadorId, int zonaId, float latitud, float longitud, int perfil, int falla) {
		this.contadorId = contadorId;
		this.zonaId = zonaId;
		this.latitud = latitud;
		this.longitud = longitud;
		this.casa = new ElectrodomesticoResource(perfil);
		this.stopWorkingTime = falla;
	}

	public void work() {
		startSocketServer();
		waitSomeTime();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if (time < 0 || time > 23) {
					time = 0;
					day++;
					PrinterTools.log("[  ***************************************** ]");
					PrinterTools.log("[    FACTURA ACUMULADA CONTADOR" + contadorId + ": " + precio_acumulado + " EUR ]");
					PrinterTools.log("[  ***************************************** ]");
					PrinterTools.log("[    TODAY IS A NEW DAY! THIS IS day " + day + " month " + day / 365 + " year " + year + "    ]");
					PrinterTools.log("[  ***************************************** ]");
				}
				if (day < 1 || day > 365) {
					day = 1;
					year++;
					PrinterTools.log("[    HAPPY NEW YEAR " + year + " ;)   ]");
				}
				if (time < stopWorkingTime)
					enviarConsumoInstantaneo(time);
				time++;
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
		final boolean listening = true;
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
				PrinterTools.socketLog("Client connected to Contador... OK (" + socket + ")");
				String jsonMessageFromAgregador = SocketTools.getJSON(socket); // desde el socket conseguir el JSON
				Container c = parseJSON(jsonMessageFromAgregador); //parsear el JSON
				if (c.type == VariablesGlobales._MESSAGE_TYPE_ENVIAR_PRECIO_CONTADOR) {
					Float preciokWh = (Float) c.objeto;
					precio_actual = preciokWh.floatValue();
					PrinterTools.log("[contador" + contadorId + " stores new price: " + precio_actual + " EUR/kWh]");
				} else if (c.type == VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR) {
					enviarConsumoInstantaneo(time); //envio lo que no habia podido enviar por no tener pallier
				}
			}
		});
		t.start();
	}

	private void enviarConsumoInstantaneo(int tiempo) {
		int consumoInstantaneo = casa.getConsumoTotal(tiempo);
		PrinterTools.contadorLog(consumoInstantaneo + " Wh @ contador" + contadorId + " t=" + tiempo + " h");
		/* cargar precio */
		precio_acumulado += (float) consumoInstantaneo * precio_actual * VariablesGlobales._RATIO_TIME / 1000; /* to
																												 * be
																												 * considered
																												 * in
																												 * kW */
		PrinterTools.contadorLog("Factura es ahora de: " + this.precio_acumulado + " EUR");
		/* enviar */
		int port = VariablesGlobales._DEFAULT_AGREGADOR_PORT + zonaId;
		this.energiaConsumidaMensual += consumoInstantaneo;
		BigInteger consumoInstantaneoPaillier;

		consumoInstantaneoPaillier = paillierContador.Encryption(BigInteger.valueOf(consumoInstantaneo));
		if (consumoInstantaneoPaillier == BigInteger.ZERO)
			requestPaillierParameters(); /* si no tiene Paillier, enviar request */
		else {
			String Message = "\"messageType\": " + VariablesGlobales._MESSAGE_TYPE_ENVIAR_CONSUMO + ", \"consum\": \"" + consumoInstantaneoPaillier.toString() + "\", \"contadorId\":" + this.contadorId + ", \"zonaId\":" + this.zonaId + ", \"time\":" + tiempo;
			String[] args = new String[1];
			args[0] = Message;
			String signature = Sign.getInstance().GenSig(args);
			String jsonMessage = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_ENVIAR_CONSUMO + ", \"consum\": \"" + consumoInstantaneoPaillier.toString() + "\", \"contadorId\":" + this.contadorId + ", \"zonaId\":" + this.zonaId + ", \"time\":" + tiempo + ", \"signature\": \"" + signature + "\"}";
			PrinterTools.printJSON(jsonMessage);
			if (SocketTools.send(VariablesGlobales._IP_AGREGADOR, port, jsonMessage)) {
				PrinterTools.log("[Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " sends data; time=" + tiempo + " to " + VariablesGlobales._IP_AGREGADOR + ":" + port + "]");
			} else
				PrinterTools.log("ERROR [Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " sends data; time=" + tiempo + " to " + VariablesGlobales._IP_AGREGADOR + ":" + port + "]");
		}
	}

	// cuando hace request de Paillier, tambien se identifica en el sistema.
	private void requestPaillierParameters() {
		int port = VariablesGlobales._DEFAULT_AGREGADOR_PORT + zonaId;
		String message = "\"messageType\": " + VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS + ", \"contadorId\": " + this.contadorId + ", \"zonaId\": " + this.zonaId + ", \"latitud\": \"" + Float.toString(this.latitud) + "\", \"longitud\": \"" + Float.toString(this.longitud) +"\"";
		String[] args = new String[1];
		args[0] = message;
		String signature = Sign.getInstance().GenSig(args);
		String jsonMessage = "{ \"messageType\": " + VariablesGlobales._MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS + ", \"contadorId\": " + this.contadorId + ", \"zonaId\": " + this.zonaId + ", \"latitud\": \"" + Float.toString(this.latitud) + "\", \"longitud\": \"" + Float.toString(this.longitud) + "\", \"signature\": \"" + signature + "\" }";
		PrinterTools.printJSON(jsonMessage);
		if (SocketTools.send(VariablesGlobales._IP_AGREGADOR, port, jsonMessage)) {
			PrinterTools.log("[Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " asks for Paillier; time=" + this.time + " to " + VariablesGlobales._IP_AGREGADOR + ":" + port + "]");
		} else
			PrinterTools.log("ERROR [Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " asks for Paillier; time=" + this.time + " to " + VariablesGlobales._IP_AGREGADOR + ":" + port + "]");
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
				paillierContador.g = new BigInteger(jsonObject.getString("g"));
				paillierContador.n = new BigInteger(jsonObject.getString("n"));
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
