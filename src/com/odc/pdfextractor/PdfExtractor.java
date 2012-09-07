package com.odc.pdfextractor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import com.odc.pdfextractor.model.Transaction;
import com.odc.pdfextractor.model.builder.SummaryBuilder;
import com.odc.pdfextractor.model.builder.TransactionBuilder;


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
    
    TransactionBuilder tranBuilder = new TransactionBuilder(filename);
    List<Transaction> transactions = tranBuilder.getTransactionList();
    SummaryBuilder sumBuilder = new SummaryBuilder(transactions);
    sumBuilder.createSummary();
  }
  
}
