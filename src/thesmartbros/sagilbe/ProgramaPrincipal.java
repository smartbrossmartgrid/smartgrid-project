package thesmartbros.sagilbe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import thesmartbros.sagilbe.classes.casa.Electrodomestico;
import thesmartbros.sagilbe.tools.Paillier;

public class ProgramaPrincipal {

	public static void main(String args[]) throws NumberFormatException, IOException {

		List<Electrodomestico> electrodomesticos = new ArrayList<Electrodomestico>();
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader (isr);
		
		
		// defino una serie de electrodomesticos
		Electrodomestico elec = new Electrodomestico();
		elec.setNombre("Tele");
		elec.setConsumo(120);
		elec.setEstado("encendido");
		elec.setForzar_apagado(false);
		elec.setForzar_encendido(false);

		// por ejemplo que esté abierto de 8 a 10h de la mañana
		// y de 12 a 14h de la mañana también
		elec.tiempos[0][0] = 8;
		elec.tiempos[1][0] = 10;

		elec.tiempos[0][1] = 12;
		elec.tiempos[1][1] = 14;

		electrodomesticos.add(0,elec);

		
		elec = new Electrodomestico();
		elec.setNombre("Microondas");
		elec.setConsumo(200);
		elec.setEstado("encendido");
		elec.setForzar_apagado(false);
		elec.setForzar_encendido(false);

		elec.tiempos[0][0] = 8;
		elec.tiempos[1][0] = 10;

		elec.tiempos[0][1] = 12;
		elec.tiempos[1][1] = 18;

		electrodomesticos.add(1,elec);

		elec = new Electrodomestico();
		elec.setNombre("PC");
		elec.setConsumo(50);
		elec.setEstado("encendido");
		elec.setForzar_apagado(false);
		elec.setForzar_encendido(false);

		elec.tiempos[0][0] = 8;
		elec.tiempos[1][0] = 10;

		elec.tiempos[0][1] = 12;
		elec.tiempos[1][1] = 24;

		electrodomesticos.add(elec);

		elec = new Electrodomestico();
		elec.setNombre("Luces");
		elec.setConsumo(200);
		elec.setEstado("encendido");
		elec.setForzar_apagado(false);
		elec.setForzar_encendido(false);

		elec.tiempos[0][0] = 10;
		elec.tiempos[1][0] = 11;

		elec.tiempos[0][1] = 12;
		elec.tiempos[1][1] = 14;

		electrodomesticos.add(elec);

		System.out.println("Prueba Eric");

		// Ejemplo Paillier
		Paillier p = Paillier.getInstance();

		// casa1
		BigInteger casa1 = new BigInteger("20");
		BigInteger casa1_enc = p.Encryption(casa1);

		// casa2
		BigInteger casa2 = new BigInteger("60");
		BigInteger casa2_enc = p.Encryption(casa2);

		// casa3
		BigInteger casa3 = new BigInteger("100");
		BigInteger casa3_enc = p.Encryption(casa3);

		// casa4
		BigInteger casa4 = new BigInteger("40");
		BigInteger casa4_enc = p.Encryption(casa4);

		// casa5
		BigInteger casa5 = new BigInteger("80");
		BigInteger casa5_enc = p.Encryption(casa5);

		// agregador
		BigInteger agregadorResult = p.AgreggatorFunction(p.nsquare, casa1_enc,
				casa2_enc, casa3_enc, casa4_enc, casa5_enc);

		// proveedor
		System.out.println(p.ProviderFunction(agregadorResult));

		// System.out.println(""+electrodomesticos.get(0).getNombre());

		System.out.println("Electrodoméstico 0:");
		System.out.println("Nombre:" + electrodomesticos.get(0).getNombre());
		System.out.println("Consumo" + electrodomesticos.get(0).getConsumo());
		System.out.println("Estado:" + electrodomesticos.get(0).getEstado());
		System.out.println("Tiempos:");

		int i = 0;
		while (i < electrodomesticos.get(0).max) {
			System.out.println("hora inicio:"
					+ electrodomesticos.get(0).tiempos[0][i] + "h");
			System.out.println("hora final:"
					+ electrodomesticos.get(0).tiempos[1][i] + "h");
			i++;
		}
		System.out.println("Electrodoméstico 1:");
		System.out.println("Nombre:" + electrodomesticos.get(1).getNombre());
		System.out.println("Consumo" + electrodomesticos.get(1).getConsumo());
		System.out.println("Estado:" + electrodomesticos.get(1).getEstado());
		System.out.println("Tiempos:");

		int k = 0;
		while (k < electrodomesticos.get(1).max) {
			System.out.println("hora inicio:"
					+ electrodomesticos.get(1).tiempos[0][k] + "h");
			System.out.println("hora final:"
					+ electrodomesticos.get(1).tiempos[1][k] + "h");
			k++;
		}
		

		System.out.println("Introduce número de electrodoméstico:");
		int elec_num = Integer.parseInt (br.readLine());
		System.out.println("Nombre:" + electrodomesticos.get(elec_num).getNombre());
		
		System.out.println("Introduce una hora:");
		int hora = Integer.parseInt (br.readLine());
		System.out.println("Consumo Actual a esa hora:" + electrodomesticos.get(elec_num).getConsumoActual(hora));
	}
}
