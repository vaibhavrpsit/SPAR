/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditableItemInfoBean.java /main/12 2014/06/12 09:33:50 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abhinavs  10/03/14 - Setting selected clearance index from the dropdown
 *                         to include it in verifying the condition if atleast one
 *                         search criteria is given.
 *    yiqzhao   09/22/14 - Fix the issue when Department is not available.
 *    yiqzhao   09/22/14 - Add style and item type.
 *    abhinavs  06/11/14 - Miscellaneous Item search related cleanup
 *    abhinavs  05/13/14 - Fix to apply null check on color, size and uom list and
 *                         not display if null or 0
 *    yiqzha    03/21/14 - Enable item id wild card search in advance search
 *                         screen.
 *    tkshar    01/15/14 - removed price as it is not a criterion for advance
 *                         search
 *    tkshar    01/15/14 - modifed to always display the dropdown value 'All' for
 *                         Clearance, Taxable and discountable fields
 *    abhinavs  09/10/13 - Fix to prevent NPE while displaying google like search
 *                         results
 *    cgreen    06/05/13 - limit editable field sizes on item display screen
 *    cgreen    05/21/13 - implement price and clearance as criteria for item
 *                         search
 *    abhinavs  04/16/13 - Fix to set Manufacturer name in the search criteria
 *    tkshar    03/21/13 - made clearance and price editable in price inquiry
 *                         search
 *    abhine    02/20/13 - fix for Item Messages do not get displayed on the Item
 *                         Display screen
 *    tkshar    12/18/12 - made Clearance field visible
 *    hyin      11/12/12 - re-work on item search result screen to make it
 *                         editable. So a new search can be performed.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.stock.ItemColorIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.ItemStyleIfc;
import oracle.retail.stores.domain.stock.ItemTypeIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.ScannerSession;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class shows information for a single item.
 * 
 * @version $Revision: /main/12 $
 */
public class EditableItemInfoBean extends AbstractItemImageAndMessageBean implements DocumentListener
{
    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 5646274181861033394L;

    protected static final Logger logger = Logger.getLogger(EditableItemInfoBean.class);

    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/12 $";
    
    /** Shadows super class fields to allow for editable widgets. */
    protected JComponent[] fields = null;

