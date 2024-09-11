/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/MSRActionGroup.java /main/13 2014/01/24 16:58:50 mjwallac Exp $
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
 *   6    360Commerce 1.5         5/6/2008 5:03:16 PM    Ranjan Ojha     Fixed
 *        the ClassCastException occuring for MSRSession.
 *   5    360Commerce 1.4         2/6/2007 2:16:22 PM    Edward B. Thorne Merge
 *         from MSRActionGroup.java, Revision 1.2.3.0 
 *   4    360Commerce 1.3         12/14/2006 2:30:17 PM  Brendan W. Farrell
 *        Merge from v7x
 *   3    360Commerce 1.2         3/31/2005 4:29:06 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:23:37 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:12:42 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/09/23 00:07:13  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.3  2004/02/12 16:48:34  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:30:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Nov 21 2003 13:43:38   rsachdeva
 * Added Comment
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 * 
 *    Rev 1.1   Oct 20 2003 09:44:12   rsachdeva
 * isMSROnline
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 * 
 *    Rev 1.0   Aug 29 2003 15:51:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.5   Jul 24 2003 15:06:20   mrm
 * Swipe anytime at Ingenico or Hypercom MSR
 * Resolution for POS SCR-2782: With Ingenico Device you should be able to swipe with at the MSR.
 * Resolution for POS SCR-3051: Enable dual use of MSR on keyboard and Hypercomm
 * 
 *    Rev 1.4   Feb 03 2003 15:56:00   vxs
 * Added javadoc headers to methods
 * Resolution for POS SCR-1936: Swipe Credit/Debit/Gift Card Tender Anytime
 * 
 *    Rev 1.3   Jan 29 2003 17:41:50   vxs
 * returning correct value in getMSRModel()
 * Resolution for POS SCR-1936: Swipe Credit/Debit/Gift Card Tender Anytime
 * 
 *    Rev 1.2   Jan 21 2003 18:03:42   vxs
 * Modified return value in getMSRModel()
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 * 
 *    Rev 1.1   Jan 21 2003 18:01:56   vxs
 * Commented system.out.println() statements
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 * 
 *    Rev 1.0   Jan 15 2003 16:32:42   vxs
 * Initial revision.
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 *Revision: /main/8 $
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

//jpos imports
import jpos.JposConst;
import jpos.JposException;
import jpos.MSR;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.DeviceMode;
import oracle.retail.stores.foundation.manager.device.ExternalMSRSession;
import oracle.retail.stores.foundation.manager.device.HypercomMSRSession;
import oracle.retail.stores.foundation.manager.device.IngenicoMSRSession;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.device.MSRSession;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModeIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;



