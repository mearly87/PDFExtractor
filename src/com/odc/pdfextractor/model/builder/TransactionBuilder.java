package com.odc.pdfextractor.model.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.odc.pdfextractor.Constants;
import com.odc.pdfextractor.classifier.HeaderClassifier;
import com.odc.pdfextractor.classifier.TableClassifier;
import com.odc.pdfextractor.comparator.LeftToRightComparator;
import com.odc.pdfextractor.comparator.StartsAfterVerticallyComparator;
import com.odc.pdfextractor.enumeration.HeaderType;
import com.odc.pdfextractor.enumeration.TableType;
import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.Location;
import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.model.Transaction;
import com.odc.pdfextractor.model.Location.ALIGNMENT;
import com.odc.pdfextractor.model.Transaction.TransactionType;
import com.odc.pdfextractor.parser.CleanPdfParser;
import com.odc.pdfextractor.parser.DirtyPdfParser;
import com.odc.pdfextractor.parser.PdfParser;
import com.odc.pdfextractor.table.cloumn.handler.ColumnHandler;

public class TransactionBuilder
{
  private final DocumentLocation doc;
  
  public TransactionBuilder(String filename) throws Exception {
	  PdfParser converter;
	    if (filename.toLowerCase().endsWith(".pdf")) {
	      converter = new CleanPdfParser();

	    } else {
	      converter = new DirtyPdfParser();
	    }
	    doc = converter.processPdf(filename);
	}

  public List<Transaction> getTransactionList() throws IOException, Exception
  {  
    List<StringLocation> dateLocations = (doc).applyRegEx(Constants.dateRegEx);
    List<StringLocation> groupedDates = combineSimilarItems(dateLocations, 0);
    List<StringLocation> dateCols = getDateColumns(groupedDates);

    List<Transaction> transactions = new ArrayList<Transaction>();
    
    for (StringLocation dateColumn : dateCols) {
      StringLocation headers = getHeaders(doc, dateColumn);
      if (headers != null) {

        
        Map<HeaderType, StringLocation> headerToDataCol = getHeaderToDataMap(dateColumn, headers);
        TableType tableName = getTableName(doc, headers, headerToDataCol);
        List<StringLocation> dates = dateColumn.getLocations();
        transactions.addAll(getTransactionList(tableName, headerToDataCol, dates));
      }
    }
    return transactions;
  }

private List<Transaction> getTransactionList(TableType tableType,
	      Map<HeaderType, StringLocation> headerToDataCol, List<StringLocation> dates)
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
	      for (HeaderType header : headerToDataCol.keySet()) {
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
	      transactions.add(trans);
	    }
	    return transactions;
	  }
  
  

  private Map<HeaderType, StringLocation> getHeaderToDataMap(StringLocation dateColumn,
      StringLocation headers)
  {
    Map<HeaderType, StringLocation> headerToDataCol = new HashMap<HeaderType, StringLocation>();
    StringLocation data = doc.getLocation(dateColumn.getPage(), dateColumn.getTop(), dateColumn.getBottom(), Location.ALIGNMENT.verticalCenter);
    Map<String, HeaderType> keywordMap = HeaderClassifier.INSTANCE;
    data = data.getLocationUnder(headers);
    for (StringLocation header : headers.getLocations()) {
     StringLocation colDataLocation;
     if (keywordMap.containsKey(header)) {
    	 HeaderType headerType = keywordMap.get(header);
    	if (headerType == HeaderType.DATE) {
    	  colDataLocation = dateColumn;	 
     	} else if (headerType == HeaderType.AMOUNT || headerType == HeaderType.DEBIT || headerType == HeaderType.CREDIT || headerType == HeaderType.BALANCE) {
    	  colDataLocation = data.getLocationUnder(header, Constants.amountRegEx);
     	} else if (headerType == HeaderType.CHECK_NUMBER) {
    	  colDataLocation = data.getLocationUnder(header);
     	} else {
     		colDataLocation = data.getLocationUnder(header);
     	}
    	headerToDataCol.put(headerType, colDataLocation);
     }
    }
    return headerToDataCol;
  }

  private StringLocation getHeaders(DocumentLocation data, StringLocation dateColumn) {

    List<StringLocation> dateLocs = dateColumn.getLocations();
    StringLocation dateHeader = dateLocs.get(0);
    StringLocation firstDate = dateLocs.get(0);
    if (dateLocs.size() > 1)
      firstDate = dateLocs.get(1);
    for (StringLocation l : data.getLocations()) {
      if (dateHeader.getPage() != -1 && l.getPage() == dateHeader.getPage()) {
        List<StringLocation> result = l.getLocations(dateHeader.getTop() - 1, firstDate.getTop() - 1, ALIGNMENT.bottom);
        List<StringLocation> validHeaders = HeaderClassifier.getValidHeaders(result);
        List<StringLocation> headerSets = getHeaderSets(validHeaders, 0);
        for (StringLocation headerSet : headerSets) {
          if (dateColumn.intersects(headerSet)) {
        	return headerSet;
          }
        }
      }
    }
    return null;
  }

  private List<StringLocation> getDateColumns(List<StringLocation> groupedDates)
	  {
	    List<StringLocation> dateCols = new ArrayList<StringLocation>();
	    for (StringLocation loc : groupedDates) {
	      List<StringLocation> locs = doc.getLocationsInline(loc);
	      dateCols.addAll(getColumn(HeaderType.DATE, locs, loc));
	    }
	    return dateCols;
	  }
  
  private List<StringLocation> getColumn(HeaderType headerType, List<StringLocation> possibleHeaders, StringLocation dataCol) {
	    List<StringLocation> dataCols = new ArrayList<StringLocation>();
	    StringLocation prevHeader = null;
	    StringLocation currHeader = null;
	    for (StringLocation header : possibleHeaders) {
		  if (HeaderClassifier.INSTANCE.get(header) == HeaderType.DATE) {
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

  
  private List<StringLocation> combineSimilarItems(List<StringLocation> items, int error) {
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
  
  private List<StringLocation> getHeaderSets(List<StringLocation> items, int error) {
    List<StringLocation> results = new ArrayList<StringLocation>();
    Map<HeaderType, List<StringLocation>> organziedItems = groupHeaders(items);
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



private Map<HeaderType, List<StringLocation>> groupHeaders(
		List<StringLocation> items) {
	Map<HeaderType, List<StringLocation>> organziedItems = new HashMap<HeaderType, List<StringLocation>>();
    for (StringLocation loc : items) {
      if (!organziedItems.containsKey(HeaderClassifier.INSTANCE.get(loc))) {
    	organziedItems.put(HeaderClassifier.INSTANCE.get(loc), new ArrayList<StringLocation>());
      }
      organziedItems.get(HeaderClassifier.INSTANCE.get(loc)).add(loc);
    }
	return organziedItems;
}



private TableType getTableName(DocumentLocation doc,
			StringLocation headers, Map<HeaderType, StringLocation> headerToDataCol) {
		TableType tableType = TableType.UNKNOWN;
		int search = 20;
			while (tableType == TableType.UNKNOWN && search < 50) {
				StringLocation tableName = doc.getLocation(headers.getPage(), headers.getTop() - search, headers.getTop(), Location.ALIGNMENT.bottom);
				tableType = TableClassifier.getTableType(tableName.toString(), headerToDataCol.keySet());
				search+= 5;
			}
		return tableType;
	}
  
}
