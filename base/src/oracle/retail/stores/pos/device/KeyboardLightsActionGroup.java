/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/KeyboardLightsActionGroup.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:38 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:12 PM  Robert Pearse   
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
 *    Rev 1.0   Dec 04 2003 16:27:54   rzurga
 * Initial revision.
 * 
 *    Rev 1.0   Oct 20 2003 09:29:04   rsachdeva
 * Initial revision.
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

//jpos imports
//import jpos.POSKeyboard;
//import jpos.JposConst;
//import jpos.JposException;

//foundation imports
//import oracle.retail.stores.foundation.manager.device.DeviceException;
//import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;
//import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
//import oracle.retail.stores.foundation.manager.device.POSKeyboardSession;

//utility imports
import jpos.JposException;

import oracle.retail.stores.pos.platform.OS4690.KeyboardLights;

//--------------------------------------------------------------------------
/**
The <code>KeyboardLightsActionGroup</code> defines the Scanner specific
device operations available to POS applications.
<p>
@version $Revision: /rgbustores_13.4x_generic_branch/1 $
@see oracle.retail.stores.pos.device.KeyboardLightsActionGroupIfc
**/
//--------------------------------------------------------------------------
public class KeyboardLightsActionGroup extends POSDeviceActionGroup implements KeyboardLightsActionGroupIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4947921358771310456L;

   /**
      revision number supplied by source-code-control system
   **/
   public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
   
   protected int lightsTimeout                         = 2500;
   protected static KeyboardLights keyboardLights     = null;
   //--------------------------------------------------------------------------
   /**
      Checks makes sure the Wait keyboard light is off. <P>
      @return true if the switched the light off, false otherwise
   **/
   //--------------------------------------------------------------------------
    public void turnWaitLightOff()
    {
            
        try
        {
            // Create and start the keyboard WAIT light extinguisher
            keyboardLights = new KeyboardLights(lightsTimeout);  
        } catch (JposException je)
        {
        }
    }
/**
 * @return
 */
    public int getLightsTimeout() {
        return lightsTimeout;
    }
    
    /**
     * @param i
     */
    public void setLightsTimeout(int i) {
        lightsTimeout = i;
    }

}


