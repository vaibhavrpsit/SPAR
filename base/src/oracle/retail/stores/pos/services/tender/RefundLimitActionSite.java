/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/RefundLimitActionSite.java /rgbustores_13.4x_generic_branch/2 2011/09/20 16:11:06 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/20/11 - formatting
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/31/2008 1:56:17 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:37 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/07/16 22:12:05  epd
 *   @scr 4268 Changing flows to add gift card credit
 *
 *   Revision 1.2  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 28 2004 15:39:10   epd
 * return after dialog
 * 
 *    Rev 1.0   Jan 28 2004 13:53:52   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.ReturnableTransactionADOIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @author epd
 */
@SuppressWarnings("serial")
public class RefundLimitActionSite extends PosSiteActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // add tender type to attributes
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        String refundTenderLetter = null;
        try 
        {
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();

            boolean isReturnWithReceipt = ((ReturnableTransactionADOIfc)txnADO).isReturnWithReceipt();
            boolean isReturnWithOriginalRetrieved = ((ReturnableTransactionADOIfc)txnADO).isReturnWithOriginalRetrieved();
            txnADO.validateRefundLimits(cargo.getTenderAttributes(), isReturnWithReceipt, isReturnWithOriginalRetrieved);
            // save and reset refund tender letter
            refundTenderLetter = cargo.getRefundTenderLetter();
            cargo.setRefundTenderLetter(null);
        }
        catch (TenderException te)
        {
            TenderErrorCodeEnum errorCode = te.getErrorCode();

            // save current letter for mailing later.
            // We need to save this in case refund limits were overridden.  In that
            // case, the letter changes, but we need the original letter.
            cargo.setRefundTenderLetter(bus.getCurrentLetter().getName());

            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String[] args = new String[1];
            args[0] = utility.retrieveText("tender", "tenderText", bus.getCurrentLetter().getName(), ""); 
            if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("OvertenderNotAllowed");
                dialogModel.setType(DialogScreensIfc.ERROR);
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            else if (errorCode == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED)
            {
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("AmountExceedsMaximum");
                dialogModel.setType(DialogScreensIfc.CONFIRMATION);
                dialogModel.setArgs(args);
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            else if (errorCode == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED)
            {
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("AmountLessThanMinimum");
                dialogModel.setType(DialogScreensIfc.CONFIRMATION);
                dialogModel.setArgs(args);
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            return;
        }

        // remail letter used to get to this site
        if (refundTenderLetter != null)
        {
            bus.mail(refundTenderLetter, BusIfc.CURRENT);
        }
        else
        {
            bus.mail(bus.getCurrentLetter().getName(), BusIfc.CURRENT);
        }
    }
    
}
