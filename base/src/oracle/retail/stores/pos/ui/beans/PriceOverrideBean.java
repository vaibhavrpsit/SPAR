/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PriceOverrideBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:28 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:21 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:24 PM  Robert Pearse
 *
 *   Revision 1.4  2004/07/19 17:49:06  awilliam
 *   @scr 4485 extra spaces in msg prompt
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Jan 06 2004 13:25:20   cdb
 * Removed references to old beans. Deprecated the old beans.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Aug 29 2003 16:11:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Apr 10 2003 13:34:34   bwf
 * Remove instanceof UtilityManagerIfc and replaced with UIUtilities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.2   Feb 13 2003 10:27:02   HDyer
 * Changed updateModel to use selected index rather than selected string since string is now internationalized. Changed updateBean to populate UI list with internationalized strings, and to use the selected index to set the scroll position. Fixed currency deprecation warning.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   Aug 14 2002 18:18:24   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:53:42   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:35:44   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:56:58   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 01 2002 22:35:46   mpm
 * Made changes for modifyitem internationalization.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//-------------------------------------------------------------------------
/**
 * This bean uses the DecimalWithReasonBeanModel.
 * @return DecimalWithReasonBeanModel The model of the bean.
 * @see #setModel
 * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated in release 7.0.0 use {@link PriceOverrideEntryBean} with Bean
    Property EditableList ommitted or set to false in corresponding
    overlay screen definition in appropriate uicfg.xml file.
 */
//-------------------------------------------------------------------------
public class PriceOverrideBean extends ValidatingBean
{

    //--------------------------------------------------------------------------
    /**
     *   Label for Reason Code. <P>
     */
    //--------------------------------------------------------------------------
    protected JLabel reasonCodeLabel = null;
    //--------------------------------------------------------------------------
    /**
     *   Container of reasons. <P>
     */
    //--------------------------------------------------------------------------
    protected JList reasonList = null;
    //--------------------------------------------------------------------------
    /**
     *   Scroll long list of reasons. <P>
     */
    //--------------------------------------------------------------------------
    protected JScrollPane reasonScrollPane = null;
    //--------------------------------------------------------------------------
    /**
     *   local reference to model being used. <P>
     */
    //--------------------------------------------------------------------------
    protected DecimalWithReasonBeanModel beanModel = null;
    //--------------------------------------------------------------------------
    /**
     *   POS implementation to allow vectors in javax.swing.DefaultListModel. <P>
     */
    //--------------------------------------------------------------------------
    protected POSListModel listModel = null;
    //--------------------------------------------------------------------------
    /**
     *   TextField for the Override Price. <P>
     */
    //--------------------------------------------------------------------------
    protected CurrencyTextField overridePriceField = null;
    //--------------------------------------------------------------------------
    /**
     *   Label for OverridePriceField. <P>
     */
    //--------------------------------------------------------------------------
    protected JLabel overridePriceLabel = null;
    //--------------------------------------------------------------------------
    /**
     *   Indicates when the model is dirty. <P>
     */
    //--------------------------------------------------------------------------
    protected boolean dirtyModel = false;

    //--------------------------------------------------------------------------
    /**
     *   Revision Number furnished by TeamConnection. <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //-------------------------------------------------------------------------
    /**
     * Constructor
     */
    //-------------------------------------------------------------------------
    public PriceOverrideBean()
    {
        super();
    }

    //-------------------------------------------------------------------------
    /**
     * Configures the class.
     */
    //-------------------------------------------------------------------------
    public void configure()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);

        setName("PriceOverrideBean");

        overridePriceLabel = uiFactory.createLabel("Override Price:", null, UI_LABEL);

        reasonCodeLabel =uiFactory.createLabel("Reason Code :", null, UI_LABEL);

        overridePriceField =
        uiFactory.createCurrencyField("OverridePriceField", "false", "false", "false");

        reasonScrollPane = uiFactory.createSelectionList("ReasonScrollPane", "large");
        reasonList = (JList)reasonScrollPane.getViewport().getView();

        UIUtilities.layoutDataPanel(this,
                                    new JLabel[] {overridePriceLabel, reasonCodeLabel},
                                    new JComponent[] {overridePriceField, reasonScrollPane});
    }

    //---------------------------------------------------------------------
    /**
     *  Returns the bean model. <P>
     *  @return model object
     *  @see oracle.retail.stores.pos.ui.beans.POSBaseBeanModel
     */
    //---------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //-------------------------------------------------------------------------
    /**
     * updates the Model properties.
     * @return The model property value.
     * @see #setModel
     */
    //-------------------------------------------------------------------------
    public void updateModel()
    {

        beanModel.setValue(overridePriceField.getDecimalValue());
        beanModel.setSelected(false);
        int reason = reasonList.getSelectedIndex();
        if (reason >= 0)
        {
            beanModel.setSelectedReasonCode(reason);
            beanModel.setSelected(true);
        }
    }
    //-------------------------------------------------------------------------
    /**
     * This method sets the model of this bean.
     * This bean uses the DecimalWithReasonBeanModel.
     * @param model The new DecimalWithReasonBeanModel to use.
     * @see #getModel
     * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
     */
    //-------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set DiscAmountBean model to null");
        }
        if (model instanceof DecimalWithReasonBeanModel)
        {
            beanModel = (DecimalWithReasonBeanModel) model;
            dirtyModel = true;
            updateBean();
        }
    }
    //---------------------------------------------------------------------
    /**
     * Update the bean if It's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if(dirtyModel)
        {
            overridePriceField.setDecimalValue(beanModel.getValue());

            // Populate the list model with the I18N strings
            POSListModel listModel = new POSListModel(beanModel.getReasonCodes());
            reasonList.setModel(listModel);
            if(beanModel.isSelected())
            {
                // Set selected index on the list to what was selected before.
                // Note, the model knows the key, but not the displayed localized
                // value, so we set what was selected previously by using the index
                reasonList.setSelectedIndex(beanModel.getSelectedIndex());
                // and then make sure that part of the scrollpane is visible
                reasonList.ensureIndexIsVisible(beanModel.getSelectedIndex());
            }
            dirtyModel = false;
        }
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        overridePriceLabel.setText(retrieveText("OverridePriceLabel", overridePriceLabel));
        reasonCodeLabel.setText(retrieveText("ReasonCodeLabel", reasonCodeLabel));

        overridePriceField.setLabel(overridePriceLabel);

    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
     *  Returns default display string. <P>
     *  @return String representation of object
     */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: PriceOverrideBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
     *  Retrieves the Team Connection revision number. <P>
     *  @return String representation of revision number
     */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //-------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //-------------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        PriceOverrideBean bean  = new PriceOverrideBean();
        bean.configure();

        UIUtilities.doBeanTest(bean);
    }
}
