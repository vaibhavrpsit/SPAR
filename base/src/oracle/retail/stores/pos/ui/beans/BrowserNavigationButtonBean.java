/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BrowserNavigationButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:17 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:19:50 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:09:37 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ui.behavior.BrowserButtonListener;

//-------------------------------------------------------------------------
/**
   This class contains one constant that forces the button bar to be
   horizontal.
   @version $KW=@(#); $Ver=pos_4.5.0:59; $EKW;
*/
//-------------------------------------------------------------------------
public class BrowserNavigationButtonBean extends NavigationButtonBean
                                         implements BrowserButtonListener
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:59; $EKW;";
    /**
        Constant for the BACK action.
    **/
    public static final String BACK    = "Back";
    /**
        Constant for the FORWARD action.
    **/
    public static final String FORWARD = "Forward";
    /**
        Constant for the STOP action.
    **/
    public static final String STOP    = "Stop";
    /**
        Constant for the REFRESH action.
    **/
    public static final String REFRESH = "Refresh";
    /**
        Constant for the HOME action.
    **/
    public static final String HOME    = "Home";

    //------------------------------------------------------------------------------
    /**
    *   Default constructor.
    */
    //------------------------------------------------------------------------------
    public BrowserNavigationButtonBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
       Creates an empty NavigationButtonBean.
       @param actions two dimensional list of buttions
    */
    //--------------------------------------------------------------------------
    public BrowserNavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }

    //---------------------------------------------------------------------
    /**
        Adds (actually sets) the yesno listener on the Yes/No button.
        @Param listener the Browser Action Listener
    **/
    //---------------------------------------------------------------------
    public void addActionListener(ActionListener listener)
    {
        try
        {
            getUIAction(BACK).setActionListener(listener);
            getUIAction(FORWARD).setActionListener(listener);
            getUIAction(STOP).setActionListener(listener);
            getUIAction(REFRESH).setActionListener(listener);
            getUIAction(HOME).setActionListener(listener);
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.BrowserNavigationButtonBean.class);
            logger.warn( "BrowserNavigationButtonBean.addValidateActionListener() did not find the NEXT action.");
        }
    }

    //---------------------------------------------------------------------
    /**
        Removes (actually resets) the yes no listener on the Yes/No button.
        @Param listener the Browser Action Listener
    **/
    //---------------------------------------------------------------------
    public void removeActionListener(ActionListener listener)
    {
        try
        {
            getUIAction(BACK).resetActionListener();
            getUIAction(FORWARD).resetActionListener();
            getUIAction(STOP).resetActionListener();
            getUIAction(REFRESH).resetActionListener();
            getUIAction(HOME).resetActionListener();
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.BrowserNavigationButtonBean.class);
            logger.warn( "BrowserNavigationButtonBean.addValidateActionListener() did not find the NEXT action.");
        }
    }

    //---------------------------------------------------------------------
    /**
        Request focus on this object.
    **/
    //---------------------------------------------------------------------
    public void requestBrowserButtonFocus()
    {
        requestFocus();
    }
    //---------------------------------------------------------------------
    /**
        Enable a button on the button bar.
        @param commandName identifies the button.
        @param enable true if button should be enabled.
    **/
    //---------------------------------------------------------------------
    public void enableButton(String actionName, boolean enable)
    {
        try
        {
            getUIAction(actionName).setEnabled(enable);
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.BrowserNavigationButtonBean.class);
            logger.warn( "BrowserNavigationButtonBean.enableButton() did not find " + actionName + "");
        }
    }
}
