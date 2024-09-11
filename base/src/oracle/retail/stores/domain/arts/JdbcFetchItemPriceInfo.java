/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcFetchItemPriceInfo.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
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

// imports
import oracle.retail.stores.domain.stock.ItemInfoIfc;
import oracle.retail.stores.domain.stock.ItemInquirySearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
 The JdbcFetchItemPriceInfo defines methods that the application calls to
 fetch an item's promotion end date and actual price
 **/
//--------------------------------------------------------------------------
public class JdbcFetchItemPriceInfo extends JdbcMerchandiseHierarchyDataOperation
{

    //revision number of this class    
    public static String revisionNumber = "$revision$";

    //serialVersionUID
    private static final long serialVersionUID = 6933195127957997176L;
    
    // ---------------------------------------------------------------------
    /**
       Default constructor.
    **/
    // ---------------------------------------------------------------------
    public JdbcFetchItemPriceInfo()
    {
        super();
        setName("JdbcFetchItemPriceInfo");
    }

    // ---------------------------------------------------------------------
    /**
     * Executes the requested SQL statments against the database.
     *
     * @param  dataTransaction     The data transaction
     * @param  dataConnection      The connection to the data source
     * @param  action              The information passed by the valet
     * @exception DataException upon error
     **/
    //-----------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {

        ItemInquirySearchCriteriaIfc inquiry = (ItemInquirySearchCriteriaIfc) action.getDataObject();

        // Figure out where we are
        String methodName = "JdbcFetchItemPriceInfo.execute()";

        if (logger.isDebugEnabled())
        {
            logger.debug(methodName);
        }

        // Make sure we've got the right transaction....
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

        //Fetch the item's price and promotion details
        ItemInfoIfc result = doFetchItemPriceInfo((JdbcDataConnection) dataConnection, inquiry);

        //Set the result to the transaction
        transaction.setResult(result);

        // We're done
        if (logger.isDebugEnabled())
        {
            logger.debug(methodName);
        }

    }

    // --------------------------------------------------------------------------------
    /**
     * String identification of this class.<p>
     * @return A string identifying the class and revision number.
     */
    // --------------------------------------------------------------------------------
    public String toString()
    {
        StringBuilder strResult = Util.classToStringHeader("JdbcFetchItemPriceInfo", getRevisionNumber(), hashCode());
        return strResult.toString();
    }

    // --------------------------------------------------------------------------------
    /**
     * Retrieves the source-code-control system revision number. <P>
     * @return String representation of revision number
     **/
    // --------------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }
}
