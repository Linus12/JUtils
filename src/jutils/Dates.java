package jutils;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * The Dates class brings together some common date conversion functionality
 * with error checking. Nothing is stored, but conversions between short 
 * formats (yyyy-MM-dd) and the Java Date class are provided.
 * 
 */

public class Dates {

    private static final String  version     = "Version 03.00";
    private static final String  dFlag       = DebugInfo.DB_DATES;
    private static final String  dPreFix     = "Dates:";

    static public   final String  DATE_FMT   = "yyyy-MM-dd"; //14.60
    static public   final String  DATE_FULL  = "yyyy-MM-dd HH:mm:ss";
    static public   final String  DATE_SEP   = "-";
    
    // support classes
    private Errs     err       = new Errs();       // for returning errors 
    private Trace    TR;                           // passed in constructor

    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------
    /**
     * Constructor for Dates, a collection of date transformation methods
     * 
     * @param tr instantiated Trace class for tracing debugging errors
     */
	public Dates(Trace tr) {
    	TR       = tr;  

	}

    //-------------------------------------------------------------------------
    //  Error routines
    //-------------------------------------------------------------------------
    /**
     * Returns the most recent error information 
     * 
     * @return Errs class containing the most recent error information
     */
    public Errs getErrs() {
        return new Errs(err);
    }

    //-------------------------------------------------------------------------
    // Date value from String Date
    //-------------------------------------------------------------------------
    /**
     * Converts a string date to a Date value
     * 		returns the date value, if it is invalid, returns null
     *  
     * @param indate string value of date to be converted
     * @return Date value of the converted date string, or null if error
     */
    public Date dateFromString(String indate) {
        err.initErrs();

        TR.debugMsgln(dFlag, dPreFix,
           		      "----Parsing String input date:  " +  indate);

        if (indate == null || indate.isEmpty() || indate.trim().isEmpty()) {
            err.eNbr     = 9800;
            err.eMessage = "Date string cannot be null or empty";
            err.eType    = Errs.ETYPE_ERROR;
            TR.debugErr(dFlag, dPreFix, err);
            return null;
        }

        if (indate.length() != 10 ||
        	!indate.substring(4,5).equals(DATE_SEP) ||
        	!indate.substring(7,8).equals(DATE_SEP)   ) {
            err.eNbr     = 9810;
            err.eMessage = "Date string must be in the format:  " + DATE_FMT;
            err.eType    = Errs.ETYPE_ERROR;
            TR.debugErr(dFlag, dPreFix, err);
            return null;
        	
        }
        	
        Date date1;
        try {
            date1 = new SimpleDateFormat(DATE_FMT).parse(indate);
        } catch (ParseException e1) {
            err.eNbr     = 9820;
            err.eMessage = "Invalid date string; Must be in the format:  " + DATE_FMT;
            err.eType    = Errs.ETYPE_ERROR;
            TR.debugErr(dFlag, dPreFix, err);
            return null;
        }
 
        TR.debugMsgln(dFlag, dPreFix, 
        		      "                 Output Value:  " +  date1 );

        return date1;

    }

    //-------------------------------------------------------------------------
    // String Date value from Date Value
    //-------------------------------------------------------------------------
    /**
     * converts a Date to a string date;  
     * returning the date value as a string.<br> 
     * If the date is null, returns an empty string<br>
     * If the date invalid, returns null<br>
     *  
     * @param inDate Date to be converted
     * @return String value of the input Date
     */
    public String dateToString(Date inDate) {
        err.initErrs();

        TR.debugMsgln(dFlag, dPreFix, 
        		      "----Parsing Date  input value:  " +  inDate);

        if (inDate == null) {
            err.eNbr     = 9830;
            err.eMessage = "Invalid Date value; cannot be null";
            err.eType    = Errs.ETYPE_ERROR;
            TR.debugErr(dFlag, dPreFix, err);
            return "";
        }

        SimpleDateFormat datefmt = new SimpleDateFormat(DATE_FMT);
        String dateS;
        try {
            dateS = datefmt.format(inDate);
        } catch (Exception e1) {
            err.eNbr     = 9840;
            err.eMessage = "Invalid Date value; Must be a valid date to convert";
            err.eType    = Errs.ETYPE_ERROR;
            TR.debugErr(dFlag, dPreFix, err);
            return "";
        }

        TR.debugMsgln(dFlag, dPreFix, 
  		              "                 Output Value:  " +  dateS );


        return dateS;

    }

