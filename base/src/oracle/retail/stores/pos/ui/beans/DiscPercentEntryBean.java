/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DiscPercentEntryBean.java /main/21 2012/10/16 17:37:29 cgreene Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 09/10/12 - Popup menu implementation
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    rsnaya 10/21/11 - Reason codenot displayed compelety in ui
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    mweis  02/09/10 - instant credit discount never shows up on the user
 *                      interface
 *    abonda 01/03/10 - update header date
 *    ohorne 03/10/09 - fix for percent inflation when using IBM jre on client
 *                      and Sun jre on server
 *    acadar 11/03/08 - transaction tax reason codes updates
 * ===========================================================================
     $Log:
      7    .v8x      1.5.1.0     3/8/2007 4:18:19 PM    Brett J. Larsen CR 4530
            - when default value not specified for a code list - display a
           blank string

           this is valid for both editable and non-editable combo boxes
      6    360Commerce1.5         1/25/2006 4:10:58 PM   Brett J. Larsen merge
           7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      5    360Commerce1.4         1/22/2006 11:45:24 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      4    360Commerce1.3         12/13/2005 4:42:44 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce1.2         3/31/2005 4:27:46 PM   Robert Pearse
      2    360Commerce1.1         3/10/2005 10:21:00 AM  Robert Pearse
      1    360Commerce1.0         2/11/2005 12:10:37 PM  Robert Pearse
     $:
      6    .v700     1.2.1.2     11/17/2005 16:20:33    Deepanshu       CR
           6128: Migration from Gap. Updated updatePropertyFields()
      5    .v700     1.2.1.1     10/26/2005 14:51:15    Jason L. DeLeau 6072:
           FIx NPE causing system hang.
      4    .v700     1.2.1.0     9/19/2005 18:01:23     Jason L. DeLeau Make
           sure CurrencyTextFields can have a blank default value.
      3    360Commerce1.2         3/31/2005 15:27:46     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:21:00     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:10:37     Robert Pearse
     $
           Base-lining of 7.1_LA
      3    360Commerce1.2         3/31/2005 3:27:46 PM   Robert Pearse
      2    360Commerce1.1         3/10/2005 10:21:00 AM  Robert Pearse
      1    360Commerce1.0         2/11/2005 12:10:37 PM  Robert Pearse
     $: DiscPercentEntryBean.java,v $
     $
     $:
      6    .v710     1.2.2.2     10/29/2005 13:40:56    Brett J. Larsen CR 3965
           v702 -> v710 merge
           WebDecimalWithReasonBean.getValue() now returns a null by default
           added logic to avoid a null pointer exception
      5    .v710     1.2.2.1     10/24/2005 14:20:53    Charles Suehs   Merged
           from .v700 to fix CR 3965.
      4    .v710     1.2.2.0     10/20/2005 18:24:29    Charles Suehs   Merge
           from DiscPercentEntryBean.java, Revision 1.2.1.0
      3    360Commerce1.2         3/31/2005 15:27:46     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:21:00     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:10:37     Robert Pearse
     $
     Revision 1.9  2004/07/20 22:42:46  dcobb
     @scr 4377 Invalid Reason Code clears markdown fields
     Save the bean model in the cargo and clear the selected reason code.

     Revision 1.8  2004/07/17 19:21:23  jdeleau
     @scr 5624 Make sure errors are focused on the beans, if an error is found
     during validation.

     Revision 1.7  2004/03/22 19:27:00  cdb
     @scr 3588 Updating javadoc comments

     Revision 1.6  2004/03/22 03:49:28  cdb
     @scr 3588 Code Review Updates

     Revision 1.5  2004/03/16 17:15:22  build
     Forcing head revision

     Revision 1.4  2004/03/16 17:15:17  build
     Forcing head revision

     Revision 1.3  2004/03/10 19:36:13  cdb
     @scr 3588 Modified percent entry bean to use PercentageTextField.
     Updated PercentageDocument to allow whole numbers only.

     Revision 1.2  2004/02/11 20:56:26  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.6   Jan 27 2004 14:00:28   cdb
 * Added Damaged flag to UI for damage discounts
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.5   Jan 21 2004 12:55:46   cdb
 * Added pre-pending of code id for reason codes.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.4   Jan 07 2004 12:41:20   cdb
 * Updated class javadoc to mention configurability of combo box editability through the uicfg.xml files.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.3   Jan 06 2004 11:02:14   cdb
 * Enhanced configurability. When non-editable combo boxes are used, a default value is set if a previously existing reason code hasn't been selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.2   Jan 05 2004 15:27:12   cdb
 * Made switching of reason code drop down to and from editable a configurable item.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Dec 30 2003 18:39:48   cdb
 * Avoiding setting reson when not model not selected causes problems when invalid values are entered - after acknowledging error, index 0 is selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Dec 23 2003 17:33:50   cdb
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
import java.math.BigDecimal;

/**
 * This Bean displays a Discount percent field and a Reason Code Form.
 * <P>
 * It uses the DecimalWithReasonBeanModel. This bean supports an editable combo
 * box. To make the combo box editable, add an EditableList bean property with
 * property value true in the workpanel of the corresponding
 * {@link UILoaderIfc#OVERLAY_SCREEN_SPEC_NAME} in the appropriate uicfg.xml
 * file. By default, it is not editable.
 * 
 * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
 * @version $Revision: /main/21 $
 */
