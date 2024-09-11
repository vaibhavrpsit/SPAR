/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadAllStoreRegisterSummaryStatus.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
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
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:14 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:39 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:57 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:29    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:39     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:42     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:57     Robert Pearse
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
 *   Revision 1.3  2004/02/12 17:13:16  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:31:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:36:50   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 30 2002 09:37:54   mpm
 * Imported changes for PostgreSQL compatibility from 5.0.
 * Resolution for Backoffice SCR-795: Employee Assignment report abends under Postgresql
 *
 *    Rev 1.1   Mar 18 2002 22:47:08   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:12   msg
 * Initial revision.
 *
 *    Rev 1.0   28 Feb 2002 14:42:30   mia
 * Initial revision.
 * Resolution for Backoffice SCR-658: Refactor DailyOps Open/Close Register
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Abstract class that contains the database calls for reading and populating
 * RegisterStatusBean.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadAllStoreRegisterSummaryStatus extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 4863562693942287306L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadAllStoreRegisterSummaryStatus.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Constant - Workstation Inactive
     */
    public static final String WORKSTATION_STATUS_INACTIVE = "0";

    /**
     * Constant - Workstation Active
     */
    public static final String WORKSTATION_STATUS_ACTIVE = "1";

    /**
     * Class constructor.
     */
    public JdbcReadAllStoreRegisterSummaryStatus()
    {
        super();
        setName("ReadAllStoreRegisterSummaryStatus");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "ReadAllStoreRegisterSummaryStatus.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        /*
         * Send back the correct transaction (or lack thereof)
         */

        Properties qProps = (Properties) action.getDataObject();
        RegisterIfc[] registers = null;
        registers = selectAllRegisters(connection, qProps);
        dataTransaction.setResult(registers);

        if (logger.isDebugEnabled()) logger.debug( "ReadAllStoreRegisterSummaryStatus.execute");
    }


    /**
     */
    public static RegisterIfc[] selectAllRegisters(JdbcDataConnection dataConnection,
                                         Properties props)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        ArrayList registers = new ArrayList();
        RegisterIfc[] returnRegisters = null;

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_WORKSTATION);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_WORKSTATION_ID);
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_WORKSTATION_TERMINAL_STATUS_CODE);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE);

        {  // start anon block
            // Retrieve the properties to be set
            String propStoreID = (String)props.get(RegisterQueryPropertiesIfc.FIELD_STOREID);
            EYSDate propBDate = (EYSDate)props.get(RegisterQueryPropertiesIfc.FIELD_BUSINESS_DATE);
            Integer propBDOp = (Integer) props.get(RegisterQueryPropertiesIfc.FIELD_BUSINESS_DATE_OPERATOR);
            Integer[] propStatus = (Integer[]) props.get(RegisterQueryPropertiesIfc.FIELD_STATUS);
            Integer propStatusOp = (Integer) props.get(RegisterQueryPropertiesIfc.FIELD_STATUS_OPERATOR);
            String[] propExcludedRegisterIDS  = (String[]) props.get(RegisterQueryPropertiesIfc.EXCLUDE_REGISTER_IDS);
            String[] propIncludedRegisterIDS  = (String[]) props.get(RegisterQueryPropertiesIfc.INCLUDE_REGISTER_IDS);

            // Set the qualifiers
            /*
             * Add Store ID Qualifier(s)
             */
            if (propStoreID != null && !propStoreID.equals(""))
            {
                sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getValueSQLString(propStoreID));
            }
            /*
             * Add Business Date Qualifier(s)
             */
            if (propBDate != null && propBDOp != null)
            {
                switch (propBDOp.intValue())
                {
                    case (RegisterQueryPropertiesIfc.OPERATOR_EQUAL):
                    {
                        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDay(propBDate));
                        break;
                    }
                    default:
                    {
                        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " != " + getBusinessDay(propBDate));
                        break;
                    }
                }
            }
            /*
             * Add Status Qualifier(s)
             */
            if (propStatus != null && propStatusOp != null && propStatus.length > 0 )
            {
                StringBuffer qualifier = new StringBuffer();
                if (propStatus.length == 1)
                {
                    qualifier.append(FIELD_WORKSTATION_TERMINAL_STATUS_CODE)
                               .append(" = ")
                               .append(propStatus[0].intValue());
                }
                else
                {
                    // arbitrary number status
                    qualifier.append("(");
                    for ( int index = 0; index < propStatus.length ; index++)
                    {
                        qualifier.append(FIELD_WORKSTATION_TERMINAL_STATUS_CODE)
                                   .append(" = ")
                                   .append(propStatus[index].intValue());
                        if (index < propStatus.length - 1 )
                        {
                            switch (propStatusOp.intValue())
                            {
                                case (RegisterQueryPropertiesIfc.OPERATOR_OR):
                                {
                                    qualifier.append(" OR ");
                                    break;
                                }
                                default:
                                {
                                    qualifier.append(" AND ");
                                    break;
                                }
                            }
                        }
                    }
                    qualifier.append(")");
                }
                sql.addQualifier(qualifier.toString());
            }
            /*
             * Add the Excluded Register ID qualifier(s)
             */
            if (propExcludedRegisterIDS != null && propExcludedRegisterIDS.length > 0 )
            {
                StringBuffer qualifier = new StringBuffer();
                if (propExcludedRegisterIDS.length == 1)
                {
                    qualifier.append(FIELD_WORKSTATION_ID)
                               .append(" != '")
                               .append(propExcludedRegisterIDS[0])
                               .append("'");
                }
                else
                {
                    // arbitrary number status
                    qualifier.append("(");
                    for ( int index = 0; index < propExcludedRegisterIDS.length ; index++)
                    {
                        qualifier.append(FIELD_WORKSTATION_ID)
                                   .append(" != '")
                                   .append(propExcludedRegisterIDS[index])
                                   .append("'");
                        if (index < propExcludedRegisterIDS.length - 1 )
                        {
                            qualifier.append(" AND ");
                        }
                    }
                    qualifier.append(")");
                }

                sql.addQualifier(qualifier.toString());
            }
            /*
             * Add the Included Register ID qualifier(s)
             */
            if (propIncludedRegisterIDS != null && propIncludedRegisterIDS.length > 0 )
            {
                StringBuffer qualifier = new StringBuffer();
                if (propIncludedRegisterIDS.length == 1)
                {
                    qualifier.append(FIELD_WORKSTATION_ID)
                               .append(" = '")
                               .append(propIncludedRegisterIDS[0])
                               .append("'");
                }
                else
                {
                    // arbitrary number
                    qualifier.append("(");
                    for ( int index = 0; index < propIncludedRegisterIDS.length ; index++)
                    {
                        qualifier.append(FIELD_WORKSTATION_ID)
                                   .append(" = '")
                                   .append(propIncludedRegisterIDS[index])
                                   .append("'");
                        if (index < propIncludedRegisterIDS.length - 1 )
                        {
                            qualifier.append(" OR ");
                        }
                    }
                    qualifier.append(")");
                }

                sql.addQualifier(qualifier.toString());
            }
        }

