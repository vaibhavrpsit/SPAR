/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ValidateItemInfoSite.java /main/39 2014/05/16 16:28:24 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *   abhinavs  10/03/14 - Setting selected clearance index from the dropdown
 *                        to include it in verifying the condition if atleast one
 *                        search criteria is given.
 *    yiqzhao  09/22/14 - Add style and item type, check the value for department.
 *    yiqzhao  09/15/14 - The parameter values should be Y or N, not Yes or No.
 *    vtemke   08/25/14 - Added the More Info Needed dialog for empty search
 *    abhinavs 08/13/14 - CAE filtering item search results cleanup
 *    abhinavs 05/12/14 - Filtering Item search results enhancement
 *    abonda   11/07/13 - set the supported locales on the citeria object to
 *                      retieve the descriptions for the supported locales
 *    abhinavs 05/22/13 - Fix to invalidate the search in case of both item
 *                      number and description is blank
 *    hyin     01/29/13 - allowing user to only enter description on item detail
 *                      search.
 *    jswan    01/07/13 - Modified to support item manager rework.
 *    sgu      12/20/12 - use locale requestor
 *    hyin     11/13/12 - enable adv search from result screen.
 *    hyin     10/16/12 - offline work for PLU lookup and Advanced item lookup.
 *    hyin     10/15/12 - set locale strings.
 *    hyin     10/12/12 - use single ItemSearchCriteria for metaTag service.
 *    hyin     10/02/12 - disable webservice call when it's offline.
 *    jswan    09/24/12 - Modified to support request of Advanced Item Search
 *                      through JPA.
 *    hyin     09/12/12 - set itemFromWebStore flag for xc flow.
 *    tzgarb   03/16/12 - Updated the site to use the store status object to
 *                      retrieve the store ID
 *    tzgarb   03/15/12 - Updated to get the UtilityManager from the TourContext
 *    hyin     09/06/12 - add maximum matches dialog.
 *    hyin     08/31/12 - meta tag search POS UI work.
 *    hyin     08/20/12 - MetaTag Search data command flow.
 *    hyin     08/16/12 - meta tag search feature.
 *    rrkohl   05/12/11 - fix for item search
 *    ohorne   02/22/11 - ItemNumber can be ItemID or PosItemID
 *    cgreen   05/26/10 - convert to oracle packaging
 *    dwfung   02/02/10 - to accept * wild card search
 *    abonda   01/03/10 - update header date
 *    mchell   01/28/09 - Modified isValidField to use * as wildcard
 *    mchell   12/23/08 - Changes for searchForItemByManufacturer parameter
 *    mchell   12/02/08 - Changes for Item search field parameter update
 *    ranojh   10/29/08 - Changes for Return, UOM and Department Reason Codes
 *    abonda   10/17/08 - I18Ning manufacturer name
 *    abonda   10/14/08 - I18Ning manufacturer name
 *    abonda   10/14/08 - updated header
 *
 *
 * ===========================================================================

     $Log:
      7    360Commerce 1.6         3/21/2008 2:57:08 PM   Mathews Kochummen
           forward port v12x to trunk. reviewed by alan
      6    360Commerce 1.5         6/21/2007 5:46:00 AM   Naveen Ganesh   Added
            the condition data.length()==0 in isValidField method
      5    360Commerce 1.4         3/13/2006 3:52:14 AM   Akhilashwar K. Gupta
           CR-10794: Updated return of method replaceStar(String oldtext)
      4    360Commerce 1.3         12/13/2005 4:42:41 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse
     $
     Revision 1.10  2004/07/17 16:03:04  lzhao
     @scr 6319: clone searchCriteria for search

     Revision 1.9  2004/06/03 14:47:45  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.8  2004/04/28 22:51:29  lzhao
     @scr 4081,4084: roll item info to inventory screen.

     Revision 1.7  2004/04/22 17:35:52  lzhao
     @scr 4291, 4384 show department and size info.

     Revision 1.6  2004/04/17 17:59:29  tmorris
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.5  2004/03/26 18:44:48  lzhao
     @scr 3840 Fix database offline in inventory inquiry.

     Revision 1.4  2004/03/03 23:15:10  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:50:34  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:11  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:00:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jan 06 2003 11:35:20   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.1   Sep 05 2002 15:29:38   jriggins
 * Replaced call to DataException.getErrorCodeString() to the new UtilityManagerIfc.getErrorCodeString().
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:22:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:06   msg
 * Initial revision.
 *
 *    Rev 1.3   08 Feb 2002 18:52:00   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.2   12 Nov 2001 12:01:46   baa
 * fix error msg for invalid data
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.1   05 Nov 2001 17:37:38   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-230: Cross Store Inventory
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   Sep 21 2001 11:29:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:04   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// java imports

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.ItemSearchCriteriaIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;

