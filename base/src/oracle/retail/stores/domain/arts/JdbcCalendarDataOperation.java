/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCalendarDataOperation.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.calendar.BusinessCalendarIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelFactoryIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelKeyIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodKeyIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.utility.Util;

/**
 * A collection of common SQL work methods that're used by various Calendar Data Operations.
 */
abstract public class JdbcCalendarDataOperation
    extends JdbcDataOperation
    implements ARTSDatabaseIfc
{

    /** revision number supplied by source-code-control system */
      public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected static final Logger logger = Logger.getLogger(JdbcCalendarDataOperation.class);
    protected static CalendarLevelFactoryIfc calendarLevelFactory =
        DomainGateway.getFactory().getCalendarLevelFactoryInstance();
///
///  ID GENERATION methods
///

    /**
     *  Actual work method - figure out what the next available CalendarID is
     * @param connection The connection to the data source
     * @return A new calendar ID
     * @throws DataException
     */
    protected int doGenerateCalendarID(JdbcDataConnection connection) throws DataException
      {
        //TODO: change this method of id generation to use DBUtils.genNextID
        int id = 0;

        // figure out where we are
        String methodName = "JdbcCalendarOperation.generateCalendarID()";

        // Cook up the SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_CALENDAR);
        stmt.addMaxFunction(FIELD_CALENDAR_ID);

        try {
          connection.execute(stmt.getSQLString());
          ResultSet rs = (ResultSet) connection.getResult();

          rs.next();
          id = rs.getInt(1) + 1;
          rs.close();
        }
        catch (SQLException se) {
          throw new DataException(DataException.SQL_ERROR, methodName, se);
        }
        catch (Exception e) {
          throw new DataException(DataException.UNKNOWN, methodName, e);
        }
        return id;
      }


    /**
     * Actual work method - Figure out what the next available levelID is for this calendar.
     */
    protected int doGenerateLevelID(JdbcDataConnection connection,
                                           int calendarID) throws DataException
    {
      //TODO: change this method of id generation to use DBUtils.genNextID
      int id = 0;

      // figure out where we are
      String methodName = "JdbcCalendarOperation.generateLevelID()";

      // Cook up the SQL Statement
      SQLSelectStatement stmt = new SQLSelectStatement();
      stmt.setTable(TABLE_CALENDAR_LEVEL);
      stmt.addMaxFunction(FIELD_CALENDAR_LEVEL_ID);
      stmt.addQualifier(FIELD_CALENDAR_ID, calendarID);

      try {
        connection.execute(stmt.getSQLString());
        ResultSet rs = (ResultSet) connection.getResult();

        rs.next();
        id = rs.getInt(1) + 1;
        rs.close();
      }
      catch (SQLException se) {
        throw new DataException(DataException.SQL_ERROR, methodName, se);
      }
      catch (Exception e) {
        throw new DataException(DataException.UNKNOWN, methodName, e);
      }
      return id;
    }


    /**
     * Figure out what the next available PeriodID is for this calendar & level.
     */
    protected CalendarPeriodKeyIfc doGeneratePeriodKey(JdbcDataConnection connection,
                                                       CalendarLevelKeyIfc levelKey) throws DataException
    {
      //TODO: change this method of id generation to use DBUtils.genNextID
      int id = 0;

      // figure out where we are
      String methodName = "JdbcCalendarDataOperation.doGeneratePeriodID()";
      try {

        // Cook up the (un)prepared SQL statement
        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_CALENDAR_PERIOD);
        stmt.addMaxFunction(FIELD_CALENDAR_PERIOD_ID);
        stmt.addQualifier(FIELD_CALENDAR_ID, levelKey.getCalendarID());
        stmt.addQualifier(FIELD_CALENDAR_LEVEL_ID, levelKey.getLevelID());

        connection.execute(stmt.getSQLString());
        ResultSet rs = (ResultSet) connection.getResult();

        rs.next();
        id = rs.getInt(1) + 1;
        rs.close();
      }
      catch (SQLException se) {
        throw new DataException(DataException.SQL_ERROR, methodName, se);
      }
      catch (Exception e) {
        throw new DataException(DataException.UNKNOWN, methodName, e);
      }
      return DomainGateway.getFactory().getCalendarPeriodKeyInstance().
          initialize(levelKey.getCalendarID(), levelKey.getLevelID(), id);
    }


    /**
     * Figure out what the next available ReportingPeriodID is.
     */
    protected int doGenerateReportingPeriodID(JdbcDataConnection connection) throws DataException
    {
      //TODO: change this method of id generation to use DBUtils.genNextID
      int id = 0;

      // figure out where we are
      String methodName = "JdbcCalendarDataOperation.doGenerateReportingPeriodID()";
      try {

        // Cook up the (un)prepared SQL statement
        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.addTable(TABLE_REPORTING_PERIOD_V4);
        stmt.addMaxFunction(FIELD_REPORTING_PERIOD_ID_V4);

        // Execute
        connection.execute(stmt.getSQLString());
        ResultSet rs = (ResultSet) connection.getResult();

        // Extract & Increment
        rs.next();
        id = rs.getInt(1) + 1;
        rs.close();

        // We're done
        return id;
      }
      catch (SQLException se) {
        throw new DataException(DataException.SQL_ERROR, methodName, se);
      }
      catch (Exception e) {
        throw new DataException(DataException.UNKNOWN, methodName, e);
      }
    }


