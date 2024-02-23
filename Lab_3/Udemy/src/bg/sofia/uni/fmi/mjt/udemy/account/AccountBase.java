package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public abstract class AccountBase implements Account{

    private final String username;
    protected double balance;
    protected Course[] courses = new Course[100];
    protected int coursesB = 0;

    protected double[] grades = new double[100];
    protected int gradesLen = 0;
    public AccountBase(String username, double balance){
        this.username=username;
        this.balance=balance;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void addToBalance(double amount) {
        if(amount<0){
            throw new IllegalArgumentException();
        }

        balance+=amount;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException {
        if(course==null || resourcesToComplete ==null){
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < coursesB; i++) {
            Course pCourse = courses[i];
            if(pCourse.equals(course)){
                for (Resource resource :
                        resourcesToComplete) {
                    course.completeResource(resource);
                }
                return;
            }
        }

        throw new CourseNotPurchasedException(course.getName());
    }

    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        if(grade<2.00 || grade>6.00){
            throw new IllegalArgumentException();
        }


        for (int i = 0; i < coursesB; i++) {
            Course pCourse = courses[i];
            if (pCourse.equals(course)) {
                if(!pCourse.isCompleted()){
                    throw new CourseNotCompletedException(course.getName());
                }

                course.complete();

                grades[gradesLen++] = grade;
                return;
            }
        }

        throw new CourseNotPurchasedException(course.getName());
    }

    @Override
    public Course getLeastCompletedCourse() {
       Course leastCompleted = null;

        for (int i = 0; i < coursesB; i++) {
            Course course = courses[i];
            if(leastCompleted==null || leastCompleted.getCompletionPercentage()>course.getCompletionPercentage()){
                leastCompleted = course;
            }
        }

        return leastCompleted;
    }
}
