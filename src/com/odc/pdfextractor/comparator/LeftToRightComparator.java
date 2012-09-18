package com.odc.pdfextractor.comparator;

import java.util.Comparator;

import com.odc.pdfextractor.model.Location;

public class LeftToRightComparator implements Comparator<Location>
{

  @Override
  public int compare(Location loc1, Location loc2)
  {

    int left = loc1.getLeft() - loc2.getLeft();
    int top = loc1.getTop() - loc2.getTop();
    if (left != 0) {
      return left;
    }
    if (top != 0) {
      return top;
    }
    return 0;
  }

}
