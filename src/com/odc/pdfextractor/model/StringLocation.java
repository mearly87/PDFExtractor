package com.odc.pdfextractor.model;

import java.util.List;

public class StringLocation extends AbstractStringLocation
{
    private final int page;
    public StringLocation(StringLocation loc) {
      super(loc);
      page = loc.getPage();
    }
    
    public StringLocation(List<? extends StringLocation> locations)
    {
      super(locations);
      if (this.getPages().size() > 1) {
        throw new RuntimeException("StringLocation may not span more than 1 page");
      } if (!this.getPages().isEmpty()) {
        this.page = this.getPages().iterator().next();
      } else {
        this.page= -1;
      }
    }

    public StringLocation(int left, int right, int top, int bottom, int page, int size)
    {
      super(left, right, top, bottom, page, size);
      this.page = page;
    } 
    

    @Override
    public int getPage()
    {
      return page;
    }
     
}