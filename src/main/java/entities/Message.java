package entities;

import java.util.Objects;

public class Message {
    private final int cType, bUserId;
    private final String message;

    public enum Commands{
        GET_PRODUCT_AMOUNT,
        DEL_PRODUCT,
        ADD_PRODUCT,
        ADD_GROUP,
        ADD_PRODUCT_TO_GROUP,
        SET_PRODUCT_PRICE;


    }

    public Message(int cType, int bUserId, String message) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }

    public int getcType() {
        return cType;
    }

    public int getbUserId() {
        return bUserId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return cType == message1.cType &&
                bUserId == message1.bUserId &&
                Objects.equals(message, message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cType, bUserId, message);
    }

    @Override
    public String toString() {
        return "Message{" +
                "cType=" + cType +
                ", bUserId=" + bUserId +
                ", message='" + message + '\'' +
                '}';
    }
}
