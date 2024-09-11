/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/LayawayTransactionADO.java /main/13 2013/04/03 16:02:29 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/03/13 - Completed layaways cannot be post Voided - see bugdb
 *                         id 16565836
 *    blarsen   11/08/11 - Prevent post-voids of deleted layaways.
 *    cgreene   10/26/11 - check for missing tokens when voiding transactions
 *    asinton   10/05/11 - prevent post voids of transactions with certain gift
 *                         card operations.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    acadar    03/09/09 - added comments from code review performed by Jack
 *                         Swan
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         3/31/2008 1:50:55 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    5    360Commerce 1.4         4/25/2007 8:52:48 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         12/13/2005 4:42:33 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:17 PM  Robert Pearse
 *
 *   Revision 1.4.4.1  2004/11/15 22:27:35  bwf
 *   @scr 7671 Create tender from rdo instead of class.  This is necessary because ADO's are not 1:1 with RDOs.
 *
 *   Revision 1.4  2004/04/27 15:50:29  epd
 *   @scr 4513 Fixing tender change options functionality
 *
 *   Revision 1.3  2004/03/29 17:51:28  blj
 *   @scr 3872 - fixed a problem with redeem amounts and numbers being
 *   retained for the next redeem.  Also fixed a problem with the redeem refund tenders
 *   getting disabled with the second or more redeem attempts.
 *
 *   Revision 1.2  2004/02/12 16:47:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.5   Feb 05 2004 13:22:40   rhafernik
 * log4j conversion
 *
 *    Rev 1.4   Jan 09 2004 18:28:22   cdb
 * Corrected glaring defect.
 * Resolution for 3677: Service Alert Cancel - not refunding correct amount.
 *
 *    Rev 1.3   Jan 09 2004 13:14:20   cdb
 * Different behavior is required for canceling a special order. Overrides evaluateTenderState for the discrepancy.
 * Resolution for 3677: Service Alert Cancel - not refunding correct amount.
 *
 *    Rev 1.2   Dec 11 2003 19:04:32   Tim Fritz
 * Added the voidCheckForSuspendedTransaction() method to check to see if the transaction is suspended.
 * Resolution for 3500: Suspended transactions can be post voided.
 *
 *    Rev 1.1   Nov 20 2003 16:57:20   epd
 * updated to use new ADO Factory Complex
 *
 *    Rev 1.0   Nov 04 2003 11:14:32   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 12:35:18   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

import org.apache.log4j.Logger;

/**
 *
 */
