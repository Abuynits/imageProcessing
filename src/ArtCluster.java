import processing.core.PApplet;

import java.util.ArrayList;

public class ArtCluster implements PixelFilter, Clickable {
    // must implement pixel filter interface is see control plus click
    static int NUMBER_CLUSTERS = 10;
    final static int NUMBER_TIMES_RUN_CHECK = 10;

    boolean setOnce = true;
    short[][] red, blue, green;
    ArrayList<Point> pts = new ArrayList<>();
    ArrayList<Group> averageCluster = new ArrayList<>();
    ArrayList<Group> groups = new ArrayList<>();
    ArrayList<Point> clusterCentersCopy = new ArrayList<>();
    ArrayList<Point> centers = new ArrayList<>();

    @Override
    public DImage processImage(DImage img) {

        red = img.getRedChannel();
        blue = img.getBlueChannel();
        green = img.getGreenChannel();
        if (setOnce) {
            pts = setPointArrayList();
            groups = setClusterArrayList();

        }
        //assignment
        for (int i = 0; i < NUMBER_TIMES_RUN_CHECK; i++) {
            mainAssignmentMethod(pts, groups, clusterCentersCopy);
            //here need to add groups to arrayList
            for (Group g : groups) {
                Point p = new Point(g.getxVal(), g.getyVal(), g.getzVal());
                centers.add(p);
            }
        }
        //make clusters of centers
        if (setOnce) {
            clusterCenters();
            groups = averageCluster;
            setOnce = false;
        }

        mainAssignmentMethod(pts, groups, clusterCentersCopy);
        centers.clear();
        setColorValues(groups);
        img.setColorChannels(red, green, blue);
        return img;
    }

    private void clusterCenters() {
        averageCluster = setClusterArrayList();
        ArrayList<Point> copyCenter = new ArrayList<>();
        mainAssignmentMethod(centers, averageCluster, copyCenter);

    }

    private void mainAssignmentMethod(ArrayList<Point> pts, ArrayList<Group> groups, ArrayList<Point> clusterCentersCopy) {
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


    private void reCopyClusterCenterArray(ArrayList<Point> clusterCentersCopy, ArrayList<Group> groups) {
        clusterCentersCopy.clear();
        for (Group g : groups) {
            clusterCentersCopy.add(new Point(g.getxVal(), g.getyVal(), g.getzVal()));
        }
    }

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

    private void setColorValues(ArrayList<Group> groups) {
        for (Group g : groups) {
            for (Point p : g.getPts()) {
                int rowVal = p.getRow();
                int colVal = p.getCol();
                red[rowVal][colVal] = (short) (255-g.getxVal());
                green[rowVal][colVal] = (short) (255-g.getyVal());
                blue[rowVal][colVal] = (short) (255-g.getzVal());
            }
        }
    }

    private void recalculateCenters(ArrayList<Group> groups) {
        for (Group g : groups) {
            g.calculateNewValues();
        }
    }

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

    private ArrayList<Group> setClusterArrayList() {
        ArrayList<Group> c = new ArrayList<>();
        for (int i = 0; i < ArtCluster.NUMBER_CLUSTERS; i++) {
            c.add(new Group());
        }
        return c;
    }

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
            setOnce = true;
        } else if (key == '-' && NUMBER_CLUSTERS > 2) {
            NUMBER_CLUSTERS--;
            setOnce = true;
        }

    }
}


