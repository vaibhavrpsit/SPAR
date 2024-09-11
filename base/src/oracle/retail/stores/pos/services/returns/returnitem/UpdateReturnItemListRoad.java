/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/UpdateReturnItemListRoad.java /main/20 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    icole     03/30/12 - Forward port of spurkaya_bug-13403991. The original
 *                         fix was in SaleReturnLineItem but the root cause is
 *                         here.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    jswan     08/17/11 - Modified to prevent the return of Gift Cards as
 *                         items and part of a transaction. Also cleaned up
 *                         references to gift cards objects in the return
 *                         tours.
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    sgu       09/14/10 - increment current index
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    jswan     12/14/09 - Modifications for 'Min return price for X days'
 *                         feature.
 *    asinton   05/05/09 - Refactored so that
 *                         UtilityManager.getInitialTransactionTax is only
 *                         called once.
 *    asinton   05/04/09 - Use default tax rate from
 *                         UtilityManager.getInitialTransactionTax.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         11/15/2007 11:18:26 AM Christian Greene use
 *         non-deprecated getSellingPrice
 *    4    360Commerce 1.3         1/22/2006 11:45:19 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:36 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse
 *
 *   Revision 1.18  2004/07/30 14:52:31  jdeleau
 *   @scr 6530 Update quantity for return without receipt on
 *   an unknown item.
 *
 *   Revision 1.17  2004/07/21 00:01:47  jdeleau
 *   @scr 6377 For ItemNotFound returns apply the default tax rate.
 *
 *   Revision 1.16  2004/07/19 19:55:59  mweis
 *   @scr 5387 Return an unknown item with a serial number forces all other return items to have its serial number.
 *
 *   Revision 1.15  2004/06/23 20:03:50  mweis
 *   @scr 5385 Return of UnknownItem with serial and size blows up app
 *
 *   Revision 1.14  2004/03/26 05:39:05  baa
 *   @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
 *
 *   Revision 1.13  2004/03/24 21:33:07  epd
 *   @scr 3561 gets the size from the search criteria for manually entered items
 *
 *   Revision 1.12  2004/03/23 18:42:21  baa
 *   @scr 3561 fix gifcard return bugs
 *
 *   Revision 1.11  2004/03/22 22:39:46  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.10  2004/03/19 23:04:11  epd
 *   @scr 3561 Updated comments and strategy for initializing SaleReturnLineItems
 *
 *   Revision 1.9  2004/03/15 15:16:51  baa
 *   @scr 3561 refactor/clean item size code, search by tender changes
 *
 *   Revision 1.8  2004/03/12 19:36:48  epd
 *   @scr 3561 Updates for handling kit items in non-retrieved no receipt returns
 *
 *   Revision 1.7  2004/03/05 22:44:05  aarvesen
 *   @scr 3561 removed an extraneous call to setItemQuantity
 *
 *   Revision 1.6  2004/03/02 18:49:54  baa
 *   @scr 3561 Returns add size info to journal and receipt
 *
 *   Revision 1.5  2004/02/26 21:54:41  epd
 *   @scr 3561 Removed some code that seemed to be doing nothing.  I hope I'm correct
 *
 *   Revision 1.4  2004/02/16 13:37:14  baa
 *   @scr  3561 returns enhancements
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Feb 09 2004 10:37:00   baa
 * return - item not found
 *
 *    Rev 1.0   05 Feb 2004 23:24:12   baa
 * Initial revision.
 *
 *    Rev 1.0   Aug 29 2003 16:06:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:05:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:46:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 25 2002 15:06:16   blj
 * remove comments from this file.
 * Resolution for POS SCR-923: Started return by gift receipt.  Esc to do the return by item-got gift rec item screen
 *
 *    Rev 1.0   Feb 25 2002 14:56:30   blj
 * Initial revision.
 *
 *    Rev 1.2   Feb 05 2002 16:43:20   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.1   Dec 10 2001 17:23:40   blj
 * updated per codereview findings.
 * Resolution for POS SCR-237: Gift Receipt Feature
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 *
 */
