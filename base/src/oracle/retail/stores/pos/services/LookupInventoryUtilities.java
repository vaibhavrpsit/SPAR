/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/LookupInventoryUtilities.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzhao 10/02/14 - Add StoreMaximumMatches checking
 *    cgreen  05/26/10 - convert to oracle packaging
 *    cgreen  04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen  04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda  01/03/10 - update header date
 *
 * ===========================================================================

     $Log:
      1    360Commerce 1.0         11/22/2007 10:57:19 PM Naveen Ganesh   
     $
     Revision 1.19.2.1  2004/12/08 02:03:19  mweis
     @scr 7804 Cannot find inventory for items with sizes (because the default implementation does not keep track of sizes).

     Revision 1.19  2004/06/03 14:47:43  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.18  2004/04/22 17:34:37  lzhao
     @scr 4284: make the methods public.

     Revision 1.17  2004/04/20 13:17:05  tmorris
     @scr 4332 -Sorted imports

     Revision 1.16  2004/04/17 17:59:28  tmorris
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.15  2004/04/14 15:17:10  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.14  2004/04/12 15:39:29  lzhao
     @scr 4293, 4299, 4310, 4330: inventory inquiry.

     Revision 1.13  2004/04/09 17:57:44  lzhao
     @scr 4299: return to multistore screen if info not found.

     Revision 1.12  2004/04/08 22:14:55  cdb
     @scr 4206 Cleaned up class headers for logs and revisions.

     Revision 1.11  2004/03/26 21:18:19  cdb
     @scr 4204 Removing Tabs.

     Revision 1.10  2004/03/26 18:44:48  lzhao
     @scr 3840 Fix database offline in inventory inquiry.

     Revision 1.9  2004/03/19 22:59:06  lzhao
     @scr #3840 Inventory Inquiry    
     remove tab and fix item size cannot be showed once change the parameter from No to Yes.

     Revision 1.8  2004/03/19 13:53:20  lzhao
     @scr 3840 Inventory Inquiry
     Fix setScanData or get para model problem.

     Revision 1.7  2004/03/19 00:59:27  lzhao
     @scr 3840 Inquiry Options: Inventory Inquiry    
     for updating getScanData or format comments.

     Revision 1.6  2004/03/18 18:33:04  lzhao
     @scr 3840 Inquiry Options: Inventory Inquiry
     Code Review Follow Up.

     Revision 1.4  2004/03/12 23:06:03  lzhao
     @scr #3840 Inquiry Options: Inventory Inquiry

     Revision 1.3  2004/03/10 17:51:42  lzhao
     @scr 3840 Inquiry Options Enhancement
     add scan, change method name, etc

     Revision 1.2  2004/03/10 15:49:33  lzhao
     @scr 3840     InquiryOptions: Inventory Inquiry
     Add comments.

     Revision 1.1  2004/03/10 00:11:45  lzhao
     @scr 3840 InquiryOptions: Inventory Inquiry

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services;

