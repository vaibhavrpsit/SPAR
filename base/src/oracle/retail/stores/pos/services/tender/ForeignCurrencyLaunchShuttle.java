/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/ForeignCurrencyLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:46 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         6/21/2007 12:53:24 PM  Charles D. Baker CR
 *         27280 - Updated to remove dependency of country codes to exist in
 *         tourscript when tendering with alternate currencies.
 *    3    360Commerce 1.2         3/31/2005 4:28:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:07 PM  Robert Pearse   
 *
 *   Revision 1.11  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.10  2004/08/12 19:58:07  crain
 *   @scr 6825 Redeeming a discounted foreign gift cert crashes the app
 *
 *   Revision 1.9  2004/04/22 19:12:38  crain
 *   @scr 4206 Updating Javadoc
 *
 *   Revision 1.8  2004/04/13 17:19:31  crain
 *   @scr 4206 Updating Javadoc
 *
 *   Revision 1.7  2004/04/09 19:26:01  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.6  2004/04/07 21:34:55  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.5  2004/03/26 04:20:19  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.4  2004/03/25 14:20:06  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.3  2004/03/22 15:51:03  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.2  2004/03/19 14:50:51  baa
 *   @scr 0 remove unused import
 *
 *   Revision 1.1  2004/03/19 07:16:09  crain
 *   @scr 4105 Foreign Currency
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.foreigncurrency.ForeignCurrencyCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * Shuttle launches Foreign Currency ADO service  
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ForeignCurrencyLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7308497800755031771L;

    /**
     * tender cargo reference
     */
    protected TenderCargo tenderCargo;
     
    
    //----------------------------------------------------------------------
    /**
     * Load a copy of TenderCargo into the Shuttle
     * @param bus the bus being loaded
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        tenderCargo = (TenderCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     * Unloads the data to the foreign currency cargo.
     * @param bus the bus being unloaded
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        ForeignCurrencyCargo foreignCurrencycargo = (ForeignCurrencyCargo)bus.getCargo();
        
        String amount = ((String)tenderCargo.getTenderAttributes().get(TenderConstants.AMOUNT));
        String foreignAmount = ((String)tenderCargo.getTenderAttributes().get(TenderConstants.ALTERNATE_AMOUNT));
        
        // set the amount
        foreignCurrencycargo.getTenderAttributes().put(TenderConstants.AMOUNT, amount);
        // set the foreign amount
        foreignCurrencycargo.getTenderAttributes().put(TenderConstants.ALTERNATE_AMOUNT, foreignAmount);
        
        foreignCurrencycargo.initializeCurrencyActions();
    }
}
