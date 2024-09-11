/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/EnterItemSizeSite.java /main/17 2013/07/30 15:28:21 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  07/30/13 - pluitem for different size should be identified by
 *                         size.
 *    ohorne    06/03/13 - added Available Item Sizes to PLU
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    sgu       06/03/10 - add item # & description to EnterItemSizeSite
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:02 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:25 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse
 *
 *   Revision 1.5  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.4  2004/04/20 13:05:35  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.3  2004/04/13 15:27:44  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.2  2004/03/26 22:29:00  baa
 *   @scr 3561 Returns Retrieve size info from bundle if db is offline
 *
 *   Revision 1.1  2004/02/19 19:29:36  epd
 *   @scr 3561 Updates for Returns - Enter Size alternate flow
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
 *    Rev 1.0   Dec 17 2003 11:37:02   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ItemSizesTransaction;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DisplayTextBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;


//------------------------------------------------------------------------------
/**
    @version $Revision: /main/17 $
**/
//------------------------------------------------------------------------------

public class EnterItemSizeSite extends PosSiteActionAdapter
{

    /** sizes tag */
    public static final String SIZE_TAG     = "sizes.";

    /** brand tag */
    public static final String BRAND_TAG    = "360";

    /** screen spec */
    public static final String DISPLAY_SPEC = "DisplaySizeInfoSpec";
    //--------------------------------------------------------------------------
    /**
        Retrieves size info
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        PLUCargoIfc cargo = (PLUCargoIfc)bus.getCargo();

        DisplayTextBeanModel model = new DisplayTextBeanModel();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        String displayText = null;
        if (cargo.getPLUItem().getAvailableItemSizes() != null)
        {
            displayText = getAvailableSizesInfo(cargo.getPLUItem().getAvailableItemSizes());
        }
        else
        {
            displayText = getAvailableSizesInfo(utility);
        }
        model.setDisplayText(displayText);

        PromptAndResponseModel parModel = new PromptAndResponseModel();
        String[] args = new String[2];
        args[0] = cargo.getPLUItemForSizePrompt().getPosItemID();
        args[1] = cargo.getPLUItemForSizePrompt().getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        parModel.setArguments(args);
        model.setPromptAndResponseModel(parModel);

        // get ui reference and display screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.ITEM_SIZE, model);
    }

    //--------------------------------------------------------------------------
    /**
        Retrieves size info from the database.
        @param utility a pointer to the utility manager
        @return A String representing all the available sizes for an item
    **/
    //--------------------------------------------------------------------------
    public String getAvailableSizesInfo(UtilityManagerIfc utility)
    {
        ItemSizesTransaction dbTrans = null;

        dbTrans = (ItemSizesTransaction) DataTransactionFactory.create(DataTransactionKeys.ITEM_SIZES_TRANSACTION);

        StringBuilder display = new StringBuilder();
        try
        {
            ItemSizeIfc[] sizes = dbTrans.readItemSizeCodes(utility.getRequestLocales());
            display.append(getAvailableSizesInfo(sizes));
        }
        catch (DataException e)
        {
            logger.warn("Error executing ItemSizesTransaction.readItemSizeCodes()", e);
            // Retrieve size info from the bundles
            display.append(getSizesInfoFromBundle(utility, BRAND_TAG));
        }
        return display.toString();
    }

    //--------------------------------------------------------------------------
    /**
        Builds the available itemSizes text for an array of ItemSizeIfc objects.
        @param itemSizes an array of ItemSizeIfc objects.
        @return A String representing all the available sizes for an item
    **/
    public String getAvailableSizesInfo(ItemSizeIfc[] itemSizes)
    {
        StringBuilder display = new StringBuilder();
        for (int i=0; i<itemSizes.length; i++)
        {
            display.append(itemSizes[i].getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)))
                   .append(" = ")
                   .append(itemSizes[i].getSizeCode())
                   .append(Util.EOL).append(Util.EOL);
        }
        return display.toString();
    }

    //--------------------------------------------------------------------------
    /**
        Retrieves size info from bundles. Although 360store does not support the
        concept of brand and this point this method includes it to facilitate
        services implementation of branding easier.
        @param utility a pointer to the utility manager
        @param brandTag  use to retrieve size info specific to a brand
        @return String the size info
    **/
    //--------------------------------------------------------------------------

    public String getSizesInfoFromBundle(UtilityManagerIfc utility, String brandTag)
    {
        StringBuffer display = new StringBuffer();

        boolean done = false;
        int count = 1;
        while (!done)
        {
            StringBuffer keyBuffer =
                new StringBuffer().append(brandTag).append(".").append(SIZE_TAG).append(String.valueOf(count));

            String sizeLine = utility.retrieveText(DISPLAY_SPEC,
                                                   BundleConstantsIfc.POS_BUNDLE_NAME,
                                                   keyBuffer.toString(),
                                                   null);

            if (!Util.isEmpty(sizeLine))
            {
                display.append(sizeLine).append(Util.EOL);
                count++;
            }
            else
            {
                done = true;
            }
        }

        return display.toString();
    }
}
