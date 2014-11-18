package thesmartbros.sagilbe;

import java.math.BigInteger;

import thesmartbros.sagilbe.tools.Paillier;

public class ProgramaPrincipal {
	
	public static void main(String args[]) {
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
        
        //agregador
        BigInteger agregadorResult = p.AgreggatorFunction(p.nsquare, casa1_enc, casa2_enc, casa3_enc, casa4_enc, casa5_enc);
        
        //proveedor
        System.out.println(p.ProviderFunction(agregadorResult));
	}
}
