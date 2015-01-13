package thesmartbros.sagilbe.classes.casa;

import java.util.ArrayList;
import java.util.List;

import thesmartbros.sagilbe.tools.VariablesGlobales;

public class ElectrodomesticoResource {

	public List<Electrodomestico> electrodomesticos;

	// Electrodomestico electrodomestico = new Electrodomestico();

	public ElectrodomesticoResource(int perfil) {
		this.electrodomesticos = new ArrayList<Electrodomestico>();
		Electrodomestico elec = new Electrodomestico();

		switch (perfil) {

		// perfiles con electrodomesticos dentro con diferentes tipos de consumo
		// cada uno

		
		//constructor de electrodomestico
		
		// Nombre del electrodoméstico
		// Cuanto Gasta
		// tiempo1 inicio, tiempo1 final
		// tiempo2 inicio tiempo2 final
		
		
		case VariablesGlobales._PERFIL_BAJO_CONSUMO:

			elec = new Electrodomestico("Nevera", 60, 0, 12, 12, 23);
			electrodomesticos.add(elec);
			elec = new Electrodomestico("Luces", 100, 10, 11, 12, 14);
			electrodomesticos.add(elec);

			break;

		case VariablesGlobales._PERFIL_4PERSONAS:

			elec = new Electrodomestico("Nevera", 160, 0, 12, 12, 23);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("PC", 500, 8, 10, 11, 13);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("Luces", 500, 10, 11, 12, 14);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("Tele", 500, 10, 11, 12, 14);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("Microondas", 500, 10, 11, 12, 14);
			electrodomesticos.add(elec);

			break;

		case VariablesGlobales._PERFIL_ALTO_CONSUMO:

			elec = new Electrodomestico("Nevera", 160, 0, 12, 12, 23);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("PC", 500, 8, 10, 11, 13);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("Luces", 500, 10, 11, 12, 14);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("Horno", 500, 10, 11, 12, 14);
			electrodomesticos.add(elec);
			elec = new Electrodomestico("Microondas", 500, 10, 11, 12, 14);
			electrodomesticos.add(elec);
			elec = new Electrodomestico("Tele", 500, 10, 11, 12, 14);
			electrodomesticos.add(elec);
			elec = new Electrodomestico("Calefaccion", 700, 10, 13, 15, 20);
			electrodomesticos.add(elec);

			break;

		case VariablesGlobales._PERFIL_MEDIO_CONSUMO:
			elec = new Electrodomestico("Horno", 500, 10, 11, 12, 14);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("Nevera", 160, 0, 12, 12, 23);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("PC", 500, 8, 10, 11, 13);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("Luces", 500, 10, 11, 12, 14);
			electrodomesticos.add(elec);

			break;

		case VariablesGlobales._PERFIL_SOLTERO:

			elec = new Electrodomestico("Nevera", 60, 0, 12, 12, 23);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("PC", 200, 8, 10, 11, 13);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("Luces", 100, 10, 11, 12, 14);
			electrodomesticos.add(elec);
			break;

		case VariablesGlobales._PERFIL_TRABAJO_MANANA:

			elec = new Electrodomestico("Nevera", 160, 0, 12, 12, 23);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("PC", 300, 15, 16, 19, 23);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("Luces", 500, 15, 16, 17, 23);
			electrodomesticos.add(elec);
			break;

		case VariablesGlobales._PERFIL_TRABAJO_TARDE:

			elec = new Electrodomestico("Nevera", 160, 0, 12, 12, 23);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("PC", 500, 8, 10, 11, 13);
			electrodomesticos.add(elec);

			elec = new Electrodomestico("Luces", 500, 10, 11, 12, 14);
			electrodomesticos.add(elec);
			break;

		}

	}

	public String toString() {
		String result = "";
		for (int j = 0; j < electrodomesticos.size(); j++) {
			result += "Nombre: " + electrodomesticos.get(j).getNombre() + "\n";
			result += "Consumo: " + electrodomesticos.get(j).getConsumo()
					+ "\n";
			result += "Estado: " + electrodomesticos.get(j).getEstado() + "\n";
			result += "Tiempos: ";

			int i = 0;
			while (i < electrodomesticos.get(j).max) {
				result += "[" + electrodomesticos.get(j).tiempos[0][i];
				result += "," + electrodomesticos.get(j).tiempos[1][i] + "] ";
				i++;
			}
			result += "\n";
		}
		return result;
	}

	public int getConsumoTotal(int horaActual) {
		int consumto_total = 0;
		for (int i = 0; i < electrodomesticos.size(); i++) {
			consumto_total += electrodomesticos.get(i).getConsumoActual(
					horaActual);
		return consumto_total;
	}

}
