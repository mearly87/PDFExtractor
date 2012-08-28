package com.odc.pdfextractor.transaction;

import java.text.SimpleDateFormat;
import java.util.Date;

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
  private Double resultingBalance;
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
  
  public void setAmount(String amount)
  {
    String amountStriped = amount.replaceAll("[^0-9|.]", "");
    this.amount = Double.parseDouble(amountStriped);
  }
  
  public Double getResultingBalance()
  {
    return resultingBalance;
  }
  public void setResultingBalance(Double resultingBalance)
  {
    this.resultingBalance = resultingBalance;
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
    return getDateString() + "\tType: " + type.toString() + "\tAmount: " + amount + "\t\tDescription: " + description;
  }
  
  
}
