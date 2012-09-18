package com.odc.pdfextractor.model;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


public class CharacterLocation extends StringLocation {

  private final char character;
  
  public CharacterLocation(int left, int right, int top, int bottom, int page, char character)
  {
    super(left, right, top, bottom, page, 1);
    this.character = character;
    
  }
  public String toString() {
    return String.valueOf(character);
  }
  
  public char getCharacter()
  {
    return character;
  }
  
  @Override
  public int size()
  {
    return 1;
  }
  
  @Override
  public StringLocation substring(int start, int end)
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
  
  public boolean matches(String regex) {
    return String.valueOf(character).matches(regex);
  }
  @Override
  public void draw(Graphics g, int xOffSet, int yOffSet, double xScale, double yScale) {
	  int x = (int) Math.round((xOffSet + getLeft()) * xScale);
	  int y = (int) Math.round((yOffSet + getTop()) * yScale);
	g.drawChars(new char[]{character}, 0, 1, x, y);
  }

	public List<StringLocation> getLocations(int left, int top, int right, int bottom) {
		List<StringLocation> result = new ArrayList<StringLocation>();
	      boolean isAfterH = left > this.getRight();
	      boolean isBeforeH = right < this.getLeft();
	      boolean isAfterV = top > this.getBottom();
	      boolean isBeforeV = bottom < this.getTop();
		if(!isAfterH && !isBeforeH  && !isAfterV && !isBeforeV ) {
			result.add(this);
		}
		return result;
	}
}