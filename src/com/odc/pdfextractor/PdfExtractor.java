package com.odc.pdfextractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.parser.CleanPdfParser;
import com.odc.pdfextractor.parser.DirtyPdfParser;
import com.odc.pdfextractor.parser.PdfParser;


public class PdfExtractor
{
  
  public static void main(String[] args) throws Exception {

    String filename = null;
    if (args.length > 0) {
      filename = args[0];
    } else {
      System.out.println("Enter filename of PDF:");
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      filename = br.readLine();
      if (!filename.toLowerCase().endsWith(".pdf") && !filename.toLowerCase().endsWith(".xml")) {
        System.out.println("File must be of extension type .pdf or .xml: " + filename);
        return;
      }
    }
    Calendar startTime = Calendar.getInstance();
    long start = startTime.getTimeInMillis();
    printTransactions(filename);
    Calendar endTime = Calendar.getInstance();
    long end = endTime.getTimeInMillis();
    System.out.println("Retrieved in " + (double) ((end - start)) / 1000 + " seconds");
  }

  private static void printTransactions(String filename) throws IOException, Exception
  {
    PdfParser converter;
    if (filename.toLowerCase().endsWith(".pdf")) {
      converter = new CleanPdfParser();

    } else {
      converter = new DirtyPdfParser();
    }
    DocumentLocation doc = converter.processPdf(filename);

    converter = null;
    
    List<StringLocation> dateLocations = (doc).applyRegEx(Constants.dateRegEx);
    List<StringLocation> groupedDates = StringLocationHelper.combineInlineItems(dateLocations, 2);
    List<StringLocation> dateCols = StringLocationHelper.getDateColumns(doc, Constants.dateRegEx, groupedDates);

    List<Map<StringLocation, StringLocation>> transactions = new ArrayList<Map<StringLocation, StringLocation>>();
    
    for (StringLocation dateColumn : dateCols) {
      StringLocation headers = StringLocationHelper.getHeaders(doc, dateColumn);
      Location tableName = doc.getLocation(headers.getPage(), headers.getTop() - 15, headers.getTop(), Location.ALIGNMENT.bottom);
      
      Map<StringLocation, StringLocation> headerToDataCol = StringLocationHelper.getHeaderToDataMap(doc, dateColumn, headers);
      List<StringLocation> dates = dateColumn.getLineLocations();
      transactions.addAll(StringLocationHelper.getTransactions(tableName, headerToDataCol, dates));
    }

    System.out.println("# of Transactions: " + transactions.size());
  }
}
