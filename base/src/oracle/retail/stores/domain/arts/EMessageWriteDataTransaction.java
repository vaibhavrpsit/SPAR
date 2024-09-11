/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/EMessageWriteDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:48 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:30:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:45:04   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:58:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.Serializable;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

//-------------------------------------------------------------------------
/**
    This class handles the DataTransaction behavior for reading EMessages.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class EMessageWriteDataTransaction extends DataTransaction
{                                                                               // begin class EMessageWriteDataTransaction
    /** 
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.EMessageWriteDataTransaction.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       The name that links this transaction to a command within DataScript.
    **/
    public static String dataCommandName="EMessageWriteDataTransaction";

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public EMessageWriteDataTransaction()
    {                                                                   // begin EMessageWriteDataTransaction()
        super(dataCommandName);
    }                                                                   // end EMessageWriteDataTransaction()

    //---------------------------------------------------------------------
    /**
       Class constructor.
       @param name data command name
    **/
    //---------------------------------------------------------------------
    public EMessageWriteDataTransaction(String name)
    {                                                                   // begin EMessageWriteDataTransaction()
        super(name);
    }                                                                   // end EMessageWriteDataTransaction()

    //---------------------------------------------------------------------
    /**
       Sends an eMessage. <P>
       @param eMessage EMessageIfc reference
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void sendEMessage(EMessageIfc eMessage) throws DataException
    {                                                                   // begin sendEMessage()
        if (logger.isDebugEnabled()) logger.debug(
                     "EMessageWriteDataTransaction.sendEMessage");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("SendEMessage");
        da.setDataObject((Serializable) eMessage);
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "" + "EMessageWriteDataTransaction.sendEMessage" + "");

    }                                                                   // end sendEMessage()

    //---------------------------------------------------------------------
    /**
       Updates an eMessage. <P>
       @param eMessage EMessageIfc reference
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateEMessage(EMessageIfc eMessage) throws DataException
    {                                                                   // begin updateEMessage()
        if (logger.isDebugEnabled()) logger.debug(
                     "EMessageWriteDataTransaction.updateEMessage");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("UpdateEMessage");
        da.setDataObject((Serializable) eMessage);
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "" + "EMessageWriteDataTransaction.updateEMessage" + "");

    }                                                                   // end updateEMessage()

    //---------------------------------------------------------------------
    /**
       Updates an eMessage with a new status. <P>
       @param eMessage EMessageIfc reference
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateEMessageStatus(EMessageIfc eMessage) throws DataException
    {                                                                   // begin updateEMessageStatus()
        if (logger.isDebugEnabled()) logger.debug(
                     "EMessageWriteDataTransaction.updateEMessageStatus");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("UpdateEMessageStatus");
        da.setDataObject((Serializable) eMessage);
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "" + "EMessageWriteDataTransaction.updateEMessageStatus" + "");

    }                                                                   // end updateEMessageStatus()

    //---------------------------------------------------------------------
    /**
       Returns the revision number of this class.
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
       Returns the string representation of this object.
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        StringBuffer strResult =
            new StringBuffer("Class: EMessageWriteDataTransaction ");
        strResult.append("(Revision ").append(getRevisionNumber());
        strResult.append(") @").append(hashCode());
        return(strResult.toString());
    }
}                                                                               // end class EMessageWriteDataTransaction
