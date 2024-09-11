/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/BuildFlatFileTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
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
 *    4    360Commerce 1.3         6/13/2006 4:12:02 PM   Brett J. Larsen CR
 *         18490 - UDM - removal of TaxAuthorityPostalCode &
 *         TaxAuthorityProvince
 *    3    360Commerce 1.2         3/31/2005 4:27:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:40 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/06/10 14:21:29  jdeleau
 *   @scr 2775 Use the new tax data for the tax flat files
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
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:29:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Sep 12 2002 17:06:06   sspiars
 * Added method to call new buildTaxRuleFlatFile operation.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.0   Jun 03 2002 16:34:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:44:54   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:04:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:57:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:35:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;


//-------------------------------------------------------------------------
/**
    This class handles the DataTransaction behavior building flat file
    representations of the PLU, Employee, and Tax Rule information.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class BuildFlatFileTransaction extends DataTransaction
{                                                                               // begin class BuildFlatFileTransaction
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.BuildFlatFileTransaction.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       The name that links this transaction to a command within DataScript.
    **/
    public static String dataCommandName="BuildFlatFileTransaction";

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public BuildFlatFileTransaction()
    {                                                                   // begin BuildFlatFileTransaction()
        super(dataCommandName);
    }                                                                   // end BuildFlatFileTransaction()

    //---------------------------------------------------------------------
    /**
       Class constructor.
       @param name data command name
    **/
    //---------------------------------------------------------------------
    public BuildFlatFileTransaction(String name)
    {                                                                   // begin BuildFlatFileTransaction()
        super(name);
    }                                                                   // end BuildFlatFileTransaction()

    //---------------------------------------------------------------------
    /**
       Build the PLU flat file.
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void buildPLUFlatFile() throws DataException
    {                                                                   // begin retrieveAlertList()
        if (logger.isDebugEnabled()) logger.debug(
                     "BuildFlatFileTransaction.buildPLUFile");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("BuildPLUFlatFile");
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "" + "BuildFlatFileTransaction.buildPLUFile" + "");
    }                                                                   // end retrieveAlertList()

    //---------------------------------------------------------------------
    /**
       Build the PLU flat file.
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void buildFlatItemFile() throws DataException
    {                                                                   // begin retrieveAlertList()
        if (logger.isDebugEnabled()) logger.debug(
                     "BuildFlatFileTransaction.buildFlatItemFile");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("BuildFlatItemFile");
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "" + "BuildFlatFileTransaction.buildFlatItemFile" + "");
    }                                                                   // end retrieveAlertList()

    //---------------------------------------------------------------------
    /**
       Build the employee flat file.
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void buildEmployeeFlatFile() throws DataException
    {                                                                   // begin retrieveAlertList()
        if (logger.isDebugEnabled()) logger.debug(
                     "BuildFlatFileTransaction.buildEmployeeFlatFile");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("BuildEmployeeFlatFile");
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "" + "BuildFlatFileTransaction.buildEmployeeFlatFile" + "");

    }

    //---------------------------------------------------------------------
    /**
       Build the Tax Rule Flat File.
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void buildNewTaxRuleFlatFile() throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "BuildFlatFileTransaction.buildNewTaxRuleFlatFile");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("BuildNewTaxRuleFlatFile");
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "" + "BuildFlatFileTransaction.buildNewTaxRuleFlatFile" + "");

    }

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
            new StringBuffer("Class: BuildFlatFileTransaction ");
        strResult.append("(Revision ").append(getRevisionNumber());
        strResult.append(") @").append(hashCode());
        return(strResult.toString());
    }
}                                                                               // end class BuildFlatFileTransaction
