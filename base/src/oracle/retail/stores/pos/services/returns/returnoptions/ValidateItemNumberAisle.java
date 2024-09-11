/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ValidateItemNumberAisle.java /main/15 2013/01/07 11:08:06 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/04/13 - Refactoring ItemManager
 *    ohorne    08/10/11 - corrected use of itemNumber
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  12/10/09 - Serialisation return without receipt changes
 *    jswan     06/26/09 - Fix issues swiping card when looking up transactions
 *                         with credit card.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         10/5/2006 1:10:04 PM   Keith L. Lesikar
 *         Merge fix from BBY. Removed Gap-specific logic regarding UPC
 *         parsing.
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *
 *   Revision 1.12  2004/07/20 15:03:09  aachinfiev
 *   @scr 5833 - Disabled check digit for item number in training mode
 *
 *   Revision 1.11  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.10  2004/04/17 17:59:28  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.9  2004/03/23 18:49:50  aarvesen
 *   @scr 3561 use the "NoColon" version to display the text
 *
 *   Revision 1.8  2004/03/15 15:16:52  baa
 *   @scr 3561 refactor/clean item size code, search by tender changes
 *
 *   Revision 1.7  2004/03/10 19:41:52  baa
 *   @scr work for parsing size from scanned item
 *
 *   Revision 1.6  2004/03/09 21:16:47  epd
 *   @scr 3561 bug fixes
 *
 *   Revision 1.5  2004/03/05 23:27:58  baa
 *   @scr 3561 Retrieve size from scanned items
 *
 *   Revision 1.4  2004/02/18 20:36:20  baa
 *   @scr 3561 Returns changes to support size
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.6   05 Feb 2004 23:27:28   baa
 * return multiple items
 *
 *    Rev 1.5   Jan 23 2004 16:10:24   baa
 * continue returns developement
 *
 *    Rev 1.4   Dec 30 2003 16:58:50   baa
 * cleanup for return feature
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.3   29 Dec 2003 22:35:26   baa
 * more return enhacements
 *
 *    Rev 1.2   Dec 29 2003 15:36:28   baa
 * return enhancements
 *
 *    Rev 1.1   Dec 19 2003 13:23:04   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Dec 17 2003 11:37:34   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;


import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.ItemSearchCriteria;
import oracle.retail.stores.domain.stock.ItemSearchCriteriaIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.PLUItemCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;


//------------------------------------------------------------------------------
/**

    @version $Revision: /main/15 $
**/
//------------------------------------------------------------------------------

@SuppressWarnings("serial")
public class ValidateItemNumberAisle extends PosLaneActionAdapter
{

    //--------------------------------------------------------------------------
    /**
            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        DataInputBeanModel dataModel = (DataInputBeanModel) ui.getModel();
        boolean isScanned = dataModel.isScanned();
        String itemNumber = dataModel.getValueAsString(ReturnUtilities.ITEM_NUMBER);
        String letterName = CommonLetterIfc.SEARCH;

        if (!Util.isEmpty(itemNumber))
        {
            //Check if item number is valid and mail appropiate letter
            //checkIfItemNumberValid(bus, itemID,isScanned);
            // Perform checkdigit on item number
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            if (isTrainingMode(cargo) == false &&
                utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_ITEMNUMBER, itemNumber) == false)
            {
                String[] args = { utility.retrieveCommonText("ItemNumberLabelNoColon")};
                UIUtilities.setDialogModel(ui,DialogScreensIfc.ACKNOWLEDGEMENT,
                                           "InvalidNumberError",  args,  "Retry");
            }
            else
            {
                if (isScanned)
                {
                    // if scanned parse for item no and size
                    itemNumber = processScannedItemNumber(cargo, itemNumber);
                }
                else
                {
                    // Only check for size if the item is not scanned
                    if (isSizeRequired(bus, itemNumber, isScanned))
                    {
                       letterName = CommonLetterIfc.SIZE;
                    }
                }

                SearchCriteriaIfc criteria = cargo.getSearchCriteria();
                if (criteria == null)
                {
                    criteria = DomainGateway.getFactory().getSearchCriteriaInstance();
                }
                criteria.setItemNumber(itemNumber);
                cargo.setSearchCriteria(criteria);
                
                cargo.setItemScanned(isScanned);
                 // else check if size required
                bus.mail(new Letter(letterName), BusIfc.CURRENT);
            }
        }
        else
        {
            // Search for transactions with out item id
            bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Returns training mode status
     *
     * @param cargo ReturnItemCargoIfc
     * @return true if in training mode, false otherwise
     */
    //----------------------------------------------------------------------
    protected boolean isTrainingMode(ReturnItemCargoIfc cargo)
    {
        boolean trainingMode = false;
        if (cargo instanceof ReturnOptionsCargo)
        {
            ReturnOptionsCargo optCargo = (ReturnOptionsCargo) cargo;
            trainingMode = optCargo.getRegister().getWorkstation().isTrainingMode();
        }
        return trainingMode;
    }

