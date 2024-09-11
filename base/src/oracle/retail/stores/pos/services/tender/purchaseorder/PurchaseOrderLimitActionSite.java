/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/purchaseorder/PurchaseOrderLimitActionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   04/21/09 - In isTranactionNonTaxable method changed local
 *                         varialble to primative boolean type and local
 *                         variable name to isNonTaxable.
 *    asinton   04/21/09 - Dont allow layaway payments, layaway complete, order
 *                         partial, and order complete to be tax exempt from
 *                         use of purchase order tender.
 *    aphulamb  04/14/09 - Fixed issue if Special Order is done by Purchase
 *                         Order
 *
 * ===========================================================================
 * $Log:
 |    4    360Commerce 1.3         6/12/2008 4:32:39 PM   Charles D. Baker CR
 |         32040 - Updated to avoid clearing tax exempt status unless a) we're
 |          removing a tax exempt tender and b) there are not remaining
 |         tenders that are tax exempt purchase orders. Code review by Jack
 |         Swan.
 |    3    360Commerce 1.2         3/31/2005 4:29:32 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:24:28 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:13:29 PM  Robert Pearse   
 |   $
 |   Revision 1.3  2004/06/15 22:31:27  crain
 |   @scr 5596 Tender_No Change Due Options available for PO Tender
 |
 |   Revision 1.2  2004/05/17 19:30:56  crain
 |   @scr 4198 Receipt prints incorrect PO Tender amount
 |
 |   Revision 1.1  2004/04/02 22:13:51  epd
 |   @scr 4263 Updates to move Purchase Order tender to its own tour
 |
 |   Revision 1.5  2004/02/27 02:43:33  crain
 |   @scr 3421 Tender redesign
 |
 |   Revision 1.4  2004/02/18 18:17:48  tfritz
 |   @scr 3718 - Added setNonTaxable() method.
 |
 |   Revision 1.3  2004/02/12 16:48:22  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 21:22:51  rhafernik
 |   @scr 0 Log4J conversion and code cleanup
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 |   updating to pvcs 360store-current
 | 
 |    Rev 1.3   Jan 26 2004 18:18:30   cdb
 | Altered to have more consistent exception when expected tender amount isn't valid.
 | Resolution for 3682: Invalid PO Amount displays when PO tender > Balance Due
 | Resolution for 3686: Invalid PO Amount displays when PO tender < Balance Due
 | 
 |    Rev 1.2   Jan 14 2004 08:59:00   cdb
 | Updated to allow invalid amount exception.
 | Resolution for 3682: Invalid PO Amount displays when PO tender > Balance Due
 | Resolution for 3686: Invalid PO Amount displays when PO tender < Balance Due
 | 
 |    Rev 1.1   Jan 13 2004 17:35:28   cdb
 | Corrected flow and updated behavior to match current requirements.
 | Resolution for 3682: Invalid PO Amount displays when PO tender > Balance Due
 | Resolution for 3686: Invalid PO Amount displays when PO tender < Balance Due
 | 
 |    Rev 1.0   Nov 04 2003 11:17:50   epd
 | Initial revision.
 | 
 |    Rev 1.0   Oct 24 2003 14:54:56   bwf
 | Initial revision.
 | Resolution for 3418: Purchase Order Tender Refactor
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.purchaseorder;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This method checks the limits on purchase orders.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class PurchaseOrderLimitActionSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4935727458456487548L;
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        tax exempt
        @deprecated as of 13.0. Use {@link TenderConstants#TAX_EXEMPT}.
     **/
     public static final String TAX_EXEMPT = TenderConstants.TAX_EXEMPT;
     /**
         taxable
         @deprecated as of 13.0. Use {@link TenderConstants#TAXABLE}.
      **/
      public static final String TAXABLE = TenderConstants.TAXABLE;
    
    //  --------------------------------------------------------------------------
    /**
       This method checks the limits on purchase orders.
        @param bus BusIfc
    **/
    //  --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get tender attributes from cargo and add tender type
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.PURCHASE_ORDER);
        String amount = (String)tenderAttributes.get(TenderConstants.AMOUNT);
        tenderAttributes.put(TenderConstants.FACE_VALUE_AMOUNT, amount);
        
        if (((Boolean)tenderAttributes.get(TenderConstants.TRANSACTION_NON_TAXABLE)).booleanValue() &&
                this.isTransactionNonTaxable(cargo))
        {
            // set the transaction as tax exempt
            cargo.getCurrentTransactionADO().setTaxExempt(null,-1);
            tenderAttributes.put(TenderConstants.TAXABLE_STATUS, TenderConstants.TAX_EXEMPT);
        }
        else
        {
            tenderAttributes.put(TenderConstants.TAXABLE_STATUS, TenderConstants.TAXABLE);
        }

        try
        {
            // Use transaction to validate limits for purchase order
            cargo.getCurrentTransactionADO().validateTenderLimits(tenderAttributes);
            CurrencyIfc depositAmount = cargo.getCurrentTransactionADO().getDepositAmount();
            if (!depositAmount.equals(DomainGateway.getBaseCurrencyInstance()))
            {
                tenderAttributes.put(TenderConstants.AMOUNT, depositAmount.getDecimalValue().toString());
            }
            
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT); 
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();
            
            if (error == TenderErrorCodeEnum.INVALID_AMOUNT)
            {
                // if invalid amount
                if (e.getNestedException() != null
                    && e.getNestedException() instanceof TenderException
                    && ((TenderException)e.getNestedException()).getErrorCode() == TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED)
                {
                    bus.mail(new Letter(CommonLetterIfc.INVALID), BusIfc.CURRENT);
                }
                // if invalid amount
                else
                {
                    bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
                }
            }
        }
    }

    /**
     * Returns true if the transaction is not one of TYPE_LAYAWAY_PAYMENT,
     * TYPE_LAYAWAY_COMPLETE, TYPE_ORDER_PARTIAL, TYPE_ORDER_COMPLETE.
     * 
     * @param cargo
     * @return boolean
     */
    protected boolean isTransactionNonTaxable(TenderCargo cargo)
    {
        boolean isNonTaxable = true;
        RetailTransactionADOIfc transactionADO = cargo.getCurrentTransactionADO();
        TenderableTransactionIfc transaction = (TenderableTransactionIfc)transactionADO.toLegacy();
        if(transaction != null)
        {
            int transactionType = transaction.getTransactionType();
            /*
             * test if the transaction is one of these transaction types.
             * if so then transaction is not valid for tax exempt by purchase order
             */
            if(transactionType == TransactionConstantsIfc.TYPE_LAYAWAY_PAYMENT ||
                    transactionType == TransactionConstantsIfc.TYPE_LAYAWAY_COMPLETE ||
                    transactionType == TransactionConstantsIfc.TYPE_ORDER_PARTIAL ||
                    transactionType == TransactionConstantsIfc.TYPE_ORDER_COMPLETE)
            {
                isNonTaxable = false;
            }
        }
        return isNonTaxable;
    }
}
