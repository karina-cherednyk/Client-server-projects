

public final class Package{
    private byte bSrc;
    private long bPktId;
    private int cType, bUserId;
    private String message;


    public Package(byte bSrc, long bPktId, int cType, int bUserId, String message) {
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }

    //unique number of client app
    public byte getbSrc() {
        return bSrc;
    }
    //package number
    public long getbPktId() {
        return bPktId;
    }
    //command code
    public int getcType() {
        return cType;
    }
    //user id
    public int getbUserId() {
        return bUserId;
    }
    //useful message
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Package aPackage = (Package) o;

        return bSrc == aPackage.bSrc &&
                bPktId == aPackage.bPktId &&
                cType == aPackage.cType &&
                bUserId == aPackage.bUserId &&
                message.equals(aPackage.message);
    }


}