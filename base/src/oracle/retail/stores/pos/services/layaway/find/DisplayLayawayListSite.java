/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/DisplayLayawayListSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
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
 *    Rev 1.0   Aug 29 2003 16:00:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   23 Jul 2003 22:50:58   baa
 * fix local navigation focus
 * 
 *    Rev 1.1   Aug 30 2002 13:04:42   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:20:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:16   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:21:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find; 
 
// foundation imports 
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
//------------------------------------------------------------------------------
/** 
    Displays the layaway list screen. Used to select a layaway for payment,
    pickup, or delete.
    
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/ 
//------------------------------------------------------------------------------
 
public class DisplayLayawayListSite extends PosSiteActionAdapter 
{ 
    /** 
        class name constant 
    **/ 
    public static final String SITENAME = "DisplayLayawayListSite"; 
 
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
            Displays the layaway list screen. Used to select a layaway for 
            payment, pickup, or delete.
            <P> 
            @param bus the bus arriving at this site 
    **/ 
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus) 
    { 
        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();
        
        POSUIManagerIfc ui 
            = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        // Set the summary list in the bean model to display
        LayawaySummaryEntryIfc[] summaries = cargo.getLayawaySummaryEntryList();
        
        ListBeanModel model = new ListBeanModel();
        model.setListModel(summaries);
         
        if (summaries != null && summaries.length > 0)
        {
            NavigationButtonBeanModel navigation = new NavigationButtonBeanModel();
            //Setup focus for initial screen
            LayawaySummaryEntryIfc layaway = summaries[0];
                        
            // if the layaway status is NEW or ACTIVE, enable the buttons
            if(layaway.getStatus() == LayawayConstantsIfc.STATUS_NEW ||
               layaway.getStatus() == LayawayConstantsIfc.STATUS_ACTIVE)
            {
                navigation.setButtonEnabled(CommonActionsIfc.PAYMENT,true);
                navigation.setButtonEnabled(CommonActionsIfc.PICKUP,true);
                navigation.setButtonEnabled(CommonActionsIfc.DELETE,true);
            }
            else if (layaway.getStatus() == LayawayConstantsIfc.STATUS_EXPIRED)
            {
                navigation.setButtonEnabled(CommonActionsIfc.PAYMENT,false);
                navigation.setButtonEnabled(CommonActionsIfc.PICKUP,false);
                navigation.setButtonEnabled(CommonActionsIfc.DELETE,true);
            }
            else
            {
                navigation.setButtonEnabled(CommonActionsIfc.PAYMENT,false);
                navigation.setButtonEnabled(CommonActionsIfc.PICKUP,false);
                navigation.setButtonEnabled(CommonActionsIfc.DELETE,false);
            }
             model.setLocalButtonBeanModel(navigation);
        }
        
        // If we did a search by customer, we have the customer name to
        // display
        if (cargo.getCustomer() != null)
        {
            CustomerIfc customer = cargo.getCustomer();
            StatusBeanModel statusModel = new StatusBeanModel();
            statusModel.setCustomerName(customer.getCustomerName());
            model.setStatusBeanModel(statusModel);
        }
                    
        // Display the screen
        ui.showScreen(POSUIManagerIfc.LAYAWAY_LIST, model);
    }

} 
