package thesmartbros.sagilbe.classes.casa;

public class Electrodomestico {

	// nombre del electrodoméstico
	private String nombre;

	private int coste_energetico;
	// estado, encendido/apagado
	private String estado;

	// para apagar o encender
	private boolean forzar_apagado = false;
	private boolean forzar_encendido = false;

	// variable que defino yo mismo para decir cuantos tiempos tendremos cómo
	// máximo
	private int max = 15;

	// matriz que nos indica los tiempos en que el electrodomestico se encuentra
	// encendido
	// hora de inicio y hora de final
	public int[][] tiempos = new int[2][max];

	// el consumo fijo del electrodoméstico
	private int consumo;

	// para obtener el consumo actual
	public int getConsumoActual(int hora_actual) {

		// if(10<x && x<20)

		int i = 0;
		// mientras el contador sea menor al número máximo de entradas de horas
		while (i < max) {

			// si la hora actual se encuetra entre los dos valores de hora
			// inicial y final
			if (this.tiempos[0][i] <= hora_actual
					&& hora_actual >= this.tiempos[1][i]) {

				// devuelves el consumo
				return consumo;
			}
			i++;
		}

		// si está apagado devuelvo consumo 0
		if (this.forzar_apagado == true) {
			return 0;
		}

		// si está encendido devuelvo el consumo sin mirar la hora
		else if (this.forzar_encendido == true) {
			return consumo;
		}

		// devuelve el valor entero del consumo
		return 0;
	}

	// forzar el apagado de un electrodoméstico
	public boolean forzarapagar() {

		this.forzar_apagado = true;

		System.out.println("Apagando electrodoméstico");
		return forzar_apagado;

	}

	// forzar el encendido de un electrodoméstico
	public boolean forzarencender() {
		this.forzar_encendido = true;

		System.out.println("Electrodoméstico encendido");
		return forzar_encendido;

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

	/**
	 * @return the forzar_apagado
	 */
	public boolean isForzar_apagado() {
		return forzar_apagado;
	}

	/**
	 * @param forzar_apagado
	 *            the forzar_apagado to set
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
	 * @param forzar_encendido
	 *            the forzar_encendido to set
	 */
	public void setForzar_encendido(boolean forzar_encendido) {
		this.forzar_encendido = forzar_encendido;
	}

	/**
	 * @return the estado
	 */
	public String getEstado() {
		return estado;
	}

	/**
	 * @param estado
	 *            the estado to set
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/**
	 * @return the consumo
	 */
	public int getConsumo() {
		return consumo;
	}

	/**
	 * @param consumo
	 *            the consumo to set
	 */
	public void setConsumo(int consumo) {
		this.consumo = consumo;
	}

	/**
	 * @return the tiempos
	 */
	public int[][] getTiempos() {
		return tiempos;
	}

}