//--------------------------------------------------------------------------
/**
The <code>MSRActionGroup</code> defines the MSR specific
device operations available to POS applications.
<p>
@version $Revision: /main/13 $
@see oracle.retail.stores.pos.device.MSRActionGroupIfc
**/
//--------------------------------------------------------------------------
public class MSRActionGroup extends POSDeviceActionGroup implements MSRActionGroupIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4844782136390976162L;

    //---------------------------------------------------------------------
    /**
       Activate the MSR session. <P>
    **/
    //---------------------------------------------------------------------
    public void beginMSRSwipe() throws DeviceException
    {
        MSRSession msrSession   = null;
        try
        {
            // first test for external device
            msrSession = (MSRSession) getDeviceTechnician().getDeviceSession(ExternalMSRSession.TYPE);
            if (msrSession.getDeviceName().compareTo(IngenicoMSRSession.DEFAULT_DEVICE_NAME) == 0)
            {
                if(!((IngenicoMSRSession)msrSession).isDeviceLocked())
                {
                    ((IngenicoMSRSession)msrSession).activate(IngenicoMSRSession.MODE_DECODESCAN);
                }
            }
            else if (msrSession.getDeviceName().compareTo(HypercomMSRSession.DEFAULT_DEVICE_NAME) == 0)
            {
                if(!((HypercomMSRSession)msrSession).isDeviceLocked())
                {
                    ((HypercomMSRSession)msrSession).activate(HypercomMSRSession.MODE_DECODESCAN);
                }
            }
            else
            {
/**
                if(!msrSession.isDeviceLocked())
                {
                    msrSession.activate(MSRSession.MODE_DECODESCAN);
                }
**/
            }
        }
        catch (DeviceException e)
        {
               throw e;
        }
    }

    //---------------------------------------------------------------------
    /**
       Deactivate the MSR session. <P>
    **/
    //---------------------------------------------------------------------
    public void endMSRSwipe() throws DeviceException
    {
        MSRSession msrSession   = null;
        try
        {
            // first test for external device
            msrSession = (MSRSession) getDeviceTechnician().getDeviceSession(ExternalMSRSession.TYPE);
            if (msrSession.getDeviceName().compareTo(IngenicoMSRSession.DEFAULT_DEVICE_NAME) == 0)
            {
                if(((IngenicoMSRSession)msrSession).isDeviceLocked())
                {
                    ((IngenicoMSRSession)msrSession).deactivate();
                }
            }
            else if (msrSession.getDeviceName().compareTo(HypercomMSRSession.DEFAULT_DEVICE_NAME) == 0)
            {
                if(((HypercomMSRSession)msrSession).isDeviceLocked())
                {
                    ((HypercomMSRSession)msrSession).deactivate();
                }
            }
            else
            {
/**
                if(!msrSession.isDeviceLocked())
                {
                    msrSession.deactivate();
                }
**/
            }
        }
        catch (DeviceException e)
        {
            if (msrSession != null)
            {
                //release the device
                DeviceModeIfc dm = new DeviceMode();
                dm.setDeviceSessionName(MSRSession.TYPE);
                dm.setDeviceModeName(MSRSession.MODE_RELEASED);
                msrSession.setDeviceMode(dm);
            }
            throw e;
        }
    }
    
    //---------------------------------------------------------------------
    /**
        Get the MSR model
        @return a MSR model
    **/
    //---------------------------------------------------------------------
    public MSRModel getMSRModel() throws DeviceException
    {
        MSRSession msrSession   = null;
        MSRModel msrModel   = null;
        // first test for external device
        msrSession = (MSRSession) getDeviceTechnician().getDeviceSession(ExternalMSRSession.TYPE);
        if (msrSession!=null)
        {
	        if (msrSession.getDeviceName().compareTo(IngenicoMSRSession.DEFAULT_DEVICE_NAME) == 0)
	        {
	            msrModel = (MSRModel) ((IngenicoMSRSession)msrSession).getMSRModel();
	        }
	        else if (msrSession.getDeviceName().compareTo(HypercomMSRSession.DEFAULT_DEVICE_NAME) == 0)
	        {
	            msrModel = (MSRModel) ((HypercomMSRSession)msrSession).getMSRModel();
	        }
	        else if (msrSession.getDeviceName().compareTo(MSRSession.DEFAULT_DEVICE_NAME) == 0)
	        {
	            msrModel = (MSRModel) ((MSRSession)msrSession).getMSRModel();
	        }
	        else if (msrSession.getDeviceName().compareTo(ExternalMSRSession.DEFAULT_DEVICE_NAME) == 0)
	        {
	            msrModel = (MSRModel) ((ExternalMSRSession)msrSession).getMSRModel();
	        }
	        else
	        {
	        	msrSession = (MSRSession) getDeviceTechnician().getDeviceSession(MSRSession.TYPE);
	            msrModel = (MSRModel) msrSession.getMSRModel();
	        }
        }
	    return msrModel;
    }

    //--------------------------------------------------------------------------
    /**
       Checks whether a MSR is online. <P>
       @return true if the MSR is online, false otherwise
    **/
    //--------------------------------------------------------------------------
    public Boolean isMSROnline()
    {
        boolean returnCode = false;
        try
        {
            DeviceTechnicianIfc dt = getDeviceTechnician();
            DeviceSessionIfc msrSession = dt.getDeviceSession(MSRSession.TYPE);
            String msrName = msrSession.getDeviceName();
            MSR msr = (MSR) msrSession.getDevice();

            if ((msr != null) && (msrName != null))
            {        
                try 
                {
                    if (msr.getState() == JposConst.JPOS_S_CLOSED)
                    {
                        msr.open(msrName);
                    }              
                    if (!msr.getClaimed())
                    {
                        msr.claim(10000);
                    }              
                    if (!msr.getAutoDisable())
                    {
                        msr.setAutoDisable(true);
                    }               
                    if (msr.getParseDecodeData())
                    {
                        msr.setParseDecodeData(false);
                    }              
                    if (!msr.getDecodeData())
                    {
                        msr.setDecodeData(true);
                    }              
                    msr.clearInput();               
                    if (msr.getClaimed())
                    {
                        msr.release();
                    }     
                    //This implies msr is Online         
                    returnCode = true;
                }
                catch (JposException je)
                {
                    //This implies msr is Offline
                }
            }
        }
        catch (DeviceException e)
        {
            //This implies msr is Offline
        }
        return (new Boolean(returnCode));
    }
}