//--------------------------------------------------------------------------
/**
    This site validates the item number stored in the cargo.
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ValidateItemInfoSite extends PosSiteActionAdapter
{
    /**
       item number field
    **/
    public static final int     ITEM_NUMBER_FIELD     = 1;
    /**
       item description field
    **/
    public static final int     ITEM_DESC_FIELD       = 2;

    /**
    item manufacturer field
    **/
    public static final int     ITEM_MANUFAC_FIELD       = 3;

    /**
    item manufacturer field
    **/
    public static final int     ITEM_META_SEARCH_FIELD = 4;


    /**
           constant for parameter name
    **/
    public static final String  ITEM_MAXIMUM_MATCHES  = "ItemMaximumMatches";

    /**
     UI wildcard tag
     **/
    public static final String UI_WILDCARD_TAG = "UIWildcard";
    /**
     UI wildcard
     **/
    public static final String UI_WILDCARD = "*";
    /**
     DB wildcard tag
     **/
    public static final String DB_WILDCARD_TAG = "DBWildcard";
    /**
     DB wildcard
     **/
    public static final String DB_WILDCARD = "%";
    /**
    default string for PlanogramDisplay
    **/
    public static final String PLANOGRAM_DISPLAY = "PlanogramDisplay";
    /**
    default string for SearchForItemByManufacturer
    **/
    public static final String SEARCH_FOR_ITEM_BY_MANUFACTURER = "SearchForItemByManufacturer";


    //----------------------------------------------------------------------
    /**
        Validate the item info stored in the cargo( number, desc and dept) . If the
        item is found, a Success letter is sent. Otherwise,
        a Failure letter is sent.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // letter to be sent
        String   letter  = null;

        // get item inquiry from cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        SearchCriteriaIfc inquiry = (SearchCriteriaIfc) cargo.getInquiry();
        cargo.resetInvalidFieldCounter();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        boolean isMetaTagSearch = inquiry.isMetaTagSearch() && (!inquiry.isSearchFromItemDetail());
        boolean searchForItemByManufacturer = false;
        boolean anyInvalidField = false;
        boolean allNullFields = false;
        try
        {
            if((pm.getStringValue(SEARCH_FOR_ITEM_BY_MANUFACTURER)).equalsIgnoreCase("Y"))
            {
                searchForItemByManufacturer = true;
            }
        }
        catch(ParameterException pe)
        {
            logger.error("Cannot retrive parameter value");
        }
        
        if (inquiry.isSearchFromItemDetail())
        {
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            if (ui.getModel() instanceof ItemInfoBeanModel)
            {
                ItemInfoBeanModel beanModel = (ItemInfoBeanModel)ui.getModel();
                String desc = beanModel.getItemDescription();
                cargo.getInquiry().setItemNumber(beanModel.getItemNumber());
                cargo.getInquiry().setDescription(desc);
            }
            
            /*If there is no valid input, and the search is too broad, ask for 
             * more criteria
             * */
            if(isMoreInfoNeeded(inquiry, searchForItemByManufacturer))
            {
                showMoreInfoNeededDialog(ui);
                return;
            }
        }
        
        //Setting search by item number to true when advanced search result has non zero elements
        if(cargo.getAdvancedSearchResult()!=null && cargo.getAdvancedSearchResult().getReturnItems() != null && 
                cargo.getAdvancedSearchResult().getReturnItems().size()>0)
        {
            cargo.getInquiry().setSearchItemByItemNumber(true);
        }
        //determine if ALL the searchable fields are null
        //manufacturer field is included in the UI only if the parameter is set.
        if (isMetaTagSearch)
        {
            if (inquiry.getMetaTagSearchStr() == null)
            {
                allNullFields = true;
            }
        } 
        else if (inquiry.isSearchFromItemDetail())
        {
            if (inquiry.getDescription() == null)
            {
                allNullFields = true;
            }
        }
        else 
        {
            if (searchForItemByManufacturer)
            {
                if (inquiry.getDescription()  == null && inquiry.getItemID()     == null && 
                    inquiry.getPosItemID()    == null && inquiry.getItemNumber() == null && 
                    inquiry.getManufacturer() == null)
                {
                    allNullFields = true;
                }
            } 
            else 
            {
                if (inquiry.getDescription() == null && inquiry.getItemID()     == null && 
                    inquiry.getPosItemID()   == null && inquiry.getItemNumber() == null)
                {
                    allNullFields = true;
                }
            }
        }
       
        //determine if any of the fields have invalid input.
        //its ok to have null fields; but if they are not null, they should contain valid input
        boolean metaSearchStrInvalid = false;
        if (isMetaTagSearch)
        {
            if (!isValidField(inquiry.getMetaTagSearchStr()))
            {
                anyInvalidField=true;
                metaSearchStrInvalid = true;
            }
        } 
        else if (inquiry.isSearchFromItemDetail())
        {
            if (!isValidField(inquiry.getDescription()) && !isValidField(inquiry.getItemNumber()))
            {
                anyInvalidField=true;
            }
        }
        else 
        {
            if (!isValidField(inquiry.getItemNumber()) || !isValidField(inquiry.getItemID()) || 
                !isValidField(inquiry.getPosItemID())  || !isValidField(inquiry.getDescription()) || 
                (searchForItemByManufacturer && !isValidField(inquiry.getManufacturer())))
            {
               anyInvalidField=true;
            }
        }


       //mark the fields in error; otherwise search the database for the item.
       if (allNullFields)
       {
    	    //ALL the fields are null; so mark them all as error
           if (isMetaTagSearch)
           {
               cargo.setInvalidField(ITEM_META_SEARCH_FIELD);
           }
           else
           {
               if (inquiry.isSearchFromItemDetail())
               {
                   cargo.setInvalidField(ITEM_DESC_FIELD);
               }
               else 
               {
                   cargo.setInvalidField(ITEM_NUMBER_FIELD);
                   cargo.setInvalidField(ITEM_DESC_FIELD);
                   if (searchForItemByManufacturer)
                   {
                       cargo.setInvalidField(ITEM_MANUFAC_FIELD);
                   }
               }
           }
           letter = CommonLetterIfc.INVALID;
       }
       else if (anyInvalidField)
       {
           //if any field is not null, check that they contain valid input; mark the fields in error
           
           //if it's metasearch and it has valid input, we won't be here
           if (metaSearchStrInvalid)
           {
               cargo.setInvalidField(ITEM_META_SEARCH_FIELD);
           }
           else if (inquiry.isSearchFromItemDetail())
           {
               cargo.setInvalidField(ITEM_DESC_FIELD);
               if ((inquiry.getItemNumber() != null) && (!isValidField(inquiry.getItemNumber())))
               {
                   cargo.setInvalidField(ITEM_NUMBER_FIELD);
               }
           }
           else 
           {
               if (!isValidField(inquiry.getItemID()) || !isValidField(inquiry.getItemNumber()) || !isValidField(inquiry.getPosItemID()))
               {
                   cargo.setInvalidField(ITEM_NUMBER_FIELD);
               }
               if (!isValidField(inquiry.getDescription()))
               {
                   cargo.setInvalidField(ITEM_DESC_FIELD);
               }
               if (searchForItemByManufacturer && !isValidField(inquiry.getManufacturer()))
               {
                   cargo.setInvalidField(ITEM_MANUFAC_FIELD);
               }
           }

        	letter = CommonLetterIfc.INVALID;
       }
       else
       {
            // retrieve item with USER_INTERFACE locale only
            inquiry.setLocaleRequestor(LocaleMap.getSupportedLocaleRequestor());
            inquiry.setMaximumMatches(getMaximumMatches(bus));
            inquiry.setStoreNumber(cargo.getStoreStatus().getStore().getStoreID());
            letter = CommonLetterIfc.SEARCH;
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
     *   Returns a boolean, validates the field
         *  @param  the string data
         *  @return boolean
        */
    //----------------------------------------------------------------------
    public boolean isValidField(String data)
    {
        boolean isValid = true;
        if (data != null && data.trim().length() == 0)
        {
            isValid = false;
        }
        return isValid;
    }

    //----------------------------------------------------------------------
    /**
     *   Returns an Integer, the maximum matches allowed from
     *   the parameter file. <P>
     *   @param bus
     *   @return int maximum matches allowed as Integer
     */
    //----------------------------------------------------------------------
    private int getMaximumMatches(BusIfc bus)
    {
        // get paramenter manager
        ParameterManagerIfc pm  = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        // maximum number of matches allowed
        Integer maximum = new Integer("100");  // default
        try
        {
            String s = pm.getStringValue(ITEM_MAXIMUM_MATCHES);
            s.trim();
            maximum = new Integer(s);
            if (logger.isInfoEnabled()) logger.info(
                        "Parameter read: " + ITEM_MAXIMUM_MATCHES + " = [" + maximum + "]");
        }
        catch (ParameterException e)
        {
            logger.error(
                         "" + Util.throwableToString(e) + "");
        }

        return(maximum.intValue());
    }

    //----------------------------------------------------------------------
    /**
     Replaces '*' to '%' before storing data to cargo
     @param  string    oldtext
     **/
    //----------------------------------------------------------------------
    protected String replaceStar(String oldtext)
    {
        UtilityManagerIfc utility =
        (UtilityManagerIfc)TourContext.getInstance().getTourBus().getManager(UtilityManagerIfc.TYPE);
        String uiWildcard =
        utility.retrieveText("Common",
                BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
                UI_WILDCARD_TAG,
                UI_WILDCARD);
        String dbWildcard =
        utility.retrieveText("Common",
                BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
                DB_WILDCARD_TAG,
                DB_WILDCARD);


        return replaceStar(oldtext,
                uiWildcard.charAt(uiWildcard.length()-1),
                dbWildcard.charAt(uiWildcard.length()-1));
    }

    //----------------------------------------------------------------------
    /**
     Replaces '*' to '%' before storing data to cargo
     @param  string    oldtext
     **/
    //----------------------------------------------------------------------
    protected String replaceStar(String oldtext, char uiWildCard, char dbWildCard)
    {
        // new string after wild card symbol replac3
        String newtext = null;

        if ( oldtext != null && !oldtext.equals(""))
           {
            newtext = oldtext.replace(uiWildCard, dbWildCard);
        }

        return newtext;
    }

    //----------------------------------------------------------------------
    /**
     Get department list.
     @param  utility     UtilityManagerIfc
     @param  storeID
     @param  departmentID
     @return DepartmentIfc[]
     **/
    //----------------------------------------------------------------------
    protected DepartmentIfc getDepartment(UtilityManagerIfc utility, String storeID, String departmentID)
    {
        // retrieve department code list from reason codes.
        CodeListIfc deptMap=utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_DEPARTMENT);

        // retrieve department entries
        CodeEntryIfc[] deptEntries = deptMap.getEntries();
        DepartmentIfc department = null;
        for(int i=0; i < deptEntries.length; i++)
        {
            if ( deptEntries[i].getCode().equals(departmentID) )
            {
                department = DomainGateway.getFactory().getDepartmentInstance();
                department.setLocalizedDescriptions(deptEntries[i].getLocalizedText());
                department.setDepartmentID(deptEntries[i].getCode());
                break;
            }
        }
        return department;
    }
    
    /**
     * Display dialog, prompting user to enter atleast one piece of search
     * criteria
     * 
     * @param ui
     */
    protected void showMoreInfoNeededDialog(POSUIManagerIfc ui)
    {
        // initialize model bean
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("MORE_INFO_NEEDED");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);

        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Check if the Advanced Item search criteria has atleast one valid search
     * criteria. Wildcards are permitted.
     * 
     * @param criteria
     * @return isMoreInfoNeeded
     */
    protected boolean isMoreInfoNeeded(ItemSearchCriteriaIfc criteria, boolean searchForItemByManufacturer)
    {
        boolean isMoreInfoNeeded = false;

        String itemNumber = criteria.getItemNumber();
        String description = criteria.getDescription();
        String itemManufacturer = criteria.getManufacturer();
        String deptID = criteria.getDepartmentID();
        String typeCode = criteria.getItemTypeCode();
        String styleCode = criteria.getItemStyleCode();
        String sizeCode = criteria.getItemSizeCode();
        String colorID = criteria.getItemColorCode();
        String uomID = criteria.getItemUOMCode();
        int taxable = criteria.getTaxable();
        int clearance = criteria.getClearance(); 
        int discountable = criteria.getDiscountable();

        // A value of 3 for "boolean" attributes means "All" (i.e. Yes OR No)
        if (Util.isEmpty(itemNumber) && Util.isEmpty(description) && Util.isEmpty(deptID)
                && Util.isEmpty(typeCode) && Util.isEmpty(styleCode) &&Util.isEmpty(sizeCode) 
                && Util.isEmpty(colorID) && Util.isEmpty(uomID)
                && taxable == 3 && discountable == 3 && clearance == 3)
        {
            isMoreInfoNeeded = true;
        }

        // itemManufacturer counts only if searchForItemByManufacturer parameter
        // = Yes
        if (searchForItemByManufacturer)
        {
            isMoreInfoNeeded = isMoreInfoNeeded && Util.isEmpty(itemManufacturer);
        }

        return isMoreInfoNeeded;
    }
    
}
