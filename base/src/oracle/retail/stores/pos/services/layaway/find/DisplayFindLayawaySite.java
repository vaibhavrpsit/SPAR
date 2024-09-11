/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/DisplayFindLayawaySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/06/11 20:16:46  mng
 *   Offline Payment screen (layaway) disables Esc Undo after initial Esc and Esc is enabled. SCR2818
 *
 *   Revision 1.3  2004/02/12 16:50:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 15 2003 11:10:24   rsachdeva
 * Customer Name for Status Model
 * Resolution for POS SCR-2579: Customer name shows on Find Layaway Customer screen
 *
 *    Rev 1.0   Aug 29 2003 16:00:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 30 2002 13:01:38   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:20:32   msg
 * Initial revision.
 *
 *    Rev 1.1   26 Mar 2002 12:29:50   dfh
 * disable Undo key if layaway transaction in progress, in cargo...
 * Resolution for POS SCR-644: Canceled Layaway Delete EJ entry has undefined line of info
 *
 *    Rev 1.0   Mar 18 2002 11:35:06   msg
 * Initial revision.
 *
 *    Rev 1.1   08 Feb 2002 16:37:34   jbp
 * keep customer on transaction when escaping from layaway find
 * Resolution for POS SCR-995: Escaping from Layaway List causes customer to unlink and disables Find button on Layaway Options
 *
 *    Rev 1.0   Sep 21 2001 11:21:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

//foundation imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the menu screen for finding layaway(s).
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayFindLayawaySite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "DisplayFindLayawaySite";

    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
     /**
       Customer name bundle tag
     **/
     protected static final String CUSTOMER_NAME_TAG = "CustomerName";
     /**
       Customer name default text
     **/
     protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    //--------------------------------------------------------------------------
    /**
    Displays the layaway options screen. Ensures the customer name
    field in the status area is blank.
    <P>
    @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =
                        (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();

        NavigationButtonBeanModel globalNavigationModel = new NavigationButtonBeanModel();

        // Update the status panel to clear any customer name
        POSBaseBeanModel beanModel = (POSBaseBeanModel)ui.getModel();

        beanModel.setGlobalButtonBeanModel(globalNavigationModel);

        // if there is a customer in cargo set to status bean
        CustomerIfc customer = cargo.getCustomer();
        if(customer != null)
        {
            // Create the customer name string from the bundle.
            UtilityManagerIfc utility =
              (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            Object parms[] = { customer.getFirstName(), customer.getLastName() };
            String pattern =
              utility.retrieveText("CustomerAddressSpec",
                                   BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                                   CUSTOMER_NAME_TAG,
                                   CUSTOMER_NAME_TEXT);
            String customerName =
              LocaleUtilities.formatComplexMessage(pattern, parms);


            ui.customerNameChanged(customerName);
        }
        else
        {
            ui.customerNameChanged("");
        }

        ui.showScreen(POSUIManagerIfc.FIND_LAYAWAY, beanModel);
    }
}
