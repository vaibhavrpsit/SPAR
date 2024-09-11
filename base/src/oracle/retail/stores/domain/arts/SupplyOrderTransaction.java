/* ===========================================================================
* Copyright (c) 2002, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/SupplyOrderTransaction.java /main/11 2011/01/27 19:03:04 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.supply.SupplyOrderIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * The SupplyItemTransaction implements the SupplyItem data operations.
 */
public class SupplyOrderTransaction extends DataTransaction implements DataTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 4650417210304320649L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$KW=@(#); $Ver=rapp.vtg_2.5:5; $EKW";

    /**
     * The name that links this transaction to a command within the DataScript.
     */
    protected static String dataCommandName = "SupplyOrderTransaction";

    /**
     * Class constructor
     */
    public SupplyOrderTransaction()
    {
        super(dataCommandName);
    }

    /**
     * This method saves a SupplyOrder object in the data store.
     * 
     * @param interface of the SupplyOrder object to be saved in the data store.
     * @exception DataException is thrown upon error.
     */
    public SupplyOrderIfc saveSupplyOrder(SupplyOrderIfc supplyOrder) throws DataException
    {
        applyDataObject(supplyOrder);

        supplyOrder = (SupplyOrderIfc) getDataManager().execute(this);

        return supplyOrder;
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
        String strResult = new String("Class: SupplyOrderTransaction (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }
}
