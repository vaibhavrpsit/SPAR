/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SelectParameterBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/22/10 - add extra call to set focus to allow mouse wheel to
 *                         work
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 16 2003 17:53:10   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:12:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Apr 23 2003 11:27:08   bwf
 * Got correct text from bundle.
 * Resolution for 2201: Parameters - List box displays <> around some choices.
 * 
 *    Rev 1.2   Aug 14 2002 18:18:38   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jun 21 2002 18:26:58   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:48:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:57:34   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 04 2002 14:15:42   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.FocusManager;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Builds the screen for parameter selection.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class SelectParameterBean extends ListBean
{
    private static final long serialVersionUID = 698296294206809523L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected SelectParameterBeanModel beanModel = new SelectParameterBeanModel();

    /**
     * Default Constructor
     */
    public SelectParameterBean()
    {
        super();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ListBean#updateModel()
     */
    @Override
    public void updateModel()
    {
        int index = list.getSelectedRow();

        beanModel.setSelectionValue(list.getModel().getElementAt(index));
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#setModel(oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set SelectParameterBean model to null");
        }
        if (model instanceof SelectParameterBeanModel)
        {
            beanModel = (SelectParameterBeanModel)model;
            updateBean();
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ListBean#updateBean()
     */
    @Override
    public void updateBean()
    {
        POSListModel listModel = new POSListModel(beanModel.getChoices());
        getList().setModel(listModel);

        Object selected = beanModel.getSelectionValue();

        if (selected != null)
        {
            list.setSelectedValue(selected, true);
        }
        else
        {
            list.setSelectedIndex(0);
        }
        list.setFocusTraversalKeysEnabled(false);
        // set the title for the parameter group in the header
        String group = beanModel.getGroup();
        headerBean.setOneLabel(0, UIUtilities.retrieveText("Common",
                    BundleConstantsIfc.PARAMETER_BUNDLE_NAME, group, group));

        // TODO FOCUS: Figure out focus issues. Without this line, the list will
        // not scroll with the mouse wheel. This may be related to F2 keys not
        // working intermittently. There is a frame/rootpane hack in
        // POSFocusManager already.
        java.awt.Window w = FocusManager.getCurrentManager().getActiveWindow();
        if (w != null)
        {
            w.requestFocus();
        }
    }

    /**
     * Returns the revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Displays the bean in it's own frame.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        SelectParameterBean bean = new SelectParameterBean();
        bean.setLabelText("Parameter,Value,Modifiable");
        bean.setLabelWeights("40,50,10");
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
