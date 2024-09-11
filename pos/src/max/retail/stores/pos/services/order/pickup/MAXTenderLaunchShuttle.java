/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  29/May/2013	Veeresh Singh, Initial Draft: Food Totals Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.context.TourADOContext;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;
import oracle.retail.stores.pos.services.tender.TenderCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the POS service to the cargo used in the Tender service. <p>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXTenderLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{                                       // begin class TenderLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4610208674545029025L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 3$";
    /**
       transaction
    **/
    protected RetailTransactionIfc transaction = null;

    //----------------------------------------------------------------------
    /**
       Loads cargo from Pickup service. <P>
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
    {                                   // begin load()

        super.load(bus);
        PickupOrderCargo cargo = (PickupOrderCargo)bus.getCargo();
        transaction = cargo.getTransaction();
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Loads data into tender service. <P>
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
    {                                   // begin unload()

        super.unload(bus);
        TenderCargo cargo = (TenderCargo)bus.getCargo();
//        cargo.setTransaction(transaction);
        
        ////////////////////////////////////
        // Construct ADO's
        ////////////////////////////////////
        TourADOContext context = new TourADOContext(bus);
        context.setApplicationID(cargo.getAppID());
        ContextFactory.getInstance().setContext(context);

        // create a register
        StoreFactory storeFactory = StoreFactory.getInstance();
        RegisterADO registerADO = storeFactory.getRegisterADOInstance();
        registerADO.fromLegacy(cargo.getRegister());
        
        // create the store
        StoreADO storeADO = storeFactory.getStoreADOInstance();
        storeADO.fromLegacy(cargo.getStoreStatus());
        
        // put store in register
        registerADO.setStoreADO(storeADO);
        
        // Create/convert/set in cargo ADO transaction
        TransactionPrototypeEnum txnType = TransactionPrototypeEnum
                        .makeEnumFromTransactionType(transaction.getTransactionType());
        RetailTransactionADOIfc txnADO = null;
        try
        {
            txnADO = txnType.getTransactionADOInstance();
        }
        catch (ADOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ((ADO)txnADO).fromLegacy(transaction);
        cargo.setCurrentTransactionADO(txnADO);
        // Change for Rev 1.0 Start
        cargo.setTransaction(transaction);
        //Change for Rev 1.0 End
        ///////////////////////////////////
        // End ADO
        ///////////////////////////////////
        
    }                                   // end unload()

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.  <P>
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
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class TenderLaunchShuttle
