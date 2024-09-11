/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/StoreDataTransaction.java /main/19 2012/05/02 14:07:48 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    hyin   04/30/12 - add method to retrieve buddy stores for a given store
 *                      id
 *    jswan  04/18/12 - Modified to support cross channel create pickup order
 *                      feature.
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    yiqzha 03/12/10 - add one method for reading store status by store id and
 *                      date.
 *    abonda 01/03/10 - update header date
 *    ranojh 10/31/08 - Refreshed View and Merged changes with Reason Codes
 *    ranojh 10/29/08 - Changes for Return, UOM and Department Reason Codes
 *
 * ===========================================================================

     $Log:
      9    360Commerce 1.8         11/22/2007 11:09:31 PM Naveen Ganesh   PSI
           Code checkin
      8    360Commerce 1.7         11/15/2007 11:40:38 AM Christian Greene Add
           serialveruid
      7    360Commerce 1.6         7/3/2007 2:05:42 PM    Alan N. Sinton  CR
           27474 - Read store information even if store history table is
           empty.
      6    360Commerce 1.5         12/8/2006 5:01:15 PM   Brendan W. Farrell
           Read the tax history when creating pos log for openclosetill
           transactions.  Rewrite of some code was needed.
      5    360Commerce 1.4         7/21/2006 2:27:38 PM   Brendan W. Farrell
           Merge fixes from v7.x.  These changes let services extend tax.
      4    360Commerce 1.3         5/10/2006 9:48:24 PM   Brett J. Larsen CR
           17307 - remove inventory
      3    360Commerce 1.2         3/31/2005 4:30:11 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:32 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:27 PM  Robert Pearse
     $
     Revision 1.6  2004/04/09 16:55:46  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:36  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:46  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:23  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:34:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 12 2003 16:05:10   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:42:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:51:30   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:10:48   msg
 * Initial revision.
 *
 *    Rev 1.1   24 Oct 2001 17:45:30   mpm
 * Added getStoreRegions() call.
 * Resolution for POS SCR-8: Item Kits
 * Resolution for POS SCR-228: Merge VABC, Pier 1 changes
 *
 *    Rev 1.0   Sep 20 2001 15:59:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:16   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;
// java imports
import java.io.Serializable;
import java.util.Locale;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedText;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.TaxTotalsContainerIfc;
import oracle.retail.stores.domain.stock.ItemInquirySearchCriteriaIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.store.RegionIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tax.TaxHistorySelectionCriteria;
import oracle.retail.stores.domain.tax.TaxHistorySelectionCriteriaIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.Address;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.Phone;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;


