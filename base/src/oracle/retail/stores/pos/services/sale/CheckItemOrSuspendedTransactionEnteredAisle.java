/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CheckItemOrSuspendedTransactionEnteredAisle.java /main/3 2013/10/08 09:44:50 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     10/04/13 - Forward port of fix for a scanned item number
 *                         getting truncated if it's a UPC that begins with 1
 *                         or 4 and for any barcode that is of length of 10
 *                         that begins with 1 or 4. This is code that was
 *                         specific to one client (GAP).
 *    rgour     12/10/12 - Enhancement in suspended transaction phase
 *    rgour     11/02/12 - Enhancements in Suspended Transactions
 *
 * ===========================================================================
 * 
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.arts.PLUTransaction;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.ItemSizeCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.MultipleQuantityDocument;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.PLUItemUtility;

@SuppressWarnings("serial")
public class CheckItemOrSuspendedTransactionEnteredAisle extends PosLaneActionAdapter
{

    /**
     * The logger to which log messages will be sent
     */
    protected static final Logger logger = Logger.getLogger(CheckItemOrSuspendedTransactionEnteredAisle.class);

    /**
     * Stores the item number in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        String itemID = null;
        boolean isScanned = false;
        boolean suspendedTransactionFound = false;
        String letterName = "ItemScanned";
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel posBase = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.SELL_ITEM);
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        cargo.skipUOMCheck(false);
        if (cargo.isRetrieveSuspendedTransactionOnSaleScreen() == false || cargo.getTransaction() != null)
        {
            letterName = "ItemScanned";
        }
        else if (posBase != null)
        {
            // Get the user input
            PromptAndResponseModel parModel = posBase.getPromptAndResponseModel();
            if (parModel != null)
            {
                isScanned = parModel.isScanned();
                if (isScanned)
                {
                    String itemIDScanned = ui.getInput().trim();
                    itemID = itemIDScanned;
                }
                else
                {
                    itemID = parModel.getResponseText();
                }

                if (itemID != null)
                {
                    // as of 13.1 we only support uppercase POS ID, BugDB
                    // 8295250
                    itemID = itemID.toUpperCase();

                    int indexOfDelimiter = itemID.indexOf(MultipleQuantityDocument.DELIMITER);
                    // If the input has item quantity
                    TransactionSummaryIfc[] summaryList = null;
                    if (indexOfDelimiter > 0)
                    {
                        letterName = "ItemScanned";
                    }
                    else
                    {
                        // saving the item number to cargo
                        cargo.setPLUItemID(itemID);
                        try
                        {
                            TransactionReadDataTransaction readTransaction = null;
                            readTransaction = (TransactionReadDataTransaction)DataTransactionFactory
                                    .create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);
                            TransactionSummaryIfc key = DomainGateway.getFactory().getTransactionSummaryInstance();
                            StoreIfc store = DomainGateway.getFactory().getStoreInstance();
                            store.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
                            key.setStore(store);
                            key.setBusinessDate(cargo.getStoreStatus().getBusinessDate());
                            key.setTransactionStatus(TransactionIfc.STATUS_SUSPENDED);
                            key.setTillID(null);
                            key.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
                            SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
                            inquiry.setTransactionSummary(key);
                            inquiry.setLocaleRequestor(utility.getRequestLocales());
                            summaryList = readTransaction.readTransactionListByStatus(inquiry);
                            if (summaryList != null)
                            {
                                for (TransactionSummaryIfc transaction : summaryList)
                                {
                                    if (transaction.getTransactionID().getTransactionIDString().equals(itemID))
                                    {
                                        letterName = "SuspendedTransaction";
                                        suspendedTransactionFound = true;
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                letterName = "ItemScanned";
                            }
                        }

                        catch (DataException e)
                        {
                            if (e.getErrorCode() == DataException.NO_DATA)
                            {
                                letterName = "ItemScanned";
                            }
                            else
                            {
                                cargo.setDataExceptionErrorCode(e.getErrorCode());
                                letterName = "ItemScanned";
                            }
                        }

                        if (suspendedTransactionFound)
                        {
                            SearchCriteriaIfc inquiry = new SearchCriteria();
                            PLURequestor pluRequestor = new PLURequestor();
                            pluRequestor.removeRequestType(PLURequestor.RequestType.AdvancedPricingRules);
                            inquiry.setPLURequestor(pluRequestor);
                            try
                            {
                                inquiry.setItemID(itemID);
                                PLUTransaction pluTransaction = null;
                                pluTransaction = (PLUTransaction)DataTransactionFactory
                                        .create(DataTransactionKeys.PLU_TRANSACTION);
                                inquiry.setLocaleRequestor(utility.getRequestLocales());
                                inquiry.setStoreNumber(cargo.getRegister().getWorkstation().getStoreID());
                                inquiry.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
                                PLUItemIfc[] pluItems = pluTransaction.getPLUItems(inquiry);
                                if (pluItems != null && pluItems.length > 0)
                                {
                                    letterName = "ConfirmItem";
                                }
                            }
                            catch (DataException de)
                            {
                                logger.error("Item :" + inquiry.getItemID() + " is not found", de);
                            }
                        }
                    }
                }
            }
        }
        bus.mail(letterName, BusIfc.CURRENT);
    }

    /**
     * Extract item size info from scanned item
     * 
     * @param itemID scanned item number
     * @return the item number
     * @deprecated 14.0 code specific to a specific client (customer) and no longer required.
     */
    protected String processScannedItemNumber(BusIfc bus, String itemID)
    {
        // Store the item size in the cargo
        String itemNumber = itemID;

        return itemNumber;
    }
}
