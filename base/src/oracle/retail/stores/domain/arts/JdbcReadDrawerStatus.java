/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadDrawerStatus.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
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
 * 4    360Commerce 1.3         1/25/2006 4:11:15 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse   
 *:
 * 4    .v700     1.2.1.0     11/16/2005 16:28:12    Jason L. DeLeau 4215: Get
 *      rid of redundant ArtsDatabaseifc class
 * 3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 *
 *Revision 1.5  2004/02/17 17:57:35  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.4  2004/02/17 16:18:44  rhafernik
 *@scr 0 log4j conversion
 *
 *Revision 1.3  2004/02/12 17:13:17  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 23:25:21  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:31:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:34   msg
 * Initial revision.
 *
 *    Rev 1.1   06 Dec 2001 16:27:04   adc
 * Removed the try  and catch block from the execute method
 * Resolution for Backoffice SCR-20: Till Pickup/Loan
 *
 *    Rev 1.0   05 Dec 2001 08:47:52   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs inserts/updates drawer status table
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $;
 */
public class JdbcReadDrawerStatus extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 8290678964695044193L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadDrawerStatus.class);

    /**
     * Class constructor.
     */
    public JdbcReadDrawerStatus()
    {
        super();
        setName("JdbcReadDrawerStatus");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadDrawerStatus.execute() starts");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        RegisterIfc register = (RegisterIfc)action.getDataObject();
        readDrawerStatus(connection, register);
        dataTransaction.setResult(register);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadDrawerStatus.execute()");
    }


    /**
        Inserts the financial tender totals into the store tender history
        table.
        <P>
        @param  dataConnection  connection to the db
        @param  safe            The safe information
        @param  tenderType      The type of tender
        @exception DataException thrown when an error occurs.
     */
    public void readDrawerStatus(JdbcDataConnection dataConnection,
                                 RegisterIfc register)
    throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.setTable(TABLE_WORKSTATION_DRAWER);

        sql.addColumn(FIELD_WORKSTATION_DRAWER_ID);
        sql.addColumn(FIELD_WORKSTATION_DRAWER_STATUS);
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID);

        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + makeSafeString(register.getWorkstation().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + makeSafeString(register.getWorkstation().getWorkstationID()));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                DrawerIfc drawer = DomainGateway.getFactory().getDrawerInstance();

                String drawerID  = getSafeString(rs, ++index);
                int drawerStatus = rs.getInt(++index);
                String tillID    = getSafeString(rs, ++index);

                drawer.setDrawerID(drawerID);
                drawer.setDrawerStatus(drawerStatus, tillID);
                register.addDrawer(drawer);
            }
        }
        catch (SQLException se)
        {
            logger.error(
            se.toString());
            throw new DataException(DataException.SQL_ERROR, "Read Drawer Status", se);
        }
        catch (DataException de)
        {
            logger.error(
            de.toString());
            throw new DataException(DataException.SQL_ERROR, "Read Drawer Status", de);
        }

    }

}
