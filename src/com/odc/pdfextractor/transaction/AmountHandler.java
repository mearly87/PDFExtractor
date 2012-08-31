package com.odc.pdfextractor.transaction;


public class AmountHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, String header, String data)
  {
    String cleanData = data.trim();
    if (!cleanData.isEmpty()) {
      trans.setAmount(data);
    }
  }

}
