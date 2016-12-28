package com.ekb.time;

import org.joda.time.DateTime;

/**
 * Created by EdwinBrown on 4/14/2016.
 */
public class MyTime {
    DateTime myTime;
    String myLabel;

    public MyTime() {

    }

    public DateTime getMyTime() {
        return myTime;
    }

    public void setMyTime(DateTime myTime) {
        this.myTime = myTime;
    }

    public String getMyLabel() {
        return myLabel;
    }

    public void setMyLabel(String myLabel) {
        this.myLabel = myLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyTime myTime1 = (MyTime) o;

        if (myTime != null ? !myTime.equals(myTime1.myTime) : myTime1.myTime != null) return false;
        return myLabel != null ? myLabel.equals(myTime1.myLabel) : myTime1.myLabel == null;

    }

    @Override
    public int hashCode() {
        int result = myTime != null ? myTime.hashCode() : 0;
        result = 31 * result + (myLabel != null ? myLabel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MyTime{" +
                "myTime=" + myTime +
                ", myLabel='" + myLabel + '\'' +
                '}';
    }
}
