/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcertificate/GiftCertificateTenderLimitActionSite.java /main/15 2011/12/05 12:16:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/12/11 - update generics
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/16/2008 5:34:14 AM   Neeraj Gautam
 *         Updated arrive(Bus busIfc) method to handle the TenderErrorCode
 *         "OVERTENDER_ILLEGAL" and display the overtendering error message. -
 *          CR-31526
 *    3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:57 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:15 PM  Robert Pearse   
 *   $
 *   Revision 1.2  2004/05/18 22:02:35  crain
 *   @scr 4936 Gift Cert_AMT_LESS_THAN_MIN_ message prompt text incorrect
 *
 *   Revision 1.1  2004/04/20 23:04:20  bwf
 *   @scr 4263 Decomposition of gift certificate.
 *
 *   Revision 1.6  2004/03/26 20:20:49  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.5  2004/03/25 14:20:06  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.4  2004/03/23 00:31:09  crain
 *   @scr 4105 Foreign Currency
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
 *    Rev 1.2   Dec 02 2003 17:53:58   crain
 * Modified flow
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.1   Nov 21 2003 09:28:38   epd
 * refactor
 * 
 *    Rev 1.0   Nov 20 2003 16:39:28   crain
 * Initial revision.
 * Resolution for 3421: Tender redesign
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.giftcertificate;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site invokes gift certificate tender limit validation
 */
public class GiftCertificateTenderLimitActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6543953849121957381L;
    /**
     * gift certificate tag
     */
    public static final String GIFT_CERTIFICATE_TAG = "GiftCertificate";
    /**
     * gift certificate text
     */
    public static final String GIFT_CERTIFICATE = "Gift Certificate";
    /** 
     * Checks the tender limits.
     * 
     * @param bus BusIfc
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap<String,Object> attributes = cargo.getTenderAttributes();
        attributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.GIFT_CERT);

        try 
        {
            cargo.getCurrentTransactionADO().validateTenderLimits(attributes);

            // mail a letter
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TenderException te)
        {
            TenderErrorCodeEnum errorCode = te.getErrorCode();
         
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            String[] args = new String[] {utility.retrieveDialogText(GIFT_CERTIFICATE_TAG,
                                                 GIFT_CERTIFICATE).toLowerCase(locale)};
            if (errorCode == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED)
            {
                displayErrorDialog(ui, "AmountExceedsMaximum", args, DialogScreensIfc.CONFIRMATION);
                return;
            }
            else if (errorCode == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED)
            {
                displayErrorDialog(ui, "AmountLessThanMinimum", args, DialogScreensIfc.CONFIRMATION);
                return;
            }
            else if (errorCode == TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED)
            {
                displayErrorDialog(ui, "CashBackExceedsLimit", args, DialogScreensIfc.CONFIRMATION);
                return;
            }
            else if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                displayErrorDialog(ui, "OvertenderNotAllowed", args, DialogScreensIfc.ERROR);
                return;
            }
        }
    }

    /**
     * Displays the specified Dialog.
     * 
     * @param ui UI Manager to handle the IO
     * @param name name of the Error Dialog to display
     * @param args arguments for the dialog screen
     * @param type the dialog type
     * @see oracle.retail.stores.pos.ui.DialogScreensIfc
     */
    private void displayErrorDialog(POSUIManagerIfc ui, String name, String[] args, int type)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);

        if (args != null)
        {
            dialogModel.setArgs(args);
        }
        dialogModel.setType(type);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
