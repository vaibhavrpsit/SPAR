/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/RentrySelectedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         4/8/2008 7:33:41 PM    Sameer Thajudin Sets
 *       the Access Function ID in AdminCargo to REENTRY_ON_OFF
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------- 
/**
    This class provides set the access function id in the cargo to RoleFunctionIfc.REENTRY_ON_OF.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------- 

public class RentrySelectedRoad extends PosLaneActionAdapter
{
	
  private static final long serialVersionUID = 1L;

  //--------------------------------------------------------------------- 
	/**
    	Sets the access function id to enable transaction reentry  
    	@param The Bus
  **/
  //--------------------------------------------------------------------- 
	
  public void traverse(BusIfc bus)
	{
		((AdminCargo) bus.getCargo()).setAccessFunctionID(RoleFunctionIfc.REENTRY_ON_OFF);
	}
}
