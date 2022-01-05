public class amc12prob {
    public static void main(String[] args) {
        int searchValue;
        int maxVal=2019;
        int count=0;
        int maxLoc=0, value=0;
        for (int k = 1; k <= maxVal; k++) {
            searchValue = k;
            for (int i = 1; i <= maxVal; i++) {
                for (int j = 1; j < maxVal; j++) {
                    if (i + j == 2020) {
                        if(i!=searchValue&&j!=searchValue)
                        count++;
                    }
                }
            }
            if(count>maxLoc){
                maxLoc=count;
                value=searchValue;
            }
            count=0;


        }
        System.out.println(value);
    }
}
