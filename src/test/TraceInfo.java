import jutils.Trace;

/**
 * The TraceInfo class contains the different flags, filenames, and Trace
 * instances for testing. There are two sets of values, one for the Test
 * Classes and one for the Classes being tested.
 * 
 * @Author    David H. Stumpf
 */
    public class TraceInfo {
    	// Debug information for the Test Program and TestClasses
    	boolean   pgmDFlag       = false;  // run test Program in debug? 
    	String    pgmTrace       = null;   // Test Program trace file
    	Trace      TR           = null;    // Test Program OUTPUT
    	
    	
    	// Debug information for the Classes being tested
    	// output.
    	boolean     testDFlag    = false;  // run tests in debug? 
    	String      testTrace    = null;   // Test Program trace file
    	Trace      TTR          = null;    // Test TraceFile
    	
    }

