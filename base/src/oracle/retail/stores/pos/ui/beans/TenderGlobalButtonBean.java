/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TenderGlobalButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:50 mszekely Exp $
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
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:53 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Oct 21 2003 10:25:42   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;

/**
 *  
 */
public class TenderGlobalButtonBean extends GlobalNavigationButtonBean implements GlobalButtonListener
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.behavior.EnableButtonListener#enableButton(java.lang.String, boolean)
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
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.TenderGlobalButtonBean.class);
            logger.warn( "TenderGlobalButtonBean.enbleButton() did not find the " + actionName + " action.");
        }
    }
}
