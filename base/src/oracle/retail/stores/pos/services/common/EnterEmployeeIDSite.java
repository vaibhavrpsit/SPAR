/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/EnterEmployeeIDSite.java /main/13 2012/06/27 09:40:06 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/27/12 - Ensure logo screen is displayed on CPOI when
 *                         parameter IdentifySalesAssociateEveryTransaction=Y
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:04:50   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:00:04   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

// foundation imports
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the SALES_ASSOCIATE_IDENTIFICATION screen.
    <p>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class EnterEmployeeIDSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
        Displays the SALES_ASSOCIATE_IDENTIFICATION screen.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        SaleCargo cargo = (SaleCargo) bus.getCargo();  
        WorkstationIfc workstation = cargo.getRegister().getWorkstation();
        PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
        paymentManager.clearSwipeAheadData(workstation);
        paymentManager.showLogo(workstation);
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.SALES_ASSOCIATE_IDENTIFICATION, new POSBaseBeanModel());
       
    }

}
