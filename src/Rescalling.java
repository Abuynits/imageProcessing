import processing.core.PApplet;

public class Rescalling implements PixelFilter, Clickable {
    static private  int  rescallingConstant=1;
// must implement pixel filter interface is see control plus click
    @Override
    public DImage processImage(DImage img) {
        short[][] pixelGrid = img.getBWPixelGrid();
        int scale = (int)(Math.sqrt(rescallingConstant));

        // we don't change the input image at all!
        return img;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {

    }

    @Override
    public void keyPressed(char key) {
    if(key=='+'){
        rescallingConstant++;
    }else if(key=='-'){
        rescallingConstant--;
    }
    }
}

