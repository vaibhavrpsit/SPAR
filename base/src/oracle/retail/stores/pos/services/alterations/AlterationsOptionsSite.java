/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/AlterationsOptionsSite.java /main/12 2012/10/16 17:37:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/10/12 - Popup menu implementation
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Mar 12 2003 12:27:26   DCobb
 * Code review cleanup.
 * Resolution for POS SCR-1753: POS 6.0 Alterations Package
 * 
 *    Rev 1.2   Mar 05 2003 18:16:00   DCobb
 * Disable buttons when modifying an existing alteration.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.1   Aug 21 2002 11:21:20   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.utility.AlterationIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * User has selected Alterations and a customer is associated with the
 * transaction
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class AlterationsOptionsSite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Show the alterations options screen.
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get a reference to the cargo
        AlterationsCargo cargo = (AlterationsCargo) bus.getCargo();

        // Get access to the UI Manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Setup the models.
        POSBaseBeanModel pModel = new POSBaseBeanModel();
        pModel.setLocalButtonBeanModel(getNavigationButtonBeanModel((AlterationPLUItemIfc) cargo.getPLUItem(),
                cargo.isModifyItemService(), cargo.isNewPLUItem()));
        // Show the AlterationsOptions screen
        ui.showScreen(POSUIManagerIfc.ALTERATION_TYPE, pModel);
    }

    /**
     * Save the selected letter in cargo
     * 
     * @param bus the bus departing from this site
     */
    @Override
    public void depart(BusIfc bus)
    {
        // Get a reference to the cargo
        AlterationsCargo cargo = (AlterationsCargo) bus.getCargo();

        // Get the letter from the bus
        LetterIfc letter = bus.getCurrentLetter();

        String letterName = null;
        if ((letter instanceof ButtonPressedLetter))
        {
            // Get the String representation of the letter name
            // from the LetterIfc object
            letterName = letter.getName();
        }

        // Set the letterName in the cargo
        cargo.setSelectedLetter(letterName);
    }

    /**
     * Builds the NavigationButtonBeanModel; this method sets the local
     * navigation buttons to their correct enabled states.
     * 
     * @param alterations PLU item
     * @param modify item service flag
     * @param new PLU item flag
     * @return NavigationButtonBeanModel
     */
    protected NavigationButtonBeanModel getNavigationButtonBeanModel(AlterationPLUItemIfc item,
            boolean modifyItemServiceFlag, boolean newPLUItemFlag)
    {
        NavigationButtonBeanModel nbbModel = new NavigationButtonBeanModel();

        // Determine if this alteration is being modified.
        if (modifyItemServiceFlag && !newPLUItemFlag)
        { // existing alteration
            // turn off all buttons

            // activate the button that matches the alteration type
            int type = item.getAlteration().getAlterationType();
            switch (type)
            {
            case (AlterationIfc.PANTS_TYPE):
                break;
            case (AlterationIfc.SHIRT_TYPE):
                break;
            case (AlterationIfc.COAT_TYPE):
                break;
            case (AlterationIfc.DRESS_TYPE):
                break;
            case (AlterationIfc.SKIRT_TYPE):
                break;
            case (AlterationIfc.REPAIRS_TYPE):
                break;
            default:
            }
        }
        else
        { // new alteration
            // refresh buttons turned off in previous iterations
        }

        return nbbModel;
    }

}
