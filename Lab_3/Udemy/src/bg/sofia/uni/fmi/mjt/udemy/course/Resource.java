package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;

import java.util.Objects;

public class Resource implements Completable{
    private final String name;
    private int progress = 0;
    private final ResourceDuration duration;

    public Resource(String name, ResourceDuration duration){
        this.name = name;
        this.duration = duration;
    }

    @Override
    public boolean isCompleted() {
        return progress==100;
    }

    @Override
    public int getCompletionPercentage() {
        return progress;
    }

    /**
     * Returns the resource name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total duration of the resource.
     */
    public ResourceDuration getDuration() {
        return duration;
    }

    /**
     * Marks the resource as completed.
     */
    public void complete() {
        progress = 100;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return progress == resource.progress && Objects.equals(name, resource.name) && Objects.equals(duration, resource.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, progress, duration);
    }
}
