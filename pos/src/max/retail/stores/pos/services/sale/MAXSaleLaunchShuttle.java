/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/SaleLaunchShuttle.java /main/16 2014/02/26 14:19:58 vbongu Exp $
 * ===========================================================================
 * Rev 1.0  Aug 23,2016		Ashish Yadav	Changes for code merging
 * Initial revision.
 * ===========================================================================
 */
package max.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.sale.SaleLaunchShuttle;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    within the Sale service and its subservices. <p>
    @version $Revision: /main/16 $
**/
//--------------------------------------------------------------------------
public class MAXSaleLaunchShuttle extends SaleLaunchShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
	// Changes starts for rev 1.0
    //protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.SaleLaunchShuttle.class);
	protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.MAXSaleLaunchShuttle.class);
	// Changes ends for rev 1.0
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/16 $";

    protected SaleCargoIfc saleCargo;
    //----------------------------------------------------------------------
    /**
       Loads cargo from modifyitem service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);

        // retrieve cargo from the parent
        saleCargo = (SaleCargoIfc)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Loads data into alterations service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);

        // retrieve cargo from the child
		// Changes starts for rev 1.0
        //SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
		 MAXSaleCargoIfc cargo = (MAXSaleCargoIfc)bus.getCargo();
		// Changes ends for rev 1.0
        cargo.setAccessFunctionID(saleCargo.getAccessFunctionID());
        cargo.setPasswordRequired(saleCargo.isPasswordRequired());
        cargo.setCustomerInfo(saleCargo.getCustomerInfo());
        cargo.setEmployee(saleCargo.getEmployee());
        cargo.setLineItem(saleCargo.getLineItem());
        cargo.setOperator(saleCargo.getOperator());
        cargo.setPLUItem(saleCargo.getPLUItem());
        cargo.setIndices(saleCargo.getIndices());
        cargo.setSalesAssociate(saleCargo.getEmployee());
        cargo.setTransaction(saleCargo.getTransaction());
        cargo.setCanSkipCustomerPrompt(saleCargo.getCanSkipCustomerPrompt());
        cargo.setCreditReferralBeanModel(saleCargo.getCreditReferralBeanModel());
		// Changes starts for rev 1.0
		cargo.setGiftCardApproved(((MAXSaleCargo) saleCargo).isGiftCardApproved());
		// Changes ends for rev 1.0
		//Changes starts for rev 1.0
		// cargo.setPLUItems(((MAXSaleCargo) saleCargo).getPLUItems());
		// System.out.println("108 Launch:"+((MAXSaleCargo) saleCargo).getPLUItems().getliqcat());


        
        if(saleCargo.getTransaction()!=null && saleCargo.getTransaction() instanceof MAXSaleReturnTransaction){
        	MAXSaleReturnTransaction returnTransaction=(MAXSaleReturnTransaction)saleCargo.getTransaction();
        if(returnTransaction.getMAXTICCustomer()!=null && returnTransaction.getMAXTICCustomer() instanceof MAXTICCustomer){
        	cargo.setTicCustomer((MAXTICCustomer)returnTransaction.getMAXTICCustomer());
        }
        }

        
    	//Changes ends for Rev 1.0
        SaleReturnTransactionIfc[] originalTxns = saleCargo.getOriginalReturnTransactions();
        if (originalTxns != null)
        {
            for (int i = 0;  i < originalTxns.length; i++)
            {
                cargo.addOriginalReturnTransaction(originalTxns[i]);
            }
        }

        // Record the timeout status in the cargo, so called services will know
        if(cargo instanceof TimedCargoIfc && saleCargo instanceof TimedCargoIfc)
        {
            ((TimedCargoIfc)cargo).setTimeout(((TimedCargoIfc)saleCargo).isTimeout());
        }
        
        cargo.setSuppressGiftCardActivation(saleCargo.isSuppressGiftCardActivation());

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.  <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  AlterationsLaunchShuttle (Revision " +
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

}                                       // end class AlterationsLaunchShuttle
