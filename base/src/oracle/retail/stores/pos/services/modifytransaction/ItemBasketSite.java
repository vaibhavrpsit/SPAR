/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mchell 08/27/13 - Refactor item basket flow for tour guidelines
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    aariye 04/02/09 - For Serial Number check of multiple quantities in
 *                      basket
 *    aariye 03/25/09 - For the Uno button in serialized item check
 *    aariye 02/28/09 - For ejournal of ItemBasket
 *    vikini 02/03/09 - Incorporating Code review Comments
 *    aariye 02/02/09 - Added ItemBasketSite for ItemBasket feature
 *    aariye 01/28/09 - Adding element for Item Basket
 *    vikini 01/21/09 - ItemBasket site creation
 *    vikini 01/21/09 - ItemBasket site creation
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import java.util.Locale;

import oracle.retail.stores.pos.services.itembasket.BasketDTO;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * Site to process items in an item basket. This site iterate through items in a
 * basket and gets serial numbers and adds related items.
 */
public class ItemBasketSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = 1L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * This method drives to different sites based on parameters setting.
     * <P>
     * 
     * @param bus Service Bus
     */
    public void arrive(BusIfc bus)
    {
        String letter = CommonLetterIfc.SUCCESS;
        ModifyTransactionCargo cargo = (ModifyTransactionCargo) bus.getCargo();
        BasketDTO basket = cargo.getBasketDTO();
        int sizeOfBasket = basket.getTotalItemsInBasket();
        
        // increase the sequence number to point to the current item
        basket.incrementSeqNumber();

        // Check whether all items in the baskets are processed, if all items
        // are processed then update transaction to the cargo and go to sale
        // tour.
        if (basket.getSeqNumber() >= sizeOfBasket)
        {
            cargo.setTransaction(basket.getTransaction());
            cargo.setUpdateParentCargoFlag(true);

            JournalManagerIfc journal = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);

            Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

            StringBuffer entry = new StringBuffer();
            entry.append(Util.EOL);

            String findBasketString = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.FIND_BASKET_LABEL, null, journalLocale);
            Object[] dataArgs = new Object[1];

            dataArgs[0] = findBasketString;
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANS_LABEL,
                    dataArgs, journalLocale));

            entry.append(Util.EOL);

            dataArgs[0] = basket.getBasketId();
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BASKET_NUMBER_LABEL,
                    dataArgs, journalLocale));

            entry.append(Util.EOL);
            journal.journal(cargo.getRegister().getWorkstation().getStoreID(), cargo.getRegister().getWorkstation()
                    .getWorkstationID(), cargo.getOperator().getEmployeeID(), null, null, entry.toString());

        }
        else
        {
            journalBasketItems(bus, basket.getsaleReturnSpecifiedItem(), basket.getTransaction(), cargo);            
            SaleReturnLineItemIfc saleReturnItem = basket.getsaleReturnSpecifiedItem();
            
            if (saleReturnItem.isSerializedItem() || saleReturnItem.isKitHeader())
            {
                letter = "GetSerialNumbers";
            }
            else
            {
                letter = "RelatedItems";
            }            
        }

        bus.mail(letter, BusIfc.CURRENT);
    }

    public void journalBasketItems(BusIfc bus, SaleReturnLineItemIfc item, SaleReturnTransactionIfc transaction,
            ModifyTransactionCargo cargo)
    {
        JournalManagerIfc journal = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc) Gateway.getDispatcher().getManager(
                JournalFormatterManagerIfc.TYPE);

        if (journal != null)
        {
            if (!item.isKitComponent()) // KitComponentLineItems are journaled
                                        // by their parent header
            {
                StringBuffer sb = new StringBuffer();
                EYSDate dob = transaction.getAgeRestrictedDOB();
                String itemID = null;
                if (item.getRelatedItemSequenceNumber() > -1)
                {
                    itemID = transaction.getLineItems()[item.getRelatedItemSequenceNumber()].getItemID();
                }
                sb.append(formatter.toJournalString(item, dob, itemID));

                if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE) // add
                                                                                            // status
                {
                    String transactionSaleStatus = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.TRANSACTION_SALE_STATUS, null);
                    sb.append(Util.EOL).append(transactionSaleStatus);
                }

                journal.journal(cargo.getOperator().getLoginID(), transaction.getTransactionID(), sb.toString());
            }
        }
        else
        {
            logger.error("No JournalManager found");
        }

    }

}
