/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/LookupSuspendedTransactionsSite.java /main/12 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/14/14 - rename retrieve to resume
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *
 * ===========================================================================

     $Log:
      3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:29 PM  Robert Pearse   
     $
     Revision 1.7  2004/06/03 14:47:43  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.6  2004/04/20 13:17:06  tmorris
     @scr 4332 -Sorted imports

     Revision 1.5  2004/04/14 15:17:10  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.4  2004/02/24 16:21:29  cdb
     @scr 0 Remove Deprecation warnings. Cleaned code.

     Revision 1.3  2004/02/12 16:51:12  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:45  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 16:02:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Apr 07 2003 10:39:02   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.0   Apr 29 2002 15:15:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   18 Jan 2002 17:37:22   vxs
 * Using method of TransactionReadDataTransaction which also takes the training mode as a param.
 * Resolution for POS SCR-396: Able to see TM trans while not in TM and vice versa
 *
 *    Rev 1.0   Sep 21 2001 11:31:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:44   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * Resumes list of suspended transactions.
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class LookupSuspendedTransactionsSite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/12 $";
    /**
     * site name constant
     */
    public static final String SITENAME = "LookupSuspendedTransactionsSite";

    /**
     * Resumes list of suspended transactions.
     * 
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.SUCCESS;

        // pull selected summary from cargo
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();
        TransactionSummaryIfc[] summaryList = null;

        // Read the summary from persistent storage
        try
        {
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

            TransactionReadDataTransaction readTransaction = null;

            readTransaction = (TransactionReadDataTransaction)DataTransactionFactory
                    .create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);

            TransactionSummaryIfc key = DomainGateway.getFactory().getTransactionSummaryInstance();
            StoreIfc store = DomainGateway.getFactory().getStoreInstance();
            store.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
            key.setStore(store);
            key.setBusinessDate(cargo.getStoreStatus().getBusinessDate());
            key.setTransactionStatus(TransactionIfc.STATUS_SUSPENDED);
            key.setTillID(null);
            key.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());

            SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
            inquiry.setTransactionSummary(key);
            inquiry.setLocaleRequestor(utility.getRequestLocales());

            summaryList = readTransaction.readTransactionListByStatus(inquiry);

            // put summary list in cargo
            cargo.setSuspendList(summaryList);
        }
        catch (DataException e)
        {
            if (e.getErrorCode() == DataException.NO_DATA)
            {
                letterName = CommonLetterIfc.NOT_FOUND;
            }
            else
            {
                cargo.setDataExceptionErrorCode(e.getErrorCode());
                letterName = CommonLetterIfc.DB_ERROR;
            }
        }

        // mail letter
        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    } // end arrive()

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        StringBuffer strResult = new StringBuffer("Class:  ");
        strResult.append("LookupSuspendedTransactionsSite (Revision ").append(getRevisionNumber()).append(") @")
                .append(hashCode());
        // pass back result
        return (strResult.toString());
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     **/
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
