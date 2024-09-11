/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/ModifyTransactionResumeCargo.java /main/16 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     05/14/14 - Fix issue removing canceled transaction from display.
 *    yiqzhao   10/10/14 - avoid NullPointerException
 *    cgreene   05/14/14 - rename retrieve to resume
 *    vbongu    04/19/13 - Added get and set methods for cancelling suspended
 *                         transaction
 *    rgour     12/04/12 - Suspened Transaction not found if entered id is
 *                         found in both Item Master and suspended transaction
 *                         list
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    jswan     01/21/10 - Fixed comments.
 *    jswan     01/21/10 - Fix an issue in which a returned gift card can be
 *                         modified during the period in which the transaction
 *                         has been suspended.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/27 22:32:05  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.4  2004/02/24 16:21:29  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 20 2004 16:27:14   DCobb
 * Replaced CancelTransactionSite with SetTransactionSite, PrintCancelTransactionSite from common, and SaveCanceledTransactionSite.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 * 
 *    Rev 1.0   Aug 29 2003 16:02:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:16:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   26 Apr 2002 11:02:12   vxs
 * Added attribute boolean visitedSuspendedListSite
 * Resolution for POS SCR-1612: Susp/Resume - SUSP_NOT_RETRIEVABLE - system crashes when pressing enter
 *
 *    Rev 1.0   Mar 18 2002 11:39:16   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.pos.services.common.TenderableTransactionCargoIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.common.utility.Util;

/**
 * Cargo class for ModifyTransactionResume service.
 */
