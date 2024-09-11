/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/MICRActionGroup.java /main/11 2011/01/21 15:18:52 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   01/21/11 - Fix for a MICR hang issue.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:02 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:30 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:36 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:48:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:30:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 21 2003 13:40:42   rsachdeva
 * Added Comment
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 * 
 *    Rev 1.0   Oct 20 2003 11:12:28   rsachdeva
 * Initial revision.
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

//jpos iports
import jpos.JposConst;
import jpos.JposException;
import jpos.MICR;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.MICRSession;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;

//--------------------------------------------------------------------------
/**
The <code>MICRActionGroup</code> defines the MICR specific
device operations available to POS applications.
<p>
@version $Revision: /main/11 $
@see oracle.retail.stores.pos.device.MICRActionGroupIfc
**/
//--------------------------------------------------------------------------
public class MICRActionGroup extends POSDeviceActionGroup implements MICRActionGroupIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 236250657065312058L;

    /**
       revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    //--------------------------------------------------------------------------
    /**
       Checks whether a MICR is online. <P>
       @return true if the MICR is online, false otherwise
    **/
    //--------------------------------------------------------------------------
    public Boolean isMICROnline()
    {
        boolean returnCode = false;
        try
        {
            DeviceTechnicianIfc dt = getDeviceTechnician();
            DeviceSessionIfc micrSession = dt.getDeviceSession(MICRSession.TYPE);
            String micrName = micrSession.getDeviceName();
            MICR micr = (MICR) micrSession.getDevice();    
            if ((micr != null) && (micrName != null))
            {        
                try 
                {
                    if (micr.getState() == JposConst.JPOS_S_CLOSED)
                    {
                        micr.open(micrName);
                    }              
                    if (!micr.getClaimed())
                    {
                        micr.claim(10000);
                    }              
                    if (!micr.getDeviceEnabled())
                    {
                        micr.setDeviceEnabled(true);
                    }                            
                    micr.clearInput();               
                    if (micr.getClaimed())
                    {
                        micr.release();
                    }       
                    //This implies that micr is online
                    returnCode = true;
                }
                catch (JposException je)
                {
                    //This implies that micr is offline
                }
            }
        }
        catch (DeviceException e)
        {
            //This implies that micr is offline
        }
        return (new Boolean(returnCode));
    }
}


