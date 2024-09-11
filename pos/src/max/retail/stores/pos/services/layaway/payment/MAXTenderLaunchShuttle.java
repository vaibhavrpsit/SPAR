/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  29/May/2013	Veeresh Singh, Initial Draft: Food Totals Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.layaway.payment;

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
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.services.layaway.payment.TenderLaunchShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;

//--------------------------------------------------------------------------
/**
   Shuttle used to transfer layaway payment related data.

    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXTenderLaunchShuttle
extends TenderLaunchShuttle
implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4592805265619114794L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 3$";

    protected LayawayCargo layawayCargo = null;

    //----------------------------------------------------------------------
    /**
        Loads the shuttle data from the parent service's cargo into this shuttle.
        The data elements to transfer are determined by the interfaces that
        the parent cargo implements.  For example, none of the layawaySearchCargo
        data elements will be transferred if the calling service's cargo implements
        layawaySummaryCargoIfc.

        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        layawayCargo = (LayawayCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Unloads the shuttle data into the cargo.

        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        //cargo.setTransaction(layawayCargo.getTenderableTransaction());

//        cargo.setOperator(layawayCargo.getOperator());
//        cargo.setRegister(layawayCargo.getRegister());

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
                        .makeEnumFromTransactionType(layawayCargo.getTransaction().getTransactionType());
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
        ((ADO)txnADO).fromLegacy(layawayCargo.getTransaction());
        cargo.setCurrentTransactionADO(txnADO);
        // Changes Start Rev 1.0
        cargo.setTransaction(layawayCargo.getTransaction());
        //Changes End Rev 1.0
        ///////////////////////////////////
        // End ADO
        ///////////////////////////////////
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.  <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class: "    + getClass().getName() +
                       "(Revision " + getRevisionNumber()  +
                       ") @" + hashCode());

        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

}
