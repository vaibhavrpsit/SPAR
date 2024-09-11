/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcAddRegisterTotals.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:06 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:52 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:17    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:35     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:35     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:52     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:30:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:35:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:20   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:57:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation modifies the Workstation History table
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcAddRegisterTotals extends JdbcSaveRegister implements ARTSDatabaseIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcAddRegisterTotals.class);

    /**
       Class constructor.
     */
    public JdbcAddRegisterTotals()
    {
        super();
        setName("JdbcAddRegisterTotals");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcAddRegisterTotals.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be updated
        // in the database.
        RegisterIfc register = (RegisterIfc)action.getDataObject();

        addRegisterTotals(connection, register);

        if (logger.isDebugEnabled()) logger.debug( "JdbcAddRegisterTotals.execute()");
    }

    /**
       Adds the financial totals to the workstation history.
       <p>
       @param  dataConnection      The connection to the data source
       @param  register            The register information to add
       @exception DataException upon error
     */
    public void addRegisterTotals(JdbcDataConnection dataConnection, RegisterIfc register)
        throws DataException
    {
        try
        {
            addWorkstationHistory(dataConnection, register);
        }
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                /*
                 * If the record is not in the database, insert it, but log a
                 * message, because something's not right.  The first time
                 * this showed up was because the database was getting reset,
                 * but hard totals was not getting reset.
                 */
                logger.warn(
                            "JdbcAddRegisterTotals: attempted to add totals to a non-existent record.");
                insertWorkstationHistory(dataConnection, register);
            }
            else
            {
                throw de;
            }
        }
    }
}
