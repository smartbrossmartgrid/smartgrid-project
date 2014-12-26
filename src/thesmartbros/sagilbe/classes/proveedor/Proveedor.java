package thesmartbros.sagilbe.classes.proveedor;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.classes.agregador.ConjuntoCasas;
import thesmartbros.sagilbe.tools.SocketTools;

//clase perteneciente al objeto proveedor

public class Proveedor {

	/* variables principales, de socket */
	private ServerSocket serverSocket = null;
	private final String _IP_AGREGADOR = "127.0.0.1";
	private final int _DEFAULT_AGREGADOR_PORT = 40000;
	private final int _DEFAULT_PROVIDER_PORT = 50000;

	/* variables secundarias, de smart grid */
	private int zona = 0;
	private ArrayList<ConjuntoCasas> listacasas = new ArrayList<ConjuntoCasas>(); // Campo de la clase

	public void start() {
		boolean listening = true;
		try {
			serverSocket = new ServerSocket(_DEFAULT_PROVIDER_PORT);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + _DEFAULT_PROVIDER_PORT);
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
					System.err.println(e);
				}
			}

			private void startAgregadorFunctions(Socket socket) {
				System.out.println("Client connected to Provider... OK");
				String jsonMessageFromAgregador = SocketTools.getJSON(socket); //desde el socket conseguir el JSON
				System.out.println(jsonMessageFromAgregador);
				//ConjuntoCasas casa = parseJSON(jsonMessageFromAgregador); //parsear el JSON
			}
		});
		t.start();
	}

	private void parseJSON(String jsonMessage) {
		ConjuntoCasas casa = new ConjuntoCasas();
		JSONObject jsonObject = null;
		try { // parsear los datos
			jsonObject = new JSONObject(jsonMessage);
			casa.setConsuma_enc(new BigInteger(jsonObject.getString("consum")));
			casa.setZonaid(jsonObject.getInt("zonaId"));
			casa.setTime(jsonObject.getInt("time"));
		} catch (NumberFormatException | JSONException e) {
			e.printStackTrace();
		}
	}
}
