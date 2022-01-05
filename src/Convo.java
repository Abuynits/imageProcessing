import processing.core.PApplet;

import java.util.Arrays;

public class Convo implements PixelFilter {
    private static final int SIZE_GROUPINGS =3;// odd#'s only
    private double[][] blurKernel =
            {{1.0 / 9, 1.0 / 9, 1.0 / 9},
                    {1.0 / 9, 1.0 / 9, 1.0 / 9},
                    {1.0 / 9, 1.0 / 9, 1.0 / 9}};
    private double[][] sobelX =
            {
                    {-1, 0, 1},
                    {-2, 0, 2},
                    {-1, 0, 1}};
    private double[][] sobelY =
            {
                    {-1, -2,-1},
                    {0, 0, 0},
                    {1, 2, 1}};

    private double[][] outlineKernel =
            {
                    {-1, -1, -1},
                    {-1, 8, -1},
                    {-1, -1, -1}};

    private double[][] embossKernel =
            {
                    {-2, -1, 0},
                    {-1, 1, 1},
                    {0, 1, 2}};
    //TODO: note the below and the above show the logic for extending a kernel

    private double[][] test =
            {
                    {-2, -1.5, -1, -.5, 0},
                    {-1.5, -2, -1, 1, 1},
                    {-1, -1, 1, 1, 1},
                    {-.5, 0, 1, 2, 1.5},
                    {0, .5, 1, 3, 2}};

    @Override
    public DImage processImage(DImage img) {
        short[][] pixels = img.getBWPixelGrid();
        short[][] outputPixels = img.getBWPixelGrid();  // <-- overwrite these values
        performTransform(pixels, outputPixels, outlineKernel);
        img.setPixels(outputPixels);

        return img;
    }

    private void performTransform(short[][] pixels, short[][] outputPixels, double[][] kernel) {

        double[][] updatedKernel = getAdjustedKernel(kernel);
        int minVal = SIZE_GROUPINGS / 2;
        int maxRowVal = outputPixels.length - SIZE_GROUPINGS / 2;
        int maxColVal = outputPixels[0].length - SIZE_GROUPINGS / 2;

        setBorders(outputPixels, (short) 0);

        for (int i = minVal; i < maxRowVal; i++) {
            for (int j = minVal; j < maxColVal; j++) {
                int value = getAveragePixels(i, j, pixels, updatedKernel);
                outputPixels[i][j] = (short) value;
            }
        }
    }

    private int getAveragePixels(int i, int j, short[][] pixels, double[][] kernel) {
        int delta = SIZE_GROUPINGS / 2;
        int totalNumberValsSum = SIZE_GROUPINGS * SIZE_GROUPINGS;
        double sum = 0;
        for (int row = i - delta; row <= i + delta; row++) {
            for (int col = j - delta; col <= j + delta; col++) {
                int rowLocInKernel = row - i + delta;
                int colLocInKernel = col - j + delta;
                double kernelMultiplier = kernel[rowLocInKernel][colLocInKernel];
                sum += pixels[row][col] * kernelMultiplier;
            }
        }
        sum = Math.min((int)(sum),255);
        return Math.max((int) (sum), 0);

    }

    private void setBorders(short[][] outputPixels, short val) {
        for (int i = 0; i < outputPixels.length; i++) {
            for (int j = 0; j < outputPixels[0].length; j++) {
                outputPixels[i][j] = val;
            }
        }
    }

    private double[][] getAdjustedKernel(double[][] kernel) {
        if (kernel.length != SIZE_GROUPINGS) {
            return reCalculateKernel(kernel);
        }
        return kernel;
    }

    private double[][] reCalculateKernel(double[][] kernel) {
        if (kernel.length == SIZE_GROUPINGS) {
            return kernel;
        } else {
            //perfomr resizing
            double[][] newKernel = resizeOldKernel(kernel);
            return reCalculateKernel(newKernel);
        }
    }

    private static double[][] resizeOldKernel(double[][] kernel) {
        double[][] newKernel = new double[kernel.length + 2][kernel[0].length + 2];
        //the followign is to recenter the old values which are kept the same
        for (int i = 1; i < newKernel.length - 1; i++) {
            for (int j = 1; j < newKernel[0].length - 1; j++) {
                newKernel[i][j] = kernel[i - 1][j - 1];
            }
        }
        //extends the corners sw, nw, se,sw
        newKernel[0][0] = kernel[0][0];
        newKernel[newKernel.length - 1][0] = kernel[kernel.length - 1][0];
        newKernel[0][newKernel[0].length - 1] = kernel[0][kernel[0].length - 1];
        newKernel[newKernel.length - 1][newKernel[0].length - 1] = kernel[kernel.length - 1][kernel[0].length - 1];
        //extend n,w,e,s
        int halfWayLoc = newKernel.length / 2;
        int oldHalfWayLoc = kernel.length / 2;
        newKernel[0][halfWayLoc] = kernel[0][oldHalfWayLoc];
        newKernel[halfWayLoc][0] = kernel[oldHalfWayLoc][0];
        newKernel[newKernel.length - 1][halfWayLoc] = kernel[kernel.length - 1][oldHalfWayLoc];
        newKernel[halfWayLoc][newKernel[0].length - 1] = kernel[oldHalfWayLoc][kernel[0].length - 1];
        // nasty brainstorm to extend wierd values
        int delay = 1;
        for (int i = 0; i < kernel.length; i++) {
            if (i == kernel.length / 2) {
                delay -= 2;

                //skip over the middle point which is extended

            } else {
                //   System.out.println("delay: "+ delay+ ", "+ i);
                double averageValUp = (kernel[0][delay] + kernel[0][i]) / 2;
                newKernel[0][i + 1] = averageValUp;
                double averageValDown = (kernel[kernel.length - 1][delay] + kernel[kernel.length - 1][i]) / 2;
                newKernel[newKernel.length - 1][i + 1] = averageValDown;

                double averageValLeft = (kernel[delay][0] + kernel[i][0]) / 2;
                newKernel[i + 1][0] = averageValLeft;
                double averageValRight = (kernel[delay][kernel.length - 1] + kernel[i][kernel.length - 1]) / 2;
                newKernel[i + 1][newKernel.length - 1] = averageValRight;
            }
            delay++;
        }
        //TODO: naturalize by making sum kernel 1
        double sum =0;
        for (int i = 0; i < newKernel.length; i++) {
            for (int j = 0; j < newKernel[0].length; j++) {
                sum+=newKernel[i][j];
            }
        }
        double devideBy = 1/sum;
        for (int i = 0; i < newKernel.length; i++) {
            for (int j = 0; j < newKernel[0].length; j++) {
                newKernel[i][j]*=devideBy;
            }
        }
        return newKernel;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {

    }
}
