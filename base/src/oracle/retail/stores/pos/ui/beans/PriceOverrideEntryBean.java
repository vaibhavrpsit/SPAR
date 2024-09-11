/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PriceOverrideEntryBean.java /main/17 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abonda 09/04/13 - initialize collections
 *    cgreen 09/10/12 - Popup menu implementation
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 11/03/08 - transaction tax reason codes updates
 * ===========================================================================
     $Log:
      5    I18N_P2    1.2.3.1     1/7/2008 3:52:27 PM    Maisa De Camargo CR
           29826 - Setting the size of the combo boxes. This change was
           necessary because the width of the combo boxes used to grow
           according to the length of the longest content. By setting the
           size, we allow the width of the combo box to be set independently
           from the width of the dropdown menu.
      4    I18N_P2    1.2.3.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
           29826 - Setting the size of the combo boxes. This change was
           necessary because the width of the combo boxes used to grow
           according to the length of the longest content. By setting the
           size, we allow the width of the combo box to be set independently
           from the width of the dropdown menu.
      3    360Commerce 1.2         3/31/2005 4:29:28 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:21 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:24 PM  Robert Pearse
     $
     Revision 1.6  2004/07/19 17:49:06  awilliam
     @scr 4485 extra spaces in msg prompt

     Revision 1.5  2004/05/06 16:37:34  awilliam
     @scr 4485 ui error

     Revision 1.4  2004/03/16 17:15:18  build
     Forcing head revision
     Revision 1.3  2004/02/12 16:49:54  kll
     @scr 3821: account for out of index error

     Revision 1.2  2004/02/11 20:56:26  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.3   Jan 21 2004 12:55:48   cdb
 * Added pre-pending of code id for reason codes.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.2   Jan 07 2004 12:41:16   cdb
 * Updated class javadoc to mention configurability of combo box editability through the uicfg.xml files.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Jan 06 2004 11:02:14   cdb
 * Enhanced configurability. When non-editable combo boxes are used, a default value is set if a previously existing reason code hasn't been selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Jan 05 2004 18:43:14   cdb
 * Initial revision.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.ui.beans;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.loader.UILoaderIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean uses the DecimalWithReasonBeanModel.
 * 
 * @return DecimalWithReasonBeanModel The model of the bean. This bean supports
 *         an editable combo box. To make the combo box editable, add an
 *         EditableList bean property with property value true in the workpanel
 *         of the corresponding {@link UILoaderIfc#OVERLAY_SCREEN_SPEC_NAME} in
 *         the appropriate uicfg.xml file. By default, it is not editable.
 * @see #setModel
 * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
 * @version $Revision: /main/17 $
 */
public class PriceOverrideEntryBean extends ValidatingBean
{
    private static final long serialVersionUID = 4877686937412838102L;

    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/17 $";

    public static String CHOICE_SEPARATOR = " - ";

    /**
     * Label for Reason Code.
     */
    protected JLabel reasonCodeLabel = null;

    /** the reason code of discount */
    protected ValidatingComboBox reasonList;

    /**
     * local reference to model being used.
     */
    protected DecimalWithReasonBeanModel beanModel = null;

    /**
     * TextField for the Override Price.
     */
    protected CurrencyTextField overridePriceField = null;

    /**
     * Label for OverridePriceField.
     */
    protected JLabel overridePriceLabel = null;

    /**
     * Indicates when the model is dirty.
     */
    protected boolean dirtyModel = false;

    /** vector with list of tags for choices **/
    protected Vector tag_list = null;

    /** vector with list of tag ids for choices **/
    protected Vector tag_list_ids = null;

    /** Indicates if code id should prepend code description in list **/
    protected boolean prependCodeID = false;

    protected HashMap<String, String> reverseMap = new HashMap<String, String>(1);

    /**
     * Constructor
     */
    public PriceOverrideEntryBean()
    {
        super();
    }

    /**
     * Configures the class.
     */
    @Override
    public void configure()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);

        setName("PriceOverrideBean");

        overridePriceLabel = uiFactory.createLabel("overridePrice", "Override Price:", null, UI_LABEL);

        reasonCodeLabel = uiFactory.createLabel("reasonCode", "Reason Code:", null, UI_LABEL);

        overridePriceField = uiFactory.createCurrencyField("OverridePriceField", "false", "false", "false");

        reasonList = uiFactory.createValidatingComboBox("ReasonCodeField", "false", "20");
        reasonList.setName("ReasonCodeField");
        reasonList.setLabel(reasonCodeLabel);

        UIUtilities.layoutDataPanel(this, new JLabel[] { overridePriceLabel, reasonCodeLabel }, new JComponent[] {
                overridePriceField, reasonList });
        beanModel = new DecimalWithReasonBeanModel();
    }

    /**
     * Allows for editable combo box. Default is false.
     */
    public void setEditableList(String editable)
    {
        reasonList.setEditable(UIUtilities.getBooleanValue(editable));
    }

    /**
     * Allows for editable combo box. Default is false.
     */
    public void setPrependCodeID(String prepend)
    {
        prependCodeID = UIUtilities.getBooleanValue(prepend);
    }

    /**
     * Returns the bean model.
     * 
     * @return model object
     * @see oracle.retail.stores.pos.ui.beans.POSBaseBeanModel
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * updates the Model properties.
     * 
     * @return The model property value.
     * @see #setModel
     */
    @Override
    public void updateModel()
    {

        beanModel.setValue(overridePriceField.getDecimalValue());
        beanModel.setSelected(false);

        String selectedReasonCode = "";
        String selected = "";
        if (reasonList.getSelectedItem() != null)
        {
            selected = (String) reasonList.getSelectedItem();
        }
        StringTokenizer selectedItemTokens = new StringTokenizer(selected, CHOICE_SEPARATOR.trim());
        if (selectedItemTokens.hasMoreTokens())
        {
            String selectedItem = selectedItemTokens.nextToken().trim();
            selectedReasonCode = selectedItem;
        }

        if (reverseMap.containsKey(selectedReasonCode))
        {
            beanModel.setSelectedReasonCode(reverseMap.get(selectedReasonCode));
        }
        else
        {
            beanModel.setSelectedReasonCode(selectedReasonCode);
        }

        beanModel.setSelected(true);
    }

    /**
     * This method sets the model of this bean. This bean uses the
     * DecimalWithReasonBeanModel.
     * 
     * @param model The new DecimalWithReasonBeanModel to use.
     * @see #getModel
     * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
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

    /**
     * Update the bean if It's been changed
     */
    protected void updateBean()
    {
        if (dirtyModel)
        {
            overridePriceField.setDecimalValue(beanModel.getValue());

            tag_list = beanModel.getReasonCodes();
            tag_list_ids = beanModel.getReasonCodeKeys();
            // String[] choices = new String[tag_list.size()];
            String tag = null;
            String displayChoice = null;
            reverseMap.clear();

            Vector<String> dataList = new Vector<String>();
            for (int i = 0; i < tag_list.size(); i++)
            {
                tag = (String) tag_list.elementAt(i);
                displayChoice = getDisplayChoice(tag);
                dataList.add(displayChoice);
                reverseMap.put(displayChoice, tag);
            }

            tag = beanModel.getSelectedReason();
            if (!reasonList.isEditable() && tag.equals(""))
            {
                tag = beanModel.getDefaultValue();
            }

            reasonList.setModel(new ValidatingComboBoxModel(dataList));
            reasonList.setSelectedItem(getDisplayChoice(tag));

            dirtyModel = false;
        }
    }

    /**
     * Provides uniform way to display choices
     * 
     * @param tag The internationalizable reason code description key
     */
    public String getDisplayChoice(String tag)
    {

        String displayChoice = retrieveText(tag, tag);
        if (!Util.isEmpty(tag))
        {
            retrieveText(tag, tag);
            if (prependCodeID)
            {
                int selectedCodeIndex = tag_list.indexOf(tag);
                if (tag_list_ids.size() > 0)
                {
                    displayChoice = tag_list_ids.elementAt(selectedCodeIndex) + CHOICE_SEPARATOR + displayChoice;
                }
            }
        }
        return displayChoice;
    }

    /**
     * Updates property-based fields.
     **/
    protected void updatePropertyFields()
    { // begin updatePropertyFields()
        overridePriceLabel.setText(retrieveText("OverridePriceLabel", overridePriceLabel));
        reasonCodeLabel.setText(retrieveText("ReasonCodeLabel", reasonCodeLabel));

        overridePriceField.setLabel(overridePriceLabel);
        reasonList.errorMessage = reasonCodeLabel.getText();
    } // end updatePropertyFields()

    /**
     * Activates this bean.
     */
    public void activate()
    {
        super.activate();
        overridePriceField.addFocusListener(this);
        reasonList.addFocusListener(this);
    }

    /**
     * Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        overridePriceField.removeFocusListener(this);
        reasonList.removeFocusListener(this);
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: PriceOverrideBean (Revision " + getRevisionNumber() + ") @" + hashCode());
        return (strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        PriceOverrideEntryBean bean = new PriceOverrideEntryBean();
        bean.configure();

        UIUtilities.doBeanTest(bean);
    }
}
