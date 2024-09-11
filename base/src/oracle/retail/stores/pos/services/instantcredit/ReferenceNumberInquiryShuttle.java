/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/ReferenceNumberInquiryShuttle.java /rgbustores_13.4x_generic_branch/1 2011/09/07 15:30:37 hyin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    hyin      09/07/11 - bug 12957179: fix Ref number inquiry screen missing
 *                         ref number problem
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.instantcredit;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

/**
 * Added this for ReferenceNumberInquiry screen.
 * @author hyin
 *
 */
public class ReferenceNumberInquiryShuttle extends FinancialCargoShuttle {
	
    /**
     *  This id is used to tell the compiler not to generate 
     *  a new serialVersionUID.
     */
    private static final long serialVersionUID = -9085553790583843667L;

    int process = InstantCreditCargo.PROCESS_REFERENCE;
	
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ReferenceNumberInquiryShuttle.class);
	
    public void load(BusIfc bus)
    {
        super.load(bus);
        process = ((InstantCreditCargo)bus.getCargo()).getProcess();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        InstantCreditCargo iCargo = (InstantCreditCargo)bus.getCargo();
        iCargo.setProcess(process);
    }    

}
