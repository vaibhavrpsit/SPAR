/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/GiftCardRedeemLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
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
 |   5    360Commerce 1.4         5/5/2008 5:12:08 PM    Alan N. Sinton  CR
 |        30689: Added register ID and transaction ID to the request and log
 |        it.  Code reviewed by Mathews Kochummen.
 |   4    360Commerce 1.3         4/27/2008 5:28:27 PM   Charles D. Baker CR
 |        31482 - Updated the journalResponse method of GetResponseSite to
 |        intelligently journal entries with the appropriate journal type
 |        (Trans or Not Trans). Code Review by Tony Zgarba.
 |   3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse   
 |   2    360Commerce 1.1         3/10/2005 10:21:55 AM  Robert Pearse   
 |   1    360Commerce 1.0         2/11/2005 12:11:14 PM  Robert Pearse   
 |  $
 |  Revision 1.5  2004/09/23 00:07:16  kmcbride
 |  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 |
 |  Revision 1.4  2004/04/22 17:31:57  lzhao
 |  @scr 3872: code review, remove toString()
 |
 |  Revision 1.3  2004/04/13 19:02:22  lzhao
 |  @scr 3872: gift card redeem.
 |
 |  Revision 1.2  2004/04/08 20:33:03  cdb
 |  @scr 4206 Cleaned up class headers for logs and revisions.
 |
 |  Revision 1.1  2004/03/31 16:17:23  lzhao
 |  @scr 3872: gift card redeem service update
 |
 |  $Revision: /rgbustores_13.4x_generic_branch/1 $
 |  Mar 29, 2004 lzhao
 | 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
  * This shuttle copies information from the cargo used
  * in the redeem service to the cargo used in the gift card redeem service. <p>
  * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GiftCardRedeemLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -361085216249754669L;

    /**
     revision number supplied by source-code-control system
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * gift card data for redeem
     */
    protected GiftCardIfc giftCard = null;
    /**
     the financial data for the register
     **/
    protected RegisterIfc register;
    /**
     The financial data for the store
     **/
    protected StoreStatusIfc storeStatus;

    /** Indicates if transaction is in progrss **/
    protected boolean transactionInProgress = false;

    /** Handle to the current transaction ADO */
    protected RetailTransactionADOIfc currentTransactionADO;

    //----------------------------------------------------------------------
    /**
     Loads cargo from redeem service. <P>
     <B>Pre-Condition(s)</B>
     <UL>
     <LI>Cargo will contain the retail transaction
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
        RedeemCargo cargo = (RedeemCargo) bus.getCargo();

        currentTransactionADO = (RetailTransactionADOIfc)cargo.getCurrentTransactionADO();
        register = cargo.getRegister();
        storeStatus = cargo.getStoreStatus();
        transactionInProgress = cargo.isTransactionInProgress();
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
     Loads cargo for gift card redeem service. <P>
     <B>Pre-Condition(s)</B>
     <UL>
     <LI>Cargo will contain the retail transaction
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
        RedeemCargo cargo = (RedeemCargo) bus.getCargo();

        cargo.setCurrentTransactionADO(currentTransactionADO);
        cargo.setRegister(register);
        cargo.setStoreStatus(storeStatus);
        cargo.setTransactionInProgress(transactionInProgress);
    }                                 
}
