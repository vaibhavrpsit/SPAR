/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/CalendarDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:02 mszekely Exp $
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
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.calendar.BusinessCalendarIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodKeyIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

public class CalendarDataTransaction extends DataTransaction
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(CalendarDataTransaction.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       The name that links this transaction to a command within DataScript.
    **/
    public static String dataCommandName = "CalendarDataTransaction";

    /**
     * private debugging attributes
     */
    private String methodName = dataCommandName;

    /**
     *  default constructor
    **/
    public CalendarDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     *  Returns default display string. <P>
     *  @return String representation of object
     **/
    public String toString()  {
      return  this.getClass().getName() + " (Revision " + revisionNumber + ")";
    }

    /**
     * Retrieves the source-code-control system revision number. <P>
     * @return String representation of revision number
     **/
    public String getRevisionNumber() {
      return(revisionNumber);
    }

    // Possible Parameters
    protected int paramCalendarID                    = -1;

    protected CalendarLevelIfc paramLevel            = null;
    protected CalendarLevelIfc paramParentLevel      = null;

    protected String paramName                       = null;
    protected int paramReportingPeriodID             = -1;
    protected boolean paramSelectComplete            = false;
    protected boolean paramOpenPeriod                = false;

    protected String paramLevelBoundaryCode          = null;
    protected EYSDate paramStartDateTime             = null;
    protected EYSDate paramEndDateTime               = null;

    protected CalendarPeriodKeyIfc paramPeriodKey    = null;

    protected CalendarPeriodIfc paramPeriod          = null;
    protected CalendarPeriodIfc paramParentPeriod    = null;
    protected CalendarPeriodIfc paramChildPeriod     = null;


    /**
     * Send the DataOperation request through the membrane for execution.
     * @param dataOperationName The data operation
     * @return The result of the data operation
     * @throws DataException Thrown in case of database errors
     */
    private Serializable executeCommand(String dataOperationName) throws DataException
    {
      // Make the data object to send through the membrane
      DataActionIfc[] dataActions = new DataActionIfc[1];
      DataAction da = new DataAction();
      da.setDataOperationName(dataOperationName);

      // da.setDataObject(null);   // Don't have a dataObject to send
      dataActions[0] = da;

      // Go do it
      setDataActions(dataActions);
      return (Serializable) getDataManager().execute(this);
    }


    /**
     * Initialize the parameters
     *
     */
    private void initializeParams()
    {
      this.paramCalendarID           = -1;
      this.paramName                 = null;

      this.paramSelectComplete       = false;
      this.paramOpenPeriod           = false;
      this.paramReportingPeriodID    = -1;

      this.paramLevelBoundaryCode    = null;
      this.paramStartDateTime        = null;
      this.paramEndDateTime          = null;

      this.paramLevel                = null;
      this.paramParentLevel          = null;

      this.paramPeriod               = null;
      this.paramParentPeriod         = null;
      this.paramChildPeriod          = null;
    }


///
///  FETCH methods
///


    /**
     * Fetch a BusinessCalendarIfc from the database using a known CalendarID.
     * @param calendarID - The identifier for the required calendar.
     * @return BusinessCalendarIfc - The requested calendar.
     * @throws DataException on any database error.
     */
    public BusinessCalendarIfc fetchCalendar(int calendarID) throws DataException
    {
      // figure out where we are
      methodName = dataCommandName + ".fetchCalendarByID()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramCalendarID = calendarID;

      // Go do it
      BusinessCalendarIfc result = (BusinessCalendarIfc) executeCommand("FetchCalendarByID");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }


    /**
     * Fetch a BusinessCalendarIfc from the database using a known CalendarName.
     * @param calendarName The calendar to fetch
     * @return A BusinessCalendarIfc object
     * @throws DataException Thrown in case of database errors
     **/
    public BusinessCalendarIfc fetchCalendar(String calendarName) throws DataException
    {
      // figure out where we are
      methodName = dataCommandName + ".fetchCalendarByName()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramName = calendarName;

      // Go do it
      BusinessCalendarIfc result = (BusinessCalendarIfc) executeCommand("FetchCalendarByName");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }


    /**
      * Fetch a CalendarPeriod from the database using a known
      * CalendarPeriodKey to locate it.
      * @param periodKey The key to locate
      * @return A CalendarPeriodKeyIfc object
      * @throws DataException Thrown in case of database errors
      */
     public CalendarPeriodIfc fetchPeriod(CalendarPeriodKeyIfc periodKey) throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchPeriod()";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set the parameters
       this.initializeParams();
       this.paramPeriodKey = periodKey;

       // Go do it
       CalendarPeriodIfc result = (CalendarPeriodIfc) executeCommand("FetchSingleCalendarPeriod");

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       return result;
     }


     /**
      * Fetch the <strong>single</strong> CalendarPeriod that the given
      * date &amp; time fall within <strong>and</strong> is in the given
      * CalendarLevelIfc.
      * @param level Calendar level of period to retrieve
      * @param dateTime Date/Time of period to retrieve
      * @return The required calendar period
      * @throws DataException Thrown in case of database errors
      */
     public CalendarPeriodIfc fetchPeriod(CalendarLevelIfc level,
                                       EYSDate dateTime) throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchPeriods(level, date)";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set parameters
       this.initializeParams();
       this.paramLevel = level;
       this.paramStartDateTime = dateTime;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllCalendarPeriodsInLevelContainingDate");

       // If there are no periods then return a null list
       if(list.size() == 0)
         return null;

       // If there is more than one period then we've got a serious problem
       if(list.size() == 0)
         throw new DataException(DataException.RESULT_SET_SIZE, methodName + " - There should be only one period in a level for a particular level");

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       return (CalendarPeriodIfc) list.get(0);
     }


     /**
      * Fetch all CalendarPeriods that're children of the given CalendarPeriod.
      * @param aPeriod Parent period to use
      * @return Array of CalendarPeriodIfc objects that are children of the given period
      * @throws DataException Thrown in case of database errors
      */
     public CalendarPeriodIfc[] fetchChildrenPeriods(CalendarPeriodIfc aPeriod)
         throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchChildrenPeriods()";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set parameters
       this.initializeParams();
       this.paramParentPeriod = aPeriod;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllChildrenCalendarPeriods");

       // If there are no child periods then return a null list
       if(list.size() == 0)
         return null;

       // Make & Populate a result
       CalendarPeriodIfc result[] = new CalendarPeriodIfc[list.size()];
       result = (CalendarPeriodIfc[]) list.toArray(result);

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       return result;
     }


     /**
      * Fetch all periods that're children of the given CalendarPeriod
      *   <strong>and</strong> are members of the given CalendarLevelIfc.
      * @param aPeriod Period to get child records for
      * @param aLevel Level to get child records for
      * @return Array of CalendarPeriodIfc objects
      * @throws DataException Thrown in case of database errors
      */
     public CalendarPeriodIfc[] fetchChildrenPeriods(CalendarPeriodIfc aPeriod,
                                                     CalendarLevelIfc aLevel) throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchAllChildrenPeriods(level)";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set parameters
       this.initializeParams();
       this.paramParentPeriod = aPeriod;
       this.paramLevel = aLevel;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllChildrenCalendarPeriodsInLevel");

       // If there is no such child period then return a null list
       if(list.size() == 0)
         return null;

       // Make & Populate a result
       CalendarPeriodIfc result[] = new CalendarPeriodIfc[list.size()];
       result = (CalendarPeriodIfc[]) list.toArray(result);

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       return result;
     }



     /**
      * Fetch all CalendarPeriods that're parents of the given CalendarPeriod.
      * @param aPeriod Period to get child records for
      * @return Array of CalendarPeriodIfc objects
      * @throws DataException Thrown in case of database errors
      */
     public CalendarPeriodIfc[] fetchParentPeriods(CalendarPeriodIfc aPeriod) throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchAllParentPeriods()";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set parameters
       this.initializeParams();
       this.paramChildPeriod = aPeriod;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllParentCalendarPeriods");

       // If there are no parent periods then return a null list
       if(list.size() == 0)
         return null;

       // Make, Populate & Leave
       CalendarPeriodIfc result[] = new CalendarPeriodIfc[list.size()];
       result = (CalendarPeriodIfc[]) list.toArray(result);

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       return result;
     }


     /**
      * Fetch the <strong>single</strong> CalendarPeriod that is the parent of the
      * given CalendarPeriod <strong>and</strong> is in the given Calendarlevel.
      * @param aPeriod Result will be parent of this period
      * @param aLevel Result will be in this level
      * @return CalendarPeriodIfc object
      * @throws DataException Thrown in case of database errors
      */
     public CalendarPeriodIfc fetchParentPeriod(CalendarPeriodIfc aPeriod,
                                                CalendarLevelIfc aLevel)
         throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchAllParentPeriods(level)";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set parameters
       this.initializeParams();
       this.paramChildPeriod = aPeriod;
       this.paramLevel = aLevel;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllParentCalendarPeriodsInLevel");

       // If there are no parent periods then return a null list
       if(list.size() == 0)
         return null;

       // If there's more than one parent period in the level then something is seriously wrong
       if(list.size() > 1)
         throw new DataException(DataException.RESULT_SET_SIZE, "CalendarPeriod has only one parent period in a particular level");

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       return (CalendarPeriodIfc) list.get(0);
     }


     /**
      * Fetch the set of CalendarPeriods that the given date &amp; time
      * fall within in the given Calendar
      * @param calendar Calendar to fetch records from
      * @param dateTime Time to match in records
      * @return Array of calendar periods
      * @throws DataException
      */
     public CalendarPeriodIfc[] fetchAllPeriods(BusinessCalendarIfc calendar,
                                                EYSDate dateTime) throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchAllCalendarPeriods(date)";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set parameters
       this.initializeParams();
       this.paramCalendarID = calendar.getCalendarID();
       this.paramStartDateTime = dateTime;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllCalendarPeriodsContainingDate");

       // If there are no periods then return a null list
       if(list.size() == 0)
         return null;

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       CalendarPeriodIfc[] result = new CalendarPeriodIfc[list.size()];
       return (CalendarPeriodIfc[]) list.toArray(result);
     }

     /**
      * Fetch ALL the CalendarPeriods that are in the given CalendarLevelIfc.
      * @param level The level of the periods to fetch
      * @return A CalendarPeriodIfc object
      * @throws DataException Thrown in case of database errors
      */
     public CalendarPeriodIfc[] fetchAllPeriods(CalendarLevelIfc level)
         throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchAllCalendarPeriods(level)";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set parameters
       this.initializeParams();
       this.paramLevel = level;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllCalendarPeriodsInLevel");

       // If there are no periods then return a null list
       if(list.size() == 0)
         return null;

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       CalendarPeriodIfc[] result = new CalendarPeriodIfc[list.size()];
       return (CalendarPeriodIfc[]) list.toArray(result);
     }


     /**
      * Fetch all ReportingPeriodIDs for ReportingPeriods that contain the
      * given date & time in the given Calendar.
      * @param calendar Calendar to retrieve records from
      * @param dateTime Date/Time to match in returned records
      * @return Array of reporting period IDs
      * @throws DataException Thrown in case of database errors
      */
     public Integer[] fetchAllReportingPeriodIDs(BusinessCalendarIfc calendar,
                                                 EYSDate dateTime) throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchAllReportingPeriods(date)";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set parameters
       this.initializeParams();
       this.paramCalendarID = calendar.getCalendarID();
       this.paramStartDateTime = dateTime;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllReportingPeriodIDsContainingDate");

       // if there are no reportingPeriodIDs then return null
       if(list.size() == 0)
         return null;

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       Integer[] result = new Integer[list.size()];
       return (Integer[]) list.toArray(result);

     }


     /**
      * Fetch all ReportingPeriodIDs for those ReportingPeriods with matching
      * CalendarPeriods in the given CalendarLevelIfc.
      * @param level Level from which to match periods
      * @return Array of reporting period IDs
      * @throws DataException Thrown in case of database errors
      */
     public Integer[] fetchAllReportingPeriodIDs(CalendarLevelIfc level) throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchAllReportingPeriods(level)";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set parameters
       this.initializeParams();
       this.paramLevel = level;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllReportingPeriodIDsInLevel");

       // if there are no reportingPeriodIDs then return null
       if(list.size() == 0)
         return null;

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       Integer[] result = new Integer[list.size()];
       return (Integer[]) list.toArray(result);
     }


     /**
      * Fetch all ReportingPeriodIDs for those ReportingPeriods with matching
      * CalendarPeriods that cover the given date &amp; time <strong>and</strong>
      * and are in the given CalendarLevelIfc.
      * @param level Level from which to match periods
      * @param dateTime date/time to match reporting periods to
      * @return Array of reporting period IDs
      * @throws DataException Thrown in case of database errors
      */
     public Integer[] fetchAllReportingPeriodIDs(CalendarLevelIfc level,
                                                 EYSDate dateTime) throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchAllReportingPeriods(date, level)";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Set parameters
       this.initializeParams();
       this.paramLevel = level;
       this.paramStartDateTime = dateTime;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllReportingPeriodIDsInLevelContainingDate");

       // if there are no reportingPeriodIDs then return null
       if(list.size() == 0)
         return null;

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       Integer[] result = new Integer[list.size()];
       return (Integer[]) list.toArray(result);
     }


    /**
      * Fetch all ReportingPeriodIDs for those ReportingPeriods with matching CalendarPeriods
      * that fall into the period of time bounded by the given start and end date &amp; time,
      * and are in the given CalendarLevelIfc.
      * @param level Calendar level from which to select records
      * @param startDateTime starting time for matching records
      * @param endDateTime ending time for matching records
      * @param selectComplete if true, periods will fall completely in the specified limits
      * @return Array of reporting period IDs
      * @throws DataException Thrown in case of database errors
      */
     public Integer[] fetchAllReportingPeriodIDs(CalendarLevelIfc level,
                                                 EYSDate startDateTime,
                                                 EYSDate endDateTime,
                                                 boolean selectComplete) throws DataException
     {
       // Figure out where we are
       methodName = dataCommandName + ".fetchAllReportingPeriodIDs(date, date)";
       if (logger.isDebugEnabled()) logger.debug( methodName);

       // Make sure we've got a complete set of parameters
       if( level == null ||
           startDateTime == null ||
           endDateTime == null)
         throw new DataException(DataException.DATA_FORMAT, methodName + " - NULL parameters not accpetable");

       // Set parameters
       this.initializeParams();
       this.paramLevel = level;
       this.paramStartDateTime = startDateTime;
       this.paramEndDateTime = endDateTime;
       this.paramSelectComplete = selectComplete;

       // Go do it
       ArrayList list = (ArrayList) executeCommand("FetchAllReportingPeriodIDsBetweenDates");

       // if there are no reportingPeriodIDs then return null
       if(list.size() == 0)
         return null;

       // We're done
       if (logger.isDebugEnabled()) logger.debug( methodName);
       Integer[] result = new Integer[list.size()];
       return (Integer[]) list.toArray(result);
     }


    /**
     *  Create a new BusinessCalendarIfc in the database, and return it.
    * @param calendarName Name of the new calendar
    * @return The newly created object
    * @throws DataException Thrown in case of database errors
    */
    public BusinessCalendarIfc createCalendar(String calendarName) throws DataException
    {
      // figure out where we are
      methodName = dataCommandName + ".createCalendar()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramName = calendarName;

      // Go do it
      BusinessCalendarIfc result = (BusinessCalendarIfc) executeCommand("CreateCalendar");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }

    /**
     * Create and return a new CalendarLevelIfc in the database as a child of the
     * given CalendarLevelIfc, with the given name & boundaryCode.
     * @param parentLevel Level of parent calendar
     * @param newLevelName Name of new level
     * @param boundaryCode Value from CalendarLevelFactoryIfc denoting rule to
     *   determine period boundary
     * @return The newly created calendar level object
     * @throws DataException Thrown in case of database errors
     */
    public CalendarLevelIfc createLevel(CalendarLevelIfc parentLevel,
                                     String newLevelName,
                                     String boundaryCode) throws DataException
    {
      // Figure out where we are
      methodName = dataCommandName + ".createLevel()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set Parameters
      this.initializeParams();
      this.paramParentLevel = parentLevel;
      this.paramName = newLevelName;
      this.paramLevelBoundaryCode = boundaryCode;

      // Go do it
      CalendarLevelIfc result = (CalendarLevelIfc) executeCommand("CreateCalendarLevel");

      // The level that's just been made is part of the same calendar
      parentLevel.getCalendar().addLevel(result);

      // Fixup the child & parent pointers
      parentLevel.addChildLink(result);
      result.addParentLink(parentLevel);

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }


    /**
     *  Create and return a new CalendarPeriod in the database as a child of the
     *  given CalendarPeriod, with the given start and end date &amp; time.
     * @param parentPeriod Create a child to this period
     * @param level Level of child to be created
     * @param startDateTime Start time of new child
     * @param endDateTime End time of new child
     * @return New calendar period object
     * @throws DataException Thrown in case of database errors
     */
    public CalendarPeriodIfc createPeriod(CalendarPeriodIfc parentPeriod,
                                          CalendarLevelIfc level,
                                          EYSDate startDateTime,
                                          EYSDate endDateTime) throws DataException
    {
      // Figure out where we are
      methodName = dataCommandName + ".createPeriod(parent, level, start, end)";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Make sure the level has a correct parentLevel.
      try {
        level.getParentLevel(parentPeriod.getLevelKey());
      } catch (DataException ex) {
        throw new DataException(DataException.DATA_FORMAT, methodName + " - proposed parent period not in parent level");
      }

      // Set the parameters
      this.initializeParams();
      this.paramParentPeriod = parentPeriod;
      this.paramLevel = level;
      this.paramStartDateTime = startDateTime;
      this.paramEndDateTime = endDateTime;

      // Go do it
      CalendarPeriodIfc result = (CalendarPeriodIfc) executeCommand("CreateCalendarPeriod");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }


    /**
     * Create and return a closed CalendarPeriod in the database in the given
     * CalendarLevelIfc that contains the given start and end date &amp; time.
     * @param level Level of new period
     * @param dateTime Time that new record will contain
     * @return New calendar period object
     * @throws DataException Thrown in case of database errors
     */
    public CalendarPeriodIfc createPeriod(CalendarLevelIfc level,
                                          EYSDate dateTime) throws DataException
    {
      // Figure out where we are
      methodName = dataCommandName + ".createPeriod(level, dateTime)";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set the parameters
      this.initializeParams();
      this.paramLevel = level;
      this.paramStartDateTime = dateTime;
      this.paramOpenPeriod = false;

      // Go do it
      CalendarPeriodIfc result = (CalendarPeriodIfc) executeCommand("CreateCalendarPeriod");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }



    /**
     * Remove the given BusinessCalendarIfc from the database.
     * @param calendar Calendar to remove
     * @throws DataException Thrown in case of database errors
     */
    public void removeCalendar(BusinessCalendarIfc calendar) throws DataException
    {
      // Figure out where we are
      methodName = dataCommandName + ".removeCalendar()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      initializeParams();
      paramCalendarID = calendar.getCalendarID();

      // Go do it
      executeCommand("RemoveCalendar");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return;
    }

    /**
     * Remove the given CalendarLevelIfc from the database.
     * @param level Level to remove
     * @throws DataException Thrown in case of database errors
     */
    public void removeLevel(CalendarLevelIfc level) throws DataException
    {
      // Figure out where we are
      methodName = dataCommandName + ".removeLevel()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set Parameters
      this.initializeParams();
      this.paramLevel = level;

      // Go do it
      executeCommand("RemoveCalendarLevel");

      // The level's parents no-longer have that level as a child
      CalendarLevelIfc parents[] = level.getParents();
      for(int i=0; i<parents.length; i++) {
        parents[i].removeChildLevel(level);
      }

     // The owning calendar no-longer has it as a level
     level.getCalendar().removeLevel(level);

     // The level is now an orphan with no parents or children
     level.setLevelKey(null);
     level.setLevelName("Zombie:" + level.getLevelName());

     level.removeChildren();
     level.removeParents();

     // We're done
     if (logger.isDebugEnabled()) logger.debug( methodName);
     return;
   }


    /**
     * Remove the given CalendarPeriod from the database.
     * @param period Period to remove
     * @throws DataException Thrown in case of database errors
     */
    public void removePeriod(CalendarPeriodIfc period) throws DataException
    {
      // Figure out where we are
      methodName = dataCommandName + ".removePeriod()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramPeriod  = period;

      // Go do it
      executeCommand("RemoveCalendarPeriod");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return;
    }


    /**
     * Associate the two given CalendarLevelIfc as parent &amp; child in the database.
     * @param parentLevel parent record in the association
     * @param childLevel child record in the association
     * @throws DataException Thrown in case of database errors
     */
    public void associateLevels(CalendarLevelIfc parentLevel,
                                CalendarLevelIfc childLevel) throws DataException
    {
      // Figure out where we are
        methodName = dataCommandName + ".associateLevels()";
        if (logger.isDebugEnabled()) logger.debug( methodName);

        // The two levels have to be part of the same calendar
        if(! parentLevel.getCalendar().equals(childLevel.getCalendar()))
          throw new DataException(DataException.UNKNOWN,
                                  "Can't associate two levels from different calendars");

        // Set the parameters
        this.initializeParams();
        this.paramParentLevel = parentLevel;
        this.paramLevel       = childLevel;

        // Go do it
        executeCommand("AssociateCalendarLevels");

        // Arrange the parent & child pointers
        parentLevel.addChildLink(childLevel);
        childLevel.addParentLink(parentLevel);

        // We're done
        if (logger.isDebugEnabled()) logger.debug( methodName);
        return;
      }


      /**
       * Remove the parent &amp; child association between the two given
       * CalendarLevelIfc from the database.
       * @param parentLevel Parent record of the association to be removed
       * @param childLevel Child record of the association to be removed
       * @throws DataException Thrown in case of database errors
       */
      public void deAssociateLevels(CalendarLevelIfc parentLevel,
                                    CalendarLevelIfc childLevel) throws DataException
      {
        // Figure out where we are
        methodName = dataCommandName + ".deAssociateLevels()";
        if (logger.isDebugEnabled()) logger.debug( methodName);

        // The two levels have to be part of the same calendar
        if(parentLevel.getCalendar() == childLevel.getCalendar())
          throw new DataException(DataException.DATA_FORMAT,
                                  "Can't associate two levels from different calendars");

        // It would also help if they are actually parent & child
        parentLevel.getChildLevel(childLevel.getLevelKey());

        // Set the parameters
        this.initializeParams();
        this.paramParentLevel = parentLevel;
        this.paramLevel       = childLevel;

        // Go do it
        executeCommand("DeAssociateCalendarLevels");

        // re-arrange the parent & child pointers
        parentLevel.removeChildLevel(childLevel);
        childLevel.removeParentLevel(parentLevel);

        // We're done
        if (logger.isDebugEnabled()) logger.debug( methodName);
        return;
      }


    /**
     * Associate the two given CalendarPeriods as parent &amp; child in the database.
     * @param parentPeriod Parent record to be associated
     * @param childPeriod Child record to be associated
     * @throws DataException Thrown in case of database errors
     */
    public void associatePeriods(CalendarPeriodIfc parentPeriod,
                                 CalendarPeriodIfc childPeriod) throws DataException
    {
      // Figure out where we are
      methodName = dataCommandName + ".associatePeriods()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set the parameters
      this.initializeParams();
      this.paramParentPeriod  = parentPeriod;
      this.paramChildPeriod   = childPeriod;

      // Go do it
      executeCommand("AssociateCalendarPeriods");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return;
    }


    /**
     * Remove the parent &amp; child association between the two given CalendarPeriods
     * from the database.
     * @param parentPeriod Parent record of the association to be removed
     * @param childPeriod Child record of the association to be removed
     * @throws DataException Thrown in case of database errors
     */
    public void deAssociatePeriods(CalendarPeriodIfc parentPeriod,
                                   CalendarPeriodIfc childPeriod) throws DataException
    {
      // Figure out where we are
      methodName = dataCommandName + ".deAssociatePeriods()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set the parameters
      this.initializeParams();
      this.paramParentPeriod    = parentPeriod;
      this.paramPeriod          = childPeriod;

      // Go do it
      executeCommand("DeAssociateCalendarPeriods");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return;
    }



    /**
     * Create and return an open CalendarPeriod in the database in the given
     * CalendarLevelIfc that contains the given start and end date &amp; time.
     * @param level Level of the new period
     * @param dateTime Time that the period must contain
     * @return A calendar period object
     * @throws DataException Thrown in case of database errors
     */
    public CalendarPeriodIfc openPeriod(CalendarLevelIfc level,
                                        EYSDate dateTime) throws DataException
    {
      // Figure out where we are
      methodName = dataCommandName + ".openPeriod(level, dateTime)";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set the parameters
      this.initializeParams();
      this.paramLevel = level;
      this.paramStartDateTime = dateTime;
      this.paramOpenPeriod = true;

      // Go do it
      CalendarPeriodIfc result = (CalendarPeriodIfc) executeCommand("CreateCalendarPeriod");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }


    /**
     * Close the currently open CalendarPeriod for the given CalendarLevel.
     * @param level Level of period to close
     * @param dateTime Closing time of period
     * @return The closed period
     * @throws DataException Thrown in case of database errors
     */
    public CalendarPeriodIfc closePeriod(CalendarLevelIfc level, EYSDate dateTime) throws DataException
    {
      // Figure out where we are
      methodName = dataCommandName + ".closePeriod()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramLevel = level;
      this.paramEndDateTime = dateTime;

      // Go do it
      CalendarPeriodIfc result = (CalendarPeriodIfc) executeCommand("CloseCalendarPeriod");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }
  }
