package com.odc.pdfextractor.comparator;

import java.util.Comparator;

import com.odc.pdfextractor.model.Location;

public class StartsAfterVerticallyComparator implements Comparator<Location>
{
  
  int ERROR = 0;
  
  public StartsAfterVerticallyComparator(int error) {
    this.ERROR = error;
  }

  @Override
  public int compare(Location loc1, Location loc2)
  {
    int pageDiff = loc1.getPage() - loc2.getPage();
    if (pageDiff != 0) {
      return pageDiff;
    }
    return loc1.getLeft() - loc2.getLeft();
  }
  
}
