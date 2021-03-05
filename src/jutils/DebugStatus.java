package jutils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * The DbgStatis class maintains the state of the debug params that will be 
 * used when creating other classes. This is done centrally so we can set the
 * debug status of a class, before it has been created, or even if we it is 
 * defined in in some other class. Once the class is created, it's debug status
 * may be maintained by calls to this class.
 * 
 * This process allows programs to set and un-set the debug flags for classes 
 * dynamically during runtime. While not actually having access to classes.
 * 
 */
public class DebugStatus {

    private static final String  version    = "Version 14.75";
    private static final String   dFlag     = DebugInfo.DB_DEBUGSTATUS;    
    private static final String  dPreFix    = "DFlags:";
    
    // field values
    public  static final String  DF_CCODE   = "dCC";  // ClassCode
    public  static final String  DF_CNAME   = "dCN";  // Class Name
    public  static final String  DF_PACKAGE = "dPN";  // Package Name
    public  static final String  DF_DNAME   = "dDN";  // Display Name
    public  static final String  DF_MGROUP  = "dMG";  // Menu Group
    public  static final String  DF_DFLAG   = "dDF";  // Debug Flag
    public  static final String  DF_UNKNOWN = "unknown"; // unknown value
    
    private static final List<String> fieldTypes
                       = Arrays.asList(DF_CCODE, DF_CNAME,
                    		           DF_DNAME, DF_MGROUP,
                    		           DF_DFLAG );

    // details of an argument
    private class DebugDetail {
    	// Debug information
    	private String  classCode   = "";    // unique code/prefix
    	private String  className   = "";    // name of the class
    	private String  packageName = "";    // name of the class's package
    	private String  menuGroup   = "";    // Name of Menu Group
    	private String  displayName = "";    // Name to display to debugger
    	private boolean debugFlag   = false; // has it been set?
    }
    
    // HashMap of all defined debug params (added through the debug.ini file,
    // or other s; key will be the classCode
    private HashMap<String, DebugDetail> dFlags = 
    		                               new HashMap<String, DebugDetail>();

    // List of known debug Params, in the order to be shown by showFlags();
    // key will be the classCode, while the object will be the package name
    // where the class is defined. 
    private LinkedHashMap<String, String> knownParams = 
    		                              new LinkedHashMap<String, String>();
    // passed in values
    private   Errs     err       = null;       // for returning errors 
    private   Trace    TR        = null;       // used to dump hard errors
    
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------
    /**
     * Nothing is done within this constructor, it is included for completeness
     */
    public DebugStatus() {

    }

    //-------------------------------------------------------------------------
    // Initialization Method
    //-------------------------------------------------------------------------
    /**
     * Nothing is defined for this class as it should be extended by the
     * Trace Class, the Trace Constructor should call this method to 
     * initialize pointers within DebugStatus back to the Trace class so that
     * the Trace methods and it's Errs class can be accessed from within 
     * Convoluted, yes, but for now it works!
     * 
     * @param tr instantiated Trace class for tracing debugging errors
	 * @param errors instantiated Errs class for error reporting
     * @param dsT Trace class with debug information. Pass null
     *        when creating a Trace file for the first time, otherwise pass
     *        an instantiated class to copy debugging values
     */
    protected void  initDebugStatus(Trace tr, Errs errors, Trace dsT) {
    	
    	TR      = tr;
    	err     = errors;

    	// if there are no known previous values, initialize the new known
    	// values. This cannot be done in the constructor because this method
    	// references TR and err!
    	if (dsT == null) {
        	addKnown(DebugInfo.DB_METH_CODES, DebugInfo.DB_PACKAGE);
    	} else {
    		// copy the known values and the current debug Flags
   			knownParams = dsT.copyKP();
   			dFlags      = dsT.copyDFlags();
    	}
  	
    }

