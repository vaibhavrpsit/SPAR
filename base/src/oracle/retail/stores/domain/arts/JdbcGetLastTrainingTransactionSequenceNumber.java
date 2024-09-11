/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcGetLastTrainingTransactionSequenceNumber.java /main/12 2012/12/07 12:21:56 jswan Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:54 PM  Robert Pearse   
 *
 *Revision 1.1  2004/08/16 21:14:50  lzhao
 *@scr 6654: remove the relationship for training mode sequence number from real training mode sequence number.
 *
 *Revision: 3$ Aug 4, 2004 lzhao
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//--------------------------------------------------------------------------
/**
 This operation gets the last training mode transaction sequence number

 <P>
 @version$
 @deprecated in version 14.0; this should have been deprecated in 13.1.  There are no callers.
 **/
//--------------------------------------------------------------------------
public class JdbcGetLastTrainingTransactionSequenceNumber extends JdbcReadTransaction
{
    /** 
     The logger to which log messages will be sent.
     **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcGetLastTrainingTransactionSequenceNumber.class);

    //----------------------------------------------------------------------
    /**
     Class constructor.
     **/
    //----------------------------------------------------------------------
    public JdbcGetLastTrainingTransactionSequenceNumber()
    {
        super();
        setName("JdbcGetLastTrainingTransactionSequenceNumber");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcGetLastTrainingTransactionSequenceNumber.execute");

        // This class is no longer used and the getLastTrainingTransactionSequenceNumber() method has been removed from JdbcReadTransaction

        // set data connection
        //JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call readTransactionsByID()
        // Integer seqNo = getLastTrainingTransactionSequenceNumber(connection, (RegisterIfc)action.getDataObject());

        // return sequence number
        // dataTransaction.setResult(seqNo);

        if (logger.isDebugEnabled()) logger.debug( "JdbcGetLastTrainingTransactionSequenceNumber.execute");
    }
}
