/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveOrderSummaryByOrderID.java /main/19 2014/06/17 15:26:37 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  06/16/14 - CAE Order summary enhancement phase I
 *    abondala  10/29/13 - retrieve order by order id supports case insensitive
 *                         search.
 *    yiqzhao   12/24/12 - Refactoring xc formatter, transformer and others.
 *    sgu       07/24/12 - fix defect in order summary retrieval
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:20 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:07    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:39:14   msg
 * Initial revision.
 *
 *    Rev 1.2   25 May 2002 19:18:16   vxs
 * Added UPPER to qualifier in setSelectQualifiers()
 *
 *    Rev 1.1   Mar 18 2002 22:46:16   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:22   msg
 * Initial revision.
 *
 *    Rev 1.4   Jan 23 2002 21:16:50   dfh
 * removed void exclusion qualifier
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   Jan 02 2002 15:30:20   dfh
 * updates to not retrieve voided new special orders
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   Jan 02 2002 13:56:10   dfh
 * updates to not retrieve order summaries if the status of the order is undefined or suspended-canceled
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   29 Nov 2001 07:05:32   mpm
 * Continuing order modifications.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 20 2001 15:59:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads all of the matching a specified order ID.
 * This class has been deprecated in wake of an enhancement from JDBC 
 * operation to JPA operation. In lieu of this use {@link #JpaRetrieveOrderSummary}
 * @version $Revision: /main/19 $
 * @deprecated as of 14.1
 */
public class JdbcRetrieveOrderSummaryByOrderID extends JdbcRetrieveOrderSummary implements ARTSDatabaseIfc
{
    /**
     *
     */
    private static final long serialVersionUID = -699732855102942016L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcRetrieveOrderSummaryByOrderID.class);

    /**
     * Class constructor.
     */
    public JdbcRetrieveOrderSummaryByOrderID()
    {
        super();
        setName("JdbcRetrieveOrderSummaryByOrderID");
    }

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataTransaction
     * @param dataConnection
     * @param action
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcRetrieveOrderSummaryByOrderID.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call RetrieveOrderSummaryByOrderID()
        OrderSearchCriteriaIfc orderSearchCriteria =
          (OrderSearchCriteriaIfc) action.getDataObject();
        OrderSummaryEntryIfc[] orderSumList =
          retrieveOrderSummaries(connection, orderSearchCriteria);

        dataTransaction.setResult(orderSumList);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcRetrieveOrderSummaryByOrderID.execute");
    }

    /**
     * Sets qualifiers to select SQL statement based on search key.
     *
     * @param sql SQLSelectStatement object
     * @param orderSearchKey OrderSearchKey object
     */
    protected void setSelectQualifiers(SQLSelectStatement sql, OrderSearchCriteriaIfc orderSearchCriteria)
    {
        super.setSelectQualifiers(sql, orderSearchCriteria);
        // test and add qualifiers for the orderSearchCriteria ID
        sql.addQualifier(ALIAS_ORDER + "." +FIELD_ORDER_ID_UPPER_CASE + " = UPPER('" + orderSearchCriteria.getOrderID() + "')");

        sql.addQualifier(ALIAS_ORDER + "." +FIELD_ORDER_STATUS + " != " + OrderConstantsIfc.ORDER_STATUS_UNDEFINED);

        sql.addQualifier(ALIAS_ORDER + "." +FIELD_ORDER_STATUS + " != " + OrderConstantsIfc.ORDER_STATUS_SUSPENDED_CANCELED);
    }
}
