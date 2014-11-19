package thesmartbros.sagilbe.classes.casa;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ElectrodomesticoResource {

	public List<Electrodomestico> electrodomesticos;

	// Electrodomestico electrodomestico = new Electrodomestico();

	public ElectrodomesticoResource() {

		this.electrodomesticos = new ArrayList<Electrodomestico>();

		// defino una serie de electrodomesticos
		Electrodomestico elec = new Electrodomestico();
		elec.setNombre("Tele");
		elec.setConsumo(120);
		elec.setEstado("encendido");
		elec.setForzar_apagado(false);
		elec.setForzar_encendido(false);
		
		//por ejemplo que esté abierto de 8 a 10h de la mañana 
		// y de 12 a 14h de la mañana también
		elec.tiempos[0][0]=8;
		elec.tiempos[1][0]=10;

		elec.tiempos[0][1]=12;
		elec.tiempos[1][1]=14;
		
		
	//	elec.tiempos =  {{16,18}, {8,12}}, {9,5}};
		//elec.setTiempos();
		electrodomesticos.add(elec);

		elec.setNombre("Microondas");
		elec.setConsumo(200);
		elec.setEstado("encendido");
		elec.setForzar_apagado(false);
		elec.setForzar_encendido(false);

		
		
		elec.tiempos[0][0]=8;
		elec.tiempos[1][0]=10;
		
		elec.tiempos[0][1]=12;
		elec.tiempos[1][1]=18;
		
		electrodomesticos.add(elec);

		elec.setNombre("PC");
		elec.setConsumo(50);
		elec.setEstado("encendido");
		elec.setForzar_apagado(false);
		elec.setForzar_encendido(false);
		
		
		elec.tiempos[0][0]=8;
		elec.tiempos[1][0]=10;
		
		elec.tiempos[0][1]=12;
		elec.tiempos[1][1]=24;
		

		electrodomesticos.add(elec);

		elec.setNombre("Luces");
		elec.setConsumo(200);
		elec.setEstado("encendido");
		elec.setForzar_apagado(false);
		elec.setForzar_encendido(false);
		
		
		elec.tiempos[0][0]=10;
		elec.tiempos[1][0]=11;
		
		elec.tiempos[0][1]=12;
		elec.tiempos[1][1]=14;
		

		electrodomesticos.add(elec);

		
		
		
	}
	

	

}
