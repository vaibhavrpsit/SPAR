/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/WorkWeekTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.util.Calendar;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
    Work Week Transaction deals with the work weeks in the database for BackOffice
**/
//------------------------------------------------------------------------------
public class WorkWeekTransaction extends DataTransaction
    implements DataTransactionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 13245692024904715L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.WorkWeekTransaction.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       The transactionName name links this transaction to a command within the
       DataScript.
    **/
    public static String transactionName = "WorkWeekTransaction";

    //---------------------------------------------------------------------
    /**
       DataCommand constructor.  Initializes dataOperations and
       dataConnectionPool.
    **/
    //---------------------------------------------------------------------
    public WorkWeekTransaction()
    {
        super(transactionName);
    }

    //---------------------------------------------------------------------
    /**
        Get the start of the current work week
        @return EYSDate the date of the start of the work week
        @exception DataException
    **/
    //---------------------------------------------------------------------
    public EYSDate getCurrentWorkWeekStart()
        throws DataException
    {
        EYSDate workWeekStart = null;
        try
        {
            // set data actions and execute
            DataActionIfc[] dataActions = new DataActionIfc[1];
            DataAction da = new DataAction();
            da.setDataOperationName("ReadWorkWeek");
            dataActions[0] = da;
            setDataActions(dataActions);

            // execute data request
            workWeekStart = (EYSDate) getDataManager().execute(this);
        }
        catch(DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                EYSDate now = getStartDay();
                insertWeek(now);
                workWeekStart = now;
            }
            else
            {
                throw de;
            }
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeTransaction.getCurrentWorkWeekStart");

        return(workWeekStart);
    }

    //---------------------------------------------------------------------
    /**
        Updates the current work week.
        @exception DataException
    **/
    //---------------------------------------------------------------------
    public void updateCurrentWeek()
        throws DataException
    {
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("UpdateWorkWeek");
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeTransaction.updateCurrentWeek");
    }

    //---------------------------------------------------------------------
    /**
        Inserts a new work week into the DB.
        @param date the date of the week start
        @exception DataException
    **/
    //---------------------------------------------------------------------
    public void insertWeek(EYSDate date)
        throws DataException
    {
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("InsertWorkWeek");
        da.setDataObject(date);
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeTransaction.insertWeek");
    }

    //---------------------------------------------------------------------
    /**
        Returns the beginning work week start date.
        @return beginning work week start date
    **/
    //---------------------------------------------------------------------
    public EYSDate getStartDay()
    {
        EYSDate now = DomainGateway.getFactory().getEYSDateInstance();
        Calendar calendar = now.calendarValue();
        int rollAmount = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        if (rollAmount > 0)
        {
            now.add(Calendar.DAY_OF_WEEK,-rollAmount); // back to Sunday for US
        }

        now.setHour(0);
        now.setMinute(0);
        now.setSecond(0);
        now.setMillisecond(0);

        return(now);
    }

    public void updateLastClockEntry(EYSDate weekStart)
        throws DataException
    {
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("UpdateLastEditedClockEntry");
        dataActions[0] = da;
        setDataActions(dataActions);
        da.setDataObject(weekStart);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "WorkWeekTransaction.updateLastEditedClockEntry");
    }

    public EYSDate getLastClockEntry(EYSDate weekStart)
        throws DataException
    {
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadLastEditedClockEntry");
        dataActions[0] = da;
        setDataActions(dataActions);
        da.setDataObject(weekStart);

        // execute data request
        EYSDate lastEntry = (EYSDate) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "WorkWeekTransaction.getLastClockEntry");

        return lastEntry;
    }

    //---------------------------------------------------------------------
    /**
       Returns the revision number of this class.
       <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
