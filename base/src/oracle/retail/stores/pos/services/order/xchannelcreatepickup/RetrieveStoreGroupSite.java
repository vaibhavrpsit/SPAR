/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/RetrieveStoreGroupSite.java /main/1 2012/05/02 14:07:48 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/18/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

// foundation imports
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.StoreDataTransaction;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    This class retrieves the group of stores associated with the current
    store. 
    be processed one at at time.
    @version $Revision: /main/1 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class RetrieveStoreGroupSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "GetStoreGroupsSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    //--------------------------------------------------------------------------
    /**
        This method retrieves the group of stores associated with the current
        store. 
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        String storeID = cargo.getStoreStatus().getStore().getStoreID();
        cargo.setListFromStoreGroup(true);

        try
        {
            StoreDataTransaction dt = null;
            
            dt = (StoreDataTransaction) DataTransactionFactory.create(DataTransactionKeys.STORE_DATA_TRANSACTION);
            LocaleRequestor requestor = new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            StoreIfc[] stores = dt.readStoresInGroupsByStoreID(storeID, requestor);
            cargo.setStoreGroup(stores);

            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (DataException e)
        {
            logger.info("Store status lookup error: \n" + e + "");
            if (e.getErrorCode() == DataException.NO_DATA)
            {
                // If the store group read gets nothing back put the 
                // the current store in the list.
                StoreIfc[] stores = new StoreIfc[1];
                stores[0] = cargo.getStoreStatus().getStore();
                cargo.setStoreGroup(stores);
                bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
            }
            else
            {
                dialogForRetrieveStoresError(bus);
            }
        }
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
}