package thesmartbros.sagilbe.tools;

import thesmartbros.sagilbe.classes.casa.Contador;

public class VariablesGlobales {

	public final static String _IP_PROVIDER = "127.0.0.1";
	public final static String _IP_AGREGADOR = "127.0.0.1";
	public final static String _IP_CONTADOR = "127.0.0.1";

	public final static int _DEFAULT_PROVIDER_PORT = 50000;
	public final static int _DEFAULT_AGREGADOR_PORT = 40000;
	public final static int _DEFAULT_CONTADOR_PORT = 30000;

	public final static int _MAX_TIME_TO_NOT_RESPOND = 3; /* times */

	/* variables de mensaje de contador (100) */
	public final static int _MESSAGE_TYPE_ENVIAR_CONSUMO = 100;
	public final static int _MESSAGE_TYPE_ENVIAR_PRECIO_CONTADOR = 101;
	public final static int _MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS = 102;

	/* variable de mensajes de agregador (200) */
	public final static int _MESSAGE_TYPE_ENVIAR_CONSUMO_AGREGADO = 200;
	public final static int _MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_AGREGADOR = 202;
	public final static int _MESSAGE_TYPE_REQUEST_TECNICO = 203;


	/* variable de mensajes de proveedor (300) */
	public final static int _MESSAGE_TYPE_ENVIAR_PRECIO_PROVIDER = 300;
	public final static int _MESSAGE_TYPE_REQUEST_PAILLIER_PARAMETERS_PROVIDER = 302;
	public final static int _MESSAGE_TYPE_ENVIAR_TECNICO = 303;


	/* calculo del precio */
	public final static float _MAX_PRICE = 0.12337f; /* euros/kWh */
	public final static float _MIN_PRICE = 0.09292f; /* euros/kWh */
	public final static float _MAX_ENERGY_GENERATED = 5000.0f; /* kWh */
	public final static float _RATIO_TIME = Contador._THREAD_TIME_INTERVAL/10000; /* 1h son 10 s */
	
	/* perfiles de consumo */
	public final static int _PERFIL_DEFAULT = -1;
	public final static int _PERFIL_BAJO_CONSUMO = 1;
	public final static int _PERFIL_MEDIO_CONSUMO = 2;
	public final static int _PERFIL_ALTO_CONSUMO = 3;
	public final static int _PERFIL_TRABAJO_MANANA = 4;
	public final static int _PERFIL_TRABAJO_TARDE = 5;
	public final static int _PERFIL_4PERSONAS = 6;
	public final static int _PERFIL_SOLTERO = 7;

}
