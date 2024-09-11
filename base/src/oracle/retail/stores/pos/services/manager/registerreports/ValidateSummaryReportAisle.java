/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/ValidateSummaryReportAisle.java /main/16 2013/06/18 17:10:18 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nsrao     11/04/14 - Corrected the method argument to display the Till Id in 
 *                         the Invalid Till dialog instead of the Date.
 *    vbongu    10/08/14 - Changed the error message to show an appropriate error
 *                         on selecting a till id which doesn't belong to a valid 
 *                         business date
 *    abhinavs  06/18/13 - Fix to validate blank business date and show a meaningful message 
 *    rabhawsa  06/17/13 - added null check on business date.
 *    mkutiana  10/11/11 - Appropriately formatting date - locale specific
 *    mchellap  09/29/11 - Fixed VAT total in summary reports
 *    cgreene   09/23/11 - refactored reports into separate methods
 *    vtemker   03/03/11 - Changes for Print Preview Reports Quickwin
 *    cgreene   05/26/10 - convert to oracle packaging
 *    yiqzhao   03/12/10 - add date as part of the search criteria and store
 *                         close status is 2 not 0.
 *    abondala  01/03/10 - update header date
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         6/4/2007 3:55:37 PM    Alan N. Sinton  CR
 *         26481 - Changes per review comments.
 *    5    360Commerce 1.4         5/15/2007 4:03:09 PM   Alan N. Sinton  CR
 *         26481 - Phase one for VAT modifications to ORPOS <ARG> Summary
 *         Reports.
 *    4    360Commerce 1.3         4/26/2007 3:47:22 PM   Mathews Kochummen fix
 *          hang when run in other locales that have localized strings
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *
 *   Revision 1.10  2004/07/23 17:57:15  jdeleau
 *   @scr 5183 Correct Flow for register reports on database error.
 *
 *   Revision 1.9  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.8  2004/07/15 17:12:13  jdeleau
 *   @scr 5183 Register Reports should go to manager options screen when
 *   a database offline error occurs.
 *
 *   Revision 1.7  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/05/14 19:29:34  jdeleau
 *   @scr 2687 Make Store Safe only appear on store reports.  Also make
 *   sure decimal points line up on summary report, fix negative over/short
 *   values appearing when they should be 0.
 *
 *   Revision 1.5  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.4  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:50:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:46  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jul 17 2003 14:27:34   sfl
 * Set the report type argument value before displaying the reporting is printing message.
 * Resolution for POS SCR-3181: Printing Register Reports - Message Prompt is incorrect.
 *
 *    Rev 1.4   Jun 26 2003 15:36:02   bwf
 * Do not internationalize when checking till type.  It is in english.
 * Resolution for 2613: Internationalization: try to print till summary report, POS client hangs up.
 *
 *    Rev 1.3   Sep 25 2002 13:35:30   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 14 2002 21:22:02   baa
 * retrieve report types from the site
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   17 May 2002 15:26:54   baa
 * externalized report types
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   16 May 2002 17:35:40   baa
 * ils
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.0   08 May 2002 19:19:52   baa
 * Initial revision.
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.0   Mar 18 2002 11:36:46   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:24:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

import java.util.ArrayList;
import java.util.HashMap;

import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.arts.StoreDataTransaction;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusAndTotalsIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.TaxTotalsIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.reports.ReportTypeConstantsIfc;
import oracle.retail.stores.pos.reports.SummaryReport;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.SummaryReportBeanModel;

/**
 * Validates the user entered register report data to ensure the till number is
 * valid, the register number is valid, and the entered business day is valid.
 * Displays an error screen informing the user if any of the entered data is
 * invalid.
 * 
 * @version $Revision: /main/16 $
 */
