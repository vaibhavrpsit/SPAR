/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemInquiryGlobalButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         7/28/2006 5:33:46 PM   Brett J. Larsen 
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


import org.apache.log4j.Logger;
import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;


//---------------------------------------------------------------------
/**
   This class contain global navigation functionality for ItemInquiry screen
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//---------------------------------------------------------------------
public class ItemInquiryGlobalButtonBean extends GlobalNavigationButtonBean
                                         implements GlobalButtonListener
{


    /**
        Finds the button using the action name and sets the enable state.
        @param actionName the action name of the button.
        @param enable true if enabled.
     */

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
