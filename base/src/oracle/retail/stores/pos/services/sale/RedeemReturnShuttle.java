/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/RedeemReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     12/01/10 - Modified to prevent loss possible loss of
 *                         transaction sequence ID during redeem process.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.redeem.RedeemCargo;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class RedeemReturnShuttle extends FinancialCargoShuttle
{                                       // begin class GiftOptionsRefundReturnShuttle()
    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.RedeemReturnShuttle.class);

    /**
       revision number supplied by pvcs
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        the redeem service cargo
    **/
    protected RedeemCargo redeemCargo;
    /**
     store financial status
     **/
    protected StoreStatusIfc storeStatus;
    /**
     register financial status
     **/
    protected RegisterIfc register;
    /**
     last printable transaction id which is used for reprint receipt
     **/
    protected String transactionID;
    //----------------------------------------------------------------------
    /**
     * This method loads the redeem service transaction.  This is an ADO
     * service transferring to a legacy service.
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        redeemCargo = (RedeemCargo) bus.getCargo();
        register = redeemCargo.getRegister();
        storeStatus = redeemCargo.getStoreStatus();
        transactionID = redeemCargo.getLastReprintableTransactionID();
    }

    //----------------------------------------------------------------------
      /**
         Loads data into sale service. <P>
         <B>Pre-Condition(s)</B>
         <B>Post-Condition(s)</B>
         <UL>
         <LI>
         </UL>
         @param  bus     Service Bus
      **/
      //----------------------------------------------------------------------
      public void unload(BusIfc bus)
      {
          SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
          
          cargo.setStoreStatus(storeStatus);
          cargo.setRegister(register);
          cargo.setLastReprintableTransactionID(transactionID);
      }
      
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  RedeemReturnShuttle (Revision " +
                                      revisionNumber +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

}                                       // end class RedeemReturnShuttle
