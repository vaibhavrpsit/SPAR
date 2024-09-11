/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/ValidateRedeemActionSite.java /main/17 2012/08/27 11:22:40 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/20/12 - removed placeholder from key TenderRedeemed
 *    rabhawsa  08/16/12 - wptg - removed placeholder from key
 *    jswan     04/11/12 - Modified to support centralized validation of gift
 *                         certificates and store credits.
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    nkgautam  02/04/10 - added training mode attributes to fix gift card
 *                         redeem issue in training mode
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *
 *   Revision 1.13.2.1  2004/10/29 20:19:06  lzhao
 *   @scr 7338: add invalid gift certificate dialog.
 *
 *   Revision 1.13  2004/08/16 19:44:40  blj
 *   @scr 5314 - added new screen for invalid store credits.
 *
 *   Revision 1.12  2004/07/19 17:45:38  jriggins
 *   @scr 6026 Removed the Voided Gift Certificate dialog
 *
 *   Revision 1.11  2004/07/17 21:14:34  jriggins
 *   @scr 6026 Added logic for checking to see if the transaction for an issued gift certificate has been post voided
 *
 *   Revision 1.10  2004/05/20 19:48:52  crain
 *   @scr 5108 Tender Redeem_Redeem Foreign Gift Certificate Receipt Incorrect
 *
 *   Revision 1.9  2004/05/10 19:08:08  crain
 *   @scr 4182 Able to use a Issued Gift Cert over and over, Tender Redeemed never appears
 *
 *   Revision 1.8  2004/05/05 23:28:04  crain
 *   @scr 4182 Able to use a Issued Gift Cert over and over, Tender Redeemed never appears
 *
 *   Revision 1.7  2004/05/04 19:05:04  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.6  2004/05/03 21:11:01  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.5  2004/04/29 15:07:19  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.4  2004/04/22 22:35:55  blj
 *   @scr 3872 - more cleanup
 *
 *   Revision 1.3  2004/04/21 15:08:58  blj
 *   @scr 3872 - cleanup from code review
 *
 *   Revision 1.2  2004/04/15 20:56:18  blj
 *   @scr 3871 - updated to fix problems with void and offline.
 *
 *   Revision 1.1  2004/04/07 22:49:40  blj
 *   @scr 3872 - fixed problems with foreign currency, fixed ui labels, redesigned to do validation and adding tender to transaction in separate sites.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderGiftCertificateADO;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 *
 */
@SuppressWarnings("serial")
public class ValidateRedeemActionSite extends SiteActionAdapter
{
    //--------------------------------------------------------------------------
    /**

     This site displays the Redeem Number Site and
     collects this number from the ui in the depart method.
     @param bus the bus arriving at this site
     **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();

        TenderADOIfc redeemTender = null;

        Letter letter = new Letter(CommonLetterIfc.SUCCESS);

        boolean isTrainingMode = false;
        if(cargo.getRegister() != null)
        {
            isTrainingMode = cargo.getRegister().getWorkstation().isTrainingMode();
        }

        try
        {
            // Create the redeem tender.
            TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
            cargo.getTenderAttributes().put(TenderConstants.STATE, TenderCertificateIfc.REDEEMED);
            cargo.getTenderAttributes().put(TenderConstants.TRAINING_MODE, new Boolean(isTrainingMode));
            redeemTender = factory.createTender(cargo.getTenderAttributes());

            cargo.setTenderADO(redeemTender);
            // Validate and Add the redeem tender to the transaction.
            redeemTender.validate();
        }
        catch (ADOException adoe)
        {
            adoe.printStackTrace();
        }
        catch (TenderException te)
        {
            TenderErrorCodeEnum error = te.getErrorCode();

            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            if (error == TenderErrorCodeEnum.CERTIFICATE_TENDERED)
            {
                String args[] = new String[2];

                args[0] = (String)redeemTender.getTenderAttributes().get(TenderConstants.NUMBER);
                args[1] = (String)redeemTender.getTenderAttributes().get(TenderConstants.REDEEM_DATE);
                displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "TenderRedeemed", args, "Invalid");
                return;
            }
            else if (error == TenderErrorCodeEnum.INVALID_NUMBER)
            {
                Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
                String[] args = new String[] {utility.retrieveDialogText(cargo.getRedeemTypeSelected(), cargo.getRedeemTypeSelected())};
                args[0] = args[0].toLowerCase(locale);
                displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "InvalidNumberError", args, "Invalid");
                return;
            }
           else if (error == TenderErrorCodeEnum.INVALID_CERTIFICATE
                        || error == TenderErrorCodeEnum.CERTIFICATE_VOIDED)
            {
               if ( redeemTender instanceof TenderGiftCertificateADO )
               {
                   String args[] = new String[1];
                   displayDialog(bus, DialogScreensIfc.ERROR, "InvalidCertificateError", args, "Invalid");
               }
               else
               {
                   displayDialog(bus, DialogScreensIfc.ERROR, "InvalidStoreCreditError", null, "Invalid");
               }
               return;
            }
           else if (error == TenderErrorCodeEnum.INVALID_CURRENCY)
           {
               if (redeemTender instanceof TenderGiftCertificateADO)
               {
                   displayDialog(bus, DialogScreensIfc.ERROR, "InvalidGiftCertificateCurrency", null, "Invalid");
               }
               else
               {
                   displayDialog(bus, DialogScreensIfc.ERROR, "InvalidStoreCreditCurrency", null, "Invalid");
               }
               return;
           }
            else if (error == TenderErrorCodeEnum.VALIDATION_OFFLINE)
            {
                String redeemType = utility.retrieveDialogText(cargo.getRedeemTypeSelected(), cargo.getRedeemTypeSelected());
                String args[] = {redeemType, redeemType};
                displayDialog(bus, DialogScreensIfc.ERROR, "ValidationOffline", args, "Success");
                return;
            }

        }

        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     *
     * @param bus
     * @param screenType
     * @param message
     * @param args
     * @param letter
     */
    protected void displayDialog(BusIfc bus, int screenType, String message, String[] args, String letter)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        if (letter != null)
        {
            UIUtilities.setDialogModel(ui, screenType, message, args, letter);
        }
        else
        {
            UIUtilities.setDialogModel(ui, screenType, message, args);
        }
    }
}
