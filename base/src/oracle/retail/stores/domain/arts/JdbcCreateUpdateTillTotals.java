/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCreateUpdateTillTotals.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:53 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:30:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:36:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   May 12 2002 20:19:04   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
    This operation performs attempts to update till totals.  If no record
    is found on the update, an insert is performed. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class JdbcCreateUpdateTillTotals
extends JdbcSaveTill
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcCreateUpdateTillTotals.class);

    //---------------------------------------------------------------------
    /**
       Class constructor.
    **/
    //---------------------------------------------------------------------
    public JdbcCreateUpdateTillTotals()
    {
        super();
        setName("JdbcCreateUpdateTillTotals");
    }

    //---------------------------------------------------------------------
    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcCreateUpdateTillTotals.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        ARTSTill artsTill = (ARTSTill) action.getDataObject();

        createUpdateTillTotals(connection,
                               artsTill.getPosTill(),
                               artsTill.getRegister());

        if (logger.isDebugEnabled()) logger.debug( "JdbcCreateUpdateTillTotals.execute()");
    }

    //---------------------------------------------------------------------
    /**
       Updates the status of a till.  If this fails, an insert is performed.
       @param  dataConnection  connection to the db
       @param  till            the till information to save
       @param  register        the register associated with the till
       @return return code (true if operation succeeds, false otherwise)
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public boolean createUpdateTillTotals(JdbcDataConnection dataConnection,
                                    TillIfc till,
                                    RegisterIfc register)
    throws DataException
    {
        boolean returnCode = updateTillHistory(dataConnection, till, register);
        if (returnCode == false)
        {
            returnCode = insertTillHistory(dataConnection, till, register);
        }

        return(returnCode);
    }
}
