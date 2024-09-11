/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/AlterationsPriceEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:15 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:35 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 26 2003 11:14:56   DCobb
 * AlterationsPriceEnteredAisle sends letter Retry on no price entered.
 * Resolution for POS SCR-2920: Alterations - Enter Price screen hangs when no alterations price is entered before pressing "Next".
 *
 *    Rev 1.0   Sep 25 2002 17:19:10   DCobb
 * Initial revision.
 * Resolution for POS SCR-1802: Response region defaults 0.00 after alterations item is added
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the Item price has been entered.
    Alterations allows zero price; the cargo priceEntered flag is set.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class AlterationsPriceEnteredAisle extends PosLaneActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = 3346368098965562814L;
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sets the price of the item.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        String letterName = CommonLetterIfc.RETRY;
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        String input = ui.getInput();
        if ((input != null) && (!input.equals("")))
        {
            letterName = CommonLetterIfc.CONTINUE;

            /*
             * Grab the item from the cargo
             */
            AlterationsCargo cargo = (AlterationsCargo)bus.getCargo();
            PLUItemIfc pluItem = cargo.getPLUItem();

            String stringValue = LocaleUtilities.parseCurrency(input,LocaleMap.getLocale(LocaleMap.DEFAULT)).toString();
            CurrencyIfc price = DomainGateway.getBaseCurrencyInstance(stringValue);
            if (pluItem != null)
            {
                pluItem.setPrice(price);
                cargo.setPriceEntered(true);
            }
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}
