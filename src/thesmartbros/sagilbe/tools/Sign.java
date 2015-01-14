package thesmartbros.sagilbe.tools;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import org.apache.commons.codec.binary.Base64;

public class Sign {

	KeyPair pair;
	PrivateKey priv;
	PublicKey pub;

	private static Sign INSTANCE = null;

	private synchronized static void createInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Sign();
		}
	}

	public static Sign getInstance() {
		if (INSTANCE == null)
			createInstance();
		return INSTANCE;
	}

	public String GenSig(String[] args) {
		byte[] realSig = null;

		/* Generate a DSA signature */

		if (args.length != 1) {
			System.out.println("Usage: GenSig nameOfFileToSign");
		} else
			try {
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA",
						"SUN");
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG",
						"SUN");

				keyGen.initialize(1024, random);

				pair = keyGen.generateKeyPair();
				priv = pair.getPrivate();
				pub = pair.getPublic();

				Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");

				dsa.initSign(priv);

				byte[] buffer = new byte[1024];
				buffer = args[0].getBytes(Charset.forName("UTF-8"));
				dsa.update(buffer);

				realSig = dsa.sign();

			} catch (Exception e) {
				System.err.println("Caught exception " + e.toString());
			}
		 String base64String = Base64.encodeBase64String(realSig);
		 return base64String;

	};

	public boolean VerSig(String[] args) {

		boolean verifies = false;

		/* Verify a DSA signature */

		if (args.length != 2) {
			System.out
					.println("Usage: VerSig publickeyfile signaturefile datafile");
		} else
			try {
				PublicKey pubKey = pub;

				Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
				sig.initVerify(pubKey);
				
				sig.update(args[0].getBytes(Charset.forName("UTF-8")));
				
				byte[] sigToVerify= Base64.decodeBase64(args[1]);

				verifies = sig.verify(sigToVerify);

				System.out.println("signature verifies: " + verifies);

			} catch (Exception e) {
				System.err.println("Caught exception " + e.toString());
			}
		;
		return verifies;

	}

}
