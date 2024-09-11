/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/validateid/GetIDTypeSite.java /main/14 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    abondala  11/03/08 - updated files related to customer id type reason
 *                         code.
 *    abondala  11/03/08 - updated files related to the Patriotic customer ID
 *                         types reason code
 *
 * ===========================================================================

     $Log:
      1    360Commerce 1.0         12/13/2005 4:47:06 PM  Barry A. Pape
     $

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.validateid;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel;

//--------------------------------------------------------------------------
/**
    This class displays the Enter id screen.
    $Revision: /main/14 $
**/
//--------------------------------------------------------------------------

public class GetIDTypeSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    //  --------------------------------------------------------------------------
    /**
        This is the arrive method which will display the screen.
        @param bus BusIfc
    **/
    //  --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ValidateIDCargoIfc cargo = (ValidateIDCargoIfc) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc   utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        CheckEntryBeanModel model = new CheckEntryBeanModel();

        String storeID = Gateway.getProperty("application", "StoreID", "");
        CodeListIfc personalIDTypes =  utility.getReasonCodes(storeID, cargo.getIDTypeCodeConstant());

        cargo.setPersonalIDTypes(personalIDTypes);
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        model.setIDTypes(personalIDTypes.getTextEntries(lcl));


        String selectedVal = cargo.getIdTypeName();

        if( selectedVal == null )
        {
            model.setSelectedIDType(0);
        }
        else
        {
            model.setSelectedIDType( model.getIDTypes().indexOf( selectedVal ) );
        }

        ui.showScreen(POSUIManagerIfc.ENTER_ID, model);
    }

    //  --------------------------------------------------------------------------
    /**
        This is the depart method which will capture the user input.
        @param bus BusIfc
    **/
    //  --------------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();

        if (letter.getName().equals("Next"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            CheckEntryBeanModel model = (CheckEntryBeanModel)
                                                ui.getModel(POSUIManagerIfc.ENTER_ID);
            ValidateIDCargoIfc cargo = (ValidateIDCargoIfc)bus.getCargo();

            String code = (String)cargo.getPersonalIDTypes().getKeyEntries().elementAt(model.getSelectedIDType());
            CodeEntryIfc codeEntry = cargo.getPersonalIDTypes().findListEntryByCode(code);

            LocalizedTextIfc localizedPersonalIDText = codeEntry.getLocalizedText();

            LocalizedCodeIfc localizedPersonalIDCode = DomainGateway.getFactory().getLocalizedCode();
            localizedPersonalIDCode.setCode(code);
            localizedPersonalIDCode.setText(localizedPersonalIDText);

            cargo.setLocalizedPersonalIDCode(localizedPersonalIDCode);

            cargo.setIdTypeName(codeEntry.getCodeName());
        }
    }
}
