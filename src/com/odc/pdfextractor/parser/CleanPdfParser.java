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
package com.odc.pdfextractor.parser;

import org.apache.pdfbox.exceptions.InvalidPasswordException;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

import com.odc.pdfextractor.model.CharacterLocation;
import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.builder.DocumentBuilder;

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
public class CleanPdfParser extends PDFTextStripper implements PdfParser
{
    private DocumentBuilder docBuilder = new DocumentBuilder(1);

    /**
     * Default constructor.
     *
     * @throws IOException If there is an error loading text stripper properties.
     */
    public CleanPdfParser() throws IOException
    {
        super.setSortByPosition( false );
    }

    /**
     * This will print the documents docBuilder.
     *
     * @param args The command line arguments.
     *
     * @throws Exception If there is an error parsing the document.
     */
    public DocumentLocation processPdf(String filename) throws Exception
    {

          PDDocument document = null;
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
                  PDPage page = (PDPage)allPages.get( i );
                  System.out.print( ".");
                  PDStream contents = page.getContents();
                  if( contents != null )
                  {
                      this.processStream( page, page.findResources(), page.getContents().getStream() );
                  }
                  docBuilder.incrementPage();
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
          return docBuilder.getDoc();
    }

    /**
     * A method provided as an event interface to allow a subclass to perform
     * some specific functionality when text needs to be processed.
     *
     * @param docBuilder.text The text to be processed
     */
    protected void processTextPosition( TextPosition textPos )
    {
      char character = textPos.getCharacter().charAt(0);
      int x = Math.round(textPos.getX());
      int width = Math.round(textPos.getWidth());
      int y = Math.round(textPos.getY());
      int height = Math.round(textPos.getHeight());
      int bottom = y + height;
      int right = x + width;
      CharacterLocation charLoc = new CharacterLocation(x, right, y, bottom, docBuilder.getPage(), character);
      docBuilder.addCharacter(charLoc);
    }

    /**
     * This will print the usage for this document.
     */
    private static void usage()
    {
        System.err.println( "Usage: java org.apache.pdfbox.examples.pdmodel.PrintTextLocations <input-pdf>" );
    }

}
