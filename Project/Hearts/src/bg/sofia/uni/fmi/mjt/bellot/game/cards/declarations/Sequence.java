package bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations;

public enum Sequence {
    TIERCE(20, 3),
    QUARTE(50, 4),
    QUINTE(100, 5);

    final int points;
    final int sequence;

    Sequence(int points, int sequence) {
        this.points = points;
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }

    public int getPoints() {
        return points;
    }
}
