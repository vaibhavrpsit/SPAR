/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/RedeemTransactionADO.java /main/15 2013/07/30 09:18:30 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     07/29/13 - for generating the RTlogs in training mode need to
 *                         save the redeem transactions
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    asinton   10/04/11 - prevent post voiding of transactions with authorized
 *                         tenders that lack necessary data for reversing.
 *    asinton   09/06/11 - remove ability to post void transactions with issue,
 *                         reload, redeem of gift cards.
 *    asinton   05/09/11 - Removed restrictions for voiding of gift cards
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   03/30/09 - Chenged method name to be more generic per review
 *                         comments.
 *    asinton   03/30/09 - Prevent post void of transaction with any gift card
 *                         tender.
 *    asinton   03/08/09 - Changes to disallow post void of gift card issue,
 *                         reload, and redeem.
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         3/31/2008 1:52:26 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    5    360Commerce 1.4         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    4    360Commerce 1.3         4/25/2007 8:52:48 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:29:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:36 PM  Robert Pearse   
 *
 *   Revision 1.22.2.1  2004/11/15 22:27:35  bwf
 *   @scr 7671 Create tender from rdo instead of class.  This is necessary because ADO's are not 1:1 with RDOs.
 *
 *   Revision 1.22  2004/06/23 15:27:32  bwf
 *   @scr 5312 DeActivate gift card when hitting undo.
 *
 *   Revision 1.21  2004/06/23 00:42:06  blj
 *   @scr 5113 added capture customer capability for store credit redeem.
 *
 *   Revision 1.20  2004/06/15 21:54:45  bwf
 *   @scr 5476 Fixed validateRefundLimits so that redeems dont crash when
 *                     using mbc or credit.  Use correct error message for cash.
 *
 *   Revision 1.19  2004/06/14 17:32:20  bwf
 *   @scr 0 fixed header.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;
import java.util.HashMap;
import java.util.Iterator;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
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
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupCashADO;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;




import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

/**
 * Redeem is an ADO service.  This class holds the business logic for 
 * redeem ADO transactions.
 */
@SuppressWarnings("serial")
public class RedeemTransactionADO extends AbstractRetailTransactionADO implements ReturnableTransactionADOIfc
{
    /** The ADO transaction being voided */
    protected RetailTransactionADOIfc originalTxnADO;
    
    /** Redeem tender type */
    protected TenderTypeEnum redeemTenderType;
    
    /** Gift Card Flag **/
    boolean giftCardFlag = false;
    
    /** Redeem Tender ADO **/
    protected TenderADOIfc redeemTenderADO = null;
    
    /**
     * This method creates the RedeemTransaction RDO object.
     */
    protected TransactionIfc instantiateTransactionRDO()
    {
        transactionRDO = DomainGateway.getFactory().getRedeemTransactionInstance();
        return transactionRDO;
    }
    
    /**
     * Attempt to add a redeem tender.  Invokes validation.
     * @param tender The redeem tender to be added
     * @throws TenderException Thrown when a validation error occurs.
     */
    public void addRedeemTender(TenderADOIfc tender) throws TenderException
    {
        //redeemTenderType = tender.getTenderType();
        setTenderType(tender.getTenderType());
        
        redeemTenderADO = tender;
        // Add tender to RDO
        ((RedeemTransactionIfc) transactionRDO).addRedeemTender((TenderLineItemIfc) ((ADO) tender).toLegacy());
    }

    /**
     * Gets the redeem tender ADO.
     * @return the redeem tender ADO.
     */
    public TenderADOIfc getRedeemTender()
    {
        return redeemTenderADO;         
    }
    
    /**
     * Sets the tender type enumeration.
     * @param type 
     */
    protected void setTenderType(TenderTypeEnum type)
    {
        redeemTenderType = type;
    }

    /**
     * Sets the tender type ADO. 
     * @param tender
     */
    protected void setTenderType(TenderADOIfc tender)
    { 
        redeemTenderType = tender.getTenderType();
    }
    
    /**
     * Returns the tender type enumeration.
     * @return the tender type enumeration.
     */
    public TenderTypeEnum getTenderType()
    {
        return redeemTenderType;
    }

    /**
     * This method saves the redeem transaction.
     * @param registerADO
     */
    public void save(RegisterADO registerADO) throws DataException
    { 
          
            // Save this redeem transaction
            BusIfc bus = TourContext.getInstance().getTourBus();
            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
            RegisterIfc registerRDO = (RegisterIfc)registerADO.toLegacy();
            TillIfc tillRDO = registerRDO.getCurrentTill();
            utility.saveTransaction(transactionRDO, tillRDO, registerRDO);
        
    }
    
