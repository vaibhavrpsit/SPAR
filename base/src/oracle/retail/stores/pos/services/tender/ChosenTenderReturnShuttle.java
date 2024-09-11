/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/ChosenTenderReturnShuttle.java /main/12 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.7  2004/08/19 21:55:41  blj
 *   @scr 6855 - Removed old code and fixed some flow issues with gift card credit.
 *
 *   Revision 1.6  2004/07/23 22:17:25  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.5  2004/07/22 22:38:41  bwf
 *   @scr 3676 Add tender display to ingenico.
 *
 *   Revision 1.4  2004/07/08 20:34:57  bwf
 *   @scr 6049
 *
 *   Revision 1.3  2004/04/28 15:46:37  blj
 *   @scr 4603 - Fix gift card change due defects.
 *
 *   Revision 1.2  2004/04/13 21:43:09  bwf
 *   @scr 4263 Fix problem with decomposition.
 *
 *   Revision 1.1  2004/04/02 20:17:27  epd
 *   @scr 4263 Refactored coupon tender into sub service
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * Copies the transaction back, regardless of whether we
 * added a tender to it or not
 */
public class ChosenTenderReturnShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 1073198897365895172L;


    protected TenderCargo childCargo;
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        childCargo = (TenderCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        TenderCargo callingCargo = (TenderCargo)bus.getCargo();
        callingCargo.setCurrentTransactionADO(childCargo.getCurrentTransactionADO());
        // this is to copy the customer over if it was gotten in the subtour.
        if(childCargo.getCustomer() != null)
        {
            callingCargo.setCustomer(childCargo.getCustomer());
        }
          
        callingCargo.setTenderADO(childCargo.getTenderADO());
        callingCargo.setLineDisplayTender(childCargo.getLineDisplayTender());
        
        callingCargo.setItemScanned(childCargo.isItemScanned());
        callingCargo.setItemQuantity(childCargo.getItemQuantity());
        
        callingCargo.setPreTenderMSRModel(childCargo.getPreTenderMSRModel());
        callingCargo.setRegister(childCargo.getRegister());
        callingCargo.setStoreStatus(childCargo.getStoreStatus());
    }
}