package com.odc.pdfextractor.transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

public class Transaction
{
  public enum TransactionType {
    BALANCE,
    DEBIT,
    CREDIT,
    CHECK,
    UNKNOWN
  }
  
  private TransactionType type;
  private Double amount;
  private Double balance;
  private Date date;
  private String description;
  
  public TransactionType getType()
  {
    return type;
  }
  public void setType(TransactionType type)
  {
    this.type = type;
  }
  public Double getAmount()
  {
    return amount;
  }
  public void setAmount(Double amount)
  {
    this.amount = amount;
  }
  
  public boolean setAmount(String amount)
  {
    String amountStriped = amount.replaceAll("[^0-9|.]", "");
    try {
      this.amount = Double.parseDouble(amountStriped);
      return true;
    } catch(Exception s) {
      return false;
    }
  }
  
  public Double getResultingBalance()
  {
    return balance;
  }
  public void setResultingBalance(Double resultingBalance)
  {
    this.balance = resultingBalance;
  }
  public Date getDate()
  {
    return date;
  }
  
  public String getDateString()
  {
    if(date != null) {
      SimpleDateFormat format = new SimpleDateFormat("MM/dd");
      return format.format(date);
    }
    return "0/00";
  }
  
  public void setDate(Date date)
  {
    this.date = date;
  }
  public String getDescription()
  {
    return description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String toString() {
    StringBuilder result = new StringBuilder();
    Formatter formatter = new Formatter(result);
    formatter.format("%-5.5s %-20.20s", getDateString(), "TYPE: " + type);
    if(amount!=null) {
      formatter.format("%-25.25s", "AMOUNT: " + amount);
    } else {
      formatter.format("%-5s %s %-17s", "", "-", "");
    }
    if(balance!=null) {
      formatter.format("%-25.25s", "BALANCE: " + balance);
    } else {
      formatter.format("%-5s %s %-17s", "", "-", "");
    }
    formatter.format("%s", "DESCRIPTION: " + description);
    return result.toString();
  }
  public void setBalance(String balance)
  {
    String amountStriped = balance.replaceAll("[^0-9|.]", "");
    try {
    this.balance = Double.parseDouble(amountStriped);
    } catch (NumberFormatException e) {
    	System.out.println("Error parsing amount");
    }
  }
  
  
}
