package bg.sofia.uni.fmi.mjt.udemy.account;


import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class StandardAccount extends AccountBase {
    public StandardAccount(String username, double balance) {
        super(username, balance);
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

        if (balance < course.getPrice()) {
            throw new InsufficientBalanceException("" + balance);
        }

        course.purchase();

        balance -= course.getPrice();
        courses[coursesB++] =course;
    }
}
