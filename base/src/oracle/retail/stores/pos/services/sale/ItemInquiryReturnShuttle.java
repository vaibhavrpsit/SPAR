/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ItemInquiryReturnShuttle.java /main/12 2014/06/22 09:20:29 jswan Exp $
 * ===========================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/23/14 - set splittedLineItems to null since current selected
 *                         splitted line item do not exist.
 *    jswan     06/16/14 - Modified to support display of extended item
 *                         recommended items on the Sale Item Screen.
 *    hyin      09/12/12 - set itemFromWebStore flag for xc flow.
 *    cgreene   03/30/12 - get journalmanager from bus
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    09/14/09 - merged from 13.1x
 *    acadar    09/14/09 - XbranchMerge acadar_bug-8875353 from
 *                         rgbustores_13.1x_branch
 *    acadar    09/08/09 - set the plu item in the SaleCargo
 *    akandru   10/29/08 - EJ externalization changes
 *    mchellap  09/30/08 - Added generated serialVersionUID
 *    mchellap  09/29/08 - QW-IIMO Updates for code review comments
 *    mchellap  09/19/08 - QW-IIMO
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

// foundation imports
import java.math.BigDecimal;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.journal.JournalFormatterManager;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

//--------------------------------------------------------------------------
/**
 This shuttle copies information from the Item Inquiry service back to
 the sale service.
 **/
//--------------------------------------------------------------------------
public class ItemInquiryReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    private static final long serialVersionUID = 6288053091943501473L;

    // Child service's cargo
    protected ItemInquiryCargo itemInquiryCargo = null;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.ModifyItemReturnShuttle.class);

    /**
     * The modified line item.
     */
    protected SaleReturnLineItemIfc lineItem = null;

    /**
     * The modified line items.
     */
    protected SaleReturnLineItemIfc[] lineItemList = null;

    /**
     * The flag that indicates whether an item is being added
     */
    protected boolean modified = false;

    /**
     * The item to add.
     */
    protected PLUItemIfc pluItem = null;

    /**
     * Item Quantity
     */
    protected BigDecimal itemQuantity = null;

    /**
     * Flag indicating whether item added is service and added thru inquiry/services
     */
    protected boolean serviceItemFlag = false;

    /**
     * transaction type - sale or return
     */
    protected SaleReturnTransactionIfc transaction;

    //----------------------------------------------------------------------
    /**
     This shuttle copies information from the Item Inquiry service back
     to the Modify Item service.
     <P>
     @param  bus Service Bus to copy cargo from.
     **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the child
        itemInquiryCargo = (ItemInquiryCargo) bus.getCargo();

        // set the add item flag
        modified = itemInquiryCargo.getModifiedFlag();

        // return the transaction in case this is the first line item
        // in order to carry customer linked info
        transaction = (SaleReturnTransactionIfc) itemInquiryCargo.getTransaction();

        //set the item to add
        pluItem = itemInquiryCargo.getPLUItem();

        if (modified && pluItem != null)
        {
            itemQuantity = itemInquiryCargo.getItemQuantity();
        }
    }

    //----------------------------------------------------------------------
    /**
     Copies the new item to the cargo for the Modify Item service.
     <P>
     @param  bus     Service Bus to copy cargo to.
     **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // retrieve cargo from the parent
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
        cargo.setRefreshNeeded(true);
        cargo.setSelectedRecommendedItemId(null);

        if (modified && pluItem != null)
        {
            if (cargo.getTransaction() == null)
            {
                if (transaction == null)
                {
                    cargo.initializeTransaction(bus);
                }
                else
                {
                    cargo.setTransaction(transaction);
                }
            }

            SaleReturnLineItemIfc item = cargo.getTransaction().addPLUItem(pluItem, itemQuantity);
            if ((this.itemInquiryCargo != null) && (this.itemInquiryCargo.isItemFromWebStore()))
            {
                item.setPluDataFromCrossChannelSource(true);
            }

            String productGroup = pluItem.getProductGroupID();
            if (productGroup != null && productGroup.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_ALTERATION))
            {
                //Set the Alteration Item Flag
                item.setAlterationItemFlag(true);
            }

            cargo.setPLUItem(pluItem);

            //set the line item for the serialized item service
            cargo.setLineItem(item);

            if (serviceItemFlag) // journal the service item added to the transaction
            {
                JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(
                        JournalManagerIfc.TYPE);
                JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc) Gateway.getDispatcher().getManager(
                        JournalFormatterManager.TYPE);
                if (journal != null)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append(formatter.toJournalString(item, null, null));

                    if (cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
                    // add status
                    {
                    	String transactionSaleStatus = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_SALE_STATUS, null);
                    	sb.append(Util.EOL).append(transactionSaleStatus);
                    }

                    journal.journal(cargo.getOperator().getLoginID(), cargo.getTransaction().getTransactionID(), sb
                            .toString());
                }
                else
                {
                    logger.error("No JournalManager found");
                }
            }

            //Show item on Line Display device
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            try
            {
                pda.lineDisplayItem(item);
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to use Line Display: " + e.getMessage() + "");
            }

        } // end if (addPLUItem)
        else if (cargo.getIndex() >= 0 || lineItemList != null)
        {
            if (lineItemList != null)
            {
                for (int i = 0; i < lineItemList.length; i++)
                {
                    cargo.getTransaction().replaceLineItem(lineItemList[i], lineItemList[i].getLineNumber());
                    cargo.setItemModifiedIndex(lineItemList[i].getLineNumber());
                }
            }

            if (transaction != null)
            {
                if (cargo.getTransaction() == null)
                {
                    cargo.setTransaction(transaction);
                }
                else if (transaction.getAgeRestrictedDOB() != null)
                {
                    cargo.getTransaction().setAgeRestrictedDOB(transaction.getAgeRestrictedDOB());
                }
            }
        } // end else
        
        if ( cargo.getTransaction() != null )
        {
             cargo.setSplittedLineItems(null);
        }
    }

    //----------------------------------------------------------------------
    /**
     Returns a string representation of this object.
     <P>
     @return String representation of object
     **/
    //----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult = new String("Class:  ItemInquiryReturnShuttle" + hashCode());
        // pass back result
        return (strResult);
    } // end toString()

    //----------------------------------------------------------------------
    /**
     Main to run a test..
     <P>
     @param  args    Command line parameters
     **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    { // begin main()
        // instantiate class
        ItemInquiryReturnShuttle obj = new ItemInquiryReturnShuttle();

        // output toString()
        System.out.println(obj.toString());
    } // end main()
}
