/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcExecuteSQL.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:07 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:54 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:10    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:37     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:54     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:30:34   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Feb 15 2003 17:32:26   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   02 Jul 2002 08:28:06   jgs
 * Initial revision.
 * Resolution for 92: Move code developed at for KB into the Product.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs inserts and updates into the employee table.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcExecuteSQL extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5285043402367199672L;
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcExecuteSQL.class);

    public void execute(DataTransactionIfc dataTransaction,
            DataConnectionIfc dataConnection,
            DataActionIfc action) throws DataException
    {
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        try
        {
            String sqlStatement = (String) action.getDataObject();
            connection.execute(sqlStatement);
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Executing user defined SQL.", e);
        }
    }
}
