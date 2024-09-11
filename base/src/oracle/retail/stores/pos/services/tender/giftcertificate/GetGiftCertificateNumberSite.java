/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcertificate/GetGiftCertificateNumberSite.java /main/13 2011/12/05 12:16:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.3  2004/07/17 18:53:29  crain
 *   @scr 4934 Gift Certificate_Entry Method_Data Input/Output
 *
 *   Revision 1.2  2004/05/18 22:02:35  crain
 *   @scr 4936 Gift Cert_AMT_LESS_THAN_MIN_ message prompt text incorrect
 *
 *   Revision 1.1  2004/04/20 23:04:20  bwf
 *   @scr 4263 Decomposition of gift certificate.
 *
 *   Revision 1.4  2004/03/05 19:51:00  crain
 *   @scr 3421 Tender redesign
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
 *    Rev 1.4   Dec 09 2003 17:17:36   crain
 * Added foreign certificate
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.3   Dec 02 2003 17:48:56   crain
 * Modified flow
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.2   Nov 25 2003 10:42:40   cdb
 * Modified to use TenderLineItemIfc for entry type - Auto implies Scanned.
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.1   Nov 24 2003 17:43:18   cdb
 * Updated to track entry method for gift certificate tendering.
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.0   Nov 20 2003 16:39:24   crain
 * Initial revision.
 * Resolution for 3421: Tender redesign
 * 
 * Initial revision.
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
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This class displays the screen to get the gift certificate number and then
 * reads it in.
 * 
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class GetGiftCertificateNumberSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/13 $";

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
        
        // Set the argText in the PromptArea
        promptModel.setArguments(arg);
        beanModel.setPromptAndResponseModel(promptModel);
        ui.showScreen(POSUIManagerIfc.CERTIFICATE_ENTRY, beanModel);        
    }

    /**
     * Depart method retrieves input.
     * 
     * @param bus Service Bus
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
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Scan);
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
