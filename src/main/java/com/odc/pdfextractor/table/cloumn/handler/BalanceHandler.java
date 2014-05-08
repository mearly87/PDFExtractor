package com.odc.pdfextractor.table.cloumn.handler;

import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.model.Transaction;
import com.odc.pdfextractor.model.Transaction.TransactionType;

public class BalanceHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, StringLocation data)
  {
    String cleanData = data.toString().trim();
    if (!cleanData.isEmpty()) {
      if (trans.getType() == null) {
        trans.setType(TransactionType.BALANCE);
      }
      trans.setBalance(cleanData);
    }
  }

}