//        sql.addQualifier(FIELD_WORKSTATION_ACTIVE_STATUS_FLAG, WORKSTATION_STATUS_ACTIVE);
        sql.addOrdering(FIELD_RETAIL_STORE_ID);
        sql.addOrdering(FIELD_WORKSTATION_ID);

        StoreIfc store = null;
        WorkstationIfc workstation = null;
        RegisterIfc register = null;
        try
        {
            dataConnection.execute(sql.getSQLString());

            EYSDate openTime = null;
            ResultSet rs = (ResultSet) dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                String registerID = getSafeString(rs, ++index);
                String storeIDstr = getSafeString(rs, ++index);
                int statusCode = rs.getInt(++index);
                EYSDate businessDate = getEYSDateFromString(rs, ++index);


                register = instantiateRegister();
                workstation = instantiateWorkstation();
                store = instantiateStore();

                /*
                 * Initialize Store object
                 */
                store.setStoreID(storeIDstr);
                /*
                 * Initialize workstation object
                 */
                workstation.setStore(store);
                workstation.setWorkstationID(registerID);
                /*
                 * Initialize register object
                 */
                register.setWorkstation(workstation);
                register.resetTotals();
                register.setWorkstation((WorkstationIfc)workstation.clone()); // cheat
                register.setStatus(statusCode);

                /*
                * Add to List of registers
                */
                registers.add(register);
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR,
                                    "selectAllRegisters: Workstation table",
                                    se);
        }
        catch (Exception e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN,
                                    "selectAllRegisters: Workstation table",
                                    e);
        }
        if (registers.isEmpty())
        {
            throw new DataException(DataException.NO_DATA,
                "No reconciled Registers found in the result set.");
        }

        if (registers.size() > 0)
        {
            returnRegisters = new RegisterIfc[registers.size()];
            registers.toArray(returnRegisters);
        }
        return(returnRegisters);
    }

    /**
       Instantiates a Workstation object.
       <p>
       @return new RegisterIfc object
     */
    protected static WorkstationIfc instantiateWorkstation()
    {
        return(DomainGateway.getFactory().getWorkstationInstance());
    }

    /**
       Instantiates a register object.
       <p>
       @return new RegisterIfc object
     */
    protected static RegisterIfc instantiateRegister()
    {
        return(DomainGateway.getFactory().getRegisterInstance());
    }

    /**
       Instantiates a Store object.
       <p>
       @return new RegisterIfc object
     */
    protected static StoreIfc instantiateStore()
    {
        return(DomainGateway.getFactory().getStoreInstance());
    }

    /**
     */
    public static String getValueSQLString(String value)
    {
        String returnStr = null;
        StringBuffer sb = new StringBuffer();
        if (value != null)
        {
            sb.append("'")
              .append(value)
              .append("'");
            returnStr = sb.toString();
        }
        return returnStr;
    }
    /**
       Returns the string representation of the business day
       <p>
       @param  businessDate     the business day
       @return the business day
     */
    public static String getBusinessDay(EYSDate businessDate)
    {
        return(dateToSQLDateString(businessDate.dateValue()));
    }


    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }

}