///
///  FETCH methods
///

    /**
     * Actual work method - Find out it's name as well as root level & period IDs
     */
     protected BusinessCalendarIfc doFetchCalendar(JdbcDataConnection connection,
                                                   int calendarID) throws DataException {

      // Figure out where we are for Exception reporting
      String methodName = "JdbcCalendarOperation.doFetchCalendar()";

      BusinessCalendarIfc result = DomainGateway.getFactory().getBusinessCalendarInstance();
      String calendarName = null;
      CalendarPeriodKeyIfc rootPeriodKey = DomainGateway.getFactory().
          getCalendarPeriodKeyInstance().initialize(calendarID, 0, 0);

      try {
        // Make the SQL statement
        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_CALENDAR);
        stmt.addColumn(FIELD_CALENDAR_NAME);
        stmt.addQualifier(FIELD_CALENDAR_ID, calendarID);

        connection.execute(stmt.getSQLString());
        ResultSet rs = (ResultSet) connection.getResult();

        // Go get the data
        if (rs.next())
        {    
    
            // Extract the data we're interested in
            calendarName = rs.getString(FIELD_CALENDAR_NAME);
        }   
            
        // We've finished with that result set
        rs.close();
     }
      catch (SQLException se) {
        throw new DataException(DataException.SQL_ERROR, methodName, se);
      }
      catch (Exception e) {
        throw new DataException(DataException.UNKNOWN, methodName, e);
      }
      
      if (calendarName == null)
      {
              throw new DataException(DataException.NO_DATA);
      }

      // Go get all the levels defined for the calendar
      ArrayList levels = doFetchAllLevels(connection, calendarID);

      // Loop through the levels setting up parent & children references
      for(int i=0; i < levels.size(); i++) {
        // Get that level
        CalendarLevelIfc aLevel = (CalendarLevelIfc) levels.get(i);

        // This level is part of the calendar we're fetching.
        aLevel.setCalendar(result);

        // Go get the list of parent levelLinks for the level we're doing
        ArrayList parentKeys;
        try {
           parentKeys = doFetchParentLevelKeys(connection,
                                               aLevel.getLevelKey());
        }
        catch (Exception e) {
          throw new DataException(DataException.UNKNOWN, methodName + ".updateLevels", e);
        }

        // Loop through the parents
        for (int j = 0; j < parentKeys.size(); j++) {

          // Go looking for this level
          for (int k = 0; k < levels.size(); k++) {
            CalendarLevelIfc aParent = (CalendarLevelIfc) levels.get(k);

            // Match up the parent & children pointers
           if (aParent.getLevelKey().equals((CalendarLevelKeyIfc) parentKeys.get(j))) {
              aLevel.addParentLink(aParent);
              aParent.addChildLink(aLevel);
            }
          }
        }
      }

      // Go fetch the required RootPeriod
      CalendarPeriodIfc rootPeriod =
          doFetchCalendarPeriod(connection,
                                rootPeriodKey);

      // Now that we have all the parts...initialize
      result.initialize(calendarID,
                        calendarName,
                        rootPeriod,
                        levels);
      // we're done
      return result;
    }


    /**
     * Actual work method - resolve CalendarName into ID
     */
    protected int doFetchCalendarID(JdbcDataConnection connection,
                                         String calendarName) throws DataException
    {
      // Figure out where we are for Exception reporting
      String methodName = "JdbcCalendarOperation.doFetchCalendarID()";

      int result = 0;

      // Make the SQL Statement
      SQLSelectStatement stmt = new SQLSelectStatement();
      stmt.setTable(TABLE_CALENDAR);
      stmt.addColumn(FIELD_CALENDAR_ID);
      stmt.addQualifier(FIELD_CALENDAR_NAME, makeSafeString(calendarName));

      try {
        connection.execute(stmt.getSQLString());
        ResultSet rs = (ResultSet) connection.getResult();

        // Go get the data
        rs.next();
        result = rs.getInt(FIELD_CALENDAR_ID);
        rs.close();
      }
      catch (SQLException se) {
        // Don't propagate this exception
        result = 0;
      }
      catch (Exception e) {
        throw new DataException(DataException.UNKNOWN, methodName, e);
      }

      // We/re done
      return result;
    }


    /**
     * Actual work method - fetch a Level from the database
     */
    protected CalendarLevelIfc doFetchLevel(JdbcDataConnection connection,
                                            CalendarLevelKeyIfc levelKey) throws DataException
    {
      // Figure out where we are for Exception reporting
      String methodName = "JdbcCalendarOperation.doFetchLevel()";

      try {
        // Cook up an SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_CALENDAR_LEVEL);
        stmt.addColumn(FIELD_CALENDAR_LEVEL_NAME);
        stmt.addColumn(FIELD_PERIOD_BOUNDARY_CODE);
        stmt.addQualifier(FIELD_CALENDAR_ID, levelKey.getCalendarID());
        stmt.addQualifier(FIELD_CALENDAR_LEVEL_ID, levelKey.getLevelID());

        String sql = stmt.getSQLString();

        connection.execute(stmt.getSQLString());
        ResultSet rs = (ResultSet) connection.getResult();

        // Extract data
        if (rs.next())
        {    
            String levelName = getSafeString(rs,1);
            String boundaryCode = getSafeString(rs, 2);
    
            // Now make the resultant Level
            CalendarLevelIfc result = calendarLevelFactory.
                getCalendarLevelInstance(boundaryCode);
            result.initialize(levelKey, levelName, boundaryCode);
            rs.close();
            return result;
        }
        else
        {
            throw new DataException(DataException.NO_DATA, methodName);
        }
      }
      catch (SQLException se) {
        throw new DataException(DataException.SQL_ERROR, methodName, se);
      }
      catch (Exception e) {
        throw new DataException(DataException.UNKNOWN, methodName, e);
      }
    }


    /**
     * Actual work method - resolve CalendarLevelName into ID
     */
    protected int doFetchCalendarLevelID(JdbcDataConnection connection,
                                         int calendarID,
                                         String calendarLevelName) throws DataException
    {
      // Figure out where we are for Exception reporting
      String methodName = "JdbcCalendarOperation.doFetchCalendarLevelID()";

      int result = 0;

      // Make the SQL Statement
      SQLSelectStatement stmt = new SQLSelectStatement();
      stmt.setTable(TABLE_CALENDAR_LEVEL);
      stmt.addColumn(FIELD_CALENDAR_LEVEL_ID);

      stmt.addQualifier(FIELD_CALENDAR_ID, calendarID);
      stmt.addQualifier(FIELD_CALENDAR_LEVEL_NAME, makeSafeString(calendarLevelName));

      try {
        connection.execute(stmt.getSQLString());
        ResultSet rs = (ResultSet) connection.getResult();

        // Go get the data
        rs.next();
        result = rs.getInt(FIELD_CALENDAR_LEVEL_ID);
        rs.close();
      }
      catch (SQLException se) {
        // Don't propagate this exception
        result = 0;
      }
      catch (Exception e) {
        throw new DataException(DataException.DATA_FORMAT, methodName, e);
      }

      // We/re done
      return result;
    }

    /**
     * Actual work method - Go fetch all levels
     */
    protected ArrayList doFetchAllLevels(JdbcDataConnection connection,
                                         int calendarID) throws DataException
    {

      // Figure out where we are for Exception reporting
      String methodName = "JdbcCalendarOperation.doFetchAllLevels()";

      try {
        // Cook up the SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_CALENDAR_LEVEL);
        stmt.addColumn(FIELD_CALENDAR_ID);
        stmt.addColumn(FIELD_CALENDAR_LEVEL_ID);
        stmt.addColumn(FIELD_CALENDAR_LEVEL_NAME);
        stmt.addColumn(FIELD_PERIOD_BOUNDARY_CODE);
        stmt.addQualifier(FIELD_CALENDAR_ID, calendarID);

        connection.execute(stmt.getSQLString());
        ResultSet rs = (ResultSet) connection.getResult();

        // Make & Populate the result
        ArrayList result = new ArrayList(7);
        while(rs.next()) {

          // Pull out two of the columns & make the levelKey
          CalendarLevelKeyIfc levelKey =
              DomainGateway.getFactory().getCalendarLevelKeyInstance();
          levelKey.initialize(rs.getInt(FIELD_CALENDAR_ID),
                              rs.getInt(FIELD_CALENDAR_LEVEL_ID));

          // Now make the level
          // since getSafeString() not being used, non-null string needs to be trimmed
          // setting null string to empty string causes malfunction
          String boundaryCode = rs.getString(FIELD_PERIOD_BOUNDARY_CODE);
          if (boundaryCode != null)
          {
            boundaryCode = boundaryCode.trim();
          }

          CalendarLevelIfc level = calendarLevelFactory.
              getCalendarLevelInstance(boundaryCode);
          level.initialize(levelKey,
                           rs.getString(FIELD_CALENDAR_LEVEL_NAME),
                           boundaryCode);

          // finally, put it in the list
          result.add(level);
        }

        rs.close();
        return result;
      }
      catch (SQLException se)
      {
        logger.debug(Util.throwableToString(se));
        throw new DataException(DataException.SQL_ERROR, methodName, se);
      }
      catch (Exception e) {
        logger.debug(Util.throwableToString(e));
        throw new DataException(DataException.UNKNOWN, methodName, e);
      }
    }


    /**
      *  Actual work method - get the list of LevelKeys that are this level's parents (there may be more than one)
     */
     protected  ArrayList doFetchParentLevelKeys(JdbcDataConnection connection,
                                                 CalendarLevelKeyIfc levelKey) throws DataException
     {
       // Figure out what our name is for exception reporting
      String methodName = "JdbcCalendarOperation.doFetchParentLevelKeys()";

      ArrayList result = null;
      try {
        // Make the SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_CALENDAR_LEVEL_ASSOCIATION);
        stmt.addColumn(FIELD_PARENT_CALENDAR_LEVEL_ID);
        stmt.addQualifier(FIELD_CALENDAR_ID, levelKey.getCalendarID());
        stmt.addQualifier(FIELD_CHILD_CALENDAR_LEVEL_ID, levelKey.getLevelID());

        // don't want rootLevel as it's own parent
        stmt.addQualifier(FIELD_PARENT_CALENDAR_LEVEL_ID + " <> " + levelKey.getLevelID());

        connection.execute(stmt.getSQLString());
        ResultSet rs = (ResultSet) connection.getResult();

        // Build a list of LevelKeys
        result = new ArrayList(10);
        while(rs.next()) {
          CalendarLevelKeyIfc newLevelKey =
               DomainGateway.getFactory().getCalendarLevelKeyInstance();

           newLevelKey.initialize(levelKey.getCalendarID(),
                                  rs.getInt(FIELD_PARENT_CALENDAR_LEVEL_ID));

           result.add(newLevelKey);
         }
        rs.close();
      }
      catch (SQLException se) {
        throw new DataException(DataException.SQL_ERROR, methodName, se);
      }
      catch (Exception e) {
        throw new DataException(DataException.UNKNOWN, methodName, e);
      }
      return result;
    }

    /**
      *  Actual work method - get the list of LevelIDs that are this level's children
      *    (there may be more than one)
     */
     protected ArrayList doFetchChildrenLevelKeys(JdbcDataConnection connection,
                                                  CalendarLevelKeyIfc levelKey) throws DataException
     {
       // Figure out what our name is for exception reporting
      String methodName = "JdbcCalendarOperation.doFetchChildrenLevelKeys()";

      ArrayList result = null;
      try {
        // Make the SQL Statement
        SQLSelectStatement stmt = new SQLSelectStatement();
        stmt.setTable(TABLE_CALENDAR_LEVEL_ASSOCIATION);
        stmt.addColumn(FIELD_CHILD_CALENDAR_LEVEL_ID);
        stmt.addQualifier(FIELD_CALENDAR_ID, levelKey.getCalendarID());
        stmt.addQualifier(FIELD_PARENT_CALENDAR_LEVEL_ID, levelKey.getLevelID());


        // don't want rootLevel as it's own parent
        stmt.addQualifier(FIELD_CHILD_CALENDAR_LEVEL_ID + " <> " + levelKey.getLevelID());

        connection.execute(stmt.getSQLString());
        ResultSet rs = (ResultSet) connection.getResult();

        // Build a list of ChildIDs
        result = new ArrayList(10);
        while(rs.next()) {
          CalendarLevelKeyIfc newLevelKey =
               DomainGateway.getFactory().getCalendarLevelKeyInstance();

           newLevelKey.initialize(levelKey.getCalendarID(),
                                  rs.getInt(FIELD_CHILD_CALENDAR_LEVEL_ID));

           result.add(newLevelKey);
        }
        rs.close();
      }
      catch (SQLException se) {
        throw new DataException(DataException.SQL_ERROR, methodName, se);
      }
      catch (Exception e) {
        throw new DataException(DataException.UNKNOWN, methodName, e);
      }
      return result;
    }


    /**
    * Actual work method - Fetch a particular known CalendarPeriod from it's CalendarPeriodKey
    */
   protected CalendarPeriodIfc doFetchCalendarPeriod(JdbcDataConnection connection,
                                                     CalendarPeriodKeyIfc periodKey) throws DataException {

     // Figure out where we are for Exception reporting
     String methodName = "JdbcCalendarOperation.doFetchCalendarPeriod(key)";

     CalendarPeriodIfc result;
     EYSDate start = null;
     EYSDate end = null;
     int reportingPeriodID = -1;

     // Go get the periodName
     try {
         String sql = "select " + FIELD_START_DATE_TIME + ", "
                                + FIELD_END_DATE_TIME + ", "
                                + FIELD_REPORTING_PERIOD_ID_V4
                                + " from " + TABLE_CALENDAR_PERIOD +  " CP"
                                + " left outer join " + TABLE_REPORTING_PERIOD_V4 + " RP"
                                + " on (" + "CP." + FIELD_CALENDAR_ID        + " = " + "RP." + FIELD_CALENDAR_ID
                                + " and " + "CP." + FIELD_CALENDAR_LEVEL_ID  + " = " + "RP." + FIELD_CALENDAR_LEVEL_ID
                                + " and " + "CP." + FIELD_CALENDAR_PERIOD_ID + " = " + "RP." + FIELD_START_CALENDAR_PERIOD_ID
                                + " and " + "CP." + FIELD_CALENDAR_LEVEL_ID  + " = " + "RP." + FIELD_END_CALENDAR_PERIOD_ID
                                + ") where CP." + FIELD_CALENDAR_ID + " = " + periodKey.getCalendarID()
                                + "    and CP." + FIELD_CALENDAR_LEVEL_ID + " = " + periodKey.getLevelID()
                                + "    and CP." + FIELD_CALENDAR_PERIOD_ID + " = " + periodKey.getPeriodID();

       connection.execute(sql);
       ResultSet rs = (ResultSet) connection.getResult();

       // Go get the data
       if (rs.next())
       {    
           start = dateToEYSDate(rs, 1);               
           end = dateToEYSDate(rs, 2);
    
           // The next field may be NULL so catch the exception
           try {
             reportingPeriodID = rs.getInt(FIELD_REPORTING_PERIOD_ID_V4);
           }
           catch (SQLException e) {
             reportingPeriodID = -1;
           }
    
           // Finished with that result set
           rs.close();
    
           // Make & Populate the result, and leave
           result = DomainGateway.getFactory().getCalendarPeriodInstance().
               initialize(periodKey, start, end, reportingPeriodID);
    
           return result;
       }
       else
       {
           throw new DataException(DataException.NO_DATA, methodName);
       }
     }                
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }

   }


   protected String setOfLevelIDs(CalendarLevelIfc levels[]) {
     StringBuffer result = new StringBuffer(64).append("(");
     int i=0;
     for(; i<levels.length-1 ; i++) {
       result.append(levels[i].getLevelKey().getLevelID());
       result.append(",");
     }
     result.append(levels[i].getLevelKey().getLevelID());
     result.append(")");

     return result.toString();
   }

    /**
    * Actual work method - Fetch a collection of CalendarPeriods that're in
    * the given CalendarLevelIfc, and contain the given date & time.
    */
   protected ArrayList doFetchAllCalendarPeriods(JdbcDataConnection connection,
                                                 CalendarLevelIfc levels[],
                                                 EYSDate dateTime) throws DataException {

     // Figure out where we are for Exception reporting
     String methodName = "JdbcCalendarOperation.doFetchAllCalendarPeriods(level, date)";

     ArrayList result = new ArrayList(7);

     Timestamp ts = new Timestamp(dateTime.dateValue().getTime());
     CalendarLevelKeyIfc levelKey = levels[0].getLevelKey();

     // Go get the periodName
     String sql = "select " + "CP." + FIELD_CALENDAR_LEVEL_ID + ", "
                                    + FIELD_CALENDAR_PERIOD_ID + ", "
                                    + FIELD_START_DATE_TIME + ", "
                                    + FIELD_END_DATE_TIME + ", "
                                    + FIELD_REPORTING_PERIOD_ID_V4
                            + " from " + TABLE_CALENDAR_PERIOD +  " CP"
                            + " left outer join " + TABLE_REPORTING_PERIOD_V4 + " RP on ("
                            +           "CP." + FIELD_CALENDAR_ID        + " = " + "RP." + FIELD_CALENDAR_ID
                            + " and " + "CP." + FIELD_CALENDAR_LEVEL_ID  + " = " + "RP." + FIELD_CALENDAR_LEVEL_ID
                            + " and " + "CP." + FIELD_CALENDAR_PERIOD_ID + " = " + "RP." + FIELD_START_CALENDAR_PERIOD_ID
                            + " and " + "CP." + FIELD_CALENDAR_LEVEL_ID  + " = " + "RP." + FIELD_END_CALENDAR_PERIOD_ID
                            + ") where CP." + FIELD_CALENDAR_ID          + " = " + levelKey.getCalendarID()
                            + "    and CP." + FIELD_CALENDAR_LEVEL_ID    + " in "+ setOfLevelIDs(levels)
                            + "    and  " + dateToSQLTimestampString(dateTime) + " >= " + FIELD_START_DATE_TIME
                            + "    and (" + dateToSQLTimestampString(dateTime) + " <= " + FIELD_END_DATE_TIME
                            + "      or " + FIELD_END_DATE_TIME + " is null)"
                            + "order by " + FIELD_END_DATE_TIME;
     try {
       connection.execute(sql);
       ResultSet rs = (ResultSet) connection.getResult();

       // Go get the data
       while(rs.next()) {
         int reportingPeriodID = -1;
         CalendarPeriodKeyIfc periodKey = DomainGateway.getFactory().
             getCalendarPeriodKeyInstance().initialize(levelKey.getCalendarID(),
                                                       rs.getInt(FIELD_CALENDAR_LEVEL_ID),
                                                       rs.getInt(FIELD_CALENDAR_PERIOD_ID));

         EYSDate start = timestampToEYSDate(rs.getTimestamp(FIELD_START_DATE_TIME));
         EYSDate end = null;



         // end timestamp may be null
         Timestamp tsEnd = rs.getTimestamp(FIELD_END_DATE_TIME);
         if(tsEnd != null)
           end = timestampToEYSDate(tsEnd);


         // The next field may be NULL so catch the exception
         try {
           reportingPeriodID = rs.getInt(FIELD_REPORTING_PERIOD_ID_V4);
         }
         catch (SQLException e) {}


         // Make, Populate & Add the new period to the result list
         result.add(DomainGateway.getFactory().
                    getCalendarPeriodInstance().initialize(periodKey,
                                                           start, end,
                                                           reportingPeriodID));
       }

       // Finished with that result set
       rs.close();

       // bye bye
       return result;
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }

   }


    /**
    *  Actual work method - get the IDs for all this periods parents (there can be more than one)
    */
   protected ArrayList doFetchParentCalendarPeriodKeys(JdbcDataConnection connection,
                                                       CalendarPeriodKeyIfc periodKey) throws DataException
   {
     // Figure out what our name is for exception reporting
    String methodName = "JdbcCalendarOperation.doFetchParentPeriodIDs()";

    ArrayList result = null;
    try {
      // Make the SQL Statement
      SQLSelectStatement stmt = new SQLSelectStatement();
      stmt.addTable(TABLE_CALENDAR_PERIOD_ASSOCIATION, ALIAS_CALENDAR_PERIOD_ASSOCIATION);
      stmt.addTable(TABLE_CALENDAR_PERIOD, ALIAS_CALENDAR_PERIOD);
      stmt.addColumn(FIELD_PARENT_CALENDAR_LEVEL_ID);
      stmt.addColumn(FIELD_PARENT_CALENDAR_PERIOD_ID);

      // do the JOIN
      stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_PARENT_CALENDAR_PERIOD_ID,
                            ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_PERIOD_ID);

      stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_PARENT_CALENDAR_LEVEL_ID,
                            ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_LEVEL_ID);

      stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_CALENDAR_ID,
                            ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_ID);

      // We're looking for the parents of a particular period
      stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                        FIELD_CALENDAR_ID,
                        Integer.toString(periodKey.getCalendarID()));

      stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                        FIELD_CHILD_CALENDAR_LEVEL_ID,
                        Integer.toString(periodKey.getLevelID()));

      stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                        FIELD_CHILD_CALENDAR_PERIOD_ID,
                        Integer.toString(periodKey.getPeriodID()));

      // Don't want the parent to be itself (ie: no rootPeriod)
      stmt.addQualifier(" not (" + ALIAS_CALENDAR_PERIOD_ASSOCIATION + "."
                                 + FIELD_PARENT_CALENDAR_LEVEL_ID + " = "
                                 + Integer.toString(periodKey.getLevelID())
                                 + " and "
                                 + ALIAS_CALENDAR_PERIOD_ASSOCIATION + "."
                                 + FIELD_PARENT_CALENDAR_PERIOD_ID + " = "
                                 + Integer.toString(periodKey.getLevelID()) + ")");

      stmt.addOrdering(ALIAS_CALENDAR_PERIOD, FIELD_START_DATE_TIME);

      connection.execute(stmt.getSQLString());
      ResultSet rs = (ResultSet) connection.getResult();

      // Build a list of parentKeys
      result = new ArrayList(10);
      while(rs.next()) {
        // Make & Populate a periodKey
        CalendarPeriodKeyIfc key = DomainGateway.getFactory().
            getCalendarPeriodKeyInstance().initialize(periodKey.getCalendarID(),
                                                      rs.getInt(FIELD_PARENT_CALENDAR_LEVEL_ID),
                                                      rs.getInt(FIELD_PARENT_CALENDAR_PERIOD_ID));

        // add it to the list we're making
        result.add(key);
      }
      rs.close();
    }
    catch (Exception e) {
      throw new DataException(DataException.UNKNOWN, methodName, e);
    }
    return result;
  }



    /**
   *  Actual work method - get the IDs for all this periods parents in a particular level
   */
  protected ArrayList doFetchParentCalendarPeriodKeys(JdbcDataConnection connection,
                                                      CalendarPeriodKeyIfc periodKey,
                                                      CalendarLevelKeyIfc  levelKey) throws DataException
  {
    // Figure out what our name is for exception reporting
   String methodName = "JdbcCalendarOperation.doFetchParentPeriodIDs()";

   ArrayList result = null;
   try {
     // Maybe make the SQL Statement
     SQLSelectStatement stmt = new SQLSelectStatement();
     stmt.addTable(TABLE_CALENDAR_PERIOD_ASSOCIATION, ALIAS_CALENDAR_PERIOD_ASSOCIATION);
     stmt.addTable(TABLE_CALENDAR_PERIOD, ALIAS_CALENDAR_PERIOD);
     stmt.addColumn(FIELD_PARENT_CALENDAR_LEVEL_ID);
     stmt.addColumn(FIELD_PARENT_CALENDAR_PERIOD_ID);

     stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_PARENT_CALENDAR_PERIOD_ID,
                           ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_PERIOD_ID);

     stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_PARENT_CALENDAR_LEVEL_ID,
                           ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_LEVEL_ID);

     stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_CALENDAR_ID,
                           ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_ID);

     // We're looking for the parents of a particular period
     stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                       FIELD_CALENDAR_ID,
                       Integer.toString(periodKey.getCalendarID()));

     stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                       FIELD_CHILD_CALENDAR_LEVEL_ID,
                       Integer.toString(periodKey.getLevelID()));

     stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                       FIELD_CHILD_CALENDAR_PERIOD_ID,
                       Integer.toString(periodKey.getPeriodID()));

     // That parent has to be in the given level
     stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                       FIELD_PARENT_CALENDAR_LEVEL_ID,
                       Integer.toString(levelKey.getLevelID()));

     // And we don't want the parent to be itself (ie: no RootPeriod)
     stmt.addQualifier(" not (" + ALIAS_CALENDAR_PERIOD_ASSOCIATION + "."
                                + FIELD_PARENT_CALENDAR_LEVEL_ID + " = "
                                + Integer.toString(periodKey.getLevelID())
                                + " and "
                                + ALIAS_CALENDAR_PERIOD_ASSOCIATION + "."
                                + FIELD_PARENT_CALENDAR_PERIOD_ID + " = "
                                + Integer.toString(periodKey.getLevelID()) + ")");

     stmt.addOrdering(ALIAS_CALENDAR_PERIOD, FIELD_START_DATE_TIME);

     connection.execute(stmt.getSQLString());
     ResultSet rs = (ResultSet) connection.getResult();

     // Build a list of parentKeys
     result = new ArrayList(10);
     while(rs.next()) {
       // Make & Populate a periodKey
       CalendarPeriodKeyIfc key = DomainGateway.getFactory().
           getCalendarPeriodKeyInstance().initialize(periodKey.getCalendarID(),
                                                     rs.getInt(FIELD_PARENT_CALENDAR_LEVEL_ID),
                                                     rs.getInt(FIELD_PARENT_CALENDAR_PERIOD_ID));

       // add it to the list we're making
       result.add(key);
     }
     rs.close();
   }
   catch (SQLException se) {
     throw new DataException(DataException.SQL_ERROR, methodName, se);
   }
   catch (Exception e) {
     throw new DataException(DataException.UNKNOWN, methodName, e);
   }
   return result;
 }


    /**
   *  Actual work method - get the IDs for all this periods children (in all Levels).
   */
  protected ArrayList doFetchChildrenCalendarPeriodKeys(JdbcDataConnection connection,
                                                        CalendarPeriodKeyIfc periodKey) throws DataException
  {
    // Figure out what our name is for exception reporting
   String methodName = "JdbcCalendarOperation.doFetchChildrenPeriodIDs()";

   ArrayList result = null;
   try {
     // Make the SQL Statement
     SQLSelectStatement stmt = new SQLSelectStatement();
     stmt.addTable(TABLE_CALENDAR_PERIOD_ASSOCIATION, ALIAS_CALENDAR_PERIOD_ASSOCIATION);
     stmt.addTable(TABLE_CALENDAR_PERIOD, ALIAS_CALENDAR_PERIOD);
     stmt.addColumn(FIELD_CHILD_CALENDAR_LEVEL_ID);
     stmt.addColumn(FIELD_CHILD_CALENDAR_PERIOD_ID);

     // do the JOIN
     stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_CHILD_CALENDAR_PERIOD_ID,
                           ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_PERIOD_ID);

     stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_CHILD_CALENDAR_LEVEL_ID,
                           ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_LEVEL_ID);

     stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_CALENDAR_ID,
                           ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_ID);

     // We're looking for the children of a particular period
     stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                       FIELD_CALENDAR_ID,
                       Integer.toString(periodKey.getCalendarID()));

     stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                       FIELD_PARENT_CALENDAR_LEVEL_ID,
                       Integer.toString(periodKey.getLevelID()));

     stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                       FIELD_PARENT_CALENDAR_PERIOD_ID,
                       Integer.toString(periodKey.getPeriodID()));

     // Don't want the child to be itself (ie: no rootPeriod)
     stmt.addQualifier(" not (" + ALIAS_CALENDAR_PERIOD_ASSOCIATION + "."
                                + FIELD_CHILD_CALENDAR_LEVEL_ID + " = "
                                + Integer.toString(periodKey.getLevelID())
                                + " and "
                                + ALIAS_CALENDAR_PERIOD_ASSOCIATION + "."
                                + FIELD_CHILD_CALENDAR_PERIOD_ID + " = "
                                + Integer.toString(periodKey.getLevelID()) + ")");

     stmt.addOrdering(ALIAS_CALENDAR_PERIOD, FIELD_START_DATE_TIME);

     connection.execute(stmt.getSQLString());
     ResultSet rs = (ResultSet) connection.getResult();

     // Build a list of ChildIDs
     result = new ArrayList(10);
     while(rs.next()) {
       // Make & Populate a periodKey
       CalendarPeriodKeyIfc key = DomainGateway.getFactory().
           getCalendarPeriodKeyInstance().initialize(periodKey.getCalendarID(),
                                                     rs.getInt(FIELD_CHILD_CALENDAR_LEVEL_ID),
                                                     rs.getInt(FIELD_CHILD_CALENDAR_PERIOD_ID));

       // add it to the list we're making
       result.add(key);
     }
     rs.close();
   }
   catch (SQLException se) {
     throw new DataException(DataException.SQL_ERROR, methodName, se);
   }
   catch (Exception e) {
     throw new DataException(DataException.UNKNOWN, methodName, e);
   }
   return result;
 }


    /**
    *  Actual work method - get the IDs for all this periods children in a particular Level
    */
   protected ArrayList doFetchChildrenCalendarPeriodKeys(JdbcDataConnection connection,
                                                         CalendarPeriodKeyIfc periodKey,
                                                         CalendarLevelKeyIfc levelKey) throws DataException
   {
     // Figure out what our name is for exception reporting
    String methodName = "JdbcCalendarOperation.doFetchChildrenPeriodIDs().ByLevel";

    ArrayList result = null;

    // Maybe make the SQL Statement
    SQLSelectStatement stmt = new SQLSelectStatement();
    stmt.addTable(TABLE_CALENDAR_PERIOD_ASSOCIATION, ALIAS_CALENDAR_PERIOD_ASSOCIATION);
    stmt.addTable(TABLE_CALENDAR_PERIOD, ALIAS_CALENDAR_PERIOD);
    stmt.addColumn(FIELD_CHILD_CALENDAR_LEVEL_ID);
    stmt.addColumn(FIELD_CHILD_CALENDAR_PERIOD_ID);

    stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_CHILD_CALENDAR_PERIOD_ID,
                          ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_PERIOD_ID);

    stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_CHILD_CALENDAR_LEVEL_ID,
                          ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_LEVEL_ID);

    stmt.addJoinQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION, FIELD_CALENDAR_ID,
                          ALIAS_CALENDAR_PERIOD,             FIELD_CALENDAR_ID);

    // We're looking for the children of a particular period
    stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                      FIELD_CALENDAR_ID,
                      Integer.toString(periodKey.getCalendarID()));

    stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                      FIELD_PARENT_CALENDAR_LEVEL_ID,
                      Integer.toString(periodKey.getLevelID()));

    stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                      FIELD_PARENT_CALENDAR_PERIOD_ID,
                      Integer.toString(periodKey.getPeriodID()));

    // That child has to be in the given level
    stmt.addQualifier(ALIAS_CALENDAR_PERIOD_ASSOCIATION,
                      FIELD_CHILD_CALENDAR_LEVEL_ID,
                      Integer.toString(levelKey.getLevelID()));

    // And we don't want the child to be itself (ie: no RootPeriod)
    stmt.addQualifier(" not (" + ALIAS_CALENDAR_PERIOD_ASSOCIATION + "."
                               + FIELD_CHILD_CALENDAR_LEVEL_ID + " = "
                               + Integer.toString(periodKey.getLevelID())
                               + " and "
                               + ALIAS_CALENDAR_PERIOD_ASSOCIATION + "."
                               + FIELD_CHILD_CALENDAR_PERIOD_ID + " = "
                               + Integer.toString(periodKey.getLevelID()) + ")");

    stmt.addOrdering(ALIAS_CALENDAR_PERIOD, FIELD_START_DATE_TIME);
    try {
      connection.execute(stmt.getSQLString());
      ResultSet rs = (ResultSet) connection.getResult();

      // Build a list of ChildIDs
      result = new ArrayList(10);
      while(rs.next()) {
        // Make & Populate a periodKey
        CalendarPeriodKeyIfc key = DomainGateway.getFactory().
            getCalendarPeriodKeyInstance().initialize(periodKey.getCalendarID(),
                                                      rs.getInt(FIELD_CHILD_CALENDAR_LEVEL_ID),
                                                      rs.getInt(FIELD_CHILD_CALENDAR_PERIOD_ID));

        // add it to the list we're making
        result.add(key);
      }
      rs.close();
    }
    catch (Exception e) {
      throw new DataException(DataException.UNKNOWN, methodName, e);
    }
    return result;
  }

    /**
   * Actual work method - Fetch all Period IDs for those CalendarPeriods
   *  which include the given date & time AND are in the given level.
   */
  protected  ArrayList doFetchAllCalendarPeriodKeys(JdbcDataConnection connection,
                                                           CalendarLevelKeyIfc levelKey,
                                                           EYSDate dateTime) throws DataException
  {
    // Figure out what our name is for exception reporting
     String methodName = "JdbcCalendarOperation.doFetchAllCalendarPeriodKeys(level, date)";

     // Make the SQL Statement
     SQLSelectStatement stmt = new SQLSelectStatement();
     stmt.setTable(TABLE_CALENDAR_PERIOD);

     stmt.addColumn(FIELD_CALENDAR_LEVEL_ID);
     stmt.addColumn(FIELD_CALENDAR_PERIOD_ID);

     // Select from particular Calendar Level
     stmt.addQualifier(FIELD_CALENDAR_ID, levelKey.getCalendarID());
     stmt.addQualifier(FIELD_CALENDAR_LEVEL_ID, levelKey.getLevelID());

     // Where start & time are either side of the given time
     stmt.addQualifier(dateToSQLTimestampString(dateTime) + " >= " + FIELD_START_DATE_TIME);
     stmt.addQualifier("(" + dateToSQLTimestampString(dateTime) + " <= " + FIELD_END_DATE_TIME
                       + " or " + FIELD_END_DATE_TIME + " is null)");

     // don't want RootPeriod
     stmt.addQualifier("((" + FIELD_CALENDAR_PERIOD_ID + " <> 0)"
                            + " or "
                            + "(" + FIELD_CALENDAR_LEVEL_ID + " <> 0))");

     // in Start date time order
     stmt.addOrdering(FIELD_START_DATE_TIME);

     try {
       connection.execute(stmt.getSQLString());
       ResultSet rs = (ResultSet) connection.getResult();

       // Go get the list of periodKeys
       ArrayList result = new ArrayList(5);
       while(rs.next()) {
         // Make & Populate a periodKey
         CalendarPeriodKeyIfc key = DomainGateway.getFactory().
             getCalendarPeriodKeyInstance().initialize(levelKey.getCalendarID(),
                                                       rs.getInt(FIELD_CALENDAR_LEVEL_ID),
                                                       rs.getInt(FIELD_CALENDAR_PERIOD_ID));

         // add it to the list we're making
         result.add(key);
       }
       // We're done
       return result;
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
    }


 /**
   * Actual work method - Fetch all Period IDs for those CalendarPeriods
   *  which include the given date & time.
   */
  protected ArrayList doFetchAllCalendarPeriodKeys(JdbcDataConnection connection,
                                                        int calendarID,
                                                        EYSDate dateTime) throws DataException
  {
    // Figure out what our name is for exception reporting
   String methodName = "JdbcCalendarOperation.doFetchAllCalendarPeriodKeys(date)";

   // Make the SQL Statement
   SQLSelectStatement stmt = new SQLSelectStatement();
   stmt.setTable(TABLE_CALENDAR_PERIOD);

   stmt.addColumn(FIELD_CALENDAR_LEVEL_ID);
   stmt.addColumn(FIELD_CALENDAR_PERIOD_ID);

   // Select from particular Calendar
   stmt.addQualifier(FIELD_CALENDAR_ID, calendarID);

   // Where start & time are either side of the given time
   stmt.addQualifier(dateToSQLTimestampString(dateTime) + " >= " + FIELD_START_DATE_TIME);
   stmt.addQualifier("(" + dateToSQLTimestampString(dateTime) + " <= " + FIELD_END_DATE_TIME
                         + " or " + FIELD_END_DATE_TIME + " is null)");

   // don't want RootPeriod
   stmt.addQualifier("((" + FIELD_CALENDAR_PERIOD_ID + " <> 0)"
                          + " or "
                          + "(" + FIELD_CALENDAR_LEVEL_ID + " <> 0))");

   // in Start date time order
   stmt.addOrdering(FIELD_START_DATE_TIME);

   try {
     connection.execute(stmt.getSQLString());
     ResultSet rs = (ResultSet) connection.getResult();

     // Go get the list of periodKeys
     ArrayList result = new ArrayList(5);
     while(rs.next()) {
       // Make & Populate a periodKey
       CalendarPeriodKeyIfc key = DomainGateway.getFactory().
           getCalendarPeriodKeyInstance().initialize(calendarID,
                                                     rs.getInt(FIELD_CALENDAR_LEVEL_ID),
                                                     rs.getInt(FIELD_CALENDAR_PERIOD_ID));

       // add it to the list we're making
       result.add(key);
     }
     // We're done
     return result;
   }
   catch (SQLException se) {
     throw new DataException(DataException.SQL_ERROR, methodName, se);
   }
   catch (Exception e) {
     throw new DataException(DataException.UNKNOWN, methodName, e);
   }
  }

    /**
   * Actual work method - Fetch all Period IDs for those CalendarPeriods
   *  which are in the given level
   */
  protected  ArrayList doFetchAllCalendarPeriodKeys(JdbcDataConnection connection,
                                                    CalendarLevelKeyIfc levelKey) throws DataException
  {
    // Figure out what our name is for exception reporting
    String methodName = "JdbcCalendarOperation.doFetchAllCalendarPeriodKeys(level)";

    // Make the SQL Statement
    SQLSelectStatement stmt = new SQLSelectStatement();
    stmt.setTable(TABLE_CALENDAR_PERIOD);

    stmt.addColumn(FIELD_CALENDAR_LEVEL_ID);
    stmt.addColumn(FIELD_CALENDAR_PERIOD_ID);

    // Select from particular Calendar Level
    stmt.addQualifier(FIELD_CALENDAR_ID, levelKey.getCalendarID());
    stmt.addQualifier(FIELD_CALENDAR_LEVEL_ID, levelKey.getLevelID());

    // don't want RootPeriod
    stmt.addQualifier("((" + FIELD_CALENDAR_PERIOD_ID + " <> 0)"
                           + " or "
                           + "(" + FIELD_CALENDAR_LEVEL_ID + " <> 0))");

    // in Start date time order
    stmt.addOrdering(FIELD_START_DATE_TIME);

    try {
      connection.execute(stmt.getSQLString());
      ResultSet rs = (ResultSet) connection.getResult();

      // Go get the list of periodKeys
      ArrayList result = new ArrayList(5);
      while(rs.next()) {
        // Make & Populate a periodKey
        CalendarPeriodKeyIfc key = DomainGateway.getFactory().
            getCalendarPeriodKeyInstance().initialize(levelKey.getCalendarID(),
                                                      rs.getInt(FIELD_CALENDAR_LEVEL_ID),
                                                      rs.getInt(FIELD_CALENDAR_PERIOD_ID));

        // add it to the list we're making
        result.add(key);
     }
     // We're done
     return result;
   }
   catch (SQLException se) {
     throw new DataException(DataException.SQL_ERROR, methodName, se);
   }
   catch (Exception e) {
     throw new DataException(DataException.UNKNOWN, methodName, e);
   }
  }


    /**
   * Actual work method - Fetch all Period IDs for those ReportingPeriods
   *  which include the given date & time AND are in the given level.
   */
  protected ArrayList doFetchAllReportingPeriodIDs(JdbcDataConnection connection,
                                                   CalendarLevelKeyIfc levelKey,
                                                   EYSDate dateTime) throws DataException
  {
    // Figure out what our name is for exception reporting
   String methodName = "JdbcCalendarOperation.doFetchAllReportingPeriodIDs(date, level)";

   // Maybe make the SQL Statement
   SQLSelectStatement stmt = new SQLSelectStatement();
   stmt.addTable(TABLE_REPORTING_PERIOD_V4, ALIAS_REPORTING_PERIOD_V4);
   stmt.addTable(TABLE_CALENDAR_PERIOD, ALIAS_CALENDAR_PERIOD);

   // We only want one column
   stmt.addColumn(ALIAS_REPORTING_PERIOD_V4, FIELD_REPORTING_PERIOD_ID_V4);

   // do the JOIN
   stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_CALENDAR_ID,
                         ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_ID);

   stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_CALENDAR_LEVEL_ID,
                         ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_LEVEL_ID);


   stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_START_CALENDAR_PERIOD_ID,
                         ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_PERIOD_ID);

   stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_END_CALENDAR_PERIOD_ID,
                         ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_PERIOD_ID);

   // We want only periods in a particular level
   stmt.addQualifier(ALIAS_CALENDAR_PERIOD, FIELD_CALENDAR_ID, Integer.toString(levelKey.getCalendarID()));
   stmt.addQualifier(ALIAS_CALENDAR_PERIOD, FIELD_CALENDAR_LEVEL_ID, Integer.toString(levelKey.getLevelID()));

   // And contain a particular date & time
   stmt.addQualifier(dateToSQLTimestampString(dateTime) + " >= " + ALIAS_CALENDAR_PERIOD + "." + FIELD_START_DATE_TIME);
   stmt.addQualifier("(" + dateToSQLTimestampString(dateTime) + " <= " + ALIAS_CALENDAR_PERIOD + "." + FIELD_END_DATE_TIME
                         + " or " + ALIAS_CALENDAR_PERIOD + "." + FIELD_END_DATE_TIME + " is null)");

   // in Start date time order
   stmt.addOrdering(ALIAS_CALENDAR_PERIOD, FIELD_START_DATE_TIME);

   try {
     String sql = stmt.getSQLString();

     connection.execute(stmt.getSQLString());
     ResultSet rs = (ResultSet) connection.getResult();

     // Go get the list of periodKeys
     ArrayList result = new ArrayList(5);
     while(rs.next()) {
       result.add(Integer.valueOf(rs.getInt(FIELD_REPORTING_PERIOD_ID_V4)));
     }
     // We're done
     return result;
   }
   catch (SQLException se) {
     throw new DataException(DataException.SQL_ERROR, methodName, se);
   }
   catch (Exception e) {
     throw new DataException(DataException.UNKNOWN, methodName, e);
   }
  }

    /**
   * Actual work method - Fetch all Period IDs for those ReportingPeriods
   *  which are in the given level.
   */
  protected ArrayList doFetchAllReportingPeriodIDs(JdbcDataConnection connection,
                                                        CalendarLevelKeyIfc levelKey) throws DataException
  {
    // Figure out what our name is for exception reporting
   String methodName = "JdbcCalendarOperation.doFetchAllReportingPeriodIDs(level)";

   // Maybe make the SQL Statement
   SQLSelectStatement stmt = new SQLSelectStatement();
   stmt.addTable(TABLE_REPORTING_PERIOD_V4, ALIAS_REPORTING_PERIOD_V4);
   stmt.addTable(TABLE_CALENDAR_PERIOD, ALIAS_CALENDAR_PERIOD);

   // We only want one column
   stmt.addColumn(ALIAS_REPORTING_PERIOD_V4, FIELD_REPORTING_PERIOD_ID_V4);

   // do the JOIN
   stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_CALENDAR_ID,
                         ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_ID);

   stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_CALENDAR_LEVEL_ID,
                         ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_LEVEL_ID);

   stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_START_CALENDAR_PERIOD_ID,
                         ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_PERIOD_ID);

   stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_END_CALENDAR_PERIOD_ID,
                         ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_PERIOD_ID);

   // We want only periods in a particular level
   stmt.addQualifier(ALIAS_CALENDAR_PERIOD, FIELD_CALENDAR_ID, Integer.toString(levelKey.getCalendarID()));
   stmt.addQualifier(ALIAS_CALENDAR_PERIOD, FIELD_CALENDAR_LEVEL_ID, Integer.toString(levelKey.getLevelID()));

   // in Start date time order
   stmt.addOrdering(ALIAS_CALENDAR_PERIOD, FIELD_START_DATE_TIME);

   try {
      String sql = stmt.getSQLString();

      connection.execute(stmt.getSQLString());
      ResultSet rs = (ResultSet) connection.getResult();

     // Go get the list of periodKeys
     ArrayList result = new ArrayList(5);
     while(rs.next()) {
       result.add(Integer.valueOf(rs.getInt(FIELD_REPORTING_PERIOD_ID_V4)));
     }
     // We're done
     return result;
   }
   catch (SQLException se) {
     throw new DataException(DataException.SQL_ERROR, methodName, se);
   }
   catch (Exception e) {
     throw new DataException(DataException.UNKNOWN, methodName, e);
   }
  }

    /**
   * Actual work method - Fetch all Period IDs for those ReportingPeriods
   *  which include the given date & time, and are in any level at all.
   */
  protected ArrayList doFetchAllReportingPeriodIDs(JdbcDataConnection connection,
                                                   int calendarID,
                                                   EYSDate dateTime) throws DataException
  {
    // Figure out what our name is for exception reporting
   String methodName = "JdbcCalendarOperations.doFetchAllCalendarPeriodKeys.date()";

   // Maybe make the SQL Statement
    SQLSelectStatement stmt = new SQLSelectStatement();
    stmt.addTable(TABLE_REPORTING_PERIOD_V4, ALIAS_REPORTING_PERIOD_V4);
    stmt.addTable(TABLE_CALENDAR_PERIOD, ALIAS_CALENDAR_PERIOD);

    // We only want one column
    stmt.addColumn(ALIAS_REPORTING_PERIOD_V4, FIELD_REPORTING_PERIOD_ID_V4);

    // do the JOIN
    stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_CALENDAR_ID,
                          ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_ID);

    stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_CALENDAR_LEVEL_ID,
                          ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_LEVEL_ID);


    stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_START_CALENDAR_PERIOD_ID,
                          ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_PERIOD_ID);

    stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_END_CALENDAR_PERIOD_ID,
                          ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_PERIOD_ID);

    // We want reporting Periods in a particular calendar
    stmt.addQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_CALENDAR_ID, Integer.toString(calendarID));

    // And contain a particular date & time
    stmt.addQualifier(dateToSQLTimestampString(dateTime) + " >= " + ALIAS_CALENDAR_PERIOD + "." + FIELD_START_DATE_TIME);
    stmt.addQualifier("(" + dateToSQLTimestampString(dateTime) + " <= " + ALIAS_CALENDAR_PERIOD + "." + FIELD_END_DATE_TIME
                          + " or " + ALIAS_CALENDAR_PERIOD + "." + FIELD_END_DATE_TIME + " is null)");

    // in Start date time order
    stmt.addOrdering(ALIAS_CALENDAR_PERIOD, FIELD_START_DATE_TIME);

    try {
      String sql = stmt.getSQLString();

      connection.execute(stmt.getSQLString());
      ResultSet rs = (ResultSet) connection.getResult();

     // Go get the list of periodKeys
     ArrayList result = new ArrayList(5);
     while(rs.next()) {
       result.add(Integer.valueOf(rs.getInt(FIELD_REPORTING_PERIOD_ID_V4)));
     }
     // We're done
     return result;
   }
   catch (SQLException se) {
     throw new DataException(DataException.SQL_ERROR, methodName, se);
   }
   catch (Exception e) {
     throw new DataException(DataException.UNKNOWN, methodName, e);
   }
  }



    /**
   * Actual work method - Fetch all Period IDs for those ReportingPeriods
   *  between the given dates & times, and are in the given level.
   */
  protected ArrayList doFetchAllReportingPeriodIDs(JdbcDataConnection connection,
                                                   CalendarLevelKeyIfc levelKey,
                                                   EYSDate startDateTime,
                                                   EYSDate endDateTime) throws DataException
  {
    // Figure out what our name is for exception reporting
    String methodName = "JdbcCalendarOperation.doFetchAllReportingPeriodKeys(level, date, date)";

    // Maybe make the SQL Statement
    SQLSelectStatement stmt = new SQLSelectStatement();
    stmt.addTable(TABLE_REPORTING_PERIOD_V4, ALIAS_REPORTING_PERIOD_V4);
    stmt.addTable(TABLE_CALENDAR_PERIOD, ALIAS_CALENDAR_PERIOD);

    // We only want one column
    stmt.addColumn(ALIAS_REPORTING_PERIOD_V4, FIELD_REPORTING_PERIOD_ID_V4);

    // do the JOIN
    stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_CALENDAR_ID,
                          ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_ID);

    stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_CALENDAR_LEVEL_ID,
                          ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_LEVEL_ID);


    stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_START_CALENDAR_PERIOD_ID,
                          ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_PERIOD_ID);

    stmt.addJoinQualifier(ALIAS_REPORTING_PERIOD_V4, FIELD_END_CALENDAR_PERIOD_ID,
                          ALIAS_CALENDAR_PERIOD,     FIELD_CALENDAR_PERIOD_ID);

    // We want only periods in a particular level
    stmt.addQualifier(ALIAS_CALENDAR_PERIOD, FIELD_CALENDAR_ID, Integer.toString(levelKey.getCalendarID()));
    stmt.addQualifier(ALIAS_CALENDAR_PERIOD, FIELD_CALENDAR_LEVEL_ID, Integer.toString(levelKey.getLevelID()));

    // And contain a particular date & time
    stmt.addQualifier(dateToSQLTimestampString(startDateTime) + " <= " + ALIAS_CALENDAR_PERIOD + "." + FIELD_START_DATE_TIME);
    stmt.addQualifier("(" + dateToSQLTimestampString(endDateTime) + " >= " + ALIAS_CALENDAR_PERIOD + "." + FIELD_END_DATE_TIME
                          + " or " + ALIAS_CALENDAR_PERIOD + "." + FIELD_END_DATE_TIME + " is null)");

    // in Start date time order
    stmt.addOrdering(ALIAS_CALENDAR_PERIOD, FIELD_START_DATE_TIME);

    try {
      String sql = stmt.getSQLString();

      connection.execute(stmt.getSQLString());
      ResultSet rs = (ResultSet) connection.getResult();

     // Go get the list of periodKeys
     ArrayList result = new ArrayList(5);
     while(rs.next()) {
       result.add(Integer.valueOf(rs.getInt(FIELD_REPORTING_PERIOD_ID_V4)));
     }
     // We're done
     return result;
   }
   catch (SQLException se) {
     throw new DataException(DataException.SQL_ERROR, methodName, se);
   }
   catch (Exception e) {
     throw new DataException(DataException.UNKNOWN, methodName, e);
   }
  }


    /**
   * Actual work method - Get the start or end date &amp; time for
   *  the given calendarPeriod (from the database).
   */
  protected EYSDate doFetchCalendarPeriodTime(JdbcDataConnection  connection,
                                              CalendarPeriodKeyIfc periodKey,
                                               boolean start) throws DataException
  {
    // Figure out what our name is for exception reporting
   String methodName = "JdbcCalendarDataOperation.doFetchCalendarPeriodTime(period)";

   // Make the SQL Statement
   SQLSelectStatement stmt = new SQLSelectStatement();
   stmt.setTable(TABLE_CALENDAR_PERIOD);

   // We want either the Start or End time
   if(start)
     stmt.addColumn(FIELD_START_DATE_TIME);
   else
     stmt.addColumn(FIELD_END_DATE_TIME);

     // For a particular period
   stmt.addQualifier(FIELD_CALENDAR_ID, Integer.toString(periodKey.getCalendarID()));
   stmt.addQualifier(FIELD_CALENDAR_LEVEL_ID, Integer.toString(periodKey.getLevelID()));
   stmt.addQualifier(FIELD_CALENDAR_PERIOD_ID, Integer.toString(periodKey.getPeriodID()));

   try {
     // Go get the result set
     connection.execute(stmt.getSQLString());
     ResultSet rs = (ResultSet) connection.getResult();
     rs.next();

     // Get the required timestamp
     Timestamp ts = rs.getTimestamp(1);

     // There may not be a timestamp on that period
     if(ts == null)
       return null;

     // Otherwise return the right date & time.
     return timestampToEYSDate(ts);
   }
   catch (SQLException se) {
     throw new DataException(DataException.SQL_ERROR, methodName, se);
   }
   catch (Exception e) {
     throw new DataException(DataException.UNKNOWN, methodName, e);
   }
  }


    /**
   * Actual work method - Fetch the startDateTime (or endDateTime) for
   * the CalendarPeriod in the given LEVEL that contains the given
   * date & time.
   * <p>
   * If the given date &amp; time occur in a gap between two periods
   * then return null;
   */
  protected EYSDate doFetchCalendarPeriodTime(JdbcDataConnection connection,
                                              CalendarLevelKeyIfc levelKey,
                                              EYSDate dateTime,
                                              boolean start) throws DataException
  {
    // Figure out what our name is for exception reporting
   String methodName = "JdbcCalendarOperation.doFetchCalendarPeriodTime(level, date)";

   // Make the SQL Statement
   SQLSelectStatement stmt = new SQLSelectStatement();
   stmt.setTable(TABLE_CALENDAR_PERIOD);

   // We want either the Start or End time
   if(start)
     stmt.addColumn(FIELD_START_DATE_TIME);
   else
     stmt.addColumn(FIELD_END_DATE_TIME);

   // We want only periods in a particular level
   stmt.addQualifier(FIELD_CALENDAR_ID, Integer.toString(levelKey.getCalendarID()));
   stmt.addQualifier(FIELD_CALENDAR_LEVEL_ID, Integer.toString(levelKey.getLevelID()));

   // And contain a particular date & time
   stmt.addQualifier(dateToSQLTimestampString(dateTime) + " >= " + FIELD_START_DATE_TIME);
   stmt.addQualifier("(" + dateToSQLTimestampString(dateTime) + " <= " + FIELD_END_DATE_TIME
                           + " or " + FIELD_END_DATE_TIME + " is null)");
   try {
     connection.execute(stmt.getSQLString());
     ResultSet rs = (ResultSet) connection.getResult();

     // There may not be a period matching the criteria
     if(! rs.next())
       return null;

     // Get the required timestamp
     Timestamp ts = rs.getTimestamp(1);

     // That field may be null
     if( ts == null)
       return null;

     // Otherwise translate the field
     return timestampToEYSDate(ts);
   }

   catch (SQLException se) {
     throw new DataException(DataException.SQL_ERROR, methodName, se);
   }
   catch (Exception e) {
     throw new DataException(DataException.UNKNOWN, methodName, e);
   }
  }

    /**
   * Actual work method
   */
  protected boolean doLevelHasPeriods(JdbcDataConnection connection,
                                       CalendarLevelKeyIfc levelKey) throws DataException
  {
    // Figure out what our name is for exception reporting
   String methodName = "JdbcCalendarOperation.doLevelHasPeriods()";

    // Assume none
    boolean result = false;

    // Make the SQL Statement
    SQLSelectStatement stmt = new SQLSelectStatement();
    stmt.setTable(TABLE_CALENDAR_PERIOD_ASSOCIATION);

    // Just get one of them
    stmt.addColumn("count(" + FIELD_CHILD_CALENDAR_PERIOD_ID + ")");

    // Periods in this level
    stmt.addQualifier(FIELD_CALENDAR_ID, levelKey.getCalendarID());
    stmt.addQualifier(FIELD_CHILD_CALENDAR_LEVEL_ID, levelKey.getLevelID());

    // Don't count RootGroups
    stmt.addQualifier("not (    (" + FIELD_CHILD_CALENDAR_LEVEL_ID  + " = " + FIELD_PARENT_CALENDAR_LEVEL_ID  + ")"
                   + "      and (" + FIELD_CHILD_CALENDAR_PERIOD_ID + " = " + FIELD_PARENT_CALENDAR_PERIOD_ID + "))");

    try {
      connection.execute(stmt.getSQLString());
      ResultSet rs = (ResultSet) connection.getResult();

      // Are there any there?
      rs.next();
      if(rs.getInt(1) != 0)
        result = true;
      rs.close();
    }
    catch (Exception e) {
      throw new DataException(DataException.UNKNOWN, methodName, e);
    }
    return result;
  }


