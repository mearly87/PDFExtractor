package com.odc.pdfextractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.odc.pdfextractor.Location.ALIGNMENT;
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
      if (dateColumn.contains(header)) {
        continue;
      }
     StringLocation colDataLocation = data.getLocationUnder(header, dateColumn.getPosition(ALIGNMENT.horizontalCenter));
     headerToDataCol.put(header, colDataLocation);
    }
    headerToDataCol.put((StringLocation) dateColumn.getFirstLcoation(), dateColumn);
    return headerToDataCol;
  }

  public static StringLocation getHeaders(DocumentLocation data, StringLocation dateColumn) {

    List<StringLocation> dateLocs = dateColumn.getLineLocations();
    ImmutableLocation dateHeader = dateLocs.get(0);
    ImmutableLocation firstDate = dateLocs.get(0);
    if (dateLocs.size() > 1)
      firstDate = dateLocs.get(1);
    return data.getUniqueLocation(dateHeader.getPage(), dateHeader.getTop() - 10, firstDate.getTop(), Location.ALIGNMENT.verticalCenter, dateHeader);
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

  public static void putInBucketByPage(final Map<Integer, StringLocationBuilder> boxes, StringLocationBuilder loc, int error, Location.ALIGNMENT alignment)
  {
    int key = -1;
    for (int i = 0; i < error; i++) {
      if (boxes.containsKey(loc.getPosition(alignment) - i)) {
        boxes.get(loc.getPosition(alignment) - i).addLocation(loc);
        key = loc.getPosition(alignment) - i;
      }
      else if (boxes.containsKey(loc.getPosition(alignment) + i)){
        boxes.get(loc.getPosition(alignment) + i).addLocation(loc);
        key = loc.getPosition(alignment) + i;
      }
    }
    if (key == -1) {
      key = loc.getPosition(alignment);
      List<LocationBuilder> newLocationList = new ArrayList<LocationBuilder>();
      newLocationList.add(loc);
      boxes.put(key, loc);
    } else {
      boxes.get(key).addLocation(loc);
    }
  }

  public static List<StringLocation> cleanColumn(String header, String data, List<StringLocation> locs) {
    Pattern headerRegEx = Pattern.compile(header);
    
    Pattern dataRegEx = Pattern.compile(data);
    
    List<StringLocation> result = new ArrayList<StringLocation>();
    StringLocationBuilder dateCol = null;
    for (StringLocation loc : locs) {
      Matcher headerMatcher = headerRegEx.matcher(loc.toString().trim());
      if (headerMatcher.matches()) {
        if (dateCol != null) {
          result.add(dateCol.toLocation());
        }
        dateCol = new StringLocationBuilder(loc);
      }
      Matcher dataMatcher = dataRegEx.matcher(loc.toString());
      if (dataMatcher.find()) {
        if (dateCol != null) {
          dateCol.addLocation(loc);
        }
      }
    }
    if (dateCol != null) {
      result.add(dateCol.toLocation());
    } 
    return result;
  }

  public static List<StringLocation> groupInlineItems(List<StringLocation> results, int error, Location.ALIGNMENT alignment)
  {
    Map<Integer, List<StringLocation>> locsByPage = new HashMap<Integer, List<StringLocation>>();
    Map<Integer, List<StringLocation>> locsMap = new HashMap<Integer, List<StringLocation>>();
    List<StringLocation> locations = new ArrayList<StringLocation>();
    for (StringLocation l : results) {
      if (!locsByPage.containsKey(l.getPage())) {
        locsByPage.put(l.getPage(), new ArrayList<StringLocation>());
      }
      locsByPage.get(l.getPage()).add(l);
    }
    for (List<StringLocation> locs : locsByPage.values()) {
      Map<Integer, StringLocationBuilder> locMap = new HashMap<Integer, StringLocationBuilder>();
      for (StringLocation location : locs) {
        putInBucketByPage(locMap, location.toLocationBuilder(), error, alignment);
      }
      for (StringLocationBuilder builder : locMap.values()) {
        locations.add(builder.toLocation());
      }
      locsMap.put(locs.get(0).getPage(), locations);
    }
    return locations;
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
