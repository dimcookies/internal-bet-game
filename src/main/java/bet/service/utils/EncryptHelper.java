package bet.service.utils;

/**
 * Interface for encryption implementations
 */
public interface EncryptHelper {
    /**
     * Encrypt the provided text using the provided salt
     * @param text
     * @param salt
     * @return
     * @throws Exception
     */
    public String encrypt(String text, String salt) throws Exception;

    /**
     * Decrypt the provided text using the provided salt
     * @param encrypted
     * @param salt
     * @return
     * @throws Exception
     */
    public String decrypt(String encrypted, String salt) throws Exception;

}
