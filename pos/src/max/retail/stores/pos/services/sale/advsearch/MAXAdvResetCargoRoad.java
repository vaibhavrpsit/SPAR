package max.retail.stores.pos.services.sale.advsearch;

import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

//--------------------------------------------------------------------------
/**
  This road is traveled when the user selects an item
  It stores the item  in the cargo.
  @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXAdvResetCargoRoad extends LaneActionAdapter
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 4221146239748221297L;
/**
      revision number
  **/
  public static final String revisionNumber = "$Revision: 3$";

  //----------------------------------------------------------------------
  /**
      Stores the item info and dept list  in the cargo.
      @param  bus     Service Bus
  **/
  //----------------------------------------------------------------------
  public void traverse(BusIfc bus)
  {
	  MAXItemInquiryCargo cargo = (MAXItemInquiryCargo)bus.getCargo();
      // Reset Cargo
      cargo.setModifiedFlag(false);
      cargo.setAddSearchPLUItem(false);
      if(bus.getCurrentLetter().getName().equals(CommonLetterIfc.UNDO) || bus.getCurrentLetter().getName().equals(CommonLetterIfc.CANCEL))
      cargo.setPLUItem(null);

  }
}
