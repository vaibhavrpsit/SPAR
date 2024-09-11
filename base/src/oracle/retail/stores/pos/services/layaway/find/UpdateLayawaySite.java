/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/UpdateLayawaySite.java /main/1 2013/07/30 15:31:23 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/18/13 - Initial
 *   
 * ===========================================================================
 * $Log: $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

//foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.LayawayDataTransaction;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


/**
 * Site to save the layaway that has been updated with a new customer.  This only occurs if the
 * customer associated with the layaway is deleted and a new one is added.
 * @author icole
 * @since 14
 */
public class UpdateLayawaySite extends PosSiteActionAdapter
{
    /**
     * 
     */
    public static final String SAVE_UPDATED_LAYAWAY_FAILED = "SaveUpdatedLayawayFailed";

    public void arrive(BusIfc bus)
    {
        boolean mailLetter = true;
        Letter result = new Letter (CommonLetterIfc.CONTINUE); // default value
        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();
        LayawayIfc layaway = cargo.getLayaway();
        LayawayDataTransaction ldt = null;
        ldt = (LayawayDataTransaction) DataTransactionFactory.create(DataTransactionKeys.LAYAWAY_DATA_TRANSACTION);
        try
        {
            ldt.updateLayaway(layaway);
        }
        catch(DataException de)
        {
            mailLetter = false; 
            logger.error("Saving layaway with new customer failed.",de);
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(SAVE_UPDATED_LAYAWAY_FAILED);
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);
            // show the screen
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        if(mailLetter)
        {
            bus.mail(result, BusIfc.CURRENT);
        }
    }
}
