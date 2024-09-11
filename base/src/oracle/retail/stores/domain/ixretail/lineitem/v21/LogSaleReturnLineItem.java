/* ===========================================================================
* Copyright (c) 2002, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/lineitem/v21/LogSaleReturnLineItem.java /main/36 2014/05/16 17:19:40 yiqzhao Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzha 05/16/14 - Handle order line item even when it is return line
 *                      item.
 *    tkshar 12/10/12 - commons-lang update 3.1
 *    mjwall 05/17/12 - Fortify: fix redundant null checks, part 6
 *    sgu    05/16/12 - check in changes after merge
 *    sgu    05/15/12 - remove column LN_ITM_REF from order line item tables
 *    yiqzha 05/14/12 - remove shipping charge from CustomerOrderForDelivery
 *    yiqzha 05/14/12 - add shipping charge
 *    yiqzha 05/10/12 - add shipping charge as a <LineItem> in POSLog XML
 *    yiqzha 04/16/12 - refactor store send from transaction totals
 *    yiqzha 04/03/12 - refactor store send for cross channel
 *    rabhaw 03/20/12 - added item condition in pos log
 *    cgreen 07/07/11 - convert entryMethod to an enum
 *    jswan  08/26/10 - Change due to merge to tip.
 *    mchell 08/26/10 - BUG#9975539 ItemLink not captured for returns
 *    jswan  08/25/10 - Fixed issues returning a transaction with a transaction
 *                      discount and non discountable items. Also refactored
 *                      the creation of PLUItems to remove extraneous data
 *                      element from the SaleReturnLineItem table.
 *    rsnaya 08/13/10 - Pos Log unknown item fix
 *    mchell 08/10/10 - BUG#9975539 ItemLink not captured for returns
 *    jkoppo 08/03/10 - Fixes for BUG#9955722, added 'NULL' checks
 *    acadar 06/16/10 - external order changes for poslog export
 *    jswan  06/01/10 - Fixed merge issues
 *    jswan  06/01/10 - Modified to support transaction retrieval performance
 *                      and data requirements improvements.
 *    jswan  05/28/10 - XbranchMerge jswan_hpqc-techissues-73 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    rrkohl 04/26/10 - POS: ORIGINAL STORE NUMBER FOR RETURN WO RECEIPT NOT
 *                      CORRECTLY POPULATED IN POSLOG/CO
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abhayg 02/25/10 - For Pos log issue
 *    abonda 01/03/10 - update header date
 *    ranojh 10/31/08 - Refreshed View and Merged changes with Reason Codes
 *    ranojh 10/29/08 - Fixed ReturnItem
 *    acadar 10/29/08 - merged to tip
 *    ddbake 10/28/08 - Update for merge
 *    acadar 10/27/08 - fix broken unittests
 *    acadar 10/25/08 - localization of price override reason codes
 *
 * ===========================================================================
     $Log:
      19   360Commerce 1.18        8/9/2007 5:50:35 PM    Brett J. Larsen CR
           28020 - can't import pos logs w/ mutiple items w/ tax mods

           backing out previous change - the tax-modifier-sequence-number
           should always be 0 since only 1 tax modifier per line item is
           supported

      18   360Commerce 1.17        8/6/2007 5:43:03 PM    Brett J. Larsen CR
           28020 - the actual line item sequence number was hard-coded to "0"
           - this mean when a transaction includes multiple tax mods, a
           duplicate key error occurs - since the seq. number is part of the
           key

           modified code to use the line item's seq num

           this does assume the item seq # and the mod seq # are equal - this
           appears to be the case base on what is saved to the database

      17   360Commerce 1.16        7/11/2007 9:30:40 AM   Alan N. Sinton  CR
           27598 - Made default value for 'amount' to be null - logic on
           outputting the Amount element is based on it being not null.  It is
            set to not null in the case where discount is by amount.
      16   360Commerce 1.15        6/5/2007 2:04:43 PM    Ranjan Ojha     Code
           Review updates to POSLog for VAT
      15   360Commerce 1.14        6/4/2007 1:44:23 PM    Sandy Gu
           rework based on review comments
      14   360Commerce 1.13        5/22/2007 9:14:25 AM   Sandy Gu        Check
            in PosLog enhancement for VAT
      13   360Commerce 1.12        5/1/2007 12:16:12 PM   Brett J. Larsen CR
           26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
           feature)
      12   360Commerce 1.11        4/25/2007 10:00:49 AM  Anda D. Cadar   I18N
           merge
      11   360Commerce 1.10        4/9/2007 4:28:56 PM    Ashok.Mondal    CR
           4069 - v7.2.2 merge to trunk. Product extensibility issues for
           circuit city POSLog.

      10   360Commerce 1.9         8/9/2006 11:03:18 AM   Christian Greene
           CR3860 - Comment out code block to creates a <TransactionLink> for
           returns without receipts.
      9    360Commerce 1.8         4/27/2006 7:29:46 PM   Brett J. Larsen CR
           17307 - remove inventory functionality - stage 2
      8    360Commerce 1.7         2/3/2006 12:49:17 PM   Deepanshu       CR
           8257: Set item description for pos log generation
      7    360Commerce 1.6         2/2/2006 2:25:53 PM    Jason L. DeLeau 7383:
            Merge to trunk
      6    360Commerce 1.5         1/25/2006 4:11:29 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      5    360Commerce 1.4         1/22/2006 11:41:35 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      4    360Commerce 1.3         12/13/2005 4:43:48 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 3:28:56 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:16 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:26 PM  Robert Pearse
     $: LogSaleReturnLineItem.java,v $
      5    .v710     1.2.2.0     9/21/2005 13:39:52     Brendan W. Farrell
           Initial Check in merge 67.
      4    .v700     1.2.3.0     12/7/2005 15:21:38     Jason L. DeLeau 7383:
           Fix problems with POSLog for Tax
      3    360Commerce1.2         3/31/2005 15:28:56     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:23:16     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:26     Robert Pearse
     $
     Revision 1.9.2.6  2005/01/21 22:41:14  jdeleau
     @scr 7888 merge Branch poslogconf into v700

     Revision 1.9.2.5.2.1  2005/01/20 16:37:23  jdeleau
     @scr 7888 Various POSLog fixes from mwright

     Revision 1.9.2.5  2004/11/29 05:07:12  mwright
     Use new tax type element, check for unique ID starting with "Rev"

     Revision 1.9.2.4  2004/11/19 00:16:33  lzhao
     @scr 7423: use taxType in TaxType tag directly.

     Revision 1.9.2.3  2004/11/11 22:31:48  mwright
     Merge from top of tree

     Revision 1.12  2004/11/11 11:09:43  mwright
     Add inventory state and location

     Revision 1.11  2004/10/28 01:41:22  mwright
     Modified to use new sale of gift card line item

     Revision 1.10  2004/10/21 21:38:08  mwright
     Updated to work with fixes to transaction level tax override in domain

     Revision 1.9  2004/08/16 00:07:45  mwright
     Cope with "Reverse Item" prepended to unique ID

     Revision 1.8  2004/08/10 07:17:12  mwright
     Merge (3) with top of tree


     Revision 1.7  2004/07/19 15:40:45  mweis
     @scr 6342 createAlterationElements() method now protected instead of private


     Revision 1.6.2.6  2004/08/09 12:42:52  mwright
     Remove occasional "Ret-" prepended to return line item unique identifiers

     Revision 1.6.2.5  2004/08/08 21:21:52  mwright
     Set tax line item unique identifier from first entry in info container.
     This is a workaround until the tax item has an accessor function

     Revision 1.6.2.4  2004/08/06 02:28:09  mwright
     Added item size code
     Added tax line unique identifier
     Created workaround for lost exempt tax information

     Revision 1.6.2.3  2004/08/01 22:38:46  mwright
     Set item size code, discount employee and damage flag

     Revision 1.6.2.2  2004/07/29 01:07:57  mwright
     Added transaction to interface parameter list
     Added new shipping details to line item if send flag is set

     Revision 1.6.2.1  2004/07/09 04:10:58  mwright
     Added isOrderLine() method to determine if srli is an order line (domain method does not work)
     Added item reference for order line items

     Revision 1.6  2004/06/30 08:09:42  mwright
     Changes introduced due to new tax engine

     Revision 1.5  2004/06/24 09:15:11  mwright
     POSLog v2.1 (second) merge with top of tree

     Revision 1.4.2.4  2004/06/23 00:24:23  mwright
     Fixed derivation of restocking fee
     Item link should contain store ID, which is not available in this logger, so it is left out
     Ensure all fields in transaction link (for return) are completed

     Revision 1.4.2.3  2004/06/15 06:30:44  mwright
     Changed tax rule on total tax element to "Total"

     Revision 1.4.2.2  2004/06/10 10:50:55  mwright
     Updated to use schema types in commerce services

     Revision 1.4.2.1  2004/05/21 01:26:26  mwright
     Added delivery order element item reference

     Revision 1.4  2004/05/06 03:33:07  mwright
     POSLog v2.1 merge with top of tree

     Revision 1.1.2.7  2004/04/26 21:56:28  mwright
     Changed from RetailTransactionPriceDerivationRule to 360 own implmentation of RetailTransactionPriceDerivationRule360, because ixretail element is not extensible
     Fixed decimal places on percentages
     Problem with restocking fee: domain object returns null when it should have a restocking fee, so we always insert zero.

     Revision 1.1.2.6  2004/04/19 07:21:27  mwright
     Updated with fixes from unit testing

     Revision 1.1.2.5  2004/04/13 06:51:20  mwright
     Removed tabs
     Ready for testing

     Revision 1.1.2.4  2004/03/28 10:32:35  mwright
     Refactored TransactionLink to POSLogTransactionLink

     Revision 1.1.2.3  2004/03/21 14:30:53  mwright
     Weekly checkin of work in progress

     Revision 1.1.2.2  2004/03/18 02:23:45  mwright
     Work in progress: Initial effort to get all the data neded to build the XML.

     Revision 1.1.2.1  2004/03/17 04:13:49  mwright
     Initial revision for POSLog v2.1

 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.ixretail.lineitem.v21;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogAlterationIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogAmountIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogMerchandiseHierarchyIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogOperatorIDIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogQuantityIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTransactionLinkIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogUnitPriceIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailPriceModifierIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionAssociateIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionCustomerOrderForDeliveryIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionDelivery360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionItemIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionKitIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionLineItemIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionPOSIdentityIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionPriceDerivationRule360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionReturnIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionSaleForDeliveryIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionSaleIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTaxIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTaxModifierIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTaxOverrideIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.SaleOfGiftCard360Ifc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsV21Ifc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.discount.LogDiscountLineItemIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogOrderItemIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogSaleReturnLineItemIfc;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogTransaction;
import oracle.retail.stores.domain.ixretail.utility.LogGiftCardIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.domain.utility.AlterationIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class creates the elements for a SaleReturnLineItem
 */
