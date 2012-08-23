package com.odc.pdfextractor.comparator;

import java.util.Comparator;

import com.odc.pdfextractor.Location;

public class TopToBottomComparator implements Comparator<Location>
{

  @Override
  public int compare(Location loc1, Location loc2)
  {

    int left = loc1.getLeft() - loc2.getLeft();
    int top = loc1.getTop() - loc2.getTop();
    if (top != 0) {
      return top;
    }
    if (left != 0) {
      return left;
    }
    return 0;
  }

}
