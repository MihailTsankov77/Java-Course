package bg.sofia.uni.fmi.mjt.udemy.exception;

public class ResourceNotFoundException extends Exception{
    public ResourceNotFoundException (String message){
        super("ResourceNotFoundException:  "+ message);
    }
}
