/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DiscAmountEntryBean.java /main/19 2012/10/16 17:37:29 cgreene Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 09/10/12 - Popup menu implementation
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 11/03/08 - transaction tax reason codes updates
 * ===========================================================================
     $Log:
      6    .v8x      1.4.1.0     3/8/2007 4:18:18 PM    Brett J. Larsen CR 4530
            - when default value not specified for a code list - display a
           blank string

           this is valid for both editable and non-editable combo boxes
      5    360Commerce1.4         1/25/2006 4:10:58 PM   Brett J. Larsen merge
           7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce1.3         1/22/2006 11:45:24 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce1.2         3/31/2005 4:27:45 PM   Robert Pearse
      2    360Commerce1.1         3/10/2005 10:20:57 AM  Robert Pearse
      1    360Commerce1.0         2/11/2005 12:10:35 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/17/2005 16:18:49    Deepanshu       CR
           6128: Migration from Gap. Updated initComponents() to localize
           reasonCodeLabel
      3    360Commerce1.2         3/31/2005 15:27:45     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:20:57     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:10:35     Robert Pearse
     $
     Revision 1.8  2004/07/20 22:42:46  dcobb
     @scr 4377 Invalid Reason Code clears markdown fields
     Save the bean model in the cargo and clear the selected reason code.

     Revision 1.7  2004/07/17 19:21:23  jdeleau
     @scr 5624 Make sure errors are focused on the beans, if an error is found
     during validation.

     Revision 1.6  2004/03/22 19:27:00  cdb
     @scr 3588 Updating javadoc comments

     Revision 1.5  2004/03/22 03:49:28  cdb
     @scr 3588 Code Review Updates

     Revision 1.4  2004/03/16 17:15:22  build
     Forcing head revision

     Revision 1.3  2004/03/16 17:15:17  build
     Forcing head revision

     Revision 1.2  2004/02/11 20:56:27  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.5   Jan 27 2004 14:00:26   cdb
 * Added Damaged flag to UI for damage discounts
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.4   Jan 21 2004 12:55:46   cdb
 * Added pre-pending of code id for reason codes.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.3   Jan 07 2004 12:41:20   cdb
 * Updated class javadoc to mention configurability of combo box editability through the uicfg.xml files.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.2   Jan 06 2004 11:02:12   cdb
 * Enhanced configurability. When non-editable combo boxes are used, a default value is set if a previously existing reason code hasn't been selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Jan 05 2004 15:27:12   cdb
 * Made switching of reason code drop down to and from editable a configurable item.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Jan 05 2004 11:23:10   cdb
 * Initial revision.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.ui.beans;

