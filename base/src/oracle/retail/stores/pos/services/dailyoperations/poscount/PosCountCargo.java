/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/PosCountCargo.java /main/28 2014/01/24 16:58:49 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/24/14 - fix null dereferences
 *    cgreene   10/25/13 - remove currency type deprecations and use currency
 *                         code instead of description
 *    abondala  09/04/13 - initialize collections
 *    arabalas  12/26/12 - Till Summary Report Mismatched in Summary and Detail
 *    jswan     12/14/10 - Fixed additional issue found in testing.
 *    jswan     12/10/10 - Fixed comments.
 *    jswan     12/10/10 - Fixed issue with BlindCount at Till Reconcile whith
 *                         tenders which are not in the tenders to be counted
 *                         list.
 *    abhayg    10/15/10 - Fix for the TILL SUMMARY REPORTS DO NOT SHOW A
 *                         SHORTAGE OF CHECKS
 *    abhayg    10/15/10 - Fix for TILL SUMMARY REPORTS DO NOT SHOW A SHORTAGE
 *                         OF CHECKS
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sgu       02/09/10 - donot count mail bank check multiple times in till
 *                         detail count
 *    crain     02/09/10 - Forward Port: PROBLEM WITH ADDED TENDERS DURING TILL
 *                         RECONCILE
 *    abondala  01/03/10 - update header date
 *    jswan     08/18/09 - Modified due to code review.
 *    jswan     08/13/09 - Added the till reconcile amount and count to the
 *                         till, workstation and store tender history tables
 *                         and to code that reads from and writes to them.
 *    jswan     07/10/09 - Fix issues with Detail Tender Count and foreign
 *                         currency.
 *    jswan     07/10/09 - XbranchMerge jswan_bug-8669309 from
 *                         rgbustores_13.1x_branch
 *    jswan     07/09/09 - Fixed issues with detailed till tender counts;
 *                         foreign currency tender was causing base currency
 *                         tender to be counted twice. Also when the operator
 *                         did not count the cash, the reconciled = 0.
 *    acadar    03/20/09 - removed commented out code
 *    acadar    03/20/09 - use the correct tender descriptor when counting
 *                         summary
 *    cgreene   03/10/09 - set denoms onto fincount tenders
 *    jswan     02/23/09 - Modifications to support Mail Bank Checks in summary
 *                         reports.
 *    mahising  02/22/09 - Fixed issue for EOD summary screen
 *
 * ===========================================================================
 * $Log:
 *     9    360Commerce 1.8         6/4/2007 9:20:40 AM    Anda D. Cadar
 *          check if base or foreign currency is counted
 *     8    360Commerce 1.7         5/23/2007 7:10:48 PM   Jack G. Swan
 *          Fixed issues with tills and CurrencyID.
 *     7    360Commerce 1.6         5/8/2007 11:32:25 AM   Anda D. Cadar
 *          currency changes for I18N
 *     6    360Commerce 1.5         4/25/2007 8:52:32 AM   Anda D. Cadar   I18N
 *           merge
 *
 *     5    360Commerce 1.4         4/10/2007 2:52:47 PM   Michael Boyd    CR
 *          26172 - v7.2.2 merge to trunk
 *
 *
 *          5    .v7x      1.3.1.0     7/27/2006 8:04:51 AM   Keith L. Lesikar
 *           Do
 *          not check charge models when reconciling foreign currencies.
 *     4    360Commerce 1.3         1/22/2006 11:45:06 AM  Ron W. Haight
 *          removed references to com.ibm.math.BigDecimal
 *     3    360Commerce 1.2         3/31/2005 4:29:22 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:24:10 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:13:06 PM  Robert Pearse
 *    $
 *    Revision 1.12.2.1  2004/11/11 21:35:54  mwisbauer
 *    @scr 7590  Changed to look at sub tender type if pickup
 *
 *    Revision 1.12  2004/07/30 21:15:40  dcobb
 *    @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *    Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
 *
 *    Revision 1.10  2004/06/24 01:20:44  dcobb
 *    @scr 4205 Feature Enhancement: Till Options
 *    Add Foreign currency detail count.
 *
 *    Revision 1.9  2004/06/21 18:07:13  dcobb
 *    @scr 4205 Feature Enhancement: Till Options
 *    Add Foreign currency detail count.
 *
 *    Revision 1.8  2004/06/18 22:19:34  dcobb
 *    @scr 4205 Feature Enhancement: Till Options
 *    Add Foreign currency count.
 *
 *    Revision 1.7  2004/06/17 22:36:28  dcobb
 *    @scr 4205 Feature Enhancement: Till Options
 *    Add foreign currency to tender detail count interface.
 *
 *    Revision 1.6  2004/06/10 16:24:02  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Add foreign currency count.
 *
 *    Revision 1.5  2004/06/07 18:29:38  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Add foreign currency counts.
 *
 *    Revision 1.4  2004/04/09 16:56:02  cdb
 *    @scr 4302 Removed double semicolon warnings.
 *
 *    Revision 1.3  2004/02/12 16:49:38  mcs
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 21:45:40  rhafernik
 *    @scr 0 Log4J conversion and code cleanup
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Feb 04 2004 18:39:14   DCobb
 * Added SUMMARY_COUNT_PICKUP and SUMMARY_COUNT_LOAN screens.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.3   Jan 02 2004 13:59:42   kll
 * errant duplication
 *
 *    Rev 1.2   Jan 02 2004 10:58:18   kll
 * end of day credit tender amounts
 * Resolution for 3513: Credit tenders not correctly counted during till close, reports are short by Credit aount
 *
 *    Rev 1.1   Nov 04 2003 17:19:36   blj
 * added an aisle to add money order totals to check totals.  Also updated a method in cargo to sum money order totals as checks.
 *
 *    Rev 1.0   Aug 29 2003 15:56:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.16   Jul 29 2003 19:46:04   DCobb
 * Set total according to country code.
 * Resolution for POS SCR-3294: Register Till Summary Report Incorrect for Till Pickup of Canadian Checks
 *
 *    Rev 1.15   Jul 03 2003 15:35:58   RSachdeva
 * constant added
 * Resolution for POS SCR-2425: Credit and Canadian Tender amounts missing from Select Tender screen when Blind Close = No
 *
 *    Rev 1.14   Jun 30 2003 16:53:22   RSachdeva
 * Blind Close = Yes, updating Cash should update only Cash
 * Resolution for POS SCR-2888: Blind Close = Yes, updating Cash updates all Tenders except Check & Debit
 *
 *    Rev 1.13   Jun 30 2003 11:18:32   RSachdeva
 * Rechecking Rev .1.11
 * Resolution for POS SCR-2759: When Open Till Float and Closing Till Float do not match, the Cash field on Select Tender is incorrectly updating
 *
 *    Rev 1.12   Jun 23 2003 13:44:06   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.11   Jun 20 2003 15:39:20   RSachdeva
 * Cash field on Select Tender is incorrectly updating
 * Resolution for POS SCR-2759: When Open Till Float and Closing Till Float do not match, the Cash field on Select Tender is incorrectly updating
 *
 *    Rev 1.10   May 20 2003 16:29:16   adc
 * Changes to support alternate currencies
 * Resolution for 2229: Register Close Till Reports not reporting Canadian Tender
 *
 *    Rev 1.9   May 01 2003 09:57:58   RSachdeva
 * COUNT_TYPE_DESCRIPTORS initialization
 * Resolution for POS SCR-2215: Internationlaztion- Till Functions -Pickup- Summar Count Screens
 *
 *    Rev 1.8   Mar 04 2003 13:50:44   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.7   Dec 09 2002 15:10:46   DCobb
 * Fixed expected amounts for alternate currencies.
 * Resolution for POS SCR-1852: Multiple defects on Till Close Select Tenders screen funtionality.
 *
 *    Rev 1.6   Dec 02 2002 12:10:50   DCobb
 * Internationalized Store Safe and Loan for Summary Count screen.
 * Resolution for POS SCR-1842: POS 6.0 Canadian Check Tender
 *
 *    Rev 1.5   Nov 27 2002 15:55:56   DCobb
 * Add Canadian Check tender.
 * Resolution for POS SCR-1842: POS 6.0 Canadian Check Tender
 *
 *    Rev 1.4   Nov 18 2002 13:37:48   kmorneau
 * added capability to display expected amounts for Blind Close
 * Resolution for 1824: Blind Close
 *
 *    Rev 1.3   Sep 18 2002 17:15:22   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 23 2002 08:31:06   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   17 May 2002 15:33:36   baa
 * externalize hard coded labels
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   07 May 2002 22:49:40   baa
 * ils
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   Mar 18 2002 23:14:44   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:24   msg
 * Initial revision.
 *
 *    Rev 1.18   12 Mar 2002 16:52:42   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.17   07 Mar 2002 13:25:42   epd
 * fixed detail check pickup count
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.15   04 Mar 2002 16:21:08   epd
 * Updates to accommodate use of TenderTypeMap class
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.14   02 Mar 2002 12:47:46   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.13   27 Feb 2002 15:58:32   epd
 * fixed canadian cash count when none tendered
 * Resolution for POS SCR-1431: Crash when Canadian tender is entered on Tender Select but no trans done
 *
 *    Rev 1.12   13 Feb 2002 10:02:16   epd
 * Modified so that check pickups will work again
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.11   Feb 05 2002 16:42:24   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.10   23 Jan 2002 13:05:04   epd
 * Added expected amount checks for Store Safe amounts
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.9   21 Jan 2002 16:01:14   epd
 * Updated to avoid matching on incorect tender
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.8   17 Jan 2002 15:48:38   epd
 * Fixed logic for poscount service
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.7   16 Jan 2002 09:18:54   epd
 * STILL A WORK IN PROGESS.
 * I'm checking this in in it's current state so as not to break the build
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.5   08 Jan 2002 13:39:42   epd
 * Updated not to add tenders to count if amounts are zero
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.3   04 Jan 2002 16:00:40   epd
 * made better use of TenderDescriptor class
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.2   02 Jan 2002 15:44:06   epd
 * Made various changes to accommodate new Till Close screens
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   12 Dec 2001 13:02:48   epd
 * Added code to allow for counting Store Safe
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:16:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.commerceservices.common.currency.DenominationIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCount;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMoneyOrderIfc;
import oracle.retail.stores.domain.tender.TenderTypeMap;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.OtherTenderDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryForeignCurrencyCountMenuBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryForeignTenderMenuBeanModel;

/**
 * The tour cargo for POS count workflow.
 * 
 * @version $Revision: /main/28 $
 */
