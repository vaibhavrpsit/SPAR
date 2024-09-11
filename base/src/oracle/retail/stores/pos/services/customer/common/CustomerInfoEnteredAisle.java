/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CustomerInfoEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:55:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Mar 20 2003 18:18:44   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.2   Sep 20 2002 17:55:12   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Sep 18 2002 17:15:20   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:33:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:22   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:24:10   msg
 * Initial revision.
 * 
 *    Rev 1.3   15 Jan 2002 17:17:34   baa
 * fix defects
 * Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.2   15 Jan 2002 11:59:48   baa
 * fix minor error
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.1   11 Jan 2002 18:08:10   baa
 * update phone field
 * Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.0   Sep 21 2001 11:14:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;
// java imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
//--------------------------------------------------------------------------
/**
    This Aisle takes the CustomerInfoBeanModel and updates the customer in
    the cargo from it.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
abstract public class CustomerInfoEnteredAisle extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public  CustomerIfc updateCustomer(CustomerIfc customer, CustomerInfoBeanModel model)
    {

         return CustomerUtilities.updateCustomer(customer, model);

    }
 }
