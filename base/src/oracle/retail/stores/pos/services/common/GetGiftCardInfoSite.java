/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/GetGiftCardInfoSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/22/2006 11:45:05 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 18 2003 12:09:10   lzhao
 * deprecate the file.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.1   Dec 08 2003 09:03:06   lzhao
 * remove expirationDate.
 * 
 *    Rev 1.0   Aug 29 2003 15:54:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 14 2003 15:52:32   sfl
 * When creating a new BigDecimal object, need to use a number  string. Therefore, not to use toFormattedString() to convert 
 * crrency value to string since it might cause problem when crrency value is greater than one thousand.
 * Resolution for POS SCR-3328: Select Canadian Check crashes POS
 *
 *    Rev 1.1   Jul 22 2003 11:04:30   DCobb
 * Extracted computeExpirationDate() to GiftCardUtility.
 * Resolution for POS SCR-2702: Retrieve suspended gift card sale trans, gift card expriation date is N/A
 *
 *    Rev 1.0   Apr 29 2002 15:35:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:09:20   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:22:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:42:18   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:13:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;
// Java imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import java.math.BigDecimal;

//--------------------------------------------------------------------------
/**
    This site displays the SELL_GIFT_CARD screen to allow
    the user to enter the required information for the gift card.
    <p>
    @deprecated deprecated in version 7.0
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetGiftCardInfoSite extends PosSiteActionAdapter
{

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Displays the SELL_GIFT_CARD menu screen to allow the user
       to enter the required information for the item.
       <P>
       Initializes the bean model from the item in the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        PLUItemCargoIfc cargo = (PLUItemCargoIfc) bus.getCargo();
        GiftCardPLUItemIfc item = (GiftCardPLUItemIfc) cargo.getPLUItem();
        GiftCardIfc giftCard = null;
        //
        // Setup the bean model and display the menu screen
        //
        if (item != null)
        {
            // create a gift card bean model
            GiftCardBeanModel model = new GiftCardBeanModel();

            if (item.getGiftCard() == null)
            {
                giftCard = DomainGateway.getFactory().getGiftCardInstance();
            }
            else
            {
                giftCard = item.getGiftCard();
            }

            // set the gift card amount in the model
            if (giftCard.getInitialBalance() != null)
            {
                BigDecimal amt = new BigDecimal(item.getGiftCard().getInitialBalance().toString());
                model.setGiftCardAmount(amt);
            }
            else
            {
                BigDecimal amt = new BigDecimal(item.getPrice().getStringValue());

                // set the amount in the model
                model.setGiftCardAmount(amt);

                // set intial balance in the gift card
                giftCard.setInitialBalance(item.getPrice());

                // set intial balance in the gift card
                giftCard.setCurrentBalance(item.getPrice());
            }

            // set date sold for the gift card
            EYSDate eysDate = DomainGateway.getFactory().getEYSDateInstance();
            giftCard.setDateSold(eysDate);

            /*if (giftCard.getExpirationDate() != null)
            {
                model.setGiftCardExpirationDate(item.getGiftCard().getExpirationDate());
            }
            else
            {
                EYSDate expirationDate =
                    GiftCardUtility.computeExpirationDate(eysDate,
                                        pmManager,
                                        bus.getServiceName());

                if ( eysDate != null)
                {
                    giftCard.setExpirationDate(expirationDate);
                    model.setGiftCardExpirationDate(expirationDate);
                }
            }*/

            if (logger.isInfoEnabled()) logger.info( "GetGiftCardInfoSite.arrive(), giftCard = " + giftCard + "");

            item.setGiftCard(giftCard);
            cargo.setPLUItem(item);

            // Ask the UI Manager to display the menu screen
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.SELL_GIFT_CARD, model);
        }

    }
}
