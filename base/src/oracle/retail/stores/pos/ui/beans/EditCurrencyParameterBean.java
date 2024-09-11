/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditCurrencyParameterBean.java /main/17 2011/12/05 12:16:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/06/10 - use default locale when displaying currency
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         7/11/2007 11:07:30 AM  Anda D. Cadar
 *         removed ISO currency code when using base currency
 *    6    360Commerce 1.5         4/25/2007 8:51:30 AM   Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         1/25/2006 4:10:59 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:44 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     9/19/2005 13:48:10     Jason L. DeLeau Make
 *         sure CurrencyTextFields can have a blank default value.
 *    3    360Commerce1.2         3/31/2005 15:27:52     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:11     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:44     Robert Pearse
 *
 *Log:
 *    7    360Commerce 1.6         7/11/2007 11:07:30 AM  Anda D. Cadar
 *         removed ISO currency code when using base currency
 *    6    360Commerce 1.5         4/25/2007 8:51:30 AM   Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         1/25/2006 4:10:59 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:44 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse
 *: EditCurrencyParameterBean.java,v $
 *Log:
 *    7    360Commerce 1.6         7/11/2007 11:07:30 AM  Anda D. Cadar
 *         removed ISO currency code when using base currency
 *    6    360Commerce 1.5         4/25/2007 8:51:30 AM   Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         1/25/2006 4:10:59 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:44 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse
 *:
 *    5    .v710     1.2.2.1     10/24/2005 14:20:53    Charles Suehs   Merged
 *         from .v700 to fix CR 3965.
 *    4    .v710     1.2.2.0     10/20/2005 18:24:37    Charles Suehs   Merge
 *         from EditCurrencyParameterBean.java, Revision 1.2.1.0
 *    3    360Commerce1.2         3/31/2005 15:27:52     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:11     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:44     Robert Pearse
 *
 *   Revision 1.4  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.1   Sep 10 2003 16:04:04   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:10:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Aug 07 2002 19:34:16   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Jun 21 2002 18:26:36   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:53:58   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:34:16   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:55:00   msg
 * Initial revision.
 *
 *    Rev 1.6   Mar 04 2002 14:15:34   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;


/**
 * This bean enables the editing of a parameter that has a currency set of
 * possible values.
 * 
 * @version $Revision: /main/17 $
 */
public class EditCurrencyParameterBean extends ValidatingBean
{
    private static final long serialVersionUID = 6257720093768823567L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/17 $";

    public static final int VALUE_LABEL = 2;

    public static String[] labelText = { "Parameter Group:", "Parameter Name:", "Parameter Value:",
            "Minimum Allowed Value:", "Maximum Allowed Value:" };

    public static String[] labelTags = { "ParameterGroupLabel", "ParameterNameLabel", "ParameterValueLabel",
            "MinimumAllowedValueLabel", "MaximumAllowedValueLabel" };

    protected JLabel[] labels = null;
    protected JLabel parameterGroupField = null;
    protected JLabel parameterNameField = null;
    protected CurrencyTextField valueTextField = null;
    protected JLabel parameterMinField = null;
    protected JLabel parameterMaxField = null;
    protected CurrencyParameterBeanModel beanModel = new CurrencyParameterBeanModel();

    /**
     * Default constructor.
     */
    public EditCurrencyParameterBean()
    {
        super();
        initialize();
    }

    /**
     * Initializes the class.
     */
    protected void initialize()
    {
        setName("EditCurrencyParameterBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Initializes the components.
     */
    protected void initComponents()
    {
        labels = new JLabel[labelText.length];

        // create the display labels
        for (int i = 0; i < labelText.length; i++)
        {
            labels[i] = uiFactory.createLabel(labelText[i], labelText[i], null, UI_LABEL);
        }

        // create non-enterable display fields
        parameterGroupField = uiFactory.createLabel("parameterGroup", "parameterGroup", null, UI_LABEL);
        parameterNameField = uiFactory.createLabel("parameterName", "parameterName", null, UI_LABEL);
        parameterMinField = uiFactory.createLabel("parameterMin", "parameterMin", null, UI_LABEL);
        parameterMaxField = uiFactory.createLabel("parameterMax", "parameterMax", null, UI_LABEL);

        // create the enterable fields
        valueTextField = uiFactory.createCurrencyField("valueTextField", "true", "false", "false");
        valueTextField.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        JComponent[] fields = { parameterGroupField, parameterNameField, valueTextField, parameterMinField,
                parameterMaxField };
        UIUtilities.layoutDataPanel(this, labels, fields);
    }

    /**
     * The framework calls this method just before display
     */
    @Override
    public void activate()
    {
        super.activate();
        valueTextField.addFocusListener(this);

    }

    /**
     * The framework calls this method just before leaving bean
     */
    public void deactivate()
    {
        super.deactivate();
        valueTextField.removeFocusListener(this);

    }

    /**
     * Overrides JPanel setVisible() method to request focus.
     */
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            valueTextField.requestFocusInWindow();
        }
    }

    /**
     * Returns the POSBaseBeanModel associated with this bean.
     * 
     * @return the POSBaseBeanModel associated with this bean.
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return (beanModel);
    }

    /**
     * Updates the bean model, populated with data from the bean.
     */
    public void updateModel()
    {
        if (valueTextField.getDecimalValue() != null)
        {
            beanModel.setNewValue(valueTextField.getDecimalValue());
        }
        else
        {
            beanModel.setNewValue(BigDecimal.ZERO);
        }
    }

    /**
     * Sets the model data into the bean fields.
     * 
     * @param model the bean model
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("An attempt was made to set the EditCurrencyParameterBean model to null.");
        }
        
        if (model instanceof CurrencyParameterBeanModel)
        {
            beanModel = (CurrencyParameterBeanModel) model;
            updateBean();
        }
    }

    /**
     * Updates the bean, populated with data from the model.
     */
    public void updateBean()
    {
        String group = beanModel.getParameterGroup();
        String name = beanModel.getParameterName();
        CurrencyIfc value = DomainGateway.getBaseCurrencyInstance(beanModel.getValue());

        parameterGroupField.setText(retrieveText(group, group));
        parameterNameField.setText(retrieveText(name, name));
        valueTextField.setCurrencyValue(value);

        // I18N change - remove ISO code for base currency
        parameterMinField.setText(getCurrencyService().formatCurrency(beanModel.getMinValue(), getDefaultLocale()));
        parameterMaxField.setText(getCurrencyService().formatCurrency(beanModel.getMaxValue(), getDefaultLocale()));

        valueTextField.requestFocusInWindow();
    }

    /**
     * Updates fields based on properties.
     */
    protected void updatePropertyFields()
    {
        for (int i = 0; i < labelText.length; i++)
        {
            labels[i].setText(retrieveText(labelTags[i], labels[i]));
        }
        valueTextField.setLabel(labels[VALUE_LABEL]);
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: EditCurrencyParameterBean (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
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
        EditCurrencyParameterBean bean = new EditCurrencyParameterBean();
        UIUtilities.doBeanTest(bean);
    }
}