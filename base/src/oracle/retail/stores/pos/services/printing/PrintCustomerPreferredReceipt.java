/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/PrintCustomerPreferredReceipt.java /main/3 2014/06/06 16:27:50 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/06/14 - code cleanup
 *    mchellap  11/23/12 - Receipt enhancement quickwin changes
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Prints receipts based on customer preference with out providing Receipt
 * Options UI.
 */
public class PrintCustomerPreferredReceipt extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 718272279572645893L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/3 $";

    /**
     * Mails {@link CommonLetterIfc#PRINT}, {@link CommonLetterIfc#CONTINUE}, 
     * {@link CommonLetterIfc#EMAIL} or {@link CommonLetterIfc#PRINT_AND_EMAIL}.
     * 
     * @param bus the bus arriving at this site
     * @see oracle.retail.stores.domain.customer.CustomerIfc#getReceiptPreference()
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();

        // See if printing is configured
        boolean shouldPrint = true;
        try
        {
            shouldPrint = pm.getBooleanValue(ParameterConstantsIfc.PRINTING_PrintReceipts);
        }
        catch (ParameterException pe)
        {
            logger.error("Could not determine PrintReceipts setting.", pe);
        }

        // print receipt if configured
        if (shouldPrint)
        {
            letter = new Letter(CommonLetterIfc.PRINT);

            // See if eReceipt functionality is enabled.
            boolean ereceiptEnabled = false;
            try
            {
                ereceiptEnabled = pm.getBooleanValue(ParameterConstantsIfc.PRINTING_eReceiptFunctionality);
            }
            catch (ParameterException pe)
            {
                logger.error("Could not determine eReceiptFunctionality setting.", pe);
            }

            if (ereceiptEnabled)
            {
                if (cargo.getTransaction().getCustomer() != null)
                {
                    if (cargo.getTransaction().getCustomer().getReceiptPreference() == ReceiptConstantsIfc.EMAIL_RECEIPT)
                    {
                        letter = new Letter(CommonLetterIfc.EMAIL);
                    }
                    else if (cargo.getTransaction().getCustomer().getReceiptPreference() == ReceiptConstantsIfc.PRINT_AND_EMAIL_RECEIPT)
                    {
                        letter = new Letter(CommonLetterIfc.PRINT_AND_EMAIL);
                    }
                }
            }
        }

        // mail the letter
        bus.mail(letter, BusIfc.CURRENT);
    }
}
