/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/HasSentFinalResult.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/05/10 - remove deprecated log amanger and technician
 *    abondala  01/03/10 - update header date
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.tender;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * Checks whether return result has been sent to RM server.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class HasSentFinalResult implements TrafficLightIfc
{
    static final long serialVersionUID = 4572355899018953584L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(HasSentFinalResult.class);

    /**
     * Checks to see if the final result has sent to RM.
     * 
     * @return true if the final result has sent to RM, false otherwise.
     */
    public boolean roadClear(BusIfc bus)
    {
        logger.debug("HasSentFinalResult.roadClear() - entry");

        boolean result = false;
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        if (cargo.getReturnRequest() == null && cargo.getReturnResponse() == null && cargo.getReturnResult() == null)
        {
            result = true;
        }
        logger.debug("HasSentFinalResult.roadClear() - exit");
        return (result);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class:  HasSentFinalResult (Revision " + getRevisionNumber() + ")" + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
