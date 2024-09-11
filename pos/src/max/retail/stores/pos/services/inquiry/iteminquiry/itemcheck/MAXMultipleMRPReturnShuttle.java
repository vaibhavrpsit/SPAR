/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.

     $Log$
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.inquiry.iteminquiry.itemcheck;

import max.retail.stores.pos.services.sale.multiplemrp.MAXMultipleMRPCargo;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;


//------------------------------------------------------------------------------
/**

    @version $Revision: /rgbustores_12.0.9in_branch/1 $
**/
//------------------------------------------------------------------------------

public class MAXMultipleMRPReturnShuttle implements ShuttleIfc
{

    //--------------------------------------------------------------------------
    /**


            @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    static final long serialVersionUID = 8726461900194986465L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";
    /**
        MultipleMRP cargo
    **/
    protected MAXMultipleMRPCargo mCargo = null;

    /**
    The line item to change multiple mrp
     **/
    protected PLUItemIfc item = null;


    public void load(BusIfc bus)
    {
//	 get cargo reference and set attributes
        MAXMultipleMRPCargo cargo = (MAXMultipleMRPCargo)bus.getCargo();
        item=cargo.getPLUItem();
    }

    //--------------------------------------------------------------------------
    /**


            @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------

    public void unload(BusIfc bus)
    {
	ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
        if (cargo.getPLUItem() != null)
        {
            cargo.setPLUItem(item);
        }
    }
    }

