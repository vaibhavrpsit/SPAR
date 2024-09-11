/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/cidscreens/LineItemDataChangedListener.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:39 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/03/25 20:25:15  jdeleau
 *   @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *   See the scr for more info.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.device.cidscreens;

/**
 * Any class that wants to listen for changes to the ItemsModel
 * class (The line item data model) must implement this interface.
 * When the data model changes, this method will get called.
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public interface LineItemDataChangedListener
{
    /**
     * Inform any listeners that the data model has changed
     *  
     *  @param event Event containing specific information about the change
     */
    public void dataModelChanged(LineItemDataChangedEvent event);
}
