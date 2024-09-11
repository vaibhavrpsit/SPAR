/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/AuditLogTransaction.java /main/10 2011/01/27 19:03:04 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.audit.AuditEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

/**
 * The Audit Log transaction deals with the Audit Log entries.
 */
public class AuditLogTransaction extends DataTransaction implements DataTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 5361442812704733419L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(AuditLogTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * The transactionName name links this transaction to a command within the
     * DataScript.
     */
    public static String transactionName = "AuditLogTransaction";

    /**
     * Default constructor
     */
    public AuditLogTransaction()
    {
        super(transactionName);
    }

    /**
     * Read the Audit Log Entries from the DB.
     * 
     * @param typeCode type code for the type of entry to retrieve
     * @see oracle.retail.stores.domain.audit.AuditEntryIfc
     */
    public AuditEntryIfc[] readAuditLogEntries(int typeCode) throws DataException
    {
        // set data actions and execute
        applyDataObject(typeCode);

        // execute data request
        AuditEntryIfc[] entries = (AuditEntryIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("AuditLogTransaction.readAuditLogEntries");

        return entries;
    }
}
