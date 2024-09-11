/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/InstantCreditReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/04/08 20:33:03  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;
//java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies the contents of InstantCredit cargo to
    Pos Cargo. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class InstantCreditReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4417994116638134049L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.InstantCreditReturnShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       store financial status
    **/
    protected StoreStatusIfc storeStatus;
    /**
       register financial status
    **/
    protected RegisterIfc register;
    /**
       tender limits
    **/
    protected TenderLimitsIfc tenderLimits;
    /**
       person signed on for this operation
    **/
    protected EmployeeIfc operator;
    /**
       person signed on for this operation
    **/
    protected boolean systemPos;
    /**
       identifier of last reprintable transaction
    **/
    protected String lastReprintableTransactionID = "";

     /**
        customer information object used to store
        customer postal code or phone with transaction
    **/
    protected CustomerInfoIfc customerInfo = null;
    
    protected TransactionIfc txn;

    //----------------------------------------------------------------------
    /**
       Copies information from the cargo used in the service.  <P>
       @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()

        // get cargo reference and extract attributes
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        storeStatus = cargo.getStoreStatus();
        register = cargo.getRegister();
        tenderLimits = cargo.getTenderLimits();
        operator = cargo.getOperator();
        lastReprintableTransactionID = cargo.getLastReprintableTransactionID();
        customerInfo = cargo.getCustomerInfo();
        txn = cargo.getTransaction();
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Copies information to the cargo used in the service. <P>
       @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()

        // get cargo reference and set attributes
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        cargo.setStoreStatus(storeStatus);
        cargo.setRegister(register);
        cargo.setTenderLimits(tenderLimits);
        cargo.setOperator(operator);
        cargo.setLastReprintableTransactionID(lastReprintableTransactionID);
        cargo.setCustomerInfo(customerInfo);
        cargo.setTransaction( (SaleReturnTransactionIfc) txn);
    }                                   // end unload()

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object. <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  FinancialCargoShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class InstantCreditCargoShuttle
