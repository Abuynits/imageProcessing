import java.util.Arrays;

public class warmUpDay2 {
    public static void main(String[] args) {

    }

    public void addStrip(short[][] pixels, int stripHeight) {
        int middle = findMiddle(pixels);
        int deviationFromMiddle = (stripHeight - 1) / 2;
        for (int r = middle - deviationFromMiddle; r <= middle + deviationFromMiddle; r++) {
            Arrays.fill(pixels[r], (short) 0);
        }
    }

    private int findMiddle(short[][] pixels) {
        return pixels.length / 2 + 1;
    }

}
