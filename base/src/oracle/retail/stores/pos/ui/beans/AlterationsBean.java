/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AlterationsBean.java /main/19 2014/03/18 16:18:16 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   03/18/14 - Make allowable characters configurable.
 *    yiqzhao   03/17/14 - Allow hypen in item id and serial number.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sbeesnal  04/22/09 - Modified to remove all the components from panel
 *                         before new layout.
 *    sgu       03/11/09 - change text fields to alphanumerice field
 *
 * ===========================================================================
 * $Log:
 *    4    I18N_P2    1.2.1.0     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    3    360Commerce 1.2         3/31/2005 4:27:12 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:36 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:16  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Feb 06 2004 17:06:50   DCobb
 * setFieldRequired(ItemDescriptionField)
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.0   Aug 29 2003 16:09:34   CSchellenger
 * Initial revision.
 *
 *    Rev 1.7   Mar 13 2003 17:53:22   DCobb
 * Generalized fitToField parameter of createConstrainedTextAreaFieldPane and moved to UIFactory.
 * Resolution for POS SCR-1753: POS 6.0 Alterations Package
 *
 *    Rev 1.6   Mar 05 2003 18:21:16   DCobb
 * Generalized names of alteration attributes.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.5   Sep 23 2002 14:29:12   DCobb
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1809: Invalid Data screen from Alteration Instructions is incorrect
 *
 *    Rev 1.4   Aug 23 2002 12:04:28   DCobb
 * Dont' use right margin for ItemDescriptionField.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.3   Aug 22 2002 16:12:22   DCobb
 * Set alteration model name so that the alteration type can be printed and journaled.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.2   Aug 21 2002 17:20:18   DCobb
 * Wrapped all fields for the Alteration screens with  a scroll pane having a fixed viewport and with Vertical Scroll Bars as needed.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.1   Aug 21 2002 11:21:32   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//java imports
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
   This bean is used for displaying the Alterations information screen
 */
//---------------------------------------------------------------------
public class AlterationsBean extends ValidatingBean
{
    /**
        Customer Bean model
    */

    /**
        Revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /main/19 $";

    protected AlterationsBeanModel beanModel = new AlterationsBeanModel();
    protected String alterationsModelName = null;

    // Constant label indices
    protected static final int ITEM_DESC   = 0;
    protected static final int HEM         = ITEM_DESC + 1;
    protected static final int SLEEVE      = HEM + 1;
    protected static final int TAPER       = SLEEVE + 1;
    protected static final int NECK        = TAPER + 1;
    protected static final int WAIST       = NECK + 1;
    protected static final int REPAIRS     = WAIST + 1;
    protected static final int OTHER       = REPAIRS + 1;
    protected static final int ITEM_NUMBER = OTHER + 1;

    // Top panel label text aray
    protected static String labelText[] =
    {
        "Item Description", "Hem", "Sleeve", "Taper",
        "Neck", "Waist", "Repairs", "Other", "Item Number"
    };

    // text tags for the labels
    public static String[] labelTag =
    {
        "ItemDescriptionLabel", "HemLabel", "SleeveLabel", "TaperLabel",
        "NeckLabel", "WaistLabel", "RepairsLabel", "OtherLabel", "ItemNumberLabel"
    };

    //  label array
    protected JLabel[] fieldLabels = null;

    /**
        Fields that contain alteration data
    */
    protected ConstrainedTextField ItemDescriptionField = null;
    protected ConstrainedTextAreaField Value1Field = null;
    protected ConstrainedTextAreaField Value2Field = null;
    protected ConstrainedTextAreaField Value3Field = null;
    protected ConstrainedTextAreaField Value4Field = null;
    protected ConstrainedTextAreaField Value5Field = null;
    protected ConstrainedTextAreaField Value6Field = null;
    protected ConstrainedTextField ItemNumberField = null;

    /**
     * Wrapper for the free text fields
     */
    protected JScrollPane Value1Pane = null;
    protected JScrollPane Value2Pane = null;
    protected JScrollPane Value3Pane = null;
    protected JScrollPane Value4Pane = null;
    protected JScrollPane Value5Pane = null;
    protected JScrollPane Value6Pane = null;

    /**
        flag that indicates if the model has been changed
    */
    protected boolean dirtyModel = false;

    /**
        editable indicator
    **/
    protected boolean editableFields;

