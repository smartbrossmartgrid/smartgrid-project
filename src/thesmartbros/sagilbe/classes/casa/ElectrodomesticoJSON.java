package thesmartbros.sagilbe.classes.casa;

import java.io.Serializable;

public class ElectrodomesticoJSON implements Serializable {

	private String nombre;
	private boolean encendido;
	private int consumo;

	public ElectrodomesticoJSON(String nombre, int consumo, boolean encendido) {
		this.nombre = nombre;
		this.consumo = consumo;
		this.encendido = encendido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isEncendido() {
		return encendido;
	}

	public void setEncendido(boolean encendido) {
		this.encendido = encendido;
	}

	public int getConsumo() {
		return consumo;
	}

	public void setConsumo(int consumo) {
		this.consumo = consumo;
	}
}
