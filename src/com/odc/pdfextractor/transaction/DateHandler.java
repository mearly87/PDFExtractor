package com.odc.pdfextractor.transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateHandler implements ColumnHandler
{

  @Override
  public void handleColumn(Transaction trans, String header, String data)
  {
	  if (trans.getDate() != null) {
		  return;
	  }
    try
    {
     Calendar cal = Calendar.getInstance();

     Date date = determineDateFormat(data.trim());
     trans.setDate(date);
    }
    catch (ParseException e)
    {
      trans.setDate(new Date(0));
    }
  }

  @SuppressWarnings("serial")
  private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
    put("^\\d{1,2}/\\d{1,2}", "M/d");
    put("[a-z]{3}[0-9]{1,2}", "MMMd");
    put("[a-z]{3}\\s[0-9]{1,2}", "MMM d");
    
}};

/**
 * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
 * format is unknown. You can simply extend DateUtil with more formats if needed.
 * @param dateString The date string to determine the SimpleDateFormat pattern for.
 * @return The matching SimpleDateFormat pattern, or null if format is unknown.
 * @throws ParseException 
 * @see SimpleDateFormat
 */
public Date determineDateFormat(String dateString) throws ParseException {
	dateString = dateString.replaceAll("\\s+", " ").trim().toLowerCase();
    for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
        if (dateString.toLowerCase().matches(regexp)) {
        	String dateFormat = DATE_FORMAT_REGEXPS.get(regexp) + " yy";
          SimpleDateFormat format = new SimpleDateFormat(dateFormat);
          return format.parse(dateString + " 12");
        }
    }
    return null; // Unknown format.
}
  
  
}