import java.util.Locale;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataManagerMsgIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.StoreDirectoryDataTransaction;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
 * provide the methods which are used in the service.
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class LookupInventoryUtilities 
{
    /**
     revision number
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     using store maximum match parameter
     **/
    protected static final String  STORE_MAXIMUM_MATCHES  = "StoreMaximumMatches";
    /**
     using require size input parameter
     **/
    protected static final String  SIZE_INPUT_FIELD  = "SizeInputField";
    /**
     using continue to search when item id not found parameter
     **/
    protected static final String  INVENTORY_NOT_FOUND_FIELD  = "InventoryNotFound";
    /**
     base logger
     **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.LookupInventoryUtilities.class);
    /**
     resource ID for Invalid Number Error dialog
     **/
    protected static final String INVALID_NUMBER_ERROR = "InvalidNumberError";
    /**
     tag for Item Number in resuorce bundle
     **/
    protected static final String ITEM_NUMBER_TAG = "ItemNumber";
    /**
     default text for Item Number in resource bundle
     **/
    protected static final String ITEM_NUMBER_TEXT = "Item Number";
    /**
     parameter manager reference, set at the first site of the service
     **/
    protected static ParameterManagerIfc pm      = null;
    /**
     utility manager reference, set at the first site of the service
     **/
    protected static UtilityManagerIfc   utility = null;
    //------------------------------------------------------------------------------
    /**
     * set parameter manager and utility manager for later to use.
     * @param paraManger
     * @param utilityManger
     */
    //------------------------------------------------------------------------------
    public static void utilityInit(ParameterManagerIfc paraManger, UtilityManagerIfc utilityManger)
    {
        pm = paraManger;
        utility = utilityManger;
    }
    //------------------------------------------------------------------------------
    /**
     * get inputSize parameter from parameter file
     * @return boolean the value of the parameter
     */
    //------------------------------------------------------------------------------
    public static boolean inputSize()
    {
        Boolean sizeInput = new Boolean(false);
        
        try
        {
            sizeInput = pm.getBooleanValue(SIZE_INPUT_FIELD);            
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }    
        return sizeInput.booleanValue();
    }

    //------------------------------------------------------------------------------
    /**
     * get continueSearch parameter from parameter file
     * @return boolean the value of the parameter
     */
    //------------------------------------------------------------------------------
    protected static boolean continueSearch()
    {
        Boolean continueSearch = new Boolean(false);
        
        try
        {
            continueSearch = pm.getBooleanValue(INVENTORY_NOT_FOUND_FIELD);            
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }    
        return continueSearch.booleanValue();
    }

    //------------------------------------------------------------------------------
    /**
     * get maxMatch parameter from parameter file
     * @return int the value of the parameter
     */
    //------------------------------------------------------------------------------
    public static int getMaxMatch(ParameterManagerIfc pm)
    {
        int maxMatch = 0;
        try
        {
            maxMatch = new Integer(pm.getStringValue(STORE_MAXIMUM_MATCHES)).intValue();            
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }    
        return maxMatch;
    }

    /**
     * search store table in the database to verify the store exists.
     * @param String storeID
     * @return int indicate the error, NONE is no error.
     * @deprecated As of release 13.1. No callers.
     */
   protected static int isValidStore(String storeID)
    {
        StoreDirectoryDataTransaction transaction = 
            (StoreDirectoryDataTransaction) DataTransactionFactory.create(DataTransactionKeys.STORE_DIRECTORY_DATA_TRANSACTION);
        
        try
        {
            StoreIfc stores[] = transaction.searchStoreDirectory(null);
            for ( int i = 0; i < stores.length; i++ )
            {
                if ( stores[i].getStoreID().equals(storeID) )
                {
                    return DataException.NONE; // no error
                }
            }
            return DataException.NO_DATA;
        }
        catch (DataException exception)
        {
            return exception.getErrorCode();
        }        
    }
    
    //------------------------------------------------------------------------------
    /**
     * get dialog bean model for the case of item id not found in the database
     * @return DialogBeanModel
     */
    //------------------------------------------------------------------------------
    public static DialogBeanModel getModelItemNotFounds()
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setType(DialogScreensIfc.CONTINUE_CANCEL);
//        dialogModel.setResourceID("LOCAL_INVENTORY_INFO_NOT_FOUND_ERROR");
        dialogModel.setResourceID("KandruError");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE,CommonLetterIfc.CONTINUE);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,CommonLetterIfc.RETRY); 
        return dialogModel;
    }

    //------------------------------------------------------------------------------
    /**
     * get dialog bean model for the case of the item is not in the inventory
     * @return DialogBeanModel
     */
    //------------------------------------------------------------------------------
    protected static DialogBeanModel getModelInventoryNotFound()
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.RETRY);
        return dialogModel;
    }
    
    //------------------------------------------------------------------------------
    /**
     * get dialog bean model for the case of the item is not in the inventory
     * @param cargo InventoryLookupCargo
     * @return DialogBeanModel
     */
    //------------------------------------------------------------------------------
