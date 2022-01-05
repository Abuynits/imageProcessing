public class gammaIntegrationDensityTest {
    public static void main(String[] args) {
        System.out.println(getDensityGroup(2));
    }
    public static double getDensityGroup(double n){//NORMALLY2
        double radius = getRadiusCluster();
        //funciton: pi^n/2*r^n
        double volume = Math.pow(Math.PI,n/2)*Math.pow(radius,n);
        volume=volume/integrateGammaFunction(2);
        return volume/200;
    }

    private static double getRadiusCluster() {
       // Point center = new Point(getxVal(),getyVal(),getzVal());
        //the radius is the point in the cluster with the furhterst distance  to the clsuter center
        double largestDist = Double.MIN_VALUE;
//        for (Point p : pointsInGroups){
//            if(p.getDistanceTo(center)>largestDist){
//                largestDist=p.getDistanceTo(center);
//            }
//        }
        return 500;
    }

    public static double integrateGammaFunction(double input) {
        input = input/2+1;
        final double lowerBound = 0;
        final double upperBound = 10000;// simulating infinity
        final double deltaX = .01;
        double lowerBoundLoop = lowerBound+deltaX;
        double upperBoundLoop = upperBound-deltaX;
        double integralSum =0;
        double numberTimesLoop = 0;
        integralSum+=getGammaOutput(input,lowerBound);
        integralSum+=getGammaOutput(input,upperBound);
        for (double loopVal= lowerBoundLoop; loopVal<=upperBoundLoop;loopVal+=deltaX){
            integralSum+=getGammaOutput(input,loopVal);
            numberTimesLoop++;
        }
        double multBy = ((upperBound-lowerBound)/numberTimesLoop)/2;
   //     System.out.println(integralSum*multBy);
        return integralSum*multBy;
        // now have to multipy by upper-lower)/2

        //using right hand appriximation rule:

        //function is integral from 0 to infinity of e^-t *t^(z-1)

    }

    public static double getGammaOutput(double input, double value) {
        final double eulers = 2.71828;
//        System.out.println("gamma output:" + Math.pow(eulers, -value) * (Math.pow(value, input - 1)));
        return Math.pow(eulers, -value) * (Math.pow(value, input - 1));

    }
}
