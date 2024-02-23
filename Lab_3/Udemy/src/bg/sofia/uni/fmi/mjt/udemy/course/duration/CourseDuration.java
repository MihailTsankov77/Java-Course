package bg.sofia.uni.fmi.mjt.udemy.course.duration;

import bg.sofia.uni.fmi.mjt.udemy.course.Resource;

public record CourseDuration (int hours, int minutes) {
    public CourseDuration {
        if (hours < 0 || hours > 24  || minutes < 0 || minutes > 60)
            throw new IllegalArgumentException("Course Duration out of range");
    }
    public static CourseDuration of (Resource [] content){
        int mins = 0;
        for (Resource resource : content)
            mins += resource.getDuration().minutes();
        return new CourseDuration(mins / 60, mins % 60);
    }

}