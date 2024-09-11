/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/OnlineDeviceStatusListener.java /main/11 2013/06/04 17:39:14 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:52:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:47:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:54   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:33:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:18:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import java.util.Hashtable;

import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Allows other beans to get the latest device status.
 * 
 * @version $Revision: /main/11 $
 */
public interface OnlineDeviceStatusListener
{
    /**
     * Passes a map of online/offline flags keyed by device. Online is true,
     * offline is false. See STATUS constants in {@link POSUIManagerIfc} for
     * possible keys.
     * 
     * @param onlineStatusHash
     */
    public void onlineDeviceStatusChanged(Hashtable<Integer,Boolean> onlineStatusHash);
}
