/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/POSDeviceActionGroup.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:38 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:07 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/03/25 20:25:15  jdeleau
 *   @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *   See the scr for more info.
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
 *    Rev 1.0   Aug 29 2003 15:51:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.7   Jul 11 2003 15:32:56   baa
 * calculate space available for descriptions based on the space left after amounts are entered.
 * 
 *    Rev 1.6   Feb 07 2003 12:09:10   vxs
 * Relocated member variables that belong in individual device action groups
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 * 
 *    Rev 1.5   Jan 15 2003 16:31:44   vxs
 * Relocated HardTotals methods to newly created action group files for HardTotals
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 * 
 *    Rev 1.4   Jan 13 2003 15:42:38   vxs
 * Relocated majority of methods into specific device action group files.
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 * 
 *    Rev 1.3   Sep 10 2002 09:47:12   jriggins
 * Added get/set for new receiptLineSize property
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Aug 14 2002 18:16:00   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 07 2002 19:33:56   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:44:20   msg
 * Initial revision.
 * 
 *    Rev 1.2   16 Apr 2002 10:30:00   vxs
 * Calling activateSigCap()/deactivateSigCap() in beginCapture()/endCapture()
 * Resolution for POS SCR-1537: Unable to complete a sig capture after escaping from Sig Cap screen and retrying
 *
 *    Rev 1.1   Mar 18 2002 22:59:02   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:14:28   msg
 * Initial revision.
 *
 *    Rev 1.14   12 Mar 2002 17:55:12   jbp
 * deprecate methods for EYSPrintableDocument
 * Resolution for POS SCR-1553: cleanup dead code
 *
 *    Rev 1.13   04 Mar 2002 18:16:08   vxs
 * Added set/get for printBufferSize
 * Resolution for POS SCR-1442: Suspending a transaction with 123 chess sets causes the printer to stop responding, application stops as well
 *
 *    Rev 1.12   Feb 21 2002 08:37:04   mpm
 * Added ability to designate printer as franking-capable
 * Resolution for POS SCR-1134: Bypass franking when printer is not franking-capable
 *
 *    Rev 1.11   Feb 06 2002 07:43:50   mpm
 * Modified to detect simulated PIN pad session.
 * Resolution for POS SCR-1132: Disable Debit button when no PIN pad present
 *
 *    Rev 1.10   Dec 18 2001 10:47:14   mpm
 * Added support for receipt-printer-buffering setting.
 * Resolution for POS SCR-452: Make receipt-printer-buffering setting accessible from device script
 *
 *    Rev 1.9   Nov 07 2001 15:50:02   vxs
 * Modified LineDisplayItem() in POSDeviceActionGroup, so accommodating changes for other files as well.
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.8   26 Oct 2001 14:56:08   jbp
 * New receipt printing methodology
 * Resolution for POS SCR-221: Receipt Design Changes
 *
 *    Rev 1.7   Oct 22 2001 09:34:32   vxs
 * Removed formatDisplayInfo() because now we have formatTextData() in Util.java in foundation.
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.6   Oct 16 2001 15:29:08   vxs
 * Made format string method static
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.5   Oct 11 2001 18:33:22   vxs
 * Correct LogMessageConstantsIfc usage
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.3   Oct 09 2001 14:42:26   vxs
 * practicing checking out/in
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.2   Oct 09 2001 13:17:30   vxs
 * Practicing checking out/in
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.1   Oct 09 2001 13:12:06   vxs
 * Practicing checking out/in
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.0   Sep 21 2001 11:09:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;
// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
/**
 * 
 *  The <code>POSDeviceActionGroupIfc</code> defines the POS specific
 *  device operations available to POS applications.
 *  <p>
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 *  @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc
 *
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class POSDeviceActionGroup implements POSDeviceActionGroupIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7266079627210130402L;

    /**
     *  The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.device.POSDeviceActionGroup.class);

    /**
     * revision number supplied by source-code control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * Link back to device sessions
    **/
    protected DeviceTechnicianIfc deviceTechnician;
    /**
     * Name used by device technician
    **/
    protected String name;
     
    /**
     * Sets the device technician reference
     * @param dt Device technician to set
     * 
     * @throws DeviceException
     */
    public void setDeviceTechnician(DeviceTechnicianIfc dt) throws DeviceException
    {
        deviceTechnician = dt;
    }

    /**
     * Gets device technician reference.
     * 
     * @return DeviceTechnician
     * @throws DeviceException thrown if DeviceTechnician is null
     */
    public DeviceTechnicianIfc getDeviceTechnician() throws DeviceException
    {
        if (deviceTechnician == null)
        {
            throw new DeviceException("Unable to find DeviceTechnician in client session.");
        }
        return(deviceTechnician);

    }

    /**
     * Set the name of the action group
     * 
     * @param name Name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Get the name of the action group
     * 
     * @return name
     */
    public String getName()
    {
        if (name == null)
        {
            return(TYPE);
        }
        return(name);

    }
}

