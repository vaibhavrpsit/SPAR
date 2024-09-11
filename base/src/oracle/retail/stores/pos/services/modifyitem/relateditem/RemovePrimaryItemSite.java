package oracle.retail.stores.pos.services.modifyitem.relateditem;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
     This site remove primary item from transaction since its auto related item is not valid.
     $Revision: /main/1 $
 **/
//--------------------------------------------------------------------------
public class RemovePrimaryItemSite extends PosSiteActionAdapter
{

    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        RelatedItemCargo cargo = (RelatedItemCargo) bus.getCargo();
        
        if ( cargo.isAddAutoRelatedItem() )
        {
            int index = cargo.getPrimaryItemSequenceNumber();
            cargo.getTransaction().removeLineItem(index);
        }
        bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
    }
}