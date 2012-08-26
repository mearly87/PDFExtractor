package com.odc.pdfextractor.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.odc.pdfextractor.Location;

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
      for (Location loc : stringLocations) {
        if (loc.getPage() != -1) {
          pages.add(loc.getPage());
        }
      }
    }
    
    public List<StringLocation> applyRegEx(String regEx) {
      Pattern date2 = Pattern.compile(regEx);
      Matcher matcher = date2.matcher(this.toString());
      List<StringLocation> locs = new ArrayList<StringLocation>();
      while(matcher.find()) {
        StringLocation loc = substring(matcher.start(), matcher.end());
        if (loc != null) {
          locs.add(loc);
        }
      }
      return locs;
    }
    
    public List<StringLocation> getLocations(int page, int lower, int upper, Location.ALIGNMENT alignment) {
      List<StringLocation> result = new ArrayList<StringLocation>();
      for (StringLocation l : getLocations()) {
        if (page != -1 && l.getPage() == page)
            result.addAll(l.getLocations(lower, upper, alignment));
      }
      return result;
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
