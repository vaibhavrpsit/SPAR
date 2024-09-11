/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SaleGlobalButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:41 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;



import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;

//-------------------------------------------------------------------------
/**
   This class contains one constant that forces the button bar to be
   horizontal.
   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
*/
//-------------------------------------------------------------------------
public class SaleGlobalButtonBean extends GlobalNavigationButtonBean 
                                  implements GlobalButtonListener
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    //---------------------------------------------------------------------
    /**
        Finds the button using the action name and sets the enable state.
        @param actionName the action name of the button.
        @param enable true if enabled.
     */
    //---------------------------------------------------------------------
    public void enableButton(String actionName, boolean enable)
    {
        try
        {
            getUIAction(actionName).setEnabled(enable); 
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.SaleGlobalButtonBean.class);
            logger.warn( "SaleLocalButtonBean.enbleButton() did not find the " + actionName + " action.");
        }
    }
}
