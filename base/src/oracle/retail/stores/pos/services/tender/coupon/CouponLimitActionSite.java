/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/coupon/CouponLimitActionSite.java /rgbustores_13.4x_generic_branch/1 2011/07/12 15:58:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/12/11 - update generics
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:13 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/08/16 21:42:51  bwf
 *   @scr 6580 Now check limits for coupon.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.coupon;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This method checks the limits for the coupon tender.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CouponLimitActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -1478031154587004786L;

    /**
     * The arrive method checks the limits.
     * 
     * @param bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get tender attributes from cargo and add tender type
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.COUPON);
        
        // Use transaction to validate limits for coupon
        try
        {
            cargo.getCurrentTransactionADO().validateTenderLimits(tenderAttributes);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum errorCode = e.getErrorCode();
         
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                // display error message
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("OvertenderNotAllowed");
                model.setType(DialogScreensIfc.ERROR);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                return;
            }
        }

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);        
    }
}
