/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/find/CustomerFindCheckReturnLetterSite.java /main/11 2012/05/31 18:40:57 acadar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    05/31/12 - removed unused method
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:21 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:27  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:41:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:32:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:14   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:24:58   msg
 * Initial revision.
 * 
 *    Rev 1.2   21 Dec 2001 12:40:48   baa
 * deprecate old classes
 * Resolution for POS SCR-478: Deprecate unused customer classes
 *
 *    Rev 1.1   23 Oct 2001 16:53:08   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:15:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.find;

// foundation imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
//--------------------------------------------------------------------------
/**
    This site Check if an override return letter was set and sends the
    approprate letter
    <p>
    @version $Revision: /main/11 $
    @deprecated As of release 5.0.0
**/
//--------------------------------------------------------------------------
public class CustomerFindCheckReturnLetterSite extends PosSiteActionAdapter
{
    //--------------------------------------------------------------------------
    /**
     *   Revision Number furnished by TeamConnection. <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------
    /**
     *   Displays the ITEM_OPTIONS screen.
     *   <P>
     *   @param  bus     Service bus.
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        Letter   letter           = null;

        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        letter = new Letter("default");

       
        bus.mail(letter, BusIfc.CURRENT);
    }
}
