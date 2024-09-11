/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/EnterDocumentFaceValueSite.java /main/1 2012/03/27 10:57:16 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This class displays the screen to enter the face value of a gift 
 * certificate or store credit after it has been determined that the
 * validation is off line.
 * 
 * @version $Revision: /main/1 $
 */
@SuppressWarnings("serial")
public class EnterDocumentFaceValueSite extends PosSiteActionAdapter
{
    /**
     * Arrive method displays screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        TenderADOIfc tender = cargo.getTenderADO();
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        String arg = null;
        if (tender.getTenderType().equals(TenderTypeEnum.STORE_CREDIT))
        {
            arg = utility.retrieveText("Common", "tenderText", "StoreCredit", ""); 
        }
        else
        {
            arg = utility.retrieveText("Common", "tenderText", "GiftCertificate", ""); 
        }
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        PromptAndResponseModel promptModel = new PromptAndResponseModel();

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        arg = arg.toLowerCase(locale);
        
        // Set the argText in the PromptArea
        promptModel.setArguments(arg);
        beanModel.setPromptAndResponseModel(promptModel);
        ui.showScreen(POSUIManagerIfc.ENTER_FACE_VALUE, beanModel);        
    }
}
