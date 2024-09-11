/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/AccessGrantedToEnterTransRentryModeRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
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
 *  1    360Commerce 1.0         4/8/2008 7:34:38 PM    Sameer Thajudin Sets
 *       the Access Function ID in AdminCargo back to default value
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.admin.AdminCargo;

//--------------------------------------------------------------------- 
/**
    If the user has the access to the security access point REENTRY_ON_OFF 
    and it has been verified, then the access function id in AdminCargo
    has to be set back to default value. 
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------- 


public class AccessGrantedToEnterTransRentryModeRoad extends PosLaneActionAdapter
{

	private static final long serialVersionUID = 1L;
	
//--------------------------------------------------------------------- 
  /**
      Sets the access function id in AdminCargo back to default value.  
      @param The Bus
  **/
  //--------------------------------------------------------------------- 
	
	public void traverse(BusIfc bus)
	{
		((AdminCargo)bus.getCargo()).setAccessFunctionID(RoleFunctionIfc.ADMIN);
	}
}
