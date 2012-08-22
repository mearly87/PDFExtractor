package com.odc.pdfreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     // StringLocation colDataLocation = data.getLocation(header.getLeft() - 5, header.getRight() + 5, Location.ALIGNMENT.left);
     StringLocation colDataLocation = data.getLocationUnder(header);
     headerToDataCol.put(header, colDataLocation);
    }
    return headerToDataCol;
  }

  public static StringLocation getHeaders(DocumentLocation data, StringLocation dateColumn) {

    List<StringLocation> dateLocs = dateColumn.getLineLocations();
    Location dateHeader = dateLocs.get(0);
    Location firstDate = dateLocs.get(0);
    if (dateLocs.size() > 1)
      firstDate = dateLocs.get(1);
    return data.getUniqueLocation(dateHeader.getPage(), dateHeader.getTop() - 10, firstDate.getTop(), Location.ALIGNMENT.verticalCenter, dateHeader);
  }

  public static List<StringLocation> getDateColumns(DocumentLocation doc, String regEx,
      Map<Integer, List<StringLocation>> locsMap)
  {
    List<StringLocation> dateCols = new ArrayList<StringLocation>();
    for (List<StringLocation> page : locsMap.values()) {
      for (Location loc : page) {
        int left = loc.getLeft();
        List<StringLocation> locs = doc.getLocations(loc.getPage(), left - 10, left + 10, Location.ALIGNMENT.left);
        dateCols.addAll(cleanColumn("(D|d)(A|a)(T|t)(E|e)", regEx, locs));
      }
    }
    return dateCols;
  }

  public static void putInBucket(final Map<Integer, List<Location>> boxes, Location loc, int error, Location.ALIGNMENT alignment)
  {
    int key = -1;
    for (int i = 0; i < error; i++) {
      if (boxes.containsKey(loc.getPosition(alignment) - i)) {
        boxes.get(loc.getPosition(alignment) - i).add(loc);
        key = loc.getPosition(alignment) - i;
      }
      else if (boxes.containsKey(loc.getPosition(alignment) + i)){
        boxes.get(loc.getPosition(alignment) + i).add(loc);
        key = loc.getPosition(alignment) + i;
      }
    }
    if (key == -1) {
      key = loc.getPosition(alignment);
      List<Location> newLocationList = new ArrayList<Location>();
      newLocationList.add(loc);
      boxes.put(key, newLocationList);
    }
    if (key == loc.getPosition(alignment)) {
      return;
    }
    int lineNums = 0;
    for (Location l : boxes.get(key)) {
      lineNums += l.getPosition(alignment);
    }
    float average = ((float)lineNums)/boxes.get(key).size();
    int newKey = Math.round(average);
    if (newKey != key) {
      if(boxes.containsKey(newKey)) {
        System.out.println("KEY COLLISION");
        List<Location> oldBox = boxes.remove(key);
        List<Location> newBox = boxes.get(newKey);
        newBox.addAll(oldBox);
        return;
      }
  
      boxes.put(newKey, boxes.remove(key));
    }
  }

  public static void putInBucketByPage(final Map<Integer, StringLocation> boxes, StringLocation loc, int error, Location.ALIGNMENT alignment)
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
      List<Location> newLocationList = new ArrayList<Location>();
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
    StringLocation dateCol = null;
    for (StringLocation loc : locs) {
      Matcher headerMatcher = headerRegEx.matcher(loc.toString().trim());
      if (headerMatcher.matches()) {
        if (dateCol != null) {
          result.add(dateCol);
        }
        dateCol = new StringLocation(loc);
      }
      Matcher dataMatcher = dataRegEx.matcher(loc.toString());
      if (dataMatcher.find()) {
        if (dateCol != null) {
          dateCol.addLocation(loc);
        }
      }
    }
    if (dateCol != null) {
      result.add(dateCol);
    } 
    return result;
  }

  public static Map<Integer, List<Location>> groupItems(List<Location> locs, int error, Location.ALIGNMENT alignment) {
  Map<Integer, List<Location>> buckets = new HashMap<Integer, List<Location>>();
    for(Location loc : locs) {
      putInBucket(buckets, loc, error, alignment);
    }
    return buckets;
  }

  public static Map<Integer, List<StringLocation>> groupInlineItems(List<StringLocation> results, int error, Location.ALIGNMENT alignment)
  {
    Map<Integer, List<StringLocation>> locsByPage = new HashMap<Integer, List<StringLocation>>();
    Map<Integer, List<StringLocation>> locsMap = new HashMap<Integer, List<StringLocation>>();
    for (StringLocation l : results) {
      if (!locsByPage.containsKey(l.getPage())) {
        locsByPage.put(l.getPage(), new ArrayList<StringLocation>());
      }
      locsByPage.get(l.getPage()).add(l);
    }
    for (List<StringLocation> locs : locsByPage.values()) {
      Map<Integer, StringLocation> locMap = new HashMap<Integer, StringLocation>();
      for (StringLocation location : locs) {
        putInBucketByPage(locMap, location, error, alignment);
      }
      List<StringLocation> locations = new ArrayList<StringLocation>(locMap.values());
      locsMap.put(locs.get(0).getPage(), locations);
    }
    return locsMap;
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
