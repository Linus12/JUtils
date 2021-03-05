package jutils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;

/**
 * The FIO class brings together some common File functionality including
 * opening, closing reading, and writing to files; with error checking.
 * Also included is the option to "trace" the calls by writing info out 
 * to a previously opened "traceFile" (or to StdOut).
 * 
 */

public class FIO {

    private  final static String   version      = "Version 14.70";
    private  final static String   dFlag        = DebugInfo.DB_FIO;
    private  final static String   dPreFix      = "fio:";

	// Types of files
	static public final String FIO_READ   = "R";
	static public final String FIO_WRITE  = "W"; 
	static public final String FIO_APPEND = "A";
	
	private  String          fileType   = "";
    private  String          fileName   = "";
    private  boolean         fileOpen   = false;
    private  boolean         eof        = false;
    
    // For File Writers and Appenders
    private  FileWriter      fw         = null;
    private  BufferedWriter  bw         = null;
    private  PrintWriter     out        = null;

    // For File Readers
	private File              file      = null;
	private FileInputStream   fis       = null;
	private BufferedReader    br        = null;
	private InputStreamReader isr       = null; 

    private  boolean         traceable  = false;
    
    private  Errs            err        = null;
    private  StringWriter    errors     = new StringWriter(); 

    // passed values
    private Trace              TR;                        // trace, passed in
    
    /**
     * File I/O routines<p>
     * Working with multiple open files is a pain, this class encompasses 
     * most of my file input/output routines with the error handling 
     * and trace process of these programs.
     * Current capabilities are:
     * <ul><li>open
     *     <li>close 
     *     <li>readline
     *     <li>writeline
     * </ul>
     * 
     * Additional routines may be added in the future.
     *  
     *  @param tr Trace instance for debugging and error messages, if null
     *            all messages are sent to System.out.<br>
     *            <b>Note:  </b> When working with the TraceFile, passing a 
     *                           null value makes sure that the methods do 
     *                           not try to write a message to the trace file
     *                           while managing the trace file!
     *                           
     */
    public FIO (Trace tr) {
    	
    	err = new Errs();
    	
    	
    	if (tr == null) {
    		traceable = false;
    	} else {
    		traceable = true;
    		TR        = tr;
    	}
    	
    }  

    //-------------------------------------------------------------------------
    // Public access to private values
    //-------------------------------------------------------------------------
    public boolean eof(){
    	return eof;
    }
    public boolean isOpen(){
    	return fileOpen;
    }
    public String fileName() {
    	return fileName;
    }
    public String fileFileName() {
    	return file.getAbsolutePath(); //getName();
    }
    //-------------------------------------------------------------------------
    // Open routines
    //-------------------------------------------------------------------------
	/**
	 * Opens the file for reading, writing, or appending.
     * 
     * @param  fileName String value of the file to be opened
     * @param  fileType String value of <b>R</b>, <b>W</b>, or <b>A</b>,
     *                  indicating we will open the file in Read, Write, or
     *                  Append mode 
     * @return boolean <code>true</code> if successful, 
     *                 otherwise <code>false</code>
     */
	public boolean open(String fileName, String fileType) {
		
		err.initErrs();
		
		this.fileName = fileName;
		this.fileType = fileType;
		
    	if (traceable)     // only printed for traceable opens!  14.03  
    		debugMsg("Open called for :  " + fileName);
		
		switch (fileType) {
		case FIO_READ:   return openRead();
		case FIO_WRITE:  return openWrite(false);
		case FIO_APPEND: return openWrite(true);
		default:
			// invalid file type
			err.eNbr        = 9500;
			err.eMessage    = "Invalid File Type:  " + fileType + 
					          ". Must be either: " +
					          FIO_READ +", " +
					          FIO_WRITE + ", or " + 
					          FIO_APPEND; 
			err.eType     = Errs.ETYPE_ERROR;
			debugErr(err, "Error attempting to open the file:  " + fileName);
			return false;
		}	
	}	

	/**
	 * Opens a  file for reading.
	 * 
	 * @return boolean <code>true</code> if successful, 
	 *                 otherwise <code>false</code>
	 */
	private boolean openRead() {
		err.initErrs();

		file    = new File(fileName);

		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			err.eNbr        = 9510;
			err.eMessage    = "Unable to open the file:  " + fileName; 
			err.eType       = Errs.ETYPE_ERROR;
			e.printStackTrace(new PrintWriter(errors));
			err.eStackTrace = "Error msg:  " + e + "\n" + errors.toString();
			debugErr(err, "Error attempting to open the file for Read Access");
			debugMsg(err.eStackTrace);
			return false;
		}