//--------------------------------------------------------------------------
/**
    The DataTransaction to perform persistent operations on
    store-related financial information. <P>
        Other operations on store data, such as search store directory
        and store in stock, are located in StoreDirectoryDataTransaction.
        @see oracle.retail.stores.domain.arts.StoreDirectoryDataTransaction
    @version $Revision: /main/19 $
**/
//--------------------------------------------------------------------------
public class StoreDataTransaction extends DataTransaction
{                                                                               // begin class StoreDataTransaction
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.StoreDataTransaction.class);

    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -1456987171534864331L;

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/19 $";
    /**
       The name that links this transaction to a command within DataScript.
    **/
    public static String dataCommandName = "StoreDataTransaction";
    /**
       The name that links this transaction to a command within DataScript.
    **/
    public static String dataSaveName = "SaveStoreDataTransaction";
    /**
     * The name that reads the tax history.
     */
    public static final String READ_TAX_HISTORY         = "ReadTaxHistory";
    /**
       The name that writes data directly to the DB even when most transactions are being queued.
    **/
    public static String notQueuedSaveName = "NotQueuedStoreDataTransaction";
    /**
       An ARTSStore constructed from a store and business day.
    **/
    protected ARTSStore artsStore = null;
    /**
       The Retail Store ID
    **/
    protected String storeID = null;
    /**
       The store status
    **/
    protected StoreStatusIfc storeStatus = null;
    /**
       The department
    **/
    protected DepartmentIfc department = null;

    /**
    The store
    **/
    protected StoreIfc[] store = null;


    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public StoreDataTransaction()
    {
        super(dataCommandName);
    }

    //---------------------------------------------------------------------
    /**
       Class constructor.
    **/
    //---------------------------------------------------------------------
    public StoreDataTransaction(String name)
    {
        super(name);
    }

    //---------------------------------------------------------------------
    /**
       Adds the financial totals information to the store. <P>
       @param  store           The Store.
       @param  businessDate    business date in EYSDate format
       @param  totals          The finanical totals information.
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void addStoreTotals(StoreIfc store,
                               EYSDate businessDate,
                               FinancialTotalsIfc totals)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.addStoreTotals");

        artsStore = new ARTSStore(store, businessDate);
        artsStore.setFinancialTotals(totals);

        // set data actions
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("AddStoreTotals");
        da.setDataObject(artsStore);
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.addStoreTotals");
    }

    //---------------------------------------------------------------------
    /**
       Returns the financial totals for the given store and business day. <P>
       @param  store           The Store.
       @param  businessDate    The business date
       @return The store financial information
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsIfc readStoreTotals(StoreIfc store, EYSDate businessDate)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.readStoreTotals");

        artsStore = new ARTSStore(store, businessDate);

        // set data actions
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadStoreTotals");
        da.setDataObject(artsStore);
        dataActions[0] = da;
        setDataActions(dataActions);

        // read totals
        FinancialTotalsIfc totals =
            (FinancialTotalsIfc) getDataManager().execute(this);

        getTaxHistory(totals);

        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.readStoreTotals");

        return(totals);
    }


    /**
     * Returns the current status for the given store.
     * @param storeID The Store ID.
     * @return
     * @throws DataException when an error occurs.
     */
    @SuppressWarnings("serial")
    public StoreStatusIfc readStoreStatus(String storeID) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.readStoreStatus");

        /*
         * Save the storeID to an instance variable
         */
        this.storeID = storeID;

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            //-------------------------------------------------------------
            /*
              This is the DataAction to get the store status
            */
            //-------------------------------------------------------------
            public Serializable getDataObject()
            {
                return StoreDataTransaction.this.storeID;
            }

            public String getDataOperationName()
            {
                return("ReadStoreStatus");
            }
        };

        setDataActions(dataActions);
        StoreStatusIfc storeStatus = (StoreStatusIfc)getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.readStoreStatus");

        return(storeStatus);
    }

    /**
     * Returns the current status for the given store.
     * @param storeID The Store ID.
     * @return
     * @throws DataException when an error occurs.
     */
    @SuppressWarnings("serial")
    public StoreStatusIfc readStoreStatus(StoreIfc store, EYSDate businessDate) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.readStoreStatus");

        artsStore = new ARTSStore(store, businessDate);

        // set data actions
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadStoreStatus");
        da.setDataObject(artsStore);
        dataActions[0] = da;
        setDataActions(dataActions);

        /*
         * Save the storeID to an instance variable
         */
        /*this.storeID = storeID;
        this.businessDate = businessDate;

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            //-------------------------------------------------------------
            /
              This is the DataAction to get the store status
            /
            //-------------------------------------------------------------
            public Serializable getDataObject()
            {
                return StoreDataTransaction.this.storeID;
            }

            public String getDataOperationName()
            {
                return("ReadStoreStatus");
            }
        };*/

        setDataActions(dataActions);
        StoreStatusIfc storeStatus = (StoreStatusIfc)getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.readStoreStatus");

        return(storeStatus);
    }
    /**
     * Returns the current open business date statuses for the given store.
     * @param storeID     The Store ID.
     * @return
     * @throws DataException when an error occurs.
     * @deprecated As of release 13.1 use  {@link #readStoreStatuses(String, LocaleRequestor)}
     */
    @SuppressWarnings("serial")
    public StoreStatusIfc[] readStoreStatuses(String storeID) throws DataException
    {                                   // begin readStoreStatuses()
        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.readStoreStatuses");

        /*
         * Save the storeID to an instance variable
         */
        this.storeID = storeID;

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            //-------------------------------------------------------------
            /*
              This is the DataAction to get the store status
            */
            //-------------------------------------------------------------
            public Serializable getDataObject()
            {
                return StoreDataTransaction.this.storeID;
            }

            public String getDataOperationName()
            {
                return("ReadStoreStatuses");
            }
        };

        setDataActions(dataActions);
        StoreStatusIfc[] storeStatus = (StoreStatusIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.readStoreStatuses");

        return(storeStatus);
    }                                   // end readStoreStatuses()

    /**
     * Returns the current open business date statuses for the given store.
     * @param storeID The Store ID.
     * @param localeRequestor the requested locales
     * @return
     * @throws DataException when an error occurs.
     */
    public StoreStatusIfc[] readStoreStatuses(String storeID, LocaleRequestor localeRequestor) throws DataException
    {
        logger.debug( "StoreDataTransaction.readStoreStatuses");

        /*
         * Save the storeID to an instance variable
         */
        this.storeID = storeID;

        StringSearchCriteria searchCriteria = new StringSearchCriteria(localeRequestor,
                                                                       StoreDataTransaction.this.storeID);
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("ReadStoreStatuses");
        dataAction.setDataObject(searchCriteria);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        StoreStatusIfc[] storeStatus = (StoreStatusIfc[]) getDataManager().execute(this);

        logger.debug( "StoreDataTransaction.readStoreStatuses");

        return(storeStatus);
    }

    //---------------------------------------------------------------------
    /**
       Returns the financial totals for the given store.
       <P>
       @param  status  The Store.
       @return The store financial information
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsIfc readStoreTotals(StoreStatusIfc status)
        throws DataException
    {
        return(readStoreTotals(status.getStore(), status.getBusinessDate()));
    }

    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    @SuppressWarnings("serial")
    public void refreshStoreStatus(StoreStatusIfc status) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.refreshStoreStatus");

        /*
         * Save the store status to an instance variable
         */
        storeStatus = status;

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            //-------------------------------------------------------------
            /*
              This is the DataAction to read the store status
            */
            //-------------------------------------------------------------
            public Serializable getDataObject()
            {
                return StoreDataTransaction.this.storeStatus;
            }

            public String getDataOperationName()
            {
                return("RefreshStoreStatus");
            }
        };

        setDataActions(dataActions);
        StoreStatusIfc newStatus = (StoreStatusIfc)getDataManager().execute(this);

        /*
         * Copy back the relevent status information
         */
        status.setSignOffOperator(newStatus.getSignOffOperator());
        status.setStatus(newStatus.getStatus());
        status.setCloseTime(newStatus.getCloseTime());

        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.refreshStoreStatus");
    }

    //---------------------------------------------------------------------
    /**
       Creates a new store financial totals entry.  This method should
       be used when opening a store for a new business day.
       <P>
       @param  status     The status of the store
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    @SuppressWarnings("serial")
    public void createStoreTotals(StoreStatusIfc status) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.createStoreTotals");

        artsStore = new ARTSStore(status.getStore(),status.getBusinessDate());

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            //-------------------------------------------------------------
            /*
              This is the DataAction to update the store totals
            */
            //-------------------------------------------------------------
            public Serializable getDataObject()
            {
                return StoreDataTransaction.this.artsStore;
            }

            public String getDataOperationName()
            {
                return("CreateStoreTotals");
            }
        };

        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.createStoreTotals");
    }

    //---------------------------------------------------------------------
    /**
       Updates the current status for the store.  This method should be
       used when updating the status of a store for an existing business
       day.
       <P>
       @param  status     The status of the store
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    @SuppressWarnings("serial")
    public void updateStoreStatus(StoreStatusIfc status) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.updateStoreStatus");

        /*
         * Save the store status to an instance variable
         */
        storeStatus = status;

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            //-------------------------------------------------------------
            /*
              This is the DataAction to update the store status
            */
            //-------------------------------------------------------------
            public Serializable getDataObject()
            {
                return StoreDataTransaction.this.storeStatus;
            }

            public String getDataOperationName()
            {
                return("UpdateStoreStatus");
            }
        };

        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.updateStoreStatus");
    }

    //---------------------------------------------------------------------
    /**
       Closes a store by updating the current status of the store.
       <P>
       @param  status  The store.
       @param  businessDate    The business date
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void closeStore(StoreStatusIfc status) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.closeStore");

        updateStoreStatus(status);

        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.closeStore");
    }

    //---------------------------------------------------------------------
    /**
       Returns a department by its name. <P>
       @param departmentName   the name of the department
       @return department
       @exception  DataException when an error occurs
       @deprecated As of release 13.1 Use @link #getDepartmentByName(String, Locale)
    **/
    //---------------------------------------------------------------------
    public DepartmentIfc getDepartmentByName(String departmentName)
        throws DataException
    {
    	return getDepartmentByName(departmentName, LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
    }

    //---------------------------------------------------------------------
    /**
       Returns a department by its name. <P>
       @param departmentName   the name of the department
       @param locale The Locale object
       @return department
       @exception  DataException when an error occurs
    **/
    //---------------------------------------------------------------------
    public DepartmentIfc getDepartmentByName(String departmentName, Locale lcl)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.getDepartmentByName");

        department = DomainGateway.getFactory().getDepartmentInstance();
        department.setDescription(lcl, departmentName);

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadDepartment");
        da.setDataObject(department);
        dataActions[0] = da;
        setDataActions(dataActions);
        DepartmentIfc dept = (DepartmentIfc)getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "StoreDataTransaction.getDepartmentByName");

        return(dept);
    }
    //  ---------------------------------------------------------------------
    /**
       Returns a store information by its store ID. <P>
       @param storeID   the ID of the store
       @return store
       @exception  DataException when an error occurs
       @deprecated As of release 13.1 use {@link #getStoreInfo(String[], LocaleRequestor)}
    **/
    //---------------------------------------------------------------------
    public StoreIfc[] getStoreInfo(String[] storeIDs)
        throws DataException
    {
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        LocaleRequestor localeRequestor = new LocaleRequestor(locale);
        return getStoreInfo(storeIDs, localeRequestor);
    }

    /**
     * Returns a store information by its store ID. <P>
     * @param storeIDs the ID of the store
     * @param localeRequestor the requested locales
     * @return the store
     * @throws DataException
     */
    public StoreIfc[] getStoreInfo(String[] storeIDs, LocaleRequestor localeRequestor)
        throws DataException
    {
        logger.debug( "StoreDataTransaction.getStoreInfo");

        DataAction da = new DataAction();
        da.setDataOperationName("ReadStoreInfo");

        ItemInquirySearchCriteriaIfc criteria = DomainGateway.getFactory().getItemInquirySearchCriteriaInstance();
        criteria.setStoreIDs(storeIDs);
        criteria.setLocaleRequestor(localeRequestor);
        da.setDataObject(criteria);

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = da;
        setDataActions(dataActions);

        store = (StoreIfc[])getDataManager().execute(this);

        logger.debug("StoreDataTransaction.getStoreInfo");

        return(store);
    }


     /**
      * Returns the store regions. <P>
      * @param inquiry
      * @return
      * @throws DataException when an error occurs
      * @deprecated As of release 13.1
      */
    public RegionIfc[] readStoreRegions(SearchCriteriaIfc inquiry) throws DataException
     {
         if (logger.isDebugEnabled()) logger.debug(
                      "StoreDataTransaction.readStoreRegions");

         // set data actions and execute
         DataActionIfc[] dataActions = new DataActionIfc[1];
         DataAction da = new DataAction();
         da.setDataOperationName("ReadStoreRegions");

         da.setDataObject(inquiry);
         //da.setDataObject(...);
         dataActions[0] = da;
         setDataActions(dataActions);
         RegionIfc[] regionsList = (RegionIfc[])getDataManager().execute(this);

         if (logger.isDebugEnabled()) logger.debug(
                     "StoreDataTransaction.readStoreRegions");

         return(regionsList);
     }

     /**
      * Reads the store information for the given store ID.
      * @param storeID
      * @return
      * @throws DataException
      * @deprecated As of release 13.1 use  {@link #readRegionDistrict(String, LocaleRequestor)}
      */
     public StoreIfc readRegionDistrict(String storeID) throws DataException
     {
         StoreIfc store = DomainGateway.getFactory().getStoreInstance();
         store.setStoreID(storeID);
         DataActionIfc[] dataActions = new DataActionIfc[1];
         DataAction dataAction = new DataAction();
         dataAction.setDataOperationName("ReadRegionDistrict");
         dataAction.setDataObject(store);
         dataActions[0] = dataAction;
         setDataActions(dataActions);
         store = (StoreIfc) getDataManager().execute(this);
         return store;
     }

     /**
      * Reads the store information for the given store ID.
      * @param storeID
      * @param localeRequestor the requested locales
      * @return
      * @throws DataException
      */
     public StoreIfc readRegionDistrict(String storeID, LocaleRequestor localeRequestor)
     throws DataException
     {
         StringSearchCriteria criteria = new StringSearchCriteria(localeRequestor, storeID);

         DataAction dataAction = new DataAction();
         dataAction.setDataObject(criteria);
         dataAction.setDataOperationName("ReadRegionDistrict");

         DataActionIfc[] dataActions = new DataActionIfc[1];
         dataActions[0] = dataAction;
         setDataActions(dataActions);

         StoreIfc store = (StoreIfc) getDataManager().execute(this);
         return store;
     }


     /**
      * Reads the buddy stores information for the given store ID.
      * @param storeID
      * @param localeRequestor the requested locales
      * @return
      * @throws DataException
      */
     public StoreIfc[] readStoresInGroupsByStoreID(String storeID, LocaleRequestor localeRequestor)
     throws DataException
     {
         StringSearchCriteria criteria = new StringSearchCriteria(localeRequestor, storeID);

         DataAction dataAction = new DataAction();
         dataAction.setDataObject(criteria);
         dataAction.setDataOperationName("ReadStoresInGroupsByStoreID");

         DataActionIfc[] dataActions = new DataActionIfc[1];
         dataActions[0] = dataAction;
         setDataActions(dataActions);

         StoreIfc[] stores = (StoreIfc[]) getDataManager().execute(this);
         
         return stores;
     }
     
     //---------------------------------------------------------------------
     /**
        Reads the safe tender descriptors.
        @return TenderDescriptorIfc[]
        @throws DataException.
     **/
     //---------------------------------------------------------------------
     public TenderDescriptorIfc[] readSafeTenders() throws DataException
     {
         DataActionIfc[] dataActions = new DataActionIfc[1];
         DataAction dataAction = new DataAction();
         dataAction.setDataOperationName("ReadSafeTenders");
         dataActions[0] = dataAction;
         setDataActions(dataActions);
         TenderDescriptorIfc[] safeTenderDescriptors = (TenderDescriptorIfc[]) getDataManager().execute(this);
         return safeTenderDescriptors;
     }

     /**
      * This method gets the tax history
      * @param totals
      * @throws DataException
      */
     private void getTaxHistory(FinancialTotalsIfc totals) throws DataException
     {
         DataActionIfc[] dataActions = new DataActionIfc[1];
         TaxHistorySelectionCriteriaIfc criteria = DomainGateway.getFactory().getTaxHistorySelectionCriteriaInstance();

         criteria.setStoreId(artsStore.getPosStore().getStoreID());
         criteria.setBusinessDate(artsStore.getBusinessDate());
         criteria.setCriteriaType(TaxHistorySelectionCriteria.SEARCH_BY_STORE);

         dataActions[0] = createDataAction(criteria, READ_TAX_HISTORY);
         setDataActions(dataActions);

         // Execute the data action.
         totals.setTaxes((TaxTotalsContainerIfc) getDataManager().execute(this));
     }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuffer strResult =
            new StringBuffer("Class:  StoreDataTransaction (Revision ");
        strResult.append(getRevisionNumber()).append(") @")
            .append(hashCode()).append("\n");
        strResult.append("storeID:                            [")
            .append(storeID).append("]\n");
        if (artsStore == null)
        {
            strResult.append("artsStore:                          [null]\n");
        }
        else
        {
            strResult.append(artsStore.toString()).append("\n");
        }
        if (storeStatus == null)
        {
            strResult.append("storeStatus:                        [null]\n");
        }
        else
        {
            strResult.append(storeStatus.toString()).append("\n");
        }
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}                                                                               // end class StoreDataTransaction
