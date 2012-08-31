package com.odc.pdfextractor.transaction;

public abstract class AbstractHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, String header, String data)
  {
    data = data.trim();
    if (data.isEmpty()) {
      data = null;
    }

  }

}
