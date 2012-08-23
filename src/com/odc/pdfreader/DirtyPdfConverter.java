package com.odc.pdfreader;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class DirtyPdfConverter implements PdfConverter
{
  
  public DocumentLocation processPdf(String filename) {
    File xml = new File(filename);
    ABBYXmlParser handler = new ABBYXmlParser();
    
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
       try {
         saxParser.parse(xml, handler);
       } catch (SAXException e) {
         if (!e.getMessage().equals("Done")) {
           throw new SAXException(e);
         }
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
   return handler.getDocument();
  }

}
