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

package max.retail.stores.domain.arts;

import java.util.Hashtable;

import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import oracle.retail.stores.domain.arts.CodeListDataTransaction;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

import org.apache.log4j.Logger;

/**
 * This class represents a Data Transaction for all the CodeList Tables.
 */
public class MAXCodeListDataTransaction extends CodeListDataTransaction
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = 7097997358678201548L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXCodeListDataTransaction.class);

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "MAXCodeListDataTransaction";



    /** List containing the Mapping between a CODE_LIST_ID and a DataOperation **/
    private static Hashtable<String, String> codeListOperations = new Hashtable<String, String>(1);

    /**
     * Default Constructor
     */
    public MAXCodeListDataTransaction()
    {
        super(dataCommandName);
    }

    public MAXCodeListDataTransaction(String name)
    {
        super(name);
    }

    
    public CodeListIfc getCodeList(CodeListSearchCriteriaIfc criteria)
			throws DataException {
		String dataOperation = getDataOperationName(criteria.getListID());

		if (logger.isDebugEnabled()) {
			logger.debug("CodeListDataTransaction.getCodeList. DataOperation: "
					+ dataOperation);
		}

		criteria.setSearchType(1);

		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName(dataOperation);
		da.setDataObject(criteria);
		dataActions[0] = da;
		setDataActions(dataActions);

		CodeListIfc codeList = (CodeListIfc) getDataManager().execute(this);

		if (codeList == null) {
			throw new DataException(6,
					"No Code List was returned to CodeListDataTransaction.");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("CodeListDataTransaction.getCodeList. DataOperation: "
					+ dataOperation);
		}
		return codeList;
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
            codeListOperations.put(MAXCodeConstantsIfc.CODE_LIST_DISCOUNT_CARD_DISCOUNT, READ_CODE_LIST_DISCOUNT);
            codeListOperations.put(MAXCodeConstantsIfc.CODE_LIST_CAPILLARY_COUPON_DISCOUNT, READ_CODE_LIST_DISCOUNT);

            
            
        }
        String operationName = codeListOperations.get(listID);

        // Defaults to READ_CODE_LIST if not mapped
        if (operationName == null)
            operationName = READ_CODE_LIST;

        return operationName;
    }
}
