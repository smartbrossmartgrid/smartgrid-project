package thesmartbros.sagilbe.classes.agregador;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.tools.Paillier;
import thesmartbros.sagilbe.tools.SocketTools;

public class Agregador {

	/* variables principales, de socket */
	public int _CONTADORES_EN_CIUDAD = 3;
	private ServerSocket serverSocket = null;
	private final String _IP_PROVIDER = "127.0.0.1";
	private final int _DEFAULT_PROVIDER_PORT = 50000;
	private final int _DEFAULT_AGREGADOR_PORT = 40000;
	private final String _IP_CONTADOR = "127.0.0.1";
	private final int _DEFAULT_CONTADOR_PORT = 30000;

	/* variables secundarias, de smart grid */
	private int zona = 0;
	private int time = 0;
	private ArrayList<ConjuntoCasas> listacasas = new ArrayList<ConjuntoCasas>(); // Campo de la clase

	public Agregador(int zonaId, int contadores) {
		this.zona = zonaId;
		this._CONTADORES_EN_CIUDAD = contadores;
	}

	public void start() {
		boolean listening = true;
		int port = _DEFAULT_AGREGADOR_PORT + zona;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
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
				System.out.println("Client connected to Agregador... OK");
				String jsonMessageFromContador = SocketTools.getJSON(socket); //desde el socket conseguir el JSON
				//System.out.println(jsonMessageFromContador);
				ConjuntoCasas casa = parseJSON(jsonMessageFromContador); //parsear el JSON

				/* comprobar que los valores del contador recibido no existen ya */

				int position = 0;
				boolean existe = false, nuevo = false;
				while (position < listacasas.size() && !existe && listacasas.size() != 0) { // buscar contador
					if (casa.getIdcasa() == listacasas.get(position).getIdcasa()) {
						existe = true;
						nuevo = listacasas.get(position).isNuevo();
					} else
						position++;
				}
				if (existe) /* colocamos la casa */
					listacasas.remove(position);
				listacasas.add(casa);

				/* comprobamos que si estan todos, el estado de todos debe ser
				 * nuevo */
				int size = 0;
				for (int i = 0; i < listacasas.size(); i++) {
					if (listacasas.get(i).isNuevo())
						size++;
					if (listacasas.get(i).getTime() > time)
						time = listacasas.get(i).getTime();
				}
				if (size == _CONTADORES_EN_CIUDAD) { //si todos son nuevos, enviamos consumos
					enviarConsumosAlProvider();
				} else { // no todos son nuevos, pero... puede que algun contador no funcione...
					// 1. averiguar si mi contador tenia datos nuevos
					if (nuevo) // tenia datos nuevos
						enviarConsumosAlProvider();
					// si tenia datos viejos, ya he hecho antes lo que debia (sustituir valores)
				}

			}
		});
		t.start();
	}

	private ConjuntoCasas parseJSON(String jsonMessage) {
		ConjuntoCasas casa = new ConjuntoCasas();
		JSONObject jsonObject = null;
		try { // parsear los datos
			jsonObject = new JSONObject(jsonMessage);
			casa.setConsuma_enc(new BigInteger(jsonObject.getString("consum")));
			casa.setIdcasa(jsonObject.getInt("contadorId"));
			casa.setZonaid(jsonObject.getInt("zonaId"));
			casa.setTime(jsonObject.getInt("time"));
		} catch (NumberFormatException | JSONException e) {
			e.printStackTrace();
		}
		return casa;
	}

	private void enviarConsumosAlProvider() {
		int port = _DEFAULT_PROVIDER_PORT;
		BigInteger consumoTotal = calcularConsumoTotal();
		String jsonMessageToProvider = "{ \"consum\": \"" + consumoTotal.toString() + "\", \"zona\":" + zona + ", \"time\":" + time + "}";
		if (SocketTools.send(_IP_PROVIDER, port, jsonMessageToProvider))
			System.out.println("[ZonaAgregador= " + this.zona + " sends data to " + _IP_PROVIDER + ":" + port + "]");
		else
			System.out.println("ERROR [ZonaAgregador=" + this.zona + "sends data to " + _IP_PROVIDER + ":" + port + "]");
	}

	private BigInteger calcularConsumoTotal() {
		Paillier p = Paillier.getInstance();
		BigInteger[] consumosMatrix = new BigInteger[listacasas.size()];
		for (int i = 0; i < listacasas.size(); i++) {
			consumosMatrix[i] = listacasas.get(i).getConsuma_enc();
			if (!listacasas.get(i).isNuevo()) // si es viejo, significa que no ha enviado el suyo
				listacasas.get(i).incrementarNotFound();
			listacasas.get(i).setNuevo(false); // los valores usados son viejos
		}
		return p.AgreggatorFunction(p.nsquare, consumosMatrix);
	}

}