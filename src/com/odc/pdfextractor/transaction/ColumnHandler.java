package com.odc.pdfextractor.transaction;

public interface ColumnHandler
{
  
  void handleColumn(Transaction trans, String header, String data);
  
}
