/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/GiftCardReloadLaunchShuttle.java /rgbustores_13.4x_generic_branch/2 2011/06/01 12:21:53 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   05/31/11 - Refactored Gift Card Redeem and Tender for APF
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         2/10/2006 11:06:44 AM  Deepanshu       CR
 *         6092: Sales Assoc sould be last 4 digits of Sales Assoc ID and not
 *         of Cashier ID on the recipt
 *    3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:56 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:15 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:49:49  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Feb 04 2004 15:25:52   blj
 * more gift card refund work.
 * 
 *    Rev 1.1   Nov 26 2003 09:19:12   lzhao
 * use methods in utility, cleanup.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   Nov 21 2003 14:45:46   lzhao
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * This shuttle copies information from the cargo used
 * in the POS service to the cargo used in the Gift Card Reload service.
 */
@SuppressWarnings("serial")
public class GiftCardReloadLaunchShuttle implements ShuttleIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.giftcard.GiftCardReloadLaunchShuttle.class);

    /**
     * Gift card cargo
     */
    protected GiftCardCargo giftCardCargo = null;

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {                                   // begin load()        
         giftCardCargo = (GiftCardCargo)bus.getCargo(); 
    }  // end load()

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {                                   // begin unload()
        GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
        
        cargo.setStoreStatus(giftCardCargo.getStoreStatus());
        cargo.setRegister(giftCardCargo.getRegister());
        cargo.setOperator(giftCardCargo.getOperator());
        cargo.setCustomerInfo(giftCardCargo.getCustomerInfo());
        cargo.setTenderLimits(giftCardCargo.getTenderLimits());
        cargo.setTransaction(giftCardCargo.getTransaction());
        cargo.setPLUItem(giftCardCargo.getPLUItem());
        cargo.setGiftCardAmount(giftCardCargo.getGiftCardAmount());
        cargo.setSalesAssociate(giftCardCargo.getSalesAssociate());
        cargo.setFundingSelectionOnly(giftCardCargo.isFundingSelectionOnly());
    }  // end unload()

}
