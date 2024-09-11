/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/ValidatePickupDateAisle.java /main/2 2013/08/27 14:46:50 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  08/24/13 - Xchannel Inventory lookup enhancement phase I
 *    jswan     04/29/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

// foundation imports
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GetPickupDateBeanModel;

//------------------------------------------------------------------------------
/**
    This class validates the entered date and saves it to the cargo
    <P>
    @version $Revision: /main/2 $
**/
//--------------------------------------------------------------------------

public class ValidatePickupDateAisle extends PosLaneActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7316292829340901290L;

    /**
       class name constant
    **/
    public static final String LANENAME = "ValidatePickupDateAisle";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:14; $EKW:";

    //----------------------------------------------------------------------
    /**
        This method validates the entered date and saves it to the cargo
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        GetPickupDateBeanModel model = (GetPickupDateBeanModel)
            ui.getModel(POSUIManagerIfc.GET_PICKUP_DATE_SCREEN);

        EYSDate enteredDate = model.getSelectedPickupDate();
        StoreIfc currentStore = cargo.getStoreForPickupByLineNum().get(
                cargo.getLineItemsBucket().get(cargo.getLineItemIndex()).getItemBucket().get(0).getLineNumber());
        AvailableToPromiseInventoryIfc atpi = cargo.getStoreItemAvailablity(
                currentStore.getStoreID(), cargo.getItemAvailablityList());
        EYSDate availableDate =  atpi.getDate();
        availableDate.setType(EYSDate.TYPE_DATE_ONLY);

        if (enteredDate.equals(availableDate) || enteredDate.after(availableDate))
        {
            for (int i=0; i<cargo.getLineItemsBucket().get(cargo.getLineItemIndex()).getItemBucket().size();i++)
            {
             cargo.getDateForPickupByLineNum().put(cargo.getLineItemsBucket().get(cargo.getLineItemIndex()).getItemBucket().get(i).getLineNumber(), enteredDate);
            }
            bus.mail(CommonLetterIfc.CONTINUE);
        }
        else
        {
            dialogForIncorrectDate(bus);
        }
    }

    /**
     * Display dialog for an incorrectly entered date
     * @param bus Service bus.
     */
    public void dialogForIncorrectDate(BusIfc bus)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("IncorrectEnteredDate");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.ERROR);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
