package com.odc.pdfextractor.model;

import com.odc.pdfextractor.Location;
import com.odc.pdfextractor.model.builder.LocationBuilder;

public interface ImmutableLocation extends Location
{  
  StringLocation substring(int start, int end);
  
}