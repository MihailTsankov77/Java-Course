package bg.sofia.uni.fmi.mjt.udemy;

import bg.sofia.uni.fmi.mjt.udemy.account.Account;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotFoundException;

import java.lang.reflect.Array;

public class Udemy implements LearningPlatform{

    private final Account[] accounts;

    private final Course[] courses;

    public Udemy(Account[] accounts, Course[] courses){

        this.accounts = new Account[accounts.length];
        System.arraycopy(accounts, 0, this.accounts, 0, accounts.length);

        this.courses = new Course[courses.length];
        System.arraycopy(courses, 0, this.courses, 0, courses.length);
    }

    @Override
    public Course findByName(String name) throws CourseNotFoundException {
        if(name==null || name.isEmpty()){
            throw new IllegalArgumentException();
        }

        for (Course course :
                courses) {
            if (name.equals(course.getName())) {
                return course;
            }
        }

        throw new CourseNotFoundException(name);
    }

    @Override
    public Course[] findByKeyword(String keyword) {
        if(keyword==null || keyword.isBlank() || !keyword.matches("\\w+")){
            throw new IllegalArgumentException();
        }


        Course[] fCourses = new Course[courses.length];
        int index = 0;

        for (Course course :
                courses) {
            if (course.getName().contains(keyword)||course.getDescription().contains(keyword)) {
                fCourses[index++] = course;
            }
        }

        Course[] smallArr =  new Course[index];
        System.arraycopy(fCourses, 0, smallArr, 0, index);

        return smallArr;
    }

    @Override
    public Course[] getAllCoursesByCategory(Category category) {
        if(category==null){
            throw new IllegalArgumentException();
        }

        Course[] fCourses = new Course[courses.length];
        int index = 0;

        for (Course course :
                courses) {
            if (category == course.getCategory()) {
                fCourses[index++] = course;
            }
        }

        Course[] smallArr =  new Course[index];
        System.arraycopy(fCourses, 0, smallArr, 0, index);

        return smallArr;
    }

    @Override
    public Account getAccount(String name) throws AccountNotFoundException {
        if(name==null || name.isBlank()){
            throw new IllegalArgumentException();
        }

        for (Account account :
                accounts) {
            if (name.equals(account.getUsername())) {
                return account;
            }
        }

        throw new AccountNotFoundException(name);
    }

    @Override
    public Course getLongestCourse() {
        Course longest = null;

        for (Course course:
             courses) {
            if(longest==null || longest.getTotalTime().hours()<course.getTotalTime().hours() ||
                    (longest.getTotalTime().hours()==course.getTotalTime().hours() && longest.getTotalTime().minutes()<course.getTotalTime().minutes())
            ){
                longest = course;
            }
        }

        return longest;
    }

    @Override
    public Course getCheapestByCategory(Category category) {
        if(category==null){
            throw new IllegalArgumentException();
        }
        Course cheapest = null;

        for (Course course :
                courses) {
            if (cheapest == null || cheapest.getPrice() > course.getPrice()) {
                cheapest = course;
            }
        }

        return cheapest;
    }
}
