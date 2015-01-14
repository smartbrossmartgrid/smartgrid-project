package thesmartbros.sagilbe;

import java.io.IOException;

import thesmartbros.sagilbe.classes.agregador.Agregador;
import thesmartbros.sagilbe.classes.casa.Contador;
import thesmartbros.sagilbe.classes.proveedor.Proveedor;
import thesmartbros.sagilbe.tools.ToolsMap;
import thesmartbros.sagilbe.tools.VariablesGlobales;

public class ProgramaPrincipal {

	private static int numContadores = 0;
	private static int numZones = 0;

	public static void main(String args[]) throws NumberFormatException, IOException {
		Proveedor provider = new Proveedor();
		provider.start();

		// Ciudad de Castelldefels
		Agregador ag0 = new Agregador(numZones);
		ag0.start();

		Contador c01 = new Contador(numContadores++, numZones, 41.276808f, 1.966329f, VariablesGlobales._PERFIL_ALTO_CONSUMO);
		Contador c02 = new Contador(numContadores++, numZones, 41.278787f, 1.967584f, VariablesGlobales._PERFIL_ALTO_CONSUMO);
		Contador c03 = new Contador(numContadores++, numZones, 41.274869f, 1.973668f, VariablesGlobales._PERFIL_TRABAJO_MANANA);
		Contador c04 = new Contador(numContadores++, numZones, 41.276808f, 1.966329f, VariablesGlobales._PERFIL_TRABAJO_MANANA);
		Contador c05 = new Contador(numContadores++, numZones, 41.265896f, 1.953828f, VariablesGlobales._PERFIL_SOLTERO);
		Contador c06 = new Contador(numContadores++, numZones, 41.266977f, 1.967604f, VariablesGlobales._PERFIL_SOLTERO, 3);
		//System.out.println(c1.toString());
		c01.work();
		/*c02.work();
		c03.work();
		c04.work();
		c05.work();
		c06.work();

		numZones++;
/*
		// Ciudad de Gava
		Agregador ag1 = new Agregador(numZones);
		ag1.start();

		Contador c11 = new Contador(numContadores++, numZones, 41.301757f, 2.001421f);
		Contador c12 = new Contador(numContadores++, numZones, 41.304303f, 2.003546f);
		Contador c13 = new Contador(numContadores++, numZones, 41.306286f, 2.004576f);
		//System.out.println(c1.toString());
		c11.work();
		c12.work();
		c13.work();

		numZones++;

		// Ciudad de Viladecans
		Agregador ag2 = new Agregador(numZones);
		ag2.start();

		Contador c21 = new Contador(numContadores++, numZones, 41.316037f, 2.015090f);
		Contador c22 = new Contador(numContadores++, numZones, 41.317262f, 2.017386f);
		Contador c23 = new Contador(numContadores++, numZones, 41.315699f, 2.025261f);
		//System.out.println(c1.toString());
		c21.work();
		c22.work();
		c23.work();

		*/
		
		
		//List<Electrodomestico> electrodomesticos = new ArrayList<Electrodomestico>();
		//InputStreamReader isr = new InputStreamReader(System.in);
		//BufferedReader br = new BufferedReader (isr);

		/*// defino una serie de electrodomesticos Electrodomestico elec = new
		 * Electrodomestico(); elec.setNombre("Tele"); elec.setConsumo(120);
		 * elec.setEstado("encendido"); elec.setForzar_apagado(false);
		 * elec.setForzar_encendido(false);
		 * 
		 * // por ejemplo que esté abierto de 8 a 10h de la mañana // y de 12
		 * a 14h de la mañana también elec.tiempos[0][0] = 8;
		 * elec.tiempos[1][0] = 10;
		 * 
		 * elec.tiempos[0][1] = 12; elec.tiempos[1][1] = 14;
		 * 
		 * electrodomesticos.add(0,elec);
		 * 
		 * 
		 * elec = new Electrodomestico(); elec.setNombre("Microondas");
		 * elec.setConsumo(200); elec.setEstado("encendido");
		 * elec.setForzar_apagado(false); elec.setForzar_encendido(false);
		 * 
		 * elec.tiempos[0][0] = 8; elec.tiempos[1][0] = 10;
		 * 
		 * elec.tiempos[0][1] = 12; elec.tiempos[1][1] = 18;
		 * 
		 * electrodomesticos.add(1,elec);
		 * 
		 * elec = new Electrodomestico(); elec.setNombre("PC");
		 * elec.setConsumo(50); elec.setEstado("encendido");
		 * elec.setForzar_apagado(false); elec.setForzar_encendido(false);
		 * 
		 * elec.tiempos[0][0] = 8; elec.tiempos[1][0] = 10;
		 * 
		 * elec.tiempos[0][1] = 12; elec.tiempos[1][1] = 24;
		 * 
		 * electrodomesticos.add(elec);
		 * 
		 * elec = new Electrodomestico(); elec.setNombre("Luces");
		 * elec.setConsumo(200); elec.setEstado("encendido");
		 * elec.setForzar_apagado(false); elec.setForzar_encendido(false);
		 * 
		 * elec.tiempos[0][0] = 10; elec.tiempos[1][0] = 11;
		 * 
		 * elec.tiempos[0][1] = 12; elec.tiempos[1][1] = 14;
		 * 
		 * electrodomesticos.add(elec);
		 * 
		 * System.out.println("Prueba Eric");
		 * 
		 * // Ejemplo Paillier Paillier p = Paillier.getInstance();
		 * 
		 * // casa1 BigInteger casa1 = new BigInteger("20"); BigInteger
		 * casa1_enc = p.Encryption(casa1);
		 * 
		 * // casa2 BigInteger casa2 = new BigInteger("60"); BigInteger
		 * casa2_enc = p.Encryption(casa2);
		 * 
		 * // casa3 BigInteger casa3 = new BigInteger("100"); BigInteger
		 * casa3_enc = p.Encryption(casa3);
		 * 
		 * // casa4 BigInteger casa4 = new BigInteger("40"); BigInteger
		 * casa4_enc = p.Encryption(casa4);
		 * 
		 * // casa5 BigInteger casa5 = new BigInteger("80"); BigInteger
		 * casa5_enc = p.Encryption(casa5);
		 * 
		 * // agregador BigInteger agregadorResult =
		 * p.AgreggatorFunction(p.nsquare, casa1_enc, casa2_enc, casa3_enc,
		 * casa4_enc, casa5_enc);
		 * 
		 * // proveedor System.out.println(p.ProviderFunction(agregadorResult));
		 * 
		 * // System.out.println(""+electrodomesticos.get(0).getNombre());
		 * 
		 * System.out.println("Electrodoméstico 0:");
		 * System.out.println("Nombre:" + electrodomesticos.get(0).getNombre());
		 * System.out.println("Consumo" +
		 * electrodomesticos.get(0).getConsumo()); System.out.println("Estado:"
		 * + electrodomesticos.get(0).getEstado());
		 * System.out.println("Tiempos:");
		 * 
		 * int i = 0; while (i < electrodomesticos.get(0).max) {
		 * System.out.println("hora inicio:" +
		 * electrodomesticos.get(0).tiempos[0][i] + "h");
		 * System.out.println("hora final:" +
		 * electrodomesticos.get(0).tiempos[1][i] + "h"); i++; }
		 * System.out.println("Electrodoméstico 1:");
		 * System.out.println("Nombre:" + electrodomesticos.get(1).getNombre());
		 * System.out.println("Consumo" +
		 * electrodomesticos.get(1).getConsumo()); System.out.println("Estado:"
		 * + electrodomesticos.get(1).getEstado());
		 * System.out.println("Tiempos:");
		 * 
		 * int k = 0; while (k < electrodomesticos.get(1).max) {
		 * System.out.println("hora inicio:" +
		 * electrodomesticos.get(1).tiempos[0][k] + "h");
		 * System.out.println("hora final:" +
		 * electrodomesticos.get(1).tiempos[1][k] + "h"); k++; }
		 * 
		 * 
		 * System.out.println("Introduce número de electrodoméstico:"); int
		 * elec_num = Integer.parseInt (br.readLine());
		 * System.out.println("Nombre:" +
		 * electrodomesticos.get(elec_num).getNombre());
		 * 
		 * System.out.println("Introduce una hora:"); int hora =
		 * Integer.parseInt (br.readLine());
		 * System.out.println("Consumo Actual a esa hora:" +
		 * electrodomesticos.get(elec_num).getConsumoActual(hora)); */

	}
}