    //-------------------------------------------------------------------------
    //  Methods used for copying values
    //-------------------------------------------------------------------------
    /**
     * While nothing is defined for this class(as it should be extended by the
     * Trace Class), subsequent classes may need to copy the DebugStatus
     * information from one Trace class to the other (for example when creating
     * a Trace Thread. These methods are provided to support that process.
     * 
     * @param tr instantiated Trace class for tracing debugging errors
     */
    protected void  copyDebugStatus(Trace tr) {
    	
    	// if there are previous values, copy them
    	Set <String> kp = tr.debugParams(); // previous values
    	if (!kp.isEmpty()) {  // previous values exist!
    		// copy the values to this instance
        	knownParams = tr.copyKP();
        	dFlags      = tr.copyDFlags();
        	
    	}
  	
    }
    /**
     * Returns a copy of the knownParams, the debug flags known for this program
     * 
     * @return a copy of the LinkedHashMap of the known Params and their
     *           packages
     */
    protected LinkedHashMap<String, String> copyKP() {
    	return new LinkedHashMap<String, String>(knownParams);
    }
 
    /**
     * Returns a copy of the dFlags, the defined debug flags and values
     * 
     * @return a copy of the HashMap of defined debug flags
     */
    protected HashMap<String, DebugDetail> copyDFlags () {
    	return new HashMap<String, DebugDetail>(dFlags);
    }
    
    //-------------------------------------------------------------------------
    //  Retrieving Debug params Methods
    //-------------------------------------------------------------------------
    /**
     * returns a Set of Strings of the Known debug params. Note that a 
     * debug Param can bed defined in the list, but not in the actual 
     * DebugFlags if the info is not in the Debug.ini file. 
     * 
     * @return Set of Strings of known debug params
     */
   public Set<String> debugParams(){
    	Set<String> kp = knownParams.keySet();
    	return kp;
    	
    }
   /**
    * Get the debug flag value for the given Class.<br>
    * <B>NOTE:  </B>If the DB_ALL flag is set, this  always returns
    *               <code>true</code>, otherwise the actual debugFlag for the 
    *               passed classCode is returned. Use this for checking if 
    *               Debugging should be performed.
    * 
    * @param classCode String value of the Class Code
    * @return boolean value of the debug flag for the class or
    *                 <code>false</code> if not defined
    */
    public boolean getDF(String classCode) {

    	if (classCode.equals("developer"))
    		return getClassDF(classCode);
    	
    	// if debugAll is set to true, return that
    	// but if set to false or not there, look for the 
    	// individual Class
    	if (getClassDF(DebugInfo.DB_ALL)) {
//    	if (dFlags.containsKey(DebugInfo.DB_ALL) &&
//    		dFlags.get(DebugInfo.DB_ALL).debugFlag   )  {
    			return true;
    		}

    	// check individually
    	return getClassDF(classCode);

    }
    
    /**
     * Get the debug flag value for the given Class<br>
    * <B>NOTE:  </B>This  returns the debugFlag for a specific
    *               classCode, regardless of the DebugALL setting.
    *               Use this for Menus or other debugging messages.
     * 
     * @param classCode String value of the Class Code
     * @return boolean value of the debug flag for the class or
     *                 <code>false</code> if not defined
     */
     public boolean getClassDF(String classCode) {
     	// check individually
     	if (dFlags.containsKey(classCode))
     		 return dFlags.get(classCode).debugFlag;
     	
     	// it's not there!
    		return false;

     }
     
   /**
    * Gets the one of the Fields for the given 
    * 
    * @param classCode String value of the  code
    * @param fldName String value of one of the predefined fields
    * @return String value of the value for the field, or null if 
    *             classCode has not been defined, or fldName is invalid
    *              
    */
   public String getField(String classCode, String fldName) {
   
   	err.initErrs();
   	DebugDetail cVal;
   	
   	// verify we have a correct type of field
   	if (!fieldTypes.contains(fldName) ) {
   		err.eNbr     =   9430;
   		err.eType    =   Errs.ETYPE_ERROR;
   		err.eMessage =   "Invalid Debug Field Type:  " + fldName;
   		TR.debugMsgln(dFlag, dPreFix, err.eMessage);
   		return null;
   	}

   	
   	// get the record
   	if (dFlags.containsKey(classCode)) {
   		cVal = dFlags.get(classCode);
   	} else {
   		err.eNbr     =   9440;
   		err.eType    =   Errs.ETYPE_WARN;
   		err.eMessage =   "Debug Class not defined:  " + classCode;
   		TR.debugMsgln(dFlag, dPreFix, err.eMessage);
   		return null;  //Not there so return null
   	}

   	// set the value
		switch  (fldName)  {
		case DF_CCODE:
			return cVal.classCode;
		case DF_PACKAGE:
			return cVal.packageName;
		case DF_CNAME:
			return cVal.className;
		case DF_DNAME:
			return cVal.displayName;
		case DF_MGROUP:
			return cVal.menuGroup;
		case DF_DFLAG:
			if (cVal.debugFlag)
				return "true";
			else
				return "false";
		default:
			// we should never get here, it should have been found earlier!
			err.eNbr     =   9450;
			err.eType    =   Errs.ETYPE_ERROR;
			err.eMessage =   "Invalid Debug Field Type:  " + fldName;
			TR.debugMsgln(dFlag, dPreFix, err.eMessage);
			return null;  // Not there so return null
		}

   }

