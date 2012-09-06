package com.odc.pdfextractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.odc.pdfextractor.Location.ALIGNMENT;
import com.odc.pdfextractor.comparator.LeftToRightComparator;
import com.odc.pdfextractor.comparator.StartsAfterVerticallyComparator;
import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.model.Transaction;
import com.odc.pdfextractor.model.Transaction.TransactionType;
import com.odc.pdfextractor.table.TableClassifier.TableType;
import com.odc.pdfextractor.table.cloumn.ColumnHeader;
import com.odc.pdfextractor.table.cloumn.HeaderMap;
import com.odc.pdfextractor.table.cloumn.handler.ColumnHandler;

public class StringLocationHelper
{
  
  public static List<Transaction> getTransactionList(TableType tableType,
	      Map<ColumnHeader, StringLocation> headerToDataCol, List<StringLocation> dates)
	  {
	    List<Transaction> transactions = new ArrayList<Transaction>();
	    
	    for (int i = 2; i <= dates.size(); i++) {
	      int lower = dates.get(i - 1).getTop();
	      int upper = Integer.MAX_VALUE;
	      if (i < dates.size()) {
	        upper = dates.get(i).getTop() - 1;
	      }
	      
	      Transaction trans = new Transaction();
	      trans.setType(tableType);
	      for (ColumnHeader header : headerToDataCol.keySet()) {
	        StringLocation transDataCol = headerToDataCol.get(header);
	        StringLocation transData = ((StringLocation) transDataCol).getLocation(lower, upper, Location.ALIGNMENT.verticalCenter);
	        ColumnHandler handler = header.getHandler();
	        if(transData != null) {
	          handler.handleColumn(trans, transData);
	        } 
	      }
	      
	      if (trans.getAmount() == null && trans.getResultingBalance() != null) {
	      	trans.setType(TransactionType.BALANCE);
	      }


	      if (trans.getType() == null) {
	        trans.setType(TransactionType.UNKNOWN);
	      }
	      // System.out.println(trans);
	      transactions.add(trans);
	    }
	    return transactions;
	  }
  
  

  public static Map<ColumnHeader, StringLocation> getHeaderToDataMap(DocumentLocation doc, StringLocation dateColumn,
      StringLocation headers)
  {
    Map<ColumnHeader, StringLocation> headerToDataCol = new HashMap<ColumnHeader, StringLocation>();
    StringLocation data = doc.getLocation(dateColumn.getPage(), dateColumn.getTop(), dateColumn.getBottom(), Location.ALIGNMENT.verticalCenter);
    Map<String, ColumnHeader> keywordMap = HeaderMap.INSTANCE;
    data = data.getLocationUnder(headers);
    for (StringLocation header : headers.getLocations()) {
     StringLocation colDataLocation;
     if (keywordMap.containsKey(header)) {
    	 ColumnHeader headerType = keywordMap.get(header);
    	if (headerType == ColumnHeader.DATE) {
    	  colDataLocation = dateColumn;	 
     	} else if (headerType == ColumnHeader.AMOUNT || headerType == ColumnHeader.DEBIT || headerType == ColumnHeader.CREDIT || headerType == ColumnHeader.BALANCE) {
    	  colDataLocation = data.getLocationUnder(header, Constants.amountRegEx);
     	} else if (headerType == ColumnHeader.CHECK_NUMBER) {
    	  colDataLocation = data.getLocationUnder(header);
     	} else {
     		colDataLocation = data.getLocationUnder(header);
     	}
    	headerToDataCol.put(headerType, colDataLocation);
     }
    }
    return headerToDataCol;
  }

  public static StringLocation getHeaders(DocumentLocation data, StringLocation dateColumn) {

    List<StringLocation> dateLocs = dateColumn.getLocations();
    StringLocation dateHeader = dateLocs.get(0);
    StringLocation firstDate = dateLocs.get(0);
    if (dateLocs.size() > 1)
      firstDate = dateLocs.get(1);
    for (StringLocation l : data.getLocations()) {
      if (dateHeader.getPage() != -1 && l.getPage() == dateHeader.getPage()) {
        List<StringLocation> result = l.getLocations(dateHeader.getTop() - 1, firstDate.getTop() - 1, ALIGNMENT.bottom);
        List<StringLocation> validHeaders = HeaderMap.getValidHeaders(result);
        List<StringLocation> headerSets = StringLocationHelper.getHeaderSets(validHeaders, 0);
        for (StringLocation headerSet : headerSets) {
          if (dateColumn.intersects(headerSet)) {
        	return headerSet;
          }
        }
      }
    }
    return null;
  }

