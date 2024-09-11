/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
  Rev 1.0     12/09/2015     Deepshikha    Initial Draft:Changes done for loyalty points redeem
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.context.TourADOContext;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;


/**
 * Copy the chosen tender type and amount to the child service
 */
public class MAXChosenTenderLaunchShuttle extends FinancialCargoShuttle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1081646659488523563L;
	protected TenderCargo callingCargo;
    
    public void load(BusIfc bus)
    {
        super.load(bus);
        callingCargo = (TenderCargo)bus.getCargo();
    }

    public void unload(BusIfc bus)
    {
        super.unload(bus);
        TenderCargo childCargo = (TenderCargo)bus.getCargo();
        childCargo.setOperator(callingCargo.getOperator());
        childCargo.setCurrentTransactionADO(callingCargo.getCurrentTransactionADO());
        childCargo.setTenderAttributes(callingCargo.getTenderAttributes());
        if (callingCargo.getCustomer() != null)
        {
            childCargo.setCustomer(callingCargo.getCustomer());
        }
        else if ((callingCargo.getTransaction() != null) && (callingCargo.getTransaction().getCustomer() != null))
        {
            childCargo.setCustomer(callingCargo.getTransaction().getCustomer());
        }
        /**Transfer transaction object**/
        if(callingCargo.getTransaction() != null)
        {
        	childCargo.setTransaction(callingCargo.getTransaction());
        }

        // Reset ADO context for calling service
        TourADOContext context = new TourADOContext(bus);
        context.setApplicationID(childCargo.getAppID());
        ContextFactory.getInstance().setContext(context);
    }

}
