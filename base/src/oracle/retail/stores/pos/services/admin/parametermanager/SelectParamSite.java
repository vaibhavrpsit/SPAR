/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/SelectParamSite.java /rgbustores_13.4x_generic_branch/2 2011/09/06 16:39:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/06/11 - sort parameters by localized names
 *    cgreene   10/22/10 - remove extra call to UI setModel
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/24/2008 12:37:56 PM  Deepti Sharma   merge
 *          from v12.x to trunk
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
 *    Rev 1.1   Oct 01 2003 13:46:48   lzhao
 * They were sorted by parameter names defined in application.xml rather than the parameter name value in parameterText properties file.
 * Resolution for 3094: List of parameters not in alphabetical order in Tender parameter group
 *
 *    Rev 1.0   Aug 29 2003 15:52:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jul 17 2003 11:19:32   sfl
 * Un-commented the block for hidden parameter checking so that parameter level hidden checking will work again. (It was commented out in December of 2001).
 * Resolution for POS SCR-2082:  External Tax  package is not working.
 *
 *    Rev 1.0   Apr 29 2002 15:39:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:05:12   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:19:54   msg
 * Initial revision.
 *
 *    Rev 1.1   10 Dec 2001 13:21:24   KAC
 * Revised to work at register level instead of corporate/store.
 * Remove locationLevel and store dependencies.
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 *
 *    Rev 1.0   Sep 21 2001 11:12:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.naming.InvalidNameException;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.ParameterIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.SettingsIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.SourceIfc;
import oracle.retail.stores.foundation.manager.parameter.CategoryAlternative;
import oracle.retail.stores.foundation.manager.parameter.Parameter;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.SelectParameterBeanModel;

/**
 * Select the parameter to be edited.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class SelectParamSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7911259628757801651L;
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * Displays all of the parameters in the group, so that the user can choose
     * one to edit
     *
     * @param bus the bus arriving at this site
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void arrive(BusIfc bus)
    {
        ParameterCargo cargo = (ParameterCargo) bus.getCargo();
        String group = cargo.getParameterGroup();
        String alternative = cargo.getAlternative();

        SelectParameterBeanModel beanModel = new SelectParameterBeanModel();

        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            CategoryAlternative catAlt = new CategoryAlternative(ParameterCargo.REGISTER, alternative);
            SourceIfc source = pm.getParametersForAlternative(catAlt);

            SettingsIfc groupSettings = source.getSettings(group);
            Vector<ParameterIfc> parameters = groupSettings.getParameters();

            // sort choices for UI.
            sortParameters(parameters);

            // convert parameter to choice models
            Vector parameterBeans = new Vector(parameters.size());

            // Convert parameters into RetailParameters (beans)
            for (int i = 0; i < parameters.size(); i++)
            {
                Parameter param = (Parameter)parameters.get(i);

                // do not display hidden parameters
                // Ignore reason codes (parameters with multiple values)
                if (!param.isHidden() && param.getValues() != null)
                {
                    Comparable<?> comparableObj = (Comparable<?>)cargo.convertParameterToBean(param);
                    parameterBeans.add(comparableObj);
                }
            }

            cargo.setRetailParameters(parameterBeans);
            beanModel.setChoices(parameterBeans);
            beanModel.setGroup(group);
        }
        catch (InvalidNameException e)
        {
            logger.error(e);
        }
        catch (ParameterException e)
        {
            logger.error(e);
        }

        // display the UI screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.PARAM_SELECT_PARAMETER, beanModel);
    }

    protected void sortParameters(Vector<ParameterIfc> parameters)
    {
        Collections.sort(parameters, new Comparator<ParameterIfc>()
        {
            @Override
            public int compare(ParameterIfc parm1, ParameterIfc parm2)
            {
                String localizedName1 = UIUtilities.retrieveText("Common", BundleConstantsIfc.PARAMETER_BUNDLE_NAME,
                        parm1.getName(), parm1.getName());
                String localizedName2 = UIUtilities.retrieveText("Common", BundleConstantsIfc.PARAMETER_BUNDLE_NAME,
                        parm2.getName(), parm2.getName());
                return localizedName1.compareToIgnoreCase(localizedName2);
            }
        });
    }
}
