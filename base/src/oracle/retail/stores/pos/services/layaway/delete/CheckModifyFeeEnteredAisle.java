/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/delete/CheckModifyFeeEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    npoola    12/28/09 - Validating the Layway deletion fee since it cant be
 *                         more than the Amount paid.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:25 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:10 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:57 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   13 Jan 2004 15:02:08   aschenk
 * Initial revision.
 *
 *    Rev 1.0   Aug 29 2003 16:00:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jun 05 2003 15:37:44   bwf
 * Make sure layaway is not null, which occurs during offline situations.
 * Resolution for 2583: POS client crashed at layaway offline payment screen
 *
 *    Rev 1.2   May 27 2003 08:48:22   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.delete;

// java imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.PaymentDetailBeanModel;


//--------------------------------------------------------------------------
/**
    Displays error message indicating payment is invalid.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckModifyFeeEnteredAisle extends PosLaneActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = 22199220864792349L;
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";




    //----------------------------------------------------------------------
    /**

       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {


        LayawayCargo layawayCargo = (LayawayCargo) bus.getCargo();
        LayawayCargo layawayDeleteCargo = (LayawayDeleteCargo) bus.getCargo();
        LayawayIfc layaway = layawayCargo.getLayaway();
        String letter = CommonLetterIfc.CONTINUE;


        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        if (ui.getModel() instanceof PaymentDetailBeanModel)
        {

            PaymentDetailBeanModel model = (PaymentDetailBeanModel) ui.getModel();

            // get deletion fee and set in layaway
            layaway.setDeletionFee(model.getDeletionFee());
        }

        // check deletion fee to see if it is ok
        CurrencyIfc amount =
            layaway.getTotalAmountPaid().subtract(layaway.getDeletionFee());

        if (amount.compareTo(DomainGateway.getBaseCurrencyInstance()) == CurrencyIfc.LESS_THAN)
        {
        //    letter = "Failure";

            // set deletion fee
            layaway.setDeletionFee(
                layaway.getTotalAmountPaid().subtract(
                layaway.getCreationFee()));
            InvalidLayawayDeletionFeeAisle(bus);
        }

        else if (ui.getModel() instanceof PaymentDetailBeanModel)
        {


            PaymentDetailBeanModel beanModel =
                    (PaymentDetailBeanModel) ui.getModel();

            CurrencyIfc layawayFee  = beanModel.getDeletionFee();

            layawayDeleteCargo.setAccessFunctionID(RoleFunctionIfc.MODIFY_LAYAWAY_FEES);


            // If the creation fee changed...
            if (layaway != null &&  // this will be null if offline
                layaway.getDeletionFee().compareTo(layawayFee) != 0 )
            {

                letter = "CheckAccess";
            }

            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }


    }

    public void InvalidLayawayDeletionFeeAisle(BusIfc bus)
    {
        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // get layaway
        LayawayIfc layaway = ((LayawayCargo) bus.getCargo()).getLayaway();

        // show dialog
        String[] args = new String[1];
        String layawayFeeAmt =
            LocaleUtilities.formatCurrency(layaway.getDeletionFee().toString(),
                                         LocaleMap.getLocale(LocaleMap.DEFAULT));

        args[0] = "" + layawayFeeAmt;
        DialogBeanModel dbm = new DialogBeanModel();
        dbm.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dbm.setResourceID("InvalidLayawayDeletionFee");
        dbm.setArgs(args);
        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, dbm);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dbm);
    }
}
