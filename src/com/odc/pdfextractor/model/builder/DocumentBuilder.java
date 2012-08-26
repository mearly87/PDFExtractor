package com.odc.pdfextractor.model.builder;

import java.util.ArrayList;
import java.util.List;

import com.odc.pdfextractor.Constants;
import com.odc.pdfextractor.model.CharacterLocation;
import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.StringLocation;

public class DocumentBuilder
{
  
  private StringLocationBuilder word;
  private StringLocationBuilder text;
  private StringLocationBuilder page;
  private List<StringLocation> doc;
  private int pageNumber;
  private int error;

  public DocumentBuilder(int error) { 
    this.error = error;
    word = new StringLocationBuilder();
    text = new StringLocationBuilder();
    page = new StringLocationBuilder();
    doc = new ArrayList<StringLocation>();
  }
  
  public void addCharacter(CharacterLocation charLoc)
  {
    if (charLoc.getCharacter() == ' ') {
      if (word != null && !word.empty()) {
          if (word.toString().trim().matches(Constants.dateRegEx) || word.toString().trim().matches(Constants.amountRegEx)) {
            page.addLocation(text);
            page.addLocation(word);
            text = new StringLocationBuilder();
          } else {
            text.addLocation(word);
          }
      }
      word = new StringLocationBuilder();
      return;
    }

    if (word.empty() && (text.isAbove(charLoc) || charLoc.isAbove(text))) {
      addWord();
    } else if (!word.empty() &&  (word.isAbove(charLoc) || charLoc.isAbove(word))) {
      addWord();
    } else if (!word.empty() && word.getRight() + error < charLoc.getLeft()) {
      addWord();
    }
    word.addLocation(charLoc);
  }

  private void addWord()
  {
    if (!word.empty()) {
      text.addLocation(word);
      word = new StringLocationBuilder();
    } if (!text.empty()) {
      page.addLocation(text);
      text = new StringLocationBuilder();
    }
  }
  
  public void incrementPage() {
    pageNumber++;
    addWord();
    doc.add(page.getLocation());
    page = new StringLocationBuilder();
  }
  
  public int getPage() {
    return pageNumber;
  }
  
  public DocumentLocation getDoc()
  {
    return new DocumentLocation(doc);
  }
  
}