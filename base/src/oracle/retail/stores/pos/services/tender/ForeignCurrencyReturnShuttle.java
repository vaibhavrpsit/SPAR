/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/ForeignCurrencyReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       12/23/08 - fixed the crash in foreign check tender
 *    ranojha   11/13/08 - Fixed Foreign Currency Till Reconciliation
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:46 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:28:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:07 PM  Robert Pearse
 *
 *   Revision 1.15  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.14  2004/08/12 19:58:07  crain
 *   @scr 6825 Redeeming a discounted foreign gift cert crashes the app
 *
 *   Revision 1.13  2004/05/04 03:35:44  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.12  2004/05/02 01:54:05  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.11  2004/04/22 19:12:38  crain
 *   @scr 4206 Updating Javadoc
 *
 *   Revision 1.10  2004/04/13 17:19:31  crain
 *   @scr 4206 Updating Javadoc
 *
 *   Revision 1.9  2004/04/09 19:26:01  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.8  2004/04/05 22:18:49  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.7  2004/04/01 21:22:10  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.6  2004/03/31 19:55:07  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.5  2004/03/26 04:20:19  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.4  2004/03/25 14:20:06  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.3  2004/03/23 00:31:09  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.2  2004/03/22 19:27:55  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.1  2004/03/22 15:51:03  crain
 *   @scr 4105 Foreign Currency
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.pos.ado.tender.CertificateTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * Shuttle returns from Foreign Currency ADO service
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ForeignCurrencyReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5682026965805247068L;

    /**
     * foreign currency cargo reference
     */
    protected TenderCargo foreignCurrencyCargo;


    //----------------------------------------------------------------------
    /**
     * Load a copy of foreign currency cargo into the Shuttle
     * @param bus the bus being loaded
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        foreignCurrencyCargo = (TenderCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     * Unloads the data to the tender currency cargo.
     * @param bus the bus being unloaded
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        TenderCargo tenderCargo = (TenderCargo)bus.getCargo();

        CurrencyIfc foreignCurrency = foreignCurrencyCargo.getCurrentAmount();
        if (foreignCurrency != null)
        {
            // set the currency
            tenderCargo.getTenderAttributes().put(TenderTDOConstants.ALTERNATE_CURRENCY, foreignCurrency);
            tenderCargo.getTenderAttributes().put(TenderConstants.ALTERNATE_AMOUNT, foreignCurrency.getStringValue());
            tenderCargo.getTenderAttributes().put(TenderConstants.AMOUNT, foreignCurrency.getBaseAmount().getStringValue());
            tenderCargo.getTenderAttributes().put(TenderConstants.CERTIFICATE_TYPE, CertificateTypeEnum.FOREIGN);
            tenderCargo.getTenderAttributes().put(TenderConstants.ALTERNATE_CURRENCY_TYPE, foreignCurrency.getType());
        }
    }
}
