package thesmartbros.sagilbe.classes.agregador;

import java.io.*;
import java.math.BigInteger;
import java.net.*;

import thesmartbros.sagilbe.tools.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.tools.JSONParser;

public class Agregador {

	BigInteger[] Consumos;

	int zona=0;

	final int PUERTO1 = 80000;

	ServerSocket sc1;

	Socket so1;

	DataOutputStream salida1;

	String mensajeRecibido1;

	final int PUERTO2 = 80001;

	ServerSocket sc2;

	Socket so2;

	DataOutputStream salida2;

	String mensajeRecibido2;
	final int PUERTO3 = 80003;

	ServerSocket sc3;

	Socket so3;

	DataOutputStream salida3;

	String mensajeRecibido3;

	// SERVIDOR1

	public void initServer1()

	{
		int idconex = 1;

		BufferedReader entrada;

		try

		{

			sc1 = new ServerSocket(PUERTO1);/*
											 * crea socket servidor que
											 * escuchara en puerto 80000
											 */

			so1 = new Socket();

			System.out.println("Esperando una conexión1:");

			so1 = sc1.accept();

			// Inicia el socket, ahora esta esperando una conexión por parte del
			// cliente

			System.out.println("cliente 1 se ha conectado.");

			// Canales de entrada y salida de datos

			entrada = new BufferedReader(new InputStreamReader(
					so1.getInputStream()));

			salida1 = new DataOutputStream(so1.getOutputStream());

			System.out.println("Confirmando conexion al cliente 1....");

			salida1.writeUTF("Conexión exitosa...1 envia un mensaje :D");

			// Recepcion de mensaje

			String result = (new JSONParser()).getJSONFromUrl(entrada
					.readLine());

			MultiplicarConsumos(idconex, result);

			sc1.close();// Aqui se cierra la conexión con el cliente

		} catch (Exception e)

		{

			System.out.println("Error: " + e.getMessage());

		}

	}

	// SERVIDOR2

	public void initServer2()

	{

		BufferedReader entrada;
		int idconex = 2;

		try

		{

			sc2 = new ServerSocket(PUERTO2);/*
											 * crea socket servidor que
											 * escuchara en puerto 80000
											 */

			so2 = new Socket();

			System.out.println("Esperando una conexión 2:");

			so2 = sc2.accept();

			// Inicia el socket, ahora esta esperando una conexión por parte del
			// cliente

			System.out.println("cliente 2 se ha conectado.");

			// Canales de entrada y salida de datos

			entrada = new BufferedReader(new InputStreamReader(
					so2.getInputStream()));

			salida2 = new DataOutputStream(so2.getOutputStream());

			System.out.println("Confirmando conexion al cliente 2....");

			salida2.writeUTF("Conexión exitosa...2 envia un mensaje :D");

			// Recepcion de mensaje

			String result = (new JSONParser()).getJSONFromUrl(entrada
					.readLine());

			MultiplicarConsumos(idconex, result);

			sc2.close();// Aqui se cierra la conexión con el cliente

		} catch (Exception e)

		{

			System.out.println("Error: " + e.getMessage());

		}

	}

	// SERVIDOR3

	public void initServer3()

	{

		BufferedReader entrada;
		int idconex = 3;

		try

		{

			sc3 = new ServerSocket(PUERTO3);/*
											 * crea socket servidor que
											 * escuchara en puerto 80000
											 */

			so3 = new Socket();

			System.out.println("Esperando una conexión 3:");

			so3 = sc3.accept();

			// Inicia el socket, ahora esta esperando una conexión por parte del
			// cliente

			System.out.println("cliente 3 se ha conectado.");

			// Canales de entrada y salida de datos

			entrada = new BufferedReader(new InputStreamReader(
					so3.getInputStream()));

			salida3 = new DataOutputStream(so3.getOutputStream());

			System.out.println("Confirmando conexion al cliente 3....");

			salida3.writeUTF("Conexión exitosa...3 envia un mensaje :D");

			// Recepcion de mensaje

			String result = (new JSONParser()).getJSONFromUrl(entrada
					.readLine());

			MultiplicarConsumos(idconex, result);

			sc3.close();// Aqui se cierra la conexión con el cliente

		} catch (Exception e)

		{

			System.out.println("Error: " + e.getMessage());

		}

	}

