/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadAssociateTotals.java /main/12 2013/09/05 10:36:18 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 10:01:15 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/25/2006 4:11:14 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:57 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:04    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:42     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:57     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:16  mcs
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
 *    Rev 1.0   Aug 29 2003 15:31:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:36:50   msg
 * Initial revision.
 *
 *    Rev 1.0   09 Apr 2002 16:58:20   jbp
 * Initial revision.
 * Resolution for POS SCR-15: Sales associate activity report performs inadequately, crashes
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AssociateProductivityIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads the associate productivity table.
 * 
 * @version $Revision: /main/12 $
 */
public class JdbcReadAssociateTotals extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 1296981270799440933L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadAssociateTotals.class);

    /**
     * revision number supplied by VM
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Class constructor.
     */
    public JdbcReadAssociateTotals()
    {
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadAssociateTotals.execute()");

        // Down cast the connecion and call the select
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        SQLSelectStatement sql = new SQLSelectStatement();
        AssociateProductivitySearchCriteria serachCriteria =
          (AssociateProductivitySearchCriteria)action.getDataObject();
        /*
         * Define table
         */
        sql.addTable(TABLE_SALES_ASSOCIATE_PRODUCTIVITY);
        EYSDate date = new EYSDate();

        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_BUSINESS_DAY_DATE);
        sql.addColumn(FIELD_EMPLOYEE_ID);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_NET_SALES_TOTAL_AMOUNT);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(serachCriteria.getStoreID()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " BETWEEN " +
                         dateToSQLDateString(serachCriteria.getStartDate()) + " AND " +
                         dateToSQLDateString(serachCriteria.getEndDate()));
        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            dataTransaction.setResult(processResults(rs));
        }
        catch (SQLException se)
        {
            logger.error( "" + se + "");
            throw new DataException(DataException.SQL_ERROR, "ReadAssociateTotals", se);

        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadAssociateTotals.execute()");
    }

    /**
       Processes the result set retrieved by the dynamic sql call.
       <P>
       @param  ResultSet           The sql resultset
       @return AssociateProductivityIfc[]   An array of associateProductivity interface objects
       @exception DataException upon error
       @exception SqlException upon error
     */
    public static AssociateProductivityIfc[] processResults(ResultSet rs) throws DataException, SQLException
    {
        AssociateProductivityIfc[] productivityArray = null;
        Vector productivityVector = new Vector(1);

        int index = 0;
        while (rs.next())
        {
            AssociateProductivityIfc ap = DomainGateway.getFactory().getAssociateProductivityInstance();
            EmployeeIfc emp = DomainGateway.getFactory().getEmployeeInstance();
            CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
            // set date
            ap.setDate(getEYSDateFromString(rs,1));
            // set employee Id
            emp.setEmployeeID(rs.getString(2).trim());
            ap.setAssociate(emp);
            // set amount
            amount.setStringValue(rs.getString(3));
            ap.setNetAmount(amount);

            productivityVector.add(index, ap);
            index++;
        }
        rs.close();

        if (!productivityVector.isEmpty())
        {
            // copy vector to array
            productivityArray = new AssociateProductivityIfc[productivityVector.size()];
            productivityVector.copyInto(productivityArray);
        }
        else
        {
            logger.warn( "No associate productivity found");
            throw new DataException(DataException.NO_DATA, "No associate productivity found");
        }

        return(productivityArray);
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
