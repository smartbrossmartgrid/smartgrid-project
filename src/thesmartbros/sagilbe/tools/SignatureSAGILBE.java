package thesmartbros.sagilbe.tools;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import org.apache.commons.codec.binary.Base64;

public class SignatureSAGILBE {

	private static KeyPair pair;
	private static PrivateKey _PRIVATE_KEY;
	private static PublicKey _PUBLIC_KEY;

	private static SignatureSAGILBE INSTANCE = null;

	private synchronized static void createInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignatureSAGILBE();
			try {
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

				keyGen.initialize(1024, random);

				pair = keyGen.generateKeyPair();
				_PRIVATE_KEY = pair.getPrivate();
				_PUBLIC_KEY = pair.getPublic();
			} catch (Exception e) {
				PrinterTools.errorsLog("ERROR: "+e.getMessage());
			}
		}
	}

	public static SignatureSAGILBE getInstance() {
		if (INSTANCE == null)
			createInstance();
		return INSTANCE;
	}

	public String GenSig(String... args) {
		byte[] realSig = null;
		byte[] buffer = null;
		String mensaje;

		/* Generate a DSA signature */

		if (args.length != 1) {
			PrinterTools.errorsLog("ERROR: Usage: GenSig nameOfFileToSign");
		} else {
			mensaje = args[0];
			String mensajeAFirmar = removeLastChar(mensaje); // last char es el "}"
			buffer = mensajeAFirmar.getBytes(Charset.forName("UTF-8"));
			try {
				Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
				dsa.initSign(_PRIVATE_KEY);
				dsa.update(buffer);
				realSig = dsa.sign();
			} catch (Exception e) {
				System.err.println("Caught exception " + e.toString());
			}
			String signature = Base64.encodeBase64String(realSig);
			mensaje = mensajeAFirmar + ", \"signature\": \"" + signature + "\" }";
			return mensaje;
		}
		return null;
	}

	public boolean VerSig(String... args) {
		boolean verifies = false;
		byte[] message = null;
		byte[] signatureToVerify = null;
		/* Verify a DSA signature */
		if (args.length != 2) {
			PrinterTools.errorsLog("ERROR: Usage: VerSig publickeyfile signaturefile datafile");
		} else {
			try {
				String messageToVerify = removeSignature(args[0]);
				message = messageToVerify.getBytes(Charset.forName("UTF-8"));
				signatureToVerify = Base64.decodeBase64(args[1]);

				Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
				sig.initVerify(_PUBLIC_KEY);
				sig.update(message);
				verifies = sig.verify(signatureToVerify);
				PrinterTools.printEJSON("signature verifies: " + verifies);
			} catch (Exception e) {
				System.err.println("Caught exception " + e.toString());
			}
		}
		return verifies;

	}

	private String removeLastChar(String str) {
		if (str.length() > 0 && str.charAt(str.length() - 1) == '}') {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	private String removeSignature(String str) {
		return str.substring(0, str.indexOf(", \"signature\": \""));
	}

}
