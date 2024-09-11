/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadStoreTills.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:18 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:19    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
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
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Dec 20 2002 11:08:02   DCobb
 * Initial revision.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads all the tills which are opened in a store for a
 * particular business day.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadStoreTills extends JdbcReadTill implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 8606251877156733275L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadStoreTills.class);

    /**
     * Class constructor.
     */
    public JdbcReadStoreTills()
    {
        super();
        setName("JdbcReadStoreTills");
    }

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadStoreTills.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        ARTSStore store = (ARTSStore)action.getDataObject();
        Vector tillVector = readStoreTills(connection, store);

        TillIfc[] tills = new TillIfc[tillVector.size()];
        tillVector.copyInto(tills);
        dataTransaction.setResult(tills);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadStoreTills.execute()");
    }

    /**
       Returns the tills for a store's business day.
       <P>
       @param  dataConnection  connection to the db
       @param  store           the store and business day
       @return vector of registers
       @exception DataException upon error
     */
    public Vector readStoreTills(JdbcDataConnection dataConnection,
                                 ARTSStore store) throws DataException
    {
        Vector tillVector = selectStoreTills(dataConnection, store);

        return(tillVector);
    }
}
