package max.retail.stores.pos.services.printing;

import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.TenderableTransactionCargoIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;
import oracle.retail.stores.pos.services.printing.PrintingLaunchShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

import org.apache.log4j.Logger;

public class MAXPrintingLaunchShuttle extends PrintingLaunchShuttle
{

    static final long serialVersionUID = 0L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.printing.MAXPrintingLaunchShuttle.class);
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 5$";

    protected SaleCargoIfc saleCargo = null;    


    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {      
    	
    	super.load(bus);
       // begin load()
        cargo = (TenderableTransactionCargoIfc) bus.getCargo();
        if (bus.getCargo() instanceof SaleCargoIfc) 
        {
        	saleCargo = (SaleCargoIfc)bus.getCargo();
        }
    }                                   // end load()


    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
    	super.unload(bus);
        PrintingCargo printingCargo = (PrintingCargo) bus.getCargo();
        TenderableTransactionIfc transaction = cargo.getTenderableTransaction();
    
        
        
        if(saleCargo.getTransaction()!=null && saleCargo.getTransaction() instanceof MAXSaleReturnTransaction){
        	MAXSaleReturnTransaction returnTransaction=(MAXSaleReturnTransaction)saleCargo.getTransaction();
        if(returnTransaction.getMAXTICCustomer()!=null && returnTransaction.getMAXTICCustomer() instanceof MAXTICCustomer){
        	((MAXSaleReturnTransaction) transaction).setTicCustomer((MAXTICCustomer)returnTransaction.getMAXTICCustomer());
        }
        }
        
        
        printingCargo.setTransaction(transaction);

    }
                                

}                                     
