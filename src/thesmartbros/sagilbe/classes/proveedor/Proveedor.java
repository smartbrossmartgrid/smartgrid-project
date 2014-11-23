package thesmartbros.sagilbe.classes.proveedor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import thesmartbros.sagilbe.tools.JSONParser;
import thesmartbros.sagilbe.tools.Paillier;

//clase perteneciente al objeto proveedor

public class Proveedor {

	// lista de zonas
	private List<Zona> miszonas;

	private int consumoTotal;
	private long precioKw;

	ServerSocket sc;
	Socket so;	
	DataOutputStream salida;

	BigInteger res;
	
	public void initServer()

	{
		BufferedReader entrada;

		try

		{

			sc = new ServerSocket(50000);/*
			 * crea socket servidor que
			 * escuchara en puerto 50000
			 */

			so = new Socket();

			System.out.println("Esperando una conexión:");

			so = sc.accept();

			// Inicia el socket, ahora esta esperando una conexión por parte del
			// cliente

			System.out.println("agregador se ha conectado.");

			// Canales de entrada y salida de datos

			entrada = new BufferedReader(new InputStreamReader(
					so.getInputStream()));

			salida = new DataOutputStream(so.getOutputStream());

			System.out.println("Confirmando conexion al agregador....");

			salida.writeUTF("Conexión exitosa... envia un mensaje :D");

			// Recepcion de mensaje

			String result = (new JSONParser()).getJSONFromUrl(entrada
					.readLine());

			calcular_precio(result);

			sc.close();// Aqui se cierra la conexión con el cliente

		} catch (Exception e)

		{

			System.out.println("Error: " + e.getMessage());

		}

	}


	// cálculo del precio
	private void calcular_precio(String result) {

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
        Paillier paillier = new Paillier();

		BigInteger precio = new BigInteger(Long.toString(precioKw));
		
		BigInteger consum = paillier.Decryption(consum_enc);

		res =  precio.multiply(consum);

	}

	// para enviar al tÃ©cnico
	private void enviar_tecnico() {

		System.out.println("Enviando tÃ©cnico");

	}


	//establece el estado que le entras como String a la zona que le pases como parametro
	private void actualizar_estado_zona(int num_zona, String estado) {
		this.miszonas.get(num_zona).setEstado(estado);
	}

	private void get_calle() {

	}

	public long getPrecioKw() {
		return precioKw;
	}

	public void setPrecioKw(long precioKw) {
		this.precioKw = precioKw;
	}

	public int getConsumoTotal() {
		return consumoTotal;
	}

	public void setConsumoTotal(int consumoTotal) {
		this.consumoTotal = consumoTotal;
	}

}
