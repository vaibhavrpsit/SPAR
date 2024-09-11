/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcertificate/GetIssuingStoreNumberSite.java /main/14 2012/03/27 10:57:14 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  02/09/09 - Depart: Setting the current store id to tender
 *                         attributes in case of Continue.
 *    simpatel  11/13/08 - Changing
 *                         Utility.getParameterValue(CORPORATESTOREID,cargo.getOperator().getStoreID())
 *    simpatel  11/13/08 - Forward Port Bug 6835430 System is looking for the
 *                         storeid even if you select corporate gift
 *                         certificate
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse
 *
 *   Revision 1.4  2004/07/14 18:47:09  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.3  2004/05/07 22:01:17  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.2  2004/04/26 19:28:40  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.1  2004/04/20 23:04:20  bwf
 *   @scr 4263 Decomposition of gift certificate.
 *
 *   Revision 1.4  2004/02/17 19:26:17  epd
 *   @scr 0
 *   Code cleanup. Returned unused local variables.
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
 *    Rev 1.1   Dec 02 2003 17:51:10   crain
 * Modified flow
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.0   Nov 20 2003 16:39:26   crain
 * Initial revision.
 * Resolution for 3421: Tender redesign
 *
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.giftcertificate;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.CertificateTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This class displays the screen to get the gift certificate number and then
 * reads it in. 
 * 
 * @version $Revision: /main/14 $
 * @deprecated un 14.0; no longer used.
 */
public class GetIssuingStoreNumberSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8637185813332062695L;
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
        UtilityIfc utility;
        try
        {
            utility = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        String corporateGiftCertificatesAccepted = utility.getParameterValue("CorporateGiftCertificatesAccepted", "N");

        if (corporateGiftCertificatesAccepted.equalsIgnoreCase("Y"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.ISSUING_STORE_NUMBER, new POSBaseBeanModel());
        }
        else
        {
            // mail a letter
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
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

            cargo.getTenderAttributes().put(TenderConstants.STORE_NUMBER, ui.getInput().trim());
            cargo.getTenderAttributes().put(TenderConstants.CERTIFICATE_TYPE, CertificateTypeEnum.STORE);
        }
        else if (letter.getName().equals("Corporate"))
        {
            cargo.getTenderAttributes().put(TenderConstants.CERTIFICATE_TYPE, CertificateTypeEnum.CORPORATE);
            UtilityIfc utility;
            try
            {
                utility = Utility.createInstance();
            }
            catch (ADOException e)
            {
                String message = "Configuration problem: could not instantiate UtilityIfc instance";
                logger.error(message, e);
                throw new RuntimeException(message, e);
            }
            String corporateStoreID = utility.getParameterValue("CorporateStoreID", cargo.getOperator().getStoreID());
            cargo.getTenderAttributes().put(TenderConstants.STORE_NUMBER, corporateStoreID);
        }
        else if (letter.getName().equals(CommonLetterIfc.CONTINUE))
        {
            // Set the current store id
            String storeID = cargo.getRegister().getWorkstation().getStoreID();
            cargo.getTenderAttributes().put(TenderConstants.STORE_NUMBER, storeID);
        }
    }
}
