/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CustomerContactDoneRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:19 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:55:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   May 27 2003 08:48:02   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.2   Mar 20 2003 18:18:44   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.1   Sep 18 2002 17:15:18   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:33:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:18   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:24:04   msg
 * Initial revision.
 * 
 *    Rev 1.2   11 Jan 2002 18:08:02   baa
 * update phone field
 * Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.1   10 Dec 2001 15:55:46   baa
 * Fix minor defects in customer
 * Resolution for POS SCR-99: Data on Customer Delete screen should be non-editable
 *
 *    Rev 1.0   Sep 21 2001 11:15:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
//--------------------------------------------------------------------------
/**
    Resets the link flag in the cargo and updates the customer contact
    information. <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerContactDoneRoad extends PosLaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Resets the link flag in the cargo and updates the customer contact
        information. <p>
        <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        cargo.setLink(false);
    }

}
