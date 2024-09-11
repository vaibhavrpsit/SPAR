/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/ChosenTenderSubTourLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 |    6    360Commerce 1.5         4/30/2008 3:50:30 PM   Charles D. Baker CR
 |         31539 - Corrected ejournalling of special order gift card tenders.
 |         Code review by Jack Swan.
 |    5    360Commerce 1.4         4/27/2008 5:28:27 PM   Charles D. Baker CR
 |         31482 - Updated the journalResponse method of GetResponseSite to
 |         intelligently journal entries with the appropriate journal type
 |         (Trans or Not Trans). Code Review by Tony Zgarba.
 |    4    360Commerce 1.3         4/24/2008 5:30:04 PM   Charles D. Baker CR
 |         31452 - Attached current transaction to gift card inquiry journal
 |         entry when one is available. Code review by Anda Cadar.
 |    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:20:15 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
 |   $
 |   Revision 1.3  2004/08/06 18:25:53  dcobb
 |   @scr 6655 Letters being checked in shuttle classes.
 |   Added check for Letter or letterName != null.
 |
 |   Revision 1.2  2004/05/12 21:22:35  rzurga
 |   @scr 5030 Re-enabling of the swipe anytime
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

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;

//--------------------------------------------------------------------------
/**
     This class is used for credit and debit tender launch shuttle.
     $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class ChosenTenderSubTourLaunchShuttle extends ChosenTenderLaunchShuttle
{
    /** previous letter **/
    protected String letter = null;
    
    /** Indicates if transaction is in progrss **/
    protected boolean transactionInProgress = false;
    
    //----------------------------------------------------------------------
    /**
        This method calls the super method and then checks the previous 
        letter in order to let the creditdebit service which start site to
        use.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        LetterIfc ltr = bus.getCurrentLetter();
        if (ltr != null)
        {
            letter = ltr.getName();
        }
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        transactionInProgress = cargo.isTransactionInProgress() || cargo.getTenderableTransaction() != null;
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
        System.out.println("childCargo "+childCargo.getCurrentTransactionADO());
        childCargo.setSubTourLetter(letter);        
        childCargo.setPreTenderMSRModel(callingCargo.getPreTenderMSRModel());
        childCargo.setTransactionInProgress(transactionInProgress);
    }    
}
