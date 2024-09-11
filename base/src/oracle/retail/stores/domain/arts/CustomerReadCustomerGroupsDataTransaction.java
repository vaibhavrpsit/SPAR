/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/CustomerReadCustomerGroupsDataTransaction.java /main/4 2012/08/17 19:19:30 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  08/17/12 - fixing the exisitng customer updates and few other
 *                         issues
 *    acadar    08/10/12 - changes to read customer groups
 *    acadar    08/09/12 - new class
 *    acadar    08/09/12 - changes for XC
 *    acadar    08/09/12 - new class
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;


// -------------------------------------------------------------------------
/**
 * The DataTransaction to perform persistent read operations on the Customer
 * groups .
 */
public class CustomerReadCustomerGroupsDataTransaction extends DataTransaction implements DataTransactionIfc
{

    /** serialVersionUID */
    private static final long serialVersionUID = -5768753417006838807L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/4 $";

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger
            .getLogger(oracle.retail.stores.domain.arts.CustomerReadCustomerGroupsDataTransaction.class);

    // ---------------------------------------------------------------------
    /**
     * Class constructor.
     * <P>
     */
    // ---------------------------------------------------------------------
    public CustomerReadCustomerGroupsDataTransaction()
    {
        super("CustomerReadCustomerGroupsDataTransaction");
    }

    // ---------------------------------------------------------------------
    /**
     * Class constructor.
     * <P>
     *
     * @param transaction name
     */
    // ---------------------------------------------------------------------
    public CustomerReadCustomerGroupsDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Retrieves a list of customer groups and the associated, currently
     * effective discount plans.
     * <P>
     *
     * @return CustomerGroupIfc object
     * @exception DataException when an error occurs
     */
    public CustomerGroupIfc readCustomerGroup(CustomerSearchCriteriaIfc searchCriteria) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("CustomerReadCustomerGroupsDataTransaction.readCustomerGroup");

        CustomerGroupIfc retrievedGroup = null;

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadCustomerGroup");
        da.setDataObject(searchCriteria);
        dataActions[0] = da;
        setDataActions(dataActions);
        retrievedGroup = (CustomerGroupIfc)getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("CustomerReadCustomerGroupsDataTransaction.readCustomerGroup");

        return (retrievedGroup);
    }
    
    /**
     * Retrieves a list of customer groups and the associated, currently
     * effective discount plans.
     * 
     * @return array of CustomerGroupIfc object
     * @exception DataException when an error occurs
     */
    @SuppressWarnings("unchecked")
    public CustomerGroupIfc[] selectCustomerGroups(LocaleRequestor localeReq) throws DataException
    {
        logger.debug("CustomerReadCustomerGroupsDataTransaction.selectCustomerGroups");

        CustomerGroupIfc[] retrievedGroups = null;

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("SelectCustomerGroups");
        CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria();
        criteria.setLocaleRequestor(localeReq);
        da.setDataObject(criteria);
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute
        List<CustomerGroupIfc> custGroupsList = (ArrayList<CustomerGroupIfc>)getDataManager().execute(this);
        retrievedGroups = new CustomerGroupIfc[custGroupsList.size()];
        custGroupsList.toArray(retrievedGroups);

        logger.debug("CustomerReadCustomerGroupsDataTransaction.selectCustomerGroups");

        return (retrievedGroups);
    }    
    
    /**
     * Read discount rules for a group ID
     * @param criteria
     * @return array of DiscountRuleIfc
     * @throws DataException
     */
    public DiscountRuleIfc[] readDiscountRules(CustomerSearchCriteriaIfc searchCriteria) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("CustomerReadCustomerGroupsDataTransaction.readDiscountRules");

        DiscountRuleIfc[] discountRules = null;

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadDiscountRules");
        da.setDataObject(searchCriteria);
        dataActions[0] = da;
        setDataActions(dataActions);
        discountRules = (DiscountRuleIfc[])getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("CustomerReadCustomerGroupsDataTransaction.readDiscountRules");

        return (discountRules);
    }

}
