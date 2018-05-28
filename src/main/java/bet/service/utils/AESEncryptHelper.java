package bet.service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encrypt text with AES algorith using Base64 representation
 */

@Component
public class AESEncryptHelper implements EncryptHelper {

	@Value("${application.encryptKey:1234567890qwertyuiopasdf}")
	private String mainKey;

	private final Cipher cipher;

	public AESEncryptHelper() throws NoSuchPaddingException, NoSuchAlgorithmException {
		cipher = Cipher.getInstance("AES/ECB/NoPadding");
	}

	public String encrypt(String text, String salt) throws Exception {
		byte[] keyBytes = getKey(salt);
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
		//pad text to 16 characters
		byte[] input = String.format("%16s", text).getBytes();

		// encryption pass
		byte[] cipherText = new byte[input.length];
		cipher.init(Cipher.ENCRYPT_MODE, key);
		int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
		cipher.doFinal(cipherText, ctLength);
		//get base64 representation
		byte[] encodedBytes = Base64.getEncoder().encode(cipherText);
		return new String(encodedBytes);
	}

	public String decrypt(String encrypted, String salt) throws Exception {
		byte[] keyBytes = getKey(salt);
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

		//decrypt from base64
		byte[] decodedBytes = Base64.getDecoder().decode(encrypted.getBytes());
		//decryption pass
		byte[] plainText = new byte[16];
		cipher.init(Cipher.DECRYPT_MODE, key);
		int ptLength = cipher.update(decodedBytes, 0, 16, plainText, 0);
		ptLength += cipher.doFinal(plainText, ptLength);
		return new String(plainText).trim();
	}

	/**
	 * Combine salt with main key to provide encryption key
	 * @param salt
	 * @return
	 */
	private byte[] getKey(String salt) {
		// add main key to salt to create the actual keu
		return (salt + mainKey).substring(0, 24).getBytes();
	}

}
