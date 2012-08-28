package com.odc.pdfextractor.transaction;

import com.odc.pdfextractor.transaction.Transaction.TransactionType;

public class DebitHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, String header, String data)
  {
    trans.setType(TransactionType.DEBIT);
    trans.setAmount(data);
  }

}
