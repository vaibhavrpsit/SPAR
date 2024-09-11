/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/DeleteTenderActionSite.java /main/11 2011/02/16 09:13:30 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse   
 *
 *   Revision 1.2.4.2  2004/11/15 22:27:37  bwf
 *   @scr 7671 Create tender from rdo instead of class.  This is necessary because ADO's are not 1:1 with RDOs.
 *
 *   Revision 1.2.4.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.2  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 20 2003 16:57:30   epd
 * updated to use new ADO Factory Complex
 * 
 *    Rev 1.0   Nov 04 2003 11:17:42   epd
 * Initial revision.
 * 
 *    Rev 1.2   Oct 31 2003 14:15:42   epd
 * added call to journal deleted tender
 * 
 *    Rev 1.1   Oct 30 2003 20:43:42   epd
 * updated to use changed API
 * 
 *    Rev 1.0   Oct 23 2003 17:29:48   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 21 2003 10:29:16   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

/**
 * Deletes a selected tender from the transaction.
 */
public class DeleteTenderActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4422585528114330243L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
    	
        // Get the tender from the model and construct an ADO tender from it
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        TenderBeanModel model = (TenderBeanModel)ui.getModel();
        
        TenderLineItemIfc tenderToRemove = model.getTenderToDelete();
        // Create ADO tender
        TenderFactoryIfc factory = null;
        try
        {
            factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
        }
        catch (ADOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TenderADOIfc tenderADO = factory.createTender(tenderToRemove);
        ((ADO)tenderADO).fromLegacy(tenderToRemove);
        
        // get Current txn from cargo
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        txnADO.removeTender(tenderADO); 
        
        
        // Journal the removal of the tender
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
        registerJournal.journal(tenderADO, JournalFamilyEnum.TENDER, JournalActionEnum.DELETE);

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}
