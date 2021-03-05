package jutils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The Args class maintains the list of valid command line arguments for
 * a program, as well as the parsing and storing the values of the arguments
 * for access by the rest of the program. This is done centrally so that
 * programs can parse all the arguments once and then simply access their
 * values, and even verify if an argument has been passed.<p>
 * Different types of arguments can be defined (Strings, Longs, Floats, 
 * Booleans, and Flags. If, when passed and parsed, the value of an argument
 * does not have the correct format, an error will be produced.<p>
 * When parsed, the Argument values will also be converted to the different 
 * types, if allowed. So while an argument may be declared a "Boolean", 
 * you can get the "String" value of its value ("true" or "false") as well
 * as the Long value of it (1L or 0L). Not all conversions are allowed. 
 * i.e. not all strings will convert to a Long. In those cases, the 
 * resulting type will be set to null; when retrieved the null will be 
 * returned and a warning will be issued.<p>
 * Booleans are the only Arguments that can have a "default value" set. If
 * a defined Boolean Argument is passed with no value, the "default value
 * defined (if any) will be returned. The user can pass an optional value
 * with the argument to specify "true" or "false" and override the default
 * value.<p>
 * All other Argument types must have valid parameter.<p>
 * The Flag type is simply a string type, however in order to be a valid 
 * value passed to the program, the string argument value must match one of 
 * the predefined values in the List of  Valid flags for that Argument.
 * 
 *
 */
public class Args {

    private   static final String  version  = "Version 03.00";
    private   static final String  dFlag    = DebugInfo.DB_ARGS;
    private   static final String  dPreFix  = "Args:";

    // Parsing may result in multiple errors, 
    static private final int   err_start  = 10000; // eNbr

	// valid argument Type
    static public final String AT_STRING  = "S";   // String
    static public final String AT_BOOLEAN = "B";   // boolean
    static public final String AT_LONG    = "L";   // long
    static public final String AT_FLOAT   = "F";   // float
    static public final String AT_FLAG    = "G";   // Flag
    static private final List<String> validTypes  
    			             = Arrays.asList(AT_STRING,   
    			            		 		 AT_BOOLEAN,
    			            		 		 AT_LONG,
    			            		 		 AT_FLOAT,
    			            		 		 AT_FLAG     );
    
    /** 
     *  This class contains the detailed information about the 
     *  specific Argument and any values that might have been 
     *  passed with it
     *   
     *
     */
    class ArgDetail {

    	// Argument definition
    	String   arg      = "";    // argument as passed, i.e.: -t
    	String   title    = "";    // "Nice Name"
    	String   type     = "";    // see valid values above 
    	Boolean  valueDB  = null;  // Default Value for boolean
    	List <String> valueFL  = null;  // list of valid flag values
    	
    	// passed in parameter value
    	boolean passed   = false;  // was it passed?
    	boolean hasValue = false;  // was there a passed value?
    	String  pValue   = null;   // value passed (always a String)
    	Long    lValue   = null;   // long value
    	Float   fValue   = null;   // float value
    	Boolean bValue   = null;   // boolean value
    	String  gValue  = null;    // holds passed Flag value

    }

    // array of all the Arguments defined
    private HashMap<String, ArgDetail> args = new HashMap<String, ArgDetail>();

    // parsing error number and error number
    private int      nbrErrors = 0;
    private int      errNbr    = err_start; // Error number to display (eNbr)

    
    // support classes
    private Errs     err       = new Errs();       // for returning errors 

    // passed in classes
    private Trace    TR        = null;             // used to dump hard errors
   
    
    //-------------------------------------------------------------------------
    // Constructors Methods
    //-------------------------------------------------------------------------
    /**
     * Constructor for Args, holder of the parsed command line arguments
     * 
     * @param trace instantiated Trace class for tracing debugging errors
     */
    public Args(Trace trace) {
    
    	TR       = trace;  // cannot be null
    	if (TR == null) {
			err.eNbr     = 9000;
			err.eMessage = "Internal:  Trace file is NULL!";
			err.eType    = Errs.ETYPE_ERROR;
    	}
    	
//      	setDefaultValues();
    }
    
	/**
     * Sets the cmdArgs Values to their default values and
     * sets the titles for their display
     */
//    private void setDefaultValues() {
//    }

    //-------------------------------------------------------------------------
    //  Error results
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
    //  Access to private variable Methods
    //-------------------------------------------------------------------------
    /**
     * Returns a boolean flag indicating if the argument was passed to the 
     * program or not. No checking is performed on the Argument's parameter 
     * value, only if it was passed or not. 
     * 
     * @param param  string of the argument to check
     * @return boolean indicating if the argument was passed to the program.
     */
    public Boolean passed(String param) {
    	err.initErrs();

    	// valid argument?
    	if (!vaildParam(param) ) {
			err.eNbr     = 9005;  // override
    		return null;
    	}
    	
    	// get the argument information
    	ArgDetail argvals = new ArgDetail();
    	argvals = args.get(param);
        
    	// was it passed?
    	return argvals.passed;
    }
        
    /**
     * Returns the string value of the title or "Nice Name" of the argument
     * 
     * @param param  string of the argument
     * @return String "Nice Name" of the argument, or an empty string if 
     *           the argument is not defined 
     */
    public String title(String param) {
    	err.initErrs();

    	// valid argument?
    	if (!vaildParam(param) ) {
			err.eNbr     = 9010;  // override
    		return null;
    	}
    	
    	// get the argument information
    	ArgDetail argvals = new ArgDetail();
    	argvals = args.get(param);
        
    	// what is its title?
    	return argvals.title;
    }
        
    /**
     * Returns a string value indicating the "type" for the specific 
     * argument.
     * 
     * @param param  string of the argument to check
     * @return String indicating the "type" of this argument
     */
    public String type(String param) {
    	err.initErrs();

    	// valid argument?
    	if (!vaildParam(param) ) {
			err.eNbr     = 9015;  // override
    		return null;
    	}
    	
    	// get the argument information
    	ArgDetail argvals = new ArgDetail();
    	argvals = args.get(param);
        
    	// What type is it?
    	return argvals.type;
    }
        

    //-------------------------------------------------------------------------
    //  Defining, Parsing, and showing Arguments Methods
    //-------------------------------------------------------------------------
    /**
     * Creates an argument entry based on passed in values, use for non Boolean
     * arguments where no duplicates are allowed 
     * 
     * @param arg  string of the argument
     * @param title  string of the argument's title or "Nice name"
     * @param type  string of the argument's type
     * @return boolean indicating the command line parameter was defined 
     *                 correctly for this program
     */
    public boolean defineArg(String arg, String title, String type ) {
    	return defineArg(arg, title, type, null, false);
    }
    /**
     * Creates an argument entry based on passed in values, use for Boolean
     * arguments where no duplicates are allowed 
     * 
     * @param arg  string of the argument
     * @param title  string of the argument's title or "Nice name"
     * @param type  string of the argument's type
     * @return boolean indicating the command line parameter was defined 
     *                 correctly for this program
     */
    public boolean defineArg(String arg, String title, String type,
    		                 Boolean bDefault) {
    	return defineArg(arg, title, type, bDefault, false);
    }
    /**
     * Creates an argument entry based on passed in values, use for non Boolean
     * arguments where allowing duplicates is controlled by a parameter 
     * 
	 * @param allowDups boolean indicating if Duplicate entries are allowed.
     * @param arg  string of the argument
     * @param title  string of the argument's title or "Nice name"
     * @param type  string of the argument's type
     * @return boolean indicating the command line parameter was defined 
     *                 correctly for this program
     */
    public boolean defineArg(Boolean allowDups, String arg, String title, 
    		                 String  type) {
    	return defineArg(arg, title, type, null, allowDups);
    }
    
    /**
     * Creates an argument entry based on passed in values, all values must
     * be passed, but may be null. Duplicates are allowed if AllowDup is 
     * set to true 
     * 
     * @param arg  string of the argument
     * @param title  string of the argument's title or "Nice name"
     * @param type  string of the argument's type
     * @param bDefault boolean indicating the default value for Boolean
     *                 arguments. May be Null (forcing user to specify)
     *                 Ignored if type is not of AT_BOOLEAN
     * @param allowDups boolean indicating if duplicate parameters will override
     *                        already defined arguments 
     * @return boolean indicating the command line parameter was defined 
     *                 correctly for this program
     */
    public boolean defineArg(String arg, String title, 
    		                 String type,  Boolean bDefault,
    		                 Boolean allowDups) {
    	err.initErrs();
        TR.debugMsgln(dFlag, dPreFix, "Defining parameter:  " + arg);

    	if (args.containsKey(arg))
    		if (!allowDups) {
    			err.eNbr     = 9030;
    			err.eMessage = "Internal:  Duplicate Argument defined:  " + arg;
    			err.eType    = Errs.ETYPE_ERROR;
    			return false;
    		}
    	
    	if (!validTypes.contains(type)) {
			err.eNbr     = 9035;
			err.eMessage = "Internal:  Unknown Argument type:  " + type;
			err.eType    = Errs.ETYPE_ERROR;
            return false;
    	}
    	
    	
    	// new Entry
    	ArgDetail newarg = new ArgDetail();

    	// fill in the details
    	newarg.arg      = arg;
    	newarg.title    = title;
    	newarg.type     = type; 
    	if (type == AT_BOOLEAN)
    		newarg.valueDB  = bDefault;
    	
    	args.put(arg, newarg);
    	
    	
    	return true;
    }

    /**
     * Creates an argument entry based on passed in values, all values must
     * be passed, but may be null. Duplicates are allowed if AllowDup is 
     * set to true 
     * 
     * @param arg  string of the argument
     * @param flags List<String> of valid values for the argument
     * @return boolean indicating the list of valid flags were defined 
     *                 correctly for this program
     */
    public boolean defineFlags(String arg, List<String> flags) {
    	err.initErrs();
        TR.debugMsgln(dFlag, dPreFix, "Defining Flag values:  " + arg);

        // argument must exist
    	if (!args.containsKey(arg)) {
    			err.eNbr     = 9035;
    			err.eMessage = "Internal:  Argument does not exist:  " + arg;
    			err.eType    = Errs.ETYPE_ERROR;
    			return false;
    	}
    	
    	// get the current argument definition
    	ArgDetail argvals = new ArgDetail();
		argvals = args.get(arg);
		
		// make sure it is a Flag type
		if (!argvals.type.equals(AT_FLAG)) {
   			err.eNbr     = 9042;
   			err.eMessage = "Internal:  Only Arguments of Flag Type can have flags values:  " + arg;
   			err.eType    = Errs.ETYPE_ERROR;
   			return false;
		}
		
		// update the default value with new value
		argvals.valueFL  = flags;
		
    	// replace the record
    	args.put(arg, argvals);

    	return true;
    }

    /**
     * Changes the Default value of Boolean arguments to a different value
     * before or after the arguments have been parsed. If the arguments have 
     * been parsed, the Boolean argument was passed, but no value was passed
     * then this will reset the values based on the new default value.
     * @param arg  string value of the argument
     * @param dValue		// if passed, but with a value, it has all ready been converted

     * @return
     */
    public boolean newDefault(String arg, Boolean dValue){
    	err.initErrs();
    	String spValue   = null;                           
    	Boolean svalueDB = null;
    	Boolean sbValue  = null;
    	
        TR.debugMsgln(dFlag, dPreFix, "Changing Default Value:  " + arg);

        // verify argument is already defined
    	if (!args.containsKey(arg)) {
   			err.eNbr     = 9040;
   			err.eMessage = "Internal:  Argument not defined:  " + arg;
   			err.eType    = Errs.ETYPE_ERROR;
   			return false;
    	}
   	
    	// get the current argument definition
    	ArgDetail argvals = new ArgDetail();
		argvals = args.get(arg);
		
		if (!argvals.type.equals(AT_BOOLEAN)) {
   			err.eNbr     = 9042;
   			err.eMessage = "Internal:  Cannot change default of non Boolean Argument:  " + arg;
   			err.eType    = Errs.ETYPE_ERROR;
   			return false;
		}

		// if no change just exit
		if (argvals.valueDB.equals(dValue)) {
			// no change return with a warning
   			err.eNbr     = 9044;
   			err.eMessage = "Internal:  new Default equals old Default, no change:  " + arg;
   			err.eType    = Errs.ETYPE_ERROR;
   			return false;
		}
		
		// save some of the previous values of this argument
		svalueDB  = argvals.valueDB;
		spValue   = argvals.pValue;
		sbValue   = argvals.bValue;
		
		// update the default value with new value
		argvals.valueDB  = dValue;
		
		// if not passed, no need to re-parse it
		// if passed, but with a value, the new default has no impact
		// if passed, but there was no value, re-parse this argument
		if (argvals.passed && !argvals.hasValue) {
			parseArg(argvals);
			if (err.eType == Errs.ETYPE_ERROR)
				return false;
		}

    	// replace the record
    	args.put(arg, argvals);

    	// show what we did
        TR.debugMsgln(dFlag, dPreFix, "Changed  Default Value");
    	TR.debugMsgln(dFlag, dPreFix, "\tArgument        :  " + argvals.arg);
        TR.debugMsgln(dFlag, dPreFix, "\tPrevious Default:  " + svalueDB);  //14.65
        TR.debugMsgln(dFlag, dPreFix, "\tNew      Default:  " + argvals.valueDB);  //14.65
        TR.debugMsgln(dFlag, dPreFix, "\tPassed?         :  " + argvals.passed);  //14.65
        TR.debugMsgln(dFlag, dPreFix, "\tHas a value?    :  " + argvals.hasValue);  //14.65
        TR.debugMsgln(dFlag, dPreFix, "\tPrevious Passed :  " + spValue);  //14.65
        TR.debugMsgln(dFlag, dPreFix, "\tPrevious Value  :  " + sbValue);  //14.65
        TR.debugMsgln(dFlag, dPreFix, "\tNew      Value  :  " + argvals.bValue);  //14.65

    	return true;
    }
    /**
     * Extracts command line arguments and map them into a HashMap of Argument
     * Details for easier processing by the program.<p>
     * Argument values are stored as their passed values and then those values 
     * are 'converted' to the other available values. However, errors in the
     * 'conversion'  will result in the 'coverted value' being set to null.<p>
     * For example:<br>
     * An argument of type String will have values for String, Long, Float, 
     * and Boolean. However if the string doesn't equal 'true' or 'false', the
     * 'Boolean value' will be null. If the string is not a properly formatted 
     * Long, the 'Long value' will be null; and so on.<p>
     * An argument of type Boolean that doesn't have a passed value, will get
     * the passed value equal to it's default value. 
     * <p>
     * The method parses the entire command line argument line and reports all
     * of the errors it finds.
     * 
     * @param clArgs  String array of command line arguments
     * @return boolean value <code>true</code> if command line arguments parsed
     *                       with no errors, otherwise <code>false</code>
     */
    public boolean parseArgs(String[] clArgs) {
		err.initErrs();
        TR.debugMsgln(dFlag, dPreFix, "Parsing parameters ");

        // if nothing passed, just return
        if (clArgs == null || clArgs.length == 0 ) {
            return true;
        }

        int aIdx      = 0;             // index into command line arguments
        int aLen      = clArgs.length;
            nbrErrors = 0;
            errNbr    = err_start; // Error number to display (eNbr)
        
        while (aIdx < aLen) {
        	// get the next argument
        	String arg = clArgs[aIdx].toLowerCase();
            TR.debugMsgln(dFlag, dPreFix, "  Parsing :  " + arg);
        	
        	// valid argument?
        	if (arg == null ||
        	    !args.containsKey(arg) ) {
                nbrErrors++;
        		errNbr++;              // eNbr
        		err.eMessage += "Invalid program argument             :  " + 
			                   clArgs[aIdx]  + "\n";
                aIdx++;    //skip this argument
                continue;
        	}
        	
        	// get argument definition
        	ArgDetail argvals = new ArgDetail();
    		argvals = args.get(arg);
        	
        	// is the next value is normally the value for this 
    		// argument.
    		String  passedValue = null;
    		Boolean nextIsArg   = false;
    		if ( aIdx+1 < aLen) { //-1 ) { // more arguments
    			passedValue = clArgs[aIdx+1];
    			nextIsArg = args.containsKey(passedValue.toLowerCase());
    		}
    		
    		// Boolean Arguments don't always have to have a passed value,
    		// so if the there is no next value, or it is a valid 
    		// argument:  use the default value (if set). Otherwise use
    		// the next value. If the next value is an invalid argument
    		// it will get "swallowed" as the boolean value! which means
    		// the invalid argument's 
    		if (argvals.type.equals(AT_BOOLEAN)) {
    			if (passedValue == null || nextIsArg ) {
					argvals.hasValue = false;
    				argvals.bValue = argvals.valueDB;  // maybe null!
    				aIdx++;   // skip past the arg
    			} else {
   					argvals.hasValue = true;
   					argvals.pValue = passedValue;
   					aIdx++;  aIdx++;  // skip past the arg and it's value
    			}
    		} else {
    			// all non boolean values require a value, so use the next
    			// value as the value
    			if (passedValue == null || nextIsArg) {
    				// no value, this is an error
                    nbrErrors++;
            		errNbr++;              // eNbr
            		err.eMessage += "Missing value for argument           :  " +
    				                 clArgs[aIdx]  + "\t" +
    				                 argvals.title + "\n";
                    aIdx++;    //skip this argument
    				continue;
    			}
    			
    			// not null and not an argument, so capture it
    			argvals.pValue = passedValue;
    			//skip the argument and value
                aIdx++; aIdx++;  // skip the argument and it's value
    		}

    		// we now have the passed value in the proper place
    		// so let's parse this argument
    		argvals.passed   = true;
    		parseArg(argvals);
    		
    		// now save the argument
            args.put(arg, argvals);

        } // while loop
        
        	// 
        if (nbrErrors > 0) {
        	err.eNbr     = errNbr;
        	err.eMessage = "Invalid arguments::\n" + err.eMessage;
    		err.eType    = Errs.ETYPE_ERROR;
            return false;
        }
        
        return true;
    }
 
    /**
     * This is where all the meat and potatoes takes place for parsing.
     * Conversion of one type of field to the other is performed based
     * on the original type of the argument.
     *  
     * @param argvals  ArgDetail record which has the argument
     *                 definition and the passed in value
     * @return boolean value indicating if the argument and its value
     *                 was parsed correctly
     */
    private Boolean parseArg(ArgDetail argvals) {

    	boolean parseOK = true;
    	
    	//====================================================================
    	//  Boolean types
    	//====================================================================
    	if (argvals.type.equals(AT_BOOLEAN)) {
    	   // Simple case, no passed value:
			if (!argvals.hasValue) {
				if (argvals.valueDB == null) { // check default value
					// no has no default value!
		      		errNbr++;      
		            nbrErrors++;
		       		err.eMessage += "Boolean Argument has no default value; " + 
		                            "a value must be passed with this argument:  " +
		                            argvals.arg    + "\n";
		       		return false;
				}
				// Since value was not passed, use the defined default
				if (argvals.valueDB) {    // default is true
					argvals.bValue  = true;
					argvals.pValue  = "true"; // converted
					argvals.lValue  = 1L;     // converted
					argvals.fValue  = 1F;     // converted
					argvals.gValue  = null;   // no valid flags defined
				} else {                  // default is false
					argvals.bValue  = false;
					argvals.pValue  = "false";    // not really passed!
					argvals.lValue  = 0L;
					argvals.fValue  = 0F;
					argvals.gValue  = null;   // no valid flags defined
				}
				return true; // no need to go further, all values have been set
			}
			// "true" value was passed?
			if (argvals.pValue.equalsIgnoreCase("true")) {
				argvals.bValue  = true;
				argvals.lValue  = 1L;
				argvals.fValue  = 1F;
				argvals.gValue  = null; // no valid flags defined
				return true; // no need to go further, all values have been set
			}
			// "false" value was passed?
			if (argvals.pValue.equalsIgnoreCase("false")) {
				argvals.bValue  = false;
				argvals.lValue  = 0L;
				argvals.fValue  = 0F;
				argvals.gValue  = null; // no valid flags defined
				return true; // no need to go further, all values have been set
			}
			// bad value passed!
      		errNbr++;      
            nbrErrors++;
       		err.eMessage += "Invalid Boolean value for:  " + argvals.arg    +
       				        "    passed value:  "          + argvals.pValue +
       				        "\n";
    	}  // end of boolean type

    	// all other Argument types require a value to be passed, so we will
    	// Validate them, store them, and convert the value to the other types
    	// when possible.

    	//====================================================================
    	// convert passed value and store as a Long value
    	//====================================================================
    	Long lVal = null;
    	try {
    		lVal = Long.parseLong(argvals.pValue);
    	} catch (NullPointerException | NumberFormatException e) {
    		// only report the error if this is a Long Type
    		if (argvals.type.equals(AT_LONG)) {
    			//  err.eNbr set in calling method
    			err.eMessage += "Invalid Long value for   :  " + argvals.arg    +
   				        		"    passed value:  "          + argvals.pValue +
   				        		"\n";
    	    	parseOK = false;
    			errNbr++;      
    			nbrErrors++;
    		}
    	}

    	// save the Long value  
    	if (lVal != null)
    		argvals.lValue = lVal;

    	//====================================================================
    	// convert passed value and store as a Float value
    	//====================================================================
    	Float fVal = null;
    	try {
    		fVal = Float.parseFloat(argvals.pValue);
    	} catch (NullPointerException | NumberFormatException e) {
    		// only report the error if this is a FloatType
    		if (argvals.type.equals(AT_FLOAT)) {
    			//  err.eNbr set in calling method
    			err.eMessage += "Invalid Float value for  :  " + argvals.arg    + 
    							"    passed value:  "          + argvals.pValue +
    							"\n";
    	    	parseOK = false;
    			errNbr++;      
    			nbrErrors++;
    		}
    	}
    	// save the value  
    	if (fVal != null)
    		argvals.fValue = fVal;

    	//====================================================================
    	// Validate the passed value as one of the Flags, and save it
    	//====================================================================
		if (argvals.type.equals(AT_FLAG)) { // flags must have a valid list
			if (argvals.valueFL        == null ||     // is the list of value flags null?
				argvals.valueFL.isEmpty()      ||     // or empty?
				argvals.valueFL.size() == 0       ) { // or nothing in it?
					err.eMessage += "Internal Error::     Flag argument:  " + argvals.arg    + 
					  			    "    does not have a valid list of flags defined.\n";
					parseOK = false;
					errNbr++;      
					nbrErrors++;
			}
			
    	 
    	
			// is the passed value in the list?
			if (argvals.valueFL.contains(argvals.pValue))
				argvals.gValue = argvals.pValue;
			else {
   				err.eMessage += "Invalid Flag value for  :  " + argvals.arg    + 
   								"    passed value:  "         + argvals.pValue +
   								"    Valid values:  "         + 
   								Arrays.toString(argvals.valueFL.toArray()) +
    								"\n";
    				parseOK = false;
    				errNbr++;      
    				nbrErrors++;
    			}
    	}

    	//====================================================================
    	// convert passed value and store as a Boolean value
    	//====================================================================
   		// check for boolean values of String types
   		//    -  Flag values can be checked now, because their values have 
   		//       been validated
   		if (argvals.type.equals(AT_STRING) || argvals.type.equals(AT_FLAG)) {
   			// "true" value was passed?
   			if (argvals.pValue.equalsIgnoreCase("true"))
   				argvals.bValue = true;

   			// "false" value was passed?
   			if (argvals.pValue.equalsIgnoreCase("false")) 
   				argvals.bValue = false;
   		}
   		
   		// final Long type conversions
   		if (argvals.type.equals(AT_LONG)) {
   			if (argvals.lValue != null) {
   		   		// Check for boolean values of Long Types
   				// equal 0, then false, all other values true
   				if (argvals.lValue == 0L)   argvals.bValue = false;
   				else                        argvals.bValue = true;
   			
   				// convert the long to a float
   				Float fval = argvals.lValue.floatValue();
   				Long  cval = fval.longValue();  // convert back to a long
   				if (cval.equals(argvals.lValue)) // only save if they are equal
   					argvals.fValue = fval;
   			}
   		}
   		
   		// final float type conversions
   		if (argvals.type.equals(AT_FLOAT)) {
   			if (argvals.fValue != null) {
   		   		// Check for boolean values of Float Types
   				// equal 0, then false, all other values true
   				if (argvals.fValue == 0F)   argvals.bValue = false;
   				else                        argvals.bValue = true;

   				// convert the float to a long
   				Long   lval = argvals.fValue.longValue();
   				Float  cval = lval.floatValue();  // convert back to a long
   				if (cval.equals(argvals.fValue)) // only save if they are equal
   					argvals.lValue = lval;
   			}
   		}
   		
    	// since we handled the Boolean Types above, not need 
    	// to check for any warnings here

    	// return the status of this conversion
    	return parseOK;
   }
    /**
     * Prints out the Argument fields onto System.out. For debugging or verbose
     * output.
     * 
     * @param title string title to be printed 
     * @param ARGFields  List of Strings of Argument fields to be printed
     */
    public void infoShow(String title, List<String> ARGFields) {
    	String line = "";
        String fmt = "  %-40s (%-1s)  :  %-8s  :  %-6s  :  %s";
    	String fml = "  %-40s %-3s    %-10s   %-6s   %s";
    	// print the title
        TR.printMsgln(title);

        // print the column headings
        line = String.format(fml, " Arg Title", " T", "  Arg", 
                " Passed",     "  Value");
        TR.printMsgln(line);
        
        // print the "underlines"
        line = String.format(fml, "-------------------------------------", "---",
        		                  "--------", "--------", 
        		                  "-------------------------------------");
        TR.printMsgln(line);
        
        // print out the values, in a specific order, 
        // to make visual comparisons easier
        for (String key: ARGFields ) {
            if (args.containsKey(key) ) {
            	ArgDetail argvals = new ArgDetail();
            	argvals = args.get(key);
                String value = argvals.pValue; // passed in value
                if (value == null ) { value = "null value";}
                line = String.format(fmt, argvals.title, argvals.type,
                		                  key, argvals.passed, value);
                TR.printMsgln(line);
            }
        }
        TR.printMsgln(" ");

    }

    //-------------------------------------------------------------------------
    // Getting Argument values Methods
    //-------------------------------------------------------------------------
    /** 
     * Returns the string value of the argument requested, if not passed the
     * null string is returned with a warning.
     * 
     * @param argParam  string of the argument type to return
     * @return string value of the argument
     */
    public String getS(String argParam) {
		err.initErrs();
    	ArgDetail argvals = new ArgDetail();

    	// valid argument?
    	if (!vaildParam (argParam)) { 
    		err.eNbr     = 9100;   // override
    		return null;
    	}
    	
    	// get the value
		argvals = args.get(argParam);
		
		// if passed return the value otherwise return null
		if (argvals.passed)
			return argvals.pValue;   // passed value or string if Boolean

		// Argument wasn't passed, so warning
   		err.eNbr     = 9102;
       	err.eMessage = "Arg not passed:  " + argParam;
   		err.eType    = Errs.ETYPE_WARN;
		return null;
		
    }

    /** 
     * Returns the boolean value of an argument. If the argument was not
     * passed, or if the value could not be converted to boolean, 
     * an appropriate warning is returned.
     * 
     * @param argParam  string of the boolean argument to return
     * @return boolean value of the passed argument, or null if it 
     *         could not be converted to a boolean value.
     */
    public Boolean getB(String argParam) {
		err.initErrs();
    	ArgDetail argvals = new ArgDetail();

    	// valid argument?
    	if (!vaildParam (argParam)) { 
    		err.eNbr     = 9110;    // override
    		return null;
    	}
    	
    	// get the value
		argvals = args.get(argParam);
		
		// check for a valid value already set
		if (argvals.bValue != null)
			return argvals.bValue;
		
		// at this point determine what kind of warning to return
       	if (argvals.passed) {
   	        err.eNbr     = 9112;
   			err.eMessage = "Argument value is not a valid Boolean\t" + 
   	                       "Argmument:  "        + argvals.arg      +
   	                       "    Passed Value:  " + argvals.pValue;
   			err.eType    = Errs.ETYPE_WARN;
       	} else {
       		// arg not passed
   	        err.eNbr     = 9114;
   			err.eMessage = "Argument was not passed:  " + argvals.arg;
   			err.eType    = Errs.ETYPE_WARN;
       	}
       	
       	return null;
		
    }

    /** 
     * Returns the Long value of an argument. If the argument was not
     * passed, or if the value could not be converted to Long, null 
     * is returned and an appropriate warning is set.
     * 
     * @param argParam  string of the argument type to return
     * @return Long value of the argument or null
     */
    public Long getL(String argParam) {
		err.initErrs();
    	ArgDetail argvals = new ArgDetail();

    	// valid argument?
    	if (!vaildParam (argParam)) { 
    		err.eNbr     = 9120;  // override
    		return null;
    	}
    	
    	// get the value
		argvals = args.get(argParam);

		// is it a valid long value?
		if (argvals.lValue != null)
			return argvals.lValue;
		
		// at this point determine what kind of warning to return
       	if (argvals.passed) {
   	        err.eNbr     = 9122;
   			err.eMessage = "Argument value is not a valid Long\t" + 
   	                       "Argmument:  "        + argvals.arg      +
   	                       "    Passed Value:  " + argvals.pValue;
   			err.eType    = Errs.ETYPE_WARN;
       	} else {
       		// arg not passed
   	        err.eNbr     = 9124;
   			err.eMessage = "Argument was not passed:  " + argvals.arg;
   			err.eType    = Errs.ETYPE_WARN;
       	}
       	
       	return null;
		
		
    }

    /** 
     * Returns the Integer value of the argument requested.<p> 
     * 
     * This calls the getL method and returns the result as an Integer
     * See getL for full details
     * @param argParam  string of the argument type to return
     * @return Integer value of the argument
     */
    public Integer getI(String argParam) {
		err.initErrs();
    
		Long    lVal = getL(argParam);
		Integer iVal = null;
		
    	ArgDetail argvals = new ArgDetail();

    	// valid argument?
    	if (!vaildParam (argParam)) { 
    		err.eNbr     = 9130;  // override
    		return null;
    	}
    	
    	// get the value
		argvals = args.get(argParam);
		lVal = getL(argParam);
		
		// is it a valid long value?
		if (lVal != null) {
			// try to convert to integer
			try {
				iVal = Math.toIntExact(lVal);
			} catch (ArithmeticException e) {
	   	        err.eNbr     = 9132;
	   			err.eMessage = "Argument value is too big to be an Integer\t" + 
	   	                       "Argmument:  "        + argvals.arg      +
	   	                       "    Passed Value:  " + argvals.pValue;
	   			err.eType    = Errs.ETYPE_WARN;
			}
			
			return iVal;
		}

		// at this point determine what kind of warning to return
       	if (argvals.passed) {
   	        err.eNbr     = 9134;
   			err.eMessage = "Argument value is not a valid Integer\t" + 
   	                       "Argmument:  "        + argvals.arg      +
   	                       "    Passed Value:  " + argvals.pValue;
   			err.eType    = Errs.ETYPE_WARN;
       	} else {
       		// arg not passed
   	        err.eNbr     = 9136;
   			err.eMessage = "Argument was not passed:  " + argvals.arg;
   			err.eType    = Errs.ETYPE_WARN;
       	}
       	
       	return null;
    }
    
    /** 
     * Returns the Float value of an argument. If the argument was not
     * passed, or if the value could not be converted to Float, null 
     * is returned and an appropriate warning is set.
     * 
     * @param argParam  string of the argument type to return
     * @return Float value of the argument or null
     */
    public Float getF(String argParam) {
		err.initErrs();
    	ArgDetail argvals = new ArgDetail();
    	
    	
    	// valid argument?
    	if (!vaildParam (argParam)) { 
    		err.eNbr     = 9140;  // override
    		return null;
    	}

    	// get the value
		argvals = args.get(argParam);

		// is it a valid long value?
		if (argvals.fValue != null)
			return argvals.fValue;
		
		// at this point determine what kind of warning to return
       	if (argvals.passed) {
   	        err.eNbr     = 9142;
   			err.eMessage = "Argument value is not a valid Float\t" + 
   	                       "Argmument:  "        + argvals.arg      +
   	                       "    Passed Value:  " + argvals.pValue;
   			err.eType    = Errs.ETYPE_WARN;
       	} else {
       		// arg not passed
   	        err.eNbr     = 9144;
   			err.eMessage = "Argument was not passed:  " + argvals.arg;
   			err.eType    = Errs.ETYPE_WARN;
       	}
       	
       	return null;
		
    }

    
    /** 
     * Returns the Flag value of an argument. If the argument was not
     * passed, or if the value could not be converted to a flag, 
     * an appropriate warning is returned.
     * 
     * @param argParam  string of the argument to return
     * @return String value of the Flag 
     */
    public String getFLag(String argParam) {
		err.initErrs();
    	ArgDetail argvals = new ArgDetail();

    	// valid argument?
    	if (!vaildParam (argParam)) { 
    		err.eNbr     = 9150;   // override
    		return null;
    	}
    	
    	// get the value
		argvals = args.get(argParam);
		
		// is it a valid Flag value value?
		if (argvals.gValue != null)
			return argvals.gValue;
		
		// at this point determine what kind of warning to return
       	if (argvals.passed) {
   	        err.eNbr     = 9152;
   			err.eMessage = "Argument cannot be converted to a Flag Type:\t" + 
   	                       "Argmument:  "        + argvals.arg    +
   	                       "    Passed Value:  " + argvals.pValue;
   			err.eType    = Errs.ETYPE_WARN;
       	} else {
       		// arg not passed
   	        err.eNbr     = 9154;
   			err.eMessage = "Argument was not passed:  " + argvals.arg;
   			err.eType    = Errs.ETYPE_WARN;
       	}
       	
       	return null;
		
    }



    //-------------------------------------------------------------------------
    // Private Methods
    //-------------------------------------------------------------------------
    /**
     * Validates that the passed parameter is valid for this program
     * 
     * @param argParam parameter to be validated
     * @return boolean value indicating if argument was passed, 
     *                 <b><code>true</code></b> indicates it was passed,
     *                 <b><code>false</code></b> indicates it was not passed
     */
    public boolean vaildParam (String argParam) {
    	err.initErrs();
    	if (argParam == null ||
    			!args.containsKey(argParam) ) {
    		err.eNbr     = 9160;
    		err.eMessage = "Argument not defined:  " + argParam;
    		err.eType    = Errs.ETYPE_ERROR;
    		return false;
    	}
    	return true;
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
     * number. It also adds all called routines to the list. If the 
     * class file name and version is already in the list, it does not
     * add them, it simply returns.
     *
     * @param  vlist Map of the current class/versions to be appended to
     * @return Map of the class/version values for this and all called
     *                routines
     */
 	static public LinkedHashMap<String, String> 
 	         getAllVersions (LinkedHashMap<String, String> vlist) {
 		String v = Args.class.getName();
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