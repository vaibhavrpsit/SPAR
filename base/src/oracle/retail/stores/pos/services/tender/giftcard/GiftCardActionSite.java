/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcard/GiftCardActionSite.java /rgbustores_13.4x_generic_branch/7 2011/09/16 10:48:10 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/15/11 - Fixed issues with completely creating of a refund
 *                         gift card tender from a issue/reload response
 *                         object.
 *    jswan     08/29/11 - Fixed Available Balance value on receipt for return
 *                         amount credited (reloaded) to an existing giftcard.
 *    cgreene   08/08/11 - Switch giftcard action to requestSubtype to avoid
 *                         clash with requestType.
 *    jswan     08/04/11 - Modified STATUS_CODE in tender attributes as an
 *                         Integer to make it consistent with all other classes
 *                         that manipulate this data element.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   03/18/10 - set the gift card status so that training mode wont
 *                         reactivate card
 *    abondala  01/03/10 - update header date
 *    sgu       01/13/09 - reformat string in decimal format before setting to
 *                         tender attributes
 *
 * ===========================================================================
 * $Log:
 *    10   360Commerce 1.9         5/29/2008 11:18:36 AM  Alan N. Sinton  CR
 *         31655: Code to allow refund of monies to multiple gift cards.  Code
 *          changes reviewed by Dan Baker.
 *    9    360Commerce 1.8         5/15/2008 4:21:39 PM   Sameer Thajudin
 *         Merged from 7.x
 *    8    360Commerce 1.7         8/23/2007 3:23:06 PM   Jack G. Swan    Fix
 *         for 28,330 - cannot tender with a gift card off-line
 *    7    360Commerce 1.6         7/26/2007 5:24:53 PM   Michael Boyd    Added
 *          checking for available balance compared to tender amount and if
 *           less then set tender amount to available amount for giftcards.
 *    6    360Commerce 1.5         4/25/2007 8:52:45 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    5    360Commerce 1.4         7/24/2006 6:44:27 PM   Keith L. Lesikar
 *         8419, Gift Card RDO
 *    4    360Commerce 1.3         2/15/2006 5:13:05 AM   Akhilashwar K. Gupta
 *         Modified to Fix CR 8235
 *    3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:52 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse
 *
 *   Revision 1.9.3  2006/02/14 21:39:22  Akhilashwar
 *   @scr 8235: Update Intial and Remaining balance of Gift Card in ejournal
 *
 *   Revision 1.9.2.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.9  2004/08/31 19:12:36  blj
 *   @scr 6855 - cleanup gift card credit code and fix defects found by PBY
 *
 *   Revision 1.8  2004/08/20 21:36:30  bwf
 *   @scr 6553 Make it so that credit/debit and gift card are the only buttons
 *                     enabled during swipe anytime.
 *
 *   Revision 1.7  2004/08/19 21:55:40  blj
 *   @scr 6855 - Removed old code and fixed some flow issues with gift card credit.
 *
 *   Revision 1.6  2004/07/22 22:38:41  bwf
 *   @scr 3676 Add tender display to ingenico.
 *
 *   Revision 1.5  2004/07/16 22:12:05  epd
 *   @scr 4268 Changing flows to add gift card credit
 *
 *   Revision 1.4  2004/06/17 16:26:17  blj
 *   @scr 5678 - code cleanup
 *
 *   Revision 1.3  2004/04/28 15:46:37  blj
 *   @scr 4603 - Fix gift card change due defects.
 *
 *   Revision 1.2  2004/04/22 13:28:15  tmorris
 *   @scr 4351 -Ensured that Gift Card's as a tender do not repeat error messages for valid entries.
 *
 *   Revision 1.1  2004/04/14 21:45:17  bwf
 *   @scr 4263 Decomposition of gift card.
 *
 *   Revision 1.5  2004/03/30 20:34:11  bwf
 *   @scr 4165 Gift Card Rework
 *
 *   Revision 1.4  2004/02/17 19:26:17  epd
 *   @scr 0
 *   Code cleanup. Returned unused local variables.
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.6   Jan 29 2004 12:00:38   blj
 * added gift card refund issue.
 *
 *    Rev 1.5   Dec 12 2003 07:42:16   blj
 * Code Review changes
 *
 *    Rev 1.4   Dec 03 2003 09:40:34   blj
 * Updated to use UIUtilities to display dialog screen.
 *
 *    Rev 1.3   Dec 01 2003 14:58:04   blj
 * cleaned up code for code review.
 *
 *    Rev 1.2   Nov 25 2003 18:15:44   blj
 * giftcard functional testing resolutions
 *
 *    Rev 1.1   Nov 20 2003 16:57:34   epd
 * updated to use new ADO Factory Complex
 *
 *    Rev 1.0   Nov 20 2003 07:02:20   blj
 * Initial revision.
 *
 *    Rev 1.0   Oct 24 2003 14:57:34   blj
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:06:52   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.giftcard;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Add Gift Card tender to the transaction or display error dialog.
 */
public class GiftCardActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8063802102919277795L;

    /**
     * Add gift card tender to the transaction.
     *
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        TenderADOIfc giftCardTender = cargo.getTenderADO();
        try
        {
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
            txnADO.addTender(giftCardTender);
            cargo.setLineDisplayTender(giftCardTender);

            // journal the added tender
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
            registerJournal.journal(giftCardTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);

            // mail a letter
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();

            // save tender in cargo
            cargo.setTenderADO(giftCardTender);
            if (error == TenderErrorCodeEnum.INVALID_QUANTITY)
            {
                displayDialog(bus, "InvalidGiftCardQuantity", null);
                return;
            }

            if (error == TenderErrorCodeEnum.INVALID_CARD_NUMBER)
            {
            	String[] args = {(String)giftCardTender.getTenderAttributes().
            	        get(TenderConstants.NUMBER)};
                displayDialog(bus, "InvalidGiftCard", args);
                return;
            }
        }
    }

    /**
     * This depart method just removes the swipe any time msr incase there is a
     * problem.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        cargo.setPreTenderMSRModel(null);
    }

    /**
     * This method displays the error messages.
     *
     * @param bus
     * @param message
     * @param args
     */
    protected void displayDialog(BusIfc bus, String message, String[] args)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, message, args);
    }
}
