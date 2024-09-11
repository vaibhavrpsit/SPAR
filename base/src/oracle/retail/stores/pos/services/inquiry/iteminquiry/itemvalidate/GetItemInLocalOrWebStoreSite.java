/*===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemvalidate/GetItemInLocalOrWebStoreSite.java /main/3 2014/06/11 13:22:16 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
*    jswan     06/10/14 - Modified to support retrieving extended data for
*                         instore items.
*    jswan     06/11/13 - Modified to fix interaction between Webstore and SIM
*                         item lookup flows.
*    jswan     01/09/13 - Added to retrieve items, regardless of location,
*                         using the item manager.
* ===========================================================================
*/

package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemvalidate;

import java.util.ArrayList;

import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemKit;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class GetItemInLocalOrWebStoreSite extends PosSiteActionAdapter
{
    
    private static final long serialVersionUID = -686797107976490489L;

    /**
     * Letter for one item found
     */
    public static final String LETTER_ONE_ITEM_FOUND = "OneItemFound";
    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";

    public void arrive(BusIfc bus)
    {
        // letter to be sent
        String   letter  = null;
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        
        // get item inquiry from cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        SearchCriteriaIfc inquiry = (SearchCriteriaIfc) cargo.getInquiry();
        cargo.resetInvalidFieldCounter();
        ArrayList<PLUItemIfc> errors = new ArrayList<PLUItemIfc>();

        try
        {
            ItemManagerIfc mgr = (ItemManagerIfc)bus.getManager(ItemManagerIfc.TYPE);
            boolean retrieveExtendedDataOnLocalPLULookup = Gateway.getBooleanProperty(
                    Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedDataOnLocalPLULookup", false);
            inquiry.setRetrieveExtendedDataOnLocalPLULookup(retrieveExtendedDataOnLocalPLULookup);
            int maxRecommendedItemsListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxRecommendedItemsListSize", "100"));
            inquiry.setMaxRecommendedItemsListSize(maxRecommendedItemsListSize);
            PLUItemIfc pluItem = mgr.getPluItem(inquiry);
            
            // If this tour was launched to retrieve a related item, do not make
            // the check for saleable status 
            if (!cargo.isRelatedItem())
            {
                if (pluItem.isKitHeader())
                {
                    if (!isItemKitAuthForSale((ItemKit)pluItem))
                    {
                        errors.add(pluItem);
                    }
                }
                else if (!isItemAuthForSale(pluItem))
                {
                    errors.add(pluItem);
                }
            }
            
            // If there are errors, display the dialog
            if (errors.size() > 0)
            {
                showErrorDialog(bus);
            }
            else
            {
                cargo.setItemFromWebStore(!inquiry.isRetrieveFromStore());
                cargo.setPLUItem(pluItem);
                cargo.setItemList(null);
                letter = LETTER_ONE_ITEM_FOUND;
            }
        }
        catch (DataException de)
        {
            logger.warn("ItemNo: "    + inquiry.getItemNumber() + 
                     " \nItem Desc: " + inquiry.getDescription() + 
                     " \nItem Dept: " + inquiry.getDepartmentID() + "", de);

            cargo.setDataExceptionErrorCode(de.getErrorCode());
            switch(de.getErrorCode())
            {
                case DataException.ADVANCED_PRICING_INFO_NOT_FOUND_ERROR:
                    letter = CommonLetterIfc.DATERANGE;
                    break;
                    
                case DataException.NO_DATA:
                    
                    // If no item was found determine the next step.
                    boolean isOffLine    = isOffLine(de.getErrorCodeExtended());
                    boolean isXChannel   = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, "XChannelEnabled", false);
                    boolean isSimEnabled = isSimLookupEnabled(bus);

                    // If cross channel is enabled...
                    if (isXChannel)
                    {
                        // and the server is online and the inquiry was looking at the local store...
                        if (!isOffLine && inquiry.isRetrieveFromStore())
                        {
                            // If SIM lookup is enabled, add the Serial Number button to the dialog.
                            if (isSimEnabled)
                            {
                                showWebOrInventorySearchDialog(bus, inquiry);
                            }
                            else // Otherwise, just add the WebStore and Cancel buttons.
                            {
                                showWebSearchDialog(bus, inquiry);
                            }
                        }
                        else
                        {
                            letter =  CommonLetterIfc.RETRY;
                        }
                    }
                    else
                    {
                        // Okay, no Cross Channel, so check for SIM lookup
                        if (isSimEnabled && !isOffLine)
                        {
                            letter =  CommonLetterIfc.SERIAL_NUMBER;
                        }
                        else
                        {
                            letter =  CommonLetterIfc.RETRY;
                        }
                    }                
                    break;
                    
                default:
                    String msg[] = new String[1];
                    msg[0] = utility.getErrorCodeString(de.getErrorCode());
                    showErrorDialog(bus,"DatabaseError",msg, "Invalid");
                    break;
            }
        }
        finally
        {
            inquiry.setRetrieveFromStore(true);
        }
        
        /*
         * Proceed to the next site
         */
        if (letter !=null)
        {
           bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
        
    }
    
    //----------------------------------------------------------------------
    /**
     *   Displays error Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showErrorDialog(BusIfc bus, String id, String[] args, String letter)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID(id);
        if (args != null)
        {
           model.setArgs(args);
        }
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK,letter);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    /**
     * Displays error Dialog
     * 
     * @param bus
     */
    private void showErrorDialog(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("ItemNotAuthForSale");
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    //----------------------------------------------------------------------
    /**
     *   Displays webService Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showWebSearchDialog(BusIfc bus, SearchCriteriaIfc inquiry)
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
        model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.RETRY);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //----------------------------------------------------------------------
    /**
     *   Displays webService Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showWebOrInventorySearchDialog(BusIfc bus, SearchCriteriaIfc inquiry)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the choice dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("SearchWebstoreSimInquiry");
        String msg[] = new String[1];
        msg[0] = inquiry.getItemNumber();
        model.setArgs(msg);
        
        model.setType(DialogScreensIfc.SEARCHWEB_SIM_CANCEL);
        model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_WEBSTORE, CommonLetterIfc.SEARCH);
        model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_SIM, CommonLetterIfc.SERIAL_NUMBER);
        model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.RETRY);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    /**
     * Check the kit item is authorized for sale
     * 
     * @param itemKit ItemKit
     * @return boolean true if it is authorized, otherwise return false
     */
    protected boolean isItemKitAuthForSale(ItemKit itemKit)
    {
        KitComponentIfc kitComponents[] = itemKit.getComponentItems();
        if (kitComponents != null)
        {
            for (int i = 0; i < kitComponents.length; i++)
            {
                if (kitComponents[i].isKitHeader())
                {
                    if (!isItemKitAuthForSale((ItemKit)kitComponents[i]))
                    {
                        return false;
                    }
                }
                else
                {
                    if (!isItemAuthForSale(kitComponents[i]))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check the item is authorized for sale
     * 
     * @param pluItem PLUItemIfc
     * @return boolean true if it is authorized, otherwise return false
     */
    protected boolean isItemAuthForSale(PLUItemIfc pluItem)
    {
        ItemClassificationIfc classification = pluItem.getItemClassification();
        if (classification != null && !classification.isAuthorizedForSale())
        {
            return false;
        }
        
        return true;
    }

    /**
     * Determines if extended code indicates the repository is off line.
     * @param extendedCode
     * @return true if off line
     */
    protected boolean isOffLine(int extendedCode)
    {
        boolean isOffLine = false;
        if (DataException.ERROR_CODE_EXTENDED_OFFLINE == extendedCode)
        {
            isOffLine = true;
        }
        
        return isOffLine;
    }
    
    /**
     * Checks whether IMEI is enalbed or not
     * @param bus
     * @return true if SIM lookup is enabled.
     */
    protected boolean isSimLookupEnabled(BusIfc bus)
    {

        boolean result = false;
        boolean IMEIEnabled = false;
        boolean serialisationEnabled = false;
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        IMEIEnabled = utility.getIMEIProperty();
        serialisationEnabled = utility.getSerialisationProperty();
        if(IMEIEnabled && serialisationEnabled)
        {
            result=true;
        }
        else
        {
            result=false;
        }
        return (result);
    }
    
}
