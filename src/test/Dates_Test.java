

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import jutils.Dates;
import jutils.Errs;
import jutils.Trace;

public class Dates_Test {
	protected final static String    version      = "Version 14.00";
//TODO  validate tests that return with dates with actual dates, not the dates converted back to strings! This means converting expected date strings into actual dates before the comparisons
	// Class to be tested
	private Dates DR;   

	// test program variables
    private   Date    testDate     = null;
    private   Date    expDate      = null;

    // valid dates
	private   String  dateIn1      = "2014-06-01";
	private   String  dateOut1     = "Sun Jun 01 00:00:00 PDT 2014";
	private   long    timeOut1     = 1401606000L;
//	private   String  dateOut1P1   = "Tue Jul 01 00:00:00 PDT 2014";
	private   String  dateOut1P1   = "2014-07-01";
	private   long    timeOut1P1   = 1404198000L;
	
	private   String  dateIn2      = "2015-04-01";
	private   String  dateOut2     = "Wed Apr 01 00:00:00 PDT 2015";
	private   long    timeOut2     = 1427871600L;
//	private   String  dateOut2P13  = "Sun May 01 00:00:00 PDT 2016";
	private   String  dateOut2P13  = "2016-05-01";
	private   long    timeOut2P13  = 1462086000L;

	// strangely valid dates
	private   String  dateIn3      = "2015-12-32";
	private   String  dateOut3     = "Fri Jan 01 00:00:00 PST 2016";
	private   long    timeOut3     = 1451635200L;
//	private   String  dateOut3P1   = "Mon Feb 01 00:00:00 PST 2016";
	private   String  dateOut3P1   = "2016-02-01";
	private   long    timeOut3P1   = 1454313600L;

	private   String  dateIn4      = "2015-13-31";
	private   String  dateOut4     = "Sun Jan 31 00:00:00 PST 2016";
	private   long    timeOut4     = 1454227200L;
//	private   String  dateOut4P1   = "Mon Feb 29 00:00:00 PST 2016";//leap year
	private   String  dateOut4P1   = "2016-02-29";//leap year
	private   long    timeOut4P1   = 1456732800L;

	private   String  dateIn5      = "2015-15-31";
	private   String  dateOut5     = "Thu Mar 31 00:00:00 PDT 2016";
	private   long    timeOut5     = 1459407600L;
	
	private   String  dateIn6      = "2015-03-31";
	private   String  dateOut6     = "Tue Mar 31 00:00:00 PDT 2015";
	private   long    timeOut6     = 1427785200L;
//	private   String  dateOut6M1   = "Sat Feb 28 00:00:00 PST 2015";
	private   String  dateOut6M1   = "2015-02-28";
	private   long    timeOut6M1   = 1425110400L;
                                   //1500970411
	private   long    timeIn1      = 1521359159L;
	
	// error numbers and messages
	private   int     noEFlag      = Errs.ETYPE_NONE;
	private   int     errFlag      = Errs.ETYPE_ERROR;
	
	private   int     noError      = 0;
	private   String  noMessage    = "";
	private   int     errNbr00     = 9300;
	private   String  errMsg00     = "Date string cannot be null or empty";
	private   int     errNbr01     = 9310;
	private   String  errMsg01     = "Date string must be in the format:  " + Dates.DATE_FMT;
	private   int     errNbr02     = 9320;
	private   String  errMsg02     = "Invalid date string; Must be in the format:  " + Dates.DATE_FMT;
	private   int     errNbr03     = 9330;
	private   String  errMsg03     = "Invalid Date value; cannot be null";
	// the following error condition cannot be tested because it requires a corrupt date
	//private   int     errNbr04     = 9340;
	//private   String  errMsg04     = "Invalid Date value; Must be a valid date to convert";
	private   int     errNbr05     = 9350;
	private   String  errMsg05     = "Invalid Date value; cannot be null";
	
	private   String  dateSNull    = null;
	private   String  dateSEmpty   = "";
	private   String  dateSBlank   = "    ";
	private   Date    dateNull     = null;
	                               // 0123456789 
	private   String  dateInBad1   = "04-01-2015";
	private   String  dateInBad1a  = "2015-Apr-01";
	private   String  dateInBad1b  = "2015/04/01";
	private   String  dateInBad1c  = "Jan 03, 2015";

