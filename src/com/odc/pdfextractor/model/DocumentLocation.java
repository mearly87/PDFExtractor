package com.odc.pdfextractor.model;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DocumentLocation extends AbstractStringLocation
{

  private Set<Integer> pages = new HashSet<Integer>();

    public DocumentLocation(StringLocation loc) {
      super(loc);
      if (loc.getPage() != -1) {
       pages.add(loc.getPage()); 
      }
    }
    
    public DocumentLocation(List<? extends StringLocation> stringLocations)
    {
      super(stringLocations);
      for (StringLocation loc : stringLocations) {
        if (loc.getPage() != -1) {
          pages.add(loc.getPage());
        }
      }
    }
    
    public List<StringLocation> getLocations(int page, int lower, int upper, Location.ALIGNMENT alignment) {
      List<StringLocation> result = new ArrayList<StringLocation>();
      for (StringLocation l : getLocations()) {
        if (page != -1 && l.getPage() == page)
            result.addAll(l.getLocations(lower, upper, alignment));
      }
      return result;
    }
    
    public List<StringLocation> getLocationsInline(Location loc) {
      
      List<StringLocation> locs = new ArrayList<StringLocation>();
      for (StringLocation l : this.getLocations()) {
        if( l.getPage() == loc.getPage()) {
          locs.addAll(l.getLocationsInline(loc));
        }
      }
      return locs;
    }

    
    public StringLocation getLocation(int page, int lower, int upper, Location.ALIGNMENT alignment) {
      for (StringLocation l : getLocations()) {
        if (page != -1 && l.getPage() == page)
            return l.getLocation(lower, upper, alignment);
      }
      return null;
    }

    @Override
    public int getPage()
    {
      return -1;
    }

}
