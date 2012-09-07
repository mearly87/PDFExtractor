package com.odc.pdfextractor.model;

import java.util.Collection;

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
  
  boolean matches(String RegEx);

  Collection<Integer> getPages();

}