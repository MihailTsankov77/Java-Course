package bg.sofia.uni.fmi.mjt.udemy.exception;

public class CourseNotPurchasedException extends Exception{
    public CourseNotPurchasedException (String message){
        super("CourseNotPurchasedException: "+message);
    }
}
