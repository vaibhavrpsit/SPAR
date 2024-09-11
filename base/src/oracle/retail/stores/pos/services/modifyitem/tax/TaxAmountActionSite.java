/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxAmountActionSite.java /main/11 2011/02/16 09:13:27 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 10/29/08 - cleaned up commented out code
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:18 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:25:45 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:40 PM  Robert Pearse
 * $
 * Revision 1.7  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.6  2004/03/11 23:10:27  bjosserand
 * @scr 3954 Tax Override
 *
 */

package oracle.retail.stores.pos.services.modifyitem.tax;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Site used when tax amount is entered.
 * 
 * @version $Revision: /main/11 $
 */
public class TaxAmountActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 7351785715715005175L;

    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Sets the tax amount in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get cargo handle
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        ItemTaxModControllerIfc cntl = cargo.getController();

        cntl.processTaxAmount(bus);

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}
