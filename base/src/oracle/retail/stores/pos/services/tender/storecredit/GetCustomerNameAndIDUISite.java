/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/storecredit/GetCustomerNameAndIDUISite.java /main/16 2012/03/27 10:57:14 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to reason codes
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         3/29/2007 5:56:41 PM   Michael Boyd    CR
 *      26172 - v8x merge to trunk
 *
 *      4    .v8x      1.2.1.0     3/11/2007 5:33:50 PM   Brett J. Larsen
 *      CR
 *      4530 - default reason code value not being used
 *
 * 3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:48 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse
 *$
 *Revision 1.7  2004/07/14 18:47:09  epd
 *@scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *Revision 1.6  2004/06/17 16:26:17  blj
 *@scr 5678 - code cleanup
 *
 *Revision 1.5  2004/06/02 04:05:19  blj
 *@scr 4529 - resolution to customer id printing issues
 *$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.storecredit;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerNameAndIDBeanModel;

/**
 * This site gets the customer first name, last name and id type.
 * @deprecated 14.0; not longer used.
 */
public class GetCustomerNameAndIDUISite extends PosSiteActionAdapter {


//    --------------------------------------------------------------------------
    /**
        This is the arrive method which will display the screen.
        @param bus BusIfc
    **/
    //  --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc   utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        CustomerNameAndIDBeanModel model = new CustomerNameAndIDBeanModel();

        String storeID = Gateway.getProperty("application", "StoreID", "");
        CodeListIfc personalIDTypes =  utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_MAIL_BANK_CHECK_ID_TYPES);

        cargo.setPersonalIDTypes(personalIDTypes);
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        model.setIDTypes(personalIDTypes.getTextEntries(lcl));

        //model.setSelectedIDType(-1);
        Object selectedVal = cargo.getLocalizedPersonalIDCode().getText(lcl);

        if( selectedVal == null )
        {
            model.setSelectedIDType(model.getIDTypes().indexOf(personalIDTypes.getDefaultOrEmptyString(lcl)));
        }
        else
        {
            model.setSelectedIDType( model.getIDTypes().indexOf( selectedVal ) );
        }

        ui.showScreen(POSUIManagerIfc.GET_CUSTOMER_NAME_AND_ID, model);
    }
    /* Get the card number from the ui and put into the tenderAttributes
     * @param BusIfc bus
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void depart(BusIfc bus)
    {
        UtilityIfc util = null;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }

        // Get information from UI
        if (bus.getCurrentLetter().getName().equals("Next"))
        {
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            CustomerNameAndIDBeanModel model = (CustomerNameAndIDBeanModel) ui.getModel();
            cargo.getTenderAttributes().put(TenderConstants.FIRST_NAME,
                    model.getFirstName());
            cargo.getTenderAttributes().put(TenderConstants.LAST_NAME,
                    model.getLastName());

            String code = (String)cargo.getPersonalIDTypes().getKeyEntries().elementAt(model.getSelectedIDType());
            CodeEntryIfc codeEntry = cargo.getPersonalIDTypes().findListEntryByCode(code);

            LocalizedTextIfc localizedPersonalIDText = codeEntry.getLocalizedText();

            LocalizedCodeIfc localizedPersonalIDCode = DomainGateway.getFactory().getLocalizedCode();
            localizedPersonalIDCode.setCode(code);
            localizedPersonalIDCode.setText(localizedPersonalIDText);
            cargo.setLocalizedPersonalIDCode(localizedPersonalIDCode);

            cargo.getTenderAttributes().put(TenderConstants.ID_TYPE, codeEntry.getCodeName());
            cargo.getTenderAttributes().put(TenderConstants.LOCALIZED_ID_TYPE, localizedPersonalIDCode);

        }
    }

}
