/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Nites		4/Jan/2013		Changes for Till Reconcilation FES
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import max.retail.stores.pos.reports.MAXTillCountReport;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.reports.TillCountReport;
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
    Prints receipt upon closing the till.

    @version $Revision: 4$
**/
//------------------------------------------------------------------------------
public class MAXPrintReceiptSite extends PosSiteActionAdapter
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 4$";

    public static final String SITENAME = "PrintReceiptSite";

    //--------------------------------------------------------------------------
    /**
       Collects the Till count or Float count then calls printReport to
       print the count(s). Displays a screen that printing is occurring.
       Catches device exceptions and displays a dialog to allow the user
       to Retry or Cancel. If parameters TillCountFloatAtClose AND
       TillCountTillAtClose are set to No, then do not count the till nor
       float, otherwise count till or float based upon each parameter's
       value (Summary/Detail).
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        boolean mailLetter = true;
        TillReconcileCargo cargo = (TillReconcileCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        int floatCountType = cargo.getFloatCountType();
        int tillCountType = cargo.getTillCountType();

        POSBaseBeanModel pbbModel = new POSBaseBeanModel();
        PromptAndResponseModel pnrModel = new PromptAndResponseModel();
        StatusBeanModel sbModel = new StatusBeanModel();

        if ((floatCountType != FinancialCountIfc.COUNT_TYPE_NONE) ||
            (tillCountType != FinancialCountIfc.COUNT_TYPE_NONE))
        {
            pnrModel.setArguments("");
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
            pbbModel.setPromptAndResponseModel(pnrModel);
            pbbModel.setStatusBeanModel(sbModel);
            ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, pbbModel);
        }

        // Get financial count
        FinancialTotalsIfc fc = cargo.getRegister().getTillByID(cargo.getTillID()).getTotals();

        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            MAXTillCountReport report = new MAXTillCountReport();
            report.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
            report.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
         // cChanges done for code merging(commenting below lines for error resolving)
            /*report.setTillCountTillAtReconcile(cargo.getRegister().getTillCountTillAtReconcile());*/
            report.setTillID(cargo.getTillID());
            report.setCashierID(cargo.getOperator().getEmployeeID());
            

            if (floatCountType != FinancialCountIfc.COUNT_TYPE_NONE)
            {
                FinancialCountIfc endFloat = fc.getEndingFloatCount().getEntered();
                report.setTillCount(endFloat);
                report.setTillCountType(TillCountReport.END_FLOAT);

                String sep = System.getProperty("line.separator");
                StringBuffer sepBuffer = new StringBuffer();
                for (int i = 0; i < 6; i++)
                {
                    sepBuffer.append(sep);
                }
				//Changes for rev 1.1 starts
             // Changes done for code merging(commenting below lines for error resolving)
                //pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, report.getFormattedReport() + sepBuffer.toString());
                PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(
                        PrintableDocumentManagerIfc.TYPE);
                pdm.printReceipt((SessionBusIfc)bus, report);
				//changes for rev 1.1 ends
                pda.cutPaper(97); // cut paper 97% of its width

                sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
            }

            if (tillCountType != FinancialCountIfc.COUNT_TYPE_NONE)
            {
                FinancialCountIfc enter = fc.getCombinedCount().getEntered();
                report.setTillCount(enter);
                report.setTillCountType(TillCountReport.CLOSE);
                String sep = System.getProperty("line.separator");
                StringBuffer sepBuffer = new StringBuffer();
                for (int i = 0 ; i < 6 ; i++)
                {
                    sepBuffer.append(sep);
                }
				//Changes for rev 1.1 starts
                //Changes done for code merging(commenting below lines for error resolving)
               // pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, report.getFormattedReport() + sepBuffer.toString());
                PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(
                        PrintableDocumentManagerIfc.TYPE);
                pdm.printReceipt((SessionBusIfc)bus, report);
				//Changes for rev 1.1 ends
                pda.cutPaper(97); // cut paper 97% of its width

                sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
            }
        }
        catch (DeviceException e)
        {

            // Update printer status
            logger.error( "PrintReceipt exception ");
            logger.error( "" + e + "");
            logger.error( "" + e.getOrigException() + "");
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
            mailLetter = false;

            String msg[] = new String[1];

            UtilityManagerIfc utility =
              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

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
}

