/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/GiftReceiptAssignedAisle.java /main/12 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:57 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse
 *
 *   Revision 1.10  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.9  2004/06/25 20:23:26  bwf
 *   @scr 0 Removed unused local variables.
 *
 *   Revision 1.8  2004/05/27 19:31:33  jdeleau
 *   @scr 2775 Remove unused imports as a result of tax engine rework
 *
 *   Revision 1.7  2004/05/27 17:12:48  mkp1
 *   @scr 2775 Checking in first revision of new tax engine.
 *
 *   Revision 1.6  2004/04/20 13:17:05  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/14 15:17:09  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/02/12 16:51:01  mcs
 *   Forcing head revision
 *
 *   Revision 1.3  2004/02/11 23:22:58  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Feb 05 2004 14:22:56   bwf
 * Do not allow gift receipts on damage discounts.
 * Resolution for 3765: Modify Item Feature
 *
 *    Rev 1.1   Jan 27 2004 15:24:52   bwf
 * Dont allow gift receipt on return item.
 * Resolution for 3765: Modify Item Feature
 *
 *    Rev 1.0   Aug 29 2003 16:01:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 20 2003 17:01:34   sfl
 * Implemented the new requirement: When an item is both a send item and gift receipt item, need to use local selling store's tax jurisdiction tax rules for tax calculation.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.0   Apr 29 2002 15:16:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:00   msg
 * Initial revision.
 *
 *    Rev 1.3   Dec 10 2001 17:23:36   blj
 * updated per codereview findings.
 * Resolution for POS SCR-237: Gift Receipt Feature
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.FinalLetter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class GiftReceiptAssignedAisle extends PosLaneActionAdapter
{

    public static final String LANENAME = "GiftReceiptAssignedAisle";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        SaleReturnLineItemIfc[] lineItems = null;
        lineItems = cargo.getItems();
        String itemID;
        StringBuilder sb = new StringBuilder();

        // Set the Gift Receipt Flag
        for (int i = 0; i < lineItems.length; i++)
        {
            // do not allow gift receipts for return items or damage discount items
            if(!lineItems[i].hasDamageDiscount() && lineItems[i].isReturnLineItem() != true)
            {
                lineItems[i].setGiftReceiptItem(true);
                itemID = lineItems[i].getItemID();
                Object dataArgs[]={itemID.trim()};
                String itemData=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM, dataArgs);

//
                sb.append(Util.EOL)
                  .append(itemData)
                  .append(Util.EOL)
                  .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_GIFT_RECEIPT_LABEL, dataArgs));
            }

        } // End of items loop
        cargo.setItems(lineItems);

        // journal it here
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        if (journal != null)
        {
            journal.journal(cargo.getCashier().getEmployeeID(),
                            cargo.getTransactionID(),
                            sb.toString());
        }
        else
        {
            logger.warn( "No journal manager found!");
        }
        // Done assigning gift receipt flag, mail a final letter.
        bus.mail(new FinalLetter("Next"), BusIfc.CURRENT);
    }
}
