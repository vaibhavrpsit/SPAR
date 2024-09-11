/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

// java imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.ItemSizeCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;
import oracle.retail.stores.pos.utility.PLUItemUtility;


//--------------------------------------------------------------------------
/**
    This aisle is traveled when the user has entered an item number
    in the SELL_ITEM screen.
    <p>
    @version $Revision: 4$
**/
//--------------------------------------------------------------------------
public class MAXAlterOrderItemNumberEnteredAisle extends PosLaneActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4010539640037162469L;
	/**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 4$";
    /**
       resource ID for Invalid Number Error dialog
    **/
    public static final String INVALID_NUMBER_ERROR = "InvalidNumberError";
    /**
       tag for Item Number in resuorce bundle
    **/
    public static final String ITEM_NUMBER_TAG = "ItemNumber";
    /**
       default text for Item Number in resuorce bundle
    **/
    public static final String ITEM_NUMBER_TEXT = "Item Number";

    //----------------------------------------------------------------------
    /**
       Stores the item number in the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        Letter letter = null;
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Get the user input
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        PromptAndResponseModel parModel =
            ((POSBaseBeanModel) ui.getModel(POSUIManagerIfc.SELL_ITEM)).getPromptAndResponseModel();
        String itemID = parModel.getResponseText();
        
        // Check Digit if not in training mode
        if (/*cargo.getRegister().getWorkstation().isTrainingMode() == false && */
            utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_ITEMNUMBER, itemID) == false)
        {
            showInvalidNumberDialog(utility, ui);
        }
        else
        {
            letter = new Letter(CommonLetterIfc.VALID);
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    
    /**
     * Extract item size info from scanned item
     * @param itemID scanned item number
     * @return the item number
     */
    protected String processScannedItemNumber(BusIfc bus, String itemID)
    {
//      Store the item size in the cargo
        ItemSizeCargoIfc cargo = (ItemSizeCargoIfc) bus.getCargo();
        String itemNumber = itemID;
        String [] parser = PLUItemUtility.getInstance().parseItemString(itemID);
        if (parser != null)
        {
          String itemSize = null;
          itemNumber = parser[0];
          itemSize = parser[1];
          cargo.setItemSizeCode(itemSize);
        }

        
        return itemNumber;
    }
    //--------------------------------------------------------------------------
    /**
        Shows the invalid item number dialog screen.
        <P>
        @param the utility manager
        @param the UI manager
    **/
    //--------------------------------------------------------------------------
    protected void showInvalidNumberDialog(UtilityManagerIfc utility, POSUIManagerIfc ui)
    {
      DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(INVALID_NUMBER_ERROR);
      dialogModel.setType(DialogScreensIfc.ERROR);
      String[] args = new String[1];
        String arg = utility.retrieveDialogText(ITEM_NUMBER_TAG, ITEM_NUMBER_TEXT);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
      args[0] = arg.toLowerCase(locale);
      dialogModel.setArgs(args);
      ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
