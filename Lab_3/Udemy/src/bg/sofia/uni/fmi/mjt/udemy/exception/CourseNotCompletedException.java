package bg.sofia.uni.fmi.mjt.udemy.exception;

public class CourseNotCompletedException extends Exception{
    public CourseNotCompletedException (String message){
        super("CourseNotCompletedException: "+message);
    }
}
