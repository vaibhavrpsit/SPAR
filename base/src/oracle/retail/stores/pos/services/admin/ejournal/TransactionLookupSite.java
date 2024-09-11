/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/ejournal/TransactionLookupSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/27/10 - default dates on the Ejournal search screen are
 *                         system dates as we are comparing these dates with
 *                         the column TS_JRL_BGN in JL_ENR which is a timestamp
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:15 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/09 20:59:24  jdeleau
 *   @scr 6077 If the dates on the EJ screen are both blank dont throw an 
 *   error message, instead use the  business date
 *
 *   Revision 1.3  2004/02/12 16:48:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:07  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 24 2003 06:32:58   rrn
 * Make default start and end search dates be the business date.
 * Resolution for 3646: EJournal - default search date should be business date not system date
 * 
 *    Rev 1.1   Dec 17 2003 09:21:32   rrn
 * Set default register ID for display and ask check if Register Number field
 * should be editable or not.
 * Resolution for 3611: EJournal to database
 * 
 *    Rev 1.0   Aug 29 2003 15:52:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 16 2002 17:12:30   baa
 * remove close drawer message for training mode
 * Resolution for POS SCR-1750: Training Mode Close Cash Drawer Message
 * 
 *    Rev 1.0   Apr 29 2002 15:40:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:12   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.ejournal;

// foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.TransactionLookupBeanModel;

//------------------------------------------------------------------------------
/**
    Initial site for electronic journal. The user will
    input optional search criteria and hit the search
    key to view transaction data.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class TransactionLookupSite extends PosSiteActionAdapter
{

    /**
       class name constant
    **/
    public static final String SITENAME = "TransactionLookupSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Displays the transaction lookup screen
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        JournalManagerIfc jm = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        
        EJournalCargo cargo = (EJournalCargo) bus.getCargo();
        TransactionLookupBeanModel model = cargo.getBeanModel();
        cargo.setNewSearch(true);
        
        if (model == null)
        {
        
            model = new TransactionLookupBeanModel();
        
            // Set default search date to *System* date
            model.setStartDate(DomainGateway.getFactory().getEYSDateInstance());
            model.setEndDate(DomainGateway.getFactory().getEYSDateInstance());
            model.setupLinkedDates(true, cargo.getBusinessDate());
            
            //Set the default time for the search to 'Midnight to Midnight'
            model.getStartTime().setHour(0);
            model.getStartTime().setMinute(0);
            model.getStartTime().setSecond(0);
            model.getEndTime().setHour(23);
            model.getEndTime().setMinute(59);
            model.getEndTime().setSecond(59);

            model.setRegisterNumber(cargo.getRegisterID());
    
        }
       
        //Determine whether the Register Number field should be editable
        if( jm.isSearchableByRegisterNumber() == false )
        {
            model.setAllowRegisterNumberField( false );
        }

        ui.showScreen(POSUIManagerIfc.FIND_TRANSACTION, model);

    }
}
