/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.odc.pdfreader;

import org.apache.pdfbox.exceptions.InvalidPasswordException;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

import java.io.IOException;

import java.util.List;

/**
 * This is an example on how to get some x/y coordinates of text.
 *
 * Usage: java org.apache.pdfbox.examples.util.PrintTextLocations &lt;input-pdf&gt;
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.7 $
 */
public class CleanPdfConverter extends PDFTextStripper
{
    private StringLocation word;
    private StringLocation text;
    private StringLocation page;
    private DocumentLocation doc;
    private int pageNumber = 0;
    
    /**
     * Default constructor.
     *
     * @throws IOException If there is an error loading text stripper properties.
     */
    public CleanPdfConverter() throws IOException
    {
        super.setSortByPosition( false );
    }

    /**
     * This will print the documents data.
     *
     * @param args The command line arguments.
     *
     * @throws Exception If there is an error parsing the document.
     */
    public DocumentLocation processCleanPdf(String filename) throws Exception
    {

          PDDocument document = null;
          doc = new DocumentLocation();
          try
          {
              document = PDDocument.load( filename );
              if( document.isEncrypted() )
              {
                  try
                  {
                      document.decrypt( "" );
                  }
                  catch( InvalidPasswordException e )
                  {
                      System.err.println( "Error: Document is encrypted with a password." );
                      System.exit( 1 );
                  }
              }
              List allPages = document.getDocumentCatalog().getAllPages();
              System.out.print("Extracting text from PDF");
              for( int i=0; i<allPages.size(); i++ )
              {
                if (page != null) {
                  if (text != null) {
                    if (word != null) {
                      text.addLocation(word);
                    }
                    page.addLocation(text);
                  }
                  doc.addLocation(page);
                }
                text = new StringLocation();
                word = new StringLocation();
                page = new StringLocation();
                  PDPage page = (PDPage)allPages.get( i );
                  System.out.print( ".");
                  PDStream contents = page.getContents();
                  if( contents != null )
                  {
                      this.processStream( page, page.findResources(), page.getContents().getStream() );
                  }
                  pageNumber++;
              }
          }
          finally
          {
            System.out.println();
              if( document != null )
              {
                  document.close();
              }
          }
          // System.out.println(doc);
          doc.sort();
          return doc;
    }

    /**
     * A method provided as an event interface to allow a subclass to perform
     * some specific functionality when text needs to be processed.
     *
     * @param text The text to be processed
     */
    protected void processTextPosition( TextPosition textPos )
    {
      String character = textPos.getCharacter();
      int x = Math.round(textPos.getX());
      int width = Math.round(textPos.getWidth());
      int y = Math.round(textPos.getY());
      int height = Math.round(textPos.getHeight());
      if (character.trim().isEmpty()) {
        if (word != null) {
          if (word.getPage() != -1)
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
      if (word.getPage() == -1 && text.getBottom() < y) {
        page.addLocation(text);
        text = new StringLocation();
      } else if (word.getPage() == -1 && text.getTop() > y + height) {
        page.addLocation(text);
        text = new StringLocation();
      } else if (word.getPage() != -1 && word.getBottom() < y) {
        text.addLocation(word);
        page.addLocation(text);
        text = new StringLocation();
        word = new StringLocation();
      } else if (word.getPage() != -1 && word.getTop() > y + height) {
        text.addLocation(word);
        page.addLocation(text);
        text = new StringLocation();
        word = new StringLocation();
      }
      else if (word.getPage() != -1 && word.getRight() + 1 < x) {
          text.addLocation(word);
          page.addLocation(text);
          text = new StringLocation();
          word = new StringLocation();
      }
      Location location = new CharacterLocation(x,
      x + width,
      y,
      y + height,
      pageNumber, character.charAt(0));
      word.addLocation(location);
    }

    /**
     * This will print the usage for this document.
     */
    private static void usage()
    {
        System.err.println( "Usage: java org.apache.pdfbox.examples.pdmodel.PrintTextLocations <input-pdf>" );
    }

}
