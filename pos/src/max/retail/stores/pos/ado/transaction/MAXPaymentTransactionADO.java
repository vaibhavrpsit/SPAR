/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0     Deepshikha Singh   28/09/2015    Initial Draft:Changes done for loyalty points redeem  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ado.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;
import oracle.retail.stores.pos.ado.transaction.VoidErrorCodeEnum;
import oracle.retail.stores.pos.ado.transaction.VoidException;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

public class MAXPaymentTransactionADO extends MAXAbstractRetailTransactionADO{
    /** HashMap of PaymentHistoryInfoIfc's retrieved from originating layaway */
    protected HashMap originalPaymentHistory = null;

    /* (non-Javadoc)
     * @see com._360commerce.ado.transaction.AbstractRetailTransactionADO#instantiateTransactionRDO()
     */
    protected TransactionIfc instantiateTransactionRDO()
    {
        return DomainGateway.getFactory().getPaymentTransactionInstance();        
    }
    
    /* (non-Javadoc)
     * @see com._360commerce.ado.transaction.AbstractRetailTransactionADO#isVoidable()
     */
    public boolean isVoidable(String currentTillID) throws VoidException
    {
        // 1) Make sure we have the same Till ID
        voidCheckForSameTill(currentTillID);

        // 2) Transaction should not already be voided
        voidCheckForPreviousVoid();
        
        // 3) Check for modified transaction
        voidCheckForModifiedTransaction();
        
        // 4) Make sure any issued tenders have not been used.
        voidCheckForIssuedTenderModifications();

        // 5) Check that void is allowed for tranasactions
        //    containing debit tenders
        voidCheckDebitAllowed();

        // 6) Make sure the transaction is not suspended
        voidCheckForSuspendedTransaction();

        return true;
    }
    
