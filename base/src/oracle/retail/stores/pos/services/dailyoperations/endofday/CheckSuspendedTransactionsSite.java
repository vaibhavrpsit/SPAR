/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/endofday/CheckSuspendedTransactionsSite.java /main/13 2012/03/08 08:56:24 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    jswan  01/05/12 - Refactor the status change of suspended transaction to
 *                      occur in a transaction so that status change can be
 *                      sent to CO as part of DTM.
 *    acadar 10/27/10 - changes to reset external order status when canceling
 *                      suspended transactions
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *
 * ===========================================================================

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:13 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse
     $
     Revision 1.7  2004/06/03 14:47:44  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.6  2004/04/20 13:13:09  tmorris
     @scr 4332 -Sorted imports

     Revision 1.5  2004/04/14 15:17:10  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.4  2004/03/03 23:15:11  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:49:37  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:46:17  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:56:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:31:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:13:44   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:26:32   msg
 * Initial revision.
 *
 *    Rev 1.1   18 Jan 2002 17:36:20   vxs
 * If the only existing suspended trans are created in training mode, then changed code to cancel those trans without bothering the operator.
 * Resolution for POS SCR-144: Training mode susp transactions are on the susp trans report
 *
 *    Rev 1.0   Sep 21 2001 11:16:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:22   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.dailyoperations.endofday;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.arts.TransactionWriteDataTransaction;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//------------------------------------------------------------------------------
/**
    Check for existence of suspended transactions.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class CheckSuspendedTransactionsSite extends PosSiteActionAdapter
{                                                                               // begin class CheckSuspendedTransactionsSite

    private static final long serialVersionUID = -3473326851301880992L;

    /**
       revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
       site name constant
    **/
    public static final String SITENAME = "CheckSuspendedTransactionsSite";
    
    private static final String TRAINING_MODE_ONLY = "TrainingModeOnly";


    //--------------------------------------------------------------------------
    /**
       Check for existence of suspended transactions.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                                                   // begin arrive()

        // letter to be mailed
        String letterName = CommonLetterIfc.FAILURE;

        boolean genuineTransExist = true;

        // get cargo
        EndOfDayCargo cargo = (EndOfDayCargo) bus.getCargo();

        // Read the summary from persistent storage
        try
        {
            TransactionReadDataTransaction readTransaction = null;

            readTransaction = (TransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);

            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            TransactionSummaryIfc key = DomainGateway.getFactory().getTransactionSummaryInstance();
            StoreIfc store = DomainGateway.getFactory().getStoreInstance();
            store.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
            key.setStore(store);
            key.setBusinessDate(cargo.getStoreStatus().getBusinessDate());
            key.setTransactionStatus(TransactionIfc.STATUS_SUSPENDED);
            key.setTillID(null);
            key.setTrainingModeUsedInQuery(false);

            SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
            inquiry.setTransactionSummary(key);
            inquiry.setLocaleRequestor(utility.getRequestLocales());

            TransactionSummaryIfc[] summaryList = readTransaction.readTransactionListByStatus(inquiry);
            cargo.setSuspendedTransactionList(summaryList);

            if (isTrainingModeTransactionsOnly(summaryList))
            {
                letterName = TRAINING_MODE_ONLY;
            }
            else
            {
                /* if we got to here, it means there are suspended transactions and the
                operator will have to give the go ahead at the dialog screen
                to cancel the suspended transactions.*/
                letterName = CommonLetterIfc.SUCCESS;
            }
        }
        catch (DataException e)
        {
            if(e.getErrorCode() != DataException.NO_DATA)
            {
                cargo.setDataExceptionErrorCode(e.getErrorCode());
                letterName = CommonLetterIfc.DB_ERROR;
            }
        }

        // mail letter, if necessary
        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }                                                                   // end arrive()

    /*
     * This method determines if the summary list only contains training
     * mode transactions.
     */
    private boolean isTrainingModeTransactionsOnly(
            TransactionSummaryIfc[] summaryList)
    {
        boolean isTrainingModeOnly = true;
        for(TransactionSummaryIfc summary: summaryList)
        {
            if (!summary.isTrainingMode())
            {
                isTrainingModeOnly = false;
                break;
            }
        }
        
        return isTrainingModeOnly;
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object. <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        StringBuffer strResult = new StringBuffer("Class:  ");
        strResult.append("CheckSuspendedTransactionsSite (Revision ")
            .append(getRevisionNumber())
            .append(") @").append(hashCode());
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}                                                                               // end class class CheckSuspendedTransactionsSite
