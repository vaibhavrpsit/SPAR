/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/SpecialOrderLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:25:25 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/02/12 16:48:17  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:22:50  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 07 2003 12:38:08   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 05 2003 14:14:50   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:04:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:09:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:43:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Dec 04 2001 16:38:02   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

 // foundation imports

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

//------------------------------------------------------------------------------
/**
    Special order launch shuttle
    <P>       
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SpecialOrderLaunchShuttle  
extends FinancialCargoShuttle
{
    /**  
        The logger to which log messages will be sent. 
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.SpecialOrderLaunchShuttle.class);

    public static final String SHUTTLENAME = "SpecialOrderLaunchShuttle";
     
    /** 
        revision number for this class 
    **/ 
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** 
        Outgoing PosCargo 
    **/ 
    protected SaleCargoIfc cargo = null; 

    //--------------------------------------------------------------------------
    /**
       Get a local copy of the Pos cargo.
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------

    public void load(BusIfc bus)
    {
        super.load(bus);
         
        // retrieve Pos cargo 
        cargo = (SaleCargoIfc) bus.getCargo(); 
    }

    //--------------------------------------------------------------------------
    /**
       Copy required data from the Pos cargo to the Special Order Cargo. 
       sets the access employee, sales associate, and register.
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------

    public void unload(BusIfc bus)
    {
        super.unload(bus); 
         
        // retrieve special order cargo
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo) bus.getCargo();
         
        // set the access employee and sales associate 
        specialOrderCargo.setOperator(cargo.getOperator());
        specialOrderCargo.setSalesAssociate(cargo.getEmployee()); 
        specialOrderCargo.setRegister(cargo.getRegister()); 
    }
}
