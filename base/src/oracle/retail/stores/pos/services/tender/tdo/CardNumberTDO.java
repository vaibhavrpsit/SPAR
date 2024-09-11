/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/CardNumberTDO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:49 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:49 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:48:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Dec 02 2003 12:53:08   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.foundation.manager.device.MSRModel;

/**
 *  
 */
public class CardNumberTDO extends TDOAdapter
{
    /**
     * Convenience method to help with getting card number from tender attributes.
     * Abstracts whether the card has been swiped or not
     * @param tenderAttributes
     * @return
     */
    public String getCardNumberFromAttributes(HashMap tenderAttributes)
    {
        String cardNumber = "";
        if (tenderAttributes.get(TenderConstants.MSR_MODEL) != null)
        {
            cardNumber = ((MSRModel)tenderAttributes.get(TenderConstants.MSR_MODEL)).getAccountNumber();
        }
        else
        {
            cardNumber = (String)tenderAttributes.get(TenderConstants.NUMBER);
        }
        return cardNumber;
    }
    
    /**
     * Convenience method to determine whether a card has been swiped or not
     * @param tenderAttributes
     * @return
     */
    public boolean isCardSwiped(HashMap tenderAttributes)
    {
        return (tenderAttributes.get(TenderConstants.MSR_MODEL) != null) ? true : false;
    }
}
