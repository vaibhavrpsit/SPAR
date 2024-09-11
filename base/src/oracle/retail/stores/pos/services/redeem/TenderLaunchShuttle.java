/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/TenderLaunchShuttle.java /rgbustores_13.4x_generic_branch/2 2011/05/23 11:58:28 rrkohli Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   05/06/11 - pos ui quickwin
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    Copies the information needed by the Tender service
    from the cargo in the Redeem service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//--------------------------------------------------------------------------
public class TenderLaunchShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.postvoid.TenderLaunchShuttle.class);
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";
    /**
       transaction
    **/
    protected RetailTransactionADOIfc transactionADO;
    /**
       redeem cargo
    **/
    protected RedeemCargo redeemCargo;
    /**
       Employee
    **/
    protected EmployeeIfc employee = null;
    
    

    //----------------------------------------------------------------------
    /**
       Get data from redeem cargo to shuttle
       <P>
       @param  bus     Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load the financial cargo
        super.load(bus);
        // load the redeem cargo
        redeemCargo = (RedeemCargo) bus.getCargo();
        transactionADO = (RetailTransactionADOIfc) redeemCargo.getCurrentTransactionADO();
        employee = redeemCargo.getEmployee();
    }

    //----------------------------------------------------------------------
    /**
       Get data from shuttle to tender cargo.
       <P>
       @param  bus     Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload the financial cargo
        super.unload(bus);
        // unload the tender cargo
        TenderCargo tenderCargo = (TenderCargo) bus.getCargo();
        // TODO: do we need both of these???
        tenderCargo.setTransaction((TenderableTransactionIfc)((ADO)transactionADO).toLegacy());
        tenderCargo.setCurrentTransactionADO(transactionADO);
        tenderCargo.setEmployee(employee);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  TenderLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}
