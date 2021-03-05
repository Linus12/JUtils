package jutils;
import javax.swing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
/**
 * The TraceThread class is a simple class that allows "windowed" programs to 
 * write their trace messages to a "window", without changes to the program if it 
 * currently writes to files. This allows a a Command Line program and a Windowed
 * program to share classes, and the classes do not have to "know" if a trace message
 * should be written to a window, a file, or StdOut.
 * 
 */


public class TraceThread extends Thread {

    private final static String  version     = "Version 14.70";

    WindowRpt tWindow;

    //JPanel      mainPanel;
    //JScrollPane eScrollPane;
    //JTextArea	errText;
    //int         widthIns              = 500;

    // passed values
    JFrame		aF;       // passed in Application Frame
    GuiInfo     guiInfo;  // passed in 

    //----------------------------------------------------------------------------------
    //   TraceWindow Constructors
    //----------------------------------------------------------------------------------
    /**
     * Create and display the Trace Window.
     * 
     * @param AppFrame the JFrame this thread trace window should be attached to 
     * @param GInfo the instantiated GuiInfo class associated with the AppFrame 
     */
    public TraceThread(JFrame AppFrame, GuiInfo GInfo) {

        aF      = AppFrame;
        guiInfo = GInfo;
    }

    public void run() {
        buildWindow();
        //debugMsg("Starting Thread based messaging - run");
    }
    
    private void buildWindow() {
        // build an empty trace Window
        tWindow = new WindowRpt(aF, guiInfo);
        tWindow.setVisible(true);
        // add a window listener for the Trace Window Dialog
        //tWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        //debugMsg("Starting Thread based messaging - window");

        tWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                //tWindow.dispose();
                tWindow.setVisible(false);
                //tWindow = null;
                
            }
        });
    }

    public void close() {
    	if (tWindow != null)
    		tWindow.dispatchEvent(new WindowEvent(tWindow, 
    				                              WindowEvent.WINDOW_CLOSING));
    }
    
    //-------------------------------------------------------------------------
    // Writes routine
    //-------------------------------------------------------------------------
	/**
	 * writes a line to the file, no hard return
     * 
     * @param line String line to be written
     */
	public void print(String line) {
		checkWindow();
    	tWindow.print(line);
    	return;
	}
	
	/**
	 * writes a line to the file, with a hard return
     * 
     * @param line String line to be written
     */
	public void println(String line) {
		checkWindow();
    	tWindow.println(line);
    	return;
	}
	
	/**
	 * Moves the view of the window to the last line in the window
     * 
     */
	public void moveToBottom() {
		checkWindow();
    	tWindow.moveToBottom();
    	return;
	}
	
	/**
	 * If window isn't there or is hidden, build it or show it
	 * 
	 */
	private void checkWindow() {
        if (tWindow == null) {
            buildWindow();
         }

        if (!tWindow.isVisible()) {
        	tWindow.setVisible(true);
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
		String v = TraceThread.class.getName();
		// if it already exists, just return
		if (vlist.containsKey(v)) {
			return vlist;
		}
		//add it to the list
		vlist.put(v, getVersion());

		vlist = WindowRpt.getAllVersions(vlist);
		vlist = GuiInfo.getAllVersions(vlist);

    	return vlist;
}


}