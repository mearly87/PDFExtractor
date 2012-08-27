package com.odc.pdfextractor.model;


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
    return new StringLocation(this);
  }
  @Override
  public String fullPrint()
  {
    return this.toString();
  }
  
  public boolean matches(String regex) {
    return String.valueOf(character).matches(regex);
  }

}