/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tilloptions/CheckAccountabilitySite.java /main/1 2013/04/17 14:42:37 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   04/10/13 - This site checks the accountability for Register or
 *                         Cashier and does the appropriate processing.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tilloptions;

// java imports
import java.io.Serializable;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.TillCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;

/**
 * This site checks the accountability for Register or Cashier and does the
 * appropriate processing.
 * <p>
 * 
 * 
 **/
public class CheckAccountabilitySite extends PosSiteActionAdapter
{

    /**
     * revision number of this class
     **/
    private static final long serialVersionUID = -7013598604298194954L;

    /**
     * Checks the accountability.
     * <P>
     * 
     * @param bus Service Bus
     **/
    public void arrive(BusIfc bus)
    {

        String letterName = null;

        boolean callOperatorID = false;
        ParameterManagerIfc pm;
        pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            Serializable[] values;
            values = pm.getParameterValues("IdentifyCashierEveryTransaction");
            RegisterIfc register = ((TillCargo)bus.getCargo()).getRegister();

            String parameterValue = (String)values[0];
            if (register.getAccountability() == AbstractFinancialEntityIfc.ACCOUNTABILITY_REGISTER
                    || (register.getAccountability() == AbstractFinancialEntityIfc.ACCOUNTABILITY_CASHIER && parameterValue
                            .equalsIgnoreCase("Y")))
            {
                callOperatorID = true;
            }
        }
        catch (ParameterException e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            letterName = CommonLetterIfc.CONTINUE;
        }

        if (callOperatorID && !bus.getCurrentLetter().getName().equals(CommonLetterIfc.FAILURE)
                && !bus.getCurrentLetter().getName().equals(CommonLetterIfc.UNDO))
        {
            letterName = "StartSale";
        }
        else if (callOperatorID
                && (bus.getCurrentLetter().getName().equals(CommonLetterIfc.FAILURE) || bus.getCurrentLetter().getName()
                        .equals(CommonLetterIfc.UNDO)))
        {
            letterName = "FailureTill";
        }
        else
        {
            letterName = CommonLetterIfc.CONTINUE;
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }

}