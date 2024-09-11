/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/FindCustomerInfoSite.java /main/11 2013/02/01 10:48:23 abhineek Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhineek  01/31/13 - Added preference field to CusotmerInfo screen
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:56:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Jun 25 2003 16:00:10   baa
 * remove default state setting for customer info lookup
 * 
 *    Rev 1.2   Apr 03 2003 14:14:16   baa
 * rename business customer classes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.1   Mar 20 2003 18:18:52   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.0   Apr 29 2002 15:32:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:54   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:38   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Jan 2002 21:02:42   baa
 * partial fix ui problems
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.1   16 Nov 2001 10:33:56   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.common.EnterCustomerInfoSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
//--------------------------------------------------------------------------
/**
    Put up Customer Info screen for input of customer name and address
    information.
    $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class FindCustomerInfoSite extends EnterCustomerInfoSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------
    /**
        Displays the Customer Info screen for input of search criteria
        of a customer. <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
          // model to use for the UI
           
          UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
          ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE); 
          CustomerInfoBeanModel model = new CustomerInfoBeanModel();
          model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));
          model.setCountries(utility.getCountriesAndStates(pm));
          model.setReceiptModes(CustomerUtilities.getReceiptPreferenceTypes(utility));
          //State info is not used for customer lookup.
          model.setStateIndex(-1);

          // show the screen
          POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

          uiManager.showScreen(POSUIManagerIfc.FIND_CUSTOMER_INFO, model);
    }


}
