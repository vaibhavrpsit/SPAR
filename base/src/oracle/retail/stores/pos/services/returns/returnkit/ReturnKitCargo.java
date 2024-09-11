/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnkit/ReturnKitCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    jswan     05/12/10 - Modify cargos for external order items return.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:52 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:54 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/03/12 19:36:48  epd
 *   @scr 3561 Updates for handling kit items in non-retrieved no receipt returns
 *
 *   Revision 1.1  2004/03/11 23:39:48  epd
 *   @scr 3561 New work to accommodate returning kit items
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnkit;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnExternalOrderItemsCargoIfc;
import oracle.retail.stores.pos.services.returns.returnitem.ReturnItemCargo;


/**
 * @author epd
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ReturnKitCargo extends ReturnItemCargo implements ReturnExternalOrderItemsCargoIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = -3051496356241670926L;
    
    /**
     * An array of kit components to return.
     */
    protected SaleReturnLineItemIfc[] kitComponents;
    
    /**
     * @return Returns the kitComponents.
     */
    public SaleReturnLineItemIfc[] getKitComponents()
    {
        return kitComponents;
    }
    /**
     * @param kitComponents The kitComponents to set.
     */
    public void setKitComponents(SaleReturnLineItemIfc[] kitComponents)
    {
        this.kitComponents = kitComponents;
    }
}