		isr = new  InputStreamReader(fis);
		br  = new  BufferedReader(isr);

		fileOpen = true;
		eof      = false;
		return true;
	}

	/**
	 * Opens a  file for writing
	 * 
	 * @param  boolean set to <code>true</code> to append to file, 
	 *                 <code>false</code> to overwrite
	 * @return boolean <code>true</code> if successful, 
	 *                 otherwise <code>false</code>
	 */
    private boolean openWrite(boolean appendit) {
		err.initErrs();
		
		String type = "";
		if (appendit) {
			type = "Append";
		} else {
			type = "Write";
		}

		file    = new File(fileName);

		// try to open the file, catching any errors
    	try{
    		fw  = new FileWriter(fileName, appendit);
    	    bw  = new BufferedWriter(fw);
    	    out = new PrintWriter(bw, true);
   	  	} catch( IOException e ){
            err.eNbr        = 9520;
            err.eMessage    = "Unable to open the file:  " + fileName; 
            err.eType       = Errs.ETYPE_ERROR;
            e.printStackTrace(new PrintWriter(errors));
            err.eStackTrace = "Error msg:  " + e + "\n" + errors.toString();
            debugErr(err, "Error attempting to the file for " + type +
            		      " access");
   	  		debugMsg(err.eStackTrace);
   	  		return false;
    	}

    	fileOpen = true;
    	return true;
    }

    //-------------------------------------------------------------------------
    // Close routines
    //-------------------------------------------------------------------------
	/**
	 * closes a open file 
     * 
     * @return boolean <code>true</code> if successful, 
     *                 otherwise <code>false</code>
     */
	public boolean close() {
		err.initErrs();

		debugMsg("Close called for :  " + fileName);
		
		
		if (!fileOpen) {
			return true;
		}
		
		switch (fileType) {
		case FIO_READ:   return closeRead();
		case FIO_WRITE:
		case FIO_APPEND: return closeWrite();
		default:
			// invalid file type
			err.eNbr        = 9530;
			err.eMessage    = "Invalid File Type:  " + fileType + 
					          ". Must be either: " +
					          FIO_READ +", " +
					          FIO_WRITE + ", or " + 
					          FIO_APPEND; 
			err.eType       = Errs.ETYPE_ERROR;
			debugErr(err, "Error attempting to close the file:  " + fileName);
			return false;
		}
	}	
	
	/**
	 * closes a file opened for reading.
     * 
     * @return boolean <code>true</code> if successful, 
     *                 otherwise <code>false</code>
     */
	private boolean closeRead() {
		err.initErrs();
		
		try {
			br.close();
			isr.close();
			fis.close();
			//	inFile.close();
		} catch (IOException e) {
            err.eNbr        = 9540;
            err.eMessage    = "Unable to close the file:  " + fileName; 
            err.eType       = Errs.ETYPE_ERROR;
            e.printStackTrace(new PrintWriter(errors));
            err.eStackTrace = "Error msg:  " + e + "\n" + errors.toString();
            debugErr(err, "Error attempting to close the file with Read Access");
   	  		debugMsg(err.eStackTrace);
			return false;
		}
		
		fileOpen = false;
		return true;
		
	}

	/**
	 * closes a file opened for writing/appending.
     * 
     * @return boolean <code>true</code> if successful, 
     *                 otherwise <code>false</code>
     */
	private boolean closeWrite() {
		err.initErrs();
		
		// secondary check for output files
    	if (out == null) {
    		return true;
    	}
 
		String type = "";
		if (fileType.equals(FIO_APPEND)) {
			type = "Append";
		} else {
			type = "Write";
		}

    	try{
    		fw.close();
    	    bw.close();
    	    out.close();
   	  	} catch( IOException e ){
            err.eNbr        = 9550;
            err.eMessage    = "Unable to close the file:  " + fileName; 
            err.eType       = Errs.ETYPE_ERROR;
            e.printStackTrace(new PrintWriter(errors));
            err.eStackTrace = "Error msg:  " + e + "\n" + errors.toString();
            debugErr(err, "Error attempting to the file for " + type +
      		      " access");
   	  		debugMsg(err.eStackTrace);
   	  		return false;
    	}
    	
    	fileOpen = false;
    	return true;
    }
	
    //-------------------------------------------------------------------------
    // Read routine
    //-------------------------------------------------------------------------
	/**
	 * Reads a line from the file 
     * 
     * @return String value of the line read, null if end of File is encountered
     */
	public String readline(){
		err.initErrs();
		
		String line = null;
		try {
			line = br.readLine();
		} catch (IOException e) {
            err.eNbr        = 9560;
            err.eMessage    = "Unable to read the file:  " + fileName; 
            err.eType       = Errs.ETYPE_ERROR;
            e.printStackTrace(new PrintWriter(errors));
            err.eStackTrace = "Error msg:  " + e + "\n" + errors.toString();
            debugErr(err, "Error attempting to read the file");
   	  		debugMsg(err.eStackTrace);
		}
		
		if (line == null) {
			eof = true;
		}
		
		return line;
		
	}
	
    //-------------------------------------------------------------------------
    // Writes routine
    //-------------------------------------------------------------------------
	/**
	 * writes a line to the file, no hard return
     * 
     * @param line String line to be written
     * @return boolean value indicating success or failure of the write
     */
	public boolean print(String line) {
		err.initErrs();
		// Make sure file is opened for write!
    	if (out == null) {
            err.eNbr        = 9570;
            err.eMessage    = "Unable to write to the file:  " + fileName; 
            err.eType       = Errs.ETYPE_ERROR;
            debugErr(err, "Error attempting to write to the file");
    		return false;
    	}
 
    	// write the line to the file
    	out.print(line);
    	out.flush();
    	return true;
	}
	
	/**
	 * writes a line to the file, with a hard return
     * 
     * @param line String line to be written
     * @return boolean value indicating success or failure of the write
     */
	public boolean println(String line) {
		err.initErrs();
		// Make sure file is opened for write!
    	if (out == null) {
            err.eNbr        = 9580;
            err.eMessage    = "File not open for write:  " + fileName; 
            err.eType       = Errs.ETYPE_ERROR;
            debugErr(err, "Error attempting to write to the file");
    		return false;
    	}
 
    	// write the line to the file
    	out.println(line);
    	out.flush();
    	return true;
	}
	
    //-------------------------------------------------------------------------
    // Rename routine
    //-------------------------------------------------------------------------
	/**
	 * Renames the file to the passed in file name, purging the "new" file if 
	 * it exists.
     * 
     * @param  fileName String value of the new file name
     * @return boolean <code>true</code> if successful, 
     *                 otherwise <code>false</code>
     */
	public boolean rename(String fileName) {
		
		if(isOpen()) {
			if (!close()) {
				return false; // error already sets
			}
		}

        Path movefrom = file.toPath();       // the existing file

        File newFile =  new File(fileName);  //passed in "new" name
        Path moveto   = newFile.toPath();

        try {
            Files.move(movefrom, moveto, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
			err.eNbr        = 9590;
			err.eMessage    = "Unable to move the file:  " + movefrom.toString() +
					          " to " + moveto.toString();
//			err.eMessage    = "Unable to move the file:  " + file.getName() +
//			          " to " + newFile.getName();
			err.eType       = Errs.ETYPE_ERROR;
			e.printStackTrace(new PrintWriter(errors));
			err.eStackTrace = "Error msg:  " + e + "\n" + errors.toString();
			debugErr(err, "Error attempting rename the files");
			debugMsg(err.eStackTrace);
			return false;
        }
        
    	return true;
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
    //  Debug trace routines
    //-------------------------------------------------------------------------
    /**
     * Displays a debugging message<p>
     * Coded here because the "TR" trace may not be available (such as when
     * opening the trace file!) 
     */
    private void debugMsg(String message) {
    	
    	if (traceable) {  
    		TR.debugMsgln(dFlag, dPreFix, message);
    	} else {
    		System.out.println(dPreFix + "\t" + message);
    	}
    }

    /**
     * Displays an error/warning message with a debugging message<p>
     * Coded here because the "TR" trace may not be available (such as when
     * opening the trace file!) 
     */
    private void debugErr(Errs err, String title) {
    	if (traceable) { 
    		TR.debugErr(dFlag, dPreFix, err, title);
    	} else {
    		if (err.eNbr != 0) {
    			String msg = dPreFix + "\t"    + title             +
    						           "\n\t(" + err.eNbr + ")   " + 
    						           err.eMessage;
    			System.out.println(msg);
    		}
    	}
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
		String v = FIO.class.getName();
		// if it already exists, just return
		if (vlist.containsKey(v)) {
			return vlist;
		}
		//add it to the list
		vlist.put(v, getVersion());

		vlist = Trace.getAllVersions(vlist);
    	vlist = Errs.getAllVersions(vlist);
    	return vlist;
	}

}