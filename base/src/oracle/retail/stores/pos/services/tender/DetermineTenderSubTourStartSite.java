/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/DetermineTenderSubTourStartSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/23/2008 4:45:42 AM   Chengegowda Venkatesh
 *          PABP 30 - Originating Point checkin for BO and CO audit Log
 *    3    360Commerce 1.2         3/31/2005 4:27:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:56 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:34 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/13 16:30:07  bwf
 *   @scr 4263 Decomposition of store credit.
 *
 *   Revision 1.1  2004/04/08 19:30:59  bwf
 *   @scr 4263 Decomposition of Debit and Credit.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;

//--------------------------------------------------------------------------
/**
     This class determines the correct start position.
     $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class DetermineTenderSubTourStartSite extends PosSiteActionAdapter
{
    //----------------------------------------------------------------------
    /**
        This method just mails the correct letter.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        EventOriginatorInfoBean.setEventOriginator("DetermineTenderSubTourStartSite.arrive");
        bus.mail(new Letter(cargo.getSubTourLetter()), BusIfc.CURRENT);
    }    
}
