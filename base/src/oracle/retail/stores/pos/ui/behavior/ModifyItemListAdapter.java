/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/ModifyItemListAdapter.java /main/12 2014/05/15 12:55:00 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/15/14 - XbranchMerge cgreene_bug-18691883 from
 *                         rgbustores_14.0x_generic_branch
 *    cgreene   05/15/14 - remove use of modifyitemlistadapter
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:52:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:47:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:32   msg
 * Initial revision.
 * 
 *    Rev 1.2   Jan 19 2002 10:32:54   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.1   13 Nov 2001 14:50:26   sfl
 * Enable the Item button when multiple items have been
 * selected from the item list in Sell Item screen.
 * Resolution for POS SCR-282: Multiple Item Selection
 * 
 *    Rev 1.0   Sep 21 2001 11:33:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:18:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import javax.swing.JList;

import oracle.retail.stores.pos.ui.beans.EYSList;

/**
 * Toggles the Item button on the sale screen based on the selection in the sale
 * list. To use this adapter, wire it into your uicfg.xml like this:
 * <pre>
        <CONNECTION
            sourceBeanSpecName="SellItemWorkPanelSpec"
            targetBeanSpecName="SellItemButtonSpec"
            listenerPackage="javax.swing.event"
            listenerInterfaceName="ListSelectionListener"
            adapterPackage="oracle.retail.stores.pos.ui.behavior"
            adapterClassName="ModifyItemListAdapter"/>
 * </pre>
 * <p>
 * Note, this adapter never actually turns the "Item" button off, which is okay
 * because the Operator needs to be able to get to "Inquiry" and "Services".
 * Therefore, this adapter is pretty pointless to use.
 * 
 * @version $Revision: /main/12 $
 * @deprecated as of 14.0.1.1. No uses.
 */
public class ModifyItemListAdapter extends AbstractListAdapter
{
    /** revision number of this class  **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    /** Constants for item action.  */
    private static final String INVALID_SELECTION = "Item[false]";
    private static final String VALID_SELECTION   = "Item[true]";

    /** static index value indicating no selected row  **/
    private static final int NO_SELECTION = -1;

    /**
     * Builds a button state string based on the selections in the list.
     * 
     * @param list the JList that triggered the event
     */
    public String determineButtonState(JList list)
    {
        EYSList multiList = (EYSList)list;

        // default assumption is to enable the Item button
        String result = VALID_SELECTION;

        int listLength = multiList.getSelectedIndices().length;
        int currentItem = multiList.getSelectedRow();

        // if one item selected and cursor on that item,
        // or no items are selected, enable the Item button
        if ((listLength == 0 && currentItem != NO_SELECTION) ||
            (listLength == 0 && currentItem == NO_SELECTION) ||
            (listLength == 1 && multiList.isSelectedIndex(currentItem)))
        {
            result = VALID_SELECTION;
        }
        return result;
    }
}
