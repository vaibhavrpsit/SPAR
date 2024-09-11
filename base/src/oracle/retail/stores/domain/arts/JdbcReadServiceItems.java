/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadServiceItems.java /rgbustores_13.4x_generic_branch/2 2011/09/15 13:34:44 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/15/11 - removed deprecated methods and changed static
 *                         methods to non-static
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       12/08/09 - rework PLURequestor to use EnumSet and rename
 *                         set/unsetRequestType to add/removeRequestType
 *    sgu       12/02/09 - change the SQL to select all service items to use a
 *                         SQL statement instead of a SQL prepared statement
 *    sgu       12/01/09 - use selectPLU instead of selectPLUs for performance
 *                         reason in reading service items
 *    sgu       11/30/09 - return tax rules during service item lookup only is
 *                         requested
 *    sgu       11/30/09 - add plu requestor to return plu information
 *                         selectively
 *    cgreene   09/25/09 - XbranchMerge cgreene_bug-8931126 from
 *                         rgbustores_13.1x_branch
 *    cgreene   09/24/09 - refactor SQL statements up support
 *                         preparedStatements for updates and inserts to
 *                         improve dept hist perf
 *    cgreene   03/01/09 - upgrade to using prepared statements for PLU
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
 *
 *   Revision 1.7  2004/06/30 15:12:48  jdeleau
 *   @scr 5868 Get tax rules with service item PLUs.
 *
 *   Revision 1.6  2004/04/09 16:55:43  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 31 2003 16:06:44   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:38:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:52   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:54   msg
 * Initial revision.
 *
 *    Rev 1.1   14 Nov 2001 14:27:02   pjf
 * Modified to call selectPLUItems()
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   Sep 20 2001 15:59:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLParameter;
import oracle.retail.stores.common.sql.SQLParameterIfc;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.GeoCodeVO;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcReadServiceItems reads all of the service items from the database.
 */
public class JdbcReadServiceItems extends JdbcPLUOperation
{
    private static final long serialVersionUID = 876964473604758491L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadServiceItems.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * Class constructor.
     */
    public JdbcReadServiceItems()
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
    @Override
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadServiceItems.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        LocaleRequestor locale = null;
        PLURequestor pluRequestor = null;
        String geoCode = null;
        String storeId = null;

        if(action.getDataObject() instanceof SearchCriteriaIfc)
        {
            SearchCriteriaIfc inquiry = (SearchCriteriaIfc) action.getDataObject();
            locale = inquiry.getLocaleRequestor();
            geoCode = inquiry.getGeoCode();
            storeId = inquiry.getStoreNumber();
            pluRequestor = inquiry.getPLURequestor();
        }

        /*
         * Send back the correct transaction (or lack thereof)
         */

        PLUItemIfc[] items = readServiceItems(connection, pluRequestor, locale);
        if (pluRequestor == null || pluRequestor.containsRequestType(PLURequestor.RequestType.TaxRules))
        {
        	if(geoCode == null)
        	{
        		JdbcReadNewTaxRules taxReader = new JdbcReadNewTaxRules();
        		GeoCodeVO geoCodeVO =  taxReader.readGeoCodeFromStoreId(connection, storeId);
        		assignTaxRules(connection, items, geoCodeVO.getGeoCode());
        	}
        	else
        	{
        		assignTaxRules(connection, items, geoCode);
        	}
        }
        dataTransaction.setResult(items);


        if (logger.isDebugEnabled()) logger.debug( "JdbcReadServiceItems.execute");
    }

    /**
     * Reads service items
     * @param dataConnection
     * @param locale
     * @return
     * @throws DataException
     */
    public PLUItemIfc[] readServiceItems(JdbcDataConnection dataConnection, PLURequestor pluRequestor, LocaleRequestor locale) throws DataException
    {
    	// Derby performs badly in this case if a prepared statement is used to select all service items. So, here we
    	// changed the logic to use SQLParameter instead of SQLParameterValue to form a SQL statement instead of a SQL
    	// prepared statment.
        SQLParameterIfc qualifier = new SQLParameter(ALIAS_ITEM + "." + FIELD_ITEM_TYPE_CODE + "='" + ARTSDatabaseIfc.ITEM_TYPE_SERVICE + "'");

        return(selectPLUItem(
                    dataConnection,
                    qualifier.toList(),
                    pluRequestor, locale));
    }
}
