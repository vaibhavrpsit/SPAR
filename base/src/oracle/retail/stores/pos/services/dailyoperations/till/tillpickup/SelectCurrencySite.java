/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpickup/SelectCurrencySite.java /main/12 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   01/29/09 - Using ISO Currency Code to Represent the Currency
 *                         Codes in the GUI
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         6/11/2007 11:51:27 AM  Anda D. Cadar   SCR
 *         27206: replace getNationality with getCountryCode; Nationality
 *         column in co_cny was poulated previosly with the value for the
 *         country code. I18N change was to populate nationality with
 *         nationality value
 *    4    360Commerce 1.3         4/25/2007 8:52:29 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:07 PM  Robert Pearse
 *
 *   Revision 1.5  2004/05/26 21:26:28  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Testing with no alternate currencies configured.
 *
 *   Revision 1.4  2004/04/29 20:06:21  dcobb
 *   @scr 4098 Open Drawer before detail count screens.
 *   Pickup changed to open drawer before detail count screens.
 *
 *   Revision 1.3  2004/02/12 16:50:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:43  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Feb 10 2004 14:29:38   DCobb
 * Open the cash drawer after a successful count.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.1   Jan 30 2004 12:48:36   DCobb
 * Configure the local navigation buttons dynamically according to the PickupTenders parameter.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.0   Aug 29 2003 15:58:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jun 26 2003 16:10:36   DCobb
 * Modifications for Canadian store front.
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.3   Jun 23 2003 13:44:08   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.2   Mar 04 2003 17:06:36   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 28 2002 10:43:42   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:26:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:36   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:19:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:15:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpickup;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This site allows the user to select LOCAL or ALTERNATE Currency pickup.
 * 
 * @version $Revision: /main/12 $
 */
public class SelectCurrencySite extends PosSiteActionAdapter
{
    /**
	 * Generated SerialVersionUID
	 */
	private static final long serialVersionUID = 257287393200888022L;
	/**
       revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";
    /**
      default pattern text
    **/
    public static final String DEF_PATTERN_TEXT = "{0} {1}";
    /**
      cash tag and default text
    **/
    public static final String CASH_TAG = "Cash";
    /**
      cash msg tag
    **/
    public static final String CASH_MSG_TAG = "CashMsg";
    /**
      checks tag and default text
    **/
    public static final String CHECKS_TAG = "Checks";
    /**
      check msg tag
    **/
    public static final String CHECKS_MSG_TAG = "ChecksMsg";
    /**
      nationality suffix
    **/
    public static final String NATIONALITY_SUFFIX = "_Nationality";

    /** Action name for Local Cash button */
    public static final String LOCAL = "Local";
    /** Action name for Alternate Cash button */
    public static final String ALTERNATE = "Alternate";

    /** Tag for text on local cash button */
    public static final String LOCAL_CASH_TAG = "LocalCash";
    /** Default text on local cash button */
    public static final String LOCAL_CASH_TEXT = "Local Cash";
    /** Tag for text on local checks button */
    public static final String LOCAL_CHECKS_TAG = "LocalChecks";
    /** Default text on local checks button */
    public static final String LOCAL_CHECKS_TEXT = "Local Checks";

    /**
     * Set the current till in the cargo. Create the till pickup transaction.
     * Set the local navigation buttons according to the Pickup Tenders
     * parameter. Show the selection screen.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TillPickupCargo cargo = (TillPickupCargo) bus.getCargo();
        TransactionUtilityManagerIfc tutility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Set current till (used by poscount service)
        TillIfc t = DomainGateway.getFactory().getTillInstance();
        t.setTillID(cargo.getRegister().getCurrentTillID());
        cargo.setTillID(cargo.getRegister().getCurrentTillID());
        t.addCashier(cargo.getOperator());
        cargo.setTill(t);

        // Create the Till Pickup Transaction
        TillAdjustmentTransactionIfc transaction =
          DomainGateway.getFactory().getTillAdjustmentTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_PICKUP_TILL);
        tutility.initializeTransaction(transaction, -1);
        cargo.setTransaction(transaction);

        // Set the local navigation buttons from the Pickup Tenders parameter
        NavigationButtonBeanModel navModel = this.getLocalNavigationModel(
                                                     (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE),
                                                     utility,
                                                     bus.getServiceName());
        POSBaseBeanModel model = new POSBaseBeanModel();
        model.setLocalButtonBeanModel(navModel);

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.SELECT_CURRENCY_TYPE, model);

    }

    /**
     * Sets the local navigation button bean model from the Pickup Tenders
     * parameter.
     * 
     * @param pm The parameter manager
     * @param utility The utility manager
     * @param serviceName The service name
     * @return The local navigation button bean model
     */
    protected NavigationButtonBeanModel getLocalNavigationModel(ParameterManagerIfc pm,
                                                                UtilityManagerIfc utility,
                                                                String serviceName)
    {
        // get list of currencies to pickup
        String pickupTenders[] = null;
        try
        {
            pickupTenders = pm.getStringValues(ParameterConstantsIfc.RECONCILIATION_PickupTenders);
        }
        catch (ParameterException pe)
        {
            logger.error(pe);
            pickupTenders = new String[4];
            pickupTenders[0] = ParameterConstantsIfc.RECONCILIATION_PickupTenders_CASH;
            pickupTenders[1] = ParameterConstantsIfc.RECONCILIATION_PickupTenders_ALTERNATE_CASH;
            pickupTenders[2] = ParameterConstantsIfc.RECONCILIATION_PickupTenders_CHECK;
            pickupTenders[3] = ParameterConstantsIfc.RECONCILIATION_PickupTenders_ALTERNATE_CHECK;
        }

        CurrencyTypeIfc[] type = DomainGateway.getAlternateCurrencyTypes();
        boolean configuredForForeignCurrency = ((type != null) && (type.length > 0));
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();

        for (int i = 0; i < pickupTenders.length; i++)
        {
            String keyName    = "F" + (i + 2);
            if (ParameterConstantsIfc.RECONCILIATION_PickupTenders_CASH.equals(pickupTenders[i]))
            {
                addCashButton(utility, navModel, keyName);
            }
            else if (ParameterConstantsIfc.RECONCILIATION_PickupTenders_CHECK.equals(pickupTenders[i]))
            {
                addCheckButton(utility, navModel, keyName);
            }
            else if (configuredForForeignCurrency
                     &&(ParameterConstantsIfc.RECONCILIATION_PickupTenders_ALTERNATE_CASH.equals(pickupTenders[i])))
            {
                addAlternateCashButton(utility, navModel, keyName, type, 0);
            }
            else if (configuredForForeignCurrency
                     && (ParameterConstantsIfc.RECONCILIATION_PickupTenders_ALTERNATE_CHECK.equals(pickupTenders[i])))
            {
                addAlternateCheckButton(utility, navModel, keyName, type, 0);
            }
        }

        return navModel;
    }

