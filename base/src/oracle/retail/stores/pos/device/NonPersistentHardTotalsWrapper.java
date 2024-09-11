/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/NonPersistentHardTotalsWrapper.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:38 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    dwfung    05/01/09 - no change.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:08 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:45 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:51:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Feb 16 2003 10:45:26   mpm
 * Initial revision.
 * Resolution for POS SCR-2053: Merge 5.1 changes into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;
// java imports
import java.io.Serializable;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.device.HardTotalsIfc;

//------------------------------------------------------------------------------
/**
    This class is a wraper for JPOS HardTotals class.  It is used in the cased
    where there is no persistence (device or hard disk) at the client.<p>

    The write() method does nothing and returns with no exception.  This allows
    the POS application to perform all its hardtotals writing actives without
    actually writing anyting to the ram disk.<p>

    The read() method always returns a failure.  This forces the POS application
    to always read store, register, till and sequence number state from the database.
    <P>

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class NonPersistentHardTotalsWrapper implements HardTotalsIfc
{
    public NonPersistentHardTotalsWrapper()
    {
    }

    //---------------------------------------------------------------------
    /**
        Updates POSHardTotals object. <P>
        @exception DeviceException if handle is not set
    **/
    //---------------------------------------------------------------------
    public void write(Serializable data) throws DeviceException
    {
        // Writes always succeed, even though they do nothing.
    }

    //---------------------------------------------------------------------
    /**
        Reads POSHardTotals object. <P>
        @exception DeviceException if handle is not set
    **/
    //---------------------------------------------------------------------
    public Object read() throws DeviceException
    {
        // Reads always fail.
        throw new DeviceException("Non Persistent Hard Totals always fail on read");
    }

}

