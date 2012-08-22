package com.odc.pdfreader.xml;

import java.util.Comparator;

public class LocationXYComparator implements Comparator<Location>
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
