
package max.retail.stores.pos.services.tender.creditdebit;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 *  Evaluates the Debit tender limits
 */
public class MAXPineLabDebitTenderLimitActionSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        // set the tender type to debit.  This may not have happened yet if
        // we got here directly by selecting "Debit" as opposed to "Credit/Debit"
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.getTenderAttributes().put(TenderConstants.TENDER_TYPE, TenderTypeEnum.DEBIT);
        
        try
        {
            // validate debit tender limits
            cargo.getCurrentTransactionADO().validateTenderLimits(cargo.getTenderAttributes());
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();

            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            if (error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                UIUtilities.setDialogModel(ui, 
                                           DialogScreensIfc.ERROR,
                                           "OvertenderNotAllowed",
                                           null,
                                           "Invalid");
                return;
            }
            else if (error == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED)
            {
                int[] buttons = new int[] {DialogScreensIfc.BUTTON_YES,
                                           DialogScreensIfc.BUTTON_NO};
                String[] letters = new String[] {"Override", "Invalid"};
                String [] args = new String[] {TenderTypeEnum.DEBIT.toString()};
                UIUtilities.setDialogModel(ui, 
                                           DialogScreensIfc.CONFIRMATION,
                                           "AmountExceedsMaximum",
                                           args,
                                           buttons,
                                           letters);
                return;
            }
            else if (error == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED)
            {
                int[] buttons = new int[] {DialogScreensIfc.BUTTON_YES,
                                           DialogScreensIfc.BUTTON_NO};
                String[] letters = new String[] {"Override", "Invalid"};
                String [] args = new String[] {TenderTypeEnum.DEBIT.toString()};
                UIUtilities.setDialogModel(ui, 
                                           DialogScreensIfc.CONFIRMATION,
                                           "AmountLessThanMinimum",
                                           args,
                                           buttons,
                                           letters);
                return;
            }
        }
        
        bus.mail(new Letter("Success"), BusIfc.CURRENT);
    }
}
