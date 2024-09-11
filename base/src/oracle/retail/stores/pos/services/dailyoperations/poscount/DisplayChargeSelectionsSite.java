/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/DisplayChargeSelectionsSite.java /rgbustores_13.4x_generic_branch/1 2011/07/28 17:17:16 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   07/28/11 - removed use of CreditCardTypes parameter. removed as
 *                         part of 13.4 Advanced Payment Foundation.
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    jswan     12/14/10 - Fix issues found during retest.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     3    360Commerce 1.2         3/31/2005 4:27:47 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse
 *    $
 *    Revision 1.5  2004/09/23 00:07:13  kmcbride
 *    @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *    Revision 1.4  2004/05/26 21:33:10  dcobb
 *    @scr 4302 Correct compiler warnings
 *
 *    Revision 1.3  2004/02/12 16:49:38  mcs
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 21:45:40  rhafernik
 *    @scr 0 Log4J conversion and code cleanup
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:56:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Jul 03 2003 10:37:12   RSachdeva
 * Credit Amount Entered to be displayed
 * Resolution for POS SCR-2991: Credit amounts missing from Select Credit screen
 *
 *    Rev 1.5   Apr 18 2003 17:15:24   baa
 * fix error with buttons not displaying
 * Resolution for POS SCR-2170: Missing property names in bundles
 *
 *    Rev 1.4   Apr 10 2003 17:21:26   bwf
 * Now pass in a list of cardTypes to SummaryChargeMenuBeanModel.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.3   Nov 26 2002 17:40:04   kmorneau
 * fix blind close to properly display expected values in expected places
 * Resolution for 1824: Blind Close
 *
 *    Rev 1.2   Nov 18 2002 13:38:00   kmorneau
 * added capability to display expected amounts for Blind Close
 * Resolution for 1824: Blind Close
 *
 *    Rev 1.1   Sep 23 2002 12:07:36   kmorneau
 * added dynamic button creation and disabling of cards which are not accepted
 * Resolution for 1815: Credit Card Types Accepted
 *
 *    Rev 1.0   Apr 29 2002 15:30:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:14:22   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:06   msg
 * Initial revision.
 *
 *    Rev 1.0   02 Jan 2002 15:42:24   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// foundation imports
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.domain.utility.Card;
import oracle.retail.stores.domain.utility.CardType;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryChargeMenuBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;

//------------------------------------------------------------------------------
/**
     This class builds the list of Credit types that the Cashier must
     count.<P>

     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayChargeSelectionsSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -964980052828123128L;


    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       Site name for logging
    **/
    public static final String SITENAME = "DisplayChargeSelectionsSite";

    //--------------------------------------------------------------------------
    /**
       This method builds the list of Credit types and calls the UI to display
       the screen.<p>

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // Get the cargo
        PosCountCargo cargo   = (PosCountCargo)bus.getCargo();

        /*
         * Ask the UI Manager to display the screen
         */
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        Dispatcher d = Dispatcher.getDispatcher();
        UtilityManager util = (UtilityManager) d.getManager(UtilityManagerIfc.TYPE);
        CardType cardType =
            util.getConfiguredCardTypeInstance();
        List tempCardList = cardType.getCardList();
        SummaryChargeMenuBeanModel scmbm = new SummaryChargeMenuBeanModel(tempCardList);
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();

        // Get the SummaryCountBeanModel from the cargo ONLY IF we have already set it (i.e. This is not
        // the first time we are viewing this screen).  Otherwise, we get it from the SummaryChargeMenuBeanModel.
        SummaryCountBeanModel sc[] = null;
        if (cargo.isChargeCountModelAssigned() == true)
        {
            sc = cargo.getChargeModels();
        }
        else
        {
            sc = scmbm.getSummaryCountBeanModel();
        }

        // get blind close parameter value and set bean model flag
        String[] blindClose = cargo.getParameterStringValues(bus, "BlindClose");
        if (blindClose == null)
        {
            blindClose = new String[1];
        }
        if (blindClose[0] == null)
        {
            blindClose[0] = new String("Y");
        }
        scmbm.setBlindClose(blindClose[0].equalsIgnoreCase("Y"));
        // set expected amounts and blind close flag
        for (int i=0; i<sc.length; i++)
        {
            sc[i].setExpectedAmount(cargo.getExpectedAmount(sc[i].getDescription(), sc[i].getAmount().getCountryCode()));
            if (blindClose[0].equalsIgnoreCase("Y"))
            {
                sc[i].setExpectedAmountHidden(true);
            }
            else
            {
                sc[i].setExpectedAmountHidden(false);
                if(cargo.getCurrentCharge().equals(PosCountCargo.NONE))
                {
                    sc[i].setAmount(sc[i].getExpectedAmount()); // set all expected amounts
                }
            }
        }

        // set tender model in cargo
        // Note:  Usually this list is generated by cargo based on tenders in financial totals, but
        // we have a static tender list, so are supplying it instead for this case.
        cargo.setChargeModels(sc);
        cargo.setChargeCountModelAssigned(true);


        // disable buttons based on whether the card is accepted
        Iterator it = tempCardList.iterator();
        int count = 1;
        while (it.hasNext())
        {
            count++;
            if (count > 8)
            {
                count = 2;
            }
            String buttonName = ((Card) it.next()).getCardName();
            navModel.setButtonEnabled(buttonName, true);
        }

        scmbm.setLocalButtonBeanModel(navModel);
        scmbm.setSummaryCountBeanModel(sc);

        ui.showScreen(POSUIManagerIfc.SELECT_CHARGE_TO_COUNT, scmbm);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  CheckEmployeeIDSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

}
