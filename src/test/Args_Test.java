
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import jutils.Args;
import jutils.Errs;
import jutils.Trace;

public class Args_Test {
	protected final static String    version      = "Version 03.00";
	
	// Class to be tested
	private Args  args;  

	//Test Arguments
    // Valid Arguments that can be passed to the program 
    static public final String ARG_BOOL      = "-b";    // Boolean Argument
    static public final String ARG_BOOL_T    = "-bt";    // Boolean Argument
    static public final String ARG_BOOL_F    = "-bf";    // Boolean Argument
    
    static public final String ARG_STRING    = "-s";    // String Argument
    static public final String ARG_STRING_T  = "-st";    // String Argument
    static public final String ARG_STRING_F  = "-sf";    // String Argument
    static public final String ARG_STRING_FL = "-sfl";    // String Argument
    static public final String ARG_STRING_L  = "-sl";    // String Argument
    
    static public final String ARG_FLAG     = "-flg";  // Flag Argument

    static public final String ARG_FLOAT    = "-flt";  // Float Argument 
    static public final String ARG_FLOAT_T  = "-fltt";  // Float Argument 
    static public final String ARG_FLOAT_F  = "-fltf";  // Float Argument
    static public final String ARG_FLOAT_L  = "-fltl";  // Float Argument
    
    static public final String ARG_LONG     = "-l";    // Long Argument
    static public final String ARG_LONG_T   = "-lt";    // Long Argument
    static public final String ARG_LONG_F   = "-lf";    // Long Argument

    static public final String ARG_NOTPASSED= "-x";    // String, not passed

    // Array of Argument values in the order they should be shown
    public static final List<String> ARGFields  
    = Arrays.asList(ARG_BOOL,      ARG_BOOL_T,         ARG_BOOL_F,
    				ARG_STRING,    ARG_STRING_T,       ARG_STRING_F,
    				ARG_STRING_FL, ARG_STRING_L,
    				ARG_FLAG,      
    				ARG_FLOAT,     ARG_FLOAT_T,        ARG_FLOAT_F,
    				ARG_FLOAT_L,
    				ARG_LONG,      ARG_LONG_T,         ARG_LONG_F,
    				ARG_NOTPASSED);
	
	



	
	// error numbers and messages
	private   int     noEFlag      = Errs.ETYPE_NONE;
	private   int     errFlag      = Errs.ETYPE_ERROR;
	private   int     warnFlag     = Errs.ETYPE_WARN;
	
	private   int     noError      = 0;
	private   String  noMessage    = "";
	
	private   String  BAD_TYPE     = "Complex";
	private   String  NOT_DEFINED  = "-nope";
	

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
	public Args_Test(TraceInfo ti) {
    	// extract the trace information needed
    	TR  = ti.TR;  // Trace for THIS class
    	TTR = ti.TTR; // Trace for the tested class
    	
    	err.initErrs();

	}

    //-------------------------------------------------------------------------
    // Testing the Class
    //-------------------------------------------------------------------------
    public void performTests(){
		
		TR.printMsgln("----------------------------------------------------------");
		TR.printMsgln(">>>>>>>>>>>       Args Test - Start");
		TR.printMsgln("----------------------------------------------------------");
		TR.printMsgln("Version Info:");
		TR.printMsgln("  Args_Test:   " + getVersion());
		TR.printMsgln("  Args:        " + Args.getVersion());
		TR.printMsgln(" ");

		// test for constructing
		constructorTests();
		
		// test defining arguments
		defineTests();
		
		// test Argument Parsing
		argParsing();
        
        // test Argument value getting
		argGets();
        
        // test Argument value conversions
		argConversions();
        
		// show info call... nothing to check
		args.infoShow("Args_Test call to infoShow", ARGFields);
        
        TR.printMsgln(" ");
		TR.printMsgln("----------------------------------------------------------");
		TR.printMsgln(">>>>>>>>>>>       Args Test - Finished");
		TR.printMsgln("----------------------------------------------------------");
	}


    //-------------------------------------------------------------------------
    // Args constructor test
    //-------------------------------------------------------------------------
    /**
     * Verifies the correct error is returned when creating the class
     * 
     */
    private void constructorTests() {
    	TR.printMsgln("Constuctor Testing");
    	// pass a null to the constructor
    	args = new Args(null);
    	checkErr(errFlag, 9000, 
    			"Internal:  Trace file is NULL!"); 

		// pass a valid value
    	args = new Args(TTR);
    	checkErr(noEFlag, noError, noMessage); 
    	
		
    
    }

    //-------------------------------------------------------------------------
    // Args Defining test
    //-------------------------------------------------------------------------
    /**
     * Verifies the correct error is returned when creating the class
     * 
     */
    private void defineTests() {
    	TR.printMsgln("Defining Args Testing");
    	boolean retVal;
    	
		//-------------------------------------------------------------
		// define some valid ones!
		//-------------------------------------------------------------
    	TR.printMsgln("  Valid Defining Args Testing");
    	// create a new Args instance
    	args = new Args(TTR);
		
    	// define a valid boolean argument
		retVal = args.defineArg(ARG_BOOL,        "Bool with no Def",   Args.AT_BOOLEAN); 
    	checkErrs(retVal, true, noEFlag, noError, noMessage); 

		retVal = args.defineArg(ARG_BOOL_T,      "Bool with True Def", Args.AT_BOOLEAN, true); 
    	checkErrs(retVal, true, noEFlag, noError, noMessage); 
		
    	retVal = args.defineArg(ARG_BOOL_F,      "Bool with Faslse Def", Args.AT_BOOLEAN, false); 
    	checkErrs(retVal, true, noEFlag, noError, noMessage); 

    	retVal = args.defineArg(ARG_STRING,      "String Param", Args.AT_STRING); 
    	checkErrs(retVal, true, noEFlag, noError, noMessage); 

    	// Define a valid Flag
    	retVal = args.defineArg(ARG_FLAG,        "Flag Param",   Args.AT_FLAG);
    	checkErrs(retVal, true, noEFlag, noError, noMessage); 

    	// Define a valid Float
    	retVal = args.defineArg(ARG_FLOAT,       "Float Param",  Args.AT_FLOAT); 
    	checkErrs(retVal, true, noEFlag, noError, noMessage); 

    	// Define a valid Long
    	retVal = args.defineArg(ARG_LONG,        "Long Param",   Args.AT_LONG); 
    	checkErrs(retVal, true, noEFlag, noError, noMessage); 
		
		//-------------------------------------------------------------
		// now we know they work, let's check the error conditions!
		//-------------------------------------------------------------
    	TR.printMsgln("  Invalid Defining Args Testing");
    	// create a new Args instance
    	args = new Args(TTR);
   	
    	// define a valid boolean argument
    	// -- attempt to redefine the same one again
		args.defineArg(ARG_BOOL,          "Help",  Args.AT_BOOLEAN, true); 
		err.initErrs();  // ignore the errors
		retVal = args.defineArg(ARG_BOOL, "xxx", Args.AT_BOOLEAN, true); 
		checkErrs(retVal, false, errFlag, 9030, 
				"Internal:  Duplicate Argument defined:  " + ARG_BOOL); 
    	
    	// -- pass an invalid Argument Type
		retVal = args.defineArg(ARG_STRING,      "xxxxxxx", BAD_TYPE); 
		checkErrs(retVal, false, errFlag, 9035, 
				"Internal:  Unknown Argument type:  " + BAD_TYPE); 

    
    }

