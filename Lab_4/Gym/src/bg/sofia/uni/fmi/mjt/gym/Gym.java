package bg.sofia.uni.fmi.mjt.gym;

import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.GymMember;
import bg.sofia.uni.fmi.mjt.gym.member.GymMemberByNameComparator;
import bg.sofia.uni.fmi.mjt.gym.member.GymMemberByProximityToGymComparator;
import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Gym implements GymAPI {

    private final int capacity;
    private final Address address;
    private final TreeSet<GymMember> members = new TreeSet<>(new GymMemberByNameComparator());

    public Gym(int capacity, Address address) {
        this.capacity = capacity;
        this.address = address;
    }

    @Override
    public SortedSet<GymMember> getMembers() {
        return members;
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByName() {
        return members;
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByProximityToGym() {
        TreeSet<GymMember> sortedMembers = new TreeSet<>(new GymMemberByProximityToGymComparator(address));

        sortedMembers.addAll(members);

        return sortedMembers;
    }

    @Override
    public void addMember(GymMember member) throws GymCapacityExceededException {
        if (member == null) {
            throw new IllegalArgumentException("#addMember arg could not be null");
        }

        if (members.size() >= capacity) {
            throw new GymCapacityExceededException(capacity);
        }

        members.add(member);
    }

    @Override
    public void addMembers(Collection<GymMember> members) throws GymCapacityExceededException {
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("#addMembers arg could not be null");
        }

        if (this.members.size() + members.size() > capacity) {
            throw new GymCapacityExceededException(capacity);
        }

        this.members.addAll(members);
    }

    @Override
    public boolean isMember(GymMember member) {
        if (member == null) {
            throw new IllegalArgumentException("#isMember arg could not be null");
        }

        for (GymMember gymMember : members) {
            if (gymMember.equals(member)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isExerciseTrainedOnDay(String exerciseName, DayOfWeek day) {
        if (exerciseName == null || exerciseName.isEmpty() || day == null) {
            throw new IllegalArgumentException("#isExerciseTrainedOnDay arg could not be null");
        }

        for (GymMember member : members) {
            Map<DayOfWeek, Workout> trainingProgram = member.getTrainingProgram();
            for (Exercise exercise : trainingProgram.get(day).exercises()) {
                if (exercise.name().equals(exerciseName)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Map<DayOfWeek, List<String>> getDailyListOfMembersForExercise(String exerciseName) {
        if (exerciseName == null || exerciseName.isEmpty()) {
            throw new IllegalArgumentException("#getDailyListOfMembersForExercise arg could not be null");
        }
        Map<DayOfWeek, List<String>> dailyListOfMembersForExercise = new HashMap<>();

        for (GymMember member : members) {
            for (DayOfWeek day : member.getTrainingProgram().keySet()) {
                Workout workout = member.getTrainingProgram().get(day);

                for (Exercise exercise : workout.exercises()) {
                    if (exercise.name().equals(exerciseName)) {
                        dailyListOfMembersForExercise.putIfAbsent(day, new ArrayList<>());
                        dailyListOfMembersForExercise.get(day).add(member.getName());

                        break;
                    }
                }
            }
        }

        return dailyListOfMembersForExercise;
    }
}