package com.odc.pdfextractor.model.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.odc.pdfextractor.Location;
import com.odc.pdfextractor.comparator.TopToBottomComparator;
import com.odc.pdfextractor.model.CharacterLocation;
import com.odc.pdfextractor.model.StringLocation;

public class StringLocationBuilder implements LocationBuilder
{
  private int page = -1;
  private SortedSet<LocationBuilder> locations = new TreeSet<LocationBuilder>(new TopToBottomComparator());
  private int size = 0;

    public StringLocationBuilder(LocationBuilder loc) {
      this.addLocation(loc);
    }
  
    public StringLocationBuilder()  { }

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
      return "{" + word.toString() + "(page: " + page + ", left: " + getLeft() + ", right: " + getRight() + ", top: " + getTop() + ", bottom: " + getBottom() + ")}\n";
    }
    
    public int getRight()
    {

      if (!locations.isEmpty())
        return locations.last().getRight();
      return -1;
    }

    public int getLeft()
    {
      if (!locations.isEmpty())
        return locations.last().getLeft();
      return -1;
    }

    public int getBottom()
    {
      if (!locations.isEmpty())
        return locations.last().getBottom();
      return -1;
    }
    
    public int getTop()
    {
      if (!locations.isEmpty())
        return locations.last().getTop();
      return -1;
    }    
    
    public void addLocation(LocationBuilder loc) {
      if (loc == null) {
        return;
      }
      if (page == -1) {
        page = loc.getPage();
      }
      locations.add(loc);
    }
    
    public void addLocation(CharacterLocation charLoc) {
      if (charLoc == null) {
        return;
      }
      if (page == -1) {
        page = charLoc.getPage();
      }
      locations.add(charLoc);
    }
    
    public List<StringLocationBuilder> getLineLocationBuilders() {
      List<StringLocationBuilder> result = new ArrayList<StringLocationBuilder>();
      StringLocationBuilder looseChars = null;
      for (LocationBuilder l : locations) {
        if (l instanceof StringLocationBuilder) {
          if (looseChars != null) {
            result.add(looseChars);
            looseChars = null;
          }
          result.add((StringLocationBuilder) l);
        } else {
          if (looseChars == null) {
            looseChars = new StringLocationBuilder(l);
          } else {
            looseChars.addLocation(l);
          }
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
    
    public List<StringLocationBuilder> getLocationBuilders(int lower, int upper, Location.ALIGNMENT alignment) {
      List<StringLocationBuilder> result = new ArrayList<StringLocationBuilder>();
      for (StringLocationBuilder l : getLineLocationBuilders()) {
        if( lower < l.getPosition(alignment) && upper > l.getPosition(alignment)) {
            result.add(l);
        }
      }
      return result;
    }
    
    public StringLocationBuilder getLocationBuilder(int lower, int upper, Location.ALIGNMENT alignment) {
      StringLocationBuilder result = new StringLocationBuilder();
      for (LocationBuilder l : locations) {
        if( lower < l.getPosition(alignment) && upper > l.getPosition(alignment)) {
          result.addLocation(l);
        }
      }
      return result;
    }

    @Override
    public boolean hasPoint(int x, int y)
    {
      return getLeft() < x && x < getRight() && getBottom() < y && y < getTop();
    }

    public StringLocationBuilder getLocationBuilderUnder(LocationBuilder header)
    {
      StringLocationBuilder result = new StringLocationBuilder();
      for (LocationBuilder l : locations) {
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
   
    public boolean empty() {
      return page == -1;
    }
    
    public boolean isAbove(LocationBuilder loc) {
      return isAbove(loc, 0);
    }
    
    public boolean isAbove(LocationBuilder loc, int error) {
      return this.getBottom() + error <= loc.getTop();
    }
    
    public boolean matches(String regex) {
      return this.toString().trim().matches(regex);
    }

    public StringLocation getLocation()
    {
      List<StringLocation> stringLocations = new ArrayList<StringLocation>();
      for (LocationBuilder location : locations){
        stringLocations.add(location.getLocation());
      }
      return new StringLocation(stringLocations);
    }
    
    public boolean containsString(String string) {
      return this.toString().contains(string);
    }

    @Override
    public Collection<Integer> getPages()
    {
      return Collections.unmodifiableCollection(Arrays.asList(page));
    }
}