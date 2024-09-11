package max.retail.stores.pos.services.dailyoperations.till.tillopen;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CheckOfflineSite;
import oracle.retail.stores.pos.services.dailyoperations.till.tillopen.TillOpenCargo;

public class MAXPosOfflineCheckSite extends CheckOfflineSite
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -1856704664729076701L;
	public static final String revisionNumber = "$Revision: 3$";

    public MAXPosOfflineCheckSite()
    {
    }

    public void arrive(BusIfc bus)
    {
        DispatcherIfc d = Gateway.getDispatcher();
        DataManagerIfc dm = (DataManagerIfc)d.getManager("DataManager");
        TillOpenCargo cargo = (TillOpenCargo)bus.getCargo();
        if(!transactionsAreOffline(dm))
        {
            Letter letter = new Letter("Continue");
            bus.mail(letter, BusIfc.CURRENT);
        } else
        {
            cargo.setAccessFunctionID(MAXRoleFunctionIfc.POS_OFFLINE_ALERT);
            Letter letter = new Letter("Invalid");
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
