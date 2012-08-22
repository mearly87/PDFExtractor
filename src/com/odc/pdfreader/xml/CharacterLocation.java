package com.odc.pdfreader.xml;

import java.util.List;

public class CharacterLocation implements Location {
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
  public Location substring(int start, int end)
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

  @Override
  public void sort()
  {
   // Already Sorted
    
  }
}