    /**
     * Redeem transactions only have 3 states: 
     * Redeem Refund Options, Refund Due and Paid Up.
     * 
     * Assumptions: No Split Tenders and No Change Due to customer.  The transaction balance
     * is negative or zero and the transaction grand total is negative or we are paid up.
     * @return TenderStateEnum result
     */
    public TenderStateEnum evaluateTenderState()
    {
        // recalculate transaction total if needed based on dirty flag
        // set by tender.
        recalculateTransactionTotal();

        TenderStateEnum result = null;
        
        // 1) There is a refund due
        if (getBalanceDue().signum() == CurrencyIfc.NEGATIVE
                && getTransactionGrandTotal().signum() == CurrencyIfc.NEGATIVE)
        {
            result = TenderStateEnum.REDEEM_REFUND_OPTIONS;
        }
        // 2) There is a refund due
        else if (getBalanceDue().signum() == CurrencyIfc.ZERO && 
                getTransactionGrandTotal().signum() == CurrencyIfc.NEGATIVE)
        {
            result = TenderStateEnum.REFUND_DUE;
        }
        // 3) All paid up
        else
        {
            result = TenderStateEnum.PAID_UP;
        }

        return result;
    }
    
    /**
     * This method is always false for redeems.
     * Redeem tenders or not returned by receipt.
     * @return false
     */
    public boolean isReturnWithReceipt()
    {
        return false;
    }
    
    /**
     *  
     * This method is always false for redeems.
     * Redeem tenders are not returns with original
     * transaction retrieved.
     * @return false
     */
    public boolean isReturnWithOriginalRetrieved()
    {
        return false;
    }
    
    /**
     * This methods returns true if the transaction is voidable.
     * @param currentTillID
     */
    public boolean isVoidable(String currentTillID) throws VoidException
    {
        // check if this is a redeem of gift card
        if(transactionRDO instanceof RedeemTransactionIfc)
        {
            RedeemTransactionIfc redeemTransactionIfc = (RedeemTransactionIfc)transactionRDO;
            if (redeemTransactionIfc.getRedeemTender().getTypeCode() == TenderLineItemIfc.TENDER_TYPE_GIFT_CARD)
            {
                throw new VoidException("Gift Card Redeem Not Voidable", VoidErrorCodeEnum.GIFT_CARD_VOID_INVALID);
            }
            if (redeemTransactionIfc.getRedeemTender().getTypeCode() == TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE)
            {
                throw new VoidException("Gift Certificate Redeem Not Voidable", VoidErrorCodeEnum.GIFT_CERTIFICATE_VOID_INVALID);
            }
            if (redeemTransactionIfc.getRedeemTender().getTypeCode() == TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT)
            {
                throw new VoidException("Store Credit Redeem Not Voidable", VoidErrorCodeEnum.STORE_CREDIT_VOID_INVALID);
            }
        }

        // check if authorized tenders are not voidable due to missing account ID token
        voidCheckAuthorizedTenderMissingToken();

        // since 13.1 post void of transaction containing any gift card operation is not allowed,
        // except sale transactions using gift card tender.
        voidCheckForGiftCardOperation();

        // 1) Make sure we have the same Till ID
        voidCheckForSameTill(currentTillID);
        
        // 2) Transaction should not already be voided
        voidCheckForPreviousVoid();

        return true;
    }

    /**
     * This method checks the refund limits during redeem.
     * @param tenderAttributes
     * @param hasReceipt
     * @param retrieved
     * @throws TenderException
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#validateRefundLimits(java.util.HashMap, boolean, boolean)
     */
    public void validateRefundLimits(HashMap tenderAttributes, boolean hasReceipt, boolean retrieved) throws TenderException
    {
        assert(tenderAttributes.get(TenderConstants.TENDER_TYPE) != null);
        
        // validate specific limits for this tender
        TenderGroupADOIfc group = getTenderGroup((TenderTypeEnum) tenderAttributes.get(TenderConstants.TENDER_TYPE));
        if (group instanceof TenderGroupCashADO)
        {
            ((TenderGroupCashADO)group).validateRedeemLimits(tenderAttributes, getBalanceDue());
        }
        else
        {
            super.validateRefundLimits(tenderAttributes, hasReceipt, retrieved);
        }
    }

    /**
     * Updates the tender attributes map with the given void tender.
     * @param voidTender
     * @throws TenderException
     */
    protected void updateTenderAttributes(TenderADOIfc voidTender) throws TenderException
    {
        // Update tender attributes
        HashMap tAttributes = voidTender.getTenderAttributes();
        voidTender.setTenderAttributes(tAttributes);
    }
    
    /**
     * Override this method in the AbstractRetailTransactionADO because
     * redeem tenders do not calculate the negative cash total.
     * 
     * @return the tender total.
     */
    protected CurrencyIfc getTenderTotal()
    {
        CurrencyIfc result = ((TenderableTransactionIfc) transactionRDO).getTransactionTotals().getAmountTender();
        
        //result = result.subtract(((TenderableTransactionIfc) transactionRDO).getNegativeCashTotal());
        
        return result;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.ADOIfc#toLegacy()
     */
    @Override
    public EYSDomainIfc toLegacy()
    {
        return transactionRDO;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    @Override
    public EYSDomainIfc toLegacy(Class type)
    {
        return transactionRDO;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    @Override
    public void fromLegacy(EYSDomainIfc rdo)
    {
        // our RDO
        transactionRDO = (RedeemTransactionIfc)rdo;
        
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
                e.printStackTrace();
            }
        }
    }    
}
