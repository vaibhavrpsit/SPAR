/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/SaveTaxMaintenanceTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.io.File;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.taximport.TaxImportResults;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
    Handles the data transactions for Tax Maintenance. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @deprecated as of 13.3. Tax import is done through DIMP.
 */
//-------------------------------------------------------------------------
public class SaveTaxMaintenanceTransaction extends DataTransaction
{
    /**  revision number of this class  **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** transaction name  **/
    public static final String SAVE_TAX_MAINTENANCE = "SaveTaxMaintenanceTransaction";

    /** The logger to which log messages will be sent  **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.SaveTaxMaintenanceTransaction.class);
    
    //---------------------------------------------------------------------
    /**
        Class constructor.
    **/
    //---------------------------------------------------------------------
    public SaveTaxMaintenanceTransaction()
    {
        super(SAVE_TAX_MAINTENANCE);
    }

    //---------------------------------------------------------------------
    /**
        Puts an XML File into the DataAction for the SaveTaxMaintenance
        DataOperation. <P>
        @param xmlFile the xml file as a String
        @exception DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public TaxImportResults saveTaxMaintenance(File taxFile) throws DataException
    {
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];

        DataAction da = new DataAction();
        da.setDataOperationName("SaveTaxMaintenance");
        da.setDataObject(taxFile);
        dataActions[0] = da;
        setDataActions(dataActions);

        String msg = "SaveTaxMaintenanceTransaction: tax file saved to DataAction";
        logger.info(msg);

        // execute data request
        getDataManager().execute(this);
               
        // Retrieve, log, and return results
        TaxImportResults results = (TaxImportResults)getResult();
        logger.info(getResultString(results));
        return results;
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
        return(Util.classToStringHeader("SaveTaxMaintenanceTransaction",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }

    //---------------------------------------------------------------------
    /**
       Create a string displaying the results of a tax import
       @param TaxImportResults object containing the results of a tax import
     * @return String containing the import results.
    **/
    //---------------------------------------------------------------------
    public static String getResultString(TaxImportResults results)
    {

        String sep  = "---------------------------------------------------\n";
        StringBuffer msg1 = 
            new StringBuffer("Tax Authorities Inserted:            ").append(results.tax_authority_inserts).append("\n");
        StringBuffer msg2 = 
            new StringBuffer("Tax Authorities Updated:             ").append(results.tax_authority_updates).append("\n");
        StringBuffer msg3 = 
            new StringBuffer("Postal Code Associations Inserted:   ").append(results.postal_association_inserts).append("\n");
        StringBuffer msg4 = 
            new StringBuffer("Tax Groups Inserted:                 ").append(results.tax_group_inserts).append("\n");
        StringBuffer msg5 = 
            new StringBuffer("Tax Rules Inserted/Updated:          ").append(results.tax_rule_inserts).append("\n");
        StringBuffer msg6 = 
            new StringBuffer("Tax Rules Skipped (errors):          ").append(results.tax_rule_errors).append("\n");

        StringBuffer msg = new StringBuffer(sep);
        msg.append(msg1)
           .append(msg2)
           .append(msg3)
           .append(msg4)
           .append(msg5)
           .append(msg6)
           .append(sep);               
        
        return msg.toString();
    }    
}

