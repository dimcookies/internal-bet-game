package bet.service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

@Component
public class EncryptUtils {

	@Value("${application.encryptKey:1234567890qwertyuiopasdf}")
	private String mainKey;

	private final Cipher cipher;

	public EncryptUtils() throws NoSuchPaddingException, NoSuchAlgorithmException {
		cipher = Cipher.getInstance("AES/ECB/NoPadding");
	}

	private String encrypt(String text, String username) throws Exception {
		byte[] keyBytes = (username + mainKey).substring(1, 25).getBytes();
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

		byte[] input = String.format("%16s", text).getBytes();

		// encryption pass
		byte[] cipherText = new byte[input.length];
		cipher.init(Cipher.ENCRYPT_MODE, key);
		int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
		cipher.doFinal(cipherText, ctLength);
		byte[] encodedBytes = Base64.getEncoder().encode(cipherText);
		return new String(encodedBytes);
	}

	private String decrypt(String encrypted, String username) throws Exception {
		byte[] keyBytes = (username + mainKey).substring(1, 25).getBytes();
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

		byte[] decodedBytes = Base64.getDecoder().decode(encrypted.getBytes());
		byte[] plainText = new byte[16];
		cipher.init(Cipher.DECRYPT_MODE, key);
		int ptLength = cipher.update(decodedBytes, 0, 16, plainText, 0);
		ptLength += cipher.doFinal(plainText, ptLength);
		return new String(plainText).trim();
	}

}