public class DiscPercentEntryBean extends ValidatingBean
{
    private static final long serialVersionUID = -4068358594231713119L;

    /** Revision Number supplied by TeamConnection */
    public static final String revisionNumber = "$Revision: /main/21 $";

    /** default text for percent label */
    public static String PERCENT_LABEL = "Discount Percent:";
    /** default text for reason label */
    public static String REASON_LABEL  = "Reason Code:";

    /** default text for choice separation */
    public static String CHOICE_SEPARATOR = " - ";

    /** label for the Discount Percent */
    protected JLabel discPercentLabel;
    /** label for the list */
    protected JLabel choiceLabel = null;
    /** text for the label */
    protected String labelText = "Reason Code:";
    /** tag for the label */
    protected String labelTags = "ReasonCodeColonLabel";

    /** the amount of discount */
    protected PercentageTextField discPercentField;
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
    public DiscPercentEntryBean()
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

        setName("DiscountPercentEntryBean");
        setLabelText(REASON_LABEL);
        setLabelTags(SelectionListBean.REASON_CODE_LABEL);
        beanModel = new DecimalWithReasonBeanModel();
    }

    /**
     * Initialize the display components.
     */
    protected void initComponents()
    {

        discPercentLabel = uiFactory.createLabel(PERCENT_LABEL, PERCENT_LABEL, null, UI_LABEL);

        discPercentField = new PercentageTextField();
        discPercentField.setName("DiscPercentField");
        discPercentField.setHorizontalAlignment(SwingConstants.RIGHT);
        discPercentField.setColumns(8);

        choiceLabel = uiFactory.createLabel(labelText, labelText, null, UI_LABEL);

        choiceList = uiFactory.createValidatingComboBox("ReasonCodeField", "false", "20");
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
     * Allows for editable combo box. Default is false.
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
            setCurrentFocus(discPercentField);
        }
    }

    /**
     * Activates this bean.
     */
    public void activate()
    {
        super.activate();
        discPercentField.addFocusListener(this);
        choiceList.addFocusListener(this);
    }

    /**
     * Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        discPercentField.removeFocusListener(this);
        choiceList.removeFocusListener(this);
    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        JLabel[] labels = { discPercentLabel, choiceLabel };
        JComponent[] components = { discPercentField, choiceList };
        UIUtilities.layoutDataPanel(this, labels, components);
    }

    /**
     * This updates the local bean model.
     * 
     * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
     */
    public void updateModel()
    {
        if(beanModel instanceof DecimalWithReasonBeanModel)
        {
            DecimalWithReasonBeanModel myModel =
                (DecimalWithReasonBeanModel)beanModel;

            // if discount percent field is spaces, then set value to 0.00
            if ("".equals(discPercentField.getText().trim()))
            {
                myModel.setValue(BigDecimalConstants.ZERO_AMOUNT);
            }
            else
            {
                //commented out because this division method causes 
                //10 to become 10000000000000000000 when the transaction 
                //is marshalled between an IBM jre on the client and 
                //a Sun JRE on the server.
                //BigDecimal dp = discPercentField.getDecimalValue().divide(new BigDecimal("100.00"));

                BigDecimal percent = discPercentField.getDecimalValue();            	
                BigDecimal dp = percent.movePointLeft(2); //divide by 100 
                myModel.setValue(dp);
            }

            myModel.setSelected(false);

            String selected = "";
            if (choiceList.getSelectedItem() != null)
            {
                selected = (String)choiceList.getSelectedItem();
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
     * This method updates the Bean if when changed by setModel
     * 
     * @see #setModel(oracle.retail.stores.foundation.manager.gui.UIModelIfc
     *      model)
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

            if(myModel != null && myModel.getValue() != null)
            {
            	//removed following code since it was rounding the percentage to the nearest decimal
            	/*double d = myModel.getValue().movePointRight(2).doubleValue();
            	long  longValue = Math.round(d);
            	discPercentField.setText(Long.toString(longValue));*/
                
                BigDecimal pct = myModel.getValue().movePointRight(2);
                if (pct.scale() > 2)
                {
                    // PercentTextField can only handle 100.00 - 0.00
                    pct = pct.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                discPercentField.setText(pct.toPlainString());
            }
            else
            {
            	discPercentField.setText("");
            }

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
     */
    public void setLabelText(String text)
    {
        labelText = text;
    }

    /**
     * Sets the label tag for the selection list field.
     * 
     * @param text the label
     */
    public void setLabelTags(String text)
    {
        labelTags = text;
        updatePropertyFields();
    }

    /**
     * Updates property-based fields.
     */
    protected void updatePropertyFields()
    {
        super.updatePropertyFields();

        discPercentLabel.setText(retrieveText("DiscountPercentLabel", discPercentLabel));
        discPercentField.setLabel(discPercentLabel);
        choiceLabel.setText(retrieveText("ReasonCodeLabel", choiceLabel));
        choiceList.setLabel(choiceLabel);
    }

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
        return (Util.parseRevisionNumber(DiscPercentEntryBean.revisionNumber));
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        DiscPercentEntryBean bean = new DiscPercentEntryBean();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
