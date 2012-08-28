package com.odc.pdfextractor.transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ibm.icu.text.SimpleDateFormat;
import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.model.helper.TestStringLocation;
import com.odc.pdfextractor.transaction.Transaction.TransactionType;

public class TransactionBuilderTest
{
  
  Map<StringLocation, StringLocation> dataMap = new HashMap<StringLocation, StringLocation>();
  
  @Before
  public void setup() {
    dataMap.put(new TestStringLocation("Debit"), null);
    dataMap.put(new TestStringLocation("Credit"), null);
    dataMap.put(new TestStringLocation("Balance"), null);
    dataMap.put(new TestStringLocation("Description"), null);
  }
  
  @Test
  public void unknownTransactionIsMarkedUknown() throws ParseException {
    Transaction trans = TransactionBuilder.getTransaction(dataMap);
    assertNotNull(trans);
    assertEquals(TransactionType.UNKNOWN, trans.getType());
  }
  
  @Test
  public void transactionWithDebitColumnFilledInIsDebit() throws ParseException {
    dataMap.put(new TestStringLocation("Debit"), new TestStringLocation("1,000"));
    Transaction trans = TransactionBuilder.getTransaction(dataMap);
    assertNotNull(trans);
    assertEquals(TransactionType.DEBIT, trans.getType());
  }
  
  @Test
  public void transactionWithDebitColumnFilledHasCorrectAmount() throws ParseException {
    dataMap.put(new TestStringLocation("Debit"), new TestStringLocation("1,000.01"));
    Transaction trans = TransactionBuilder.getTransaction(dataMap);
    assertNotNull(trans);
    assertEquals(new Double(1000.01), trans.getAmount());
  }
  
  @Test
  public void transactionWithCreditColumnFilledInIsCredit() throws ParseException {
    dataMap.put(new TestStringLocation("Credit"), new TestStringLocation("1,000.01"));
    Transaction trans = TransactionBuilder.getTransaction(dataMap);
    assertNotNull(trans);
    assertEquals(TransactionType.CREDIT, trans.getType());
  }
  
  @Test
  public void transactionWithCreditColumnFilledHasCorrectAmount() throws ParseException {
    dataMap.put(new TestStringLocation("Credit"), new TestStringLocation("1,000.01"));
    Transaction trans = TransactionBuilder.getTransaction(dataMap);
    assertNotNull(trans);
    assertEquals(new Double(1000.01), trans.getAmount());
  }
  
  @Test
  public void transactionWithDescriptionInMapHasDescription() throws ParseException {
    dataMap.put(new TestStringLocation("Description"), new TestStringLocation("This is a transcation"));
    Transaction trans = TransactionBuilder.getTransaction(dataMap);
    assertNotNull(trans);
    assertEquals(trans.getDescription(), "This is a transcation");
  }
  
  @Test
  public void transactionWithDateInMapHasDate() throws ParseException {
    dataMap.put(new TestStringLocation("Date"), new TestStringLocation("11/2"));
    Transaction trans = TransactionBuilder.getTransaction(dataMap);
    assertNotNull(trans);
    SimpleDateFormat formatter = new SimpleDateFormat("M/d"); 
    assertEquals(formatter.parse("11/2"), trans.getDate());
  }

}
