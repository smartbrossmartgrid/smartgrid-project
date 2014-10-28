package thesmartbros.sagilbe.classes.casa;

public class Contador {

	private float precio_acumulado;
	private float precio_actual;
	private int energia_consumida;
	private float latitud;
	private float longitud;

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

	public int getEnergia_consumida() {
		return energia_consumida;
	}

	public void setEnergia_consumida(int energia_consumida) {
		this.energia_consumida = energia_consumida;
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
