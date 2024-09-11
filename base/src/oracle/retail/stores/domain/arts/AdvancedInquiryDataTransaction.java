/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/AdvancedInquiryDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  10/16/08 - Advance Item Inquiry
 *    mchellap  10/16/08 - Adance Item Inquiry
 *
 *
 * ===========================================================================
 */

package oracle.retail.stores.domain.arts;

// foundation imports
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.stock.ItemColorIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.ItemStyleIfc;
import oracle.retail.stores.domain.stock.ItemTypeIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

//-------------------------------------------------------------------------
/**
    This class handles the DataTransaction behavior for parameter (system setting)
    updates.
**/
//-------------------------------------------------------------------------
public class AdvancedInquiryDataTransaction extends DataTransaction
{

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "AdvancedInquiryDataTransaction";

    /**
     * default constructor
     */
    public AdvancedInquiryDataTransaction()
    {
        super(dataCommandName);
    }

    //---------------------------------------------------------------------
    /**
        Returns the list of available Item types.
        <p>
        @exception DataException is thrown if the itemTypeList cannot be found.

    **/
    //---------------------------------------------------------------------
     public ItemTypeIfc[] getItemTypeList(LocaleRequestor localeRequestor) throws DataException
     {

        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("ReadItemTypes");
        dataAction.setDataObject(localeRequestor);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        ItemTypeIfc[] itemTypeList =(ItemTypeIfc[]) getDataManager().execute(this);


        return (itemTypeList);
     }

     //---------------------------------------------------------------------
     /**
         Returns the list of available styles.
         <p>
         @exception DataException is thrown if the styleList cannot be found.

     **/
     //---------------------------------------------------------------------
      public ItemStyleIfc[] getStyleList(LocaleRequestor localeRequestor) throws DataException
      {

         DataAction dataAction = new DataAction();
         dataAction.setDataOperationName("ReadItemStyles");
         dataAction.setDataObject(localeRequestor);

         DataActionIfc[] dataActions = new DataActionIfc[1];
         dataActions[0] = dataAction;


         setDataActions(dataActions);
         ItemStyleIfc[] styleList =(ItemStyleIfc[]) getDataManager().execute(this);


         return (styleList);
      }

      //---------------------------------------------------------------------
      /**
          Returns the list of available styles.
          <p>
          @exception DataException is thrown if the colorList cannot be found.

      **/
      //---------------------------------------------------------------------
       public ItemColorIfc[] getColorList(LocaleRequestor localeRequestor) throws DataException
       {

          DataAction dataAction = new DataAction();
          dataAction.setDataOperationName("ReadItemColors");
          dataAction.setDataObject(localeRequestor);


          DataActionIfc[] dataActions = new DataActionIfc[1];
          dataActions[0] = dataAction;


          setDataActions(dataActions);
          ItemColorIfc[] colorList =(ItemColorIfc[]) getDataManager().execute(this);


          return (colorList);
       }

       //---------------------------------------------------------------------
       /**
           Returns the list of available sizes.
           <p>
           @exception DataException is thrown if the sizeList cannot be found.

       **/
       //---------------------------------------------------------------------
        public ItemSizeIfc[] getSizeList(LocaleRequestor localeRequestor) throws DataException
        {

           DataAction dataAction = new DataAction();
           dataAction.setDataOperationName("ReadItemSizes");
           dataAction.setDataObject(localeRequestor);

           DataActionIfc[] dataActions = new DataActionIfc[1];
           dataActions[0] = dataAction;


           setDataActions(dataActions);
           ItemSizeIfc[] sizeList =(ItemSizeIfc[]) getDataManager().execute(this);


           return (sizeList);
        }

}                                           // end class ParameterTransaction
