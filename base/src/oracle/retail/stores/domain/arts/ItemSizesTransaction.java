/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ItemSizesTransaction.java /main/14 2011/01/27 19:03:04 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:32 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:42 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/03/16 18:27:08  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.2  2004/03/12 23:02:57  lzhao
 *   @scr #3840 Inquiry Operations: Inventory Inquiry
 *   Add item size feature, get item size code based on table description.
 *
 *   Revision 1.1  2004/02/18 22:42:14  epd
 *   @scr 3561 New data transaction reads all available size codes from database
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;

import org.apache.log4j.Logger;

/**
 * Reads all the item sizes from the database.
 *
 * @author epd 
 */
public class ItemSizesTransaction extends DataTransaction
{
    private static final long serialVersionUID = -2470499626029072830L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(DatabasePurgeTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * The name that links this transaction to a command within DataScript.
     */
    protected static final String dataCommandName = "ItemSizesTransaction";

    /**
     * Read all the Size Codes from the database
     * 
     * @return An array of Item Size objects for all sizes in the database.
     * @exception DataException when an error occurs
     */
    public ItemSizeIfc[] readItemSizeCodes(LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("ItemSizesTransaction.readItemSizeCodes");

        ItemSizeIfc[] retrievedItemSizeCodes = null;

        applyDataObject(localeRequestor);

        retrievedItemSizeCodes = (ItemSizeIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("ItemSizesTransaction.readItemSizeCodes");

        return (retrievedItemSizeCodes);
    }

    /**
     * Read the item size which has the table description from the database
     * 
     * @param localeRequestor Locales to search
     * @param tableDescription Color ID to search
     * @return An Item Size object in the database.
     * @exception DataException when an error occurs
     */
    public ItemSizeIfc readItemSizeCode(LocaleRequestor localeRequestor, String tableDescription) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("ItemSizesTransaction.readItemSizeCode");

        ItemSizeIfc retrievedItemSizeCode = null;

        StringSearchCriteria searchCriteria = new StringSearchCriteria(localeRequestor, tableDescription);
        applyDataObject(searchCriteria);

        retrievedItemSizeCode = (ItemSizeIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("ItemSizesTransaction.readItemSizeCode");

        return (retrievedItemSizeCode);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.DataTransaction#getTransactionName()
     */
    public String getTransactionName()
    {
        return dataCommandName;
    }
}
