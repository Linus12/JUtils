package jutils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
/**
 * The Sleep class brings together some common functionality for 
 * "sleeping" while background processes are taking place. Including
 * the ability of producing "slogging" messages, (a row of dots appearing
 * so that user realizes something is happening). "Slogging" is not the
 * same as a progress bar as there is no indication of "how much of the 
 * total" has been completed.
 * 
 */

public class Sleep {

    private static final  String  version  = "Version 14.70";
    private static final  String  dFlag    = DebugInfo.DB_SLEEP;
    private static final  String  dPreFix  = "sl:";

    private int                   multiplier = 1000;
    
    // slogging variables
    private int                 slogMax     = 72; // max length of slog output
    private int                 slogCurr    = 0;  // length of current slog
    private int                 slogCount   = 0;  // count of "slogCount calls"
    private int                 slogCMax    = 500; // max to count before extra sleep
    private int                 slogCLimit  = 200;  // limit of slogCount

    // error handling
    private Errs                  err        = new Errs();
    private StringWriter          errors     = new StringWriter();

    // passed in from calling routines
    private Trace                 TR;          // Trace and output handling

    /** 
     * Standard constructor

	 * Use this routine to create the sleep routine, without changing the 
	 * sleep multiplier.<p>  
	 * See setSleepMultiplier(int) for information on the sleep multiplier
	 * 
	 * @param tr  instantiated Trace Class
     */
    public Sleep(Trace tr) {
        TR = tr;
     }
    
    /**
	 * Use this routine to reset the sleep multiplier for your specific 
	 * machine.<p>  
	 * <b>Note about the sleep multiplier:</b><br>
	 * While 1000 milliseconds should equal a 1 second sleep time, you may have
	 * to also factor in the calling time, and other logic time to come up with
	 * an accurate 1 second sleep time. In the case of my test machine,  
	 * when called in a tight loop, a multiplier of 350 averages out to about 
	 * a 1 second wall clock wait. Again, this is an average when called 
	 * within a loop running about 600 times waiting "1" second between each
	 * call (Waits 10 minutes, checking every second to see if a flag has been
	 * set. <p>
	 * On the other hand, my development box needs it set to 999.
	 * Your mileage will vary<p>
	 * If you need to adjust the multiplier in your situation, can can pass
	 * the new multiplier in this constructor, or call the setSleepMulitiplier
	 * routine directly after creating the instance.
	 *
	 * @param millisecs int value of the "milliseconds" that will equal 1 second
	 *                      on your machine
	 */
    public void setSleepMultiplier(int millisecs) {
    	this.multiplier = millisecs;
    }
    
    /**
	 * Causes the program to wait for a certain amount of time. The amount is
	 * varied by changing the multiplier. While passing in a value of 1 with 
	 * a multiplier of 1000 should equal 1 second, some systems may be slower
	 * or faster. Therefore you may modify the multiplier to give you the 
	 * "wall time" wait you need. 
	 * 
	 * Note that if "verbose" is passed as true, then a message is printed and
	 * wait times done in "one 'secs' intervals to print out a "." to show 
	 * the program is still working. If "verbose" is false, we simply wait the 
	 * full amount in one fell swoop.
	 * 
	 * @param secs The number of "seconds"  you wish to wait.
	 * @param verbose boolean value to indicate 
	 */
	public  void sleep4Time(int secs, boolean verbose) {
        err.initErrs();
        
        Thread.currentThread();
        
		if (verbose) {
			TR.printMsg("Sleeping for about " + secs + " seconds");
			for (int i=secs ; i>0; i--){
				TR.printMsg(".");
				sleepNow(1);
			}
			TR.printMsgln(" ");
		} else {
			sleepNow(secs);
		}

	}
	
	private void sleepNow(int secs) {
		try {
			if (secs < 0) secs = 1;  // minimum sleep time
			Thread.sleep(secs * multiplier);
		} catch (InterruptedException e) {
			err.eNbr = 9700;
			err.eMessage = "Unable to sleep";
			err.eType       = Errs.ETYPE_WARN;
			e.printStackTrace(new PrintWriter(errors));
			err.eStackTrace = "Error msg:  " + e + "\n" + 
			                  errors.toString();
			TR.debugErr(dFlag, dPreFix, err, 
					    "Unable to Sleep " + secs + " multiplier(s)");
			TR.debugMsgln(dFlag, dPreFix, err.eStackTrace);
		}
	
	}

	public void sleepNow(Long milisecs) {
		try {
			if (milisecs < 0) milisecs = 1L;  // minimum sleep time
			Thread.sleep(milisecs);
		} catch (InterruptedException e) {
			err.eNbr = 9710;
			err.eMessage = "Unable to sleep";
			err.eType       = Errs.ETYPE_WARN;
			e.printStackTrace(new PrintWriter(errors));
			err.eStackTrace = "Error msg:  " + e + "\n" + 
			                  errors.toString();
			TR.debugErr(dFlag, dPreFix, err, 
					    "Unable to Sleep " + milisecs + " milisecond(s)");
			TR.debugMsgln(dFlag, dPreFix, err.eStackTrace);
		}
	
	}

    //-------------------------------------------------------------------------
    // methods to print "yes we're still working" indicator
    //-------------------------------------------------------------------------
    /**
     * prints the starting message for the "slogging through" message
     * @param msg String value of the message to print
     * @param getsPer int value of the number of gets per call
     */
    public void slogMsgStart(String msg, int getsPer) {
    	TR.printMsg("  " + msg);
    	slogCurr = msg.length();
    	// set the slog count limit based on number of gets per call
    	slogCLimit = slogCMax / getsPer;
    	slogCount  = 0;
    }
    
    /**
     * Prints out a dot indicating progress is happening
     */
    public void slogMsgCount() {
    	if (slogCurr >= slogMax) {
    		TR.printMsg("\n  ");
    		slogCurr = 0;
    	}

    	// if we've reached the maximum number of slog calls, then sleep for
    	// three seconds otherwise just sleep for 1 second.
    	// This gives the interface a breather and keeps us from
    	// making too many calls too fast.
    	if (slogCount >= slogCLimit) {
        	TR.printMsg("*");
        	sleep4Time(3, false);
    		slogCount = 0;
    	} else {
        	TR.printMsg(".");
        	sleep4Time(1, false);
    	}

    	slogCurr++;
    	slogCount++;

    }

    /**
     *  Prints out final message indicating the "slogging" is done
     */
    public void slogMsgDone() {
    	String msg = "Done!";
    	if ((slogCurr + msg.length()) > slogMax) {
    		TR.printMsgln(" ");
    		slogCurr = 0;
    		TR.printMsg(".");
    	}
    	TR.printMsgln(msg);
    	slogCurr  = 0;
    	slogCount = 0;
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
     * @param vlist Map of the current class/versions to be appended to
     * @return Map of the class/version values for this and all called
     *                routines
     */
	public static LinkedHashMap<String, String> 
	         getAllVersions (LinkedHashMap<String, String> vlist) {
		String v = Sleep.class.getName();
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