public class ModifyTransactionResumeCargo extends AbstractFinancialCargo implements CargoIfc, DBErrorCargoIfc,
        TenderableTransactionCargoIfc
{
    /**  */
    private static final long serialVersionUID = 8046749331208257677L;

    /**
     * the transaction selected for completion
     */
    protected RetailTransactionIfc transaction = null;

    /**
     * suspend list
     */
    protected TransactionSummaryIfc[] suspendList = null;

    /**
     * suspend transaction Id entered in sale item screen
     */
    protected String transactionIDEntered = null;

    /**
     * selected summary
     */
    protected TransactionSummaryIfc selectedSummary = null;

    /**
     * This vector contains a list of SaleReturnTransacions on which returns
     * have been completed. It will be used if a transaction with returned line
     * items is resumed.
     */
    protected List<SaleReturnTransactionIfc> originalReturnTransactions = null;

    /**
     * Indicates whether previous site was DisplaySuspendedListSite. Used when
     * putting up the InvalidModifiedTransaction dialog screen from
     * LookupTransactionSite
     */
    protected boolean visitedSuspendedListSite = false;

    /**
     * Indicates that the new transaction created from the resumed transaction
     * should be cancel due to issues with invalid returned gift cards.
     */
    protected boolean cancellingRecreatedTransaction = false;
    
    /**
     * Boolean to cancel the original suspended transaction. If all the line items in 
     * the suspended transaction are okay to be resumed then a new transaction is created
     * and original suspended transaction is cancelled
     */
    protected boolean cancelSuspendedTransaction = true;


    /**
     * Constructs ModifyTransactionResumeCargo object.
     */
    public ModifyTransactionResumeCargo()
    {
    }

    /**
     * Returns the transactionIDEntered.
     * 
     * @return the transactionIDEntered.
     */
    public String getTransactionIDEntered()
    {
        return transactionIDEntered;
    }

    /**
     * Sets the value for transactionIDEntered.
     * 
     * @param String transactionIDEntered
     */
    public void setTransactionIDEntered(String transactionIDEntered)
    {
        this.transactionIDEntered = transactionIDEntered;
    }

    /**
     * Returns the retail transaction.
     * 
     * @return the retail transaction
     */
    public void setTransaction(RetailTransactionIfc value)
    {
        transaction = value;
    }

    /**
     * Returns the retail transaction.
     * 
     * @return the retail transaction
     */
    public RetailTransactionIfc getTransaction()
    {
        return (transaction);
    }
   
    /**
     * Returns list of suspended transactions.
     * 
     * @return list of suspended transactions
     */
    public void setSuspendList(TransactionSummaryIfc[] value)
    {
        suspendList = value;
    }

    /**
     * Returns list of suspended transactions.
     * 
     * @return list of suspended transactions
     */
    public TransactionSummaryIfc[] getSuspendList()
    {
        return (suspendList);
    }

    /**
     * Sets selected transaction summary using index into list.
     * 
     * @param value index into transaction summary
     */
    public void setSelectedSummary(int index)
    {
        selectedSummary = suspendList[index];
    }

    /**
     * Sets selected transaction summary.
     * 
     * @param value selected transaction summary
     */
    public void setSelectedSummary(TransactionSummaryIfc value)
    {
        selectedSummary = value;
    }

    /**
     * Returns selected transaction summary.
     * 
     * @return selected transaction summary
     */
    public TransactionSummaryIfc getSelectedSummary()
    {
        return (selectedSummary);
    }

    /**
     * Sets priorListSite
     * 
     * @param value boolean representing state of priorListSite
     */
    public void setVisitedSuspendedListSite(boolean value)
    {
        visitedSuspendedListSite = value;
    }

    /**
     * Gets priorListSite
     * 
     * @return true if previous site visited was DisplaySuspendedSite, false
     *         otherwise
     */
    public boolean isVisitedSuspendedListSite()
    {
        return visitedSuspendedListSite;
    }

    /**
     * Resume the array of transactions on which items have been returned.
     * This cargo does not track this data.
     * 
     * @return SaleReturnTransactionIfc[]
     */
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        if (originalReturnTransactions != null)
        {
            return originalReturnTransactions.toArray(new SaleReturnTransactionIfc[originalReturnTransactions.size()]);
        }
        else
        {
            return null;
        }
    }

    /**
     * Resume the array of transactions on which items have been returned.
     * This cargo does not track this data.
     * 
     * @return SaleReturnTransactionIfc[]
     */
    public void resetOriginalReturnTransactions()
    {
        originalReturnTransactions = null;
    }
    
    /**
     * @return Returns the cancellingRecreatedGiftCard.
     */
    public boolean isCancellingRecreatedTransaction()
    {
        return cancellingRecreatedTransaction;
    }

    /**
     * @param cancellingRecreatedGiftCard The cancellingRecreatedGiftCard to
     *            set.
     */
    public void setCancellingRecreatedTransaction(boolean cancellingRecreatedGiftCard)
    {
        this.cancellingRecreatedTransaction = cancellingRecreatedGiftCard;
    }

    /**
     * Returns the boolean to cancel original suspended transaction
     * @return cancelSuspendedTransaction
     */
    public boolean isCancelSuspendedTransaction() 
    {
        return cancelSuspendedTransaction;
    }

    /**
     * Sets the boolean to cancel original suspended transaction
     * @param cancelOriginalTransaction
     */
    public void setCancelSuspendedTransaction(boolean cancelSuspendedTransaction) 
    {
        this.cancelSuspendedTransaction = cancelSuspendedTransaction;
    }

    /**
     * Add a transaction to the vector of transactions on which items have been
     * returned. This cargo does not track this data.
     * 
     * @param SaleReturnTransactionIfc
     */
    public void addOrignalReturnTransaction(SaleReturnTransactionIfc transaction)
    {
        // check to see if an array already exist; if not make one.
        if (originalReturnTransactions == null)
        {
            originalReturnTransactions = new ArrayList<SaleReturnTransactionIfc>();
        }
        else
        {
            // Check to see if this transaction is already in the array.
            // if so, remove the current reference.
            int size = originalReturnTransactions.size();
            for (int i = 0; i < size; i++)
            {
                SaleReturnTransactionIfc temp = (SaleReturnTransactionIfc) originalReturnTransactions.get(i);
                if (areTransactionIDsTheSame(temp, transaction))
                {
                    originalReturnTransactions.remove(i);
                    // Stop the loop.
                    i = size;
                }
            }
        }
        originalReturnTransactions.add(transaction);
    }

    /**
     * Test the two SaleReturnTransactionIfc objects to see if they refer to the
     * same transaction. Cannot use the equals because the numbers of returned
     * items in the SaleReturnLineItems will not be the same.
     * 
     * @return boolean true if the transaction objects refer to the same
     *         transaction.
     */
    static public boolean areTransactionIDsTheSame(SaleReturnTransactionIfc tran1, SaleReturnTransactionIfc tran2)
    {
        boolean theSame = false;
        if (Util.isObjectEqual(tran1.getTransactionIdentifier(), tran2.getTransactionIdentifier())
                && Util.isObjectEqual(tran1.getBusinessDay(), tran2.getBusinessDay()))
        {
            theSame = true;
        }
        return theSame;
    }

    /**
     * Check if a transaction is in the list of original return transactions.
     * 
     * @return boolean true if the transaction is in the list
     */
    public boolean isTransactionInList(TransactionIDIfc transID,
                                       EYSDate businessDay)
    {
        Iterator<SaleReturnTransactionIfc> iter;                  // enumerator for the transaction list
        SaleReturnTransactionIfc srt;   // transaction in the list

        boolean exitf;                  // exit flag for loop
        boolean rc;                     // return code


        rc = false;                     // initialize return code

        // if the list has been instantiated
        if (originalReturnTransactions != null)
        {                               // Begin transaction list exists

            iter = originalReturnTransactions.iterator();

            // initialize the loop control flag
            exitf = false;

            // search for a matching transaction
            while (iter.hasNext() == true &&
                   exitf == false)
            {                           // Begin search for matching transaction

                srt = (SaleReturnTransactionIfc) iter.next();

                if (Util.isObjectEqual(transID, srt.getTransactionIdentifier()) &&
                    Util.isObjectEqual(businessDay, srt.getBusinessDay()))

                {                       // Begin found a match

                    rc = true;          // set the return code
                    exitf = true;       // don't need to search anymore

                }                       // End found a match

            }                           // End search for a matching transaction

        }                               // End transaction list exists


        return rc;                      // return the return code
    }
    
    /**
     * Removes the selected transaction from the list after the
     * operator selects to cancel the suspended transaction.
     */
    public void removeSelectedTransactionFromSummaryList()
    {
        if (suspendList == null)
        {
            return;
        }
        
        // If there is only one in the list, null out the list
        if (suspendList.length == 1)
        {
            suspendList = null;
        }
        else
        {
            // Create a new summary list which one shorter that the current list.
            int newLength = suspendList.length - 1;
            TransactionSummaryIfc[] newSuspendList = new TransactionSummaryIfc[newLength];
            
            // Iterate through the current list, copying all summaries except the current
            // one.
            int index = 0;
            for (TransactionSummaryIfc summary: suspendList)
            {
                if (summary != selectedSummary)
                {
                    newSuspendList[index] = summary;
                    index++;
                }
            }
            suspendList = newSuspendList;
            selectedSummary = null;
        }
    }

    /**
     * Resumes the saved transaction
     * 
     * @return the TenderableTransactionIfc that is being printed
     */
    public TenderableTransactionIfc getTenderableTransaction()
    {
        return transaction;
    }

    /**
     * Resume the till ID.
     * 
     * @return String till ID
     */
    public String getTillID()
    {
        return null;
    }

    /**
     * Returns the string representation of the object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        StringBuffer strResult = new StringBuffer();
        strResult.append("Class:  ModifyTransactionResumeCargo").append(" (Revision ").append(getRevisionNumber())
                .append(")").append(hashCode());
        if (getTransaction() == null)
        {
            strResult.append("Transaction:                        [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("Transaction:                        [").append(getTransaction().getTransactionID())
                    .append("]").append(Util.EOL);
        }
        return (strResult.toString());
    } // end toString()
}