    //----------------------------------------------------------------------
    /**
     * Extracts item size info from scanned item
     * @param cargo ReturnCargo
     * @param itemID scanned item number
     * @return String the item number
     */
    //----------------------------------------------------------------------
    protected String processScannedItemNumber(ReturnItemCargoIfc cargo, String itemID)
    {
        String itemNumber = itemID;
        // logic specific to Gap's implementation being removed
        /*
        String [] parser = PLUItemUtility.getInstance().parseItemString(itemID);
        if (parser != null)
        {
          String itemSize = null;
          itemNumber = parser[0];
          itemSize = parser[1];
          SearchCriteriaIfc criteria = cargo.getSearchCriteria();
          if (criteria == null)
          {
              criteria = DomainGateway.getFactory().getSearchCriteriaInstance();
          }
          criteria.setItemSizeCode(itemSize);
          criteria.setItemID(itemNumber);
          cargo.setSearchCriteria(criteria);
        }
        */

        return itemNumber;
    }


    //----------------------------------------------------------------------
    /**
     * Checks if the size is required for this item
     *  Accoding to the requirements if the item was scanned size info has
     *  already been provided if required, therefore for scanned items
     *  this method returns false
     * @param bus the bus
     * @param itemNumber the item number
     * @param isScanned flag that tells if item was scanned or manually entered
     * @return boolean flag that tells if item size info is required
     */
    //----------------------------------------------------------------------
    protected boolean isSizeRequired(BusIfc bus, String itemNumber, boolean isScanned)
    {
        boolean sizeRequired = false;
        // If the item was manually enter, lookup and check if size is
        // required
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        if (!isScanned)
        {
            PLUItemIfc pluItem = lookupItem(itemNumber, utility, bus);

            if (pluItem != null && pluItem.isItemSizeRequired())
            {
                PLUItemCargoIfc cargo = (PLUItemCargoIfc)bus.getCargo();
                cargo.setPLUItem(pluItem);
                sizeRequired = true;
            }

        }
        return sizeRequired;
    }

    //----------------------------------------------------------------------
    /**
     * Lookup item info
     *
     * @param itemNumber the item number
     * @return PLUItemIfc a plu item
     */
    //----------------------------------------------------------------------
    protected PLUItemIfc lookupItem(String itemNumber, UtilityManagerIfc utility, BusIfc bus)
    {
        PLUItemIfc pluItem = null;
        try
        {
            ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);

            //set itemNumber
            ItemSearchCriteriaIfc inquiry = DomainGateway.getFactory().getItemSearchCriteriaInstance();
            inquiry.setLocaleRequestor(utility.getRequestLocales());
            inquiry.setItemNumber(itemNumber);
            inquiry.setSearchItemByItemNumber(true);
            inquiry.setRetrieveFromStore(true);
            pluItem =  mgr.getPluItem(inquiry);
        } catch (DataException de)
        {
            logger.warn("PLUItem: " + itemNumber + " error = " + de.getMessage());
        }
        return pluItem;
    }

}
