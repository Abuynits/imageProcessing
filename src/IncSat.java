import processing.core.PApplet;

import java.util.Arrays;

public class IncSat implements PixelFilter, Clickable {
    static final int VAL_SUBTRACT = 10;
    static private int increaseSat = 50;
    static private short[][] redChannel, greenChannel, blueChannel, hChannel, sChannel, vChannel;

    // must implement pixel filter interface is see control plus click
    @Override
    public DImage processImage(DImage img) {

        redChannel = img.getRedChannel();
        greenChannel = img.getGreenChannel();
        blueChannel = img.getBlueChannel();
        hChannel = redChannel;
        sChannel = redChannel;
        vChannel = redChannel;
        for (int r = 0; r < redChannel.length; r++) {
            for (int c = 0; c < redChannel[r].length; c++) {
                short R = (short) (redChannel[r][c] + 1);
                short G = (short) (greenChannel[r][c] + 1);
                short B = (short) (blueChannel[r][c] + 1);
                short[] hsvVals = transformRGB(R, G, B);
              //  hsvVals[2] = addValue(hsvVals[2]);
                System.out.println("---=-=-=-=");
                System.out.println(R + "," + G + "," + B);
                System.out.println(" HSV vals:");
                System.out.println(Arrays.toString(hsvVals));
                short[] increasedSatRBG = transformHSV(hsvVals);
                System.out.println("final vals");
                System.out.println(Arrays.toString(increasedSatRBG));

                hChannel[r][c] = hsvVals[0];
                sChannel[r][c] = hsvVals[1];
                vChannel[r][c] = hsvVals[2];

            }
        }
       // img.setColorChannels(hChannel, sChannel, vChannel);
        img.setColorChannels(redChannel, greenChannel, blueChannel);
        return img;
    }

    private short[] transformHSV(short[] hsvVals) {
        double h =(hsvVals[0]/100.0);
       double s = (hsvVals[1]/100.0);
        double  v =  (hsvVals[2]/100.0);
        double chroma =  (s * v);
        double[] partialRGBArray = getPartailArrayWithH(h, chroma);
       double delta =  (v-chroma);
        short newR = (short) ((partialRGBArray[0]+delta)*255);
        short newG = (short) ((partialRGBArray[1]+delta)*255);
        short newB = (short) ((partialRGBArray[2]+delta)*255);

        return new short[]{newR,newG,newB};
    }

    private double[] getPartailArrayWithH(double h, double chroma) {
        double sectorH = h / (double) 60;


        short calc = (short) (chroma * (1 - Math.abs(Math.floorMod((int) sectorH, 2) - 1)));
        if (0 <= sectorH && sectorH < 1) {
            return new double[]{chroma, calc, 0};
        } else if (1 <= sectorH && sectorH < 2) {
            return new double[]{calc, chroma, 0};
        } else if (2 <= sectorH && sectorH < 3) {
            return new double[]{0, chroma, calc};
        } else if (3 <= sectorH && sectorH < 4) {
            return new double[]{0, calc, chroma};
        } else if (4 <= sectorH && sectorH < 5) {
            return new double[]{calc, 0, chroma};
        } else if (5 <= sectorH && sectorH < 6) {
            return new double[]{chroma, 0, calc};
        }//h=360
        return new double[]{0, 0, 0};

    }

    private short addValue(short hsvVal) {
        if (hsvVal + increaseSat > 255) {
            return 255;
        }
        return (short) (hsvVal + increaseSat);
    }

    private short[] transformRGB(short r, short g, short b) {
        double h, s, v;
        double percentR = r / 255.0;
        double percentG = g / 255.0;
        double percentB = b / 255.5;

        double cMax = Math.max(percentR, Math.max(percentG, percentB));
        double cMin = Math.min(percentR, Math.min(percentG, percentB));

        double deltaC = cMax - cMin;
        if (deltaC == 0) {
            h = 0;
        } else {
            h = getHValue(cMax, r, g, b, deltaC);
        }
        v = cMax * 100;

        if (cMax == 0) {
            s = 0;
        } else {
            s = (deltaC / cMax) * 100;
        }

        return new short[]{(short) h, (short) s, (short) v};
    }

    private double getHValue(double cMax, short r, short g, short b, double deltaC) {

        if (cMax == r) {
            return (60 * ((g - b) / deltaC) + 360) % 360;
        } else if (cMax == g) {
            return (60 * ((b - r) / deltaC) + 120) % 360;
        }
        return (60 * ((r - g) / deltaC) + 240) % 360;

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
            increaseSat += VAL_SUBTRACT;
        } else if (key == '-' && increaseSat > VAL_SUBTRACT) {
            increaseSat -= VAL_SUBTRACT;
        }
    }
}

