/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveSupplyOrderOperation.java /main/16 2012/05/21 15:50:19 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    npoola    08/25/10 - passed the connection object to the
 *                         IdentifierService getNextID method to use right
 *                         connection
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
 *    8    360Commerce 1.7         6/13/2006 2:14:15 PM   Brett J. Larsen CR
 *         18490 - UDM - QU_ON_HD changed from int -> dec(8,2)
 *    7    360Commerce 1.6         6/1/2006 12:49:56 PM   Charles D. Baker
 *         Remove unused imports
 *    6    360Commerce 1.5         6/1/2006 12:28:42 PM   Brendan W. Farrell
 *         Update comments.
 *    5    360Commerce 1.4         5/31/2006 5:04:01 PM   Brendan W. Farrell
 *         Move from party to id gen.
 *         
 *    4    360Commerce 1.3         1/25/2006 4:11:24 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:08    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:50     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.6  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Feb 15 2003 17:33:46   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Iterator;

import oracle.retail.stores.common.identifier.IdentifierConstantsIfc;
import oracle.retail.stores.common.identifier.IdentifierServiceIfc;
import oracle.retail.stores.common.identifier.IdentifierServiceLocator;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.supply.SupplyOrderIfc;
import oracle.retail.stores.domain.supply.SupplyOrderLineItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * The JdbcSupplyOrderOperation implements the supplyOrder JDBC data store
 * operation.
 * 
 * @version $Revision: /main/16 $
 */
public class JdbcSaveSupplyOrderOperation extends JdbcDataOperation implements DataOperationIfc, ARTSDatabaseIfc
{
    static final long serialVersionUID = -3200431426947601384L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveSupplyOrderOperation.class);

    /**
     * Class constructor.
     */
    public JdbcSaveSupplyOrderOperation()
    {
        super();
        setName("SaveSupplyOrderOperation");
    }

    /**
     * This method is used to execute a specific operation for a specific
     * transaction against a specific datastore. <B>Pre-Condition</B>
     * <UL>
     * <LI>The DataTransactionIfc contains any application-specific data
     * elements.
     * <LI>The DataConnectionIfc is valid.
     * <LI>The DataActionIfc contains the necessary DataObjects.
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>The appropriate data operations have been executed by the
     * DataConnection using the input data provided by the DataTransactionIfc
     * and the DataActionIfc.
     * <LI>Any results have been posted to the DataTransactionIfc.
     * </UL>
     * 
     * @param dt The DataTransactionIfc that provides a place to post results.
     * @param dc The DataConnection that provides a connection to the datastore.
     * @param da The DataActionIfc that provides specific input data for this
     *            operation.
     * @exception DataException is thrown if the operation cannot be completed.
     */
    public void execute(DataTransactionIfc transaction, DataConnectionIfc connection, DataActionIfc action)
            throws DataException
    {
        SupplyOrderIfc supplyOrder = (SupplyOrderIfc)action.getDataObject();

        saveSupplyOrder(connection, supplyOrder);

        transaction.setResult(supplyOrder);
    }

    /**
     * This method saves a supplyOrder object in the database.
     * 
     * @param connection data connection interface object
     * @param an interface of the Supply Order object to be stored in the
     *            database.
     * @exception DataException upon error
     */
    public void saveSupplyOrder(DataConnectionIfc connection, SupplyOrderIfc supplyOrder) throws DataException
    {

        // don't bother generating a transaction number or saving if there are
        // no supply items
        if (supplyOrder.hasLineItems())
        {

            try
            {
                // Build SQL statement.
                String storeID = supplyOrder.getStoreID();
                String associateID = supplyOrder.getAssociateID();
                String orderDate = dateToSQLDateString(supplyOrder.getOrderDate());

                // get an Order ID
                String orderID = supplyOrder.getOrderID();

                if (Util.isEmpty(orderID))
                {
                    // Set the generated ID in the supplyOrder
                    orderID = generateSupplyOrderID(connection);
                    supplyOrder.setOrderID(orderID);
                }

                SQLInsertStatement sql = new SQLInsertStatement();

                sql.setTable(TABLE_SUPPLY_ORDER);
                sql.addColumn(FIELD_RETAIL_STORE_ID, inQuotes(storeID));
                sql.addColumn(FIELD_SUPPLY_ORDER_ID, inQuotes(orderID));
                sql.addColumn(FIELD_SUPPLY_ORDER_ASSOCIATE_ID, inQuotes(associateID));
                sql.addColumn(FIELD_BUSINESS_DAY_DATE, orderDate);

                // do an insert for every line item in the supply order
                Iterator i = supplyOrder.supplyOrderLineItemIterator();
                while (i.hasNext())
                {

                    SupplyOrderLineItemIfc lineItem = (SupplyOrderLineItemIfc)i.next();
                    sql.addColumn(FIELD_SUPPLY_ORDER_SHORT_ID, inQuotes(lineItem.getItemID()));
                    sql.addColumn(FIELD_SUPPLY_ORDER_QUANTITY, lineItem.getStandardOrderQuantity().toString());
                    sql.addColumn(FIELD_SUPPLY_ORDER_ONHAND_QUANTITY, "0.0");

                    connection.execute(sql.getSQLString()); // do the insert.
                } // while (i.hasNext()) {

            }
            catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "JdbcSaveSupplyOrderOperation", e);
        }
      } // hasLineItems()
    }

    /**
     * Generates a unique party id. {@link IdentifierServiceIfc}.
     * 
     * @param dataConnection The connection to the data source
     * @return the party_id
     * @exception DataException upon error
     */
    protected String generateSupplyOrderID(DataConnectionIfc dataConnection) throws DataException
    {
        int id = IdentifierServiceLocator.getIdentifierService().getNextID(
                ((JdbcDataConnection)dataConnection).getConnection(), IdentifierConstantsIfc.COUNTER_SUPPLY_ORDER);
        return String.valueOf(id);
    }

    /**
     * Returns a string representation of this object.
     * <P>
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  JdbcSupplyOrderOperation (Revision " + getRevisionNumber() + ")"
                + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * <P>
     * 
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

}
