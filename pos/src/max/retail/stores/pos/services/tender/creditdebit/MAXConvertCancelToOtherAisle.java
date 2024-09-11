/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

public class MAXConvertCancelToOtherAisle extends PosLaneActionAdapter implements LaneActionIfc {
	static final long serialVersionUID = 9202472882941911198L;
	public static final String LANENAME = "ConvertCancelToOtherAisle";

	public void traverse(BusIfc bus) {
		bus.mail(new Letter("Undo"), BusIfc.CURRENT);
	}
}