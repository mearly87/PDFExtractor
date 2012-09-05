package com.odc.pdfextractor.transaction;

import com.odc.pdfextractor.transaction.Transaction.TransactionType;

public class CreditHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, String header, String data)
  {
    String cleanData = data.trim();
    if (!cleanData.isEmpty()) {
     if ( trans.setAmount(data) ) {
         trans.setType(TransactionType.CREDIT);
     }
    }
  }

}
