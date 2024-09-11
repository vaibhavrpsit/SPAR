/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/coupon/GetCouponAmountSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       01/13/09 - specify decimal format (non locale sensitive) in
 *                         tender attraibutes
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse
 *
 *   Revision 1.1  2004/04/02 20:17:27  epd
 *   @scr 4263 Refactored coupon tender into sub service
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.1   Nov 20 2003 16:57:32   epd
 * updated to use new ADO Factory Complex
 *
 *    Rev 1.0   Nov 04 2003 11:17:44   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 30 2003 13:01:06   crain
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.coupon;

import java.util.HashMap;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCouponADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This class displays the screen to get the coupon amount and then reads it in.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetCouponAmountSite extends PosSiteActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = -3676494268867633660L;
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Arrive method displays screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        TenderCouponADO couponTender = null;
        // Get tender attributes
        HashMap tenderAttributes = cargo.getTenderAttributes();
        // Add tender type
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.COUPON);

        try
        {
            // create a new coupon tender
            TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
            couponTender = (TenderCouponADO)factory.createTender(tenderAttributes);
        }
        catch (ADOException adoe)
        {
            adoe.printStackTrace();
        }
        catch (TenderException e)
        {
            assert(false) : "This should never happen, because UI enforces proper format";
        }

        // set the tenderADO in cargo
        cargo.setTenderADO(couponTender);

        try
        {
            couponTender.calculateCouponAmount();
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        catch (TenderException e)
        {
            if (e.getErrorCode() == TenderErrorCodeEnum.MANUAL_INPUT)
            {
                ui.showScreen(POSUIManagerIfc.COUPON_AMOUNT, new POSBaseBeanModel());
            }
            else
            {
                displayErrorDialog(ui, "CouponNumberNotValid");
            }
        }
    }

    //----------------------------------------------------------------------
    /**
        Depart method retrieves input.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        LetterIfc letter = (LetterIfc) bus.getCurrentLetter();

        // If the user entered a coupon amount
        if (letter.getName().equals("Next"))
        {
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            // Get the manually entered coupon amount and set it in the coupon
            TenderCouponADO coupon = (TenderCouponADO)cargo.getTenderADO();
            HashMap tenderAttributes = coupon.getTenderAttributes();
            String amount = LocaleUtilities.parseCurrency(ui.getInput().trim(), LocaleMap.getLocale(LocaleMap.DEFAULT)).toString();
            tenderAttributes.put(TenderConstants.AMOUNT, amount);
            try
            {
                coupon.setTenderAttributes(tenderAttributes);
            }
            catch (TenderException e)
            {
                logger.error( "GetCouponAmountSite.depart(): Invalid amount " + Util.throwableToString(e) + "");
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
       Display the specified error dialog
       @param POSUIManagerIfc UI Manager to handle the IO
       @param String name of the error dialog to display
       @param String[] args for the error dialog
    **/
    //--------------------------------------------------------------------------
    protected void displayErrorDialog(POSUIManagerIfc ui, String name)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setArgs(null);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
