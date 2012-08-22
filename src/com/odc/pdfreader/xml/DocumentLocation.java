package com.odc.pdfreader.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.odc.pdfreader.xml.Location.ALIGNMENT;

public class DocumentLocation implements Location
{

  private int right = Integer.MIN_VALUE;
  private int left = Integer.MAX_VALUE;
  private int bottom = Integer.MIN_VALUE;
  private int top = Integer.MAX_VALUE;
  private List<Integer> pages = new ArrayList();;
  private List<StringLocation> locations = new ArrayList<StringLocation>();
  private int size = 0;
  private boolean addSpace = false;

    public DocumentLocation(StringLocation loc) {
      this.addLocation(loc);
    }
  
    public DocumentLocation()
    {
      // TODO Auto-generated constructor stub
    }
    

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
      return word.toString() + "(page: " + pages.get(0) + "-" + pages.get(pages.size() - 1) + ", left: " + left + ", right: " + right + ", top: " + top + ", bottom: " + bottom + ")\n";
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
    
    
    public void addLocation(StringLocation loc) {
      if (loc == null) {
        return;
      }
      if (!pages.contains(loc.getPage())) {
        pages.add(loc.getPage());
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
      locations.add(loc);
    }
    
    public List<StringLocation> getLocations() {
      return Collections.unmodifiableList(locations);
    }
    
    public int size() {
      return size + 1;
    }

    public int getPosition(Location.ALIGNMENT alignment) throws RuntimeException {
      switch (alignment) {
      case left:
        return left;
      case right:
        return right;
      case top:
        return top;
      case bottom:
        return bottom;
      case horizontalCenter:
        return (left + right) / 2;
      case verticalCenter:
        return (top + bottom) / 2;
      }
      throw new RuntimeException("Invalid alignment: " + alignment);
        
    }

    public int getPage()
    {
      return pages.size();
    }
    
    public List<StringLocation> applyRegEx(String regEx) {
      Pattern date2 = Pattern.compile(regEx);
      Matcher matcher = date2.matcher(this.toString());
      List<StringLocation> locs = new ArrayList<StringLocation>();
      // System.out.println(this.toString());
      while(matcher.find()) {
        StringLocation loc = substring(matcher.start(), matcher.end());
        // System.out.println(this.toString().substring(matcher.start(), matcher.end()) + " : " + loc.toString());
        if (loc != null) {
          locs.add(loc);
        }
      }
      return locs;
    }
    
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

    public void addLocations(List<StringLocation> locations)
    {
      for (StringLocation l : locations) {
        addLocation(l);
      }
      
    }
    
    public List<StringLocation> getLocations(int page, int lower, int upper, Location.ALIGNMENT alignment) {
      List<StringLocation> result = new ArrayList<StringLocation>();
      for (StringLocation l : locations) {
        if (page != -1 && l.getPage() == page)
            result.addAll(l.getLocations(lower, upper, alignment));
      }
      return result;
    }
    
    public StringLocation getLocation(int page, int lower, int upper, Location.ALIGNMENT alignment) {
      StringLocation result = new StringLocation();
      for (StringLocation l : locations) {
        if (page != -1 && l.getPage() == page)
            return (StringLocation) l.getLocation(lower, upper, alignment);
      }
      return null;
    }

    public boolean hasPoint(int x, int y)
    {
      return left < x && x < right && bottom < y && y < top;
    }

    public StringLocation getUniqueLocation(int page, int lower, int upper, ALIGNMENT alignment, Location loc)
    {
      for (StringLocation l : locations) {
        if (page != -1 && l.getPage() == page) {
          return l.getHeaders(lower, upper, alignment, loc);
        }
      }
      return null;
    }

    @Override
    public void sort()
    {
      for (StringLocation loc : locations) {
        loc.sort();
      }
    }

}
