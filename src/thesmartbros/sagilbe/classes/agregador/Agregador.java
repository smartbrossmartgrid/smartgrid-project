package thesmartbros.sagilbe.classes.agregador;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;

import thesmartbros.sagilbe.tools.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.classes.agregador.ConjuntoCasas;
import thesmartbros.sagilbe.tools.JSONParser;

public class Agregador extends Thread {

	int zona = 0;
	final int PUERTO1 = 40000;
	ServerSocket sc1;
	Socket so1;
	DataOutputStream salida1;
	String mensajeRecibido1;
	final int PUERTO2 = 40001;
	public ArrayList<ConjuntoCasas> listacasas = null;  // Campo de la clase

	// ServerSocket sc2;
	// Socket so2;
	// DataOutputStream salida2;
	// String mensajeRecibido2;
	// final int PUERTO3 = 40002;
	// ServerSocket sc3;
	// Socket so3;
	// DataOutputStream salida3;
	// String mensajeRecibido3;
	@Override
	public void run() {
		initServer1();
	}

	// SERVIDOR1

	public void initServer1() {
		BufferedReader entrada;
		try {
			sc1 = new ServerSocket(PUERTO1);/*
											 * crea socket servidor que
											 * escuchara en puerto 40000
											 */
			so1 = new Socket();

			System.out.println("Esperando una conexión:");

			so1 = sc1.accept();

			// Inicia el socket, ahora esta esperando una conexión por parte del
			// cliente

			System.out.println("cliente se ha conectado.");

			// Canales de entrada y salida de datos

			entrada = new BufferedReader(new InputStreamReader(
					so1.getInputStream()));

			// salida1 = new DataOutputStream(so1.getOutputStream());

			System.out.println("Confirmando conexion al cliente....");

			// salida1.writeUTF("Conexión exitosa...1 envia un mensaje :D");

			// Recepcion de mensaje

			MandarConsumo(entrada.readLine());
					

			sc1.close();// Aqui se cierra la conexión con el cliente

		} catch (Exception e)

		{

			System.out.println("Error: " + e.getMessage());

		}

	}

	private void MandarConsumo(String entrada) {

		String result = (new JSONParser()).getJSONFromUrl(entrada);

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			jsonObject = new JSONObject(result.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BigInteger consum_enc = null;
		try {
			consum_enc = new BigInteger(jsonObject.getString("consum")
					.getBytes());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int idcasa = 0;
		try {
			idcasa = Integer.parseInt(jsonObject.getString("contadorId"));
		} catch (NumberFormatException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int idzona = 0;
		try {
			idzona = Integer.parseInt(jsonObject.getString("zonaId"));
		} catch (NumberFormatException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int position=0;
		boolean encontrado=false;
		boolean existe=false;
		
		while(position < listacasas.size() || !encontrado){
			
			if(idcasa==listacasas.get(position).getIdcasa()){
				
				encontrado=true;
			}
			else
				position++;
		}

		if(existe){
			
			if(listacasas.get(position).isNuevo()==false){
				BigInteger multiplicar = MultiplicarConsumos(idcasa);
				SendAgregadorResult(multiplicar,idzona);
			}
			
			ConjuntoCasas conjunto = new ConjuntoCasas();
			conjunto.setConsuma_enc(consum_enc);
			conjunto.setIdcasa(idcasa);
			conjunto.setZonaid(idzona);
			conjunto.setNuevo(true);
			listacasas.add(conjunto);
			listacasas.add(position,conjunto);
			
		}
		else {
		ConjuntoCasas conjunto = new ConjuntoCasas();
		conjunto.setConsuma_enc(consum_enc);
		conjunto.setIdcasa(idcasa);
		conjunto.setZonaid(idzona);
		conjunto.setNuevo(true);
		listacasas.add(conjunto);
		}

	}


	private BigInteger MultiplicarConsumos(int idcasa) {
		BigInteger consum_enc = null;
		BigInteger agregadorResult=null;

		int position=0;
		boolean encontrado=false;
		
		while(position < listacasas.size() || !encontrado){
			
			if(idcasa==listacasas.get(position).getIdcasa()){
				
				encontrado=true;
			}
			else
				position++;	
		}
		
		if (listacasas.get(position).isNuevo()==false) {
			Paillier p = new Paillier();
			agregadorResult = p.AgreggatorFunction(p.nsquare,
					listacasas.get(0).getConsuma_enc(), listacasas.get(1).getConsuma_enc(), listacasas.get(2).getConsuma_enc());
		}
		
		
		position=0;
		
		while(position < listacasas.size()){
			
			listacasas.get(position).setNuevo(false);

			position++;	
		}
		
		return agregadorResult;

	}

	private Boolean SendAgregadorResult(BigInteger agregadorResult, int idzona) {
		String jsonMessage = "{ \"consum\": " + agregadorResult.toString()
				+ ", \"zona\":" + idzona + "}";
		Socket socket = null;
		OutputStream outstream = null;
		PrintWriter out = null;
		try {
			socket = new Socket("127.0.0.1", 50000 /* + zona */);
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
