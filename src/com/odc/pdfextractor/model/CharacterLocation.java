package com.odc.pdfextractor.model;

import com.odc.pdfextractor.Location;
import com.odc.pdfextractor.model.builder.LocationBuilder;

public class CharacterLocation implements ImmutableLocation, LocationBuilder {
  private final int right;
  private final int left;
  private final int bottom;
  private final int top;
  private final int page;
  private final char character;
  
  public CharacterLocation(int left, int right, int top, int bottom, int page, char character)
  {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.page = page;
    this.character = character;
    
  }
  public String toString() {
    return String.valueOf(character);
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
  public int getPage()
  {
    return page;
  }
  public char getCharacter()
  {
    return character;
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
  @Override
  public int size()
  {
    return 1;
  }
  
  @Override
  public ImmutableLocation substring(int start, int end)
  {
    if (start + size() != end) {
      throw new RuntimeException("Invalid indexes");
    }
    return this;
  }
  @Override
  public String fullPrint()
  {
    return this.toString();
  }
  
  @Override
  public boolean hasPoint(int x, int y)
  {
    return left < x && x < right && bottom < y && y < top;
  }
  
  public boolean isAbove(Location loc) {
    return isAbove(loc, 0);
  }
  
  public boolean isAbove(Location loc, int error) {
    return this.getBottom() + error <= loc.getTop() && this.getBottom() <= loc.getTop() + error;
  }
  
  public boolean matches(String regex) {
    return String.valueOf(character).matches(regex);
  }

  @Override
  public ImmutableLocation toLocation()
  {
    return this;
  }
  @Override
  public LocationBuilder toLocationBuilder()
  {
    return this;
  }
}