	private   String  dateInBad2   = "YYYY-MM-DD";
	private   String  dateInBad2a  = "2015-01-xx";
	
	private   String  dateOutBad   = null;
	private   long    timeOutBad   = 0L;


	// Support classes
	private Errs     err           = new Errs();
    private Trace    TR            = null;     // Trace for this class
    private Trace    TTR           = null;     // Trace for this Tested Class


	// make sure assertions are enabled 
	static {
		boolean assertsEnabled = false;
		assert assertsEnabled = true; // Intentional side effect!!!
		if (!assertsEnabled)
			throw new RuntimeException("Asserts must be enabled!!!");
	} 

	//-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------
	/**
	 * Constructor for Dates_Test, class for testing the Dates.java class
     * 
     * @param ti TraceInfo needed for tracing this test and the tested class
	 */
	public Dates_Test(TraceInfo ti) {
    	// extract the trace information needed
    	TR  = ti.TR;
    	TTR = ti.TTR;
    	
    	err.initErrs();

	}

    //-------------------------------------------------------------------------
    // Testing the Class
    //-------------------------------------------------------------------------
    public void performTests(){
		
		TR.printMsgln("----------------------------------------------------------");
		TR.printMsgln(">>>>>>>>>>>       Dates Test - Start");
		TR.printMsgln("----------------------------------------------------------");
		TR.printMsgln("Version Info:");
		TR.printMsgln("  Dates_Test:  " + getVersion());
		TR.printMsgln("  Dates:       " + Dates.getVersion());
		TR.printMsgln(" ");

		DR = new Dates(TTR); 
		TR.printMsgln("----New test-------------------------------vvvvv------------");
        testDate = new Date(timeIn1 * 1000);
        testDateToString(testDate, dateIn1, noEFlag, noError, noMessage);
		TR.printMsgln("----New test-------------------------------^^^^^------------");
        
        
        TR.printMsgln(" Dates - dateFromString validation");
        // test dateFromString takes a string in the format of YYYY-MM-DD and 
		// returns a Java Date 
        testDateFromString(dateIn1, dateOut1, timeOut1, noEFlag, noError, noMessage);
        testDateFromString(dateIn2, dateOut2, timeOut2, noEFlag, noError, noMessage);

        // test dateFromString Strange dates returns a Java Date 
        testDateFromString(dateIn3, dateOut3, timeOut3, noEFlag, noError, noMessage);
        testDateFromString(dateIn4, dateOut4, timeOut4, noEFlag, noError, noMessage);
        testDateFromString(dateIn5, dateOut5, timeOut5, noEFlag, noError, noMessage);

        // test null, empty, and blank input dates
        testDateFromString(dateSNull,  dateOutBad, timeOutBad, errFlag, errNbr00, errMsg00);
        testDateFromString(dateSEmpty, dateOutBad, timeOutBad, errFlag, errNbr00, errMsg00);
        testDateFromString(dateSBlank, dateOutBad, timeOutBad, errFlag, errNbr00, errMsg00);
        
        // test dates delimiters in wrong place and wrong delimiters
        testDateFromString(dateInBad1,  dateOutBad, timeOutBad, errFlag, errNbr01, errMsg01);
        testDateFromString(dateInBad1a, dateOutBad, timeOutBad, errFlag, errNbr01, errMsg01);
        testDateFromString(dateInBad1b, dateOutBad, timeOutBad, errFlag, errNbr01, errMsg01);
        testDateFromString(dateInBad1c, dateOutBad, timeOutBad, errFlag, errNbr01, errMsg01);
        
        // pass in some bad dates! note that java allows dates like Feb 31 and April 31
        testDateFromString(dateInBad2,  dateOutBad, timeOutBad, errFlag, errNbr02, errMsg02);
        testDateFromString(dateInBad2a, dateOutBad, timeOutBad, errFlag, errNbr02, errMsg02);

        testDateFromString(dateIn6, dateOut6, timeOut6, noEFlag, noError, noMessage);
        
        
    	TR.printMsgln(" ");
        TR.printMsgln(" Dates - dateToString validation");

        // convert a string to a date (using the previously tested routine) 
		// test dateFromString takes a string in the format of YYYY-MM-DD and 
		// returns a Java Date 
		Date dateReturn  = DR.dateFromString(dateIn1);
        err = DR.getErrs();
        if (err.eType != Errs.ETYPE_NONE || dateReturn == null) {
			TR.printMsgln("");
			TR.printMsgln("**********************************************************");
			TR.printMsgln("** Internal error:  Previously verified String to Date  **");
			TR.printMsgln("**                    Conversion Failed                 **");
			TR.printMsgln("**********************************************************");
    		System.exit(1);
        }
        
        // now convert that valid date back to a string
        testDateToString(dateReturn, dateIn1, noEFlag, noError, noMessage);

        // test error conditions (null input date)
        testDateToString(dateNull, dateSEmpty, errFlag, errNbr03, errMsg03);

        // can't corrupt a date for testing so we will skip that check
        //testDate = new Date(0);
        //testDateToString(testDate, dateSNull, errFlag, errNbr04, errMsg04);

        // convert a time to date 
        testDate = new Date(timeOut1 * 1000);
        testDateToString(testDate, dateIn1, noEFlag, noError, noMessage);

        testDate = new Date(timeOut2 * 1000);
        testDateToString(testDate, dateIn2, noEFlag, noError, noMessage);
        
        
    	TR.printMsgln(" ");
        TR.printMsgln(" Dates - addMonths validation");

        // pass a null vlaue to addMonths
        testAddMonths(dateNull, 1, dateSNull, timeOutBad, errFlag, errNbr05, errMsg05);
        
        // add one month to timeOut1
        testDate = new Date(timeOut1 * 1000);
        testAddMonths(testDate, 1, dateOut1P1, timeOut1P1, noEFlag, noError, noMessage);
        
        // add 13 months to timeOut2
        testDate = new Date(timeOut2 * 1000);
        testAddMonths(testDate, 13, dateOut2P13, timeOut2P13, noEFlag, noError, noMessage);
        
        // add 1 month to timeOut3
        testDate = new Date(timeOut3 * 1000);
        testAddMonths(testDate, 1, dateOut3P1, timeOut3P1, noEFlag, noError, noMessage);
        
        // add 1 month to timeOut4
        testDate = new Date(timeOut4 * 1000);
        testAddMonths(testDate, 1, dateOut4P1, timeOut4P1, noEFlag, noError, noMessage);
        
        // subtract 1 month to timeOut6
        testDate = new Date(timeOut6 * 1000);
        testAddMonths(testDate, -1, dateOut6M1, timeOut6M1, noEFlag, noError, noMessage);
        
    	TR.printMsgln(" ");
        TR.printMsgln(" Dates - inactiveDate validation");

        // inactive date increases the current date by the number of months, so
        testInactiveDate(3, noEFlag, noError, noMessage);
        
        
        
        
        
        TR.printMsgln(" ");
		TR.printMsgln("----------------------------------------------------------");
		TR.printMsgln(">>>>>>>>>>>       Dates Test - Finished");
		TR.printMsgln("----------------------------------------------------------");
	}


