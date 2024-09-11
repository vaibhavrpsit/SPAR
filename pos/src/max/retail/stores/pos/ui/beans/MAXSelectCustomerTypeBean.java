/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Aug 21, 2018		Bhanu Priya		Changes for Capture PAN CARD CR
 *
 ********************************************************************************/

package max.retail.stores.pos.ui.beans;

import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.AlphaNumericTextField;
import oracle.retail.stores.pos.ui.beans.SelectionListBean;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;


public class MAXSelectCustomerTypeBean extends ValidatingBean
{
    private static final long serialVersionUID = -1448964136561855599L;

    /** Revision Number supplied by TeamConnection */
    public static final String revisionNumber = "$Revision: /main/19 $";


    public static String CUSTOMER_TYPE = "Customer Type:";

    /** default text for chocie separator */
    public static String CHOICE_SEPARATOR = "-";

    

    protected JLabel customerTypeChoiceLabel = null;
    protected String labelText = "Customer Type:";
    protected String labelTags = "ReasonCodeColonLabel";

  

    @SuppressWarnings("rawtypes")
	protected ValidatingComboBox customerTypeList;
    @SuppressWarnings("rawtypes")
	protected Vector tag_list = null;
    protected Vector tag_list_ids = null;
    protected boolean prependCodeID = false;

    /**
     * Default constructor.
     */
    public MAXSelectCustomerTypeBean()
    {
        super();
    }

    /**
     * Initialize the class.
     */
    public void configure()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();

        setName("SelectCustomerTypeBean");

        setLabelText(CUSTOMER_TYPE);
        setLabelTags("Customer Type");
        beanModel = new MAXSelectCustomerTypeBeanModel();
    }

    /**
     * Initialize the display components.
     */
    @SuppressWarnings("deprecation")
	protected void initComponents()
    {
    	
    	customerTypeChoiceLabel = uiFactory.createLabel(labelText, labelText, null, UI_LABEL);

    	customerTypeList = uiFactory.createValidatingComboBox("customerTypeField", "false", "25");
    	customerTypeList.setLabel(customerTypeChoiceLabel);
    	customerTypeList.setRequired(true);
      
    }

  
    public void setEditableList(String editable)
    {
    	customerTypeList.setEditable(UIUtilities.getBooleanValue(editable));
    }


    public void setPrependCodeID(String prepend)
    {
        prependCodeID = UIUtilities.getBooleanValue(prepend);
    }

    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(customerTypeList);
        }
    }

    /**
     * Activates this bean.
     */
    public void activate()
    {
        super.activate();
      
        customerTypeList.addFocusListener(this);
    }

    /**
     * Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        customerTypeList.removeFocusListener(this);
    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this, new JLabel[] { customerTypeChoiceLabel }, new JComponent[] {
        		customerTypeList });
    }

  
    public void updateModel()
    {
        if (beanModel instanceof MAXSelectCustomerTypeBeanModel)
        {
        	MAXSelectCustomerTypeBeanModel model = (MAXSelectCustomerTypeBeanModel) beanModel;

        	model.setSelected(false);
            String selected = "";
            if (customerTypeList.getSelectedItem() != null)
            {
                selected = (String) customerTypeList.getSelectedItem();
            }
            StringTokenizer selectedItemTokens = new StringTokenizer(selected, CHOICE_SEPARATOR.trim());
            if (selectedItemTokens.hasMoreTokens())
            {
                String selectedItem = selectedItemTokens.nextToken().trim();
                model.setSelectedReasonCode(selectedItem);
            }
            else
            {
            	model.setSelectedReasonCode("");
            }
        }
    }

    @SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	protected void updateBean()
    {
        if(beanModel instanceof MAXSelectCustomerTypeBeanModel )
        {
        	MAXSelectCustomerTypeBeanModel model = (MAXSelectCustomerTypeBeanModel) beanModel;

          //  tag_list = myModel.getReasonCodes();
            tag_list = model.getCustomerType();
         //   tag_list_ids = myModel.getReasonCodeKeys();
            String tag = null;
            String displayChoice = null;
          
            Vector<String> dataList = new Vector<String>();
            for (int i = 0; i < tag_list.size() ; i++)
            {
               tag = (String)tag_list.elementAt(i);
               displayChoice = getDisplayChoice(tag);
               dataList.add(displayChoice);
            }

        
            tag = model.getSelectedReason();
            if (tag.equals(""))
            {
                tag = model.getDefaultValue();
            }
            
         
            customerTypeList.setModel(new ValidatingComboBoxModel (dataList));
            customerTypeList.setSelectedItem(getDisplayChoice(tag));
            customerTypeList.setSelectedIndex(0);
        }
    }

    
    public String getDisplayChoice(String tag)
    {

        String displayChoice = retrieveText(tag,tag);
//        if (!Util.isEmpty(tag))
//        {
//            retrieveText(tag,tag);
//            if (prependCodeID)
//            {
//            }
//        }
        return displayChoice;
    }


    public void setLabelText(String text)
    {
        labelText = text;
    }

    public void setLabelTags(String text)
    {
        labelTags = text;
        updatePropertyFields();
    }

    /**
     * Updates property-based fields.
     **/
    @SuppressWarnings("deprecation")
	protected void updatePropertyFields()
    { // begin updatePropertyFields()
        super.updatePropertyFields();
        customerTypeChoiceLabel.setText(retrieveText("customerTypeField", customerTypeChoiceLabel));
        customerTypeList.setLabel(customerTypeChoiceLabel);
    } // end updatePropertyFields()

 
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) + "(Revision " + getRevisionNumber()
                + ") @" + hashCode());
    }

  
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(MAXSelectCustomerTypeBean.revisionNumber));
    }

  
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        MAXSelectCustomerTypeBean bean = new MAXSelectCustomerTypeBean();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
