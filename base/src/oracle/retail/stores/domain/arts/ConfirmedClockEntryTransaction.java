/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ConfirmedClockEntryTransaction.java /main/13 2013/09/05 10:36:18 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.audit.AuditEntry;
import oracle.retail.stores.domain.audit.AuditEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeClockEntryPair;
import oracle.retail.stores.domain.employee.EmployeeClockEntryPairIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * The Confirmed Clock Entry transaction deals with the Clock Entry pairs in the
 * Time Maintenance service.
 */
public class ConfirmedClockEntryTransaction extends DataTransaction implements DataTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -7114432450360852572L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(ConfirmedClockEntryTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * The transactionName name links this transaction to a command within the
     * DataScript.
     */
    public static String transactionName = "ConfirmedClockEntryTransaction";

    /**
     * DataCommand constructor. Initializes dataOperations and
     * dataConnectionPool.
     */
    public ConfirmedClockEntryTransaction()
    {
        super(transactionName);
    }

    /**
     * Get the confirmed clock entries from the database
     * 
     * @return EmployeeClockEntryPairIfc[] the entries
     * @exception DataException
     */
    public EmployeeClockEntryPairIfc[] getConfirmedClockEntries() throws DataException
    {
        // execute data request
        EmployeeClockEntryPairIfc[] clockEntryPairs = (EmployeeClockEntryPairIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("EmployeeTransaction.readConfirmedClockEntries");

        return (clockEntryPairs);
    }

    /**
     * Remove the selected Confirmed Clock Entry from the DB.
     * 
     * @param pair the entry to remove
     * @exception DataException
     */
    public void removeConfirmedClockEntry(EmployeeClockEntryPairIfc pair) throws DataException
    {
        // generate the Audit Log entry for the removal of this clock entry pair
        AuditEntryIfc entry = instantiateAuditEntry();
        entry.setEntryType(AuditEntryIfc.TYPE_CLOCK_ENTRY);
        entry.setFieldName("IsDeleted");
        entry.setUserID(Integer.parseInt(pair.getManagerID()));
        entry.setObjectID("" + pair.getPairID());
        entry.setOldValue("0");
        entry.setNewValue("1");
        entry.setReasonCode(-1);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();

        // TODO: genNextId should be used for getting a table's unique id
        // JdbcReadLastAuditLogEntryID can be deleted (don't forget to remove it
        // from all the technicians)

        // get the last entry ID
        da.setDataOperationName("ReadLastAuditEntryID");
        dataActions[0] = da;
        setDataActions(dataActions);

        int entryID = ((Integer) getDataManager().execute(this)).intValue();

        da.setDataOperationName("InsertAuditEntry");
        dataActions[0] = da;
        setDataActions(dataActions);

        entry.setEntryID(++entryID);

        // write the audit entry
        da.setDataObject(entry);
        getDataManager().execute(this);

        // do the actual removal of the clock entry
        da.setDataOperationName("RemoveConfirmedClockEntry");
        dataActions[0] = da;
        setDataActions(dataActions);
        da.setDataObject(pair);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("EmployeeTransaction.readConfirmedClockEntries");
    }

    /**
     * Add the selected clock entry pair to the DB.
     * 
     * @param pair the entry to add
     * @exception DataException
     */
    public void addConfirmedClockEntry(EmployeeClockEntryPairIfc pair) throws DataException
    {
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();

        // only audit the pair if it was entered by a manager.
        if (pair.isEdited())
        {
            // create an empty dummy pair for comparison
            EmployeeClockEntryPairIfc comparePair = new EmployeeClockEntryPair(null, null, "", -1, -1, -1, null);

            // audit the entry against the dummy. three audit entries should be
            // created (timeIn, timeOut, and timeType)
            AuditEntry[] auditEntries = auditEntry(comparePair, pair);
            if (auditEntries.length > 0)
            {
                // get the last entry ID
                da.setDataOperationName("ReadLastAuditEntryID");
                dataActions[0] = da;
                setDataActions(dataActions);

                int entryID = ((Integer) getDataManager().execute(this)).intValue();

                da.setDataOperationName("InsertAuditEntry");
                dataActions[0] = da;
                setDataActions(dataActions);

                for (int j = 0; j < auditEntries.length; j++)
                {
                    auditEntries[j].setEntryID(++entryID);

                    // write the audit entry
                    da.setDataObject(auditEntries[j]);
                    getDataManager().execute(this);
                }
            }
        }

        // save the clock entry.
        da.setDataOperationName("AddConfirmedClockEntry");
        dataActions[0] = da;
        setDataActions(dataActions);
        da.setDataObject(pair);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("EmployeeTransaction.addConfirmedClockEntry");
    }

    /**
     * Update the selected Clock Entry Pair in the DB.
     * 
     * @param entry the entry to update
     * @exception DataException
     */
    public void updateConfirmedClockEntry(EmployeeClockEntryPairIfc entry) throws DataException
    {
        // get the list
        EmployeeClockEntryPairIfc[] entries = getConfirmedClockEntries();
        EmployeeClockEntryPairIfc oldEntry = null;

        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();

        // audit the entry.
        if (oldEntry != null)
        {
            AuditEntry[] auditEntries = auditEntry(oldEntry, entry);
            if (auditEntries.length > 0)
            {
                // get the last entry ID
                da.setDataOperationName("ReadLastAuditEntryID");
                dataActions[0] = da;
                setDataActions(dataActions);

                int entryID = ((Integer) getDataManager().execute(this)).intValue();

                da.setDataOperationName("InsertAuditEntry");
                dataActions[0] = da;
                setDataActions(dataActions);

                for (int j = 0; j < auditEntries.length; j++)
                {
                    auditEntries[j].setEntryID(++entryID);

                    // write the audit entry
                    da.setDataObject(auditEntries[j]);
                    getDataManager().execute(this);
                }
            }
        }

        // save the entry to the DB.
        da.setDataOperationName("UpdateConfirmedClockEntry");
        dataActions[0] = da;
        setDataActions(dataActions);
        da.setDataObject(entry);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("EmployeeTransaction.updateConfirmedClockEntry");
    }

    /**
     * Get the last Pair ID.
     * 
     * @exception DataException
     */
    public int getLastPairID() throws DataException
    {
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadLastClockEntryPairID");
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        int pairID = ((Integer) getDataManager().execute(this)).intValue();

        if (logger.isDebugEnabled())
            logger.debug("ConfirmedClockEntryTransaction.getLastPairID");

        return pairID;
    }

    /**
     * Generate an array of Audit Entries based on the old and new values of the
     * object
     * 
     * @param oldPair the old pair
     * @param newPair the new pair
     * @return an array of Audit Entries
     */
    public AuditEntry[] auditEntry(EmployeeClockEntryPairIfc oldPair, EmployeeClockEntryPairIfc newPair)
    {
        Vector<AuditEntryIfc> v = new Vector<AuditEntryIfc>(1);

        boolean oldTimeInIsNull = oldPair.getClockIn() == null || oldPair.getClockIn().getClockEntry() == null;
        boolean newTimeInIsNull = newPair.getClockIn() == null || newPair.getClockIn().getClockEntry() == null;
        boolean timeInIsNull = oldTimeInIsNull || newTimeInIsNull;
        boolean oldTimeOutIsNull = oldPair.getClockOut() == null || oldPair.getClockOut().getClockEntry() == null;
        boolean newTimeOutIsNull = newPair.getClockOut() == null || newPair.getClockOut().getClockEntry() == null;
        boolean timeOutIsNull = oldTimeOutIsNull || newTimeOutIsNull;
        boolean timeInChanged = (!timeInIsNull && (oldPair.getClockIn().getClockEntry().getHour() != newPair
                .getClockIn().getClockEntry().getHour() || oldPair.getClockIn().getClockEntry().getMinute() != newPair
                .getClockIn().getClockEntry().getMinute()));
        boolean timeOutChanged = (!timeOutIsNull && (oldPair.getClockOut().getClockEntry().getHour() != newPair
                .getClockOut().getClockEntry().getHour() || oldPair.getClockOut().getClockEntry().getMinute() != newPair
                .getClockOut().getClockEntry().getMinute()));

        // test for time in change
        if (timeInChanged || (oldTimeInIsNull && !newTimeInIsNull))
        {
            AuditEntryIfc entry = instantiateAuditEntry();
            entry.setEntryType(AuditEntryIfc.TYPE_CLOCK_ENTRY);
            entry.setFieldName("TimeIn");
            entry.setUserID(Integer.parseInt(newPair.getManagerID()));
            entry.setObjectID("" + newPair.getPairID());
            entry.setOldValue(oldTimeInIsNull ? null : oldPair.getClockIn().getClockEntry());
            entry.setNewValue(newTimeInIsNull ? null : newPair.getClockIn().getClockEntry());
            entry.setReasonCode(newPair.getEditReasonCode());

            v.addElement(entry);
        }

        // test for time out change. if we just linked in the Out, then this
        // doesn't get an Audit entry.
        if (!newPair.getJustLinkedOut()
                && (timeOutChanged || (oldTimeOutIsNull && !newTimeOutIsNull) || (newTimeOutIsNull && !oldTimeOutIsNull)))
        {
            AuditEntryIfc entry = instantiateAuditEntry();
            entry.setEntryType(AuditEntryIfc.TYPE_CLOCK_ENTRY);
            entry.setFieldName("TimeOut");
            entry.setUserID(Integer.parseInt(newPair.getManagerID()));
            entry.setObjectID("" + newPair.getPairID());
            entry.setOldValue(oldTimeOutIsNull ? null : oldPair.getClockOut().getClockEntry());
            entry.setNewValue(newTimeOutIsNull ? null : newPair.getClockOut().getClockEntry());
            entry.setReasonCode(newPair.getEditReasonCode());

            v.addElement(entry);
        }

        // reset the Just Linked flag. this is all we use it for
        newPair.setJustLinkedOut(false);

        // test for time out change
        if (oldPair.getTimeType() != newPair.getTimeType())
        {
            AuditEntryIfc entry = instantiateAuditEntry();
            entry.setEntryType(AuditEntryIfc.TYPE_CLOCK_ENTRY);
            entry.setFieldName("TimeType");
            entry.setUserID(Integer.parseInt(newPair.getManagerID()));
            entry.setObjectID("" + newPair.getPairID());
            entry.setOldValue(new Integer(oldPair.getTimeType()));
            entry.setNewValue(new Integer(newPair.getTimeType()));
            entry.setReasonCode(newPair.getEditReasonCode());

            v.addElement(entry);
        }

        AuditEntry[] retValue = new AuditEntry[v.size()];

        int i = 0;
        Enumeration<AuditEntryIfc> e = v.elements();
        while (e.hasMoreElements())
        {
            retValue[i++] = (AuditEntry) e.nextElement();
        }

        return retValue;
    }

    /**
     * Instantiate and return an AuditEntry instance.
     */
    public AuditEntryIfc instantiateAuditEntry()
    {
        return DomainGateway.getFactory().getAuditEntryInstance();
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
	/*
	 * public String getRevisionNumber() { return
	 * (Util.parseRevisionNumber(revisionNumber)); }
	 */
}
