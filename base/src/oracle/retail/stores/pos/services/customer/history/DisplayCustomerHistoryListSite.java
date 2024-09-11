/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/history/DisplayCustomerHistoryListSite.java /rgbustores_13.4x_generic_branch/2 2011/07/14 12:11:51 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   07/14/11 - Display only linked customer details (Bug 12686871)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse   
 *
 *   Revision 1.5.2.1  2005/01/14 19:26:20  bwf
 *   @scr 7869 Moved fix for 6837 into ReadCustomerHistorySite so that we can check whether to history list at all.
 *
 *   Revision 1.5  2004/08/16 22:02:54  jdeleau
 *   @scr 6837 Filter out transaction types that don't belong (suspended, cancelled, and postvoided)
 *
 *   Revision 1.4  2004/07/30 22:41:55  jdeleau
 *   @scr 2392 Make sure transactions are sorted as they should be.
 *
 *   Revision 1.3  2004/02/12 16:49:31  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:44:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Mar 03 2003 16:31:54   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.3   Jan 27 2003 12:52:12   bwf
 * Created a depart method to check if the current letter was cancel and then stop displaying customer name.
 * Resolution for 1937: Cancelling from History List displays updated cust. name in Sell Item
 * 
 *    Rev 1.2   Aug 14 2002 11:16:12   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 18 2002 15:18:48   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:32:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:34   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:16   msg
 * Initial revision.
 * 
 *    Rev 1.3   Jan 19 2002 10:28:12   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.2   16 Nov 2001 10:33:08   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.1   05 Nov 2001 17:36:54   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   19 Oct 2001 15:54:24   baa
 * Initial revision.
 * Resolution for POS SCR-209: Customer History
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.history;

// foundation imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//------------------------------------------------------------------------------
/**

    $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//------------------------------------------------------------------------------

public class DisplayCustomerHistoryListSite extends SiteActionAdapter
{

    //--------------------------------------------------------------------------
    /**
     *      Displays customer history list
            @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
      // Create the model and set the data
        ReturnCustomerCargo cargo = (ReturnCustomerCargo) bus.getCargo();
        ListBeanModel model = new  ListBeanModel();
        ArrayList summaries = new ArrayList(Arrays.asList(cargo.getTransactionSummary()));
        Collections.sort(summaries);        
        model.setListModel(summaries.toArray(new TransactionSummaryIfc[0]));

        // Display the screen
        // Display customer name only if it is a linked customer
        String customerName = new String("");
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        if (cargo.isLinkCustomer() && cargo.getPreviousCustomer() != null)
        {
            String[] vars = { cargo.getPreviousCustomer().getFirstName(), cargo.getPreviousCustomer().getLastName() };
            String pattern = utility.retrieveText("CustomerAddressSpec", BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                    TagConstantsIfc.CUSTOMER_NAME_TAG, TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG);
            customerName = LocaleUtilities.formatComplexMessage(pattern, vars);
        }

        CustomerCargo.displayCustomerName(bus, customerName);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.HISTORY_LIST, model);

    }
    
    //--------------------------------------------------------------------------
    /**
     *      Displays customer history list
            @param bus the bus departing at this site
    **/
    //--------------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        String letterName = bus.getCurrentLetter().getName();
        if(letterName.equals("Cancel"))
        {   
            CustomerCargo.displayCustomerName(bus,"");           
        }
    }


}
