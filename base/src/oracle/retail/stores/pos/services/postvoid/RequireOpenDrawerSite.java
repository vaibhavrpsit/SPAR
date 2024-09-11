/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/RequireOpenDrawerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:50 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:44 PM  Robert Pearse   
 *
 *   Revision 1.2.4.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.2  2004/02/12 16:48:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 19 2003 13:56:28   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.transaction.VoidTransactionADO;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This class determines where the flow will proceed to.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class RequireOpenDrawerSite extends PosSiteActionAdapter
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
        This method performs some journaling and determines the next site to
        proceed to.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        VoidCargo cargo = (VoidCargo)bus.getCargo();
        VoidTransactionADO voidTxn = (VoidTransactionADO) cargo.getCurrentTransactionADO();
        
        JournalFactoryIfc jrnlFact = null;
        try
        {
            jrnlFact = JournalFactory.getInstance();
        }
        catch (ADOException e)
        {
            logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
        }
        RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
        // Journal voided tender totals
        registerJournal.journal(voidTxn, 
                                JournalFamilyEnum.TENDER, 
                                JournalActionEnum.VOID_TOTAL);
                                
        String letterName = "Success";
        if (voidTxn.openDrawerRequired())
        {
            letterName = "Open";
        } 
        bus.mail(new Letter(letterName), BusIfc.CURRENT); 
    }
}
