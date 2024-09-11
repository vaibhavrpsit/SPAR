/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxAmountValidationSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:30:18 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:25:45 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:40 PM  Robert Pearse   
 * $
 * Revision 1.10  2004/05/07 20:09:09  dcobb
 * @scr 4654 Added On/Off with Ineligible Items alternate flow.
 *
 * Revision 1.9  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.8  2004/03/11 23:10:27  bjosserand
 * @scr 3954 Tax Override
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;


//--------------------------------------------------------------------------
/**
 * Site used when tax amount is entered.
 * <P>
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class TaxAmountValidationSite extends PosSiteActionAdapter
{
    /** Revision Number for this class. */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     * Validates the entered tax amount.
     * <P>
     * @param bus  The service bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ItemTaxModControllerIfc cntl = cargo.getController();

        try
        {
            cntl.validateTaxAmount(bus);

            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TaxErrorException te)
        {
            cargo.displayDialog(ui, te.getErrorTextResourceName(), DialogScreensIfc.ERROR);
        }
    }

}
