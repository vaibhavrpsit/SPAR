/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/CaptureCustomerInfoTDO.java /main/21 2013/09/17 15:25:59 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  09/17/13 - Added a check for undefined id type to avoid NPE
 *    yiqzhao   01/10/13 - Add business name for store credit and store credit
 *                         tender line tables.
 *    mjwallac  09/19/12 - remove unneeded import of PhoneTest class
 *    cgreene   04/03/12 - removed deprecated methods
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mdecama   12/03/08 - Setting the personalID using the locale
 *    abondala  11/07/08 - fixing the POS crash if the personal id type is not
 *                         selected
 *    mdecama   10/27/08 - I18N - Refactoring Reason Codes for
 *                         CaptureCustomerIDTypes

     $Log:
      6    .v8x      1.4.1.0     3/11/2007 3:53:54 PM   Brett J. Larsen CR 4530
            - default reason code not being displayed in list

           added support for default reason code (IDType for this screen)
      5    360Commerce1.4         12/8/2006 2:04:54 PM   Charles D. Baker CR
           22872 - Updated to save country name in country field - rather than
            country code.
           Hre's the info from luis and judy: the country name (Japan) should
           be in the table
      4    360Commerce1.3         7/25/2006 7:53:03 PM   Charles D. Baker
           Updated to handle DB update breaking single address line column
           into two. Corrected other special handling as mail bank check
           shares Customer object with Capture Cutomer.
      3    360Commerce1.2         3/31/2005 4:27:20 PM   Robert Pearse
      2    360Commerce1.1         3/10/2005 10:19:59 AM  Robert Pearse
      1    360Commerce1.0         2/11/2005 12:09:48 PM  Robert Pearse
     $
     Revision 1.9  2004/07/14 18:47:08  epd
     @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation

     Revision 1.8  2004/07/02 17:04:42  khassen
     @scr 5642 - fixing country indexing.

     Revision 1.7  2004/06/18 12:12:26  khassen
     @scr 5684 - Feature enhancements for capture customer use case.

     Revision 1.6  2004/03/11 16:28:36  khassen
     @scr 0 Capture Customer Info use-case - fixed array OOB exception

     Revision 1.5  2004/02/27 22:04:33  khassen
     @scr 0 Capture Customer Info use-case - Fixed phone stuff.

     Revision 1.4  2004/02/27 21:09:01  khassen
     @scr 0 Capture Customer Info use-case - code clean-up and post-review modifications

     Revision 1.3  2004/02/27 19:20:52  epd
     @scr 0 removed unused import

     Revision 1.2  2004/02/27 19:19:18  khassen
     @scr 0 Capture Customer Info use-case


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * @author kph
 *
 * This class implements TDO functionality for the capture customer
 * info use case.
 */
public class CaptureCustomerInfoTDO extends TDOAdapter implements TDOUIIfc
{
    protected static final Logger logger = Logger.getLogger(CaptureCustomerInfoTDO.class);

    public static final String BUS = "Bus";


    /**
     * buildBeanModel constructs a basic CaptureCustomerInfoBeanModel
     * for use in the capture customer info use-case.
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        BusIfc bus = (BusIfc) attributeMap.get(BUS);
        CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        CaptureCustomerInfoBeanModel model = new CaptureCustomerInfoBeanModel();
        String storeState   = CustomerUtilities.getStoreState(pm);
        String storeCountry = CustomerUtilities.getStoreCountry(pm);

        int countryIndx = utility.getCountryIndex(storeCountry, pm);
        model.setCountryIndex(countryIndx);
        model.setStateIndex(utility.getStateIndex(countryIndx,
                                                  storeState.substring(3,storeState.length()),
                                                  pm));
        model.setCountries(utility.getCountriesAndStates(pm));

        CodeListIfc personalIDTypes = cargo.getPersonalIDTypes();
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        model.setIDTypes(personalIDTypes.getTextEntries(lcl));
        model.setDefaultIDType(personalIDTypes.getDefaultOrEmptyString(lcl));
        model.setIdTypesKeys(personalIDTypes.getKeyEntries());

        return model;
    }
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine1(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        // Not used in this implementation.
        return null;
    }
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine2(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        // Not used in this implementation.
        return null;
    }

    /**
     * modelToCustomer copies the customer information over to
     * the model, based on specific needs for the capture
     * customer use-case.
     * @param model
     * @param customer
     */
    public void customerToModel(CaptureCustomerInfoBeanModel model, BusIfc bus, CaptureCustomerIfc customer)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        if (customer != null)
        {
            int countryIndex = 0;
            int stateIndex = 0;
            countryIndex = utility.getCountryIndex(customer.getCountry(), pm);
            stateIndex   = utility.getStateIndex(countryIndex, customer.getState(), pm);
            model.setFirstName(customer.getFirstName());
            model.setLastName(customer.getLastName());
            model.setPhoneNumber(customer.getPhoneNumber(), PhoneConstantsIfc.PHONE_TYPE_HOME);
            model.setPhoneType(customer.getPhoneType());
            model.setAddressLine1(customer.getAddressLine(0));
            model.setAddressLine2(customer.getAddressLine(1));
            model.setCountryIndex(countryIndex);
            model.setStateIndex(stateIndex);
            model.setCity(customer.getCity());
            model.setPostalCode(customer.getPostalCode());
            if (customer.getPersonalIDType() != null
                    && !LocalizedCodeIfc.CODE_UNDEFINED.equals(customer.getPersonalIDType().getCode()))
            {
                Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
                model.setIDType(customer.getPersonalIDType().getText(lcl));
            }
        }
    }

    /**
     * modelToCustomer copies the model information over to
     * the customer, based on specific needs for the capture
     * customer use-case.
     * NOTE: **The CaptureCustomer object must NOT be null.**
     * @param model
     * @param customer
     */
    public void modelToCustomer(CaptureCustomerInfoBeanModel model, BusIfc bus, CaptureCustomerIfc customer)
    {
        CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo) bus.getCargo();

        if (customer != null)
        {
            customer.setCity(model.getCity());
            customer.setState(model.getState());
            //Country name should be saved in lo_ads table - not country code
            customer.setCountry(model.getCountry());
            customer.setPostalCode(model.getPostalCode());
            customer.setFirstName(model.getFirstName());
            customer.setLastName(model.getLastName());
            customer.setCustomerName(model.getOrgName());
            customer.setPhoneType(model.getPhoneType());
            customer.setPhoneNumber(model.getPhoneNumber());

            String idType = model.getSelectedKey();
            CodeEntryIfc idTypeEntry = cargo.getPersonalIDTypes().findListEntryByCode(idType);
            LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
            if (idTypeEntry != null)
            {
	            localizedCode.setCode(idTypeEntry.getCode());
	            localizedCode.setText(idTypeEntry.getLocalizedText());
            }
            customer.setPersonalIDType(localizedCode);
            customer.setAddressLine(1, model.getAddressLine1());
            customer.setAddressLine(2, model.getAddressLine2());
        }
    }
}
