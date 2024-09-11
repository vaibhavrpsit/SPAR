/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/PLUTransaction.java /main/21 2013/12/30 11:44:42 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  03/19/13 - PLURequester attached to inquiry to fetch the item
 *                         image for pricing inquiry
 *    jswan     01/07/13 - Modified to support item manager rework.
 *    jswan     09/24/12 - Modified to support request of Advanced Item Search
 *                         through JPA.
 *    hyin      09/06/12 - renaming JpaReadItemInfo
 *    hyin      08/31/12 - meta tag search POS UI work.
 *    jswan     08/30/12 - Result of merge with repository.
 *    jswan     08/29/12 - Modified to support SearchCriteria.
 *    hyin      08/20/12 - MetaTag Search data command flow.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   01/22/09 - deprecate getStoreCouponItem(String) and override
 *                         with one that takes an inquiry object
 * 
 * ===========================================================================
 *    $Log:
 *     5    360Commerce 1.4          1/25/2006 4:11:35 PM   Brett J. Larsen merge
 *            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *     4    360Commerce 1.3          12/13/2005 4:43:46 PM  Barry A. Pape
 *           Base-lining of 7.1_LA
 *     3    360Commerce 1.2          3/31/2005 4:29:21 PM   Robert Pearse   
 *     2    360Commerce 1.1          3/10/2005 10:24:07 AM  Robert Pearse   
 *     1    360Commerce 1.0          2/11/2005 12:13:04 PM  Robert Pearse   
 *    $:
 *     6    .v710      1.2.2.1      10/25/2005 17:53:00    Charles Suehs   Merged
 *           from v700.
 *     5    .v710      1.2.2.0      10/24/2005 16:05:58    Charles Suehs   Merge
 *           from PLUTransaction.java, Revision 1.2.1.0
 *     4    .v700      1.2.1.0      9/13/2005 15:40:09 *    Jason L. DeLeau If an
 *           id_itm_pos maps to multiple id_itms, let the user choose which one
 *           to use.
 *     3    360Commerce1.2          3/31/2005 15:29:21 *    Robert Pearse
 *     2    360Commerce1.1          3/10/2005 10:24:07 *    Robert Pearse
 *     1    360Commerce1.0          2/11/2005 12:13:04 *    Robert Pearse
 *    $
 *    Revision 1.5  2004/09/23 00:30:50  kmcbride
 *    @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *    Revision 1.4  2004/02/17 16:18:47  rhafernik
 *    @scr 0 log4j conversion
 *
 *    Revision 1.3  2004/02/12 17:13:19  mcs
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 23:25:24  bwf
 *    @scr 0 Organize imports.
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *    updating to pvcs 360store-current
 *
 *    Rev 1.0   Aug 29 2003 15:33:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Mar 31 2003 16:07:42   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.4   Mar 25 2003 13:55:36   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.3   Feb 03 2003 08:03:50   jgs
 * Add optional store number to PLU Lookup.
 * Resolution for 105: Modify PLU Look to optionally include the store number in the item lookup.
 *
 *    Rev 1.2   Dec 31 2002 09:43:52   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.1   12 Jun 2002 14:38:30   adc
 * Undeprecated the method getDepartmentList because BackOffice doesn't read reason codes, but needs the list of departments
 * Resolution for Backoffice SCR-996: Deprecation warnings -  services\common package
 *
 *    Rev 1.0   Jun 03 2002 16:42:10   msg
 * Initial revision.
 *
 *    Rev 1.2   10 Apr 2002 17:19:06   baa
 * deprecate class and methods for reading dept list
 * Resolution for POS SCR-1562: Get Department list from Reason Codes, not separate Dept. list.
 *
 *    Rev 1.1   Mar 18 2002 22:50:48   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:10:14   msg
 * Initial revision.
 *
 *    Rev 1.2   27 Dec 2001 16:39:54   pjf
 * Store coupon revisions.
 * Resolution for POS SCR-10: Advanced Pricing
 *
 *    Rev 1.1   05 Nov 2001 17:34:02   baa
 * Implement code review changes. Customer & Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   Sep 20 2001 15:56:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import oracle.retail.stores.common.item.AdvItemSearchResults;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.common.utility.Util;

/**
 * The PLUTransaction implements the price lookup operation.
 **/
