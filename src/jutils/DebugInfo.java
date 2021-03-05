package jutils;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
/**
 * The DbgInfo class maintains the list of debug flags for the classes 
 * defined in this package. 
 */


public class DebugInfo {

    static private final String  version     = "Version 14.70";

    // Static methodCode values for the debug flags
    static public  final String DB_ALL         = "ALL";  // all debug flag
    static public  final String DB_CLEAR       = "CLEAR" ; // clear them all
    

    // jutils package
    static public  final String DB_PACKAGE     = "jutils";  // package name

    // classes that have debugging 
    static public  final String DB_ARGS        = "AG" ;		// Args
    static public  final String DB_DATES       = "DT" ;		// Dates
    static public  final String DB_DEBUGSTATUS = "DS" ;		// Debug Status
	//static public  final String DB_ERRS        = "ER" ;	// Errs - no debug
	static public  final String DB_FILESEARCH  = "FS" ;	// FileSearch - no debug
    static public  final String DB_FIO         = "FIO";		// FIO
    //static public  final String DB_GUIINFO     = "GU";		// GuiInfo - no debug
    //static public  final String DB_HELP        = "HP";		// Help - no debug
    static public  final String DB_SLEEP       = "SP" ;		// Sleep
    //static public  final String DB_TRACE       = "TR" ;	// Trace - no debug
    static public  final String DB_TRACETHREAD = "TT" ;		// Trace Thread
    //static public  final String DB_WINDOWRPT   = "WR" ;	// WindowRpt - no debug
 
    // list of those classes with debugging, in the order to be displayed
    static public  final List<String> DB_METH_CODES
                      = Arrays.asList(// jutils package
                    		  		  DB_ALL,         DB_CLEAR,
                    		  		  DB_ARGS,        
                    		  		  DB_DATES,
                    		  		  DB_DEBUGSTATUS,  
                    		  		  DB_FIO,         DB_SLEEP,
                    		  		  DB_TRACETHREAD
                    		  		  
                    		  		);
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
		String v = DebugInfo.class.getName();
		// if it already exists, just return
		if (vlist.containsKey(v)) {
			return vlist;
		}
		//add it to the list
		vlist.put(v, getVersion());

		return vlist;
	}

}
