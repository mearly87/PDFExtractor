package com.odc.pdfextractor.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.odc.pdfextractor.comparator.LeftToRightComparator;
import com.odc.pdfextractor.model.CharacterLocation;
import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.builder.DocumentBuilder;


public class ABBYXmlParser extends DefaultHandler {
  boolean charParams = false;
  
  int page = -1;
  DocumentBuilder docBuilder = new DocumentBuilder(7, new LeftToRightComparator());
  
  int left;
  int right;
  int top;
  int bottom;
  
  public void startElement(String uri, String localName,String qName, Attributes attr) throws SAXException {

      // System.out.println("Start Element :" + qName);

    if (qName.equalsIgnoreCase("charParams")) {
      charParams = true;
      left = Integer.parseInt(attr.getValue("l"));
      right = Integer.parseInt(attr.getValue("r"));
      top = Integer.parseInt(attr.getValue("t"));
      bottom = Integer.parseInt(attr.getValue("b"));
    }
    else if (qName.equalsIgnoreCase("page")) {
      docBuilder.incrementPage();
    }
  }
  
  public void endDocument() {
    System.out.println("DONE");
  }

  public void characters(char ch[], int start, int length) throws SAXException {

      if (charParams) {
        //System.out.print(ch[start]);
        CharacterLocation charLoc = new CharacterLocation(left, right, top, bottom, docBuilder.getPage(), ch[start]);
        docBuilder.addCharacter(charLoc);
      }
      charParams = false;
   }

  public DocumentLocation getDocument() {
    if (docBuilder != null) {
      return docBuilder.getDoc();
    }
    return null;
  }
}



