/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/sellorderitem/PrintItemRoad.java /main/5 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    icole     03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                         have more generic code, rather than heavily Pincomm.
 *    blarsen   06/14/11 - Adding storeID to scrolling receipt request.
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.sellorderitem;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

public class PrintItemRoad extends PosLaneActionAdapter
{
    /**
     * generated serial version UID
     */
    private static final long serialVersionUID = -3061171338950907519L;

    /**
     * Journals the added line item information and makes the call to display
     * the item info on the pole display device.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        SellOrderItemCargo          cargo       = (SellOrderItemCargo)bus.getCargo();
        SaleReturnLineItemIfc       srli        = cargo.getLineItem();
        SaleReturnTransactionIfc    transaction = cargo.getTransaction();

        JournalManagerIfc journal =
            (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        if (journal != null)
        {
            // add journal entry for an unknown item
            if (srli.getPLUItem() instanceof UnknownItemIfc)
            {
                StringBuffer sb = new StringBuffer();
                sb.append(formatter.toJournalString(srli, null, null));

                Object dataArgs[]={srli.getPLUItem().getDepartmentID()};
                String deptNumber = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DEPARTMENT_NUMBER, dataArgs);

                String itemNotFound =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_NOT_FOUND, dataArgs);

                sb.append(Util.EOL).append(deptNumber)
                .append(Util.EOL).append(itemNotFound).append(Util.EOL);

                journal.journal(cargo.getSalesAssociate().getLoginID(),
                        transaction.getTransactionID(),
                        sb.toString());
            }
            else
            {
                // add journal entry for an known plu item
                if (!srli.isKitComponent()) //KitComponentLineItems are journaled by their parent header
                {
                    StringBuffer sb = new StringBuffer();
                    EYSDate dob = transaction.getAgeRestrictedDOB();
                    String itemID = null;
                    if(srli.getRelatedItemSequenceNumber() > -1)
                    {
                        itemID = transaction.getLineItems()[srli.getRelatedItemSequenceNumber()].getItemID();
                    }
                    sb.append(formatter.toJournalString(srli, dob, itemID));

                    journal.journal(cargo.getOperator().getLoginID(),
                            transaction.getTransactionID(),
                            sb.toString());
                }
            }
        }
        else
        {
            logger.error("No JournalManager found");
        }

        //Show item on Line Display device
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
        try
        {
            pda.lineDisplayItem(srli);
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display.",e);
        }
        WorkstationIfc workstation = cargo.getRegister().getWorkstation();
        PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
        paymentManager.addItem(workstation, srli, transaction);

    }//end traverse
}
