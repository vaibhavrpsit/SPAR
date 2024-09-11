package max.retail.stores.pos.services.dailyoperations.poscount;

import max.retail.stores.domain.tender.MAXTenderLineItemIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;

public class MAXEWalletSelectedAisle extends PosLaneActionAdapter
{
	  /**
		 * 
		 */
		private static final long serialVersionUID = -101415319029258993L;
	/**
	     revision number of this class
	  **/
	  public static String revisionNumber = "$Revision: 3$";

	  //----------------------------------------------------------------------
	  /**
	     Sets the currency selected.
	     <p>
	     @param  bus     Service Bus
	  **/
	  //----------------------------------------------------------------------
	  public void traverse(BusIfc bus)
	  {
	      PosCountCargo cargo = (PosCountCargo)bus.getCargo();
	      cargo.setCurrentActivityOrCharge(
	          DomainGateway.getFactory()
	                       .getTenderTypeMapInstance()
	                       .getDescriptor(MAXTenderLineItemIfc.TENDER_TYPE_EWALLET));

	      String letterName = "CountSummary";
	      if (!cargo.getSummaryFlag())
	      {
	          if (cargo.currentHasDenominations())
	          {
	              letterName = "CashDetail";
	          }
	          else
	          {
	              letterName = "CountDetail";
	          }
	      }
	      
	      bus.mail(new Letter(letterName), BusIfc.CURRENT);
	  }

	  //----------------------------------------------------------------------
	  /**
	     Returns a string representation of the object.
	     <P>
	     @return String representation of object
	  **/
	  //----------------------------------------------------------------------
	  public String toString()
	  {
	      String strResult = new String("Class:  " + getClass().getName() + "(Revision " +
	                                    getRevisionNumber() + ")" + hashCode());
	      return(strResult);
	  }

	  //----------------------------------------------------------------------
	  /**
	     Returns the revision number of the class.
	     <P>
	     @return String representation of revision number
	  **/
	  //----------------------------------------------------------------------
	  public String getRevisionNumber()
	  {
	      return(revisionNumber);
	  }

}

