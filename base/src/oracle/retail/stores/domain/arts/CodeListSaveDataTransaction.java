/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/CodeListSaveDataTransaction.java /main/8 2013/09/05 10:36:17 abondala Exp $
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
 *    mdecama   11/05/08 - Data Transaction for saving CodeList

 * =========================================================================== */

package oracle.retail.stores.domain.arts;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListSaveCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

/**
 * This class represents a Data Transaction for all the CodeList Tables.
 */
public class CodeListSaveDataTransaction extends DataTransaction
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = 7097997358678201548L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.CodeListSaveDataTransaction.class);

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "CodeListSaveDataTransaction";

    /**
     * Data Operation Names
     */
    public static String SAVE_CODE_LIST = "SaveLocalizedCodeList";
    public static String SAVE_CODE_LIST_DEPARTMENT = "SaveLocalizedCodeListDepartment";
    public static String SAVE_CODE_LIST_UNIT_OF_MEASURE = "SaveLocalizedCodeListUnitOfMeasure";
    public static String SAVE_CODE_LIST_DISCOUNT = "SaveLocalizedCodeListDiscount";

    /** List containing the Mapping between a CODE_LIST_ID and a DataOperation **/
    private static Hashtable<String, String> codeListOperations = new Hashtable<String, String>(1);

    /**
     * Default Constructor
     */
    public CodeListSaveDataTransaction()
    {
        super(dataCommandName);
    }

    public CodeListSaveDataTransaction(String name)
    {
        super(name);
    }


    /**
     * Saves a CodeList
     * @param criteria
     * @throws DataException
     */
    public void saveCodeList(CodeListSaveCriteriaIfc criteria) throws DataException
    {
        String dataOperation = getDataOperationName(criteria.getCodeList().getListDescription());

        if (logger.isDebugEnabled())
            logger.debug("CodeListSaveDataTransaction.saveCodeList. DataOperation: " + dataOperation);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName(dataOperation);
        da.setDataObject(criteria);
        dataActions[0] = da;
        setDataActions(dataActions);

        getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("CodeListSaveDataTransaction.saveCodeList. DataOperation: " + dataOperation);

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
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE, SAVE_CODE_LIST_UNIT_OF_MEASURE);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_DEPARTMENT, SAVE_CODE_LIST_DEPARTMENT);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE,
                    SAVE_CODE_LIST_DISCOUNT);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT, SAVE_CODE_LIST_DISCOUNT);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_AMOUNT, SAVE_CODE_LIST_DISCOUNT);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE, SAVE_CODE_LIST_DISCOUNT);
            codeListOperations.put(CodeConstantsIfc.CODE_LIST_PREFERRED_CUSTOMER_DISCOUNT, SAVE_CODE_LIST_DISCOUNT);
        }
        String operationName = codeListOperations.get(listID);

        // Defaults to SAVE_CODE_LIST if not mapped
        if (operationName == null)
            operationName = SAVE_CODE_LIST;

        return operationName;
    }
}
