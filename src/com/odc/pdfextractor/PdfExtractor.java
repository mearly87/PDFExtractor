package com.odc.pdfextractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.model.Transaction;
import com.odc.pdfextractor.model.Transaction.TransactionType;
import com.odc.pdfextractor.parser.CleanPdfParser;
import com.odc.pdfextractor.parser.DirtyPdfParser;
import com.odc.pdfextractor.parser.PdfParser;
import com.odc.pdfextractor.table.TableClassifier;
import com.odc.pdfextractor.table.TableClassifier.TableType;
import com.odc.pdfextractor.table.cloumn.ColumnHeader;


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
    List<Transaction> transactions = getTransactionList(filename);
    Calendar endTime = Calendar.getInstance();
    long end = endTime.getTimeInMillis();
    System.out.println("Retrieved in " + (double) ((end - start)) / 1000 + " seconds");
    System.out.println("# of Transactions: " + transactions.size());
    // TODO
    SimpleDateFormat simpleDateformat = new SimpleDateFormat("MMM");
    Map<String, Map<TransactionType, List<Transaction>>> transByMonth = new HashMap<String, Map<TransactionType, List<Transaction>>>();
    for (Transaction trans : transactions) {
    	System.out.println(trans);
    	if (trans.getDate() != null) {
	    	String month = simpleDateformat.format(trans.getDate());
	    	if (!transByMonth.containsKey(month)) {
	    		Map<TransactionType, List<Transaction>> transTypeMap = new HashMap<TransactionType, List<Transaction>>();
	    		for (TransactionType transType : TransactionType.values())
	    			transTypeMap.put(transType, new ArrayList<Transaction>());
	    		transByMonth.put(month, transTypeMap);
	    	}
	    	transByMonth.get(month).get(trans.getType()).add(trans);
    	}
    }

    for (String key : transByMonth.keySet()) {
    	System.out.println("### " + key + " ###");

		for (TransactionType transType : TransactionType.values()) {
			System.out.print(transType + ": " + transByMonth.get(key).get(transType).size());
			double total = 0.0;
			double balance = 0.0;
	    	Calendar prevBalanceDate = null;
	    	Calendar currBalanceDate = null;
	    	Transaction lastTransaction = null;
			for (Transaction t : transByMonth.get(key).get(transType)) {
				if (t.getAmount() != null)
					total += t.getAmount();
				if (t.getResultingBalance() != null) {
					prevBalanceDate = currBalanceDate;
					currBalanceDate = Calendar.getInstance();
					currBalanceDate.setTime(t.getDate());
					if (prevBalanceDate != null ) {
						try {
						balance += lastTransaction.getResultingBalance() * (currBalanceDate.get(Calendar.DAY_OF_MONTH) - prevBalanceDate.get(Calendar.DAY_OF_MONTH));
						} catch (NullPointerException e) {
							
						}
					}
				}
				lastTransaction = t;
			}
			System.out.print(" Total: " + total);
			if (currBalanceDate != null) {
				try {
					balance += lastTransaction.getResultingBalance() * (currBalanceDate.getActualMaximum(Calendar.DAY_OF_MONTH) + 1 - currBalanceDate.get(Calendar.DAY_OF_MONTH));
				} catch (NullPointerException e) {
					
				}
				System.out.print(" Average Balance: " + balance / currBalanceDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
			System.out.println();
		}
    }
  }
  
  public static List<Transaction> getTransactionList(String filename) throws IOException, Exception
  {
    PdfParser converter;
    if (filename.toLowerCase().endsWith(".pdf")) {
      converter = new CleanPdfParser();

    } else {
      converter = new DirtyPdfParser();
    }
    DocumentLocation doc = converter.processPdf(filename);
    System.out.println(doc);
    converter = null;
    
    List<StringLocation> dateLocations = (doc).applyRegEx(Constants.dateRegEx);
    List<StringLocation> groupedDates = StringLocationHelper.combineSimilarItems(dateLocations, 0);
    List<StringLocation> dateCols = StringLocationHelper.getDateColumns(doc, groupedDates);

    List<Transaction> transactions = new ArrayList<Transaction>();
    
    for (StringLocation dateColumn : dateCols) {
      StringLocation headers = StringLocationHelper.getHeaders(doc, dateColumn);
      if (headers != null) {

        
        Map<ColumnHeader, StringLocation> headerToDataCol = StringLocationHelper.getHeaderToDataMap(doc, dateColumn, headers);
        TableType tableName = getTableName(doc, headers, headerToDataCol);
        List<StringLocation> dates = dateColumn.getLocations();
        transactions.addAll(StringLocationHelper.getTransactionList(tableName, headerToDataCol, dates));
      }
    }
    return transactions;
  }

private static TableType getTableName(DocumentLocation doc,
		StringLocation headers, Map<ColumnHeader, StringLocation> headerToDataCol) {
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
