/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved. 
 *
 *	Rev 1.0 	May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/
/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved. 
 *
 *	Rev 1.0 	June 17, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/

package max.retail.stores.pos.services.returns.returnoptions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.DiscountableLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnData;
import oracle.retail.stores.pos.services.returns.returnoptions.CreateAndUpdateTransactionSite;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This creates a transaction if needed and updates the transaction with the
 * return items.
 *
 */
public class MAXCreateAndUpdateTransactionSite extends CreateAndUpdateTransactionSite
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = 6827832462449192175L;
    /**
     * site name constant
     */
    public static final String SITENAME = "CreateAndUpdateTransactionSite";
    /**
     * Constant for journalling
     */
    protected static final int ITEM_PRICE_LENGTH = 13;
    /**
     * Constant for journalling
     */
    protected static final int ITEM_NUMBER_LENGTH = 20;
     /**
      * Customer name bundle tag
      */
     protected static final String CUSTOMER_NAME_TAG = "CustomerName";
     /**
      * Customer name default text
      */
     protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";
     /**
      * For electronic journaling (EJ), names of countries or states that need to be marked as unknown.
      * The actual text used in the EJ will come from the "commonText" resource bundle.
      */
    protected static final String[] UNKNOWN_NAMES = { "ZZ", "ZZZ" };

    /**
     * This creates a transaction if needed and updates the transaction with the
     * return items.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the cargo and current transaction
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.FAILURE);

        // Get the return item information from the cargo
        ReturnData returnData = cargo.getReturnData();

        ReturnItemIfc[] returnItems = null;
        PLUItemIfc[] pluItems = null;
        SaleReturnLineItemIfc[] saleReturnLineItems = null;

        if (returnData != null)
        {
            returnItems = returnData.getReturnItems();
            pluItems = returnData.getPLUItems();
            saleReturnLineItems = returnData.getSaleReturnLineItems();
       }

        if(returnItems != null && returnItems.length != 0)
        {
            SaleReturnTransactionIfc transaction = cargo.getTransaction();
            
            SaleReturnTransactionIfc originalTransaction = cargo.getOriginalTransaction();

            // If there is no transaction ....
            if (transaction == null)
            {
                TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);

                transaction =
                        DomainGateway.getFactory().getSaleReturnTransactionInstance();

                transaction.setSalesAssociate(cargo.getSalesAssociate());
                utility.initializeTransaction(transaction, TransactionUtilityManagerIfc.GENERATE_SEQUENCE_NUMBER);
                cargo.setTransaction(transaction);
            }

          //Rev 1.0 start 
			if(originalTransaction != null && originalTransaction.getCustomerInfo() != null)
			{
				//transaction.setCustomer(originalTransaction.getCustomer());
				if(cargo.getTransaction() instanceof MAXSaleReturnTransaction)
				{
					MAXSaleReturnTransaction tran=(MAXSaleReturnTransaction) cargo.getTransaction();
									
					PhoneIfc custMobile = originalTransaction.getCustomerInfo().getPhoneNumber();
					tran.setCustOgMobile(custMobile.getPhoneNumber());
					tran.setOgTransaction(originalTransaction.getTransactionID());
					
					
				}
				if (cargo.getTransaction() instanceof MAXLayawayTransaction)
				{
					MAXLayawayTransaction tran=(MAXLayawayTransaction) cargo.getTransaction();
					
					PhoneIfc custMobile = originalTransaction.getCustomerInfo().getPhoneNumber();
					tran.setCustOgMobile(custMobile.getPhoneNumber());
					tran.setOgTransaction(originalTransaction.getTransactionID());
					
				}
				
			}
			if(originalTransaction != null && originalTransaction.getCustomer() != null)
			{
				transaction.setCustomer(originalTransaction.getCustomer());

			}
			if(originalTransaction != null && originalTransaction.getCustomer() != null && originalTransaction.getCustomerInfo() == null)
			{
				if(originalTransaction.getCustomer().getPhoneByType(0) != null)
				{
					if(cargo.getTransaction() instanceof MAXSaleReturnTransaction)
				{
					MAXSaleReturnTransaction tran=(MAXSaleReturnTransaction) cargo.getTransaction();
					
					PhoneIfc custMobile = originalTransaction.getCustomer().getPhoneByType(0);
					tran.setCustOgMobile(custMobile.getPhoneNumber());
					tran.setOgTransaction(originalTransaction.getTransactionID());
				}
				if (cargo.getTransaction() instanceof MAXLayawayTransaction)
				{
					MAXLayawayTransaction tran=(MAXLayawayTransaction) cargo.getTransaction();
					PhoneIfc custMobile = originalTransaction.getCustomer().getPhoneByType(0);
					tran.setCustOgMobile(custMobile.getPhoneNumber());
					tran.setOgTransaction(originalTransaction.getTransactionID());
				
		}
				}
			}
			//Rev 1.0 end 
            
            // Get the line items from the retrieve transaction
            // Process each return line item
            for(int i = 0; i < returnData.getReturnItems().length; i++)
            {
                // Prepare line item.
                SaleReturnLineItemIfc srli = null;

                ReturnItemIfc returnItem = returnItems[i];

                if ( returnItem != null )
                {
                    BigDecimal quantityReturned = returnItems[i].getItemQuantity().negate();
                    BigDecimal quantityReturnable = returnItems[i].getQuantityReturnable();

                    // If this is a manual return ...
                   if (!returnItem.isFromRetrievedTransaction())
                   {
                        // Add the line item
                        if (cargo.isExternalOrder())
                        {
                            ExternalOrderItemIfc eoi = pluItems[i].getReturnExternalOrderItem();
                            srli = transaction.addReturnItem(pluItems[i], returnItem, eoi);
                            srli.setExternalOrderItemID(eoi.getId());
                            srli.setExternalOrderParentItemID(eoi.getParentId());
                        }
                        else
                        {
                            srli = transaction.addReturnItem(pluItems[i], returnItem, quantityReturned);
                        }

                        if (!Util.isEmpty(saleReturnLineItems[i].getItemSerial()))
                        {
                            srli.setItemSerial(saleReturnLineItems[i].getItemSerial());
                        }
                        //if it is manual gift receipt return item  copy to srli
                        if (returnItem.isFromGiftReceipt())
                        {
                            srli.setGiftReceiptItem(returnItem.isFromGiftReceipt());
                        }

                        // copy entry method from return item
                        srli.setEntryMethod(returnItem.getEntryMethod());
                        journalLine(bus, cargo, pluItems, i, srli);
                    }
                    // If this is a return based on a retrieved transaction
                    else
                    {
                        // Use the Sale Return item from the transaction
                        srli = saleReturnLineItems[i];

                        // This fix was made to make sure the price reaching RM from POS is Discounted Price and not Selling Price.
                        // Makinf sure there is no null pointer and Deivide by Zero.
                        if(srli.getExtendedDiscountedSellingPrice() != null && srli.getItemPrice().getItemQuantityDecimal() != null && srli.getItemPrice().getItemQuantityDecimal().intValue() > 0)
                        {
                          CurrencyIfc price = srli.getExtendedDiscountedSellingPrice().abs().divide(srli.getItemPrice().getItemQuantityDecimal());
                          returnItem.setPrice(price);
                        }

                        if (srli.isGiftReceiptItem())
                        {
                            returnItem.setFromGiftReceipt(srli.isGiftReceiptItem());
                        }

                        srli.setReturnItem(returnItem);
                        // Set the send label count to zero. Shipment addresses do not print on
                        // the receipt for returns.
                        srli.setSendLabelCount(0);

                        // set the restocking fee from the return item in the item price
                        srli.getItemPrice().setRestockingFee(returnItem.getRestockingFee());

                       
                        
                        // Update the quantity returned value in the order line item status.
                        if (srli instanceof OrderLineItemIfc)
                        {
                            //OrderItemStatusIfc ois = ((OrderLineItemIfc)srli).getOrderItemStatus();
                            //ois.setQuantityReturned(ois.getQuantityReturned().add(quantityReturned.abs()));
                            if (cargo.getOriginalTransaction() != null)
                            {
                                ((OrderLineItemIfc)srli).setOrderID(cargo.getOriginalTransaction().getOrderID());
                            }
                            
                            // This method does the equivalent of srli.modifyItemQuantity() for order line items
                            ((OrderLineItemIfc)srli).prorateItemForReturn(quantityReturned);
                            
                            // The prorateItemForReturn() converts transaction discounts to item discounts.
                            // The transaction discount amount must be cleared to make the line totals 
                            // calculations to come out correctly.
                            ((DiscountableLineItemIfc)srli).clearTransactionDiscounts();
                            
                            // Add the line item to the transaction and journal it.
                            transaction.addLineItem(srli);
                            addTax(transaction, srli, returnItem);
                            journalLine(bus, cargo, pluItems, i, srli);
                            
                            // Save the return, discount and tax amounts to the Order Line Item Status object.
                            ((OrderLineItemIfc)srli).prepareReturnLineItem();
                            
                        }
                        else
                        {
                            srli.modifyItemQuantity(quantityReturned);

                            // convert transaction discounts to item discounts (if needed)
                            List<SaleReturnLineItemIfc> srliListRemainders = convertTransactionDiscounts(transaction, srli, returnData, i, quantityReturned, quantityReturnable);
                            
                            // the srli was split up to account for some rounding issues, new elements were added to the returnData's arrays by
                            // convertTransactionDiscounts(), so, updated references
                            if (srliListRemainders != null && srliListRemainders.size() > 0)
                            {
                                returnItems = returnData.getReturnItems();
                                pluItems = returnData.getPLUItems();
                                saleReturnLineItems = returnData.getSaleReturnLineItems();
                            }

                            transaction.addLineItem(srli);
                            addTax(transaction, srli, returnItem);
                            journalLine(bus, cargo, pluItems, i, srli);

                            // the srli was split up, add the new srli to the transaction, increment the counter so it
                            // not processed twice
                            if (srliListRemainders != null && srliListRemainders.size() > 0)
                            {
                                for (SaleReturnLineItemIfc srliNew : srliListRemainders)
                                {
                                    i++;
                                    transaction.addLineItem(srliNew);
                                    addTax(transaction, srliNew, returnItem);
                                    journalLine(bus, cargo, pluItems, i, srliNew);
                                }
                            }
                        }
                        
                    } // no receipt (manual) vs. receipt (isFromRetrievedTransaction)
                } // returnItem != null
            } // loop over returnItems

            // Add retrieved tranaction(s) to the array of original transactions.
            updateOriginalTransactionArray(cargo);

            // Add tenders from the retrieved tranaction(s) to the array of
            // original tenders.
            updateOriginalTendersArray(cargo);

            // Set the customer to the transaction and the status panel.
            CustomerIfc customer = getCustomer(transaction, cargo);

            if (customer != null)
            {
                transaction.setCustomer(customer);
                // set the customer's name in the status area
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

                UtilityManagerIfc utility =
                  (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                Object parms[] = { customer.getFirstName(), customer.getLastName() };
                String pattern = utility.retrieveText("CustomerAddressSpec",
                                       BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                                       CUSTOMER_NAME_TAG,
                                       CUSTOMER_NAME_TEXT);
                String customerName = LocaleUtilities.formatComplexMessage(pattern, parms);

                ui.customerNameChanged(customerName);
            }

            // Indicate to the return shuttle that data should be transfered to the
            // calling service.
            cargo.setTransferCargo(true);
            letter = new Letter(CommonLetterIfc.SUCCESS);
         }

        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * journal the new srli
     *
     *
     * @param bus
     * @param cargo
     * @param pluItems
     * @param index
     * @param srli
     */
    private void journalLine(BusIfc bus, ReturnOptionsCargo cargo, PLUItemIfc[] pluItems, int index,
            SaleReturnLineItemIfc srli)
    {
        // Journal each item.
        JournalManagerIfc jm = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (jm != null)
        {
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            journalLineItem(jm, srli, pluItems[index], cargo, utility);
        }
    }

    /**
     * add tax to the new srli
     *
     * @param transaction
     * @param srli
     * @param ri
     */
    private void addTax(SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc srli,
            ReturnItemIfc ri)
    {
        // Add the line item to the transaction.  When line items are added to the list
        // the transaction tax is recalculated.  We will need to negate the tax values at this point.
        SaleReturnLineItemIfc[] liList = (SaleReturnLineItemIfc[]) transaction.getLineItems();
        srli = liList[liList.length-1];
        if (srli.getItemTax().getTaxMode() != TaxIfc.TAX_MODE_EXEMPT)
        {
            srli.getItemTax().setDefaultRate(ri.getTaxRate());
            if (srli.getItemTax().getTaxScope() == TaxIfc.TAX_SCOPE_TRANSACTION)
            {
                // force item scope here
                srli.getItemTax().setTaxScope(TaxIfc.TAX_SCOPE_ITEM);
            }
        }
    }

    /**
     * Since the transaction-scope discounts are not carried over to the return
     * transaction, each item's transaction-scope discounts are converted to
     * item-scope amount discounts.
     *
     * Without this conversion the recalculation which occurs when transactions
     * are read from the database will not consider the transaction-scope discounts.
     *
     * This results in incorrect extended discount amounts in the PosLog.  (And whatever
     * else reads the transactions after they are saved.)
     *
     * Note that the reason code is not carried over since there is no mapping
     * between one discount type's codes to the item-scope amount type's code.
     *
     * Unfortunately, the conversion from a transaction discount to an item
     * discount can result in a discount rounding error.
     *
     * For instance if coolboxes x 3 (qauntity) has a $0.05 discount.  ($0.05 is not
     * evenly divisible by 3.)
     *
     * This method compensates for this by adding, when needed, an srli to contain the
     * extra discount amount.
     *
     * The additional srli is created when all items are being returned (if needed).
     *
     * To make tax, etc work properly for the new srli this method also inserts return data
     * and plu items into the returnData object.
     *
     * The SRLI parameter and its associated return data is adusted when the SRLI is split up
     * to handle the rounding error.
     *
     * IMPORTANT NOTE: The caller of this method must adust any indexes to compensate for the
     * insertion of new elements into the returnData's arrays.
     *
     * @param transaction
     * @param srli SaleReturnLineItemIfc object
     * @param returnData
     * @param returnDataIndex
     * @param quantityReturned quantity of item returned in this srli
     * @param quantityReturnable quantity of item that can be returned
     * @return returns a list of new SRLI, if needed, which contains par
     */
    protected List<SaleReturnLineItemIfc> convertTransactionDiscounts(
            SaleReturnTransactionIfc transaction,
            SaleReturnLineItemIfc srli,
            ReturnData returnData, int returnDataIndex,
            BigDecimal quantityReturned, BigDecimal quantityReturnable)
    {

        ItemDiscountStrategyIfc[] discounts = srli.getItemPrice().getTransactionDiscounts();

        // do nothing if there are no transaction discounts
        if (discounts == null || discounts.length == 0)
        {
            return null;
        }

        ReturnItemIfc returnItem1 = returnData.getReturnItems()[returnDataIndex];

        // the srliRemainder which may be needed to contain the leftover discount due to rounding errror
        List<SaleReturnLineItemIfc> srliRemainders = new ArrayList<SaleReturnLineItemIfc>();

        boolean allReturned = quantityReturned.abs().intValue() == quantityReturnable.abs().intValue();
        BigDecimal originalQuantityPurchased = returnItem1.getQuantityPurchased().abs();

        // clear transaction discounts, these are replaced with item discounts
        srli.clearTransactionDiscounts();

        CurrencyIfc originalDiscountAmount = DomainGateway.getBaseCurrencyInstance();
        ItemTransactionDiscountAuditIfc itda = DomainGateway.getFactory().getItemTransactionDiscountAuditInstance();

        // loop over all transaction discounts, converting each into an item
        // discount
        for (int i = 0; i < discounts.length; i++)
        {
            itda = (ItemTransactionDiscountAuditIfc)discounts[i];
            originalDiscountAmount = ((CurrencyIfc)itda.getDiscountAmount().clone()).abs().add(originalDiscountAmount);
        }
        
        // round down to avoid refunding more than original discount (since this
        // item discount is multiplied by quantity)
        originalDiscountAmount.setRoundingMode(BigDecimal.ROUND_FLOOR);
        CurrencyIfc newItemDiscountAmount = originalDiscountAmount.divide(originalQuantityPurchased);
        newItemDiscountAmount.setRoundingMode(BigDecimal.ROUND_HALF_UP); 
        
        // set rounding mode back to normal
        CurrencyIfc newTotalSrliDiscountAmount = newItemDiscountAmount.multiply(quantityReturned.abs());

        // compare the total discount using the rounded down total discount with
        // the actual discount from the original transaction
        CurrencyIfc unadjustedTotalDiscountAmount = newItemDiscountAmount.multiply(originalQuantityPurchased);
        CurrencyIfc remainingTotalDiscountAmount = originalDiscountAmount.subtract(unadjustedTotalDiscountAmount);
        CurrencyIfc adjustedEachDiscountAmount = newItemDiscountAmount.add(remainingTotalDiscountAmount);

        boolean roundingErrorRequiresCorrection = unadjustedTotalDiscountAmount.compareTo(originalDiscountAmount) == CurrencyIfc.LESS_THAN;

        itda.setDiscountAmount(newTotalSrliDiscountAmount);

        ItemDiscountByAmountIfc ritda = null;
        

        // this srli contains a single item, there was a rounding error, this
        // item must include the leftover (adjusted) discount
        if (allReturned && roundingErrorRequiresCorrection && quantityReturned.abs().intValue() == 1)
        {
            ritda =  createItemDiscountByAmount(adjustedEachDiscountAmount, itda);
        }
        else
        {
            ritda =  createItemDiscountByAmount(newItemDiscountAmount, itda);
        }

        srli.addItemDiscount(ritda);

        // the srli must be split up to compensate for the rounding error
        if (allReturned && roundingErrorRequiresCorrection && quantityReturned.abs().intValue() > 1)
        {
            /*
             * Create the srli item based on the remainingTotalDiscountAmount
             * until the discounted amount is less than or equal to the selling
             * price new return items and plu items must be inserted into
             * returndData for the new srli the 1st return srli and its return
             * data's quantities must be decremented to account for the new srli
             */
            boolean adjustmentRequired = true;
            // amountTobeAdjustedFortheSplitItem is the difference between the
            // selling price and each item discount amount
            CurrencyIfc amountTobeAdjustedFortheSplitItem = srli.getSellingPrice().subtract(newItemDiscountAmount);

            while (adjustmentRequired)
            {

                // the srli which may be needed to contain the leftover discount
                // due to rounding errror
                SaleReturnLineItemIfc srliRemainder = null;

                // create a return item for the new srli in the returnData
                // the new srli that contains the adjusted discount will always
                // have quantity 1
                ReturnItemIfc additionalReturnItem = (ReturnItemIfc)returnItem1.clone();
                additionalReturnItem.setQuantityReturnable(BigDecimal.ONE);
                additionalReturnItem.setItemQuantity(BigDecimal.ONE);

                // reduce quantity of the 1st return item
                returnItem1.setItemQuantity(returnItem1.getItemQuantity().subtract(BigDecimal.ONE));

                List<ReturnItemIfc> riList = new ArrayList<ReturnItemIfc>(Arrays.asList(returnData.getReturnItems()));
                riList.add(returnDataIndex + 1, additionalReturnItem);
                returnData.setReturnItems(riList.toArray(new ReturnItemIfc[0]));

                // create a plu item for the new srli in the returnData
                List<PLUItemIfc> pluList = new ArrayList<PLUItemIfc>(Arrays.asList(returnData.getPLUItems()));
                pluList.add(returnDataIndex + 1, (PLUItemIfc)returnData.getPLUItems()[returnDataIndex].clone());
                returnData.setPLUItems(pluList.toArray(new PLUItemIfc[0]));

                srliRemainder = (SaleReturnLineItemIfc)srli.clone();
                // don't keep the 1st srli's discounts
                srliRemainder.getItemPrice().clearItemDiscounts();
                
                srliRemainder.modifyItemQuantity(BigDecimal.ONE.negate());
                srliRemainder.setReturnItem(additionalReturnItem);

                srliRemainder.getItemPrice().setRestockingFee(additionalReturnItem.getRestockingFee());

                // create a srli item for the new srli in the returnData
                List<SaleReturnLineItemIfc> srliList = new ArrayList<SaleReturnLineItemIfc>(Arrays.asList(returnData
                        .getSaleReturnLineItems()));
                srliList.add(returnDataIndex + 1, srliRemainder);
                srliRemainders.add(srliRemainder);
                returnData.setSaleReturnLineItems(srliList.toArray(new SaleReturnLineItemIfc[0]));

                // create a 2nd item discount for the new srli
                ItemDiscountByAmountIfc ritda2 = DomainGateway.getFactory().getItemDiscountByAmountInstance();
                ritda2.setAssignmentBasis(itda.getAssignmentBasis());
                ritda2.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
                ritda2.setDiscountScope(DiscountRuleConstantsIfc.DISCOUNT_SCOPE_ITEM);
                ritda2.setDiscountEmployee(itda.getDiscountEmployee());

                // decrement the quantity returned value
                quantityReturned = quantityReturned.add(BigDecimal.ONE);
                // reduce the 1st srli's quantity (since one was moved into
                // srliRemainder)
                srli.modifyItemQuantity(quantityReturned);

                CurrencyIfc discountAmountForSplit = DomainGateway.getBaseCurrencyInstance();
                // Compare the remainingTotalDiscountAmount with the
                // amountTobeAdjustedFortheSplitItem , if the
                // remainingTotalDiscountAmount is greater , then it needs one
                // more split .
                // adjust the amount from the remainingTotalDiscountAmount
                if (remainingTotalDiscountAmount.compareTo(amountTobeAdjustedFortheSplitItem) == CurrencyIfc.GREATER_THAN)
                {

                    remainingTotalDiscountAmount = remainingTotalDiscountAmount
                            .subtract(amountTobeAdjustedFortheSplitItem);

                }
                // Compare the remainingTotalDiscountAmount with the
                // amountTobeAdjustedFortheSplitItem , if the
                // remainingTotalDiscountAmount is not greater , then reset the
                // adjustmentRequired to exit the loop .
                // amountTobeAdjustedFortheSplitItem is reset with
                // remainingTotalDiscountAmount
                else
                {
                    amountTobeAdjustedFortheSplitItem = remainingTotalDiscountAmount;
                    adjustmentRequired = false;

                }

                // Add the each item's discount amount to the
                // amountTobeAdjustedFortheSplitItem and set this discount to
                // the new srli item
                discountAmountForSplit = amountTobeAdjustedFortheSplitItem.add(newItemDiscountAmount);
                ritda2.setDiscountAmount(discountAmountForSplit);
                srliRemainder.addItemDiscount(ritda2);

            }

        }

        return srliRemainders;

    }

    /**
     * This method creates item discount amount object
     * @param discountAmount
     * @param itda
     * @return
     */
    @SuppressWarnings("deprecation")
    protected ItemDiscountByAmountIfc createItemDiscountByAmount (CurrencyIfc discountAmount, ItemTransactionDiscountAuditIfc itda)
    {
        ItemDiscountByAmountIfc ritda = DomainGateway.getFactory().getItemDiscountByAmountInstance(); 
        ritda.setDiscountAmount(discountAmount);
        // convert the discount to an item amount discount
        ritda.setAssignmentBasis(itda.getAssignmentBasis());
        ritda.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
        ritda.setDiscountScope(DiscountRuleConstantsIfc.DISCOUNT_SCOPE_ITEM);
        ritda.setDiscountEmployee(itda.getDiscountEmployee());
        ritda.setReason(itda.getReason());
        ritda.setPromotionId(itda.getPromotionId());
        ritda.setPromotionComponentId(itda.getPromotionComponentId());
        ritda.setPromotionComponentDetailId(itda.getPromotionComponentDetailId());
        ritda.setReferenceID(itda.getReferenceID());
        ritda.setReferenceIDCode(itda.getReferenceIDCode());
        ritda.setRuleID(itda.getRuleID());
        return ritda;
    }
    
    /**
     * Add all required return information to the ejournal. <P>
     *
     * @param jm JournalManagerIfc
     * @param item SaleReturnLineItemIfc contains data to journal
     * @param pluItem Not used
     * @param cargo containing customer info for journaling
     *
     * @parm ReturnOptionsCargo contains data to journal
     */
    protected void journalLineItem(JournalManagerIfc jm, SaleReturnLineItemIfc item,
                                   PLUItemIfc pluItem, ReturnOptionsCargo cargo, UtilityManagerIfc utility)
    {
      JournalFormatterManagerIfc formatter =
        (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
        ItemPriceIfc ip = item.getItemPrice();
        StringBuffer sb = new StringBuffer();
        CurrencyIfc amountDiscountTotals = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amountPercentDiscountTotals = DomainGateway.getBaseCurrencyInstance();
        sb.append(formatter.toJournalString(item, null, null));
        // When tax needs to keep precision at line item level and round at transaction
        // level, need to keep 5-digit after decimal point.
        if(item.getItemPrice() != null && item.getItemPrice().getItemTax() != null
                && item.getItemPrice().getItemTax().getTaxInformationContainer() != null &&
                item.getItemPrice().getItemTax().getTaxInformationContainer().getTaxAmount() != null)
        {
            String tax = item.getItemPrice().getItemTax().getTaxInformationContainer().getTaxAmount().toFormattedString();
            if(!(tax.equals("0.00") || tax.equals("0,00")))
            {
                Object dataObject[]={tax};
                String taxData = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_LABEL, dataObject);

                sb.append(Util.EOL);
                sb.append(taxData);
            }
        }

        ItemDiscountStrategyIfc[] ida = ip.getItemDiscountsByAmount();
        // accumulate amounts of item discount by amount
        if (ida.length > 0)
        {
            amountDiscountTotals =
                ((ItemDiscountByAmountIfc)ida[0]).getDiscountAmount();
            for(int i = 1; i > ida.length; i++)
            {
                amountDiscountTotals =
                    amountDiscountTotals.add
                    (((ItemDiscountByAmountIfc)ida[i]).getDiscountAmount());
            }
        }

        ItemDiscountStrategyIfc[] idp = ip.getItemDiscountsByPercentage();
        // accumulate amounts of item discounts by percent
        if (idp.length > 0)
        {
            BigDecimal percentDiscountTotals = idp[0].getDiscountRate();
            for(int i = 1; i > idp.length; i++)
            {
                percentDiscountTotals =
                    percentDiscountTotals.add(idp[i].getDiscountRate());
            }
            BigDecimal sellingPrice = new BigDecimal(ip.getSellingPrice().toString());
            percentDiscountTotals = percentDiscountTotals.multiply(sellingPrice);
            amountPercentDiscountTotals =
                DomainGateway.getBaseCurrencyInstance(percentDiscountTotals.toString());
        }

        // accumulate return item transaction discounts
        ItemDiscountStrategyIfc[] idr = ip.getReturnItemDiscounts();
        if (idr.length > 0)
        {                               // begin check return discounts
            int method = -1;
            ReturnItemTransactionDiscountAuditIfc ritda = null;
            for (int i = 0; i < idr.length; i++)
            {                           // begin loop through return discounts
                ritda = (ReturnItemTransactionDiscountAuditIfc) idr[i];
                                // exclude customer discounts
                                // These are journaled later in the stream.
                if (ritda.getAssignmentBasis() !=
                    DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER)
                {                       // begin check non-customer discounts
                    method = ritda.getOriginalDiscountMethod();
                    if (method ==
                        DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
                    {
                        amountPercentDiscountTotals =
                            amountPercentDiscountTotals.add
                            (ritda.getDiscountAmount().negate());
                    }
                    else if (method ==
                             DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
                    {
                        amountDiscountTotals =
                            amountDiscountTotals.add
                            (ritda.getDiscountAmount().negate());
                    }
                }                       // end check non-customer discounts
            }                           // end loop through return discounts
        }                               // end check return discounts

        // if amount discounts, print it
        if ((amountDiscountTotals.signum() > 0)
                || ((amountDiscountTotals.signum() < 0) && (item.isReturnLineItem())))
        {
            BigDecimal amountDiscountTotalsDecimal =
                new BigDecimal(amountDiscountTotals.toString());
            String amountDiscountString = DomainGateway.getBaseCurrencyInstance(amountDiscountTotalsDecimal.multiply(item.getReturnItem().getItemQuantity()).toString()).toGroupFormattedString();
            Object discountDataObject[]={amountDiscountString};
            String discountReturned = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISCOUNT_RETURNED, discountDataObject);

            sb.append(Util.EOL)
              .append(discountReturned);

        }

        // if percentage discounts, print it
        if ((amountPercentDiscountTotals.signum() > 0)
                || ((amountPercentDiscountTotals.signum() < 0) && (item.isReturnLineItem())))
        {
            String percentDiscountString = amountPercentDiscountTotals.multiply(item.getReturnItem().getItemQuantity()).toString();
            Object discountDataObject[]={percentDiscountString};

            String discountReturned = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISCOUNT_RETURNED_PER, discountDataObject);
            sb.append(Util.EOL).append(discountReturned);

        }

        //journal any advanced pricing discount amounts
        ItemDiscountStrategyIfc advancedPricingDiscount = item.getAdvancedPricingDiscount();
        if (advancedPricingDiscount != null)
        {
            CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();

            switch (advancedPricingDiscount.getDiscountMethod())
            {
                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE :
                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT :
                    amount = advancedPricingDiscount.getDiscountAmount();
                    break;
                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE :
                    amount.setDecimalValue(advancedPricingDiscount.getDiscountRate());
                    amount = amount.multiply(item.getExtendedSellingPrice().negate());
                    break;
                default :
                    break;
            }

            // if advanced pricing discount, print it
            if (amount.signum() > 0)
            {
                BigDecimal amountDecimal = new BigDecimal(amount.toString());
                String amountString = DomainGateway.getBaseCurrencyInstance(amountDecimal.toString()).toGroupFormattedString();

                Object dealDiscountDataObject[]={amountString};
                String dealDiscountReturned = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DEAL_DISCOUNT_RETURNED, dealDiscountDataObject);

                sb.append(Util.EOL)
                  .append(dealDiscountReturned);

            }

        }

        // journal the restocking fee, if any
        CurrencyIfc extendedRestockingFee = ip.getExtendedRestockingFee();
        if (extendedRestockingFee != null)
        {
            if (extendedRestockingFee.compareTo(DomainGateway.getBaseCurrencyInstance())!= CurrencyIfc.EQUALS)
            {
                CurrencyIfc restockingFee = extendedRestockingFee.multiply(new BigDecimal(-1));
                String restockingFeeString = restockingFee.toGroupFormattedString();

                Object restockDataObject[]={restockingFeeString};
                String restockingFeeData = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.RESTOCKING_FEE, restockDataObject);

                sb.append(Util.EOL).append(restockingFeeData);
            }
        }

        // Sales Associate
        if (item.getSalesAssociate() != null)
        {
            String assc = item.getSalesAssociate().getEmployeeID();
            Object dataObject[]={assc};

            String salesAssociation = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SALES_ASSOCIATION, dataObject);
            sb.append(Util.EOL).append(salesAssociation);

        }

        CustomerInfoIfc customerInfo = cargo.getCustomerInfo();
        if (customerInfo != null && customerInfo.getPersonalID().getMaskedNumber() != null
                && customerInfo.getPersonalID().getMaskedNumber().length() > 0
                && isFirstReturnItem(cargo.getTransaction(), item))
        {
            Object personalIDDataObject[]={customerInfo.getPersonalID().getMaskedNumber()};
            String personalID = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PERSONAL_ID, personalIDDataObject);

            String personalIDTypeCode = "";
            if(customerInfo.getLocalizedPersonalIDType() != null)
            {
                personalIDTypeCode = customerInfo.getLocalizedPersonalIDType().getCode();
            }
            Object personalIDTypeDataObject[]={personalIDTypeCode};
            String personalIDType = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PERSONAL_ID_TYPE, personalIDTypeDataObject);

            Object stateIDDataObject[]={retrieveNameFor(customerInfo.getPersonalIDState(), utility)};
            String stateID = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.STATE_ID, stateIDDataObject);

            Object countryIDDataObject[]={retrieveNameFor(customerInfo.getPersonalIDCountry(), utility)};
            String countryID = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.COUNTRY_ID, countryIDDataObject);

            sb.append(Util.EOL)
              .append(Util.EOL)
              .append(personalID)
              .append(Util.EOL)
              .append(personalIDType)
              .append(Util.EOL)
              .append(stateID)
              .append(Util.EOL)
              .append(countryID);
        }

        jm.journal(cargo.getOperator().getLoginID(),
                   cargo.getTransaction().getTransactionID(),
                   sb.toString());
    }

    /**
     * Returns a name appropriate for the state or country passed in, which is typically the name given to us.
     * But in the case that the name is officially unknown, returns whatever the resource bundle has for this name --
     * typically a blank.
     *
     * @param name    The name of a state or country
     * @param utility The current UtilityManager
     *
     * @return An appropriate name for the state or country.
     * @see #UNKNOWN_NAMES
     */
    protected String retrieveNameFor(String name, UtilityManagerIfc utility)
    {
        // Defense
        if (utility == null || name == null)
        {
            return name;
        }

        // Check to see if we are in the land of the unknown (for example: "ZZ" or "ZZZ")
        boolean bKnown = true;   // Assume we are known
        for (int i = 0; i < UNKNOWN_NAMES.length && bKnown; ++i)
        {
            if (name.equals(UNKNOWN_NAMES[i]))
            {
                bKnown = false;  // I am "bUnknown"
            }
        }

        // Return something.  :)
        if (bKnown)
        {
            // If we are in the land of the known, simply return with the original name.
            return name;
        }

        // If we are in the land of the unknown, use the UtilityManager to determine what we should actually show.
        return utility.retrieveCommonText(name);
    }

    /**
     * Method to determine if the current item is the first return item in the
     * transaction. Used when journalling personal ID info, which only gets
     * journalled with the first return item.<P>
     *
     * @param transaction SaleReturnTransactionIfc transaction
     * @param currentItem SaleReturnLineItemIfc line item in transaction
     *
     * @return true when the current item is the first return
     *  item in the transaction.
     */
    protected boolean isFirstReturnItem(SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc currentItem)
    {
        Vector<AbstractTransactionLineItemIfc> lineItems = null;
        SaleReturnLineItemIfc li = null;
        boolean firstFlag = true;
        int returnCount = 0;

        // If either transaction or list of items is null (which shouldn't happen)
        // then just return true that this is the first return item.
        if (transaction == null)
            return firstFlag;

        lineItems = transaction.getLineItemsVector();
        if (lineItems == null)
            return firstFlag;

        // Go through the list of line items
        // If there are one or more return items found before we get to the
        // current line item, then this is not the first return item.
        for (int i=0; i<lineItems.size(); i++)
        {
            li = (SaleReturnLineItemIfc)lineItems.elementAt(i);
            if (li.isReturnLineItem())
            {
                // Increment count of the return line items
                returnCount++;

                // Quit when we've reached the current line item
                if (currentItem.equals(li))
                    break;
            }
        }

        // If there are more returns ahead of this line item, then this is
        // not the first
        if (returnCount >= 2)
        {
            firstFlag = false;
        }

        return firstFlag;  // Return result
    }

    /**
     * Add retrieved tranaction(s) to the array of original transactions.
     * @param cargo
     */
    protected void updateOriginalTransactionArray(ReturnOptionsCargo cargo)
    {
        List<SaleReturnTransactionIfc> list = cargo.getOriginalExternalOrderReturnTransactions();
        if (cargo.isExternalOrder() && list != null)
        {
            for(SaleReturnTransactionIfc trans: list)
            {
                cargo.addOriginalReturnTransaction(trans);
            }
        }
        else
        if (cargo.getOriginalTransaction() != null)
        {
            cargo.addOriginalReturnTransaction(cargo.getOriginalTransaction());
        }
    }

    /**
     * Add tenders from the retrieved tranaction(s) to the array of
     * original tenders.
     * @param cargo
     */
    protected void updateOriginalTendersArray(ReturnOptionsCargo cargo)
    {
        List<SaleReturnTransactionIfc> list = cargo.getOriginalExternalOrderReturnTransactions();
        if (cargo.isExternalOrder() && list != null)
        {
            for(SaleReturnTransactionIfc trans: list)
            {
                ReturnTenderDataElementIfc[] returnTenders = getOriginalTenders(
                        trans.getTenderLineItems());
                trans.setReturnTenderElements(returnTenders);
                cargo.appendOriginalTenders(returnTenders);
            }
        }
        else
        if (cargo.getOriginalTransaction() != null)
        {
            ReturnTenderDataElementIfc[] returnTenders = getOriginalTenders(
                    cargo.getOriginalTransaction().getTenderLineItems());
            cargo.getOriginalTransaction().setReturnTenderElements(returnTenders);
            cargo.appendOriginalTenders(cargo.getOriginalTransaction().getReturnTenderElements());
        }
    }

    /*
     * Retrieve tenders from original transaction
     */
    private ReturnTenderDataElementIfc[] getOriginalTenders(TenderLineItemIfc[] tenderList)
    {
        ReturnTenderDataElementIfc [] tenders = new ReturnTenderDataElementIfc[tenderList.length];
        for (int i =0; i < tenderList.length; i++)
        {
            tenders[i]=DomainGateway.getFactory().getReturnTenderDataElementInstance();
            tenders[i].setTenderType(tenderList[i].getTypeCode());
            if (tenderList[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
            {
                tenders[i].setCardType(((TenderChargeIfc)tenderList[i]).getCardType());
            }
            tenders[i].setAccountNumber(new String(tenderList[i].getNumber()));
            tenders[i].setTenderAmount(tenderList[i].getAmountTender());
        }
        return tenders;
    }

    /**
     * Find the customer associated with this transaction, if there is one.
     * @param transaction
     * @param cargo
     * @return
     */
    protected CustomerIfc getCustomer(SaleReturnTransactionIfc transaction, ReturnOptionsCargo cargo)
    {
        CustomerIfc customer = null;

        if (transaction.getCustomer() != null)
        {
            // if there is a customer associated with the current transaction.
            customer = transaction.getCustomer();
        }
        else
        if (cargo.getCustomer() != null)
        {
            // if there is a customer in cargo.
            customer = cargo.getCustomer();
        }
        else
        {
            if (cargo.isExternalOrder())
            {
                // if this is an external order, use the customer from the last
                // transaction returned.
                List<SaleReturnTransactionIfc> list = cargo.getOriginalExternalOrderReturnTransactions();
                if (list != null)
                {
                    for(SaleReturnTransactionIfc trans: list)
                    {
                        if (trans.getCustomer() != null)
                        {
                            customer = trans.getCustomer();
                        }
                    }
                }
            }
            else
            {
                // if there is a customer associated with the origional transaction.
                SaleReturnTransactionIfc trans = cargo.getOriginalTransaction();
                if (trans != null && trans.getCustomer() != null)
                {
                    customer = trans.getCustomer();
                }
            }
        }

        return customer;
    }
}

