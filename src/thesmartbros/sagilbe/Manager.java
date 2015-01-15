package thesmartbros.sagilbe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
	private ServerSocket serverSocket = null;

	public void start() {
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
					e.printStackTrace();
				}
			}

			private void startManagerFunctions(Socket socket) {
				PrinterTools.socketLog("Client connected to Manager... OK (" + socket + ")");
				String jsonMessageFromContador = SocketTools.getJSON(socket); // desde el socket conseguir el JSON
				Container c = parseJSON(jsonMessageFromContador); //parsear el JSON
				if (c.type == VariablesGlobales._MESSAGE_TYPE_ALERTA_CONSUMO_SUPERADO) {}
			}
		});
		t.start();
	}

	private Container parseJSON(String jsonMessage) {
		Container c = new Container();
		float longitud = 0;
		float latitud = 0;
		int contadorid = 0;
		JSONObject jsonObject = null;
		int type = -1;
		try { // parsear los datos
			jsonObject = new JSONObject(jsonMessage);
			type = jsonObject.getInt("messageType");
			if (type == VariablesGlobales._MESSAGE_TYPE_ALERTA_CONSUMO_SUPERADO) {
				contadorid = jsonObject.getInt("contadorId");
				longitud = Float.parseFloat(jsonObject.getString("longitud"));
				latitud = Float.parseFloat(jsonObject.getString("latitud"));
				JSONArray array = jsonObject.getJSONArray("turnedOnDevices");
				List<ElectrodomesticoJSON> turnedOnDevices = new ArrayList<ElectrodomesticoJSON>();
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
		return c;
	}

	private class Container {
		public int type = 0;
		public int contadorid = 0;
		public float longitud = 0;
		public float latitud = 0;
	}

	private String getStreet(float latitud, float longitud) {
		return ToolsMap.getLocationFromName(latitud, longitud);
	}

}
