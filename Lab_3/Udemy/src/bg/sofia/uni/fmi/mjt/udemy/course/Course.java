package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.util.Arrays;
import java.util.Objects;

public class Course implements Completable, Purchasable{
    private final String name;
    private final String description;
    private final double price;
    private final Resource[] content;
    private final CourseDuration totalTime;
    private final Category category;
    private double progress;
    private int areAllResourcesCompleted = 0; // -1 is undetermined

    private boolean isCompleted = false;
    private boolean isPurchased = false;

    public Course(String name, String description, double price, Resource[] content, Category category){
        this.name = name;
        this.description = description;
        this.price = price;
        this.content = new Resource[content.length];
        System.arraycopy(content, 0, this.content, 0, content.length);

        this.category = category;

        totalTime = CourseDuration.of(content);
    }


    @Override
    public boolean isCompleted() {
        if(!isCompleted && areAllResourcesCompleted()){
            isCompleted = true;
        }

        return isCompleted;
    }

    public void complete(){
        isCompleted = true;
    }

    public boolean areAllResourcesCompleted() {
        for (Resource resource :
                content) {
            areAllResourcesCompleted = resource.isCompleted()? 1 : 0;
        }


        return areAllResourcesCompleted ==1;
    }

    @Override
    public int getCompletionPercentage() {
        return (int) Math.round(progress);
    }

    @Override
    public void purchase() {
        isPurchased = true;
    }

    @Override
    public boolean isPurchased() {
        return isPurchased;
    }

    /**
     * Returns the name of the course.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the course.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the price of the course.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the category of the course.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns the content of the course.
     */
    public Resource[] getContent() {
        return content;
    }

    /**
     * Returns the total duration of the course.
     */
    public CourseDuration getTotalTime() {
        return totalTime;
    }

    /**
     * Completes a resource from the course.
     *
     * @param resourceToComplete the resource which will be completed.
     * @throws IllegalArgumentException if resourceToComplete is null.
     * @throws ResourceNotFoundException if the resource could not be found in the course.
     */
    public void completeResource(Resource resourceToComplete) throws ResourceNotFoundException {
        if(resourceToComplete==null){
            throw new IllegalArgumentException();
        }

        if(content.length==0){
            throw new ResourceNotFoundException(resourceToComplete.getName());
        }

        for (Resource resource :
                content) {
            if (resource.equals(resourceToComplete)) {
                if(!resource.isCompleted()){
                    resource.complete();
                    progress += 100d/content.length;

                    areAllResourcesCompleted = -1;
                }

                return;
            }
        }
        
        throw new ResourceNotFoundException(resourceToComplete.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Double.compare(price, course.price) == 0 && Double.compare(progress, course.progress) == 0 && areAllResourcesCompleted == course.areAllResourcesCompleted && isPurchased == course.isPurchased && Objects.equals(name, course.name) && Objects.equals(description, course.description) && Arrays.equals(content, course.content) && Objects.equals(totalTime, course.totalTime) && category == course.category;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, description, price, totalTime, category, progress, areAllResourcesCompleted, isCompleted, isPurchased);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
