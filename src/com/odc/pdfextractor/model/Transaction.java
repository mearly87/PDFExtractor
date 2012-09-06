package com.odc.pdfextractor.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import com.odc.pdfextractor.table.TableClassifier.TableType;

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
  
  public void setType(TableType type) {
	  if (type == null) {
		  return;
	  }
    if (type == TableType.CREDIT) {
    	setType(TransactionType.CREDIT);
    } else if (type == TableType.DEBIT) {
    	setType(TransactionType.DEBIT);
    	if (amount != null && amount >=0) {
    		amount = amount * -1;
    	}
    } else if (type == TableType.BALANCE) {
    	setType(TransactionType.BALANCE);
    }	else if (type == TableType.CHECK) {
    	setType(TransactionType.CHECK);
    	if (amount != null && amount >=0) {
    		amount = amount * -1;
    	}
    }
  }
  
  public Double getAmount()
  {
    return amount;
  }
  
  public boolean setAmount(String amount)
  {
	  int sign = 1;
	  if (amount.contains("-") || type == TransactionType.DEBIT || type == TransactionType.CHECK) {
		  sign = -1;
	  }
	  // Get rid of extra stuff, and only accept the first number in the amount string
    String amountStriped = amount.split(" ")[0].replaceAll("[^0-9|.]", "");
    try {
      this.amount = sign * Double.parseDouble(amountStriped);
      if (getType() == TransactionType.BALANCE) {
	      	if (getResultingBalance() == null) {
	      		setResultingBalance(getAmount());
	      	}
	      }
      return true;
    } catch(Exception s) {
     System.out.println(amountStriped);
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
