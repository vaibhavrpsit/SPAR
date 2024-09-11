/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/ScannerActionGroup.java /rgbustores_13.4x_generic_branch/2 2011/07/01 09:04:17 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     07/01/11 - Chaged check for online to leave the scanner in the
 *                         same state on exit as upon entry for BUG_ID 335.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:04 PM  Robert Pearse   
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
 *    Rev 1.1   Nov 21 2003 13:55:10   rsachdeva
 * Added Comment
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 * 
 *    Rev 1.0   Oct 20 2003 09:29:04   rsachdeva
 * Initial revision.
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

//jpos imports
import jpos.JposConst;
import jpos.JposException;
import jpos.Scanner;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.ScannerSession;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;

//--------------------------------------------------------------------------
/**
The <code>ScannerActionGroup</code> defines the Scanner specific
device operations available to POS applications.
<p>
@version $Revision: /rgbustores_13.4x_generic_branch/2 $
@see oracle.retail.stores.pos.device.ScannerActionGroupIfc
**/
//--------------------------------------------------------------------------
public class ScannerActionGroup extends POSDeviceActionGroup implements ScannerActionGroupIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1234641171669729694L;

   /**
      revision number supplied by source-code-control system
   **/
   public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";
   //--------------------------------------------------------------------------
   /**
      Checks whether a Scanner is online. <P>
      @return true if the scanner is online, false otherwise
   **/
   //--------------------------------------------------------------------------
    public Boolean isScannerOnline()
    {
        boolean returnCode = false;
        boolean notOpened = false;
        boolean notClaimed = false;
        boolean notEnabled = false;
        
        try
        {
            DeviceTechnicianIfc dt = getDeviceTechnician();
            DeviceSessionIfc scannerSession = dt.getDeviceSession(ScannerSession.TYPE);
            String scannerName = scannerSession.getDeviceName();
            Scanner scanner = (Scanner) scannerSession.getDevice();  
         
            if ((scanner != null) && (scannerName != null))
            {        
                try 
                {
                    if (scanner.getState() == JposConst.JPOS_S_CLOSED)
                    {
                        notOpened = true;
                        scanner.open(scannerName);
                    }               
                    if (!scanner.getClaimed())
                    {
                        notClaimed = true;
                        scanner.claim(10000);
                    }             
                    if (!scanner.getDeviceEnabled())
                    {
                        notEnabled = true;
                        scanner.setDeviceEnabled(true);
                    }       
                    // Leave in same state as found
                    if(notEnabled && scanner.getDeviceEnabled())
                    {
                        scanner.setDeviceEnabled(false);
                    }
                    if(notClaimed && scanner.getClaimed())
                    {
                        scanner.release();
                    }
                    if(notOpened && scanner.getState() != JposConst.JPOS_S_CLOSED)
                    {
                        scanner.close();
                    }
                    //This implies that scanner is Online
                    returnCode = true;
                }
                catch (JposException je)
                {
                    //This implies that scanner is Offline                
                }
            }
        }
        catch (DeviceException e)
        {    
            //This implies that scanner is Offline
        }
        return (new Boolean(returnCode));
    }
}


