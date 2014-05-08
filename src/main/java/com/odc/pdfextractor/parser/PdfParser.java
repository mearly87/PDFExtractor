package com.odc.pdfextractor.parser;

import com.odc.pdfextractor.model.DocumentLocation;

public interface PdfParser
{

  DocumentLocation processPdf(String filename) throws Exception;

}