    //-------------------------------------------------------------------------
    //  Defining and Changing Values of Debug Flags Methods
    //-------------------------------------------------------------------------
    /**
     * Creates a Debug Flag entry based on passed in values, normally called 
     * only by getDebugFlags, this  is public to allow individual 
     * classes to "self-define" needed. 
     * 
     * @param iCode String value, unique  code
     * @param iMGroup String value of the Menu group (if any) 
     * @param iClassName String value of the class name  
     * @param iDisplay String value of the display name to the debugger,
     *                  or on a menu
     * @param val boolean value of the initial value of the debug flag
     * 
     */
    public void defineFlag(String iCode,      String iMGroup,
    			 		   String iClassName, String iDisplay,
    					   boolean val) {
    	err.initErrs();
    	
    	//if the code is known, get it's Package Name
    	String pName = "";
    	if (knownParams.containsKey(iCode))
    		pName = knownParams.get(iCode);
    	else { 
        	// add it to the list of known flags 
        	if (!addKnown(iCode, DF_UNKNOWN)) 
        		err.initErrs();
    		pName = DF_UNKNOWN;
    	}
    	
    	// build a Debug Detail Record
    	DebugDetail nVal = new DebugDetail();
    	nVal.classCode   = iCode;
    	nVal.packageName = pName;
    	nVal.menuGroup   = iMGroup;
    	nVal.className   = iClassName;
    	nVal.displayName = iDisplay;
    	nVal.debugFlag   = val;
    	
    	// add it to the list of debug flags
    	dFlags.put(iCode,  nVal);

    	return;
    	
    }

 
    /**
     * Set the one of the Fields for the given 
     * 
     * @param Code String value of the  code
     * @param fldName String value of one of the predefined fields
     * @param val String value of the value to be set
     * @return boolean value indicating success of failure of the  
     */
    public boolean setField(String Code, String fldName, String val) {
    
    	err.initErrs();
    	DebugDetail cVal;
    	
    	// verify we have a correct type of field
    	if (!fieldTypes.contains(fldName) ) {
    		err.eNbr     =   9410;
    		err.eType    =   Errs.ETYPE_ERROR;
    		err.eMessage =   "Invalid Debug Field Type:  " + fldName;
    		TR.debugMsgln(dFlag, dPreFix, err.eMessage);
    		return false;
    	}

    	
    	// get the current record
    	if (dFlags.containsKey(Code)) {
    		cVal = dFlags.get(Code);
    	} else {
    		cVal = new DebugDetail();
    	}

    	// set the value
		switch  (fldName)  {
		case DF_CCODE:
			cVal.classCode     = val;   break;
		case DF_PACKAGE:
			cVal.packageName   = val;   break;
		case DF_CNAME:
			cVal.className     = val;   break;
		case DF_DNAME:
			cVal.displayName   = val;   break;
		case DF_MGROUP:
			cVal.menuGroup     = val;   break;
		case DF_DFLAG:
			if (val.equalsIgnoreCase("true"))
				cVal.debugFlag = true;
			else
				cVal.debugFlag = false;
			break;
		default:
    		err.eNbr     =   9420;
    		err.eType    =   Errs.ETYPE_ERROR;
    		err.eMessage =   "Invalid Debug Field Type:  " + fldName;
    		TR.debugMsgln(dFlag, dPreFix, err.eMessage);
    		return false;
		}
		
		// update the record
		dFlags.put(Code, cVal);
		return true;

    }
 
