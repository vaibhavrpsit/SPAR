/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveAssociateProductivity.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    mpbarnet  08/26/09 - In execute(), check if a sales associate is
 *                         available before updating sales associate
 *                         productivity table.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:21 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:25:49    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   06 Jun 2002 11:41:10   jbp
 * changes to check update count
 * Resolution for POS SCR-15: Sales associate activity report performs inadequately, crashes
 *
 *    Rev 1.0   Jun 03 2002 16:39:22   msg
 * Initial revision.
 *
 *    Rev 1.3   23 May 2002 09:56:16   sfl
 * Switched the order to do update and insert on the
 * Sales Associate Productivity table to resolve the
 * rollback problem caused by Postgresl driver.
 * Resolution for POS SCR-1623: Feature_Dirty_Data_POS
 *
 *    Rev 1.2   20 May 2002 18:11:30   jbp
 * removed unnecessary logging
 * Resolution for POS SCR-1668: Return - Cannot retrieve any receipts to do a return
 *
 *    Rev 1.1   May 12 2002 23:17:46   mhr
 * quoting db2 fix
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.0   11 Apr 2002 18:06:38   jbp
 * Initial revision.
 * Resolution for POS SCR-15: Sales associate activity report performs inadequately, crashes
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.ArrayList;
import java.util.Iterator;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads the associate productivity table.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcSaveAssociateProductivity extends JdbcSaveRetailTransaction implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -9005803469533992724L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveAssociateProductivity.class);

    /**
     * revision number supplied by VM
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected boolean isUpdateStatement = false;

    /**
     * Class constructor.
     */
    public JdbcSaveAssociateProductivity()
    {
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
    	
        boolean salesAssocAvailable;    // true if sales associate is available on transaction
        EmployeeIfc employee;           // sales associate from lineitem
        
    	
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveAssociateProductivity.execute()");
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // get array list from dataAction
        ARTSAssociateProductivity ap =
            (ARTSAssociateProductivity)action.getDataObject();
        ArrayList lineItems = ap.getLineItems();
        Iterator lineItemsIter = lineItems.iterator();
        String transactionEmpID = ap.getTransactionSalesAssociateID();

        while (lineItemsIter.hasNext())
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItemsIter.next();
            
            // get the employee from the lineitem
            employee = lineItem.getSalesAssociate();
            
            // check if a sales associate is available
            salesAssocAvailable =
              isSaleAssociateAvailable(employee, transactionEmpID);
            
            // if there is a sales associate available on the transaction
            if (salesAssocAvailable == true)
            {
              // save to the sales associate productivity table
              try
              {
                saveAssociateProductivity(connection,lineItem,ap);
              }
              // catch data exception
              catch (DataException de)
              {
                logger.error(de);
                throw new DataException(de.getErrorCode(),
                                        "JdbcSaveAssociateProductivity",
                                        de);
              }
            }
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveAssociateProductivity.execute()");
    }

    /**
        Saves the associate productivity
        <P>
        @param  dataConnection  Data Source
        @param  SaleReturnLineItemIfc        The sales/return line item
        @param  ARTSAssociateProductivity ap
        @exception DataException
     */
    public void saveAssociateProductivity(JdbcDataConnection dataConnection,
                                       SaleReturnLineItemIfc lineItem,
                                       ARTSAssociateProductivity ap)
                                       throws DataException
    {
        try
        {
            updateAssociateProductivity(dataConnection, lineItem, ap);
            if(dataConnection.getUpdateCount() == 0)
            {
                insertAssociateProductivity(dataConnection, lineItem, ap);
            }
        }
        catch(DataException de)
        {
            throw new DataException(DataException.UNKNOWN, "saveAssociateProductivity", de);
        }
    }

    /**
        Checks if sales associate is available on the transaction
        <P>
        @param  employee
        @param  transactionEmpID
        @return boolean - true if associate is available, false otherwise
     */
    protected boolean isSaleAssociateAvailable(EmployeeIfc employee,String transactionEmpID)
    {
    	
    	boolean rc;						// return code
    	
    	// Return true if an associate is available either from the lineitem employee ID
    	// or the transaction employee ID from the associate productivity object
    	
    	rc = ((employee != null &&
        	   employee.getEmployeeID() != null &&
        	   employee.getEmployeeID().equals("") == false) ||
        	   (transactionEmpID.equals("") == false));
    	
    	
    	return rc;
    		
	}
    
    /**
        Inserts associate productivity for individual lineitem.
        <p>
        @param  dataConnection          Data source connection to use
        @param  SaleReturnLineItem      The lineitem to update table with
        @param  ARTSAssociateProductivity ap
        @exception DataException upon error
     */
    public void insertAssociateProductivity(JdbcDataConnection dataConnection,
                                      SaleReturnLineItemIfc lineItem,
                                      ARTSAssociateProductivity ap)
                                      throws DataException
    {
        isUpdateStatement = false;
        //Get all necessary data
        SQLInsertStatement sql = new SQLInsertStatement();
        // Table
        sql.setTable(TABLE_SALES_ASSOCIATE_PRODUCTIVITY);
        // Fields, all fields are not in place in database
        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(ap.getStoreID()));
        sql.addColumn(FIELD_WORKSTATION_ID, makeSafeString(ap.getWorkstationID()));

        // find proper sales associate
        String salesAssoc = null;
        if (lineItem.getSalesAssociate() != null &&
           lineItem.getSalesAssociate().getEmployeeID() != null &&
           !lineItem.getSalesAssociate().getEmployeeID().equals(""))
        {
            salesAssoc = lineItem.getSalesAssociate().getEmployeeID();
        }
        else
        {
            salesAssoc = ap.getTransactionSalesAssociateID();
        }
        sql.addColumn(FIELD_EMPLOYEE_ID, makeSafeString(salesAssoc));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(ap.getBusinessDate()));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_NET_SALES_TOTAL_AMOUNT,
              getNetSalesTotalAmount(lineItem));  // NetSalesTotalAmount
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertAssociateProductivity", e);
        }
     }
         /**
        Updates associate productivity for individual lineitem.
        <p>
        @param  dataConnection          Data source connection to use
        @param  SaleReturnLineItem      The lineitem to update table with
        @param  ARTSAssociateProductivity ap
        @exception DataException upon error
     */
    public void updateAssociateProductivity(JdbcDataConnection dataConnection,
                                            SaleReturnLineItemIfc lineItem,
                                            ARTSAssociateProductivity ap)
                                            throws DataException
    {
        //Get all necessary data
        SQLUpdateStatement  sql = new SQLUpdateStatement();
        isUpdateStatement = true;
        // Table
        sql.setTable(TABLE_SALES_ASSOCIATE_PRODUCTIVITY);
        // Fields, all fields are not in place in database
        sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(ap.getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID, makeSafeString(ap.getWorkstationID()));

        // find proper sales associate
        String salesAssoc = null;
        if (lineItem.getSalesAssociate() != null &&
           lineItem.getSalesAssociate().getEmployeeID() != null &&
           !lineItem.getSalesAssociate().getEmployeeID().equals(""))
        {
            salesAssoc = lineItem.getSalesAssociate().getEmployeeID();
        }
        else
        {
            salesAssoc = ap.getTransactionSalesAssociateID();
        }
        sql.addQualifier(FIELD_EMPLOYEE_ID, makeSafeString(salesAssoc));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(ap.getBusinessDate()));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_NET_SALES_TOTAL_AMOUNT,
              getNetSalesTotalAmount(lineItem)); //NetSalesTotalAmount
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateAssociateProductivity", e);
        }
     }

         /**
        Returns the NetSalesTotalAmount for the line item
        <p>
        @param  lineItem    The line item
        @return String NetSalesTotalAmount
     */
    protected String getNetSalesTotalAmount(AbstractTransactionLineItemIfc lineItem)
    {
        String value = null;
        if (lineItem != null && lineItem instanceof SaleReturnLineItemIfc)
        {
            value = ((SaleReturnLineItemIfc)lineItem).getExtendedDiscountedSellingPrice().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_WORKSTATION_TIME_PERIOD_NET_SALES_TOTAL_AMOUNT
                    + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
       Returns a string representation of this object.
       @param none
       @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  JdbcSaveAssociateProductivity (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }

    /**
       Returns the revision number of the class.
       @param none
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
