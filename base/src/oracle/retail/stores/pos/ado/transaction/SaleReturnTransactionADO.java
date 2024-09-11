/* ===========================================================================
* Copyright (c) 2001, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/SaleReturnTransactionADO.java /main/20 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    sgu       08/17/12 - refactor discount audit
 *    icole     12/13/11 - Reset evaluate tender limits to handle case of UNDO
 *                         from tender after a manager override.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    asinton   10/04/11 - prevent post voiding of transactions with authorized
 *                         tenders that lack necessary data for reversing.
 *    asinton   09/06/11 - remove ability to post void transactions with issue,
 *                         reload, redeem of gift cards.
 *    asinton   05/10/11 - refactor post void for gift cards for APF
 *    asinton   05/09/11 - Removed restrictions for voiding of gift cards
 *    aariyer   07/30/10 - For the OCC Screen display
 *    acadar    06/11/10 - changes for postvoid and signature capture
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    asinton   03/30/09 - Chenged method name to be more generic per review
 *                         comments.
 *    asinton   03/30/09 - Prevent post void of transaction with any gift card
 *                         tender.
 *    asinton   03/18/09 - Modified to show correct dialog.
 *    asinton   03/08/09 - Changes to disallow post void of gift card issue,
 *                         reload, and redeem.
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 * ===========================================================================
     $Log:
      6    360Commerce 1.5         3/31/2008 1:53:57 PM   Mathews Kochummen
           forward port from v12x to trunk
      5    360Commerce 1.4         4/25/2007 8:52:48 AM   Anda D. Cadar   I18N
           merge

      4    360Commerce 1.3         12/13/2005 4:42:34 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:29:49 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:00 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:01 PM  Robert Pearse
     $
     Revision 1.18.2.1  2004/11/15 22:27:35  bwf
     @scr 7671 Create tender from rdo instead of class.  This is necessary because ADO's are not 1:1 with RDOs.

     Revision 1.18  2004/08/05 21:12:51  blj
     @scr 6195 - corrected a problem with MSRModel data being lost for postvoids.

     Revision 1.17  2004/07/29 13:00:45  khassen
     @scr 5002 added another condition for calling the CCI screen.

     Revision 1.16  2004/07/28 22:21:58  lzhao
     @scr 6592: change for fit ISD.

     Revision 1.15  2004/07/24 03:31:34  blj
     @scr 5421 - unused imports

     Revision 1.14  2004/07/23 22:17:25  epd
     @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode

     Revision 1.13  2004/07/14 21:21:55  jriggins
     @scr 4401 Added logic to determine when to capture customer info during tender

     Revision 1.12  2004/07/13 22:41:12  epd
     @scr 5955 (ServicesImpact) Addressed complaints about logger, exceptions, etc

     Revision 1.11  2004/07/13 20:14:28  lzhao
     @scr 6144: returned gift card cannot be voided.

     Revision 1.10  2004/07/06 20:15:05  crain
     @scr 6004 System crashes when redeeming a gift certificate for Mail Bank Check

     Revision 1.9  2004/06/03 15:36:28  bwf
     @scr 5368 Fixed erik's unused imports.

     Revision 1.8  2004/06/03 14:47:44  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.7  2004/05/19 23:09:28  cdb
     @scr 5103 Updating to more correctly handle register reports.

     Revision 1.6  2004/05/16 20:54:18  blj
     @scr 4476 rework,postvoid and cleanup

     Revision 1.5  2004/04/19 15:57:08  tmorris
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.4  2004/02/27 02:27:37  crain
     @scr 3421: Tender redesign

     Revision 1.3  2004/02/25 18:32:52  bwf
     @scr 3883 Credit Rework.

     Revision 1.2  2004/02/12 16:47:57  mcs
     Forcing head revision

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.10   Feb 05 2004 13:22:50   rhafernik
 * log4j conversion
 *
 *    Rev 1.9   Feb 01 2004 13:42:42   bjosserand
 * Mail Bank Check.
 *
 *    Rev 1.8   Jan 22 2004 15:19:52   epd
 * updated void dialogs
 *
 *    Rev 1.7   Jan 21 2004 14:51:02   epd
 * Fixed altered gift card functionality
 *
 *    Rev 1.6   Jan 19 2004 18:49:56   crain
 * Added linkCustomer
 * Resolution for 3720: Wrong balance recalculated after removing customer discount for PO tender
 *
 *    Rev 1.5   Dec 16 2003 11:18:32   bwf
 * Use new createtender constructor per code review.
 *
 *    Rev 1.4   Dec 11 2003 19:04:36   Tim Fritz
 * Added the voidCheckForSuspendedTransaction() method to check to see if the transaction is suspended.
 * Resolution for 3500: Suspended transactions can be post voided.
 *
 *    Rev 1.3   Dec 11 2003 13:10:20   bwf
 * Updated for tenders that cant use class name.
 * Resolution for 3538: Mall Certificate Tender
 *
 *    Rev 1.2   Nov 20 2003 16:57:22   epd
 * updated to use new ADO Factory Complex
 *
 *    Rev 1.1   Nov 13 2003 17:03:10   epd
 * Refactoring: updated to use new method to access context
 *
 *    Rev 1.0   Nov 04 2003 11:14:36   epd
 * Initial revision.
 *
 *    Rev 1.1   Oct 29 2003 15:30:22   epd
 * Added method to see if transaction contains send items
 *
 *    Rev 1.0   Oct 17 2003 12:35:20   epd
 * Initial revision.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.ado.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.UpdateReturnedItemsCommandDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.DiscountableLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.TaxableLineItemIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupMallCertificateADO;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;

import org.apache.log4j.Logger;

/**
 * This transaction represents Sales/Returns/Exchanges.
 *
 */
