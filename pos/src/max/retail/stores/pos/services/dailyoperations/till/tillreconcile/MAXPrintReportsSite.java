/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Nites		4/Jan/2013		Changes for Till Reconcilation FES
  Rev 1.0	Prateek		28/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import max.retail.stores.pos.reports.MAXSummaryReport;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.reports.SummaryReport;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile.TillReconcileCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Print the Close Till Summary Report.

     @version $Revision: 8$
**/
//------------------------------------------------------------------------------
public class MAXPrintReportsSite extends PosSiteActionAdapter
{
    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: 8$";
    /**
      message text tag
    **/
    public static final String CLOSE_SUCCESS_MESSAGE_TAG = "CloseSuccessMsg";
    /**
      default message pattern
    **/
    public static final String CLOSE_SUCCESS_MESSAGE = "Till {0} has successfully closed.";

    //--------------------------------------------------------------------------
    /**
       Build the report and print it.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	DispatcherIfc       d = Gateway.getDispatcher();
        DataManagerIfc     dm = (DataManagerIfc)d.getManager(DataManagerIfc.TYPE);
        
        boolean isoffline = isDatabaseOffline(dm);
        TillReconcileCargo cargo = (TillReconcileCargo) bus.getCargo();
        StoreIfc store = cargo.getStoreStatus().getStore();

        EYSDate businessDate = cargo.getStoreStatus().getBusinessDate();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        boolean mailLetter = true;

        RegisterIfc register = cargo.getRegister();
        String tillID = cargo.getTillID();
        TillIfc till = register.getTillByID(tillID);
        EmployeeIfc emp = till.getSignOnOperator();

        POSBaseBeanModel pbbModel = new POSBaseBeanModel();
        PromptAndResponseModel pnrModel = new PromptAndResponseModel();
        StatusBeanModel sbModel = new StatusBeanModel();

        String[] vars = new String[1];
        vars[0] = tillID;
        String pattern = utility.retrieveText("Common",
                                       BundleConstantsIfc.TILL_BUNDLE_NAME,
                                       CLOSE_SUCCESS_MESSAGE_TAG,
                                       CLOSE_SUCCESS_MESSAGE);
        String message = LocaleUtilities.formatComplexMessage(pattern, vars);

        pnrModel.setArguments(message);
        sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        pbbModel.setPromptAndResponseModel(pnrModel);
        pbbModel.setStatusBeanModel(sbModel);
        ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, pbbModel);

        PrintableDocumentManagerIfc printableDocumentManager =
            (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
        SummaryReport report = printableDocumentManager.getSummaryReportInstance();
        FinancialTotalsDataTransaction ftdt = null;

        ftdt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);

        /*try
        {
            // See if till exists
            ftdt.readTillStatus(store, tillID);

            // Since saving the till reconcile transaction is asynchronous it may not have
            // finished by this time.  By trying again until the till status is "Reconciled"
            // we may be able to retrieve the fresh data.  Giving it 3 tries to accomplish.
            TillIfc[] tills = null;
            for(int tries = 3; tries > 0; tries--)
            {
                tills = ftdt.readTillTotals(store.getStoreID(), tillID, businessDate);
                if (tills.length > 1)
                {
                    for(int cnt = 1; cnt < tills.length; cnt++)
                    {
                        tills[0].addTotals(tills[cnt].getTotals());
                    }
                }
                if(tills[0].getStatus() == AbstractFinancialEntityIfc.STATUS_RECONCILED)
                {
                    // break the loop
                    tries = 0;
                }
                else
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException ie)
                    {
                        // eat the exception!
                    }
                }
            }
            report.setFinancialEntity(tills[0]);
        }
        catch (DataException exception) // revert to hard totals info
        {
            logger.error( "" + exception + "");
            report.setFinancialEntity(register.getTillByID(tillID));
        }*/

        report.setFinancialEntity(register.getTillByID(tillID));
        // Update header info
        report.setStartDate(businessDate);
        report.setStoreID(store.getStoreID());
        report.setRegisterID(register.getWorkstation().getWorkstationID());
        report.setCashierID(cargo.getOperator().getEmployeeID());
        if(report instanceof MAXSummaryReport)
        	((MAXSummaryReport)report).setOffline(isoffline);

        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
            String sep = System.getProperty("line.separator");
            StringBuffer sepBuffer = new StringBuffer();
            for (int i = 0 ; i < 6 ; i++)
            {
                sepBuffer.append(sep);
            }
            if(cargo.getTillCountType() == FinancialCountIfc.COUNT_TYPE_NONE)
            {
                report.setCountTillAtClose(false);
            }
			//Changes for rev 1.1 starts
          //Changes done for code merging(commenting below lines for error resolving)
            //pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, report.getFormattedReport() + sepBuffer.toString());
            PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(
                    PrintableDocumentManagerIfc.TYPE);
            pdm.printReceipt((SessionBusIfc)bus, report);
            report.setCountTillAtClose(true); // set right back to make sure everything works ok
            //Changes for rev 1.1 ends
			pda.cutPaper(97); // cut paper 97% of its width
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        }
        catch (DeviceException e)
        {
            // Update printer status
            logger.error( "Device Exception!!!!! " + e + "");
            logger.error( "" + e.getOrigException() + "");
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
            mailLetter = false;

            String msg[] = new String[1];

            msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                                                BundleConstantsIfc.PRINTER_OFFLINE);

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryContinue");
            model.setType(DialogScreensIfc.RETRY_CONTINUE);
            model.setArgs(msg);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        catch (PrintableDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (mailLetter)
        {
            bus.mail(new Letter("Success"), BusIfc.CURRENT);
        }

    }
    protected boolean isDatabaseOffline(DataManagerIfc dm)
    {
        boolean        offline = true;

        try
        {
            if (dm.getTransactionOnline
                  (UtilityManagerIfc.CLOSE_REGISTER_TRANSACTION_NAME) ||
                dm.getTransactionOnline
                  (UtilityManagerIfc.CLOSE_STORE_REGISTER_TRANSACTION_NAME))
            {
                offline = false;
            }
        }
        catch (DataException de)
        {
            // If not found then running default datatech and offline will not
            // be detected.
        }

        return offline;
    }
}
