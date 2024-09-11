/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadShippingChargeByPriceRange.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
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
 *    5    360Commerce 1.4         4/25/2007 10:01:13 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:00    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
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
 *    Rev 1.0   Aug 29 2003 15:32:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:38:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:54   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:56   msg
 * Initial revision.
 *
 *    Rev 1.3   11 Jan 2002 16:34:14   sfl
 * Code cleanup based on good suggestions collected during
 * code review.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.2   04 Jan 2002 16:09:42   sfl
 * More comments clean up.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.1   03 Jan 2002 10:06:22   sfl
 * Do the clean up for the comments.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.0   03 Dec 2001 18:13:22   sfl
 * Initial revision.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;



/**
    This operation returns a dollar amount based shipping charge retrieved from
    database Shipping Charge table.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public class JdbcReadShippingChargeByPriceRange extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadShippingChargeByPriceRange.class);

    /**
       Class constructor.
     */
    public JdbcReadShippingChargeByPriceRange()
    {
        super();
        setName("JdbcReadShippingChargeByPriceRange");
    }

    /**
       Executes the SQL statements against the database.
       @param  dataTransaction
       @param  dataConnection
       @param  action   dataAction
       @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingChargeByPriceRange.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        CurrencyIfc currency = null;

        // grab arguments and call ReadShippingChargeByPriceRange()

        CurrencyIfc totalDollar = (CurrencyIfc) action.getDataObject();

        currency = readShippingChargeByPriceRange(connection, totalDollar);

        dataTransaction.setResult(currency);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingChargeByPriceRange.execute");
    }


    /**
       Executes the SQL statements against the database.
       @return  amount of shipping price derived by price range
       @param  dataConnection
       @param  currency   The total price amount of selected send items
       @exception DataException upon error
     */
    public CurrencyIfc readShippingChargeByPriceRange(JdbcDataConnection connection, CurrencyIfc currency) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingChargeByPriceRange.readShippingChargeByPriceRange()");

        CurrencyIfc shippingChargeBasedOnPriceRange = null;

        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_SHIPPING_CHARGE, ALIAS_CHARGE);

        // add columns

        sql.addColumn(FIELD_CHARGE_AMOUNT);

        // add qualifiers

        sql.addQualifier(FIELD_LOWER_AMOUNT + " <= " + currency);
        sql.addQualifier(FIELD_UPPER_AMOUNT + " >= " + currency);

        int rsStatus = 0;

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();


            int index;

            // loop through result set
            while (rs.next())
            {
                ++rsStatus;
                index = 0;
                // parse the data from the database

                shippingChargeBasedOnPriceRange = getCurrencyFromDecimal(rs, ++index);

            }
            // end loop through result set
            // close result set
            rs.close();

        }
        catch (DataException de)
        {
            logger.warn(de);
            if (de.getErrorCode() == DataException.UNKNOWN)
            {
                throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
            }
            else
            {
                throw de;
            }
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "readShippingChargeByPriceRange");
            throw new DataException(DataException.SQL_ERROR, "readShippingChargeByPriceRange", se);
        }

        if (rsStatus == 0)
        {
            logger.warn( "No price range based shipping charge found");
            throw new DataException(DataException.NO_DATA, "No price range based shipping charge found");
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingChargeByPriceRange.readShippingChargeByPriceRange()");

        return(shippingChargeBasedOnPriceRange);
    }
}
