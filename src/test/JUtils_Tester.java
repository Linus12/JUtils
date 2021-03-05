/**
 * The Test class runs the selected class cases, based on the input parameters.
 * It can run the tests normally, or by turning debug on in order to test the 
 * debug output of a given class. 
 *         
 * The program accepts a number of Command Line parameters that affect the 
 * way the tests are run.
 * 
 * @Author    David H. Stumpf
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import jutils.Args;
import jutils.Errs;
import jutils.Trace;



/**
 * The TestIt program allows a number of Command line parameters to determine 
 * which tests are run and how they are run.<p>
 * 
 * The TestIt program accepts the following arguments:<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>[-help] [-v] [-db] [tdb] [-t filename]
 *                                        [-all] [i ConfigFileName]
 *                                        [-c] [-cu] [-fu] [fa]
 *                                        [-d] [-f]  
 *                                        
</b><br>
 * <p>
 * <b>Notes on Argument values:</b>
 * <ol>
 * <li><b>-help</b> Prints out the help information, then quits.
 * <li><b>-v</b> Prints the version number of all the classes, then quits.
 *     </li><p>
 * <li><b>-db</b> indicates the TestIt Program is to be run in debug mode
 *     </li><p>
 * <li><b>-tdb</b> indicates the tests are to be run in debug mode</i></p>
 * <li><b>-t</b> optional Test Trace File Name. If not specified, any tracing 
 *     messages will be written to Standard Out. If you are not tracing the 
 *     program and not tracing individual tests, this argument does not need
 *     to be set.</li><p> 
 * <li><b>-All</b> indicates that All tests should be run. If passed, 
 *     individual test flags are ignored.</li><p>
 * <li><b>-i</b> optional Configuration File name. If not specified, the 
 *     TestConfig.ini file is used.
 * </ol>
 * <b>Individual Test Flags</b>
 * <ul>
 * <li><b>-c </b> Test the Config Class interfaces</li><p>
 * <li><b>-cu</b> Test the CUser Class interfaces</li><p>
 * <li><b>-fu</b> Test the FUser Class interfaces</li><p>
 * <li><b>-fa</b> Test the FUser Authorization Class interfaces</li><p>
 * <li><b>-d </b> Test the Date Class Interfaces</li><p>
 * <li><b>-f </b> Test the File Class Interfaces</li><p>
 * </ul>
 */
public class JUtils_Tester {
    
	private static final String  version      = "Version 14.50";

	// House keeping values
    private Boolean   versionOnly    = false;  // only printing versions?
    private String    startTime      = "";     // program start time
	private Boolean   pgmDFlag       = false;  // run test Program in debug? 
	private String    pgmTrace       = null;   // Test Program trace file
	private boolean   needHelp       = false;  // show the help messages?
	
    // support objects of the Test Program and Test Classes
	private  Errs       err          = new Errs();  // Test Program errors
    private  Args       args         = null;        // Test Program Args         
	private  Trace      TR           = null;        // Test Program OUTPUT
    

	
	// Valid Arguments that can be passed to the program 
    // Test Program parameters
    static public final String ARG_HELP        = "-help"; // Display Help
    static public final String ARG_VERSION     = "-v";    // Display Version
    static public final String ARG_DEBUG       = "-x";    // Run in Debug Mode 
	static public final String ARG_TRACEF      = "-t";    // Name of trace file

    // How do we run the tests
    static public final String ARG_TEST_DEBUG  = "-tdb";  // Run Tests in Debug
    static public final String ARG_TEST_TRACEF = "-ttf"; // Test trace file

    // Test Program: Which tests to perform
    static public final String ARG_ALL         = "-all"; // Perform All Test

