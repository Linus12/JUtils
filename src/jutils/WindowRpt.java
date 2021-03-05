package jutils;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
/**
 * A simple window that can be opened so that text can be written on it. 
 * Used for displaying "help" messages, "about" messages, and "version 
 * information" in windowed programs.
 * 
 */


@SuppressWarnings("serial")
public class WindowRpt extends JFrame {

    private final static  String version     = "Version 14.70";
    // no debugging trace info currently configured
    //private static final String  dFlag       = DebugInfo.DB_WINDOWRPT;
    //private static final String  dPreFix     = "WReport:";


    final static boolean MULTICOLORED = false;

    JPanel      mainPanel;
    JScrollPane eScrollPane;
    JTextArea	errText;
    int         widthIns              = 500;

    //----------------------------------------------------------------------------------
    //   WindowRpt Constructors
    //----------------------------------------------------------------------------------
    /**
     * Create and display a Report Window. Used for output and for "tracing messages"
     * 
     * @param AppFrame JFrame class of the Parent JFrame this window is attached to
     * @param guiInfo  Instantiated GuiInfo class of the AppFrame 
     */
    public WindowRpt(JFrame AppFrame, GuiInfo guiInfo) {

    		//GuiInfo guiInfo, int windowNum, 
        super(guiInfo.getWindowTitle() + " Stats");
        if (guiInfo.getNumWindows() > 1) {
            this.setTitle(this.getTitle() + 
            		       "    -    (" + guiInfo.getNumWindows() + ")");
        }
        
        // set up the error field
        errText = new JTextArea();
        errText.setSize(widthIns,20);
        errText.setEditable(false);
        if (MULTICOLORED) {
            errText.setOpaque(false);
            errText.setBackground(new Color(255, 0, 0));
        }
        Color bColor = this.getBackground();    
        errText.setBackground(bColor);
        errText.setVisible(true);
        
        //always scroll when appending
        DefaultCaret caret = (DefaultCaret)errText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);



        // set up the scrollPane
        eScrollPane = new JScrollPane();
        eScrollPane.setLayout(new ScrollPaneLayout());
        eScrollPane.setViewportView(errText);
        if (MULTICOLORED) {
            eScrollPane.setOpaque(true);
            eScrollPane.setBackground(new Color(255, 0, 0));
        }
        eScrollPane.setVisible(true);
        eScrollPane.setPreferredSize(new Dimension(1000, 200));

        // add the scrollPane to the main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout()); //!! added

        mainPanel.add(eScrollPane); //, BorderLayout.CENTER);

        //this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                //this.dispose();
                errText.setText("");
            }
        });

        getContentPane().add(mainPanel);
        
        // set the image for this window
        ArrayList<Image> imageList = guiInfo.getImageList();
        if (imageList !=null) {
            this.setIconImages(imageList);
        }
        this.setLocationRelativeTo(AppFrame);

        this.pack();
        this.setVisible(true);


    }

    /**
     * Display a message in the trace window
     * 
     * @param msg String to display 
     */
    public void println(String msg){
        errText.append(msg);
        errText.append("\n");
        this.revalidate();
        
    }

    /**
     * Display a string in the trace window
     * 
     * @param msg String to display 
     */
    public void print(String msg){
        errText.append(msg);
        this.repaint();
        
    }

	/**
	 * Moves the view of the window to the last line in the window
     * 
     */
	public void moveToBottom() {
		errText.setCaretPosition(errText.getDocument().getLength());
		this.setExtendedState(JFrame.ICONIFIED);
		this.setExtendedState(JFrame.NORMAL);
    	return;
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
		String v = WindowRpt.class.getName();

		// if it already exists, just return
		if (vlist.containsKey(v)) {
			return vlist;
		}
		//add it to the list
		vlist.put(v, getVersion());

    	vlist = GuiInfo.getAllVersions(vlist);

    	return vlist;
}


}