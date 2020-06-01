package utils;

public class Properties {
    public static final int SERVER_PORT = 2305;
    public final static int packetPartFirstLengthWithoutwLen = Byte.BYTES + Byte.BYTES + Long.BYTES;
    public final static int packetPartFirstLength = packetPartFirstLengthWithoutwLen + Integer.BYTES;
    public final static int packetPartFirstLengthWithCRC16 = packetPartFirstLength + Short.BYTES;
    public static final int BYTES_WITHOUT_MESSAGE = Integer.BYTES + Integer.BYTES;
    public static final int MAX_MESSAGE_SIZE = 255;
    public static final int BYTES_MAX_SIZE = BYTES_WITHOUT_MESSAGE + MAX_MESSAGE_SIZE;
    public final static int packetMaxSize = packetPartFirstLengthWithCRC16 + BYTES_MAX_SIZE;
    public final static String INET_ADDRESS_NAME = null;


}
