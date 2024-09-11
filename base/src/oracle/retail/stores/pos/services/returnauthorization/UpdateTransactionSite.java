/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returnauthorization/UpdateTransactionSite.java /rgbustores_13.4x_generic_branch/3 2011/09/15 10:02:51 abondala Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/15/11 - mark transaction as cancel in RTLog
 *    rsnayak   08/17/11 - Fix for return denial itam
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    nkgautam  03/04/10 - fixed pos crash issue for a return transaction
 *                         containing receipted authorised item and
 *                         non-receipted denial item
 *    abondala  01/03/10 - update header date
 *    abondala  03/10/09 - fix the NPE during a return with no receipt and the
 *                         RM server returns a Denial response.
 *    abondala  03/09/09 - Do not change the quantity to zero for an item if
 *                         the return response from RM is Denial.
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.returnauthorization;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Removes denied items from the return transaction
 */
public class UpdateTransactionSite extends PosSiteActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = 7493290593877331815L;

    //--------------------------------------------------------------------------
    /**
       This site removes return line items from a transaction if the lines did not get
       return approval from RM server and manager override.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.NEXT);

        // remove denied items from the return transaction and original transaction.
        ReturnAuthorizationCargoIfc cargo = (ReturnAuthorizationCargoIfc) bus.getCargo();

        ReturnResponseLineItemIfc[] returnResponseLineItems = cargo.getReturnResponseLineItems();
        int numOfReturnResponse = 0;
        if ( returnResponseLineItems!=null )
        {
            numOfReturnResponse = returnResponseLineItems.length;
        }
        SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc)cargo.getTransaction();
        List<ReturnResponseLineItemIfc> deniedReturnResponseLineItems = new ArrayList<ReturnResponseLineItemIfc>();
        for (int i=0; i< numOfReturnResponse; i++) 
        {
            if ( !((returnResponseLineItems[i].getApproveDenyCode()).toLowerCase().contains("authorization")) &&
                 !(returnResponseLineItems[i].isManagerOverride() )) 
            {
                deniedReturnResponseLineItems.add(returnResponseLineItems[i]);
            }
        }

        Collections.sort(deniedReturnResponseLineItems, new LineNumberDescending());

        SaleReturnTransactionIfc[] origSRTs = cargo.getOriginalReturnTransactions();
        Iterator<ReturnResponseLineItemIfc> iter = deniedReturnResponseLineItems.iterator();
        while ( iter.hasNext() )
        {
            ReturnResponseLineItemIfc deniedReturnResponseLineItem = (ReturnResponseLineItemIfc)iter.next();
            transaction.removeLineItem(deniedReturnResponseLineItem.getSaleReturnLineItemIndex());
            // SM-CR-3640: In the original transaction, do not reduce the amount of items available for return
            if(origSRTs != null)
            {
                SaleReturnLineItemIfc origLI = findSRLI(deniedReturnResponseLineItem, origSRTs);
                if (origLI != null)
                {
                    BigDecimal qtyReturned = origLI.getQuantityReturnedDecimal().add(deniedReturnResponseLineItem.getItemQuantityDecimal());
                    origLI.setQuantityReturned(qtyReturned);
                }
            }
        }

        int numOfDeniedReturnResponse = deniedReturnResponseLineItems.size();
        if ( numOfDeniedReturnResponse == numOfReturnResponse )
        {     
        	// if there is no sale item in the transaction when all return items are denied, cancel the transaction.
        	if (transaction.getLineItemsSize() == 0)
        	{
                transaction.setTransactionType(TransactionIfc.TYPE_RETURN);
                transaction.setTransactionStatus(TransactionIfc.STATUS_CANCELED);
        	}
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("REFUND_DENIED");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.NEXT);

            //display dialog
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

        }
        else
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Returns the original SaleReturnLineItem based on ReturnResponseLineItem.
     * In no line item can be found, returns null.
     *
     * @param retRLI    the ReturnResponseLineItem being paired
     * @param origSRTs  the original transaction that carries the line items.
     * @return The original line item based on the returned one.  Might return null.
     */
    protected SaleReturnLineItemIfc findSRLI (ReturnResponseLineItemIfc retRLI, SaleReturnTransactionIfc[] origSRTs)
    {
        SaleReturnLineItemIfc theSRLI = null;
        TransactionIDIfc origTransID = retRLI.getReturnItem().getOriginalTransactionID();

        theSearch:
        for (int i=0; i < origSRTs.length && theSRLI == null; ++i)
        {
            if (origTransID != null && origSRTs[i].getTransactionIdentifier().equals(origTransID))
            {
                Iterator iter = origSRTs[i].getLineItemsIterator();
                while (iter.hasNext())
                {
                    SaleReturnLineItemIfc tempSRLI = (SaleReturnLineItemIfc) iter.next();
                    if (match(retRLI, tempSRLI))
                    {
                        theSRLI = tempSRLI;
                        break theSearch;
                    }
                }
            }
        }
        return theSRLI;
    }

    /**
     * Returns whether the ReturnResponseLineItem matches the SaleReturnLineItem.
     * @param retRLI  the ReturnResponseLineItem
     * @param srli    the SaleReturnLineItem
     * @return Whether the ReturnResponseLineItem matches the SaleReturnLineItem.
     */
    protected boolean match (ReturnResponseLineItemIfc retRLI, SaleReturnLineItemIfc srli)
    {
        boolean matched = false;

        // Attempt to see if the ReturnResponseLineItem seems to be from the SaleReturnLineItem.
        if (retRLI.getOriginalLineNumber() == srli.getOriginalLineNumber() &&
            retRLI.getPLUItemID().equals(srli.getPLUItemID()) &&
            (retRLI.getReturnItem().getQuantityPurchased().compareTo(srli.getItemQuantityDecimal()) == 0) &&
            (retRLI.getItemQuantityDecimal().abs().compareTo(srli.getQuantityReturnedDecimal()) <= 0)) // retRLI.qty <= srli.qty
        {
            matched = true;
        }

        return matched;
    }
}
