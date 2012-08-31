package com.odc.pdfextractor.transaction;

import com.odc.pdfextractor.transaction.Transaction.TransactionType;

public class CheckNumberHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, String header, String data)
  {
    String cleanData = data.trim();
    if (!cleanData.isEmpty()) {
      trans.setType(TransactionType.CHECK);
      if (trans.getDescription() == null) {
        trans.setDescription("CHECK NO: " + data);
      } else {
        trans.setDescription("CHECK NO: " + data + ", " + trans.getDescription());
      }
    }   
  }

}
