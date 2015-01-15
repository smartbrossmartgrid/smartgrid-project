package thesmartbros.sagilbe;

import java.awt.Container;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.classes.proveedor.Zona;
import thesmartbros.sagilbe.classes.proveedor.Proveedor.Tiempo;
import thesmartbros.sagilbe.tools.Paillier;
import thesmartbros.sagilbe.tools.PrinterTools;
import thesmartbros.sagilbe.tools.SocketTools;
import thesmartbros.sagilbe.tools.ToolsMap;
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
				if (c.type == VariablesGlobales._MESSAGE_TYPE_ALERTA_CONSUMO_SUPERADO) {
				}
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