import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.foundation.manager.gui.loader.UILoaderIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This Bean displays a Discount Amount and a Reason Code Form. It uses the
 * DecimalWithReasonBeanModel. This bean supports an editable combo box. To make
 * the combo box editable, add an EditableList bean property with property value
 * true in the workpanel of the corresponding
 * {@link UILoaderIfc#OVERLAY_SCREEN_SPEC_NAME} in the appropriate uicfg.xml
 * file. By default, it is not editable.
 * 
 * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
 * @version $Revision: /main/19 $
 */
public class DiscAmountEntryBean extends ValidatingBean
{
    private static final long serialVersionUID = -1448964136561855599L;

    /** Revision Number supplied by TeamConnection */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /** default text for amount label */
    public static String AMOUNT_LABEL = "Discount Amount:";

    /** default text for reason label */
    public static String REASON_LABEL = "Reason Code:";

    /** default text for chocie separator */
    public static String CHOICE_SEPARATOR = " - ";

    /** Label for Discount Amount */
    protected JLabel discAmountLabel;
    /** label for the list */
    protected JLabel choiceLabel = null;

    /** text for the label */
    protected String labelText = "Reason Code:";
    /** tag for the label */
    protected String labelTags = "ReasonCodeColonLabel";

    /** Amount of discount */
    protected CurrencyTextField discAmountField;
    /** the reason code of discount */
    protected ValidatingComboBox choiceList;
    /** vector with list of tags for choices **/
    protected Vector tag_list = null;
    /** vector with list of tag ids for choices **/
    protected Vector tag_list_ids = null;
    /** Indicates if code id should prepend code description in list **/
    protected boolean prependCodeID = false;

    /**
     * Default constructor.
     */
    public DiscAmountEntryBean()
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

        setName("DiscountAmountBean");

        setLabelText(REASON_LABEL);
        setLabelTags(SelectionListBean.REASON_CODE_LABEL);
        beanModel = new DecimalWithReasonBeanModel();
    }

    /**
     * Initialize the display components.
     */
    protected void initComponents()
    {
        discAmountLabel = uiFactory.createLabel(AMOUNT_LABEL, AMOUNT_LABEL, null, UI_LABEL);

        discAmountField = uiFactory.createCurrencyField("DiscAmountField", "true", "false", "true");
        discAmountField.setHorizontalAlignment(SwingConstants.RIGHT);
        discAmountField.setRequired(false);
        discAmountField.setMinLength(3);

        choiceLabel = uiFactory.createLabel(labelText, labelText, null, UI_LABEL);

        choiceList = uiFactory.createValidatingComboBox("ReasonCodeField", "false", "15");
        choiceList.setLabel(choiceLabel);
    }

    /**
     * Allows for editable combo box. Default is false.
     * 
     * @param editable True if choice list should be editable
     */
    public void setEditableList(String editable)
    {
        choiceList.setEditable(UIUtilities.getBooleanValue(editable));
    }

    /**
     * Allows for prepending the reason code ID to the description in the drop
     * down list.
     * 
     * @param prepend True if code ID should be prepended to reason code name
     */
    public void setPrependCodeID(String prepend)
    {
        prependCodeID = UIUtilities.getBooleanValue(prepend);
    }

    /**
     * Overrides JPanel setVisible() method to request focus.
     * 
     * @param aFlag True to make this component visible
     */
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(discAmountField);
        }
    }

    /**
     * Activates this bean.
     */
    public void activate()
    {
        super.activate();
        discAmountField.addFocusListener(this);
        choiceList.addFocusListener(this);
    }

    /**
     * Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        discAmountField.removeFocusListener(this);
        choiceList.removeFocusListener(this);
    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this, new JLabel[] { discAmountLabel, choiceLabel }, new JComponent[] {
                discAmountField, choiceList });
    }

    /**
     * Update the Model with changes.
     * 
     * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
     */
    public void updateModel()
    {
        if (beanModel instanceof DecimalWithReasonBeanModel)
        {
            DecimalWithReasonBeanModel myModel = (DecimalWithReasonBeanModel) beanModel;

            // if discount amount is spaces, then set value to 0.00
            if ("".equals(discAmountField.getText()))
            {
                myModel.setValue(BigDecimalConstants.ZERO_AMOUNT);
            }
            else
            {
                myModel.setValue(discAmountField.getDecimalValue());
            }
            myModel.setSelected(false);
            String selected = "";
            if (choiceList.getSelectedItem() != null)
            {
                selected = (String) choiceList.getSelectedItem();
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

    /**
     * Update the bean if It's been changed This bean uses the
     * DecimalWithReasonBeanModel.
     * 
     * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
     */
    protected void updateBean()
    {
        if(beanModel instanceof DecimalWithReasonBeanModel)
        {
            DecimalWithReasonBeanModel myModel =
                (DecimalWithReasonBeanModel)beanModel;

            tag_list = myModel.getReasonCodes();
            tag_list_ids = myModel.getReasonCodeKeys();
            String tag = null;
            String displayChoice = null;

            Vector<String> dataList = new Vector<String>();
            for (int i = 0; i < tag_list.size() ; i++)
            {
               tag = (String)tag_list.elementAt(i);
               displayChoice = getDisplayChoice(tag);
               dataList.add(displayChoice);
            }

            discAmountField.setDecimalValue(myModel.getValue());
            tag = myModel.getSelectedReason();
            if (tag.equals(""))
            {
                tag = myModel.getDefaultValue();
            }
            choiceList.setModel(new ValidatingComboBoxModel (dataList));
            choiceList.setSelectedItem(getDisplayChoice(tag));
        }
    }

    /**
     * Provides uniform way to display choices
     * 
     * @param tag The internationalizable reason code description key
     * @return list entry corresponding to a given reason code
     */
    public String getDisplayChoice(String tag)
    {

        String displayChoice = retrieveText(tag,tag);
        if (!Util.isEmpty(tag))
        {
            retrieveText(tag,tag);
            if (prependCodeID)
            {
                int selectedCodeIndex = tag_list.indexOf(tag);
                if (!(selectedCodeIndex == -1))
                {
                    displayChoice = tag_list_ids.elementAt(selectedCodeIndex)
                                    + CHOICE_SEPARATOR
                                    + displayChoice;
                }
            }
        }
        return displayChoice;
    }

    /**
     * Sets the label for the selection list field.
     * 
     * @param text the label
     **/
    public void setLabelText(String text)
    {
        labelText = text;
    }

    /**
     * Sets the label tag for the selection list field.
     * 
     * @param text the label
     **/
    public void setLabelTags(String text)
    {
        labelTags = text;
        updatePropertyFields();
    }

    /**
     * Updates property-based fields.
     **/
    protected void updatePropertyFields()
    { // begin updatePropertyFields()
        super.updatePropertyFields();
        discAmountLabel.setText(retrieveText("DiscountAmountLabel", discAmountLabel));
        discAmountField.setLabel(discAmountLabel);
        choiceLabel.setText(retrieveText("ReasonCodeLabel", choiceLabel));
        choiceList.setLabel(choiceLabel);
    } // end updatePropertyFields()

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) + "(Revision " + getRevisionNumber()
                + ") @" + hashCode());
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(DiscAmountEntryBean.revisionNumber));
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        DiscAmountEntryBean bean = new DiscAmountEntryBean();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