//    protected static DialogBeanModel getModelInventoryNotFound(InventoryLookupCargo cargo)
//    {
//        DialogBeanModel dialogModel = new DialogBeanModel();
//        dialogModel.setType(DialogScreensIfc.ERROR);
//        dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
//        if ( cargo.getStoreSelectType()==InventoryLookupCargo.SPECIFIC_STORE_INQUIRY )
//            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
//        else
//            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "MultiStoreRetry");
//        
//        return dialogModel;
//    }
    
    //------------------------------------------------------------------------------
    /**
     * get dialog bean model for the case of the item has too many match in the inventory
     * @return DialogBeanModel
     */
    //------------------------------------------------------------------------------
    protected static DialogBeanModel getModelTooManyMatches()
    {  
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setResourceID("TooManyMatches");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.RETRY);
        
        return dialogModel;
    }
    
    //------------------------------------------------------------------------------
    /**
     * get dialog bean model for the case of inventory offline
     * @return DialogBeanModel
     */
    //------------------------------------------------------------------------------
    protected static DialogBeanModel getModelInventoryOffline()
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setResourceID("InventoryOffline");
        //dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
        
        return dialogModel;
    }
    
    //------------------------------------------------------------------------------
    /**
     * get dialog bean model for the case of database access error
     * @param int errorCode 
     * @return DialogBeanModel
     */
    //------------------------------------------------------------------------------
    protected static DialogBeanModel getModelDatabaseError(int errorCode)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setResourceID("DATABASE_ERROR");
        String msg[] = new String[2];
        msg[0] = utility.getErrorCodeString(errorCode);
        msg[1] = 
        utility.retrieveDialogText("DATABASE_ERROR.Contact", 
                DataManagerMsgIfc.CONTACT);                         
        dialogModel.setArgs(msg);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);
        
        return dialogModel;
    }
    
    //------------------------------------------------------------------------------
    /**
     * get dialog bean model for the case of store not found
     * @return DialogBeanModel
     */
    //------------------------------------------------------------------------------
    protected static DialogBeanModel getModelStoreNotFounds()
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setResourceID("InvalidStoreNumberError");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "InvalidStoreNumber");
                
        return dialogModel;
    }   
    //--------------------------------------------------------------------------
    /**
     Shows the invalid item number dialog screen.
     <P>
     @param ui the UI manager
     @return DialogBeanModel
     **/
    //--------------------------------------------------------------------------
    protected static DialogBeanModel getInvalidNumberDialog(POSUIManagerIfc ui)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(INVALID_NUMBER_ERROR);
        dialogModel.setType(DialogScreensIfc.ERROR);
        String[] args = new String[1];
        String arg = utility.retrieveDialogText(ITEM_NUMBER_TAG, ITEM_NUMBER_TEXT);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        args[0] = arg.toLowerCase(locale);
        dialogModel.setArgs(args);
        return dialogModel;
        
    }
    
    //--------------------------------------------------------------------------
    /**
     * get an error dialog when doing search store id
     * @param int error code return from accessing db
     * @return StoreInStockSearchCriteriaIfc
     */
    //--------------------------------------------------------------------------
    public static DialogBeanModel getErrorDialogInStoreIDCheck(int errorCode)
    {
        DialogBeanModel dialogModel = null;
        switch ( errorCode )
        {
            case DataException.NO_DATA:
                dialogModel =  LookupInventoryUtilities.getModelStoreNotFounds();
                break;
            case DataException.DATA_FORMAT:
            case DataException.CONNECTION_ERROR:
                dialogModel =  LookupInventoryUtilities.getModelInventoryOffline();
                break;
            default:
                dialogModel =  LookupInventoryUtilities.getModelDatabaseError(errorCode);
        }
        return dialogModel;
    }
    

    //------------------------------------------------------------------------------
    /**
     * find PLUItem from the database
     * @param cargo inventory lookup cargo
     * @param locale Locale 
     * @return PLUItemIfc
     * @throws DataException
     */
    //------------------------------------------------------------------------------
//    protected static PLUItemIfc validateItemID(InventoryLookupCargo cargo, 
//                                               Locale locale) throws DataException
//    {
//        PLUTransaction pluTransaction = null;
//        
//        pluTransaction = (PLUTransaction) DataTransactionFactory.create(DataTransactionKeys.PLU_TRANSACTION);
//        
//        // create item search criteria            
//        SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
//        inquiry.setLocale(locale);
//        inquiry.setItemID(cargo.getItemID());
//        inquiry.setStoreNumber(cargo.getRegister().getWorkstation().getStore().getStoreID());
//        
//        // call JdbcPLUOperation/FFPLULookupOperation to find the item in
//        // TABLE_POS_IDENTITY and TABLE_ITEM which contains the item id in the store
//        return pluTransaction.getPLUItem(inquiry);
//    }
}
