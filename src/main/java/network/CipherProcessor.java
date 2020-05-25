package network;


public interface CipherProcessor {
    void decrypt(byte[] pack) throws Exception;
    byte[] encrypt(Package pack) throws Exception;
}
