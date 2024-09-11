package max.retail.stores.pos.device;

import java.io.Serializable;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceActionIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.MSRActionGroupIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;

public class MAXPOSDeviceActions extends POSDeviceActions{
	
	public MAXPOSDeviceActions(SessionBusIfc bus) {

		super(bus);
	}

	public void beginMSRSwipe() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                MSRActionGroupIfc dag = (MSRActionGroupIfc) dt.getDeviceActionGroup(MSRActionGroupIfc.TYPE);
                dag.beginMSRSwipe();
                return null;
            }
        };

        transportValet(this);
    }
	
	public void endMSRSwipe() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                MSRActionGroupIfc dag = (MSRActionGroupIfc) dt.getDeviceActionGroup(MSRActionGroupIfc.TYPE);
                dag.endMSRSwipe();
                return null;
            }
        };

        transportValet(this);
    }
	public Boolean isFormOnline() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                MAXFormActionGroupIfc dag = (MAXFormActionGroupIfc) dt.getDeviceActionGroup(MAXFormActionGroupIfc.TYPE);
                return (dag.isFormOnline());
            }
        };
        return ((Boolean) transportValet(this));
    }
	
	

}