	//-------------------------------------------------------------------------
    // dateFromString Test method
    //-------------------------------------------------------------------------
	/**
	 * performs a call to the Dates.dateFromString method, then checks that 
	 * the call returns the expected values
	 * 
	 * @param inDate String value of the Date to be converted
	 * @param expDateS After converting to a date, what "string value" is 
	 *                   does the date equate to, 
	 * @param expTime  Long value of the Date
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param espMsg   String value of the expected Errs.eMessage 
	 */
	private void testDateFromString(String inDate, String expDateS, long expTime,
			                        int expEType, int expErr, String expMsg){
		
		Date    dateReturn = null;
		long    timeReturn = 0L;

		err.initErrs();

		// convert the expected date String to an actual Date value
		expDate  = DR.dateFromString(expDateS);
		
		if (inDate == null) 
			TR.printMsg("\tDate from String:  null");
		else
			TR.printMsg("\tDate from String:  " + inDate);

		// test dateFromString takes a string in the format of YYYY-MM-DD and 
		// returns a Java Date 
		dateReturn  = DR.dateFromString(inDate);
	    checkErrs(dateReturn, expDate, expEType, expErr, expMsg);

	    // extract the "time value" if a valid return
	    if (dateReturn != null) {
            // convert that into a Time, since the beginning of time (January 1, 1970, 00:00:00 GMT)
            timeReturn  = dateReturn.getTime() /1000;
        }
       	assert timeReturn == expTime : 
      		  "Expected Time:  " + expTime + "  Actual:  " + timeReturn;
/*
       	err = DR.getErrs();

        if (err.eType == Errs.ETYPE_NONE)
        	TR.printMsg("\tErrType:  ETYPE_NONE ");
        else
        	TR.printMsg("\tErrType:  " + err.eType +
        			    "\tError:  ("  + err.eNbr  + ") " + err.eMessage);
        
        // extract the "time value" if a valid return
        if (dateReturn != null) {
            // convert that into a Time, since the beginning of time (January 1, 1970, 00:00:00 GMT)
            timeReturn  = dateReturn.getTime() /1000;
        }

        // validate we got the expected results
        if (expDateS == null)
            assert dateReturn == null : 
      	      "Expected Date:  null  Actual:  " + dateReturn;
        else
        	assert dateReturn.toString().equals(expDateS) : 
        	      "Expected Date:  " + expDateS + "  Actual:  " + dateReturn;

       	assert timeReturn == expTime : 
  	      		  "Expected Time:  " + expTime + "  Actual:  " + timeReturn;
		assert err.eType == expEType  :
		      	  "Expected ErrType:  " + expEType + "  Actual:  " + err.eType; 
		assert err.eNbr == expErr  :
				  "Expected Error:  " + expErr + "  Actual:  " + err.eType; 
		assert err.eMessage.equals(expMsg)  :
	      	  	  "Expected Msg:  " + expMsg + "  Actual:  " + err.eMessage;
  
		TR.printMsgln("  - Results as expected");
*/
	}
	
