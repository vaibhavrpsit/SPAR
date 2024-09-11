/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/LinkCustomerIDAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:12  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 27 2003 08:48:04   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 15:33:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:40   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:24:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   28 Nov 2001 17:46:30   baa
 * fix cancel for offline
 * Resolution for POS SCR-199: Cust Offline screen returns to Sell Item instead of Cust Opt's
 *
 *    Rev 1.0   Sep 21 2001 11:15:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    Aisle to traverse if the database is offline and the customer ID
    should be linked.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LinkCustomerIDAisle extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Sets the cargo to link the customer ID. <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        
        cargo.setCustomer(DomainGateway.getFactory().getCustomerInstance());
        cargo.getCustomer().setCustomerID(cargo.getCustomerID());
        cargo.getCustomer().setLastName(cargo.getCustomerID());
        cargo.getCustomer().setCustomerName(cargo.getCustomerID());
        cargo.setLink(true);

        bus.mail(new Letter(CommonLetterIfc.OFFLINE), BusIfc.CURRENT);
    }
}
