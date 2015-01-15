package thesmartbros.sagilbe;

import thesmartbros.sagilbe.classes.agregador.Agregador;
import thesmartbros.sagilbe.classes.casa.Contador;
import thesmartbros.sagilbe.classes.proveedor.Proveedor;
import thesmartbros.sagilbe.tools.VariablesGlobales;

public class ProgramaPrincipal {

	private static int numContadores = 0;
	private static int numZones = 0;

	public static void main(String args[]) {
		Proveedor provider = new Proveedor();
		provider.start();

		// Ciudad de Castelldefels
		Agregador ag0 = new Agregador(numZones);
		ag0.start();

		Contador c01 = new Contador(numContadores++, numZones, 41.276808f, 1.966329f, 0.05000f, VariablesGlobales._PERFIL_ALTO_CONSUMO);
		Contador c02 = new Contador(numContadores++, numZones, 41.278787f, 1.967584f, 0.30000f, VariablesGlobales._PERFIL_ALTO_CONSUMO);
		Contador c03 = new Contador(numContadores++, numZones, 41.274869f, 1.973668f, 0.25000f, VariablesGlobales._PERFIL_TRABAJO_MANANA);
		Contador c04 = new Contador(numContadores++, numZones, 41.276808f, 1.966329f, 0.20000f, VariablesGlobales._PERFIL_TRABAJO_MANANA);
		Contador c05 = new Contador(numContadores++, numZones, 41.265896f, 1.953828f, 0.15000f, VariablesGlobales._PERFIL_SOLTERO);
		Contador c06 = new Contador(numContadores++, numZones, 41.266977f, 1.967604f, 0.10000f, VariablesGlobales._PERFIL_SOLTERO, 6);
		//System.out.println(c1.toString());
		c01.work();
		c02.work();
		c03.work();
		c04.work();
		c05.work();
		c06.work();

		numZones++;

		// Ciudad de Gava
		Agregador ag1 = new Agregador(numZones);
		ag1.start();

		Contador c11 = new Contador(numContadores++, numZones, 41.301757f, 2.001421f, 0.4000f, VariablesGlobales._PERFIL_4PERSONAS);
		Contador c12 = new Contador(numContadores++, numZones, 41.304303f, 2.003546f, 0.4500f, VariablesGlobales._PERFIL_MEDIO_CONSUMO);
		Contador c13 = new Contador(numContadores++, numZones, 41.306286f, 2.004576f, 0.5000f, VariablesGlobales._PERFIL_TRABAJO_TARDE, 13);
		//System.out.println(c1.toString());
		c11.work();
		c12.work();
		c13.work();

		/* numZones++;
		 * 
		 * // Ciudad de Viladecans Agregador ag2 = new Agregador(numZones);
		 * ag2.start();
		 * 
		 * Contador c21 = new Contador(numContadores++, numZones, 41.316037f,
		 * 2.015090f, VariablesGlobales._PERFIL_SOLTERO); Contador c22 = new
		 * Contador(numContadores++, numZones, 41.317262f, 2.017386f,
		 * VariablesGlobales._PERFIL_SOLTERO); Contador c23 = new
		 * Contador(numContadores++, numZones, 41.315699f, 2.025261f,
		 * VariablesGlobales._PERFIL_MEDIO_CONSUMO);
		 * //System.out.println(c1.toString()); c21.work(); c22.work();
		 * c23.work(); */
	}
}
