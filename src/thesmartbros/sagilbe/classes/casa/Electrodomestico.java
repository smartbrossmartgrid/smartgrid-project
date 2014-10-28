package thesmartbros.sagilbe.classes.casa;

public class Electrodomestico {

	private long tiempo_uso;
	private long tiempo_apagado;
	private String nombre;
	private int coste_energetico;
	private boolean estado;

	public long getTiempo_uso() {
		return tiempo_uso;
	}

	public void setTiempo_uso(long tiempo_uso) {
		this.tiempo_uso = tiempo_uso;
	}

	public long getTiempo_apagado() {
		return tiempo_apagado;
	}

	public void setTiempo_apagado(long tiempo_apagado) {
		this.tiempo_apagado = tiempo_apagado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getCoste_energetico() {
		return coste_energetico;
	}

	public void setCoste_energetico(int coste_energetico) {
		this.coste_energetico = coste_energetico;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

}
