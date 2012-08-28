package com.odc.pdfextractor.transaction;

public class DescriptionHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, String header, String data)
  {
    trans.setDescription(data);
  }

}
