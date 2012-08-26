package com.odc.pdfextractor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.odc.pdfextractor.Location;

public abstract class AbstractStringLocation implements Location
{
  private final int right;
  private final int left;
  private final int bottom;
  private final int top;
  private final int size;
  
  private final Set<Integer> pageSet = new HashSet<Integer>();
  private final List<StringLocation> locations;


    public AbstractStringLocation(StringLocation loc) {
      locations = new ArrayList<StringLocation>(1);
      this.addLocation(loc);
      right = loc.getRight();
      left = loc.getLeft();
      bottom = loc.getBottom();
      top = loc.getTop();
      size = loc.size();
    }
  
    private void addLocation(StringLocation loc) {
      if (loc == null) {
        return;
      }
      locations.add(loc);
    }
    
    public AbstractStringLocation(List<? extends StringLocation> stringLocations)
    {
      locations = new ArrayList<StringLocation>(stringLocations.size());
      int size = 0;
      int left = Integer.MAX_VALUE;
      int right = Integer.MIN_VALUE;
      int top = Integer.MAX_VALUE;
      int bottom = Integer.MIN_VALUE;
      if (stringLocations != null) {
        for (StringLocation location : stringLocations) {
          addLocation(location);
          size += location.size();
          int locLeft = location.getLeft();
          int locRight = location.getRight();
          int locTop = location.getTop();
          int locBottom = location.getBottom();
          addPageNumber(location.getPage());
          
          
          if (locLeft < left) {
            left = locLeft;
          }
          if (locRight > right) {
            right = locRight;
          }
          if (locTop < top) {
            top = locTop;
          }
          if (locBottom > bottom) {
            bottom = locBottom;
          }
        }
      }
      this.size = size;
      this.left = left;
      this.right = right;
      this.bottom = bottom;
      this.top = top;
    }

    private void addPageNumber(int page)
    {
      if (page > -1)
      pageSet.add(page);
    }

    public AbstractStringLocation(int left, int right, int top, int bottom, int page, int size)
    {
      locations = Collections.emptyList();
      this.left = left;
      this.right = right;
      this.top = top;
      this.bottom = bottom;
      addPageNumber(page);
      this.size = size;
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
      for (StringLocation loc : locations) {
        word.append(loc.fullPrint());
      }
      return "{" + word.toString() + "(page: " + pageSet + ", left: " + left + ", right: " + right + ", top: " + top + ", bottom: " + bottom + ")}\n";
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
    
    public List<StringLocation> getLocations() {
      return Collections.unmodifiableList(locations);
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
    public Collection<Integer> getPages()
    {
      return Collections.unmodifiableCollection(pageSet);
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
    
    public StringLocation substring(int start, int end) {
      List<StringLocation> locations = new ArrayList<StringLocation>();
      int charPointer = 0;
      for (StringLocation loc : this.locations) { 
        if (start >= end) {
          return new StringLocation(locations);
        }
        if (start <= charPointer && charPointer < end || 
            start < charPointer + loc.size() && charPointer + loc.size() < end || 
            start > charPointer && charPointer + loc.size() >= end) {
          int endIndex = Math.min(loc.size(), end - charPointer);
          int startIndex = start - charPointer;

          StringLocation newLoc = loc.substring(startIndex, endIndex);
          locations.add(newLoc);
          start = start + newLoc.size();
        } 
        charPointer = charPointer + loc.size();
      }
      return new StringLocation(locations);
    }
    
    public List<StringLocation> getLocations(int lower, int upper, Location.ALIGNMENT alignment) {
      List<StringLocation> locs = new ArrayList<StringLocation>(locations);
      for (Location l : locations) {
        if( lower > l.getPosition(alignment) || upper < l.getPosition(alignment)) {
          locs.remove(l);
        }
      }
      return locs;
    }
    
    public StringLocation getLocation(int lower, int upper, Location.ALIGNMENT alignment) {

      return new StringLocation(getLocations(lower, upper, alignment));
    }
    

    @Override
    public boolean hasPoint(int x, int y)
    {
      return left < x && x < right && bottom < y && y < top;
    }

    public boolean containsString(String substring)
    {
      return this.toString().contains(substring);
    }

    public StringLocation getLocationUnder(StringLocation header)
    {
      List<StringLocation> result = new ArrayList<StringLocation>();
      for (StringLocation l : locations) {
        if (l.getLeft() <= header.getLeft() && l.getRight() > header.getLeft() || l.getLeft() >= header.getLeft() && l.getLeft() < header.getRight()) {
          result.add(l);
          continue;
        }
      }
      return new StringLocation(result);
    }
   
    public boolean empty() {
      return pageSet.size() > 0;
    }
    
    public boolean isAbove(Location loc) {
      return isAbove(loc, 0);
    }
    
    public boolean isAbove(Location loc, int error) {
      return this.getBottom() + error <= loc.getTop();
    }
    
    public boolean matches(String regex) {
      return this.toString().trim().matches(regex);
    }
   
}