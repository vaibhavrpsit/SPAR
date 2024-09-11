/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/SecurityOverrideReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
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
 *  1    360Commerce 1.0         4/8/2008 7:35:22 PM    Sameer Thajudin Sets
 *       the Access Function ID in AdminCargo back to default value 
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

// Foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//------------------------------------------------------------------------------
/**
    This shuttle reverts the access function id AdminCargo to its default value
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SecurityOverrideReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2873368255293267735L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(SecurityOverrideReturnShuttle.class);
   
    /**
       class name constant
    **/
    public static final String SHUTTLENAME = "SecurityOverrideReturnShuttle";
    
    //--------------------------------------------------------------------- 
    /**
        ShuttleIfc interface has a definition for the method below.
        @param The Bus
    **/
    //--------------------------------------------------------------------- 
    public void load(BusIfc bus)
    {
       	
    }
    
    //--------------------------------------------------------------------- 
    /**
        When returning from the security override station, the access function id
        in AdminCargo is set back to default value. 
        @param The Bus
    **/
    //--------------------------------------------------------------------- 
    public void unload(BusIfc bus)
    {
    	((AdminCargo)bus.getCargo()).setAccessFunctionID(RoleFunctionIfc.ADMIN);
    }
      
}
