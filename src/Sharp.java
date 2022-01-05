import processing.core.PApplet;

public class Sharp implements PixelFilter {
    private static final int SIZE_GROUPINGS =3;// odd#'s only
    final static int THRESHHOLD=125;
    final static double N = Math.PI/2;
    final static double E = 0;
    final static double W = Math.PI;
    final static double S = 3*Math.PI/2;

    final static double NE = Math.PI/4;
    final static double  NW = 3*Math.PI/4;
    final static double  SW = 5*Math.PI/4;
    final static double  SE = 7*Math.PI/4;

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


    @Override
    public DImage processImage(DImage img) {
        short[][] pixels = img.getBWPixelGrid();
        short[][] pixelsX = img.getBWPixelGrid();
        short[][] pixelsY = img.getBWPixelGrid();
        short[][] outputPixels = img.getBWPixelGrid();  // <-- overwrite these values
        performTransform(pixels, pixelsX,sobelX);
        performTransform(pixels, pixelsY,sobelY);
        //sobel has been completed
        double[][] magnitudeArray = getMagArray(pixelsX,pixelsY);
        double[][] angleArray = getAngleArray(pixelsX,pixelsY);
        /*
        brainstorm: for each pixel, get the compass location by the closests angle value...
         then search for the pixels with teh greatest values.. set that to white (255), and black(0)
         continue loop over all pixels.
         */
        performLinearization(pixels,outputPixels,magnitudeArray,angleArray);
      //  resetPixels(outputPixels,pixelsX,pixelsY);
        img.setPixels(outputPixels);
        return img;
    }

    private void performLinearization(short[][] pixels, short[][] outputPixels, double[][] magnitudeArray, double[][] angleArray) {
        for (int i = 1; i < pixels.length-1; i++) {
            for (int j = 1; j < pixels[0].length-1; j++) {
                double currentAngle=angleArray[i][j];
                double currentMag = magnitudeArray[i][j];
                if(currentMag!=0){
                    //assume magnitude not 0 -if 0, means all pixels are the same value

                    double direction = getClosestCompassDir(currentAngle);
                    //want to get point in that compass direction...
                   getLargestInCompassDirection(currentMag,direction,magnitudeArray,i,j,outputPixels,pixels);
                }

            }
        }
    }

    private void getLargestInCompassDirection(double currentMag, double currentAngle, double[][] magnitudeArray, int row, int col,short[][]output,short[][] pixels) {
       int rowShift=0,colShift =0;
       if(currentAngle==N||currentAngle==NW||currentAngle==NE){
           colShift--;
       }
        if(currentAngle==S||currentAngle==SW||currentAngle==SE){
            colShift++;
        }
        if(currentAngle==W||currentAngle==SW||currentAngle==NW){
            rowShift++;
        }
        if(currentAngle==E||currentAngle==SE||currentAngle==NE){
            rowShift--;
        }
        if(currentMag<magnitudeArray[rowShift+row][colShift+col]){
            output[row][col]=255;
           row=rowShift+row;
           col=colShift+col;
        }else{
            output[row+rowShift][col+colShift]=255;
        }
        output[row][col]=0;
    }


    private double getClosestCompassDir(double currentAngle) {


       double wDelta, eDelta, nDelta, sDelta, nwDelta, swDelta,neDelta, seDelta;
       wDelta=Math.abs(currentAngle-W);
        sDelta=Math.abs(currentAngle-S);
        nDelta=Math.abs(currentAngle-N);
        eDelta=Math.abs(currentAngle-E);
        swDelta=Math.abs(currentAngle-SW);
        seDelta=Math.abs(currentAngle-SE);
        neDelta=Math.abs(currentAngle-NE);
        nwDelta=Math.abs(currentAngle-NW);
        double maxValue = Math.max(wDelta,nDelta);
        maxValue = Math.max(maxValue,eDelta);
        maxValue = Math.max(maxValue,swDelta);
        maxValue = Math.max(maxValue,seDelta);
        maxValue = Math.max(maxValue,neDelta);
        maxValue = Math.max(maxValue,nwDelta);
        maxValue = Math.max(maxValue,sDelta);

        if(maxValue==sDelta){
            return S;
        }else if(maxValue ==nDelta){
            return N;
        }else if(maxValue ==wDelta){
            return W;
        }
        else if(maxValue ==eDelta){
            return E;
        }else if(maxValue ==neDelta){
            return NE;
        }else if(maxValue ==nwDelta){
            return NW;
        }else if(maxValue ==seDelta){
            return SE;
        }
        return SW;

    }

    private double getNextCompassDir(double dir) {
       if(dir!=SE){
           dir+=Math.PI/4;
           return dir;
       }else{
           return 0;
       }
    }

    private double[][] getAngleArray(short[][] pixelsX, short[][] pixelsY) {
        double[][] arr = new double[pixelsX.length][pixelsX[0].length];
        for (int i = 0; i < pixelsX.length; i++) {
            for (int j = 0; j < pixelsX[0].length; j++) {
                int xVal = pixelsX[i][j];
                int yVal = pixelsY[i][j];
//                System.out.println("***********");
              //  System.out.println(xVal+ ","+ yVal);
                double angle = Math.atan(yVal/(double)xVal);
               // System.out.println(angle);
                arr[i][j]=angle;
            }
        }
        return arr;
    }

    private double[][] getMagArray(short[][] pixelsX, short[][] pixelsY) {
        double[][] arr = new double[pixelsX.length][pixelsX[0].length];
        for (int i = 0; i < pixelsX.length; i++) {
            for (int j = 0; j < pixelsX[0].length; j++) {
                int xVal = pixelsX[i][j];
                int yVal = pixelsY[i][j];
                double mag = Math.sqrt(xVal*xVal+yVal*yVal);
                arr[i][j]= mag;
            }
        }
        return arr;
    }

    private void resetPixels(short[][] pixels, short[][] pixelsX, short[][] pixelsY) {
        for (int i = 0; i < pixelsX.length; i++) {
            for (int j = 0; j < pixelsX[0].length; j++) {
                int xVal = pixelsX[i][j];
                int yVal = pixelsY[i][j];
                short magnitude = (short) (Math.sqrt(xVal * xVal + yVal * yVal));
                if (magnitude > THRESHHOLD) {
                    pixels[i][j]=0;
                }else{
                    pixels[i][j]=255;
                }
            }
        }
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
        //TODO: changed following lines
     /*   sum = Math.min((int)(sum),255);
        return Math.max((int) (sum), 0);
*/
        return (int)sum;
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
        for (int i = 0; i < newKernel.length; i++) {
            for (int j = 0; j < newKernel[0].length; j++) {
                newKernel[i][j]/= newKernel.length;
            }
        }
        return newKernel;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {

    }
}
