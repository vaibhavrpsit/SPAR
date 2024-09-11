/* ===========================================================================
* Copyright (c) 2001, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/ProcessExternalOrderSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/23/10 - Modified to set store number and geo code on the
 *                         cargo so that the tax rules will be retrieved with
 *                         the PLU.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    jswan     05/19/10 - Add transaction reentry classes
 *    jswan     05/19/10 - Add site for returns external order refactoring.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ExternalOrderItemReturnStatusElement;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;

//--------------------------------------------------------------------------
/**
   This method gets the next available external order item, updates 
   the cargo with POS Item ID an mails a continue letter.  This causes
   the tour to process the item ID as if the user had entered it.
   
   This will be called iteratively until all external order items have
   been processed.
**/
//--------------------------------------------------------------------------
public class ProcessExternalOrderSite extends PosSiteActionAdapter
{

    /** serialVersionUID */
    private static final long serialVersionUID = 6430044420573899581L;

    //----------------------------------------------------------------------
    /**
       This method gets the next available external order item, updates 
       the cargo with POS Item ID an mails a continue letter.  This causes
       the tour to process the item ID as if the user had entered it.
       
       This will be called iteratively until all external order items have
       been processed.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();
        boolean itemIDFound      = false;
        String letter            = CommonLetterIfc.SUCCESS;

        // If there are still external order item process...
        // Get the next external order item that has not been returned.
        for(int i = 0; i < cargo.getExternalOrderItemReturnStatusElements().size() && !itemIDFound; i++)
        {
            ExternalOrderItemReturnStatusElement statusElement = cargo.
                getExternalOrderItemReturnStatusElements().get(i);
            if (!statusElement.isSelected())
            {
                statusElement.setSelected(true);
                cargo.setPLUItemID(statusElement.getExternalOrderItem().getPOSItemId());
                cargo.setCurrentExternalOrderItemReturnStatusElement(statusElement);
                cargo.setItemScanned(false);
                itemIDFound = true;
                letter      = CommonLetterIfc.CONTINUE;
            }
        }
        
        if (!itemIDFound)
        {
            cargo.setCurrentItem(cargo.getLastLineItemReturnedIndex());
        }
        else
        {
            //Have to set the GeoCode to get the tax rules back
            if(cargo.getStoreStatus() != null &&
                    cargo.getStoreStatus().getStore() != null )
            {
                cargo.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
                cargo.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
            }
        }
        
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}
