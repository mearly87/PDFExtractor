package com.odc.pdfextractor.model;

import java.util.Collection;

import com.odc.pdfextractor.Location;

public class VerticallyCenteredStringLocation extends StringLocation
{
    final int bottomCentered;
    final int topCentered;
    public VerticallyCenteredStringLocation(StringLocation loc) {
      super(loc);
      bottomCentered = loc.getBottom();
      topCentered = loc.getTop();
    }
    
    public VerticallyCenteredStringLocation(Collection<? extends StringLocation> locations)
    {
      super(locations);
      int bottoms = 0;
      int tops = 0;
      for (Location loc : locations) {
        if (loc.size() > 0)
          bottoms+= loc.getBottom();
          tops += loc.getTop();
      }
      bottomCentered = bottoms / locations.size();
      topCentered = tops / locations.size();
    }

    @Override
    public int getBottom() {
      return bottomCentered;
    }
    
    @Override
    public int getTop() {
      return topCentered;
    }
     
}