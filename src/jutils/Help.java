package jutils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
/**
 * The Help class handles the returning of information in the "help Files"
 * so that actual text in "program 'help' screens or on StdOut can be 
 * maintained in better editors, rather than directly embedded in the 
 * .java source files.
 * 
 */
public class Help{

    private final static  String version     = "Version 03.00";
    // no debugging trace info currently configured
    //private static final String  dFlag       = DebugInfo.DB_HELP;
    //private static final String  dPreFix     = "Help:";

    private Boolean       newLine            = false;

    private HashMap <String, List <String> > helpFiles =
    		                 new HashMap <String, List <String> >(); 

    // error handling
    private Errs                  err        = new Errs();
    private StringWriter          errors     = new StringWriter();

    
    //-------------------------------------------------------------------------
    // Constructors Methods
    //-------------------------------------------------------------------------
    /**
     * Basic Constructor for Help.
     * 
     * Defaults to not adding a NewLine after each line after read from the 
     * file.
     * 
     */
    public Help(){
    }

    /**
     * Basic Constructor for Help.
     * 
     * Allows the user to pass a boolean to determine if a newline should be
     * added after reading each line of the file. 
     * 
     * @param nlFlag  Boolean indicating if a new line should be added
     *                (<code>true</code>) to the end of each line in the help
     *                file, or not (<code>false</code>).
     */
    public Help(Boolean nlFlag){
    	newLine = nlFlag;
    }

    //-------------------------------------------------------------------------
    // Help Setup Methods
    //-------------------------------------------------------------------------
    /**
     * Sets the newLine value to the passed in value
     * 
     * @param nlFlag  Boolean indicating if a new line should be added
     *                (<code>true</code>) to the end of each line in the help
     *                file, or not (<code>false</code>).
     */
    public void setNLFlag(Boolean nlFlag){
    	newLine = nlFlag;
    }

    /**
     * Adds one or more help files to the list of help files, associated with 
     * an identifier for easier help file maintenance and display
     *  
     * @param ident String identifier for this type of help
     * @param hFiles List of strings of file names to be read 
     * 
     * @return boolean indicating if a new entry was added (<code>true</code>), 
     *             or if an existing entry was replaced (<code>false</code>)
     */
    public boolean defineHelp(String ident, List <String> hFiles){
        err.initErrs();
    
    	boolean retval = helpFiles.containsKey(ident);
    	
    	helpFiles.put(ident, hFiles);

    	if (retval) {
            err.eNbr     = 9600;
            err.eMessage = "Help list replaced for  " + ident;
            err.eType    = Errs.ETYPE_WARN;
    		
    	}
    	
    	return !retval;
    }
    //-------------------------------------------------------------------------
    // Help Methods
    //-------------------------------------------------------------------------
    /**
     * Get the appropriate list of help files from the previously setup HashMap
     * and return the concatenated results in a stringBuilder
     * 
     * @param ident String identifier for this type of help
     * @param htmlOutput boolean indicating if the method is outputting to a 
     *                     'window' and therefore needs HTML codes for
     *                     proper display  
     * @return   StringBuilder of the help message requested
     */
    public StringBuilder getHelp(String ident, boolean htmlOutput) {
        err.initErrs();
   	
    	// if the ident doesn't exist, set error and return null
    	if (!helpFiles.containsKey(ident)) {
            err.eNbr     = 9610;
            err.eMessage = "No Help list defined for  " + ident;
            err.eType    = Errs.ETYPE_ERROR;
            return null;
    	}
    	
    	// get the list of files to be concatenated
    	List <String> hUrl = helpFiles.get(ident);
    	
    	// get and return the help 
    	return getHelp(hUrl,htmlOutput);
    	
    }

    /**
     * Read the appropriate help files and return the concatenated results in
     * a StringBuilder
     * 
     * @param hFiles  List of strings containing the resource files to read
     * @param htmlOutput boolean indicating if the method is outputting to a 
     *                     'window' and therefore needs HTML codes for
     *                     proper display  
     * @return   StringBuilder of the help message requested
     */
    public StringBuilder getHelp(List <String> hFiles, boolean htmlOutput){
        err.initErrs();

        StringBuilder helpString = new StringBuilder();

        // if the list of URLS is empty, return null
        if (hFiles.isEmpty()) {
            err.eNbr     = 9620;
            err.eMessage = "List of Help files is empty!";
            err.eType    = Errs.ETYPE_ERROR;
            return null;
        	
        }
        
		// output the help text(s)
    	for(String hf : hFiles){
    		// get the help contents, setting error if needed
    		StringBuilder fh = fileHelp(hf);
    		if (fh.length() != 0) {
    			// append it, and separate it from the next one
    			helpString.append(fh);
    			helpString.append("\n");
    		} 
    		// if there was an error, stop processing
    		if (err.eType != Errs.ETYPE_NONE) continue;
    	}
    	
		// html format for a window?
		if (htmlOutput) {
			helpString.insert(0, "<html><body>\n<pre>\n");
			helpString.append("</pre>\n</body></html>");
		}

    	return helpString;
    }

    //-------------------------------------------------------------------------
    // Getting Help Info Methods
    //-------------------------------------------------------------------------
    /**
     * Reads the appropriate help file, append each line to a StringBuilder.
     * @param fName  String of the resource file to be read
     * 
     * @return   StringBuilder of the appended lines from the file
     * 
     */
    private StringBuilder fileHelp(String fName) {
    	StringBuilder helpBuffer = new StringBuilder();

		if (fName== null || fName.isEmpty()) {
			err.eNbr        = 9630;
			err.eMessage    = "Help File Name is empty or null"; 
			err.eType       = Errs.ETYPE_ERROR;
			return helpBuffer;
    	}

    	try {
    		URL resourceFile = getClass().getResource(fName);

    		if (resourceFile == null) {
    			err.eNbr        = 9640;
    			err.eMessage    = "File missing:  " + fName; 
    			err.eType       = Errs.ETYPE_ERROR;
    			return helpBuffer;
        	}
        	

    		File file = new File(resourceFile.getFile());
    		FileReader fileReader = new FileReader(file);

    		BufferedReader bufferedReader = new BufferedReader(fileReader);
    		String line;

    		while ((line = bufferedReader.readLine()) != null) {
    			helpBuffer.append(line);
    			if (!newLine) 
    				helpBuffer.append("\n");
    		}

    		fileReader.close(); 

    	} catch (IOException e) {
			err.eNbr        = 9650;
			err.eMessage    = "Unable to process Help File:  " + fName; 
			err.eType       = Errs.ETYPE_ERROR;
			e.printStackTrace(new PrintWriter(errors));
			err.eStackTrace = "Error msg:  " + e + "\n" + errors.toString();
    	}

    	return helpBuffer;  // return what we have
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
 		String v = Help.class.getName();
 		// if it already exists, just return
 		if (vlist.containsKey(v)) {
 			return vlist;
 		}
 		//add it to the list
 		vlist.put(v, getVersion());

    	vlist = Errs.getAllVersions(vlist);
    	return vlist;
    }

}
