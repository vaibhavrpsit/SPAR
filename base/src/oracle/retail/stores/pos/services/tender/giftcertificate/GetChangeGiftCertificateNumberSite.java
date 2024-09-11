/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcertificate/GetChangeGiftCertificateNumberSite.java /main/14 2011/12/05 12:16:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.1  2004/04/22 20:52:17  epd
 *   @scr 4513 FIxes to tender, especially gift card, gift cert, and store credit
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.giftcertificate;

import java.util.Locale;

import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ado.tender.CertificateTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This class prompts for gift cert number. It also assigns the amount since we
 * already know it.
 * 
 * @version $Revision: /main/14 $
 */
public class GetChangeGiftCertificateNumberSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 2520675657782091412L;
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/14 $";
    
    /**
     * Arrive method displays screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        PromptAndResponseModel promptModel = new PromptAndResponseModel();

        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        String arg = utility.retrieveCommonText("Gift", "Gift");
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        arg = arg.toLowerCase(locale);
        
        // turn off global buttons
        NavigationButtonBeanModel globalBean = new NavigationButtonBeanModel();
        globalBean.setButtonEnabled(CommonActionsIfc.CANCEL, false);
        beanModel.setGlobalButtonBeanModel(globalBean);
        
        // Set the argText in the PromptArea
        promptModel.setArguments(arg);
        beanModel.setPromptAndResponseModel(promptModel);
        ui.showScreen(POSUIManagerIfc.CERTIFICATE_ENTRY, beanModel);        
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // If the user entered a gift certificate number
        if (letter.getName().equals(CommonLetterIfc.NEXT))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            boolean isScanned = ((POSBaseBeanModel)ui.getModel()).getPromptAndResponseModel().isScanned();
            
            // Get the gift certificate number and put in the cargo
            cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput().trim());
            if (isScanned)
            {
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Automatic);
            }
            else
            {
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
            }
        }
        else if (letter.getName().equals("Foreign"))
        {
            cargo.getTenderAttributes().put(TenderConstants.CERTIFICATE_TYPE, CertificateTypeEnum.FOREIGN);
        }
    }
}
