/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 
  Rev 1.0	Atul Shukla		23/April/2018		Initial Draft: Changes for Employee Discount FES functionality
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.util.StringTokenizer;
import java.util.Vector;

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


public class MAXEmployeeDiscountBean extends ValidatingBean
{
    private static final long serialVersionUID = -1448964136561855599L;

    /** Revision Number supplied by TeamConnection */
    public static final String revisionNumber = "$Revision: /main/19 $";

    public static String EMPLOYEE_ID_LABEL = "Employee ID:";

    public static String COMPANY_NAME = "Company Name:";

    /** default text for chocie separator */
    public static String CHOICE_SEPARATOR = " - ";

    protected JLabel employeeIdLabel;

    protected JLabel companyNameChoiceLabel = null;
    protected String labelText = "Company Name:";
    protected String labelTags = "ReasonCodeColonLabel";

    protected AlphaNumericTextField employeeIdField;

    @SuppressWarnings("rawtypes")
	protected ValidatingComboBox companyNameList;
    @SuppressWarnings("rawtypes")
	protected Vector tag_list = null;
    protected Vector tag_list_ids = null;
    protected boolean prependCodeID = false;

    /**
     * Default constructor.
     */
    public MAXEmployeeDiscountBean()
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

        setName("EmployeeDiscountBean");

        setLabelText(COMPANY_NAME);
        setLabelTags("Company Name");
        beanModel = new MAXEmployeeDiscountBeanModel();
    }

    /**
     * Initialize the display components.
     */
    @SuppressWarnings("deprecation")
	protected void initComponents()
    {
    	employeeIdLabel = uiFactory.createLabel(EMPLOYEE_ID_LABEL, EMPLOYEE_ID_LABEL, null, UI_LABEL);

        employeeIdField = uiFactory.createAlphaNumericField("EmployeeIdField", "4", "10");
        employeeIdField.setHorizontalAlignment(SwingConstants.RIGHT);
        employeeIdField.setRequired(true);
        employeeIdField.setMinLength(4);

        companyNameChoiceLabel = uiFactory.createLabel(labelText, labelText, null, UI_LABEL);

        companyNameList = uiFactory.createValidatingComboBox("CompanyNameField", "false", "15");
        companyNameList.setLabel(companyNameChoiceLabel);
        companyNameList.setRequired(true);
      
    }

  
    public void setEditableList(String editable)
    {
    	companyNameList.setEditable(UIUtilities.getBooleanValue(editable));
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
            setCurrentFocus(employeeIdField);
        }
    }

    /**
     * Activates this bean.
     */
    public void activate()
    {
        super.activate();
        employeeIdField.addFocusListener(this);
        companyNameList.addFocusListener(this);
    }

    /**
     * Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        employeeIdField.removeFocusListener(this);
        companyNameList.removeFocusListener(this);
    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this, new JLabel[] { employeeIdLabel, companyNameChoiceLabel }, new JComponent[] {
        		employeeIdField, companyNameList });
    }

  
    public void updateModel()
    {
        if (beanModel instanceof MAXEmployeeDiscountBeanModel)
        {
        	MAXEmployeeDiscountBeanModel myModel = (MAXEmployeeDiscountBeanModel) beanModel;

            // if discount amount is spaces, then set value to 0.00
            if ("".equals(employeeIdField.getText()))
            {
                myModel.setValue(BigDecimalConstants.ZERO_AMOUNT);
            }
            else
            {
                myModel.setEmpId((employeeIdField.getText()));
            }
            myModel.setSelected(false);
            String selected = "";
            if (companyNameList.getSelectedItem() != null)
            {
                selected = (String) companyNameList.getSelectedItem();
            }
            StringTokenizer selectedItemTokens = new StringTokenizer(selected, CHOICE_SEPARATOR.trim());
            if (selectedItemTokens.hasMoreTokens())
            {
                String selectedItem = selectedItemTokens.nextToken().trim();
                myModel.setSelectedReasonCode(selectedItem);
            }
            else
            {
                myModel.setSelectedReasonCode("");
            }
        }
    }

    @SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	protected void updateBean()
    {
        if(beanModel instanceof MAXEmployeeDiscountBeanModel)
        {
        	MAXEmployeeDiscountBeanModel myModel =
                (MAXEmployeeDiscountBeanModel)beanModel;

          //  tag_list = myModel.getReasonCodes();
            tag_list = myModel.getCompanyName();
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

            employeeIdField.setText((myModel.getEmpId()));
            tag = myModel.getSelectedReason();
            if (tag.equals(""))
            {
                tag = myModel.getDefaultValue();
            }
            companyNameList.setModel(new ValidatingComboBoxModel (dataList));
            companyNameList.setSelectedItem(getDisplayChoice(tag));
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
        employeeIdLabel.setText(retrieveText("EmployeeIdLabel", employeeIdLabel));
        employeeIdField.setLabel(employeeIdLabel);
        companyNameChoiceLabel.setText(retrieveText("CompanyNameLabel", companyNameChoiceLabel));
        companyNameList.setLabel(companyNameChoiceLabel);
    } // end updatePropertyFields()

 
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) + "(Revision " + getRevisionNumber()
                + ") @" + hashCode());
    }

  
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(MAXEmployeeDiscountBean.revisionNumber));
    }

  
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        MAXEmployeeDiscountBean bean = new MAXEmployeeDiscountBean();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
