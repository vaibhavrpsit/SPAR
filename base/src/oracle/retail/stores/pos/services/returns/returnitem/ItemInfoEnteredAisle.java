/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/ItemInfoEnteredAisle.java /main/20 2013/01/29 16:18:43 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/29/13 - Remove item size check to allow alpha numeric
 *                         string.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    vikini    02/10/09 - Add tax component for returned Items not on file.
 *    nkgautam  01/21/09 - Added a null check condition for department in model
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/22/2006 11:45:18 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:38 PM  Robert Pearse
 *
 *   Revision 1.2  2004/07/30 14:52:31  jdeleau
 *   @scr 6530 Update quantity for return without receipt on
 *   an unknown item.
 *
 *   Revision 1.1  2004/03/22 22:39:46  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;
// java imports
import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ReadNewTaxRuleTransaction;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.ItemSizeCargoIfc;
import oracle.retail.stores.pos.services.common.PLUCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemNotFoundBeanModel;
import oracle.retail.stores.pos.utility.PLUItemUtility;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
    This aisle is traversed after the user enters additional item
    information.  It adds the item to the item list in the cargo.
    <p>
    @version $Revision: /main/20 $
**/
//--------------------------------------------------------------------------
public class ItemInfoEnteredAisle extends LaneActionAdapter
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.common.ItemInfoEnteredAisle.class);
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
       Constant for invalid serial quantity error screen
    **/
    public static final String QUANTITY_NOTICE = "QuantityNotice";
    /**
        unit of measure tag
    **/
    public static final String UNIT_OF_MEASURE_TAG = "unitOfMeasure";
    /**
        unit of measure default text
    **/
    public static final String UNIT_OF_MEASURE_TEXT = "units";

    //----------------------------------------------------------------------
    /**
        This aisle is traversed when the user enters
        the item information in the ITEM_NOT_FOUND screen.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        /*
         * Grab the bean model from the UI
         */
        ReturnItemCargo cargo = (ReturnItemCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ItemNotFoundBeanModel model;
        model = (ItemNotFoundBeanModel)ui.getModel(POSUIManagerIfc.ITEM_NOT_FOUND);

        UnknownItemIfc item = DomainGateway.getFactory().getUnknownItemInstance();
        item.setItemID(model.getItemNumber());
        //item size code being set
        if (!Util.isEmpty(model.getItemSize()) && cargo instanceof ItemSizeCargoIfc)
        {
            ((ItemSizeCargoIfc)cargo).setItemSizeCode(model.getItemSize());
        }
        String desc = model.getItemDescription();
        item.getLocalizedDescriptions().initialize(LocaleMap.getSupportedLocales(), desc);
        BigDecimal amt = model.getPrice();
        item.setPrice(DomainGateway.getBaseCurrencyInstance(amt.toString()));
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        if (model.getDepartmentName()!=null)
        {
        	item.setDepartmentID
	          (((utility.getReasonCodes(cargo.getStoreID(), CodeConstantsIfc.CODE_LIST_DEPARTMENT)).
	            findListEntry(model.getDepartmentName(), false, locale)).getCode());
        }
        item.setTaxable(model.getTaxable());
        // set tax group to 1 to  match parts for demo purposes.  This will be replaced in Release 2.5
        // by the standard default tax group.
        item.setTaxGroupID(1);
        // set unit of measure
        setUnitOfMeasure(model,
                         item,
                         cargo);

        lookupTaxes(item, cargo);

        // If this return is for an external order, associate
        // the external order with the PLUItem.
        if (cargo.isExternalOrder())
        {
            item.setReturnExternalOrderItem(cargo.
                    getCurrentExternalOrderItemReturnStatusElement().
                        getExternalOrderItem());
        }
        
        /*
         * Update the cargo with the entered information
         */
        cargo.setPLUItem(item);
        cargo.setDepartmentName(model.getDepartmentName());
        cargo.setItemSerial(model.getItemSerial());
        cargo.setUnknownItemQuantity(model.getQuantity());

        if (Util.isEmpty(model.getItemSerial()) ||
            Util.isObjectEqual(model.getQuantity(), BigDecimalConstants.ONE_AMOUNT) )
        {
            cargo.completeItemNotFound();

            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);

            //Show item on Line Display device
            try
            {
                pda.lineDisplayItem(item);
            }
            catch (DeviceException e)
            {
                logger.warn(
                            "Unable to use Line Display: " + e.getMessage() + "");
            }

            /*
             * Continue to next site
             */
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {
            // Using "generic dialog bean".
            DialogBeanModel dialogBeanmodel = new DialogBeanModel();
            dialogBeanmodel.setResourceID(QUANTITY_NOTICE);
            dialogBeanmodel.setType(DialogScreensIfc.ERROR);

            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBeanmodel);
        }
    }

    //---------------------------------------------------------------------
    /**
        Set unit of measure on item, as needed. <P>
        @param model ItemNotFoundBeanModel object
        @param item UnknownItemIfc object
        @param cargo PLUCargoIfc object
    **/
    //---------------------------------------------------------------------
    public void setUnitOfMeasure(ItemNotFoundBeanModel model,
                                 UnknownItemIfc item,
                                 ReturnItemCargo cargo)
    {
        // begin setUnitOfMeasure()
        UtilityManagerIfc utility =
          (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        // get string and look it up in code map
        String uomString = model.getUnitOfMeasure();
        CodeListIfc list = utility.getReasonCodes(cargo.getOperator().getStoreID(), CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        CodeEntryIfc uomCodeEntry = list.findListEntry(uomString, false, locale);
        String uomCode = uomCodeEntry.getCode();


        item.setUOMCode(uomCode);
        item.getLocalizedUOMNames().initialize(LocaleMap.getSupportedLocales(), uomString);
        item.setUOMName(locale, uomString);
    }                                   // end setUnitOfMeasure()


    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }


//  ---------------------------------------------------------------------
    /**
        This method looks up the tax rules and applies the same
        at the item. <P>
        @param item UnknownItemIfc object
        @param cargo PLUCargoIfc object
    **/
    //---------------------------------------------------------------------
    public void lookupTaxes(UnknownItemIfc item, PLUCargoIfc cargo)
    {
        //Only look up tax rules if taxable
        if(item.getTaxable())
        {
            if(cargo.getGeoCode() == null)
            {
                logger.error("GeoCode is null can not look up taxes on unknown item");
            }
            else
            {
                try
                {
                    //retrieve tax rules from database
                    ReadNewTaxRuleTransaction taxRuleTransaction = (ReadNewTaxRuleTransaction) DataTransactionFactory.create(DataTransactionKeys.READ_NEW_TAX_RULE_TRANSACTION);
                    TaxRulesVO taxRulesVO = taxRuleTransaction.getDepartmentDefaultTaxRules(cargo.getGeoCode(), item.getDepartmentID());

                    if(taxRulesVO.hasTaxRules())
                    {
                        //we can do the following line because we
                        //should only have the tax rules for one
                        //tax group
                        item.setTaxRules(taxRulesVO.getAllTaxRules());
                    }

                }
                catch(DataException de)
                {
                    logger.error("Received exception looking up tax rules for unknown item with geoCode = "
                            + cargo.getGeoCode() + "and department = " + item.getDepartmentID(), de);
                }
            }
        }

    }

}
