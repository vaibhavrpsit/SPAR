/*=========================================================package oracle.retail.stores.pos.services.instantcredit;==================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/ReferenceSelectedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/24 19:03:16 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         05/23/11 - move inquiry for payment into instantcredit service
* sgu         05/23/11 - add the new class
* sgu         05/23/11 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.instantcredit;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

public class ReferenceSelectedRoad extends LaneActionAdapter
{
    //----------------------------------------------------------------------
    /**
       Sets the type of operation to be Instant Credit Temporary Pass
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        cargo.setProcess(InstantCreditCargo.PROCESS_REFERENCE);
    }
}