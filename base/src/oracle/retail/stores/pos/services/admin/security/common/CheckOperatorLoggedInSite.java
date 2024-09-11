/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/common/CheckOperatorLoggedInSite.java /main/1 2012/10/29 16:37:49 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    vbongu 10/23/12 - check if operator exists
 *    
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security.common;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This site checks to see if the operator exists in the bus.         
 * @author vbongu
 * @since 14.0  
 */
public class CheckOperatorLoggedInSite extends PosSiteActionAdapter implements SiteActionIfc
{   
    public void arrive(BusIfc bus)
    {
        UserAccessCargoIfc cargo = (UserAccessCargoIfc) bus.getCargo();
        
        if(cargo.getOperator() != null)
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.LOGIN), BusIfc.CURRENT);
        }
    }
    
    
}
