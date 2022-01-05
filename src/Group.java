import java.util.ArrayList;

public class Group {

    int xVal, yVal, zVal;
    short[] colorArr = new short[3];
    ArrayList<Point> pointsInGroups = new ArrayList<>();
    boolean pointConstructorIsRun = false;
    double integralGamma = integrateGammaFunction(2);

    public Group() {
        setRandomGroup();
    }

    public Group(int r, int g, int b) {

    }

    public Group(int x, int y, int r, int g, int b) {
        xVal = x;
        yVal = y;
        zVal = 0;
        colorArr[0] = (short) r;
        colorArr[1] = (short) g;
        colorArr[2] = (short) b;

    }

    public short[] getColorArr() {
        return colorArr;
    }

    public void setColorArr(short[] colorArr) {
        this.colorArr = colorArr;
    }

    public double getDistanceTo(int r, int g, int b) {
        return Math.sqrt(((r - getxVal()) * (r - getxVal())) + ((g - getyVal()) * (g - getyVal())) + ((b - getzVal()) * (b - getzVal())));
    }

    private void setRandomGroup() {
        xVal = getRandomValue();
        yVal = getRandomValue();
        if (colorArr[0] + colorArr[1] + colorArr[2] == 0) {
            zVal = getRandomValue();
        }
        // System.out.println("r: " + rVal + ",g: " + gVal + " ,b:" + bVal);
    }

    private int getRandomValue() {
        return (int) (Math.random() * 256);
    }


    public int getxVal() {
        return xVal;
    }

    public void setxVal(int xVal) {
        this.xVal = xVal;
    }

    public int getyVal() {
        return yVal;
    }

    public void setyVal(int yVal) {
        this.yVal = yVal;
    }

    public int getzVal() {
        return zVal;
    }

    public void setzVal(int zVal) {
        this.zVal = zVal;
    }//setter and getter

    public void addNewValue(Point p) {
        pointsInGroups.add(p);
    }

    public void clearList() {
        pointsInGroups.clear();
    }

    public double getDensityGroup(double n) {//NORMALLY2
        double radius = getRadiusCluster();
        //funciton: pi^n/2*r^n
        double volume = Math.pow(Math.PI, n / 2) * Math.pow(radius, n);
        volume = volume / integralGamma;
        return volume / pointsInGroups.size();
    }

    /**
     * gets the center of the radius, which is the max distance of the value
     * @return
     */
    private double getRadiusCluster() {
        Point center = new Point(getxVal(), getyVal(), getzVal());
        //the radius is the point in the cluster with the furhterst distance  to the clsuter center
        double largestDist = Double.MIN_VALUE;
        for (Point p : pointsInGroups) {
            if (p.getDistanceTo(center) > largestDist) {
                largestDist = p.getDistanceTo(center);
            }
        }
        return largestDist;
    }

    /**
     * performs the integration of any function using the trapeziodal approximation method
     *
     * @param input the value
     * @return the integration
     */
    public double integrateGammaFunction(double input) {
        input = input / 2 + 1;
        final double lowerBound = 0;
        final double upperBound = 10000;// simulating infinity
        final double deltaX = .5;
        double lowerBoundLoop = lowerBound + deltaX;
        double upperBoundLoop = upperBound - deltaX;
        double integralSum = 0;
        double numberTimesLoop = 0;
        integralSum += getGammaOutput(input, lowerBound);
        integralSum += getGammaOutput(input, upperBound);
        for (double loopVal = lowerBoundLoop; loopVal <= upperBoundLoop; loopVal += deltaX) {
            integralSum += getGammaOutput(input, loopVal);
            numberTimesLoop++;
        }
        double multBy = ((upperBound - lowerBound) / numberTimesLoop) / 2;
        return integralSum * multBy;
        // now have to multipy by upper-lower)/2

        //using right hand appriximation rule:

        //function is integral from 0 to infinity of e^-t *t^(z-1)

    }

    /**
     * the function which gets the integration of
     * @param input in this case, this is the dimensions
     * @param value this is the radisu
     * @return the outpout of the function which is not an integration
     */
    public double getGammaOutput(double input, double value) {
        final double eulers = 2.71828;
        return Math.pow(eulers, -value) * (Math.pow(value, input - 1));

    }

    public void calculateNewValues() {
        int gSum = 0, bSum = 0, rSum = 0;
        for (Point p : pointsInGroups) {
            gSum += p.getyVal();
            rSum += p.getxVal();
            bSum += p.getzVal();
        }
        if (pointsInGroups.size() == 0) {
            setRandomGroup();
        } else {
            gSum = gSum / pointsInGroups.size();
            bSum = bSum / pointsInGroups.size();
            rSum = rSum / pointsInGroups.size();

            setyVal(gSum);
            setzVal(bSum);
            setxVal(rSum);
        }

    }

    public ArrayList<Point> getPts() {
        return pointsInGroups;
    }
}