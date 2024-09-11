/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcFetchAllReportingPeriodIDsBetweenDates.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
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
import java.util.ArrayList;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 */
public class JdbcFetchAllReportingPeriodIDsBetweenDates extends JdbcCalendarDataOperation
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
     String methodName = "JdbcFetchAllReportingPeriodIDsBetweenDates.execute()";
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

      fetchReportingPeriods(transaction, (JdbcDataConnection) dataConnection);
      if (logger.isDebugEnabled()) logger.debug( methodName);
    }


    /**
     * fetchReportingPeriods()
     */
    protected void fetchReportingPeriods(CalendarDataTransaction transaction,
                                         JdbcDataConnection connection) throws DataException
    {
      // figure out where we are
      String methodName = this.getClass().getName() + ".fetchAllReportingPeriodIDs(date, date))";

      // Searching for reporting periods BETWEEN TWO dates & times
      EYSDate startDateTime, endDateTime;

      // selectComplete = false - we're going to include periods partially in the target.
      if (!transaction.paramSelectComplete) {
        // We need to push startDateTime back to beginning of calendar period
        // the given startDateTime falls into.
        startDateTime =
            doFetchCalendarPeriodTime(connection,
                                      transaction.paramLevel.getLevelKey(),
                                      transaction.paramStartDateTime,
                                      true);

        // There may not be a period to push back into
        if (startDateTime == null)
          startDateTime = transaction.paramStartDateTime;

          // ...and push endDateTime forward to end of calendar period the
          // given endDateTime falls into.
        endDateTime =
            doFetchCalendarPeriodTime(connection,
                                      transaction.paramLevel.getLevelKey(),
                                      transaction.paramEndDateTime,
                                      false);

        // There may not be a period to push forward into
        if (endDateTime == null)
          endDateTime = transaction.paramEndDateTime;
      }

      // selectComplete = TRUE
      else {
        // We can just use the two parameters as is
        startDateTime = transaction.paramStartDateTime;
        endDateTime = transaction.paramEndDateTime;
      }


      // Now we can go get the required list of reportingPeriodIDs
      ArrayList result = doFetchAllReportingPeriodIDs(connection,
                                                      transaction.paramLevel.getLevelKey(),
                                                      startDateTime,
                                                      endDateTime);
    // We're done
    transaction.setResult(result);
  }

  /**
   * Debug/Logging representation of this class
   * @return String containing class name and version number
   */
  public String toString()
  {
    return "JdbcFetchAllReportingPeriodIDsBetweenDates - version: " + getRevisionNumber();
  }

  /**
   * Revision number of the code
   * @return String containing revision number as set by source code control system.
   */
  public String getRevisionNumber()  {
    return Util.parseRevisionNumber(revisionNumber);
  }
}