public class ValidateSummaryReportAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -164815622319560468L;

    /** revision number for this class */
    public static final String revisionNumber = "$Revision: /main/16 $";

    public static final String LANENAME = "ValidateSummaryReportAisle";
    private static final int STORE = 0;
    private static final int REGISTER = 1;
    private static final int TILL = 2;

    /**
     * Retrieves the user entered data, displays the Print Standby screen,
     * retrieves the report data if the business day, till number, and register
     * number are valid.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
    	RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
    	StoreIfc store = cargo.getStoreStatus().getStore();
    	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
    	SummaryReportBeanModel beanModel = (SummaryReportBeanModel)ui.getModel(POSUIManagerIfc.SUMMARY_REPORT);
    	EYSDate date = beanModel.getBusinessDate();
    	String tillRegNum = beanModel.getTillRegNumber();
    	String dateString="";
    	if (date != null)
    	{
    		dateString = date.toFormattedString();
    	}
    	String reportType = beanModel.getSelectedType();
    	PrintableDocumentManagerIfc pdm =
    			(PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
    	SummaryReport report = (SummaryReport)pdm.getParameterBeanInstance(ReportTypeConstantsIfc.SUMMARY_REPORT);
    	boolean mailLetter = true;
    	UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

    	ArrayList allReportTypes = beanModel.getReportTypesModel();
    	String storeType = (String)allReportTypes.get(STORE);
    	String registerType = (String)allReportTypes.get(REGISTER);
    	String tillType = (String)allReportTypes.get(TILL);
    	String OverrideAthyName = utility.retrieveText("Common",
    			BundleConstantsIfc.MANAGER_BUNDLE_NAME,
    			"OverrideTax",
    			"");
    	
    	if(StringUtils.isEmpty(dateString))
    	{
    		String promptText = utility.retrieveText
    				("Common",
    						BundleConstantsIfc.COMMON_BUNDLE_NAME,
    						"BusinessDay",
    						"");
    		showErrorDialog(bus, promptText, "BLANK_BUSINESS_DAY", new DataException(DataException.DATA_FORMAT));
    	}

    	else 
    	{
    		if (reportType != null && reportType.equals(tillType))
    		{
    			mailLetter = reportOnTill(bus, store, beanModel, date, tillRegNum, dateString,
    					report, OverrideAthyName);
    		}
    		else if (reportType != null && reportType.equals(registerType))
    		{
    			mailLetter = reportOnRegister(bus, store, beanModel, date, tillRegNum, dateString,
    					report, OverrideAthyName);
    		}
    		else if (reportType != null && reportType.equals(storeType))
    		{
    			mailLetter = reportOnStore(bus, store, date, dateString, report, OverrideAthyName);
    		}
    		else
    		{
    			logger.error("ValidateSummaryReportAisle.getTotals() Invalid summary Report Type");
    			mailLetter = false;
    		}
    		if (mailLetter)
    		{
    			PromptAndResponseModel parModel = new PromptAndResponseModel();
    			POSBaseBeanModel baseModel = new POSBaseBeanModel();

    			parModel.setArguments(reportType);
    			baseModel.setPromptAndResponseModel(parModel);

    			report.setStoreID(store.getStoreID());
    			report.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
    			report.setCashierID(cargo.getOperator().getEmployeeID());
    			report.setStartDate(date);            
    			report.getFinancialEntity().getTotals().setVatEnabled(isVATEnabled());            
    			cargo.setReport(report);

    			if ("Preview".equals(bus.getCurrentLetter().getName()))
    			{
    				bus.mail(new Letter("PrintPreview"), BusIfc.CURRENT);
    			}
    			else
    			{
    				ui.showScreen(POSUIManagerIfc.PRINT_REPORT, baseModel);
    				bus.mail(new Letter("ValidSummary"), BusIfc.CURRENT);
    			}
    		}
    	}
    }

    /**
     * @param bus
     * @param store
     * @param date
     * @param dateString
     * @param report
     * @param taxOverrideAuthorityName
     * @return
     */
    protected boolean reportOnStore(BusIfc bus, StoreIfc store, EYSDate date,
            String dateString, SummaryReport report, String taxOverrideAuthorityName)
    {
        try
        {
            StoreDataTransaction sdt = (StoreDataTransaction)DataTransactionFactory
                    .create(DataTransactionKeys.STORE_DATA_TRANSACTION);

            StoreStatusIfc storeStat = sdt.readStoreStatus(store, date);
            FinancialTotalsIfc storeTotals = sdt.readStoreTotals(store, date);
            StoreStatusAndTotalsIfc sst = DomainGateway.getFactory().getStoreStatusAndTotalsInstance();
            sst.copyStoreStatus(storeStat);
            TaxTotalsIfc[] taxTotals = storeTotals.getTaxes().getTaxTotals();
            for (int i = 0; i < taxTotals.length; i++)
            {
                if (taxTotals[i].getTaxAuthorityId() == 0)
                {
                    taxTotals[i].setTaxAuthorityName(taxOverrideAuthorityName);
                }
            }
            sst.setTotals(storeTotals);
            report.setFinancialEntity(sst);
        }
        catch (DataException ex)
        {
            showErrorDialog(bus, dateString, RegisterReportsCargo.INVALID_BUSINESS_DAY, ex);
            return false;
        }
        return true;
    }

    /**
     * @param bus
     * @param store
     * @param beanModel
     * @param date
     * @param tillRegNum
     * @param dateString
     * @param report
     * @param taxOverrideAuthorityName
     * @return
     */
    protected boolean reportOnRegister(BusIfc bus, StoreIfc store, SummaryReportBeanModel beanModel,
            EYSDate date, String tillRegNum, String dateString,
            SummaryReport report, String taxOverrideAuthorityName)
    {
        FinancialTotalsDataTransaction ftdt = (FinancialTotalsDataTransaction)DataTransactionFactory
                .create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);

        try
        {
            // If it's for register see if register exists
            WorkstationIfc workStation = DomainGateway.getFactory().getWorkstationInstance();
            workStation.setWorkstationID(tillRegNum);
            workStation.setStore(store);
            ftdt.readRegisterStatus(workStation);
        }
        catch (DataException ex)
        {
            showErrorDialog(bus, beanModel.getTillRegNumber(), RegisterReportsCargo.INVALID_REGISTER, ex);
            return false;
        }
        try
        {
            RegisterIfc[] registers = ftdt.readRegister(store.getStoreID(), tillRegNum, date);

            if (registers.length > 1)
            {
                for (int cnt = 1; cnt < registers.length; cnt++)
                {
                    FinancialTotalsIfc ftc = registers[cnt].getTotals();
                    TaxTotalsIfc[] taxTotals = ftc.getTaxes().getTaxTotals();
                    for (int i = 0; i < taxTotals.length; i++)
                    {
                        if (taxTotals[i].getTaxAuthorityId() == 0)
                        {
                            taxTotals[i].setTaxAuthorityName(taxOverrideAuthorityName);
                        }
                    }

                    registers[0].addTotals(ftc);
                }
            }
            FinancialTotalsIfc ftc = registers[0].getTotals();
            TaxTotalsIfc[] taxTotals = ftc.getTaxes().getTaxTotals();
            for (int i = 0; i < taxTotals.length; i++)
            {
                if (taxTotals[i].getTaxAuthorityId() == 0)
                {
                    taxTotals[i].setTaxAuthorityName(taxOverrideAuthorityName);
                }
            }

            report.setFinancialEntity(registers[0]);
        }
        catch (DataException ex)
        {
            showErrorDialog(bus, dateString, RegisterReportsCargo.INVALID_BUSINESS_DAY, ex);
            return false;
        }
        return true;
    }

    /**
     * @param store
     * @param ui
     * @param beanModel
     * @param date
     * @param tillRegNum
     * @param errorString
     * @param dateString
     * @param report
     * @param utility
     * @param taxOverrideAuthorityName
     * @return
     */
    protected boolean reportOnTill(BusIfc bus, StoreIfc store,
            SummaryReportBeanModel beanModel, EYSDate date,
            String tillRegNum, String dateString,
            SummaryReport report, String taxOverrideAuthorityName)
    {
        FinancialTotalsDataTransaction ftdt;
        ftdt = (FinancialTotalsDataTransaction)DataTransactionFactory
                .create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
        try
        {
            // See if till exists
            ftdt.readTillStatus(store, tillRegNum);
        }
        catch (DataException ex)
        {
            showErrorDialog(bus, beanModel.getTillRegNumber(), RegisterReportsCargo.INVALID_TILL, ex);
            return false;
        }
        try
        {
            TillIfc[] tills = ftdt.readTillTotals(store.getStoreID(), tillRegNum, date);

            if (tills.length > 1)
            {
                for (int cnt = 1; cnt < tills.length; cnt++)
                {
                    FinancialTotalsIfc ftc = tills[cnt].getTotals();
                    TaxTotalsIfc[] taxTotals = ftc.getTaxes().getTaxTotals();
                    for (int i = 0; i < taxTotals.length; i++)
                    {
                        if (taxTotals[i].getTaxAuthorityId() == 0)
                        {
                            taxTotals[i].setTaxAuthorityName(taxOverrideAuthorityName);
                        }
                    }
                    tills[0].addTotals(ftc);
                }
            }
            FinancialTotalsIfc ftc = tills[0].getTotals();
            TaxTotalsIfc[] taxTotals = ftc.getTaxes().getTaxTotals();
            for (int i = 0; i < taxTotals.length; i++)
            {
                if (taxTotals[i].getTaxAuthorityId() == 0)
                {
                    taxTotals[i].setTaxAuthorityName(taxOverrideAuthorityName);
                }
            }

            report.setFinancialEntity(tills[0]);
        }
        catch (DataException ex)
        {
            showErrorDialog(bus, tillRegNum, RegisterReportsCargo.INVALID_TILL, ex);
            return false;
        }
        return true;
    }

    protected void showErrorDialog(BusIfc bus, String invalidId, String errorCode, DataException ex)
    {
    	logger.error(ex);
    	UtilityManagerIfc utility =  (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
    	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
    	String errorString[] = new String[2]; 
    	if (ex.getErrorCode() != DataException.NO_DATA && ex.getErrorCode()!=DataException.DATA_FORMAT)
    	{
    		errorString[0] = utility.getErrorCodeString(ex.getErrorCode());
    		HashMap<Integer,String> map = new HashMap<Integer,String>(1);
    		map.put(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);
    		showDialogScreen(ui, errorString, RegisterReportsCargo.DATABASE_ERROR, map);
    	}
    	else
    	{
    		errorString[0] = invalidId;
    		showDialogScreen(ui, errorString, errorCode, null);
    	}
    }

    /**
     * Set the args in the ui model and display the error dialog.
     * 
     * @param ui POSUIManagerIfc
     * @param args String array for the text to display on the dialog
     * @param id String identifier for the configuration of the dialog
     */
    private void showDialogScreen(POSUIManagerIfc ui, String[] args, String id, HashMap<Integer,String> buttonToLetterMapping)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(id);
        model.setType(DialogScreensIfc.ERROR);

        if(buttonToLetterMapping != null)
        {
            for(Integer buttonName : buttonToLetterMapping.keySet())
            {
                String letter = buttonToLetterMapping.get(buttonName);
                model.setButtonLetter(buttonName.intValue(), letter);
            }
        }
        // there will be no args for info not found.
        if (args[0] != null)
        {
            model.setArgs(args);
        }

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    private boolean isVATEnabled()
    {
        return Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);
    }

}
