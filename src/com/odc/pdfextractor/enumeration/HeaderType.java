package com.odc.pdfextractor.enumeration;

import com.odc.pdfextractor.table.cloumn.handler.AmountHandler;
import com.odc.pdfextractor.table.cloumn.handler.BalanceHandler;
import com.odc.pdfextractor.table.cloumn.handler.CheckNumberHandler;
import com.odc.pdfextractor.table.cloumn.handler.ColumnHandler;
import com.odc.pdfextractor.table.cloumn.handler.CreditHandler;
import com.odc.pdfextractor.table.cloumn.handler.DateHandler;
import com.odc.pdfextractor.table.cloumn.handler.DebitHandler;
import com.odc.pdfextractor.table.cloumn.handler.DescriptionHandler;
import com.odc.pdfextractor.table.cloumn.handler.UnknownHandler;

public enum HeaderType {
    DATE(new DateHandler()),
    CHECK_NUMBER(new CheckNumberHandler()),
    DEBIT(new DebitHandler()),
    CREDIT(new CreditHandler()),
    BALANCE(new BalanceHandler()),
    DESCRIPTION(new DescriptionHandler()), 
    AMOUNT(new AmountHandler()),
    UNKOWN(new UnknownHandler());
    private ColumnHandler handler;
    
    HeaderType(ColumnHandler handler) {
      this.setHandler(handler);
    }

	public ColumnHandler getHandler() {
		return handler;
	}

	public void setHandler(ColumnHandler handler) {
		this.handler = handler;
	}
  }