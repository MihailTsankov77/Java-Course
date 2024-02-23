package bg.sofia.uni.fmi.mjt.bellot.server.room;

public class RoomThead extends Thread {
    private final Room room;

    RoomThead(Room room) {
        this.room = room;
    }

    @Override
    public void run() {
        room.start();
    }
}
