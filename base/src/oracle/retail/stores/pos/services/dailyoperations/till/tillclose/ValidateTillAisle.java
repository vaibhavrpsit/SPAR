/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillclose/ValidateTillAisle.java /main/1 2011/12/13 18:06:54 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   12/13/11 - Confirm till can be reconciled. (It might have been
 *                         reconciled from BO after it was closed in POS.)
 *    blarsen   12/13/11 - XbranchMerge djindal_bug-12963620 from
 *                         rgbustores_13.0x_branch
 *    djindal   09/21/11 - File header added and Code indented.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillclose;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.TillCargo;

/**
 * This file will fetch the till status from the database and check whether the
 * till has been already reconciled or not. If the till has been reconciled then
 * an error message is displayed.
 */
public class ValidateTillAisle extends PosLaneActionAdapter
{
    public void traverse(BusIfc bus)
    {
        FinancialTotalsDataTransaction db = (FinancialTotalsDataTransaction)DataTransactionFactory
                .create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
        String letterName = CommonLetterIfc.CONTINUE;
        TillCargo cargo = (TillCargo)bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        TillIfc till;
        String tillID = cargo.getTillID();
        try
        {
            till = db.readTillStatus(register.getWorkstation().getStore(), tillID);
            if (till.getStatus() == AbstractFinancialEntityIfc.STATUS_RECONCILED)
            {
                letterName = TillLetterIfc.TILL_ERROR;
                cargo.setErrorType(TillCargo.TILL_ALREADY_RECONCILED_TYPE);
                cargo.setTillFatalError();
            }
        }
        catch (DataException e)
        {
            // There is an error retrieving the till status, since this is an
            // edge condition, log it and go on.
            logger.warn("Unable to verify that the till belongs to register "
                    + register.getWorkstation().getWorkstationID() + ".", e);
        }
        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }
}
