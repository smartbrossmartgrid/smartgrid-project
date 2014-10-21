package thesmartbros.sagilbe.classes.proveedor;

import java.util.List;

//clase perteneciente al objeto proveedor

public class Proveedor {

	// lista de zonas
	private List<Zona> miszonas;

	private int consumoTotal;

	// cálculo del precio
	private void calcular_precio(int consumo) {

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

}
