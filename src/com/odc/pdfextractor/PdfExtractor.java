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
import com.odc.pdfextractor.parser.CleanPdfParser;
import com.odc.pdfextractor.parser.DirtyPdfParser;
import com.odc.pdfextractor.parser.PdfParser;
import com.odc.pdfextractor.transaction.Transaction;
import com.odc.pdfextractor.transaction.Transaction.TransactionType;
import com.odc.pdfextractor.transaction.TransactionBuilder;


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
    List<Map<StringLocation, StringLocation>> transMap = getTransactions(filename);
    Calendar endTime = Calendar.getInstance();
    long end = endTime.getTimeInMillis();
    System.out.println("Retrieved in " + (double) ((end - start)) / 1000 + " seconds");
    List<Transaction> transactions = new ArrayList<Transaction>();
    for (Map<StringLocation, StringLocation> trans : transMap) {
      transactions.add(TransactionBuilder.getTransaction(trans));
    }
    // TODO
    SimpleDateFormat simpleDateformat = new SimpleDateFormat("MMM");
    Map<String, Map<TransactionType, List<Transaction>>> transByMonth = new HashMap<String, Map<TransactionType, List<Transaction>>>();
    for (Transaction trans : transactions) {
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
    	System.out.print(key + " ");

		for (TransactionType transType : TransactionType.values()) {
			System.out.print(transType + ": " + transByMonth.get(key).get(transType).size());
			double total = 0.0;
			double balance = 0.0;
			int bCount = 0;
			for (Transaction t : transByMonth.get(key).get(transType)) {
				if (t.getAmount() != null)
					total += t.getAmount();
				if (t.getResultingBalance() != null) {
					balance += t.getResultingBalance();
					bCount++;
				}
			}
			System.out.print(" Total: " + total);
			if (bCount > 0) {
				System.out.println(" Average Balance: " + balance / bCount);				
			}
			System.out.println();
		}
    }
  }

  private static List<Map<StringLocation, StringLocation>> getTransactions(String filename) throws IOException, Exception
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
    List<StringLocation> groupedDates = StringLocationHelper.combineInlineItems(dateLocations, 0);
    List<StringLocation> dateCols = StringLocationHelper.getDateColumns(doc, Constants.dateRegEx, groupedDates);

    List<Map<StringLocation, StringLocation>> transactions = new ArrayList<Map<StringLocation, StringLocation>>();
    
    for (StringLocation dateColumn : dateCols) {
      StringLocation headers = StringLocationHelper.getHeaders(doc, dateColumn);
      if (headers != null) {
        StringLocation tableName = doc.getLocation(headers.getPage(), headers.getTop() - 25, headers.getTop(), Location.ALIGNMENT.bottom);
        
        Map<StringLocation, StringLocation> headerToDataCol = StringLocationHelper.getHeaderToDataMap(doc, dateColumn, headers);
        List<StringLocation> dates = dateColumn.getLocations();
        transactions.addAll(StringLocationHelper.getTransactions(tableName, headerToDataCol, dates));
      }
    }
    System.out.println("# of Transactions: " + transactions.size());
    return transactions;
  }
}
