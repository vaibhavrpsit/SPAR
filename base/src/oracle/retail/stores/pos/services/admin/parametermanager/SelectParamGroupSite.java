/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/SelectParamGroupSite.java /rgbustores_13.4x_generic_branch/2 2011/09/06 16:39:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/06/11 - sort groups by localized names
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/10/2008 7:39:19 AM   Manas Sahu      Event
 *          originator changes
 *    3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:50  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   07 Jul 2003 19:34:10   baa
 * check group function role
 * 
 *    Rev 1.1   30 Jun 2003 23:21:00   baa
 * check group access
 * 
 *    Rev 1.0   Apr 29 2002 15:39:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:08   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:52   msg
 * Initial revision.
 * 
 *    Rev 1.2   Jan 19 2002 10:28:02   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.1   10 Dec 2001 13:21:22   KAC
 * Revised to work at register level instead of corporate/store.
 * Remove locationLevel and store dependencies.
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 * 
 *    Rev 1.0   Sep 21 2001 11:11:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.naming.CompoundName;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.SelectorIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.SettingsIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.SourceIfc;
import oracle.retail.stores.foundation.manager.parameter.CategoryAlternative;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

/**
 * Choose which parameter group (e.g. tender) will be edited.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
@SuppressWarnings("serial")
public class SelectParamGroupSite extends PosSiteActionAdapter
{

    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * Displays the list of defined parameter groups for the location level (and
     * perhaps the specific store) chosen by the user.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        Vector<String> parameterGroups = new Vector<String>();

        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            SelectorIfc selector = pm.getSelector();
            CategoryAlternative catAlt = null;
            String alternative = null;
            CompoundName[] catAlts = selector.getCategoryAlternatives();

            // Find the category and alternative corresponding to the register
            for (int i = 0; (i < catAlts.length) && (alternative == null); i++)
            {
                // If this is the register, we're done.
                if (ParameterCargo.REGISTER.equalsIgnoreCase(catAlts[i].get(0)))
                {
                    alternative = catAlts[i].get(1);
                    cargo.setAlternative(alternative);
                    catAlt = new CategoryAlternative(ParameterCargo.REGISTER, alternative);
                }
            }
            SourceIfc source = pm.getParametersForAlternative(catAlt);
            Enumeration settingsEnum = source.getSettingsKeys();

            SettingsIfc settings = null;
            String groupName = null;

            // Assemble the list of available groups
            while (settingsEnum.hasMoreElements())
            {
                groupName = (settingsEnum.nextElement()).toString();
                settings = source.getSettings(groupName);

                // if this particular setting is not hidden,
                // then add it to the list
                if (!settings.isHidden())
                {
                    parameterGroups.add(groupName);
                }
            }
        }
        catch (Exception e)
        {
            if (logger.isInfoEnabled())
                logger.info(e.toString());
        }

        sortGroupNames(parameterGroups);

        DataInputBeanModel beanModel = new DataInputBeanModel();
        beanModel.setSelectionChoices("choiceList", parameterGroups);
        beanModel.setSelectionValue("choiceList", parameterGroups.firstElement());
        // Setup cargo with list of choices
        cargo.setParameterGroups(parameterGroups);

        EventOriginatorInfoBean.setEventOriginator("SelectParamGroupSite.arrive");

        // display the UI screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.PARAM_SELECT_GROUP, beanModel);
    }

    /**
     * Sort the choices by displayed name before building the model.
     *
     * @param parameterGroups
     */
    protected void sortGroupNames(Vector<String> parameterGroups)
    {
        Collections.sort(parameterGroups, new Comparator<String>()
        {
            @Override
            public int compare(String name1, String name2)
            {
                String groupName1 = UIUtilities.retrieveText("Common", BundleConstantsIfc.PARAMETER_BUNDLE_NAME,
                        name1, name1);
                String groupName2 = UIUtilities.retrieveText("Common", BundleConstantsIfc.PARAMETER_BUNDLE_NAME,
                        name2, name2);
                return groupName1.compareToIgnoreCase(groupName2);
            }
        });
    }
}