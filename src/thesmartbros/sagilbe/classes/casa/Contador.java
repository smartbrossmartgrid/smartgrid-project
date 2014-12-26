package thesmartbros.sagilbe.classes.casa;

import java.math.BigInteger;
import java.util.Timer;
import java.util.TimerTask;

import thesmartbros.sagilbe.tools.Paillier;
import thesmartbros.sagilbe.tools.SocketTools;

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
	private final String _IP_AGGREGADOR = "127.0.0.1";
	private final int _DEFAULT_AGREGADOR_PORT = 40000;

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
		int port = _DEFAULT_AGREGADOR_PORT + zonaId;
		this.energiaConsumidaMensual += consumoInstantaneo;
		BigInteger consumoInstantaneoPaillier = Paillier.getInstance().Encryption(BigInteger.valueOf(consumoInstantaneo));
		String jsonMessage = "{ \"consum\": \"" + consumoInstantaneoPaillier.toString() + "\", \"contadorId\":" + this.contadorId + ", \"zonaId\":" + this.zonaId + ", \"time\":" + this.time + "}";
		if (SocketTools.send(_IP_AGGREGADOR, port, jsonMessage))
			System.out.println("[Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " sends data; time=" + this.time + " to " + _IP_AGGREGADOR + ":" + port + "]");
		else
			System.out.println("ERROR [Contador=" + this.contadorId + " at zoneid=" + this.zonaId + " sends data; time=" + this.time + " to " + _IP_AGGREGADOR + ":" + port + "]");
	}

	public String toString() {
		return casa.toString();
	}
}
