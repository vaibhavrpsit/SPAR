/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/BusinessReadDataTransaction.java /main/16 2012/12/13 10:05:11 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    acadar    08/05/12 - refactoring
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

import org.apache.log4j.Logger;

/**
 * The DataTransaction to perform persistent read operations on the POS Customer
 * object.
 */
public class BusinessReadDataTransaction extends DataTransaction implements DataTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -5885933155331861531L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(BusinessReadDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$KW=; $Ver=; $EKW;";

    /**
     * The name that links this transaction to a command within the DataScript.
     */
    protected static String dataCommandName = "BusinessReadDataTransaction";

    /**
     * Class constructor.
     */
    public BusinessReadDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     */
    public BusinessReadDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Read a Business Customer by business info from the data store.
     * 
     * @param customer A Customer that contains the key values required to
     *            restore the transaction from a persistent store.
     * @return A customer vector that matches the key criteria, null if no
     *         Customer matches.
     * @exception DataException when an error occurs
     * @deprecated as of 14.0. Use {@link lookupBusiness(CustomerSearchCriteriaIfc)}
     */
    @SuppressWarnings("unchecked")
    public CustomerIfc[] lookupBusiness(CustomerIfc customer) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("BusinessReadDataTransaction.lookupBusiness");

        applyDataObject(new ARTSCustomer(customer));

        Vector<CustomerIfc> custVector = (Vector<CustomerIfc>)getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("BusinessReadDataTransaction.lookupBusiness");

        return custVector.toArray(new CustomerIfc[custVector.size()]);
    }
    
    /**
     * Read a Business Customer by business info from the data store.
     * 
     * @param customer A Customer that contains the key values required to
     *            restore the transaction from a persistent store.
     * @return A customer resultList that matches the key criteria, null if no
     *         Customer matches.
     * @exception DataException when an error occurs
     */
    @SuppressWarnings("unchecked")
    public ResultList lookupBusiness(CustomerSearchCriteriaIfc criteria) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("BusinessReadDataTransaction.lookupBusiness");
        
        CustomerIfc[] retrievedCustomersArray = null;
        
        applyDataObject(criteria);

        ResultList resultList = (ResultList)getDataManager().execute(this);
        List<CustomerIfc> custList = resultList.getList();
        
        retrievedCustomersArray = new CustomerIfc[custList.size()];
        custList.toArray(retrievedCustomersArray);
        List<CustomerIfc> customersList = Arrays.asList(retrievedCustomersArray);
        resultList.setList(customersList);

        if (logger.isDebugEnabled())
            logger.debug("BusinessReadDataTransaction.lookupBusiness");

        return resultList;
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class: BusinessReadDataTransaction (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }
}
