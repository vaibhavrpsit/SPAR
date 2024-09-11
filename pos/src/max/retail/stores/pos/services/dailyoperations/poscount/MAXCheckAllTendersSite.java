
package max.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;


//------------------------------------------------------------------------------
/**
     Check to make sure that all tender have been counted. <P>

     @version $Revision: 3$
**/
//------------------------------------------------------------------------------

public class MAXCheckAllTendersSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8961678876151073252L;


    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: 3$";
    /**
       Site name for logging
    **/
    public static final String SITENAME = "CheckAllTendersSite";

    //--------------------------------------------------------------------------
    /**

       Updates the totals with the counted amounts when counting the till. <P>


       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // Get the cargo
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();

        // If the count is for float, loan or pick, exit.
        if (cargo.getCountType() != PosCountCargo.TILL)
        {
            bus.mail(new Letter("Success"), BusIfc.CURRENT);
        }
        else // Count is for Till; perform the check.
        {
            if (cargo.getSummaryFlag())
            {
                cargo.updateTillSummaryInTotals();
            }
            else
            {
                // update the cash detail counts
                CurrencyDetailBeanModel[] model = cargo.getCurrencyDetailBeanModels();
                for(int i = 0; i < model.length; i++)
                {
                    cargo.updateCashDetailAmountInTotals(model[i]);
                }
                // clear the cash detail hashtable for use with foreign currencies
                cargo.resetCurrencyDetailBeanModels();

                // update the other tender detail counts
                cargo.updateTenderDetailAmountsInTotals();
                // clear the other tender detail map for use with foreign currencies
                cargo.resetTenderDetails();
            }
            RegisterIfc register = cargo.getRegister();
            register.getTillByID(cargo.getTillID()).setTotals(cargo.getFinancialTotals());
            cargo.setRegister(register);
            bus.mail(new Letter("Success"), BusIfc.CURRENT);
        }
 
    }

}
