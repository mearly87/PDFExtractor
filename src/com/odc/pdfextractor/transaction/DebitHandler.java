package com.odc.pdfextractor.transaction;

import com.odc.pdfextractor.transaction.Transaction.TransactionType;

public class DebitHandler extends AbstractHandler
{

  @Override
  public void handleColumn(Transaction trans, String header, String data)
  {
    String cleanData = data.trim();
    if (!cleanData.isEmpty()) {
      trans.setType(TransactionType.DEBIT);
      trans.setAmount(data);
    }
  }

}
