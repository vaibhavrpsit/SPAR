/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ItemPriceModifiedAisle.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 10/28/08 - localization for item tax reason codes
 *    acadar 10/27/08 - use localized price override reason codes
 *    acadar 10/25/08 - localization of price override reason codes
 *
 * ===========================================================================

     $Log:
      7    360Commerce 1.6         6/4/2007 6:01:32 PM    Alan N. Sinton  CR
           26486 - Changes per review comments.
      6    360Commerce 1.5         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
           26486 - EJournal enhancements for VAT.
      5    360Commerce 1.4         4/25/2007 8:52:23 AM   Anda D. Cadar   I18N
           merge

      4    360Commerce 1.3         1/22/2006 11:45:12 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:33 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:30 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:41 PM  Robert Pearse
     $
     Revision 1.4  2004/08/03 19:12:56  dcobb
     @scr 5440 Price Override: Indicator not printing on Kit Components

     Revision 1.3  2004/02/12 16:51:02  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:39:28  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:01:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 26 2003 14:33:48   RSachdeva
 * Removed use of CodeEntry.getCode() method
 * Resolution for POS SCR-2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.0   Apr 29 2002 15:17:02   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:12   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 05 2002 16:42:38   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.1   08 Nov 2001 09:20:10   pjf
 * Enhancements to kit components flow.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   Sep 21 2001 11:28:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:12   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifyitem;

//java imports
import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.FinalLetter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;

//--------------------------------------------------------------------------
/**
 *   Journal the item price modification.<P>
 *   @version $Revision: /main/13 $
 */
//--------------------------------------------------------------------------
public class ItemPriceModifiedAisle extends PosLaneActionAdapter
{
    //--------------------------------------------------------------------------
    /**
     *   Revision Number furnished by TeamConnection. <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /main/13 $";

    //--------------------------------------------------------------------------
    /**
     *   This site journals an item price override and mails a FinalLetter
     *   to exit the service.
     *   @param  BusIfc bus
     *   @return void
     *   @exception
     */
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        savePriceModification(bus);
        bus.mail(new FinalLetter("Next"), BusIfc.CURRENT);
    }
    //--------------------------------------------------------------------------
    /**
     *   This method saves an item price override to the cargo. The item price
     *   override is journaled.
     *   @param  BusIfc bus
     */
    //--------------------------------------------------------------------------
    protected void savePriceModification(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        SaleReturnLineItemIfc item = cargo.getItem();
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        //Save old item info in stringbuffer for journal
        StringBuffer sb = new StringBuffer();
        sb.append(formatter.toJournalRemoveString(item));

        ItemDiscountStrategyIfc[] itemDiscounts =
            item.getItemPrice().getItemDiscounts();
        int numItemDiscounts = 0;
        if((itemDiscounts != null) && (itemDiscounts.length > 0))
        {
            for(int i = 0; i < itemDiscounts.length; i++)
            {
                if(!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit))
                {
                    numItemDiscounts++;
                }
            }
            if(numItemDiscounts > 0)
            {
                sb.append(Util.EOL);
                sb.append(formatter.toJournalRemoveString(item));
            }
        }


        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel)ui.getModel(POSUIManagerIfc.PRICE_OVERRIDE);

        BigDecimal price = beanModel.getValue();
        String reasonCodeKey = beanModel.getSelectedReasonKey();
        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        CodeListIfc rcl = cargo.getLocalizedPriceOverrideReasons();

        if (rcl != null)
        {
            CodeEntryIfc entry = rcl.findListEntryByCode (reasonCodeKey);
            localizedCode.setCode(reasonCodeKey);
            localizedCode.setText(entry.getLocalizedText());
        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }

        StringBuffer buf=new StringBuffer("**ItemPriceModifiedAisle Received input:"+price +"reason");
        buf.append(localizedCode.getCode());
        if (logger.isInfoEnabled()) logger.info("" + buf.toString() + "");

        // make the price into CurrencyIfc
        CurrencyIfc currency = DomainGateway.getBaseCurrencyInstance(price.toString());

        cargo.getItem().modifyItemPrice(currency, localizedCode);
        cargo.getItem().getItemPrice().calculateItemTotal();

        // journal it here
        JournalManagerIfc journal =
            (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        if (journal != null)
        {
            //save new item info in stringbuffer for journal
            sb.append(Util.EOL);
            sb.append(formatter.toJournalString(cargo.getItem(), null, null));
            itemDiscounts = item.getItemPrice().getItemDiscounts();
            numItemDiscounts = 0;
            if((itemDiscounts != null) && (itemDiscounts.length > 0))
            {
                for(int i = 0; i < itemDiscounts.length; i++)
                {
                    if(!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit))
                    {
                        numItemDiscounts++;
                    }
                }
                if(numItemDiscounts > 0)
                {
                    sb.append(Util.EOL);
                    sb.append(formatter.toJournalString(item, null, null));
                }
            }

            //actually write the journal
            journal.journal(cargo.getCashier().getEmployeeID(),
                            cargo.getTransactionID(),
                            sb.toString());
        }
        else
        {
            logger.warn( "No journal manager found!");
        }
    }

}
