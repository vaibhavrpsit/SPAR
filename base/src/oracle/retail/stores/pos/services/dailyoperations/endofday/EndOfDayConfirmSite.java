/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/endofday/EndOfDayConfirmSite.java /main/14 2012/05/02 12:32:15 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  05/01/12 - Fortify: fix redundant null checks, part 3
 *    mjwallac  04/24/12 - Fixes for Fortify redundant null check
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/16/09 - set the expected amount to be equal to the entered
 *                         amount for till pickups and loans
 *    sgu       04/15/09 - display sub tender type for credit tender in end of
 *                         day financial summary screen
 *
 * ===========================================================================
 * $Log:
 *  6    360Commerce 1.5         7/17/2007 2:36:32 PM   Anda D. Cadar   do not
 *       display the ISO currencyCode for base currency
 *  5    360Commerce 1.4         5/23/2007 7:10:48 PM   Jack G. Swan    Fixed
 *       issues with tills and CurrencyID.
 *  4    360Commerce 1.3         4/25/2007 8:52:34 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:21:23 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 * $
 * Revision 1.12  2004/07/30 21:10:53  dcobb
 * @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 * Replaced deprecated getTenderItemByDescription(String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
 *
 * Revision 1.11  2004/06/29 17:05:38  cdb
 * @scr 4205 Removed merging of money orders into checks.
 * Added ability to count money orders at till reconcile.
 *
 * Revision 1.10  2004/06/22 00:13:23  cdb
 * @scr 4205 Updated to merge money orders into checks during till reconcile.
 *
 * Revision 1.9  2004/06/03 14:47:44  epd
 * @scr 5368 Update to use of DataTransactionFactory
 *
 * Revision 1.8  2004/05/26 19:57:39  jriggins
 * @scr 5160 Code review updates
 *
 * Revision 1.7  2004/05/18 19:57:29  jriggins
 * @scr 5160 Changed various tender type labels to use ISO descriptions
 *
 * Revision 1.6  2004/04/20 13:13:09  tmorris
 * @scr 4332 -Sorted imports
 *
 * Revision 1.5  2004/04/14 15:17:10  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.4  2004/03/03 23:15:11  bwf
 * @scr 0 Fixed CommonLetterIfc deprecations.
 *
 * Revision 1.3  2004/02/12 16:49:37  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:46:17  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11 01:04:15
 * cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.0 Aug 29 2003 15:56:28 CSchellenger Initial revision.
 *
 * Rev 1.5 Jul 02 2003 13:48:38 bwf Added code to get correct alternate
 * currency tender text. Resolution for 2952: Internationalization -EOD Summary
 * Report has Tag for Canadian Currency
 *
 * Rev 1.4 Mar 11 2003 17:03:50 bwf Database Internationalization Resolution
 * for 1866: I18n Database support
 *
 * Rev 1.3 Mar 04 2003 09:28:24 RSachdeva Clean Up Code Conversion Resolution
 * for POS SCR-1740: Code base Conversions
 *
 * Rev 1.2 Aug 19 2002 14:18:52 RSachdeva Code conversion Resolution for POS
 * SCR-1740: Code base Conversions
 *
 * Rev 1.1 23 May 2002 17:44:04 vxs Removed unneccessary concatenations in
 * logging statements. Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 * Rev 1.0 Apr 29 2002 15:31:10 msg Initial revision.
 *
 * Rev 1.1 Mar 18 2002 23:13:46 msg - updated copyright
 *
 * Rev 1.0 Mar 18 2002 11:26:34 msg Initial revision.
 *
 * Rev 1.3 05 Mar 2002 14:19:28 epd Fixed screen which shows store totals
 * Resolution for POS SCR-1443: No Canadian Cash line on Store Financial Totals
 * Summary screen
 *
 * Rev 1.2 04 Mar 2002 12:37:50 pdd Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 * Rev 1.1 25 Feb 2002 17:33:46 baa display end of day screen Resolution for
 * POS SCR-1413: Financial info missing from EOD Summary screen
 *
 * Rev 1.0 Sep 21 2001 11:16:26 msg Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.endofday;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.StoreDataTransaction;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.FinancialTotalsSummaryEntry;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//--------------------------------------------------------------------------
/**
 * Displays a confirmation screen, confirming the operator's intention to close
 * the store.
 * <P>
 *
 * @version $Revision: /main/14 $
 */
//--------------------------------------------------------------------------
public class EndOfDayConfirmSite extends PosSiteActionAdapter
{ // begin class EndOfDayConfirmSite
    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Hold all the tender names associated with pickups; this is temporary
     * variable used to build the model.
     */
    protected transient Vector tenderDesc = new Vector();
    /**
     * Hold all the tender amounts associated with pickups; this is temporary
     * variable used to build the model.
     */
    protected transient Vector tenderAmount = new Vector();
    /**
     * Starting Float tag
     */
    public static final String STARTING_FLOAT_TAG = "StartingFloat";
    /**
     * Starting Float default text
     */
    public static final String STARTING_FLOAT_TEXT = "Starting Float";
    /**
     * Ending Float tag
     */
    public static final String ENDING_FLOAT_TAG = "EndingFloat";
    /**
     * Ending Float default text
     */
    public static final String ENDING_FLOAT_TEXT = "Ending Float";
    /**
     * Till Loans tag
     */
    public static final String TILL_LOANS_TAG = "TillLoans";
    /**
     * Till Loans default text
     */
    public static final String TILL_LOANS_TEXT = "Loans";
    /**
     * Pickups tag and default text
     */
    public static final String PICK_UPS_TAG = "Pickups";
    /**
     * Desc Pattern Tag
     */
    public static final String DESC_TAG = "Desc";
    /**
     * Desc default pattern text
     */
    public static final String DESC_PATTERN_TEXT = "{0}{1}{2}{3}({4})";

    /**
     * Desc pattern text  for base currency
     */
    public static final String DESC_PATTERN_BASE_TEXT = "{0}{1}{2}{3}";

    /**
     * Desc Pattern Tag
     */
    public static final String DESC_BASE_TAG = "DescBase";
    /**
     * Financial Totals Summary Entry Spec
     */
    public static final String FINANCIAL_TOTALS_SUMMARY_ENTRY_SPEC =
        "FinancialTotalsSummaryEntrySpec";

    //--------------------------------------------------------------------------
    /**
     * Displays a confirmation dialog, confirming the operators intention to
     * close the store.
     * <P>
     *
     * @param bus
     *            the bus arriving at this site
     */
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Clean out and leftover values from a previous traversal
        tenderDesc = new Vector();
        tenderAmount = new Vector();

        // get cargo reference
        EndOfDayCargo cargo = (EndOfDayCargo) bus.getCargo();

        try
        {
            StoreStatusIfc ss = (StoreStatusIfc) cargo.getStoreStatus().clone();

            StoreDataTransaction dt = null;

            dt = (StoreDataTransaction) DataTransactionFactory.create(DataTransactionKeys.STORE_DATA_TRANSACTION);

            FinancialTotalsIfc ft = dt.readStoreTotals(ss);

            FinancialCountTenderItemIfc[] tendersArray = ft.getCombinedCount().getExpected()
                .getSummaryTenderItems();
            ArrayList list = new ArrayList();
            if (tendersArray != null)
            {
                list = new ArrayList(Arrays.asList(tendersArray));
            }

            cargo.setStoreTotals(ft);

            // get ui handle
            POSUIManagerIfc ui =
                (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

            // set bean model
            ListBeanModel model = new ListBeanModel();
            model.setListModel(createListModel(cargo.getStoreTotals()));
            ui.showScreen(
                POSUIManagerIfc.STORE_FINANCIAL_TOTALS_SUMMARY,
                model);
        }
        catch (DataException e)
        {
            cargo.setDataExceptionErrorCode(e.getErrorCode());
            bus.mail(new Letter(CommonLetterIfc.DB_ERROR), BusIfc.CURRENT);
        }

    }

    //---------------------------------------------------------------------
    /**
     * Creates list model object from financial totals.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     *
     * @param totals
     *            FinancialTotals object.
     */
    //---------------------------------------------------------------------
    public POSListModel createListModel(FinancialTotalsIfc totals)
    { // begin createListModel()
        POSListModel listModel = new POSListModel();

        FinancialCountTenderItemIfc[] enteredItems =
            totals.getCombinedCount().getEntered().getTenderItems();
        FinancialCountTenderItemIfc enteredItem = null;
        FinancialCountTenderItemIfc expectedItem = null;
        FinancialCountIfc expectedItems =
            totals.getCombinedCount().getExpected();
        CurrencyIfc expectedAmt = DomainGateway.getBaseCurrencyInstance();
        FinancialTotalsSummaryEntry ftse = null;
        // get the utility manager
        UtilityManagerIfc utility =
            (UtilityManagerIfc) Gateway.getDispatcher().getManager(
                UtilityManagerIfc.TYPE);

        // Add Tenders to the the list model
        for (int i = 0; i < enteredItems.length; i++)
        {
            if (enteredItems[i].isSummary())
            {
                expectedItem =
                    expectedItems.getTenderItem(
                        enteredItems[i].getTenderDescriptor(),
                        false);
                expectedAmt.setZero();
                if (expectedItem == null)
                {
                    logger.info(
                        "EndOfDayConfirmSite.createListModel(); Matching expected tender for "
                            + enteredItems[i].getDescription()
                            + " not found.");
                }
                else
                {
                    expectedAmt = expectedItem.getAmountTotal();
                
                    CurrencyIfc zero = (CurrencyIfc)expectedAmt.clone();
                    zero.setZero();
    
                    if (expectedItem != null
                            && (expectedItem.getNumberItemsIn() != 0
                            || expectedItem.getNumberItemsOut() != 0
                            || expectedItem.getNumberItemsTotal() != 0
                            || enteredItems[i].getNumberItemsIn() != 0
                            || enteredItems[i].getNumberItemsOut() != 0
                            || enteredItems[i].getNumberItemsTotal() != 0))
                    {
                        ftse = new FinancialTotalsSummaryEntry();
    
                        // get Correct Locale based Tender Type descriptor
                        ftse.setType(getTenderTypeText(utility, expectedItem));
    
                        ftse.setExpected((CurrencyIfc) expectedAmt.clone());
                        ftse.setEntered(enteredItems[i].getAmountTotal());
                        listModel.addElement(ftse);
                    }
                }
            }
        }

        // Add Starting Float to list model
        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        td.setCountryCode(DomainGateway.getBaseCurrencyInstance().getCountryCode());
        td.setCurrencyID(DomainGateway.getBaseCurrencyType().getCurrencyId());

        enteredItem =
            totals
                .getStartingFloatCount()
                .getEntered()
                .getSummaryTenderItemByDescriptor(
                td);

        ftse = new FinancialTotalsSummaryEntry();
        String startingFloat =
            utility.retrieveText(
                FINANCIAL_TOTALS_SUMMARY_ENTRY_SPEC,
                BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                STARTING_FLOAT_TAG,
                STARTING_FLOAT_TEXT);
        ftse.setType(startingFloat);
        ftse.setDisplayExpected(true);
        if (enteredItem == null)
        {
            logger.info(
                "EndOfDayConfirmSite..createListModel(); Entered Starting Float not found.");
            ftse.getEntered().setZero();
        }
        else
        {
            ftse.setEntered(enteredItem.getAmountTotal());
        }
        listModel.addElement(ftse);

        // Add Ending Float to list model
        enteredItem =
            totals
                .getEndingFloatCount()
                .getEntered()
                .getSummaryTenderItemByDescriptor(
                td);
        ftse = new FinancialTotalsSummaryEntry();
        ftse.setDisplayExpected(true);
        String endingFloat =
            utility.retrieveText(
                FINANCIAL_TOTALS_SUMMARY_ENTRY_SPEC,
                BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                ENDING_FLOAT_TAG,
                ENDING_FLOAT_TEXT);
        ftse.setType(endingFloat);
        if (enteredItem == null)
        {
            logger.info(
                "EndOfDayConfirmSite.createListModel(); Entered Ending Float not found.");
            ftse.getEntered().setZero();
        }
        else
        {
            ftse.setEntered(enteredItem.getAmountTotal());
        }
        listModel.addElement(ftse);

        // Add Loans to list model
        ReconcilableCountIfc[] rc = totals.getTillLoans();
        CurrencyIfc enteredAmt = DomainGateway.getBaseCurrencyInstance();

        for (int y = 0; y < rc.length; y++)
        {
            enteredItem =
                rc[y].getEntered().getSummaryTenderItemByDescriptor(td);
            enteredAmt = enteredAmt.add(enteredItem.getAmountTotal());
        }
        ftse = new FinancialTotalsSummaryEntry();
        String tillLoans =
            utility.retrieveText(
                FINANCIAL_TOTALS_SUMMARY_ENTRY_SPEC,
                BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                TILL_LOANS_TAG,
                TILL_LOANS_TEXT);
        ftse.setType(tillLoans);
        ftse.setEntered((CurrencyIfc) enteredAmt.clone());
        ftse.setDisplayExpected(true);
        //for loans the expected amount is equal to the entered amount
        ftse.setExpected(ftse.getEntered());
        listModel.addElement(ftse);

        // Add pickups to list model; build the vectors of descriptions and
        // amounts.
        buildPickTenderVectors(utility, totals.getTillPickups());

        if (tenderDesc.size() > 0)
        {
            for (int i = 0; i < tenderDesc.size(); i++)
            {
                ftse = new FinancialTotalsSummaryEntry();
                String desc = (String) tenderDesc.elementAt(i);
                ftse.setType(desc);
                enteredAmt = (CurrencyIfc) tenderAmount.elementAt(i);
                ftse.setEntered((CurrencyIfc) enteredAmt.clone());
                ftse.setDisplayExpected(true);
                //for pickups the expected amount is equal to the entered amount
                ftse.setExpected(ftse.getEntered());
                listModel.addElement(ftse);
            }
        }
        else
        {
            enteredAmt.setZero();
            ftse = new FinancialTotalsSummaryEntry();
            String pickups =
                utility.retrieveText(
                    FINANCIAL_TOTALS_SUMMARY_ENTRY_SPEC,
                    BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                    PICK_UPS_TAG,
                    PICK_UPS_TAG);
            ftse.setType(getTenderTypeText(utility, pickups));
            ftse.setEntered((CurrencyIfc) enteredAmt.clone());
            ftse.setDisplayExpected(true);
            listModel.addElement(ftse);
        }

        return listModel;
    } // end createListModel()

    //---------------------------------------------------------------------
    /**
     * Builds the PickupTender Vectors.
     * <P>
     *
     * @param ReconcilableCountIfc[]
     *            contains pickup info.
     */
    //---------------------------------------------------------------------
    protected void buildPickTenderVectors(UtilityManagerIfc utility, ReconcilableCountIfc[] rc)
    {
        // Get Pickups id
        String pickups =
            utility.retrieveText(
                FINANCIAL_TOTALS_SUMMARY_ENTRY_SPEC,
                BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                PICK_UPS_TAG,
                PICK_UPS_TAG);

        // Interate trough the pickup Reconcilable Count array...
        for (int i = 0; i < rc.length; i++)
        {
            // Get the summary pickup tender items...
            FinancialCountTenderItemIfc[] fcti =
                rc[i].getEntered().getSummaryTenderItems();
            for (int y = 0; y < fcti.length; y++)
            {
                // Get the amount and desciption for each count tender item...
                String desc = getTenderTypeText(utility, fcti[y], pickups);
                CurrencyIfc amount = fcti[y].getAmountTotal();

                // If the tender does not already appear in the vectors,
                // add new elements.
                int index = tenderDesc.indexOf(desc);
                if (index == -1)
                {
                    tenderDesc.addElement(desc);
                    tenderAmount.addElement(amount);
                }
                else
                // Otherwise, add the current amount to the accumulator.
                {
                    CurrencyIfc oAmount =
                        (CurrencyIfc) tenderAmount.elementAt(index);
                    amount = amount.add(oAmount);
                    tenderAmount.setElementAt(amount, index);
                }
            }
        }
    }

    //----------------------------------------------------------------------
    /**
     * Returns the internationalized alternate currency text.
     * <P>
     *
     * @param tempDesc
     *            temporary description
     * @param utility
     *            UtilityManagerIfc for internationalizing
     * @return String representation of internationalized text
     */
    //----------------------------------------------------------------------
    public String getInternationalizedAlternateCurrency(
        String tempDesc,
        UtilityManagerIfc utility)
    {
        int spaceIndex = 0;
        int j = 0;
        boolean flag = false;
        String tempString = null;
        while (j < tempDesc.length() && !flag)
        {
            if (tempDesc.charAt(j) == ' ')
            {
                spaceIndex = j;
                flag = true;
            }
            j++;
        }
        if (flag)
        {
            String tempString1 =
                tempDesc.substring(0, spaceIndex).concat(
                    tempDesc.substring(spaceIndex + 1, tempDesc.length()));
            tempString = utility.retrieveCommonText(tempString1);
        }
        else
        {
            tempString = tempDesc;
        }
        return (tempString);
    }

    //----------------------------------------------------------------------
    /**
     * Generates a description of the tender type to be displayed on the EOD Summary screen with tender type and ISO identifier.
     * In the case of Canadian Cash Pickups, the text would be:  Pickups (CAD)
     *
     * @param utility UtilityManagerIfc instance
     * @param transactionTypeText Type of register transaction (e.g. Loan, Pickup, etc.).
     * @return String representing the tender type including ISO descriptor
     */
    //----------------------------------------------------------------------
    protected String getTenderTypeText(UtilityManagerIfc utility, String transactionTypeText)
    {
        return getTenderTypeText(utility, null, transactionTypeText);
    }

    //----------------------------------------------------------------------
    /**
     * Generates a description of the tender type to be displayed on the EOD Summary screen with tender type and ISO identifier.
     * In the case of Canadian Cash, the text would be:  Cash (CAD)
     *
     * @param utility UtilityManagerIfc instance
     * @param fcTenderItem Contains tender type and ISO currency code information. Can be null.
     * @return String representing the tender type including ISO descriptor
     */
    //----------------------------------------------------------------------
    protected String getTenderTypeText(UtilityManagerIfc utility, FinancialCountTenderItemIfc fcTenderItem)
    {
        return getTenderTypeText(utility, fcTenderItem, "");
    }

    //----------------------------------------------------------------------
    /**
     * Generates a description of the tender type to be displayed on the EOD Summary screen with tender type and ISO identifier.
     * In the case of Canadian Cash Pickups, the text would be:  Cash Pickups (CAD)
     *
     * @param utility UtilityManagerIfc instance
     * @param fcTenderItem Contains tender type and ISO currency code information. Can be null.
     * @param transactionTypeText Type of register transaction (e.g. Loan, Pickup, etc.).
     * @return String representing the tender type including ISO descriptor
     */
    //----------------------------------------------------------------------
    protected String getTenderTypeText(UtilityManagerIfc utility, FinancialCountTenderItemIfc fcTenderItem,
                                       String transactionTypeText)
    {
        String tenderTypeText = "";

        // Get the text version of the tender type
        TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
        String formattedTenderType = "";
        String currencyCode = "";
        String tenderCurrencyCode = "";
        String baseCurrencyCode = DomainGateway.getBaseCurrencyType().getCurrencyCode();

        String pattern = utility.retrieveText(FINANCIAL_TOTALS_SUMMARY_ENTRY_SPEC,
                BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME, DESC_BASE_TAG, DESC_PATTERN_BASE_TEXT);



        if (fcTenderItem != null)
        {
        	String tenderDescriptor = tenderTypeMap.getDescriptor(fcTenderItem.getTenderType());
        	if (tenderDescriptor.equals(TenderTypeEnum.CREDIT.toString()))
        	{
        		formattedTenderType = utility.retrieveCommonText(fcTenderItem.getTenderSubType());
        	}
        	else
        	{
        		formattedTenderType = utility.retrieveCommonText(tenderDescriptor);
        	}

            tenderCurrencyCode = fcTenderItem.getAmountIn().getType().getCurrencyCode();
            // use ISO currency Code for alternate currencies
            if(!tenderCurrencyCode.equals(baseCurrencyCode))
            {
                currencyCode = tenderCurrencyCode;
//              Get the formatted string using the values
                pattern = utility.retrieveText(FINANCIAL_TOTALS_SUMMARY_ENTRY_SPEC,
                        BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME, DESC_TAG, DESC_PATTERN_TEXT);
            }
        }


        /*
         * Message Args
         * 0 = tender type text
         * 1 = space if arg 0 is not empty or empty string otherwise
         * 2 = register transaction type text (eg. Pickup, Loan,...can be empty too)
         * 3 = space if arg 2 is not empty or empty string otherwise
         * 4 = currency code
         */
        String msgArgs[] = new String[] { formattedTenderType,
                "".equals(formattedTenderType) ? "" : " ",
                transactionTypeText,
                "".equals(transactionTypeText) ? "" : " ",
                currencyCode};
        tenderTypeText = LocaleUtilities.formatComplexMessage(pattern, msgArgs);

        return tenderTypeText;
    }

    //----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     *
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
} // end class EndOfDayConfirmSite
