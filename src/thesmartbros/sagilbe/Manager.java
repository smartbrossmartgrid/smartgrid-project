package thesmartbros.sagilbe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.classes.casa.ElectrodomesticoJSON;
import thesmartbros.sagilbe.classes.proveedor.ToolsMap;
import thesmartbros.sagilbe.tools.PrinterTools;
import thesmartbros.sagilbe.tools.SocketTools;
import thesmartbros.sagilbe.tools.VariablesGlobales;

public class Manager {

	/* variables principales, de socket */
	private static ServerSocket serverSocket = null;

	public static void main(String args[]) {
		System.out.println(" WELCOME!           ");
		System.out.println("Waiting for connections...");
		start();
	}

	public static void start() {
		final boolean listening = true;
		try {
			serverSocket = new ServerSocket(VariablesGlobales._DEFAULT_MANAGER_TEST_PORT);
		} catch (IOException e) {
			PrinterTools.errorsLog("Could not listen on port: " + VariablesGlobales._DEFAULT_MANAGER_TEST_PORT);
			System.exit(-1);
		}
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					while (listening && serverSocket != null)
						startManagerFunctions(serverSocket.accept());
					serverSocket.close();
					serverSocket = null;
				} catch (Exception e) {
					PrinterTools.errorsLog(e.toString());
				}
			}

			private void startManagerFunctions(Socket socket) {
				System.out.println("Bienvenido a SAGILBE");
				PrinterTools.socketLog("Client connected to Manager... OK (" + socket + ")");
				String jsonMessageFromContador = SocketTools.getJSONClean(socket);
				Container c = parseJSON(jsonMessageFromContador);
				if (c.type == VariablesGlobales._MESSAGE_TYPE_ALERTA_CONSUMO_SUPERADO) {
					String calle = ToolsMap.getLocationFromName(c.latitud, c.longitud);
					System.out.println("[-------- Contador (id=" + c.contadorid + ") en la calle: " + calle + " --------]");
					System.out.println("Indique el n�mero del electrom�stico que quiera apagar (0 para finalizar)");
					for (int i = 0; i < c.electrodomesticos.size(); i++) {
						System.out.println(i + 1 + ") " + c.electrodomesticos.get(i).getNombre() + " ----- consumo: " + c.electrodomesticos.get(i).getConsumo() + " Wh");
					}
					Scanner Console = new Scanner(System.in);
					String pattern = "[0-9]+"; /* Se compara la entrada de
												 * texto con este patron que
												 * solo incluye n�meros */
					String s = Console.nextLine();
					if (s.matches(pattern)) {
						int j = Integer.parseInt(s);
						while (j != 0) {
							c.electrodomesticos.get(j-1).setEncendido(false);
							s = Console.nextLine();
							if (s.matches(pattern)) {
								j = Integer.parseInt(s);
							} else {
								System.out.println("Inserte un n�mero");
							}
						}
						if (j == 0)
							enviar(c.electrodomesticos, c.contadorid);
					} else {
						System.out.println("Inserte un n�mero");
					}
				}
				System.out.println("ADIOS!");
			}
		});
		t.start();
	}

	private static Container parseJSON(String jsonMessage) {
		Container c = new Container();
		float longitud = 0;
		float latitud = 0;
		int contadorid = 0;
		JSONObject jsonObject = null;
		List<ElectrodomesticoJSON> turnedOnDevices = new ArrayList<ElectrodomesticoJSON>();
		int type = -1;
		try { // parsear los datos
			jsonObject = new JSONObject(jsonMessage);
			type = jsonObject.getInt("messageType");
			if (type == VariablesGlobales._MESSAGE_TYPE_ALERTA_CONSUMO_SUPERADO) {
				contadorid = jsonObject.getInt("contadorId");
				longitud = Float.parseFloat(jsonObject.getString("longitud"));
				latitud = Float.parseFloat(jsonObject.getString("latitud"));
				JSONArray array = jsonObject.getJSONArray("turnedOnDevices");
				for (int i = 0; i < array.length(); i++) {
					JSONObject jsonArrayObject = array.getJSONObject(i);
					int consumo = jsonArrayObject.getInt("consumo");
					boolean encendido = jsonArrayObject.getBoolean("encendido");
					String nombre = jsonArrayObject.getString("nombre");
					ElectrodomesticoJSON ejson = new ElectrodomesticoJSON(nombre, consumo, encendido);
					turnedOnDevices.add(ejson);
				}
			}
		} catch (NumberFormatException | JSONException e) {
			e.printStackTrace();
		}
		c.type = type;
		c.contadorid = contadorid;
		c.longitud = longitud;
		c.latitud = latitud;
		c.electrodomesticos = turnedOnDevices;
		return c;
	}

	private static class Container {
		public int type = 0;
		public int contadorid = 0;
		public float longitud = 0;
		public float latitud = 0;
		public List<ElectrodomesticoJSON> electrodomesticos = new ArrayList<ElectrodomesticoJSON>();
	}

	private static void enviar(List<ElectrodomesticoJSON> electrodomesticos, int contadorid) {
		int port = VariablesGlobales._DEFAULT_CONTADOR_PORT + contadorid;
		String jsonMessage = "{\"messageType\": " + VariablesGlobales._MESSAGE_TYPE_CLIENTE + ", \"contadorId\": " + contadorid + "}";
		try {
			JSONObject json = new JSONObject(jsonMessage);
			List<ElectrodomesticoJSON> turnedOnDevices = electrodomesticos;
			JSONArray array = new JSONArray(turnedOnDevices);
			json.put("turnedOnDevices", array);
			jsonMessage = json.toString();
		} catch (JSONException e) {
			PrinterTools.errorsLog(e.toString());
		}
		if (SocketTools.sendClean(VariablesGlobales._IP_CONTADOR, port, jsonMessage)) {
			PrinterTools.log("[Customer sends new config " + VariablesGlobales._IP_CONTADOR + ":" + port + "]");
		} else
			PrinterTools.log("ERROR [*!*!*!* Customer sends new config " + VariablesGlobales._IP_CONTADOR + ":" + port + "]");
	}

}
