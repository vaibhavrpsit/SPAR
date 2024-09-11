/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCreateCalendar.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
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
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.calendar.BusinessCalendarIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelKeyIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodKeyIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Performs all the actual database accesses required by the other classes
 * in the package.
 * <p>
 * The 360 Commerce Data Manager will create a single instance of this class
 * and ensure all instances of {@link Calendar Calendar},
 * {@link CalendarPeriod CalendarPeriod} &amp;
 * {@link CalendarLevel CalendarLevel} access the database using
 * that single instance.
 */
public class JdbcCreateCalendar extends JdbcCalendarDataOperation
{

      /** revision number supplied by source-code-control system */
      public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  /**
   * Executes the requested SQL statments against the database.
   *
   * @param  dataTransaction     The data transaction
   * @param  dataConnection      The connection to the data source
   * @param  action              The information passed by the valet
   * @exception DataException upon error
  **/
   public void execute(DataTransactionIfc dataTransaction,
                       DataConnectionIfc dataConnection,
                       DataActionIfc action)   throws DataException {

     // Figure out where we are
     String methodName = "JdbcCreateCalendar.execute()";
     if (logger.isDebugEnabled()) logger.debug( methodName);

      // Make sure we've got the right transaction....
      CalendarDataTransaction transaction;
      try {
        transaction = (CalendarDataTransaction) dataTransaction;
      } catch (ClassCastException ex) {
        throw new DataException(DataException.DATA_FORMAT,
                                methodName + ": Invalid dataTransaction (" +
                                dataTransaction.getClass().getName() + ")");
      }
      createCalendar(transaction, (JdbcDataConnection) dataConnection);
      if (logger.isDebugEnabled()) logger.debug( methodName);
    }


    /**
     * createCalendar()
     * Creates a new Calendar in the database, and then fetches it
     */
    protected void createCalendar(CalendarDataTransaction transaction,
                                  JdbcDataConnection connection) throws DataException
    {
      // figure out where we are
      String methodName = "JdncCalendarDataOperation.createCalendar()";

      // Make sure the name is non-empty
      if (transaction.paramName == null || transaction.paramName.equals(""))
        throw new DataException(DataException.DATA_FORMAT,
                                methodName + "- Calendar.name is NULL or empty");

      // Make sure the name hasn't been used already
      if(doFetchCalendarID(connection, transaction.paramName) != 0)
        throw new DataException(DataException.KEY_VIOLATION_ERROR, "Proposed Calendar Name already exists");

      // ... make the new Calendar function
      int calendarID = doGenerateCalendarID(connection);
      doCreateCalendar(connection,
                       calendarID,
                       transaction.paramName);

      // ... make the ROOT CalendarLevel
      CalendarLevelKeyIfc rootLevelKey = DomainGateway.getFactory().
          getCalendarLevelKeyInstance().initialize(calendarID, 0);

      doCreateLevel(connection,
                    rootLevelKey,
                    transaction.paramName + " RootLevel", null);

      // ... make the ROOT CalendarLevel its own parent
      doAssociateLevels(connection,
                        rootLevelKey,
                        rootLevelKey);

      // ... make the ROOT CalendarPeriod (it is it's own parent)
      // Note: rootPeriod lasts the entire 21st Century... I'll be dead by the time this is a problem.
      CalendarPeriodKeyIfc rootPeriodKey = DomainGateway.getFactory().
          getCalendarPeriodKeyInstance().initialize(calendarID, 0, 0);
      EYSDate startDate = DomainGateway.getFactory().getEYSDateInstance();
      EYSDate endDate   = DomainGateway.getFactory().getEYSDateInstance();
      startDate.initialize(2000, 01, 01);
      endDate.initialize(2099, 12, 31, 23, 59, 59);
      CalendarPeriodIfc rootPeriod =
          DomainGateway.getFactory().getCalendarPeriodInstance()
          .initialize(rootPeriodKey,
                      startDate,
                      endDate,
                      -1);

      // Write the rootPeriod in the database
      doCreateCalendarPeriod(connection, rootPeriod);

      // Make sure the rootPeriod is its own parent
      doAssociatePeriods(connection,
                         rootPeriod.getPeriodKey(),
                         rootPeriod.getPeriodKey());


      // ...everything worked ... go load it all back
      BusinessCalendarIfc result = doFetchCalendar(connection, calendarID);
      transaction.setResult(result);
    }

    /**
     * Debug/Logging representation of this class
     * @return String containing class name and version number
     */
    public String toString()
    {
      return "JdbcCreateCalendar - version: " + getRevisionNumber();
    }

    /**
     * Revision number of the code
     * @return String containing revision number as set by source code control system.
     */
    public String getRevisionNumber()  {
      return Util.parseRevisionNumber(revisionNumber);
    }
}
