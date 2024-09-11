/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/TransferReturnTransactionShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/15/10 - Added for external order integration.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

// Java imports

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

//--------------------------------------------------------------------------
/**
    This shuttle sets up the Return Transaction service to retrieve kit component
    items for manual return.  A temporary transaction is created and initialized
    with tax values using the utility manager.  The transaction is used to
    initialize the kit component line items by adding the kit header plu item.
    The kit component line items from the temporary transaction are
    returned to the POS service and added to the transaction in progress.
**/
//--------------------------------------------------------------------------
public class TransferReturnTransactionShuttle extends FinancialCargoShuttle
{
    /** serialVersionUID */
    private static final long serialVersionUID = -241836975145308775L;

    /** 
     * Item Return Cargo
     */
    protected ReturnTransactionCargo fromCargo = null;

    //----------------------------------------------------------------------
    /**
       Store data from parent service in the shuttle
       <P>
       @param  bus     Parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        fromCargo = (ReturnTransactionCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Transfer parent data to child cargo.
       <P>
       @param  bus     Child Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ReturnTransactionCargo toCargo = (ReturnTransactionCargo)bus.getCargo();
        toCargo.setOriginalSaleLineItems(fromCargo.getOriginalSaleLineItems());
        toCargo.setKitHeaderItems(fromCargo.getKitHeaderItems());
        toCargo.setLineItemsToDisplayList(fromCargo.getLineItemsToDisplayList());
        toCargo.setTransactionDetailsDisplayed(fromCargo.areTransactionDetailsDisplayed());
        toCargo.setSelectedItemIndex(fromCargo.getSelectedItemIndex());
        toCargo.setSelectedIndexes(fromCargo.getSelectedIndexes());
        toCargo.setDoneSelectingDetailItems(fromCargo.isDoneSelectingDetailItems());
        toCargo.setHighlightItem(fromCargo.isHighlightItem());
        toCargo.setTransDetailFreshVisit(fromCargo.isTransDetailFreshVisit());
        toCargo.setPLUItemID(fromCargo.getPLUItemID());
        toCargo.setCurrentItem(fromCargo.getCurrentItem());
        toCargo.setTransferCargo(fromCargo.getTransferCargo());
        toCargo.setReturnData(fromCargo.getReturnData());
        toCargo.setDepartmentID(fromCargo.getDepartmentID());
        toCargo.setReturnSaleLineItems(fromCargo.getReturnSaleLineItems());
        toCargo.setReturnItems(fromCargo.getReturnItems());
        toCargo.setDisplayedTaxRatesUnavailableDialog(fromCargo.isDisplayedTaxRatesUnavailableDialog());
        toCargo.setGeoCode(fromCargo.getGeoCode());
        toCargo.setStoreID(fromCargo.getStoreID());
        toCargo.setEnableCancelItemNotFoundFromReturns(fromCargo.isEnableCancelItemNotFoundFromReturns());
        toCargo.setUnknownItemQuantity(fromCargo.getUnknownItemQuantity());
        toCargo.setLastLineItemReturnedIndex(fromCargo.getLastLineItemReturnedIndex());
        toCargo.setSalesAssociateID(fromCargo.getSalesAssociateID());
        toCargo.setDepartmentName(fromCargo.getDepartmentName());
        toCargo.setDataExceptionErrorCode(fromCargo.getDataExceptionErrorCode());
        toCargo.setReturnItemInfo(fromCargo.getReturnItemInfo());
        toCargo.setValidationFailed(fromCargo.getValidationFailed());
        toCargo.setItemScanned(fromCargo.isItemScanned());
        toCargo.setMaxPLUItemIDLength(fromCargo.getMaxPLUItemIDLength());
        toCargo.setLocalizedReasonCodes(fromCargo.getLocalizedReasonCodes());
        toCargo.setOriginalTransaction(fromCargo.getOriginalTransaction());
        toCargo.setOriginalReturnTransactions(fromCargo.getOriginalReturnTransactions());
        toCargo.setOriginalExternalOrderReturnTransactions(fromCargo.getOriginalExternalOrderReturnTransactions());
        toCargo.setTransaction(fromCargo.getTransaction());
        toCargo.setSearchCriteria(fromCargo.getSearchCriteria());
        toCargo.setTransactionFound(fromCargo.isTransactionFound());
        toCargo.setGiftReceiptSelected(fromCargo.isGiftReceiptSelected());
        toCargo.setHaveReceipt(fromCargo.haveReceipt());
        toCargo.setOriginalTransactionId(fromCargo.getOriginalTransactionId());
        toCargo.setSearchByTender(fromCargo.isSearchByTender());
        toCargo.setExternalOrderItemReturnStatusElements(fromCargo.getExternalOrderItemReturnStatusElements());
        toCargo.setCurrentExternalOrderItemReturnStatusElement(fromCargo.getCurrentExternalOrderItemReturnStatusElement());
        toCargo.setExternalOrder(fromCargo.isExternalOrder());
        toCargo.setAccessFunctionID(fromCargo.getAccessFunctionID());
    }
}