    //--------------------------------------------------------------------------
    /**
        Adds the "Local Cash" button to the local navigation button bean model.
        @param utility      The utility manager
        @param model        The lacal navigation button bean model
        @param functionKey  The function key name
    **/
    //--------------------------------------------------------------------------
    protected void addCashButton(UtilityManagerIfc utility, NavigationButtonBeanModel model, String functionKey)
    {
        String localCashLabel = utility.retrieveText("Common",
                                                      BundleConstantsIfc.TILL_BUNDLE_NAME,
                                                      CASH_TAG,
                                                      CASH_TAG);
        model.addButton(LOCAL, localCashLabel, true, null, functionKey, null);
        model.setButtonLabel(LOCAL, localCashLabel);
    }

    //--------------------------------------------------------------------------
    /**
        Adds the "Local Checks" button to the local navigation button bean model.
        @param utility      The utility manager
        @param model        The lacal navigation button bean model
        @param functionKey  The function key name
    **/
    //--------------------------------------------------------------------------
    protected void addCheckButton(UtilityManagerIfc utility, NavigationButtonBeanModel model, String functionKey)
    {
        String localCheckLabel = utility.retrieveText("Common",
                                                      BundleConstantsIfc.TILL_BUNDLE_NAME,
                                                      CHECKS_TAG,
                                                      CHECKS_TAG);
        model.addButton(CHECKS_TAG, localCheckLabel, true, null, functionKey, null);
        model.setButtonLabel(CHECKS_TAG, localCheckLabel);
    }

    //--------------------------------------------------------------------------
    /**
        Adds the "<Alternate> Cash" button to the local navigation button bean model.
        @param utility      The utility manager
        @param model        The lacal navigation button bean model
        @param functionKey  The function key name
        @param alternate    The alternate currencies list
        @param index        The index of the alternate currency
    **/
    //--------------------------------------------------------------------------
    protected void addAlternateCashButton(UtilityManagerIfc utility, NavigationButtonBeanModel model, String functionKey,
                                          CurrencyTypeIfc[] alternate, int index)
    {
        String cashText = utility.retrieveText("Common",
                                               BundleConstantsIfc.TILL_BUNDLE_NAME,
                                               CASH_TAG,
                                               CASH_TAG);
        String patternCash = utility.retrieveText("Common",
                                                  BundleConstantsIfc.TILL_BUNDLE_NAME,
                                                  CASH_MSG_TAG,
                                                  DEF_PATTERN_TEXT);
        String[] vars = new String[2];
        vars[0] = utility.retrieveCommonText(alternate[index].getCurrencyCode());
        vars[1] = cashText;
        String label = LocaleUtilities.formatComplexMessage(patternCash,vars);
        String alternateActionName = ALTERNATE + index;
        model.addButton(alternateActionName, label, true, null, functionKey, null);
        model.setButtonLabel(alternateActionName, label);
    }

    //--------------------------------------------------------------------------
    /**
        Adds the "<Alternate> Checks" button to the local navigation button bean model.
        @param utility      The utility manager
        @param model        The lacal navigation button bean model
        @param functionKey  The function key name
        @param alternate    The alternate currencies list
        @param index        The index of the alternate currency
    **/
    //--------------------------------------------------------------------------
    protected void addAlternateCheckButton(UtilityManagerIfc utility, NavigationButtonBeanModel model, String functionKey,
                                           CurrencyTypeIfc[] alternate, int index)
    {
        String checksText = utility.retrieveText("Common",
                                                 BundleConstantsIfc.TILL_BUNDLE_NAME,
                                                 CHECKS_TAG,
                                                 CHECKS_TAG);
        String patternChecks = utility.retrieveText("Common",
                                                    BundleConstantsIfc.TILL_BUNDLE_NAME,
                                                    CHECKS_MSG_TAG,
                                                    DEF_PATTERN_TEXT);
        String[] vars = new String[2];
        vars[0] = utility.retrieveCommonText(alternate[index].getCurrencyCode());
        vars[1] = checksText;
        String label = LocaleUtilities.formatComplexMessage(patternChecks,vars);
        String alternateActionName = ALTERNATE + index + CHECKS_TAG;
        model.addButton(alternateActionName, label, true, null, functionKey, null);
        model.setButtonLabel(alternateActionName, label);
    }
}
