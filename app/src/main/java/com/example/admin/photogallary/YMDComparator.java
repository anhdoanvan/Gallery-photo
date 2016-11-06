package com.example.admin.photogallary;

import java.util.Comparator;

/**
 * Created by admin on 11/4/16.
 */

public class YMDComparator  implements Comparator<GridItem> {

    @Override
    public int compare(GridItem o1, GridItem o2) {
        return o2.getTime().compareTo(o1.getTime());
    }

}