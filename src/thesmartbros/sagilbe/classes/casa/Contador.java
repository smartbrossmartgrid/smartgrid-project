package thesmartbros.sagilbe.classes.casa;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;

import thesmartbros.sagilbe.tools.Paillier;

public class Contador {

	private float precio_acumulado;
	private float precio_actual;
	private int energiaConsumidaMensual = 0;
	private int contadorId = 0;

	/* map */
	private float latitud;
	private float longitud;

	private ElectrodomesticoResource casa = new ElectrodomesticoResource();

	public void enviarConsumoInstantaneo() {
		int consumoInstantaneo = casa.getConsumoTotal();
		this.energiaConsumidaMensual += consumoInstantaneo;
		BigInteger consumoInstantaneoPaillier = Paillier.getInstance()
				.Encryption(BigInteger.valueOf(consumoInstantaneo));
		String jsonMessage = "{ \"consum\": "
				+ consumoInstantaneoPaillier.toString() + ", \"contadorId\":"
				+ contadorId + "}";
		Socket socket = null;
		OutputStream outstream = null;
		PrintWriter out = null;
		try {
			socket = new Socket("127.0.0.1", 80000 + contadorId);
			outstream = socket.getOutputStream();
			out = new PrintWriter(outstream);
			out.print(jsonMessage);
		} catch (UnknownHostException e) {
			System.err.print(e);
		} finally {
			out.close();
			outstream.close();
			socket.close();
		}
	}

	public float getPrecio_acumulado() {
		return precio_acumulado;
	}

	public void setPrecio_acumulado(float precio_acumulado) {
		this.precio_acumulado = precio_acumulado;
	}

	public float getPrecio_actual() {
		return precio_actual;
	}

	public void setPrecio_actual(float precio_actual) {
		this.precio_actual = precio_actual;
	}

	public int getEnergiaConsumidaMensual() {
		return energiaConsumidaMensual;
	}

	public void setEnergiaConsumidaMensual(int energiaConsumidaMensual) {
		this.energiaConsumidaMensual = energiaConsumidaMensual;
	}

	public float getLatitud() {
		return latitud;
	}

	public void setLatitud(float latitud) {
		this.latitud = latitud;
	}

	public float getLongitud() {
		return longitud;
	}

	public void setLongitud(float longitud) {
		this.longitud = longitud;
	}

}