public class PLUTransaction extends DataTransaction implements DataTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    private static final long serialVersionUID = 6952022663937149571L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/21 $";

    /**
     * The default name that links this transaction to a command within the
     * DataScript. Equals "PLUTransaction".
     */
    protected static final String dataCommandName = "PLUTransaction";

    /**
     * DataCommand constructor. Initializes dataOperations and
     * dataConnectionPool.
     */
    public PLUTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Obtains a PLUItem and its associated pricing rules given a pluKey.
     * 
     * @param pluKey The String lookup key. :116
     * @exception DataException is thrown if the PLUItem cannot be found.
     * @deprecated as of 13.1. Use {@link getPLUItem(String, Locale)}
     */
    public PLUItemIfc getPLUItem(String pluKey) throws DataException
    {
        MAXSearchCriteriaIfc inquiry = (MAXSearchCriteriaIfc) DomainGateway.getFactory().getSearchCriteriaInstance();
        inquiry.setItemID(pluKey);
        return getPLUItem(inquiry);
    }

    /**
     * Obtains a PLUItem and its associated data given an id and a locale requestor.
     * @param pluKey
     * @param requestor
     * @return
     * @throws DataException
     * @deprecated as of 14.0. Use {@link getPLUItem(SearchCriteriaIfc)}
     */
    public PLUItemIfc getPLUItem(String pluKey, LocaleRequestor requestor) throws DataException
    {
        MAXSearchCriteriaIfc inquiry = (MAXSearchCriteriaIfc) DomainGateway.getFactory().getSearchCriteriaInstance();
        inquiry.setItemID(pluKey);
        inquiry.setLocaleRequestor(requestor);
        return getPLUItem(inquiry);
    }

    /**
     * Get a list of all items that match the specified criteria
     * 
     * @param inquiry search criteria
     * @return List of items
     * @throws DataException
     * @since 7.0.2
     */
    public PLUItemIfc[] getPLUItems(MAXSearchCriteriaIfc inquiry) throws DataException
    {
        if (Util.isEmpty(inquiry.getStoreNumber()))
        {
            inquiry.setStoreNumber(getStoreID());
        }

        if (Util.isEmpty(inquiry.getCorporatePricingRuleStoreID()))
        {
            inquiry.setCorporatePricingRuleStoreID(getCorpPricingRuleStoreID());
        }

        if (inquiry.getPLURequestor() == null)
        {
            inquiry.setPLURequestor(new PLURequestor());
        }
        if(inquiry.getEmpID()){
        	inquiry.setEmpID(true);
        }
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(inquiry);
        dataAction.setDataOperationName("PLULookup");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;
        
        inquiry.getPLURequestor().setEmpID(inquiry.getEmpID());

        setDataActions(dataActions);
      
        PLUItemIfc[] pluItems = (PLUItemIfc[]) getDataManager().execute(this);
        return pluItems;
    }

    /**
     * Obtains a PLUItem and its associated pricing rules given a
     * SearchCriteriaIfc.
     * 
     * @param inquiry The SearchCriteriaIfc lookup
     * @exception DataException is thrown if the PLUItem cannot be found.
     */
    public PLUItemIfc getPLUItem(MAXSearchCriteriaIfc inquiry) throws DataException
    {
        PLUItemIfc item = null;
        PLUItemIfc[] items = getPLUItems(inquiry);
        if (items != null && items.length > 0)
        {
            item = items[0];
        }
        return item;
    }

    /**
     * This method deterimes if the store number should be used in the PLU
     * lookup. It returns the store number if it should be used and null if it
     * should not be used.
     * 
     * @return String store number or null
     */
    private String getStoreID()
    {
        String storeID = null;

        // This setting resides in domain.properties
        String useStoreID = Gateway.getProperty("domain.properties", "UseStoreIDInPLULookup", "xyz");

        if (useStoreID.equalsIgnoreCase("true"))
        {
            // This setting resides in applicaiton.properties
            storeID = Gateway.getProperty("application", "StoreID", null);
        }

        return storeID;
    }

    /**
     * This method deterimes if the store number should be used in the PLU
     * lookup. It returns the store number if it should be used and null if it
     * should not be used.
     * 
     * @return String store number or null
     */
    private String getCorpPricingRuleStoreID()
    {
        String storeID = null;

        storeID = Gateway.getProperty("application", "CorporatePricingRuleStoreID", "CORP");

        return storeID;
    }

    /**
     * Obtains a store coupon PLUItem and its associated pricing rules given a
     * pluKey and store number.
     * 
     * @param inquiry the object holding the coupon plu key and store number
     * @exception DataException is thrown if the PLUItem cannot be found.
     */
    public PLUItemIfc getStoreCouponItem(SearchCriteriaIfc inquiry) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(inquiry);
        dataAction.setDataOperationName("SCLULookup");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        PLUItemIfc[] pluItems = (PLUItemIfc[]) getDataManager().execute(this);

        PLUItemIfc item = null;
        if (pluItems != null && pluItems.length > 0)
        {
            item = pluItems[0];
        }

        // For now, only return the first match
       // System.out.println("ITEM 308 :"+item);
        return (item);
    }

    /**
     * Returns the all Items that match a given itemInfo (Number and/or desc and
     * dept).
     * 
     * @param pluKey The String lookup key.
     * @exception DataException is thrown if the PLUItem cannot be found.
     * @deprecated in version 14.0; replaced by getItemsForAdvancedSearch()
     */
    public PLUItemIfc[] getMatchingItems(SearchCriteriaIfc inquiry) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(inquiry);
        dataAction.setDataOperationName("ReadItemInfo");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        PLUItemIfc[] pluItems = (PLUItemIfc[]) getDataManager().execute(this);

        //System.out.println("pluItems 332 :"+pluItems);
        return (pluItems);
    }
    
    /**
     * Returns the all Items that match a given itemInfo (Number and/or desc and
     * dept).
     * 
     * @param pluKey The String lookup key.
     * @exception DataException is thrown if the PLUItem cannot be found.
     */
    public AdvItemSearchResults getItemsForAdvancedSearch(SearchCriteriaIfc inquiry) throws DataException
    {
        if (inquiry.getPLURequestor() == null)
        {
            inquiry.setPLURequestor(new PLURequestor());
        }

        DataAction dataAction = new DataAction();
        dataAction.setDataObject(inquiry);
        dataAction.setDataOperationName("ReadItemInfo");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        AdvItemSearchResults items = (AdvItemSearchResults)getDataManager().execute(this);

        //System.out.println("items 360 :"+items);
        return (items);
    }
    
    /**
     * Returns all items based on Meta tag search string
     * @param inquiry
     * @returnb
     * @throws DataException if anything goes abnormal
     */
    public AdvItemSearchResults getMetaTagItems(SearchCriteriaIfc inquiry) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(inquiry);
        dataAction.setDataOperationName("ReadMetaTagItemInfo");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        AdvItemSearchResults items = (AdvItemSearchResults) getDataManager().execute(this);

      //  System.out.println("items 382 :"+items);
        return items;
    }

    /**
     * Returns the list of available deparments.
     * 
     * @exception DataException is thrown if the depList cannot be found.
     */
    public DepartmentIfc[] getDepartmentList() throws DataException
    {

        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("ReadDepartmentList");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        DepartmentIfc[] deptList = (DepartmentIfc[]) getDataManager().execute(this);

       // System.out.println("deptList 403 :"+deptList);
        return (deptList);
    }

    /**
     * Returns all of the service items.
     * 
     * @return all of the service items.
     * @exception DataException is thrown if the PLUItem cannot be found.
     * @deprecated in 14.0 see getItemsForAdvancedSearch(); the SearchCriteriaIfc object
     * should have the itemTypeCode set to ARTSDatabaseIfc.ITEM_TYPE_SERVICE.
     */
    public PLUItemIfc[] getServiceItems() throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(null);
        dataAction.setDataOperationName("ReadServiceItems");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        PLUItemIfc[] pluItems = (PLUItemIfc[]) getDataManager().execute(this);

       // System.out.println("pluItems 427 :"+pluItems);
        return (pluItems);
    }

    /**
     * Returns all of the service items.
     * 
     * @param inquiry of type SearchCriteriaIfc
     * @return all of the service items.
     * @exception DataException is thrown if the PLUItem cannot be found.
     * @deprecated in 14.0 see getItemsForAdvancedSearch(); the SearchCriteriaIfc object
     * should have the itemTypeCode set to ARTSDatabaseIfc.ITEM_TYPE_SERVICE.
     */
    public PLUItemIfc[] getServiceItems(SearchCriteriaIfc inquiry) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(inquiry);
        dataAction.setDataOperationName("ReadServiceItems");
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        PLUItemIfc[] pluItems = (PLUItemIfc[]) getDataManager().execute(this);

      //  System.out.println("pluItems 451 :"+pluItems);
        return (pluItems);
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = new StringBuilder("Class: PLUTransaction (Revision ");
        strResult.append(getRevisionNumber()).append(") @").append(hashCode());
       // System.out.println("strResult.toString() 473 :"+strResult.toString());
        return strResult.toString();
    }
}
