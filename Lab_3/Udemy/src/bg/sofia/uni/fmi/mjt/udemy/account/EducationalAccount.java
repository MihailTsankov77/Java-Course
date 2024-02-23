package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class EducationalAccount extends AccountBase {

    private int courseIndex = 0;

    public EducationalAccount(String username, double balance) {
        super(username, balance);
    }


    private boolean hasDiscount(){
        if(gradesLen<5 || gradesLen- courseIndex<5){
            return false;
        }
        double grade = 0;
        for (int i = gradesLen-1; i>=courseIndex; --i){
            grade += grades[i];
        }

        if(grade/5 >=4.50){
            courseIndex = gradesLen;
            return true;
        }
        courseIndex ++;
        return false;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if(coursesB>=100){
            throw new MaxCourseCapacityReachedException();
        }

        for (int i = 0; i < coursesB; i++) {
            Course pCourse = courses[i];
             if (pCourse.equals(course)) {
                 throw new CourseAlreadyPurchasedException(course.getName());
             }
         }

        double price = (1 - (hasDiscount() ? AccountType.EDUCATION.getDiscount() : 0)) * course.getPrice();
        if (balance < price) {
            throw new InsufficientBalanceException("" + balance);
        }

        course.purchase();

        course.complete();

        balance -= price;
        courses[coursesB++] =course;
    }
}
