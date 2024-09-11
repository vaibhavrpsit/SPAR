/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale.validate;

import org.apache.log4j.Logger;

import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

//--------------------------------------------------------------------------
/**
    Transfer necessary data from the Sale service to the Customer service.
    <p>
    $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8503248783524557335L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.validate.MAXCustomerLaunchShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    protected SaleCargoIfc saleCargo = null;


    //----------------------------------------------------------------------
    /**
       ##COMMENT-LOAD##
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        saleCargo= (SaleCargoIfc)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       ##COMMENT-UNLOAD##
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        MAXCustomerMainCargo cargo = (MAXCustomerMainCargo)bus.getCargo();
        String transactionID = null;
        EmployeeIfc operador = saleCargo.getOperator();

        if (saleCargo.getTransaction() != null)
        {
            transactionID = saleCargo.getTransaction().getTransactionID();
            if (saleCargo.getTransaction().getCustomer() != null)
            {
                cargo.setCustomerLink(true);
                cargo.setOriginalCustomer(saleCargo.getTransaction().getCustomer());
                cargo.setCustomer(null);

            }
        }
        else
        {
        	// Changes starts for code merging(commenting below line)
          //CustomerUtilities.journalCustomerEnter(operador.getEmployeeID(), transactionID);
        	CustomerUtilities.journalCustomerEnter(bus,operador.getEmployeeID(), transactionID);
        	// Changes ends for code merging
        }

        cargo.setRegister(saleCargo.getRegister());
        cargo.setTransactionID(transactionID);
        cargo.setEmployee(operador);
        cargo.setOperator(operador);
        cargo.setOfflineIndicator(CustomerCargo.OFFLINE_LINK);
        cargo.setOfflineExit(false);
        cargo.setTICCustomerLookup(true);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  getClass().getName() (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