    /**
     * Makes sure there have been no returns against any line
     * items in this transaction. Applies to sale items only.
     * @throws VoidException Thrown when it is determined a return has been performed.
     */
    protected void voidCheckForModifiedTransaction()
    throws VoidException
    {
        // Cannot void a layaway if it is not active
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            voidCheckStatus();
        }
    }
    
    /**
     * This method is overriden for Payment transaction so that
     * the transaction can verify that the tender being added is 
     * not a house account.  This is to satisfy the requirement that
     * disallows house account payments with a house account
     * @see com._360commerce.pos.ado.transaction.AbstractRetailTransactionADO#addTender(com._360commerce.pos.ado.tender.TenderADOIfc)
     */
    public void addTender(TenderADOIfc tender) throws TenderException
    {
        // If a house account, bug out
        if (tender instanceof TenderCreditADO &&
            ((TenderCreditADO)tender).getCreditType() == CreditTypeEnum.HOUSECARD)
        {
            throw new TenderException("Cannot make House Account payment with House Account card", 
                                      TenderErrorCodeEnum.INVALID_TENDER_TYPE);
        }
        
        super.addTender(tender);
    }
    
    /**
     * Makes sure the layaway payment is being made agains an active layaway
     * @throws VoidException
     */
    protected void voidCheckStatus()
    throws VoidException
    {
        if (((LayawayPaymentTransactionIfc)transactionRDO)
                    .getLayaway().getStatus() != LayawayConstantsIfc.STATUS_ACTIVE)
        {
            throw new VoidException("Transaction Modified", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
        }
    }

    /* (non-Javadoc)
     * Specific transaction types that need additional logic overrides super class
     * @see com._360commerce.ado.transaction.RetailTransactionADOIfc#getEnabledTenderOptions()
     */
    public TenderTypeEnum[] getEnabledTenderOptions()
    {
        if( transactionRDO.getTransactionType() == TransactionConstantsIfc.TYPE_HOUSE_PAYMENT ) 
        {
            // Copy all Tender types except Store Credit as a payment method for House Account
            TenderTypeEnum[] allTypes = super.getEnabledTenderOptions();
            ArrayList tenderList = new ArrayList();
            for( int i = 0; i < allTypes.length; i += 1 )
            {
                if( allTypes[ i ] != TenderTypeEnum.STORE_CREDIT )
                {
                    tenderList.add( allTypes[ i ] );
                }
            }
            // convert list to array        
            TenderTypeEnum[] tenderTypeArray = new TenderTypeEnum[tenderList.size()];
            tenderTypeArray = (TenderTypeEnum[])tenderList.toArray(tenderTypeArray);
            return tenderTypeArray;
        } else {
            return super.getEnabledTenderOptions();
        }
    }    
    
    /* (non-Javadoc)
     * @see com._360commerce.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map getJournalMemento()
    {
        return super.getJournalMemento();
    }
    
    /* (non-Javadoc)
     * @see com._360commerce.ado.transaction.RetailTransactionADOIfc#save(com._360commerce.ado.store.RegisterADO)
     */
    public void save(RegisterADO registerADO) throws DataException
    {
        // TODO Auto-generated method stub
    }
 
    //----------------------------------------------------------------------
    /**
        This method returns true if PAT Cash Tender criteria are met
        @return true if PAT Cash Tender criteria are met
    **/
    //----------------------------------------------------------------------
    public boolean isPATCashTransaction()
    {
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering isPATCashTransaction");
        }
        boolean isPATCashTender = false;
        
        if(transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            UtilityIfc util = getUtility();
            if ("Y".equals(util.getParameterValue("PATCustomerInformation", "Y")))
            {
                logger.debug("Layaway Total: " + ((LayawayPaymentTransactionIfc)transactionRDO).getLayaway().getTotal());
                CurrencyIfc patCashThreshold = DomainGateway.getBaseCurrencyInstance(PAT_CASH_THRESHOLD);
                if (patCashThreshold.compareTo(((LayawayPaymentTransactionIfc)transactionRDO).getLayaway().getTotal()) 
                                                        == CurrencyIfc.LESS_THAN)
                {
                    if ((getBalanceDue().signum() != CurrencyIfc.POSITIVE))
                    {
                        CurrencyIfc patCashTotal = DomainGateway.getBaseCurrencyInstance();
                        
                        HashMap newTenders = getPATCashTenders();
                        for (Iterator i = newTenders.keySet().iterator(); i.hasNext();)
                        {
                            Object key = i.next();
                            patCashTotal = patCashTotal.add(((PaymentHistoryInfoIfc)newTenders.get(key)).getTenderAmount());
                        }
                        if (patCashTotal.compareTo(((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getGrandTotal()) 
                            == CurrencyIfc.GREATER_THAN)
                        {
                            patCashTotal = ((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getGrandTotal();
                        }

                        isPATCashTender = patCashThreshold.compareTo(patCashTotal) == CurrencyIfc.LESS_THAN;

                        if (isPATCashTender)
                        {
                            restorePaymentHistory(originalPaymentHistory,
                                ((LayawayPaymentTransactionIfc)transactionRDO).getLayaway().getPaymentHistoryInfoCollection());
                            if (logger.isDebugEnabled())
                            {
                                logger.debug("This is a PAT Cash Transaction (including history) for layaway complete");
                            }
                        } // End check on sum of pat cash tenders
                        else
                        {
                            updatePaymentHistory(originalPaymentHistory,
                                ((LayawayPaymentTransactionIfc)transactionRDO).getLayaway().getPaymentHistoryInfoCollection(),
                                newTenders);                            
                            logger.debug("Not PAT Cash Transaction as total pat tenders is less than PAT Cash Threshold: " + patCashTotal);
                        }
                    } // Check balance due
                    else if (logger.isDebugEnabled())
                    {
                        logger.debug("Not PAT Cash Transaction (yet) as balance due is positive: "
                                     + getBalanceDue());
                    }
                } // End layaway total check
                else if (logger.isDebugEnabled())
                {
                    logger.debug("Not PAT Cash layaway as layaway total is less than PAT Cash Threshold: "
                                 + ((LayawayPaymentTransactionIfc)transactionRDO).getLayaway().getTotal());
                }
            } // End parameter check
            else if (logger.isDebugEnabled())
            {
                logger.debug("Not PAT Cash Transaction as PATCustomerInformation parameter is 'N'");
            }
        }

        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting isPATCashTransaction");
        }
        return isPATCashTender;
    }
    
    /**
     * Some transactions require specific behavior when voided to reverse certain actions (such as contacting a 3rd
     * party authorizer for a reversal). Each transaction type should be responsible for handling its own specific
     * logic. It returns a HashMap of Tender Groups containing inversed tenders
     * 
     * We use this method explicitly for reversing the payment 
     * 
     * @return Inverse tenders from the current transaction.
     * @throws AuthorizationException
     */
    protected LinkedHashMap<TenderTypeEnum, TenderGroupADOIfc> processVoid() throws AuthorizationException
    {
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            updatePaymentHistoryForVoid(originalPaymentHistory,
                ((LayawayPaymentTransactionIfc)transactionRDO).getLayaway().getPaymentHistoryInfoCollection());
        }
        return super.processVoid();
    }
    
    /* (non-Javadoc)
     * @see com._360commerce.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return transactionRDO;
    }
    
    /* (non-Javadoc)
     * @see com._360commerce.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }
    /* (non-Javadoc)
     * @see com._360commerce.ado.ADOIfc#fromLegacy(com.extendyourstore.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        transactionRDO = (PaymentTransactionIfc)rdo;

        // get and convert RDO tenders
        Iterator iter = ((TenderableTransactionIfc) transactionRDO).getTenderLineItemsVector().iterator();
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Save Payment History
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            LayawayPaymentTransactionIfc layawayTransaction = ((LayawayPaymentTransactionIfc)transactionRDO);
            if (layawayTransaction.getLayaway() != null &&
                layawayTransaction.getLayaway().getPaymentHistoryInfoCollection() != null)
            {
                originalPaymentHistory = getPaymentHistoryHash(layawayTransaction.getLayaway().getPaymentHistoryInfoCollection());
            }
            else
            {
                originalPaymentHistory = new HashMap(0);
            }
        }

        // TODO: finish converting other entities
        
    }
}