public class UpdateReturnItemListRoad extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -1997186537962233207L;

    /**
     * This road set gift receipt flag to false.
     *
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();
        UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        setupReturnItem(cargo, util, pm);

        // get the default tax rate from the utility manager and set on the return item
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
        TransactionTaxIfc transactionTax = utility.getInitialTransactionTax();
        double defaultTaxRate = transactionTax.getDefaultRate();
        cargo.getReturnItem().setTaxRate(defaultTaxRate);

        // create and initialize a transaction
        // IMPORTANT: this transaction is temporary.  DO NOT INITIALIZE with a new
        // transaction ID.  We intentionally do NOT do that.
        // We only are using this temp txn to access business logic that initializes
        // a SaleReturnLineItem
        SaleReturnTransactionIfc tempTxn = DomainGateway.getFactory().getSaleReturnTransactionInstance();
        tempTxn.setTransactionTax(transactionTax);
        SaleReturnLineItemIfc item = tempTxn.addReturnItem(cargo.getPLUItem(),cargo.getReturnItem(), BigDecimalConstants.ONE_AMOUNT);

        if (cargo.getPLUItem().isItemSizeRequired() && Util.isEmpty(item.getItemSizeCode()))
        {
           item.setItemSizeCode(cargo.getSearchCriteria().getItemSizeCode());
           // reset size info in cargo
           cargo.getSearchCriteria().setItemSizeCode(null);
        }

        if(cargo.getPLUItem() instanceof UnknownItemIfc)
        {
            item.setItemQuantity(cargo.getUnknownItemQuantity());
        }

        // Handle the serial number, if any.
        String serial = cargo.getItemSerial();
        if (serial != null && item != null)
        {
            item.setItemSerial(serial);  // move the value to the item
            cargo.setItemSerial(null);   // reset the cargo
        }

        cargo.addReturnSaleLineItem(item);
        //  Set gift receipt flag back to false

        cargo.setPLUItem(null);
        cargo.setCurrentItem(cargo.getCurrentItem()+1);
    }

    /**
     * @param cargo
     * @param utility
     * @param pm
     */
    protected void setupReturnItem(ReturnItemCargo cargo, UtilityManagerIfc utility, ParameterManagerIfc pm )
    {
        // Return item
        ReturnItemIfc itemReturn = DomainGateway.getFactory().getReturnItemInstance();

        // get the pluItem from the cargo
        PLUItemIfc pluItem = cargo.getPLUItem();

        // at this point, you don't have the quantities set up
        // so call addQuantity to build up the array
        if (! pluItem.isKitHeader())
        { //UI is not displayed for Kit Header items
            int pricingGroupID = -1;
            if (cargo.getTransaction() != null &&
                cargo.getTransaction().getCustomer() != null &&
                cargo.getTransaction().getCustomer().getPricingGroupID() != null)
            {
                pricingGroupID = cargo.getTransaction().getCustomer().getPricingGroupID();
            }
            itemReturn.setPrice(pluItem.getReturnPrice(pricingGroupID));
            itemReturn.setItemQuantity(BigDecimalConstants.ONE_AMOUNT);

            // set the scanned method
            if (cargo.isItemScanned())
            {
                itemReturn.setEntryMethod(EntryMethod.Scan);
            }
            else
            {
                itemReturn.setEntryMethod(EntryMethod.Manual);
            }
        } // end getRevisionNumber()

        // set the size if entered
        if (cargo.getSearchCriteria() != null &&
            cargo.getSearchCriteria().getItemSizeCode() != null)
        {
            ItemSizeIfc size = DomainGateway.getFactory().getItemSizeInstance();
            size.setSizeCode(cargo.getSearchCriteria().getItemSizeCode());
            itemReturn.setItemSize(size);
        }
        else if (cargo.getItemSizeCode() != null)
        {
            // Provided for an UnknownItem
            ItemSizeIfc size = DomainGateway.getFactory().getItemSizeInstance();
            size.setSizeCode(cargo.getItemSizeCode());
            itemReturn.setItemSize(size);
        }

        // set the itemReturn object in the cargo
        cargo.addReturnItem(itemReturn);
        // Ensure cargo.currentItem is set to last entry.
        if (cargo.getReturnItems() != null)
        {
            cargo.setCurrentItem(cargo.getReturnItems().length -1);
        }
    }
}