  public static List<StringLocation> getDateColumns(DocumentLocation doc, List<StringLocation> groupedDates)
	  {
	    List<StringLocation> dateCols = new ArrayList<StringLocation>();
	    for (StringLocation loc : groupedDates) {
	      List<StringLocation> locs = doc.getLocationsInline(loc);
	      dateCols.addAll(getColumn(ColumnHeader.DATE, locs, loc));
	    }
	    return dateCols;
	  }
  
  public static List<StringLocation> getColumn(ColumnHeader headerType, List<StringLocation> possibleHeaders, StringLocation dataCol) {
	    List<StringLocation> dataCols = new ArrayList<StringLocation>();
	    StringLocation prevHeader = null;
	    StringLocation currHeader = null;
	    for (StringLocation header : possibleHeaders) {
		  if (HeaderMap.INSTANCE.get(header) == ColumnHeader.DATE) {
			prevHeader = currHeader;
			currHeader = header;
			if (prevHeader != null) {
			  StringLocation col = dataCol.getLocation(prevHeader, currHeader);
			  if (!col.empty())
				  dataCols.add(col);
			}
		  }
	    }
	    if (currHeader != null) {
	    	List<StringLocation> lastCol = dataCol.getLocationsUnder(currHeader);
	    	lastCol.add(0, currHeader);
	    	dataCols.add(new StringLocation(lastCol));
	    }
	    return dataCols;
	  }

  
  public static List<StringLocation> combineSimilarItems(List<StringLocation> items, int error) {
    List<StringLocation> result = new ArrayList<StringLocation>();
    Comparator<Location> isAfterComparator = new StartsAfterVerticallyComparator(error);
    Collections.sort(items, isAfterComparator);
    List<StringLocation> buffer = new ArrayList<StringLocation>();
    for (StringLocation loc : items) {
      if (buffer.size() == 0) {
        buffer.add(loc);
      } else {
        boolean isBefore = buffer.get(0).getRight() + error < loc.getLeft();
        boolean isAfter = buffer.get(0).getLeft() - error > loc.getRight();
        if (!isAfter && !isBefore && buffer.get(0).getPage() == loc.getPage()) {
          buffer.add(loc);
        }  else {
        result.add(new StringLocation(buffer));
        buffer = new ArrayList<StringLocation>();
        buffer.add(loc);
      }

      }
    }
    if (buffer.size() != 0) {
      result.add(new StringLocation(buffer));
    }
    return result;
  }
  
  public static List<StringLocation> getHeaderSets(List<StringLocation> items, int error) {
    List<StringLocation> results = new ArrayList<StringLocation>();
    Map<ColumnHeader, List<StringLocation>> organziedItems = groupHeaders(items);
    LeftToRightComparator comparator = new LeftToRightComparator();
    List<List<StringLocation>> headerSets = new ArrayList<List<StringLocation>>();
    for (List<StringLocation> headers : organziedItems.values()) {
    	int index = 0;
    	StringLocation  ptr = null; 
		Collections.sort(headers, comparator);	
		for (StringLocation header : headers) {
			if (ptr == null) {
				ptr = header;
			} else if (ptr.getRight() + 10 > header.getLeft()) {
				List<StringLocation> locs = new ArrayList<StringLocation>(ptr.getLocations());
				locs.add(header);
				ptr = new StringLocation(locs);
			} else {
				if (headerSets.size() <= index) {
					headerSets.add(new ArrayList<StringLocation>());
				}
				headerSets.get(index).add(ptr);
				index++;
				ptr = header;
			}
		}
		if (ptr != null) {
			if (headerSets.size() <= index) {
				headerSets.add(new ArrayList<StringLocation>());
			}
			headerSets.get(index).add(ptr);
		}
	}
    for (List<StringLocation> headerSet : headerSets) {
        Collections.sort(headerSet, comparator);
        results.add(new StringLocation(headerSet));
    }
    Collections.sort(results, comparator);
    return results;
  }



private static Map<ColumnHeader, List<StringLocation>> groupHeaders(
		List<StringLocation> items) {
	Map<ColumnHeader, List<StringLocation>> organziedItems = new HashMap<ColumnHeader, List<StringLocation>>();
    for (StringLocation loc : items) {
      if (!organziedItems.containsKey(HeaderMap.INSTANCE.get(loc))) {
    	organziedItems.put(HeaderMap.INSTANCE.get(loc), new ArrayList<StringLocation>());
      }
      organziedItems.get(HeaderMap.INSTANCE.get(loc)).add(loc);
    }
	return organziedItems;
}
  
}
