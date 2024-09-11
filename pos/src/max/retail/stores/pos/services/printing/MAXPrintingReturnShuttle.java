/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* initial draft -- by vaibhav
 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.printing;

// foundation imports
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.TenderableTransactionCargoIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
   Shuttle used to Return the printing service.

    @version $Revision: 5$
**/
//--------------------------------------------------------------------------
/**
 * @author Kumar Vaibhav
 *
 */
public class MAXPrintingReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9218330229065219843L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.printing.MAXPrintingReturnShuttle.class);
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 5$";
    /**
       Calling service's cargo
    **/
    protected TenderableTransactionCargoIfc cargo = null;
    /**
       Calling service's saleCargo
    **/
    protected SaleCargoIfc saleCargo = null;   
    
    protected PrintingCargo printCargo =null;

    //----------------------------------------------------------------------
    /**
       Loads the shuttle data from the cargo.

       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
        cargo = (PrintingCargo) bus.getCargo();
        if (bus.getCargo() instanceof PrintingCargo) 
        {
        	printCargo = (PrintingCargo)bus.getCargo();
        }
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Unloads the shuttle data into the sale cargo.

       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        SaleCargoIfc saleCargo = (SaleCargoIfc) bus.getCargo();
        TenderableTransactionIfc transaction = cargo.getTenderableTransaction();
       /* if (printCargo != null) 
        {
        	transaction.setSalesAssociate(printCargo.getSalesAssociate());
		}*/
        saleCargo.setTransaction((MAXSaleReturnTransactionIfc) transaction);
       // saleCargo.setTillID(cargo.getTillID());
        UtilityManagerIfc utility =(UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
       // saleCargo.setCodeListMap(utility.getCodeListMap());
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
        String strResult =
            new String("Class:  MAXPrintingReturnShuttle(Revision " +
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

}                                       // end class PrintingReturnShuttle
