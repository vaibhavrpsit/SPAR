/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateDrawerStatus.java /main/15 2014/07/09 13:10:48 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/26/14 - Forward port fix for handling the condition of two
 *                         registers opened with same till with one or both
 *                         offline at time of open.
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
 * 4    360Commerce 1.3         1/25/2006 4:11:26 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:52 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse   
 *:
 * 4    .v700     1.2.1.0     11/16/2005 16:26:12    Jason L. DeLeau 4215: Get
 *      rid of redundant ArtsDatabaseifc class
 * 3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:22:52     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:12:05     Robert Pearse
 *
 *Revision 1.5  2004/02/17 17:57:36  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.4  2004/02/17 16:18:46  rhafernik
 *@scr 0 log4j conversion
 *
 *Revision 1.3  2004/02/12 17:13:19  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 23:25:23  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jan 06 2003 11:56:16   DCobb
 * Updated the scope of log messages to DOMAIN.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.0   Jun 03 2002 16:40:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:49:28   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:06   msg
 * Initial revision.
 *
 *    Rev 1.1   05 Dec 2001 10:07:50   epd
 * fixed bug
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   05 Dec 2001 08:47:52   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
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
 * @version $Revision: /main/15 $;
 */
public class JdbcUpdateDrawerStatus extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 1527356730643232450L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateDrawerStatus.class);

    /**
     * Class constructor.
     */
    public JdbcUpdateDrawerStatus()
    {
        super();
        setName("JdbcUpdateDrawerStatus");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateDrawerStatus.execute() starts");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        RegisterIfc register = (RegisterIfc)action.getDataObject();

        try
        {
            DrawerIfc[] drawers = register.getDrawers();
            for (int i=0; i<drawers.length; i++)
            {
                updateDrawerStatus(connection, drawers[i], register);
            }
        }
        catch (DataException de)
        {
            logger.error(de);
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateDrawerStatus.execute()");
    }

    /**
     * Inserts the financial tender totals into the store tender history table.
     * 
     * @param dataConnection connection to the db
     * @param safe The safe information
     * @param tenderType The type of tender
     * @exception DataException thrown when an error occurs.
     */
    public void updateDrawerStatus(JdbcDataConnection dataConnection,
                                   DrawerIfc drawer,
                                   RegisterIfc register)
    throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_WORKSTATION_DRAWER);
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID, makeSafeString(drawer.getOccupyingTillID()));
        sql.addColumn(FIELD_WORKSTATION_DRAWER_STATUS, drawer.getDrawerStatus());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        // Add Qualifier(s)
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + makeSafeString(register.getWorkstation().getStoreID()));
        if(register.isTillClose())
        {
            sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + makeSafeString(register.getClosingTillID()));
        }
        else
        {
            sql.addQualifier(FIELD_WORKSTATION_ID + " = " + makeSafeString(register.getWorkstation().getWorkstationID()));
        }        
        sql.addQualifier(FIELD_WORKSTATION_DRAWER_ID + " = " + makeSafeString(drawer.getDrawerID()));

        try
        {
            dataConnection.execute(sql.getSQLString());
            if (dataConnection.getUpdateCount() == 0)
            {
                insertDrawerStatus(dataConnection, drawer, register);
            }
        }
        catch (DataException de)
        {
            if (de.getErrorCodeExtended() == DataException.NO_DATA)
            {
                insertDrawerStatus(dataConnection, drawer, register);
            }
            else
            {
                throw new DataException(DataException.SQL_ERROR, "Update Drawer Status", de);
            }
        }
    }

    /**
     * Inserts the drawer status into the drawer status table table.
     * 
     * @param dataConnection connection to the db
     * @param drawer The drawer to be saved
     * @param register contains important info to be saved
     * @exception DataException thrown when an error occurs.
     */
    public void insertDrawerStatus(JdbcDataConnection dataConnection,
                                   DrawerIfc drawer,
                                   RegisterIfc register)
    throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_WORKSTATION_DRAWER);
        sql.addColumn(FIELD_WORKSTATION_DRAWER_ID, makeSafeString(drawer.getDrawerID()));
        sql.addColumn(FIELD_WORKSTATION_ID, makeSafeString(register.getWorkstation().getWorkstationID()));
        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(register.getWorkstation().getStoreID()));
        sql.addColumn(FIELD_WORKSTATION_DRAWER_STATUS, drawer.getDrawerStatus());
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID, makeSafeString(drawer.getOccupyingTillID()));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

        dataConnection.execute(sql.getSQLString());
    }
}
