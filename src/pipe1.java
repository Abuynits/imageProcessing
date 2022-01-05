import processing.core.PApplet;

import java.util.ArrayList;

public class pipe1 implements PixelFilter {
    ArrayList<PixelFilter> filters = new ArrayList<>();

    public pipe1() {
        Monochrome m = new Monochrome();
        filters.add(m);
        twoDCluster t = new twoDCluster();
        filters.add(t);


    }

    // must implement pixel filter interface is see control plus click
    @Override
    public DImage processImage(DImage img) {
        for (PixelFilter p : filters) {
            img = p.processImage(img);
        }
        return img;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
        for (PixelFilter f : filters){
            f.drawOverlay(window,original,filtered);
        }
    }

}

