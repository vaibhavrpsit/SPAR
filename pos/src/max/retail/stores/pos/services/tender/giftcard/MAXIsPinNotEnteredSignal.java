package max.retail.stores.pos.services.tender.giftcard;

import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;

import org.apache.log4j.Logger;

public class MAXIsPinNotEnteredSignal implements TrafficLightIfc {
	
	static final long serialVersionUID = 4572355899018953584L;
	  public static String revisionNumber = "$Revision: 1.1 $";

	  protected static Logger logger = Logger.getLogger(MAXIsPinNotEnteredSignal.class);

	  public boolean roadClear(BusIfc bus)
	  {
	    if (logger.isDebugEnabled()) logger.info("ENTRYLSIPLIsPinNotEnteredSignal.roadClear()");

	    boolean result = false;
	    TenderCargo cargo = (TenderCargo)bus.getCargo();
	    GiftCardIfc giftCard = (GiftCardIfc)cargo.getGiftCard();

	    if (giftCard == null)
	    {
	      result = true;
	    }
	    if (logger.isDebugEnabled()) logger.info("EXITLSIPLIsPinNotEnteredSignal.roadClear()");
	    return result;
	  }

	  public String toString()
	  {
	    String strResult = new String("Class:  IsTillSuspendedSignal (Revision " + getRevisionNumber() + ")" + hashCode());

	    return strResult;
	  }

	  public String getRevisionNumber()
	  {
	    return revisionNumber;
	  }

}
