/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DisplayHelpBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:46 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/12/2006 5:25:35 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/21/2006 9:56:27 PM   Kulbhushan Sharma
 *         Some code refactoring
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 10 2003 15:41:58   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:10:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.6   Jun 20 2003 17:10:58   baa
 * fix problems with help screens
 * 
 *    Rev 1.5   Jun 19 2003 18:45:22   baa
 * move help files to locale bundle
 * 
 *    Rev 1.4   Apr 11 2003 17:28:26   baa
 * partial fix for moving  help files
 * Resolution for POS SCR-2152: Organize locale sensitive Files
 * 
 *    Rev 1.3   Mar 06 2003 10:43:52   RSachdeva
 * clean up
 * Resolution for POS SCR-1848: Help Support
 * 
 *    Rev 1.2   Dec 10 2002 13:30:36   RSachdeva
 * JavaHelp Implementation and Internationalization Support
 * Resolution for POS SCR-1848: Help Support
 * 
 *    Rev 1.1   Aug 14 2002 18:17:22   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:49:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:14   msg
 * Initial revision.
 * 
 *    Rev 1.4   14 Feb 2002 17:56:48   jbp
 * sizing for linux in defaultuicfg
 * Resolution for POS SCR-1214: Help screen on Beetle does not fill up entire screen
 *
 *    Rev 1.3   05 Feb 2002 09:39:38   jbp
 * changes for new ui
 * Resolution for POS SCR-833: Selecting Help followed by a dialog screen causes the application to hang/crash
 *
 *    Rev 1.2   Jan 19 2002 10:29:56   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   12 Oct 2001 11:56:18   jbp
 * Initial revision.
 * Resolution for POS SCR-211: HTML Help Functionality
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Label;
import java.net.MalformedURLException;
import java.net.URL;

import javax.help.DefaultHelpModel;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelp;
import javax.help.JHelpTOCNavigator;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
    This class displays the journal display screen.
    It is used with the DisplayTextBeanModel class. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see oracle.retail.stores.pos.ui.beans.DisplayTextBeanModel
**/
//----------------------------------------------------------------------------
public class DisplayHelpBean  extends BaseBeanAdapter
{
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** the bean name **/
    protected String beanName = "DisplayHelpBean";

    /** The bean model **/
    protected DisplayHtmlBeanModel beanModel = new DisplayHtmlBeanModel();

    /** 
       The main panel 
       @deprecated Deprecated in 6.0. Replaced by the new JavaHelp Implementation 
                   for the Help System.
    **/
    //protected HtmlPane displayHtmlPane = null;

    /**
       The area to display the text - transaction 
       @deprecated Deprecated in 6.0. Replaced by the new JavaHelp Implementation 
                   for the Help System.
    **/
    protected JTextArea displayTextArea = null;  

    /** 
       The scroll listener. 
       @deprecated Deprecated in 6.0. Replaced by the new JavaHelp Implementation 
                   for the Help System.
    **/
    //protected ScrollListener listener = new ScrollListener();
    
    /** 
        The Help Viewer used to create Table Of Contents and Display Html Help Topics
    **/
    JHelp helpViewer = null;

