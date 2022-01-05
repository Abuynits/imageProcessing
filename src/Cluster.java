import processing.core.PApplet;

import java.util.ArrayList;

public class Cluster implements PixelFilter, Clickable {
    // must implement pixel filter interface is see control plus click
    static int NUMBER_CLUSTERS = 10;
    final static int NUMBER_TIMES_RUN_CHECK = 10;

    boolean initializeOnce = true;
    short[][] red, blue, green;
    ArrayList<Point> pts = new ArrayList<>();
    ArrayList<Group> averageCluster = new ArrayList<>();
    ArrayList<Group> groups = new ArrayList<>();
    ArrayList<Point> clusterCentersCopy = new ArrayList<>();
    ArrayList<Point> centers = new ArrayList<>();

    public DImage getImgArray(short[][] points) {
       return null;
    }
    //THREED CLSUTERING
    @Override
    public DImage processImage(DImage img) {

        red = img.getRedChannel();
        blue = img.getBlueChannel();
        green = img.getGreenChannel();
        if (initializeOnce) {
            pts = setPointArrayList();
            groups = setClusterArrayList();
        }//TODO: initialize clusters to already cluster centers - DONE
        //run x times to fill the center array of values
        for (int i = 0; i < NUMBER_TIMES_RUN_CHECK; i++) {
            findCusterCenters(pts, groups, clusterCentersCopy);
            //add the cluster centers to another point list to them find clusters of the clsuter center
            for (Group g : groups) {
                //this treats the clsuters as points... see explanation of findCenterOfClusterCenters();
                Point p = new Point(g.getxVal(), g.getyVal(), g.getzVal());
                centers.add(p);
            }
        }
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
            for (Point p : g.getPts()) {
                int rowVal = p.getRow();
                int colVal = p.getCol();
                red[rowVal][colVal] = (short) (g.getxVal());
                green[rowVal][colVal] = (short) (g.getyVal());
                blue[rowVal][colVal] = (short) (g.getzVal());
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
        for (int i = 0; i < Cluster.NUMBER_CLUSTERS; i++) {
            int rowLoc = (int) (Math.random() * red.length);
            int colLoc = (int) (Math.random() * red[0].length);
            int rVal = getColorFromLocation(rowLoc, colLoc, red);
            int gVal = getColorFromLocation(rowLoc, colLoc, green);
            int bVal = getColorFromLocation(rowLoc, colLoc, blue);
            c.add(new Group(rVal, gVal, bVal));
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
        ArrayList<Point> pts = new ArrayList<>(red.length);
        for (int i = 0; i < red.length; i++) {
            for (int j = 0; j < red[i].length; j++) {
                pts.add(new Point(red[i][j], green[i][j], blue[i][j], i, j));
            }
        }
        return pts;

    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
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


