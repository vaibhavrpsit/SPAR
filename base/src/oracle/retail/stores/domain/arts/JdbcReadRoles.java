/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadRoles.java /main/25 2014/02/10 11:22:29 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  02/06/14 - Fortify Null Derefernce fix
 *    mjwallac  12/19/13 - fix POS null dereferences (part 1)
 *    mjwallac  05/01/12 - Fortify: fix redundant null checks, part 3
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    masahu    07/07/11 - FORTIFY FIX: The sensitive SQLs get logged
 *    acadar    08/24/10 - updated based on review comments
 *    acadar    08/23/10 - changes for roles
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    sgu       03/17/09 - use LocaleUtilities.getLocaleFromString to convert
 *                         String to Locale object
 *    blarsen   11/07/08 - removed various warnings - fixed query which
 *                         retrieved localized role function titles
 *    blarsen   11/06/08 - fixing db2 issue - ID_GP_WRK, an int, is quoted
 *    blarsen   11/04/08 - moved JdbcEmployeeLookupOps.getRoles into this class
 *                         - refactored method to reuse existing query - added
 *                         support for a Role search criteria to retrieve just
 *                         one role
 *    ranojha   10/31/08 - Ensure all base currency/alt currency and database
 *                         locales are deprecated
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:38    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.7.2.2  2004/10/21 16:15:33  jdeleau
 *   @scr 7436 Fix crash on looking up employee by ID
 *
 *   Revision 1.7.2.1  2004/10/15 18:50:24  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.9  2004/10/11 22:05:02  jdeleau
 *   @scr 7306 Further updates to reading roles by application
 *
 *   Revision 1.8  2004/10/11 22:00:49  jdeleau
 *   @scr 7306 Fix roles not appearing after they are created
 *
 *   Revision 1.7  2004/08/18 01:53:33  kll
 *   @scr 6826: extract roles specific to POS
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Apr 22 2003 12:32:28   adc
 * Changes to accomodate new Back Office requirements
 * Resolution for 1935: Roles/Security updates
 *
 *    Rev 1.2   Mar 27 2003 11:14:46   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.1   Feb 24 2003 11:03:04   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:38:00   msg
 * Initial revision.
 *
 *    Rev 1.3   23 May 2002 19:17:20   sfl
 * Fixed the security access role problem caused by
 * DB2 changes. Now the fix makes Syabse, Postgresql,
 * and DB2 all work.
 * Resolution for Domain SCR-28: Porting POS 5.0 to Postgresql
 *
 *    Rev 1.2   May 19 2002 01:08:04   mpb
 * In processResults(), compare bFlag with a char '0' instead of an int 0.
 * Resolution for Backoffice SCR-890: DB2: Get DB error for saving role.
 *
 *    Rev 1.1   Mar 18 2002 22:45:52   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:54   msg
 * Initial revision.
 *
 *    Rev 1.1   23 Jan 2002 16:57:08   baa
 * sort role function in alphabetical order
 * Resolution for POS SCR-167: Store parameters entered despite security access restriction
 * Resolution for POS SCR-197: Find Role not listing role names in alphabetical order
 *
 *    Rev 1.0   Sep 20 2001 16:00:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.Role;
import oracle.retail.stores.domain.employee.RoleFunctionGroupIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.LocaleMap;

