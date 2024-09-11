/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the price override service to the Pricing service. <P>
    @version $Revision: 3$
**/
//------------------------------------------------------------------------------
public class MAXPriceOverrideReturnShuttle extends FinancialCargoShuttle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 9054745727845925563L;

	/** The logger to which log messages will be sent. **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.pricing.MAXPriceOverrideReturnShuttle.class);

    /** revision number supplied by source-code-control system **/
    public static String revisionNumber = "$Revision: 3$";

    /** class name constant **/
    public static final String SHUTTLENAME = "MAXPriceOverrideReturnShuttle";

    /** The Pricing Cargo of the source service **/

    protected PricingCargo pricingCargo = null;	
    //--------------------------------------------------------------------------
    /**
       Copies information from the cargo used in the price override service. <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        pricingCargo = (PricingCargo)bus.getCargo(); 
    }

    //--------------------------------------------------------------------------
    /**
       Copies information to the cargo used in the price override service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        MAXPricingCargo pc = (MAXPricingCargo) bus.getCargo();  //Rev 1.0 changes
        pc.setItems(pricingCargo.getItems());
        pc.setTransaction(pricingCargo.getTransaction());
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
