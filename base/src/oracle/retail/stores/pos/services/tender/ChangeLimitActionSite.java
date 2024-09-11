/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/ChangeLimitActionSite.java /main/14 2011/12/05 12:16:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       02/03/11 - check in all
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         5/15/2008 6:09:53 PM   Anil Rathore    port
 *         the changes from 7.x with modification. Changes reviewed by Dan.
 *    5    360Commerce 1.4         3/31/2008 1:56:54 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    4    360Commerce 1.3         4/7/2006 5:04:07 AM    Akhilashwar K. Gupta
 *         CR-10596: Updated catch block of arrive() method.
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:52 PM  Robert Pearse
 *
 *   Revision 1.3  2004/09/22 18:24:38  bwf
 *   @scr 3511 Create cash change tender and make sure refund options
 *   	       button calculations work correctly.
 *
 *   Revision 1.2  2004/07/09 20:55:26  bvanschyndel
 *   @scr 3068 ClassCastException is thrown when change is issued for a House Account payment
 *
 *   Revision 1.1  2004/06/19 17:33:33  bwf
 *   @scr 5205 These are the overhaul changes to the Change Due Options
 *                     screen and max change calculations.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.ReturnableTransactionADOIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
     This class checks the change limits and pops up error messages.
     $Revision: /main/14 $
 **/
//--------------------------------------------------------------------------
public class ChangeLimitActionSite extends PosSiteActionAdapter
{

    //----------------------------------------------------------------------
    /**
        This method checks the change limits and displays error messages.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // add tender type to attributes
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        String changeTenderLetter = null;
        try
        {
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();

            boolean isReturnWithReceipt = false;
            boolean isReturnWithOriginalRetrieved = false;
            try
            {
                isReturnWithReceipt = ((ReturnableTransactionADOIfc)txnADO).isReturnWithReceipt();
                isReturnWithOriginalRetrieved = ((ReturnableTransactionADOIfc)txnADO).isReturnWithOriginalRetrieved();
            }
            catch (ClassCastException e)
            {
                isReturnWithReceipt = false;
                isReturnWithOriginalRetrieved = false;
            }
            txnADO.validateChangeLimits(cargo.getTenderAttributes(), isReturnWithReceipt, isReturnWithOriginalRetrieved);
            // save and reset refund tender letter
            changeTenderLetter = cargo.getRefundTenderLetter();
            cargo.setRefundTenderLetter(null);
        }
        catch (TenderException te)
        {
            TenderErrorCodeEnum errorCode = te.getErrorCode();
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            if (TenderTypeEnum.GIFT_CERT.toString().equals(cargo.getTenderAttributes().get(TenderConstants.TENDER_TYPE).toString()))
            {
                UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                if (errorCode == TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED)
                {
                    ui.showScreen(
                            POSUIManagerIfc.DIALOG_TEMPLATE, getDialogModel(utility, "MoreThanMaximumAmount"));
                    return;
                }
                else if (errorCode == TenderErrorCodeEnum.MIN_CHANGE_LIMIT_VIOLATED)
                {
                    ui.showScreen(
                            POSUIManagerIfc.DIALOG_TEMPLATE, getDialogModel(utility, "LessThanMinimumAmount"));
                    return;
                }
            }
            else
           {
            	cargo.setRefundTenderLetter(bus.getCurrentLetter().getName());
                if (errorCode == TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED)
                {
                    int screenType = DialogScreensIfc.CONFIRMATION;
                    String[] args =  {DomainGateway.getFactory()
                                      .getTenderTypeMapInstance()
                                      .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH)};

                    UIUtilities.setDialogModel(ui, screenType, "CashBackExceedsLimit", args);
                    return;
                }
           }
        }
        // remail letter used to get to this site
        if (changeTenderLetter != null)
        {
            bus.mail(changeTenderLetter, BusIfc.CURRENT);
        }
        else
        {
            // remail letter used to get to this site
            bus.mail(bus.getCurrentLetter(), BusIfc.CURRENT);
        }
    }
    /**
     * Get more than maximum issue amount error dialog model
     * @param utility  Utility Manager
     * @param resourceID String
     * @return  DialogBeanModel
     */
    protected DialogBeanModel getDialogModel(UtilityManagerIfc utility, String resourceID)
    {
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String[] args = new String[2];
        args[0] = utility.retrieveDialogText("GiftCertificate",
                "Gift Certificate").toLowerCase(locale);
        args[1] = args[0];

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(resourceID);
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setArgs(args);
        return dialogModel;
    }
}
