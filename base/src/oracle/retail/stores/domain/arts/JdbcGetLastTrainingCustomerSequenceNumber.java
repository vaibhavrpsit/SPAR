/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcGetLastTrainingCustomerSequenceNumber.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    06/02/10 - removed the training mode increment id dependency
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    mahising  11/12/08 - added for customer
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
 This operation gets the last training mode customer sequence number
 @deprecated as of 13.3 use non-training mode sequence instead 
 <P>
 @version$
 **/
//--------------------------------------------------------------------------
public class JdbcGetLastTrainingCustomerSequenceNumber extends JdbcReadRegister
{

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     The logger to which log messages will be sent.
     **/
    private static Logger logger = Logger
            .getLogger(oracle.retail.stores.domain.arts.JdbcGetLastTrainingCustomerSequenceNumber.class);

    //----------------------------------------------------------------------
    /**
     Class constructor.
     **/
    //----------------------------------------------------------------------
    public JdbcGetLastTrainingCustomerSequenceNumber()
    {
        super();
        setName("JdbcGetLastTrainingCustomerSequenceNumber");
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
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcGetLastTrainingCustomerSequenceNumber.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // grab arguments and call readTransactionsByID()
        Integer seqNo = getLastTrainingCustomerSequenceNumber(connection, (RegisterIfc)action.getDataObject());

        // return sequence number
        dataTransaction.setResult(seqNo);

        if (logger.isDebugEnabled())
            logger.debug("JdbcGetLastTrainingCustomerSequenceNumber.execute");
    }
}