    // Individual tests available for this package
    static public final String ARG_ARGS        = "-ta";   // Args Test
    static public final String ARG_DATES       = "-td";   // Date Test
    static public final String ARG_DATE_INPUT  = "-tdi";  // Date Input Test
    static public final String ARG_DBGFLAGS    = "-tdbf"; // DebugFlags Test
    static public final String ARG_DEBUGINFO   = "-tdbi"; // DebugInfo Test
    static public final String ARG_ERRS        = "-te";   // Error Test
    static public final String ARG_FILESEARCH  = "-tfs";  // File Search Test
    static public final String ARG_FIO         = "-tfio";  // FIO Test
    static public final String ARG_GUIINFO     = "-tg";	// GuiInfo Test
    static public final String ARG_HELPT       = "-thelp"; // Help Test
    static public final String ARG_SLEEP       = "-ts";  // Sleep Test
    static public final String ARG_TRACE       = "-tt";  // Trace Test
    static public final String ARG_TRACETHREAD = "-ttt";	// Trace Thread Test
    static public final String ARG_WINDOWRPT   = "-twr";	// Window Report Test



    // Array of Argument values in the order they should be shown
    private static final List<String> ARGFields  
    = Arrays.asList(// Test Program Params
    				ARG_HELP,    	ARG_VERSION, 
    		        ARG_DEBUG,   	ARG_TRACEF,
    		        // Test debugging
    		        ARG_TEST_DEBUG, ARG_TEST_TRACEF, 
    		        ARG_ALL,     	
    		        // Tests available in this package
    		        ARG_ARGS,       
    		        ARG_DATES,	 	ARG_DATE_INPUT,
    		        ARG_DBGFLAGS,
    		        ARG_DEBUGINFO,	ARG_ERRS,       ARG_FILESEARCH, 
    		        ARG_FIO,        ARG_GUIINFO,	ARG_HELPT,
                    ARG_SLEEP,      ARG_TRACE,      ARG_TRACETHREAD,
                    ARG_WINDOWRPT

                   );

    // Order tests are to be performed
    private static final List<String> Tests  
    = Arrays.asList(
    				ARG_DATE_INPUT,
    				ARG_ERRS,
                    ARG_ARGS,
    		        ARG_DATES,
                    ARG_TRACE,
                    ARG_FILESEARCH,   
                    ARG_FIO,
    		        ARG_DBGFLAGS,
    		        ARG_DEBUGINFO,
                    ARG_GUIINFO,
                    ARG_HELPT,
                    ARG_SLEEP,
                    ARG_TRACETHREAD,
                    ARG_WINDOWRPT
                   );


	// Classes being tested will have their "Debug Status" set in the 
    // debug.ini file (or will be set "globally" by a program parameter)
    private Args_Test           argsTest;
//    private Dates_Test          datesTest;
    private Dates_Input         datesInput;
//	private Errs_Test           errsTest;
//    private FileSearch_Test		fileSearchTest;
//    private Help_Test           helpTest;

// needed?    
    private final static String  about      = "/resources/about.txt"; // help

