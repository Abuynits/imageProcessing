import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;

public class twoDCluster implements PixelFilter, Clickable {
    // must implement pixel filter interface is see control plus click
    static int NUMBER_CLUSTERS = 15;
    final static int NUMBER_TIMES_RUN_CHECK = 10;
    final static int NUMBER_TIMES_CALC =1;
    short[][] red, blue, green;
    boolean initializeOnce = true;
    ArrayList<Point> pts = new ArrayList<>();
    ArrayList<Group> averageCluster = new ArrayList<>();
    ArrayList<Group> groups = new ArrayList<>();
    ArrayList<Point> clusterCentersCopy = new ArrayList<>();
    ArrayList<Point> centers = new ArrayList<>();
    double[][] sdClusterList = new double[NUMBER_CLUSTERS][NUMBER_TIMES_CALC +1];
    double[] clusterStandardDeviations= new double[NUMBER_CLUSTERS];
    short[][] points;
    static int increaseAccuracy=0;


    public DImage getImgArray(short[][] points) {
        return null;
    }

    //THREED CLSUTERING
    @Override
    public DImage processImage(DImage img) {
        points = img.getBWPixelGrid();

        red = new short[points.length][points[0].length];
        blue = new short[points.length][points[0].length];
        green = new short[points.length][points[0].length];
        if (initializeOnce) {
            pts = setPointArrayList();
            groups = setClusterArrayList();
        }

        while (!haveRightNumberOfClusters()&initializeOnce) {
            if(increaseAccuracy< NUMBER_TIMES_CALC) {
                NUMBER_CLUSTERS++;
                increaseAccuracy++;
                groups = setClusterArrayList();
                for (int i = 0; i < NUMBER_TIMES_RUN_CHECK; i++) {
                    findCusterCenters(pts, groups, clusterCentersCopy);
                    //add the cluster centers to another point list to them find clusters of the clsuter center
                    for (Group g : groups) {
                        //this treats the clsuters as points... see explanation of findCenterOfClusterCenters();
                        Point p = new Point(g.getxVal(), g.getyVal(), g.getzVal());
                        centers.add(p);
                    }

                }
            }else{
                increaseAccuracy=0;
            }
        }
       // System.out.println(NUMBER_CLUSTERS);
        //make clusters of centers
        if (initializeOnce) {
            findCenterOfClusterCenters();
            //NOTE: i am doing the following on purpose b/c I want to set pointss to the average clsuters not the calcualted once
            groups = averageCluster;
            initializeOnce = false;
        }
        //perform the same method to find the center of clusters (see findCenterOfClusterCenters(); )
        findCusterCenters(pts, groups, clusterCentersCopy);
        centers.clear();
        setColorValues(groups);
        img.setColorChannels(red, green, blue);
        return img;
    }

    private boolean haveRightNumberOfClusters() {
        //TODO:WANT TO FIND THE AVERAGE DENSITY FOR EACH CLUSTER BY RUNNING IT MULTIPLE TIMES
        if(initializeOnce) {
            if (NUMBER_CLUSTERS != 2) {
                ArrayList<Double> meanDensity = new ArrayList<>();
                double groupAverageDensity = 0;
                int numberTimesDevide = 0;
                for (Group g : groups) {
                    double val = g.getDensityGroup(2);
                    if (val > 1) {
                        //   System.out.println("\t"+ val);
                        numberTimesDevide++;
                        meanDensity.add(val);
                        groupAverageDensity += val;
                    } else {
                        meanDensity.add(0.0);
                        numberTimesDevide++;
                    }
                }
                double meanGroupDensity = groupAverageDensity / numberTimesDevide;
                if(NUMBER_CLUSTERS==2){
                    meanGroupDensity*=1.1;
                }

                double standardDeviation = calculateSd(meanDensity, meanGroupDensity);
                System.out.println("checking.... cluster num:" + NUMBER_CLUSTERS + ", standard deviation: " + standardDeviation);
                sdClusterList[NUMBER_CLUSTERS-1][increaseAccuracy] = standardDeviation;
                clusterStandardDeviations[NUMBER_CLUSTERS - 1] = standardDeviation;
                NUMBER_CLUSTERS--;
                return false;
            } else {
                NUMBER_CLUSTERS = getSmallestStandardDeviation(clusterStandardDeviations);
                System.out.println("best Number of Clusters: "+ NUMBER_CLUSTERS);
                System.out.println(Arrays.deepToString(sdClusterList));
                return true;
            }
        }
        return true;
    }

