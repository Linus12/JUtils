package jutils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
/**
 * The Trace class combines the writing program information to StdOut 
 * and to files using a common set of tools. An extension of DebugStatus,
 * it allows for "Debugging Trace messages" to be with written to a 
 * "Trace" file, or for those messages to not be written. Methods are
 * also available to format information in the Errs class in a standard
 * way, as well as the ability to display "Separator Lines" in files 
 * and to the user.
 * 
 */

public class Trace extends DebugStatus {

    private  final static String  version  = "Version 14.70";
    
    // Trace Separator types 
    public   final static String  TRACE_SEP_BEGIN  = "B";
    public   final static String  TRACE_SEP_END    = "E";
    public   final static String  TRACE_SEP_STAR   = "*";
    public   final static String  TRACE_SEP_ERROR  = "*";
    public   final static String  TRACE_SEP_DOUBLE = "D";
    public   final static String  TRACE_SEP_SEP01  = "1";   // V14.51
        
    // normally we want to send all Trace Messages to the appropriate trace
    // location (Trace File, Trace Window). But there is a need to force to
    // print directly to the System Out, for example before dying an
    // inglorious death. Therefore methods have been created to call the basic
    // trace routines (separator, messages, etc.) but to force them to 
    // System Out, even if a Trace File or Trace Window is open.  
    private  boolean     showOnTR    = true;       // show in trace window/file

    // for tracing in an environment with a trace file
    private  boolean     fTrace     = false;       // Trace messages to a file?
    private  FIO         fileT      = null;        // Trace File
    
    // For tracing in a Gui environment with a trace window
    private  boolean     gui        = false;

    // passed values
    private  String      fTraceName = "trace.log"; // default filename

    
    private  TraceThread TT         = null;        // only set if gui is true
    private  TraceThread UM         = null;        // only set if gui is true

    // support classes
    private  Errs        err         = new Errs(); // Error Handling
    

    //-------------------------------------------------------------------------
    //  Class constructors
    //-------------------------------------------------------------------------
    /**
     * Constructor for file based trace files or for initial program tracing.<p>
     * If the Trace File Name passed is null, then tracing will go to 
     * System.out. <p>
     * Methods are coded so that printing to the trace file or System
     * Out will print exactly the same.<p>
     * If the Trace File Name is blank, the default name will be used. 
     * Otherwise the passed in file will be used.<p> 
     * When opened, if the Trace file exists, the file will be appended to, 
     * not overwritten.<p>
     * Note that the file will not be opened until the first message is to be
     * written. At which time a "Opened" date/time stamp will be written to the
     * file.<p>
     * If the file close routine is called, and it may not due to the program 
     * ending in a error, a "Closed" date/time stamp will be written to the file
     * before it is closed. 
     *  
     *  @param tfName string value of the name of the trace file, or null;
     * @param tr a previously instantiated Trace class, or null. This 
     *                      parameter is provided so that a class can trace
     *                      to a file to begin with, and then create a trace
     *                      window but not have to initialize all of the 
     *                      debug states again.             
     *   
     */
    public Trace(String tfName, Trace tr) {
    	
    	super();  			// initialize the DebugStatus 
    	initTrace(tfName);  // initialize the trace variables
    	
   		// Define the Trace File. We pass null as the "Trace" instance because
    	// it should not "write a trace message to the Trace file" when 
   		// opening, writing, or closing the Trace File. Those messages 
   		// should go to SysOut.
    	fileT = new FIO(null); 
    
    	// now that we actually have a valid trace file destination,
    	// setup the debug status with this info
    	initDebugStatus(this, err, tr);
    	
    	
    }  

