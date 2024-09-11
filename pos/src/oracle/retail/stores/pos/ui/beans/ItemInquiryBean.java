/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemInquiryBean.java /main/32 2012/10/26 17:01:58 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  09/24/14 - enabled ui wildcard
 *    yiqzhao   09/22/14 - add item size required value check
 *    cgreene   10/17/12 - tweak implementation of search field with icon
 *    vbongu    10/11/12 - Item inquiry with magnifying glass icon changes
 *    hyin      09/05/12 - increase display length.
 *    hyin      08/15/12 - new meta tag adv search feature.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nkgautam  03/25/09 - fix for inventory inquiry screen when item
 *                         description is too long
 *    mchellap  03/17/09 - Changed itemNumberField type from alphanumeric to
 *                         text
 *    sgu       03/11/09 - change text fields to alphanumerice field
 *    mchellap  01/05/09 - Department search field changes
 *    asinton   11/19/08 - Changed calls from getScanData to getScanLabelData.
 *    sgu       10/24/08 - externalize manufacturer label
 *    ranojha   10/23/08 - Fixed the localized text for ItemSize and Buttons
 *    ranojha   10/21/08 - Changes for POS for UnitOfMeasure I18N
 *    ranojha   10/16/08 - Implementation for UnitOfMeasure I18N Changes
 *    miparek   10/16/08 - department list changes for locale requestor
 *    miparek   10/16/08 - dept list changes
 *    ranojha   10/14/08 - Enhanced Sites, Beans for I18N work for
 *                         UnitOfMeasure, Style and Color
 *    ranojha   10/13/08 - Working on Item Style, Size and Color changes
 *    ddbaker   10/09/08 - Refactor of reference implementation of POS I18N
 *                         Persistence
 *    ddbaker   10/06/08 - Preliminary I18N Persistence Updates for Size, Style
 *                         and Color.
 *    mchellap  09/30/08 - Updated copy right header
 *
 *     $Log:
 *      5    I18N_P2    1.3.2.0     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *           max length of constraied text field.
 *      4    360Commerce 1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *           Base-lining of 7.1_LA
 *      3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse
 *      2    360Commerce 1.1         3/10/2005 10:22:27 AM  Robert Pearse
 *      1    360Commerce 1.0         2/11/2005 12:11:38 PM  Robert Pearse
 *     $
 *     Revision 1.8  2004/07/17 19:21:23  jdeleau
 *     @scr 5624 Make sure errors are focused on the beans, if an error is found
 *     during validation.
 *
 *     Revision 1.7  2004/06/25 21:58:23  lzhao
 *     @scr 4087: remove not necessary import
 *
 *     Revision 1.6  2004/06/25 21:56:04  lzhao
 *     @scr 4087: not allow multi selection
 *
 *     Revision 1.5  2004/06/25 21:42:27  lzhao
 *     @scr 4087: change drop down back to list.
 *
 *     Revision 1.4  2004/05/03 18:28:14  lzhao
 *     @scr 4087: change list to combo box.
 *
 *     Revision 1.3  2004/03/16 17:15:17  build
 *     Forcing head revision
 *
 *     Revision 1.2  2004/02/11 20:56:26  rhafernik
 *     @scr 0 Log4J conversion and code cleanup
 *
 *     Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *     updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 16 2003 17:52:36   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:10:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jul 09 2003 10:29:34   bwf
 * Added listener to itemNumberField.
 * Resolution for 3075: At Item Search screen, scaner read number only one time.
 *
 *    Rev 1.3   Jun 30 2003 09:55:54   bwf
 * Changed to constrained text field.
 *
 *    Rev 1.2   Jun 26 2003 13:04:04   bwf
 * Set lengths for fields.
 * Resolution for 2526: Item Search Screen-There is not max if the Item # or Discription entering more than max cause database error
 *
 *    Rev 1.1   Aug 14 2002 18:17:50   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:55:38   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:34:48   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:55:34   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 05 2002 19:34:30   mpm
 * Text externalization for inquiry UI artifacts.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Locale;

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
import oracle.retail.stores.foundation.manager.device.ScannerModel;
import oracle.retail.stores.foundation.manager.device.ScannerSession;
import oracle.retail.stores.foundation.manager.gui.UIConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.persistence.utility.DBConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.EnableButtonListener;
import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;

