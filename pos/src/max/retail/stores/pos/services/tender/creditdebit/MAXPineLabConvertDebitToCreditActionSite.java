/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev	1.0 	27-Aug-2015		Geetika4.Chugh		<Comments>	
*
********************************************************************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderDebitADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;

/**
 *  User has chosen to convert a debit to a credit
 */
public class MAXPineLabConvertDebitToCreditActionSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        
        // Get debit
        TenderDebitADO debit = (TenderDebitADO)cargo.getTenderADO();
        // Get tender attributes and update for Credit
        HashMap tenderAttributes = debit.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CREDIT);

        cargo.setTenderADO(null);
        cargo.setTenderAttributes(tenderAttributes);
            
        // continue
        bus.mail(new Letter("Continue"), BusIfc.CURRENT);
    }
}
