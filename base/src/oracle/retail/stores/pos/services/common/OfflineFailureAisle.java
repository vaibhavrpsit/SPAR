/*===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/OfflineFailureAisle.java /main/1 2013/07/05 15:31:26 mkutiana Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* mkutiana    07/05/13 - Updated dialog screen for Role update when offline
*                        error.
* mkutiana    07/05/13 - Role update Offline failure dialog
* mkutiana    07/05/13 - Creation
* ===========================================================================
*/


package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.pos.services.common.DatabaseFailureAisle;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Aisle that is traversed when the DBError/Offline error. Displays Dialog indicating DB or Offline error
 */
public class OfflineFailureAisle extends DatabaseFailureAisle
{
    private static final long serialVersionUID = 7504306649427387761L;
    
    /**
     * resource id constant
     */
    public static final String RESOURCE_ID = "DatabaseOfflineError";
    
    /**
     * class name constant
     */
    public static final String LANENAME = "OfflineFailureAisle";
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.DatabaseFailureAisle#setModelDisplayText
     *      (BusIfc bus, DBErrorCargoIfc cargo, DialogBeanModel model)
     */
    @Override
    public void setModelDisplayText(BusIfc bus, DBErrorCargoIfc cargo, DialogBeanModel model)
    {
        model.setResourceID(RESOURCE_ID);
    }
}
