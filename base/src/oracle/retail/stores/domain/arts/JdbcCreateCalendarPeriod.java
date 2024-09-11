/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCreateCalendarPeriod.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
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

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelIfc;
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
 */
public class JdbcCreateCalendarPeriod extends JdbcCalendarDataOperation
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
     String methodName = "JdbcCreateCalendarPeriod.execute()";
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

      CalendarPeriodIfc result;

      // We have been given all required info, so just make it
      if ((transaction.paramParentPeriod != null) &&
          (transaction.paramLevel != null) &&
          (transaction.paramStartDateTime != null) &&
          (transaction.paramEndDateTime != null))

       result = createCalendarPeriod((JdbcDataConnection) dataConnection,
                                     transaction.paramLevel.getLevelKey(),
                                     transaction.paramParentPeriod.getPeriodKey(),
                                     transaction.paramStartDateTime,
                                     transaction.paramEndDateTime);


      // We have a level & dateTime so work out period boundaries first
      else if ((transaction.paramLevel != null) &&
               (transaction.paramStartDateTime != null) &&
               (transaction.paramParentPeriod == null) &&
               (transaction.paramEndDateTime == null))

        result = createCalendarPeriod((JdbcDataConnection) dataConnection,
                                      transaction.paramLevel,
                                      transaction.paramStartDateTime,
                                      transaction.paramOpenPeriod);

      else
        throw new DataException(DataException.DATA_FORMAT, "No recognized parameters in dataTransaction");


      // We're done
      transaction.setResult(result);
      if (logger.isDebugEnabled()) logger.debug( methodName);
    }


    /**
     *   Makes a new CalendarPeriod in the database & constructs matching instance.
     */
    protected CalendarPeriodIfc createCalendarPeriod(JdbcDataConnection connection,
                                                     CalendarLevelKeyIfc levelKey,
                                                     CalendarPeriodKeyIfc parentPeriodKey,
                                                     EYSDate startDateTime,
                                                     EYSDate endDateTime) throws DataException {

      // Go get the new periodKey & ReportingPeriodID
      CalendarPeriodKeyIfc periodKey = doGeneratePeriodKey(connection, levelKey);
            int reportingPeriodID = doGenerateReportingPeriodID(connection);


      // Make the period
      CalendarPeriodIfc result =  DomainGateway.getFactory().
          getCalendarPeriodInstance().initialize(periodKey,
                                                 startDateTime,
                                                 endDateTime,
                                                 reportingPeriodID);

       // Make the new period in the database
       doCreateCalendarPeriod(connection, result);

       // ... and associate it with it's parent
       doAssociatePeriods(connection, periodKey, parentPeriodKey);

       // We're finished
       return result;
     }


     /**
      *
      */
     protected CalendarPeriodIfc createCalendarPeriod(JdbcDataConnection connection,
                                                      CalendarLevelIfc level,
                                                      EYSDate dateTime,
                                                      boolean openPeriod) throws DataException
     {

       // figure out where we are
       String methodName = this.getClass().getName() + "doGeneratePeriodID()";

       // Is there already a period in that level for the given time?
       ArrayList list =
           doFetchAllCalendarPeriodKeys(connection,
                                        level.getLevelKey(),
                                        dateTime);
       if(list.size() != 0)
         throw new DataException(DataException.UNKNOWN,
                                 methodName + " - " + level.getLevelName() + " period already exists for " + dateTime.toFormattedString(EYSDate.FORMAT_MMDDYYYY));

       // Okay we can do it - get the list of parentLevels that the target Level has...
       CalendarLevelIfc parentLevels[] = level.getParents();

       // There may not be any parentLevels - there should be.
       if(parentLevels == null)
         throw new DataException(DataException.UNKNOWN, "Can't createCalendarPeriod(level, date) - no ParentLevels defined");

       // Get a list of parentPeriods (one for each parent level)
       list = doFetchAllCalendarPeriods(connection,
                                        parentLevels,
                                        dateTime);

       // There may not be enough parentPeriods -- recurse our way up the tree of levels
       if(list.size() != parentLevels.length) {
         // Recurse our way up the level tree -- for each parentLevel defined
        for(int i=0; i<parentLevels.length; i++) {
          try {
            list.add(createCalendarPeriod(connection, parentLevels[i], dateTime,
                                          openPeriod));
          } catch (DataException ex) {}
        }
       }

       // Turn the ArrayList into an array of parents
       CalendarPeriodIfc parentPeriods[] = new CalendarPeriodIfc[list.size()];
       parentPeriods = (CalendarPeriodIfc[]) list.toArray(parentPeriods);

       // We need a periodKey & reportingPeriodID for the new period
       CalendarPeriodKeyIfc periodKey = doGeneratePeriodKey(connection, level.getLevelKey());
       int reportingPeriodID = doGenerateReportingPeriodID(connection);

       // The level has its own special rules for creating the required CalendarPeriod
       CalendarPeriodIfc result =
           level.createCalendarPeriod(parentPeriods, periodKey, reportingPeriodID, dateTime, openPeriod);

       // Create the CalendarPeriod in the database
       doCreateCalendarPeriod(connection, result);

       // ...and associate it with the various parents
       for(int i=0; i<parentPeriods.length; i++)
         doAssociatePeriods(connection,
                            result.getPeriodKey(),
                            parentPeriods[i].getPeriodKey());

       return result;
     }

   /**
    * Debug/Logging representation of this class
    * @return String containing class name and version number
    */
   public String toString()
   {
     return "JdbcCreateCalendarPeriod - version: " + getRevisionNumber();
   }

   /**
    * Revision number of the code
    * @return String containing revision number as set by source code control system.
    */
   public String getRevisionNumber()  {
     return Util.parseRevisionNumber(revisionNumber);
   }
}
