/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved. 
  Rev 1.1   Prateek		22/07/2013		Changes done for BUG 7242
  Rev 1.0	Prateek		27/06/2013		Initial Draft:	Scanning HOT Keys for Return
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.common;
//java imports
import java.util.Locale;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.PLUTransaction;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureConstantsIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.PLUCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


//--------------------------------------------------------------------------
/**
    This site queries the database for the item number
    in the cargo. It mails a Success letter
    if the item is found. It mails a Failure
    letter if the item is not found.
    <p>
    @version $$Revision: /rgbustores_12.0.9in_branch/1 $;
**/
//--------------------------------------------------------------------------
public class MAXLookupItemSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";
    
	/**MAX Rev 1.1 Change : Start**/
    protected static final String MULTIPLE_HOT_KEY = "MultipleHotKeyItemFound";
    protected static boolean multipleHotKeyItemFlag = false;
    protected static boolean hotKeyNotAssignedItemFlag = false;
	/**MAX Rev 1.1 Change : End**/

    //----------------------------------------------------------------------
    /**
       Queries the database for the item number in the cargo.
       A Success letter is mailed if the item
       is found. A Failure letter is mailed
       if the item is not found.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

		/**MAX Rev 1.1 Change : Start**/
    	multipleHotKeyItemFlag = false;
    	hotKeyNotAssignedItemFlag =false;
		/**MAX Rev 1.1 Change : End**/
        /*
         * Grab the item number from the cargo
         */
        PLUCargoIfc cargo   = (PLUCargoIfc)bus.getCargo();
        String      itemID  = cargo.getPLUItemID();

        PLUItemIfc     pluItem = null;
        Letter      letter  = null;
        try
        {
            PLUTransaction pluTransaction = null;

            pluTransaction = (PLUTransaction) DataTransactionFactory.create(DataTransactionKeys.PLU_TRANSACTION);

            Locale  locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

            //set locale and itemID
            MAXSearchCriteriaIfc inquiry = (MAXSearchCriteriaIfc) DomainGateway.getFactory().getSearchCriteriaInstance();
 // changes starts for code merging(commenting below line as setLocale() method is removed in base 14)           
            //inquiry.setLocale(locale);
 // Changes ends for code merging
            inquiry.setItemID(itemID);
            inquiry.setGeoCode(cargo.getGeoCode());
            inquiry.setStoreNumber(cargo.getStoreID());
            if(itemID.length()==2)
            {
            	pluItem = getHotKeySItems(bus, cargo, inquiry);
            }
            else
            {
            	pluItem = pluTransaction.getPLUItem(inquiry);
            }

            cargo.setPLUItem(pluItem);
            // clear the ID now that we have the whole item
            cargo.setPLUItemID(null);

            // if it is a gift card send a "GiftCard" letter
            if (pluItem instanceof GiftCardPLUItemIfc)
            {
                letter = new Letter("GCInquiry");
            }
            else if (pluItem == null)
            {
                letter = new Letter("Failure");
            }
            // if it is not a gift card check for unit of measure
            else
            {
                if ((pluItem.getUnitOfMeasure() == null) ||
                     pluItem.getUnitOfMeasure().getUnitID().equals(
                        UnitOfMeasureConstantsIfc.UNIT_OF_MEASURE_TYPE_UNITS))
                {
                    letter = new Letter("Success");
                }
                else
                {
                    letter = new Letter("UnitOfMeasure");
                }
            }
        }
        catch (DataException de)
        {
            logger.warn(bus.getServiceName() + "PLUItem: " + itemID + " error = " + de.getMessage());
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            ReturnItemCargoIfc returncargo=(ReturnItemCargoIfc)cargo;
            boolean giftReceipt=returncargo.isGiftReceiptSelected();
            if(giftReceipt)
            {
       	   		letter=new Letter("GiftReceipt");
            }
            else
            {
            	letter = new Letter("NotValid");
            }
        }

        /*
         * Proceed to next site
         */
		 /**MAX Rev 1.1 Change : Start**/
        if(multipleHotKeyItemFlag)
        	showErrorDialog(bus,"MultipleHotKeyItem");
        else if (hotKeyNotAssignedItemFlag || pluItem == null)
        	showErrorDialog(bus,"INFO_NOT_FOUND_ERROR");
        else
        	bus.mail(letter, BusIfc.CURRENT);
		/**MAX Rev 1.1 Change : End**/
    }
    
    public PLUItemIfc getHotKeySItems(BusIfc bus, PLUCargoIfc cargo,
			MAXSearchCriteriaIfc inquiry) {
		// letter to be sent
		String letter = null;
		UtilityManagerIfc utility = (UtilityManagerIfc) bus
				.getManager(UtilityManagerIfc.TYPE);

		try {

			MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
					.create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
			String itemId = null;
			try {
				itemId = ((MAXHotKeysTransaction) hotKeysTransaction)
						.getItemIdFromHotKey(inquiry.getItemID());
			} catch (DataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.toString());
			}
			/**MAX Rev 1.1 Change : Start**/
			if(checkMultipleItems(itemId))
			{
				multipleHotKeyItemFlag = true;
				return null;
			}
			else if (itemId==null || itemId.equals(""))
			{
				hotKeyNotAssignedItemFlag = true;
				return null;	
			}
			else
				itemId = itemId.substring(0,itemId.length()-1);
			/**MAX Rev 1.1 Change : End**/
			if (itemId != null)
				inquiry.setItemID(itemId);

			PLUTransaction pluTransaction = null;

			pluTransaction = (PLUTransaction) DataTransactionFactory
					.create(DataTransactionKeys.PLU_TRANSACTION);

			// get list of items matching search criteria from database
			inquiry.setMaximumMatches(1);
			Locale locale = LocaleMap
					.getLocale(LocaleConstantsIfc.USER_INTERFACE);
// changes starts for code merging(commenting below line as setLocale() method is removed in base 14)           
            //inquiry.setLocale(locale);
 // Changes ends for code merging
			// set storeID
			inquiry.setStoreNumber(cargo.getStoreID());

			PLUItemIfc pluItems = pluTransaction.getPLUItem(inquiry);
			return pluItems;
		} catch (DataException de) {
			int errorCode = de.getErrorCode();
			logger.warn("ItemNo: " + inquiry.getItemID() + " \nItem Desc: "
					+ inquiry.getDescription() + " \nItem Dept: "
					+ inquiry.getDepartmentID() + "");

			logger.warn("Error: " + de.getMessage() + " \n " + de + "");

			cargo.setDataExceptionErrorCode(errorCode);

			// Don't think that args is used, but...
			String args[] = new String[1];

			// Set the appropriate letter to mail.
			// If the regular item lookup did return result,
			// still need to do store coupon lookup.
			// letter = CommonLetterIfc.RETRY;

			/* India Localization Changes - Item Creation Changes */
			// For Indian Scenario Item Creations hould be disabled in ORPOS.
			/* India Localization Changes - Item Creation Changes */
			return null;
		}
	}
	
	/**MAX Rev 1.1 Change : Start**/
    private boolean checkMultipleItems(String item)
    {
    	boolean flag = false;
    	int c=0;
    	for(int i=0;i<item.length();i++)
    		if(item.charAt(i)==';')
    			c++;
    	if(c>1)
    		flag = true;;
    	return flag;
    }
    
    protected void showErrorDialog(BusIfc bus, String error)
    {
    	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
    	DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(error);        
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
	/**MAX Rev 1.1 Change : End**/
    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  LookupItemSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
       Main to run a test..
       <P>
       @param  args    Command line parameters
    **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        MAXLookupItemSite obj = new MAXLookupItemSite();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
