/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/CheckForPersonalIDRequiredSite.java /main/24 2013/07/22 12:33:39 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  10/17/14 - Fixing wrongly used reason code type 
 *                         for capturing customer ID type.
 *    cgreene   07/22/13 - prevent possible npe with customerInfo
 *    abondala  01/14/13 - fix the issue of return transaction linked with a
 *                         business custome
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/12/11 - revert aba number encryption, which is not sensitive
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/19/11 - encryption CR
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   06/18/09 - added javadoc about using check id types
 *    nkgautam  04/01/09 - Fix for populating ID number and State in personal
 *                         id entry screen when original transaction contains
 *                         this information
 *    masahu    02/27/09 - For a Customer linked to the transaction changed
 *                         CheckForPersonalIDRequiredSite to get the customer
 *                         info from transaction
 *    mahising  02/27/09 - Fixed personal id issue for business customer
 *    mahising  02/17/09 - fixed personal id business customer issue
 *    mdecama   12/16/08 - Rename parameters FormOfIDRequiredForRetrievedReturn
 *                         to FormOfIDForRetrievedReturn and
 *                         FormOfIDRequiredForNonRetrievedReturn to
 *                         FormOfIDForNonretrievedReturn
 *    mdecama   12/03/08 - Using new Parameters -
 *                         FormOfIdRequiredForRetrievedReturns and
 *                         FormOfIdRequiredForNonRetrievedReturns
 *    mdecama   10/28/08 - I18N - Reason Codes for Customer Types.
 * ===========================================================================

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse
     $
     Revision 1.6  2004/09/15 16:34:22  kmcbride
     @scr 5881: Deprecating parameter retrieval logic in cargo classes and logging parameter exceptions

     Revision 1.5  2004/03/10 14:16:46  baa
     @scr 0 fix javadoc warnings

     Revision 1.4  2004/03/03 23:15:11  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:51:52  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:25  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
     updating to pvcs 360store-current
 *
 *    Rev 1.0   Aug 29 2003 16:06:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Jul 03 2003 10:23:38   jgs
 * Added code to support Driver's licence validation.
 * Resolution for 1874: Add Driver's License Validation to Return Prompt for ID
 *
 *    Rev 1.5   Apr 16 2003 11:32:36   pdd
 * removed reference to DomainUtilities
 * Resolution for 2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.4   Apr 09 2003 14:44:12   HDyer
 * Cleanup from code review.
 * Resolution for POS SCR-1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.3   Mar 24 2003 10:08:16   baa
 * remove reference to foundation.util.EMPTY_STRING
 * Resolution for POS SCR-2101: Remove uses of  foundation constant  EMPTY_STRING
 *
 *    Rev 1.2   Feb 21 2003 09:35:34   baa
 * Changes for contries.properties refactoring
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Dec 16 2002 14:35:48   HDyer
 * Set default state correctly.
 * Resolution for POS-SCR 1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.0   Dec 16 2002 09:47:12   HDyer
 * Initial revision.
 * Resolution for POS-SCR 1854: Return Prompt for ID feature for POS 6.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import java.util.Locale;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.tender.TenderCheck;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.PersonalIDEntryBeanModel;

/**
 * This site checks the returns parameters to see if ID is required for
 * processing the return. A screen is displayed to collect the ID information if
 * personal ID is required.
 * 
 * @version $Revision: /main/24 $
 */
public class CheckForPersonalIDRequiredSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8288958606272416864L;

    /**
     * Check the return parameters to see if ID is required for processing the
     * return. If personal ID is required, then display the enter ID info
     * screen. Otherwise just continue on.
     * <p>
     * The ID Types populated into the model are from the server of type
     * {@link CodeConstantsIfc#CODE_LIST_CHECK_ID_TYPES}.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get needed objects from bus
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
        boolean bIdTypeAdded = false;
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        CodeListIfc idTypes = null;
        try
        {
            // Get the ID required parameter to see if personal ID is required
            String parameterValue;
            if (cargo.areAllItemsFromTransaction())
            {
                parameterValue = pm.getStringValue(ParameterConstantsIfc.RETURN_FormOfIDForRetrievedReturn);
            }
            else
            {
                parameterValue = pm.getStringValue(ParameterConstantsIfc.RETURN_FormOfIDForNonretrievedReturn);
            }
            // Parameter may have more than one value containing 'PersonalID',
            // so look for that string to be contained rather than testing for equal
            if (!cargo.isCustomerInfoCollected() && (parameterValue.indexOf(ParameterConstantsIfc.RETURN_FormOfID_PERSONAL_ID) != -1))
            {
                // Create model and set up combo box vectors
                PersonalIDEntryBeanModel model = new PersonalIDEntryBeanModel();

                // get default state
                String state = pm.getStringValue(ParameterConstantsIfc.BASE_StoreStateProvince);
                String shortState = state.substring(3, state.length());
                String country = pm.getStringValue(ParameterConstantsIfc.BASE_StoreCountry);
                EncipheredDataIfc personalIDNumber = null;
                String firstName = "";
                String lastName = "";
                String companyName = null;
                CustomerInfoIfc customerInfo = cargo.getCustomerInfo();
                if (customerInfo != null)
                {
                    if (customerInfo.getPersonalID().getEncryptedNumber() != null && customerInfo.getPersonalID().getMaskedNumber().length() > 0)
                    {
                        country = customerInfo.getPersonalIDCountry();
                        personalIDNumber = customerInfo.getPersonalID();
                        shortState = customerInfo.getPersonalIDState();
                    }

                    if (!Util.isEmpty(customerInfo.getFirstName()) || !Util.isEmpty(customerInfo.getLastName())
                            || !Util.isEmpty(customerInfo.getCompanyName()))
                    {
                        firstName = customerInfo.getFirstName();
                        lastName = customerInfo.getLastName();
                        companyName = customerInfo.getCompanyName();
                    }
                    else
                    {
                        if (cargo.getCustomer() != null)
                        {
                            if (cargo.getCustomer().isBusinessCustomer())
                            {
                                model.setBusinessCustomer(true);
                                companyName = cargo.getCustomer().getLastName();
                            }
                            else
                            {
                                firstName = cargo.getCustomer().getFirstName();
                                lastName = cargo.getCustomer().getLastName();
                            }

                        }
                        else
                        {
                            if (cargo.getTransaction() != null && cargo.getTransaction().getCustomer() != null)
                            {
                                if (cargo.getTransaction().getCustomer().isBusinessCustomer())
                                {
                                    model.setBusinessCustomer(true);
                                    companyName = cargo.getTransaction().getCustomer().getLastName();

                                }
                                else
                                {
                                    firstName = cargo.getTransaction().getCustomer().getFirstName();
                                    lastName = cargo.getTransaction().getCustomer().getLastName();
                                }
                            }
                        }
                    }
                }

                TenderLineItemIfc tenderItems[] = cargo.getOriginalTenderLineItemsArray();
                for (int i = 0; i < tenderItems.length; i++)
                {
                    if (tenderItems[i] != null && tenderItems[i] instanceof TenderCheck)
                    {
                        TenderCheck check = (TenderCheck)tenderItems[i];
                        personalIDNumber = check.getPersonalID();
                        String selectedIDType = (check.getPersonalIDType().getCodeName() != null)?
                                check.getPersonalIDType().getCode() : "";
                        idTypes = utility.getReasonCodes(cargo.getOperator().getStoreID(),
                                CodeConstantsIfc.CODE_LIST_CHECK_ID_TYPES);
                        model.inject(idTypes, selectedIDType, lcl);
                        bIdTypeAdded = true;
                    }
                }

                // get list of all available states
                CountryIfc[] countries = utility.getCountriesAndStates(pm);
                // get the index in the array of countries that matches the
                // selected country
                int countryIndex = utility.getCountryIndex(country, pm);
                int stateIndx = utility.getStateIndex(countryIndex, shortState, pm);
                model.setCountries(countries);

                // Set the ID types
                if (!bIdTypeAdded)
                {
                    String selectedIDType = (customerInfo != null && customerInfo.getLocalizedPersonalIDType() != null)?
                            customerInfo.getLocalizedPersonalIDType().getCode() : "";
                    idTypes = utility.getReasonCodes(cargo.getOperator().getStoreID(),
                            CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_TYPES);
                    model.inject(idTypes, selectedIDType, lcl);
                }

                // Set a default state
                model.setStateIndex(stateIndx);
                model.setCountryIndex(countryIndex);
                model.setPersonalID(personalIDNumber);
                // Sets Customer Name
                if (companyName != null && !(companyName.trim().equals("")))
                {
                    model.setBusinessCustomer(true);
                    model.setOrgName(companyName);
                }
                else
                {
                    model.setFirstName(firstName);
                    model.setLastName(lastName);
                }

                cargo.setIdTypes(idTypes);
                // Show personal ID entry screen
                ui.showScreen(POSUIManagerIfc.PERSONALID_ENTRY, model);
            }
            else
            {
                // No ID required so just move on to next site
                bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }
        }
        catch (ParameterException pe)
        {
            logger.error(pe);
            bus.mail(new Letter(CommonLetterIfc.FAILURE), BusIfc.CURRENT);
        }
    }
}
