/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/StoreSafeReadDataTransaction.java /main/13 2011/01/27 19:03:04 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:30 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/04/09 16:55:43  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.6  2004/04/08 22:14:54  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.financial.StoreSafeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;

import org.apache.log4j.Logger;

/**
 * This class handles the DataTransaction behavior for reading the StoreSafe and
 * related objects to the database.
 * 
 * @version $Revision: /main/13 $
 */
public class StoreSafeReadDataTransaction extends DataTransaction
{
    private static final long serialVersionUID = -6415363203520751313L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(StoreSafeReadDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "StoreSafeReadDataTransaction";

    /**
     * Class constructor.
     */
    public StoreSafeReadDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     * 
     * @param name data command name
     */
    public StoreSafeReadDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Updates the store safe total and count for loans, pickups or deposits.
     * This method should be used any time a loan, a pickup or deposit is
     * perform.
     * 
     * @param safe The store safe
     * @exception DataException when an error occurs.
     */
    public StoreSafeIfc readStoreSafeTotals(StoreSafeIfc safe) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("StoreSafeReadDataTransaction.readStoreSafeTotals starts");

        applyDataObject(safe);

        safe = (StoreSafeIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("StoreSafeReadDataTransaction.readStoreSafeTotals ends");
        return safe;
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
        // result string
        String strResult = new String("Class: StoreSafeReadDataTransaction (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

}
