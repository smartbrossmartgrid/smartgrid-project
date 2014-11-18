package thesmartbros.sagilbe.classes.casa;

public class Electrodomestico {

	private long tiempo_uso;
	private long tiempo_apagado;
	private String nombre;
	private int coste_energetico;
	private boolean estado;

	private boolean forzar_apagado=false;
	private boolean forzar_encendido=false;
	
	//variable que defino yo mismo para decir cuantos tiempos tendremos cómo máximo
	private int max=15;
	
	//matriz que nos indica los tiempos en que el electrodomestico se encuentra encendido
	//hora de inicio y hora de final
	private int [][] tiempos = new int[2][max]; 

	
	
	
	
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

	/**
	 * @return the forzar_apagado
	 */
	public boolean isForzar_apagado() {
		return forzar_apagado;
	}

	/**
	 * @param forzar_apagado the forzar_apagado to set
	 */
	public void setForzar_apagado(boolean forzar_apagado) {
		this.forzar_apagado = forzar_apagado;
	}

	/**
	 * @return the forzar_encendido
	 */
	public boolean isForzar_encendido() {
		return forzar_encendido;
	}

	/**
	 * @param forzar_encendido the forzar_encendido to set
	 */
	public void setForzar_encendido(boolean forzar_encendido) {
		this.forzar_encendido = forzar_encendido;
	}

}
