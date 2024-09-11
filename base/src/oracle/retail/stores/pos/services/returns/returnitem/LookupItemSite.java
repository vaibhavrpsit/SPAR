/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/LookupItemSite.java /main/2 2013/06/28 17:29:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    jswan  11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 *    yiqzha 06/28/13 - Change the dialog name to ItemNotFoundInStore.
 *    mkutia 03/08/13 - Moved this from the common package and implemented
 *                      Webstore WS call
 *    yiqzha 01/04/13 - Refactoring ItemManager
 *    jswan  08/17/11 - Modified to prevent the return of Gift Cards as items
 *                      and part of a transaction. Also cleaned up references
 *                      to gift cards objects in the return tours.
 *    ohorne 02/22/11 - ItemNumber can be ItemID or PosItemID
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    npoola 08/12/10 - GCInquiry is called only when the item is eligible for
 *                      return
 *    jswan  06/17/10 - Checkin external order integration files for refresh.
 *    jswan  05/27/10 - First pass changes to return item for external order
 *                      project.
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    abonda 01/03/10 - update header date
 *    abonda 10/17/08 - I18Ning manufacturer name
 *    abonda 10/15/08 - I18Ning manufacturer name
 *
 *
 * ===========================================================================

     $Log:
      6    360Commerce 1.5         3/10/2008 3:51:48 PM   Sandy Gu
           Specify store id for non receipted return item query.
      5    360Commerce 1.4         11/15/2007 10:12:37 AM Christian Greene
           Belize merge - check for null and set to Failure
      4    360Commerce 1.3         8/1/2006 6:16:52 PM    Brett J. Larsen CR
           16536 - adding support for returns using gift receipt with
           transaction and item not found
      3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:20 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse
     $

      4    .v7x      1.2.1.0     5/12/2006 7:07:53 AM   Dinesh Gautam
           CR16536-Modified for adding new Letter for
           ITEM_NOT_FOUND_PRICE_CODE screen

     Revision 1.9  2004/06/07 20:19:24  mkp1
     @scr 2775 PLU now checks whether item is taxable before retrieving tax rules

     Revision 1.8  2004/06/03 14:47:44  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.7  2004/04/17 17:59:28  tmorris
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.6  2004/03/11 15:44:12  epd
     @scr 3561 fixed app crash for unknown item

     Revision 1.5  2004/02/19 19:29:36  epd
     @scr 3561 Updates for Returns - Enter Size alternate flow

     Revision 1.4  2004/02/12 16:48:02  mcs
     Forcing head revision

     Revision 1.3  2004/02/11 23:22:58  bwf
     @scr 0 Organize imports.

     Revision 1.2  2004/02/11 21:19:59  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Feb 09 2004 10:02:48   baa
 * Initial revision.
 *
 *    Rev 1.0   Aug 29 2003 15:54:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 07 2003 12:30:10   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Apr 29 2002 15:34:48   msg
 * Initial revision.
 *
 *    Rev 1.2   25 Apr 2002 18:51:58   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 *    Rev 1.1   Mar 18 2002 23:09:46   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:22:58   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 05 2002 16:42:20   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.1   20 Nov 2001 09:18:30   pjf
 * Kit updates for return by kit header number.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   Sep 21 2001 11:13:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:14   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemSearchCriteriaIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureConstantsIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site queries the database for the item number in the cargo. It mails a
 * Success letter if the item is found. It mails a Failure letter if the item is
 * not found.
 */
public class LookupItemSite extends PosSiteActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -103151399047641669L;
    
    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";

    /**
     * Queries the database for the item number in the cargo. A Success letter
     * is mailed if the item is found. A Failure letter is mailed if the item is
     * not found.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Grab the item number from the cargo
        ReturnItemCargoIfc cargo  = (ReturnItemCargoIfc)bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        PLUItemIfc pluItem        = null;
        Letter letter             = null;
        SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
        String itemNumber         = cargo.getPLUItemID();

        try
        {
            if (cargo.getItemLookupLocaction().equals(ReturnItemCargoIfc.ItemLookupType.STORE))
            {
                inquiry.setRetrieveFromStore(true);
            }
            else
            {
                inquiry.setRetrieveFromStore(false);
            }
            inquiry.setLocaleRequestor(utility.getRequestLocales());
            inquiry.setItemNumber(itemNumber);
            inquiry.setSearchItemByItemNumber(true);
            inquiry.setGeoCode(cargo.getGeoCode());
            inquiry.setStoreNumber(cargo.getStoreID());                      
            inquiry.setPLURequestor(new PLURequestor());
            cargo.setSearchCriteria(inquiry);
            ItemManagerIfc mgr = (ItemManagerIfc)bus.getManager(ItemManagerIfc.TYPE);
            pluItem = mgr.getPluItem(inquiry);

            cargo.setPLUItem(pluItem);
            
            // if it is eligible return item and a gift card send a "GiftCard" letter
            if (pluItem == null)
            {
                letter = new Letter(CommonLetterIfc.FAILURE);
            }
            else if (pluItem instanceof GiftCardPLUItemIfc)
            {
                letter = new Letter("GCInquiry");
            }
            // if it is not a gift card check for unit of measure
            else
            {
                if ((pluItem.getUnitOfMeasure() == null) ||
                     pluItem.getUnitOfMeasure().getUnitID().equals(
                        UnitOfMeasureConstantsIfc.UNIT_OF_MEASURE_TYPE_UNITS))
                {
                    letter = new Letter(CommonLetterIfc.SUCCESS);
                }
                else
                {
                    letter = new Letter("UnitOfMeasure");
                }
            }
            
            // The item was found, so reset the lookup location to its initial state.
            cargo.setItemLookupLocaction(ReturnItemCargoIfc.ItemLookupType.STORE);
        }
        catch (DataException de)
        {
            logger.warn(bus.getServiceName() + "ItemNumber: " + itemNumber + " error = " + de.getMessage());
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            ReturnItemCargoIfc returncargo=(ReturnItemCargoIfc)cargo;
            boolean giftReceipt=returncargo.isGiftReceiptSelected();
            if(giftReceipt)
            {
                letter=new Letter("GiftReceipt");
            }
            else
            {
                letter = new Letter(CommonLetterIfc.FAILURE);
            }
        }
        
        bus.mail(letter, BusIfc.CURRENT);
    }
    
   
    /**
     *   Displays webService Dialog
     *   @param BusIfc
     *   @param ItemSearchCriteriaIfc
     *   @deprecated in 14.0 see DetermineNextLookupSite
     */
    protected void showWebServiceDialog(BusIfc bus, ItemSearchCriteriaIfc inquiry)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the choice dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("ItemNotFoundInStore");
        String msg[] = new String[1];
        msg[0] = inquiry.getItemNumber();
        model.setArgs(msg);
        
        model.setType(DialogScreensIfc.SEARCHWEBSTORE_CANCEL);
        model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_WEBSTORE, CommonLetterIfc.SEARCH);
        model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.FAILURE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
