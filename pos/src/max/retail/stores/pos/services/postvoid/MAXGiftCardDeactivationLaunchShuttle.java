/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.1  08/08/2013     Jyoti Rawal, Changed the Gift Card Tender flow
*   Rev 1.0  22/Jul/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.postvoid;
// java imports
import org.apache.log4j.Logger;

import max.retail.stores.pos.services.tender.activation.MAXActivationCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.tender.TenderGiftCard;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.postvoid.VoidCargo;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the POS service to the cargo used in the Gift Card Activation service. <p>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXGiftCardDeactivationLaunchShuttle implements ShuttleIfc
{                                       // begin class GiftCardDeactivationLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1921176218180400799L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.postvoid.MAXGiftCardDeactivationLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 3$";
    /**
       transaction
    **/
    protected VoidTransactionIfc voidTransaction = null;
    /**
     * original transaction
     */
    protected RetailTransactionADOIfc origTrans = null;
    /**
     gift card data for activation
     */
    protected GiftCardIfc giftCard = null;
    /** 
     * redeem transaction flag 
     * **/
    protected boolean isRedeemTransaction = false;
    /**
     the financial data for the register
     **/
    protected RegisterIfc register;
    /**
     The financial data for the store
     **/
    protected StoreStatusIfc storeStatus;

    //----------------------------------------------------------------------
    /**
       Loads cargo from void service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the void transaction
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

        VoidCargo cargo = (VoidCargo) bus.getCargo();
        voidTransaction = (VoidTransactionIfc)((ADO)cargo.getCurrentTransactionADO()).toLegacy();
        
        
        origTrans = (RetailTransactionADOIfc)cargo.getOriginalTransactionADO();
        
//        if (origTrans instanceof RedeemTransactionADO)
//        {
            isRedeemTransaction = true;
            TenderableTransactionIfc rTransRDO = (TenderableTransactionIfc)((ADO)origTrans).toLegacy();
            TenderLineItemIfc[] redeemTender = rTransRDO.getTenderLineItems();//getTenderLineItemsVector();
            giftCard = DomainGateway.getFactory().getGiftCardInstance();
            for(int i = 0;i<redeemTender.length;i++){
            	if(redeemTender[i] instanceof TenderGiftCard){
            	  if (giftCard != null && ((TenderGiftCard)redeemTender[i]).getCardNumber() != null)
                    {
//                        giftCard.setCardNumber(((TenderGiftCardIfc)redeemTender).getCardNumber());
                        giftCard.setRequestType(GiftCardIfc.GIFT_CARD_REDEEM_VOID);
                        giftCard.setCardNumber(((TenderGiftCard)redeemTender[i]).getCardNumber());
                        giftCard.setRequestType(GiftCardIfc.GIFT_CARD_REDEEM_VOID);
						//Rev 1.1 changes start
                        giftCard.setCurrentBalance(((TenderGiftCard)redeemTender[i]).getGiftCard().getCurrentBalance());
                        giftCard.setInitialBalance(((TenderGiftCard)redeemTender[i]).getGiftCard().getInitialBalance());
						//Rev 1.1 changes end
                    }
            	}
//            	redeemTender[i].
            }

                    
        register = cargo.getRegister();
        storeStatus = cargo.getStoreStatus();
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Loads data into GiftCardActivation service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the void transaction
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

        //ActivationCargo cargo = (ActivationCargo) bus.getCargo();
        MAXActivationCargo cargo = new MAXActivationCargo();
        ((MAXActivationCargo) cargo).setRetailTransaction((RetailTransactionIfc) voidTransaction);
        if (isRedeemTransaction)
        {
            ((MAXActivationCargo) cargo).setGiftCard(giftCard);
        }
        cargo.setRegister(register);
        cargo.setStoreStatus(storeStatus);
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
        String strResult = new String("Class:  GiftCardDeactivationLaunchShuttle (Revision " +
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

}                                       // end class GiftCardDeactivationLaunchShuttle
