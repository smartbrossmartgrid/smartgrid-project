package thesmartbros.sagilbe.tools;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

/**
 * Este programa ofrece las funcionalidades criptográficas siguientes 1. Cifrado
 * con AES 2. El descifrado con AES
 *
 * Algoritmo de alto nivel: 1. Generar una clave DES (especificar el tamaño de
 * la clave durante esta fase) 2. Cree el Cipher 3. Para cifrar: Inicializar el
 * cifrado para el cifrado 4. Para descifrar: Inicializar el cifrado para
 * descifrar
 */

public class SymmetricEncrypt {

	private SecretKey secretKey;
	private String Algorithm = "AES";

	private static SymmetricEncrypt INSTANCE = null;

	private synchronized static void createInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SymmetricEncrypt();
		}
	}

	public static SymmetricEncrypt getInstance() {
		if (INSTANCE == null)
			createInstance();
		return INSTANCE;
	}

	public SymmetricEncrypt() {
		/**
		 * Paso 1. Generación de una clave AES mediante keygenerator Inicializa
		 * el tamaño de clave de 128
		 * 
		 */
		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			secretKey = keyGen.generateKey();
		} catch (Exception exp) {
			System.out.println(" Exception inside constructor " + exp);
		}

	}

	/**
	 * Paso 2. Crear un sistema de cifrado mediante la especificación de los
	 * siguientes parámetros a. Nombre del algoritmo - aquí es AES
	 */

	public String encryptData(String text) {
		byte[] byteDataToEncrypt = text.getBytes();
		byte[] byteCipherText = new byte[200];
		String strCipherText;
		try {
			Cipher aesCipher = Cipher.getInstance(Algorithm);

			/**
			 * Paso 3. Inicialice el cifrado para el cifrado
			 */
			if (Algorithm.equals("AES")) {
				aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, aesCipher.getParameters());
			} else if (Algorithm.equals("RSA/ECB/PKCS1Padding")) {
				aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
			}

			/**
			 * Paso 4. Cifrar los datos 1. Declarar / inicializar los datos.
			 * Aquí los datos son de tipo String 2. Convertir el texto de
			 * entrada a Bytes 3. Cifrado de los bytes, utilizando el método
			 * doFinal
			 */
			byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
			strCipherText = Base64.encodeBase64String(byteCipherText);
			return strCipherText;
		} catch (NoSuchAlgorithmException noSuchAlgo) {
			System.out.println(" No Such Algorithm exists " + noSuchAlgo);
		} catch (NoSuchPaddingException noSuchPad) {
			System.out.println(" No Such Padding exists " + noSuchPad);
		} catch (InvalidKeyException invalidKey) {
			System.out.println(" Invalid Key " + invalidKey);
		} catch (BadPaddingException badPadding) {
			System.out.println(" Bad Padding " + badPadding);
		} catch (IllegalBlockSizeException illegalBlockSize) {
			System.out.println(" Illegal Block Size " + illegalBlockSize);
			illegalBlockSize.printStackTrace();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return null;
	}

	/**
	 * Paso 5. Descifrar los datos 1. Inicialice el cifrado para descifrar 2.
	 * Descifrar los bytes cifrados utilizando el método doFinal
	 */

	public String decryptData(String message) {
		byte[] byteCipherText = Base64.decodeBase64(message);
		byte[] byteDecryptedText = new byte[200];
		String strDecryptedText;
		try {
			Cipher aesCipher = Cipher.getInstance(Algorithm);
			if (Algorithm.equals("AES")) {
				aesCipher.init(Cipher.DECRYPT_MODE, secretKey, aesCipher.getParameters());
			} else if (Algorithm.equals("RSA/ECB/PKCS1Padding")) {
				aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
			}

			byteDecryptedText = aesCipher.doFinal(byteCipherText);
			strDecryptedText = new String(byteDecryptedText);
			return strDecryptedText;
		}

		catch (NoSuchAlgorithmException noSuchAlgo) {
			System.out.println(" No Such Algorithm exists " + noSuchAlgo);
		} catch (NoSuchPaddingException noSuchPad) {
			System.out.println(" No Such Padding exists " + noSuchPad);
		} catch (InvalidKeyException invalidKey) {
			System.out.println(" Invalid Key " + invalidKey);
			invalidKey.printStackTrace();
		} catch (BadPaddingException badPadding) {
			System.out.println(" Bad Padding " + badPadding);
			badPadding.printStackTrace();
		} catch (IllegalBlockSizeException illegalBlockSize) {
			System.out.println(" Illegal Block Size " + illegalBlockSize);
			illegalBlockSize.printStackTrace();
		} catch (InvalidAlgorithmParameterException invalidParam) {
			System.out.println(" Invalid Parameter " + invalidParam);
		}
		return null;
	}
}