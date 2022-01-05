import processing.core.PApplet;

public class ColorMasking implements PixelFilter, Clickable {
    static private int targetR = 0, targetG = 0, targetB = 0;
    static final int VAL_SUBTRACT = 10;
    static private int tolerance = 50;
    static private short[][] redChannel, greenChannel, blueChannel;

    // must implement pixel filter interface is see control plus click
    @Override
    public DImage processImage(DImage img) {

        redChannel = img.getRedChannel();
        greenChannel = img.getGreenChannel();
        blueChannel = img.getBlueChannel();
        double wantedDistance = getColorDistance(targetR, targetB, targetG);
        for (int r = 0; r < redChannel.length; r++) {
            for (int c = 0; c < redChannel[r].length; c++) {
                if (closeEnough(r, c, wantedDistance)) {
                    redChannel[r][c] = 0;
                    greenChannel[r][c] = 0;
                    blueChannel[r][c] = 0;
                } else {
                    redChannel[r][c] = 255;
                    greenChannel[r][c] = 255;
                    blueChannel[r][c] = 255;
                }
            }
        }
        img.setColorChannels(redChannel, greenChannel, blueChannel);
        return img;
    }

    private boolean closeEnough(int r, int c, double wantedDistance) {
        int redVal = redChannel[r][c];
        int greenVal = greenChannel[r][c];
        int blueVal = blueChannel[r][c];
        double currentDistance = getColorDistance(redVal, blueVal, greenVal);
        double delta = Math.abs(wantedDistance - currentDistance);
        return delta < tolerance;
    }

    private double getColorDistance(int red, int blue, int green) {
        return Math.sqrt(red * red + blue * blue + green * green);
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
        window.fill(255, 0, 0);
        window.ellipse(original.getWidth(), original.getHeight(), 10, 10);

        window.fill(0, 255, 0);
        window.ellipse(0, 0, 10, 10);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        targetR = redChannel[mouseY][mouseX];
        targetG = greenChannel[mouseY][mouseX];
        targetB = blueChannel[mouseY][mouseX];
    }

    @Override
    public void keyPressed(char key) {
        if (key == '+') {
            tolerance += VAL_SUBTRACT;
        } else if (key == '-' && tolerance > VAL_SUBTRACT) {
            tolerance -= VAL_SUBTRACT;
        }
    }
}