    //-------------------------------------------------------------------------
    // Args Parsing Test
    //-------------------------------------------------------------------------
    private void argParsing() {

    	TR.printMsgln("Parsing Arg String Testing");
    	// test specific variables
    	String[] tstArgs = null;
    	Boolean  retVal;
    	
    	// define some args
    	setupArgs();
    	
    	
    	// parse a null arg string
    	tstArgs = null;
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, true, noEFlag, noError, noMessage);
    	
		// Parse an empty Arg String
    	tstArgs = new String[]{};
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, true, noEFlag, noError, noMessage); 
    	
		// Parse an valid Arg String
    	tstArgs = new String[]{"-bt", "-s", "validstring",
    			               "-flg", "validFlag",
    			               "-flt", "1.0", 
    			               "-l",   "5000"
    			              };
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, true, noEFlag, noError, noMessage); 

    	//Parse a boolean Arg, with a string after it
    	tstArgs = new String[]{"-b", "validstring",
	               			   "-flg", "validFlag",
	               			   "-flt", "1.0", 
	               			   "-l",   "5000"
	              			  };
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, false, errFlag, 10001, 
    			   "Invalid arguments::\nInvalid Boolean value for:  " +
    			   ARG_BOOL +
    			   "    passed value:  validstring\n");
    	
    	
    	//Parse a String Arg with nothing after
    	tstArgs = new String[]{"-bt", 
    							"-s",
    			   			   "-flg", "validFlag",
    			   			   "-flt", "1.0", 
    			   			   "-l",   "5000"
   			  				  };
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, false, errFlag, 10001, 
    			   "Invalid arguments::\nMissing value for argument           :  " +
    			   ARG_STRING +
    			   "\tString Param\n");

    	//Parse a String with a "number" after it 
    	// treated as a string
    	tstArgs = new String[]{"-bt", 
    						   "-s", "400",
    			   			   "-flg", "validFlag",
    			   			   "-flt", "1.0", 
    			   			   "-l",   "5000"
   			  				  };
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, true, noEFlag, noError, noMessage);
    	
    	//Parse a Flag with a two character String after it
    	// Flags are strings it's up to calling program to slice/dice it
    	tstArgs = new String[]{"-bt", 
    			               "-s", "valid String value",
	   			   			   "-flg", "flag fart",
	   			   			   "-flt", "1.0", 
	   			   			   "-l",   "5000"
    	};
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, true, noEFlag, noError, noMessage);

    	//Parse a Float with an Integer after
    	// will convert it to a "float"
    	tstArgs = new String[]{"-bt", 
    			               "-s", "valid String value",
    						   "-flg", "validFlag",
    						   "-flt", "1", 
    						   "-l",   "5000"
    	};
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, true, noEFlag, noError, noMessage);

    	
    	// Parse a Float with a non-numeric string after it
    	tstArgs = new String[]{ "-bt", 
    			                "-s", "valid String value",
				   				"-flg", "validFlag",
				   				"-flt", "stringValue", 
				   				"-l",   "5000"
    	};
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, false, errFlag, 10001, 
    			"Invalid arguments::\n"                +
    			"Invalid Float value for  :  -flt"    + 
				"    passed value:  stringValue\n");
 	
    	//Parse an Long with an Decimal number after
    	tstArgs = new String[]{"-bt", 
    			               "-s", "valid String value",
    						   "-flg", "validFlag",
    						   "-flt", "2.0", 
    						   "-l",   "5.000"
    	};
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, false, errFlag, 10001, 
    			"Invalid arguments::\n"                +
    			"Invalid Long value for   :  -l"    + 
				"    passed value:  5.000\n");
    	
    	// Parse a Long with a non-numeric string after it
    	tstArgs = new String[]{ "-bt", 
    			                "-s", "valid String value",
				   				"-flg", "validFlag",
				   				"-flt", "2.0", 
				   				"-l",   "StringValue"
    	};
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, false, errFlag, 10001,
    			"Invalid arguments::\n"                +
    			"Invalid Long value for   :  -l"    + 
				"    passed value:  StringValue\n");
    	
    	// Parse a Float with a non-numeric string after it AND
    	// Parse a Long with a non-numeric string after it
    	tstArgs = new String[]{ "-bt", 
    			                "-s", "valid String value",
				   				"-flg", "validFlag",
				   				"-flt", "stringValue", 
				   				"-l",   "A String Value"
    	};
    	retVal =  args.parseArgs(tstArgs);
    	checkErrs(retVal, false, errFlag, 10002,
    			"Invalid arguments::\n"                +
    			"Invalid Float value for  :  -flt"    + 
				"    passed value:  stringValue\n"    +
    			"Invalid Long value for   :  -l"    + 
				"    passed value:  A String Value\n");
    	

    	
    	
    }
    //-------------------------------------------------------------------------
    // Arg Getting Tests
    //-------------------------------------------------------------------------
    private void argGets() {
    	TR.printMsgln("Arg information getting");
    	// test specific variables
    	String[] tstArgs = null;
    	Boolean  retVal;
    	String   retValS;
    	Long     retValL;
    	Float    retValFL;
    	
    	
    	// define some args
    	setupArgs();
		// Parse an valid Arg String
    	tstArgs = new String[]{"-bt", 
    			               "-s", "validstring",
    			               "-flg", "validFlag",
    			               "-flt", "1.0", 
    			               "-l",   "5000"
    			              };
    	retVal = args.parseArgs(tstArgs);
    	checkErrs(retVal, true, noEFlag, noError, noMessage);

    	// was the param passed (for an undefined parameter)?
    	retVal = args.passed(NOT_DEFINED);
		checkErrs(retVal, null, errFlag, 9005, 
				"Argument not defined:  " + NOT_DEFINED); 
    	
		// was the not passed argument passed?
    	retVal = args.passed(ARG_NOTPASSED);
		checkErrs(retVal, false, noEFlag, noError, noMessage); 
    	
		// was the String argument passed?
    	retVal = args.passed(ARG_STRING);
		checkErrs(retVal, true, noEFlag, noError, noMessage); 
    	
    	// get the title for something that wasn't defined
    	retValS = args.title(NOT_DEFINED);
		checkErrs(retValS, null, errFlag, 9010, 
				"Argument not defined:  " + NOT_DEFINED); 
  
		// what is the title for the "not passed" argument?
    	retValS = args.title(ARG_NOTPASSED);
		checkErrs(retValS, "Not passed in Tests", noEFlag, noError, noMessage);
		
    	// get the title for something that was passed
    	retValS = args.title(ARG_STRING);
		checkErrs(retValS, "String Param", noEFlag, noError, noMessage);
  
		// getS checking
		TR.printMsgln("  getS - Getting String Values of Args");
		// get String value for Not Defined
    	retValS = args.getS(NOT_DEFINED);
		checkErrs(retValS, null, errFlag, 9100, 
				   "Argument not defined:  -nope");
		// get String value for not passed (default value)
    	retValS = args.getS(ARG_NOTPASSED);
		checkErrs(retValS, null, warnFlag, 9102, "Arg not passed:  " + ARG_NOTPASSED);
		// get String value for the String Param
    	retValS = args.getS(ARG_STRING);
		checkErrs(retValS, "validstring", noEFlag, noError, noMessage);
		// get String value for the Boolean Param
    	retValS = args.getS(ARG_BOOL_T);
		checkErrs(retValS, "true", noEFlag, noError, noMessage);
		// get String value for the Flag Param
    	retValS = args.getS(ARG_FLAG);
		checkErrs(retValS, "validFlag", noEFlag, noError, noMessage);
		// get String value for the Float Param
    	retValS = args.getS(ARG_FLOAT);
		checkErrs(retValS, "1.0", noEFlag, noError, noMessage);
		// get String value for the Long Param
    	retValS = args.getS(ARG_LONG);
		checkErrs(retValS, "5000", noEFlag, noError, noMessage);
		
		// getB checking
		TR.printMsgln("  getB - Getting Boolean Values of Args");
		// get Boolean value for Not Defined
    	retVal = args.getB(NOT_DEFINED);
		checkErrs(retVal, null, errFlag, 9110, 
				   "Argument not defined:  -nope");
		// get Boolean value for not passed (default value)
    	retVal = args.getB(ARG_NOTPASSED);
		checkErrs(retVal, null, warnFlag, 9114, 
				  "Argument was not passed:  -x");
		// get Boolean value for the String Param
    	retVal = args.getB(ARG_STRING);
		checkErrs(retVal, null, warnFlag, 9112, 
				 "Argument value is not a valid Boolean\t" + 
 	             "Argmument:  "        + ARG_STRING      +
 	             "    Passed Value:  validstring");
		// get Boolean value for the Boolean Param
    	retVal = args.getB(ARG_BOOL_T);
		checkErrs(retVal, true, noEFlag, noError, noMessage);
		// get Boolean value for the Flag Param
    	retVal = args.getB(ARG_FLAG);
		checkErrs(retVal, null, warnFlag, 9112, 
				 "Argument value is not a valid Boolean\t" + 
		 	     "Argmument:  "        + ARG_FLAG      +
		 	     "    Passed Value:  validFlag");
		// get Boolean value for the Float Param
    	retVal = args.getB(ARG_FLOAT);
		checkErrs(retVal, true, noEFlag, noError, noMessage);
		// get Boolean value for the Long Param
    	retVal = args.getB(ARG_LONG);
		checkErrs(retVal, true, noEFlag, noError, noMessage);

		// getL checking
		TR.printMsgln("  getL - Getting Long Values of Args");
		// get Long value for Not Defined
    	retValL = args.getL(NOT_DEFINED);
		checkErrs(retValL, null, errFlag, 9120, 
				   "Argument not defined:  -nope");
		// get Long value for not passed (default value)
    	retValL = args.getL(ARG_NOTPASSED);
		checkErrs(retValL, null, warnFlag, 9124, 
				  "Argument was not passed:  -x");
		// get Long value for the String Param
    	retValL = args.getL(ARG_STRING);
		checkErrs(retValL, null, warnFlag, 9122, 
				 "Argument value is not a valid Long\t" + 
		 	             "Argmument:  "        + ARG_STRING      +
		 	             "    Passed Value:  validstring");
		// get Long value for the Boolean Param
    	retValL = args.getL(ARG_BOOL_T);
		checkErrs(retValL, 1L, noEFlag, noError, noMessage);

		// get Long value for the Flag param
		retValL = args.getL(ARG_FLAG);
		checkErrs(retValL, null, warnFlag, 9122, 
				 "Argument value is not a valid Long\t" + 
				 	     "Argmument:  "        + ARG_FLAG      +
				 	     "    Passed Value:  validFlag");
		// get Long value for the Float Param
    	retValL = args.getL(ARG_FLOAT);
		checkErrs(retValL, 1L, noEFlag, noError, noMessage);

		// get Long value for the Long Param
    	retValL = args.getL(ARG_LONG);
		checkErrs(retValL, 5000L, noEFlag, noError, noMessage);
		
		// getF checking
		TR.printMsgln("  getF - Getting Float Values of Args");
		// get Float value for Not Defined
    	retValFL = args.getF(NOT_DEFINED);
		checkErrs(retValFL, null, errFlag, 9140, 
				   "Argument not defined:  -nope");
		// get Float value for not passed (default value)
    	retValFL = args.getF(ARG_NOTPASSED);
		checkErrs(retValFL, null, warnFlag, 9144, 
				  "Argument was not passed:  -x");
		// get Float value for the String Param
    	retValFL = args.getF(ARG_STRING);
		checkErrs(retValFL, null, warnFlag, 9142, 
				 "Argument value is not a valid Float\t" + 
		 	             "Argmument:  "        + ARG_STRING      +
		 	             "    Passed Value:  validstring");
		// get Float value for the Boolean Param
    	retValFL = args.getF(ARG_BOOL_T);
		checkErrs(retValFL, 1.0F, noEFlag, noError, noMessage);
		
		// get Float value for the Flag Param
    	retValFL = args.getF(ARG_FLAG);
		checkErrs(retValFL, null, warnFlag, 9142, 
				 "Argument value is not a valid Float\t" + 
				 	     "Argmument:  "        + ARG_FLAG      +
				 	     "    Passed Value:  validFlag");
		// get Float value for the Float Param
    	retValFL = args.getF(ARG_FLOAT);
		checkErrs(retValFL, 1.0f, noEFlag, noError, noMessage);
		// get Float value for the Long Param
    	retValFL = args.getF(ARG_LONG);
		checkErrs(retValFL, 5000.0f, noEFlag, noError, noMessage);
//		9115, 
//				  "Internal:  Argument is not a float:  -l");
		
		// validParam checking
		TR.printMsgln("  validParam - validating Param");
		// get validate Not Defined
    	retVal = args.vaildParam(NOT_DEFINED);
		checkErrs(retVal, false, errFlag, 9160, 
				   "Argument not defined:  -nope");
		// get validate not passed
    	retVal = args.vaildParam(ARG_NOTPASSED);
		checkErrs(retVal, true, noEFlag, noError, noMessage);
		// get valdate the String Param
    	retVal = args.vaildParam(ARG_STRING);
		checkErrs(retVal, true, noEFlag, noError, noMessage);

		// get Flag...
		// not fully implemented. so not tested
		
    }
    //-------------------------------------------------------------------------
    // Arg Conversion Testing
    //-------------------------------------------------------------------------
    private void argConversions() {
    	TR.printMsgln("Arg Conversions");
    	// test specific variables
    	String[] tstArgs = null;
    	Boolean  bVal;
    	String   sVal;
    	Long     lVal;
    	Float    fVal;
    	Integer  iVal;
    	String   arg;
    	String   aval;
    	// define some args
    	setupArgs();

    	//====================================================================
    	// Getting booleans  as different types
    	//====================================================================

		TR.printMsgln("  Boolean - Getting values of different types");
    	// Parse an valid Arg String
    	tstArgs = new String[]{"-b",   "false", 
    			               "-bt",
    			               "-bf"
    			              };
    	bVal = args.parseArgs(tstArgs);
		checkErrs(bVal, true, noEFlag, noError, noMessage); 

		// getB boolean True values checking
		TR.printMsgln("  Boolean = true Default  Getting values of different types");
		arg = ARG_BOOL_T;
		// get Boolean value as a Boolean 
    	bVal = args.getB(arg);
		checkErrs(bVal, true,   noEFlag, noError, noMessage);
		// get Boolean value as a String
    	sVal = args.getS(arg);
		checkErrs(sVal, "true", noEFlag, noError, noMessage); 
		// get Boolean value as Flag
    	sVal = args.getFLag(arg);
		checkErrs(sVal, null, warnFlag, 9152, 
					"Argument cannot be converted to a Flag Type:\t" +
					"Argmument:  -bt    Passed Value:  true");
		// get Boolean value as a Float
    	fVal = args.getF(arg);
		checkErrs(fVal, 1.0f,   noEFlag, noError, noMessage); 
		// get Boolean value as a Long
    	lVal = args.getL(arg);
		checkErrs(lVal, 1L,     noEFlag, noError, noMessage);
		// get Boolean value as an Integer
    	iVal = args.getI(arg);
		checkErrs(iVal, 1,      noEFlag, noError, noMessage);
		
		// getB boolean False values checking
		TR.printMsgln("  Boolean = false Default  Getting values of different types");
		arg = ARG_BOOL_F;
		// get Boolean value as a Boolean 
    	bVal = args.getB(arg);
		checkErrs(bVal, false,   noEFlag, noError, noMessage);
		// get Boolean value as a String
    	sVal = args.getS(arg);
		checkErrs(sVal, "false", noEFlag, noError, noMessage); 
		// get Boolean value as Flag
    	sVal = args.getFLag(arg);
		checkErrs(sVal, null, warnFlag, 9152, 
				"Argument cannot be converted to a Flag Type:\t" +
				"Argmument:  -bf    Passed Value:  false");
		// get Boolean value as a Float
    	fVal = args.getF(arg);
		checkErrs(fVal, 0.0f,   noEFlag, noError, noMessage); 
		// get Boolean value as a Long
    	lVal = args.getL(arg);
		checkErrs(lVal, 0L,     noEFlag, noError, noMessage);
		// get Boolean value as an Integer
    	iVal = args.getI(arg);
		checkErrs(iVal, 0,      noEFlag, noError, noMessage);
		// getL checking

		// getB boolean False values checking
		TR.printMsgln("  Boolean = 'false'  Getting values of different types");
		arg = ARG_BOOL;
		// get Boolean value as a Boolean 
    	bVal = args.getB(arg);
		checkErrs(bVal, false,   noEFlag, noError, noMessage);
		// get Boolean value as a String
    	sVal = args.getS(arg);
		checkErrs(sVal, "false", noEFlag, noError, noMessage); 
		// get Boolean value as Flag
    	sVal = args.getFLag(arg);
		checkErrs(sVal, null, warnFlag, 9152, 
				"Argument cannot be converted to a Flag Type:\t" +
				"Argmument:  -b    Passed Value:  false");
		// get Boolean value as a Float
    	fVal = args.getF(arg);
		checkErrs(fVal, 0.0f,   noEFlag, noError, noMessage); 
		// get Boolean value as a Long
    	lVal = args.getL(arg);
		checkErrs(lVal, 0L,     noEFlag, noError, noMessage);
		// get Boolean value as an Integer
    	iVal = args.getI(arg);
		checkErrs(iVal, 0,      noEFlag, noError, noMessage);

		
		
    	//====================================================================
    	// Getting Strings as different types
    	//====================================================================
		TR.printMsgln("  String - Getting values of different types");
    	// Parse an valid Arg String
    	tstArgs = new String[]{ARG_STRING,   "validString",
    						   ARG_STRING_T, "true",
    						   ARG_STRING_F, "false", 
    						   ARG_STRING_FL, "176.55",
    						   ARG_STRING_L,  "50000"
    			              };
    	bVal = args.parseArgs(tstArgs);
		checkErrs(bVal, true, noEFlag, noError, noMessage); 

		// getS String values checking
		TR.printMsgln("  String = 'validString' - Getting values of different types");
		arg = ARG_STRING;
		aval = "validString";
		// get String value as a Boolean 
    	bVal = args.getB(arg);
		checkErrs(bVal, null,  warnFlag, 9112, 
				    "Argument value is not a valid Boolean\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as a String
    	sVal = args.getS(arg);
		checkErrs(sVal, aval, noEFlag, noError, noMessage); 
		// get String value as Flag
    	sVal = args.getFLag(arg);
		checkErrs(sVal, null, warnFlag, 9152, 
					"Argument cannot be converted to a Flag Type:\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as a Float
    	fVal = args.getF(arg);
		checkErrs(fVal, null,  warnFlag, 9142, 
				    "Argument value is not a valid Float\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as a Long
    	lVal = args.getL(arg);
		checkErrs(lVal, null,  warnFlag, 9122, 
				    "Argument value is not a valid Long\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as an Integer
    	iVal = args.getI(arg);
		checkErrs(iVal, null,  warnFlag, 9134, 
 				    "Argument value is not a valid Integer\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);
		
		// getS String values checking
		TR.printMsgln("  String = 'true' - Getting values of different types");
		arg = ARG_STRING_T;
		aval = "true";
		// get String value as a Boolean 
    	bVal = args.getB(arg);
		checkErrs(bVal, true,  noEFlag, noError, noMessage);
		// get String value as a String
    	sVal = args.getS(arg);
		checkErrs(sVal, aval, noEFlag, noError, noMessage); 
		// get String value as Flag
    	sVal = args.getFLag(arg);
		checkErrs(sVal, null, warnFlag, 9152, 
					"Argument cannot be converted to a Flag Type:\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as a Float
    	fVal = args.getF(arg);
		checkErrs(fVal, null,  warnFlag, 9142, 
				   "Argument value is not a valid Float\t" +
							"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as a Long
    	lVal = args.getL(arg);
		checkErrs(lVal, null,  warnFlag, 9122, 
				   "Argument value is not a valid Long\t" +
							"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as an Integer
    	iVal = args.getI(arg);
		checkErrs(iVal, null,  warnFlag, 9134, 
				   "Argument value is not a valid Integer\t" +
							"Argmument:  " + arg + "    Passed Value:  " + aval);
		
		// getS String values checking
		TR.printMsgln("  String = 'false' - Getting values of different types");
		arg = ARG_STRING_F;
		aval = "false";
		// get String value as a Boolean 
    	bVal = args.getB(arg);
		checkErrs(bVal, false,  noEFlag, noError, noMessage);
		// get String value as a String
    	sVal = args.getS(arg);
		checkErrs(sVal, aval, noEFlag, noError, noMessage); 
		// get String value as Flag
    	sVal = args.getFLag(arg);
		checkErrs(sVal, null, warnFlag, 9152, 
					"Argument cannot be converted to a Flag Type:\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as a Float
    	fVal = args.getF(arg);
		checkErrs(fVal, null,  warnFlag, 9142, 
				   "Argument value is not a valid Float\t" +
							"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as a Long
    	lVal = args.getL(arg);
		checkErrs(lVal, null,  warnFlag, 9122, 
				   "Argument value is not a valid Long\t" +
							"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as an Integer
    	iVal = args.getI(arg);
		checkErrs(iVal, null,  warnFlag, 9134, 
				   "Argument value is not a valid Integer\t" +
							"Argmument:  " + arg + "    Passed Value:  " + aval);

		// getS String values checking
		TR.printMsgln("  String = '176.55' - Getting values of different types");
		arg = ARG_STRING_FL;
		aval = "176.55";
		// get String value as a Boolean 
    	bVal = args.getB(arg);
		checkErrs(bVal, null,  warnFlag, 9112, 
				    "Argument value is not a valid Boolean\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as a String
    	sVal = args.getS(arg);
		checkErrs(sVal, aval, noEFlag, noError, noMessage); 
		// get String value as Flag
    	sVal = args.getFLag(arg);
		checkErrs(sVal, null, warnFlag, 9152, 
					"Argument cannot be converted to a Flag Type:\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as a Float
    	fVal = args.getF(arg);
		checkErrs(fVal, 176.55F,  noEFlag, noError, noMessage);
		// get String value as a Long
    	lVal = args.getL(arg);
		checkErrs(lVal, null,  warnFlag, 9122, 
				    "Argument value is not a valid Long\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);
		// get String value as an Integer
    	iVal = args.getI(arg);
		checkErrs(iVal, null,  warnFlag, 9134, 
 				    "Argument value is not a valid Integer\t" +
					"Argmument:  " + arg + "    Passed Value:  " + aval);

			// getS String values checking
			TR.printMsgln("  String = '50000' - Getting values of different types");
			arg = ARG_STRING_L;
			aval = "50000";
			// get String value as a Boolean 
	    	bVal = args.getB(arg);
			checkErrs(bVal, null,  warnFlag, 9112, 
					    "Argument value is not a valid Boolean\t" +
						"Argmument:  " + arg + "    Passed Value:  " + aval);
			// get String value as a String
	    	sVal = args.getS(arg);
			checkErrs(sVal, aval, noEFlag, noError, noMessage); 
			// get String value as Flag
	    	sVal = args.getFLag(arg);
			checkErrs(sVal, null, warnFlag, 9152, 
						"Argument cannot be converted to a Flag Type:\t" +
						"Argmument:  " + arg + "    Passed Value:  " + aval);
			// get String value as a Float
	    	fVal = args.getF(arg);
			checkErrs(fVal, 50000F,  noEFlag, noError, noMessage);
			// get String value as a Long
	    	lVal = args.getL(arg);
			checkErrs(lVal, 50000L,  noEFlag, noError, noMessage);
			// get String value as an Integer
	    	iVal = args.getI(arg);
			checkErrs(iVal, 50000,  noEFlag, noError, noMessage);

	    	//====================================================================
	    	// Getting Floats as different types
	    	//====================================================================
			TR.printMsgln("  String - Getting values of different types");
	    	// Parse an valid Arg String
	    	tstArgs = new String[]{ARG_FLOAT,   "13.6F",
	    						   ARG_FLOAT_T, "1.0",
	    						   ARG_FLOAT_F, "0F", 
	    						   ARG_FLOAT_L,  "50000F"
	    			              };
	    	bVal = args.parseArgs(tstArgs);
			checkErrs(bVal, true, noEFlag, noError, noMessage); 

			arg = ARG_FLOAT;
			aval = "13.6F";
			TR.printMsgln("  Float = '" + aval + "' - Getting values of different types");
			// get Float value as a Boolean 
	    	bVal = args.getB(arg);
			checkErrs(bVal, true, noEFlag, noError, noMessage);
			// get Float value as a String
	    	sVal = args.getS(arg);
			checkErrs(sVal, aval, noEFlag, noError, noMessage); 
			// get Float value as Flag
	    	sVal = args.getFLag(arg);
			checkErrs(sVal, null, warnFlag, 9152, 
						"Argument cannot be converted to a Flag Type:\t" +
						"Argmument:  " + arg + "    Passed Value:  " + aval);
			// get Float value as a Float
	    	fVal = args.getF(arg);
			checkErrs(fVal, 13.6F,  noEFlag, noError, noMessage);
			// get Float value as a Long
	    	lVal = args.getL(arg);
			checkErrs(lVal, null,  warnFlag, 9122, 
					    "Argument value is not a valid Long\t" +
						"Argmument:  " + arg + "    Passed Value:  " + aval);
			// get Float value as an Integer
	    	iVal = args.getI(arg);
			checkErrs(iVal, null,  warnFlag, 9134, 
	 				    "Argument value is not a valid Integer\t" +
						"Argmument:  " + arg + "    Passed Value:  " + aval);


			arg = ARG_FLOAT_T;
			aval = "1.0";
			TR.printMsgln("  Float = '" + aval + "' - Getting values of different types");
			// get Float value as a Boolean 
	    	bVal = args.getB(arg);
			checkErrs(bVal, true, noEFlag, noError, noMessage);
			// get Float value as a String
	    	sVal = args.getS(arg);
			checkErrs(sVal, aval, noEFlag, noError, noMessage); 
			// get Float value as Flag
	    	sVal = args.getFLag(arg);
			checkErrs(sVal, null, warnFlag, 9152, 
						"Argument cannot be converted to a Flag Type:\t" +
						"Argmument:  " + arg + "    Passed Value:  " + aval);
			// get Float value as a Float
	    	fVal = args.getF(arg);
			checkErrs(fVal, 1F,  noEFlag, noError, noMessage);
			// get Float value as a Long
	    	lVal = args.getL(arg);
			checkErrs(lVal, 1L,  noEFlag, noError, noMessage);
			// get Float value as an Integer
	    	iVal = args.getI(arg);
			checkErrs(iVal, 1, noEFlag, noError, noMessage);

			arg = ARG_FLOAT_F;
			aval = "0F";
			TR.printMsgln("  Float = '" + aval + "' - Getting values of different types");
			// get Float value as a Boolean 
	    	bVal = args.getB(arg);
			checkErrs(bVal, false, noEFlag, noError, noMessage);
			// get Float value as a String
	    	sVal = args.getS(arg);
			checkErrs(sVal, aval, noEFlag, noError, noMessage); 
			// get Float value as Flag
	    	sVal = args.getFLag(arg);
			checkErrs(sVal, null, warnFlag, 9152, 
						"Argument cannot be converted to a Flag Type:\t" +
						"Argmument:  " + arg + "    Passed Value:  " + aval);
			// get Float value as a Float
	    	fVal = args.getF(arg);
			checkErrs(fVal, 0F,  noEFlag, noError, noMessage);
			// get Float value as a Long
	    	lVal = args.getL(arg);
			checkErrs(lVal, 0L,  noEFlag, noError, noMessage);
			// get Float value as an Integer
	    	iVal = args.getI(arg);
			checkErrs(iVal, 0,  noEFlag, noError, noMessage);

			
			arg = ARG_FLOAT_L;
			aval = "50000F";
			TR.printMsgln("  Float = '" + aval + "' - Getting values of different types");
			// get Float value as a Boolean 
			bVal = args.getB(arg);
			checkErrs(bVal, true, noEFlag, noError, noMessage);
			// get Float value as a String
			sVal = args.getS(arg);
			checkErrs(sVal, aval, noEFlag, noError, noMessage); 
			// get Float value as Flag
			sVal = args.getFLag(arg);
			checkErrs(sVal, null, warnFlag, 9152, 
					"Argument cannot be converted to a Flag Type:\t" +
							"Argmument:  " + arg + "    Passed Value:  " + aval);
			// get Float value as a Float
			fVal = args.getF(arg);
			checkErrs(fVal, 50000F,  noEFlag, noError, noMessage);
			// get Float value as a Long
			lVal = args.getL(arg);
			checkErrs(lVal, 50000L,  noEFlag, noError, noMessage);
			// get Float value as an Integer
			iVal = args.getI(arg);
			checkErrs(iVal, 50000,  noEFlag, noError, noMessage);

			//====================================================================
			// Getting Longs as different types
			//====================================================================
			TR.printMsgln("  String - Getting values of different types");
			// Parse an valid Arg String
			tstArgs = new String[]{ARG_LONG,   "151515",
								   ARG_LONG_T, "1",
								   ARG_LONG_F, "0", 
			};
			bVal = args.parseArgs(tstArgs);
			checkErrs(bVal, true, noEFlag, noError, noMessage); 

			arg = ARG_LONG;
			aval = "151515";
			TR.printMsgln(" Long = '" + aval + "' - Getting values of different types");
			// get Long value as a Boolean 
			bVal = args.getB(arg);
			checkErrs(bVal, true, noEFlag, noError, noMessage);
			// get Long value as a String
			sVal = args.getS(arg);
			checkErrs(sVal, aval, noEFlag, noError, noMessage); 
			// get Long value as Flag
			sVal = args.getFLag(arg);
			checkErrs(sVal, null, warnFlag, 9152, 
					"Argument cannot be converted to a Flag Type:\t" +
							"Argmument:  " + arg + "    Passed Value:  " + aval);
			// get Long value as a Float
			fVal = args.getF(arg);
			checkErrs(fVal, 151515F,  noEFlag, noError, noMessage);
			// get Long value as a Long
			lVal = args.getL(arg);
			checkErrs(lVal, 151515L,  noEFlag, noError, noMessage);
			// get Long value as an Integer
			iVal = args.getI(arg);
			checkErrs(iVal, 151515,  noEFlag, noError, noMessage);
			

			arg = ARG_LONG_T;
			aval = "1";
			TR.printMsgln("  Long = '" + aval + "' - Getting values of different types");
			// get Long value as a Boolean 
			bVal = args.getB(arg);
			checkErrs(bVal, true, noEFlag, noError, noMessage);
			// get Long value as a String
			sVal = args.getS(arg);
			checkErrs(sVal, aval, noEFlag, noError, noMessage); 
			// get Long value as Flag
			sVal = args.getFLag(arg);
			checkErrs(sVal, null, warnFlag, 9152, 
					"Argument cannot be converted to a Flag Type:\t" +
							"Argmument:  " + arg + "    Passed Value:  " + aval);
			// get Long value as a Float
			fVal = args.getF(arg);
			checkErrs(fVal, 1F,  noEFlag, noError, noMessage);
			// get Long value as a Long
			lVal = args.getL(arg);
			checkErrs(lVal, 1L,  noEFlag, noError, noMessage);
			// get Long value as an Integer
			iVal = args.getI(arg);
			checkErrs(iVal, 1, noEFlag, noError, noMessage);

			arg = ARG_LONG_F;
			aval = "0";
			TR.printMsgln("  Float = '" + aval + "' - Getting values of different types");
			// get Long value as a Boolean 
			bVal = args.getB(arg);
			checkErrs(bVal, false, noEFlag, noError, noMessage);
			// get Long value as a String
			sVal = args.getS(arg);
			checkErrs(sVal, aval, noEFlag, noError, noMessage); 
			// get Long value as Flag
			sVal = args.getFLag(arg);
			checkErrs(sVal, null, warnFlag, 9152, 
					"Argument cannot be converted to a Flag Type:\t" +
							"Argmument:  " + arg + "    Passed Value:  " + aval);
			// get Long value as a Float
			fVal = args.getF(arg);
			checkErrs(fVal, 0F,  noEFlag, noError, noMessage);
			// get Long value as a Long
			lVal = args.getL(arg);
			checkErrs(lVal, 0L,  noEFlag, noError, noMessage);
			// get Long value as an Integer
			iVal = args.getI(arg);
			checkErrs(iVal, 0,  noEFlag, noError, noMessage);




    }
    //-------------------------------------------------------------------------
	// Parameter and Configuration Processing methods
	//-------------------------------------------------------------------------
    /** 
     * Defines the parameters that will be used for testing,  no checking of 
     * errors from the tested class needed, because they should already have
     * been tested!
     */
    private void setupArgs() {
    	// create a new Args instance
    	args = new Args(TTR);
    	
    	int nbrErrs = 0;
    	
    	for (String key: ARGFields ) {
    		boolean de = false;
    		boolean df = true;
    		
    		switch (key) {
    		// boolean parameters - assume no value if if passed with no values
    		case ARG_BOOL: 
    			de = args.defineArg(key,        "Boolean Param - Def null", 
    					       		Args.AT_BOOLEAN); 
    			break;
    			
      		// boolean parameters - assume true if passed with no values
        	case ARG_BOOL_T: 
        		de = args.defineArg(key,        "Boolean Param - Def True", 
        					       		Args.AT_BOOLEAN, true); 
        			break;
            // boolean parameters - assume false if passed with no values
        	case ARG_BOOL_F: 
       			de = args.defineArg(key,        "Boolean Param - Def False", 
        					       	Args.AT_BOOLEAN, false); 
        			break;
        			
    		// String Arguments 
    		case ARG_STRING: 
    			de = args.defineArg(key,      "String Param", 	Args.AT_STRING); 
    			break;
    		case ARG_STRING_T: 
    			de = args.defineArg(key,      "String 'true'",   	Args.AT_STRING); 
    			break;
    		case ARG_STRING_F: 
    			de = args.defineArg(key,      "String 'false'",       Args.AT_STRING); 
    			break;
    		case ARG_STRING_FL: 
    			de = args.defineArg(key,      "String Float value",  Args.AT_STRING); 
    			break;
    		case ARG_STRING_L: 
    			de = args.defineArg(key,      "String Long value", 	Args.AT_STRING); 
    			break;

    		case ARG_FLAG:  
    			de = args.defineArg(key,        "Flag Param",
    								Args.AT_FLAG);
    			df = args.defineFlags(key,Arrays.asList("validFlag", 
			                                            "f",
			                                            "1.3",
			                                            "flag fart",
			                                            "5000"
    		                                           ));
                break;

    		case ARG_FLOAT:           
    			de = args.defineArg(key,       "Float Param",			           		Args.AT_FLOAT); 
                break;
    		case ARG_FLOAT_T:           
    			de = args.defineArg(key,       "Float Param - 1.0 for true",       		Args.AT_FLOAT); 
                break;
    		case ARG_FLOAT_F:           
    			de = args.defineArg(key,       "Float Param - 0.0 for false",      		Args.AT_FLOAT); 
                break;
    		case ARG_FLOAT_L:           
    			de = args.defineArg(key,       "Float Param - valid Long",       		Args.AT_FLOAT); 
                break;

    		case ARG_LONG:   
    			de = args.defineArg(key,        "Long Param",			                Args.AT_LONG); 
                break;
    		case ARG_LONG_T:   
    			de = args.defineArg(key,        "Long Param - 1L for true",			    Args.AT_LONG); 
                break;
    		case ARG_LONG_F:   
    			de = args.defineArg(key,        "Long Param - 0L for false",			Args.AT_LONG); 
                break;
                
    		case ARG_NOTPASSED:   
    			de = args.defineArg(ARG_NOTPASSED,   "Not passed in Tests",            Args.AT_STRING); 
                break;
                
            // something internal went wrong! List not setup
   			default:           
   	    		de = false;
    		}
    		if (!(de && df)) {
    			err = args.getErrs();
    			if (err.eType == Errs.ETYPE_NONE) {
    				err.eNbr     = 10;
    				err.eMessage = "Testing error:  " +
 	    		                   "Invalid program argument defined:  " + key;
    				err.eType    = Errs.ETYPE_ERROR;
    			}
    			nbrErrs++;
    		}
    	}

    	if (nbrErrs == 0) return;
    	
    	// some sort of error!
    	TR.printErr(err, "Error setting up parameters to test");
		System.exit(1);
    }
    
    
    
    //-------------------------------------------------------------------------
    // Error printing/checking Methods
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    // Arg Getting Tests
    //-------------------------------------------------------------------------
	/**
	 * Verifies that the "boolean" return from the called class is equal
	 * to the expected value, then verifies the err from the called class
	 * matches the expected values
	 *
	 * @param rVal     boolean value of the actual return
	 * @param expRtn   Expected boolean return value
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param expMsg   String value of the expected Errs.eMessage
	 */ 
    private void checkErrs(Boolean rVal, Boolean expRtn, 
            int expEType, int expErr, String expMsg) {

    	assert rVal == expRtn : "\nExpected Return:  " + expRtn + 
                "    Actual:  " + rVal;
    	// get the error messages
    	checkErr(expEType, expErr, expMsg);

    }

	/**
	 * Verifies that the "float" return from the called class is equal
	 * to the expected value, then verifies the err from the called class
	 * matches the expected values
	 *
	 * @param rVal     float value of the actual return
	 * @param expRtn   Expected float return value
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param expMsg   String value of the expected Errs.eMessage
	 */ 
    private void checkErrs(Float rVal, Float expRtn, 
            int expEType, int expErr, String expMsg) {

    	if (expRtn == null)
        	assert rVal == expRtn : "\nExpected Return:  " + expRtn + 
            						"    Actual:  " + rVal;
    	else
    		assert rVal.equals(expRtn) : "\nExpected Return:  " + expRtn + 
                						"    Actual:  " + rVal;
    	// get the error messages
    	checkErr(expEType, expErr, expMsg);

    }

	/**
	 * Verifies that the "Long" return from the called class is equal
	 * to the expected value, then verifies the err from the called class
	 * matches the expected values
	 *
	 * @param rVal     long value of the actual return
	 * @param expRtn   Expected long return value
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param expMsg   String value of the expected Errs.eMessage
	 */ 
    private void checkErrs(Long rVal, Long expRtn, 
    		                int expEType, int expErr, String expMsg) {

    	if (expRtn == null)
        	assert rVal == expRtn : "\nExpected Return:  " + expRtn + 
            						"    Actual:  " + rVal;
       	else
       		assert rVal.equals(expRtn) : "\nExpected Return:  " + expRtn + 
       									 "    Actual:  " + rVal;
		// get the error messages
		checkErr(expEType, expErr, expMsg);

    }

	/**
	 * Verifies that the "Integer" return from the called class is equal
	 * to the expected value, then verifies the err from the called class
	 * matches the expected values
	 *
	 * @param rVal     Integer value of the actual return
	 * @param expRtn   Expected Integer return value
	 * @param expEType int value of the expected Errs.eType
	 * @param expErr   int value of the expected Errs.eNbr
	 * @param expMsg   String value of the expected Errs.eMessage
	 */ 
    private void checkErrs(Integer rVal, Integer expRtn, 
    		                int expEType, int expErr, String expMsg) {

    	if (expRtn == null)
        	assert rVal == expRtn : "\nExpected Return:  " + expRtn + 
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
    	err = args.getErrs();
    	
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
		String v = Args_Test.class.getName();
		// if it already exists, just return
		if (vlist.containsKey(v)) {
			return vlist;
		}
		//add it to the list
		vlist.put(v, getVersion());

		vlist = Args.getAllVersions(vlist);
		
		vlist = Errs.getAllVersions(vlist);
    	vlist = Trace.getAllVersions(vlist);

    	return vlist;
	} 
}