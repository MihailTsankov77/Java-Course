package bg.sofia.uni.fmi.mjt.udemy.exception;

public class CourseAlreadyPurchasedException extends Exception{
    public CourseAlreadyPurchasedException (String message){
        super("CourseAlreadyPurchasedException: "+message);
    }
}

