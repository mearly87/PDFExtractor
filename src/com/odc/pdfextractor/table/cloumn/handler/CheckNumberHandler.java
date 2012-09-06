package com.odc.pdfextractor.table.cloumn.handler;

import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.model.Transaction;
import com.odc.pdfextractor.model.Transaction.TransactionType;

public class CheckNumberHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, StringLocation data)
  {
    String cleanData = data.toString().trim();
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