    /**
     * Constructor for GUI based Trace Windows or for initial program 
     * tracing.<p>
     * Two windows are used for Gui based apps. One is for the "Trace Messages"
     * and the other is for normal messages that would be displayed to the
     * the user. So  a message to be "printed" would go to the  
     * "User Message Window" while a 'Debug' or 'Trace' message would go to 
     * the "Trace Window". This just makes it easier to seperate the debugging
     * info from the normal output.<p> 
     * If the gui flag is set to <code>true</code>, then all  
     * trace routines are sent to the appropriate TraceThreads. 
     * If the flag is set to anything else, then tracing will go to 
     * either the Trace file or to System.out. <p>
     * Methods are coded so that printing to the trace window or System
     * Out will print exactly the same.<p>
     * When opened, if the Trace window exists, the window will be appended 
     * to, not overwritten.<p>
     * Note that the neither the User Message Window, nor the Trace window,
     * will not be opened until the first message is to be written. At which
     * time a "Opened" date/time stamp will be written to the window.<p>
     * If the Trace window close routine is called, and it may not due to the
     * stopping in a error, a "Closed" date/time stamp will be written to 
     * the window before it is closed. 
     *  
     * @param traceWindow  TraceThread instance to handle GUI trace
     *                      Requests when a thread has been created to 
     *                      handle windows.<br>
     *                      May be set to <code>null</code>, to indicate that
     *                      all trace messages are sent to System.out; 
     *                      otherwise the trace messages are sent to the 
     *                      TraceThread
     * @param userMsgWindow  TraceThread instance to handle GUI "print"
     *                      Requests when a thread has been created to 
     *                      handle windows.<br>
     *                      May be set to <code>null</code>, to indicate that
     *                      these messages are sent to System.out; 
     *                      otherwise the messages are sent to the 
     *                      userMsgWindow
     * @param tr a previously instantiated Trace class, or null. This 
     *                      parameter is provided so that a class can trace
     *                      to a file to begin with, and then create a trace
     *                      window but not have to initialize all of the 
     *                      debug states again.             
     */
    public Trace(TraceThread traceWindow, 
    			 TraceThread userMsgWindow, 
    		     Trace tr                     ) {

    	super();  // initialize the DebugStatus 
    	
    	initTrace(null);  // null is passed as the trace file name so that
    	                  // no changes are made!

    	// if writeing to windows, both must be passed!
    	if (traceWindow != null && userMsgWindow != null) {
    		gui    = true;
    		TT     = traceWindow;
    		UM     = userMsgWindow;
    	} 
    	
    	// now that we actually have a valid trace file destination,
    	// setup the debug status with this info
    	initDebugStatus(this, err, tr);
    	
    	
    }  

