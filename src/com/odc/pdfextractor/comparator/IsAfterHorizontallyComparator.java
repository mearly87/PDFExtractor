package com.odc.pdfextractor.comparator;

import java.util.Comparator;

import com.odc.pdfextractor.Location;

public class IsAfterHorizontallyComparator implements Comparator<Location>
{
  
  int ERROR = 0;
  
  public IsAfterHorizontallyComparator(int error) {
    this.ERROR = error;
  }

  @Override
  public int compare(Location loc1, Location loc2)
  {
    int pageDiff = loc1.getPage() - loc2.getPage();
    boolean isAfter = loc1.getTop() - ERROR > loc2.getBottom();
    if (pageDiff != 0) {
      return pageDiff;
    }
    if (isAfter) {
      return 1;
    } 
    return  loc1.getTop() - ERROR - loc2.getBottom();
  }
  
}
