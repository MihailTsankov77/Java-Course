package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class BusinessAccount extends AccountBase {
    private final Category[] allowedCategories;
    public BusinessAccount(String username, double balance, Category[] allowedCategories){
        super(username,balance);
        this.allowedCategories = allowedCategories;
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


        for (Category category :
                allowedCategories) {
            if (category == course.getCategory()) {

                double price = (1 - AccountType.BUSINESS.getDiscount()) * course.getPrice();

                if (balance < price) {
                    throw new InsufficientBalanceException("" + balance);
                }

                course.purchase();

                balance -= price;

                courses[coursesB++] =course;
                return;
            }
        }
        throw new IllegalArgumentException();


    }
}
