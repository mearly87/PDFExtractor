package com.odc.pdfextractor.transaction;

import com.odc.pdfextractor.transaction.Transaction.TransactionType;

public class BalanceHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, String header, String data)
  {
    String cleanData = data.trim();
    if (!cleanData.isEmpty()) {
      if (trans.getType() == null) {
        trans.setType(TransactionType.BALANCE);
      }
      trans.setBalance(data);
    }
  }

}
