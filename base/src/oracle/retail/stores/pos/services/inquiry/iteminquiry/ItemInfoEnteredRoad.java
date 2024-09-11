/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ItemInfoEnteredRoad.java /main/30 2014/07/16 08:58:27 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  10/03/14 - Setting selected clearance index from the dropdown
 *                         to include it in verifying the condition if atleast one
 *                         search criteria is given.
 *    abhinavs  09/22/14 - Correctly setting style and item type 
 *                         (id instead of names) to the inquiry object.
 *    yiqzhao   09/22/14 - Add style and item type.
 *    abhinavs  08/13/14 - Fix to set inquiry from input when
 *                         filtering is enabled
 *    abhinavs  07/14/14 - CAE item search results filtering cleanup
 *    abhinavs  05/20/14 - Fix to correctly set discountable criterion
 *    jswan     02/13/14 - Fixed item search infinite loop issue.
 *    icole     10/04/13 - Forward port of fix for a scanned item number
 *                         getting truncated if it's a UPC that begins with 1
 *                         or 4 and for any barcode that is of length of 10
 *                         that begins with 1 or 4. This is code that was
 *                         specific to one client (GAP).
 *    cgreene   05/21/13 - implement price and clearance as criteria for item
 *                         search
 *    hyin      02/01/13 - re-arrange taxable and discountable drop down menu.
 *    hyin      01/29/13 - allowing user to only enter description on item
 *                         detail search.
 *    hyin      01/25/13 - add all option to dept, taxable and discountable
 *                         fields.
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    hyin      11/13/12 - enable adv search from result screen.
 *    hyin      09/26/12 - fix metatag sql to handle non-stock items.
 *    asinton   09/13/12 - reworked the logic to test if inquiry object is null
 *                         to prevent a null pointer exception.
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   02/29/12 - get itemNumber from cargo instead of ui if already
 *                         set
 *    hyin      08/16/12 - meta tag search feature.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    ddbaker   10/23/08 - Final updates for localized item description support
 *    mchellap  09/30/08 - Updated copy right comment
 *
 *     $Log:
 *      5    360Commerce 1.4         8/7/2006 1:36:51 PM    Brett J. Larsen CR
 *           17286 - fix issues with advance price search - next button not
 *           enabled when it should be
 *
 *           v7x->360Commerce
 *      4    360Commerce 1.3         12/13/2005 4:42:41 PM  Barry A. Pape
 *           Base-lining of 7.1_LA
 *      3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse
 *      2    360Commerce 1.1         3/10/2005 10:22:27 AM  Robert Pearse
 *      1    360Commerce 1.0         2/11/2005 12:11:38 PM  Robert Pearse
 *     $
 *
 *      5    .v7x      1.3.1.0     6/2/2006 8:09:33 AM    Dinesh Gautam
 *           CR17286-fix for enabling enter/Next button on Adv search screen
 *
 *     Revision 1.11  2004/07/17 16:03:04  lzhao
 *     @scr 6319: clone searchCriteria for search
 *
 *     Revision 1.10  2004/06/23 23:39:17  lzhao
 *     @scr 5650: parse bar code.
 *
 *     Revision 1.9  2004/06/23 23:14:55  lzhao
 *     @scr 5650: parse bar code for item id and item size for item inquiry
 *
 *     Revision 1.8  2004/06/21 22:20:06  lzhao
 *     @scr 5650: price inquiry investigation.
 *
 *     Revision 1.7  2004/06/17 20:59:22  lzhao
 *     @scr 5650: Dump information for finding the problem
 *
 *     Revision 1.6  2004/05/03 18:30:29  lzhao
 *     @scr 4544, 4556: keep user entered info when back to the page.
 *
 *     Revision 1.5  2004/03/16 18:30:46  cdb
 *     @scr 0 Removed tabs from all java source code.
 *
 *     Revision 1.4  2004/02/16 22:41:16  lzhao
 *     @scr 3841:Inquiry Option Enhancement
 *     add gift code and add multiple inquiry.
 *
 *     Revision 1.3  2004/02/12 16:50:30  mcs
 *     Forcing head revision
 *
 *     Revision 1.2  2004/02/11 21:51:11  rhafernik
 *     @scr 0 Log4J conversion and code cleanup
 *
 *     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *     updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Sep 05 2002 14:44:56   jriggins
 * ReplaceStar() now pulls the wildcard characters from the bundle instead of being hardcoded.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:22:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:33:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:29:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:10   msg
 * header update
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.util.Locale;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemInquiryBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This road is traveled when the user enters the item number. It stores the
 * item number in the cargo.

 */
