package bg.sofia.uni.fmi.mjt.gym.member;

import java.util.Comparator;

public class GymMemberByNameComparator implements Comparator<GymMember> {

    @Override
    public int compare(GymMember first, GymMember second) {
        return first.getName().compareTo(second.getName());
    }

}