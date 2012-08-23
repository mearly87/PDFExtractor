package com.odc.pdfextractor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.odc.pdfextractor.Location;
import com.odc.pdfextractor.model.builder.StringLocationBuilder;

public class StringLocation implements ImmutableLocation
{
  private final int right;
  private final int left;
  private final int bottom;
  private final int top;
  private final int page;
  private final int size;
  
  private List<ImmutableLocation> locations = new ArrayList<ImmutableLocation>();


    public StringLocation(ImmutableLocation loc) {
      this.addLocation(loc);
      right = loc.getRight();
      left = loc.getLeft();
      bottom = loc.getBottom();
      top = loc.getTop();
      page = loc.getPage();
      size = loc.size();
    }
  
    private void addLocation(ImmutableLocation loc) {
      if (loc == null) {
        return;
      }
      locations.add(loc);
    }
    
    public StringLocation(List<ImmutableLocation> stringLocations)
    {
      int size = 0;
      int left = Integer.MAX_VALUE;
      int right = Integer.MIN_VALUE;
      int top = Integer.MAX_VALUE;
      int bottom = Integer.MIN_VALUE;
      int page = -1;
      if (stringLocations != null) {
        for (ImmutableLocation immutableLocation : stringLocations) {
          addLocation(immutableLocation);
          size += immutableLocation.size();
          int locLeft = immutableLocation.getLeft();
          int locRight = immutableLocation.getRight();
          int locTop = immutableLocation.getTop();
          int locBottom = immutableLocation.getBottom();
          if (page == -1) {
            page = immutableLocation.getPage();
          } else if (page != immutableLocation.getPage()) {
            throw new RuntimeException("String location cannot span more than 1 page");
          }
          
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
      this.page = page;
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
      for (ImmutableLocation loc : locations) {
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

    public List<StringLocation> getLineLocations() {
      List<StringLocation> result = new ArrayList<StringLocation>();
      StringLocationBuilder looseChars = null;
      for (ImmutableLocation l : locations) {
        if (l instanceof StringLocation) {
          if (looseChars != null) {
            result.add(looseChars.toLocation());
            looseChars = null;
          }
          result.add((StringLocation) l);
        } else {
          if (looseChars == null) {
            looseChars = new StringLocationBuilder(l);
          } else {
            looseChars.addLocation(l.toLocationBuilder());
          }
        }
      }
      if (looseChars != null) {
        result.add(looseChars.toLocation());
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
      StringLocationBuilder builder = new StringLocationBuilder();
      int charPointer = 0;
      for (ImmutableLocation loc : locations) { 
        if (start >= end) {
          return builder.toLocation();
        }
        if (start <= charPointer && charPointer < end || 
            start < charPointer + loc.size() && charPointer + loc.size() < end || 
            start > charPointer && charPointer + loc.size() >= end) {
          int endIndex = Math.min(loc.size(), end - charPointer);
          int startIndex = start - charPointer;

          ImmutableLocation newLoc = loc.substring(startIndex, endIndex);
          builder.addLocation(newLoc.toLocationBuilder());
          start = start + newLoc.size();
        } 
        charPointer = charPointer + loc.size();
      }
      
      return builder.toLocation();
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
      StringLocationBuilder result = new StringLocationBuilder();
      for (ImmutableLocation l : locations) {
        if( lower < l.getPosition(alignment) && upper > l.getPosition(alignment)) {
          result.addLocation(l);
        }
      }
      return result.toLocation();
    }

    @Override
    public boolean hasPoint(int x, int y)
    {
      return left < x && x < right && bottom < y && y < top;
    }

    // TODO: What if there headers are in two lines with duplication of words on one of the lines?
    public StringLocation getHeaders(int lower, int upper, Location.ALIGNMENT alignment, ImmutableLocation loc)
    {
      StringLocationBuilder result = new StringLocationBuilder();
      for (ImmutableLocation l : locations) {
        if( lower < l.getPosition(alignment) && upper > l.getPosition(alignment)) {

          // If this header is aligned inside of result, 
          // it is underneath one of the headers and should be included
          if (result.getLeft() <= loc.getLeft() && result.getRight() >= loc.getRight()) {
            List<StringLocationBuilder> headers = result.getLineLocationBuilders();
            boolean done = false;
            for (StringLocationBuilder header : headers) {
                if (l.getLeft() <= header.getLeft() && l.getRight() >= header.getRight()) {
                  header.addLocation(l);
                  done = true;
                  continue;
                }

            }
            if(done) {
              continue;
            }
          }
          
          // If result contains this header already, it is a table with horizontal duplication
          // Either return this set of headers, or start a new set depending on if text is in this set
          if (result.containsString(l.toString().trim())) {
            if (result.getLeft() <= loc.getLeft() && result.getRight() >= loc.getRight()) {
              return result.toLocation();
            } else {
              result = new StringLocationBuilder();
            }
          }

          result.addLocation(l);
        }
      }
      return result.toLocation();
    }

    public boolean containsString(String substring)
    {
      return this.toString().contains(substring);
    }

    public StringLocation getLocationUnder(ImmutableLocation header)
    {
      StringLocationBuilder result = new StringLocationBuilder();
      for (ImmutableLocation l : locations) {
        if (l.getLeft() <= header.getLeft() && l.getRight() > header.getLeft()) {
          result.addLocation(l);
          continue;
        } else if (l.getLeft() >= header.getLeft() && l.getLeft() < header.getRight() ) {
          result.addLocation(l);
          continue;
        }
      }
      return result.toLocation();
    }
   
    public boolean empty() {
      return page == -1;
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

    @Override
    public StringLocationBuilder toLocationBuilder()
    {
      StringLocationBuilder builder = new StringLocationBuilder();
      for (ImmutableLocation loc : locations) {
        builder.addLocation(loc.toLocationBuilder());
      }
      return builder;
    }
}