/**
    This operation reads the role table.
     <P>
    @version $Revision: /main/25 $

**/
public class JdbcReadRoles extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
     *
     */
    private static final long serialVersionUID = 8127544747880608349L;

    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadRoles.class);

    /**
       revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/25 $";

    /**
       Class constructor.
     */
    public JdbcReadRoles()
    {
    }

    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadRoles.execute()");

        SearchCriteriaIfc searchCriteria = (SearchCriteriaIfc) action.getDataObject();

        if (searchCriteria != null && searchCriteria.getLocaleRequestor() == null)
        {
            searchCriteria.setLocaleRequestor(getDefaultLocaleRequestor());
        }

        SQLSelectStatement sql = buildSelectStatement(searchCriteria);
        Serializable result = readRoles(dataConnection, action, sql);
        dataTransaction.setResult(result);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadRoles.execute()");
    }


    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @param sql the SQL statement to be executed
       @exception DataException upon error
    **/
     protected Serializable readRoles(DataConnectionIfc dataConnection, DataActionIfc action, SQLSelectStatement sql)
  throws DataException
  {
      SearchCriteriaIfc searchCriteria = null;
        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            LocaleRequestor locales = null;

            if(action.getDataObject() instanceof SearchCriteriaIfc)
            {
                searchCriteria = (SearchCriteriaIfc)action.getDataObject();
                locales = searchCriteria.getLocaleRequestor();
            }

            // create a locale requestor with the default locale if no requestor was specified
            // this should not happen, log an error
            if (locales == null)
            {
                locales = getDefaultLocaleRequestor();
                logger.error("LocaleRequestor not specifed.  Using DEFAULT locale");
            }

            RoleIfc[] roles = processResults(rs, dataConnection, searchCriteria);
            return roles;
        }
        catch (SQLException se)
        {
            logger.error( "" + se + "");
            throw new DataException(DataException.SQL_ERROR, "ReadRoles", se);

        }
     }


    /**
    *
    * @return
    * @deprecated 7.0, a searchCriteria object must be passed in
     */
    protected SQLSelectStatement buildSelectStatement()
    {
         return buildSelectStatement(null);
    }
         /**
       Builds the  SQL statement.<P>
       @return SQLSelectStatement
    **/
         protected static SQLSelectStatement buildSelectStatement(SearchCriteriaIfc searchCriteria)
     {
         SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Define table
         */
        sql.addTable(TABLE_ROLE, ALIAS_ROLE);
        sql.addTable(TABLE_ROLE_ACCESS, ALIAS_ROLE_ACCESS);
        sql.addTable(TABLE_RESOURCE, ALIAS_RESOURCE);

        /*
         * Add columns and their values
        */
        sql.addColumn(ALIAS_ROLE + "." + FIELD_ROLE_ID);        //1
        sql.addColumn(FIELD_ROLE_DESCRIPTION);                  //2
        sql.addColumn(FIELD_GROUP_RESOURCE_ID);                 //3
        sql.addColumn(FIELD_GROUP_WRITE_ACCESS_LEVEL_FLAG);     //4
        sql.addColumn(FIELD_GROUP_RESOURCE_DESCRIPTION);         //5
        sql.addColumn(ALIAS_RESOURCE + "." + FIELD_GROUP_PARENT_NAME); //6
        sql.addColumn(ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_VISIBILITY_CODE); //7
        sql.addColumn(ALIAS_ROLE_ACCESS + "." + FIELD_PARENT_RESOURCE_ID); //8
        sql.addColumn(ALIAS_ROLE_ACCESS + "." + FIELD_ID_APPLICATION);


        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(ALIAS_ROLE + "." + FIELD_ROLE_ID + " = " +
                         ALIAS_ROLE_ACCESS + "." + FIELD_ROLE_ID);

        sql.addQualifier(ALIAS_RESOURCE + "." + FIELD_PARENT_RESOURCE_ID + " = " +
                         ALIAS_ROLE_ACCESS + "." + FIELD_PARENT_RESOURCE_ID);

        buildSearchCriteriaClause(searchCriteria, sql);

        sql.addOrdering(FIELD_ROLE_DESCRIPTION);
        return sql;
     }

    /*
     * Adds where clauses for the search criteria to the specified sql statement
     *
     * @param searchCriteria
     * @param sql
     */
    private static void buildSearchCriteriaClause(SearchCriteriaIfc searchCriteria, SQLSelectStatement sql)
    {
        if(searchCriteria != null)
        {
            if(searchCriteria.getApplicationId() != RoleIfc.UNKNOWN)
            {
                sql.addQualifier(ALIAS_ROLE_ACCESS + "." + FIELD_ID_APPLICATION + " = " +
                         searchCriteria.getApplicationId());
            }

            if (searchCriteria.getRole() != null)
            {
                sql.addQualifier(ALIAS_ROLE_ACCESS + "." + FIELD_ROLE_ID + " = " +
                        searchCriteria.getRole().getRoleID());

            }
        }
    }


    /**
       Processes the result set retrieved by the dynamic sql call.
       <P>
       @param  ResultSet           The sql resultset
       @return RoleIfc[]   An array of role interface objects
       @exception DataException upon error
       @exception SqlException upon error
       @deprecated deprecated as of 13.1 - locales are required and will be specified by the caller
     */
    public static RoleIfc[] processResults(ResultSet rs) throws DataException, SQLException
    {
        int holdRoleID              = RoleFunctionIfc.FUNCTION_UNDEFINED;
        RoleIfc role                = null;
        Vector roleVector           = new Vector(0);
        RoleIfc[] rolesArray        = null;
        Vector funcVector           = new Vector(2);
        RoleFunctionIfc[] funcArray = new RoleFunctionIfc[2];;
        int roleID                  = 0;
        String roleTitle            = null;

        while (rs.next())
        {
            // Grab the fields selected from the database


            roleID    = rs.getInt(1);
            if (roleID != holdRoleID)
            {
                // if not the first time ..
                if (holdRoleID != RoleFunctionIfc.FUNCTION_UNDEFINED)
                {
                    // Add the functions to the role and the role to the vector
                    funcArray = new RoleFunctionIfc[funcVector.size()];
                    funcVector.copyInto((RoleFunctionIfc[])funcArray);
                    if (role != null)
                    {
                        role.setFunctions(funcArray);
                        roleVector.addElement((RoleIfc)role);
                    }
                }

                // Set up the next the role and function array
                holdRoleID = roleID;

                role = DomainGateway.getFactory().getRoleInstance();
                role.setRoleID(roleID);
                roleTitle = getSafeString(rs,2);
                role.setTitle(roleTitle);
                funcVector = new Vector(2);
            }

            // Get the function info and build the function
            int funcID       = rs.getInt(3);
            //byte bFlag       = rs.getByte(4);
            String bFlag     = rs.getString(4);
            boolean access   = true;

            if (bFlag.equals("0"))
            {
                access = false;
            }
            String funcTitle = getSafeString(rs, 5);
            String groupName = getSafeString(rs, 6);

            int visibilityLvl = rs.getInt(7);
            int groupID = rs.getInt(8);

            if (funcVector != null)
            {
                RoleFunctionIfc rf  = DomainGateway.getFactory().getRoleFunctionInstance();
    
                rf.setFunctionID(funcID);
                rf.setTitle(funcTitle);
                rf.setAccess(access);
                RoleFunctionGroupIfc functionGroup = DomainGateway.getFactory().getRoleFunctionGroupInstance();
                functionGroup.setGroupID(groupID);
                functionGroup.setGroupName(groupName);
                rf.setFunctionGroup(functionGroup);
                rf.setVisibilityLevel(visibilityLvl);
                funcVector.addElement(rf);
            }
        }
        rs.close();

        // Add the last role to the vector
        if (role != null)
        {
            funcArray = new RoleFunctionIfc[funcVector.size()];
            funcVector.copyInto((RoleFunctionIfc[])funcArray);
            role.setFunctions(funcArray);
            roleVector.addElement((RoleIfc)role);
        }

        if (roleVector.isEmpty())
        {
            logger.warn( "No roles found");
            throw new DataException(DataException.NO_DATA, "No roless found");
        }
        else
        {
            rolesArray = new RoleIfc[roleVector.size()];
            roleVector.copyInto((RoleIfc[])rolesArray);
        }

        return(rolesArray);
    }
    /**
         Processes the result set retrieved by the dynamic sql call.
         <P>
         @param  ResultSet           The sql resultset
         @param  dataConnection   DataConnectionIfc
         @param  Locale              Locale of the register
         @return RoleIfc[]   An array of role interface objects
         @exception DataException upon error
         @exception SqlException upon error
         @deprecated deprecated as of 13.1 - LocaleRequestor now used to specify locale
     */
            public static RoleIfc[] processResults(ResultSet rs, DataConnectionIfc dataConnection, Locale sqlLocale) throws DataException, SQLException
      {
          int holdRoleID              = RoleFunctionIfc.FUNCTION_UNDEFINED;
          RoleIfc role                = null;
          Vector roleVector           = new Vector(0);
          RoleIfc[] rolesArray        = null;
          Vector funcVector           = new Vector(2);;
          RoleFunctionIfc[] funcArray = new RoleFunctionIfc[2];
          int roleID                  = 0;
          String roleTitle            = null;

          while (rs.next())
          {
              // Grab the fields selected from the database
              roleID    = rs.getInt(1);
              if (roleID != holdRoleID)
              {
                  // if not the first time ..
                  if (holdRoleID != RoleFunctionIfc.FUNCTION_UNDEFINED)
                  {
                      // Add the functions to the role and the role to the vector
                      funcArray = new RoleFunctionIfc[funcVector.size()];
                      funcVector.copyInto((RoleFunctionIfc[])funcArray);
                      if (role != null)
                      {
                          role.setFunctions(funcArray);
                          roleVector.addElement((RoleIfc)role);
                      }
                  }

                  // Set up the next the role and function array
                  holdRoleID = roleID;

                  role = DomainGateway.getFactory().getRoleInstance();
                  role.setRoleID(roleID);
                  roleTitle = getSafeString(rs,2);
                  role.setTitle(roleTitle);
                  funcVector = new Vector(2);
              }

              // Get the function info and build the function
              int funcID       = rs.getInt(3);
              //byte bFlag       = rs.getByte(4);
              String bFlag     = rs.getString(4);
              boolean access   = true;

              if (bFlag.equals("0"))
              {
                  access = false;
              }
              String funcTitle = getSafeString(rs,5);
              String groupName = getSafeString(rs, 6);
              int visibilityLvl = rs.getInt(7);
              int groupID = rs.getInt(8);
              RoleFunctionIfc rf  = DomainGateway.getFactory().getRoleFunctionInstance();
              rf.setFunctionID(funcID);
              rf.setTitle(funcTitle);
              rf.setAccess(access);
              RoleFunctionGroupIfc group = DomainGateway.getFactory().getRoleFunctionGroupInstance();
              group.setGroupID(groupID);
              group.setGroupName(groupName);
              rf.setFunctionGroup(group);
              rf.setVisibilityLevel(visibilityLvl);
              if (funcVector != null)
              {
                  funcVector.addElement(rf);
              }
          }
          rs.close();

              // Add the last role to the vector
          if (role != null)
          {
              funcArray = new RoleFunctionIfc[funcVector.size()];
              funcVector.copyInto((RoleFunctionIfc[])funcArray);
              role.setFunctions(funcArray);
              roleVector.addElement((RoleIfc)role);
          }

          if (roleVector.isEmpty())
          {
              logger.warn( "No roles found");
              throw new DataException(DataException.NO_DATA, "No roless found");
          }
          else
          {
              try
              {
                  RoleIfc tmpRole = null;
                  if(!sqlLocale.equals(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)))
                  {
                      for(int i = 0; i < roleVector.size(); i++)
                      {
                          tmpRole = (RoleIfc) roleVector.elementAt(i);
                          funcVector = new Vector(0);

                          int tempID = tmpRole.getRoleID();
                          SQLSelectStatement sqlI18N = new SQLSelectStatement();
                          sqlI18N.addTable(TABLE_ROLE_ACCESS_I8, ALIAS_ROLE_ACCESS);
                          sqlI18N.addTable(TABLE_RESOURCE_I8, ALIAS_RESOURCE);

                          //add columns
                          sqlI18N.addColumn("distinct "+ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_RESOURCE_DESCRIPTION);
                          sqlI18N.addColumn(ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_RESOURCE_ID);
                          sqlI18N.addColumn(ALIAS_RESOURCE + "." + FIELD_GROUP_PARENT_NAME);

                          //add qualifiers
                          StringBuffer qualI18N = new StringBuffer();
                          qualI18N.append(ALIAS_ROLE_ACCESS + "." + FIELD_LOCALE +" = '" + sqlLocale + "'");
                          qualI18N.append(" AND " + ALIAS_ROLE_ACCESS + "." + FIELD_EMPLOYEE_ROLE + " = " + tempID);
                          qualI18N.append(" AND " + ALIAS_ROLE_ACCESS + "." + FIELD_PARENT_RESOURCE_ID + " = "
                                                  + ALIAS_RESOURCE + "." + FIELD_PARENT_RESOURCE_ID);
                          qualI18N.append(" AND " + ALIAS_ROLE_ACCESS + "." + FIELD_LOCALE + " = " + ALIAS_RESOURCE + "." + FIELD_LOCALE);

                          sqlI18N.addQualifier(qualI18N.toString());
                          sqlI18N.addOrdering(ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_RESOURCE_DESCRIPTION);

                          dataConnection.execute(sqlI18N.getSQLString());
                          ResultSet rsLocale = (ResultSet)dataConnection.getResult();

                          while (rsLocale.next())
                          {
                              RoleFunctionIfc rf =  DomainGateway.getFactory().getRoleFunctionInstance();

                              String tempTitle = getSafeString(rsLocale, 1);
                              int tempFuncID = rsLocale.getInt(2);
                              String tempFunctionGroupName = getSafeString(rsLocale, 3);
                              rf = tmpRole.getFunction(tempFuncID);
                              RoleFunctionGroupIfc functionGroup = rf.getFunctionGroup();
                              if (functionGroup != null)
                              {
                                  functionGroup.setGroupName(tempFunctionGroupName);
                              }
                              if(rf.getTitle() != "")
                              {
                                  rf.setTitle(tempTitle);
                                  funcVector.addElement(rf);
                              }
                           }
                           rs.close();
                      }
                      if (funcVector != null)
                          {
                          funcArray = new RoleFunctionIfc[funcVector.size()];
                          funcVector.copyInto((RoleFunctionIfc[])funcArray);
                          if (tmpRole != null)
                          {
                              tmpRole.setFunctions(funcArray);
                          }
                      }
                  }

              }
              catch(SQLException se)
              {
                  logger.error( "" + se + "");
                  throw new DataException(DataException.SQL_ERROR, "ReadRoleFunctions", se);
              }
              ResultSet rsLocale = null;
              try
              {
                  if(!sqlLocale.equals(LocaleMap.getLocale(LocaleConstantsIfc.DATABASE)))
                  {
                      SQLSelectStatement sqlI18N = new SQLSelectStatement();
                      sqlI18N.addTable(TABLE_ROLE_I8, ALIAS_ROLE);
                      sqlI18N.addColumn(ALIAS_ROLE + "." + FIELD_ROLE_ID);
                      sqlI18N.addColumn(ALIAS_ROLE + "." + FIELD_ROLE_DESCRIPTION);
                      StringBuffer qualI18N = new StringBuffer();
                      qualI18N.append(ALIAS_ROLE + "." + FIELD_LOCALE +" = '" + sqlLocale + "'");
                      sqlI18N.addQualifier(qualI18N.toString());
                      dataConnection.execute(sqlI18N.getSQLString());
                      rsLocale = (ResultSet)dataConnection.getResult();
                  }

                  if(rsLocale != null)
                  {
                      int lclRoleID = 0;
                      String lclRoleTitle = null;
                      while(rsLocale.next())
                      {
                          lclRoleID = rsLocale.getInt(1);
                          lclRoleTitle = getSafeString(rsLocale,2);
                          RoleIfc tempRl = null;
                          for(int i = 0;i<roleVector.size();i++)
                          {
                              tempRl = (RoleIfc) roleVector.elementAt(i);
                              if(tempRl.getRoleID() == lclRoleID)
                              {
                                  tempRl.setTitle(lclRoleTitle);
                                  break;
                              }
                          }
                      }
                      rsLocale.close();
                  }
              }
              catch (SQLException se)
              {
                  logger.error( "" + se + "");
                  throw new DataException(DataException.SQL_ERROR, "ReadRoles", se);
              }

              rolesArray = new RoleIfc[roleVector.size()];
              roleVector.copyInto((RoleIfc[])rolesArray);
          }

          return(rolesArray);
      }


            /**
         Processes the result set retrieved by the dynamic sql call.
         <P>
         @param  ResultSet           The sql resultset
         @param  dataConnection   DataConnectionIfc
         @param  Locale              Locale of the register
         @return RoleIfc[]   An array of role interface objects
         @exception DataException upon error
         @exception SqlException upon error
     */
      public static RoleIfc[] processResults(ResultSet rs, DataConnectionIfc dataConnection, SearchCriteriaIfc searchCriteria)
      throws DataException, SQLException
      {
          Vector<RoleIfc> roleVector = null;

          /////////////////////////////////////////////////////////////
          // get roles, their functions and the functions' group from the result set
          // this query does not retrieve any I18N descriptions
          roleVector = getRolesFunctionsAndGroups(rs);

          if (roleVector.isEmpty())
          {
              logger.warn( "No roles found");
              throw new DataException(DataException.NO_DATA, "No roles found");
          }

          /////////////////////////////////////////////////////////////
          // update role function groups with I18N descriptions
          // note: role function descriptions use resource bundles for I18N descriptions
          try
          {
              RoleIfc role = null;
              for(int i = 0; i < roleVector.size(); i++)
              {
                  role = (RoleIfc) roleVector.elementAt(i);
                  readRoleFunctionAndFunctionGroupLocalizedDescriptions(dataConnection, role, searchCriteria);
              }
          }
          catch(SQLException se)
          {
              logger.error( "" + se + "");
              throw new DataException(DataException.SQL_ERROR, "ReadRoleFunctions", se);
          }

          /////////////////////////////////////////////////////////////
          // update roles with I18N descriptions
          try
          {
              readRoleLocalizedDescriptions(dataConnection, roleVector, searchCriteria);
          }
          catch (SQLException se)
          {
              logger.error( "" + se + "");
              throw new DataException(DataException.SQL_ERROR, "ReadRoles", se);
          }

          RoleIfc[] rolesArray = new RoleIfc[roleVector.size()];
          roleVector.copyInto((RoleIfc[])rolesArray);

          return(rolesArray);
      }

    /*
     * Process the result set, creating a vector or roles which have their functions and function groups
     *
     * @param rs
     * @param roleVector
     * @throws SQLException
     */
    private static Vector<RoleIfc> getRolesFunctionsAndGroups(ResultSet rs) throws SQLException
    {
        Vector<RoleIfc> roleVector = new Vector<RoleIfc>(20);
        RoleIfc role                = DomainGateway.getFactory().getRoleInstance();
        Vector<RoleFunctionIfc> funcVector = new Vector<RoleFunctionIfc>(20);
        RoleFunctionIfc[] funcArray = null;
        int roleID                  = 0;
        int holdRoleID              = RoleFunctionIfc.FUNCTION_UNDEFINED;

        while (rs.next())
        {
            // Grab the fields selected from the database
            roleID    = rs.getInt(1);
            if (roleID != holdRoleID)
            {
                // if not the first time ..
                if (holdRoleID != RoleFunctionIfc.FUNCTION_UNDEFINED)
                {
                    // Add the functions to the role and the role to the vector
                    funcArray = new RoleFunctionIfc[funcVector.size()];
                    funcVector.copyInto(funcArray);
                    role.setFunctions(funcArray);
                    roleVector.addElement(role);
                }

                // Set up the next the role and function array
                holdRoleID = roleID;

                role = DomainGateway.getFactory().getRoleInstance();
                role.setRoleID(roleID);
                funcVector = new Vector<RoleFunctionIfc>(20);
            }

            // Get the function info and build the function
            int funcID       = rs.getInt(3);
            //byte bFlag       = rs.getByte(4);
            String bFlag     = rs.getString(4);
            boolean access   = true;

            if (bFlag.equals("0"))
            {
                access = false;
            }
            String title = getSafeString(rs,5);
            int visibilityLvl = rs.getInt(7);
            int groupID = rs.getInt(8);
            RoleFunctionIfc rf  = DomainGateway.getFactory().getRoleFunctionInstance();
            rf.setTitle(title);
            rf.setFunctionID(funcID);
            rf.setAccess(access);
            RoleFunctionGroupIfc group = DomainGateway.getFactory().getRoleFunctionGroupInstance();
            group.setGroupID(groupID);
            rf.setFunctionGroup(group);
            rf.setVisibilityLevel(visibilityLvl);
            funcVector.addElement(rf);
        }
        rs.close();

        // Add the last role to the vector
        if (role != null)
        {
            funcArray = new RoleFunctionIfc[funcVector.size()];
            funcVector.copyInto(funcArray);
            role.setFunctions(funcArray);
            roleVector.addElement(role);
        }

        return roleVector;
    }

    /*
     * Update the roles with I18N descriptions from the database
     *
     * @param dataConnection
     * @param locales
     * @param roleVector
     * @throws DataException
     * @throws SQLException
     */
    private static void readRoleLocalizedDescriptions(DataConnectionIfc dataConnection,
            Vector<RoleIfc> roleVector, SearchCriteriaIfc searchCriteria) throws DataException, SQLException
    {
        SQLSelectStatement sqlI18N = buildRoleLocalesSelectStatement(searchCriteria);
        String sqlI18Nstr = sqlI18N.getSQLString();
        dataConnection.execute(sqlI18Nstr);
        ResultSet rsLocale = (ResultSet)dataConnection.getResult();

        if(rsLocale != null)
        {
            /*
             * loop over all locales returned from database
             * for each locale, search the role vector to find the locale's role
             */
            int rowRoleID = 0;
            String lclStr = null;
            String lclRoleTitle = null;
            while(rsLocale.next())
            {
                rowRoleID = rsLocale.getInt(1);
                lclStr = getSafeString(rsLocale,2);
                Locale lcl =  LocaleUtilities.getLocaleFromString(lclStr);
                lclRoleTitle = getSafeString(rsLocale,3);

                RoleIfc role = null;
                for(int i = 0;i<roleVector.size();i++)
                {
                    role = roleVector.elementAt(i);
                    if(role.getRoleID() == rowRoleID)
                    {
                        role.setTitle(lcl, lclRoleTitle);
                        break;
                    }
                }
            }
            rsLocale.close();
        }
    }

    /*
     * Update the specified role's function groups and functions with I18N descriptions from the database
     *
     *  Note: Role function descriptions are not retrieved from the I18N database.  POS uses
     *  resource bundles for role function descriptions.  This is inconsistent.  But it is
     *  out of scope for the I18N project (13.1) to make it consistent.
     *
     * @param dataConnection
     * @param locales
     * @param role
     * @param roleID
     * @throws DataException
     * @throws SQLException
     */
    private static void readRoleFunctionAndFunctionGroupLocalizedDescriptions(
            DataConnectionIfc dataConnection, RoleIfc role, SearchCriteriaIfc searchCriteria)
    throws DataException, SQLException
    {

          Vector<RoleFunctionIfc> functionVector = new Vector<RoleFunctionIfc>(20);
          RoleFunctionIfc[] funcArray = null;

          SQLSelectStatement selectStatement = buildResourceAndFunctionGroupLocalesSelectStatement(
                  role.getRoleID(), searchCriteria);
          String sqlStr = selectStatement.getSQLString();
          dataConnection.execute(sqlStr);
          ResultSet rs = (ResultSet)dataConnection.getResult();

          /*
           * loop over all the localized data for the functions
           * there will likely be more than 1 record for each function
           * keep adding the localized messages
           *
           */
          int previousFunctionID = RoleFunctionIfc.FUNCTION_UNDEFINED;
          RoleFunctionIfc roleFunction = null;
          while (rs.next())
          {
              String rowFuncionTitle = getSafeString(rs, 1);
              int rowFunctionID = rs.getInt(2);
              String rowLocaleStr = getSafeString(rs, 3);
              Locale rowLocale = LocaleUtilities.getLocaleFromString(rowLocaleStr);
              String rowFunctionGroupName = getSafeString(rs, 4);
              String title = getSafeString(rs,5);

              // if the function id differs from the previous function id
              // then, we've processes all the localized records for the previous function
              // and a new function must be created for the current record
              if (previousFunctionID != rowFunctionID)
              {
                  roleFunction = role.getFunction(rowFunctionID);
                  functionVector.addElement(roleFunction);
              }

              if (roleFunction != null)
              {
                  RoleFunctionGroupIfc functionGroup = roleFunction.getFunctionGroup();
                  if (functionGroup != null)
                  {
                      functionGroup.setGroupName(rowLocale, rowFunctionGroupName);
                  }
                  roleFunction.setLocalizedTitle(rowLocale, rowFuncionTitle);
                  roleFunction.setTitle(title);
              }
          }

          rs.close();

          funcArray = new RoleFunctionIfc[functionVector.size()];
          functionVector.copyInto(funcArray);
          role.setFunctions(funcArray);
    }


    /**
     * Finds the role information associated with the current employee.
     *
     * @return Employee employee object
     * @param roleID
     *          roleid
     * @param connection
     *          data connection interface object
     * @exception DataException
     */
        static public RoleIfc getRole(int roleID, DataConnectionIfc dataConnection)
        throws DataException
    {
        return getRole(roleID, dataConnection, getDefaulSearchCriteria());
    }

    /**
     * Finds the role information associated with the current employee.
     *
     * @return Employee employee object
     * @param roleID
     *          roleid
     * @param connection
     *          data connection interface object
     * @param localeRequestor
     *          localeRequestor for roles
     * @exception DataException
     */
        static public RoleIfc getRole(int roleID, DataConnectionIfc dataConnection, LocaleRequestor localeRequestor)
        throws DataException
    {
        return getRole(roleID, dataConnection, getDefaulSearchCriteria(localeRequestor));
    }

    /**
     * Finds the role information associated with the current employee.
     *
     * @return Employee employee object
     * @param roleID
     *          roleid
     * @param connection
     *          data connection interface object
     * @exception DataException
     */
        static public RoleIfc getRole(int roleID, DataConnectionIfc dataConnection, SearchCriteriaIfc searchCriteria)
        throws DataException
    {
        RoleIfc[] roles = null;

        RoleIfc searchRole = new Role();
        searchRole.setRoleID(roleID);
        searchCriteria.setRole(searchRole);

        SQLSelectStatement sql = buildSelectStatement(searchCriteria);
        sql.addQualifier(ALIAS_ROLE_ACCESS + "." + FIELD_ID_APPLICATION + " = " + RoleIfc.POINT_OF_SALE);

        try
        {
            dataConnection.execute(sql.getSQLString(), false);
            ResultSet rs = (ResultSet) dataConnection.getResult();
            roles = processResults(rs, dataConnection, searchCriteria);
            if (roles == null || roles.length == 0)
            {
                logger.error(
                    "JdbcReadRoles.getRole(): No role row is available.");
                throw new DataException(
                    DataException.REFERENTIAL_INTEGRITY_ERROR,
                    "No role row is available.");
            }
            else if (roles.length > 1)
            {
                logger.error(
                    "JdbcReadRoles.getRole(): More than one role row available.");
                throw new DataException(
                    DataException.REFERENTIAL_INTEGRITY_ERROR,
                    "More than one role row available.");
            }
        }
        catch (SQLException se)
        {
            logger.error("" + se + "");
            throw new DataException(
                DataException.SQL_ERROR,
                "Read Role SQL error",
                se);
        }

        return roles[0];
    }

    /*
     * Builds the SQL statement for retrieving localized values for Roles
     *
     * @param locales
     * @return
     */
    private static SQLSelectStatement buildRoleLocalesSelectStatement(SearchCriteriaIfc searchCriteria)
    {
        SQLSelectStatement sqlI18N = new SQLSelectStatement();
        sqlI18N.addTable(TABLE_ROLE_I8, ALIAS_ROLE_I8);
        sqlI18N.addColumn(ALIAS_ROLE_I8 + "." + FIELD_ROLE_ID);
        sqlI18N.addColumn(ALIAS_ROLE_I8 + "." + FIELD_LOCALE);
        sqlI18N.addColumn(ALIAS_ROLE_I8 + "." + FIELD_ROLE_DESCRIPTION);
        StringBuffer qualI18N = new StringBuffer();
        qualI18N.append(ALIAS_ROLE_I8 + "." + FIELD_LOCALE +
                buildINClauseString(LocaleMap.getBestMatch("", searchCriteria.getLocaleRequestor().getLocales())));

        sqlI18N.addQualifier(qualI18N.toString());
        return sqlI18N;
    }

    /*
     * Builds the SQL statement for retrieving localized values for Resources
     *
     * @param locales
     * @param roleID
     * @return
     */
    private static SQLSelectStatement buildResourceAndFunctionGroupLocalesSelectStatement(
            int roleID, SearchCriteriaIfc searchCriteria)
    {
        SQLSelectStatement sqlI18N = new SQLSelectStatement();
        sqlI18N.addTable(TABLE_ROLE_ACCESS, ALIAS_ROLE_ACCESS);
        sqlI18N.addTable(TABLE_RESOURCE_I8, ALIAS_RESOURCE_I8);
        sqlI18N.addTable(TABLE_ROLE_ACCESS_I8, ALIAS_ROLE_ACCESS_I8);

        //add columns
        sqlI18N.addColumn(ALIAS_ROLE_ACCESS_I8 + "." + FIELD_GROUP_RESOURCE_DESCRIPTION_DISPLAY);
        sqlI18N.addColumn(ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_RESOURCE_ID);
        sqlI18N.addColumn(ALIAS_RESOURCE_I8 + "." + FIELD_LOCALE);
        sqlI18N.addColumn(ALIAS_RESOURCE_I8 + "." + FIELD_GROUP_PARENT_NAME);
        sqlI18N.addColumn(ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_RESOURCE_DESCRIPTION);

        //add qualifiers
        StringBuffer qualI18N = new StringBuffer();

        qualI18N.append(ALIAS_RESOURCE_I8 + "." + FIELD_LOCALE +
                buildINClauseString(LocaleMap.getBestMatch("", searchCriteria.getLocaleRequestor().getLocales())));

        qualI18N.append(" AND " + ALIAS_RESOURCE_I8 + "." + FIELD_LOCALE + " = " + ALIAS_ROLE_ACCESS_I8 + "." + FIELD_LOCALE);

        qualI18N.append(" AND " + ALIAS_ROLE_ACCESS + "." + FIELD_EMPLOYEE_ROLE + " = " + roleID);
        qualI18N.append(" AND " + ALIAS_ROLE_ACCESS + "." + FIELD_PARENT_RESOURCE_ID + " = "
                + ALIAS_RESOURCE_I8 + "." + FIELD_PARENT_RESOURCE_ID);
        qualI18N.append(" AND " + ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_RESOURCE_DESCRIPTION + " = "
                + ALIAS_ROLE_ACCESS_I8 + "." + FIELD_GROUP_RESOURCE_DESCRIPTION);

        sqlI18N.addQualifier(qualI18N.toString());

        buildSearchCriteriaClause(searchCriteria, sqlI18N);

        sqlI18N.addOrdering(ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_RESOURCE_DESCRIPTION);

        return sqlI18N;
    }

    /**
       Returns a string representation of this object.
       <P>
       @param none
       @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  JdbcReadRoles (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }

    /*
     * returns a locale requestor for the default locale
     *
     * used in cases where no locales are availabe or specified
     *
     * @return a locale requestor for the default locale
     */
    private static LocaleRequestor getDefaultLocaleRequestor()
    {
        return new LocaleRequestor(LocaleMap.getSupportedLocales());
    }

    /*
     * returns a search criteria with default locale requestor and POS application ID
     *
     * @return a search criteria with default locale and POS application ID
     */
    private static SearchCriteriaIfc getDefaulSearchCriteria()
    {
        return getDefaulSearchCriteria(getDefaultLocaleRequestor());
    }

    /*
     * returns a search criteria with specified locale POS application ID
     *
     * @return a search criteria with specified locale and POS application ID
     */
    private static SearchCriteriaIfc getDefaulSearchCriteria(LocaleRequestor localeRequestor)
    {
        SearchCriteriaIfc searchCriteria = DomainGateway.getFactory().getSearchCriteriaInstance();
        searchCriteria.setLocaleRequestor(localeRequestor);
        searchCriteria.setApplicationId(RoleIfc.POINT_OF_SALE);
        return searchCriteria;
    }

    /**
       Returns the revision number of the class.
       <P>
       @param none
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