    private int getSmallestStandardDeviation(double[] clusterStandardDeviations) {
        double[] sdList = getAverageSd(sdClusterList);
        double minSD = Double.MAX_VALUE;
        int num=0;
        for (int i=0;i<sdList.length;i++){
            double d =sdList[i];
                if(d!=0){
                    if(d<minSD){
                        minSD=d;
                        num=i+1;
                    }
                }
        }
        return num;
    }

    private double[] getAverageSd(double[][] list) {
        double[] ret = new double[list.length];
        for (int i = 0; i < list.length; i++) {
            double devBy = 0;
            double ave= 0;
            for (int j = 0; j < list[0].length; j++) {
                if(list[i][j]!=0){
                    ave+=list[i][j];
                    devBy++;
                }
            }
            ret[i]=ave/devBy;
        }
        return ret;
    }

    private double calculateSd(ArrayList<Double> meanDensity, double meanGroupDensity) {
        double sum =0;
        for (Double d: meanDensity){
            sum+=(d-meanGroupDensity)*(d-meanGroupDensity);
        }
        return (sum/meanDensity.size());
    }

    /**
     * this is used to find the cluster centers of the clusters.
     * I use this to increase accuracy by treating the old clsuters as points, and finding their center
     * by finding the center of the clusters, this allows me to better pinpoint the true center, hence increasing accuracy
     * it uses the same code as for the first clustering
     */
    private void findCenterOfClusterCenters() {
        averageCluster = setClusterArrayList();
        ArrayList<Point> copyCenter = new ArrayList<>();
        findCusterCenters(centers, averageCluster, copyCenter);

    }

    /**
     * the beefy method that does the general calculation of the cluster centers
     *
     * @param pts                an arrayList of all the points possible to exist
     * @param groups             an arraylist of clusters
     * @param clusterCentersCopy the copy of the cluster array list to determine if one needs to recenter the clusters
     */
    private void findCusterCenters(ArrayList<Point> pts, ArrayList<Group> groups, ArrayList<Point> clusterCentersCopy) {
        boolean breakTest = false;
        do {
            assignPointsToClusters(pts, groups);
            recalculateCenters(groups);
            if (!detectChangeCluster(clusterCentersCopy, groups)) {
                breakTest = true;
            } else {
                reCopyClusterCenterArray(clusterCentersCopy, groups);
            }
        } while (!breakTest);
        //  } while (!breakTest || clusterCentersCopy == null);
    }

    /**
     * if the centers are not the same, I update the cluster center copy to hold the most recent cluster centers
     * this then allows me to check once the actiually cluster centers are recalcutated if the old centers match the new centers
     *
     * @param clusterCentersCopy
     * @param groups
     */
    private void reCopyClusterCenterArray(ArrayList<Point> clusterCentersCopy, ArrayList<Group> groups) {
        clusterCentersCopy.clear();
        for (Group g : groups) {
            clusterCentersCopy.add(new Point(g.getxVal(), g.getyVal(), g.getzVal()));
        }
    }

    /**
     * This checks to see if the old cluster center changed.
     * if it chagned, it means that the centers are not calibrated yet, therfore I need to repeat the process of resetting once again
     *
     * @param clusterCentersCopy the copy
     * @param groups             the current one
     * @return
     */
    private boolean detectChangeCluster(ArrayList<Point> clusterCentersCopy, ArrayList<Group> groups) {
        for (int i = 0; i < clusterCentersCopy.size(); i++) {
            int clusterRVal = groups.get(i).getxVal();
            int clusterGVal = groups.get(i).getyVal();
            int clusterBVal = groups.get(i).getzVal();
            int oldClusterBVal = clusterCentersCopy.get(i).getzVal();
            int oldClusterRVal = clusterCentersCopy.get(i).getxVal();
            int oldClusterGVal = clusterCentersCopy.get(i).getyVal();
            if (clusterBVal != oldClusterBVal || clusterGVal != oldClusterGVal && clusterRVal != oldClusterRVal) {
                return true;
            }
        }
        return false;
    }

