package com.odc.pdfextractor.comparator;

import java.util.Comparator;

import com.odc.pdfextractor.Location;

public class IsAfterVerticallyComparator implements Comparator<Location>
{
  
  int ERROR = 0;
  
  public IsAfterVerticallyComparator(int error) {
    this.ERROR = error;
  }

  @Override
  public int compare(Location loc1, Location loc2)
  {
    int pageDiff = loc1.getPage() - loc2.getPage();
    // boolean isBefore = loc1.getRight() + ERROR < loc2.getLeft();
    boolean isAfter = loc1.getLeft() - ERROR > loc2.getRight();
    if (pageDiff != 0) {
      return pageDiff;
    }
    if (loc1.getLeft() == loc2.getLeft() && loc1.getRight() == loc2.getRight()) {
      return 0;
    }
    if (isAfter) {
      return 1;
    } 
    return -1;
  }
  
}