    /**
     * The mail class controls the obtaining of the information to determine
     * which tests to run and how to run them. Specific "Test Classes" will be
     * written and executed to exercise the individual classes to be tested.<p> 
     * This class prints out the date, file of the program being run, 
     * along with the version; gets and parses the command line arguments, then
     * runs the requested tests.
     *
     * @param clArgs command line arguments as String[] passed in by the System
     */
    public JUtils_Tester(String[] clArgs) { 

		// Get and format the current date/time
		startTime =         // starting time stamp 
    			new SimpleDateFormat("dd-MMM-yyyy HH:mm.sss").
    			format(Calendar.getInstance().getTime());

		// everything to SysOut for now
		TR = new Trace((String)null, null);  

        // print out the program file, version, and Start Time first
		TR.printMsgln(String.format("%s\t%s\t%s\n", 
			          JUtils_Tester.class.getProtectionDomain().
			          getCodeSource().getLocation().getFile(),
			          version,
			          startTime));


    	// Get Arguments, Overrides for the main testing program as well as
        // as the params for the Test Classes and the classes being
        // tested
    	if(!getCmdLineArgs(clArgs)) {
    		// Something went wrong,
    		// Tell the user, along with the error if present, and quit
    		if (err.eType == Errs.ETYPE_NONE)
    			TR.printMsgln(  "\nProblem with Arguments");
    		else
    			TR.printErr(err,"\nProblem with Arguments");

    		TR.printMsgln("\n*Args*--end--*Args*\n\n");

    		System.exit(1);
    	}

    	// print out only if debugging
        if (pgmDFlag) { 
            args.infoShow("All Main Program Arguments", ARGFields);
            //TODO  print Debugging parameters
            TR.printMsgln("");
        }

    	
        // need help?
        if (needHelp) {
            fileHelp();
    		System.exit(0);
        }

        // do we only want to show the version?
    	if (versionOnly) { 
    		LinkedHashMap<String, String>  vList = new LinkedHashMap<String, String> (); 
    		getAllVersions(vList); 
    		// print out the program file, version, and Start Time first
    		showVersion("Called Classes Version Info", vList);
    		TR.printMsgln("v--end--v");  // Good bye
    		System.exit(0);
    	}

    	// Launch the appropriate tests
    	if (!testPackageClasses()) {
    		// Something went wrong,
    		// Tell the user, along with the error if present, and quit
    		if (err.eType == Errs.ETYPE_NONE)
    			TR.printMsgln(  "\nProblem with Test Arguments");
    		else
    			TR.printErr(err,"\nProblem with Test Arguments");

    		TR.printMsgln("\n*TestArgs*--end--*TestArgs*\n\n");

    		System.exit(1);
    	}
    	TR.close();
    	TR.printMsgln("o--end--o");  // Good bye
        
    }
    
	//-------------------------------------------------------------------------
	// Parameter and Configuration Processing methods
	//-------------------------------------------------------------------------
    /**
     * Extracts command line arguments needed to run this Test Program. Not all
     * arguments are obtained, as the tests to run will be obtained later.
     * 
     * @param clArgs  String array of command line arguments
     * @return boolean flag where <code>true</code> indicates Parameters were 
     *                      set correctly; while <code>false</code> indicates 
     *                      there was an error with the process
     */
    private boolean getCmdLineArgs(String[] clArgs) {

		// Define and set up the Arguments
		if (!setupArgs())   return false;

   	
        // if nothing passed, just return, as the defaults are already set
        if (clArgs.length == 0 ) {
       		args.infoShow("Default Arguments", ARGFields); // show the args
            return true;
        }

    	// parse all the values
        if (!args.parseArgs(clArgs)) {
        	err = args.getErrs();
        	return false;
        }

        Boolean bVal = null;
        
        // check for version checking
        bVal = args.getB(ARG_VERSION);
    	err = args.getErrs();
    	if (err.eType == Errs.ETYPE_ERROR) return false;
    	if (err.eType == Errs.ETYPE_NONE)  versionOnly = bVal;
    	
    	// check for debugging (Tracing) of the Test Program
        bVal = args.getB(ARG_DEBUG);
       	err = args.getErrs();  // check for error
       	if (err.eType == Errs.ETYPE_ERROR) return false;
       	if (err.eType == Errs.ETYPE_NONE)  pgmDFlag = bVal;
       	
        // If we are tracing the Test Program, see if there is a
        // Trace File Name override
        if (pgmDFlag) {
        	String tfName = args.getS(ARG_TRACEF);
        	err = args.getErrs();
        	if (err.eType != Errs.ETYPE_NONE) return false;
        	// override the trace file name if passed
        	if (notNullOrEmpty(tfName))
        		pgmTrace = tfName;
        }
        
        // check for needing help
        bVal = args.getB(ARG_HELP);
       	err = args.getErrs();  // check for error
       	if (err.eType == Errs.ETYPE_ERROR) return false;
       	if (err.eType == Errs.ETYPE_NONE)  needHelp = bVal;

        
        return true;
    }

