/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/CustomersFoundSite.java /main/11 2012/05/31 18:40:56 acadar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    05/23/12 - CustomerManager refactoring
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:32  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:00  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:32:24   msg
 * Initial revision.
 * 
 *    Rev 1.2   21 Mar 2002 11:12:30   baa
 * Add Too Many Customers dialog
 * Resolution for POS SCR-568: Too Many Matches screen name and text errors not to spec
 *
 *    Rev 1.1   Mar 18 2002 23:12:46   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:25:32   msg
 * Initial revision.
 *
 *    Rev 1.4   21 Feb 2002 14:36:48   jbp
 * set first customer found to top list when found by emp ID
 * Resolution for POS SCR-1372: Selecting Customer to Delete on Customer Select when search done by Emp ID screen hangs application
 *
 *    Rev 1.3   18 Feb 2002 18:43:40   baa
 * save original customer info
 * Resolution for POS SCR-1242: Selecting 'Enter' on Duplicate ID screen in Customer returns the wrong information
 *
 *    Rev 1.2   25 Jan 2002 21:02:36   baa
 * partial fix ui problems
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.1   16 Nov 2001 10:33:40   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;
import java.util.Vector;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DualListBeanModel;
//--------------------------------------------------------------------------
/**
    Determines how to process the customers found by the search.
    $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class CustomersFoundSite extends PosSiteActionAdapter
{
    /**
     * 
     */
    private static final long serialVersionUID = 2809181071844815371L;
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";


    //----------------------------------------------------------------------
    /**
        Checks the number of customers found and does the appropriate action.
        <p>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void arrive(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo) bus.getCargo();

        // grab the customers returned
        CustomerIfc[] customerList = ((CustomerIfc[])cargo.getCustomerList().toArray());

        if (customerList.length == 1)
        {
            cargo.setCustomer(customerList[0]);
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {

            // setup model to display data using new UI format
            DualListBeanModel model = new DualListBeanModel();
            Vector topList = new Vector();

            if(cargo.getOriginalCustomer() != null)
            {
                topList.addElement(cargo.getOriginalCustomer());
            }
            else
            {
                topList.addElement(customerList[0]);
            }

            model.setTopListModel(topList);
            model.setListModel(cargo.getCustomerList());

            // Display the screen
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(cargo.getScreen(), model);
        }
    }


}
