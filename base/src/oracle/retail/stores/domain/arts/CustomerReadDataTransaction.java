/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/CustomerReadDataTransaction.java /main/28 2014/03/04 12:41:11 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    03/03/14 - removing setTotalRecords from selectcustomer method
 *    abondala  11/14/13 - configure the retry flag through the spring context
 *                         in persistenceContext.xml for the data transactions
 *    abondala  11/13/13 - when the pos server is rebooted, the first
 *                         webservice call never makes to the server becuase of
 *                         RMI lookup excetion. Solution is to retry the RMI
 *                         lookup.
 *    jswan     10/23/13 - Fixed unit test issues.
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    cgreene   09/24/12 - Implement maximum customer record retrieval for dtm
 *                         export
 *    abondala  08/17/12 - fixing the exisitng customer updates and few other
 *                         issues
 *    acadar    08/05/12 - refactoring
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    08/01/12 - integration with jpa
 *    cgreene   08/22/11 - removed deprecated methods
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         11/9/2006 7:28:30 PM   Jack G. Swan
 *         Modifided for XML Data Replication and CTR.
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse
 *
 *   Revision 1.7  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:29:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 02 2003 13:47:50   baa
 * I18n Database conversion for customer group
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:34:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:00   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:04:46   msg
 * Initial revision.
 *
 *    Rev 1.1   12 Mar 2002 16:31:18   adp
 * Added readCustomerbyEmail method
 * Resolution for Domain SCR-43: Read customer by email
 *
 *    Rev 1.0   Sep 20 2001 15:57:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:35:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;
import oracle.retail.stores.domain.manager.datareplication.DataReplicationCustomerEntryIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

/**
 * The DataTransaction to perform persistent write operations on the POS
 * Customer object.
 * 
 * @version $Revision: /main/28 $
 * @see oracle.retail.stores.domain.arts.CustomerWriteDataTransaction
 */
