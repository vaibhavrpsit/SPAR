/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTillTotals.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:19 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:13    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:46     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
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
 *    Rev 1.0   Aug 29 2003 15:32:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:38:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:48   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:46   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:00:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
    This operation reads the workstation information.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public class JdbcReadTillTotals extends JdbcReadTill
                                implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 343096237637444757L;
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadTillTotals.class);

    /**
       Class constructor.
     */
    public JdbcReadTillTotals()
    {
        super();
        setName("JdbcReadTillTotals");
    }

    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadTillTotals.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSTill artsTill = (ARTSTill)action.getDataObject();
        TillIfc[] tills = readTillTotals(connection,
                                         artsTill.getStoreID(),
                                         artsTill.getTillID(),
                                         artsTill.getBusinessDate());

        /*
         * Send back the result
         */
        dataTransaction.setResult(tills);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadTillTotals.execute()");
    }

    /**
       Returns a list of reconciled tills for the given till ID and
       business day.
       <P>
       @param  dataConnection  connection to the db
       @param  storeID         The store ID
       @param  tillID          The till ID
       @param  businessDate    The business date
       @return list of reconciled tills
       @exception DataException upon error
     */
    public TillIfc[] readTillTotals(JdbcDataConnection dataConnection,
                                    String storeID,
                                    String tillID,
                                    EYSDate businessDate)
        throws DataException
    {
        Vector tillVector = selectTillHistory(dataConnection, storeID, tillID, businessDate);

        TillIfc[] tills = new TillIfc[tillVector.size()];
        tillVector.copyInto(tills);

        return(tills);
    }
}

