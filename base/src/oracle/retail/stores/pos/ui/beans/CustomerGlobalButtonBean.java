/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerGlobalButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
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

import javax.swing.event.DocumentEvent;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;

//-------------------------------------------------------------------------
/**
   This class is used to control the global naviation panel for the customer
   info screen after login.
*/
//-------------------------------------------------------------------------
public class CustomerGlobalButtonBean extends GlobalNavigationButtonBean
{
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
        Determines if the response field has text and sets the "Next"
        and "Clear" buttons appropriately.
        @Param evt the cocument event
    **/
    //---------------------------------------------------------------------
    public void checkAndEnableButtons(DocumentEvent evt)
    {
        try
        {
            int len=evt.getDocument().getLength();

            if(len > 0)
            {
                //will need to check and see if they are already enabled
                //and if we can ignore next
                getUIAction(CLEAR).setEnabled(true);
                if ( len >= minLength)
                {
                    if (manageNextButton)
                    {
                        getUIAction(NEXT).setEnabled(true);
                    }
                }
                else
                {
                    if (manageNextButton)
                    {
                        getUIAction(NEXT).setEnabled(false);
                    }
                }
            }
            else
            {
                //will need to check and see if they are already disabled
                //and if we can ignore next
                getUIAction(CLEAR).setEnabled(false);
                if (manageNextButton)
                {
                    getUIAction(NEXT).setEnabled(true);
                }
            }
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.CustomerGlobalButtonBean.class);
            logger.warn( "CustomerGlobalButtonBean.checkAndEnableButtons() did not find the NEXT or CLEAR action.");
        }
    }
}