    /**
     * assing values to the actual centers
     * this takes in the locations of the points which then find the point in
     * group to which it belongs and sets the actual group value to the point
     *
     * @param groups
     */

    private void setColorValues(ArrayList<Group> groups) {
        for (Group g : groups) {
            short[] cArr = g.getColorArr();
            for (Point p : g.getPts()) {
                int rowVal = p.getxVal();
                int colVal = p.getyVal();
                // System.out.println("setting: "+ rowVal + ", "+ colVal + " to "+ Arrays.toString(cArr));
                red[rowVal][colVal] = cArr[0];
                green[rowVal][colVal] = cArr[1];
                blue[rowVal][colVal] = cArr[2];
            }
        }
    }

    /**
     * after assigning all the points i rewcalculate the center for each fo the clusters
     * finding the average location of all the points and setting that as the center of the cluster
     *
     * @param groups
     */

    private void recalculateCenters(ArrayList<Group> groups) {
        for (Group g : groups) {
            g.calculateNewValues();
        }
    }

    /**
     * this reassings points to clusters
     * the closests cluster gets the assigned point
     *
     * @param pts    all of the points
     * @param groups all of the groups
     */
    private void assignPointsToClusters(ArrayList<Point> pts, ArrayList<Group> groups) {
        for (Group group : groups) {
            group.clearList();
        }
        for (Point p : pts) {
            double minimumDist = Double.MAX_VALUE;
            int location = 0;
            for (int loc = 0; loc < groups.size(); loc++) {
                Group g = groups.get(loc);
                double currentDist = g.getDistanceTo(p.getxVal(), p.getyVal(), p.getzVal());
                if (currentDist < minimumDist) {
                    minimumDist = currentDist;
                    location = loc;
                }
            }
            groups.get(location).addNewValue(p);

        }
    }

    /**
     * sets the arraylist of clusters
     * assings random color values that exist in the rgb space as the cluster center and creates new cluster centers
     *
     * @return
     */
    private ArrayList<Group> setClusterArrayList() {
        ArrayList<Group> c = new ArrayList<>();
        for (int i = 0; i < twoDCluster.NUMBER_CLUSTERS; i++) {
            int loc = (int) (Math.random() * pts.size());
            Point p = pts.get(loc);
            int xLoc = p.getRow();
            int yLoc = p.getCol();
            int rVal = 127;
            int gVal = (int) (Math.random() * 256);
            int bVal = (int) (Math.random() * 256);
            c.add(new Group(xLoc, yLoc, rVal, gVal, bVal));
        }
        return c;
    }

    /**
     * returns the color value at a certain location
     *
     * @param rowLoc
     * @param colLoc
     * @param colorArr the color arrray (r,g or b) color space
     * @return the actual value that it holds
     */
    private int getColorFromLocation(int rowLoc, int colLoc, short[][] colorArr) {
        return colorArr[rowLoc][colLoc];
    }

    /**
     * sets the point array
     * a point array consists of points(r,g,b) objects nad their locations in the arraylist
     *
     * @return a new array
     */
    private ArrayList<Point> setPointArrayList() {

        ArrayList<Point> pts = new ArrayList<>(points.length);

        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
               if (points[i][j] ==0) {//TODO: set to 0 bc of monochrome filter
                    pts.add(new Point(i, j, 0));
               }
            }
        }

        return pts;

    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
        //TODO: how display center?
        for (Group g : groups) {
            int xLoc = g.getxVal();
            int yLoc = g.getyVal();
            window.fill(255, 0, 0);
            window.ellipse(yLoc, xLoc, 10, 10);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {

    }

    @Override
    public void keyPressed(char key) {
        if (key == '=') {
            NUMBER_CLUSTERS++;
            initializeOnce = true;
        } else if (key == '-' && NUMBER_CLUSTERS > 2) {
            NUMBER_CLUSTERS--;
            initializeOnce = true;
        }

    }
}


