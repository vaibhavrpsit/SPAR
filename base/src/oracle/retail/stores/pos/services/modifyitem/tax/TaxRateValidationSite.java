/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxRateValidationSite.java /main/10 2011/02/16 09:13:27 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:25:49 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:44 PM  Robert Pearse   
 * $
 * Revision 1.6  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.5  2004/03/09 16:45:14  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.4  2004/03/08 23:37:03  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.3  2004/03/08 21:07:52  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.2  2004/03/07 18:44:11  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.1  2004/03/05 22:57:24  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.4  2004/03/05 00:41:52  bjosserand
 * @scr 3954 Tax Override
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Site used for processing data from tax rate override screen.
 * 
 * @version $Revision: /main/10 $
 */
public class TaxRateValidationSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -8090441775957350822L;
    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * This site is executed when a tax rate is entered at the UI and the
     * Success button is pressed.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ItemTaxModControllerIfc cntl = cargo.getController();

        try
        {
            cntl.validateTaxRate(bus);

            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TaxErrorException te)
        {
            displayDialog(ui, te.getErrorTextResourceName(), DialogScreensIfc.ERROR);
        }
    }

    /**
     * Display the specified Error Dialog
     * 
     * @param String
     *            name of the Error Dialog to display
     * @param POSUIManagerIfc
     *            UI Manager to handle the IO
     */
    protected void displayDialog(POSUIManagerIfc ui, String name, int dialogType)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(dialogType);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}