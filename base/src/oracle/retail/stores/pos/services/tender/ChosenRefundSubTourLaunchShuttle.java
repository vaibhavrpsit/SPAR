/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/ChosenRefundSubTourLaunchShuttle.java /rgbustores_13.4x_generic_branch/2 2011/06/01 12:21:53 asinton Exp $
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
 |    5    360Commerce 1.4         4/30/2008 3:50:30 PM   Charles D. Baker CR
 |         31539 - Corrected ejournalling of special order gift card tenders.
 |         Code review by Jack Swan.
 |    4    360Commerce 1.3         4/29/2008 12:39:05 PM  Charles D. Baker CR
 |         31508 - Changed launch shuttle to store in cargo when gift card
 |         inquiry is done within the context of a transaction. Thus it will
 |         be available when needed in GetResponseSite. Code review by Jack
 |         Swan.
 |    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:20:15 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
 |   $
 |   Revision 1.3  2004/08/12 20:46:35  bwf
 |   @scr 6567, 6069 No longer have to swipe debit or credit for return if original
 |                               transaction tendered with one debit or credit.
 |
 |   Revision 1.2  2004/08/06 18:25:53  dcobb
 |   @scr 6655 Letters being checked in shuttle classes.
 |   Added check for Letter or letterName != null.
 |
 |   Revision 1.1  2004/04/13 16:30:07  bwf
 |   @scr 4263 Decomposition of store credit.
 |
 |   Revision 1.1  2004/04/08 19:30:59  bwf
 |   @scr 4263 Decomposition of Debit and Credit.
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;

//--------------------------------------------------------------------------
/**

 $Revision: /rgbustores_13.4x_generic_branch/2 $
 **/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ChosenRefundSubTourLaunchShuttle extends ChosenTenderLaunchShuttle
{
    /** previous letter **/
    protected String letter = null;

    /** Refund letter indicator string **/
    protected static final String REFUND = "Refund";

    /** Next tender from cargo **/
    protected TenderADOIfc nextTender = null;

    /** Indicates if transaction is in progrss **/
    protected boolean transactionInProgress = false;

    /** authorization transaction type for gift card funding */
    protected int authorizationTransactionType;

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.tender.ChosenTenderLaunchShuttle#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        super.load(bus);
        LetterIfc ltr = bus.getCurrentLetter();
        if (ltr != null)
        {
            String letterName = ltr.getName();
            if (letterName != null)
            {
                letter = letterName + "Refund";
            }
        }
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        nextTender = cargo.getNextTender();
        transactionInProgress = cargo.isTransactionInProgress() || cargo.getTenderableTransaction() != null;
        authorizationTransactionType = cargo.getAuthorizationTransactionType();
    }

    //----------------------------------------------------------------------
    /**
        This method just calls the super
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        TenderCargo childCargo = (TenderCargo)bus.getCargo();
        childCargo.setSubTourLetter(letter);   
        childCargo.setNextTender(nextTender);
        childCargo.setTransactionInProgress(transactionInProgress);
        childCargo.setAuthorizationTransactionType(authorizationTransactionType);
    }    
}
