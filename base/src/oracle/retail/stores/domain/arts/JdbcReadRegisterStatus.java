/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadRegisterStatus.java /main/13 2013/09/05 10:36:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
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
 *    5    360Commerce 1.4         8/3/2006 5:23:51 PM    Brett J. Larsen CR
 *         19009 - workaround for case when an employee is deleted out from
 *         under the POS - this should not happen - it is not allowed from our
 *          interfaces - however, external entities have access to our
 *         database and this was a problem for services
 *
 *         v7x->360Commerce merge
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse   
 *:
 *
 *    5    .v7x      1.3.1.0     6/16/2006 11:11:19 AM  Michael Wisbauer added
 *         code to handle not finding the employee.
 *
 *    4    .v700     1.2.1.0     11/16/2005 16:26:35    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 12 2003 18:54:14   DCobb
 *  Removed stationary qualifier in selectRegisterTills().
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.1   Dec 20 2002 11:15:26   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.0   Jun 03 2002 16:37:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:40   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:38   msg
 * Initial revision.
 *
 *    Rev 1.1   26 Oct 2001 09:52:50   epd
 * This Data Operation now selects any tills associated
 * with the desired register
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 20 2001 16:00:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads the current status of a workstation.
 * 
 * @version $Revision: /main/13 $
 */
public class JdbcReadRegisterStatus extends JdbcReadRegister implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 2658712895114141136L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadRegisterStatus.class);

    /**
     * Class constructor.
     */
    public JdbcReadRegisterStatus()
    {
        super();
        setName("JdbcReadRegisterStatus");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadRegisterStatus.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        WorkstationIfc workstation = (WorkstationIfc)action.getDataObject();
        RegisterIfc register = readRegisterStatus(connection, workstation);

        // Retrieve the list of tills for this register
        if (register != null)
        {
            selectRegisterTills(connection, register);
        }

        /*
         * Send back the result
         */
        dataTransaction.setResult(register);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadRegisterStatus.execute()");
    }

    /**
       Returns the current status of the register.
       <P>
       @param  dataConnection  connection to the db
       @param  workstation     the workstation
       @return register status
       @exception DataException upon error
     */
    public RegisterIfc readRegisterStatus(JdbcDataConnection dataConnection,
                                          WorkstationIfc workstation)
        throws DataException
    {
        RegisterIfc register = selectWorkstation(dataConnection, workstation);

        return(register);
    }


    /**
       Updates the Register object with a list of Tills associated with this register.
       <P>
       @param  dataConnection   connection to the db
       @param  register         the register
       @return register status
       @exception DataException upon error
     */
    public void selectRegisterTills(JdbcDataConnection dataConnection,
                                    RegisterIfc register)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_TILL, ALIAS_TILL);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID);
        sql.addColumn(FIELD_TILL_SIGNON_OPERATOR);
        sql.addColumn(FIELD_TILL_SIGNOFF_OPERATOR);
        sql.addColumn(FIELD_TILL_STATUS_CODE);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE);

        sql.addColumn(FIELD_WORKSTATION_ACCOUNTABILITY);
        sql.addColumn(FIELD_TILL_TYPE);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + makeSafeString(register.getWorkstation().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + makeSafeString(register.getWorkstation().getWorkstationID()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + dateToSQLDateString(register.getBusinessDate()));

        try
        {
            dataConnection.execute(sql.getSQLString());

            String signOnOperatorID = null;
            String signOffOperatorID = null;
            HashMap signOnOperatorMap = new HashMap(1);
            HashMap signOffOperatorMap = new HashMap(1);
            ResultSet rs = (ResultSet) dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                String tillID = getSafeString(rs,++index);
                signOnOperatorID = getSafeString(rs, ++index);
                signOffOperatorID = getSafeString(rs, ++index);
                int statusCode = rs.getInt(++index);
                EYSDate businessDate = getEYSDateFromString(rs, ++index);

                String accountability = getSafeString(rs, ++index);
                String tillType = getSafeString(rs, ++index);

                /*
                 * Initialize till object
                 */
                TillIfc till = DomainGateway.getFactory().getTillInstance();
                till.resetTotals();
                till.setTillID(tillID);
                till.setStatus(statusCode);
                till.setBusinessDate(businessDate);

                till.setRegisterAccountability(Integer.parseInt(accountability));
                till.setTillType(Integer.parseInt(tillType));
                till.setDrawerID("");

                // put operator ID's in hash in order to use to lookup Employee below for each Till
                if (signOnOperatorID != null && signOnOperatorID.length() > 0)
                {
                    signOnOperatorMap.put(tillID, signOnOperatorID);
                }
                if (signOffOperatorID != null && signOffOperatorID.length() > 0)
                {
                    signOffOperatorMap.put(tillID, signOffOperatorID);
                }

                register.addTill(till);
            }

            rs.close();


            TillIfc[] tills = register.getTills();
            for (int i=0; i<tills.length; i++)
            {
                String tillID = tills[i].getTillID();
                if (signOnOperatorMap.containsKey(tillID))
                {
                    EmployeeIfc employee = getTillEmployee(dataConnection, (String)signOnOperatorMap.get(tillID));
                    tills[i].setSignOnOperator(employee);
                    tills[i].addCashier(employee);
                }
                if (signOffOperatorMap.containsKey(tillID))
                {
                    EmployeeIfc employee = getTillEmployee(dataConnection, (String)signOffOperatorMap.get(tillID));
                    tills[i].setSignOffOperator(employee);
                }
            }
            register.setTills(tills);
            tills = null;

        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "selectTill: Till table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectTill: Till table", e);
        }
    }

    /***************************************************************************************
     *  This method was created to handle an employee that was sign on a till but the emp 
     *  record does not exist in the pa_em table.  this should not happen because base marks 
     *  emp's as in-active and not delete the record.  This method is to handle an outside process 
     *  modifying the table.  Need to still have an employee object at least with an id.
     * @param dataConnection
     * @param operatorID
     * @return
     */
	private EmployeeIfc getTillEmployee(JdbcDataConnection dataConnection, String operatorID)
	{
		EmployeeIfc emp;
		try
		{
			emp = getEmployee(dataConnection, operatorID);
			
			
		}
        catch(DataException de)
        {
            logger.warn("Unable to retrieve employee " + operatorID + 
                    ". Using employee info available to contruct the EmployeeIfc object.");                    
            logger.warn("Reason: " + de);
            emp = DomainGateway.getFactory().getEmployeeInstance();
            emp.setEmployeeID(operatorID);
        }
		
		return emp;
	}
}

