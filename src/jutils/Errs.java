package jutils;
import java.util.LinkedHashMap;

/**
 * The Errs class is a simple class that allows classes to set error 
 * information including a number, a type (None, Error, Warning), an Error
 * Message and StackTrace. This allows this information to be easily obtained
 * by a class without having to always pass it, as it can get it only when 
 * needed.
 * 
 */


public class Errs {

    private static final String  version     = "Version 14.70";

    public               int     eNbr        = 0;
    public               String  eMessage    = "";
    public               String  eStackTrace = ""; 
    public               int     eType       = 0;
    static public final int  ETYPE_NONE      = 0;
    static public final int  ETYPE_ERROR     = 1;
    static public final int  ETYPE_WARN      = 2;
    public               String  eField      = "";

    /**
     * Constructor for Error class
     */
    public Errs () {
        initErrs();
    }
    
    /**
     * Constructor for Error class, copying values from a current one
     * 
     * @param err Errs instance to copy values from 
     */
    public Errs(Errs err) {
        eNbr        = err.eNbr;
        eMessage    = err.eMessage;
        eStackTrace = err.eStackTrace;
        eType       = err.eType;
        eField      = err.eField;
    }
 
   /**
    * Initializes the values in the Errs class
    */
    public void initErrs() {
        eNbr           = 0;
        eMessage       = "";
        eStackTrace    = "";
        eType          = ETYPE_NONE;
        eField         = "";

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
		String v = Errs.class.getName();
		// if it already exists, just return
		if (vlist.containsKey(v)) {
			return vlist;
		}
		//add it to the list
		vlist.put(v, getVersion());

    	return vlist;
    }


}
