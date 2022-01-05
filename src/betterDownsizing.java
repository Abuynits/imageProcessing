import processing.core.PApplet;

public class betterDownsizing implements PixelFilter, Clickable {
    // must implement pixel filter interface is see control plus click
    private static int rescale = 1;

    @Override
    public DImage processImage(DImage img) {
        short[][] pixelGrid = img.getBWPixelGrid();
        short[][] downsizedImg = new short[pixelGrid.length / rescale][pixelGrid[0].length / rescale];
        int rescaleRowLoc = -1, rescaleColLoc = -1;
        for (int r = 0; r < pixelGrid.length; r += rescale) {
            rescaleRowLoc++;
            for (int c = 0; c < pixelGrid[r].length; c += rescale) {
                rescaleColLoc++;
                int valFill = getAveragPixel(pixelGrid, r, c, rescale);
                fillGrid(downsizedImg, rescaleRowLoc, rescaleColLoc, valFill);

            }
            rescaleColLoc = -1;
        }
        img.setPixels(downsizedImg);
        return img;
    }

    private void fillGrid(short[][] downsizedImg, int rescaleRowLoc, int rescaleColLoc, int valFill) {
        if (rescaleRowLoc < downsizedImg.length) {
            if (rescaleColLoc < downsizedImg[rescaleRowLoc].length)
                downsizedImg[rescaleRowLoc][rescaleColLoc] = (short) valFill;
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

