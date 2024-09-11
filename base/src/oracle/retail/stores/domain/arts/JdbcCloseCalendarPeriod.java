/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCloseCalendarPeriod.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
import oracle.retail.stores.domain.utility.calendar.CalendarLevelIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodKey;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 */
public class JdbcCloseCalendarPeriod extends JdbcCalendarDataOperation
{

      /** revision number supplied by source-code-control system */
      public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  public void execute(DataTransactionIfc dataTransaction,
                       DataConnectionIfc dataConnection,
                       DataActionIfc action)   throws DataException {

     // Figure out where we are
     String methodName = "JdbcCloseCalendarPeriod.execute()";
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

      closeCalendarPeriod(transaction, (JdbcDataConnection) dataConnection);
      if (logger.isDebugEnabled()) logger.debug( methodName);
    }

    protected void closeCalendarPeriod(CalendarDataTransaction transaction,
                                       JdbcDataConnection connection) throws DataException
    {
      // figure out where we are
      String methodName = this.getClass().getName() + ".doClosePeriod()";

      // Get the (hopefully open) period from the given level
      CalendarLevelIfc level[] = new CalendarLevelIfc[1];
      level[0] = transaction.paramLevel;
      ArrayList list =
          super.doFetchAllCalendarPeriods(connection,
                                          level,
                                          transaction.paramEndDateTime);
      // There should be only one period
      if(list.size() != 1)
        throw new DataException(DataException.UNKNOWN,
                                methodName + " - Unable to find open period in level:" + level[0].getLevelName());
      CalendarPeriodIfc period = (CalendarPeriodIfc) list.get(0);

      // Check the period's current endDateTime - if it has already been closed
      //  - something is wrong
      if(period.getEndDateTime() != null)
        throw new DataException(DataException.UNKNOWN, methodName + " - period is already closed");


      // Okay, actually do the close...go get the period's parents
      list =
          super.doFetchParentCalendarPeriodKeys(connection, period.getPeriodKey());
      CalendarPeriodIfc parentPeriods[] = new CalendarPeriodIfc[list.size()];
      for(int i=0; i<list.size(); i++) {
        parentPeriods[i] =
            doFetchCalendarPeriod(connection, (CalendarPeriodKey) list.get(i));
      }


      //...now we can go get the close date & time for that period
      //          (the level knows the period end rules)
      EYSDate endDateTime =
          transaction.paramLevel.getCloseDateTime(parentPeriods,
                                                  period,
                                                  transaction.paramEndDateTime);

      // Finally we can close it
      doCloseCalendarPeriod(connection,
                            period.getPeriodKey(),
                            endDateTime);

      // Go fetch it back.
      period =
          doFetchCalendarPeriod(connection, period.getPeriodKey());
      transaction.setResult(period);
    }

    /**
     * Debug/Logging representation of this class
     * @return String containing class name and version number
     */
    public String toString()
    {
      return "JdbcCloseCalendarPeriod - version: " + getRevisionNumber();
    }

    /**
     * Revision number of the code
     * @return String containing revision number as set by source code control system.
     */
    public String getRevisionNumber()  {
      return Util.parseRevisionNumber(revisionNumber);
    }
}
