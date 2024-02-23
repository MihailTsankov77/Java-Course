package bg.sofia.uni.fmi.mjt.gym.member;

import java.util.Comparator;

public class GymMemberByProximityToGymComparator implements Comparator<GymMember> {

    private final Address gymAddress;

    public GymMemberByProximityToGymComparator(Address gymAddress) {
        this.gymAddress = gymAddress;
    }

    @Override
    public int compare(GymMember first, GymMember second) {
        double dist1 = gymAddress.getDistanceTo(first.getAddress());
        double dist2 = gymAddress.getDistanceTo(second.getAddress());

        if (dist1 == dist2) {
            return 0;
        }

        return dist1 < dist2 ? -1 : 1;
    }
}
