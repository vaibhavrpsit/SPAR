/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/ShowAlterationsScreenSite.java /main/12 2011/12/05 12:16:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:59 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
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
 *    Rev 1.0   Aug 29 2003 15:54:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.5   Mar 12 2003 12:27:26   DCobb
 * Code review cleanup.
 * Resolution for POS SCR-1753: POS 6.0 Alterations Package
 * 
 *    Rev 1.4   Mar 05 2003 18:18:14   DCobb
 * Generalized names of alterations attributes.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.3   Sep 20 2002 17:46:16   DCobb
 * New modify item alternate flow.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.2   Aug 22 2002 16:31:50   DCobb
 * Removed default item description.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.1   Aug 21 2002 11:21:22   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// Java imports
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.utility.AlterationIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.AlterationsBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
    User has selected a particular alterations.
    This class loads the appropriate Screen.<P>
    @version $Revision: /main/12 $
**/
//------------------------------------------------------------------------------
public class ShowAlterationsScreenSite extends PosSiteActionAdapter
{
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/12 $";
    /** Alteration arg tags **/
    public static final String ALTERATION = "alteration";
    public static final String REPAIR = "repair";

    //--------------------------------------------------------------------------
    /**
       Show the appropriate alterations screen.<P>
       @param bus the bus containing the alterations cargo
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        AlterationsCargo cargo = (AlterationsCargo)bus.getCargo();

        // Get access to the UI Manager from the bus
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

       // Show the screen
        showAlterationsScreen(ui, (AlterationPLUItemIfc)cargo.getPLUItem(), cargo.getSelectedLetter());
    }

    //--------------------------------------------------------------------------
    /**
       Show the alterations screen for the selection.<P>
       @param ui the ui mamanger
       @param alterationItem the alteration PLU item
       @param selected letter the user's selection
    **/
    //--------------------------------------------------------------------------
    public void showAlterationsScreen(POSUIManagerIfc ui, AlterationPLUItemIfc alterationItem, String selectedLetter)
    {
        // Construct the models
        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        AlterationsBeanModel aModel = new AlterationsBeanModel();

        // Setup the models

        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        String argText = utility.retrieveText(
                POSUIManagerIfc.STATUS_SPEC,
                BundleConstantsIfc.ALTERATIONS_BUNDLE_NAME,
                ALTERATION,
                ALTERATION,
                locale);
        String screenID = "";

        int alterationType = AlterationIfc.TYPE_UNDEFINED;

        if (selectedLetter.equals(AlterationsCargo.ACTION_PANTS)) // Show Pants alterations
        {
            screenID = POSUIManagerIfc.PANTS_ALTERATION;
            alterationType = AlterationIfc.PANTS_TYPE;
        }
        else if (selectedLetter.equals(AlterationsCargo.ACTION_SHIRT)) // Show Shirt alterations
        {
            screenID = POSUIManagerIfc.SHIRT_ALTERATION;
            alterationType = AlterationIfc.SHIRT_TYPE;
        }
        else if (selectedLetter.equals(AlterationsCargo.ACTION_COAT)) // Show Coat alterations
        {
            screenID = POSUIManagerIfc.COAT_ALTERATION;
            alterationType = AlterationIfc.COAT_TYPE;
        }
        else if (selectedLetter.equals(AlterationsCargo.ACTION_SKIRT)) // Show Skirt alterations
        {
            screenID = POSUIManagerIfc.SKIRT_ALTERATION;
            alterationType = AlterationIfc.SKIRT_TYPE;
        }
        else if (selectedLetter.equals(AlterationsCargo.ACTION_DRESS)) // Show Dress alterations
        {
            screenID = POSUIManagerIfc.DRESS_ALTERATION;
            alterationType = AlterationIfc.DRESS_TYPE;
        }
        else if (selectedLetter.equals(AlterationsCargo.ACTION_REPAIRS)) // Show Repairs alterations
        {
            argText = utility.retrieveText(
                    POSUIManagerIfc.STATUS_SPEC,
                    BundleConstantsIfc.ALTERATIONS_BUNDLE_NAME,
                    REPAIR,
                    REPAIR,
                    locale);
            screenID = POSUIManagerIfc.REPAIRS_ALTERATION;
            alterationType = AlterationIfc.REPAIRS_TYPE;
        }


        AlterationIfc alteration = alterationItem.getAlteration();
        if (!alteration.getItemDescription().equals("") &&
            (alteration.getAlterationType() == alterationType))
        {
            aModel.setItemDescription(alteration.getItemDescription());
            aModel.setItemNumber(alteration.getItemNumber());
            aModel.setValue1(alteration.getValue1());
            aModel.setValue2(alteration.getValue2());
            aModel.setValue3(alteration.getValue3());
            aModel.setValue4(alteration.getValue4());
            aModel.setValue5(alteration.getValue5());
            aModel.setValue6(alteration.getValue6());
        }

        pandrModel.setArguments(argText);
        aModel.setPromptAndResponseModel(pandrModel);

        // Show the screen
        ui.showScreen(screenID, aModel);
    }
}
