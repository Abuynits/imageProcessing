import processing.core.PApplet;

public class AddBorders implements PixelFilter, Clickable {
    private static int size = 0;
    private static int borderColor = 0;

    // must implement pixel filter interface is see control plus click
    @Override
    public DImage processImage(DImage img) {
        // we don't change the input image at all!
        return img;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
        window.fill(borderColor);
        window.stroke(borderColor);
        int maxWidth = original.getWidth(), maxHeight = original.getHeight();
        window.rect(0, 0, maxWidth, size);
        window.rect(0, maxHeight - size, maxWidth, size);

        window.rect(0, 0, size, maxHeight);
        window.rect(maxWidth - size, 0, size, maxHeight);
        window.ellipse(original.getWidth(), original.getHeight(), 10, 10);

        window.fill(0, 255, 0);
        window.ellipse(0, 0, 10, 10);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        short[][] pixels = img.getBWPixelGrid();
        borderColor = pixels[mouseY][mouseX];
    }

    @Override
    public void keyPressed(char key) {
        if (key == '+') {
            size += 10;
        }
        if (key == '-') {
            size -= 10;
        }
    }
}

