/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/SearchItemInLocalOrWebStoreSite.java /main/11 2014/07/16 08:58:27 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
*    abhinavs  11/25/14 - Setting webstore items indicator when
*                         item search results are from web.
*    jswan     11/17/14 - Fixed recommended item read failure.
*    abhinavs  10/06/14 - Un-setting item number to null and
*                         setting inquiry obj to null in case of DB error.
*    abhinavs  10/03/14 - Showing custom error messages when no item is found.
*    abhinavs  09/22/14 - Handling data exception gracefully and 
*                         send 'UNDO' instead of 'RETRY'
*    abhinavs  09/04/14 - Minor tweaks to implement correct UNDO action 
*                         on item filtering results set
*    abhinavs  08/13/14 - Fixing simple typo error of sending 'Retry'
*                         on cancel action
*    abhinavs  07/14/14 - CAE item search results filtering cleanup
*    jswan     06/06/14 - Modified to support a tabbed UI for the item info
*                         bean for the ICE project.
*    yiqzhao   07/31/13 - Avoid NullPointerException and infinite loop.
*    abhinavs  07/30/13 - Fix to send cancel letter on cancel action and
*                         handling it accordingly
*    vtemker   07/10/13 - Fixed issue with Item Price search - prevent all
*                         webstore items from being retrieved
*    yiqzhao   06/28/13 - Change the dialog name to ItemNotFoundInStore.
*    yiqzhao   05/10/13 - Display the limited items even when meta tag search
*                         returns too many items to display.
*    hyin      03/06/13 - fix metatag search offline message.
*    hyin      01/29/13 - allowing user to only enter description on item
*                         detail search.
*    yiqzhao   01/23/13 - Remove deadlock when store server is offline.
*    jswan     01/07/13 - Modified to support item manager rework.
* ===========================================================================
*/

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import oracle.retail.stores.common.item.AdvItemSearchResults;
import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.ValetException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


public class SearchItemInLocalOrWebStoreSite extends PosSiteActionAdapter
{
    
    private static final long serialVersionUID = -686797107976490489L;

    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    
    /**
     * constant for parameter name
     **/
    public static final String ITEM_MAXIMUM_MATCHES = "ItemMaximumMatches";