	//-------------------------------------------------------------------------
    // dateToString Test method
    //-------------------------------------------------------------------------
	/**
	 * performs a call to the Dates.dateToString method, then checks that 
	 * the call returns the expected values
	 * 
	 * @param inDate DateValue to be process
	 * @param expDateS Expected string value to be returned
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param espMsg   String value of the expected Errs.eMessage 
	 */
	private void testDateToString(Date inDate, String expDateS,
			                      int expEType, int expErr, String expMsg) {
		String  retDateS = null;

		if (inDate == null) 
			TR.printMsg("\tDate to String:  null");
		else
			TR.printMsg("\tDate to String:  " + inDate);

		// test dateFromString takes a string in the format of YYYY-MM-DD and 
		// returns a Java Date 
		retDateS  = DR.dateToString(inDate);
		checkErrs(retDateS, expDateS,expEType, expErr, expMsg);

	}

	//-------------------------------------------------------------------------
    // addMonths Test method
    //-------------------------------------------------------------------------
	/**
	 * performs a call to the Dates.addMonths method, then checks that 
	 * the call returns the expected values
	 * 
	 * @param inDate Date Value of the starting date
	 * @param nbrMonths int value of the number of months to be added
	 * @param expDateS After adding nbrMonths to inDate, the expected 
	 *                    "string value" of the returned Date 
	 * @param expTime  Long value of the returned Date
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param espMsg   String value of the expected Errs.eMessage 
	 */
	private void testAddMonths(Date inDate, int nbrMonths, 
			                   String expDateS, long expTime,
			                   int expEType, int expErr, String expMsg){
		
		Date    dateReturn = null;
		long    timeReturn = 0L;

		err.initErrs();

		// convert the expected date String to an actual Date value
		expDate  = DR.dateFromString(expDateS);
		
		if (inDate == null) 
			TR.printMsg("\tAdd Months Date:  null");
		else
			TR.printMsg("\tAdd Months Date:  " + inDate);
		
		TR.printMsg("     Increment:  " + nbrMonths);
		

		// call addMonths - returns a Java Date 
		dateReturn  = DR.addMonths(inDate, nbrMonths);
	    checkErrs(dateReturn, expDate, expEType, expErr, expMsg);

	    // extract the "time value" if a valid return
	    if (dateReturn != null) {
            // convert that into a Time, since the beginning of time (January 1, 1970, 00:00:00 GMT)
            timeReturn  = dateReturn.getTime() /1000;
        }
       	assert timeReturn == expTime : 
      		  "Expected Time:  " + expTime + "  Actual:  " + timeReturn;
	}
	