    /**
     * Initializes the values of the trace class. Needed because a program
     * may create a Trace class initially during startup to print to the
     * System Out, but then create a trace file or trace window for all
     * subsequent calls. This resets the trace variables, in case the 
     * program doesn't call close and/or just decides to reuse the instance.
     * 
     *  @param tfName string value of the name of the trace file, or null;
     */
    private void initTrace(String tfName) {
    	err = new Errs();
    	
    	// reset file tracing fields
    	fTrace = false;
    	fileT  = null; 

    	// reset gui tracing fields
    	gui    = false;
    	TT     = null;
    	UM     = null;
    	
    	// if passed, set the Trace file name
    	if (tfName != null) {
   			fTrace  = true;  // tracing a to a file
    		if (!tfName.isEmpty())   // passed name not empty 
    			fTraceName = tfName; // save file name for later
    	}

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
    // General public methods
    //-------------------------------------------------------------------------
    /**
     * Closes the Trace instance. While not "required" to be called, it does
     * help in locating the "end" of a run in the trace file.<p>
     * For file based Traces, a clean up method tClose is called to 
     * close the actual file.
     * For GUI based Traces, the Trace window is closed.<p>
     * The trace variables are reset to null, in case the program decides to 
     * reuse the instance again without calling the constructor (to print
     * messages to StdOut.
     */
    public void close() {
    	err.initErrs();
    
    	// GUI based Traces
    	if (gui) {
    		// close the Trace window
    		TT.close();
    		// close the userMessage Window
    		UM.close();
    	}
    	
    	// File based traces
    	if (fTrace){
        	if (fileT.isOpen()) {        // is the file open?
        		// create a time stamp
        		String timeStamp = "Closed - " + 
        				new SimpleDateFormat("dd-MMM-yyyy HH:mm.sss").
                                    format(Calendar.getInstance().getTime());
            
        		// write the time stamp
        		traceMsgln(timeStamp);
        		traceSep(TRACE_SEP_DOUBLE);
 
        		// close the file
        		if (!fileT.close()) {
        			err = fileT.getErrs();
        		}
        	} // trace file is open
    	}
    	
    	// Reset the trace info, but not the file name or debug information
    	initTrace(null);
    	return;
    }  

    //-------------------------------------------------------------------------
    // Public Print Output methods (to SysOut)
    //-------------------------------------------------------------------------
    /**
     * Display a General separator line to the user
     */
    public void printSep(){
    	showOnTR = false;   // turn off tracing
    	traceSep("");
    	showOnTR = true;    // turn on  tracing
    }
    
    /**
     * Display a Beginning, Ending, or General separator line to the user
     * @param sType String value for the type of separator to be displayed,
     *                      see traceSep(String sType) for more info
     */
    public void printSep(String sType){
    	showOnTR = false;   // turn off tracing
    	traceSep(sType);
    	showOnTR = true;    // turn on  tracing
    }
    /**
     * Displays a message, based on the Errs values, with an optional
     * message string (may be blank), to the user 
     * 
     * @param err Errs class with the error values set
     * @param message string with an additional message for the user
     */
    public void printErr(Errs err, String message) {
    	showOnTR = false;   // turn off tracing
    	traceErr(err, message, "") ;
    	showOnTR = true;    // turn on  tracing
    }

    /**
     * Displays a message, based on the Errs values, with an optional
     * message string (may be blank), to the user in a block 
     * 
     * @param err Errs class with the error values set
     * @param message string with an additional message for the user
     */
    public void printErrBlock(Errs err, String message) {
    	showOnTR = false;   // turn off tracing
    	traceMsgln("***** Error ********************************************");
    	traceErr(err, message, "") ;
    	traceMsgln("********************************************************");
    	showOnTR = true;    // turn on  tracing
    }

    /**
     * Displays a tracing message to the user, without a hard return
     * 
     * @param msg  String message to be displayed
     */
    public void printMsg(String msg) {
    	showOnTR = false;   // turn off tracing
    	traceMsg(msg);
    	showOnTR = true;    // turn on  tracing
     }

    /**
     * Displays a tracing message to the user, with a hard return
     * 
     * @param msg  String message to be displayed
     */
    public void printMsgln(String msg) {
    	showOnTR = false;   // turn off tracing
    	traceMsgln(msg);
    	showOnTR = true;    // turn on  tracing
    }

    /**
     * Displays a formatted message. Used to display Field names or 
     * descriptions and values. The "name" is defaulted to 40 characters
     * with two additional preceding spaces. 
     * @param title  string value to print first
     * @param value  string value to print second
     */
    public void printFmt(String title, Object value) {
    	showOnTR = false;   // turn off tracing
    	traceFmt(title, value);
    	showOnTR = true;   // turn off tracing
    }

    //-------------------------------------------------------------------------
    //  Public Debug Trace methods
    //-------------------------------------------------------------------------
    
    /**
     * Displays a debugging message separator
     * 
     * @param cDCode String value of a DebugInfo Debug Method Code. 
     */
	public void debugSep(String cDCode) {
        if (cDCode == null || isDebug(cDCode)) {  
    	   traceSep("");
       }
    }

    /**
     * Display a separator line in the trace, if debugging
     * 
     * @param cDCode String value of a DebugInfo Debug Method Code. 
     * @param sType string value equal to one of the following values:
     *         <ul><li><b>TRACE_SEP_BEGIN</b> - prints a "Beginning" separator line</li>
     *             <li><b>TRACE_SEP_END</b> - prints an "Ending" separator line</li>
     *             <li><b>TRACE_SEP_STAR</b> - prints a star separator line</li>
     *             <li><b>TRACE_SEP_DOUBLE</b> - prints a "double" separator line</li>
     *             <li><b><i>anything else</i></b> - prints a dash separator line</li>
     *         </ul>
     */
    public void debugSep(String cDCode, String sType){
        if (cDCode == null || isDebug(cDCode)) {  
        	traceSep(sType);
        }
    }

    /**
     * Displays the current error message on Trace output
     * 
     * @param cDCode String value of a DebugInfo Debug Method Code. 
     * @param dPreFix string to be written in front of error to distinguish
     *                       its origin
     * @param err Errs class with the error values set
     */
	public void debugErr(String cDCode, String dPreFix, Errs err) {
        if (cDCode == null || isDebug(cDCode)) {  
           traceErr(err, "", dPreFix);
       }
   }


	/**
     * Displays a specific error message on Trace output, with a message
     * 
     * @param cDCode String value of a DebugInfo Debug Method Code. 
     * @param dPreFix string to be written in front of error to distinguish
     *                       its origin
     * @param message  String message to be displayed
     * @param err Errs class with the error values set
     */
	public void debugErr(String cDCode, String dPreFix, 
			             Errs err, String message) {
        if (cDCode == null || isDebug(cDCode)) {  
            traceErr(err, message, dPreFix);
        }
    }

    /**
     * Displays a message on Trace output, with a newline
     * 
     * @param cDCode String value of a DebugInfo Debug Method Code. 
     * @param dPreFix string to be written in front of error to distinguish
     *                       its origin
     * @param message  String message to be displayed
     */
	public void debugMsgln(String cDCode, String dPreFix, String message) {
        if (cDCode == null || isDebug(cDCode)) {  
            traceMsgln(dPreFix + "\t" + message);
        }
    }

    /**
     * Displays a formatted message. Used to display Field names or 
     * descriptions and values. The "name" is defaulted to 40 characters
     * with two additional preceding spaces.
     *  
     * @param cDCode String value of a DebugInfo Debug Method Code. 
     * @param dPreFix string to be written in front of error to distinguish
     *                       its origin
     * @param title  string description to print first
     * @param value  string value to print second
     */
    public void debugFmt(String cDCode, String dPreFix, 
    		             String title,  Object value)       {
        if (cDCode == null || isDebug(cDCode)) {  
        	traceFmt(dPreFix + "\t" + title, value);
        }
    }
    
    //-------------------------------------------------------------------------
    // Debug methods
    //-------------------------------------------------------------------------
    /** 
     * Determines if we are debugging a specific class. If debugging hasn't 
     * been defined, or we are not debugging the class, a false value is
     * returned, otherwise a true value is returned.
     * 
     * @param cDCode String value of the Class Debug Code to be checked
     * @return  boolean value indicating if debugging is initiated for the
     *             the class specified by cDCode 
     */
    public boolean isDebug(String cDCode) {
    	if (getDF(cDCode) ) 
    		return true;
    	else
    		return false;
    		
    }
    
    //-------------------------------------------------------------------------
    // Public Trace Output methods
    //-------------------------------------------------------------------------
    /**
     * Display a Beginning, Ending, or General  separator line in the trace
     * 
     * @param sType string value equal to one of the following values:
     *         <ul><li><b>TRACE_SEP_BEGIN</b> - prints a "Beginning" separator line</li>
     *             <li><b>TRACE_SEP_END</b> - prints an "Ending" separator line</li>
     *             <li><b>TRACE_SEP_STAR</b> - prints a star separator line</li>
     *             <li><b>TRACE_SEP_DOUBLE</b> - prints a "double" separator line</li>
     *             <li><b><i>anything else</i></b> - prints a dash separator line</li>
     *         </ul>
     */
    public void traceSep(String sType){
    	if        (sType.equalsIgnoreCase(TRACE_SEP_BEGIN)) {
    		traceMsgln("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" +
    				   "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
    		          //123456789x123456789x123456789x123456789x123456789x
    	} else if (sType.equalsIgnoreCase(TRACE_SEP_END)) {
    		traceMsgln("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^" +
    			  	   "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    		          //123456789x123456789x123456789x123456789x123456789x
    	} else if (sType.equalsIgnoreCase(TRACE_SEP_STAR)) {
    		traceMsgln("**************************************************" +
 			  	       "******************************");
 		              //123456789x123456789x123456789x123456789x123456789x
    	} else if (sType.equalsIgnoreCase(TRACE_SEP_DOUBLE)) {
    		traceMsgln("\n"                                                 +
    	               "==================================================" +
    	               "========================================\n");
 		              //123456789x123456789x123456789x123456789x123456789x
    	} else if (sType.equalsIgnoreCase(TRACE_SEP_SEP01)) {                  // V14.51
    		traceMsgln("\n"                                                 +  // V14.51
                       "-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*" +  // V14.51
                       "-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n"   );       // V14.51
            		  //123456789x123456789x123456789x123456789x123456789x     // V14.51
    	} else {
		    traceMsgln("--------------------------------------------------" +
			  	       "------------------------------");
		              //123456789x123456789x123456789x123456789x123456789x
    	}
    }
    
    /**
     * Displays a message, based on the Errs values, with an optional
     * message string, to the user, either directly on System.out, 
     * on the gui Window, or to a file.
     * 
     * @param err Errs class with the error values set
     * @param message  String message to be displayed may be empty
     */
    public void traceErr(Errs err, String message) {
    	traceErr(err, message, "");
    }
    
    private void traceErr(Errs err, String message, String preFix) {
    	// build a set of trace lines to be written
    	String outMsg = "";
    	
    	// add the prefix
		if ((preFix  != null) && (!preFix.isEmpty()))
			outMsg += preFix + "\t";

		// add the optional message
		if ((message != null) && (!message.isEmpty())) 
			outMsg += message + "\n"; // print extra message
    	
    	// format the "message from the err" class
    	outMsg +=  "\t(" + err.eNbr + ")   " + err.eMessage;
    	
    	// determine where to print it!
    	traceMsgln(outMsg);

    	// was there a stack trace?
		if (err.eStackTrace != null && !err.eStackTrace.isEmpty()) 
			traceMsgln(err.eStackTrace);

/*    	
    	if (gui) { 
    		if ((preFix  != null) && (!preFix.isEmpty()))
    			TT.print(preFix + "\t");

    		if ((message != null) && (!message.isEmpty())) {
    			TT.println(message); // print extra message
    		}
    		TT.println(errOut);
        } else {
        	if (fTrace && showOnTR) {
        
        		if (!fileT.isOpen())     // File Trace open?
        			open();

        		if ((preFix  != null) && (!preFix.isEmpty()))
        			fileT.print(preFix + "\t");
        		
        		if ((message != null) && (!message.isEmpty())) 
        			fileT.println(message); // print extra message
        		
        		fileT.println(errOut);
        		
        		if (err.eStackTrace != null && !err.eStackTrace.isEmpty()) 
        			fileT.println(err.eStackTrace);
        		
        	} else { 
            	if ((message != null) && (!message.isEmpty())) 
            	   	System.out.println(message);  // print "err" message
        		System.out.println(errOut);        
           		if (err.eStackTrace != null && !err.eStackTrace.isEmpty()) 
        			System.out.println(err.eStackTrace);
               	System.out.flush();
        	}	
        }
*/
    }

    /**
     * Displays a formatted message. Used to display Field names or 
     * descriptions and values. The "name" is defaulted to 40 characters
     * with two additional preceding spaces. 
     * 
     * @param title  string value to print first
     * @param value  string value to print second
     */
    public void traceFmt(String title, Object value) {
    	traceMsgln(String.format("  %-40s %s", title, value));
    }
    
    //-------------------------------------------------------------------------
    // Actual writing of to the Trace File, windows, and SysOut
    //-------------------------------------------------------------------------
    /**
     * Moves the pointer to the bottom of User Message Window so they latest
     * Messages are displayed to the user and scrolling begins again
     */
    public void moveToEnd() {
    	if (!gui) return;
    	
    	if (UM == null)  return;
    	
    	UM.moveToBottom();
    	
    }
    /**
     * Displays a tracing message to the user, without a hard return,
     * either directly on System.out, on the gui Window, or to a file.
     * 
     * @param msg  String message to be displayed
     */
    public void traceMsg(String msg) {
    	if (msg == null || msg.isEmpty()) { return;}
    	
    	// handle Windows writes first
    	if (gui) { 
        	if (showOnTR) {
        		TT.print(msg);
        		return;
        	} else if (UM != null) {
        		UM.print(msg);
        		return;
        	}
        }

    	// Check for Trace File writes
       	if (fTrace && showOnTR) {
       		if (!fileT.isOpen()) { open(); }
        	fileT.print(msg);
        	return;
        }
        
       	// all other cases write to SysOut!
       	System.out.print(msg);
		System.out.flush();
    }

    /**
     * Displays a tracing message to the user, with a hard return,
     * either directly on System.out, on the gui Window, or to a file.
     * 
     * @param msg  String message to be displayed
     */
    public void traceMsgln(String msg) {
    	if (msg == null || msg.isEmpty()) { return;}
    	
    	// handle Windows writes first
    	if (gui) { 
        	if (showOnTR) {
        		TT.println(msg);
        		return;
        	} else if (UM != null) {
        		UM.println(msg);
        		return;
        	}
        }

    	// Check for Trace File writes
       	if (fTrace && showOnTR) {
       		if (!fileT.isOpen()) { open(); }
        	fileT.println(msg);
        	return;
        }
        
       	// all other cases write to SysOut!
       	System.out.println(msg);
		System.out.flush();

/*		
		if (gui) { 
        	if (showOnTR)   TT.println(msg);
        	else            UM.println(msg);
        } else {
        	if (fTrace && showOnTR) {
        		if (!fileT.isOpen()) {
        			open();
        		}
        		fileT.println(msg);
        	} else {      
        		System.out.println(msg);
        		System.out.flush();
        	}
        }
*/
    }

    //-------------------------------------------------------------------------
    // Private Trace methods
    //-------------------------------------------------------------------------
    /**
     * Opens a file based Trace file. Internal to the Trace because the file 
     * is only opened when something needs to be written to it. Thus the main
     * program code can be sprinkled with calls to Trace Calls, but the file
     * only be opened, if a message actually needs to be written to the file.
     * 
     * @return boolean value where <code>true</code> indicates the file was
     *                 opened successfully, otherwise <code>false</code> is 
     *                 returned.
     */
    private boolean open() {
    	
    	err.initErrs();
    	
    	// try to open the file for append, catching any errors
    	if (!fileT.open(fTraceName, FIO.FIO_APPEND)) {
			err = fileT.getErrs();
			return false;
		}
		
    	// create a time stamp
        String timeStamp = "Opened - " + 
        		new SimpleDateFormat("dd-MMM-yyyy HH:mm.sss").
                                format(Calendar.getInstance().getTime());
        
        // write the time stamp
        fileT.println(timeStamp);
    	
        return true;
    }
    
    //-------------------------------------------------------------------------
    // Static Version Methods
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
		String v = Trace.class.getName();
		// if it already exists, just return
		if (vlist.containsKey(v)) {
			return vlist;
		}
		//add it to the list
		vlist.put(v, getVersion());

    	vlist = FIO.getAllVersions(vlist);
    	vlist = TraceThread.getAllVersions(vlist);
    	vlist = Errs.getAllVersions(vlist);
    	
    	vlist = DebugStatus.getAllVersions(vlist);
    	
    	return vlist;
	}

}
