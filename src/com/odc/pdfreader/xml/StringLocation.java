package com.odc.pdfreader.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.odc.pdfreader.xml.Location.ALIGNMENT;

public class StringLocation implements Location
{
  private int right = Integer.MIN_VALUE;
  private int left = Integer.MAX_VALUE;
  private int bottom = Integer.MIN_VALUE;
  private int top = Integer.MAX_VALUE;
  private int page = -1;
  private List<Location> locations = new ArrayList<Location>();
  private int size = 0;

    public StringLocation(Location loc) {
      this.addLocation(loc);
    }
  
    public StringLocation()  { }
    
    public String toString() {
      StringBuilder word = new StringBuilder();
      for (Location loc : locations) {
        word.append(loc.toString());
      }
      return word.toString() + " ";
    }
    
    public String fullPrint() {
      StringBuilder word = new StringBuilder();
      for (Location loc : locations) {
        word.append(loc.fullPrint());
      }
      return "{" + word.toString() + "(page: " + page + ", left: " + left + ", right: " + right + ", top: " + top + ", bottom: " + bottom + ")}\n";
    }
    
    public int getRight()
    {
      return right;
    }

    public int getLeft()
    {
      return left;
    }

    public int getBottom()
    {
      return bottom;
    }
    
    public int getTop()
    {
      return top;
    }
  
    public void sort() {
      for (Location l : locations) {
        l.sort();
      }
      Collections.sort(locations, new LocationXYComparator());
    }
    
    
    public void addLocation(Location loc) {
      if (loc == null) {
        return;
      }
      expandByLocation(loc);
      locations.add(loc);
    }
    
    public List<StringLocation> getLineLocations() {
      List<StringLocation> result = new ArrayList<StringLocation>();
      StringLocation looseChars = null;
      for (Location l : locations) {
        if (l instanceof StringLocation) {
          if (looseChars != null) {
            result.add(looseChars);
          }
          result.add((StringLocation) l);
        } else {
          looseChars.addLocation(l);
        }
      }
      if (looseChars != null) {
        result.add(looseChars);
      }
      return Collections.unmodifiableList(result);
    }
    
    public int size() {
      return size + 1;
    }

    public int getPosition(Location.ALIGNMENT alignment) throws RuntimeException {
      switch (alignment) {
      case left:
        return getLeft();
      case right:
        return getRight();
      case top:
        return getTop();
      case bottom:
        return getBottom();
      case horizontalCenter:
        return (getLeft() + getRight()) / 2;
      case verticalCenter:
        return (getTop() + getBottom()) / 2;
      }
      throw new RuntimeException("Invalid alignment: " + alignment);
    }

    @Override
    public int getPage()
    {
      return page;
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
    
    @Override
    public StringLocation substring(int start, int end) {
      StringLocation result = new StringLocation();
      int charPointer = 0;
      for (Location loc : locations) { 
        if (start >= end) {
          return result;
        }
        if (start <= charPointer && charPointer < end || 
            start < charPointer + loc.size() && charPointer + loc.size() < end || 
            start > charPointer && charPointer + loc.size() >= end) {
          int endIndex = Math.min(loc.size(), end - charPointer);
          int startIndex = start - charPointer;

          Location newLoc = loc.substring(startIndex, endIndex);
          result.addLocation(newLoc);
          start = start + newLoc.size();
        } 
        charPointer = charPointer + loc.size();
      }
      
      return result;
    }

    public void addLocations(List<Location> locations)
    {
      for (Location l : locations) {
        addLocation(l);
      }
      
    }
    
    public List<StringLocation> getLocations(int lower, int upper, Location.ALIGNMENT alignment) {
      List<StringLocation> result = new ArrayList<StringLocation>();
      for (StringLocation l : getLineLocations()) {
        if( lower < l.getPosition(alignment) && upper > l.getPosition(alignment)) {
            result.add(l);
        }
      }
      return result;
    }
    
    public StringLocation getLocation(int lower, int upper, Location.ALIGNMENT alignment) {
      StringLocation result = new StringLocation();
      for (Location l : locations) {
        if( lower < l.getPosition(alignment) && upper > l.getPosition(alignment)) {
          result.addLocation(l);
        }
      }
      return result;
    }

    @Override
    public boolean hasPoint(int x, int y)
    {
      return left < x && x < right && bottom < y && y < top;
    }

    // TODO: What if there headers are in two lines with duplication of words on one of the lines?
    public StringLocation getHeaders(int lower, int upper, ALIGNMENT alignment, Location loc)
    {
      StringLocation result = new StringLocation();
      for (Location l : locations) {
        if( lower < l.getPosition(alignment) && upper > l.getPosition(alignment)) {

          // If this header is aligned inside of result, 
          // it is underneath one of the headers and should be included
          if (result.getLeft() <= loc.getLeft() && result.getRight() >= loc.getRight()) {
            List<StringLocation> headers = result.getLineLocations();
            boolean done = false;
            for (StringLocation header : headers) {
                if (l.getLeft() <= header.getLeft() && l.getRight() >= header.getRight()) {
                  header.addLocation(l);
                  result.expandByLocation(l);
                  done = true;
                  continue;
                }

            }
            if(done) {
              continue;
            }
          }
          
          // If result contains this header already, it is a table with horizontal duplication
          // Either return this set of headers, or start a new set depending on if loc is in this set
          if (((StringLocation) result).containsString(l.toString().trim())) {
            if (result.getLeft() <= loc.getLeft() && result.getRight() >= loc.getRight()) {
              return result;
            } else {
              result = new StringLocation();
            }
          }

          result.addLocation(l);
        }
      }
      return result;
    }

    public boolean containsString(String substring)
    {
      return this.toString().contains(substring);
    }

    public void expandByLocation(Location loc)
    {
      if (page == -1) {
        page = loc.getPage();
      }
      if (page != loc.getPage()) {
        // throw new RuntimeException("Cannot create Location box that spans two pages");
      }
      if (loc.getLeft() < left) {
        left = loc.getLeft();
      }
      if (loc.getRight() > right) {
        right = loc.getRight();
      }
      if (loc.getTop() < top) {
        top = loc.getTop();
      }
      if (loc.getBottom() > bottom) {
        bottom = loc.getBottom();
      }
      size = size + loc.size();
    }

    public StringLocation getLocationUnder(Location header)
    {
      StringLocation result = new StringLocation();
      for (Location l : locations) {
        if (l.getLeft() <= header.getLeft() && l.getRight() > header.getLeft()) {
          result.addLocation(l);
          continue;
        } else if (l.getLeft() >= header.getLeft() && l.getLeft() < header.getRight() ) {
          result.addLocation(l);
          continue;
        }
      }
      return result;
    }
   
}