import processing.core.PApplet;

public class LeftHalfFilter implements PixelFilter {
/*
returns image represents only left half of original image
 */
    @Override
    public DImage processImage(DImage img) {
        short[][] inputPixels = img.getBWPixelGrid();
        short[][] outputPixels = new short[inputPixels.length][inputPixels[0].length/2];

        for (int r = 0; r < outputPixels.length; r++) {
            for (int c = 0; c < outputPixels[r].length; c++) {
                outputPixels[r][c]= inputPixels[r][c];
            }
        }
        img.setPixels(outputPixels);
        // we don't change the input image at all!
        return img;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
//
    }

}