public class ItemInfoEnteredRoad extends LaneActionAdapter
{
    private static final long serialVersionUID = -7236780670685092921L;

    /**
     * Stores the item info and dept list in the cargo.
     *
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Initialize this variable to false; it will be set to true later if necessary.
        if (cargo.getInquiry() != null)
        {
            cargo.getInquiry().setSearchFromItemDetail(false);
            if (cargo.isItemFromWebStore())
            {
                cargo.getInquiry().setRetrieveFromStore(false);
            }
        }

        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        // Initialize bean model values
        if (ui.getModel() instanceof ItemInquiryBeanModel)
        {
            setItemInquiry(cargo, (ItemInquiryBeanModel)ui.getModel(), uiLocale);
        }
        else
        {
            /* Test if the item number is available on the inquiry object from the cargo.
             * If this is the case then the inquiry object was populated for Mobile POS,
             * else (itemNumber == null) we get the item number from the UI.
             */
            String itemNumber = null;
            if (cargo.getInquiry() != null && !Util.isEmpty(cargo.getInquiry().getItemNumber()))
            {
                itemNumber = cargo.getInquiry().getItemNumber();
                // use new search info
                if (cargo.getInquiry().isMetaTagSearch())
                {
                    cargo.getInquiry().setMetaTagSearchStr(itemNumber);
                }
            }
            if (itemNumber == null || cargo.isFilterSearchResults())
            {
                setItemInquiryFromInput(bus, cargo, (POSBaseBeanModel)ui.getModel(), uiLocale);
            }
        }
    }

    /**
     * Set the inquiry details onto the cargo.
     *
     * @param cargo
     * @param model
     * @param uiLocale
     */
    protected void setItemInquiry(ItemInquiryCargo cargo, ItemInquiryBeanModel model, Locale uiLocale)
    {
        if (model.isMetaTagAdvSearch())
        {
            cargo.setInquiry(uiLocale, model.getMetaTagSearchStr());
        }
        else
        {
            String itemNumber = null;
            String itemDesc = null;
            String geoCode = null;
            String manufacturer = null;
            if (model.getItemNumber() != null && model.getItemNumber().trim().length() > 0)
            {
                itemNumber = model.getItemNumber().trim();
            }

            if (model.getItemDesc() != null && model.getItemDesc().trim().length() > 0)
            {
                itemDesc = model.getItemDesc().trim();
            }

            if (model.getManufacturer() != null && model.getManufacturer().trim().length() > 0)
            {
                manufacturer = model.getManufacturer().trim();
            }
            
            // Including extra parameter to setInquiry method for filtering the search results
            cargo.setInquiry(uiLocale, itemNumber, itemDesc, model.getSelectedDept(), geoCode, manufacturer,
                    model.getSelectedType(), model.getSelectedUOM(), model.getSelectedStyle(),
                    model.getSelectedColor(), model.getSelectedSize(),null);
        }
    }


    /**
     * Set the inquiry details onto the cargo.
     *
     * @param cargo
     * @param model
     * @param uiLocale
     */
    protected void setItemInquiryFromInput(BusIfc bus, ItemInquiryCargo cargo, POSBaseBeanModel model, Locale uiLocale)
    {
        // for the search by entering item id on prompt area.
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        String itemID = ui.getInput().trim();
        String itemNumber = itemID;
        if(cargo.getAdvancedSearchResult() != null && cargo.getAdvancedSearchResult().getReturnItems() != null
                && !cargo.isFilterSearchResults())
        {
            cargo.getAdvancedSearchResult().setReturnItems(null);
            if(cargo.getInquiry() != null && cargo.getInquiry().getItemIdList() != null)
            {
                cargo.getInquiry().setItemIdList(null);
            }
        }
        if (Util.isEmpty(itemNumber))
        {
            if (ui.getModel() instanceof ItemInfoBeanModel)
            {
                ItemInfoBeanModel iibModel = (ItemInfoBeanModel)ui.getModel();

                // dept
                String deptID = null;
                if (iibModel.getSelectedDeptIdx() > 0)
                {
                    deptID = iibModel.getDeptList()[iibModel.getSelectedDeptIdx()].getDepartmentID();
                }
                
                // type
                String typeCode = null;
                if (iibModel.getSelectedTypeIdx() > 0) // first one is "all"
                {
                    typeCode = iibModel.getTypeList()[iibModel.getSelectedTypeIdx()].getItemTypeID();
                }

                // style
                String styleCode = null;
                if (iibModel.getSelectedStyleIdx() > 0) // first one is "all"
                {
                    styleCode = iibModel.getStyleList()[iibModel.getSelectedStyleIdx()].getIdentifier();
                }
               
                // size
                String sizeCode = null;
                if (iibModel.isItemSizeRequired())
                {
                    int sizeIdx = iibModel.getSelectedSizeIdx();
                    if (sizeIdx > 0) // first one is "all"
                    {
                        sizeCode = iibModel.getSizeList()[sizeIdx].getSizeCode();
                    }
                }

                // color
                String colorID = null;
                if (iibModel.getSelectedColorIdx() > 0) // first one is "all"
                {
                    colorID = iibModel.getColorList()[iibModel.getSelectedColorIdx()].getIdentifier();
                }

                // uom
                String uomID = null;
                if (iibModel.getSelectedUomIdx() > 0) // first one is "all"
                {
                    uomID = iibModel.getUomList()[iibModel.getSelectedUomIdx()].getUnitID();
                }
                
              //Including an extra parameter to setInquiry method for filtering the search results
                if(cargo.getAdvancedSearchResult()!=null){
                cargo.setInquiry(uiLocale, iibModel.getItemNumber(), iibModel.getItemDescription(), deptID,
                        /* geoCode */null, iibModel.getItemManufacturer(),
                        typeCode, uomID, styleCode, colorID, sizeCode, cargo.getAdvancedSearchResult().getReturnItems());
                }
                else
                {
                    cargo.setInquiry(uiLocale, iibModel.getItemNumber(), iibModel.getItemDescription(), deptID,
                            /* geoCode */null, iibModel.getItemManufacturer(),
                            typeCode, uomID, styleCode, colorID, sizeCode, null); 
                }

                cargo.getInquiry().setSearchFromItemDetail(true);

                /*
                 * For taxable and discountable on UI:
                 * <ul>
                 * <li>0---All
                 * <li>1---Yes
                 * <li>2---No
                 * </ul>
                 * <ul>
                 * In DB:
                 * <li>0---No
                 * <li>1---Yes
                 * </ul>
                 */
                if (iibModel.getSelectedTaxableIdx() > 0) // not ALL
                {
                    // means not taxable, convert to DB value: 0=no
                    if (iibModel.getSelectedTaxableIdx() == 2)
                    {
                        // in DB, 0 means tax exempt
                        cargo.getInquiry().setTaxable(0);
                    }
                    else
                    {
                        // 1=yes
                        cargo.getInquiry().setTaxable(iibModel.getSelectedTaxableIdx());
                    }

                }else
                {
                    // to look for both taxable and non-taxable items
                    cargo.getInquiry().setTaxable(3);
                }

                // discountable
                if (iibModel.getSelectedDiscountableIdx() > 0) // not ALL
                {
                    // means not discountable
                    if (iibModel.getSelectedDiscountableIdx() == 2)
                    {
                        cargo.getInquiry().setDiscountable(0);
                    }
                    else
                    {
                        // 1 = yes
                        cargo.getInquiry().setDiscountable(iibModel.getSelectedDiscountableIdx());
                    }
                }
                else
                {
                 // to look for both discountable and non-discountable items
                    cargo.getInquiry().setDiscountable(3);
                }
                // price
                cargo.getInquiry().setPrice(iibModel.getPrice());

                // clearance
                if (iibModel.getSelectedClearanceIdx() > 0) // not ALL
                {
                    cargo.getInquiry().setSearchItemByClearance(true);
                    // means not clearance
                    if (iibModel.getSelectedClearanceIdx()  == 2)
                    {
                        cargo.getInquiry().setClearance(0);
                    }
                    else
                    {
                        // 1 = yes
                        cargo.getInquiry().setClearance(iibModel.getSelectedClearanceIdx());
                    }
                }
                else
                {
                 // to look for both on clearance and not-on clearance items
                    cargo.getInquiry().setClearance(3);
                    cargo.getInquiry().setSearchItemByClearance(false);
                }
                cargo.getInquiry().setOnClearance(iibModel.isOnClearance());
            }
        }
        else
        {
            cargo.setInquiry(uiLocale, itemNumber, null, "-1");
        }

        if (cargo.isMetaTagSearch())
        {
            cargo.getInquiry().setMetaTagSearchStr(itemID);
        }
    }

    /**
     * Extract item size info from scanned item
     *
     * @param itemID scanned item number
     * @return the item number
     * @deprecated 14.0 code specific to a specific client (customer) and no longer required.
     */
    protected String processScannedItemNumber(BusIfc bus, String itemID)
    {
        String itemNumber = itemID;
        return itemNumber;
    }
}
