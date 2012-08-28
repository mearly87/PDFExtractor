package com.odc.pdfextractor.model.builder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.odc.pdfextractor.Constants;
import com.odc.pdfextractor.Location;
import com.odc.pdfextractor.comparator.LeftToRightComparator;
import com.odc.pdfextractor.comparator.TopToBottomComparator;
import com.odc.pdfextractor.model.CharacterLocation;
import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.model.StringLocation;

public class DocumentBuilder
{
  private Comparator<Location> topToBottom = new TopToBottomComparator();
  private Comparator<Location> leftToRight = new LeftToRightComparator();
  private ArrayList<StringLocation> word;
  private ArrayList<StringLocation> text;
  private SortedSet<StringLocation> page;
  private List<StringLocation> doc = new ArrayList<StringLocation>();
  private int pageNumber;
  private int error;

  public DocumentBuilder(int error, Comparator<Location> comparator) { 
    this.error = error;
    // this.comparator = comparator;
    word = new ArrayList<StringLocation>();
    text = new ArrayList<StringLocation>();
    page = new TreeSet<StringLocation>(topToBottom);
  }
  
  public void addCharacter(CharacterLocation charLoc)
  {
    if (charLoc.getCharacter() == ' ') {
      if (word != null && !word.isEmpty()) {
        StringLocation wordLoc = new StringLocation(word);
          if (wordLoc.toString().trim().matches(Constants.dateRegEx) || wordLoc.toString().trim().matches(Constants.amountRegEx)) {
            page.add(new StringLocation(text));
            page.add(wordLoc);
            text = new ArrayList<StringLocation>();
          } else {
            text.add(wordLoc);
          }
      }
      word = new ArrayList<StringLocation>();
      return;
    }

    if (word.isEmpty() && (isAbove(text, charLoc) || isUnder(text, charLoc))) {
      addWord();
    } else if (!word.isEmpty() &&  (isAbove(word, charLoc) || isUnder(word, charLoc))) {
      addWord();
    } else if (!word.isEmpty() && word.get(word.size() -1).getRight() + error < charLoc.getLeft()) {
      addWord();
    }
    word.add(charLoc);
  }
  
  private boolean isAbove(List<? extends StringLocation> loc1, CharacterLocation loc2)
  {
    if (loc1.isEmpty()) {
      return false;
    }
    return loc1.get(loc1.size() - 1).getBottom() <= loc2.getTop();
  }
  
  private boolean isUnder(List<? extends StringLocation> loc1, StringLocation loc2) {
    if (loc1.isEmpty()) {
      return false;
    }
    return loc2.getBottom() <= loc1.get(loc1.size() -1).getTop();
  }

  private void addWord()
  {
    if (!word.isEmpty()) {
      text.add(new StringLocation(word));
      word = new ArrayList<StringLocation>();
    } if (!text.isEmpty()) {
      page.add(new StringLocation(text));
      text = new ArrayList<StringLocation>();
    }
  }
  
  public void incrementPage() {
    pageNumber++;
    addWord();
    doc.add(new StringLocation(page));
    page = new TreeSet<StringLocation>(topToBottom);
  }
  
  public int getPage() {
    return pageNumber;
  }
  
  public DocumentLocation getDoc()
  {
    return new DocumentLocation(doc);
  }
  
}