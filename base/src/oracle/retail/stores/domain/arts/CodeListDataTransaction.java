/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/CodeListDataTransaction.java /main/9 2013/09/05 10:36:17 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   10/15/08 - Transaction to Handle CodeList Operations
 * =========================================================================== */

package oracle.retail.stores.domain.arts;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

/**
 * This class represents a Data Transaction for all the CodeList Tables.
 */
public class CodeListDataTransaction extends DataTransaction
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = 7097997358678201548L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.CodeListDataTransaction.class);

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "CodeListDataTransaction";

    /**
     * Data Operation Names
     */
    public static String READ_CODE_LIST = "ReadCodeList";
    public static String READ_CODE_LIST_DEPARTMENT = "ReadCodeListDepartment";
    public static String READ_CODE_LIST_SHIPPING_METHOD = "ReadCodeListShippingMethod";
    public static String READ_CODE_LIST_UNIT_OF_MEASURE = "ReadCodeListUnitOfMeasure";
    public static String READ_CODE_LIST_DISCOUNT = "ReadCodeListDiscount";

    /** List containing the Mapping between a CODE_LIST_ID and a DataOperation **/
    private static Hashtable<String, String> codeListOperations = new Hashtable<String, String>(1);

    /**
     * Default Constructor
     */
    public CodeListDataTransaction()
    {
        super(dataCommandName);
    }

    public CodeListDataTransaction(String name)
    {
        super(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.pos.manager.ifc.CodeListManagerIfc#getCode(oracle.retail.stores.domain.utility.SearchCodeCriteriaIfc)
     */
    public LocalizedCodeIfc getCode(CodeSearchCriteriaIfc criteria) throws DataException
    {
        String dataOperation = getDataOperationName(criteria.getListID());

        if (logger.isDebugEnabled())
            logger.debug("CodeListDataTransaction.getCode. DataOperation: " + dataOperation);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName(dataOperation);
        da.setDataObject(criteria);
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        LocalizedCodeIfc code = (LocalizedCodeIfc)getDataManager().execute(this);

        if (code == null)
        {
            throw new DataException(DataException.NO_DATA, "No Code List was returned to: " + dataCommandName);
        }

        if (logger.isDebugEnabled())
            logger.debug("CodeListDataTransaction.getCode. DataOperation: " + dataOperation);

        return (code);
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.pos.manager.ifc.CodeListManagerIfc#getCodeList(oracle.retail.stores.domain.utility.SearchCodeListCriteriaIfc)
     */
    public CodeListIfc getCodeList(CodeListSearchCriteriaIfc criteria) throws DataException
    {
        String dataOperation = getDataOperationName(criteria.getListID());

        if (logger.isDebugEnabled())
            logger.debug("CodeListDataTransaction.getCodeList. DataOperation: " + dataOperation);

        // Update the SearchType
        criteria.setSearchType(CodeListSearchCriteriaIfc.SEARCH_CODE_LIST);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName(dataOperation);
        da.setDataObject(criteria);
        dataActions[0] = da;
        setDataActions(dataActions);

        CodeListIfc codeList = (CodeListIfc)getDataManager().execute(this);

        if (codeList == null)
        {
            throw new DataException(DataException.NO_DATA, "No Code List was returned to CodeListDataTransaction.");
        }

        if (logger.isDebugEnabled())
            logger.debug("CodeListDataTransaction.getCodeList. DataOperation: " + dataOperation);

        return (codeList);
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.pos.manager.ifc.CodeListManagerIfc#getCodeListIDs(java.lang.String)
     */
    public List <String> getCodeListIDs(CodeListSearchCriteriaIfc criteria) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("CodeListDataTransaction.getCodeListIDs");

        // Update the SearchType
        criteria.setSearchType(CodeListSearchCriteriaIfc.SEARCH_CODE_LIST_ID);

        DataActionIfc[] dataActions = new DataActionIfc[5];
        DataAction codeListDataAction = new DataAction();
        codeListDataAction.setDataOperationName(READ_CODE_LIST);
        codeListDataAction.setDataObject(criteria);
        dataActions[0] = codeListDataAction;

        DataAction codeListDepartmentDataAction = new DataAction();
        codeListDepartmentDataAction.setDataOperationName(READ_CODE_LIST_DEPARTMENT);
        codeListDepartmentDataAction.setDataObject(criteria);
        dataActions[1] = codeListDepartmentDataAction;

        DataAction codeListShippingDataAction = new DataAction();
        codeListShippingDataAction.setDataOperationName(READ_CODE_LIST_SHIPPING_METHOD);
        codeListShippingDataAction.setDataObject(criteria);
        dataActions[2] = codeListShippingDataAction;

        DataAction codeListDiscountsDataAction = new DataAction();
        codeListDiscountsDataAction.setDataOperationName(READ_CODE_LIST_DISCOUNT);
        codeListDiscountsDataAction.setDataObject(criteria);
        dataActions[3] = codeListDiscountsDataAction;

        DataAction codeListUOMDataAction = new DataAction();
        codeListUOMDataAction.setDataOperationName(READ_CODE_LIST_UNIT_OF_MEASURE);
        codeListUOMDataAction.setDataObject(criteria);
        dataActions[4] = codeListUOMDataAction;

        setDataActions(dataActions);

        // execute data request
        List <String> codeListIDs= (List <String>)getDataManager().execute(this);

        if (codeListIDs == null)
        {
            throw new DataException(DataException.NO_DATA, "No Code List was returned to CodeListDataTransaction.");
        }

        if (logger.isDebugEnabled())
            logger.debug("CodeListDataTransaction.getCodeListIDs.");

        return (codeListIDs);
    }

    /**
     * Retrieves the Data Operation Name
     *
     * @param listID
     * @return
     */
    public static String getDataOperationName(String listID)
    {
        if (codeListOperations.isEmpty())
        {
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE, READ_CODE_LIST_UNIT_OF_MEASURE);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_SHIPPING_METHOD, READ_CODE_LIST_SHIPPING_METHOD);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_DEPARTMENT, READ_CODE_LIST_DEPARTMENT);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE,
                    READ_CODE_LIST_DISCOUNT);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT, READ_CODE_LIST_DISCOUNT);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_AMOUNT, READ_CODE_LIST_DISCOUNT);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE, READ_CODE_LIST_DISCOUNT);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_PREFERRED_CUSTOMER_DISCOUNT, READ_CODE_LIST_DISCOUNT);
        }
        String operationName = codeListOperations.get(listID);

        // Defaults to READ_CODE_LIST if not mapped
        if (operationName == null)
            operationName = READ_CODE_LIST;

        return operationName;
    }
}
