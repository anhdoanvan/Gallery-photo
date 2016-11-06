package com.example.admin.photogallary;

/**
 * Created by admin on 11/3/16.
 */

public class GridItem {
    /**
     * Picture path
     */
    private String path;
    /**
     * Image is added to the mobile phone in time, take only date
     */
    private String time;
    /**
     * Each Item corresponds to the HeaderId
     */
    private int headerId;

    public GridItem(String path, String time) {
        super();
        this.path = path;
        this.time = time;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public int getHeaderId() {
        return headerId;
    }

    public void setHeaderId(int headerId) {
        this.headerId = headerId;
    }
}
