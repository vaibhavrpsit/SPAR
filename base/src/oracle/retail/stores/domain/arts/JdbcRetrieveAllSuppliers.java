/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveAllSuppliers.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech75 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                      SQLException to DataException
 *    abonda 01/03/10 - update header date
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         1/25/2006 4:11:20 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:27:41    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
     $
     Revision 1.6  2004/04/09 16:55:46  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:36  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:45  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:23  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:32:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   21 Aug 2002 11:10:20   adc
 * Initial revision.
 * Resolution for kbpos SCR-2230: Order line items details being duplicated on order save
 *
 *    Rev 1.0   19 Aug 2002 16:01:22   adc
 * Initial revision.
 * Resolution for Backoffice SCR-805: Receiving
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.purchasing.SupplierIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This class provides the methods needed to search the supplier directory.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcRetrieveAllSuppliers extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -1842431199917563187L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcRetrieveAllSuppliers.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcRetrieveAllSupliers.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        SupplierIfc[] retrievedSuppliers = null;

        try
        {
            // attempt to search supplier directory
            retrievedSuppliers = searchSuppliers(connection);
        }
        catch (DataException de)
        {
            throw de;
        }

        dataTransaction.setResult(retrievedSuppliers);

        if (logger.isDebugEnabled()) logger.debug( "JdbcRetrieveAllSuppliers.execute()");
    }

    /**
       Returns the suppliers.
       @param  dataConnection  connection to the db
       @return array of suppliers
       @exception DataException upon error
       @deprecated As of release 13.1
     */
    public SupplierIfc[] searchSuppliers(JdbcDataConnection dataConnection)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_SUPPLIER);

        // add columns
        sql.addColumn(FIELD_SUPPLIER_ID);
        sql.addColumn(FIELD_SUPPLIER_NAME);

        SupplierIfc[] list = null;
        try
        {
            dataConnection.execute(sql.getSQLString());
            list = parseResultSet(dataConnection);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "searchSuppliers", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "searchSuppliers", e);
        }

        return(list);
    }

    /**
       Parses result set and creates suppliers records. <P>
       @param dataConnection data connection
       @return array of SupplierIfc objects
       @exception SQLException thrown if result set cannot be parsed
       @exception DataException thrown if no records in result set
     */
    protected SupplierIfc[] parseResultSet(JdbcDataConnection dataConnection)
        throws SQLException, DataException
    {
        Vector resultVector = new Vector();
        SupplierIfc supplier = null;

        SupplierIfc[] arraySuppliers = null;

        ResultSet rs = (ResultSet) dataConnection.getResult();
        int recordsFound = 0;

        if (rs != null)
        {
            while (rs.next())
            {
                recordsFound++;
                int index = 0;
                supplier = instantiateSupplierIfc();
                supplier.setSupplierID(getSafeString(rs, ++index));
                supplier.setSupplierName(getSafeString(rs, ++index));

                resultVector.addElement(supplier);
            }

            // close result set
            rs.close();
        }

        // handle not found
        if (recordsFound == 0)
        {
            String msg = "JdbcRetrieveAllSuppliers: suppliers not found.";
            throw new DataException(DataException.NO_DATA, msg);
        }

        else
        {
            // copy vector elements to array
            int n = resultVector.size();
            arraySuppliers = new SupplierIfc[n];
            resultVector.copyInto(arraySuppliers);
            if (logger.isInfoEnabled()) logger.info(
                        "" + "Matches found:  " + "" + Integer.toString(n) + "");
        }

        return(arraySuppliers);
    }


    /**
       Instantiates SupplierIfc object. <P>
       @return SupplierIfc object
     */
    public SupplierIfc instantiateSupplierIfc()
    {
        return(DomainGateway.getFactory().getSupplierInstance());
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
