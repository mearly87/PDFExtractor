package com.odc.pdfextractor.table.cloumn.handler;

import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.model.Transaction;
import com.odc.pdfextractor.model.Transaction.TransactionType;

public class CreditHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, StringLocation data)
  {
    String cleanData = data.toString().trim();
    if (!cleanData.isEmpty()) {
     if ( trans.setAmount(cleanData) ) {
         trans.setType(TransactionType.CREDIT);
     }
    }
  }

}