public class LogSaleReturnLineItem extends LogLineItem implements LogSaleReturnLineItemIfc, IXRetailConstantsV21Ifc
{
    /**
     * Set to the owning transaction when the public interface is called.
     */
    protected TransactionIfc transaction;

    /**
     * This boolean contains state which indicates whether the current line item
     * was voided/deleted from the transaction.
     */
    protected boolean voidFlag = false;

    /**
     * Constructs LogSaleReturnLineItem object.
     */
    public LogSaleReturnLineItem()
    {
    }

    /**
     * Updates the data collection element with line item data.
     *
     * @param srli SaleReturnLineItem
     * @param doc parent document
     * @param el Actually a RetailTransactionLineItemIfc object, that collects
     *            data
     * @param voidFlag flag indicating line has been voided
     * @param sequenceNumber sequence number
     * @return updated RetailTransactionLineItemIfc object (its a reference
     *         anyway)
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(SaleReturnLineItemIfc srli, TransactionIfc transaction, Document doc, Element el,
            boolean voidFlag, int sequenceNumber) throws XMLConversionException
    {
        // Save void/delete state.
        this.voidFlag = voidFlag;

        RetailTransactionLineItemIfc element = (RetailTransactionLineItemIfc)el;
        this.transaction = transaction;

        // set up the sequence number and void flag fields:
        createElement(doc, element, null, voidFlag, sequenceNumber);

        element.setEntryMethod(srli.getEntryMethod().getIxRetailDescriptor());

        createItemElements(srli, element); // was itemElement, we need to pass
                                           // on the element data collector

        return el;
    }

    /**
     * Updates the data collection element with line item data.
     *
     * @param srli SaleReturnLineItem
     * @param doc parent document
     * @param el Actually a RetailTransactionLineItemIfc object, that collects
     *            data
     * @param sequenceNumber sequence number
     * @return updated RetailTransactionLineItemIfc object (its a reference
     *         anyway)
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(SaleReturnLineItemIfc srli, TransactionIfc transaction, Document doc, Element el,
            int sequenceNumber) throws XMLConversionException
    {
        return (createElement(srli, transaction, doc, el, false, sequenceNumber));
    }

    // new developments: shp_rds_sls_rtn may have multiple lines for multiple
    // send items, so we need to set the delivery element in the line item
    protected RetailTransactionDelivery360Ifc addShippingDetails(SaleReturnLineItemIfc srli)
            throws XMLConversionException
    {
        RetailTransactionDelivery360Ifc deliveryElement = null;
        int sendLabelCount = srli.getSendLabelCount();
        SendPackageLineItemIfc[] sendPackages = null;
        if (transaction instanceof VoidTransaction)
        {
            sendPackages = ((VoidTransaction)transaction).getSendPackages();
        }
        else
        {
            SaleReturnTransactionIfc retailTrans = (SaleReturnTransactionIfc)transaction;
            sendPackages = retailTrans.getSendPackages();
        }

        if (sendPackages != null)
        {
            boolean isExternalSend = false;
            ShippingMethodIfc method = null;
            CustomerIfc customer = null;
            //ItemTaxIfc itemTax = null;
            for (int i = 0; i < sendPackages.length; i++)
            {
                ShippingMethodIfc testMethod = sendPackages[i].getShippingMethod();
                //itemTax = sendPackages[i].getItemTax();
                // backout of Kintore change to shipping method interface and
                // Jdbc reader: we no longer have access to the shipping method
                // label
                // This relies on the 1-based shipping method position in the
                // vector being the same as the line item shipping label:
                if (sendLabelCount == i + 1)
                {
                    method = testMethod;
                    customer = sendPackages[i].getCustomer();
                    isExternalSend = sendPackages[i].isExternalSend();

                }
            }
            deliveryElement = setDeliveryDetails(method, customer, Integer.toString(sendLabelCount));
            
            if (deliveryElement != null)
            {
                deliveryElement.setExternalShippingFlag(isExternalSend);
            }

        }

        return deliveryElement;
    }

    protected ItemTaxIfc getShippingChargeItemTax(int sendLabelCount)
    {
    	if ( transaction instanceof SaleReturnTransactionIfc)
    	{
	    	AbstractTransactionLineItemIfc lineItems[] = ((SaleReturnTransactionIfc)transaction).getLineItems();
	    	 ((SaleReturnTransactionIfc)transaction).getSendPackageCount();
	    	for (int i=0; i<lineItems.length; i++)
	    	{
	    		if (lineItems[i] instanceof SaleReturnLineItemIfc)
	    		{
	    			SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems[i];
	    			if (lineItem.isShippingCharge())
	    			{
	    				if (lineItem.getSendLabelCount()==sendLabelCount)
	    				{
	    					return lineItem.getItemTax();
	    				}
	    			}
	    		}

	    	}
    	}
    	return null;
    }
    /**
     * Creates elements for item.
     *
     * @param srli sale return line item
     * @param el Actually a RetailTransactionLineItemIfc object, that collects
     *            data
     * @exception XMLConversionException thrown if error occurs.
     */
    protected void createItemElements(SaleReturnLineItemIfc srli, RetailTransactionLineItemIfc lineItemElement)
            throws XMLConversionException
    {
        // We always create an itemElement. It is set in whichever of the sale
        // elements we create (sale, return, order etc).
        RetailTransactionItemIfc itemElement = getSchemaTypesFactory().getRetailTransactionItemInstance();

        String sizeCode = srli.getItemSizeCode();
        boolean isGiftCardSale = false;
        boolean relatedItemReturnFlag = srli.isRelatedItemReturnable();
        int relatedItemSeqNum = srli.getRelatedItemSequenceNumber();       
        // check for return
        if (srli.isReturnLineItem())
        {
            RetailTransactionReturnIfc returnElement = getSchemaTypesFactory().getRetailTransactionReturnInstance();
            lineItemElement.setReturn(returnElement);
            returnElement.setRetailtransactionItem(itemElement);

            // if return, create disposal and original transaction elements
            createReturnItemElements(srli, itemElement, returnElement);
            returnElement.setSizeCode(sizeCode);

            if (srli.getPLUItem() instanceof GiftCardPLUItemIfc)
            {
                isGiftCardSale = true;
                SaleOfGiftCard360Ifc giftElement = getSchemaTypesFactory().getSaleOfGiftCard360Instance();
                LogGiftCardIfc logGiftCard = IXRetailGateway.getFactory().getLogGiftCardInstance();
                logGiftCard.createGiftCardNumberElement(((GiftCardPLUItemIfc)srli.getPLUItem()).getGiftCard(), null,
                        parentDocument, giftElement);
                giftElement.setRetailtransactionItem(itemElement);
                returnElement.setGiftCard(giftElement.getGiftCard());
            }

        }
        // check for gift card
        // The this line item is a voided gift card, do not treat it as a gift
        // card; it has not been
        // activated and therefore, has no "tender part" to be logged.
        else if (srli.getPLUItem() instanceof GiftCardPLUItemIfc && !voidFlag)
        {
            isGiftCardSale = true;
            SaleOfGiftCard360Ifc giftElement = getSchemaTypesFactory().getSaleOfGiftCard360Instance();
            LogGiftCardIfc logGiftCard = IXRetailGateway.getFactory().getLogGiftCardInstance();
            logGiftCard.createGiftCardNumberElement(((GiftCardPLUItemIfc)srli.getPLUItem()).getGiftCard(), null,
                    parentDocument, giftElement);
            giftElement.setRetailtransactionItem(itemElement);
            lineItemElement.setSaleOfGiftCard(giftElement);
        }
        // check for send item
        else if (srli.getItemSendFlag() || srli.isShippingCharge())
        {
            RetailTransactionSaleForDeliveryIfc deliveryElement = getSchemaTypesFactory()
                    .getRetailTransactionSaleForDeliveryInstance();
            lineItemElement.setSaleForDelivery(deliveryElement);
            deliveryElement.setItem(itemElement);

            // add array of RetailTransactionModificationIfc
            if (srli.isAlterationItem())
            {
                deliveryElement.setAlteration(createAlterationElements(srli));
            }
            RetailTransactionDelivery360Ifc deliveryLineElement = addShippingDetails(srli);

            deliveryElement.setDelivery(deliveryLineElement);
            deliveryElement.setSizeCode(sizeCode);
            deliveryElement.setRelatedItemReturnFlag(relatedItemReturnFlag);
            deliveryElement.setRelatedItemSeqNum(relatedItemSeqNum);

            if (srli.isShippingCharge())
            {
            	deliveryElement.setShippingChargeFlag(true);
            }
        }
        else
        // plain sale
        {
            RetailTransactionSaleIfc saleElement = getSchemaTypesFactory().getRetailTransactionSaleInstance();
            lineItemElement.setSale(saleElement);
            saleElement.setLineItem(itemElement);

            // add array of RetailTransactionModificationIfc
            if (srli.isAlterationItem())
            {
                saleElement.setAlteration(createAlterationElements(srli));
            }
            saleElement.setSizeCode(sizeCode);

            saleElement.setRelatedItemReturnFlag(relatedItemReturnFlag);
            saleElement.setRelatedItemSeqNum(relatedItemSeqNum);
        }

        if (srli.isOrderItem())
        {
            // Note: The sale return line item table has a field for the send
            // label. This must be zero for non-order line items,
            // so we don't export the zero value for non-order lines. The import
            // code must infer the zero. (task 603)
            RetailTransactionCustomerOrderForDeliveryIfc deliveryOrderElement = getSchemaTypesFactory()
                    .getRetailTransactionCustomerOrderForDeliveryInstance();
            lineItemElement.setCustomerOrderForDelivery(deliveryOrderElement);
            deliveryOrderElement.setItem(itemElement);
            // add order item elements
            deliveryOrderElement.setItemReference(Integer.toString(srli.getOrderLineReference()));
            LogOrderItemIfc logOrderLine = IXRetailGateway.getFactory().getLogOrderItemInstance();
            logOrderLine.createElement(srli.getOrderItemStatus(), parentDocument, deliveryOrderElement);

            if (deliveryOrderElement.getItemReference() == null)
            {
                deliveryOrderElement.setItemReference(Integer.toString(srli.getOrderLineReference()));
            }
            deliveryOrderElement.setSizeCode(sizeCode);
        }
        
        RetailTransactionPOSIdentityIfc identityElement = getSchemaTypesFactory()
                .getRetailTransactionPOSIdentityInstance();
        identityElement.setPOSIDType(ELEMENT_POS_IDENTITY);
        identityElement.setPOSItemID(srli.getPosItemID());
        // no qualifier in v1.0 code...it is optional in v2.1
        itemElement.setPOSIdentity(identityElement);

        itemElement.setItemID(srli.getPLUItemID());
        String description = srli.getPLUItem().getDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
        if (description == null)
        {
            description = srli.getReceiptDescription();
        }
        itemElement.setDescription(description);
        CurrencyIfc unitListPrice = srli.getItemPrice().getSellingPrice();

        // set list price based on override code (this may reflect a defect)
        if (!srli.isKitHeader()) // CR29357 - Kit unit, extended and
                                 // extendedDiscount should be zero.
        {
            // if no override, use item price
            if (srli.getItemPrice().getItemPriceOverrideReason().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
            {
                ItemPriceIfc price = srli.getItemPrice();
                unitListPrice = price.getSellingPrice();
            }
        }
        POSLogAmountIfc amountElement = getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                currency(unitListPrice));
        POSLogUnitPriceIfc unitPriceElement = getSchemaTypesFactory().getPOSLogUnitPriceInstance().initialize(
                amountElement);
        // Note that we are leaving the optional fields empty...should search
        // for the default data to go in
        itemElement.setUnitListPrice(unitPriceElement);

        // extended amount
        CurrencyIfc extendedAmount = DomainGateway.getBaseCurrencyInstance();
        if (!srli.isKitHeader()) // CR29357 - Kit unit, extended and
                                 // extendedDiscount should be zero.
        {
            extendedAmount = srli.getItemPrice().getExtendedSellingPrice();
        }

        POSLogAmountIfc extendedAmountElement = getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                currency(extendedAmount));
        itemElement.setExtendedAmount(extendedAmountElement);

