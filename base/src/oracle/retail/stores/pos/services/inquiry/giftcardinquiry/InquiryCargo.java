/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/giftcardinquiry/InquiryCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 11:02:36 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   04/25/11 - Refactored giftcard inquiry for APF
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 |    4    360Commerce 1.3         4/27/2008 5:28:27 PM   Charles D. Baker CR
 |         31482 - Updated the journalResponse method of GetResponseSite to
 |         intelligently journal entries with the appropriate journal type
 |         (Trans or Not Trans). Code Review by Tony Zgarba.
 |    3    360Commerce 1.2         3/31/2005 4:28:22 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:22:06 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:11:24 PM  Robert Pearse   
 |   $
 |   Revision 1.3  2004/06/29 19:59:03  lzhao
 |   @scr 5477: add gift card inquiry in training mode.
 |
 |   Revision 1.2  2004/04/09 16:56:03  cdb
 |   @scr 4302 Removed double semicolon warnings.
 |
 |   Revision 1.1  2004/04/07 21:10:08  lzhao
 |   @scr 3872: gift card redeem and revise gift card activation
 |
 |   Revision 1.3  2004/02/12 16:50:22  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 21:51:11  rhafernik
 |   @scr 0 Log4J conversion and code cleanup
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 |   updating to pvcs 360store-current
 | 
 |    Rev 1.2   Nov 10 2003 07:23:36   baa
 | restore deleted methods
 | 
 |    Rev 1.0   Aug 29 2003 15:59:40   CSchellenger
 | Initial revision.
 | 
 |    Rev 1.1   Sep 26 2002 15:48:38   kmorneau
 | print an inquiry slip for customer
 | Resolution for 1816: Gift Card Balance Slip
 | 
 |    Rev 1.0   Apr 29 2002 15:23:06   msg
 | Initial revision.
 | 
 |    Rev 1.0   Mar 18 2002 11:32:58   msg
 | Initial revision.
 | 
 |    Rev 1.0   Sep 21 2001 11:20:40   msg
 | Initial revision.
 | 
 |    Rev 1.1   Sep 17 2001 13:08:02   msg
 | header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.giftcardinquiry;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This cargo contains the persistent data needed in the Gift Card Inquiry
 * service.
 * 
 */
public class InquiryCargo implements CargoIfc
{
    /**
     * The gift card returned
     */
    protected GiftCardIfc giftCard = null;

    /**
     * The product group items.
     */
    protected SaleReturnLineItemIfc[] productGroupItems = null;

    /**
     * The counter for product group items array.
     */
    protected int productGroupItemsCounter = 0;

    /**
     * The flag to show that inquiry has to loop for all gift cards in product group items array.
     */
    protected boolean loopThroughItems = false;

    /**
     * Indicates transaction in progress
     */
    protected boolean transactionInProgress = false;

    /**
     * The result of the interaction with the authorization manager
     */
    protected String responseCode;

    /**
     * The current register
     */
    protected RegisterIfc register = null;

    /**
     * Logger instance to send log messages to.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.inquiry.giftcardinquiry.InquiryCargo.class);

    /**
     * Returns the gift card.
     * @return GiftCard.
     */
    public GiftCardIfc getGiftCard()
    {
        return(giftCard);
    }

    /**
     * Sets the gift card.
     * @param card gift card
     */
    public void setGiftCard(GiftCardIfc card)
    {
        giftCard = card;
    }

    /**
     * Gets the product group items.
     * @return SaleReturnLineItemIfc[].
     */
    public SaleReturnLineItemIfc[] getProductGroupItems ()
    {
         return productGroupItems;
    }

    /**
     * Sets the product group items.
     * @param items as  SaleReturnLineItemIfc[].
     */
    public void setProductGroupItems (SaleReturnLineItemIfc[] items)
    {
         productGroupItems = items;
    }

    /**
     * Gets the counter for product group items array.
     * @return int.
     */
    public int getProductGroupItemsCounter()
    {
       return productGroupItemsCounter;
    }

    /**
     * Sets the counter for product group items array.
     * @param i index as int.
     */
    public void setProductGroupItemsCounter(int i)
    {
       productGroupItemsCounter = i;
    }

    /**
     * Gets the flag for looping trough product group items array.
     * @return boolean.
     */
    public boolean getLoopThroughItems()
    {
       return loopThroughItems;
    }

    /**
     * Sets the flag for looping trough product group items array.
     * @param b boolean.
     */
    public void setLoopThroughItems(boolean b)
    {
       loopThroughItems = b;
    }

    /**
     * Sets the transaction in progress status
     * @param value boolean representing transaction in progress status
     */
    public void setTransactionInProgress(boolean value)
    {
        transactionInProgress = value;
    }

    /**
     * Gets the transaction in progress status
     * @return a boolean representing transaction in progress status
     */
    public boolean isTransactionInProgress()
    {
       return transactionInProgress;
    }
    
    /**
     * Retrieves one parameter value as a string.
     * @param pm the parameter manager
     * @param parameter the parameter to retrieve the value of
     * @return the parameter value
     */
    public String getParameterValue(ParameterManagerIfc pm, String parameter)
    {
        String paramValue = null;
        try
        {
            paramValue = pm.getStringValue(parameter);
        }
        catch(ParameterException pe)
        {
            logger.error("Inquiry: Parameter could not be read: [" + parameter + "]: " + Util.throwableToString(pe));
            if (pe.getCause() != null)
            {
                logger.error("Inquiry: ParameterException.NestedException: \n" + Util.throwableToString(pe.getCause()));
            }
        }
        return paramValue;
    }

    /**
     * Returns the register object.
     * @return The register object.
     */
    public RegisterIfc getRegister()
    {                                   // begin getRegister()
        return register;
    }                                   // end getRegister()

    /**
     * Sets the register object.
     * @param  value  The register object.
     */
    public void setRegister(RegisterIfc value)
    {                                   // begin setRegister()
        register = value;
    }                                   // end setRegister()

}
