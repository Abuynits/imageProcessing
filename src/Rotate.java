import processing.core.PApplet;

import java.util.Arrays;

public class Rotate implements PixelFilter, Clickable{
// must implement pixel filter interface is see control plus click
    static double rotateAngle=0;
    static short[][] rotGrid;
    static double multVal=1;
    @Override
    public DImage processImage(DImage img) {

        /*
        [[a][b]] * [x] = [ax+by]
        [[c][d]]   [y]   [cx+dy]
        rotational matrix by anglea:
        [[cos(theta)],[-sin(theta)]] * [x]= [xcos(theta)-ysin(theta)]
        [[sin(theta)],[cos(theta)]]    [y]  [xsin(theta)+ycos(theta)]
         */
        short[][] pixelGrid = img.getBWPixelGrid();
        int translateRight = pixelGrid[0].length/2;
        int translateDown = pixelGrid.length/2;
        int maxSize = (int)Math.sqrt(pixelGrid.length*pixelGrid.length+ pixelGrid[0].length*pixelGrid[0].length);
        int allignDown = ((maxSize-pixelGrid.length))/2;
        int allignRigth = ((maxSize-pixelGrid[0].length))/2;
//        System.out.println(trasR);
//        System.out.println("translate down is "+ transD);
        short[][] transGrid = new short[maxSize][maxSize];
        translateGrid(transGrid,pixelGrid,allignDown,allignRigth);
        rotGrid= new short[transGrid.length][transGrid[0].length];
        copyGrid(transGrid);
        //EVERYTHING^^^^^^^^ ABOVE CENTERALLIGNS THE IMAGE
        for (int r = 0; r < transGrid.length; r++) {
            for (int c = 0; c < transGrid[r].length; c++) {
           //     System.out.println("-------------");
                short oldColor = transGrid[r][c];
//                System.out.println("oldLoc: "+ r+ ", "+ c);
//                System.out.println("newLoc: "+ (r-translateDown)+ ", "+ (c-translateDown));

                int[] coordinates = new int[] {r-translateRight-allignRigth,c-translateDown-allignDown};
                coordinates=performTransformation(coordinates);
            //    System.out.println("afterPerform: "+ locs[0]+ ", "+ locs[1]);
             //   System.out.println(Arrays.toString(Arrays.stream(locs).toArray()));
                int newXloc = coordinates[0]+translateRight+allignRigth;
                int newYloc = coordinates[1]+translateDown+allignDown;
              //  System.out.println("final New Cord: "+ newXloc+ ", "+ newYloc);
               // System.out.println(oldColor);
                if(newXloc>0&&newYloc>0&&newXloc<rotGrid.length&newYloc<rotGrid[0].length) {
                    rotGrid[newXloc][newYloc] = oldColor;
                }
            }
        }
        img.setPixels(rotGrid);
// 101 and 200
        return img;
    }

    private void copyGrid(short[][] transGrid) {
        for (int i = 0; i < transGrid.length; i++) {
            System.arraycopy(transGrid[i], 0, rotGrid[i], 0, transGrid[0].length);
        }
    }

    private void translateGrid(short[][] newGrid, short[][] pixelGrid, int translateDown, int translateRight) {
        for (int r = 0; r < pixelGrid.length; r++) {
            for (int c = 0; c < pixelGrid[0].length; c++) {
                int translatedRow = r+translateDown;
                int translateCol = c+translateRight;
//                System.out.println(r + "," + c);
//               System.out.println(translatedRow + "," + translateCol);
//               System.out.println(newGrid.length + ","+ newGrid[0].length);
                int val=pixelGrid[r][c];
                newGrid[translatedRow-1][translateCol-1]=(short)val;
            }
        }
    }


    private int[] performTransformation(int[] arr){
        double x= arr[0];
        double y=arr[1];
        double cosTheta= Math.cos(rotateAngle);
        double sinTheta = Math.sin(rotateAngle);
        double newX = x*cosTheta-y*sinTheta;
        double newY = x*sinTheta+y*cosTheta;
        double[] rawVals = new double[]{newX,newY};
        return getClosestsInts(rawVals);
    }

    private int[] getClosestsInts(double[] rawVals) {
        int[] ret = new int[2];
        double rawX= rawVals[0];
        double rawY= rawVals[1];
        ret[0]= getClosestDouble(rawX);
        ret[1]= getClosestDouble(rawY);
        return ret;

    }

    private int getClosestDouble(double val) {
        double smallest = (int)val;
        double largest = smallest+1;
        if(val-smallest<largest-val){
            return (int)smallest;
        }
        return  (int)largest;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
        int deg= calcDeg(rotateAngle);
        window.fill(255);
        window.textSize(8);
        window.text("degrees: "+ deg, 5,10);
        window.text("jump by: " + multVal,5,20);
    }

    private int calcDeg(double rotateAngle) {
        int deg= (int)(rotateAngle*180/(Math.PI));
        while (deg>=360){
            deg-=360;
        }
        while(deg<0){
            deg+=360;
        }
        return deg;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {

    }

    @Override
    public void keyPressed(char key) {

        if(key=='='){
        //    System.out.println("click Reg");
            rotateAngle+=multVal*Math.PI/360;
        }else if(key=='-'){
            rotateAngle-=multVal*Math.PI/360;
        }
        if(key=='a'){
            multVal*=2;
        }else if(key=='q'){
            multVal/=2;
        }
    }
}