public class CustomerReadDataTransaction extends DataTransaction implements DataTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -3638134869338468768L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(CustomerReadDataTransaction.class);

    /**
     * revision number of this class
     */
    public static String revisionNumber = "$Revision: /main/28 $";

    /**
     * Class constructor.
     */
    public CustomerReadDataTransaction()
    {
        super("CustomerReadDataTransaction");
    }

    /**
     * Class constructor.
     * 
     * @param transaction name
     */
    public CustomerReadDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Read a Customer from the data store.
     * 
     * @param customer A Customer that contains the key values required to
     *            restore the transaction from a persistent store.
     * @return The Customer that matches the key criteria, null if no Customer
     *         matches.
     * @exception DataException when an error occurs
     * @deprecated in 14.0. Use {@link readCustomer(CustomerSearchCriteriaIfc customerCriteria) } 
     */
    public CustomerIfc readCustomer(CustomerIfc customer) throws DataException
    {
        logger.debug("CustomerReadDataTransaction.readCustomer");

        CustomerIfc retrievedCustomer = null;
        ARTSCustomer artsCustomer = new ARTSCustomer(customer);
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadCustomer");
        da.setDataObject(artsCustomer);
        dataActions[0] = da;
        setDataActions(dataActions);
        retrievedCustomer = (CustomerIfc)getDataManager().execute(this);

        logger.debug("CustomerReadDataTransaction.readCustomer");

        return (retrievedCustomer);
    }
    
    public CustomerIfc readCustomer(CustomerSearchCriteriaIfc criteria) throws DataException
    {
        logger.debug("CustomerReadDataTransaction.readCustomer");

        CustomerIfc retrievedCustomer = null;

        
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadCustomer");
        da.setDataObject(criteria);
        dataActions[0] = da;
        setDataActions(dataActions);
        
        ResultList resultList = (ResultList)getDataManager().execute(this);
        retrievedCustomer = (CustomerIfc)resultList.getList().get(0);
        
        logger.debug("CustomerReadDataTransaction.readCustomer");

        return (retrievedCustomer);
    }
    
    /**
     * Read a Customer by employee ID from the data store.
     * 
     * @param customer A Customer that contains the key values required to
     *            restore the transaction from a persistent store.
     * @return A customer vector that matches the key criteria, null if no
     *         Customer matches.
     * @exception DataException when an error occurs
     * @deprecated in 14.0.  Use {@link readCustomerbyEmployee(CustomerSearchCriteriaIfc}}
     */
    @SuppressWarnings("unchecked")
    public CustomerIfc[] readCustomerbyEmployee(CustomerIfc customer) throws DataException
    {
        logger.debug("CustomerReadDataTransaction.readCustomerbyEmployee");

        CustomerIfc[] retrievedCustomers = null;

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadCustomerbyEmployee");
        da.setDataObject(customer);
        dataActions[0] = da;
        setDataActions(dataActions);

        List<CustomerIfc> custList = (ArrayList<CustomerIfc>)getDataManager().execute(this);
        retrievedCustomers = new CustomerIfc[custList.size()];
        custList.toArray(retrievedCustomers);


        logger.debug("CustomerReadDataTransaction.readCustomerbyEmployee");

        return (retrievedCustomers);
    }
   
    /**
     * Read a Customer by employee ID from the data store.
     * 
     * @param customer A Customer that contains the key values required to
     *            restore the transaction from a persistent store.
     * @return A customer resultList that matches the key criteria, null if no
     *         Customer matches.
     * @exception DataException when an error occurs
     */
    @SuppressWarnings("unchecked")
    public ResultList readCustomerbyEmployee(CustomerSearchCriteriaIfc criteria) throws DataException
    {
        logger.debug("CustomerReadDataTransaction.readCustomerbyEmployee");
        CustomerIfc[] retrievedCustomersArray = null;
        
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadCustomerbyEmployee");
        da.setDataObject(criteria);
        dataActions[0] = da;
        setDataActions(dataActions);

        ResultList resultList = (ResultList)getDataManager().execute(this);
        List<CustomerIfc> custList = resultList.getList();
        
        retrievedCustomersArray = new CustomerIfc[custList.size()];
        custList.toArray(retrievedCustomersArray);
        List<CustomerIfc> customersList = Arrays.asList(retrievedCustomersArray);
        resultList.setList(customersList);



        logger.debug("CustomerReadDataTransaction.readCustomerbyEmployee");

        return (resultList);
    }
    
    /**
     * Select a list of Customers from the data store based on arbitrary key
     * criteria.
     * 
     * @param customer A Customer that contains first name, last name, at least
     *            one address line, city, state, and postal code. Optional
     *            parameters are a second address line, phone number, and phone
     *            number type.
     * @return A Vector of Customers that match the criteria.
     * @exception DataException when an error occurs
     * @deprecated in 14.0. Use {@link selectCustomers(CustomerSearchCriteriaIfc)}
     */
    @SuppressWarnings("unchecked")
    public CustomerIfc[] selectCustomers(CustomerIfc customer) throws DataException
    {
        logger.debug("CustomerReadDataTransaction.selectCustomers");
      

        CustomerIfc[] retrievedCustomers = null;

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("SelectCustomers");
        da.setDataObject(customer);
        dataActions[0] = da;
        setDataActions(dataActions);

        List<CustomerIfc> custList = (ArrayList<CustomerIfc>)getDataManager().execute(this);
        retrievedCustomers = new CustomerIfc[custList.size()];
        custList.toArray(retrievedCustomers);

        logger.debug("CustomerReadDataTransaction.selectCustomers");

        return (retrievedCustomers);
    }

    /**
     * Select a list of Customers from the data store based on arbitrary key
     * criteria.
     * 
     * @param customer A Customer that contains first name, last name, at least
     *            one address line, city, state, and postal code. Optional
     *            parameters are a second address line, phone number, and phone
     *            number type.
     * @return ResultList of Customers that match the criteria.
     * @exception DataException when an error occurs
     */
    @SuppressWarnings("unchecked")
    public ResultList selectCustomers(CustomerSearchCriteriaIfc criteria) throws DataException
    {
        logger.debug("CustomerReadDataTransaction.selectCustomers");
      
        CustomerIfc[] retrievedCustomersArray = null;
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("SelectCustomers");
        da.setDataObject(criteria);
        dataActions[0] = da;
        setDataActions(dataActions);

        ResultList resultList = (ResultList)getDataManager().execute(this);
        List<CustomerIfc> custList = resultList.getList();
        
        retrievedCustomersArray = new CustomerIfc[custList.size()];
        custList.toArray(retrievedCustomersArray);
        List<CustomerIfc> customersList = Arrays.asList(retrievedCustomersArray);
        resultList.setList(customersList);
        
        logger.debug("CustomerReadDataTransaction.selectCustomers");

        return (resultList);
    }

    /**
     * Retrieves a list of customer groups and the associated, currently
     * effective discount plans.
     * 
     * @return array of CustomerGroupIfc object
     * @exception DataException when an error occurs
     */
    @SuppressWarnings("unchecked")
    public CustomerGroupIfc[] selectCustomerGroups(LocaleRequestor sqlLocale) throws DataException
    {
        logger.debug("CustomerReadDataTransaction.selectCustomerGroups");

        CustomerGroupIfc[] retrievedGroups = null;

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("SelectCustomerGroups");
        CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria();
        criteria.setLocaleRequestor(sqlLocale);
        da.setDataObject(criteria);
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute
        List<CustomerGroupIfc> custGroupsList = (ArrayList<CustomerGroupIfc>)getDataManager().execute(this);
        retrievedGroups = new CustomerGroupIfc[custGroupsList.size()];
        custGroupsList.toArray(retrievedGroups);

        logger.debug("CustomerReadDataTransaction.selectCustomerGroups");

        return (retrievedGroups);
    }

    /**
     * Retrieves customer on the basis of tax id
     * 
     * @param CustomerIfc customer
     * @return array CustomerIfc objects
     * @exception DataException thrown if error occurs
     * @deprecated since 14. No replacement provided.
     */
    @SuppressWarnings("unchecked")
    public CustomerIfc[] readCustomerbyTaxID(CustomerIfc customer) throws DataException
    {
        logger.debug("CustomerReadDataTransaction.readCustomerbyTaxID");

        CustomerIfc[] retrievedCustomers = null;
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadCustomerbyTaxID");
        da.setDataObject(customer);
        dataActions[0] = da;
        setDataActions(dataActions);

        List<CustomerIfc> custList = (ArrayList<CustomerIfc>)getDataManager().execute(this);
        retrievedCustomers = new CustomerIfc[custList.size()];
        
        custList.toArray(retrievedCustomers);        

        logger.debug("CustomerReadDataTransaction.readCustomerbyTaxID");

        return (retrievedCustomers);
    }
    
    
    /**
     * Retrieves customer on the basis of tax id
     * 
     * @param CustomerIfc customer
     * @return resultList of CustomerIfc objects
     * @exception DataException thrown if error occurs
     */
    @SuppressWarnings("unchecked")
    public ResultList readCustomerbyTaxID(CustomerSearchCriteriaIfc criteria) throws DataException
    {
        logger.debug("CustomerReadDataTransaction.readCustomerbyTaxID");
        
        CustomerIfc[] retrievedCustomersArray = null;
        
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadCustomerbyTaxID");
        da.setDataObject(criteria);
        dataActions[0] = da;
        setDataActions(dataActions);

        ResultList resultList = (ResultList)getDataManager().execute(this);
        List<CustomerIfc> custList = resultList.getList();
        
        retrievedCustomersArray = new CustomerIfc[custList.size()];
        custList.toArray(retrievedCustomersArray);
        List<CustomerIfc> customersList = Arrays.asList(retrievedCustomersArray);
        resultList.setList(customersList);
        
        logger.debug("CustomerReadDataTransaction.readCustomerbyTaxID");

        return (resultList);
    }

    /**
     * Retrieves transaction identifiers for customers which haven't been
     * assigned to a batch. The business date and store identifier parameters
     * are optional.
     * 
     * @param storeID store identifier
     * @param columnID indcates if the TLog or Batch Archive column will be
     *            used.
     * @return array of tlog transaction entry objects
     * @exception DataException thrown if error occurs
     */
    public DataReplicationCustomerEntryIfc[] retrieveCustomerNotInBatch(String storeID, int columnID)
        throws DataException
    {
        return retrieveCustomerNotInBatch(storeID, columnID, -1);
    }

    /**
     * Retrieves transaction identifiers for customers which haven't been
     * assigned to a batch. The business date and store identifier parameters
     * are optional.
     * 
     * @param storeID store identifier
     * @param columnID indcates if the TLog or Batch Archive column will be
     *            used.
     * @param maximumCustomersToExport set to -1 if unlimited.
     * @return array of tlog transaction entry objects
     * @exception DataException thrown if error occurs
     */
    public DataReplicationCustomerEntryIfc[] retrieveCustomerNotInBatch(String storeID, int columnID, int maximumCustomersToExport)
        throws DataException
    {
        logger.debug("CustomerReadDataTransaction.retrieveCustomerNotInBatch");

        DataReplicationCustomerEntryIfc[] entries = retrieveCustomerIDsByBatchID(storeID, null,
                POSLogTransactionEntryIfc.NO_BATCH_IDENTIFIED, columnID, maximumCustomersToExport);

        logger.debug("CustomerReadDataTransaction.retrieveCustomerNotInBatch");
        return (entries);
    }

    /**
     * Same as calling {@link #retrieveCustomerIDsByBatchID(String, EYSDate, String, int, int)}
     * with -1 unlimited maximum.
     */
    public DataReplicationCustomerEntryIfc[] retrieveCustomerIDsByBatchID(String storeID, EYSDate businessDate,
            String batchID, int columnID)
        throws DataException
    {
        return retrieveCustomerIDsByBatchID(storeID, businessDate, batchID, columnID, -1);
    }

    /**
     * Retrieves transaction identifiers for TLog creation tlog batch code,
     * business date and store ID. The business date and store identifier
     * parameters are optional.
     * 
     * @param storeID store identifier
     * @param businessDate business date (optional)
     * @param batchID TLog batch identifier
     * @param columnID indicates if the TLog or Batch Archive column will be
     *            updated.
     * @param maximumCustomersToExport set to -1 if unlimited.
     * @return array of transaction tlog entry objects
     * @exception DataException thrown if error occurs
     */
    public DataReplicationCustomerEntryIfc[] retrieveCustomerIDsByBatchID(String storeID, EYSDate businessDate,
            String batchID, int columnID, int maximumCustomersToExport)
        throws DataException
    {
        logger.debug("CustomerReadDataTransaction.retrieveCustomerIDsByBatchID");

        // set data actions and execute
        DataReplicationCustomerEntryIfc searchKey = DomainGateway.getFactory().getDataReplicationCustomerEntry();
        searchKey.setStoreID(storeID);
        searchKey.setBusinessDate(businessDate);
        searchKey.setBatchID(batchID);
        searchKey.setColumnID(columnID);
        searchKey.setMaximumTransactionsToExport(maximumCustomersToExport);
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(searchKey, "RetrieveCustomerIDsByBatchID");
        setDataActions(dataActions);

        DataReplicationCustomerEntryIfc[] entries = (DataReplicationCustomerEntryIfc[])getDataManager().execute(this);

        if (logger.isDebugEnabled())
        {
            logger.debug("CustomerReadDataTransaction.retrieveCustomerIDsByBatchID from data manager: "
                    + getDataManager().getName());

            for (int i = 0; i < dataActions.length; i++)
            {
                logger.debug("DataAction[" + i + "] - " + dataActions[i].getDataOperationName() + ", "
                        + dataActions[i].getDataObject());
            }
        }

        return (entries);
    }

    /**
     * Overridden to allow for second action to read discount groups if missing
     * during retrieval.
     */
    @Override
    public Serializable execute(Object obj) throws DataException
    {
        Serializable result = super.execute(obj);
        if (result instanceof CustomerIfc)
        {
            
        }
        return result;
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
}