        createQuantityElement(srli, itemElement);
        createAssociateElement(srli, itemElement);

        /*
         * Item link is used to pass the store ID to the POSLog for return
         */
        if (srli.isReturnLineItem() && srli.getReturnItem() != null)
        {
            String itemLink = srli.getReturnItem().getStore().getStoreID();

            if (Util.isEmpty(itemLink))
            {
                itemLink = transaction.getFormattedStoreID();
            }
            itemElement.setItemLink(new String[] { itemLink });
        }

        createRetailPriceModifierElements(srli, itemElement);

        if (!isGiftCardSale)
        {
            createRetailTransactionTaxElements(srli, itemElement);
            createRetailTransactionTaxModifierElement(srli, itemElement);
        }

        if (!Util.isEmpty(srli.getItemSerial()))
        {
            itemElement.setSerialNumber(new String[] { srli.getItemSerial() });
        }

        POSLogMerchandiseHierarchyIfc merc = getSchemaTypesFactory().getPOSLogMerchandiseHierarchyInstance();
        merc.setLevel("POSDepartment");
        merc.setText(srli.getPLUItem().getDepartmentID());
        itemElement.setMerchandiseHierarchy(merc);
        itemElement.setItemType(getItemType(srli.getPLUItem()));

        // denote item not on file, as needed
        if (srli.getPLUItem() instanceof UnknownItemIfc || srli.getPLUItem().getItemClassification().getItemType() == 0)
        {
            itemElement.setItemNotOnFileFlag(new Boolean(true));
        }

