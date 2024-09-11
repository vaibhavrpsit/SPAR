/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcFetchAllLevelItems.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.stock.ItemInfoIfc;
import oracle.retail.stores.domain.stock.ItemInquirySearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

// ---------------------------------------------------------------------------------
/**
 * Fetches the item information for the set of Items that're assigned to the
 * merchandise classification hierarchy group (and it's children groups) from
 * the MerchandiseHierarchy tables in the ARTS data model (Release 4.0).
 * <p>
 * 
 * @version $revision$
 */
// --------------------------------------------------------------------------------
public class JdbcFetchAllLevelItems extends JdbcMerchandiseHierarchyDataOperation
{

    // revision number of this class    
    public static String revisionNumber = "$revision$";

    // serialVersionUID
    private static final long serialVersionUID = -8842109662502682538L;
    
    
    // ---------------------------------------------------------------------
    /**
       Default constructor.
     */
    // ---------------------------------------------------------------------
    public JdbcFetchAllLevelItems()
    {
        super();
        setName("JdbcFetchAllLevelItems");
    }


    // --------------------------------------------------------------------------------
    /**
     * Executes the requested SQL statments against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    // --------------------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {

        //Get the search criteria from action
        ItemInquirySearchCriteriaIfc inquiry = (ItemInquirySearchCriteriaIfc) action.getDataObject();

        //Set the current method name for logging
        String methodName = "JdbcFetchAllLevelItems.execute()";
        if (logger.isDebugEnabled())
        {
            logger.debug(methodName);
        }

        //Make sure we've got the right transaction....
        MerchandiseHierarchyDataTransaction transaction;
        try
        {
            transaction = (MerchandiseHierarchyDataTransaction) dataTransaction;
        }
        catch (ClassCastException ex)
        {
            throw new DataException(DataException.DATA_FORMAT, methodName + ": Invalid dataTransaction ("
                    + dataTransaction.getClass().getName() + ")");
        }

        //Get the items for the current search criteria
        fetchAllMemberItems(transaction, (JdbcDataConnection) dataConnection, inquiry);

        if (logger.isDebugEnabled())
        {
            logger.debug(methodName);
        }

    }

    // --------------------------------------------------------------------------------
    /**
     * Retrieve Item information for the given search criteria
     * 
     * @param transaction The data transaction
     * @param connection The connection to the data source
     * @exception DataException upon error
     */
    // --------------------------------------------------------------------------------
    protected void fetchAllMemberItems(MerchandiseHierarchyDataTransaction transaction, JdbcDataConnection connection,
            ItemInquirySearchCriteriaIfc inquiry) throws DataException
    {

        //Fetch the items
        ItemInfoIfc[] result = doFetchAllLevelItems(connection, inquiry);

        //Set the result to the transaction
        transaction.setResult(result);
    }

    // ---------------------------------------------------------------------------
    /**
     * String identification of this class.
     * <p>
     * 
     * @return A string identifying the class and revision number.
     */
    // ---------------------------------------------------------------------------
    public String toString()
    {
        StringBuilder strResult = Util.classToStringHeader("JdbcFetchAllLevelItems", getRevisionNumber(), hashCode());
        return strResult.toString();
    }

    // --------------------------------------------------------------------------------
    /**
     * Retrieves the source-code-control system revision number.
     * <P>
     * 
     * @return String representation of revision number
     */
    // --------------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }
}
