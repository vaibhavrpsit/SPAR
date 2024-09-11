/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/RetrieveStoreByCriteriaAisle.java /main/3 2013/01/07 13:42:32 hyin Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     10/02/14 - Add StoreMaximumMatches checking
* hyin        01/07/13 - search store from co; using store transformer.
* yiqzhao     07/02/12 - Read text from orderText bundle file and define screen
*                        names
* yiqzhao     06/05/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.manager.storesearch.StoreSearchManagerIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.storesearch.StoreSearchCriteria;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.LookupInventoryUtilities;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    This class gets the store criteria from the screen model, puts it in a
    StoreIfc model and uses it to retrieve the list of associated stores.
    <P>
    @version $Revision: /main/3 $
**/
//--------------------------------------------------------------------------

public class RetrieveStoreByCriteriaAisle extends PosLaneActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7316292829340901290L;

    /**
       class name constant
    **/
    public static final String LANENAME = "ValidatePickupDateAisle";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:14; $EKW:";

    //----------------------------------------------------------------------
    /**
        This class gets the store criteria from the screen model, puts it in a
        StoreIfc model and uses it to retrieve the list of associated stores.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        // Get cargo and ui manager
        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        StoreSearchManagerIfc storeSearchManager = (StoreSearchManagerIfc)bus.getManager(StoreSearchManagerIfc.TYPE);

        // Get model and locale
        DataInputBeanModel model = (DataInputBeanModel)ui.getModel(POSUIManagerIfc.STORE_SEARCH_FOR_SHIP);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        
        LocaleRequestor localeReq = new LocaleRequestor(locale);
        StoreSearchCriteria criteria = new StoreSearchCriteria();
        criteria.setLocaleReq(localeReq);

        // Set the data from the model on the lookup objects
        String field = (String)model.getValue(POSUIManagerIfc.STORE_NAME_FIELD);
        if (!Util.isEmpty(field))
        {
            criteria.setStoreName(field);
        }
        field = (String)model.getValue(POSUIManagerIfc.STORE_NUMBER_FIELD);
        if (!Util.isEmpty(field))
        {
            criteria.setStoreID(field);
        }
        field = (String)model.getValue(POSUIManagerIfc.CITY_FIELD);
        if (!Util.isEmpty(field))
        {
            criteria.setCity(field);
        }
        field = (String)model.getValue(POSUIManagerIfc.POSTAL_CODE_FIELD);
        if (!Util.isEmpty(field))
        {
            criteria.setPostalCode(field);
        }
        field = (String)model.getValue(POSUIManagerIfc.STATE_CODE_FIELD);
        if (!Util.isEmpty(field))
        {
            criteria.setState(field);
        }
        field = (String)model.getValue(POSUIManagerIfc.COUNTRY_CODE_FIELD);
        if (!Util.isEmpty(field))
        {
            criteria.setCountry(field);
        }

        // Retrieve the stores.
        try
        {
            StoreIfc[] stores = storeSearchManager.searchRetailStore(criteria);
            
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            if ( stores.length > LookupInventoryUtilities.getMaxMatch(pm) )
            {
                dialogTooManyMatches(bus);
            }
            else
            {
                cargo.setStoreGroup(stores);

                bus.mail(new Letter(CommonLetterIfc.SELECT), BusIfc.CURRENT);
            }
        }
        catch (DataException e)
        {
            if (e.getErrorCode() == DataException.NO_DATA)
            {
                dialogForNotFoundError(bus);
            }
            else
            {
                logger.info("Error looking up stores by criteria: ", e);
                dialogForRetrieveStoresError(bus);
            }
        }
    }

    /**
     * display the Not Found dialog
     * @param bus
     */
    protected void dialogForNotFoundError(BusIfc bus)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("INFO_NOT_FOUND_ERROR");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.NOT_FOUND);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    /**
     * Display error dialog
     * @param bus Service bus.
     */
    protected void dialogForRetrieveStoresError(BusIfc bus)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("ListOfStoresNotAvailable");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.ERROR);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    /**
     * display the Too Many Matches dialog
     * @param bus
     */
    protected void dialogTooManyMatches(BusIfc bus)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("MaxMatches");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.TOO_MANY_MATCHES_MESSAGE);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }    
}