public class PosCountCargo implements CargoIfc
{
    private static final Logger logger = Logger.getLogger(PosCountCargo.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/28 $";
    /*
     * The following data members must be set by the launch shuttle that
     * initializes the cargo for this service.
     */
    /**
     * This is a reference to the object that holds all the counts for this
     * register.
     */
    protected RegisterIfc register = null;
    /**
     * This identifies the till in the register.
     */
    protected String tillID = null;
    /**
     * Indicates which type to count (Till, Loan, Starting Float, Ending Float
     * Pickup).
     */
    protected int countType = 0;
    /**
     * Data members that can be set by the launch shuttle to change the behavior
     * of the service.
     */
    /**
     * Indicates if the service should count details or summary.
     */
    protected boolean summaryFlag = true;
    /**
     * This object is cloned from the FinancialTotalsIfc object in the till. It
     * holds the changes that this sevice makes to the till totals object.
     */
    protected FinancialTotalsIfc financialTotals = null;
    /**
     * This object contains just the counted information in order to update the
     * till and the Register data NOTE: this should be created via a Fatory
     * implementation
     */
    protected FinancialTotalsIfc updateFT = DomainGateway.getFactory().getFinancialTotalsInstance();
    /**
     * Data members used internally in the service.
     */
    /**
     * Contains the name of the tender that the service is counting.
     */
    protected String currentActivity = NONE;
    /**
     * Contains the name of the charge that the service is counting.
     */
    protected String currentCharge = NONE;
    /**
     * Contains the name of the tender that the service is counting.
     */
    protected String currentFLPTender = NONE;
    /**
     * Contains the current summary amount entered by the user
     */
    protected CurrencyIfc currentAmount = null;
    /**
     * Contains the default expected amount; pickups and loans don't have an
     * expected amount;
     */
    protected CurrencyIfc defaultExpectedAmount = null;
    /**
     * Contains the current cash details entered by the user
     */
    protected Hashtable<String, CurrencyDetailBeanModel> currencyDetailBeanModels = new Hashtable<String, CurrencyDetailBeanModel>();
    /**
     * Holds the count models for tender items.
     */
    protected SummaryCountBeanModel[] tenderModels = null;
    /**
     * Holds the count models charge tenders.
     */
    protected SummaryCountBeanModel[] chargeModels = null;
    /**
     * Holds the count models charge tenders.
     */
    protected Hashtable<String, Object> tenderDetails = new Hashtable<String, Object>();
    /**
     * Holds the count models charge tenders.
     */
    protected Vector<String> acceptedCount = new Vector<String>();
    /**
     * pickup Count detail level
     */
    protected int pickupCountDetailLevel = 0;
    /**
     * Tender Descriptor hash used for the alternate tenders
     */
    protected HashMap<String, TenderDescriptorIfc> tenderDescriptorHash = new HashMap<String, TenderDescriptorIfc>(0);
    /**
     * Acceptable count types
     */
    public static final int START_FLOAT = 0;
    public static final int END_FLOAT = 1;
    public static final int LOAN = 2;
    public static final int PICKUP = 3;
    public static final int TILL = 4;
    public static final int START_SAFE = 5;
    public static final int END_SAFE = 6;

    /**
     * Screen resource ids used in this service.
     */
    public static final String RECONCILIATION_ERROR = "TenderReconciliationError";
    public static final String TENDERS_NOT_COUNTED = "TendersNotCounted";
    // Tender type map
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
    /**
     * String constants for predefined tender types
     */
    public static final String CASH = tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH);
    public static final String CHECK = tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK);
    public static final String CHARGE = tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHARGE);
    public static final String GIFT_CARD = tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CARD);
    public static final String TRAVELERS_CHECK = tenderTypeMap
            .getDescriptor(TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK);
    public static final String MEDIA = "Media";
    public static final String NONE = "None";
    public static final String STRING_ZERO = "0";
    public static final String TENDERS_TO_COUNT_AT_TILL_RECONCILE = "TendersToCountAtTillReconcile";
    public static final String BLIND_CLOSE = "BlindClose";
    /**
     * boolean value used to determine whether we have set Count Model when
     * reconciling Till
     */
    boolean tenderCountModelAssigned = false;
    /**
     * boolean value used to determine whether we have set Count Model when
     * reconciling Till
     */
    boolean chargeCountModelAssigned = false;
    /**
     * The 'To' Register for Pickup or Loan when operating without a safe
     */
    String pickupAndLoanToRegister = null;
    /**
     * The 'From' Register for Pickup or Loan when operating without a safe
     */
    String pickupAndLoanFromRegister = null;
    /**
     * Float label tag
     */
    public static final String FLOAT_TAG = "Float";
    /**
     * Float label default text
     */
    public static final String FLOAT_TEXT = "float";
    /**
     * Loan label tag
     */
    public static final String LOAN_TAG = "Loan";
    /**
     * Loan label default text
     */
    public static final String LOAN_TEXT = "loan";
    /**
     * Pickup label tag
     */
    public static final String PICKUP_TAG = "Pickup";
    /**
     * Loan label default text
     */
    public static final String PICKUP_TEXT = "pickup";
    /**
     * Till label tag
     */
    public static final String TILL_TAG = "Till";
    /**
     * Till label default text
     */
    public static final String TILL_TEXT = "till";
    /**
     * Store safe label tag
     */
    public static final String STORE_SAFE_TAG = "StoreSafe";
    /**
     * Store safe default text
     */
    public static final String STORE_SAFE_TEXT = "store safe";
    /**
     * String array to convert countType to text.
     */
    public static String[] COUNT_TYPE_DESCRIPTORS = { FLOAT_TAG, FLOAT_TAG, LOAN_TAG, PICKUP_TAG, TILL_TAG,
            STORE_SAFE_TAG, STORE_SAFE_TAG };
    /**
     * String array to convert countType to default text.
     */
    public static String[] COUNT_TYPE_DESCRIPTORS_DEFAULT_LOWERCASE = { FLOAT_TEXT, FLOAT_TEXT, LOAN_TEXT, PICKUP_TEXT,
            TILL_TEXT, STORE_SAFE_TEXT, STORE_SAFE_TEXT };

    /**
     * Lower Case that will be attached to the Tag Name for Retrieval from
     * Resource Bundle. This is used to return lower case text.
     */
    public static final String LOWER_CASE = "LowerCase";
    /**
     * Currency Country Code
     */
    protected String tenderNationality = null;
    /**
     * Credit Amount Enter
     */
    public static final String CREDIT_AMT_ENTER = "CreditAmtEnter";
    /**
     * The Financial Count Tender Totals for the foreign currencies taken into
     * the till
     */
    protected FinancialCountTenderItemIfc[] foreignCurrencyFinancialCountTenderTotals = null;
    /**
     * The summary count bean model for the foreign currencies
     */
    protected SummaryCountBeanModel[] summaryForeignCurrencyCountBeanModel = null;
    /**
     * The selected foreign currency to count
     */
    protected String currentForeignCurrency = NONE;
    /**
     * The nationality of the selected foreign currency to count
     */
    protected String currentForeignNationality = NONE;
    /**
     * boolean value used to determine whether we have set Foreign Tender Count
     * Model when reconciling Till
     */
    boolean foreignTenderCountModelAssigned = false;
    /**
     * Holds the count models for foreign tender items.
     */
    protected HashMap<String, Object> foreignTenderModels = new HashMap<String, Object>(0);
    /**
     * Holds the local navigation button bean model for select foreign tender
     * screen.
     */
    protected NavigationButtonBeanModel foreignTenderLocalNavigationModel = null;
    /**
     * Holds the list of tenders accepted for the current foreign currency.
     */
    protected String[] foreignTendersAccepted = null;
    /**
     * Holds the list of tenders to count for the current foreign currency.
     */
    protected String[] foreignTendersToCount = null;
    /**
     * Indicates if a blind close should be performed at till reconcile
     */
    private boolean blindClose = true;
    /**
     * The array of tender types that should be counted at till reconcile
     */
    private ArrayList<String> tendersToCount = new ArrayList<String>();

    /** String constants for predefined foreign currency desriptors **/
    public static final String CAD = "CAD";
    public static final String MXN = "MXN";
    public static final String GBP = "GBP";
    public static final String EUR = "EUR";
    public static final String JPY = "JPY";

    /**
     * Constructs PosCountCargo object.
     * <P>
     */
    public PosCountCargo()
    {
        defaultExpectedAmount = DomainGateway.getBaseCurrencyInstance();
        currentFLPTender = CASH;
    }

    /**
     * Returns a reference to the object that holds all the counts for this
     * register.
     * 
     * @return a register object.
     */
    public RegisterIfc getRegister()
    {
        return (register);
    }

    /**
     * Sets the register.
     * 
     * @param value The register.
     */
    public void setRegister(RegisterIfc value)
    {
        register = value;
    }

    /**
     * Returns the till identifier.
     * 
     * @return the till identifier.
     */
    public String getTillID()
    {
        return (tillID);
    }

    /**
     * Sets till identifier.
     * 
     * @param value The till identifier.
     */
    public void setTillID(String value)
    {
        tillID = value;
    }

    /**
     * Returns the financial totals.
     * 
     * @return the financial totals.
     */
    public FinancialTotalsIfc getFinancialTotals()
    {
        return (financialTotals);
    }

    /**
     * Retrieves update financial totals.
     * 
     * @return the updated financial totals
     */
    public FinancialTotalsIfc getUpdateTotals()
    {
        return (updateFT);
    }

    /**
     * Sets the Financial Totals.
     * 
     * @param value the financial totals.
     */
    public void setFinancialTotals(FinancialTotalsIfc value)
    {
        financialTotals = value;
    }

    /**
     * Returns whether the service should count details or summary.
     * 
     * @return True if the service should count details or summary.
     */
    public boolean getSummaryFlag()
    {
        return (summaryFlag);
    }

    /**
     * Sets whether the service should count details or summary.
     * 
     * @param value True if the service should count details or summary.
     */
    public void setSummaryFlag(boolean value)
    {
        summaryFlag = value;
    }

    /**
     * Returns the pickup count detail level
     * 
     * @return the pickup count detail level.
     */
    public int getPickupCountDetailLevel()
    {
        return (pickupCountDetailLevel);
    }

    /**
     * Sets the pickup count detail level.
     * 
     * @param value The pickup count detail level
     */
    public void setPickupCountDetailLevel(int value)
    {
        pickupCountDetailLevel = value;
    }

    /**
     * Returns which type to count (Till, Loan, Float, Pickup).
     * 
     * @return which type to count (Till, Loan, Float, Pickup).
     */
    public int getCountType()
    {
        return (countType);
    }

    /**
     * Sets which type to count (Till, Loan, Float, Pickup).
     * 
     * @param value the type to count (Till, Loan, Float, Pickup).
     */
    public void setCountType(int value)
    {
        countType = value;
    }

    /**
     * Returns Contains the name of the tender that the service is counting.
     * 
     * @return Contains the name of the tender that the service is counting.
     */
    public String getCurrentActivity()
    {
        return (currentActivity);
    }

    /**
     * Sets the name of the tender that the service is counting.
     * 
     * @param value the name of the tender that the service is counting.
     */
    public void setCurrentActivity(String value)
    {
        currentActivity = value;
    }

    /**
     * Returns the name of the tender for Float, Loans and Pickups that the
     * service is counting.
     * 
     * @return the name of the tender that the service is counting.
     */
    public String getCurrentFLPTender()
    {
        return (currentFLPTender);
    }

    /**
     * Sets name of the tender for Float, Loans and Pickups that the service is
     * counting.
     * 
     * @param value the name of the tender that the service is counting.
     */
    public void setCurrentFLPTender(String value)
    {
        currentFLPTender = value;
    }

    /**
     * Returns the name of the charge that the service is counting.
     * 
     * @return the name of the charge that the service is counting.
     */
    public String getCurrentCharge()
    {
        return (currentCharge);

    }

    /**
     * Sets the name of the charge that the service is counting.
     * 
     * @param value the name of the charge that the service is counting.
     */
    public void setCurrentCharge(String value)
    {
        currentCharge = value;
    }

    /**
     * Returns the name of the activity or charge.
     * 
     * @return the name of the activity or charge that the service is counting.
     */
    public String getCurrentActivityOrCharge()
    {
        if (currentActivity.equals(CHARGE))
        {
            return (currentCharge);
        }
        else
        {
            return (currentActivity);
        }
    }

    /**
     * Sets the name of the activity or charge.
     * 
     * @param value The name of the activity or charge that the service is
     *            counting.
     */
    public void setCurrentActivityOrCharge(String value)
    {
        if (currentActivity.equals(CHARGE))
        {
            currentCharge = value;
        }
        else
        {
            currentActivity = value;
        }
    }

    /**
     * Sets the amount entered by the user.
     * 
     * @param value the amount.
     */
    public void setCurrentAmountStr(String value)
    {
        if (value == null || value.length() == 0)
        {
            value = STRING_ZERO;
        }
        currentAmount = (CurrencyIfc)getExpectedAmount().clone();
        currentAmount.setStringValue(value);
    }

    /**
     * Sets the amount entered by the user.
     * 
     * @param value CurrencyIfc The amount which is currently being counted.
     */
    public void setCurrentAmount(CurrencyIfc value)
    {
        currentAmount = value;
    }

    /**
     * Returns the expected total amount for the current state or the cargo.
     * 
     * @return the expected total amount for the current state or the cargo.
     */
    public CurrencyIfc getCurrentAmount()
    {
        if (currentAmount == null)
        {
            return defaultExpectedAmount;
        }
        else
        {
            return currentAmount;
        }
    }

    /**
     * Returns the default expected total amount.
     * 
     * @param value The default expected amount
     */
    public void setDefaultExpectedAmount(CurrencyIfc value)
    {
        defaultExpectedAmount = value;
    }

    /**
     * Returns the default expected total amount.
     * 
     * @return the default expected total amount.
     */
    public CurrencyIfc getDefaultExpectedAmount()
    {
        return defaultExpectedAmount;
    }

    /**
     * Returns a boolean which indicates if the current count has denominations
     * that can be counted seperately.
     * 
     * @return true if there are denominations
     */
    public boolean currentHasDenominations()
    {
        /*
         * // This code is broken just now; uncomment when fixed.
         * FinancialCountTenderItemIfc fti = financialTotals.getCombinedCount().
         * getExpected
         * ().getTenderItemByDescription(getCurrentActivityOrCharge()); return
         * fti.getHasDenominations();
         */
        boolean ret = false;
        if (getCurrentActivityOrCharge().endsWith(CASH))
        {
            ret = true;
        }
        return ret;
    }

    /**
     * Returns the cash count details entered by the user.
     * 
     * @param key is the country code of the currency being counted.
     * @return Cash count details object.
     */
    public CurrencyDetailBeanModel getCurrencyDetailBeanModel(String key)
    {
        return ((CurrencyDetailBeanModel)currencyDetailBeanModels.get(key));
    }

    /**
     * Sets the cash count details entered by the user.
     * 
     * @param key is the country code of the currency being counted.
     * @param value is the bean model containing the detail currency counts.
     */
    public void addCurrencyDetailBeanModel(String key, CurrencyDetailBeanModel value)
    {
        currencyDetailBeanModels.put(key, value);
    }

    /**
     * Resets the cash count details hashtabler.
     */
    public void resetCurrencyDetailBeanModels()
    {
        currencyDetailBeanModels.clear();
    }

    /**
     * This method converts the currencyDetailBeanModels Hashtable into an array
     * and returns the array.
     * 
     * @return the array of the CurrencyDetailBeanModels
     */
    public CurrencyDetailBeanModel[] getCurrencyDetailBeanModels()
    {
        CurrencyDetailBeanModel[] aModel = new CurrencyDetailBeanModel[currencyDetailBeanModels.size()];
        Enumeration<CurrencyDetailBeanModel> hModel = currencyDetailBeanModels.elements();
        for (int i = 0; i < aModel.length; i++)
        {
            aModel[i] = (CurrencyDetailBeanModel)hModel.nextElement();
        }
        return aModel;
    }

    /**
     * Get a bean model for each (there should only be one) base currency cash
     * tender, whether is has been counted or not.
     * 
     * @return CurrencyDetailBeanModel[]
     */
    public CurrencyDetailBeanModel[] getCashCurrencyDetailBeanModels()
    {
        FinancialCountIfc fc = financialTotals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc[] tenders = fc.getTenderItems();
        String baseCurrencyCountryCode = DomainGateway.getBaseCurrencyInstance().getCountryCode();

        // Iterate through all the tenders
        for (int i = 0; i < tenders.length; i++)
        {
            // If the current tender is base currency and cash ...
            String countryCode = tenders[i].getAmountTotal().getCountryCode();
            if (baseCurrencyCountryCode.equals(countryCode)
                    && tenders[i].getTenderDescriptor().getTenderType() == TenderLineItemIfc.TENDER_TYPE_CASH)
            {
                if (!currencyDetailBeanModels.containsKey(countryCode))
                {
                    addCurrencyDetailBeanModel(countryCode,
                            createCompletedCurrencyDetailBeanModelFromTenderItem(tenders[i]));
                }
            }
        }

        return getCurrencyDetailBeanModels();
    }

    /**
     * Get a bean model for each foreign currency cash tender, whether is has
     * been counted or not.
     * 
     * @return CurrencyDetailBeanModel[]
     */
    public CurrencyDetailBeanModel[] getForeignCashCurrencyDetailBeanModels()
    {
        FinancialCountIfc fc = financialTotals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc[] tenders = fc.getTenderItems();
        String baseCurrencyCountryCode = DomainGateway.getBaseCurrencyInstance().getCountryCode();

        // Iterate through all the tender
        for (int i = 0; i < tenders.length; i++)
        {
            // If the current tender is foreign and cash ...
            String countryCode = tenders[i].getAmountTotal().getCountryCode();
            if (!baseCurrencyCountryCode.equals(countryCode)
                    && tenders[i].getTenderDescriptor().getTenderType() == TenderLineItemIfc.TENDER_TYPE_CASH)
            {
                if (!currencyDetailBeanModels.containsKey(countryCode))
                {
                    addCurrencyDetailBeanModel(countryCode,
                            createCompletedCurrencyDetailBeanModelFromTenderItem(tenders[i]));
                }
            }
        }

        return getCurrencyDetailBeanModels();
    }

    /*
     * Create a completed CurrencyDetailBeanModel object from a tender item.
     * This method is private because tour classes should not call this method
     * directly.
     */
    private CurrencyDetailBeanModel createCompletedCurrencyDetailBeanModelFromTenderItem(
            FinancialCountTenderItemIfc tenderItem)
    {
        CurrencyDetailBeanModel beanModel = new CurrencyDetailBeanModel();
        beanModel.setTotal(tenderItem.getAmountTotal());
        beanModel.setSummaryCurrencyDescription(getCurrentActivityOrCharge());
        // There are no counts; create a zero length array.
        beanModel.setDenominationCounts(new Long[0]);

        return beanModel;
    }

    /**
     * Returns the expected total amount for the current state of the cargo.
     * 
     * @return the expected total amount for the current state of the cargo.
     */
    public CurrencyIfc getExpectedAmount()
    {
        FinancialCountTenderItemIfc fti = null;
        CurrencyIfc amount = null;

        TenderDescriptorIfc tenderDescriptor = getTenderDescriptor();

        if (countType == TILL)
        {
            // it is possible that the tender is null if we never tendered this
            // tender type
            FinancialCountIfc fc = financialTotals.getCombinedCount().getExpected();

            if (tenderDescriptor.getTenderType() == TenderLineItemIfc.TENDER_TYPE_CASH)
            {
                // amount = fc.getTenderItem(tenderDescriptor,
                // false).getAmountTotal();
                FinancialCountTenderItemIfc fcti = fc.getTenderItem(tenderDescriptor, false);
                if (fcti == null)
                {
                    if (tenderDescriptor.getCountryCode() == DomainGateway.getBaseCurrencyInstance().getCountryCode())
                    {
                        amount = DomainGateway.getBaseCurrencyInstance();
                        amount.setStringValue("0.00");
                    }
                    else
                    {
                        amount = DomainGateway.getAlternateCurrencyInstance(tenderDescriptor.getCountryCode());
                        amount.setStringValue("0.00");
                    }
                }
                else
                {
                    amount = fcti.getAmountTotal();
                }
            }
            else if (tenderDescriptor.getTenderType() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
            {
                if (getSummaryFlag() == true)
                {
                    FinancialCountIfc fcSummed = FinancialCount.sumFinancialCountTender(fc, tenderDescriptor);
                    FinancialCountTenderItemIfc fcti = fcSummed.getTenderItem(tenderDescriptor, false);
                    if (fcti == null)
                    {
                        amount = DomainGateway.getBaseCurrencyInstance();
                        amount.setStringValue("0.00");
                    }
                    else
                    {
                        amount = fcti.getAmountTotal();
                    }
                }
                else
                {
                    FinancialCountTenderItemIfc fcti = fc.getTenderItem(tenderDescriptor, false);
                    if (fcti == null)
                    {
                        amount = DomainGateway.getBaseCurrencyInstance();
                        amount.setStringValue("0.00");
                    }
                    else
                    {
                        amount = fcti.getAmountTotal();
                    }
                }
            }
            else if (fc.getTenderItem(tenderDescriptor, false) != null) // don't
                                                                        // need
                                                                        // to
                                                                        // sum
                                                                        // tenders
                                                                        // on
                                                                        // 'if'
                                                                        // test
            {
                amount = fc.getTenderItem(tenderDescriptor, false).getAmountTotal();
            }
            else
            {
                if (tenderDescriptor.getCountryCode() == DomainGateway.getBaseCurrencyType().getCountryCode())
                {
                    amount = DomainGateway.getBaseCurrencyInstance();
                    amount.setStringValue("0.00");
                }
                else
                {
                    amount = DomainGateway.getAlternateCurrencyInstance(tenderDescriptor.getCountryCode());
                    amount.setStringValue("0.00");
                }
            }
        }
        else if (countType == START_FLOAT)
        {
            fti = financialTotals.getStartingFloatCount().getExpected()
                    .getSummaryTenderItemByDescriptor(getTenderDescriptorForCurrentFLPTender());

            amount = fti.getAmountIn();
        }
        else if (countType == END_FLOAT)
        {

            fti = financialTotals.getEndingFloatCount().getExpected()
                    .getSummaryTenderItemByDescriptor(getTenderDescriptorForCurrentFLPTender());

            amount = fti.getAmountOut();
        }
        else if (countType == START_SAFE)
        {
            fti = financialTotals.getStartingSafeCount().getExpected()
                    .getSummaryTenderItemByDescriptor(getTenderDescriptorForCurrentFLPTender());

            amount = fti.getAmountTotal();
        }
        else if (countType == END_SAFE)
        {
            fti = financialTotals.getEndingSafeCount().getExpected()
                    .getSummaryTenderItemByDescriptor(getTenderDescriptorForCurrentFLPTender());

            amount = fti.getAmountTotal();
        }
        else if (countType == PICKUP && getTenderDescriptorForCurrentFLPTender().getTenderSubType().equals(CHECK))
        {
            fti = financialTotals.getCombinedCount().getExpected()
                    .getSummaryTenderItemByDescriptor(getTenderDescriptorForCurrentFLPTender());

            if (fti != null && fti.getAmountTotal() != null)
            {
                amount = fti.getAmountTotal();
            }
            else
            {
                amount = DomainGateway.getBaseCurrencyInstance();
            }
        }
        else
        {
            // Loans, pickups, pay-ins, and pay-outs do not have expected
            // counts.
            // whatever has been counted is the expected count.
            amount = getCurrentAmount();
        }

        return amount;
    }

    /**
     * Puts the values passed into the Totals object. "FLP" in the method name
     * stands for float, loan or Pickup.
     * 
     * @param entAmt entered amount to set in the totals object.
     * @param entCnt entered count to set in the totals object.
     */
    public void updateFLPSummaryInTotals(CurrencyIfc entAmt, int entCnt)
    {
        // Get the appropriate FC
        FinancialCountIfc fc = getCurrentFinancialCount();
        FinancialCountIfc ufc = getUpdateFinancialCount();
        CurrencyIfc zero = (CurrencyIfc)entAmt.clone();
        zero.setStringValue(STRING_ZERO);

        TenderDescriptorIfc td = getTenderDescriptorForCurrentFLPTender();
        td.setCountryCode(entAmt.getCountryCode());
        td.setCurrencyID(entAmt.getType().getCurrencyId());
        td.setTenderSubType("");

        // Set the entered counts for start floats and loans.
        if (countType == START_FLOAT || countType == LOAN)
        {
            // These counts are going into the till
            fc.addTenderItem(td, entCnt, 0, entAmt, zero, getCurrentFLPTender(), "", false, true);
            ufc.addTenderItem(td, entCnt, 0, entAmt, zero, getCurrentFLPTender(), "", false, true);
        }
        else if (countType == START_SAFE || countType == END_SAFE)
        {
            fc.addTenderItem(td, entCnt, 0, entAmt, zero, getCurrentFLPTender(), "", false, true);
        }
        // Set the entered counts for end floats and pickups.
        else
        {
            // These counts are going out of the till
            fc.addTenderItem(td, 0, entCnt, zero, entAmt, getCurrentFLPTender(), "", false, true);
            ufc.addTenderItem(td, 0, entCnt, zero, entAmt, getCurrentFLPTender(), "", false, true);
        }
    }

    /**
     * Updates the summary totals for the till.
     */
    public void updateTillSummaryInTotals()
    {
        // Put the value in the financial totals object
        FinancialCountIfc fc = financialTotals.getCombinedCount().getEntered();
        FinancialCountIfc ufc = updateFT.getCombinedCount().getEntered();
        CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amtIn = null;
        CurrencyIfc amtOut = null;
        int cntIn = 0;
        int cntOut = 0;

        // Iterate through the tender models for tenders that the operator had
        // an opportunity
        // to count. If the operator did not count a specific tender, a value
        // from the
        // expected counts may still be used
        for (int i = 0; i < getTenderModels().length; i++)
        {
            // Handle all non credit tenders
            if (!tenderModels[i].getDescription().equals(CHARGE))
            {
                TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
                td.setCountryCode(tenderModels[i].getAmount().getCountryCode());
                td.setCurrencyID(tenderModels[i].getAmount().getType().getCurrencyId());
                td.setTenderType(tenderModels[i].getTenderType());

                // Get the expected count value if necessay.
                CurrencyIfc total = determineTotalAmountToSave(td, tenderModels[i].getAmount());

                // A negative value indicates an amount that is leaving the
                // till; put it in
                // the out bucket.
                if (total.signum() == CurrencyIfc.NEGATIVE)
                {
                    amtIn = zero;
                    amtOut = total.negate();
                    cntIn = 0;
                    cntOut = 1;
                }
                else
                {
                    amtIn = total;
                    amtOut = zero;
                    cntIn = 1;
                    cntOut = 0;
                }

                // Don't save any tender counts that have no "in" or "out"
                // values.
                if (!(amtIn.signum() == 0 && amtOut.signum() == 0))
                {
                    fc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, tenderModels[i].getDescription(), "", false,
                            true);
                    ufc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, tenderModels[i].getDescription(), "", false,
                            true);
                }
            }
        }

        // Iterate through the credit tender models for tenders that the
        // operator had an opportunity
        // to count. If the operator did not count a specific tender, a value
        // from the
        // expected counts may still be used
        for (int i = 0; i < getChargeModels().length; i++)
        {
            TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
            td.setCountryCode(DomainGateway.getBaseCurrencyType().getCountryCode());
            td.setCurrencyID(DomainGateway.getBaseCurrencyType().getCurrencyId());
            td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CHARGE);
            td.setTenderSubType(chargeModels[i].getDescription());

            // Get the expected count value if necessay.
            CurrencyIfc total = determineTotalAmountToSave(td, chargeModels[i].getAmount());

            // A negative value indicates an amount that is leaving the till;
            // put it in
            // the "out" bucket.
            if (total.signum() == CurrencyIfc.NEGATIVE)
            {
                amtIn = zero;
                amtOut = total.negate();
                cntIn = 0;
                cntOut = 1;
            }
            else
            {
                amtIn = total;
                amtOut = zero;
                cntIn = 1;
                cntOut = 0;
            }

            // Don't save any tender counts that have no "in" or "out" values.
            if (!(amtIn.signum() == 0 && amtOut.signum() == 0))
            {
                fc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, chargeModels[i].getDescription(), CHARGE, false,
                        true);
                ufc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, chargeModels[i].getDescription(), CHARGE, false,
                        true);
            }
        }

        // Mail Bank Check does not make into the tender models; this code
        // "balances" the tender.
        setMailBankCheckTenderEnteredAmount();
    }

    /**
     * Controls setting the tender count details entered by the user into the
     * totals.
     * <P>
     */
    public void updateTenderDetailAmountsInTotals()
    {
        // Tenders for which manual counting is disabled (we want the system
        // count)
        // will not be part of the tenderDetails hashtable. We need to put them
        // in this hash before
        // we update.
        FinancialCountIfc fc = financialTotals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc[] tenders = fc.getTenderItems();

        String baseCurrencyCountryCode = DomainGateway.getBaseCurrencyInstance().getCountryCode();

        for (int i = 0; i < tenders.length; i++)
        {
            String countryCode = tenders[i].getAmountTotal().getCountryCode();
            if ((currentForeignCurrency.equals(NONE) && baseCurrencyCountryCode.equals(countryCode))
                    || (!currentForeignCurrency.equals(NONE) && !baseCurrencyCountryCode.equals(countryCode)))
            {
                boolean skipTender = false; // skip Cash and Charge tenders
                String tenderName = tenders[i].getDescription();

                if (tenders[i].getTenderDescriptor().getTenderType() == TenderLineItemIfc.TENDER_TYPE_CASH)
                {
                    skipTender = true;
                }
                else if (tenders[i].getTenderDescriptor().getTenderType() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
                {
                    // Make sure description and summary description set
                    // properly
                    tenders[i].setSummaryDescription(CHARGE);
                }
                else
                {
                    setCurrentActivityOrCharge(tenderTypeMap.getDescriptor(tenders[i].getTenderDescriptor()
                            .getTenderType()));
                }

                // If tender not in tenderDetails, we need to add it (Ignore
                // 'Cash'. This is handled elsewhere.
                if (tenderDetails.containsKey(tenderName) == false && skipTender == false)
                {
                    // If the count is for till recondile, get the expected
                    // count value if necessary.
                    CurrencyIfc amount = tenders[i].getAmountTotal();
                    if (countType == TILL)
                    {
                        amount = determineTotalAmountToSave(tenders[i].getTenderDescriptor(), amount);
                    }
                    OtherTenderDetailBeanModel model = new OtherTenderDetailBeanModel();
                    model.setTotal(amount);
                    CurrencyIfc[] c = new CurrencyIfc[1];
                    c[0] = amount;
                    model.setTenderAmounts(c);
                    model.setDescription(tenders[i].getDescription());
                    model.setSummaryDescription(tenders[i].getSummaryDescription());
                    tenderDetails.put(tenders[i].getDescription(), model);
                }
            }
        }

        Enumeration<Object> enumer = tenderDetails.elements();
        while (enumer.hasMoreElements())
        {
            OtherTenderDetailBeanModel model = (OtherTenderDetailBeanModel)enumer.nextElement();
            updateTenderDetailAmountsInTotals(model);
        }
    }

    /**
     * Controls setting the tender count details entered by the user into the
     * totals.
     * 
     * @deprecated in 13.3; see updateTillTenderDetailAmountsInTotals()
     */
    public void updateTenderDetailAmountsInTotals(boolean blindClose)
    {
        this.blindClose = blindClose;
        updateTillTenderDetailAmountsInTotals();
    }

    /**
     * Controls setting the tender count details entered by the user into the
     * totals.
     * <P>
     */
    public void updateTillTenderDetailAmountsInTotals()
    {
        // Select the set of tenders that should saved for till reconcile
        FinancialCountTenderItemIfc[] tenders = getTenderCountItemsToSave();
        FinancialCountIfc fc = financialTotals.getCombinedCount().getEntered();
        FinancialCountIfc ufc = updateFT.getCombinedCount().getEntered();
        CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();

        // Reserve variable for the following loop
        TenderDescriptorIfc td;
        CurrencyIfc amtIn;
        CurrencyIfc amtOut;
        int cntIn;
        int cntOut;

        // Iterate through the tender count items
        for (FinancialCountTenderItemIfc tender : tenders)
        {
            // Get the expected count if necessary
            CurrencyIfc total = determineTotalAmountToSave(tender.getTenderDescriptor(), tender.getAmountTotal());
            cntIn = 0;
            cntOut = 0;

            // A negative count amount indicates the value is leaving the till;
            // Put it in the out bucket.
            if (total.signum() == CurrencyIfc.NEGATIVE)
            {
                // Convert the negative currency dollar to a positive
                amtOut = total.negate();
                amtIn = zero;
                cntOut = 1;
            }
            else
            {
                amtIn = total;
                amtOut = zero;
                cntIn = 1;
            }

            // Add a tender item to the Financial Count object that will be
            // saved to the database.
            td = tender.getTenderDescriptor();
            if (td.getTenderType() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
            {
                fc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, td.getTenderSubType(), tender.getDescription(),
                        false, true);
                ufc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, td.getTenderSubType(), tender.getDescription(),
                        false, true);
            }
            else
            {
                fc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, tender.getDescription(), "", false, true);
                ufc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, tender.getDescription(), "", false, true);
            }
        }
        Enumeration<Object> enumer = tenderDetails.elements();
        while (enumer.hasMoreElements())
        {
            OtherTenderDetailBeanModel model = (OtherTenderDetailBeanModel)enumer.nextElement();
            updateTenderDetailAmountsInTotals(model);
        }
    }

    /**
     * This method builds an array of FinancialCountTenderItemIfc items which
     * represents the all tenders that should be saved to the database.
     * 
     * @return FinancialCountTenderItemIfc array
     */
    protected FinancialCountTenderItemIfc[] getTenderCountItemsToSave()
    {
        // Get the base currency country code
        String baseCurrencyCountryCode = DomainGateway.getBaseCurrencyInstance().getCountryCode();

        // Put the list of entered tenders into the tenders to save list.
        ArrayList<FinancialCountTenderItemIfc> tendersToSave = new ArrayList<FinancialCountTenderItemIfc>();
        FinancialCountTenderItemIfc[] tenders = financialTotals.getCombinedCount().getEntered().getTenderItems();
        for (FinancialCountTenderItemIfc tender : tenders)
        {
            // Exclude cash items. Also there can be a tender descriptor which
            // represents the total
            // of all credit tenders; do not include that.
            if (tender.getTenderDescriptor().getTenderType() != TenderLineItemIfc.TENDER_TYPE_CASH
                    && !tender.getTenderDescriptor().getTenderSubType().equals(CHARGE))
            {
                // Add to the list if we are counting local currency and tender
                // is local currency
                if (currentForeignCurrency.equals(NONE)
                        && baseCurrencyCountryCode.equals(tender.getAmountTotal().getCountryCode()))
                {
                    getCountFromTenderBeanModel(tender);
                    tendersToSave.add(tender);
                }
                else
                {
                    // Add to the list if we are count foreign currency and
                    // tender is foreign currency
                    String countryCode = getCountryCodeForForeignCurrency(currentForeignCurrency);
                    if (tender.getAmountTotal().getCountryCode().equals(countryCode))
                    {
                        getCountFromTenderBeanModel(tender);
                        tendersToSave.add(tender);
                    }
                }
            }
        }

        // Convert the ArrayList to an array
        FinancialCountTenderItemIfc[] returnTenders = new FinancialCountTenderItemIfc[tendersToSave.size()];
        tendersToSave.toArray(returnTenders);
        return returnTenders;
    }

    /**
     * Update the tenderCountItem object with counted value from the bean models
     * 
     * @param tenderCountItem
     */
    protected void getCountFromTenderBeanModel(FinancialCountTenderItemIfc tenderCountItem)
    {
        SummaryCountBeanModel[] beanModels = null;
        if (tenderCountItem.getTenderDescriptor().getTenderType() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
        {
            beanModels = chargeModels;
        }
        else
        {
            beanModels = tenderModels;
        }

        // If the bean model is null, then no amount has been entered and
        // the default value of zero is correct.
        if (beanModels != null)
        {
            for (SummaryCountBeanModel beanModel : beanModels)
            {
                if (tenderCountItem.getDescription().equals(beanModel.getDescription()))
                {
                    tenderCountItem.setAmountIn(beanModel.getAmount());
                    break;
                }
            }
        }
    }

    /**
     * The mail bank check tender does not make into the tender count UI models;
     * This method sets the entered count equal to the the expect count.
     */
    protected void setMailBankCheckTenderEnteredAmount()
    {
        FinancialCountIfc fc = financialTotals.getCombinedCount().getEntered();
        FinancialCountIfc ufc = updateFT.getCombinedCount().getEntered();
        CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amtIn = null;
        CurrencyIfc amtOut = null;
        int cntIn = 0;
        int cntOut = 0;
        TenderDescriptorIfc mbcTD = DomainGateway.getFactory().getTenderDescriptorInstance();
        mbcTD.setCountryCode(zero.getCountryCode());
        mbcTD.setCurrencyID(zero.getType().getCurrencyId());
        mbcTD.setTenderType(TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK);
        FinancialCountTenderItemIfc[] fcti = financialTotals.getCombinedCount().getExpected()
                .getDetailTenderItemBySummaryDescription(mbcTD);
        if (fcti != null && fcti.length == 1)
        {
            amtIn = zero;
            amtOut = fcti[0].getAmountOut();
            fc.addTenderItem(mbcTD, cntIn, cntOut, amtIn, amtOut,
                    tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK), "", false, true);
            ufc.addTenderItem(mbcTD, cntIn, cntOut, amtIn, amtOut,
                    tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK), "", false, true);
        }
    }

    /**
     * Controls setting the tender count details entered by the user into the
     * totals.
     * 
     * @param model OtherTenderDetailBeanModel
     */
    public void updateTenderDetailAmountsInTotals(OtherTenderDetailBeanModel model)
    {
        // Initialize local varaibles
        FinancialCountIfc fc = getCurrentFinancialCount();
        FinancialCountIfc ufc = getUpdateFinancialCount();
        CurrencyIfc amounts[] = model.getTenderAmounts();
        String desc = model.getDescription();
        String sDesc = model.getSummaryDescription();
        String baseCurrencyCountryCode = DomainGateway.getBaseCurrencyInstance().getCountryCode();
        CurrencyIfc baseCurrencyZero = DomainGateway.getBaseCurrencyInstance();

        // Accumulate the amounts and counts.
        for (int cnt = 0; cnt < amounts.length; cnt++)
        {
            String countryCode = amounts[cnt].getCountryCode();
            int currencyID = amounts[cnt].getType().getCurrencyId();
            CurrencyIfc zero = null;
            int tenderType = tenderTypeMap.getTypeFromDescriptor(desc);

            // If a tenderType isn't found using model's description, try again
            // using summaryDescription.
            if (tenderType == TenderLineItemIfc.TENDER_TYPE_UNKNOWN)
            {
                // This check was added because the desc for a "Credit" model is
                // a subType (i.e. AmEx, Visa, etc..),
                // but the model's summaryDesc is "Credit". This is ugly, but
                // it's too risky to change at this stage.
                tenderType = tenderTypeMap.getTypeFromDescriptor(sDesc);
            }

            if (baseCurrencyCountryCode.equals(countryCode))
            {
                zero = baseCurrencyZero;
            }
            else
            {
                zero = DomainGateway.getAlternateCurrencyInstance(countryCode);
                int index = desc.indexOf(' ');
                if (index != TenderLineItemIfc.TENDER_TYPE_UNKNOWN)
                {
                    tenderType = tenderTypeMap.getTypeFromDescriptor(desc.substring(index + 1));
                }

                // for foreign currency tender type mapping
                if (tenderType == TenderLineItemIfc.TENDER_TYPE_UNKNOWN)
                {
                    StringTokenizer st = new StringTokenizer(desc, " ");
                    int tokens = st.countTokens();
                    String s = "";
                    if (tokens > 1)
                    {
                        while (st.hasMoreTokens())
                        {
                            s = st.nextToken();
                        }
                    }
                    tenderType = tenderTypeMap.getTypeFromDescriptor(s);
                }
            }
            CurrencyIfc amtIn = null;
            CurrencyIfc amtOut = null;
            int cntIn = 0;
            int cntOut = 0;
            if (amounts[cnt].signum() == CurrencyIfc.NEGATIVE)
            {
                // Convert the negative currency dollar to a positive
                amtOut = amounts[cnt].negate();
                amtIn = zero;
                cntOut = 1;
            }
            else
            {
                amtIn = amounts[cnt];
                amtOut = zero;
                cntIn = 1;
            }

            TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
            td.setCountryCode(countryCode);
            td.setTenderType(tenderType);
            td.setCurrencyID(currencyID);

            if (sDesc == null || sDesc.length() == 0)
            {
                fc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, desc, "", false, true);
                ufc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, desc, "", false, true);
            }
            else
            {
                td.setTenderSubType(desc);
                fc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, desc, sDesc, false, true);
                ufc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, desc, sDesc, false, true);
            }
        }
    }

    /**
     * Controls setting the check count details entered by the user into the
     * totals.
     * 
     * @param model OtherTenderDetailBeanModel
     */
    public void updateCheckAmountsInTotals(OtherTenderDetailBeanModel model)
    {
        // Initialize local varaibles
        CurrencyIfc zero = null; // DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc total = null; // DomainGateway.getBaseCurrencyInstance();

        if ((tenderNationality == null)
                || tenderNationality.equals(DomainGateway.getBaseCurrencyInstance().getCountryCode()))
        {
            zero = DomainGateway.getBaseCurrencyInstance();
            total = DomainGateway.getBaseCurrencyInstance();
        }
        else
        {
            CurrencyTypeIfc alternateCurrencyType[] = DomainGateway.getAlternateCurrencyTypes();
            zero = DomainGateway.getAlternateCurrencyInstance(alternateCurrencyType[0].getCountryCode());
            total = DomainGateway.getAlternateCurrencyInstance(alternateCurrencyType[0].getCountryCode());
        }

        FinancialCountIfc fc = getCurrentFinancialCount();
        FinancialCountIfc ufc = getUpdateFinancialCount();
        CurrencyIfc amounts[] = model.getTenderAmounts();
        String desc = model.getDescription();
        String sDesc = model.getSummaryDescription();
        int count = 0;

        // Accumulate the amounts and counts.
        for (int cnt = 0; cnt < amounts.length; cnt++)
        {
            TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
            td.setCountryCode(amounts[cnt].getCountryCode());
            td.setCurrencyID(amounts[cnt].getType().getCurrencyId());
            td.setTenderType(tenderTypeMap.getTypeFromDescriptor(desc));
            // tender sub type set to Check to differentiate it from the summary
            // item added below
            td.setTenderSubType(desc);
            // update total
            total = total.add(amounts[cnt]);
            count++;

            if (countType == START_FLOAT || countType == LOAN || countType == TILL)
            {
                // These counts are coming into the till
                fc.addTenderItem(td, 1, 0, amounts[cnt], zero, desc, sDesc, false, false);
                ufc.addTenderItem(td, 1, 0, amounts[cnt], zero, desc, sDesc, false, false);
            }
            else
            {
                // These count are going out of the till
                fc.addTenderItem(td, 0, 1, zero, amounts[cnt], desc, sDesc, false, false);
                ufc.addTenderItem(td, 0, 1, zero, amounts[cnt], desc, sDesc, false, false);
            }
        }

        // Now add SUMMARY check line item containing total of all Cash
        // denominations
        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CHECK);
        td.setTenderSubType("");
        td.setCountryCode(total.getCountryCode());
        td.setCurrencyID(total.getType().getCurrencyId());

        if (countType == START_FLOAT || countType == LOAN || countType == TILL)
        {
            // These counts are coming into the till
            fc.addTenderItem(td, count, 0, total, zero, desc, "", false, true);
            ufc.addTenderItem(td, count, 0, total, zero, desc, "", false, true);
        }
        else if (countType == START_SAFE || countType == END_SAFE)
        {
            fc.addTenderItem(td, count, 0, total, zero, desc, "", false, true);
        }
        // Set the entered counts for end floats
        else
        {
            // These count are going out of the till
            fc.addTenderItem(td, 0, count, zero, total, desc, "", false, true);
            ufc.addTenderItem(td, 0, count, zero, total, desc, "", false, true);
        }

    }

    /**
     * Controls setting the check count details entered by the user into the
     * totals.
     * 
     * @param checks TenderCheckIfc[]
     */
    public void updateCheckAmountsInTotals(TenderCheckIfc checks[])
    {
        // Initialize local varaibles
        CurrencyIfc zero = null; // DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc total = null; // DomainGateway.getBaseCurrencyInstance();
        boolean alternateCurrency = false;
        if ((tenderNationality == null)
                || tenderNationality.equals(DomainGateway.getBaseCurrencyInstance().getCountryCode()))
        {
            zero = DomainGateway.getBaseCurrencyInstance();
            total = DomainGateway.getBaseCurrencyInstance();
        }
        else
        {
            CurrencyTypeIfc alternateCurrencyType[] = DomainGateway.getAlternateCurrencyTypes();
            zero = DomainGateway.getAlternateCurrencyInstance(alternateCurrencyType[0].getCountryCode());
            total = DomainGateway.getAlternateCurrencyInstance(alternateCurrencyType[0].getCountryCode());
            alternateCurrency = true;
        }

        FinancialCountIfc fc = getCurrentFinancialCount();
        FinancialCountIfc ufc = getUpdateFinancialCount();
        String desc = getCurrentFLPTender();
        String sDesc = getCurrentFLPTender();
        String countryCode = total.getCountryCode();
        int currencyID = total.getType().getCurrencyId();

        int count = 0;

        // Accumulate the amounts and counts.
        for (int cnt = 0; cnt < checks.length; cnt++)
        {
            TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
            td.setCountryCode(countryCode);
            td.setCurrencyID(currencyID);
            td.setTenderType(tenderTypeMap.getTypeFromDescriptor(desc));
            // tender sub type set to Check to differentiate it from the summary
            // item added below
            td.setTenderSubType(desc);
            // update total
            CurrencyIfc checkAmount = null;
            if (alternateCurrency)
            {
                checkAmount = ((TenderAlternateCurrencyIfc)checks[cnt]).getAlternateCurrencyTendered();
            }
            else
            {
                checkAmount = checks[cnt].getAmountTender();
            }
            total = total.add(checkAmount);
            count++;

            if (countType == START_FLOAT || countType == LOAN || countType == TILL)
            {
                // These counts are coming into the till
                fc.addTenderItem(td, 1, 0, checks[cnt].getAmountTender(), zero, desc, sDesc, false, false);
                ufc.addTenderItem(td, 1, 0, checks[cnt].getAmountTender(), zero, desc, sDesc, false, false);
            }
            else
            {
                // These count are going out of the till
                fc.addTenderItem(td, 0, 1, zero, checkAmount, desc, sDesc, false, false);
                ufc.addTenderItem(td, 0, 1, zero, checkAmount, desc, sDesc, false, false);
            }
        }

        // Now add SUMMARY check line item containing total of all Cash
        // denominations
        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CHECK);
        td.setTenderSubType("");
        td.setCountryCode(total.getCountryCode());
        td.setCurrencyID(total.getType().getCurrencyId());

        if (countType == START_FLOAT || countType == LOAN || countType == TILL)
        {
            // These counts are coming into the till
            fc.addTenderItem(td, count, 0, total, zero, desc, "", false, true);
            ufc.addTenderItem(td, count, 0, total, zero, desc, "", false, true);
        }
        else if (countType == START_SAFE || countType == END_SAFE)
        {
            fc.addTenderItem(td, count, 0, total, zero, desc, "", false, true);
        }
        // Set the entered counts for end floats
        else
        {
            // These count are going out of the till
            fc.addTenderItem(td, 0, count, zero, total, desc, "", false, true);
            ufc.addTenderItem(td, 0, count, zero, total, desc, "", false, true);
        }

    }

    /**
     * Money Order Tender is counted as checks for financial counts the totals.
     * 
     * @param checks TenderMoneyOrderIfc[]
     */
    public void updateCheckAmountsInTotalsForMoneyOrder(TenderMoneyOrderIfc checks[])
    {
        // Initialize local varaibles
        CurrencyIfc zero = null; // DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc total = null; // DomainGateway.getBaseCurrencyInstance();

        if ((tenderNationality == null)
                || tenderNationality.equals(DomainGateway.getBaseCurrencyInstance().getCountryCode()))
        {
            zero = DomainGateway.getBaseCurrencyInstance();
            total = DomainGateway.getBaseCurrencyInstance();
        }
        else
        {
            CurrencyTypeIfc alternateCurrencyType[] = DomainGateway.getAlternateCurrencyTypes();
            zero = DomainGateway.getAlternateCurrencyInstance(alternateCurrencyType[0].getCountryCode());
            total = DomainGateway.getAlternateCurrencyInstance(alternateCurrencyType[0].getCountryCode());
        }

        FinancialCountIfc fc = getCurrentFinancialCount();
        FinancialCountIfc ufc = getUpdateFinancialCount();
        String desc = getCurrentFLPTender();
        String sDesc = getCurrentFLPTender();
        String countryCode = total.getCountryCode();

        int count = 0;

        // Accumulate the amounts and counts.
        for (int cnt = 0; cnt < checks.length; cnt++)
        {
            TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
            td.setCountryCode(countryCode);
            td.setTenderType(tenderTypeMap.getTypeFromDescriptor(desc));
            td.setCurrencyID(checks[cnt].getAmountTender().getType().getCurrencyId());
            // tender sub type set to Check to differentiate it from the summary
            // item added below
            td.setTenderSubType(desc);
            // update total
            CurrencyIfc checkAmount = null;
            checkAmount = checks[cnt].getAmountTender();
            // Add checkamount to the total
            total = total.add(checkAmount);
            count++;

            if (countType == START_FLOAT || countType == LOAN || countType == TILL)
            {
                // These counts are coming into the till
                fc.addTenderItem(td, 1, 0, checks[cnt].getAmountTender(), zero, desc, sDesc, false, false);
                ufc.addTenderItem(td, 1, 0, checks[cnt].getAmountTender(), zero, desc, sDesc, false, false);
            }
            else
            {
                // These count are going out of the till
                fc.addTenderItem(td, 0, 1, zero, checkAmount, desc, sDesc, false, false);
                ufc.addTenderItem(td, 0, 1, zero, checkAmount, desc, sDesc, false, false);
            }
        }

        // Now add SUMMARY check line item containing total of all Cash
        // denominations
        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CHECK);
        td.setTenderSubType("");
        td.setCountryCode(total.getCountryCode());
        td.setCurrencyID(total.getType().getCurrencyId());

        if (countType == START_FLOAT || countType == LOAN || countType == TILL)
        {
            // These counts are coming into the till
            fc.addTenderItem(td, count, 0, total, zero, desc, "", false, true);
            ufc.addTenderItem(td, count, 0, total, zero, desc, "", false, true);
        }
        else if (countType == START_SAFE || countType == END_SAFE)
        {
            fc.addTenderItem(td, count, 0, total, zero, desc, "", false, true);
        }
        // Set the entered counts for end floats
        else
        {
            // These count are going out of the till
            fc.addTenderItem(td, 0, count, zero, total, desc, "", false, true);
            ufc.addTenderItem(td, 0, count, zero, total, desc, "", false, true);
        }

    }

    /**
     * Sets the cash detail amounts in the financial totals object. Services
     * that count Cash details (pennies, nickels, dimes, etc.) call this method.
     * 
     * @param model CurrencyDetailBeanModel
     */
    public void updateCashDetailAmountInTotals(CurrencyDetailBeanModel model)
    {
        int enteredCnt = 0;
        String enteredDsc = null;
        CurrencyIfc enteredAmt = null;
        FinancialCountIfc cfc = getCurrentFinancialCount();
        FinancialCountIfc ufc = getUpdateFinancialCount();
        CurrencyIfc zero = (CurrencyIfc)model.getTotal().clone();
        zero.setStringValue(STRING_ZERO);
        Long[] counts = model.getDenominationCounts();
        DenominationIfc[] denominations = (DenominationIfc[])zero.getType().getDenominations()
                .toArray(new DenominationIfc[0]);
        CurrencyIfc total = (CurrencyIfc)zero.clone();

        for (int cnt = 0; cnt < counts.length; cnt++)
        {
            // Get the data from detail bean model
            enteredCnt = counts[cnt].intValue();
            enteredDsc = denominations[cnt].getDenominationName();
            enteredAmt = (CurrencyIfc)zero.clone();
            enteredAmt.setStringValue(denominations[cnt].getDenominationValue());
            enteredAmt = enteredAmt.multiply(new BigDecimal(enteredCnt));

            // Update total for adding summary item
            total = total.add(enteredAmt);

            TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
            td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
            td.setTenderSubType(enteredDsc); // name of denomination
            td.setCountryCode(enteredAmt.getCountryCode());
            td.setCurrencyID(enteredAmt.getType().getCurrencyId());
            td.setDenomination(denominations[cnt]);

            // Set the entered counts for start floats and loans.
            if (countType == START_FLOAT || countType == LOAN || countType == TILL)
            {
                // These counts are coming into the till
                cfc.addTenderItem(td, enteredCnt, 0, enteredAmt, zero, enteredDsc,
                        model.getSummaryCurrencyDescription(), false, false);
                ufc.addTenderItem(td, enteredCnt, 0, enteredAmt, zero, enteredDsc,
                        model.getSummaryCurrencyDescription(), false, false);
            }
            else if (countType == START_SAFE || countType == END_SAFE)
            {
                cfc.addTenderItem(td, enteredCnt, 0, enteredAmt, zero, enteredDsc,
                        model.getSummaryCurrencyDescription(), false, false);
            }
            // Set the entered counts for end floats
            else
            {
                // These count are going out of the till
                cfc.addTenderItem(td, 0, enteredCnt, zero, enteredAmt, enteredDsc,
                        model.getSummaryCurrencyDescription(), false, false);
                ufc.addTenderItem(td, 0, enteredCnt, zero, enteredAmt, enteredDsc,
                        model.getSummaryCurrencyDescription(), false, false);
            }
        }

        // The operator has not counted the tender then there will be no counts;
        // if this is a till reconcile count use zero, otherwise use the total
        // from the the model.
        if (counts.length == 0)
        {
            total = model.getTotal();
            if (countType == TILL)
            {
                total = zero;
            }
        }

        // Now add SUMMARY cash line item containing total of all Cash
        // denominations
        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        td.setTenderSubType("");
        td.setCountryCode(total.getCountryCode());
        td.setCurrencyID(total.getType().getCurrencyId());

        // If this is a till reconcile, get the expected count if necessay.
        if (countType == TILL)
        {
            total = determineTotalAmountToSave(td, total);
        }

        if (countType == START_FLOAT || countType == LOAN || countType == TILL)
        {
            // These counts are coming into the till
            cfc.addTenderItem(td, 1, 0, total, zero, model.getSummaryCurrencyDescription(), "", false, true);
            ufc.addTenderItem(td, 1, 0, total, zero, model.getSummaryCurrencyDescription(), "", false, true);
        }
        else if (countType == START_SAFE || countType == END_SAFE)
        {
            cfc.addTenderItem(td, 1, 0, total, zero, model.getSummaryCurrencyDescription(), "", false, true);
        }
        // Set the entered counts for end floats
        else
        {
            // These count are going out of the till
            cfc.addTenderItem(td, 0, 1, zero, total, model.getSummaryCurrencyDescription(), "", false, true);
            ufc.addTenderItem(td, 0, 1, zero, total, model.getSummaryCurrencyDescription(), "", false, true);
        }
    }

    /**
     * Sets the cash detail amounts in the financial totals object. Services
     * that count Cash details (pennies, nickels, dimes, etc.) call this method
     * based on the blindClose parameter
     * 
     * @param model
     * @param BlindClose
     * @deprecated in 13.3; see updateTillCashDetailAmountInTotals()
     */
    public void updateCashDetailAmountInTotals(CurrencyDetailBeanModel model, boolean blindClose)
    {
        this.blindClose = blindClose;
        updateTillCashDetailAmountInTotals(model);
    }

    /**
     * Sets the cash detail amounts in the financial totals object. Services
     * that count Cash details (pennies, nickels, dimes, etc.) call this method
     * based on the blindClose parameter
     * 
     * @param model
     * @param BlindClose
     */
    public void updateTillCashDetailAmountInTotals(CurrencyDetailBeanModel model)
    {
        int enteredCnt = 0;
        String enteredDsc = null;
        CurrencyIfc enteredAmt = null;
        FinancialCountIfc cfc = getCurrentFinancialCount();
        FinancialCountIfc ufc = getUpdateFinancialCount();
        CurrencyIfc zero = (CurrencyIfc)model.getTotal().clone();
        zero.setStringValue(STRING_ZERO);
        Long[] counts = model.getDenominationCounts();
        DenominationIfc[] denominations = (DenominationIfc[])zero.getType().getDenominations()
                .toArray(new DenominationIfc[0]);
        CurrencyIfc total = (CurrencyIfc)zero.clone();

        // Iterate throught the individual demonination counts, add tender count
        // line items
        // the financial count saved to the database and increment the running
        // total.
        for (int cnt = 0; cnt < counts.length; cnt++)
        {
            // Get the data from detail bean model
            enteredCnt = counts[cnt].intValue();
            enteredDsc = denominations[cnt].getDenominationName();
            enteredAmt = (CurrencyIfc)zero.clone();
            enteredAmt.setStringValue(denominations[cnt].getDenominationValue());
            enteredAmt = enteredAmt.multiply(new BigDecimal(enteredCnt));

            // Update total for adding summary item
            total = total.add(enteredAmt);

            TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
            td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
            td.setTenderSubType(enteredDsc); // name of denomination
            td.setCountryCode(enteredAmt.getCountryCode());
            td.setCurrencyID(enteredAmt.getType().getCurrencyId());
            td.setDenomination(denominations[cnt]);

            // These count are going out of the till
            cfc.addTenderItem(td, 0, enteredCnt, zero, enteredAmt, enteredDsc, model.getSummaryCurrencyDescription(),
                    false, false);
            ufc.addTenderItem(td, 0, enteredCnt, zero, enteredAmt, enteredDsc, model.getSummaryCurrencyDescription(),
                    false, false);
        }

        // Now add SUMMARY cash line item containing total of all Cash
        // denominations
        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        td.setTenderSubType("");
        td.setCountryCode(total.getCountryCode());
        td.setCurrencyID(total.getType().getCurrencyId());

        // get the expected count, if necessary.
        total = determineTotalAmountToSave(td, total);

        // These count are going out of the till
        cfc.addTenderItem(td, 1, 0, total, zero, model.getSummaryCurrencyDescription(), "", false, true);
        ufc.addTenderItem(td, 1, 0, total, zero, model.getSummaryCurrencyDescription(), "", false, true);
    }

    /**
     * Determines whether to use the entered amount or the expected amount if
     * the entered amout is equal to zero.
     * 
     * @param td
     * @param countedAmount
     * @return The amount to save
     */
    protected CurrencyIfc determineTotalAmountToSave(TenderDescriptorIfc td, CurrencyIfc countedAmount)
    {
        // If this is a blind close and the count of this tender is
        // required, then the expected amount cannot be used.
        boolean useExpectedIfEnteredIsZero = true;
        String tenderDescription = getTenderToCountParameterText(td);
        if (blindClose && tendersToCount.contains(tenderDescription))
        {
            useExpectedIfEnteredIsZero = false;
        }

        // If the expected amount can be used, and the counted amount is zero...
        CurrencyIfc returnCount = null;
        if (useExpectedIfEnteredIsZero && countedAmount.signum() == 0)
        {
            // Get the associated expected amount, and if not null, use that
            // amount
            FinancialCountTenderItemIfc tenderItem = financialTotals.getCombinedCount().getExpected()
                    .getTenderItem(td, false);
            if (tenderItem == null)
            {
                returnCount = countedAmount;
            }
            else
            {
                returnCount = tenderItem.getAmountTotal();
            }
        }
        else
        {
            returnCount = countedAmount;
        }

        return returnCount;
    }

    /**
     * Get the tender to count text associated with the current tender.
     * 
     * @param td
     * @return
     */
    protected String getTenderToCountParameterText(TenderDescriptorIfc td)
    {
        // Get the text from the tender map
        String parameterName = TenderTypeMap.getTenderTypeMap().getCountedTenderDescriptor(td.getTenderType());

        // If the currency is a foreign currency, add the ISO currency
        // ID as prefix.
        CurrencyTypeIfc baseCurrencyType = DomainGateway.getBaseCurrencyType();
        if (baseCurrencyType.getCurrencyId() != td.getCurrencyID())
        {
            CurrencyTypeIfc[] currencyTypes = DomainGateway.getAlternateCurrencyTypes();
            for (CurrencyTypeIfc currencyType : currencyTypes)
            {
                if (currencyType.getCurrencyId() == td.getCurrencyID())
                {
                    parameterName = currencyType.getCurrencyCode() + parameterName;
                    break;
                }
            }
        }

        return parameterName;
    }

    /**
     * Finds or creates the Financial Count Object associated with the current
     * count type.
     * 
     * @return FinancialCountIfc The Financial Count Object associated with the
     *         current count type
     */
    protected FinancialCountIfc getCurrentFinancialCount()
    {
        FinancialCountIfc fc = null;
        ReconcilableCountIfc rc = null;

        switch (countType)
        {
        case TILL:
            fc = financialTotals.getCombinedCount().getEntered();
            break;

        case START_FLOAT:
            fc = financialTotals.getStartingFloatCount().getEntered();
            break;

        case END_FLOAT:
            fc = financialTotals.getEndingFloatCount().getEntered();
            break;

        case START_SAFE:
            fc = financialTotals.getStartingSafeCount().getEntered();
            break;

        case END_SAFE:
            fc = financialTotals.getEndingSafeCount().getEntered();
            break;

        case LOAN:
            rc = financialTotals.instantiateReconcilableCountIfc();
            financialTotals.addTillLoans(rc);
            fc = rc.getEntered();
            break;

        case PICKUP:
            rc = financialTotals.instantiateReconcilableCountIfc();
            financialTotals.addTillPickups(rc);
            fc = rc.getEntered();
            break;

        default:
            break;
        }

        return fc;
    }

    /**
     * Finds or creates the Financial Count Object associated with the Update
     * count type.
     * 
     * @return the financial count
     */
    protected FinancialCountIfc getUpdateFinancialCount()
    {
        FinancialCountIfc fc = null;
        ReconcilableCountIfc rc = null;

        switch (countType)
        {
        case TILL:
            fc = updateFT.getCombinedCount().getEntered();
            break;

        case START_FLOAT:
            fc = updateFT.getStartingFloatCount().getEntered();
            break;

        case END_FLOAT:
            fc = updateFT.getEndingFloatCount().getEntered();
            break;

        case LOAN:
            rc = updateFT.instantiateReconcilableCountIfc();
            updateFT.addTillLoans(rc);
            fc = rc.getEntered();
            break;

        case PICKUP:
            rc = updateFT.instantiateReconcilableCountIfc();
            updateFT.addTillPickups(rc);
            fc = rc.getEntered();
            break;

        default:
            break;
        }

        return fc;
    }

    /**
     * Places an the entered amount into the correct total summary bean model
     * (tender or charge).
     * 
     * @param enteredAmt the entered amount
     */
    public void updateCountModel(CurrencyIfc enteredAmt)
    {
        SummaryCountBeanModel[] models = getCountModels();
        String name = getCurrentActivityOrCharge();

        for (int i = 0; i < models.length; i++)
        {
            String mDesc = models[i].getDescription();
            if (mDesc.equals(name))
            {
                // and set the amount in it so that it will show
                // on the selection panel.
                // models[i].setNegativeAllowed(new Boolean(true));
                models[i].setNegativeAllowed(Boolean.TRUE);
                // This sets the enteredAmt to the model[i]
                models[i].setAmount(enteredAmt);
                break; // break for loop
            }
        }

        if (currentActivity.equals(CHARGE))
        {
            updateChargeCountModel();
        }
    }

    /**
     * Updates the charge summary amount in the the tender SummaryCountBeanModel
     * array.
     */
    public void updateChargeCountModel()
    {
        CurrencyIfc cd = DomainGateway.getBaseCurrencyInstance();
        for (int i = 0; i < chargeModels.length; i++)
        {
            cd = cd.add(chargeModels[i].getAmount());
        }

        for (int i = 0; i < tenderModels.length; i++)
        {
            if (tenderModels[i].getDescription().equals(CHARGE))
            {
                tenderModels[i].setAmount(cd);
            }
        }
    }

    /**
     * Adds the current tender name to the list of accepted counts.
     */
    public void updateAcceptedCount()
    {
        String name = getNameFromCurrentActivityOrCharge();
        if (!acceptedCount.contains(name))
        {
            acceptedCount.addElement(name);
        }
    }

    /**
     * Adds the current tender name to the list of accepted counts.
     */
    public void updateAcceptedCountWithCharge()
    {
        if (!acceptedCount.contains(CHARGE))
        {
            acceptedCount.addElement(CHARGE);
        }
    }

    /**
     * Checks the accepted count vector to see if it contains the tender name.
     * 
     * @param name The name of tender.
     * @return boolean true if the given name is contained in the accepted count
     *         vector.
     */
    public boolean acceptedCountContains(String name)
    {
        return acceptedCount.contains(name);
    }

    /**
     * Gets the array of SummaryCountBeanModels.
     * 
     * @return An array of SummaryCountBeanModels
     */
    public SummaryCountBeanModel[] getCountModels()
    {
        // Decide which list to build.
        if (currentActivity.equals(CHARGE))
        {
            return getChargeModels();
        }
        else
        {
            return getTenderModels();
        }
    }

    /**
     * Builds the list of Credit SummaryCountBeanModels from the Expected Counts
     * in the TenderCount Totals object.
     * 
     * @return SummaryCountBeanModel[] The list of Credit SummaryCountBeanModels
     */
    protected SummaryCountBeanModel[] getChargeModels()
    {
        if (chargeModels == null)
        {
            SummaryCountBeanModel scbm = null;

            FinancialCountTenderItemIfc[] fctis = financialTotals.getCombinedCount().getExpected().getTenderItems();
            Set<Object> set = new HashSet<Object>();
            Set<Object> setTester = new HashSet<Object>();
            for (int i = 0; i < fctis.length; i++)
            {
                if (fctis[i].getSummaryDescription().equals(CHARGE))
                {
                    scbm = new SummaryCountBeanModel();
                    scbm.setDescription(fctis[i].getDescription());
                    scbm.setAmount((fctis[i].getAmountIn()).subtract(fctis[i].getAmountOut()));
                    scbm.setNegativeAllowed(new Boolean(true));
                    boolean success = setTester.add(scbm.getDescription());
                    if (success)
                    {
                        set.add(scbm);
                    }
                }
            }
            chargeModels = new SummaryCountBeanModel[set.size()];
            chargeModels = (SummaryCountBeanModel[])set.toArray(chargeModels);
        }

        return chargeModels;
    }

    /**
     * Builds the list of Tender types.
     * 
     * @return the list of tender types
     */
    protected SummaryCountBeanModel[] getTenderModels()
    {
        if (tenderModels == null)
        {
            Vector<SummaryCountBeanModel> modelsVector = new Vector<SummaryCountBeanModel>();
            SummaryCountBeanModel scbm = null;
            CurrencyIfc currency = null;

            FinancialCountIfc fc = financialTotals.getTenderCount();
            FinancialCountTenderItemIfc[] fctis = fc.getTenderItems();
            for (int i = 0; i < fctis.length; i++)
            {
                if (fctis[i].isSummary())
                {
                    scbm = new SummaryCountBeanModel();
                    currency = fctis[i].getAmountTotal();
                    currency = (CurrencyIfc)currency.clone();
                    currency.setStringValue(STRING_ZERO);
                    scbm.setAmount(currency);
                    scbm.setDescription(fctis[i].getDescription());
                    scbm.setNegativeAllowed(new Boolean(true));
                    modelsVector.addElement(scbm);
                }
            }

            tenderModels = new SummaryCountBeanModel[modelsVector.size()];
            modelsVector.copyInto(tenderModels);
        }

        return tenderModels;
    }

    /**
     * Sets the list of Tender types.
     * 
     * @param scbm The tender summary count models
     */
    protected void setTenderModels(SummaryCountBeanModel[] scbm)
    {
        tenderModels = scbm;

    }

    /**
     * Sets the list of Tender types.
     * 
     * @param scbm The summary count bean model array of charges.
     */
    protected void setChargeModels(SummaryCountBeanModel[] scbm)
    {
        chargeModels = scbm;

    }

    /**
     * Adds a tender descriptor to the hash. A descriptor is needed for each
     * type of alternate tender we are counting (i.e.: canadian cash, mexican
     * checks)
     * 
     * @param key The alternate tender
     * @param td The associated tender descriptor object.
     */
    public void addTenderDescriptor(String key, TenderDescriptorIfc td)
    {
        tenderDescriptorHash.put(key, td);
    }

    /**
     * Retrieves the desired descriptor based on the current activity.
     * 
     * @return TenderDescriptorIfc The tender descriptor for the current
     *         activity
     */
    public TenderDescriptorIfc getTenderDescriptor()
    {
        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();

        if (currentActivity.equals(CHARGE))
        {
            td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CHARGE);
            td.setCountryCode(DomainGateway.getBaseCurrencyType().getCountryCode());
            td.setTenderSubType(currentCharge);
            td.setCurrencyID(DomainGateway.getBaseCurrencyType().getCurrencyId());
        }
        else if (tenderDescriptorHash.get(currentActivity) != null)
        {
            // alternate tender types are contained in this hash
            td = (TenderDescriptorIfc)tenderDescriptorHash.get(currentActivity);
        }
        else
        {
            td.setTenderType(tenderTypeMap.getTypeFromDescriptor(currentActivity));
            if (getCurrentForeignCurrency().equals(NONE))
            {
                td.setCountryCode(DomainGateway.getBaseCurrencyType().getCountryCode());
                td.setCurrencyID(DomainGateway.getBaseCurrencyType().getCurrencyId());
            }
            else
            {
                td.setCountryCode(getCountryCodeForForeignCurrency(currentForeignCurrency));
                td.setCurrencyID(getCurrencyIDForForeignCurrency(currentForeignCurrency));
            }
        }

        return td;
    }

    /**
     * Retrieves the desired descriptor based on the current FLP tender.
     * 
     * @return TenderDescriptorIfc The tender descriptor for the current FLP
     *         tender.
     */
    public TenderDescriptorIfc getTenderDescriptorForCurrentFLPTender()
    {
        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();

        // Remove foreign nationality part
        String currentFLPTenderDescription = getCurrentFLPTender();
        int index = currentFLPTenderDescription.lastIndexOf(" ");
        if (index != -1)
        {
            currentFLPTenderDescription = currentFLPTenderDescription.substring(index + 1);
        }
        td.setTenderType(tenderTypeMap.getTypeFromDescriptor(currentFLPTenderDescription));
        td.setCountryCode(getTenderNationality());

        // check if base or alternate
        CurrencyIfc base = DomainGateway.getBaseCurrencyInstance();
        int currencyId = base.getType().getCurrencyId();
        if (!base.getCountryCode().equals(getTenderNationality()))
        {
            CurrencyIfc curr = DomainGateway.getAlternateCurrencyInstance(getTenderNationality());
            currencyId = curr.getType().getCurrencyId();
        }
        td.setCurrencyID(currencyId);
        td.setTenderSubType(currentFLPTenderDescription);

        return td;
    }

    /**
     * Gets the expected/entered tender items array from financial totals
     * current state.
     * 
     * @return Contains amount entered by the user.
     */
    public FinancialCountTenderItemIfc[] getTenderItemsArray()
    {
        return financialTotals.getTenderCount().getTenderItems();
    }

    /**
     * Builds the key for the current entry in the tenderDetails hash map.
     * 
     * @return String The key for the current tenderDetails map entry.
     */
    public Object buildKeyForTenderDetails()
    {
        StringBuffer buildKey = new StringBuffer();
        if (!getCurrentForeignNationality().equals(NONE))
        {
            buildKey.append(getCurrentForeignNationality()).append(" ");
        }
        buildKey.append(getCurrentActivityOrCharge());
        return buildKey.toString();
    }

    /**
     * Gets the current OtherTenderDetailsBeanModel.
     * 
     * @return Contains amount entered by the user.
     */
    public OtherTenderDetailBeanModel getOtherTenderDetailBeanModel()
    {
        // Get the key and look for the model in the hashtable
        Object key = buildKeyForTenderDetails();
        OtherTenderDetailBeanModel model = (OtherTenderDetailBeanModel)tenderDetails.get(key);

        // If there is none, create one.
        if (model == null)
        {
            model = new OtherTenderDetailBeanModel();
            model.setDescription((String)key);
            if (getCurrentActivity().equals(PosCountCargo.CHARGE))
            {
                model.setSummaryDescription(PosCountCargo.CHARGE);
            }

            CurrencyIfc total = (CurrencyIfc)getExpectedAmount().clone();
            total.setStringValue(STRING_ZERO);
            model.setTotal(total);
            setOtherTenderDetailBeanModel(model);
        }

        return model;
    }

    /**
     * Sets the current OtherTenderDetailsBeanModel.
     * 
     * @param value Contains amount entered by the user.
     */
    public void setOtherTenderDetailBeanModel(OtherTenderDetailBeanModel value)
    {
        // Get the key
        String key = (String)buildKeyForTenderDetails();

        // If it already exits in the hashtable, remove it.
        if (tenderDetails.containsKey(key))
        {
            tenderDetails.remove(key);
        }

        // Add the the model to the table
        tenderDetails.put(key, value);
    }

    /**
     * Resets the tenderDetails map.
     */
    public void resetTenderDetails()
    {
        tenderDetails.clear();
    }

    /**
     * Instantiates a FinancialCountTenderItemIfc. It uses a call to
     * getFloatCount() to do that. It does not matter which FinancialCount
     * object we use because this is a general purpose convience routine.
     * 
     * @return a FinancialCountTenderItemIfc object
     */
    public FinancialCountTenderItemIfc instantiateFinancialCountTenderItemIfc()
    {
        return financialTotals.getStartingFloatCount().getEntered().instantiateFinancialCountTenderItemIfc();
    }

    /**
     * Set the Till Reconcile tender models flag
     * 
     * @param value True if the tender count model has been assigned
     */
    public void setTenderCountModelAssigned(boolean value)
    {
        tenderCountModelAssigned = value;
    }

    /**
     * Returns the Till Reconcile tender models flag
     * 
     * @return boolean Indicates whether the tender model has been assigned.
     */
    public boolean isTenderCountModelAssigned()
    {
        return tenderCountModelAssigned;
    }

    /**
     * Set the Till Reconcile charge models flag
     * 
     * @param value true if the charge count model has been assigned
     */
    public void setChargeCountModelAssigned(boolean value)
    {
        chargeCountModelAssigned = value;
    }

    /**
     * Returns the Till Reconcile charge models flag
     * 
     * @return boolean Indicates whether the charge count model has been
     *         assigned.
     */
    public boolean isChargeCountModelAssigned()
    {
        return chargeCountModelAssigned;
    }

    /**
     * Gets the string values associated with a specified parameter. If the
     * value can not be found, a warning is logged and null is returned.
     * 
     * @param bus The service bus
     * @param parameter The parameter to read the values of
     * @return a String array containing the values for the parameter or null if
     *         the parameter can not be found
     */
    public String[] getParameterStringValues(BusIfc bus, String parameter)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        String[] result = null;

        try
        {
            result = pm.getStringValues(parameter);
            if (logger.isDebugEnabled())
                logger.debug("" + getClass().getName() + ": getParameterStringValues(String): read parameter "
                        + parameter + "");
        }
        catch (ParameterException pe)
        {
            logger.warn("" + getClass().getName() + ": getParameterStringValues(String): " + "Could not read parameter"
                    + " [" + parameter + "]");
        }

        return result;
    }

    /**
     * Parameter values required by the till reconcile process.
     * 
     * @param bus The service bus
     */
    public void setTillReconcileValuesFromParameters(BusIfc bus)
    {
        blindClose = getBlindCloseParameterValue(bus);
        getTendersToCountAtTillReconcileParameterValue(bus);
    }

    /**
     * Gets the boolean value associated with the Blind Close parameter. If the
     * value can not be found, a warning is logged and true.
     * 
     * @param bus The service bus
     * @return boolean value for the parameter
     */
    public boolean getBlindCloseParameterValue(BusIfc bus)
    {
        boolean blindClose = true;
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            Boolean blindCloseParameter = pm.getBooleanValue(BLIND_CLOSE);
            blindClose = blindCloseParameter.booleanValue();
        }
        catch (ParameterException pe)
        {
            logger.warn(getClass().getName() + ": getBlindCloseParameter(): Could not read parameter [BlindClose]");
        }
        return blindClose;
    }

    /**
     * Gets the array of counted tenders from the parameter manager.
     * 
     * @param bus The service bus
     * @return String array of parameter types
     */
    public void getTendersToCountAtTillReconcileParameterValue(BusIfc bus)
    {
        String[] tenders = null;
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            tenders = pm.getStringValues(TENDERS_TO_COUNT_AT_TILL_RECONCILE);
        }
        catch (ParameterException pe)
        {
            logger.warn(getClass().getName() + "Could not read parameter [TendersToCountAtTillReconcile]");
            tenders = new String[3];
            tenders[0] = CASH;
            tenders[1] = CHECK;
            tenders[2] = TRAVELERS_CHECK;
        }

        Collections.addAll(tendersToCount, tenders);
    }

    /**
     * Get the expected amount for a tender type. Note that this will not work
     * for alternate currency.
     * 
     * @param tenderType the tender type as defined in
     *            oracle.retail.stores.domain.tender.TenderLineItemIfc
     * @return the total expected amount for the tender type
     */
    public CurrencyIfc getExpectedAmount(int tenderType)
    {
        CurrencyIfc expectedAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc tmpAmount = null;
        FinancialCountTenderItemIfc[] fcti = financialTotals.getCombinedCount().getExpected().getTenderItems();
        for (int i = 0; i < fcti.length; i++)
        {
            if ((fcti[i] != null) && (fcti[i].getTenderType() == tenderType))
            {
                tmpAmount = fcti[i].getAmountTotal();
                if (tmpAmount != null)
                {
                    expectedAmount = expectedAmount.add(tmpAmount);
                    if (tenderType != TenderLineItemIfc.TENDER_TYPE_CHARGE)
                    {
                        break;
                    }
                }
            }
        }
        return expectedAmount;
    }

    /**
     * Get the expected amount for a specific tender type.
     * 
     * @param description from the tender descriptor
     * @return the total expected amount for the tender
     * @deprecated in version 13.3 see getExpectedAmount(String, String), this
     *             method is not recommended due to fact that it can erroneously
     *             return a base currency value for a foreign currency
     *             description.
     */
    public CurrencyIfc getExpectedAmount(String description)
    {
        return getExpectedAmount(description, DomainGateway.getBaseCurrencyInstance().getCountryCode());
    }

    /**
     * Get the expected amount for a specific tender type.
     * 
     * @param description from the tender descriptor
     * @return the total expected amount for the tender
     */
    public CurrencyIfc getExpectedAmount(String description, String countryCode)
    {
        CurrencyIfc expectedAmount = null;
        CurrencyIfc tmpAmount = null;
        // Cash Field in Select Tender to be independent of opening/closing
        // float
        FinancialCountTenderItemIfc[] fcti = financialTotals.getCombinedCountForTenderReconcile().getExpected()
                .getTenderItems();
        for (int i = 0; i < fcti.length; i++)
        {
            String itemDescription = fcti[i].getDescription();
            if ((fcti[i] != null) && (itemDescription != null) && (itemDescription.equals(description)))
            {
                tmpAmount = fcti[i].getAmountTotal();
                if (tmpAmount != null)
                { // this needs to work with alternate currencies as well
                    if (expectedAmount == null)
                    {
                        expectedAmount = tmpAmount;
                    }
                    else
                    {
                        expectedAmount = expectedAmount.add(tmpAmount);
                    }
                }

            }
        }

        if (expectedAmount == null)
        {

            expectedAmount = DomainGateway.getCurrencyInstance(countryCode);

        }
        return expectedAmount;
    }

    /**
     * Returns the input string with all blanks removed.
     * 
     * @param in The input String
     * @return String representation of input string with all blank characters
     *         removed.
     */
    public String removeBlanks(String in)
    {
        StringBuffer out = new StringBuffer(in.length());
        int j = 0;
        for (int i = 0; i < in.length(); i++)
        {
            char c = in.charAt(i);
            if (c != ' ')
            {
                out.insert(j++, c);
            }
        }
        out.setLength(j);
        return (out.toString());
    }

    /**
     * Sets the tender nationality.
     * 
     * @param value The tender nationality
     */
    public void setTenderNationality(String value)
    {
        tenderNationality = value;
    }

    /**
     * Returns the tender nationality.
     * 
     * @return String The tender nationality
     */

    public String getTenderNationality()
    {
        if (tenderNationality == null)
        {
            return DomainGateway.getBaseCurrencyInstance().getCountryCode();
        }
        else
        {
            return tenderNationality;
        }
    }

    /**
     * Returns the 'To' Register for Pickup or Loan when operating without a
     * safe.
     * 
     * @return String 'To' Register unique ID
     */
    public String getPickupAndLoanToRegister()
    {
        return pickupAndLoanToRegister;
    }

    /**
     * Sets the 'To' Register for Pickup or Loan when operating without a safe.
     * 
     * @param value 'To' Register unique ID
     */
    public void setPickupAndLoanToRegister(String value)
    {
        pickupAndLoanToRegister = value;
    }

    /**
     * Returns the 'From' Register for Pickup or Loan when operating without a
     * safe.
     * 
     * @return String 'From' Register unique ID
     */
    public String getPickupAndLoanFromRegister()
    {
        return pickupAndLoanFromRegister;
    }

    /**
     * Sets the 'From' Register for Pickup or Loan when operating without a
     * safe.
     * 
     * @param value 'From' Register unique ID
     */
    public void setPickupAndLoanFromRegister(String value)
    {
        pickupAndLoanFromRegister = value;
    }

    /**
     * Gets the array of foreign currency financial tender totals.
     * 
     * @return FinancialCountTenderItemIfc[] The array of alternate currency
     *         financial tender totals
     */
    public FinancialCountTenderItemIfc[] getForeignCurrencyFinancialCountTenderTotals()
    {
        if (foreignCurrencyFinancialCountTenderTotals == null)
        {
            foreignCurrencyFinancialCountTenderTotals = getFinancialTotals().getCombinedCount().getExpected()
                    .getAlternateCurrencyFinancialCountTenderTotals();
        }
        return foreignCurrencyFinancialCountTenderTotals;
    }

    /**
     * Sets the array of foreign currency financial tender totals.
     * 
     * @param value FinancialCountTenderItemIfc[]
     */
    public void setForeignCurrencyFinancialCountTenderTotals(FinancialCountTenderItemIfc[] value)
    {
        foreignCurrencyFinancialCountTenderTotals = value;
    }

    /**
     * Gets the Summary Count Bean Model array for Foreign Currency Count.
     * 
     * @return SummaryCountBeanModel[]
     */
    public SummaryCountBeanModel[] getSummaryForeignCurrencyCountBeanModel()
    {
        return summaryForeignCurrencyCountBeanModel;
    }

    /**
     * Sets the Summary Count Bean Model array for Foreign Currency Count.
     * 
     * @param value SummaryCountBeanModel[]
     */
    public void setSummaryForeignCurrencyCountBeanModel(SummaryCountBeanModel[] value)
    {
        summaryForeignCurrencyCountBeanModel = value;
    }

    /**
     * Gets the array of foreign currency summary count bean models. Initializes
     * the models with the expected amounts on the first call.
     * 
     * @param sftmbm The Summary Foreign Tender Menu Bean Model
     * @param bus The service bus
     * @return SummaryCountBeanModel[] The foreign currency summary count bean
     *         models
     */
    public SummaryCountBeanModel[] getForeignCurrencyBeanModels(SummaryForeignCurrencyCountMenuBeanModel sftmbm,
            BusIfc bus)
    {
        if (getSummaryForeignCurrencyCountBeanModel() == null)
        {
            SummaryCountBeanModel[] scbm = sftmbm.getSummaryCountBeanModel();

            // get blind close parameter value and set bean model flag
            boolean blindClose = getBlindCloseParameterValue(bus);
            sftmbm.setBlindClose(blindClose);

            // set expected amounts
            FinancialCountTenderItemIfc[] alternateCurrencyAmount = getForeignCurrencyFinancialCountTenderTotals();
            for (int i = 0; i < scbm.length; i++)
            {
                CurrencyIfc expectedAmount = null;
                for (int j = 0; j < alternateCurrencyAmount.length; j++)
                {
                    if (alternateCurrencyAmount[j].getCurrencyCode().equals(scbm[i].getLabel()))
                    {
                        expectedAmount = alternateCurrencyAmount[j].getAmountTotal();
                    }
                }
                if (expectedAmount == null)
                {
                    expectedAmount = DomainGateway.getAlternateCurrencyInstance(scbm[i].getLabel());
                }

                scbm[i].setExpectedAmount(expectedAmount);
                if (blindClose)
                {
                    scbm[i].setExpectedAmountHidden(true);
                }
                else
                {
                    // set all amounts to the expected amount
                    scbm[i].setExpectedAmountHidden(false);
                    if (getCurrentForeignCurrency().equals(PosCountCargo.NONE))
                    {
                        scbm[i].setAmount(scbm[i].getExpectedAmount());
                    }
                }
            }
            setSummaryForeignCurrencyCountBeanModel(scbm);

        }
        return summaryForeignCurrencyCountBeanModel;
    }

    /**
     * Gets the current foreign currency to count.
     * 
     * @return String The currency description.
     */
    public String getCurrentForeignCurrency()
    {
        return currentForeignCurrency;
    }

    /**
     * Sets the current foreign currency to count.
     * 
     * @param value String The currency description.
     */
    public void setCurrentForeignCurrency(String value)
    {
        currentForeignCurrency = value;
    }

    /**
     * Gets the nationality of the current foreign currency to count.
     * 
     * @return String The current foreign nationality.
     */
    public String getCurrentForeignNationality()
    {
        return currentForeignNationality;
    }

    /**
     * Sets the nationality of the current foreign currency to count.
     * 
     * @param value String The current foreign nationality.
     */
    public void setCurrentForeignNationality(String value)
    {
        currentForeignNationality = value;
    }

    /**
     * Set the Till Reconcile foreign tender models flag
     * 
     * @param value True if the foreign tender count model has been assigned
     */
    public void setForeignTenderCountModelAssigned(boolean value)
    {
        foreignTenderCountModelAssigned = value;
    }

    /**
     * Returns the Till Reconcile foreign tender models flag
     * 
     * @return boolean Indicates whether the foreign tender model has been
     *         assigned.
     */
    public boolean isForeignTenderCountModelAssigned()
    {
        return foreignTenderCountModelAssigned;
    }

    /**
     * Returns the list of Foreign Tender models for the current foreign
     * currency.
     * 
     * @return the list of tender types
     */
    protected SummaryCountBeanModel[] getForeignTenderModels()
    {
        SummaryCountBeanModel[] ftm = null;

        String foreignCurrency = getCurrentForeignCurrency();

        if (foreignCurrency != null)
        {
            ftm = (SummaryCountBeanModel[])foreignTenderModels.get(foreignCurrency);
        }

        return ftm;
    }

    /**
     * Returns the list of Foreign Tender models for the current foreign
     * currency.
     * 
     * @return the list of tender types
     */
    protected SummaryCountBeanModel[] getForeignTenderModelsForTillCount()
    {
        SummaryCountBeanModel[] ftms = getForeignTenderModels();

        // If the foreign tender model is null, this means that the operator
        // never
        // counted this particular foreign tender. We still need to account for
        // these
        // items. Get the defaults from the SummaryForeignTenderMenuBeanModel.
        if (ftms == null)
        {
            String countryCode = getCountryCodeForForeignCurrency(getCurrentForeignCurrency());
            if (countryCode != null)
            {
                // Get the correct currency type for the foriegn currency.
                SummaryForeignTenderMenuBeanModel sftmbm = new SummaryForeignTenderMenuBeanModel();
                ftms = sftmbm.getSummaryCountBeanModel();
                CurrencyIfc zero = DomainGateway.getAlternateCurrencyInstance(countryCode);
                // Iterate through the SummaryCountBeanModels and set the
                // currency.
                for (SummaryCountBeanModel ftm : ftms)
                {
                    ftm.setAmount(zero);
                }
            }
        }

        return ftms;
    }

    /**
     * Sets the list of Foreign Tender types.
     * 
     * @param scbm The foreign tender summary count models
     */
    protected void setForeignTenderModels(SummaryCountBeanModel[] scbm)
    {
        String foreignCurrency = getCurrentForeignCurrency();

        if (foreignCurrency != null)
        {
            foreignTenderModels.put(foreignCurrency, scbm);
        }
    }

    /**
     * Gets the local navigation button bean model for the foreign select tender
     * screen.
     * 
     * @return The navigation button bean model.
     */
    protected NavigationButtonBeanModel getForeignTenderLocalNavigationModel()
    {
        return foreignTenderLocalNavigationModel;
    }

    /**
     * Sets the local navigation button bean model for the foreign select tender
     * screen.
     * 
     * @param value The navigation button bean model.
     */
    protected void setForeignTenderLocalNavigationModel(NavigationButtonBeanModel value)
    {
        foreignTenderLocalNavigationModel = value;
    }

    /**
     * Gets the list of foreign tenders accepted for the current foregin
     * currency.
     * 
     * @return The list of foreign tenders accepted
     */
    protected String[] getForeignTendersAccepted()
    {
        return foreignTendersAccepted;
    }

    /**
     * Sets the list of foreign tenders accepted for the current foregin
     * currency.
     * 
     * @param value The list of foreign tenders accepted
     */
    protected void setForeignTendersAccepted(String[] value)
    {
        foreignTendersAccepted = value;
    }

    /**
     * Gets the list of foreign tenders to count for the current foregin
     * currency.
     * 
     * @return The list of foreign tenders to count
     */
    protected String[] getForeignTendersToCount()
    {
        return foreignTendersToCount;
    }

    /**
     * Sets the list of foreign tenders to count for the current foregin
     * currency.
     * 
     * @param value The list of foreign tenders to count
     */
    protected void setForeignTendersToCount(String[] value)
    {
        foreignTendersToCount = value;
    }

    /**
     * Looks up the country code for the given foreign currency ISO code.
     * 
     * @param isoCode The foreign currency ISO code
     * @return String The country code for the foreign currency description
     */
    public String getCountryCodeForForeignCurrency(String isoCode)
    {
        String returnValue = null;

        CurrencyTypeIfc currencies[] = DomainGateway.getAlternateCurrencyTypes();
        for (int i = 0; i < currencies.length; i++)
        {
            try
            {
                Object currencyObject = CurrencyServiceLocator.getCurrencyService().createCurrency(currencies[i]);
                if (currencyObject instanceof CurrencyIfc)
                {
                    CurrencyIfc currency = (CurrencyIfc)currencyObject;
                    if (currency.getCurrencyCode().equals(isoCode))
                    {
                        returnValue = ((CurrencyIfc)currency).getCountryCode();
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                logger.error(getClass().getName() + ": getCountryCodeForForeignCurrency(String): "
                        + "Foreign currency not found for" + " [" + isoCode + "] ");
            }
        }

        return returnValue;
    }

    /**
     * Looks up the country code for the given foreign currency ISO code.
     * 
     * @param isoCode The foreign currency ISO code
     * @return String The country code for the foreign currency ISO code
     */
    public int getCurrencyIDForForeignCurrency(String isoCode)
    {
        int returnValue = 0;

        CurrencyTypeIfc currencies[] = DomainGateway.getAlternateCurrencyTypes();
        for (int i = 0; i < currencies.length; i++)
        {
            try
            {
                Object currencyObject = CurrencyServiceLocator.getCurrencyService().createCurrency(currencies[i]);
                if (currencyObject instanceof CurrencyIfc)
                {
                    CurrencyIfc currency = (CurrencyIfc)currencyObject;
                    if (currency.getCurrencyCode().equals(isoCode))
                    {
                        returnValue = ((CurrencyIfc)currency).getType().getCurrencyId();
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                logger.error(getClass().getName() + ": getCountryCodeForForeignCurrency(String): "
                        + "Foreign currency not found for" + " [" + isoCode + "] ");
            }
        }

        return returnValue;
    }

    /**
     * Builds the name from the current activity or current charge. The
     * nationality is prepended for foreign currencies.
     * 
     * @return String The name of the current activity or charge
     */
    protected String getNameFromCurrentActivityOrCharge()
    {
        StringBuffer name = new StringBuffer();
        if (!getCurrentForeignNationality().equals(NONE))
        {
            name.append(getCurrentForeignNationality()).append(" ");
        }
        name.append(getCurrentActivityOrCharge());
        return name.toString();
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        String strResult = new String("Class:  PosCountCargo (Revision " + getRevisionNumber() + ") @" + hashCode());
        strResult += "\n";
        // add attributes to string
        if (register == null)
        {
            strResult += "register:                           [null]";
        }
        else
        {
            strResult += register.toString();
        }
        if (financialTotals == null)
        {
            strResult += "financialTotals:                    [null]";
        }
        else
        {
            strResult += financialTotals.toString();
        }
        strResult += "summaryFlag:                        [" + summaryFlag + "]";
        strResult += "countType:                          [" + countType + "]";
        strResult += "currentActivity:                    [" + currentActivity + "]";
        strResult += "currentCharge:                      [" + currentCharge + "]";
        // pass back result
        return (strResult);
    }

    /**
     * Returns the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Updates the summary totals for the till.
     */
    public void updateTillSummaryInTotalsNoChargeModels()
    {
        // Put the value in the financial totals object
        FinancialCountIfc fc = financialTotals.getCombinedCount().getEntered();
        FinancialCountIfc ufc = updateFT.getCombinedCount().getEntered();
        CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amtIn = null;
        CurrencyIfc amtOut = null;
        int cntIn = 0;
        int cntOut = 0;

        // Get the values from the tender SummaryCountBeanModel array and build
        // FinancialCountTenderItems for the Financial Totals
        for (int i = 0; i < getTenderModels().length; i++)
        {
            cntIn = 0;
            cntOut = 0;
            TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
            td.setCountryCode(tenderModels[i].getAmount().getCountryCode());
            td.setCurrencyID(tenderModels[i].getAmount().getType().getCurrencyId());
            td.setTenderType(tenderModels[i].getTenderType());

            // Get the expected count, if necessary.
            CurrencyIfc total = determineTotalAmountToSave(td, tenderModels[i].getAmount());

            if (total.signum() == CurrencyIfc.NEGATIVE)
            {
                amtIn = zero;
                amtOut = total.negate();
                cntOut = 1;
            }
            else
            {
                amtIn = total;
                amtOut = zero;
                cntIn = 1;
            }

            // Don't create the charge tender item. It will be created
            // by the charge model processing.
            // Also, don't add if both amounts are zero
            if (!tenderModels[i].getDescription().equals(CHARGE)
                    && ((amtIn.compareTo(zero) != CurrencyIfc.EQUALS) || (amtOut.compareTo(zero) != CurrencyIfc.EQUALS)))
            {
                fc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, tenderModels[i].getDescription(), "", false, true);
                ufc.addTenderItem(td, cntIn, cntOut, amtIn, amtOut, tenderModels[i].getDescription(), "", false, true);
            }
        }
    }

}
