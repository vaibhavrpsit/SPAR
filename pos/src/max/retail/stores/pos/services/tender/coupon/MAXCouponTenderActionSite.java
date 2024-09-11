package max.retail.stores.pos.services.tender.coupon;

import java.awt.Color;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
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
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCouponTenderActionSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8525776769554548097L;
	/** revision number **/
    public static final String revisionNumber = "$Revision: 3$";
    
    /* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
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
            bus.mail(new Letter("Success"), BusIfc.CURRENT);
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
        dialogModel.setBannerColor(Color.RED);
        dialogModel.setType(DialogScreensIfc.YES_NO);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Failure");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Yes");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
