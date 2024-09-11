/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/SigCapActionGroup.java /main/12 2014/01/24 16:58:49 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/24/14 - fix null dereferences
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:30:05 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:25:20 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:15 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/09/23 00:07:13  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.4  2004/04/08 20:33:02  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 *Revision: /main/7 $
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import java.io.Serializable;

import jpos.JposException;
import jpos.SignatureCapture;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.DeviceMode;
import oracle.retail.stores.foundation.manager.device.SigCapModel;
import oracle.retail.stores.foundation.manager.device.SigCapSession;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModeIfc;

//--------------------------------------------------------------------------
/**
The <code>SigCapActionGroup</code> defines Signature Capture
specific device operations available to POS applications.
<p>
@version $Revision: /main/12 $
@see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc
**/
//--------------------------------------------------------------------------
public class SigCapActionGroup extends POSDeviceActionGroup implements SigCapActionGroupIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4007288861233556908L;

    /**
        signature Capture Form
    **/
    protected String signatureCaptureForm = "";
    
    //---------------------------------------------------------------------
    /**
       Begin signature capture.  <P>
       @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#beginCapture
    **/
    //---------------------------------------------------------------------
    public void beginCapture(String formName) throws DeviceException
    {
        SigCapSession       sigCapSession   = null;
        SignatureCapture    sigCap          = null;

        try
        {
            sigCapSession   = (SigCapSession) getDeviceTechnician().getDeviceSession(SigCapSession.TYPE);

            //throw a DEVICE_UNAVAILABLE exception if a SimulatedSigCapSession is configured
            //this allows POS to distinguish between an offline situation and a register
            //without a signature capture device
            if (sigCapSession.isSessionSimulated())
            {
                throw new DeviceException(DeviceException.DEVICE_UNAVAILABLE);
            }

            sigCapSession.activateSigCap(SigCapSession.MODE_READY_TO_CAPTURE);
            sigCap          = (SignatureCapture) sigCapSession.getDevice();

            //These parameters are now specified in devices.xml as properties
            //to device action group instead of hardcoded in POS app
            //(so, yes, parameters passed in are ignored)
            sigCap.beginCapture(getSignatureCaptureForm());

        }
        catch (JposException e)
        {
            logger.error(
                         "Signature Capture Exception: Error on begin capture. " + e.getMessage() + "  Error Code:  " + Integer.toString(e.getErrorCode()) + "  Extended Error Code:  " + Integer.toString(e.getErrorCodeExtended()) + "");

            throw new DeviceException(DeviceException.JPOS_ERROR,"Signature Capture error", e);
        }
        catch (DeviceException e)
        {
            //release the device
            if (sigCapSession != null)
            {
                DeviceModeIfc dm = new DeviceMode();
                dm.setDeviceSessionName(SigCapSession.TYPE);
                dm.setDeviceModeName(SigCapSession.MODE_RELEASED);
                sigCapSession.setDeviceMode(dm);
            }
            throw e;
        }
    }

    //---------------------------------------------------------------------
    /**
       End signature capture.  <P>
       @return Serialized signature data or null
       @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#endCapture
    **/
    //---------------------------------------------------------------------
    public Serializable endCapture() throws DeviceException
    {
        SigCapSession           sigCapSession = null;
        SignatureCapture        sigCap        = null;
        Serializable            signature     = null;

/*
        try
        {
*/
            sigCapSession   = (SigCapSession) getDeviceTechnician().getDeviceSession(SigCapSession.TYPE);
            sigCap          = (SignatureCapture) sigCapSession.getDevice();

/*
       This line is commented out because this method is not supported on the
       Hypercom ICE 6000.
       If another device is used that does support this call, then uncomment
       the line.
*/
//            sigCap.endCapture();

            signature       = ((SigCapModel)sigCapSession.getDeviceModel()).getPointArray();
            sigCapSession.deactivateSigCap();
/*
        }
        catch (JposException e)
        {
            logger.error(
                         "Signature Capture Exception: Error on end capture. " + e.getMessage() + "  Error Code:  " + Integer.toString(e.getErrorCode()) + "  Extended Error Code:  " + Integer.toString(e.getErrorCodeExtended()) + "");

            //release the device
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(SigCapSession.TYPE);
            dm.setDeviceModeName(SigCapSession.MODE_RELEASED);
            sigCapSession.setDeviceMode(dm);

            throw new DeviceException(DeviceException.JPOS_ERROR,"Signature Capture error", e);
        }
*/
        return signature;
    }

    //---------------------------------------------------------------------
    /**
        Sets the form to display on the signature capture device.
        @param value name of the form
    **/
    //---------------------------------------------------------------------
    public void setSignatureCaptureForm(String value)
    {
         signatureCaptureForm = value;
    }
    
    //---------------------------------------------------------------------
    /**
        Gets the name of the form to display on the signature capture screen
        @return name of the signature capture form
    **/
    //---------------------------------------------------------------------
    public String getSignatureCaptureForm()
    {
        return signatureCaptureForm;
    }

    //---------------------------------------------------------------------
    /**
       Returns true if signature capture is simulated.  <P>
       @return Serializable Boolean true if signature capture is simulated, false otherwise
       @exception DeviceException is thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Serializable isSignatureCaptureSimulated() throws DeviceException
    {                                   // begin isSignatureCaptureSimulated()
        String stringValue;             // returned hard totals device name
        DeviceTechnicianIfc dt;
        SigCapSession sigCapSession;

        dt = getDeviceTechnician();
        sigCapSession =
            (SigCapSession) dt.getDeviceSession(SigCapSession.TYPE);
        Boolean returnValue = new Boolean(sigCapSession.isSessionSimulated());

        return returnValue;
    }                                   // end isSignatureCaptureSimulated()

}

