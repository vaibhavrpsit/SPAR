/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/delete/IsInvalidLayawayDeletionFeeSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 *    4    360Commerce 1.3         4/25/2007 8:52:25 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:28:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:32 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.0   Aug 29 2003 16:00:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 26 2003 12:44:34   sfl
 * When delete layaway, don't charge layaway fee again.
 * Resolution for POS SCR-3252: Layaway Delete is Layaway Fee and Delete Fee is not Correct in totals
 *
 *    Rev 1.0   Apr 29 2002 15:21:00   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:50   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:21:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.delete;

// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.PaymentDetailBeanModel;

//--------------------------------------------------------------------------
/**
    This signal determines if the layaway deletion fee is too much (i.e. would
    cause the customer to owe money).
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsInvalidLayawayDeletionFeeSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9026821310386995497L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Signal name for toString
    **/
    public static final String SIGNALNAME = "IsInvalidLayawayDeletionFeeSignal";

    //----------------------------------------------------------------------
    /**
        This signal determines if the layaway deletion fee is too much (i.e. would
        cause the customer to owe money).
        <p>
        @param bus the bus trying to proceed
        @return true if deletion fee error; false otherwise
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        boolean notOk = false;

        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        LayawayCargo layawayCargo = (LayawayCargo) bus.getCargo();
        LayawayIfc layaway = layawayCargo.getLayaway();

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
            notOk = true;

            // set deletion fee
            layaway.setDeletionFee(
                layaway.getTotalAmountPaid().subtract(
                layaway.getCreationFee()));
        }

        return notOk;
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}
