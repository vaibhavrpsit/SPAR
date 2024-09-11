/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/RetrieveSuspendedTransactionsReportSite.java /main/11 2011/03/08 17:21:24 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    vtemke 03/07/11 - Print Preview for Reports - fixed review comments
 *    vtemke 03/03/11 - Changes for Print Preview Reports Quickwin
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *
 * 
     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:24:50 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:13:52 PM  Robert Pearse   
     $
     Revision 1.9  2004/07/23 17:57:15  jdeleau
     @scr 5183 Correct Flow for register reports on database error.

     Revision 1.8  2004/07/22 00:06:34  jdeleau
     @scr 3665 Standardize on I18N standards across all properties files.
     Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.

     Revision 1.7  2004/06/03 14:47:44  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.6  2004/04/20 13:17:06  tmorris
     @scr 4332 -Sorted imports

     Revision 1.5  2004/04/14 15:17:10  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.4  2004/03/03 23:15:14  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:50:59  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:46  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 16:01:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jul 17 2003 14:28:56   sfl
 * Based on BA decision, not showing report type argument value for report is printing message.
 * Resolution for POS SCR-3181: Printing Register Reports - Message Prompt is incorrect.
 *
 *    Rev 1.3   Mar 05 2003 09:46:12   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 25 2002 13:16:18   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 10 2002 14:11:54   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:19:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:36:42   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:24:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:10   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.manager.registerreports;

// Foundation imports
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
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.reports.RegisterReport;
import oracle.retail.stores.pos.reports.SuspendedTransactionsReport;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
    Retrieves data for suspended transactions report. <P>
    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------
public class RetrieveSuspendedTransactionsReportSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 2064579396898050914L;
    
    /**
       revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
       site name constant
    **/
    public static final String SITENAME = "RetrieveSuspendedTransactionsReportSite";
    /**
        not yet available tag
    **/
    public static final String NOT_YET_AVAILABLE_TAG = "NotYetAvailable";
    /**
        not yet available default text
    **/
    public static final String NOT_YET_AVAILABLE_TEXT = "NOT YET AVAILABLE";
    //--------------------------------------------------------------------------
    /**
       Retrieves data for suspended transactions report. <P>
       @param bus the bus coming to this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        TransactionSummaryIfc[] summaryList = null;
        // get cargo reference
        RegisterReportsCargoIfc cargo = (RegisterReportsCargoIfc) bus.getCargo();
        // mail letter indicator
        boolean mailLetter = true;

        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        
        try
        {                               // begin retrieve data try block
            // retrieve data
            TransactionReadDataTransaction dataTransaction = null;
            
            dataTransaction = (TransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);
            
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

            summaryList = dataTransaction.readTransactionListByStatus(inquiry);
        }                               // end retrieve data try block
        catch (DataException exception)
        {                               // begin data exception try block
            // not found error is non-fatal
            if (exception.getErrorCode() != DataException.NO_DATA)
            {                           // begin handle database erorr
                logger.error( "" + exception + "");
                mailLetter = false;
                // A real error
                String[] args = new String[1];
                args[0] = utility.getErrorCodeString(exception.getErrorCode());
                DialogBeanModel model = new DialogBeanModel();
                model.setType(DialogScreensIfc.ERROR);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);
                model.setResourceID(RegisterReportsCargo.DATABASE_ERROR);
                // there will be no args for info not found.
                model.setArgs(args);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }                           // end handle database error
        }                               // end data exception try block


        // set up report and mail a letter if it's Ok
        if (mailLetter)
        {
            // create report and load data
            SuspendedTransactionsReport str = new SuspendedTransactionsReport(summaryList);

            // set header values in report
            if (cargo.getOperator() == null || cargo.getOperator().getEmployeeID() == null)
            {
                String notYetAvailable = utility.retrieveText("Common",
                                                              BundleConstantsIfc.MANAGER_BUNDLE_NAME,
                                                              NOT_YET_AVAILABLE_TAG,
                                                              NOT_YET_AVAILABLE_TEXT);
                str.setCashierID(notYetAvailable);
            }
            else
            {
                str.setCashierID(cargo.getOperator().getEmployeeID());
            }

            PromptAndResponseModel parModel = new PromptAndResponseModel();
            POSBaseBeanModel    baseModel    = new POSBaseBeanModel();

            parModel.setArguments("");
            baseModel.setPromptAndResponseModel(parModel);

            str.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
            str.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
            cargo.setReport((RegisterReport) str);

            // Preview Report
            bus.mail(new Letter("PrintPreview"), BusIfc.CURRENT);
        }

    }                                   // end arrive()

}