    //---------------------------------------------------------------------
    /**
       Default Constructor.
    */
    //---------------------------------------------------------------------
    public AlterationsBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     * Configures the class.
     */
    public void configure()
    {
        setName("AlterationsBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initComponents();
    }

    //--------------------------------------------------------------------------
    /**
     *     Initialize this bean's components.
     */
    protected void initComponents()
    {
        initLabels();
        initFields();
    }

    //--------------------------------------------------------------------------
    /**
     *    Initialize the setting for the data fields.
     */
    protected void initFields()
    {
        Font font        = UIManager.getFont("TextArea.font");
        ItemDescriptionField = uiFactory.createConstrainedField
              ("ItemDescriptionField", "1", "60", "40");
        ItemDescriptionField.setFont(font);
        ItemDescriptionField.setMinimumSize(ItemDescriptionField.getPreferredSize());

        ItemNumberField = uiFactory.createAlphaNumericPlusField
              ("ItemNumberField", "1", "14", false, '-');
        
        //TODO:  border to look like Panes
        ItemNumberField.setFont(font);
        ItemNumberField.setMinimumSize(ItemNumberField.getPreferredSize());

        Value1Pane = uiFactory.createConstrainedTextAreaFieldPane
            ("Value1Field", "0", "60", "43", "true", "true",
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
            true);
        Value1Pane.setName("Value1Pane");
        Value1Field =
            (ConstrainedTextAreaField)Value1Pane.getViewport().getView();

        Value2Pane = uiFactory.createConstrainedTextAreaFieldPane
            ("Value2Field", "0", "60", "43", "true", "true",
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
            true);
        Value2Pane.setName("Value2Pane");
        Value2Field =
            (ConstrainedTextAreaField)Value2Pane.getViewport().getView();

        Value3Pane = uiFactory.createConstrainedTextAreaFieldPane
            ("Value3Field", "0", "60", "43", "true", "true",
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
            true);
        Value3Pane.setName("Value3Pane");
        Value3Field =
            (ConstrainedTextAreaField)Value3Pane.getViewport().getView();

        Value4Pane = uiFactory.createConstrainedTextAreaFieldPane
            ("Value4Field", "0", "60", "43", "true", "true",
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
            true);
        Value4Pane.setName("Value4Pane");
        Value4Field =
            (ConstrainedTextAreaField)Value4Pane.getViewport().getView();

        Value5Pane = uiFactory.createConstrainedTextAreaFieldPane
            ("Value5Field", "0", "60", "43", "true", "true",
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
            true);
        Value5Pane.setName("Value5Pane");
        Value5Field =
            (ConstrainedTextAreaField)Value5Pane.getViewport().getView();

        Value6Pane = uiFactory.createConstrainedTextAreaFieldPane
            ("Value6Field", "0", "60", "43", "true", "true",
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
            true);
        Value6Pane.setName("Value6Pane");
        Value6Field =
            (ConstrainedTextAreaField)Value6Pane.getViewport().getView();
    }

    //--------------------------------------------------------------------------
    /**
     * Initializes the setting for the field labels.
     */
    protected void initLabels()
    {
        int numLabels = labelTag.length;
        fieldLabels = new JLabel[numLabels];

        for(int i = 0; i < numLabels; i++)
        {
            String text = retrieveText(labelTag[i], labelText[i]) + " ";
            fieldLabels[i] = uiFactory.createLabel(text, null, UI_LABEL);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Initializes the layout and lays out the components.
     * @param modelName alterations model name (e.g. Pants)
     */
    protected void initializeLayout(String modelName)
    {
        JLabel[] modelLabels = null;
        JComponent[] modelComps = null;

        if (modelName.equals("Pants") || modelName.equals("Skirt"))
        {
            modelLabels = new JLabel[6];
            modelLabels[0] = fieldLabels[ITEM_DESC];
            modelLabels[1] = fieldLabels[HEM];
            modelLabels[2] = fieldLabels[TAPER];
            modelLabels[3] = fieldLabels[WAIST];
            modelLabels[4] = fieldLabels[OTHER];
            modelLabels[5] = fieldLabels[ITEM_NUMBER];

            modelComps = new JComponent[6];
            modelComps[0] = ItemDescriptionField;
            modelComps[1] = Value1Pane;
            modelComps[2] = Value2Pane;
            modelComps[3] = Value3Pane;
            modelComps[4] = Value4Pane;
            modelComps[5] = ItemNumberField;
        }
        else if (modelName.equals("Shirt"))
        {
            modelLabels = new JLabel[6];
            modelLabels[0] = fieldLabels[ITEM_DESC];
            modelLabels[1] = fieldLabels[SLEEVE];
            modelLabels[2] = fieldLabels[TAPER];
            modelLabels[3] = fieldLabels[NECK];
            modelLabels[4] = fieldLabels[OTHER];
            modelLabels[5] = fieldLabels[ITEM_NUMBER];

            modelComps = new JComponent[6];
            modelComps[0] = ItemDescriptionField;
            modelComps[1] = Value1Pane;
            modelComps[2] = Value2Pane;
            modelComps[3] = Value3Pane;
            modelComps[4] = Value4Pane;
            modelComps[5] = ItemNumberField;
        }
        else if (modelName.equals("Coat") || modelName.equals("Dress"))
        {
            modelLabels = new JLabel[8];
            modelLabels[0] = fieldLabels[ITEM_DESC];
            modelLabels[1] = fieldLabels[HEM];
            modelLabels[2] = fieldLabels[SLEEVE];
            modelLabels[3] = fieldLabels[TAPER];
            modelLabels[4] = fieldLabels[NECK];
            modelLabels[5] = fieldLabels[WAIST];
            modelLabels[6] = fieldLabels[OTHER];
            modelLabels[7] = fieldLabels[ITEM_NUMBER];

            modelComps = new JComponent[8];
            modelComps[0] = ItemDescriptionField;
            modelComps[1] = Value1Pane;
            modelComps[2] = Value2Pane;
            modelComps[3] = Value3Pane;
            modelComps[4] = Value4Pane;
            modelComps[5] = Value5Pane;
            modelComps[6] = Value6Pane;
            modelComps[7] = ItemNumberField;
        }
        else
        {
            modelLabels = new JLabel[4];
            modelLabels[0] = fieldLabels[ITEM_DESC];
            modelLabels[1] = fieldLabels[REPAIRS];
            modelLabels[2] = fieldLabels[OTHER];
            modelLabels[3] = fieldLabels[ITEM_NUMBER];

            modelComps = new JComponent[4];
            modelComps[0] = ItemDescriptionField;
            modelComps[1] = Value1Pane;
            modelComps[2] = Value2Pane;
            modelComps[3] = ItemNumberField;
        }
        ItemDescriptionField.setLabel(fieldLabels[ITEM_DESC]);
        ItemNumberField.setLabel(fieldLabels[ITEM_NUMBER]);
        setFieldRequired(ItemDescriptionField, true);

        removeAll();

        UIUtilities.layoutDataPanel(this, modelLabels, modelComps);
    }

    //---------------------------------------------------------------------
    /**
       Set the alterationsModel <P>
       @return
    */
    //---------------------------------------------------------------------
    public void setAlterationsModel(String myModelName)
    {
        initializeLayout(myModelName);
        dirtyModel = true;
        updateBean();
        alterationsModelName = myModelName;
    }

    //----------------------------------------------------------------------------
    /**
     * The framework calls this method just before display
     */
    //----------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        updateModel();
    }

    //----------------------------------------------------------------------------
    /**
        Returns the base bean model.<P>
        @return POSBaseBeanModel
    */
    //----------------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //---------------------------------------------------------------------
    /**
        Updates the model from the screen.
    */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setAlterationsModel(alterationsModelName);
        beanModel.setItemDescription(ItemDescriptionField.getText());
        beanModel.setItemNumber(ItemNumberField.getText());
        beanModel.setValue1(Value1Field.getText());
        beanModel.setValue2(Value2Field.getText());
        beanModel.setValue3(Value3Field.getText());
        beanModel.setValue4(Value4Field.getText());
        beanModel.setValue5(Value5Field.getText());
        beanModel.setValue6(Value6Field.getText());
    }

    //---------------------------------------------------------------------
    /**
       Sets the model property  value.<P>
       @param model UIModelIfc the new value for the property.
    */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set AlterationsBean " +
                "model to null");
        }
        else
        {
            if (model instanceof AlterationsBeanModel)
            {
                beanModel = (AlterationsBeanModel)model;
                dirtyModel = true;
                updateBean();
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the information displayed on the screen's if the model's
        been changed.
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        String name = beanModel.getAlterationsModel();
        if (!name.equals(""))
        {
            alterationsModelName = name;
        }
        ItemDescriptionField.setText(beanModel.getItemDescription());
        ItemNumberField.setText(beanModel.getItemNumber());
        Value1Field.setText(beanModel.getValue1());
        Value2Field.setText(beanModel.getValue2());
        Value3Field.setText(beanModel.getValue3());
        Value4Field.setText(beanModel.getValue4());
        Value5Field.setText(beanModel.getValue5());
        Value6Field.setText(beanModel.getValue6());

        // Set the dirtyModel flag
        dirtyModel = false;
    }

    //---------------------------------------------------------------------------
    /**
     *  Update property fields.
     */
    //---------------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        int numLabels = labelTag.length;
        for(int i = 0; i < numLabels; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTag[i], labelText[i]) + "  ");
        }
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: AlterationsBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        AlterationsBean bean = new AlterationsBean();
        AlterationsBeanModel model = new AlterationsBeanModel();
        bean.setModel(model);
        bean.setAlterationsModel("Pants");
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }

}
