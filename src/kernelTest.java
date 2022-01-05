import java.util.Arrays;

public class kernelTest {
    private static double[][] embossKernel =
            {
                    {-2, -1, 0},
                    {-1, 1, 1},
                    {0, 1, 2}};
    //TODO: note the below and the above show the logic for extending a kernel

    private static double[][] test =
            {
                    {-2, -1.5, -1, -.5, 0},
                    {-1.5, -2, -1, 1, 1},
                    {-1, -1, 1, 1, 1},
                    {-.5, 0, 1, 2, 1.5},
                    {0, .5, 1, 3, 2}};
    public static void main(String[] args) {
        double[][] newK=test;
        for (int i = 0; i < newK.length; i++) {
            System.out.println(Arrays.toString(newK[i]));
        }
            newK =resizeOldKernel(test);
        for (int i = 0; i < newK.length; i++) {
            System.out.println(Arrays.toString(newK[i]));
        }

    }
    private static double[][] resizeOldKernel(double[][] kernel) {
        double[][] newKernel = new double[kernel.length+2][kernel[0].length+2];
        //the followign is to recenter the old values which are kept the same
        for (int i = 1; i < newKernel.length-1; i++) {
            for (int j = 1; j < newKernel[0].length-1; j++) {
                newKernel[i][j]=kernel[i-1][j-1];
            }
        }
        //extends the corners sw, nw, se,sw
        newKernel[0][0]=kernel[0][0];
        newKernel[newKernel.length-1][0]=kernel[kernel.length-1][0];
        newKernel[0][newKernel[0].length-1]=kernel[0][kernel[0].length-1];
        newKernel[newKernel.length-1][newKernel[0].length-1]=kernel[kernel.length-1][kernel[0].length-1];
        //extend n,w,e,s
        int halfWayLoc = newKernel.length/2;
        int oldHalfWayLoc = kernel.length/2;
        newKernel[0][halfWayLoc]=kernel[0][oldHalfWayLoc];
        newKernel[halfWayLoc][0]=kernel[oldHalfWayLoc][0];
        newKernel[newKernel.length-1][halfWayLoc]=kernel[kernel.length-1][oldHalfWayLoc];
        newKernel[halfWayLoc][newKernel[0].length-1]=kernel[oldHalfWayLoc][kernel[0].length-1];
        // nasty brainstorm to extend wierd values
        int delay=1;
        for (int i = 0; i <kernel.length; i++) {
            if(i==kernel.length/2){
                delay-=2;

                //skip over the middle point which is extended

            }else {
                //System.out.println("delay: "+ delay+ ", "+ i);
                double averageValUp = (kernel[0][delay] + kernel[0][i]) / 2;
                newKernel[0][i + 1] = averageValUp;
                double averageValDown = (kernel[kernel.length-1][delay] + kernel[kernel.length-1][i]) / 2;
                newKernel[newKernel.length-1][i + 1] = averageValDown;

                double averageValLeft = (kernel[delay][0] + kernel[i][0]) / 2;
                newKernel[i+1][0] = averageValLeft;
                double averageValRight = (kernel[delay][kernel.length-1] + kernel[i][kernel.length-1]) / 2;
                newKernel[i+1][newKernel.length-1] = averageValRight;
            }
            delay++;
        }
        return newKernel;
    }
}
