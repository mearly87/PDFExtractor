package com.odc.pdfextractor.table.cloumn.handler;

import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.model.Transaction;

public interface ColumnHandler
{
  
  void handleColumn(Transaction trans, StringLocation data);
  
}
