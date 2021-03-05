package jutils;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * The FileSearch class brings together some common file locating
 * functionality with error checking. This includes the ability to search 
 * through multiple directories for a file with a single call.
 * 
 * 14.70  Added isDir to allow the check of Directory existance without
 *        loading all the files into a HashMap.
 * 
 *
 */


public class FileSearch {

    private static final String  version  = "Version 03.00";
    private static final String  dFlag       = DebugInfo.DB_FILESEARCH;
    private static final String  dPreFix     = "FSearch:";
    
    // List of files to be searched through
    private HashMap<String, String> fNames = new HashMap<String, String>();
    
    // support classes
    private Errs     err       = new Errs();       // for returning errors 
    private Trace    TR;                           // passed in constructor
    
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------
    /**
     * Constructor for FileSearch, holder of existing files in the directory
     * 
     * @param tr instantiated Trace class for tracing debugging errors
     */
	public FileSearch(Trace tr){
		// save the trace class for future access
		TR = tr;

	}

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
    //  File exist Checking
    //-------------------------------------------------------------------------
    /**
     * Returns a boolean flag indicating if the passed File Name exists in the
     * loaded HashMap of files. 
     * 
     * @param fileNameToSearch String value of the file name to look for
     * @return boolean indicating if name was found in the HashMap; 
     *               <code>true</code>if it was found, otherwise 
     *               <code>false</code>
     */
    public boolean locateFile(String fileNameToSearch) {

    	err.initErrs();
    	
    	if (fNames.containsKey(fileNameToSearch.toUpperCase()) ) {
    		return true;
    	}

    	err.eNbr     = 9900;
		err.eMessage = "File not found in Map:\t" + fileNameToSearch;
		err.eType    = Errs.ETYPE_WARN;
        TR.debugErr(dFlag, dPreFix, err);
    	return false;

    }

    //-------------------------------------------------------------------------
    // File Name Map loading
    //-------------------------------------------------------------------------
    /**
     * Verifies the passed file is a directory and then loads all of the files
     * in the passed directory and all sub-directories into the HashMap.
     * If the passed in file is not a directory, an error is returned.
     * @param dirName File class for the Top level directory name to be 
     *                 searched for files
     * @return boolean value indicating that if there were any problems. 
     *               <code>true</code> is returned if no problems, otherwise
     *               <code>false</code> is returned and the Err values are set
     */
    public boolean loadDir(File dirName) {
    	err.initErrs();

    	// Make sure the file is a directory and it exists
    	if (!isDir(dirName))
    		return false;
    	
    	// load the files, if any, into the HashMap, 
    	//including those in sub directories
    	loadAll(dirName);

    	return true;
    }

    /**
     * Verifies the passed file is a directory. 
     * If the passed in file is not a directory, an error is returned.
     * 
     * @param dirName File class for the Top level directory name to be 
     *                 searched for files
     * @return boolean value indicating that if there were any problems. 
     *               <code>true</code> is returned if no problems, otherwise
     *               <code>false</code> is returned and the Err values are set
     */
    public boolean isDir(File dirName) {
    	err.initErrs();

    	// verify file exists      
    	if (!dirName.exists()) {
        	err.eNbr     = 9910;
    		err.eMessage = "Directory does not exist:\t" + dirName;
    		err.eType    = Errs.ETYPE_ERROR;
            TR.debugErr(dFlag, dPreFix, err);
    		return false;
    	}

    	// verify file is a directory      
    	if (!dirName.isDirectory()) {
        	err.eNbr     = 9920;
    		err.eMessage = "File is not a directory:\t" + dirName;
    		err.eType    = Errs.ETYPE_ERROR;
            TR.debugErr(dFlag, dPreFix, err);
    		return false;
    	}

    	return true;
    }
    /**
     *Loads each if the files located in the passed in directory into the 
     * HashMap. If the any of the found files are directories, this routine
     * is recursively called so that ALL files in all sub-directories are 
     * added.
     * @param dirName  File Class for the Directory to be searched for files
     */
    private void loadAll(File dirName) {

		TR.printMsgln("Searching directory... " + dirName.getAbsoluteFile());
    	
    	// for each directory/file in the current directory
    	for (File fn : dirName.listFiles()) {
    		// if directory, load files in that directory
    		if (fn.isDirectory())   
    			loadAll(fn);
    		 else 
    			loadFile(fn.getName());
    	}

    }
    
    /**
     * Loads the passed file name into the HashMap, verifying it doesn't 
     * exist before hand. The key is shifted to upper case to make searching
     * faster.
     * @param fileName  String value of the file Name
     */
    public void loadFile(String fileName) {
		// if not already in the HashMap, add it
		String key = fileName.toUpperCase();
		if (!fNames.containsKey(key) ) {
				fNames.put(key, fileName);
		}
    	
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

    	String v = FileSearch.class.getName();
    	// if it already exists, just return
    	if (vlist.containsKey(v)) {
    		return vlist;
    	}

    	//add it to the list
    	vlist.put(v, getVersion());
    	
    	// supporting classes
    	vlist = Errs.getAllVersions(vlist);
    	vlist = Trace.getAllVersions(vlist);

    	return vlist;
    }

}