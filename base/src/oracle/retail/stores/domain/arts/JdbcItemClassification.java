/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcItemClassification.java /main/3 2013/08/08 16:51:16 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/05/13 - renamed column id_lst_itm per Luis
 *    sthallam  04/05/12 - Enhanced RPM Integration-Item Mod Classification
 * 
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This class provides the methods needed to get classifications attached to the item.
 * 
 * @version $Revision: /main/3 $
 */
public class JdbcItemClassification extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -1842431199917563187L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcItemClassification.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/3 $";

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSelectItemClassification.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        PLUItemIfc pluItem = (PLUItemIfc) action.getDataObject();
        try
        {
            // getting classifications
            getClassifications(connection, pluItem);
        }
        catch (DataException de)
        {
            throw de;
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcSelectItemClassification.execute()");
    }

    /**
       Returns the classifications.
       @param  dataConnection  connection to the db
       @return array of classifications
       @exception DataException upon error
     */
    public List<MerchandiseClassificationIfc> getClassifications(JdbcDataConnection dataConnection,PLUItemIfc pluItem)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_ITEM_CLASSIFICATIONS);
        // add columns
        sql.addColumn(FIELD_MERCHANDISE_CLASSIFICATION_CODE);
        sql.addQualifier(new SQLParameterValue(TABLE_ITEM_CLASSIFICATIONS,FIELD_ITEM_ID, pluItem.getItemID()));
        List<MerchandiseClassificationIfc> classificationList = null;
        try
        {
            dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
            classificationList = parseResultSet(dataConnection);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "getClassifications", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "getClassifications", e);
        }

        return classificationList;
    }

    /**
       Parses result set and creates classification records. <P>
       @param dataConnection data connection
       @return array of MerchandiseClassificationIfc objects
       @exception SQLException thrown if result set cannot be parsed
       @exception DataException thrown if no records in result set
     */
    protected List<MerchandiseClassificationIfc> parseResultSet(JdbcDataConnection dataConnection)
        throws SQLException, DataException
    {
        MerchandiseClassificationIfc classification = null;
        List<MerchandiseClassificationIfc> classifications = new ArrayList<MerchandiseClassificationIfc>();

        ResultSet rs = (ResultSet) dataConnection.getResult();

        if (rs != null)
        {
            while (rs.next())
            {
                int index = 0;
                classification = instantiateMerchandiseClassificationsIfc();
                classification.setIdentifier(getSafeString(rs, ++index));
                classifications.add(classification);
            }

            // close result set
            rs.close();
        }

        return classifications;
    }


    /**
       Instantiates MerchandiseClassificationIfc object. <P>
       @return MerchandiseClassificationIfc object
     */
    public MerchandiseClassificationIfc instantiateMerchandiseClassificationsIfc()
    {
        return(DomainGateway.getFactory().getMerchandiseClassificationInstance());
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