public class LayawayTransactionADO extends AbstractRetailTransactionADO
                                   implements ReturnableTransactionADOIfc
{
    private static final long serialVersionUID = 7165918101444517455L;

    /** the performance logger */
    protected static final Logger perf = Logger.getLogger("PERF." + LayawayTransactionADO.class.getName());

    /** HashMap of PaymentHistoryInfoIfc's retrieved from originating layaway */
    protected HashMap<String,PaymentHistoryInfoIfc> originalPaymentHistory = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.AbstractRetailTransactionADO#instantiateTransactionRDO()
     */
    protected TransactionIfc instantiateTransactionRDO()
    {
        return DomainGateway.getFactory().getLayawayTransactionInstance();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.AbstractRetailTransactionADO#isVoidable()
     */
    public boolean isVoidable(String currentTillID) throws VoidException
    {
        // check if authorized tenders are not voidable due to missing account ID token
        voidCheckAuthorizedTenderMissingToken();

        // cannot allow voiding of transactions containing gift card issues, reloads, or redeems.
        voidCheckForGiftCardOperation();

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

        // 7) Make sure the layaway was not deleted
        voidCheckForDeleted();

        return true;
    }

    /**
     * Makes sure this layaway is not complete. Applies to sale items only.
     * @throws VoidException Thrown when it is determined a return has been performed.
     */
    protected void voidCheckForModifiedTransaction()
    throws VoidException
    {
        // completed layways should not be post voided
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_COMPLETE)
        {
            throw new VoidException("Completed layaways not allowed for void", VoidErrorCodeEnum.INVALID_TRANSACTION);
        }
        // new layaways should have a "new" status
        else if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_INITIATE)
        {
            voidCheckStatus();
        }
    }

    /**
     * Makes sure the layaway was not "deleted".
     * @throws VoidException
     */
    protected void voidCheckForDeleted()
    throws VoidException
    {
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_DELETE)
        {
            throw new VoidException("Deleted layaways not allowed for void", VoidErrorCodeEnum.INVALID_TRANSACTION);
        }
    }

    /**
     * Makes sure the layaway has a status of "new".
     * @throws VoidException
     */
    protected void voidCheckStatus()
    throws VoidException
    {
        if (((LayawayTransactionIfc)transactionRDO).getLayaway().getStatus() != LayawayConstantsIfc.STATUS_NEW)
        {
            throw new VoidException("Transaction Modified", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map<String,Object> getJournalMemento()
    {
        return super.getJournalMemento();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#save(oracle.retail.stores.ado.store.RegisterADO)
     */
    public void save(RegisterADO registerADO) throws DataException
    {
        // TODO Auto-generated method stub
    }

    /*
     * Indicates that a layaway delete is a return transaction
     * @return boolean
     */
    public boolean isReturnWithReceipt()
    {
        boolean result = false;
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_DELETE)
        {
            result = true;
        }
        return result;
    }

    /*
     * Returns true if Layaway transaction is a delete
     * @return boolean
     */
    public boolean isLayawayDelete()
    {
        boolean result = false;
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_DELETE)
        {
            result = true;
        }
        return result;
    }

    /*
     * Returns if a type of layaway transaction is considered to be a return with the original
     * retrieved. Return true for layaway delete only. In the case of a layaway transaction,
     * this function should always return identical result as the isReturnWithReceipt function.
     */
    public boolean isReturnWithOriginalRetrieved()
    {
        return isReturnWithReceipt();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#evaluateBalanceDue()
     */
    public TenderStateEnum evaluateTenderState()
    {
        // recalculate transaction total if needed based on dirty flag
        // set by tender.
        recalculateTransactionTotal();

        TenderStateEnum result = null;
        // 1) if the balance is positive, tenders are due
        if (getBalanceDue().signum() == CurrencyIfc.NEGATIVE &&
        transactionRDO.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_DELETE)
        {
            result = TenderStateEnum.REFUND_OPTIONS;
        }
        else
        {
            result = super.evaluateTenderState();
        }

        return result;
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

        UtilityIfc util = getUtility();
        if ("Y".equals(util.getParameterValue("PATCustomerInformation", "Y")))
        {
            logger.debug("Layaway Total: " + ((LayawayTransactionIfc)transactionRDO).getLayaway().getTotal());
            CurrencyIfc patCashThreshold = DomainGateway.getBaseCurrencyInstance(PAT_CASH_THRESHOLD);
            if (patCashThreshold.compareTo(((LayawayTransactionIfc)transactionRDO).getLayaway().getTotal())
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
                    if (transactionRDO.getTransactionType() != TransactionIfc.TYPE_LAYAWAY_COMPLETE)
                    {
                        isPATCashTender = patCashThreshold.compareTo(patCashTotal) == CurrencyIfc.LESS_THAN;

                        if (isPATCashTender)
                        {
                            restorePaymentHistory(originalPaymentHistory,
                                ((LayawayTransactionIfc)transactionRDO).getLayaway().getPaymentHistoryInfoCollection());
                            if (logger.isDebugEnabled())
                            {
                                logger.debug("This is a PAT Cash Transaction (including history) for layaway complete");
                            }
                        } // End check on sum of pat cash tenders
                        else
                        {
                            updatePaymentHistory(originalPaymentHistory,
                                ((LayawayTransactionIfc)transactionRDO).getLayaway().getPaymentHistoryInfoCollection(),
                                newTenders);
                            logger.debug("Not PAT Cash Transaction as total pat tenders is less than PAT Cash Threshold: " + patCashTotal);
                        }
                    } // End check for layaway complete
                    else
                    {
                        patCashTotal = totalPaymentHistory(originalPaymentHistory, patCashTotal);
                        isPATCashTender = patCashThreshold.compareTo(patCashTotal) == CurrencyIfc.LESS_THAN;
                        if (logger.isDebugEnabled())
                        {
                            if (isPATCashTender)
                            {
                                logger.debug("This is a PAT Cash Transaction (including history) for layaway complete");
                            }
                            else
                            {
                                logger.debug("Not PAT Cash Transaction as total pat tenders (including history) is less than PAT Cash Threshold: " + patCashTotal);
                            }
                        }
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
                             + ((LayawayTransactionIfc)transactionRDO).getLayaway().getTotal());
            }
        } // End parameter check
        else if (logger.isDebugEnabled())
        {
            logger.debug("Not PAT Cash Transaction as PATCustomerInformation parameter is 'N'");
        }

        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting isPATCashTransaction");
        }
        return isPATCashTender;
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
    @SuppressWarnings("rawtypes")
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        transactionRDO = (LayawayTransactionIfc)rdo;

        // get and convert RDO tenders
        Iterator<TenderLineItemIfc> iter = ((TenderableTransactionIfc)transactionRDO).getTenderLineItemsVector().iterator();
        while (iter.hasNext())
        {
            // Create ADO tender from RDO tender
            TenderLineItemIfc tenderRDO = iter.next();
            try
            {
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                TenderADOIfc tenderADO = factory.createTender(tenderRDO);
                ((ADO)tenderADO).fromLegacy(tenderRDO);

                // add the tender to the transaction
                addTenderNoValidation(tenderADO);
            }
            catch (ADOException e)
            {
                // TODO Log
                e.printStackTrace();
            }

        }

        // Save Payment History
        if (((LayawayTransactionIfc)transactionRDO).getLayaway() != null &&
            ((LayawayTransactionIfc)transactionRDO).getLayaway().getPaymentHistoryInfoCollection() != null)
        {
            originalPaymentHistory = getPaymentHistoryHash(((LayawayTransactionIfc)transactionRDO).getLayaway().getPaymentHistoryInfoCollection());
        }
        else
        {
            originalPaymentHistory = new HashMap<String,PaymentHistoryInfoIfc>(0);
        }
    }
}