    /** 
     * Defines the parameters that can be passed to the program
     * 
    * @return boolean flag where <code>true</code> indicates Parameters were 
     *                      set up correctly; 
     *                      while <code>false</code> indicates there was an
     *                      error with the process
     */
    private boolean setupArgs() {
    	args     = new Args(TR);
    	int nbrErrs = 0;
    	
    	for (String key: ARGFields ) {
    		switch (key) {
    		// Test Program params
    		case ARG_HELP: 
    			args.defineArg(ARG_HELP,        "Help", 
    					       Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_VERSION: 
    			args.defineArg(ARG_VERSION,     "Version Only", 
    					       Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_DEBUG:   
    			args.defineArg(ARG_DEBUG,       "Debug Mode",
    					       Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_TRACEF:   
    			args.defineArg(ARG_TRACEF,      "Trace File Name",
			                   Args.AT_STRING  ); 
                break;

    		// How the tests are run
    		case ARG_TEST_DEBUG:     
    			args.defineArg(ARG_TEST_DEBUG,  "Tests run in Debug",
    					       Args.AT_BOOLEAN, true); 
    			break;

    		case ARG_TEST_TRACEF:
    			args.defineArg(ARG_TEST_TRACEF, "Test Trace File Name",
    					       Args.AT_STRING);
    			break;
    			
    		// Which tests to run
    		case ARG_ALL:   
    			args.defineArg(ARG_ALL,         "Run All Tests",
					           Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_ARGS:   
    			args.defineArg(ARG_ARGS,        "Argument Test",
					           Args.AT_BOOLEAN, true); 
    			break;

    		case ARG_DATES:  
    			args.defineArg(ARG_DATES,       "Date Test",
				               Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_DATE_INPUT:
    			args.defineArg(ARG_DATE_INPUT,   "Date Input Test", 
    					       Args.AT_BOOLEAN,  true);
    			break;
    		case ARG_DBGFLAGS:  
    			args.defineArg(ARG_DBGFLAGS,    "Debug Flags Test",
				               Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_ERRS:     
    			args.defineArg(ARG_ERRS,        "Error Handling Test",
    					       Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_DEBUGINFO:
    			args.defineArg(ARG_DEBUGINFO,   "Debug Info Test",
					           Args.AT_BOOLEAN, true); 
    			break;

    		case ARG_FILESEARCH:  
    			args.defineArg(ARG_FILESEARCH,  "File Search Test",
					           Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_FIO:  
    			args.defineArg(ARG_FIO,         "File I/O Test",
					           Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_GUIINFO:  
    			args.defineArg(ARG_GUIINFO,     "GUI Info Test",
					           Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_HELPT:  
    			args.defineArg(ARG_HELPT,       "Help Test",
					           Args.AT_BOOLEAN, true); 
    			break;
    		case ARG_SLEEP:
    			args.defineArg(ARG_SLEEP,       "Sleep Test",
				           Args.AT_BOOLEAN,     true); 
    			break;
    		case ARG_TRACE:
    			args.defineArg(ARG_TRACE,       "Trace Test",
				           Args.AT_BOOLEAN, 	true); 
    			break;
    		case ARG_TRACETHREAD:
    			args.defineArg(ARG_TRACETHREAD, "TraceThread Test",
				           Args.AT_BOOLEAN, 	true); 
    			break;
    		case ARG_WINDOWRPT:
    			args.defineArg(ARG_WINDOWRPT,   "Window Report Test",
				           Args.AT_BOOLEAN, 	true); 
    			break;
    			
            // something internal went wrong! List not setup
   			default:           
   	    		TR.printMsgln("Internal error:  " +
   	    		              "Invalid program argument defined:  " + key);
                nbrErrs++;
    		}
    		err = args.getErrs();
    		if (err.eType == Errs.ETYPE_NONE) continue;  // no errors
    		
    		TR.printErrBlock(err, "Problem Defining Argument:  " + key);
    		nbrErrs++;
    		
    	}

    	if (nbrErrs == 0) return true;
    	else              return false;
    }
    
    //-------------------------------------------------------------------------
    // Help Methods
    //-------------------------------------------------------------------------
    /**
     * Read the appropriate help file, append it to the passed StringBuilder
     *  and returns the combined StringBuilder
     * @param fName  String of the resource file to be read
     * @param helpBuffer Existing StringBuilder of the help information so
     *        far, may be empty
     * @return   StringBuilder of the previous help message appended with 
     *           the help message requested
     */
    private String fileHelp() {
    	StringBuilder helpBuffer = new StringBuilder();
    	try {
    		URL resourceFile = getClass().getResource(about);
    		File file = new File(resourceFile.getFile());
    		FileReader fileReader = new FileReader(file);

    		BufferedReader bufferedReader = new BufferedReader(fileReader);
    		String line;

    		while ((line = bufferedReader.readLine()) != null) {
                TR.printMsgln(line);
    		}

    		fileReader.close();

    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	return helpBuffer.toString();
    }

    //-------------------------------------------------------------------------
    // Package Specific Tests
    //-------------------------------------------------------------------------
    /**
     * Perform the specific Class Tests, based on the parameters passed to the
     * test Program.
     */
    private boolean testPackageClasses() {
    	
    	// set up the Trace Information class for the Class Tests
    	TraceInfo ti = new TraceInfo();
    	ti.pgmDFlag = pgmDFlag;
    	ti.pgmTrace = pgmTrace;
    	ti.TR       = TR;
    	
        // check for debugging (Tracing) of the Classes being tested
    	 Boolean bVal  = args.getB(ARG_TEST_DEBUG);
       	err = args.getErrs();  // check for error
       	if (err.eType == Errs.ETYPE_ERROR) return false;
       	if (err.eType == Errs.ETYPE_NONE)  ti.testDFlag = bVal;
        // If we are tracing the classes, see if there is a
        // Trace File Name override
        if (ti.testDFlag) {
        	String tfName  = "";
        	String sVal = args.getS(ARG_TEST_TRACEF);
        	err = args.getErrs();
        	if (err.eType == Errs.ETYPE_ERROR) return false;
        	if (err.eType == Errs.ETYPE_NONE)  tfName = sVal;
        	// override the "null" trace file name, if not passed will be ""
       		ti.testTrace = tfName;
    		// Create a trace class for the Classes being tested
        }
    	// create the Trace for the tested Classes
		ti.TTR = new Trace(ti.testTrace, null);  
        // load the debug flags, showing the values
        ti.TTR.getDebugFlags("", false);

        // if we are debugging, show the Debug parms for all the tests
        if (pgmDFlag) 
        	ti.TTR.showFlags();

        // Launch the appropriate tests
    	boolean testAll       = false;
        boolean dateInputTest = false;
        
        bVal = args.getB(ARG_ALL);
       	err = args.getErrs();  // check for error
       	if (err.eType == Errs.ETYPE_ERROR) return false;
       	if (err.eType == Errs.ETYPE_NONE)  testAll = bVal;
        bVal = args.getB(ARG_DATE_INPUT);
       	err = args.getErrs();  // check for error
       	if (err.eType == Errs.ETYPE_ERROR) return false;
       	if (err.eType == Errs.ETYPE_NONE)  dateInputTest = bVal;

       	if (dateInputTest) testAll = false; //if testing date input, don't test all!

    	for (String test: Tests) {

    		// doing this test?
    		boolean doThisTest = false;
            bVal = args.getB(test);
           	err = args.getErrs();  // check for error
           	if (err.eType == Errs.ETYPE_ERROR) return false;
           	if (err.eType == Errs.ETYPE_NONE)  doThisTest = bVal;
    		
    		
    		if (!testAll && !doThisTest) {
//    			System.out.println("----------------------------------------------------------");
    			System.out.println("  >>>>>>>> " + args.title(test) + " Not Run");
//    			System.out.println("----------------------------------------------------------");
    			continue;
    		}

    		switch (test) {
    		case ARG_ARGS:
    			if (testAll || doThisTest) {
    				argsTest = new Args_Test(ti);
    				argsTest.performTests();
    			}
    			break;


    		case ARG_DATE_INPUT: // ongoing
    			if (!testAll && doThisTest) {
    				datesInput = new Dates_Input(ti);
    				datesInput.performTests();
    			}
    			break;
 /*   			
    		case ARG_ERRS:   // completed
    			if (testAll || doThisTest) {
    				errsTest = new Errs_Test(ti);
    				errsTest.performTests();
    			}
    			break;

    		case ARG_DATES: // completed
    			if (testAll || doThisTest) {
    				datesTest = new Dates_Test(ti);
    				datesTest.performTests();
    			}
    			break;
    			
    		case ARG_FIO:  
    			if (testAll || doThisTest) {
//    				fioT = new FIO_Test(ti);
//    				fioT.performTests();
    			}
    			break;

    		case ARG_FILESEARCH:   // completed
    			if (testAll || doThisTest) {
    				fileSearchTest = new FileSearch_Test(ti);
    				fileSearchTest.performTests();
    			}
    			break;
    		case ARG_HELPT:
    			if (testAll || doThisTest) {
    				helpTest = new Help_Test(ti);
    				helpTest.performTests();
    			}
    			break;

*/    			
    		// something internal went wrong! Invalid Test
   			default:           
   	    		TR.printMsgln("No valid test for:  " + args.title(test));
    		}

    	}   	
    	
    	// close the Test Trace file!
    	ti.TTR.close();

    	return true;
    	
    }


    //-------------------------------------------------------------------------
    // Helper/Convenience methods 
    //-------------------------------------------------------------------------
    /**
     * Simple helper function to check if a String is 
     * null or empty.
     * 
     * @param s  string value to be checked
     * @return boolean value indicating if <b>s</b> is null or an empty string
     * 
     */
    private boolean isNullOrEmpty(String s) {
    	if (s == null || s.isEmpty()) return true;
    	return false;
    }
 
	/**
	 * Simple helper function to check if a String is 
	 * null or empty.
	 * 
	 * @param s  string value to be checked
	 * @return boolean value indicating if <b>s</b> Not null AND Not an empty string
	 * 
	 */
	private boolean notNullOrEmpty(String s) {
		if (s != null && !s.isEmpty()) return true;
		return false;
	}

    //-------------------------------------------------------------------------
    // Version Methods
    //-------------------------------------------------------------------------
    /**
     * print out the version numbers of the called routines
     * 
     * @param title String value to be printed as the title of the values
     * @param vlist LinkedHashMap of mapped pairs of class file names and the version
     *                        number.
     */
    private void showVersion(String title, 
    		                        LinkedHashMap<String, String>  vlist) {
    	TR.printMsgln(title);
    	for(Entry<String, String> versn: vlist.entrySet()){
    		String sV = String.format("  %-20s%-20s\n", 
    				versn.getKey(),
    				versn.getValue());
    		TR.printMsg(sV);
    	} 
    	TR.printMsgln("");
    }
    
    //-------------------------------------------------------------------------
    // Version Methods
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
     * number.
     * @param  vlist Map of the current class/versions to be appended to
     * @return Map of the class/version values for this and all called
     *                routines
     */
	public static LinkedHashMap<String, String> 
	         getAllVersions (LinkedHashMap<String, String> vlist ) {
		String v = JUtils_Tester.class.getName();
		// if it already exists, just return
		if (vlist.containsKey(v)) {
			return vlist;
		}
		//add it to the list
		vlist.put(v, getVersion());
		
		// Add the Test Classes, classes being tested will be added by them
		vlist = Args_Test.getAllVersions(vlist);
//		vlist = Dates_Test.getAllVersions(vlist);
//		vlist = Dates_Input.getAllVersions(vlist);
//		vlist = Errs_Test.getAllVersions(vlist);
//		vlist = FileSearch_Test.getAllVersions(vlist);
//		vlist = Help_Test.getAllVersions(vlist);

		
		return vlist;
	}

}
