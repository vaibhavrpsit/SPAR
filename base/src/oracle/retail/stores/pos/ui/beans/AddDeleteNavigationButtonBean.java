/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AddDeleteNavigationButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:39 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:27:09 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:19:31 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:09:24 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ui.behavior.AddDeleteListener;

//-------------------------------------------------------------------------
/**
   This class contains one constant that forces the button bar to be
   horizontal.
   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
*/
//-------------------------------------------------------------------------
public class AddDeleteNavigationButtonBean extends NavigationButtonBean
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /**
        Constant for the YESNO action.
    **/
    public static final String ADD  = "Add";
    public static final String DELETE = "Delete";

    //------------------------------------------------------------------------------
    /**
    *   Default constructor.
    */
    //------------------------------------------------------------------------------
    public AddDeleteNavigationButtonBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
       Creates an empty NavigationButtonBean.
       @param actions two dimensional list of buttions
    */
    //--------------------------------------------------------------------------
    public AddDeleteNavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }

    //---------------------------------------------------------------------
    /**
        Adds (actually sets) the yesno listener on the Yes/No button.
        @Param listener the YesNo Action Listener
    **/
    //---------------------------------------------------------------------
    public void addAddDeleteListener(AddDeleteListener listener)
    {
        try
        {
            getUIAction(ADD).setActionListener(listener);
            getUIAction(DELETE).setActionListener(listener);
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.AddDeleteNavigationButtonBean.class);
            logger.warn( "AddDeleteNavigationButtonBean.addValidateActionListener() did not find the  action.");
        }
    }

    //---------------------------------------------------------------------
    /**
        Removes (actually resets) the yes no listener on the Yes/No button.
        @Param listener the yesno Action Listener
    **/
    //---------------------------------------------------------------------
    public void removeAddDeleteListener(AddDeleteListener listener)
    {
        try
        {
            getUIAction(ADD).resetActionListener();
            getUIAction(DELETE).resetActionListener();
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.AddDeleteNavigationButtonBean.class);
            logger.warn( "AddDeleteNavigationButtonBean.addValidateActionListener() did not find the  action.");
        }
    }

}
