/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/GetCheckIDTypeSite.java /main/17 2011/12/05 12:16:22 cgreene Exp $
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
 *    mkochumm  01/30/09 - start clean for type page
 *    ranojha   11/18/08 - Fixed GetCheckIDTypeSite for NullPointerException.
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to reason codes
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/29/2007 5:49:49 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *
 *         4    .v8x      1.2.1.0     3/11/2007 4:54:31 PM   Brett J. Larsen
 *         CR 4530
 *         - default reason code values not being displayed - adding support
 *         for this
 *         
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/14 18:47:09  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.1  2004/04/13 21:07:36  bwf
 *   @scr 4263 Decomposition of check.
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   29 Jan 2004 15:33:12   Tim Fritz
 * Changed the model.setSelectedIDType
 *
 *    Rev 1.1   Nov 13 2003 15:50:00   bwf
 * Point to -1 selected to see blank.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.0   Nov 07 2003 16:11:48   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
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
    $Revision: /main/17 $
**/
//--------------------------------------------------------------------------

public class GetCheckIDTypeSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/17 $";

    //  --------------------------------------------------------------------------
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
        CheckEntryBeanModel model = new CheckEntryBeanModel();
        
        cargo.getTenderAttributes().remove(TenderConstants.ID_TYPE);
        cargo.getTenderAttributes().remove(TenderConstants.ID_NUMBER);
        cargo.getTenderAttributes().remove(TenderConstants.PHONE_NUMBER);
        cargo.getTenderAttributes().remove(TenderConstants.ID_COUNTRY);
        cargo.getTenderAttributes().remove(TenderConstants.ID_COUNTRY_INDEX);
        cargo.getTenderAttributes().remove(TenderConstants.ID_STATE);
        cargo.getTenderAttributes().put(TenderConstants.ID_COUNTRY_INDEX, -1);
        		
        String storeID = Gateway.getProperty("application", "StoreID", "");
        CodeListIfc personalIDTypes =  utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_CHECK_ID_TYPES);

        cargo.setPersonalIDTypes(personalIDTypes);

        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        if (personalIDTypes != null)
        {
        	model.setIDTypes(personalIDTypes.getTextEntries(lcl));
        }

        String selectedVal = cargo.getLocalizedPersonalIDCode().getText(lcl);

        if(personalIDTypes!=null && (selectedVal == null || selectedVal.trim().equals("")))
        {

            model.setDefaultIDType(model.getIDTypes().indexOf(personalIDTypes.getDefaultOrEmptyString(lcl)));
            model.setSelectedIDType(CodeConstantsIfc.CODE_INTEGER_UNDEFINED);
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
        LetterIfc letter = (LetterIfc) bus.getCurrentLetter();

        if (letter.getName().equals("Next"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            CheckEntryBeanModel model = (CheckEntryBeanModel)
                                                ui.getModel(POSUIManagerIfc.ENTER_ID);
            TenderCargo cargo = (TenderCargo)bus.getCargo();

            String code = (String)cargo.getPersonalIDTypes().getKeyEntries().elementAt(model.getSelectedIDType());
            CodeEntryIfc codeEntry = cargo.getPersonalIDTypes().findListEntryByCode(code);

            LocalizedTextIfc localizedPersonalIDText = codeEntry.getLocalizedText();

            LocalizedCodeIfc localizedPersonalIDCode = DomainGateway.getFactory().getLocalizedCode();
            localizedPersonalIDCode.setCode(code);
            localizedPersonalIDCode.setText(localizedPersonalIDText);
            localizedPersonalIDCode.setCodeName(codeEntry.getCodeName());
            cargo.setLocalizedPersonalIDCode(localizedPersonalIDCode);

            cargo.getTenderAttributes().put(TenderConstants.ID_TYPE, codeEntry.getCodeName());
            cargo.getTenderAttributes().put(TenderConstants.LOCALIZED_ID_TYPE, localizedPersonalIDCode);
        }

    }
}
