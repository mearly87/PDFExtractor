package com.odc.pdfextractor.comparator;

import java.util.Comparator;

import com.odc.pdfextractor.Location;

public class HorizontalIntersectionComparator implements Comparator<Location>
{
  
  int ERROR = 0;
  
  public HorizontalIntersectionComparator(int error) {
    this.ERROR = error;
  }

  @Override
  public int compare(Location loc1, Location loc2)
  {
    int pageDiff = loc1.getPage() - loc2.getPage();
    boolean isBefore = loc1.getBottom() + ERROR < loc2.getTop();
    boolean isAfter = loc1.getTop() - ERROR > loc2.getBottom();
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
