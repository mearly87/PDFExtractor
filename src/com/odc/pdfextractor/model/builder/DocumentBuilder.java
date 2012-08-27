package com.odc.pdfextractor.model.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.odc.pdfextractor.Constants;
import com.odc.pdfextractor.comparator.TopToBottomComparator;
import com.odc.pdfextractor.model.CharacterLocation;
import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.StringLocation;

public class DocumentBuilder
{
  private TopToBottomComparator comparator = new TopToBottomComparator();
  private SortedSet<StringLocation> word = new TreeSet<StringLocation>(comparator);
  private SortedSet<StringLocation> text = new TreeSet<StringLocation>(comparator);
  private SortedSet<StringLocation> page = new TreeSet<StringLocation>(comparator);
  private List<StringLocation> doc = new ArrayList<StringLocation>();
  private int pageNumber;
  private int error;

  public DocumentBuilder(int error) { 
    this.error = error;

  }
  
  public void addCharacter(CharacterLocation charLoc)
  {
    if (charLoc.getCharacter() == ' ') {
      if (word != null && !word.isEmpty()) {
          if (word.toString().trim().matches(Constants.dateRegEx) || word.toString().trim().matches(Constants.amountRegEx)) {
            page.add(new StringLocation(text));
            page.add(new StringLocation(word));
            text = new TreeSet<StringLocation>(comparator);
          } else {
            text.add(new StringLocation(word));
          }
      }
      word = new TreeSet<StringLocation>(comparator);
      return;
    }

    if (word.isEmpty() && (isAbove(text, charLoc) || isUnder(text, charLoc))) {
      addWord();
    } else if (!word.isEmpty() &&  (isAbove(word, charLoc) || isUnder(word, charLoc))) {
      addWord();
    } else if (!word.isEmpty() && word.last().getRight() + error < charLoc.getLeft()) {
      addWord();
    }
    word.add(charLoc);
  }
  
  private boolean isAbove(SortedSet<StringLocation> loc1, StringLocation loc2) {
    if (loc1.isEmpty()) {
      return false;
    }
    return loc1.last().getBottom() + error <= loc2.getTop();
  }
  
  private boolean isUnder(SortedSet<StringLocation> loc1, StringLocation loc2) {
    if (loc1.isEmpty()) {
      return false;
    }
    return loc2.getBottom() + error <= loc1.first().getTop();
  }

  private void addWord()
  {
    if (!word.isEmpty()) {
      text.add(new StringLocation(word));
      word = new TreeSet<StringLocation>(comparator);
    } if (!text.isEmpty()) {
      page.add(new StringLocation(text));
      text = new TreeSet<StringLocation>(comparator);
    }
  }
  
  public void incrementPage() {
    pageNumber++;
    addWord();
    doc.add(new StringLocation(page));
    page = new TreeSet<StringLocation>(comparator);
  }
  
  public int getPage() {
    return pageNumber;
  }
  
  public DocumentLocation getDoc()
  {
    return new DocumentLocation(doc);
  }
  
}