/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateTillTotals.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:28 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:45    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:54     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:41:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:50:18   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:46   msg
 * Initial revision.
 *
 *    Rev 1.1   29 Jan 2002 09:51:08   epd
 * Deprecated all methods using accumulate and added new methods without accum parameter (default behavior is to accumulate totals)
 * Resolution for POS SCR-770: Remove the accumulate parameter and all references to it.
 *
 *    Rev 1.0   Sep 20 2001 15:56:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// foundation imports
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs updates in the till history table.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcUpdateTillTotals extends JdbcSaveTill implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -2951808264269409207L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateTillTotals.class);

    /**
     * Class constructor.
     */
    public JdbcUpdateTillTotals()
    {
        super();
        setName("JdbcUpdateTillTotals");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcUpdateTillTotals.execute()");

        // getUpdateCount() is about the only thing outside of DataConnectionIfc
        // that we need.
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be updated
        // in the database.
        ARTSTill artsTill = (ARTSTill) action.getDataObject();
        updateTillTotals(connection, artsTill.getPosTill(), artsTill.getRegister());

        if (logger.isDebugEnabled())
            logger.debug("JdbcUpdateTillTotals.execute()");
    }

    /**
     * Updates the financial totals of a till.
     * 
     * @param dataConnection connection to the db
     * @param till The till information
     * @param register the register associated with the till
     * @exception DataException upon error
     */
    public void updateTillTotals(JdbcDataConnection dataConnection, TillIfc till, RegisterIfc register)
            throws DataException
    {
        /*
         * This is rather simplified at the moment, The update will only return
         * false is there is no record
         */
        if (!updateTillHistory(dataConnection, till, register))
        {
            insertTillHistory(dataConnection, till, register);
        }
    }
}
