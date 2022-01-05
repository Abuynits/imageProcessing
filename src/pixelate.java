import processing.core.PApplet;

import javax.swing.*;

public class pixelate implements PixelFilter, Clickable {
    // must implement pixel filter interface is see control plus click
    private static int rescale = 1;

    @Override
    public DImage processImage(DImage img) {
        short[][] pixelGrid = img.getBWPixelGrid();
        for (int r = 0; r < pixelGrid.length; r += rescale) {
            for (int c = 0; c < pixelGrid[r].length; c += rescale) {
                int valFill = getAveragPixel(pixelGrid, r, c, rescale);
                fillGrid(pixelGrid, valFill, r, c, rescale);
            }
        }
        img.setPixels(pixelGrid);
        return img;
    }

    private void fillGrid(short[][] pixelGrid, int valFill, int r, int c, int rescale) {
        for (int row = r; row < r+rescale; row++) {
            for (int col = c; col < c+rescale; col++) {
                if(row<pixelGrid.length&&col<pixelGrid[r].length) {
                    pixelGrid[row][col] = (short) valFill;
                }
            }
        }
    }

    private int getAveragPixel(short[][] pixelGrid, int r, int c, int rescale) {
        int total = 0;
        for (int i = 0; i < rescale; i++) {
            for (int j = 0; j < rescale; j++) {
                total += pixelGrid[r][c];
            }
        }
        return total / (rescale * rescale);
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {

    }

    @Override
    public void keyPressed(char key) {
        if (key == '+') {
            rescale++;
        } else if (key == '-') {
            if (rescale > 1) {
                rescale--;
            }
        }
    }
}

