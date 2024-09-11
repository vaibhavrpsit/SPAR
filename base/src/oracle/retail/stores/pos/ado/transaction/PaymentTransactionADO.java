/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/PaymentTransactionADO.java /rgbustores_13.4x_generic_branch/1 2011/06/10 16:09:10 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/11/11 - Used LinkedHashMaps to ensure order and added
 *                         generics tempaltes.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    abondala  02/18/09 - check for layaway payment with HA card
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 8:52:48 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         12/13/2005 4:42:34 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:19 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:02 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:01 PM  Robert Pearse
 *
 *   Revision 1.4.2.1  2004/11/15 22:27:35  bwf
 *   @scr 7671 Create tender from rdo instead of class.  This is necessary because ADO's are not 1:1 with RDOs.
 *
 *   Revision 1.4  2004/07/09 14:30:32  bvanschyndel
 *   @scr 5850 Default customer language was not the default store language
 *
 *   Revision 1.3  2004/02/12 16:47:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:04:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Feb 06 2004 10:17:30   Tim Fritz
 * Added getEnabledTenderOptions() method.
 * Resolution for 3803: System allows Store Credit tender on a House Account payment
 *
 *    Rev 1.2   Jan 07 2004 14:57:52   epd
 * fixed SCR 3607 - now cannot tender a house account payment with a house account
 *
 *    Rev 1.1   Dec 11 2003 19:04:36   Tim Fritz
 * Added the voidCheckForSuspendedTransaction() method to check to see if the transaction is suspended.
 * Resolution for 3500: Suspended transactions can be post voided.
 *
 *    Rev 1.0   Nov 04 2003 11:14:34   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 12:35:18   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
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
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.data.DataException;

public class PaymentTransactionADO extends AbstractRetailTransactionADO
{
    private static final long serialVersionUID = -6846504059899482232L;
    /** HashMap of PaymentHistoryInfoIfc's retrieved from originating layaway */
    protected LinkedHashMap<String,PaymentHistoryInfoIfc> originalPaymentHistory = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.AbstractRetailTransactionADO#instantiateTransactionRDO()
     */
    protected TransactionIfc instantiateTransactionRDO()
    {
        return DomainGateway.getFactory().getPaymentTransactionInstance();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.AbstractRetailTransactionADO#isVoidable()
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
     * Makes sure there have been no returns against any line items in this
     * transaction. Applies to sale items only.
     * 
     * @throws VoidException Thrown when it is determined a return has been
     *             performed.
     */
    protected void voidCheckForModifiedTransaction() throws VoidException
    {
        // Cannot void a layaway if it is not active
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            voidCheckStatus();
        }
    }

    /**
     * This method is overridden for Payment transaction so that the transaction
     * can verify that the tender being added is not a house account. This is to
     * satisfy the requirement that disallows house account payments with a
     * house account
     * 
     * @see oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO#addTender(oracle.retail.stores.pos.ado.tender.TenderADOIfc)
     */
    public void addTender(TenderADOIfc tender) throws TenderException
    {
        // If a house account, bug out
        if (tender instanceof TenderCreditADO &&
            ((TenderCreditADO)tender).getCreditType() == CreditTypeEnum.HOUSECARD &&
                transactionRDO.getTransactionType() != TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            throw new TenderException("Cannot make House Account payment with House Account card",
                                      TenderErrorCodeEnum.INVALID_TENDER_TYPE);
        }

        super.addTender(tender);
    }

    /**
     * Makes sure the layaway payment is being made agains an active layaway
     * 
     * @throws VoidException
     */
    protected void voidCheckStatus() throws VoidException
    {
        if (((LayawayPaymentTransactionIfc)transactionRDO)
                    .getLayaway().getStatus() != LayawayConstantsIfc.STATUS_ACTIVE)
        {
            throw new VoidException("Transaction Modified", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
        }
    }

    /**
     * Specific transaction types that need additional logic overrides super
     * class.
     *
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#getEnabledTenderOptions()
     */
    public TenderTypeEnum[] getEnabledTenderOptions()
    {
        if( transactionRDO.getTransactionType() == TransactionConstantsIfc.TYPE_HOUSE_PAYMENT )
        {
            // Copy all Tender types except Store Credit, House Account, and Coupon 
            // as a payment method for House Account
            TenderTypeEnum[] allTypes = super.getEnabledTenderOptions();
            ArrayList<TenderTypeEnum> tenderList = new ArrayList<TenderTypeEnum>(allTypes.length);
            for (TenderTypeEnum type : allTypes)
            {
                if (!TenderTypeEnum.STORE_CREDIT.equals(type) &&
                    !TenderTypeEnum.HOUSE_ACCOUNT.equals(type) &&
                    !TenderTypeEnum.COUPON.equals(type))
                {
                    tenderList.add(type);
                }
            }

            // convert list to array
            TenderTypeEnum[] tenderTypeArray = new TenderTypeEnum[tenderList.size()];
            tenderTypeArray = tenderList.toArray(tenderTypeArray);
            return tenderTypeArray;
        }

        return super.getEnabledTenderOptions();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map getJournalMemento()
    {
        return super.getJournalMemento();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#save(oracle.retail.stores.ado.store.RegisterADO)
     */
    public void save(RegisterADO registerADO) throws DataException
    {
    }

    /**
     * This method returns true if PAT Cash Tender criteria are met
     * 
     * @return true if PAT Cash Tender criteria are met
     */
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

                        HashMap<String,PaymentHistoryInfoIfc> newTenders = getPATCashTenders();
                        for (String key : newTenders.keySet())
                        {
                            patCashTotal = patCashTotal.add(newTenders.get(key).getTenderAmount());
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
    @Override
    protected LinkedHashMap<TenderTypeEnum,TenderGroupADOIfc> processVoid() throws AuthorizationException
    {
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            updatePaymentHistoryForVoid(originalPaymentHistory,
                ((LayawayPaymentTransactionIfc)transactionRDO).getLayaway().getPaymentHistoryInfoCollection());
        }
        return super.processVoid();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return transactionRDO;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        transactionRDO = (PaymentTransactionIfc)rdo;

        // get and convert RDO tenders
        Iterator<TenderLineItemIfc> iter = ((TenderableTransactionIfc) transactionRDO).getTenderLineItemsVector().iterator();
        while (iter.hasNext())
        {
            // Create ADO tender from RDO tender
            TenderLineItemIfc tenderRDO = iter.next();
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
                logger.error("Error converting RDO from legacy.", e);
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
                originalPaymentHistory = new LinkedHashMap<String,PaymentHistoryInfoIfc>(0);
            }
        }
    }
}
