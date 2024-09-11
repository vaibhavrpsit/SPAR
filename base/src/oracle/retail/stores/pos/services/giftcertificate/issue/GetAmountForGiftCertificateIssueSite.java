/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcertificate/issue/GetAmountForGiftCertificateIssueSite.java /main/14 2013/01/04 16:42:28 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   01/04/13 - Fixed problem with pricing of a gift certificate
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       01/14/09 - use decimal format to set string value of a currency
 *                         object
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/21/2008 3:03:22 PM   Mathews Kochummen
 *         forward port v12x to trunk. reviewed by alan
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:09 PM  Robert Pearse
 *
 *   Revision 1.3  2004/03/02 00:32:36  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.2  2004/02/22 17:23:32  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.1  2004/02/20 14:15:17  crain
 *   @scr 3814 Issue Gift Certificate
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcertificate.issue;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

/**
 *  This site displays the gift certificate amount screen
 */
public class GetAmountForGiftCertificateIssueSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -268486526374875896L;

    /**
     * gift certificate number field
     */
    public static final String GIFT_CERTIFICATE_NUMBER   = "giftCertificateNumberField";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        NavigationButtonBeanModel localNavigationButtonBeanModel =
            GiftCardUtilities.getGiftCardDenominationsModel(
                utility,
                pm,
                logger,
                bus.getServiceName());
        DataInputBeanModel model = new DataInputBeanModel();
        model.setLocalButtonBeanModel(localNavigationButtonBeanModel);
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        model.setValue(GIFT_CERTIFICATE_NUMBER, cargo.getPLUItem().getItemID());
        ui.showScreen(POSUIManagerIfc.GIFT_CERTIFICATE_AMOUNT, model);
    }

}
