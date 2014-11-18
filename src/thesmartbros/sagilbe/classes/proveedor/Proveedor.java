package thesmartbros.sagilbe.classes.proveedor;

import java.util.List;

//clase perteneciente al objeto proveedor

public class Proveedor {

	// lista de zonas
	private List<Zona> miszonas;

	private int consumoTotal;
	private int precioKw;

	// cálculo del precio
	private void calcular_precio(int consumo, int precio) {


		System.out.println("consumo:"+consumo);

		System.out.println("precio:"+precio);
		
	}

	// para enviar al técnico
	private void enviar_tecnico() {

		System.out.println("Enviando técnico");
		
	}

	
	//establece el estado que le entras como String a la zona que le pases como parametro
	private void actualizar_estado_zona(int num_zona, String estado) {
		this.miszonas.get(num_zona).setEstado(estado);
	}

	private void get_calle() {

	}

	public int getPrecioKw() {
		return precioKw;
	}

	public void setPrecioKw(int precioKw) {
		this.precioKw = precioKw;
	}

	public int getConsumoTotal() {
		return consumoTotal;
	}

	public void setConsumoTotal(int consumoTotal) {
		this.consumoTotal = consumoTotal;
	}

}
