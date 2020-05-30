package exceptions;

public class MagicByteException extends  Exception{
    public MagicByteException(byte val){super("Magic byte expected got "+val);}
}