    //-------------------------------------------------------------------------
    // Full String Date value from Date Value
    //-------------------------------------------------------------------------
    /**
     * converts a Date to a string date;  
     * returning the date value as a string.<br> 
     * If the date is null, returns an empty string<br>
     * If the date invalid, returns null<br>
     * This returns the date and time, resolution based on the input date
     *  
     * @param inDate Date to be converted
     * @return String value of the input Date
     */
    public String dateToFullString(Date inDate) {
        err.initErrs();

        TR.debugMsgln(dFlag, dPreFix, 
        		      "----Parsing Date  input value:  " +  inDate);

        if (inDate == null) {
            err.eNbr     = 9850;
            err.eMessage = "Invalid Date value; cannot be null";
            err.eType    = Errs.ETYPE_ERROR;
            TR.debugErr(dFlag, dPreFix, err);
            return "";
        }

        SimpleDateFormat datefmt = new SimpleDateFormat(DATE_FULL);
        String dateS;
        try {
            dateS = datefmt.format(inDate);
        } catch (Exception e1) {
            err.eNbr     = 9860;
            err.eMessage = "Invalid Date value; Must be a valid date to convert";
            err.eType    = Errs.ETYPE_ERROR;
            TR.debugErr(dFlag, dPreFix, err);
            return "";
        }

        TR.debugMsgln(dFlag, dPreFix, 
  		              "                 Output Value:  " +  dateS );


        return dateS;

    }

    //-------------------------------------------------------------------------
    // Calendar routines
    //-------------------------------------------------------------------------
    /**
     * Returns a date value equal to the number of months added in the parameter.
     * parameter can be positive or negative.  
     * If the input date is null, returns a null date<br>
     * If the date invalid, returns null<br>
     *  
     * @param inDate  starting Date value
     * @param months  integer number of months to add (positive or negative)
     * @return Date value of the resulting date
     */
    public Date addMonths(Date inDate, int months) {
    	err.initErrs();
    	
        TR.debugMsgln(dFlag, dPreFix, 
  		      "----Add Months  input values:  " +  inDate + "\t" + months);

        if (inDate == null) {
            err.eNbr     = 9870;
            err.eMessage = "Invalid Date value; cannot be null";
            err.eType    = Errs.ETYPE_ERROR;
            TR.debugErr(dFlag, dPreFix, err);
            return null;
        }

        Calendar cal = Calendar.getInstance();
    	cal.setTime(inDate);
    	cal.add(Calendar.MONTH, months);
    	
    	Date retVal = cal.getTime();
        TR.debugMsgln(dFlag, dPreFix, "    addMonths return:  " +  retVal );
        return   retVal;
    }

    /**
     * Returns a date value equal to the current Date plus the number of
     * months passed as an integer.
     * Parameter can be positive or negative.  
     *  
     * @param months  integer number of months to add (positive or negative) to
     *                today's date
     * @return Date value of the resulting date
     */
    public Date inactiveDate(int months) {
    	err.initErrs();
    	
    	Date   retDate  = new Date();  // Today's date
    	
    	// strip off the seconds and convert it back to a date
    	String todayS = dateToString(retDate);
        retDate = dateFromString(todayS);
        
    	retDate  = addMonths(retDate, months); // "add" the months

        TR.debugMsgln(dFlag, dPreFix, "    inactiveDate return:  " + retDate);

    	return retDate;
    }
    
    //-------------------------------------------------------------------------
    // Version Routines
    //-------------------------------------------------------------------------
   /**  Returns the version number of this class.
     *
     * @return string value of version number
     */
    static public String getVersion(){ 
        return version;
    }

    /**
     * Returns list of mapped pairs of class file names and the version
     * number. It also adds all called routines to the list. If the 
     * class file name and version is already in the list, it does not
     * add them, it simply returns.
     *
     * @param  vlist Map of the current class/versions to be appended to
     * @return Map of the class/version values for this and all called
     *                routines
     */
	public static LinkedHashMap<String, String> 
	         getAllVersions (LinkedHashMap<String, String> vlist) {
		String v = Dates.class.getName();
		// if it already exists, just return
		if (vlist.containsKey(v)) {
			return vlist;
		}
		//add it to the list
		vlist.put(v, getVersion());
		
		vlist = Errs.getAllVersions(vlist);
		vlist = Trace.getAllVersions(vlist);

    	return vlist;
    }



}