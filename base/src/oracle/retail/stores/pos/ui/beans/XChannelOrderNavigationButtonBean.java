/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/XChannelOrderNavigationButtonBean.java /main/14 2013/01/10 14:03:57 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/22/12 - remove order filled status
 *    sgu       05/21/12 - remove order item pending status
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionListener;

import org.apache.log4j.Logger;


//-------------------------------------------------------------------------
/**
   This class contains one constant that forces the button bar to be
   horizontal.
   @version $KW=@(#); $Ver=pos_4.5.0:5; $EKW;
*/
//-------------------------------------------------------------------------
public class XChannelOrderNavigationButtonBean extends NavigationButtonBean
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.x:2; $EKW;";
    /**
        Constants for button names.
    **/
    public static final String PICKUP   = "Pick Up";
    public static final String CANCELED = "Canceled";

    //------------------------------------------------------------------------------
    /**
    *   Default constructor.
    */
    //------------------------------------------------------------------------------
    public XChannelOrderNavigationButtonBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
       Creates an empty NavigationButtonBean.
       @param actions two dimensional list of buttions
    */
    //--------------------------------------------------------------------------
    public XChannelOrderNavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }

    //---------------------------------------------------------------------
    /**
        Adds (actually sets) the listener on the Next button.
        @Param listener the Action Listener
    **/
    //---------------------------------------------------------------------
    public void addActionListener(ActionListener listener)
    {
        try
        {
            getUIAction(PICKUP).setActionListener(listener);
            getUIAction(CANCELED).setActionListener(listener);
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.XChannelOrderNavigationButtonBean.class);
            logger.warn( "OrderNavigationButtonBean.addActionListener() did not find the NEXT action.");
        }
    }

    //---------------------------------------------------------------------
    /**
        Removes (actually resets) the listener on the Next button.
        @Param listener the Action Listener
    **/
    //---------------------------------------------------------------------
    public void removeActionListener(ActionListener listener)
    {
        try
        {
            getUIAction(PICKUP).resetActionListener();
            getUIAction(CANCELED).resetActionListener();

        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.XChannelOrderNavigationButtonBean.class);
            logger.warn( "OrderNavigationButtonBean.removeActionListener() did not find the NEXT action.");
        }
    }
}
