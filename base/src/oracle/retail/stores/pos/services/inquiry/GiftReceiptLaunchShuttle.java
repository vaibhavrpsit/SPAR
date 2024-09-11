/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/GiftReceiptLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:45 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:50:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:10  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:21:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Oct 2001 17:41:12   baa
 * cross store inventory feature
 * Resolution for POS SCR-230: Cross Store Inventory
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;


//------------------------------------------------------------------------------
/**

    @version $KW; $Ver; $EKW;
**/
//------------------------------------------------------------------------------

public class GiftReceiptLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7259334843894699751L;


    //--------------------------------------------------------------------------
    /**


            @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------

    public void load(BusIfc bus)
    {
    }

    //--------------------------------------------------------------------------
    /**


            @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------

    public void unload(BusIfc bus)
    {
    }
}
