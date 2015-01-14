package thesmartbros.sagilbe.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public final class Encrip_Decrip {
	static KeyPair pair;
	static PrivateKey priv;
	static PublicKey pub;
	public static final String ALGORITHM = "RSA";


	private static Encrip_Decrip INSTANCE = null;

	private synchronized static void createInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Encrip_Decrip();
		}
	}

	public static Encrip_Decrip getInstance() {
		if (INSTANCE == null)
			createInstance();
		return INSTANCE;
	}

	public static String encrypt(String text) {

		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance(ALGORITHM);
			keyGen.initialize(2048);
			pair = keyGen.generateKeyPair();
			pub = pair.getPublic();
			priv = pair.getPrivate();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		byte[] cipherText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			// encrypt the plain text using the public key

			cipher.init(Cipher.ENCRYPT_MODE, pub);
			cipherText = cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String base64String = Base64.encodeBase64String(cipherText);
		return base64String;
	}

	public static String decrypt(String message) {
		byte[] dectyptedText = null;
		byte[] text = Base64.decodeBase64(message);
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);

			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, priv);
			dectyptedText = cipher.doFinal(text);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new String(dectyptedText);
	}
}