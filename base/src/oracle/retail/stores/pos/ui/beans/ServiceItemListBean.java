/* ===========================================================================
 * Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ServiceItemListBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/13/14 - convert ServiceItemListBean to an actual ListBean
 *                         instead of a screen with one combobox.
 *    jswan     11/03/10 - Fixed issues with displaying text and drop down
 *                         fields on screen with a single lable.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ddbaker   11/20/08 - Updates for clipping problems
 *
 * ===========================================================================
 * $Log:
 *  4    I18N_P2    1.2.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *       29826 - Setting the size of the combo boxes. This change was
 *       necessary because the width of the combo boxes used to grow according
 *        to the length of the longest content. By setting the size, we allow
 *       the width of the combo box to be set independently from the width of
 *       the dropdown menu.
 *  3    360Commerce 1.2         3/31/2005 4:29:56 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:25:12 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:10 PM  Robert Pearse   
 *
 * Revision 1.4  2004/07/17 19:21:23  jdeleau
 * @scr 5624 Make sure errors are focused on the beans, if an error is found
 * during validation.
 *
 * Revision 1.3  2004/03/16 17:15:18  build
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 20:56:26  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 16 2003 17:53:12   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:12:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:40   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:48:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   15 Apr 2002 09:36:04   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:57:38   msg
 * Initial revision.
 *
 *    Rev 1.4   Mar 01 2002 22:35:46   mpm
 * Made changes for modifyitem internationalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   16 Feb 2002 19:20:30   baa
 * add default selection to combo box
 * Resolution for POS SCR-1292: Tab not functioning on Non Merchandise item drop down box
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.BorderFactory;

import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean allows the user to select a non-merchandise item from a dropdown
 * list.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ServiceItemListBean extends ItemListBean
{
    private static final long serialVersionUID = 5240148872501962394L;

    /** revision number supplied by source-code-control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** class name */
    public static final String CLASSNAME = "ServiceItemListBean";

    /** The model for this bean. */
    protected ServiceItemListBeanModel beanModel = new ServiceItemListBeanModel();

    /**
     * Constructs bean.
     */
    public ServiceItemListBean()
    {
    }

    /**
     * Sets the model data into the bean fields.
     * 
     * @param model the bean model
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("An attempt was made to set the ServiceItemListBean model to null.");
        }

        if (model instanceof ServiceItemListBeanModel)
        {
            beanModel = (ServiceItemListBeanModel)model;
            updateBean();

        }
    }

    /**
     * Updates the model for the current settings of this bean.
     */
    @Override
    public void updateModel()
    {
        int index = list.getSelectedRow();
        beanModel.setSelectedIndex(index);
    }

    /**
     * Updates the bean for the current settings of this bean.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void updateBean()
    {
        POSListModel posListModel = new POSListModel<ItemSearchResult>(beanModel.getServiceItems());
        getList().setModel(posListModel);

        int index = beanModel.getSelectedIndex();
        if (index >= 0)
        {
            list.setSelectedIndex(index);
        }
        else
        {
            list.setSelectedIndex(0);            
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Overridden to add some space around the edges. This list doesn't need
     * the entire screen.
     *
     * @see oracle.retail.stores.pos.ui.beans.ListBean#configureScrollPane()
     */
    @Override
    protected void configureScrollPane()
    {
        this.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        super.configureScrollPane();
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: ServiceItemListBean (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

    /**
     * Main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();
        ServiceItemListBean bean = new ServiceItemListBean();
        UIUtilities.doBeanTest(bean);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }
}
