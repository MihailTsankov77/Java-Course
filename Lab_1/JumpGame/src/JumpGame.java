public class JumpGame {

    private static boolean jumper(int[] array, int index) {
        if (index >= array.length) {
            return false;
        }

        if (array.length - 1 == index) {
            return true;
        }

        for (int i = 1; i <= array[index]; i++) {
            if (jumper(array, index + i)) {
                return true;
            }
        }

        return false;

    }

    public static boolean canWin(int[] array) {
        return jumper(array, 0);
    }
}