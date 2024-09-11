/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/BusinessInfoLinkAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:27:18 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:19:54 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:09:42 PM  Robert Pearse   
 * $
 * Revision 1.4  2004/03/03 23:15:06  bwf
 * @scr 0 Fixed CommonLetterIfc deprecations.
 *
 * Revision 1.3  2004/02/12 16:49:25  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:40:12  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   11 Jul 2003 01:15:18   baa
 * fix security overwrite flow
 * 
 *    Rev 1.2   Jun 27 2003 18:14:32   baa
 * fix discounts for business customer
 * Resolution for 2728: Linking a Business Customer with a Discount (Gold) Discount is not applied.
 * Resolution for 2741: Return layaway item with receipt, customer discount is not counted for refund
 * 
 *    Rev 1.1   Apr 11 2003 14:32:22   baa
 * delete business customer classes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.0   Apr 03 2003 15:22:20   baa
 * Initial revision.
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.4   Mar 26 2003 16:41:42   baa
 * fix minor bugs with customer refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.3   Mar 26 2003 10:42:46   baa
 * add changes from acceptance test
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.2   Oct 09 2002 14:52:30   kmorneau
 * final postal check
 * Resolution for 1814: Customer find by BusinessInfo crashes POS
 * 
 *    Rev 1.1   Oct 09 2002 11:25:02   kmorneau
 * flow and postal code check changes
 * Resolution for 1814: Customer find by BusinessInfo crashes POS
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// java imports
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    This Aisle takes the BusCustomerInfoBeanModel and updates the customer in
    the cargo from it.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated as of release 6.0 replaced by CustomerContactDoneRoad
**/
//--------------------------------------------------------------------------
public class BusinessInfoLinkAisle extends PosLaneActionAdapter
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
        String letterName = CommonLetterIfc.CONTINUE;
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        cargo.setLink(true);
        //journalInfo(cargo);

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

}
