import processing.core.PApplet;

import java.util.Arrays;

public class colorNoise implements PixelFilter, Clickable {

    // must implement pixel filter interface is see control plus click
    static int tolerance = 50;
    static int greenToggle = 0, redToggle = 0, blueToggle = 0;
    static int oldGreenToggle = 0, oldRedToggle = 0, oldBlueToggle = 0;
    short[][] redChannel, greenChannel, blueChannel;
    short[][] randomRedChannel, randomGreenChannel, randomBlueChannel;
    boolean greenChange, redChange, blueChange;

    @Override
    public DImage processImage(DImage img) {
        redChannel = img.getRedChannel();
        greenChannel = img.getGreenChannel();
        blueChannel = img.getBlueChannel();

        randomGreenChannel = checkIfAddNoise(greenToggle, greenChannel, greenChange, randomGreenChannel);
        randomRedChannel = checkIfAddNoise(redToggle, redChannel, redChange, randomRedChannel);
        randomBlueChannel = checkIfAddNoise(blueToggle, blueChannel, blueChange, randomBlueChannel);
        img.setColorChannels(randomRedChannel, randomGreenChannel, randomBlueChannel);
        return img;
    }

    private short[][] checkIfAddNoise(int toggle, short[][] channel, boolean change, short[][] unchangedRandomChannel) {
        if (toggle % 2 == 0) {
            return channel;
        } else {
            if (change) {
                return randomizeChannel(channel);
            }
            return unchangedRandomChannel;
        }
    }

    private short[][] randomizeChannel(short[][] channel) {

        short[][] newChannel = new short[channel.length][channel[0].length];
        for (int r = 0; r < channel.length; r++) {
            for (int c = 0; c < channel[r].length; c++) {
                int minValue = channel[r][c] - tolerance;
                int actualAddition = minValue + (int) (Math.random() * tolerance * 2 + 1);
                newChannel[r][c] = (short) actualAddition;
            }
        }
        redChange = false;
        greenChange = false;
        blueChange = false;
        return newChannel;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {

    }

    @Override
    public void keyPressed(char key) {
        if (key == '=') {
            tolerance++;
        } else if (key == '-' & tolerance > 0) {
            tolerance--;
        }
        if (key == 'g') {
            greenChange = true;
            greenToggle++;
            System.out.println("green added: " + greenToggle + "old toggle " + oldGreenToggle);

        } else if (key == 'b') {
            blueChange = true;
            blueToggle++;
            System.out.println("blue added: " + blueToggle);

        } else if (key == 'r') {
            redChange = true;
            redToggle++;
            System.out.println("red added: " + redToggle);

        }
    }
}

