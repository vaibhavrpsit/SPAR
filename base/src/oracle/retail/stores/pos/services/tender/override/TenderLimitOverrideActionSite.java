/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/override/TenderLimitOverrideActionSite.java /rgbustores_13.4x_generic_branch/1 2011/07/28 21:09:47 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/02 20:56:24  epd
 *   @scr 4263 Updates to accommodate new tender limit override station
 *
 *   Revision 1.2  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   29 Jan 2004 08:57:30   Tim Fritz
 * Allowing an invalid driver's license format is now checking security access.
 * 
 *    Rev 1.0   Nov 04 2003 11:17:54   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:29:54   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:06:50   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.override;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 *  Take a tender from cargo and attempt to invoke a tender limit override
 *  for the given tender
 */
public class TenderLimitOverrideActionSite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        cargo.setAccessFunctionID(RoleFunctionIfc.TENDER_LIMIT);

        // The transaction must attempt to override the behavior for the tender limits
        // for the current group
        boolean overrideSuccess = cargo.getCurrentTransactionADO()
                                       .overrideFunction(cargo.getOverrideOperator(),
                                                         RoleFunctionIfc.TENDER_LIMIT,
                                                         (TenderTypeEnum)cargo.getTenderAttributes()
                                                                              .get(TenderConstants.TENDER_TYPE));

        String letter = (overrideSuccess) ? "Success" : "Failure";    
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}
