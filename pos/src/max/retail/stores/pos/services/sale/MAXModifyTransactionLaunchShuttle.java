package max.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;
import oracle.retail.stores.pos.services.sale.ModifyTransactionLaunchShuttle;

public class MAXModifyTransactionLaunchShuttle extends ModifyTransactionLaunchShuttle{


    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.MAXModifyTransactionLaunchShuttle.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: 3$";

    /**
       POS cargo
    **/
    protected MAXSaleCargoIfc saleCargo = null;
    /**
       sales associate set using modify transaction sales associate
    **/ 
    protected boolean salesAssociateAlreadySet = false;
    //----------------------------------------------------------------------
    /**
       This method will clone the retail transaction from the parent cargo.
       <P>
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
        saleCargo = (MAXSaleCargoIfc)bus.getCargo();        
        salesAssociateAlreadySet = saleCargo.isAlreadySetTransactionSalesAssociate();



    }

    //----------------------------------------------------------------------
    /**
       The child cargo is passsed in here. Provide a reference for the cloned
       object from the child cargo.
       <P>
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

        RetailTransactionIfc transaction = null;
        // clone the transaction
        if (saleCargo.getTransaction() != null)
        {
            transaction = (RetailTransactionIfc)saleCargo.getTransaction();
        }

        // retrieve cargo from the child(ModifyTransaction Cargo)
        ModifyTransactionCargo cargo = (ModifyTransactionCargo) bus.getCargo();

        // set the child reference to the cloned object
        cargo.setTransaction(transaction);
        //cargo.getCustomerInfo().getPhoneNumber();
       // System.out.println("97 transaction :"+transaction);
        cargo.setSalesAssociate(saleCargo.getEmployee());
        if(saleCargo.getCustomerInfo()!=null)
        {
        	cargo.setCustomerInfo(saleCargo.getCustomerInfo());
        }
        if (salesAssociateAlreadySet)
        {
            cargo.setAlreadySetTransactionSalesAssociate(true);
        }

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
        String strResult = new String("Class:  ModifyTransactionLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
       //System.out.println("118 Modifytransaction :"+strResult);
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
    	//System.out.println(revisionNumber);
        return(revisionNumber);
    }
}
