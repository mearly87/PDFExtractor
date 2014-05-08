package com.odc.pdfextractor.model;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.odc.pdfextractor.comparator.TopToBottomComparator;
import com.odc.pdfextractor.model.Location.ALIGNMENT;

public class StringLocation extends AbstractStringLocation
{
	
	public final static StringLocation TABLE_HEADER = new StringLocation(-1,-1,-1,-1,-1,-1); 
	
    private final int page;
    public StringLocation(StringLocation loc) {
      super(loc);
      page = loc.getPage();
    }
   
    
    public StringLocation(Collection<? extends StringLocation> locations)
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
    
    public int getPage()
    {
      return page;
    }
     
	public void draw(Graphics g, int xOffSet, int yOffSet, double xScale, double yScale) {
		for (StringLocation l : getLocations()) {
			l.draw(g, xOffSet, yOffSet, xScale, yScale);
		}
	}

	public List<StringLocation> getLocations(int left, int top, int right, int bottom) {
		List<StringLocation> result = new ArrayList<StringLocation>();
		for (StringLocation loc : getLocations()) {
			result.addAll(loc.getLocations(left, top, right, bottom));
		}
		Collections.sort(result, new TopToBottomComparator());
		return result;
	}
	public StringLocation getLocation(int left, int top, int right, int bottom) {
		return new StringLocation(getLocations(left, top, right, bottom));
	}
}