    /**
     * Set the debug flag value for the given Class.<P>
     * <b>NOTE:  </b> If the classCode is "DB_CLEAR" then the
     *                debug Flags for all Defined classes are set
     *                to false, regardless of the value passed in val.
     * 
     * @param classCode String value of the  code
     * @param val boolean value of the value to be set 
     */
    public void setDF(String classCode, boolean val) {
    	// check for Debug All
    	if (classCode.equals(DebugInfo.DB_CLEAR)) {
    		clearAll();
    	}
 
    	// get the current record
    	if (dFlags.containsKey(classCode)) {
    		DebugDetail cVal = dFlags.get(classCode);
    		cVal.debugFlag   = val;
    		dFlags.put(classCode, cVal);
    		return;
    	}
    	
    	// value not there, set it anyway
    	DebugDetail nVal = new DebugDetail();
    	nVal.classCode   = classCode;
    	nVal.debugFlag   = val;
    	dFlags.put(classCode,  nVal);
    	
    }
 
    /**
     * Sets all defined debug params to false, thereby turning them all off.
     */
     private void clearAll() {
    	 // turning off debugging for all defined s
    	 for (String key : dFlags.keySet()) {
     		DebugDetail cVal = dFlags.get(key);   // get it
     		cVal.debugFlag   = false;             // set it
     		dFlags.put(key, cVal);                // update it
    	 }
     }

     //-------------------------------------------------------------------------
     // Debug Configuration File Methods
     //-------------------------------------------------------------------------
     /**
      * Opens the debug.ini file, if exists, and extracts the records from it
      * that define each of the routines that will be debugged. If the file 
      * does not exist, nothing is set. Each entry of the file consists of 
      * tab delimited fields with the fields in the following order:<br>
      * <ul>
      * <li><b>Class Code</b> is the code for the class as defined in 
      *                    <i>DebugInfo.java</i></li>
      * <li><b>Class Name</b> is the class Name</li> 
      * <li><b>Display Name</b> is the name to be displayed in error messages 
      *                      and menus</li>
      * <li><b>Value</b> is a string equal to "true" or "on" to indicate that
      *                  debugging is "on" for this class; any other value 
      *                  indicates that debugging is "off" for this class.
      *                  (Normal values would be "false" or "off")</li>
      * </ul>        
      *<p>Records that begin with "*" are considered comments and are ignored
      * 
      * @param dbFile String value of the program specific DebugIni file,
      *                 if null, empty, or blank, will default to debug.ini
      * @param show boolean value indicating if we should show the list of
      *                 the current "known" params
      *                  
      */
     public void getDebugFlags(String dbFile, boolean show) {
    	 
    	 if (dbFile == null || dbFile.isEmpty() || dbFile.trim().isEmpty())
    		 dbFile = "debug.ini";  // default value
    	 
    	 String line = null;
    	 FIO fs = new FIO(TR);  // define the file IO
    	 
    	 // open the file, if not there, just return
    	 if (!fs.open(dbFile, FIO.FIO_READ)) {
			 err = fs.getErrs();
//			 TR.printErr(err, "Error opening " + dbFile);
    	 	return;
    	 }
    	 
    	 // get and process each record
    	 while (!fs.eof()) {
    		 // get the next record
    		 line = fs.readline();
    		 if (line == null) {
    			 // error or eof
    			 if (fs.eof())  break;
    			 err = fs.getErrs();
    			 TR.printErr(err, "Error reading " + dbFile);
    			 break;  // just close and forget about it
    		 }
    		 
    		 // check for empty line
    		 // check for comment line
    		 if (line.length() == 0  ||
    		     line.startsWith("*")  ) continue; // skip the line
    		 
    		 // split the line
    		 String[] info = line.split("\t", 5);
    		 if (info.length < 5) continue;  // not enough fields
    		 if (info[0].isEmpty()) continue; // must have Class Code to add
    		 
    		 // define the flag
    		 defineFlag(info[0], info[1], info[2], info[3],
    				    (info[4].equalsIgnoreCase("true") || 
    				     info[4].equalsIgnoreCase("on")      ) );
    	 }
    	 
    	 // close the file
    	 fs.close();
    	 // ignore any errors
    	 
    	 // if requested, show the current list of all known debug flags
    	 if (show) showFlags();
    	 
     }