@SuppressWarnings("serial")
public class SaleReturnTransactionADO extends AbstractRetailTransactionADO implements ReturnableTransactionADOIfc
{
    /** the logger */
    protected static final Logger logger = Logger.getLogger(SaleReturnTransactionADO.class);

    /** the performance logger */
    protected static final Logger perf = Logger.getLogger("PERF." + SaleReturnTransactionADO.class.getName());

    /**
     * Create the appropriate RDO transaction
     * @see oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO#instantiateTransactionRDO()
     */
    protected TransactionIfc instantiateTransactionRDO()
    {
        // Create the RDO transaction
        transactionRDO = DomainGateway.getFactory().getSaleReturnTransactionInstance();
        return transactionRDO;
    }

    /* (non-Javadoc)
     * This is overridden to initialize the transaction tax information.
     * @see oracle.retail.stores.ado.transaction.AbstractRetailTransactionADO#intializeTransactionRDO(oracle.retail.stores.domain.transaction.TransactionIfc, oracle.retail.stores.domain.customer.CustomerInfoIfc, oracle.retail.stores.domain.employee.EmployeeIfc, oracle.retail.stores.ado.store.RegisterADO)
     */
    protected void intializeTransactionRDO(
        TransactionIfc txnRDO,
        CustomerInfoIfc customerInfoRDO,
        EmployeeIfc operatorRDO,
        RegisterADO registerADO)
    {
        super.intializeTransactionRDO(txnRDO, customerInfoRDO, operatorRDO, registerADO);

        // set the transaction tax on our RDO transaction.
        BusIfc bus = TourContext.getInstance().getTourBus();
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        ((SaleReturnTransactionIfc) txnRDO).setTransactionTax(
            utility.getInitialTransactionTax());
    }