import org.apache.log4j.Logger;

/**
 * This bean uses the ItemInquiryBeanModel.
 * 
 * @return ItemInquiryBeanModel The model of the bean.
 * @see #setModel
 * @see oracle.retail.stores.pos.ui.beans.ItemInquiryBeanModel
 * @version $Revision: /main/32 $
 */
public class ItemInquiryBean extends ValidatingBean implements DocumentListener
{
    private static final long serialVersionUID = -3302957389105911287L;

    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/32 $";

    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(ItemInquiryBean.class);

    /**
     * Display Length of Item Description in the item enquiry screen
     */
    public static final int MAX_ITM_DESC_DISPLAY_LENGTH = 16;

    /**
     * Cross-channel type adv. search label and field.
     */
    protected JLabel advSearchLabel = null;
    protected ValidatingTextField advSearchField = null;

    /**
     * Label for department code.
     */
    protected JLabel deptCodeLabel = null;

    /**
     * Combo for long list of departments
     */
    protected ValidatingComboBox deptComboBox = null;
    /**
     * local reference to model being used.
     */
    protected ItemInquiryBeanModel beanModel = null;
    /**
     * TextField for the item number
     */
    protected NumericTextField itemNumberField = null;
    /**
     * TextField for the item description
     */
    protected ValidatingTextField itemDescField = null;
    /**
     * Label for item number.
     */
    protected JLabel itemNumberLabel = null;
    /**
     * Label for item description.
     */
    protected JLabel itemDescLabel = null;
    /**
     * Label for department.
     */
    protected JLabel itemDeptLabel = null;

    /**
     * TextField for the manufacturer
     */
    protected ValidatingTextField itemManufacturerField = null;
    /**
     * Label for item number.
     */
    protected JLabel itemManufacturerLabel = null;

    /**
     * The global enable button listener
     */
    protected EnableButtonListener globalButtonListener = null;

    /**
     * Label for item type
     */
    protected JLabel itemTypeLabel = null;

    /**
     * Label for UOM
     */
    protected JLabel itemUOMLabel = null;

    /**
     * Label for item style.
     */
    protected JLabel itemStyleLabel = null;

    /**
     * Label for color.
     */
    protected JLabel itemColorLabel = null;

    /**
     * Label for item size.
     */
    protected JLabel itemSizeLabel = null;

    /**
     * Combo for long list of item types
     */
    protected ValidatingComboBox itemTypeCombo = null;

    /**
     * Combo for long list of UOMs
     */
    protected ValidatingComboBox itemUOMCombo = null;

    /**
     * Combo for list of item styles
     */
    protected ValidatingComboBox itemStyleCombo = null;

    /**
     * Combo for list of colors
     */
    protected ValidatingComboBox itemColorCombo = null;

    /**
     * Combo for list of sizes
     */
    protected ValidatingComboBox itemSizeCombo = null;

    /**
     * Constructor
     */
    public ItemInquiryBean()
    {
        initialize();
    }