     //-------------------------------------------------------------------------
     // Known Debug Parameter Methods
     //-------------------------------------------------------------------------
     /**
      *  Adds a single param to the list of known debug params
      *  
      *  @param paramCode string value of the "known" debug param
      *  @param pName string value of the package the known class is part of
      *  @return boolean value indicating the success of the add, returns
      *          <code>false</code> if the param was already "known"
      */
     public boolean addKnown(String paramCode, String pName) {

    	 if (knownParams.containsKey(paramCode)) {
     		err.eNbr     =   9460;
     		err.eType    =   Errs.ETYPE_WARN;
     		err.eMessage =   "Debug Param is already known:  " + paramCode;
     		TR.debugMsgln(dFlag, dPreFix, err.eMessage);
     		return false;
    	}

    	 // add it to the list of known params
    	 knownParams.put(paramCode, pName);
    	 
    	 return true;
     }
     
     /**
      *  Adds a list of params to the list of known debug params
      *  
      *  @param paramCodes List of Strings of param params to be "known"
      *  @param pName string value of the package the known classes are part of
      *  @return boolean value indicating the success of the adds, returns
      *          <code>false</code> if any of params were already "known"
      */
     public boolean addKnown(List<String> paramCodes, String pName) {

    	 if (paramCodes == null || paramCodes.isEmpty()) {
      		err.eNbr     =   9470;
      		err.eType    =   Errs.ETYPE_ERROR;
      		err.eMessage =   "List of Params is null or empty";
      		TR.debugMsgln(dFlag, dPreFix, err.eMessage);
      		return false;
    		 
    	 }

    	 // add each param to the list of known params
    	 List<String> ErrParams = new ArrayList<String>();
    	 
    	 for (String pCode: paramCodes) {
    		 if (!addKnown(pCode, pName))  ErrParams.add(pCode);
    	 }
    	 
    	 if (!ErrParams.isEmpty()) {
     		err.eNbr     =   9480;
     		err.eType    =   Errs.ETYPE_ERROR;
     		err.eMessage =   "Debug Param(s) already known:  " + ErrParams;
     		TR.debugMsgln(dFlag, dPreFix, err.eMessage);
     		return false;
    	}

    	 return true;
     }

     //-------------------------------------------------------------------------
     // Showing the Values Methods
     //-------------------------------------------------------------------------
     /**
      * Displays the defined Debug Flags on the Trace file in the order defined
      * known Debug Flags. If a Debug Flag is "known", but not defined, it is 
      * not printed. Menu Group is NOT displayed.
      */
     public void showFlags() {
     	DebugDetail cVal;
     	String line = "";
        String fmt = "  %-12s : %-7s : %-16s : %-16s : %-12s : %s";
    	
    	// print the title
        TR.printMsgln("Debug parameters");

        // print the column headings
        line = String.format(fmt, " Class Code", " Value",
        		                  " Class",      " Package",
        		                  " Menu Group", 
        		                  " Display Name"           );
        TR.printMsgln(line);
        
        // print the "underlines"
        line = String.format(fmt, "------------", "-------",
        		                  "----------------", "----------------",
        		                  "------------",
        		                  "--------------------------------");
        TR.printMsgln(line);
        
        // print out the values, in a specific order, 
        // to make visual comparisons easier
        for (String key: knownParams.keySet() ){ //DebugInfo.DB_METH_CODES ) {
            if (dFlags.containsKey(key) ) {
            	// get the current record
            	cVal = dFlags.get(key);
            	line = String.format(fmt, 
            			             " " + cVal.classCode, 
            			             " "     + cVal.debugFlag,
            			             " "     + cVal.className,
            			             " "     + cVal.packageName,
            			             " "     + cVal.menuGroup,
            			             " "     + cVal.displayName);
                TR.printMsgln(line);
            }
        }
        TR.printMsgln(" ");

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
     public static LinkedHashMap<String, String> 
              getAllVersions (LinkedHashMap<String, String> vlist) {
    	 String v = DebugStatus.class.getName();
    	 // if it already exists, just return
    	 if (vlist.containsKey(v)) {
    		 return vlist;
    	 }
    	 //add it to the list
    	 vlist.put(v, getVersion());
    	 
    	 vlist = FIO.getAllVersions(vlist);

     	vlist = FIO.getAllVersions(vlist);
     	vlist = TraceThread.getAllVersions(vlist);
     	vlist = Errs.getAllVersions(vlist);

    	 return vlist;
     }

}
