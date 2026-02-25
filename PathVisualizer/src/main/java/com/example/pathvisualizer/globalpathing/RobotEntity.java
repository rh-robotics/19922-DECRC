package com.example.pathvisualizer.globalpathing;

public class RobotEntity {
    double x, y, heading; // kept in the same units as are fed into SplinePath
    int activePointIndex = 0; // furthest point that's been seen
    double catchRange;

    public RobotEntity(double x, double y, double heading, double catchRange) {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.catchRange = catchRange;
    }

    public boolean inRange(double[] point) {
        return Math.sqrt(Math.pow(x - point[0], 2) + Math.pow(y - point[1], 2)) <= catchRange;
    }

    public void updatePosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void updatePosition(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    public int getActivePointIndex(){
        return activePointIndex;
    }

    public void incrementActivePointIndex(){
        activePointIndex ++;
    }

    public void setActivePointIndex(int index){
        activePointIndex = index;
    }
}
