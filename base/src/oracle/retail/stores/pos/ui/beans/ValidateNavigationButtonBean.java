/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ValidateNavigationButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:42 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Foundation imports

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ui.behavior.ValidateActionListener;

//-------------------------------------------------------------------------
/**
    This class is intended for use when a local navigation bean needs
    to validate fields in the work panel.
   @version $KW=@(#); $Ver=pos_4.5.0:51; $EKW;
*/
//-------------------------------------------------------------------------
public class ValidateNavigationButtonBean extends NavigationButtonBean
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:51; $EKW;";
    /**
        Letter to send if the fields validate.
        Note that the accessors are implemented so this attribute can be
        set via <BEANPROPERTY/> in the xml.
    **/
    protected String actionName = "Approved";

    //------------------------------------------------------------------------------
    /**
    *   Default constructor.
    */
    //------------------------------------------------------------------------------
/*
    public ValidateNavigationButtonBean()
    {
        super();
    }
*/
    //--------------------------------------------------------------------------
    /**
       Creates an empty NavigationButtonBean.
       @param actions two dimensional list of buttions
    */
    //--------------------------------------------------------------------------
/*
    public YesNoNavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }
*/

    //---------------------------------------------------------------------
    /**
        Sets the actionName.
        @param name String
    **/
    //---------------------------------------------------------------------
    public void setActionName(String name)
    {
        actionName = name;
    }

    //---------------------------------------------------------------------
    /**
        Gets the actionName.
        @return String
    **/
    //---------------------------------------------------------------------
    public String getActionName()
    {
        return actionName;
    }

    //---------------------------------------------------------------------
    /**
        Adds (actually sets) the validation listener on the button.
        @Param listener the Validate Action Listener
    **/
    //---------------------------------------------------------------------
    public void addValidateActionListener(ValidateActionListener listener)
    {
        try
        {
            //Allow multiple action name listeners
            StringTokenizer actionList = new StringTokenizer(actionName,",",false);
            while(actionList.hasMoreTokens())
            {
               getUIAction(actionList.nextToken()).setActionListener(listener);
            }
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.ValidateNavigationButtonBean.class);
            logger.warn(
                     "GlobalNavigationButtonBean.addValidateActionListener() did not find the " + actionName + " action.");
        }
    }

    //---------------------------------------------------------------------
    /**
        Removes (actually resets) the validation listener on the button.
        @Param listener the Validate Action Listener
    **/
    //---------------------------------------------------------------------
    public void removeValidateActionListener(ValidateActionListener listener)
    {
        try
        {
            StringTokenizer actionList = new StringTokenizer(actionName,",",false);
            while(actionList.hasMoreTokens())
            {
               getUIAction(actionList.nextToken()).resetActionListener();
            }
            
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.ValidateNavigationButtonBean.class);
            logger.warn(
                     "GlobalNavigationButtonBean.addValidateActionListener() did not find the " + actionName + " action.");
        }
    }
}