	private void MultiplicarConsumos(int idconex, String result) {
		BigInteger consum_enc = null;

		try {
			JSONArray resultsArray = (new JSONObject(result))
					.getJSONArray("results");
			JSONObject results = resultsArray.getJSONObject(0);
			JSONArray myResults = results.getJSONArray("address_components");
			String consum = myResults.getJSONObject(1).getString("consum");
			consum_enc = new BigInteger(consum.getBytes());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (Consumos[idconex - 1] == null) {
			Consumos[idconex - 1] = consum_enc;
		}

		else if (Consumos[idconex - 1] != null) {
			Paillier p = new Paillier();

			if (Consumos[0] != null && Consumos[1] != null
					&& Consumos[2] != null) {
				BigInteger agregadorResult = p.AgreggatorFunction(p.nsquare,
						Consumos[0], Consumos[1], Consumos[2]);
				Consumos[0] = null;
				Consumos[1] = null;
				Consumos[2] = null;
				
				Boolean res= SendAgregadorResult(agregadorResult);
				if (res){
					
				}
				
			}
			if (Consumos[0] != null && Consumos[1] != null
					&& Consumos[2] == null) {
				BigInteger agregadorResult = p.AgreggatorFunction(p.nsquare,
						Consumos[0], Consumos[1]);
				Consumos[0] = null;
				Consumos[1] = null;
				Consumos[2] = null;
			}
			if (Consumos[0] != null && Consumos[1] == null
					&& Consumos[2] != null) {
				BigInteger agregadorResult = p.AgreggatorFunction(p.nsquare,
						Consumos[0], Consumos[2]);
				Consumos[0] = null;
				Consumos[1] = null;
				Consumos[2] = null;
			}
			
			if (Consumos[0] == null && Consumos[1] == null
					&& Consumos[2] == null) {
				BigInteger agregadorResult = p.AgreggatorFunction(p.nsquare,
						Consumos[1], Consumos[2]);
				Consumos[0] = null;
				Consumos[1] = null;
				Consumos[2] = null;
			}
			if (Consumos[0] != null && Consumos[1] == null
					&& Consumos[2] == null) {
				BigInteger agregadorResult = Consumos[0];
				Consumos[0] = null;
				Consumos[1] = null;
				Consumos[2] = null;
			}
			if (Consumos[0] == null && Consumos[1] != null
					&& Consumos[2] == null) {
				BigInteger agregadorResult = Consumos[1];
				Consumos[0] = null;
				Consumos[1] = null;
				Consumos[2] = null;
			}
			if(Consumos[0]==null&&Consumos[1]==null&&Consumos[2]!=null){
        		BigInteger agregadorResult = Consumos[2];
            	Consumos[0]=null;
            	Consumos[1]=null;
            	Consumos[2]=null;
		}

	}

}
	private Boolean SendAgregadorResult(BigInteger agregadorResult) {
		String jsonMessage = "{ \"consum\": "
				+ agregadorResult.toString() + ", \"zona\":"
				+ zona + "}";
		Socket socket = null;
		OutputStream outstream = null;
		PrintWriter out = null;
		try {
			socket = new Socket("127.0.0.1", 90000 + zona);
			outstream = socket.getOutputStream();
			out = new PrintWriter(outstream);
			out.print(jsonMessage);
		} catch (UnknownHostException e) {
			System.err.print(e);
			return false;
		} catch (IOException e) {
			System.err.print(e);
			e.printStackTrace();
			return false;
		} finally {
			out.close();
			try {
				outstream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
	