    /**
     * Configure the class.
     */
    @Override
    public void configure()
    {
        setName("EditableItemInfoBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void removeUpdate(DocumentEvent de)
    {
        boolean isEmpty = false;

        if (isEmpty)
        {
            try
            {
                DeviceTechnicianIfc dt = (DeviceTechnicianIfc)Gateway.getDispatcher().getLocalTechnician(
                        DeviceTechnicianIfc.TYPE);
                if (dt != null)
                {
                    try
                    {
                        String sessionName = ScannerSession.TYPE;
                        ScannerSession scannerSession = (ScannerSession)dt.getDeviceSession(sessionName);
                        scannerSession.setEnabled(true);
                    }
                    catch (DeviceException e)
                    {
                        logger.error("setScannerData: deviceException", e);
                    }
                }
            }
            catch (TechnicianNotFoundException e)
            {
                logger.error("setScannerData: can't get deviceTechnician", e);
            }
            catch (Exception e)
            {
                logger.error("setScannerData: can't get deviceTechnician", e);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void insertUpdate(DocumentEvent e)
    {
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void changedUpdate(DocumentEvent e)
    {
    }

    /**
     * updates the Model properties.
     * 
     * @return The model property value.
     * @see #setModel
     */
    @SuppressWarnings("deprecation")
    @Override
    public void updateModel()
    {        
        beanModel.setItemNumber(replaceStar(((ConstrainedTextField)fields[ITEM_NUMBER]).getText()));
        beanModel.setItemDescription(((ConstrainedTextField)fields[DESCRIPTION]).getText());
        beanModel.setOnClearance(((ValidatingComboBox)fields[CLEARANCE]).getSelectedIndex() == 1);
        beanModel.setSelectedClearanceIdx(((ValidatingComboBox)fields[CLEARANCE]).getSelectedIndex());
        beanModel.setPrice(((CurrencyTextField)fields[PRICE]).getDecimalValue());
        if(beanModel.isSearchItemByManufacturer())
        {
            beanModel.setItemManufacturer(((ConstrainedTextField)fields[MANUFACTURER]).getText());
        }
        beanModel.setSelectedDeptIdx(((ValidatingComboBox)fields[DEPARTMENT]).getSelectedIndex());
        beanModel.setSelectedTypeIdx(((ValidatingComboBox)fields[TYPE]).getSelectedIndex());
        beanModel.setSelectedStyleIdx(((ValidatingComboBox)fields[STYLE]).getSelectedIndex());
        beanModel.setSelectedSizeIdx(((ValidatingComboBox)fields[SIZE]).getSelectedIndex());
        beanModel.setSelectedColorIdx(((ValidatingComboBox)fields[COLOR]).getSelectedIndex());
        beanModel.setSelectedUomIdx(((ValidatingComboBox)fields[MEASURE]).getSelectedIndex());
        if (beanModel.isItemSizeRequired())
        {
            beanModel.setSelectedTaxableIdx(((ValidatingComboBox)fields[TAX]).getSelectedIndex());
            beanModel.setSelectedDiscountableIdx(((ValidatingComboBox)fields[DISCOUNT]).getSelectedIndex());
        }
        else
        {
            beanModel.setSelectedTaxableIdx(((ValidatingComboBox)fields[TAX-1]).getSelectedIndex());
            beanModel.setSelectedDiscountableIdx(((ValidatingComboBox)fields[DISCOUNT-1]).getSelectedIndex());
        }       
    }
    
    /**
     * Re-do the layout by removing all widgets, re-initializing them and
     * triggering the layout manager. Calls {@link #revalidate()} afterwards.
     * <p>
     * The layout will change based on the size and planogram parameter
     * specified. The parameter can be changed at runtime.
     *
     * @see #initLayout()
     * @see #initComponents(int, String[])
     */
    protected void setupLayout()
    {
        // lay out data panel
        removeAll();
        if (beanModel == null)
        {
            initComponents(MAX_FIELDS, labelText);
        }
        else
        {
            if (beanModel.isItemSizeRequired())
            {
                if (beanModel.isUsePlanogramID())
                {
                    initComponents(MAX_FIELDS, labelText);
                }
                else
                {
                    initComponents(MAX_FIELDS - 1, labelTextSizeNoPlanogram);
                }
            }
            else
            {
                if (beanModel.isUsePlanogramID())
                {
                    initComponents(MAX_FIELDS - 1, labelTextNoSizePlanogram);
                }
                else
                {
                    initComponents(MAX_FIELDS - 2, labelTextNoSizeNoPlanogram);
                }

            }
        }
        initLayout();
        revalidate();
    }

    /**
     * Initialize the display components.
     */
    @SuppressWarnings("deprecation")
    protected void initComponents(int maxFields, String[] label)
    {
        labels = new JLabel[maxFields];
        fields = new JComponent[maxFields];

        for (int i = 0; i < maxFields; i++)
        {
            labels[i] = uiFactory.createLabel(label[i] + "label", label[i], null, UI_LABEL);
            
            if ((i == DEPARTMENT) || (i == TYPE) || (i == STYLE)|| (i == SIZE) || (i == COLOR) || (i == MEASURE) || (i == TAX))
            {
                fields[i] = uiFactory.createValidatingComboBox(label[i] + "field", "false", MAX_LENGTH);
            }
            else
            {
                fields[i] = uiFactory.createConstrainedField(label[i] + "field", "1", MAX_LENGTH);
            }

            if ((label.equals(labelText)) || (label.equals(labelTextSizeNoPlanogram)))
            {
                if (i == DISCOUNT)
                {
                    fields[i] = uiFactory.createValidatingComboBox(label[i] + "field", "false", MAX_LENGTH);
                }

                if (i >= 13)
                {
                    // for those non-editable fields
                    fields[i] = uiFactory.createLabel(label[i] + "field", "", null, UI_LABEL);
                }
            }
            else
            {
                if (i >= 12)
                {
                    // for those non-editable fields
                    fields[i] = uiFactory.createLabel(label[i] + "field", "", null, UI_LABEL);
                }
            }

            if (i == ITEM_NUMBER)
            {
                ((ConstrainedTextField)fields[i]).setMaxLength(maxFieldLengths[0]);
            }
            else if (i == DESCRIPTION)
            {
                ((ConstrainedTextField)fields[i]).setMaxLength(maxFieldLengths[1]);
            }
            else if (i == MANUFACTURER || i == PLANOGRAMID)
            {
                labels[i].setVisible(false);
                fields[i].setVisible(false);
            }
            else if (i == PRICE)
            {
                CurrencyTextField field = uiFactory.createCurrencyField(label[i] + "field");
                fields[i] = field;               
                field.setValue(null);
                field.setVisible(false);
                labels[i].setVisible(false);
            }
            else if (i == CLEARANCE)
            {
                fields[i] = uiFactory.createValidatingComboBox(label[i] + "field", "false", MAX_LENGTH);
            }
        }
    }

    /**
     * Layout the components.
     */
    @Override
    public void initLayout()
    {
        UIUtilities.layoutDataPanel(this, labels, fields);
    }

    /**
     * Update the bean if It's been changed. Changes all text then calls
     * {@link #setupLayout()}.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void updateBean()
    {
        // re-add everything
        setupLayout();
        // mark as not needed validation up to parent
        invalidate();

        ((ConstrainedTextField)fields[ITEM_NUMBER]).setText(beanModel.getItemNumber());
        String desc = beanModel.getItemDescription();
        if (desc == null)
        {
            desc = "";
        }
        ((ConstrainedTextField)fields[DESCRIPTION]).setText(makeSafeStringForDisplay(desc,
                MAX_ITM_DESC_DISPLAY_LENGTH));
        
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        
        // Load department lists
        DepartmentIfc[] depts = beanModel.getDeptList();
        if (depts!=null)
        {
            String[] deptDesc = new String[depts.length];

            
            int deptSelectedIdx = 0;
            for (int i = 0; i < depts.length; i++)
            {
                deptDesc[i] = depts[i].getDescription(uiLocale);
                if (deptDesc[i].equalsIgnoreCase(beanModel.getItemDept()))
                {
                    deptSelectedIdx = i;
                }
            }
            setComboBoxModel(deptDesc, (ValidatingComboBox)fields[DEPARTMENT], deptSelectedIdx);
        }
        
        setComboBoxModel(beanModel.getYesAndNo(), (ValidatingComboBox) fields[CLEARANCE], 0);
        
        // ILRM CR : Display Item Level Information if present
        String itemLevelMessage = addLineBreaks(beanModel.getItemLevelMessage(), MAX_CHARS_BEFORE_LINE_BREAK);
        StringBuilder screenMessage = new StringBuilder("<html><b><body>");
        screenMessage.append(itemLevelMessage).append("</body></b></html>");

        setImageLabelContents(null, ItemListRenderer.getLoadingImage(getParent()));
        if(beanModel.isItemSizeRequired() && beanModel.isUsePlanogramID())
        {
            ((JLabel)fields[ITEM_LEVEL_MESSAGE]).setText(screenMessage.toString());
            if (Util.isEmpty(itemLevelMessage))
            {
                labels[ITEM_LEVEL_MESSAGE].setVisible(false);
                fields[ITEM_LEVEL_MESSAGE].setVisible(false);
            }
            else
            {
                labels[ITEM_LEVEL_MESSAGE].setVisible(true);
                fields[ITEM_LEVEL_MESSAGE].setVisible(true);
            }
        }
        else if(beanModel.isItemSizeRequired() || beanModel.isUsePlanogramID())
        {
            ((JLabel)fields[ITEM_LEVEL_MESSAGE - 1]).setText(screenMessage.toString());
            if (Util.isEmpty(itemLevelMessage))
            {
                labels[ITEM_LEVEL_MESSAGE - 1].setVisible(false);
                fields[ITEM_LEVEL_MESSAGE - 1].setVisible(false);
            }
            else
            {
                labels[ITEM_LEVEL_MESSAGE - 1].setVisible(true);
                fields[ITEM_LEVEL_MESSAGE - 1].setVisible(true);
            }
        }
        else
        {
            ((JLabel)fields[ITEM_LEVEL_MESSAGE - 2]).setText(screenMessage.toString());
            if (Util.isEmpty(itemLevelMessage))
            {
                labels[ITEM_LEVEL_MESSAGE - 2].setVisible(false);
                fields[ITEM_LEVEL_MESSAGE - 2].setVisible(false);
            }
            else
            {
                labels[ITEM_LEVEL_MESSAGE - 2].setVisible(true);
                fields[ITEM_LEVEL_MESSAGE - 2].setVisible(true);
            }
        }

        // Added to display manufacturer
        if (beanModel.isSearchItemByManufacturer())
        {
            ((ConstrainedTextField)fields[MANUFACTURER]).setText(beanModel.getItemManufacturer());
            fields[MANUFACTURER].setVisible(true);
            labels[MANUFACTURER].setVisible(true);
        }
        else
        {
            fields[MANUFACTURER].setVisible(false);
            labels[MANUFACTURER].setVisible(false);
        }

        //Color
        ItemColorIfc[] colors = beanModel.getColorList();
        if(colors != null)
        {
            String[] colorDesc = new String[colors.length];
            int colorSelectedIdx = 0;
            for (int i = 0; i < colors.length; i++)
            {
                colorDesc[i] = colors[i].getDescription(uiLocale);
                if (!StringUtils.isEmpty(colorDesc[i]) && colorDesc[i].equalsIgnoreCase(beanModel.getColorDesc()))
                {
                    colorSelectedIdx = i;
                }
            }
            setComboBoxModel(colorDesc, (ValidatingComboBox)fields[COLOR], colorSelectedIdx);
        }
        
        //TYPE
        ItemTypeIfc[] types = beanModel.getTypeList();
        if(types != null)
        {
            String[] typeDesc = new String[types.length];
            int itemTypeSelectedIdx = 0;
            for (int i = 0; i < types.length; i++)
            {
                typeDesc[i] = types[i].getItemTypeName();
                if (!StringUtils.isBlank(typeDesc[i]) && typeDesc[i].equalsIgnoreCase(beanModel.getItemType()))
                {
                    itemTypeSelectedIdx = i;
                }
            }
            setComboBoxModel(typeDesc, (ValidatingComboBox)fields[TYPE], itemTypeSelectedIdx);
        }

        //fields[MEASURE].setText(beanModel.getUnitOfMeasure());
        UnitOfMeasureIfc[] uoms = beanModel.getUomList();
        if(uoms != null)
        {
            String[] uomDesc = new String[uoms.length];
            int uomSelectedIdx = 0;
            for (int i = 0; i < uoms.length; i++)
            {
                uomDesc[i] = uoms[i].getName(uiLocale);
                if (!StringUtils.isEmpty(uomDesc[i]) && uomDesc[i].equalsIgnoreCase(beanModel.getUnitOfMeasure()))
                {
                    uomSelectedIdx = i;
                }
            }
            setComboBoxModel(uomDesc, (ValidatingComboBox)fields[MEASURE], uomSelectedIdx);
        }
        
        //STYLE
        ItemStyleIfc[] styles = beanModel.getStyleList();
        if(styles != null)
        {
            String[] styleDesc = new String[styles.length];
            int itemStyleSelectedIdx = 0;
            for (int i = 0; i < styles.length; i++)
            {
                styleDesc[i] = styles[i].getDescription(uiLocale);
                if (!StringUtils.isBlank(styleDesc[i]) && styleDesc[i].equalsIgnoreCase(beanModel.getItemStyle()))
                {
                    itemStyleSelectedIdx = i;
                }
            }
            setComboBoxModel(styleDesc, (ValidatingComboBox)fields[STYLE], itemStyleSelectedIdx);
        }


        if (beanModel.isItemSizeRequired())
        {
            // fields[SIZE].setText(beanModel.getItemSize());
            ItemSizeIfc[] sizes = beanModel.getSizeList();
            if(sizes != null)
            {
                String[] sizeDesc = new String[sizes.length];
                int sizeSelectedIdx = 0;
                for (int i = 0; i < sizes.length; i++)
                {
                    sizeDesc[i] = sizes[i].getDescription(uiLocale);
                    if (sizeDesc[i].equalsIgnoreCase(beanModel.getItemSize()))
                    {
                        sizeSelectedIdx = i;
                    }
                }
                setComboBoxModel(sizeDesc, (ValidatingComboBox)fields[SIZE], sizeSelectedIdx);
            }
            setComboBoxModel(beanModel.getYesAndNo(), (ValidatingComboBox) fields[TAX], 0);
            setComboBoxModel(beanModel.getYesAndNo(), (ValidatingComboBox) fields[DISCOUNT], 0);

            if (beanModel.isUsePlanogramID())
            {
                if (beanModel.getPlanogramID() != null)
                {
                    String[] planogram = beanModel.getPlanogramID();
                    int index = planogram.length;
                    if (index > 0)
                    {
                        StringBuffer sbDispPlanogram = new StringBuffer("<html>");

                        int i = 0;
                        for (i = 0; i < index; i++)
                        {
                            sbDispPlanogram.append(planogram[i] + "<p>");
                        }
                        sbDispPlanogram.append("</html>");
                        String dispPlanogram = sbDispPlanogram.toString();

                        ((JLabel)fields[PLANOGRAMID]).setText(dispPlanogram);
                        fields[PLANOGRAMID].setVisible(true);
                        labels[PLANOGRAMID].setVisible(true);
                    }
                    else
                    {
                        ((JLabel)fields[PLANOGRAMID]).setText("");
                        fields[PLANOGRAMID].setVisible(true);
                        labels[PLANOGRAMID].setVisible(true);
                    }
                }
                else
                {
                    ((JLabel)fields[PLANOGRAMID]).setText("");
                    fields[PLANOGRAMID].setVisible(true);
                    labels[PLANOGRAMID].setVisible(true);
                }

                for (int i = 0; i < labelText.length; i++)
                {
                    labels[i].setText(retrieveText(labelTags[i], labelText[i]));
                }
            }
            else
            {
                for (int i = 0; i < labelTextSizeNoPlanogram.length; i++)
                {
                    labels[i].setText(retrieveText(labelTagsSizeNoPlanogram[i], labelTextSizeNoPlanogram[i]));
                }
            }
        }

        else
        {
            setComboBoxModel(beanModel.getYesAndNo(), (ValidatingComboBox) fields[TAX -1], 0);
            setComboBoxModel(beanModel.getYesAndNo(), (ValidatingComboBox) fields[DISCOUNT -1], 0);
             
            if (beanModel.isUsePlanogramID())
            {
                if (beanModel.getPlanogramID() != null)
                {
                    String[] planogram = beanModel.getPlanogramID();
                    int index = planogram.length;
                    if (index > 0)
                    {
                        StringBuffer sbDispPlanogram = new StringBuffer("<html>");

                        int i = 0;
                        for (i = 0; i < index; i++)
                        {
                            sbDispPlanogram.append(planogram[i] + "<p>");
                        }
                        sbDispPlanogram.append("</html>");
                        String dispPlanogram = sbDispPlanogram.toString();

                        ((JLabel)fields[PLANOGRAMID - 1]).setText(dispPlanogram);
                        fields[PLANOGRAMID - 1].setVisible(true);
                        labels[PLANOGRAMID - 1].setVisible(true);
                    }
                    else
                    {
                        ((JLabel)fields[PLANOGRAMID - 1]).setText("");
                        fields[PLANOGRAMID - 1].setVisible(true);
                        labels[PLANOGRAMID - 1].setVisible(true);
                    }
                }
                else
                {
                    ((JLabel)fields[PLANOGRAMID - 1]).setText("");
                    fields[PLANOGRAMID - 1].setVisible(true);
                    labels[PLANOGRAMID - 1].setVisible(true);
                }

                for (int i = 0; i < labelTextNoSizePlanogram.length; i++)
                {
                    labels[i].setText(retrieveText(labelTagsNoSizePlanogram[i], labelTextNoSizePlanogram[i]));
                }
            }
            else
            {
                for (int i = 0; i < labelTextNoSizeNoPlanogram.length; i++)
                {
                    labels[i].setText(retrieveText(labelTagsNoSizeNoPlanogram[i], labelTextNoSizeNoPlanogram[i]));
                }
            }
        }
        
        //Not displaying below given fields if size is 0 or obj is null
        if(beanModel.getDeptList() == null || beanModel.getDeptList().length==0)
        {
            fields[DEPARTMENT].setVisible(false);
            labels[DEPARTMENT].setVisible(false);
        }
        if(beanModel.getTypeList() == null || beanModel.getTypeList().length==0)
        {
            fields[TYPE].setVisible(false);
            labels[TYPE].setVisible(false);
        }
        if(beanModel.getStyleList() == null || beanModel.getStyleList().length==0)
        {
            fields[STYLE].setVisible(false);
            labels[STYLE].setVisible(false);
        }
        if(beanModel.getColorList() == null || beanModel.getColorList().length==0)
        {
            fields[COLOR].setVisible(false);
            labels[COLOR].setVisible(false);
        }
        if(beanModel.getSizeList() == null || beanModel.getSizeList().length==0)
        {
            fields[SIZE].setVisible(false);
            labels[SIZE].setVisible(false);
        }
        if(beanModel.getUomList()==null || beanModel.getUomList().length==0)
        {
            fields[MEASURE].setVisible(false);
            labels[MEASURE].setVisible(false);
        }
        revalidate();
        loadImage(beanModel);
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
     * Convenience method to populate a comboBox
     * 
     * @param data the data to be display in the combo box
     * @param field the actual combo box field receiving the data
     * @param selected index the default selected value
     */
    protected void setComboBoxModel(String[] data, ValidatingComboBox field, int selectedIndex)
    {
        if (data != null)
        {
            ValidatingComboBoxModel model = new ValidatingComboBoxModel(data);
            field.setModel(model);
            field.setSelectedIndex(selectedIndex);
        }
    }

    /**
     * Put the specified contents into the correct position of the image label.
     *
     * @param text
     * @param icon
     */
    protected void setImageLabelContents(String text, ImageIcon icon)
    {
        if(beanModel.isItemSizeRequired() && beanModel.isUsePlanogramID())
        {
            ((JLabel)fields[IMAGE]).setText(text);
            ((JLabel)fields[IMAGE]).setIcon(icon);
        }
        else if(beanModel.isItemSizeRequired() || beanModel.isUsePlanogramID())
        {
            ((JLabel)fields[IMAGE - 1]).setText(text);
            ((JLabel)fields[IMAGE - 1]).setIcon(icon);
        }
        else
        {
            ((JLabel)fields[IMAGE - 2]).setText(text);
            ((JLabel)fields[IMAGE - 2]).setIcon(icon);
        }
    }

    /**
     * Lengths used for max lengths of the text fields. Item ID, Description, Price.
     *
     * @return the maxFieldLengths
     * @see ConstrainedTextField#setMaxLength(int)
     */
    public int[] getMaxFieldLengths()
    {
        return maxFieldLengths;
    }

    /**
     * Lengths used for max lengths of the text fields. Item ID, Description, Price.
     * 
     * @param maxFieldLengths the maxFieldLengths to set
     * @see ConstrainedTextField#setMaxLength(int)
     */
    public void setMaxFieldLengths(int[] maxFieldLengths)
    {
        this.maxFieldLengths = maxFieldLengths;
    }

    /**
     * Set a comma-delimited list of lengths to use for the fields in this bean.
     * 
     * @param maxFieldLengthsDelimitedString the maxFieldLengths to set
     * @see ConstrainedTextField#setMaxLength(int)
     * @see #setMaxFieldLengths(int[])
     */
    public void setMaxFieldLengths(String maxFieldLengthsDelimitedString)
    {
        String[] lengths = maxFieldLengthsDelimitedString.split(",");
        this.maxFieldLengths = new int[lengths.length];
        for (int i = 0; i < lengths.length; i++)
        {
            this.maxFieldLengths[i] = Integer.parseInt(lengths[i]);
        }
    }
    
    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args command line arguments. None are needed.
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        ItemInfoBeanModel model = new ItemInfoBeanModel();
        model.setItemDescription("Chess Set");
        model.setItemNumber("20020012");
        model.setPrice(new BigDecimal(49.99));

        EditableItemInfoBean bean = new EditableItemInfoBean();
        bean.setModel(model);

        UIUtilities.doBeanTest(bean);
    }

}