/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/sellorderitem/AddItemSite.java /main/9 2012/12/10 19:16:07 tksharma Exp $
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/sellorderitem/AddItemSite.java /main/9 2012/12/10 19:16:07 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    sgu       07/09/10 - donot promot for serial# if the send package id is
 *                         blank
 *    sgu       06/21/10 - added site declaration
 *    acadar    06/03/10 - refresh to tip
 *    sgu       06/02/10 - minor fixes
 *    sgu       06/02/10 - refactor AddItemSite to move business logic to
 *                         TransactionUtility
 *    sgu       06/01/10 - check in after merge
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/25/10 - additional fixes for the process order flow
 *    acadar    05/21/10 - initial version
 *    acadar    05/21/10 - initial version with some dummy code
 *    acadar    05/17/10 - added call to ExternalOrderMAnager; additional fixes
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.sellorderitem;

import oracle.retail.stores.domain.externalorder.ExternalOrderSaleItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.utility.TransactionUtility;

import org.apache.commons.lang3.StringUtils;


/**
 * This site adds an item to the transaction.
 *
 * @version $Revision: /main/9 $
 */
public class AddItemSite extends PosSiteActionAdapter
{
    /**
     *  Serial version UID
     */
    private static final long serialVersionUID = 3875291393441494743L;

    /**
     * Letter for serial number
     */
    public static final String LETTER_GET_SERIAL_NUMBER = "GetSerialNumbers";

    /**
     * Letter for invalid
     */
    public static final String LETTER_NOT_VALID = "NotValid";

    /**
     * Adds the item to the transaction. Mails Continue letter is special order
     * to not ask for serial numbers, else mails GetSerialNumbers letter to
     * possibly ask for serial numbers.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
    	String letter = CommonLetterIfc.CONTINUE;

    	SellOrderItemCargo cargo = (SellOrderItemCargo)bus.getCargo();
    	SaleReturnTransactionIfc transaction = cargo.getTransaction();
    	PLUItemIfc pluItem = cargo.getPLUItem();
    	boolean isItemScaned = false;
    	String itemSizeCode = cargo.getItemSizeCode();
    	ExternalOrderSaleItemIfc externalOrderItem = cargo.getExternalOrderItem();
    	boolean hasExternalSend = !StringUtils.isBlank(externalOrderItem.getSendPackageId());
    	ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

    	// create the sale return line item for the external order item
        SaleReturnLineItemIfc  srli = cargo.getTransaction().addPLUItem(pluItem, externalOrderItem);
        cargo.setLineItem(srli);

        // if serial # is entered for an unknown item, disregard it if the order has external shipping
    	String itemSerial = cargo.getItemSerial();
        if (hasExternalSend)
        {
        	itemSerial = null;
        }

        //call the transaction utility to process the newly added sale line item
        boolean isValidFlag = TransactionUtility.processNewSaleLineItem(transaction, srli, isItemScaned, itemSizeCode, itemSerial, pm);
        if (!isValidFlag)
        {
        	letter = LETTER_NOT_VALID;
        }
        else
        {
        	// reset item size
        	cargo.setItemSizeCode(null);

        	//check for serial item if serialized item or kit header
        	if (srli.isSerializedItem() || srli.isKitHeader() || srli.getItemSerial() != null)
        	{
            	//donot prompt for serial # if it is an unknown item (already prompted) or there is external send
        		if (!(pluItem instanceof UnknownItemIfc) && !hasExternalSend)
        		{
        			letter = LETTER_GET_SERIAL_NUMBER;
        		}
        	}

        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }


}
