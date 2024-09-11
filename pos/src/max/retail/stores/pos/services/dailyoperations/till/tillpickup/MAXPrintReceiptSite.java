/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.
  
  Rev 1.0  03/Sep/2013	Prateek			Changes done to supress print in training mode.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.till.tillpickup;

// Java imports
import java.io.Serializable;
import java.util.Properties;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.gui.InternationalTextSupport;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.till.tillpickup.TillPickupCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Prints receipt after a till pickup.
    <P>
    @version $Revision: 3$
**/
//------------------------------------------------------------------------------
public class MAXPrintReceiptSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 3$";
    /**
       PrintReceiptSite
    **/
    public static final String SITENAME = "PrintReceiptSite";
    /**
        pickup text tag
    **/
    protected static String PICKUP_TEXT_TAG = "PickupTextFull";
    /**
        pickup text
    **/
    protected static String PICKUP_TEXT = "pickup.";
    /**
       parameter string for Till Pickup Receipts
    **/
    public static final String tillPickupReceiptCount = "NumberTillPickupReceipts";
    /**
       parameter string for Pickup and Loan Receipt Signature Line Printing
    **/
    protected static final String PICKUP_AND_LOAN_PRINT_SIGNATURE = "PickupAndLoanReceiptSignatureLinePrinting";
    /**
       parameter string for Operate with Safe
    **/
    protected static final String OPERATE_WITH_SAFE = "OperateWithSafe"; 
    /**
       parameter string for Pickup and Loan Receipt Signature Line Printing
    **/
    protected static final String PRINT_SIGNATURE = "PickupAndLoanReceiptSignatureLinePrinting"; 

    //--------------------------------------------------------------------------
    /**
       Collects the Till pickup count then calls printReport to
       print the count. Displays a screen that printing is occurring.
       Catches device exceptions and displays a dialog to allow the user
       to Retry or Cancel. If parameter TillCountTillPickup is set to No
       then do not count the pickup amount, otherwise count the pickup
       amount based upon this parameter's value (Summary/Detail).
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        Letter letter = null;
        TillPickupCargo cargo = (TillPickupCargo) bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        StringBuffer argumentText = new StringBuffer(cargo.getTenderName());
        String pickupText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                           BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                                           PICKUP_TEXT_TAG,
                                           PICKUP_TEXT);
        argumentText.append(" ").append(pickupText).append("  ");
        pandrModel.setArguments(argumentText.toString());

        baseModel.setPromptAndResponseModel(pandrModel);
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        baseModel.setStatusBeanModel(statusModel);
        ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, baseModel);

        try
        {
        	TransactionIfc transaction = cargo.getTransaction();
        	if(transaction instanceof TillAdjustmentTransaction)
        	{	if(!transaction.isTrainingMode())
        			printReport(bus);
        	}
        	else        			
        		printReport(bus);
            letter = new Letter("Success");
        }
        catch (DeviceException e)
        {
            // Update printer status
            logger.error(bus.getServiceName() + ": PrintReceipt exception ");
            logger.error(e);
            logger.error(e.getOrigException());
            statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

            String msg[] = new String[1];
            msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                                                BundleConstantsIfc.PRINTER_OFFLINE);

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryContinue");
            model.setType(DialogScreensIfc.RETRY_CONTINUE);
            model.setArgs(msg);
            model.setStatusBeanModel(statusModel);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    //--------------------------------------------------------------------------
    /**
       Print the report
    **/
    //--------------------------------------------------------------------------
    private void printReport(BusIfc bus) throws DeviceException
    {
        TillPickupCargo cargo = (TillPickupCargo) bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        String[] footer  = {};
        String[] header  = null;

        Integer receiptCount = new Integer(1);
        boolean operateWithSafe = true;
        int printSignatureCount = 1;
        try
        {
             receiptCount = pm.getIntegerValue(tillPickupReceiptCount);
             Serializable header_values[] = pm.getParameterValues("ReceiptHeader");
             header  = new String[header_values.length];
             System.arraycopy(header_values, 0, header, 0, header_values.length);
             printSignatureCount = pm.getIntegerValue(PICKUP_AND_LOAN_PRINT_SIGNATURE).intValue();
             operateWithSafe = pm.getBooleanValue(OPERATE_WITH_SAFE).booleanValue();
         }
         catch (ParameterException e)
         {
             logger.error(e);
         }

        if (!operateWithSafe)
        {
            // The minimum signature count is 1 when not operating with safe.
            if (printSignatureCount < 1)
            {
                printSignatureCount = 1;
            }
            
            // When not operating with safe, receipt must be printed even when Parameter setting 'Till Pickup Receipt Print Control = 0'. 
            if (receiptCount.intValue() < 1)
            {
                receiptCount = new Integer(1);
            }
        }

        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
        EYSPrintableDocumentIfc receipt = null;
        TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
        String nationality = cargo.getTenderNationality();

        // get properties for receipt
        // cChanges done for code merging(commenting below lines for error resolving)
        /*Properties props = InternationalTextSupport.getInternationalBeanText
        ("receipt",
                UtilityManagerIfc.RECEIPT_BUNDLES);*/

        if (cargo.getPickupCountType() != FinancialCountIfc.COUNT_TYPE_NONE ||cargo.getTenderName().equals(tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK)))
        {
            // Get financial count
            FinancialTotalsIfc fc = cargo.getRegister().getTillByID(cargo.getTillID()).getTotals();
            // Get all the pickups in this till
            ReconcilableCountIfc[] tillPickups = fc.getTillPickups();
            // This pickup is the last in the Pickups array
            FinancialCountIfc count = tillPickups[tillPickups.length-1].getEntered();
         // cChanges done for code merging(commenting below lines for error resolving)
            /*receipt = new TillAdjustmentsReport(cargo.getTransaction(),
                                                header, footer,
                                                count, cargo.getPickupCountType(),
                                                false, receiptCount.intValue(),
                                                printSignatureCount,
                                                nationality);
            receipt.setProps(props);*/
            pda.printDocument( receipt );
        }
        else if (cargo.getPickupCountType() == FinancialCountIfc.COUNT_TYPE_NONE &&cargo.getTenderName().indexOf(tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH)) >= 0)
        {
        	// cChanges done for code merging(commenting below lines for error resolving)
            /*receipt = new TillAdjustmentsReport(cargo.getTransaction(),
                                                header, footer,
                                                null, cargo.getPickupCountType(),
                                                false, receiptCount.intValue(),
                                                printSignatureCount,
                                                nationality);
            receipt.setProps(props);
            pda.printDocument( receipt );*/
        }
    }
}
