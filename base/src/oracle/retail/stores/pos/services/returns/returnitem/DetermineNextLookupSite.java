/* ===========================================================================
* Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header$
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// foundation imports
import oracle.retail.stores.domain.stock.ItemSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc.ItemLookupType;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Checks for external order processing.
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class DetermineNextLookupSite extends PosSiteActionAdapter
{
    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";

    /**
     * Constant for UIN search letter
     */
    public static final String UIN_SEARCH = "UINSearch";

    /**
     * Constant for enter item letter
     */
    public static final String ITEM_NOT_FOUND = "ItemNotFound";
    
    //----------------------------------------------------------------------
    /**
       The tour can arrive at this site a maximum of three times for any
       one item lookup.
       <p>
       1. After attempting to lookup and not finding the item in the store DB.
       <p>
       2. After attempting to lookup and not finding the item in SIM.  This
       lookup may or may not be called depending on configuration.
       <p>
       3. After attempting to lookup and not finding the item in the Web Store.  
       This lookup may or may not be called depending on configuration.
       <p>
       This class determines the next lookup that should be called; if all
       configured lookups fail to find the item, it mails "ItemNotFound"
       letter. 
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        String letter = null;
        ReturnItemCargoIfc cargo   = (ReturnItemCargoIfc)bus.getCargo();
        
        // Get the next lookup type. 
        ItemLookupType lookupType = determineTheNextLookupType(cargo); 

        // Verify the there is another configured location to search.
        // If not, reset the location to search the store, and set
        // the letter to "ItemNotFound".
        if (isSearchComplete(bus, cargo, lookupType))
        {
            cargo.setItemLookupLocaction(ReturnItemCargoIfc.ItemLookupType.STORE);
            letter = ITEM_NOT_FOUND;
        }
        else
        {
            // In order to get here, one these two conditions must be true. If the SIM lookup type is
            // set and enabled, set the letter to search SIM.
            if (lookupType.equals(ReturnItemCargoIfc.ItemLookupType.UIN) && isUINEnabled(bus))
            {
                letter = UIN_SEARCH;
            }
            else
            {
                // Otherwise, ask the user if they want to look in the web store.
                cargo.setItemLookupLocaction(ReturnItemCargoIfc.ItemLookupType.WEB);
                showWebServiceDialog(bus, cargo.getSearchCriteria());
            }
        }
        
        // If there is a letter, mail it.
        if (letter != null)
        {
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
    }

    /**
     * Determine the next possible lookup type based on the current lookup type,
     * set it on the cargo, and return it to the caller.
     * @param cargo
     * @return the lookup type
     */
    protected ItemLookupType determineTheNextLookupType(ReturnItemCargoIfc cargo)
    {
        ItemLookupType lookupType = cargo.getItemLookupLocaction();
        if (lookupType.equals(ReturnItemCargoIfc.ItemLookupType.STORE))
        {
            lookupType  = ReturnItemCargoIfc.ItemLookupType.UIN;
            cargo.setItemLookupLocaction(lookupType);
        }
        else
        if (lookupType.equals(ReturnItemCargoIfc.ItemLookupType.UIN))
        {
            lookupType  = ReturnItemCargoIfc.ItemLookupType.WEB;
            cargo.setItemLookupLocaction(lookupType);
        }
        else
        if (lookupType.equals(ReturnItemCargoIfc.ItemLookupType.WEB))
        {
            lookupType  = ReturnItemCargoIfc.ItemLookupType.STORE;
            cargo.setItemLookupLocaction(lookupType);
        }
        return lookupType;
    }

    /**
     * Determines if more search locations are available.
     * @param bus
     * @param cargo
     * @param lookupType
     * @return true if more search locations are available.
     */
    protected boolean isSearchComplete(BusIfc bus, ReturnItemCargoIfc cargo,
            ItemLookupType lookupType)
    {
        boolean isComplete  = true;
        boolean isXchannelEnabled = isXChannelEnabled();
        
        // If the SIM search is available, do that lookup.
        if (lookupType.equals(ReturnItemCargoIfc.ItemLookupType.UIN) && isUINEnabled(bus))
        {
            isComplete = false;
        }
        else
        // UIN is the selected type, but the web store search is available, move on to
        // that lookup.
        if (lookupType.equals(ReturnItemCargoIfc.ItemLookupType.UIN) && isXchannelEnabled)
        {
            isComplete = false;
        }
        else
        // If the web store search is available, do that lookup.
        if (lookupType.equals(ReturnItemCargoIfc.ItemLookupType.WEB) && isXchannelEnabled)
        {
            isComplete = false;
        }
            
        return isComplete;
    }

    /**
     * Determines if UIN is enabled.
     * @param bus
     * @return true UIN is enabled
     */
    protected boolean isUINEnabled(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        return (utility.getIMEIProperty() && utility.getSerialisationProperty());
    }

    /**
     * Determines is XChannel is enabled
     * @return true if XChannel is enabled
     */
    protected boolean isXChannelEnabled()
    {
        return Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, "XChannelEnabled", false);
    }

    /**
     *   Displays webService Dialog
     *   @param BusIfc
     *   @param ItemSearchCriteriaIfc
     */
    protected void showWebServiceDialog(BusIfc bus, ItemSearchCriteriaIfc inquiry)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the choice dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("ItemNotFoundInStore");
        String msg[] = new String[1];
        msg[0] = inquiry.getItemNumber();
        model.setArgs(msg);
        
        model.setType(DialogScreensIfc.SEARCHWEBSTORE_CANCEL);
        model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_WEBSTORE, CommonLetterIfc.SEARCH);
        model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, ITEM_NOT_FOUND);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
