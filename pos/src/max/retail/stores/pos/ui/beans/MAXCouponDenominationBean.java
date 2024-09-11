
package max.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.beans.BaseBeanAdapter;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

//--------------------------------------------------------------------------
/**
    Allows the usr to select a tender to work with.
    @version $Revision: 7$
**/
//--------------------------------------------------------------------------
public class MAXCouponDenominationBean extends BaseBeanAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1881232336182649406L;
	/**
        revision number
    **/
    public static final String revisionNumber = "$Revision: 7$";
    /**
        The bean model for this bean
    **/
    protected MAXCouponDenominationBeanModel beanModel = new MAXCouponDenominationBeanModel();
    /**
        The label for the total field
    **/
    protected static final String TOTAL_TEXT         = "Total:";
    /**
        The tag for the total field label
    **/
    protected static final String TOTAL_LABEL         = "TotalLabel";
    /**
        Screen labels
    **/
    protected JLabel[]            fieldLabels = null;
    /**
        Screen fields
    **/
    protected JComponent[] quantityFields = null;
    
    protected JComponent[] totalFields = null;
    
    protected JLabel            denominationLabel = null;
    /**
        Screen fields
    **/
    protected JLabel quantitylabel = null;
    
    protected JLabel amountLabel = null;
    
    /**
        Indicates if this class was started from the Main method in this class.
    **/
    public boolean              testOnly    = false;
    
    
    protected JScrollPane panelScrollPane = null;
    
    protected JPanel jPanel = null;
    /**
     * User locale
     */
    protected  Locale locale =  LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

    //----------------------------------------------------------------------------
    /**
        Constructs the bean.
    **/
    //----------------------------------------------------------------------------
    public MAXCouponDenominationBean()
    {
        super();
        setName("MAXCouponDenominationBean");
    }

    //----------------------------------------------------------------------------
    /**
        Calls methods to set up the labels, fields and button bar. <P>
    **/
    //----------------------------------------------------------------------------
    protected void initialize()
    {
        removeAll();

        uiFactory.configureUIComponent(this, UI_PREFIX);

        setLayout(new GridBagLayout());

        ArrayList sc = beanModel.getCouponDenominationCountBeanModel();
        CurrencyIfc total          = null;
        
        try
        {
            total =  DomainGateway.getBaseCurrencyInstance();
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database or server may be offline, using default number of fraction digits", e);
        }

        
        
        panelScrollPane = new JScrollPane();
        
        panelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelScrollPane.setMinimumSize(new Dimension(400, 400));
        panelScrollPane.setMaximumSize(new Dimension(400, 300));
        jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        jPanel.setVisible(true);
        
        
        fieldLabels                = new JLabel[sc.size() + 3];
        quantityFields             = new JComponent[sc.size() + 3];
        totalFields                = new JComponent[sc.size() + 3];
        String labelText = null;

        fieldLabels[0] = uiFactory.createLabel("Denominations", null, UI_LABEL);
        quantityFields[0] = uiFactory.createLabel("Quantity", null, UI_LABEL);
        totalFields[0] = uiFactory.createLabel("Amount", null, UI_LABEL);
        // labels and fields for base currency
        for(int i = 0; i < sc.size(); i++)
        {
            if (total != null)
            {
                total = total.add(((MAXCouponDenominationCountBeanModel)sc.get(i)).getAmount());
            }

            // create label and field
            labelText = ((MAXCouponDenominationCountBeanModel)sc.get(i)).getLabelTag();
            	//retrieveText(((MAXCouponDenominationCountBeanModel)sc.get(i)).getLabelTag(),
            		//((MAXCouponDenominationCountBeanModel)sc.get(i)).getLabel());
            fieldLabels[i+1] = uiFactory.createLabel(labelText, null, UI_LABEL);
           
            
            ConstrainedTextField  currencyField = uiFactory.createConstrainedField(((MAXCouponDenominationCountBeanModel)sc.get(i)).getDescription() +  "AmountField", "1", "10");
            
            
            currencyField.setText(((MAXCouponDenominationCountBeanModel)sc.get(i)).getAmount().toFormattedString(locale));
            currencyField.setEditable(false);
            
            currencyField.setEnabled(false);
            totalFields[i+1] = currencyField;            
            
            ConstrainedTextField quantityField = uiFactory.createNumericField(((MAXCouponDenominationCountBeanModel)sc.get(i)).getDescription() + "QuantityField","1", "2");
            quantityField.setText(((MAXCouponDenominationCountBeanModel)sc.get(i)).getQuantity()+"");
            currencyField.setEditable(false);
            currencyField.setEnabled(false);
            quantityFields[i+1]=quantityField;


            if (((MAXCouponDenominationCountBeanModel)sc.get(i)).isFieldDisabled())
            {
                totalFields[i+1].setEnabled(false);
                quantityFields[i+1].setEnabled(false);
            }
            
            // layout label field pair
            int col = 0;
            int row = i;
            
            layoutComponent(jPanel, panelScrollPane, this,fieldLabels[i],quantityFields[i],totalFields[i],col,row,false);
        }

        // blank line - There must be at least one character in order for the line to show
        int pos = sc.size()+1;
        
        fieldLabels[pos] = uiFactory.createLabel(" ", null, UI_LABEL);
        JLabel spacer = uiFactory.createLabel("", null, UI_LABEL);
        spacer.setEnabled(false);
        spacer.setVisible(true);
        JLabel spacer2 = uiFactory.createLabel("", null, UI_LABEL);
        spacer.setEnabled(false);
        spacer.setVisible(true);
        totalFields[pos] = spacer;
        quantityFields[pos]=spacer2;
        // total field
        int totalPos = sc.size() + 2;

        labelText = retrieveText(TOTAL_LABEL, TOTAL_TEXT);
        fieldLabels[totalPos] = uiFactory.createLabel(labelText, null, UI_LABEL);
        ConstrainedTextField  currencyField = uiFactory.createConstrainedField(TOTAL_TEXT +  "AmountField", "1", "10");
        
        currencyField.setEditable(false);
        if (total != null)
        {
            currencyField.setText(total.toFormattedString(locale));
        }
        
        currencyField.setEnabled(false);
        totalFields[totalPos] = currencyField;
        quantityFields[totalPos]=null;
        layoutDataPanel(jPanel, panelScrollPane, this,fieldLabels,quantityFields, totalFields);
    }

    //----------------------------------------------------------------------------
    /**
        Adds the counted amout fields to the panel; the number of fields and
        their values depends on the data in the model. <P>
    **/
    //----------------------------------------------------------------------------
    protected void updateBean()
    {
      initialize();
      String text = ((JTextField)totalFields[totalFields.length-1]).getText();
      beanModel.setTotal(DomainGateway.getBaseCurrencyInstance(text));
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    public void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        if (beanModel != null)
        {
        	ArrayList sc = beanModel.getCouponDenominationCountBeanModel();

            if (fieldLabels != null)
            {
                for(int i = 0; i < sc.size(); i++)
                {
                    fieldLabels[i].setText(retrieveText(((MAXCouponDenominationCountBeanModel)sc.get(i)).getLabelTag(),
                                                        ((MAXCouponDenominationCountBeanModel)sc.get(i)).getLabel()));
                }

                int totalPos = sc.size() + 1;
                fieldLabels[totalPos].setText(retrieveText(TOTAL_LABEL,TOTAL_TEXT));
            }
        }

    }                                   // end updatePropertyFields()

    //----------------------------------------------------------------------------
    /**
        Set the bean model into the bean. <P>
        @param model  The bean model
    **/
    //----------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model==null)
        {
            throw new NullPointerException("Attempt to set SummaryTenderMenuBean"
                                           + " to null");
        }
        if (model instanceof MAXCouponDenominationBeanModel)
        {
            beanModel = (MAXCouponDenominationBeanModel)model;
            updateBean();
        }
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: SummaryTenderMenuBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

   
    
    public void layoutComponent(
            JPanel bean,
            JScrollPane panelScrollPane,
            JPanel thisPanel, 
            JLabel label,
            JComponent quantityField,
            JComponent totalField,
            int xValue,
            int yValue,
            boolean bottom)
        {
            UIFactory factory = UIFactory.getInstance();
            GridBagConstraints gbc = factory.getConstraints("DataEntryBean");

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 0;
            gbc.weighty = 0;

            String labelInsets = new String("defaultLabelLeft");
            String fieldInsets = new String("defaultFieldRight");

            // layout top label and field
            gbc.gridx = xValue;
            gbc.gridy = yValue;

            if (yValue == 0) // use top insets
            {
                labelInsets = "defaultLabelTop";
                fieldInsets = "defaultFieldTop";
            }
            else if (bottom) // use bottom insets
            {
                labelInsets = "defaultLabelBottom";
                fieldInsets = "defaultFieldBottom";
            }

            gbc.insets = factory.getInsets(labelInsets);
            // check if the label is a divider
            if (label instanceof UIFactory.EYSDivider)
            {
                gbc.gridwidth = GridBagConstraints.REMAINDER;
            }
            bean.add(label, gbc);
            
            if (quantityField != null)
            {
                gbc.fill = GridBagConstraints.NONE;
                if (bottom)
                {
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.weighty = 1.0;
                    //gbc.weightx=1.0;
                }
                gbc.insets = factory.getInsets(fieldInsets);
                gbc.gridx = xValue + 1;
                bean.add(quantityField, gbc);
            }
            
            if (totalField != null)
            {
                gbc.fill = GridBagConstraints.NONE;
                if (bottom)
                {
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.weighty = 1.0;
                    //gbc.weightx=1.0;
                }
                gbc.insets = factory.getInsets(fieldInsets);
                gbc.gridx = xValue + 2;
                bean.add(totalField, gbc);
            }
            panelScrollPane.getViewport().setView(bean);
            thisPanel.add(panelScrollPane);
        }
    
    public void layoutDataPanel(
            JPanel bean,
            JScrollPane panelScrollPane,
            JPanel thisPanel,
            JLabel[] labels,
            JComponent[] quantityFields,
            JComponent[] totalFields)
        {
            layoutDataPanel(bean,panelScrollPane,
                    thisPanel, labels, quantityFields, totalFields, true);
        }

        //--------------------------------------------------------------------------
        /**
         * Applies the standard constraints to a set of labels and components. The
         * standard layout is the GridBagLayout, and the constraints are derived
         * from the ui properties. The assumption is that the labels and components
         * line up as complementary pairs in two columns.
         *
         * @param bean
         *            the panel to add components to
         * @param thisPanel 
         * @param panelScrollPane2 
         * @param labels
         *            an array of text labels
         * @param components
         *            an array of components
         * @param addWeight
         *            flag indicating last components get full weight
         */
        public void layoutDataPanel(
            JPanel bean,
            JScrollPane panelScrollPane, 
            JPanel thisPanel, 
            JLabel[] labels,
            JComponent[] quantityFields,
            JComponent[] totalFields,
            boolean addWeight)
        {
            int size = labels.length;

            UIFactory factory = UIFactory.getInstance();

            bean.setLayout(new GridBagLayout());
            GridBagConstraints constraints =
                factory.getConstraints("DataEntryBean");
            constraints.weightx = 0;
            constraints.weighty = 0;
            // layout top label and field
            constraints.gridy = 0;
            if (size == 1 || addWeight)
            {
                constraints.insets = factory.getInsets("singleLabelTop");
            }
            else
            {
                constraints.insets = factory.getInsets("defaultLabelTop");
            }
            constraints.fill = GridBagConstraints.HORIZONTAL;
            if (labels[0] != null)
            {
                bean.add(labels[0], constraints);
            }

            if (size == 1 || addWeight)
            {
                constraints.insets = factory.getInsets("singleFieldTop");
            }
            else
            {
                constraints.insets = factory.getInsets("defaultFieldTop");
            }
            constraints.gridx = 1;

            // adjust for single pair layouts
            if (size == 1)
            {
                constraints.weighty = 1.0;
                //constraints.weightx = 1.0;
            }
            constraints.fill = GridBagConstraints.NONE;
            bean.add(quantityFields[0], constraints);
            constraints.gridx = 2;
            bean.add(totalFields[0], constraints);
            // layout the middle labels and fields
            for (int i = 1; i < (size - 1); i++)
            {
                constraints.gridy = i;
                constraints.gridx = 0;
                constraints.insets = factory.getInsets("defaultLabelLeft");
                constraints.fill = GridBagConstraints.HORIZONTAL;

                if (labels[i] != null)
                {
                    // check if the label is a divider
                    if (labels[i] instanceof UIFactory.EYSDivider)
                    {
                        constraints.gridwidth = GridBagConstraints.REMAINDER;
                    }
                    else
                    {
                        constraints.gridwidth = 1;
                    }
                    bean.add(labels[i], constraints);
                }
                if (quantityFields[i] != null)
                {
                    constraints.gridx = 1;
                    constraints.insets = factory.getInsets("defaultFieldRight");
                    constraints.fill = GridBagConstraints.NONE;
                    bean.add(quantityFields[i], constraints);
                }
                if (totalFields[i] != null)
                {
                    constraints.gridx = 2;
                    constraints.insets = factory.getInsets("defaultFieldRight");
                    constraints.fill = GridBagConstraints.NONE;
                    bean.add(totalFields[i], constraints);
                }
            }
            constraints.gridwidth = 2;

            // layout the bottom label and field
            if (size > 1)
            {
                constraints.gridy = labels.length - 1;
                constraints.gridx = 0;
                constraints.gridwidth = 1;
                constraints.gridheight = 1;
                constraints.insets = factory.getInsets("defaultLabelBottom");
                constraints.fill = GridBagConstraints.HORIZONTAL;
                bean.add(labels[size - 1], constraints);

                constraints.gridx = 1;
                /*if (addWeight)
                {
                    constraints.weighty = 1.0;
                    //constraints.weightx = 1.0;
                }
                constraints.fill = GridBagConstraints.NONE;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                constraints.insets = factory.getInsets("defaultFieldBottom");
                bean.add(quantityFields[size - 1], constraints);

                
                
                constraints.gridx = 2;*/
                if (addWeight)
                {
                    constraints.weighty = 1.0;
                    //constraints.weightx = 1.0;
                }
                constraints.fill = GridBagConstraints.NONE;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                constraints.insets = factory.getInsets("defaultFieldBottom");

                bean.add(totalFields[size - 1], constraints);
            }
            
           //panelScrollPane.setViewportView(bean);
           panelScrollPane.getViewport().setView(bean);
           thisPanel.add(panelScrollPane);
        }

}
