package com.odc.pdfextractor.model.helper;

import java.util.Collection;

import com.odc.pdfextractor.model.StringLocation;

public class TestStringLocation extends StringLocation
{

    String data;
    public TestStringLocation(int left, int right, int top, int bottom, int page, int size, String data)
    {
      super(left, right, top, bottom, page, size);
      this.data = data;
    } 
    
    public TestStringLocation(String data)
    {
      super(0, 1, 0, 1, 0, data.length());
      this.data = data;
    } 
     
    public String toString() {
      return data + " ";
    }
}