    /**
     * Initialize the class.
     */
    private void initialize()
    {
        setName("ItemInquiryBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        advSearchLabel = uiFactory.createLabel("Item Search Criteria", "Item Search Criteria:", null, UI_LABEL);
        advSearchField = uiFactory.createSearchField("advSearchField", "0", "60", "24", true, this);

        itemNumberLabel = uiFactory.createLabel("Item Number", "Item Number:", null, UI_LABEL);
        itemDescLabel = uiFactory.createLabel("Item Description", "Item Description:", null, UI_LABEL);
        // Configurable Search
        itemManufacturerLabel = uiFactory.createLabel("Manufacturer", "Manufacturer:", null, UI_LABEL);
        itemDeptLabel = uiFactory.createLabel("Department", "Department:", null, UI_LABEL);
        itemManufacturerLabel.setVisible(false);
        itemNumberField = uiFactory.createNumericField("itemNumberField", "1", "14");

        itemDescField = uiFactory.createConstrainedField("itemDescField", "0", "60", "16");
        // Configurable Search
        itemManufacturerField = uiFactory.createConstrainedField("itemManufacturerField", "1", "30", "16");
        itemManufacturerField.setVisible(false);

        deptComboBox = uiFactory.createValidatingComboBox("deptComboBox", "false", "16");

        itemTypeLabel = uiFactory.createLabel("Item Type", "Item Type:", null, UI_LABEL);
        itemTypeCombo = uiFactory.createValidatingComboBox("itemTypeCombo", "false", "16");

        itemUOMLabel = uiFactory.createLabel("Unit Of Measure", "Unit Of Measure:", null, UI_LABEL);
        itemUOMCombo = uiFactory.createValidatingComboBox("itemUOMCombo", "false", "16");

        itemStyleLabel = uiFactory.createLabel("Style", "Style:", null, UI_LABEL);
        itemStyleCombo = uiFactory.createValidatingComboBox("itemStyleCombo", "false", "16");

        itemColorLabel = uiFactory.createLabel("Color", "Color:", null, UI_LABEL);
        itemColorCombo = uiFactory.createValidatingComboBox("itemColorCombo", "false", "16");

        itemSizeLabel = uiFactory.createLabel("Size", "Size:", null, UI_LABEL);
        itemSizeCombo = uiFactory.createValidatingComboBox("itemSizeCombo", "false", "16");

        UIUtilities.layoutDataPanel(this, new JLabel[] { advSearchLabel, itemNumberLabel, itemDescLabel,
                itemManufacturerLabel, itemDeptLabel, itemTypeLabel, itemUOMLabel, itemStyleLabel, itemColorLabel,
                itemSizeLabel }, new JComponent[] { advSearchField, itemNumberField, itemDescField, itemManufacturerField,
                deptComboBox, itemTypeCombo, itemUOMCombo, itemStyleCombo, itemColorCombo, itemSizeCombo });
    }

    /**
     * Returns the POSBaseBeanModel properties.
     * 
     * @return The model value.
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return (beanModel);
    }

    /**
     * The framework calls this method just before display
     */
    public void activate()
    {
        super.activate();

        advSearchField.getDocument().addDocumentListener(this);
        advSearchField.addFocusListener(this);
        itemNumberField.getDocument().addDocumentListener(this);
        itemNumberField.addFocusListener(this);
        itemDescField.addFocusListener(this);
        itemManufacturerField.addFocusListener(this);

        updateBean();
    }

    /**
     * updates the Model properties.
     * 
     * @return The model property value.
     * @see #setModel
     */
    public void updateModel()
    {
        if (beanModel.isMetaTagAdvSearch())
        {
            beanModel.setMetaTagSearchStr(advSearchField.getText());
        }
        else
        {

            beanModel.setItemNumber(Util.replaceStar(itemNumberField.getText(), UIConstantsIfc.UI_WILD_CARD.charAt(0),
                    DBConstantsIfc.DB_WILD_CARD.charAt(0)));
            beanModel.setItemDesc(itemDescField.getText());

            if (beanModel.isSearchItemByManufacturer())
            {
                beanModel.setManufacturer(itemManufacturerField.getText());
            }

            if (beanModel.isSearchItemByDepartment())
            {
                beanModel.setSelectedIndex(deptComboBox.getSelectedIndex());
            }

            if (beanModel.isSearchItemByType())
            {
                beanModel.setSelectedTypeIndex(itemTypeCombo.getSelectedIndex());
            }

            if (beanModel.isSearchItemByUOM())
            {
                beanModel.setSelectedUOMIndex(itemUOMCombo.getSelectedIndex());
            }

            if (beanModel.isSearchItemByStyle())
            {
                beanModel.setSelectedStyleIndex(itemStyleCombo.getSelectedIndex());
            }

            if (beanModel.isSearchItemByColor())
            {
                beanModel.setSelectedColorIndex(itemColorCombo.getSelectedIndex());
            }

            if (beanModel.isSearchItemBySize())
            {
                beanModel.setSelectedSizeIndex(itemSizeCombo.getSelectedIndex());
            }
        }
        manageNextButton();
    }

    /**
     * Sets the model of this bean.
     * 
     * @param model The new UIModel to use.
     * @see #getModel
     * @see oracle.retail.stores.pos.ui.beans.ItemInquiryBeanModel
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ItemInquiryBean model to null");
        }
        if (model instanceof ItemInquiryBeanModel)
        {
            beanModel = (ItemInquiryBeanModel)model;
            updateBean();
        }
    }

    /**
     * Update the bean if It's been changed
     */
    protected void updateBean()
    {
        if (beanModel.isMetaTagAdvSearch())
        {
            advSearchField.setText(beanModel.getMetaTagSearchStr());
            setCurrentFocus(advSearchField);

            itemNumberLabel.setVisible(false);
            itemNumberField.setVisible(false);
            itemDescLabel.setVisible(false);
            itemDescField.setVisible(false);
            itemDeptLabel.setVisible(false);
            deptComboBox.setVisible(false);
            itemTypeCombo.setVisible(false);
            itemTypeLabel.setVisible(false);
            itemUOMCombo.setVisible(false);
            itemUOMLabel.setVisible(false);
            itemStyleCombo.setVisible(false);
            itemStyleLabel.setVisible(false);
            itemColorCombo.setVisible(false);
            itemColorLabel.setVisible(false);
            itemSizeCombo.setVisible(false);
            itemSizeLabel.setVisible(false);
            itemManufacturerLabel.setVisible(false);
            itemManufacturerField.setVisible(false);
        }
        else
        {
            advSearchLabel.setVisible(false);
            advSearchField.setVisible(false);
            itemNumberField.setText(beanModel.getItemNumber());
            itemDescField.setText(UIUtilities.makeSafeStringForDisplay(beanModel.getItemDesc(),
                    MAX_ITM_DESC_DISPLAY_LENGTH));

            if (beanModel.searchItemByDepartment)
            {
                itemDeptLabel.setVisible(true);
                deptComboBox.setVisible(true);
                // Load department lists
                DepartmentIfc[] depts = beanModel.getDeptList();
                String[] deptDesc = new String[depts.length];
                Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
                // deptList.removeAllItems();
                for (int i = 0; i < depts.length; i++)
                {
                    deptDesc[i] = depts[i].getDescription(uiLocale);
                    // deptList.addItem(deptDesc[i]);
                }
                setComboBoxModel(deptDesc, deptComboBox, beanModel.getSelectedIndex());
            }
            else
            {
                itemDeptLabel.setVisible(false);
                deptComboBox.setVisible(false);
            }

            if (beanModel.searchItemByType)
            {
                itemTypeCombo.setVisible(true);
                itemTypeLabel.setVisible(true);
                // Load item type lists
                ItemTypeIfc[] types = beanModel.getTypeList();
                String[] typeDesc = new String[types.length];
                for (int i = 0; i < types.length; i++)
                {
                    typeDesc[i] = types[i].getItemTypeName();
                }

                setComboBoxModel(typeDesc, itemTypeCombo, beanModel.getSelectedTypeIndex());
            }
            else
            {
                itemTypeCombo.setVisible(false);
                itemTypeLabel.setVisible(false);
            }

            Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

            if (beanModel.searchItemByUOM)
            {
                // Load UOM lists
                itemUOMCombo.setVisible(true);
                itemUOMLabel.setVisible(true);
                UnitOfMeasureIfc[] uoms = beanModel.getUomList();
                String[] uomDesc = new String[uoms.length];

                for (int i = 0; i < uoms.length; i++)
                {
                    uomDesc[i] = uoms[i].getName(uiLocale);
                }
                setComboBoxModel(uomDesc, itemUOMCombo, beanModel.getSelectedUOMIndex());
            }
            else
            {
                itemUOMCombo.setVisible(false);
                itemUOMLabel.setVisible(false);
            }

            if (beanModel.searchItemByStyle)
            {
                // Load item style list
                itemStyleCombo.setVisible(true);
                itemStyleLabel.setVisible(true);
                ItemStyleIfc[] styles = beanModel.getStyleList();
                String[] styleDesc = new String[styles.length];

                for (int i = 0; i < styles.length; i++)
                {
                    styleDesc[i] = styles[i].getName(uiLocale);
                }
                setComboBoxModel(styleDesc, itemStyleCombo, beanModel.getSelectedStyleIndex());
            }
            else
            {
                itemStyleCombo.setVisible(false);
                itemStyleLabel.setVisible(false);
            }

            if (beanModel.searchItemByColor)
            {
                // Load item color list
                itemColorCombo.setVisible(true);
                itemColorLabel.setVisible(true);
                ItemColorIfc[] colors = beanModel.getColorList();
                String[] colorDesc = new String[colors.length];

                for (int i = 0; i < colors.length; i++)
                {
                    colorDesc[i] = colors[i].getName(uiLocale);
                }
                setComboBoxModel(colorDesc, itemColorCombo, beanModel.getSelectedColorIndex());
            }
            else
            {
                itemColorCombo.setVisible(false);
                itemColorLabel.setVisible(false);
            }

            if (beanModel.searchItemBySize && beanModel.isItemSizeRequired())
            {
                itemSizeCombo.setVisible(true);
                itemSizeLabel.setVisible(true);
                ItemSizeIfc[] sizes = beanModel.getSizeList();
                String[] sizeDesc = new String[sizes.length];

                for (int i = 0; i < sizes.length; i++)
                {
                    sizeDesc[i] = sizes[i].getDescription(uiLocale);
                }
                setComboBoxModel(sizeDesc, itemSizeCombo, beanModel.getSelectedSizeIndex());
            }
            else
            {
                itemSizeCombo.setVisible(false);
                itemSizeLabel.setVisible(false);
            }

            if (beanModel.isSearchItemByManufacturer())
            {
                itemManufacturerField.setText(beanModel.getManufacturer());
                itemManufacturerLabel.setVisible(true);
                itemManufacturerField.setVisible(true);
            }
            else
            {
                itemManufacturerLabel.setVisible(false);
                itemManufacturerField.setVisible(false);
            }
            setCurrentFocus(itemNumberField);
        }
        manageNextButton();
    }

    /**
     * deactivate any settings made by this bean to external entities
     */

    public void deactivate()
    {
        super.deactivate();

        advSearchField.getDocument().removeDocumentListener(this);
        advSearchField.removeFocusListener(this);
        itemNumberField.getDocument().removeDocumentListener(this);
        itemNumberField.removeFocusListener(this);
        itemDescField.removeFocusListener(this);
        itemManufacturerField.removeFocusListener(this);
    }

    /**
     * Override setVisible() to request focus.
     */
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            if (beanModel.isMetaTagAdvSearch())
            {
                setCurrentFocus(advSearchField);
            }
            else
            {
                setCurrentFocus(itemNumberField);
            }
        }
    }

    /**
     * Receive scanner data. Called by the UI Framework.
     * 
     * @param data DeviceModelIfc
     */
    public void setScannerData(DeviceModelIfc data)
    {
        ScannerModel scannerModel = (ScannerModel)data;

        // Strip any leading alpha characters from the JPOS scanner input. UPC
        // codes are always numeric
        int index = 0;
        String temp = new String(scannerModel.getScanLabelData());

        while (Character.isLetter(temp.charAt(index)))
        {
            index++;
        }

        final String numeric = temp.substring(index);

        if (beanModel.isMetaTagAdvSearch())
        {
            advSearchField.setText(numeric);
        }
        else
        {
            itemNumberField.setText(numeric);
        }

        updateModel();

        if (!beanModel.isMetaTagAdvSearch())
        {
            setCurrentFocus(itemDescField);
        }

    }

    /**
     * Implementation of DocumentListener interface.
     * 
     * @param e a document event
     */
    public void changedUpdate(DocumentEvent e)
    {
        manageNextButton();
    }

    /**
     * Implementation of DocumentListener interface.
     * 
     * @param e a document event
     */
    public void insertUpdate(DocumentEvent e)
    {
        manageNextButton();
    }

    /**
     * Implementation of DocumentListener interface.
     * 
     * @param e a document event
     */
    public void removeUpdate(DocumentEvent de)
    {
        boolean isEmpty = false;
        if (beanModel.isMetaTagAdvSearch())
        {
            if (advSearchField.getText().equals(""))
            {
                isEmpty = true;
            }
        }
        else
        {
            if (itemNumberField.getText().equals(""))
            {
                isEmpty = true;
            }
        }

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

    /**
     * Updates property-based fields.
     */
    protected void updatePropertyFields()
    {
        advSearchLabel.setText(retrieveText("AdvSearchLabel", "Item Search Criteria:"));
        advSearchField.setLabel(advSearchLabel);

        itemNumberLabel.setText(retrieveText("ItemNumberLabel", "Item Number:"));
        itemDescLabel.setText(retrieveText("ItemDescriptionLabel", "Item Description:"));
        itemDeptLabel.setText(retrieveText("DepartmentLabel", "Department:"));
        itemManufacturerLabel.setText(retrieveText("ItemManufacturerLabel", "Manufacturer:"));

        itemTypeLabel.setText(retrieveText("ItemTypeLabel", "Type:"));
        itemUOMLabel.setText(retrieveText("ItemUOMLabel", "Unit Of Measure:"));
        itemStyleLabel.setText(retrieveText("ItemStyleLabel", "Style:"));
        itemColorLabel.setText(retrieveText("ItemColorLabel", "Color:"));
        itemSizeLabel.setText(retrieveText("ItemInquiryItemSizeLabel", "Size:"));

        itemNumberField.setLabel(itemNumberLabel);
        itemDescField.setLabel(itemDescLabel);
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: ItemInquiryBean (Revision " + getRevisionNumber() + ") @" + hashCode());
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
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        ItemInquiryBean bean = new ItemInquiryBean();
        bean.setModel(new ItemInquiryBeanModel());

        UIUtilities.doBeanTest(bean);
    }

    /**
     * Adds (actually sets) the enable button listener on the bean.
     * 
     * @param listener
     */
    public void addGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = listener;
    }

    /**
     * Removes the enable button listener from the bean.
     * 
     * @param listener
     */
    public void removeGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = null;
    }

    /**
     * Gets the enable button listener from the bean.
     * 
     * @return listener
     */
    public EnableButtonListener getGlobalButtonListener()
    {
        return globalButtonListener;
    }

    /**
     * Determines if the "Next" button should be enabled.
     */
    public void manageNextButton()
    {
        if (globalButtonListener != null)
        {
            if (!Util.isEmpty(advSearchField.getText())
                    || !Util.isEmpty(itemNumberField.getText())
                    || !Util.isEmpty(itemDescField.getText())
                    || !Util.isEmpty(itemManufacturerField.getText()))
            {
                globalButtonListener.enableButton(CommonActionsIfc.NEXT, true);
            }
            else
            {
                globalButtonListener.enableButton(CommonActionsIfc.NEXT, false);
            }
        }
    }
}