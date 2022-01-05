import processing.core.PApplet;

public class Break implements PixelFilter, Clickable {
    static private short[][] redChannel, greenChannel, blueChannel;
    static private short[][] oldRedChannel, oldGreenChannel, oldBlueChannel;
    static int halfQuads = 9;
    static int newGridWidth, newGridHeigth;

    static boolean stop = false;

    // must implement pixel filter interface is see control plus click
    @Override
    public DImage processImage(DImage img) {
        newGridWidth = (img.getWidth() / halfQuads);
        newGridHeigth = (img.getHeight() / halfQuads);
        redChannel = img.getRedChannel();
        greenChannel = img.getGreenChannel();
        blueChannel = img.getBlueChannel();
        for (int i = 0; i < halfQuads; i++) {
            //heigth change
            for (int j = 0; j < halfQuads; j++) {
                //loop over quadrants and choose val to exchange with.
                int exchangeCol = (int) (Math.random() * halfQuads);
                int exchangeRow = (int) (Math.random() * halfQuads);
                exchangePixels(i, j, exchangeCol, exchangeRow);
            }
        }
        if (!stop) {
            img.setColorChannels(redChannel, greenChannel, blueChannel);
        } else {
            img.setColorChannels(oldRedChannel, oldGreenChannel, oldBlueChannel);
        }
        return img;
    }

    private void exchangePixels(int r, int c, int exchangeCol, int exchangeRow) {
        int minCurrentGridHeight = c * newGridHeigth;
        int maxCurrentGridHeight = minCurrentGridHeight + newGridHeigth;
        int minCurrentGridWidth = r * newGridWidth;
        int maxCurrentGridWidth = minCurrentGridWidth + newGridWidth;

        int minExchangeGridWidth = exchangeRow * newGridWidth;

        for (int row = minCurrentGridWidth; row < maxCurrentGridWidth; row++) {
            int minExchangeGridHeight = exchangeCol * newGridHeigth;
            for (int col = minCurrentGridHeight; col < maxCurrentGridHeight; col++) {
                peformSwapChannelPixels(redChannel, row, col, minExchangeGridWidth, minExchangeGridHeight);
                peformSwapChannelPixels(greenChannel, row, col, minExchangeGridWidth, minExchangeGridHeight);
                peformSwapChannelPixels(blueChannel, row, col, minExchangeGridWidth, minExchangeGridHeight);

                minExchangeGridHeight++;
            }
            minExchangeGridWidth++;
        }

    }

    private void peformSwapChannelPixels(short[][] channel, int row, int col, int exchangeRow, int exchangeCol) {
        short holdValue = channel[col][row];
        channel[col][row] = channel[exchangeCol][exchangeRow];
        channel[exchangeCol][exchangeRow] = holdValue;
    }


    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {

    }

    @Override
    public void keyPressed(char key) {
        if (key == 'a') {
            stop = false;
        } else if (key == 'q') {
            oldBlueChannel = blueChannel;
            oldGreenChannel = greenChannel;
            oldRedChannel = redChannel;
            stop = true;
        }
        if(key=='='){
            halfQuads+=1;
        }else if(key=='-'&&halfQuads>1){
            halfQuads-=1;
        }
    }
}

