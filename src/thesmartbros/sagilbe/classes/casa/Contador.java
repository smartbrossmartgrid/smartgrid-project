package thesmartbros.sagilbe.classes.casa;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import thesmartbros.sagilbe.tools.Paillier;

public class Contador {

	private float precio_acumulado;
	private float precio_actual;
	private int energiaConsumidaMensual = 0;
	private int contadorId = 0;
	private int zonaId = 0;

	/* map */
	private float latitud;
	private float longitud;

	/* hour */
	private int time = 0; /* from 0 to 23 */
	private final int _THREAD_TIME_INTERVAL = 5000; /* ms */

	private ElectrodomesticoResource casa;

	public Contador(int contadorId, int zonaId) {
		this.contadorId = contadorId;
		this.zonaId = zonaId;
		this.casa = new ElectrodomesticoResource();
	}

	public void work() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if (time < 0 || time > 23)
					time = 0;
				enviarConsumoInstantaneo(time++);
			}
		}, 0, _THREAD_TIME_INTERVAL);
	}

	public void enviarConsumoInstantaneo(int time) {
		int consumoInstantaneo = casa.getConsumoTotal(time);
		this.energiaConsumidaMensual += consumoInstantaneo;
		BigInteger consumoInstantaneoPaillier = Paillier.getInstance().Encryption(BigInteger.valueOf(consumoInstantaneo));
		String jsonMessage = "{ \"consum\": " + consumoInstantaneoPaillier.toString() + ", \"contadorId\":" + this.contadorId + ", \"zonaId\":" + this.zonaId + "}";
		Socket socket = null;
		OutputStream outstream = null;
		PrintWriter out = null;
		try {
			socket = new Socket("127.0.0.1", 40000 + zonaId);
			outstream = socket.getOutputStream();
			out = new PrintWriter(outstream);
			out.print(jsonMessage);
			System.out.println("[Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " sends data; time="+this.time+"]");
		} catch (UnknownHostException e) {
			System.err.print(e);
		} catch (IOException e) {
			System.err.print(e);
		} finally {
			try {
				out.close();
				outstream.close();
				socket.close();
			} catch (IOException e) {
				System.err.print(e);
			} catch (Exception e) {
				System.err.print(e);
			}
		}
	}

	public String toString() {
		return casa.toString();
	}
	
	/* POJO */
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
