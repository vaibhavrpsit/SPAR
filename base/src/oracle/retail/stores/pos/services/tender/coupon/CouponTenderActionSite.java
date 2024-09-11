/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/coupon/CouponTenderActionSite.java /main/11 2011/02/16 09:13:29 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *   Revision 1.2.2.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.2  2004/07/22 22:38:41  bwf
 *   @scr 3676 Add tender display to ingenico.
 *
 *   Revision 1.1  2004/04/02 20:17:27  epd
 *   @scr 4263 Refactored coupon tender into sub service
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
 *    Rev 1.0   Nov 04 2003 11:17:38   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 30 2003 13:01:06   crain
 * Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.coupon;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderCouponADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
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
 * This class validates the coupon limits and adds the tender to the
 * transaction.
 * 
 * @version $Revision: /main/11 $
 */
public class CouponTenderActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 7445586109742440179L;
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        
        try
        {
            // Use transaction to validate limits for coupon
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
            TenderCouponADO couponTender = (TenderCouponADO)cargo.getTenderADO();
            txnADO.validateTenderLimits(couponTender.getTenderAttributes());
            
            // add coupon tender to transaction
            txnADO.addTender(couponTender);
            cargo.setLineDisplayTender(couponTender);
            
            // journal the added tender
            JournalFactoryIfc jrnlFact = null;
            try
            {
                jrnlFact = JournalFactory.getInstance();
            }
            catch (ADOException e)
            {
                logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
                throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            }
            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
            registerJournal.journal(couponTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
        
            // mail a letter
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum errorCode = e.getErrorCode();
         
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                displayErrorDialog(ui, "CouponOvertenderNotAllowed");
            }   
        }
    }

    /**
     * Display the specified error dialog
     * 
     * @param POSUIManagerIfc UI Manager to handle the IO
     * @param String name of the error dialog to display
     * @param String[] args for the error dialog
     */
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