    /**
     * @see oracle.retail.stores.pos.ado.transaction.TransactionADOIfc#isVoidable()
     */
    public boolean isVoidable(String currentTillID) throws VoidException
    {
        // check if authorized tenders are not voidable due to missing account ID token
        voidCheckAuthorizedTenderMissingToken();

        // since 13.1 post void of transaction containing any gift card operation is not allowed,
        // except sale transactions using gift card tender.
        voidCheckForGiftCardOperation();

        // 1 Make sure the transaction has the same Till ID
        voidCheckForSameTill(currentTillID);

        // 2) Transaction should not already be voided
        voidCheckForPreviousVoid();

        // 3) Make sure any issued tenders have not been used.
        voidCheckForIssuedTenderModifications();

        // 4) Check for modified transaction
        voidCheckForModifiedTransaction();

        // 5) Check that void is allowed for tranasactions
        //    containing debit tenders
        voidCheckDebitAllowed();

        // 6) Make sure the transaction is not suspended
        voidCheckForSuspendedTransaction();

        // 7) make sure that the transaction does not contain an external order
        voidCheckForExternalOrder();

        return true;
    }

    /**
     * Makes sure there have been no returns against any line
     * items in this transaction. Applies to sale items only.
     * @throws VoidException Thrown when it is determined a return has been performed.
     */
    protected void voidCheckForModifiedTransaction() throws VoidException
    {
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_SALE)
        {
            voidCheckForReturnItems();
        }
    }

    /**
     * Voidable transactions must check that the transaction does not have an external order
     */
    protected void voidCheckForExternalOrder() throws VoidException
    {
       if(((SaleReturnTransactionIfc)transactionRDO).hasExternalOrder())
       {
           throw new VoidException("External Order", VoidErrorCodeEnum.INVALID_TRANSACTION);
       }
    }

    /**
     * Returns a list of items provided an ID number
     * @param itemID The desired item ID
     * @return An array of line items.
     */
    protected SaleReturnLineItemIfc[] getItemListByItemID(String itemID)
    {
        ArrayList<SaleReturnLineItemIfc> list = new ArrayList<SaleReturnLineItemIfc>(10);
        Iterator<AbstractTransactionLineItemIfc> iter = ((RetailTransactionIfc) transactionRDO).getLineItemsIterator();
        SaleReturnLineItemIfc item = null;

        while (iter.hasNext())
        {
            item = (SaleReturnLineItemIfc) iter.next();

            if (item.getItemID().equals(itemID))
            {
                list.add(item);
            }
        }

        // convert List to array
        SaleReturnLineItemIfc[] lineItemArray = new SaleReturnLineItemIfc[list.size()];
        list.toArray(lineItemArray);
        return lineItemArray;
    }

    /**
     * This method returns a boolean flag indicating whether or not
     * this transaction contains Send items.
     * @return boolean flag to indicate that this transaction contains send items.
     */
    public boolean containsSendItems()
    {
        boolean result = false;

        // loop through all the line items and return true if
        // any of them are send items
        AbstractTransactionLineItemIfc[] items = ((RetailTransactionIfc) transactionRDO).getLineItems();
        for (int i = 0; i < items.length; i++)
        {
            if (items[i] instanceof SaleReturnLineItemIfc && ((SaleReturnLineItemIfc) items[i]).getItemSendFlag())
            {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * This method returns a boolean flag indicating whether or not
     * this transaction contains return items.
     *
     * @return boolean flag to indicate that this transaction contains return items.
     */
    public boolean containsReturnItems()
    {
        boolean result = false;

        // loop through all the line items and return true if
        // any of them are return items
        AbstractTransactionLineItemIfc[] items = ((RetailTransactionIfc) transactionRDO).getLineItems();
        if (items != null)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] instanceof SaleReturnLineItemIfc && ((SaleReturnLineItemIfc) items[i]).isReturnLineItem())
                {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * This method returns a boolean value indicating whether or
     * not any of the return line items were NOT retrieved from
     * the database.  Used for the capture customer feature.
     * @return boolean to indicate that this transaction containts non-retrieved return items.
     */
    public boolean containsNonRetrievedReturnItems()
    {
        boolean result = false;

        // Loop through all the line items: if any of the line items are
        // return transactions that have NOT been retrieved from the database,
        // then return true.
        AbstractTransactionLineItemIfc[] items = ((RetailTransactionIfc) transactionRDO).getLineItems();
        if (items != null)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] instanceof SaleReturnLineItemIfc && ((SaleReturnLineItemIfc) items[i]).isReturnLineItem())
                {
                    SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) items[i];
                    ReturnItemIfc returnItem = lineItem.getReturnItem();
                    if (!returnItem.isFromRetrievedTransaction())
                    {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * This method returns a boolean flag indicating whether or not
     * the customer is present.
     *
     * @return boolean to indicate that this transaction is performed with customer present.
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#isCustomerPresent()
     */
    public boolean isCustomerPresent()
    {
        boolean result = false;
        SaleReturnTransactionIfc saleReturnTrans = (SaleReturnTransactionIfc) transactionRDO;
        result = saleReturnTrans.isCustomerPhysicallyPresent();
        return result;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#save(oracle.retail.stores.pos.ado.store.RegisterADO)
     */
    @Override
    public void save(RegisterADO registerADO)
    {
        // empty implementation
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO#updateForVoid()
     */
    @Override
    public void updateForVoid()
        throws DataException
    {
        AbstractTransactionLineItemIfc lineItemRDOs[] = getVoidLineItems();
        UpdateReturnedItemsCommandDataTransaction dbTrans = null;

        dbTrans = (UpdateReturnedItemsCommandDataTransaction) DataTransactionFactory.create(DataTransactionKeys.UPDATE_RETURNED_ITEMS_COMMAND_DATA_TRANSACTION);

        dbTrans.updateVoidedReturnedItems(lineItemRDOs);
    }

    /**
     * Clones the items from the original transaction and performs
     * some reversal of quantities and whatnot.
     * @return An array of void items.
     */
    protected AbstractTransactionLineItemIfc[] getVoidLineItems()
    {
        AbstractTransactionLineItemIfc[] lineItems = ((RetailTransactionIfc) transactionRDO).getLineItems();
        AbstractTransactionLineItemIfc[] voidItems = new AbstractTransactionLineItemIfc[lineItems.length];

        for (int i = 0; i < lineItems.length; i++)
        { // begin loop through
            // original transaction line items
            AbstractTransactionLineItemIfc newLineItem = (AbstractTransactionLineItemIfc) lineItems[i].clone();
            voidItems[i] = newLineItem;

            // negate the line itemTaxAmount
            if (voidItems[i] instanceof TaxableLineItemIfc)
            {
                TaxableLineItemIfc tli = (TaxableLineItemIfc) voidItems[i];
                tli.setItemTaxAmount(tli.getItemTaxAmount().negate());
            }

            // negate the quantity
            voidItems[i].modifyItemQuantity(voidItems[i].getItemQuantityDecimal().negate());

            // negate the line item discounts
            if (voidItems[i] instanceof DiscountableLineItemIfc)
            {
                DiscountableLineItemIfc dli = (DiscountableLineItemIfc) voidItems[i];
                ItemPriceIfc ip = dli.getItemPrice();
                // negate the discount amounts so that the extended discount price will calculate correctly
                ip.setItemTransactionDiscountAmount(ip.getItemTransactionDiscountAmount().negate());
                ip.setItemDiscountTotal(dli.getItemDiscountTotal().negate());
                setTransactionDiscounts(ip);
                ip.recalculateItemTotal();
            }
        } // end loop through

        return (voidItems);
    }

    /**
     * The pro-rated amount of the transaction discount amount must
     * be negated for each transaction discount.
     *
     * @param ip ItemPriceIfc object
     */
    protected void setTransactionDiscounts(ItemPriceIfc ip)
    { // begin setTransactionDiscounts()
        // pull transaction discounts
        ItemDiscountStrategyIfc[] discounts = ip.getItemDiscounts();
        int len = 0;
        // clear all discounts
        ip.clearItemDiscounts();
        ItemTransactionDiscountAuditIfc itda = null;
        ItemTransactionDiscountAuditIfc nitda = null;
        ReturnItemTransactionDiscountAuditIfc ritda = null;
        ReturnItemTransactionDiscountAuditIfc nritda = null;
        if (discounts != null)
        {
            len = discounts.length;
        }
        for (int i = 0; i < len; i++)
        {
            if (discounts[i] instanceof ItemTransactionDiscountAuditIfc)
            {
                itda = (ItemTransactionDiscountAuditIfc) discounts[i];
                // set new discount with value sign reversed
                nitda = DomainGateway.getFactory().getItemTransactionDiscountAuditInstance();

                nitda.initialize(
                    itda.getDiscountAmount().negate(),
                    itda.getReason(),
                    itda.getAssignmentBasis());
                nitda.setOriginalDiscountMethod(itda.getOriginalDiscountMethod());

                ip.addItemDiscount(nitda);
            }
            else
                if (discounts[i] instanceof ReturnItemTransactionDiscountAuditIfc)
                {
                    ritda = (ReturnItemTransactionDiscountAuditIfc) discounts[i];
                    nritda = DomainGateway.getFactory().getReturnItemTransactionDiscountAuditInstance();
                    nritda.initialize(
                        ritda.getDiscountAmount().negate(),
                        ritda.getReason());
                    nritda.setAssignmentBasis(ritda.getAssignmentBasis());
                    ip.addItemDiscount(nritda);
                }
                else
                {
                    ip.addItemDiscount(discounts[i]);
                }
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    @Override
    public Map getJournalMemento()
    {
        return super.getJournalMemento();
    }

    /**
     * We must look at the line items in this transaction to be sure.
     * @return boolean to indicate if transaction is return with receipt.
     * @see oracle.retail.stores.pos.ado.transaction.ReturnableTransactionADOIfc#isReturnWithReceipt()
     */
    public boolean isReturnWithReceipt()
    {
        boolean result = false;

        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_RETURN)
        {
            SaleReturnTransactionIfc saleReturnTrans = (SaleReturnTransactionIfc) transactionRDO;
            Vector lineItems = saleReturnTrans.getLineItemsVector();

            // Look for a receipt on each return item
            for (int i = 0; i < lineItems.size(); i++)
            {
                SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) lineItems.elementAt(i);
                ReturnItemIfc item = lineItem.getReturnItem();

                // Verify there was a return item
                if (item != null)
                {
                    TransactionIDIfc originalID = item.getOriginalTransactionID();

                    // If the original ID is non-empty, something
                    // was entered for the receipt number
                    if (originalID != null && !originalID.getTransactionIDString().equals(""))
                    {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }
    /**
     * If the original transaction of a return is successfully
     * retrieved, return true; otherwise return false.
     * @return boolean to indicate that transaction is return with original retrieved transaction.
     */
    public boolean isReturnWithOriginalRetrieved()
    {
        boolean result = false;

        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_RETURN)
        {
            result = hasBeenRetrieved();
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    @Override
    public void fromLegacy(EYSDomainIfc rdo)
    {
        //assert(rdo instanceof SaleReturnTransaction);
        transactionRDO = (SaleReturnTransaction) rdo;


        // get and convert RDO tenders
        Iterator<TenderLineItemIfc> iter = ((TenderableTransactionIfc) transactionRDO).getTenderLineItemsVector().iterator();
        while (iter.hasNext())
        {
            // Create ADO tender from RDO tender
            TenderLineItemIfc tenderRDO = (TenderLineItemIfc) iter.next();
            TenderTypeEnum type = TenderTypeEnum.makeEnumFromString(tenderRDO.getTypeDescriptorString());
            TenderFactoryIfc factory;
            try
            {
                factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
                TenderADOIfc tenderADO;
                if (type != null)
                { // if using hash map
                    tenderADO = factory.createTender(type);
                }
                else
                {
                    tenderADO = factory.createTender(tenderRDO);
                }
                ((ADO) tenderADO).fromLegacy(tenderRDO);

                // add the tender to the transaction
                addTenderNoValidation(tenderADO);
            }
            catch (ADOException e)
            {
                logger.error(e.getMessage(), e);
                String message = "No exception should occur here.  " +
                                 "Please correct the source of the problem.";
                assert(false) : message;
            }
        }
    }

    /**
     * This method updates the transaction totals after linking a customer.
     * @param customer CustomerIfc
     */
    public void linkCustomer(CustomerIfc customer)
    {
        super.linkCustomer(customer);
        ((SaleReturnTransactionIfc) transactionRDO).updateTransactionTotals();
    }

    /**
     * This method retrieves the tax mode for the transaction.
     * @return int the transaction tax status.
     */
    public int getTransactionTaxStatus()
    {
        return ((SaleReturnTransactionIfc) transactionRDO).getTransactionTax().getTaxMode();
    }

    /**
     * This method returns true if PAT Cash Tender criteria are met
     * @return true if PAT Cash Tender criteria are met
     */
    public boolean isPATCashTransaction()
    {
        if (perf.isInfoEnabled())
        {
            perf.info("Entering isPATCashTransaction");
        }
        boolean isPATCashTender = false;

        UtilityIfc util = getUtility();
        if ("Y".equals(util.getParameterValue("PATCustomerInformation", "Y")))
        {
            if (getBalanceDue().signum() != CurrencyIfc.POSITIVE)
            {
                CurrencyIfc patCashThreshold = DomainGateway.getBaseCurrencyInstance(PAT_CASH_THRESHOLD);
                if (patCashThreshold.compareTo(((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getGrandTotal())
                                            == CurrencyIfc.LESS_THAN)
                {
                    CurrencyIfc patCashTotal = DomainGateway.getBaseCurrencyInstance();
                    HashMap newTenders = getPATCashTenders();
                    for (Iterator i = newTenders.keySet().iterator(); i.hasNext();)
                    {
                        Object key = i.next();
                        patCashTotal = patCashTotal.add(((PaymentHistoryInfoIfc)newTenders.get(key)).getTenderAmount());
                    }
                    isPATCashTender = patCashThreshold.compareTo(patCashTotal) == CurrencyIfc.LESS_THAN;
                    if (logger.isDebugEnabled())
                    {
                        if (isPATCashTender)
                        {
                            logger.debug("Not PAT Cash Transaction as PAT Cash amount is less than PAT Cash Threshold: " + patCashTotal);
                        }
                        else
                        {
                            logger.debug("This is a PAT Cash Transaction");
                        }
                    }
                }
                else if (logger.isDebugEnabled())
                {
                    logger.debug("Not PAT Cash Transaction as grand total is less than PAT Cash Threshold: " + ((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getGrandTotal());
                }
            }
            else if (logger.isDebugEnabled())
            {
                logger.debug("Not PAT Cash Transaction (yet) as balance due is not positive: " + getBalanceDue());
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Not PAT Cash Transaction as PATCustomerInformation parameter is 'N'");
        }

        if (perf.isInfoEnabled())
        {
            perf.info("Exiting isPATCashTransaction");
        }
        return isPATCashTender;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.ADOIfc#toLegacy()
     */
    @Override
    public EYSDomainIfc toLegacy()
    {
        return transactionRDO;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    @Override
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }
    /**
     * Reset so the tender limits will be evaluated.
     */
    public void resetEvaluateTenderLimits(TenderTypeEnum tenderType)
    {
        TenderGroupADOIfc tenderADO = getTenderGroup(tenderType);
        if(tenderADO instanceof TenderGroupMallCertificateADO)
        {
            TenderGroupMallCertificateADO ado = (TenderGroupMallCertificateADO) tenderADO;
            ado.resetEvaluateTenderLimits();
        }

    }

}