        // v2.1 change: we only add a kit header here (if this line item is a
        // kit header)
        // The kit member line items are created (by this class) and are
        // inserted into the kit element by the caller.
        createKitElements(srli, itemElement);

        if (srli.getRegistry() != null && !Util.isEmpty(srli.getRegistry().getID()))
        {
            itemElement.setRegistryID(srli.getRegistry().getID());
        }

        // extended discount amount
        CurrencyIfc extendedDiscountAmount = DomainGateway.getBaseCurrencyInstance();
        if (!srli.isKitHeader()) // CR29357 - Kit unit, extended and
                                 // extendedDiscount should be zero.
        {
            extendedDiscountAmount = srli.getItemPrice().getExtendedDiscountedSellingPrice();
        }
        POSLogAmountIfc extendedDiscountElement = getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                currency(extendedDiscountAmount));
        itemElement.setExtendedDiscountAmount(extendedDiscountElement);

        // get gift receipt, if necessary
        if (srli.isGiftReceiptItem())
        {
            itemElement.setGiftReceiptFlag(new Boolean(true));
        }

        // if external order item

        if (StringUtils.isNotEmpty(srli.getExternalOrderItemID()))
        {
            lineItemElement.setExternalOrderLineItemID(srli.getExternalOrderItemID());
            lineItemElement.setParentExternalOrderLineItemID(srli.getExternalOrderParentItemID());
            lineItemElement.setExternalPricingFlag(srli.hasExternalPricing());
        }

    }

    /**
     * Translates 360 item classification type to IXRetail-compliant type.
     *
     * @param pluItem PLUItemIfc object
     * @return item type attribute value
     */
    protected String getItemType(PLUItemIfc pluItem)
    {
        String itemTypeString = ATTRIBUTE_VALUE_STOCK;
        int itemType = pluItem.getItem().getItemClassification().getItemType();
        if (itemType == ItemClassificationConstantsIfc.TYPE_SERVICE)
        {
            itemTypeString = ATTRIBUTE_VALUE_SERVICE;
        }

        return itemTypeString;
    }

    /**
     * Creates Quantity element.
     *
     * @param srli SaleReturnLineItemIfc object
     * @param el line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createQuantityElement(SaleReturnLineItemIfc srli, RetailTransactionItemIfc el)
            throws XMLConversionException
    {

        POSLogQuantityIfc quantityElement = getSchemaTypesFactory().getPOSLogQuantityInstance();

        BigDecimal quantity = srli.getItemQuantityDecimal();

        LogTransaction.setupQuantityElement(quantityElement, quantity, srli.getPLUItem().getUnitOfMeasure());

        el.setQuantity(quantityElement);

    }

    /**
     * Creates RetailTransactionAssociate element.
     *
     * @param srli SaleReturnLineItemIfc object
     * @param el sale return line item element
     * @exception XMLConversionException thrown if error occurs translating to
     *                XML
     */
    protected void createAssociateElement(SaleReturnLineItemIfc srli, RetailTransactionItemIfc el)
            throws XMLConversionException
    {

        String associateID = null;
        if (srli.getSalesAssociate() != null)
        {
            associateID = srli.getSalesAssociate().getEmployeeID();
        }

        if (!Util.isEmpty(associateID))
        {
            RetailTransactionAssociateIfc associate = getSchemaTypesFactory().getRetailTransactionAssociateInstance();
            POSLogOperatorIDIfc operator = getSchemaTypesFactory().getPOSLogOperatorIDInstance();
            operator.setOperatorID(associateID);
            associate.setAssociateID(operator);
            if (srli.getSalesAssociateModifiedFlag())
            {
                associate.setCommissionOverrideFlag(new Boolean(true));
            }
            el.setAssociate(new RetailTransactionAssociateIfc[] { associate });
        }
    }

    /**
     * Creates RetailPriceModifier elements and adds array of them to
     * RetailTransactionItem.
     *
     * @param srli SaleReturnLineItemIfc object
     * @param el RetailTransactionItemIfc element to receive array of price
     *            modifiers
     * @exception XMLConversionException thrown if error occurs translating to
     *                XML
     */
    protected void createRetailPriceModifierElements(SaleReturnLineItemIfc srli, RetailTransactionItemIfc el)
            throws XMLConversionException
    {
        List<RetailPriceModifierIfc> modifierList = new ArrayList<RetailPriceModifierIfc>();

        int discountSequenceNumber = 0;

        // check for price override
        if (!srli.getItemPrice().getItemPriceOverrideReason().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
        {

            RetailPriceModifierIfc modifierElement = getSchemaTypesFactory().getRetailPriceModifierInstance();
            modifierElement.setSequenceNumber(Integer.toString(discountSequenceNumber));
            createPriceOverrideElements(modifierElement, srli);
            modifierList.add(modifierElement);
            ++discountSequenceNumber;
        }

        ItemDiscountStrategyIfc[] modifiers = srli.getItemPrice().getItemDiscounts();
        ItemDiscountStrategyIfc discountLineItem = null;

        // get number of discounts for loop
        int numDiscounts = 0;
        if (modifiers != null)
        {
            numDiscounts = modifiers.length;
        }

        // loop through line items
        for (int i = 0; i < numDiscounts; i++)
        {
            discountLineItem = modifiers[i];

            // skip transaction discounts
            int scope = discountLineItem.getDiscountScope();
            if (scope != DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION)
            {
                RetailPriceModifierIfc modifierElement = getSchemaTypesFactory().getRetailPriceModifierInstance();
                modifierElement.setSequenceNumber(Integer.toString(discountSequenceNumber));
                createItemDiscountElements(modifierElement, discountLineItem, srli);
                String employee = null;
                boolean damageFlag = false;
                employee = discountLineItem.getDiscountEmployeeID();
                if (discountLineItem.isDamageDiscount())
                {
                    damageFlag = true;
                }
                modifierElement.setDiscountEmployee(employee);
                modifierElement.setDiscountDamage(new Boolean(damageFlag));

                modifierList.add(modifierElement);
            }

            ++discountSequenceNumber;
        }

        // add the array of modifiers to the item:
        if (modifierList.size() > 0)
        {
            RetailPriceModifierIfc[] array = modifierList.toArray(new RetailPriceModifierIfc[modifierList.size()]);
            el.setRetailPriceModifier(array);
        }
    }

    /**
     * Creates retail price modifier element(s) for item discount.
     *
     * @param rpmElement retail price modifier element
     * @param discountLineItem discount line item
     * @param srli sale return line item
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createItemDiscountElements(RetailPriceModifierIfc rpmElement,
            ItemDiscountStrategyIfc discountLineItem, SaleReturnLineItemIfc srli) throws XMLConversionException
    {
        // the reason code is mandatory in the schema, but we don't have one
        // here.
        // we set the reason code to N/A just to satisfy the schema.
        rpmElement.setReasonCode("N/A");

        // set method code attribute
        String methodCode = ATTRIBUTE_VALUE_PROMOTION;

        // note: this leaves customer discounts as a promotion
        if (discountLineItem.isAdvancedPricingRule())
        {
            methodCode = ATTRIBUTE_VALUE_PRICE_RULE;
        }
        rpmElement.setMethodCode(methodCode);

        // These values are set here, and saved for use later when we set the
        // price derivation rule element
        CurrencyIfc amount = null;
        BigDecimal percent = null;

        int discountMethod = discountLineItem.getDiscountMethod();
        switch (discountMethod)
        {
        case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE:
            // build percentage discount element
            percent = discountLineItem.getDiscountRate().movePointRight(2);
            rpmElement.setPercent(percent);
            rpmElement.setPercentAction(ATTRIBUTE_VALUE_SUBTRACT);
            break;

        case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT:
        case DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE:
            // build amount discount element
            // derive amount text
            if (discountLineItem instanceof ItemDiscountByAmountIfc)
            {
                ItemDiscountByAmountIfc discount = (ItemDiscountByAmountIfc)discountLineItem;
                amount = discount.getDiscountAmount();
            }
            else if (discountLineItem instanceof ReturnItemTransactionDiscountAuditIfc)
            {
                ReturnItemTransactionDiscountAuditIfc discount = (ReturnItemTransactionDiscountAuditIfc)discountLineItem;
                amount = discount.getDiscountAmount();
            }
            else
            {
                amount = DomainGateway.getBaseCurrencyInstance("0");

            }
            rpmElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(amount)));
            rpmElement.setAmountAction(ATTRIBUTE_VALUE_SUBTRACT);
            break;

        default:
            // if we get here, amount and percent are still null!!!
            amount = DomainGateway.getBaseCurrencyInstance("0");
            rpmElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(amount)));
            rpmElement.setAmountAction(ATTRIBUTE_VALUE_SUBTRACT);
            break;
        }

        rpmElement.setPreviousPrice(currency(srli.getItemPrice().getPermanentSellingPrice()));

        // create discount line item element for adding price derivation rule
        LogDiscountLineItemIfc logDiscountLineItem = IXRetailGateway.getFactory().getLogDiscountLineItemInstance();

        RetailTransactionPriceDerivationRule360Ifc priceDerivationRuleElement = getSchemaTypesFactory()
                .getRetailTransactionPriceDerivationRuleInstance();
        logDiscountLineItem.createPriceDerivationRuleElements(discountLineItem, null,
                priceDerivationRuleElement);
        // the logger does not set the amount/percentage field....weird v1.0
        // design....
        if (percent != null)
        {
            priceDerivationRuleElement.setPercent(percent);
            priceDerivationRuleElement.setPercentAction(ATTRIBUTE_VALUE_SUBTRACT);
        }
        if (amount != null)
        {
            priceDerivationRuleElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(amount)));
            priceDerivationRuleElement.setAmountAction(ATTRIBUTE_VALUE_SUBTRACT);
        }
        rpmElement.setPriceDerivationRule(priceDerivationRuleElement);
    }

    /**
     * Creates retail price modifier element(s) for price override
     *
     * @param rpmElement retail price modifier element
     * @param srli sale return line item
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createPriceOverrideElements(RetailPriceModifierIfc rpmElement, SaleReturnLineItemIfc srli)
            throws XMLConversionException
    {
        rpmElement.setMethodCode(ATTRIBUTE_VALUE_PRICE_OVERRIDE);

        rpmElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                currency(srli.getItemPrice().getSellingPrice())));
        rpmElement.setAmountAction(ATTRIBUTE_VALUE_REPLACE);
        rpmElement.setPreviousPrice(currency(srli.getItemPrice().getPermanentSellingPrice()));
        rpmElement.setReasonCode(srli.getItemPrice().getItemPriceOverrideReason().getCode());

        RetailTransactionPriceDerivationRule360Ifc priceDerivationRuleElement = getSchemaTypesFactory()
                .getRetailTransactionPriceDerivationRuleInstance();
        LogDiscountLineItemIfc logDiscountLineItem = IXRetailGateway.getFactory().getLogDiscountLineItemInstance();
        logDiscountLineItem.createPriceOverrideRuleElements(null, priceDerivationRuleElement, srli);

        // hese are not used by the import, but required by the schema:
        priceDerivationRuleElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                currency(srli.getItemPrice().getSellingPrice())));
        priceDerivationRuleElement.setAmountAction(ATTRIBUTE_VALUE_REPLACE);
        priceDerivationRuleElement.setPriceDerivationAccountingType("None");
        priceDerivationRuleElement.setPostProcessTypeCode("0");

        rpmElement.setPriceDerivationRule(priceDerivationRuleElement);
    }

    /**
     * Adds one modification to the list.
     *
     * @param modificationList List to add to
     * @param alteration alteration desctiption, may be null or empty.
     */
    private void addModification(List<String> modificationList, String alteration)
    {
        if (alteration != null && alteration.length() > 0)
        {
            modificationList.add(alteration);
        }
    }

    /**
     * Method createAlterationElements.
     *
     * @param srli
     * @param itemElement
     */
    protected POSLogAlterationIfc createAlterationElements(SaleReturnLineItemIfc srli) throws XMLConversionException
    {

        AlterationIfc alteration = ((AlterationPLUItemIfc)(srli.getPLUItem())).getAlteration();

        POSLogAlterationIfc alterationElement = getSchemaTypesFactory().getAlterationInstance();

        alterationElement.setType(AlterationIfc.IXRETAIL_ALTERATION_TYPE_DESCRIPTOR[alteration.getAlterationType()]);
        List<String> modificationList = new ArrayList<String>();
        addModification(modificationList, alteration.getItemDescription());
        addModification(modificationList, alteration.getValue1());
        addModification(modificationList, alteration.getValue2());
        addModification(modificationList, alteration.getValue3());
        addModification(modificationList, alteration.getValue4());
        addModification(modificationList, alteration.getValue5());
        addModification(modificationList, alteration.getValue6());

        if (modificationList.size() > 0)
        {
            String[] array = modificationList.toArray(new String[modificationList.size()]);

            alterationElement.setInstruction(array);
        }
        return alterationElement;
    }

    protected void createRetailTransactionTaxModifierElement(SaleReturnLineItemIfc srli, RetailTransactionItemIfc el)
            throws XMLConversionException
    {
        ItemTaxIfc itemTax = srli.getItemPrice().getItemTax();
        int taxMode = itemTax.getTaxMode();
        // if override (or item exempt?) create appropriate element
        if (taxMode == TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT || taxMode == TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE
                || taxMode == TaxIfc.TAX_MODE_TOGGLE_OFF || taxMode == TaxIfc.TAX_MODE_TOGGLE_ON)
        {
            RetailTransactionTaxModifierIfc taxModElement = getSchemaTypesFactory()
                    .getRetailTransactionTaxModifierInstance();
            taxModElement.setTaxGroupID(Integer.toString(itemTax.getTaxGroupId()));
            taxModElement.setTaxScope(TaxIfc.TAX_SCOPE_DESCRIPTOR[srli.getTaxScope()]);
            taxModElement.setTaxModifierSequenceNumber("0"); // only 1 tax
                                                             // modifier per
                                                             // line item is
                                                             // supported - so,
                                                             // hard code
                                                             // sequence to 0
            taxModElement.setReasonCode(itemTax.getReason().getCode());
            RetailTransactionTaxOverrideIfc overrideElement = getSchemaTypesFactory()
                    .getRetailTransactionTaxOverrideInstance();
            CurrencyIfc taxAmount = srli.getTaxInformationContainer().getTaxAmount();
            if (taxMode == TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT)
            {
                CurrencyIfc overrideTaxAmount = taxAmount;
                // Fix the taxable and tax amounts for transaction-level
                // overrides:
                if (srli.getTaxScope() == TaxIfc.TAX_SCOPE_TRANSACTION)
                {
                    if (transaction instanceof SaleReturnTransactionIfc)
                    {
                        SaleReturnTransactionIfc retailTrans = (SaleReturnTransactionIfc)transaction;
                        overrideTaxAmount = retailTrans.getTransactionTotals().getTaxTotal();
                    }
                }
                overrideElement.setNewTaxAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                        currency(overrideTaxAmount)));
            }
            overrideElement.setOriginalTaxAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(taxAmount)));
            double taxRate = 0.00;
            double defaultRate = itemTax.getDefaultRate() * 100.0;
            if (taxMode == TaxIfc.TAX_MODE_OVERRIDE_RATE)
            {
                taxRate = itemTax.getOverrideRate() * 100.0;
            }
            overrideElement.setOriginalPercent(new BigDecimal(Double.toString(defaultRate)));
            overrideElement.setNewTaxPercent(new BigDecimal(Double.toString(taxRate)));
            taxModElement.setTaxOverrideElement(overrideElement);
            taxModElement.setSaleReturnTaxAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(taxAmount)));
            taxModElement.setTaxMode(TaxIfc.IXRETAIL_TAX_MODE_DESCRIPTOR[itemTax.getTaxMode()]);
            el.setTaxModifier(taxModElement);
        }
    }

    /**
     * Creates RetailTransactionTax elements for line item.
     *
     * @param srli sale return line item object
     * @param el parent element
     * @exception XMLConversionException is thrown if error occurs
     */
    protected void createRetailTransactionTaxElements(SaleReturnLineItemIfc srli, RetailTransactionItemIfc el)
            throws XMLConversionException
    {
        List<RetailTransactionTaxIfc> taxList = new ArrayList<RetailTransactionTaxIfc>();
        ItemTaxIfc itemTax = srli.getItemPrice().getItemTax();
        CurrencyIfc taxAmount = itemTax.getItemTaxAmount();
        CurrencyIfc inclusiveTotalTax = itemTax.getItemInclusiveTaxAmount();
        TaxInformationContainerIfc taxInfoContainer = srli.getItemPrice().getItemTax().getTaxInformationContainer();
        TaxInformationIfc[] taxInfo = taxInfoContainer.getTaxInformation();

        for (int i = 0; i < taxInfo.length; i++)
        {
            RetailTransactionTaxIfc taxEl = getSchemaTypesFactory().getRetailTransactionTaxInstance();
            TaxInformationIfc info = taxInfo[i];
            CurrencyIfc infoTaxableAmount = info.getTaxableAmount();
            CurrencyIfc infoTaxAmount = info.getTaxAmount();
            double taxRate = info.getTaxPercentage().doubleValue();

            taxEl.setTaxAuthority(Integer.toString(taxInfo[i].getTaxAuthorityID()));
            taxEl.setTaxRuleID(info.getTaxRuleName());
            taxEl.setTaxGroupID(Integer.toString(info.getTaxGroupID()));
            taxEl.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(infoTaxAmount)));
            taxEl.setTotalTax(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(taxAmount)));
            taxEl.setTaxableAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(infoTaxableAmount)));
            taxEl.setPercent(new BigDecimal(Double.toString(taxRate)));
            taxEl.setTaxMode(TaxIfc.IXRETAIL_TAX_MODE_DESCRIPTOR[info.getTaxMode()]);
            taxEl.setTaxScope(TaxIfc.TAX_SCOPE_DESCRIPTOR[srli.getTaxScope()]);
            taxEl.setTaxHoliday(new Boolean(info.getTaxHoliday()));
            taxEl.setUniqueID(info.getUniqueID());

            // we no longer use the IXRetail TaxType enumeration:
            int taxType = info.getTaxTypeCode();
            taxEl.setInclusiveTaxFlag(new Boolean(info.getInclusiveTaxFlag()));
            taxEl.setTaxType360(Integer.toString(taxType));
            taxEl.setInclusiveTotalTax(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(inclusiveTotalTax)));
            taxList.add(taxEl);
        }

        RetailTransactionTaxIfc[] array = taxList.toArray(new RetailTransactionTaxIfc[taxList.size()]);
        el.setTax(array);
    }

    /**
     * Create elements for kits.
     *
     * @param srli sale return line item
     * @param el element to which additions are to be made
     * @exception XMLConversionException is thrown if error occurs
     */
    protected void createKitElements(SaleReturnLineItemIfc srli, RetailTransactionItemIfc el)
            throws XMLConversionException
    {
        if (srli.isKitHeader())
        {
            RetailTransactionKitIfc kitElement = getSchemaTypesFactory().getRetailTransactionKitInstance();
            // kitElement.setItemCollectionID(srli.getItemID());
            kitElement.setKitHeaderReferenceID(Integer.toString(srli.getKitHeaderReference()));
            el.setKit(kitElement);
        }
    }

    /**
     * Creates elements for return-item original transaction link.
     *
     * @param srli sale return line item object
     * @param el line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTransactionLinkElements(SaleReturnLineItemIfc srli, RetailTransactionItemIfc el)
            throws XMLConversionException
    {
        ReturnItemIfc returnItem = srli.getReturnItem();
        TransactionIDIfc originalTransactionID = returnItem.getOriginalTransactionID();
        POSLogTransactionLinkIfc linkElement = getSchemaTypesFactory().getTransactionLinkInstance();

        // if no transaction ID, this is a return without an original
        // transaction. Then no element is created.
        if (originalTransactionID != null)
        {
            linkElement.setRetailStoreID(originalTransactionID.getStoreID());
            linkElement.setWorkstationID(originalTransactionID.getWorkstationID());
            linkElement.setSequenceNumber(originalTransactionID.getFormattedTransactionSequenceNumber());

            // v1.0 comment (I don't understand it): return without receipt may
            // have transaction ID but no business date.
            // therefore, bypass if null
            EYSDate date = returnItem.getOriginalTransactionBusinessDate();
            linkElement.setBusinessDayDate(dateValue(date));
            linkElement.setReasonCode(ELEMENT_RETURN);

            linkElement.setLineItemSequenceNumber(Integer.toString(returnItem.getOriginalLineNumber()));
            el.setTransactionLink(linkElement);
        }
    }

    /**
     * Creates elements for a return item.
     *
     * @param srli sale return line item
     * @param el item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createReturnItemElements(SaleReturnLineItemIfc srli, RetailTransactionItemIfc itemElement,
            RetailTransactionReturnIfc returnElement) throws XMLConversionException
    {
        returnElement.setReason(srli.getReturnItem().getReason().getCode());
        returnElement.setItemCondition(srli.getReturnItem().getItemCondition().getCode());

        // not in v1.0, but required by v2.1 schema:
        CurrencyIfc restockingFee = srli.getReturnItem().getRestockingFee();
        if (restockingFee == null)
        {
            // try somewhere else
            ItemPriceIfc price = srli.getItemPrice();
            restockingFee = price.getRestockingFee();

            if (restockingFee == null)
            {
                restockingFee = DomainGateway.getBaseCurrencyInstance("0");
            }
        }
        returnElement.setRestockingFee(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                currency(restockingFee)));

        createTransactionLinkElements(srli, itemElement);
    }
}