    public void arrive(BusIfc bus)
    {

        String letter = null;
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        cargo.resetInvalidFieldCounter();
        SearchCriteriaIfc inquiry = null;
        if(!cargo.isItemSearchOrigCriteria())
        {
            inquiry  = cargo.getInquiry();
        }
        else
        {
            inquiry = cargo.getItemsSearchOrigCriteria();
        }
        AdvItemSearchResults aisr = null;
        
        ItemManagerIfc mgr = (ItemManagerIfc)bus.getManager(ItemManagerIfc.TYPE);
        
        try
        {
            if(!StringUtils.isEmpty(inquiry.getMetaTagSearchStr()) && inquiry.isMetaTagSearch())
            {
                cargo.setAdvanceSearch(false);
            }
            // retrieve item with USER_INTERFACE locale only for item search
            inquiry.setLocaleRequestor(new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
            inquiry.setPricingDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            boolean retrieveExtendedDataOnLocalItemSearch = Gateway.getBooleanProperty(
                    Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedDataOnLocalItemSearch", false);
            inquiry.setRetrieveExtendedDataOnLocalItemSearch(retrieveExtendedDataOnLocalItemSearch);
            int maxRecommendedItemsListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxRecommendedItemsListSize", "100"));
            inquiry.setMaxRecommendedItemsListSize(maxRecommendedItemsListSize);
            if (inquiry.getMaximumMatches() == -1)
            {
                inquiry.setMaximumMatches(getMaximumMatches(bus));
            }
            aisr = mgr.searchPluItems(inquiry);

            if (aisr == null)
            {
                throw new DataException(DataException.NO_DATA);
            }
            else
            {
                List<ItemSearchResult> items = aisr.getReturnItems();
                if (items.size() < 1)
                {
                    throw new DataException(DataException.NO_DATA);
                }

                if (aisr.isExceedLimit())
                {
                    // The results on the cargo.
                    cargo.setAdvancedSearchResult(aisr);
                    if (inquiry.isRetrieveFromStore())
                    {
                        cargo.setItemFromWebStore(false);
                    }
                    else
                    {
                        cargo.setItemFromWebStore(true);
                        cargo.getAdvancedSearchResult().setWebstoreItems(true);
                    }
                    throw new DataException(DataException.RESULT_SET_SIZE);
                }

                // The results on the cargo.
                cargo.setAdvancedSearchResult(aisr);
                if (inquiry.isRetrieveFromStore())
                {
                    cargo.setItemFromWebStore(false);
                }
                else
                {
                    cargo.setItemFromWebStore(true);
                    cargo.getAdvancedSearchResult().setWebstoreItems(true);
                }
                if (items.size() > 1)
                {
                    letter = CommonLetterIfc.NEXT;
                }
                else 
                {
                    cargo.setItem(aisr.getReturnItems().get(0));
                    letter = CommonLetterIfc.SUCCESS;
                }
            }
        }
        catch (DataException de)
        {
            // Set the error code on the cargo
            int errorCode = de.getErrorCode();
            //This condition indicates webstore is offline
            // or client have trouble communicating with it
            if(de.getCause() instanceof ValetException)
            {
                if (cargo.getInquiry() != null)
                {
                    cargo.getInquiry().setRetrieveFromStore(true);
                }
            }
            cargo.setDataExceptionErrorCode(errorCode);
            String msg[] = new String[1];

            String itemID = "";
            if ( inquiry.isSearchItemByItemID() )
            {
                itemID = inquiry.getItemID();
            }
            else if ( inquiry.isSearchItemByPosItemID() )
            {
                itemID = inquiry.getPosItemID();
            }
            else if ( inquiry.isSearchItemByItemNumber() )
            {
                itemID = inquiry.getItemNumber();
            }
            
            // Log the error.
            if (cargo.getInquiry().isSearchFromItemDetail())
            {
                logger.warn("Advanced search failed; " + itemID + 
                        " \nItem Desc: " + inquiry.getDescription() + 
                        " \nItem Dept: " + inquiry.getDepartmentID() + 
                        " \nItem Manufacturer: " + inquiry.getManufacturer() + "", de);
            }
            else if (inquiry.isMetaTagSearch())
            {
                logger.warn("Meta tag search failed; search string: " + inquiry.getMetaTagSearchStr() + 
                            " \nStoreID: " + inquiry.getStoreNumber() + 
                            " \nLocale: " + inquiry.getLocaleRequestor().getDefaultLocale().toString() + 
                            " \nMaxMatches: " + inquiry.getMaximumMatches(), de);
            }
            else
            {
                logger.warn("Advanced search failed; ItemNo: " + itemID + 
                            " \nItem Desc: " + inquiry.getDescription() + 
                            " \nItem Dept: " + inquiry.getDepartmentID() + 
                            " \nItem Manufacturer: " + inquiry.getManufacturer() + "", de);
            }

            // Determine the next course of action
            switch(de.getErrorCode())
            {
                case DataException.NO_DATA:
                    
                    boolean isOffLine = false;
                    if (DataException.ERROR_CODE_EXTENDED_OFFLINE == de.getErrorCodeExtended() ||
                        (aisr != null && aisr.isUsingDerby()))
                    {
                        isOffLine = true;
                    }
                    boolean isXChannel = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, "XChannelEnabled", false);
                    
                    if (isXChannel && !isOffLine && inquiry.isRetrieveFromStore())
                    {
                        if(cargo.isFilterSearchResults())
                        {
                            showFilteringErrorDialog(bus,"FILTER_CRI_NOT_SATIS_ERROR", null, CommonLetterIfc.UNDO);
                        }
                        else
                        {
                            showWebServiceDialog(bus, inquiry); 
                        }
                        
                    }
                    else
                    {
                        //prepare for re-enter item id.
                        inquiry.setItemNumber(null);

                        if ((isOffLine) && (inquiry.isMetaTagSearch()))
                        {
                            showErrorDialog(bus,"SystemOfflineForMetatagSearch", null, CommonLetterIfc.RETRY);
                        }
                        else
                        {
                            
                            if(cargo.isFilterSearchResults())
                            {
                                
                                showFilteringErrorDialog(bus,"FILTER_CRI_NOT_SATIS_ERROR", null, CommonLetterIfc.UNDO); 
                            }
                            else
                            {
                                showErrorDialog(bus,"INFO_NOT_FOUND_ERROR", null, CommonLetterIfc.UNDO); 
                            }
                        }
                        
                    }
                    
                    break;
    
                case DataException.RESULT_SET_SIZE:
                    msg[0] = String.valueOf(inquiry.getMaximumMatches());
                    showMaximumMatchDialog(bus, "MaxMatchReached", msg);
                    break;

                default:
                    UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                    msg[0] = utility.getErrorCodeString(de.getErrorCode());
                    cargo.setInquiry(null);
                    showErrorDialog(bus,"DatabaseError",msg, CommonLetterIfc.UNDO);
                    break;
            }
        }
        finally
        {   
            if(cargo.getInquiry() != null)
            {
                cargo.getInquiry().setSearchFromItemDetail(false);
            }
            inquiry.setRetrieveFromStore(true);
            cargo.setItemSearchOrigCriteria(false);
        }
        
        /*
         * The letter will be null if an error dialog has been displayed.
         * In the is case the dialog will mail the appropriate letter.
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
    
    //----------------------------------------------------------------------
    /**
     *   Displays maximum match Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showMaximumMatchDialog(BusIfc bus, String id, String[] args)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the acknowledgement dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID(id);
        if (args != null)
        {
           model.setArgs(args);
        }
        model.setType(DialogScreensIfc.CONTINUE_CANCEL);
        model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, CommonLetterIfc.NEXT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    //----------------------------------------------------------------------
    /**
     *   Displays maximum match Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showWebServiceDialog(BusIfc bus, SearchCriteriaIfc inquiry)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the choice dialog

        DialogBeanModel model = new DialogBeanModel();

        String msg[] = new String[1];
        if (inquiry.isSearchItemByItemID())
        {
            msg[0] = inquiry.getItemID();
        }
        else if (inquiry.isSearchItemByItemNumber())
        {
            msg[0] = inquiry.getItemNumber();
        }
        else if (inquiry.isSearchFromItemDetail())
        {
            msg[0] = inquiry.getDescription();
        }
        else if(inquiry.isMetaTagSearch())
        {
            msg[0] = inquiry.getMetaTagSearchStr();
        }
        if(msg[0] != null && !StringUtils.isEmpty(msg[0]))
        {
            if (!inquiry.isMetaTagSearch())
            {
                model.setResourceID("ItemNotFoundInStore");
                model.setArgs(msg);
                model.setType(DialogScreensIfc.SEARCHWEBSTORE_CANCEL);
                model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_WEBSTORE, CommonLetterIfc.SEARCH);
                model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
            else
            {
                model.setResourceID("MetaTagSearchItemNotFoundInStore");
                model.setArgs(msg);
                model.setType(DialogScreensIfc.SEARCHWEBSTORE_CANCEL);
                model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_WEBSTORE, CommonLetterIfc.SEARCH);
                model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
        }
        else
        {
            model.setResourceID("ItemNotFoundInStoreGeneral");
            model.setType(DialogScreensIfc.SEARCHWEBSTORE_CANCEL);
            model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_WEBSTORE, CommonLetterIfc.SEARCH);
            model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
    }
    
    
    //----------------------------------------------------------------------
    /**
     *   Displays error Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showFilteringErrorDialog(BusIfc bus, String id, String[] args, String letter)
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
     * Returns value of ItemMaximumMatches from properties file.
     * 
     * @param bus
     * @return int representing the maximum match.
     */
    protected int getMaximumMatches(BusIfc bus)
    {
        // get paramenter manager
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        // maximum number of matches allowed
        Integer maximum = new Integer("100"); // default
        try
        {
            if (pm != null)
            {
                String s = pm.getStringValue(ITEM_MAXIMUM_MATCHES);
                s.trim();
                maximum = new Integer(s);
            }
            if (logger.isInfoEnabled())
                logger.info("Parameter read: " + ITEM_MAXIMUM_MATCHES + " = [" + maximum + "]");
        }
        catch (ParameterException e)
        {
            logger.error("" + Util.throwableToString(e) + "");
        }

        return (maximum.intValue());
    }

}