	//-------------------------------------------------------------------------
    // inactiveDate Test method
    //-------------------------------------------------------------------------
	/**
	 * performs a call to the Dates.inactiveDate method, then checks that 
	 * the call returns the expected values
	 * 
	 * @param nbrMonths int value of the number of months to be added
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param espMsg   String value of the expected Errs.eMessage 
	 */
	private void testInactiveDate(int nbrMonths, 
			                   	  int expEType, int expErr, String expMsg){

		// add the number of months today
		testDate = new Date();
		String todayS = DR.dateToString(testDate);   
		Calendar expC = Calendar.getInstance();
		expC.add(Calendar.MONTH, nbrMonths);
		String expS = String.format("%tF", expC);
		expDate  = DR.dateFromString(expS);

		TR.printMsg("\tInactive Date Months to Add :  " + nbrMonths + 
			    "    Today:  " + todayS);

		// now get the Inactive Date, nbrMonths from now
		Date retDate = DR.inactiveDate(nbrMonths);
	    checkErrs(retDate, expDate, expEType, expErr, expMsg);
		
	}

	//-------------------------------------------------------------------------
    // Testing program code Reducer Methods
    //-------------------------------------------------------------------------
	/**
	 * Verifies that the "Date" return from the called class is equal
	 * to the expected value, then verifies the err from the called class
	 * matches the expected values
	 *
	 * @param rVal     Date value of the actual return
	 * @param expRtn   Expected Date return value
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param expMsg   String value of the expected Errs.eMessage
	 */ 
    private void checkErrs(Date rVal, Date expRtn, 
    		                int expEType, int expErr, String expMsg) {

    	if (expRtn == null)
    		assert rVal == expRtn  :  "\nExpected Return:  " + expRtn + 
                    "    Actual:  " + rVal;
    	else
    		assert rVal.equals(expRtn) : "\nExpected Return:  " + expRtn + 
            		"    Actual:  " + rVal;
		// get the error messages
		checkErr(expEType, expErr, expMsg);

    }

	/**
	 * Verifies that the "String" return from the called class is equal
	 * to the expected value, then verifies the err from the called class
	 * matches the expected values
	 *
	 * @param rVal     String value of the actual return
	 * @param expRtn   Expected String return value
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param expMsg   String value of the expected Errs.eMessage
	 */ 
    private void checkErrs(String rValS, String expRtnS, 
            				int expEType, int expErr, String expMsg) {

    	if (expRtnS == null)
    		assert rValS == expRtnS  :  "\nExpected Return:  " + expRtnS + 
                    "    Actual:  " + rValS;
    	else
    	if (expRtnS.isEmpty())
    		assert rValS.isEmpty() : "\nExpected Return:  empty String" + 
                                         "    Actual:  " + rValS;
    	else
    		assert rValS.equals(expRtnS) : "\nExpected Return:  " + expRtnS + 
            		"    Actual:  " + rValS;
    		
    		// get the error messages
    	checkErr(expEType, expErr, expMsg);

    }

	/**
	 * Verifies that the err from the called class match the expected values
	 *
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param expMsg   String value of the expected Errs.eMessage
	 */ 
    private void checkErr(int expEType, int expErr, String expMsg){

    	// get the tested Class errors
    	err = DR.getErrs();
    	
    	// show the Error, if any
    	if (err.eType == Errs.ETYPE_NONE)
        	TR.printMsg("\tErrType:  ETYPE_NONE ");
        else
        	TR.printMsg("\tErrType:  " + err.eType +
        			    "\tError:  ("  + err.eNbr  + ") " + err.eMessage);

    	// verify the expected values
    	assert err.eType == expEType  :
    	    "\nExpected ErrType:  " + expEType + "  Actual:  " + err.eType; 
    	assert err.eNbr == expErr  :
		    "\nExpected Error:  "   + expErr   + "  Actual:  " + err.eNbr; 
    	assert err.eMessage.equals(expMsg)  :
	  	    "\nExpected Msg:  "     + expMsg   + "\nActual:        " + err.eMessage;

    	TR.printMsgln("  - Results as expected");
    	
    }

    //-------------------------------------------------------------------------
    // Testing program Version Methods
    //     Static because they are not specific to an instance
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
		String v = Dates_Test.class.getName();
		// if it already exists, just return
		if (vlist.containsKey(v)) {
			return vlist;
		}
		//add it to the list
		vlist.put(v, getVersion());

		vlist = Dates.getAllVersions(vlist);
		
		vlist = Errs.getAllVersions(vlist);
    	vlist = Trace.getAllVersions(vlist);

    	return vlist;
	} 
}