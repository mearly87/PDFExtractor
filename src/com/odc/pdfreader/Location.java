package com.odc.pdfreader;

import java.util.List;


public interface Location
{
  public enum ALIGNMENT {
    left,
    right,
    top,
    bottom, 
    horizontalCenter,
    verticalCenter
  }

  int getRight();

  int getLeft();

  int getBottom();

  int getTop();

  int getPage();
  
  int size();

  int getPosition(Location.ALIGNMENT alignment) throws RuntimeException;

  String fullPrint();
  
  boolean hasPoint(int x, int y);
  
  void sort();
  
  Location substring(int start, int end);
  
  boolean isAbove(Location loc);
  
  boolean matches(String RegEx);
}