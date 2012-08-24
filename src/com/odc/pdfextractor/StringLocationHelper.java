package com.odc.pdfextractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.odc.pdfextractor.Location.ALIGNMENT;
import com.odc.pdfextractor.comparator.VerticalIntersectionComparator;
import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.ImmutableLocation;
import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.model.builder.LocationBuilder;
import com.odc.pdfextractor.model.builder.StringLocationBuilder;

public class StringLocationHelper
{

  public static List<Map<StringLocation, StringLocation>> getTransactions(Location tableName,
      Map<StringLocation, StringLocation> headerToDataCol, List<StringLocation> dates)
  {
    List<Map<StringLocation, StringLocation>> transactions = new ArrayList<Map<StringLocation, StringLocation>>();
    for (int i = 2; i <= dates.size(); i++) {
      Map<StringLocation, StringLocation> transMap = new HashMap<StringLocation, StringLocation>();
      int lower = dates.get(i - 1).getTop();
      int upper = Integer.MAX_VALUE;
      if (i < dates.size()) {
        upper = dates.get(i).getTop();
      }
  
      StringLocation dHeader = null;
      StringLocation d = null;
      for (StringLocation header : headerToDataCol.keySet()) {
        StringLocation transDataCol = headerToDataCol.get(header);
        StringLocation transData = ((StringLocation) transDataCol).getLocation(lower, upper, Location.ALIGNMENT.verticalCenter);
        
        // Remove description to make pretty print the transaction with description last
        if (header.toString().trim().equalsIgnoreCase("description")) {
          dHeader = header;
          d = transData;
        } else {
          transMap.put(header, transData);
        }
      }
      printTransaction(tableName, transactions, transMap, dHeader, d);
    }
    return transactions;
  }

  public static Map<StringLocation, StringLocation> getHeaderToDataMap(DocumentLocation doc, StringLocation dateColumn,
      StringLocation headers)
  {
    Map<StringLocation, StringLocation> headerToDataCol = new HashMap<StringLocation, StringLocation>();
    StringLocation data = doc.getLocation(dateColumn.getPage(), dateColumn.getTop() - 3, dateColumn.getBottom() + 3, Location.ALIGNMENT.verticalCenter);
    data = data.getLocation(headers.getLeft() - 3, headers.getRight() + 3, Location.ALIGNMENT.horizontalCenter);
    
    for (StringLocation header : headers.getLineLocations()) {
     StringLocation colDataLocation = data.getLocationUnder(header);
     headerToDataCol.put(header, colDataLocation);
    }
    return headerToDataCol;
  }

  public static StringLocation getHeaders(DocumentLocation data, StringLocation dateColumn) {

    List<StringLocation> dateLocs = dateColumn.getLineLocations();
    ImmutableLocation dateHeader = dateLocs.get(0);
    ImmutableLocation firstDate = dateLocs.get(0);
    if (dateLocs.size() > 1)
      firstDate = dateLocs.get(1);
    for (StringLocation l : data.getLocations()) {
      if (dateHeader.getPage() != -1 && l.getPage() == dateHeader.getPage()) {
        List<StringLocation> result = l.getLocations(dateHeader.getTop(), firstDate.getTop(), ALIGNMENT.verticalCenter);
        List<StringLocation> headers = StringLocationHelper.combineInlineItems(result, 0);
        StringLocation firstheader = headers.get(0);
        int start = 0;
        int end = headers.size();
        for (int i = 1; i < headers.size(); i++) {
          if (firstheader.toString().equals(headers.get(i).toString())) {
            if (dateHeader.getLeft() < headers.get(i - 1).getRight()) {
              end = i;
              break;
            } else {
              start = i;
            }
          }
        }
        return new StringLocation(headers.subList(start, end));
      }
    }
    return null;
  }

  public static List<StringLocation> getDateColumns(DocumentLocation doc, String regEx,
      List<StringLocation> locations)
  {
    List<StringLocation> dateCols = new ArrayList<StringLocation>();
    for (StringLocation loc : locations) {
      int left = loc.getLeft();
      List<StringLocation> locs = doc.getLocations(loc.getPage(), left - 10, left + 10, Location.ALIGNMENT.left);
      dateCols.addAll(cleanColumn("(D|d)(A|a)(T|t)(E|e)", regEx, locs));
    }
    return dateCols;
  }

  public static List<StringLocation> cleanColumn(String header, String data, List<StringLocation> locs) {
    Pattern headerRegEx = Pattern.compile(header);
    
    Pattern dataRegEx = Pattern.compile(data);
    
    List<StringLocation> result = new ArrayList<StringLocation>();
    List<StringLocation> dateCol = null;
    for (StringLocation loc : locs) {
      Matcher headerMatcher = headerRegEx.matcher(loc.toString().trim());
      if (headerMatcher.matches()) {
        if (dateCol != null) {
          result.add(new StringLocation(dateCol));
        }
        dateCol = new ArrayList<StringLocation>();
        dateCol.add(loc);
      }
      Matcher dataMatcher = dataRegEx.matcher(loc.toString());
      if (dataMatcher.find()) {
        if (dateCol != null) {
          dateCol.add(loc);
        }
      }
    }
    if (dateCol != null) {
      result.add(new StringLocation(dateCol));
    } 
    return result;
  }
  
  public static List<StringLocation> combineSimilarItems(List<StringLocation> items, Comparator<Location> comparator) {
    List<StringLocation> result = new ArrayList<StringLocation>();
    Collections.sort(items, comparator);
    List<StringLocation> buffer = new ArrayList<StringLocation>();
    for (StringLocation loc : items) {
      if (buffer.size() == 0) {
        buffer.add(loc);
      } else if (comparator.compare(buffer.get(0), loc) == 0) {
        buffer.add(loc);
      } else {
        result.add(new StringLocation(buffer));
        buffer = new ArrayList<StringLocation>();
        buffer.add(loc);
      }
    }
    if (buffer.size() != 0) {
      result.add(new StringLocation(buffer));
    }
    return result;
  }
  
  public static List<StringLocation> combineInlineItems(List<StringLocation> items, int error) {
    return combineSimilarItems(items, new VerticalIntersectionComparator(error));
  }

  private static void printTransaction(Location tableName,
      List<Map<StringLocation, StringLocation>> transactions,
      Map<StringLocation, StringLocation> transMap, StringLocation dHeader, StringLocation d)
  {
    System.out.print("Transaction in table " + tableName + " " + transMap + " ");
    if (dHeader != null) {
      System.out.print("\t" + dHeader + ": " + d);
    }
    System.out.println();
    transMap.put(dHeader, d);
    transactions.add(transMap);
  }
}
