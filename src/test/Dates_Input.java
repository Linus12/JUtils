

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Scanner;

import jutils.Dates;
import jutils.Errs;
import jutils.Trace;

public class Dates_Input {
	protected final static String    version      = "Version 14.60";

	// Class to be tested
	private Dates DR;   

    private  Scanner        SysIn      = new Scanner(System.in); 

    //----------
    
	

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
	public Dates_Input(TraceInfo ti) {
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
		TR.printMsgln(">>>>>>>>>>>       Date Input Test - Start");
		TR.printMsgln("----------------------------------------------------------");
		TR.printMsgln("Version Info:");
		TR.printMsgln("  Dates_Test:  " + getVersion());
		TR.printMsgln("  Dates:       " + Dates.getVersion());
		TR.printMsgln(" ");

		DR = new Dates(TR); 
		
        // get the user input until either a valid value is entered OR
        // the user indicates they wish to stop
        while (true) {
            err.initErrs();
            // try to the input from the user 
            TR.printMsg("Enter a date number or a date String\n    Enter '//' to quit.  >>"  );

            String inline = SysIn.nextLine();

            // if the value is null or empty
            if (inline == null || inline.isEmpty())   {
            		inline = "";
            }
            String inValue = inline.trim();  // clean up the field

            // check for user canceling
            if (inValue.equals("//")) {
                err.eNbr     = 530;
                err.eMessage = "User cancelled field input.";
                err.eType    = Errs.ETYPE_WARN;
                break;   // user is tired and wants to go home
            }

            // test date for input as a number or a date string
            // if it contains only numbers, test as date to string
            Long tValue = 0L;
            try {
                tValue = Long.parseLong(inValue);
            } catch (NumberFormatException e) {
            	// test string a date value
                Date dateOut = DR.dateFromString(inValue);
                err = DR.getErrs();
                if (err.eType != Errs.ETYPE_NONE){
                	TR.printMsgln("Invalid date string:  " + inValue);
                } else {
                	TR.printMsgln("Date Value:  " + dateOut);
                }
                TR.printMsgln("\n");
                continue;  // get next value
            }
            // convert long value to a date
            Date dateIn = null;
            dateIn = new Date(tValue * 1000);
            String dateOutS = DR.dateToFullString(dateIn);
            err = DR.getErrs();
            if (err.eType != Errs.ETYPE_NONE){
            	TR.printMsgln("Invalid date string:  " + inValue);
            } else {
            	TR.printMsgln("Date Value:  " + dateOutS);
            }
            TR.printMsgln("\n");
           
        } // while loop	

		
        
        TR.printMsgln(" ");
		TR.printMsgln("----------------------------------------------------------");
		TR.printMsgln(">>>>>>>>>>>       Dates Test - Finished");
		TR.printMsgln("----------------------------------------------------------");
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
		String v = Dates_Input.class.getName();
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