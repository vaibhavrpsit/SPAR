/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/TillEnteredAisle.java /main/12 2014/06/25 15:23:30 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/24/14 - Forward port fix to prevent 2 or more online
 *                         registers from opening the same till when opening
 *                         simultaneously.
 *    subrdey   08/01/13 - Making the till id to uppercase to have consistency
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/4/2007 11:01:28 PM  Robinson Joseph code
 *         modified to trim the till id.
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:04 PM  Robert Pearse   
 *
 *   Revision 1.12.2.2  2004/11/09 17:00:45  rsachdeva
 *   @scr 7654 Till Open  when database is offline
 *
 *   Revision 1.12.2.1  2004/11/09 16:53:49  rsachdeva
 *   @scr 7654 Till Open does not always work when database is offline
 *
 *   Revision 1.12  2004/08/12 15:17:16  dcobb
 *   @scr 6792 TillStatusSite - cargo contains outdated register/till data
 *   Added java doc.
 *
 *   Revision 1.11  2004/07/07 18:27:11  dcobb
 *   @scr 1734 Wrong error message when attempt to open another till in reg acct.
 *   Fixed in CheckTillStatusSite. Moved deprecated TillOpenCargo to the deprecation tree and imported new TillCargo from _360commerce tree..
 *
 *   Revision 1.10  2004/06/25 22:36:46  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Code cleanup.
 *
 *   Revision 1.9  2004/06/24 17:23:11  dcobb
 *   @scr 5263 - Can't resume suspended till.
 *   Backed out khassen changes.
 *
 *   Revision 1.8  2004/06/23 14:24:07  khassen
 *   @scr 5263 - Updated register to reflect current status of till.
 *
 *   Revision 1.7  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/13 12:57:46  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:27:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:40   msg
 * Initial revision.
 * 
 *    Rev 1.3   06 Mar 2002 13:34:40   epd
 * fixed bug, based on selecting no data
 * Resolution for POS SCR-953: Till options - if database error occurs, system should display a message  - no message is displayed
 * 
 *    Rev 1.2   06 Mar 2002 13:05:56   epd
 * modified to display dialog on database error
 * Resolution for POS SCR-953: Till options - if database error occurs, system should display a message  - no message is displayed
 * 
 *    Rev 1.1   15 Nov 2001 09:25:38   epd
 * Fixed a MAJOR bug.  You will now not be able to close and reopen a till on the same business day on ANY register (assuming there are no database errors).
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:18:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillopen.TillOpenCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    This aisle is traversed when the user has entered the till ID.
    @version $Revision: /main/12 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class TillEnteredAisle extends PosLaneActionAdapter
{

    public static final String revisionNumber = "$Revision: /main/12 $";

    //--------------------------------------------------------------------------
    /**
        Retrieve the till ID from the user interface and verify that:
        1) It is a valid till ID.
        2) The till ID has not been opened on any register for this business day.
        if the till ID is OK, create the TIllIfc object, associate the operator  
        with the till and save the tillID and the TillIfc object in the cargo.
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        Letter letter = new Letter (CommonLetterIfc.CONTINUE);
        TillIfc t = DomainGateway.getFactory().getTillInstance();
        boolean bOk = true;

        // Local reference to till cargo
        TillOpenCargo cargo = (TillOpenCargo) bus.getCargo();

        // Local reference to register
        RegisterIfc register = (RegisterIfc) cargo.getRegister().clone();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Get Input (till id) from UI
        String tillId = ui.getInput().trim().toUpperCase();

        // Is this a valid till ID
        if (t.isIdValid(tillId) != true)
        {
            letter = new Letter(CommonLetterIfc.TILL_ID_ERROR);
            bOk = false;
        }

        boolean mailLetter = true;
        // make sure this till has not been opened on any register this business day
        try
        {
            FinancialTotalsDataTransaction dt = null;
            
            dt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
            
            TillIfc dbTill = dt.readTillStatus(register.getWorkstation().getStore(),
                                               tillId);

            cargo.setTillIdVerified(true);

            // compare business dates of store and till retrieved from database
            // use equals() because business date only uses calendar date and not time
            if (dbTill.getBusinessDate().dateValue().equals(register.getBusinessDate().dateValue()))
            {
                letter = new Letter(CommonLetterIfc.TILL_OPEN_ERROR);
                cargo.setTillFatalError();
                bOk = false;
            }
        }
        catch (DataException e)
        {
            // Finding no data is not a problem, so we don't handle that condition.
            if (e.getErrorCode() != DataException.NO_DATA)
            {
                if ((bOk) && (register.getTillByID(tillId) != null))
                {
                    letter = new Letter(CommonLetterIfc.TILL_OPEN_ERROR);
                    cargo.setTillFatalError();
                    bOk = false;
                }
                if(bOk)
                {
                    mailLetter = false;
                    cargo.setTillIdVerified(false);
                    
                    DialogBeanModel model = new DialogBeanModel();
                    model.setType(DialogScreensIfc.ERROR);
                    model.setResourceID("TillIdNotVerified");
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                }
            }
        }

        // Make sure till has not been added (opened)
        // in this register.
        if ((bOk) &&
            (register.getTillByID(tillId) != null))
        {
            letter = new Letter(CommonLetterIfc.TILL_OPEN_ERROR);
            cargo.setTillFatalError();
            bOk = false;
        }

        // If everything ok so far, set till id, add operator
        // to till and add till to cargo and to register.
        if (bOk)
        {
            t.setTillID(tillId);
            t.addCashier(cargo.getOperator());
            cargo.setTillID(tillId);
            cargo.setTill(t);
        }

        if (mailLetter)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

}
