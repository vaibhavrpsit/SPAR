/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/override/SecurityOverrideReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/07/28 21:09:47 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:07 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.1  2004/06/15 16:37:35  awilliam
 *   @scr 5455 and 5248 tender override displays wrong error msg when tender override not in securityaccess for manager override list for a no acces user creating endless loop updated to use the common security procedure also added a security return shuttle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.override;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.admin.security.override.SecurityOverrideCargo;

//--------------------------------------------------------------------------
/**

    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SecurityOverrideReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8474623885653849776L;

    /**
     The logger to which log messages will be sent.
     **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.security.common.SecurityOverrideLaunchShuttle.class);
   /**
    * the class name
    */
    public static final String SHUTTLENAME = "SecurityOverrideReturnShuttle";
    /**
     The calling service's cargo.
     **/
    protected UserAccessCargoIfc callingCargo = null;
    
    protected EmployeeIfc overrideOperator = null;

    //--------------------------------------------------------------------------
    /**
     Copies information from the security override cargo used in the security service.
     @param bus the bus being loaded
     **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        SecurityOverrideCargo callingCargo = (SecurityOverrideCargo) bus.getCargo();
        overrideOperator = callingCargo.getOperator();
    }

    //--------------------------------------------------------------------------
    /**
     Copies information to the cargo used in calling service.
     @param bus the bus being unloaded
     **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // set default
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        cargo.setOverrideOperator(overrideOperator);
        cargo.getCurrentTransactionADO().overrideFunction(cargo.getOverrideOperator(),
                RoleFunctionIfc.TENDER_LIMIT,
                (TenderTypeEnum)cargo.getTenderAttributes()
                .get(TenderConstants.TENDER_TYPE));
    }

   
}
