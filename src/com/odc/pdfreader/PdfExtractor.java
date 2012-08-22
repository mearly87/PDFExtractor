package com.odc.pdfreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
      if (!filename.toLowerCase().endsWith(".pdf")) {
        System.out.println("File must be of extension type .pdf");
        return;
      }
    }
    printTransactions(filename);
  }

  private static void printTransactions(String filename) throws IOException, Exception
  {
    CleanPdfConverter converter = new CleanPdfConverter();
    DocumentLocation doc = converter.processCleanPdf(filename);
    converter = null;
    
    List<StringLocation> dateLocations = (doc).applyRegEx(Constants.dateRegEx);
    Map<Integer, List<StringLocation>> groupedDates = StringLocationHelper.groupInlineItems(dateLocations, 2, Location.ALIGNMENT.left);
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