    /**
        The Table Of Contents Navigator.
    **/
    JHelpTOCNavigator jToc = null;
    
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.DisplayHelpBean.class);

    //---------------------------------------------------------------------
    /**
       Default class Constructor and initializes its components.
     **/
    //---------------------------------------------------------------------
    public DisplayHelpBean()
    {
        super();
        UI_PREFIX = "DisplayBean";
    }
    //---------------------------------------------------------------------
    /**
       Initialize the class and its screen members.
     **/
    //---------------------------------------------------------------------
    public void configure()
    {
        // Intialize the panel
        setName(beanName);
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new BorderLayout());
        
        helpViewer = new JHelp();
        add(helpViewer,"Center");
    }
    //---------------------------------------------------------------------
    /**
       Gets the POSBeanModel associated with this bean.
       @return the POSBaseBeanModel associated with this bean
   **/
    //---------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }
    //---------------------------------------------------------------------
    /**
       Activate this screen and listeners.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
    }
    //---------------------------------------------------------------------
    /**
       Deactivate this screen and listeners.
    **/
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
    }

    //---------------------------------------------------------------------
    /**
       Deactivate key listeners
       @deprecated Deprecated in 6.0. Replaced by the new JavaHelp Implementation 
                   for the Help System.
    **/
    //---------------------------------------------------------------------
   /**
	protected void deactivateListeners()
    {
       displayHtmlPane.resetKeyboardActions();
    }
	*/
    //---------------------------------------------------------------------
    /**
       Listener class that handles the keystrokes for scrolling
       @deprecated Deprecated in 6.0. Replaced by the new JavaHelp Implementation 
                   for the Help System.
    **/ 
    //---------------------------------------------------------------------
    /**
	protected class ScrollListener implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            JScrollBar bar = displayHtmlPane.getVerticalScrollBar();
            if (e.getActionCommand().equals("DOWN"))
            {
                bar.setValue(bar.getValue() +
                             bar.getUnitIncrement(-1));
            }
            else if (e.getActionCommand().equals("UP"))
            {
                bar.setValue(bar.getValue() -
                             bar.getUnitIncrement(1));
            }
            else if (e.getActionCommand().equals("PAGE_DOWN"))
            {
                bar.setValue(bar.getValue() +
                             bar.getBlockIncrement(-1));
            }
            else if (e.getActionCommand().equals("PAGE_UP"))
            {
                bar.setValue(bar.getValue() -
                             bar.getBlockIncrement(1));
            }
            else if (e.getActionCommand().equals("END"))
            {
                bar.setValue(bar.getMaximum());
            }
            else if (e.getActionCommand().equals("HOME"))
            {
                bar.setValue(bar.getMinimum());
            }
        }
    }
	*/
    //---------------------------------------------------------------------
    /**
       Initialize the DisplayTextBeanBorderLayout.
       @deprecated Deprecated in 6.0. Replaced by the new JavaHelp Implementation 
                   for the Help System.
    **/
    //---------------------------------------------------------------------
    protected void initDisplayHtmlBeanBorderLayout()
    {

    }
    //---------------------------------------------------------------------
    /**  
       Initialize the initDisplayTextArea.
       @deprecated Deprecated in 6.0. Replaced by the new JavaHelp Implementation 
                   for the Help System.
    **/
    //---------------------------------------------------------------------
    public void initDisplayTextArea()
    {
        displayTextArea = new JTextArea(" ");
        displayTextArea.setEditable(false);
        displayTextArea.setLineWrap(true);
        displayTextArea.setWrapStyleWord(true);
        displayTextArea.setFont(new Font("Monospaced",Font.PLAIN,12));
        displayTextArea.setEnabled(true);
    }
    
    //---------------------------------------------------------------------
    /**
       Initialize the initDisplayTextPane.
       @deprecated Deprecated in 6.0. Replaced by the new JavaHelp Implementation 
                   for the Help System.
    **/
    //---------------------------------------------------------------------
    /**
	protected void initDisplayHtmlPane()
    {
    }
	*/
    
    
    //---------------------------------------------------------------------
    /**
       Set the text to be displayed
       <P>
       @return none
       @deprecated Deprecated in 6.0. Replaced by the new JavaHelp Implementation 
                   for the Help System.
    **/
    //---------------------------------------------------------------------
    public void setDisplayText(String value)
    {
        if (value == null)
            value = new String(" ");

        displayTextArea.setText(value);
    }
    //---------------------------------------------------------------------
    /**
       Set the URL to be displayed as per the User Interface Locale.
       Sets the HelpSet that calls individual Help Topics and activates 
       the Active Screen in the Table of contents of the JavaHelp Help Viewer.
       <P>
       @return none
     **/
    //---------------------------------------------------------------------
    public void setDisplayURL(URL value)
    {
        boolean isURLPresent = true;
        if (value == null)
        {
            // display not found error
            logger.error( "URL is null");
            removeAll();
            add (new Label(retrieveText("helpNotFound")),"Center");
            isURLPresent = false;
        }
        else
        {
    
            HelpSet hs = null;
            try
            {
                hs = new HelpSet(null, value);  
            } 
            catch(HelpSetException hse)
            {
                if (logger.isInfoEnabled()) logger.info(
                            "Help Directory/HelpSet File for User Interface Locale doesn't exist or there is a Parsing problem.Hence using Default Locale");
                try
                {
                    String str = value.toString();
                    hs = new HelpSet(null, new URL(str));  
                }
                catch(HelpSetException enEx)
                {
                    logger.error(
                                 "Help Directory/HelpSet File for even Default Locale doesn't exist or there is a Parsing problem");
                }
                catch(MalformedURLException e)
                {
                    logger.error( 
                                 "Bad URL received");
                }
            }
            try
            {
                //Sets the HelpModel that provides the data. 
                helpViewer.setModel(new DefaultHelpModel(hs));
                if (isURLPresent)
                {
                    helpViewer.setCurrentID(javax.help.Map.ID.create(beanModel.getScreenName(),hs));
                }
                //Adding for Making Print and Page Setup Buttons Invisible from JToolBar of JHelp. 
                //This is for JavaHelp 1.1.3. 
                //The JavaHelp 2.0 is coming up with XML Customization and ToolBar methods.
                for(int i = 0;i < helpViewer.getComponentCount();i++)
                {
                    Component c = helpViewer.getComponent(i);     
                    if(c == null || !(c instanceof JToolBar)) 
                        continue; 
                    JToolBar toolBar = (JToolBar) c;
                    JButton button = (JButton) toolBar.getComponentAtIndex(3);
                    button.setVisible(false);
                    button = (JButton) toolBar.getComponentAtIndex(4);
                    button.setVisible(false);
                }
                jToc = ((JHelpTOCNavigator)helpViewer.getCurrentNavigator());
                jToc.setRequestFocusEnabled(true);
                jToc.requestFocus(); 
            }
            catch(InvalidHelpSetContextException invalid)
            {
                logger.error(
                             "ID not present in Table of contents/ Map file");
            }
        }
    }
    //---------------------------------------------------------------------
    /**
       Sets the current model to the updated model..
       @param UIModelIfc A DisplayTextBeanModel
       @see oracle.retail.stores.pos.ui.beans.DisplayTextBeanModel
    **/
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set DisplayTextBean model to null.");
        }
        else
        if(model instanceof DisplayHtmlBeanModel)
        {
            beanModel = (DisplayHtmlBeanModel)model;
            updateBean();
        }
    }
    //---------------------------------------------------------------------
    /**
       Update the bean with fresh data
    **/
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        setDisplayURL(beanModel.getDisplayURL());
    }

    //---------------------------------------------------------------------
    /**
       Set the focus for the screen.
    **/
    //---------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
    }
    //---------------------------------------------------------------------
    /**
       Returns default display string.
       <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: DisplayTextBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }
    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
