package com.odc.pdfextractor.comparator;

import java.util.Comparator;

import com.odc.pdfextractor.Location;

public class VerticalIntersectionComparator implements Comparator<Location>
{
  
  int ERROR = 0;
  
  public VerticalIntersectionComparator(int error) {
    this.ERROR = error;
  }

  @Override
  public int compare(Location loc1, Location loc2)
  {
    int pageDiff = loc1.getPage() - loc2.getPage();
    boolean isBefore = loc1.getRight() + ERROR < loc2.getLeft();
    boolean isAfter = loc1.getLeft() - ERROR > loc2.getRight();
    if (pageDiff != 0) {
      return pageDiff;
    }
    if (isBefore) {
      return -1;
    } if (isAfter) {
      return 1;
    } 
    return 0;
  }
  
}
