import processing.core.PApplet;

import javax.swing.*;
import java.util.ArrayList;

public class Monochrome implements PixelFilter, Clickable {
    // must implement pixel filter interface is see control plus click
    int val = 200;

    @Override

    public DImage processImage(DImage img) {

        short[][] pixels = img.getBWPixelGrid();
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                val =getProperThreshold(pixels);
                if (pixels[i][j] < val) {
                    pixels[i][j] = 0;
                } else {
                    pixels[i][j] = 255;
                }
            }
        }
        img.setPixels(pixels);
        return img;
    }

    private short getProperThreshold(short[][] pixels) {
        ArrayList<Double> aveVal = new ArrayList<>();
        final  int WIDTH_CALC=5;
        int imageWidth = pixels[0].length;
        int imageHeight = pixels.length;
        int widthLoopVal = imageWidth/WIDTH_CALC;
        int heightLoopVal = imageHeight/WIDTH_CALC;
        for (int i = 0; i < imageHeight; i+=heightLoopVal) {
            for (int j = 0; j < imageWidth; j+=widthLoopVal) {
                aveVal.add(calculateAverageWeight(i,j,pixels,WIDTH_CALC));
            }
        }
        double sun =0;
        for (int i = 0; i < aveVal.size(); i++) {
            sun +=aveVal.get(i);
        }
        return (short)(sun/aveVal.size());
    }

    private Double calculateAverageWeight(int i, int j, short[][] pixels, int loopOver) {
        double devideBy = loopOver*loopOver;
        double sum=0;
        for (int k = 0; k < loopOver; k++) {
            for (int l = 0; l < loopOver; l++) {
                sum+=pixels[i][j] ;
            }
        }
        return sum/devideBy;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        short[][] pixels = img.getBWPixelGrid();
        val = pixels[mouseY][mouseX];
    }

    @Override
    public void keyPressed(char key) {
        if (key == '+') {
            val += 10;
        }
        if (key == '-') {
            val -= 10;
        }
    }
}

