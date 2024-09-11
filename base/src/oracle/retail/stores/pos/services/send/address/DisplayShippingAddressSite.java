/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/address/DisplayShippingAddressSite.java /main/12 2014/01/24 11:48:10 abananan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abananan  01/24/14 - Populate Receipt Preference values in model.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         8/8/2007 6:04:23 PM    Maisa De Camargo
 *         Removed the Logic that disables the UNDO Button in the Shipping
 *         Address Screen.
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.12  2004/09/16 20:05:09  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.11  2004/06/21 13:16:07  lzhao
 *   @scr 4670: cleanup
 *
 *   Revision 1.10  2004/06/19 14:06:14  lzhao
 *   @scr 4670: integrate with capture customer
 *
 *   Revision 1.9  2004/06/16 21:44:15  lzhao
 *   @scr 4670: add dialog, update phone, state, country
 *
 *   Revision 1.8  2004/06/16 13:42:07  lzhao
 *   @scr 4670: refactoring Send for 7.0.
 *
 *   Revision 1.7  2004/06/07 23:02:00  lzhao
 *   @scr 4670: add business name.
 *
 *   Revision 1.6  2004/06/04 20:23:44  lzhao
 *   @scr 4670: add Change send functionality.
 *
 *   Revision 1.5  2004/05/27 16:35:55  rsachdeva
 *   @scr 4670 Send: Multiple Sends
 *
 *   Revision 1.4  2004/05/11 19:06:52  rsachdeva
 *   @scr 4670 Send: Multiple Sends
 *
 *   Revision 1.3  2004/02/12 16:51:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.9   Jul 19 2003 10:48:46   baa
 * changes to shipping address screen get lost upon return to the screen when data validation failed.
 * Resolution for 3159: Send Transaction- Modifying Customer or Adding New Customer unable to select Canadian Province.
 * 
 *    Rev 1.8   May 06 2003 13:41:10   baa
 * updates for business customer
 * Resolution for POS SCR-2203: Business Customer- unable to Find previous entered Busn Customer
 * 
 *    Rev 1.7   Apr 16 2003 12:22:58   baa
 * defect fixes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.6   Apr 11 2003 13:19:10   baa
 * remove deprecations
 * Resolution for POS SCR-2155: Deprecation warnings - EmployeeIfc
 * 
 *    Rev 1.5   Apr 08 2003 12:56:04   baa
 * I18n phone types
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.4   Feb 21 2003 09:35:34   baa
 * Changes for contries.properties refactoring
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.3   Feb 20 2003 14:23:06   RSachdeva
 * Code Conversion Clean Up as per Coding Standards
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Sep 19 2002 09:46:58   baa
 * use new method for getting country/state info
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Sep 13 2002 10:34:38   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:04:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:06   msg
 * Initial revision.
 * 
 *    Rev 1.4   03 Jan 2002 14:22:36   baa
 * cleanup code
 * Resolution for POS SCR-520: Prepare Send code for review
 *
 *    Rev 1.3   13 Dec 2001 18:00:04   baa
 * updates to support offline
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.2   12 Dec 2001 17:25:40   baa
 * updates for  journaling send feature
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.1   06 Dec 2001 18:48:46   baa
 * additional updates for  send feature
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.0   04 Dec 2001 17:23:00   baa
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send.address;


import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the customer shipping address screen.
    <P>
    $Revision: /main/12 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class DisplayShippingAddressSite extends PosSiteActionAdapter
{
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     invalid postal code resource id
     **/
    public static final String INVALID_POSTAL_CODE = "InvalidPostalCode";
    //--------------------------------------------------------------------------
    /**
        Displays the layaway customer screen.
        <P>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        SendCargo           cargo   = (SendCargo)bus.getCargo();
        POSUIManagerIfc     ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc   utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm      = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        
        ShippingMethodBeanModel model =  new ShippingMethodBeanModel();;
        // Only Setting That which is needed to display the Shipping Address Screen
        CustomerIfc shipCustomer = cargo.getShipToInfo();
        if ( shipCustomer != null )
        {
            model = (ShippingMethodBeanModel)CustomerUtilities.populateCustomerInfoBeanModel(shipCustomer, utility, pm, model);
        }
        model.setCountries(utility.getCountriesAndStates(pm));
        model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));
        model.setReceiptModes(CustomerUtilities.getReceiptPreferenceTypes(utility));
        // set the customer's name in the status area
        CustomerIfc billingCustomer = cargo.getCustomer();
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName(billingCustomer.getFirstLastName());
        model.setStatusBeanModel(statusModel);
        NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
        model.setGlobalButtonBeanModel(globalModel);
        ui.showScreen(POSUIManagerIfc.SHIPPING_ADDRESS, model);
    }

}
