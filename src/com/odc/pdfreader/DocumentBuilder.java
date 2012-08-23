package com.odc.pdfreader;

public class DocumentBuilder
{
  
  
  
  private StringLocation word;
  private StringLocation text;
  private StringLocation page;
  private DocumentLocation doc;
  private int pageNumber;
  private int error;

  public DocumentBuilder(int error) { 
    this.error = error;
    word = new StringLocation();
    text = new StringLocation();
    page = new StringLocation();
    doc = new DocumentLocation();
  }
  
  public void addCharacter(CharacterLocation charLoc)
  {
    if (charLoc.getCharacter() == ' ') {
      if (word != null && !word.empty()) {
          if (word.toString().trim().matches(Constants.dateRegEx) || word.toString().trim().matches(Constants.amountRegEx)) {
            page.addLocation(text);
            page.addLocation(word);
            text = new StringLocation();
          } else {
            text.addLocation(word);
          }
      }
      word = new StringLocation();
      return;
    }

    if (word.empty() && (text.isAbove(charLoc) || charLoc.isAbove(text))) {
      // System.out.println("{word[" + word.getBottom() + "," + word.getTop()+ "] [" + charLoc.getBottom() + "," + word.getTop() + " + ]}");
      addWord();
    } else if (!word.empty() &&  (word.isAbove(charLoc) || charLoc.isAbove(word))) {
     //  System.out.println("{word[" + word.getBottom() + "," + word.getTop()+ "] [" + charLoc.getBottom() + "," + word.getTop() + " + ]}");
      addWord();
    } else if (word.getRight() + error < charLoc.getLeft()) {
      addWord();
    }
    word.addLocation(charLoc);
  }

  private void addWord()
  {
    if (!word.empty()) {
      text.addLocation(word);
      word = new StringLocation();
    }
    page.addLocation(text);
    text = new StringLocation();
  }
  
  public void incrementPage() {
    pageNumber++;
    addWord();
    doc.addLocation(page);
    page = new StringLocation();
  }
  
  public int getPage() {
    return pageNumber;
  }
  
  public DocumentLocation getDoc()
  {
    return doc;
  }
  
}