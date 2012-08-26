package com.odc.pdfextractor.model.builder;

import com.odc.pdfextractor.Location;
import com.odc.pdfextractor.model.StringLocation;


public interface LocationBuilder extends Location
{
  StringLocation getLocation();
}