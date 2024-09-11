/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/FingerprintReaderActionGroup.java /main/4 2012/02/22 12:28:56 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  02/22/12 - XbranchMerge
 *                         mkutiana_bug13728958-disable_fpdevice_based_on_parm
 *                         from rgbustores_13.4x_generic_branch
 *    mkutiana  02/21/12 - disable the fingerprintreader (at login) when
 *                         parameter is set to NoFingerprint
 *    icole     08/17/11 - Use checkHealth for determining if online.
 *    blarsen   02/24/11 - fingerprint reader action group
 * 
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import jpos.BiometricsControl111;
import jpos.JposConst;
import jpos.JposException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.FingerprintReaderSession;
import oracle.retail.stores.foundation.manager.device.FingerprintReaderSessionIfc;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;

//--------------------------------------------------------------------------
/**
@see oracle.retail.stores.pos.device.FingerprintReaderActionGroupIfc
**/
//--------------------------------------------------------------------------
public class FingerprintReaderActionGroup extends POSDeviceActionGroup implements FingerprintReaderActionGroupIfc
{
	private String checkHealthOKText = "OK";
	
	
    /**
     * @see oracle.retail.stores.pos.device.FingerprintReaderActionGroupIfc#isFingerprintReaderOnline()
    **/
    public Boolean isFingerprintReaderOnline()
    {
        boolean isOnline = false;
        boolean closeDevice = false;
  
        try
        {
            DeviceTechnicianIfc dt = getDeviceTechnician();
            DeviceSessionIfc fingerprintReaderSession = dt.getDeviceSession(FingerprintReaderSession.TYPE);
            String fingerprintReaderName = fingerprintReaderSession.getDeviceName();
            BiometricsControl111 fingerprintReader = (BiometricsControl111) fingerprintReaderSession.getDevice();    
            if ((fingerprintReader != null) && (fingerprintReaderName != null))
            {        
                try 
                {
                 
                    // Be careful not to interfere with the device if it is open and active.
                    // The online status check can occur while a user is midway through enrolling fingerprints, etc.
                    
                    if (fingerprintReader.getState() == JposConst.JPOS_S_CLOSED)
                    {
                        fingerprintReader.open(fingerprintReaderName);
                        closeDevice = true;
                    }
                    fingerprintReader.checkHealth(jpos.JposConst.JPOS_CH_INTERNAL);
                    if(fingerprintReader.getCheckHealthText().contains(checkHealthOKText))
                    {
                    	isOnline = true;
                    }
                    // don't close unless device was opened earlier by this method
                    if (closeDevice)
                    {
                        fingerprintReader.close();
                    }
                }
                catch (JposException je)
                {
                	je.printStackTrace();
                    //This implies that fingerprintReader is offline
                }
            }
        }
        catch (DeviceException e)
        {
            //This implies that fingerprintReader is offline
        }
        return (new Boolean(isOnline));
    }

    /**
     * @see oracle.retail.stores.pos.device.FingerprintReaderActionGroupIfc#verifyFingerprintMatch()
    **/
    public Boolean verifyFingerprintMatch(byte[] enrolledFingerprintTemplate, byte[] fingerprint)
    {
        boolean isMatch = false;
        
        try
        {
            DeviceTechnicianIfc dt = getDeviceTechnician();
            FingerprintReaderSessionIfc fingerprintReaderSession = (FingerprintReaderSessionIfc)dt.getDeviceSession(FingerprintReaderSession.TYPE);
            isMatch = fingerprintReaderSession.verifyMatch(enrolledFingerprintTemplate, fingerprint);
        }
        catch (DeviceException e) 
        {
            //unable to confirm match, consider it not a match
        }
        return (new Boolean(isMatch));
    }
 
    /**
     * Set the string to use to check for health good.
     * 
     * @param checkHealthOKText
     */
    public void setCheckHealthOKText(String checkHealthOKText) 
    {
        this.checkHealthOKText = checkHealthOKText;
    }


    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.device.FingerprintReaderActionGroupIfc#deactivateFingerprintReader()
     */
    public void deactivateFingerprintReader(){	    
        try
        {
            DeviceTechnicianIfc dt = getDeviceTechnician();
            DeviceSessionIfc fingerprintReaderSession = dt.getDeviceSession(FingerprintReaderSession.TYPE);
            fingerprintReaderSession.deactivate();
        }
        catch (DeviceException e)
        {
            //This implies that fingerprintReader is offline
        }
    }

}