///
///  CREATE methods
///


    /**
   * Actual work method
   */
  protected void doCreateCalendar(JdbcDataConnection connection,
                                  int calendarID,
                                  String calendarName) throws DataException
  {
    // figure out where we are
    String methodName = "JdbcCalendarOperation.doCreateCalendar()";

    // Make the new level
    SQLInsertStatement stmt = new SQLInsertStatement();
    stmt.setTable(TABLE_CALENDAR);

    stmt.addColumn(FIELD_CALENDAR_ID, calendarID);
    stmt.addColumn(FIELD_CALENDAR_NAME, makeSafeString(calendarName));

    // Don't forget the timestamps
    stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                   getSQLCurrentTimestampFunction());
    stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                   getSQLCurrentTimestampFunction());

    try {
      connection.execute(stmt.getSQLString());
    }
    catch (Exception e) {
      throw new DataException(DataException.UNKNOWN, methodName, e);
    }
  }


    /**
    * Actual work method - Make a level in the database with the given child level.
    */
   protected void doCreateLevel(JdbcDataConnection connection,
                                CalendarLevelKeyIfc newLevelKey,
                                String newLevelName,
                                String boundaryCode) throws DataException
   {
     // figure out where we are
     String methodName = "JdbcCalendarOperation.doCreateLevel()";

     // Make the new level
     SQLInsertStatement stmt = new SQLInsertStatement();
     stmt.setTable(TABLE_CALENDAR_LEVEL);

     stmt.addColumn(FIELD_CALENDAR_ID, newLevelKey.getCalendarID());
     stmt.addColumn(FIELD_CALENDAR_LEVEL_ID, newLevelKey.getLevelID());
     stmt.addColumn(FIELD_CALENDAR_LEVEL_NAME, makeSafeString(newLevelName));
     stmt.addColumn(FIELD_PERIOD_BOUNDARY_CODE, makeSafeString(boundaryCode));

     // Don't forget the timestamps
     stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                    getSQLCurrentTimestampFunction());
     stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                    getSQLCurrentTimestampFunction());

     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }

    /**
    * Actual work method - Create a period in the database, and associate it with it's parent.
    */
   protected void doCreateCalendarPeriod(JdbcDataConnection connection,
                                         CalendarPeriodIfc period) throws DataException
   {
     // Figure out where we are
     String methodName =  "JdbcCalendarDataOperation.doCreateCalendarPeriod()";

     // Make the new CALENDAR period
     CalendarPeriodKeyIfc periodKey = period.getPeriodKey();
     SQLInsertStatement stmtCalendar = new SQLInsertStatement();
     stmtCalendar.setTable(TABLE_CALENDAR_PERIOD);

     stmtCalendar.addColumn(FIELD_CALENDAR_ID, periodKey.getCalendarID());
     stmtCalendar.addColumn(FIELD_CALENDAR_LEVEL_ID, periodKey.getLevelID());
     stmtCalendar.addColumn(FIELD_CALENDAR_PERIOD_ID, periodKey.getPeriodID());
     stmtCalendar.addColumn(FIELD_START_DATE_TIME, dateToSQLTimestampString(period.getStartDateTime()));
     if(period.getEndDateTime() == null)
       stmtCalendar.addColumn(FIELD_END_DATE_TIME, "NULL");
     else
       stmtCalendar.addColumn(FIELD_END_DATE_TIME, dateToSQLTimestampString(period.getEndDateTime()));

     // Don't forget the timestamps
     stmtCalendar.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                            getSQLCurrentTimestampFunction());
     stmtCalendar.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                            getSQLCurrentTimestampFunction());

     try {
       String sql = stmtCalendar.getSQLString();
       connection.execute(stmtCalendar.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }

     // Don't bother with -1 ReportingPeriodID
     if(period.getReportingPeriodID() == -1)
       return;

     // Make the relevant REPORTING period
     SQLInsertStatement stmtReporting = new SQLInsertStatement();
     stmtReporting.setTable(TABLE_REPORTING_PERIOD_V4);

     stmtReporting.addColumn(FIELD_REPORTING_PERIOD_ID_V4, period.getReportingPeriodID());
     stmtReporting.addColumn(FIELD_CALENDAR_ID, periodKey.getCalendarID());
     stmtReporting.addColumn(FIELD_CALENDAR_LEVEL_ID, periodKey.getLevelID());
     stmtReporting.addColumn(FIELD_START_CALENDAR_PERIOD_ID, periodKey.getPeriodID());
     stmtReporting.addColumn(FIELD_END_CALENDAR_PERIOD_ID, periodKey.getPeriodID());

     // Don't forget the timestamps
     stmtReporting.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                             getSQLCurrentTimestampFunction());
     stmtReporting.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                             getSQLCurrentTimestampFunction());

     try {
       String sql = stmtReporting.getSQLString();

       connection.execute(stmtReporting.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }


///
///  REMOVE methods
///


    /**
    * Actual work method.
    */
   protected void doRemoveCalendar(JdbcDataConnection connection,
                                   int calendarID) throws DataException
   {
     // Figure out where we are
     String methodName = "JdbcCalendarOperation.doRemoveLevel()";

     // Maybe make the SQL Statement
     SQLDeleteStatement stmt = new SQLDeleteStatement();
     stmt.setTable(TABLE_CALENDAR);
     stmt.addQualifier(FIELD_CALENDAR_ID, calendarID);

     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }


    /**
    * Actual work method.
    */
   protected void doRemoveLevel(JdbcDataConnection connection,
                                CalendarLevelKeyIfc levelKey) throws DataException
   {
     // Figure out where we are
     String methodName = "JdbcCalendarOperation.doRemoveLevel()";

     // Maybe make the SQL Statement
     SQLDeleteStatement stmt = new SQLDeleteStatement();
     stmt.setTable(TABLE_CALENDAR_LEVEL);
     stmt.addQualifier(FIELD_CALENDAR_ID, levelKey.getCalendarID());
     stmt.addQualifier(FIELD_CALENDAR_LEVEL_ID, levelKey.getLevelID());

     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }


    /**
    * Actual work method.
    */
   protected void doRemoveLevels(JdbcDataConnection connection,
                                 int calendarID) throws DataException
   {
     // Figure out where we are
     String methodName = "JdbcCalendarOperation.doRemoveLevels()";

     // Maybe make the SQL Statement
     SQLDeleteStatement stmt = new SQLDeleteStatement();
     stmt.setTable(TABLE_CALENDAR_LEVEL);
     stmt.addQualifier(FIELD_CALENDAR_ID, calendarID);

     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }


    /**
     * Actual work method - The given level is no longer the child of any other level
     */
   protected void doRemoveLevelAssociations(JdbcDataConnection connection,
                                            CalendarLevelKeyIfc levelKey) throws DataException
   {
     // figure out where we are
     String methodName = "JdbcCalendarOperation.doRemoveLevelAssociations()";

     // Maybe make the SQL Statement
     SQLDeleteStatement stmt = new SQLDeleteStatement();
     stmt.setTable(TABLE_CALENDAR_LEVEL_ASSOCIATION);
     stmt.addQualifier(FIELD_CALENDAR_ID, levelKey.getCalendarID());
     stmt.addQualifier(FIELD_CHILD_CALENDAR_LEVEL_ID, levelKey.getLevelID());

     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }


    /**
    * Actual work method -Remove all periods in a calendar period from the database
    */
   protected void doRemoveAllCalendarPeriods(JdbcDataConnection connection,
                                             int calendarID) throws DataException
   {
     // Figure out where we are
     String methodName = "JdbcCalendarOperation.doRemoveAllPeriods()";

     // Maybe make the SQL Statement
     SQLDeleteStatement stmtReporting = new SQLDeleteStatement();
     stmtReporting.setTable(TABLE_REPORTING_PERIOD_V4);
     stmtReporting.addQualifier(FIELD_CALENDAR_ID, calendarID);

     try {
       connection.execute(stmtReporting.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }

     // Maybe make the SQL Statement
     SQLDeleteStatement stmtCalendar = new SQLDeleteStatement();
     stmtCalendar.setTable(TABLE_CALENDAR_PERIOD);
     stmtCalendar.addQualifier(FIELD_CALENDAR_ID, calendarID);

     try {
       connection.execute(stmtCalendar.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }

    /**
    * Actual work method - Remove all links in the calendar
    */
   protected void doRemoveAllCalendarAssociations(JdbcDataConnection connection,
                                                   int calendarID) throws DataException
   {
     // figure out where we are
     String methodName = "JdbcCalendarOperation.doRemoveCalendarAssociations()";

     // Delete all the Period Associations
     SQLDeleteStatement stmtPeriod = new SQLDeleteStatement();
     stmtPeriod.setTable(TABLE_CALENDAR_PERIOD_ASSOCIATION);
     stmtPeriod.addQualifier(FIELD_CALENDAR_ID, calendarID);

     try {
       connection.execute(stmtPeriod.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }

     // Now delete the Level Associations
     SQLDeleteStatement stmtLevel = new SQLDeleteStatement();
     stmtLevel.setTable(TABLE_CALENDAR_LEVEL_ASSOCIATION);
     stmtLevel.addQualifier(FIELD_CALENDAR_ID, calendarID);

     try {
       connection.execute(stmtLevel.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }

    /**
    * Actual work method - The given levels are no longer parent and child
    */
  protected void doRemoveLevelAssociation(JdbcDataConnection connection,
                                          CalendarLevelKeyIfc parentLevelKey,
                                          CalendarLevelKeyIfc levelKey) throws DataException
  {
    // figure out where we are
    String methodName = "JdbcCalendarOperation.doRemoveLevelAssociation()";

    // Make the SQL Statement
    SQLDeleteStatement stmt = new SQLDeleteStatement();
    stmt.setTable(TABLE_CALENDAR_LEVEL_ASSOCIATION);
    stmt.addQualifier(FIELD_CALENDAR_ID, levelKey.getCalendarID());
    stmt.addQualifier(FIELD_CHILD_CALENDAR_LEVEL_ID, levelKey.getLevelID());
    stmt.addQualifier(FIELD_PARENT_CALENDAR_LEVEL_ID, parentLevelKey.getLevelID());

    try {
      connection.execute(stmt.getSQLString());
    }
    catch (Exception e) {
      throw new DataException(DataException.UNKNOWN, methodName, e);
    }
  }


    /**
    * Actual work method - The given period is no longer the child of any other period.
    */
   protected void doRemovePeriodAssociations(JdbcDataConnection connection,
                                             CalendarPeriodKeyIfc periodKey) throws DataException
   {
     // figure out where we are
     String methodName = "JdbcCalendarOperation.doRemovePeriodAssociations()";

     // Make the SQL Statement
     SQLDeleteStatement stmt = new SQLDeleteStatement();
     stmt.setTable(TABLE_CALENDAR_PERIOD_ASSOCIATION);
     stmt.addQualifier(FIELD_CALENDAR_ID, periodKey.getCalendarID());
     stmt.addQualifier(FIELD_CHILD_CALENDAR_LEVEL_ID, periodKey.getLevelID());
     stmt.addQualifier(FIELD_CHILD_CALENDAR_PERIOD_ID, periodKey.getPeriodID());

     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }


    /**
   * Actual work method -Remove a particular period from the database
   */
  protected void doRemoveCalendarPeriod(JdbcDataConnection connection,
                                        CalendarPeriodKeyIfc periodKey) throws DataException
  {
    // Figure out where we are
    String methodName = "JdbcCalendarOperations.doRemovePeriod()";

    // Delete the matching ReportingPeriod (if any)
    SQLDeleteStatement stmtReporting = new SQLDeleteStatement();
    stmtReporting.setTable(TABLE_REPORTING_PERIOD_V4);

    stmtReporting.addQualifier(FIELD_CALENDAR_ID, periodKey.getCalendarID());
    stmtReporting.addQualifier(FIELD_CALENDAR_LEVEL_ID, periodKey.getLevelID());
    stmtReporting.addQualifier("(" + FIELD_START_CALENDAR_PERIOD_ID + " = " + periodKey.getPeriodID()
                                   + " or "
                                   + FIELD_END_CALENDAR_PERIOD_ID + " = " + periodKey.getPeriodID() + ")");

    try {
      connection.execute(stmtReporting.getSQLString());
    }
    catch (Exception e) {
      throw new DataException(DataException.UNKNOWN, methodName, e);
    }


    // Now delete the CalendarPeriod
    SQLDeleteStatement stmtCalendar = new SQLDeleteStatement();
    stmtCalendar.setTable(TABLE_CALENDAR_PERIOD);

    stmtCalendar.addQualifier(FIELD_CALENDAR_ID, periodKey.getCalendarID());
    stmtCalendar.addQualifier(FIELD_CALENDAR_LEVEL_ID, periodKey.getLevelID());
    stmtCalendar.addQualifier(FIELD_CALENDAR_PERIOD_ID, periodKey.getPeriodID());
    try {
      connection.execute(stmtCalendar.getSQLString());
    }
    catch (Exception e) {
      throw new DataException(DataException.UNKNOWN, methodName, e);
    }
  }


    /**
    * Actual work method - The two given periods are no longer associated
     */
    protected void doRemovePeriodAssociation(JdbcDataConnection connection,
                                             CalendarPeriodKeyIfc parentPeriodKey,
                                             CalendarPeriodKeyIfc childPeriodKey) throws DataException
   {
     // figure out where we are
     String methodName = "JdbcCalendarOperation.doRemovePeriodAssociation()";

     // Make the SQL Statement
     SQLDeleteStatement stmt = new SQLDeleteStatement();
     stmt.setTable(TABLE_CALENDAR_PERIOD_ASSOCIATION);
     stmt.addQualifier(FIELD_CALENDAR_ID, parentPeriodKey.getCalendarID());

     stmt.addQualifier(FIELD_PARENT_CALENDAR_LEVEL_ID, parentPeriodKey.getLevelID());
     stmt.addQualifier(FIELD_PARENT_CALENDAR_PERIOD_ID, parentPeriodKey.getPeriodID());

     stmt.addQualifier(FIELD_CHILD_CALENDAR_LEVEL_ID, childPeriodKey.getLevelID());
     stmt.addQualifier(FIELD_CHILD_CALENDAR_PERIOD_ID, childPeriodKey.getPeriodID());

     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }

///
///  WRITE methods
///

    /**
    *   writeCalendar()
    *   Updates the Calendar table from the given parameters
    */
   protected void doWriteCalendar(JdbcDataConnection connection,
                                  int calendarID,
                                  String newName) throws DataException
   {

     // figure out where we are
     String methodName = "JdbcCalendarDataOperation.writeCalendar()";

     // Make the update statement
     SQLUpdateStatement stmt = new SQLUpdateStatement();
     stmt.setTable(TABLE_CALENDAR);
     stmt.addColumn(FIELD_CALENDAR_NAME, makeSafeString(newName));
     stmt.addQualifier(FIELD_CALENDAR_ID, calendarID);

     // Don't forget the timestamp
     stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                    getSQLCurrentTimestampFunction());
     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }

    /**
    *   writeCalendarLevel()
    *   Updates the CalendarLevel table from the given CalendarLevel object
    */
   protected void doWriteCalendarLevel(JdbcDataConnection connection,
                                       CalendarLevelIfc level) throws DataException
   {

     // figure out where we are
     String methodName = "JdbcCalendarDataOperation.writeCalendarLevel()";

     // Make the update statement
     SQLUpdateStatement stmt = new SQLUpdateStatement();
     stmt.setTable(TABLE_CALENDAR_LEVEL);

     stmt.addColumn(FIELD_CALENDAR_LEVEL_NAME, makeSafeString(level.getLevelName()));
     stmt.addColumn(FIELD_PERIOD_BOUNDARY_CODE, makeSafeString(level.encodePeriodBoundary()));

     stmt.addQualifier(FIELD_CALENDAR_ID, level.getLevelKey().getCalendarID());
     stmt.addQualifier(FIELD_CALENDAR_LEVEL_ID, level.getLevelKey().getLevelID());

     // Don't forget the timestamps
     stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                    getSQLCurrentTimestampFunction());

     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }


///
///  ASSOCIATE methods
///

    /**
    * Actual work method - The given child & parent levels are now related.
    */
   protected void doAssociateLevels(JdbcDataConnection connection,
                                    CalendarLevelKeyIfc levelKey,
                                    CalendarLevelKeyIfc parentLevelKey) throws DataException
   {
     // figure out where we are
     String methodName = "JdbcCalendarOperations.doAssociatePeriods()";

     // Make the SQL Statement
     SQLInsertStatement stmt = new SQLInsertStatement();
     stmt.setTable(TABLE_CALENDAR_LEVEL_ASSOCIATION);

     stmt.addColumn(FIELD_CALENDAR_ID, levelKey.getCalendarID());
     stmt.addColumn(FIELD_CHILD_CALENDAR_LEVEL_ID, levelKey.getLevelID());
     stmt.addColumn(FIELD_PARENT_CALENDAR_LEVEL_ID, parentLevelKey.getLevelID());

     // Don't forget the timestamps
     stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                    getSQLCurrentTimestampFunction());
     stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                    getSQLCurrentTimestampFunction());

     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN,
                               methodName + ".association", e);
     }
   }

    /**
    * Actual work method - The given child & parent periods are now related.
    */
   protected void doAssociatePeriods(JdbcDataConnection connection,
                                     CalendarPeriodKeyIfc periodKey,
                                     CalendarPeriodKeyIfc parentKey) throws DataException
   {
     // figure out where we are
     String methodName = "JdbcCalendarOperation.doAssociatePeriods()";

     // Make the SQL Statement
     SQLInsertStatement stmt = new SQLInsertStatement();
     stmt.setTable(TABLE_CALENDAR_PERIOD_ASSOCIATION);

     stmt.addColumn(FIELD_CALENDAR_ID, periodKey.getCalendarID());

     stmt.addColumn(FIELD_CHILD_CALENDAR_LEVEL_ID, periodKey.getLevelID());
     stmt.addColumn(FIELD_CHILD_CALENDAR_PERIOD_ID, periodKey.getPeriodID());

     stmt.addColumn(FIELD_PARENT_CALENDAR_LEVEL_ID, parentKey.getLevelID());
     stmt.addColumn(FIELD_PARENT_CALENDAR_PERIOD_ID, parentKey.getPeriodID());

     // Don't forget the timestamps
     stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                    getSQLCurrentTimestampFunction());
     stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                    getSQLCurrentTimestampFunction());

     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN,
                               methodName + ".association", e);
     }
   }


    /**
    * Actual work method - Close the period that is open
    */
   protected void doCloseCalendarPeriod(JdbcDataConnection connection,
                                        CalendarPeriodKeyIfc periodKey,
                                        EYSDate endDateTime) throws DataException
   {
     // figure out where we are
     String methodName = "JdbcCalendarOperation.doClosePeriod()";

     SQLUpdateStatement stmt = new SQLUpdateStatement();
     stmt.setTable(TABLE_CALENDAR_PERIOD);
     stmt.addColumn(FIELD_END_DATE_TIME, dateToSQLTimestampString(endDateTime));
     stmt.addQualifier(FIELD_CALENDAR_ID, periodKey.getCalendarID());
     stmt.addQualifier(FIELD_CALENDAR_LEVEL_ID, periodKey.getLevelID());
     stmt.addQualifier(FIELD_CALENDAR_PERIOD_ID, periodKey.getPeriodID());

     // Don't forget the timestamps
     stmt.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                    getSQLCurrentTimestampFunction());
     try {
       connection.execute(stmt.getSQLString());
     }
     catch (Exception e) {
       throw new DataException(DataException.UNKNOWN, methodName, e);
     }
   }

    /**
     * Debug/Logging representation of this class
     * 
     * @return String containing class name and version number
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = Util.classToStringHeader("JdbcCalendarOperation",
                getRevisionNumber(), hashCode());
        return strResult.toString();
    }

    /**
     * Revision number of the code
     * 
     * @return String containing revision number as set by source code control
     *         system.
     */
   public String getRevisionNumber()  {
     return Util.parseRevisionNumber(revisionNumber);
   }
}
