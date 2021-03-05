package jutils;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
/**
 * The GuiInfo class brings together some common Window functionality for 
 * programs using windowed interfaces. 
 * 
 */


public class GuiInfo{

    private final static  String version     = "Version 14.70";
    // no debugging trace info currently configured
    //private static final String  dFlag       = DebugInfo.DB_GUIINFO;
    //private static final String  dPreFix     = "GInfo:";

    private String           windowTitle     = "default";
    private int              numWindows      = 0;
    
    private ArrayList<Image> imageList   = new ArrayList<Image>();
    
    //Specify the Default "look and feel" to use.  Valid values:
    //null (use the default), "Metal", "System", "Windows7", "Motif", "GTK+"
    final   static   String   LOOKANDFEEL =  "Windows7";

    
    /**
     * Class constructor
     * 
     * @param title  String value of the title to set for this window
     * @param iconURLs String Array of icons to be used for this window
     */
    public GuiInfo(String title, String[] iconURLs){

    	// save the window Title
    	windowTitle  = title;
    	
    	// initialize the number of windows in play
    	numWindows = 0;
    	
    	// set up the icons for the Windows
    	if (iconURLs != null)
    		for(String url : iconURLs){
    			Image image = loadImage(url);
    			if (image != null) {
    				imageList.add(image);
    			}
    		}
    	
    }
    /**
     * returns the buffered image loaded from the passed in location
     * 
     * @param URL the location of the image to be obtained
     * @return the buffered image
     */
    private BufferedImage loadImage(String URL){
    	BufferedImage bi;
    	try {
    		bi = ImageIO.read( getClass().getResource(URL));
    		//            bi = ImageIO.read(y);
    	} catch (IOException e) {
    		// just ignore it and move on
    		return null;
    	}
    	return bi;
    }

    //-------------------------------------------------------------------------
    // Look and Feel Version Routines
    //-------------------------------------------------------------------------
    /**
     * Sets the UIManager's look and feel. Should be called before showing the
     * GUI.<p>
     * Valid values are :
     * <ul><li> null (use the default)
     *     <li> "Metal"
     *     <li> "System"
     *     <li> "Windows7"
     *     <li> "Motif"
     *     <li> "GTK+"
     * </ul>
     * @param inLookAndFeel string value of look and feel to implement
     * @return String value of actual value used
     */
    public String initLookAndFeel(String inLookAndFeel) {
        
        String  lookAndFeel   = null;
        String  uiLookAndFeel = null;
        Boolean tryAgain      = false;
        
        if (inLookAndFeel == null || inLookAndFeel.isEmpty()) {
            lookAndFeel = LOOKANDFEEL;
        } else {
            lookAndFeel = inLookAndFeel;
        }
        
        // get the actual look and feel, based on the passed in value
        if (lookAndFeel.equalsIgnoreCase("Metal")) {
            uiLookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
        } else if (lookAndFeel.equalsIgnoreCase("System")) {
            uiLookAndFeel = UIManager.getSystemLookAndFeelClassName();
        } else if (lookAndFeel.equalsIgnoreCase("Windows7")) {
            uiLookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        } else if (lookAndFeel.equalsIgnoreCase("Motif")) {
            uiLookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        } else if (lookAndFeel.equalsIgnoreCase("GTK+")) { //new in 1.4.2
            uiLookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        } else {
            System.err.println("Unexpected value of LOOKANDFEEL specified: "
                    + lookAndFeel);
            uiLookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
        }

        // Attempt to actually set the Look And Feel
        try {
            UIManager.setLookAndFeel(uiLookAndFeel);
        } catch (ClassNotFoundException e) {
            System.err.println("Couldn't find class for specified look and feel:"
                    + lookAndFeel);
            System.err.println("Did you include the L&F library in the class path?");
            tryAgain = true;
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Can't use the specified look and feel ("
                    + lookAndFeel
                    + ") on this platform.");
            tryAgain = true;
        } catch (Exception e) {
            System.err.println("Couldn't get specified look and feel ("
                    + lookAndFeel
                    + "), for some reason.");
            e.printStackTrace();
            tryAgain = true;
        }

        if (tryAgain && !lookAndFeel.equalsIgnoreCase(LOOKANDFEEL) ){
            System.err.println("Using the default look and feel.");
            return initLookAndFeel(LOOKANDFEEL);
        }
        

        // return the look asd feel 
        return uiLookAndFeel;
    }
    /**
     * returns the ArrayList of the possible images for the window Icon
     * 
     * @return then arrayList of the possible images for the window icon
     */
    public ArrayList<Image> getImageList() {
    	return imageList;
    }

    public boolean imageListLoaded() {
    	if (imageList ==null) {
    		return false;
    	} else {
    		return true;
    	}
    }
    /**
     * Window Title
     * @return String value of the Window Title
     */
    public String getWindowTitle() {
    	return windowTitle;
    }
    

    /**
     * Number of Windows Routines
     * @return int value of the number of windows currently open
     */
    public int getNumWindows() {
    	return numWindows;
    }

    public void incNumWindows() {
    	numWindows++;
    }

    public void decNumWindows() {
    	numWindows--;
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
 		String v = GuiInfo.class.getName();
 		// if it already exists, just return
 		if (vlist.containsKey(v)) {
 			return vlist;
 		}
 		//add it to the list
 		vlist.put(v, getVersion());
    	return vlist;
    }

}
