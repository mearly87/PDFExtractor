package com.odc.pdfextractor.model.builder;

import com.odc.pdfextractor.Location;
import com.odc.pdfextractor.model.ImmutableLocation;


public interface LocationBuilder extends Location
{
  ImmutableLocation toLocation();
}