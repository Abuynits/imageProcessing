public class Point {
        int xVal, yVal, zVal, col, row;
        //getter and setter

        public int getxVal() {
            return xVal;
        }

        public int getyVal() {
            return yVal;
        }

        public int getCol() {
            return this.col;
        }

        public int getRow() {
            return this.row;
        }

        public int getzVal() {
            return zVal;
        }

        //setter and getter
        public Point(int xVal, int yVal, int bVal, int row, int col) {
            this.xVal = xVal;
            this.yVal = yVal;
            this.zVal = bVal;
            this.col = col;
            this.row = row;
        }
    public Point(int xVal, int yVal, int bVal) {
        this.xVal = xVal;
        this.yVal = yVal;
        this.zVal = bVal;
    }
    public double getDistanceTo(Point p){
        return Math.sqrt((getxVal()-p.getxVal())*(getxVal()-p.getxVal())+
                (getyVal()-p.getyVal())*(getyVal()-p.getyVal())+
                (getzVal()-p.getzVal())*(getzVal()-p.getzVal()));
    }
}
