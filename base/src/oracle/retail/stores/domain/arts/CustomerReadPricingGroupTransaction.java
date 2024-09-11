/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/CustomerReadPricingGroupTransaction.java /main/9 2012/08/21 21:50:59 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  08/21/12 - jpa for pricing group
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  12/23/08 - fix base issue
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/15/08 - Update for Customer Module
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    mahising  11/12/08 - added for customer
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
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
 * The DataTransaction to perform persistent read operations on the POS pricing
 * group object.
 */
public class CustomerReadPricingGroupTransaction extends DataTransaction implements DataTransactionIfc
{

	/** serialVersionUID */
	private static final long serialVersionUID = -8299819085070624403L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/9 $";

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger
            .getLogger(oracle.retail.stores.domain.arts.CustomerReadPricingGroupTransaction.class);

    // ---------------------------------------------------------------------
    /**
     * Class constructor.
     * <P>
     */
    // ---------------------------------------------------------------------
    public CustomerReadPricingGroupTransaction()
    {
        super("CustomerReadPricingGroupTransaction");
    }

    // ---------------------------------------------------------------------
    /**
     * Class constructor.
     * <P>
     *
     * @param transaction name
     */
    // ---------------------------------------------------------------------
    public CustomerReadPricingGroupTransaction(String name)
    {
        super(name);
    }

    /**
     * Retrieves a list of customer groups and the associated, currently
     * effective discount plans.
     * <P>
     *
     * @return array of CustomerGroupIfc object
     * @exception DataException when an error occurs
     */
    @SuppressWarnings("unchecked")
    public CustomerGroupIfc[] selectCustomerGroups(LocaleRequestor localeReq) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("CustomerReadPricingGroupTransaction.selectCustomerGroups");

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
        
        List<CustomerGroupIfc> custGroupsList = (ArrayList<CustomerGroupIfc>)getDataManager().execute(this);
        retrievedGroups = new CustomerGroupIfc[custGroupsList.size()];
        custGroupsList.toArray(retrievedGroups);

        if (logger.isDebugEnabled())
            logger.debug("CustomerReadPricingGroupTransaction.selectCustomerGroups");

        return (retrievedGroups);

    }

    // ---------------------------------------------------------------------
    /**
     * Retreives a list of customer pricing groups.
     * <P>
     *
     * @return array of PricingGroupIfc object
     * @exception DataException when an error occurs
     */
    // ---------------------------------------------------------------------
    public PricingGroupIfc[] readPricingGroup(Locale localeReq) throws DataException
    {
        return readPricingGroup(new LocaleRequestor(localeReq));
    }

    // ---------------------------------------------------------------------
    /**
     * Retreives a list of customer pricing groups.
     * <P>
     * 
     * @param sqlLocale Locale Identifier
     * @return array of PricingGroupIfc object
     * @exception DataException when an error occurs
     **/
    // ---------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public PricingGroupIfc[] readPricingGroup(LocaleRequestor localeReq) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("CustomerReadPricingGroupTransaction.readPricingGroup");
        PricingGroupIfc[] retrievedGroups = null;
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadPricingGroup");
        da.setDataObject(localeReq);
        dataActions[0] = da;
        setDataActions(dataActions);
        
        List<PricingGroupIfc> pricingGroupsList = (ArrayList<PricingGroupIfc>)getDataManager().execute(this);
        retrievedGroups = new PricingGroupIfc[pricingGroupsList.size()];
        pricingGroupsList.toArray(retrievedGroups);

        if (logger.isDebugEnabled())
            logger.debug("CustomerReadPricingGroupTransaction.readPricingGroup");

        return (retrievedGroups);

    }
}
