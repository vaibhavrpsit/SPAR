/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DataInputBean.java /main/23 2013/05/28 09:18:11 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     05/28/13 - Fixed an ArrayIndexOutOfBounds exception caused by
 *                         calling setText() multiple times on a 
 *                         JFormattedTextField.
 *    cgreene   07/15/11 - add support to reflectively invoke methods against
 *                         the fields for label arguments
 *    cgreene   07/14/11 - tweak search by credit debit and gift card number
 *    cgreene   06/17/11 - log errors when creating fields
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   12/17/10 - XbranchMerge cgreene_msrsession_cce from
 *                         rgbustores_13.3x_generic_branch
 *    cgreene   12/17/10 - fixed ScannerSession to MSRSession while setting MSR
 *                         input.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   08/25/09 - XbranchMerge
 *                         asinton_8821882_fromstallama_bug_8606887 from
 *                         rgbustores_13.1x_branch
 *    asinton   08/24/09 - XbranchMerge stallama_bug-8606887 from
 *                         rgbustores_13.0x_branch
 *    cgreene   05/01/09 - string pooling performance aenhancements
 *    glwang    01/22/09 - HPQC1610 fix
 *    asinton   11/19/08 - Changed calls from getScanData to getScanLabelData.
 *
 * ===========================================================================
 * $Log:
 *   6    360Commerce 1.5         2/27/2008 3:19:23 PM   Alan N. Sinton  CR
 *        29989: Changed masked to truncated for UI renders of PAN.
 *   5    360Commerce 1.4         12/28/2007 1:29:33 PM  Leona R. Slepetis
 *        removed sensitive data logging PABP FR15
 *   6    I18N_P2    1.3.1.1     1/8/2008 4:34:22 PM    Maisa De Camargo CR
 *        29826 - Setting the size of the combo boxes. This change was
 *        necessary because the width of the combo boxes used to grow
 *        according to the length of the longest content. By setting the size,
 *         we allow the width of the combo box to be set independently from
 *        the width of the dropdown menu.
 *   5    I18N_P2    1.3.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *        alphanumerice fields for I18N purpose
 *   4    360Commerce 1.3         11/29/2007 5:15:58 PM  Alan N. Sinton  CR
 *        29677: Protect user entry fields of PAN data.
 *   3    360Commerce 1.2         3/31/2005 4:27:40 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:20:46 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:10:27 PM  Robert Pearse
 *  $
 *  Revision 1.13  2004/07/17 19:21:23  jdeleau
 *  @scr 5624 Make sure errors are focused on the beans, if an error is found
 *  during validation.
 *
 *  Revision 1.12  2004/05/17 23:27:57  crain
 *  @scr 4492 Issue Amount Entry does not have focus for Amount field
 *
 *  Revision 1.11  2004/03/25 15:07:16  baa
 *  @scr 3561 returns bug fixes
 *
 *  Revision 1.10  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.9  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.8  2004/03/09 20:17:00  rzurga
 *  @scr 3991 Clean up the code and add some comments
 *
 *  Revision 1.7  2004/03/09 07:26:35  rzurga
 *  @scr 3991 Refactor barcode date handling in a more generic fashion
 *
 *  Revision 1.6  2004/03/08 19:13:09  rzurga
 *  @scr 3991 Reformat sections of code
 *
 *  Revision 1.5  2004/02/27 19:51:16  baa
 *  @scr 3561 Return enhancements
 *
 *  Revision 1.4  2004/02/26 16:47:10  rzurga
 *  @scr 0 Add optional and customizable date to the transaction id and its receipt barcode
 *
 *  Revision 1.3  2004/02/13 13:57:21  baa
 *  @scr 3561  Returns enhancements
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.5   Jan 23 2004 16:28:30   baa
 * continue return development
 *
 *    Rev 1.4   Jan 13 2004 14:36:28   baa
 * move scannedfields method to model
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.3   Dec 29 2003 15:44:34   baa
 * return enhancements
 *
 *    Rev 1.2   16 Dec 2003 00:05:08   baa
 * enhacements for validating comboboxes
 *
 *    Rev 1.1   Dec 15 2003 13:51:32   baa
 * return enhancements
 *
 *    Rev 1.0   Aug 29 2003 16:10:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Mar 20 2003 18:19:00   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.3   Sep 09 2002 08:40:24   baa
 * fix null pointer condition
 * Resolution for POS SCR-1805: DataInputBean aborts when no fields are defined
 *
 *    Rev 1.2   Aug 15 2002 17:55:48   baa
 * apply foundation  updates to UISubsystem
 *
 * Resolution for POS SCR-1769: 5.2 UI defects resulting from change to java 1.4
 *
 *    Rev 1.1   Aug 14 2002 18:17:10   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:26   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:33:44   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:53:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 23 2002 15:04:10   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Jan 19 2002 11:03:18   mpm
 * Initial revision.
 * Resolution for POS SCR-214: ReceiptLogo on Receipt
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.MICRModel;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.device.MSRSession;
import oracle.retail.stores.foundation.manager.device.ScannerModel;
import oracle.retail.stores.foundation.manager.device.ScannerSession;
import oracle.retail.stores.foundation.manager.gui.FieldSpec;
import oracle.retail.stores.foundation.manager.gui.InputBeanIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.foundation.utility.ReflectionUtility;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.log4j.Logger;

/**
 *  Standard bean that displays a gridded set of data input
 *  components and labels.
 */
public class DataInputBean extends ValidatingBean implements InputBeanIfc, ActionListener
{
    /**  serialVersionUID */
    private static final long serialVersionUID = 6560187348522057537L;

    /** The logger to which log messages will be sent.    */
    private static final Logger logger = Logger.getLogger(DataInputBean.class);

    // This is a hashtable storeing all primitive type kwywords and their wrapper classes.
    protected static final Map<String,Class<?>> primitiveTypeWrapperClassTbl = new HashMap<String,Class<?>>(8);
    static {
        primitiveTypeWrapperClassTbl.put("boolean", Boolean.class);
        primitiveTypeWrapperClassTbl.put("char", Character.class);
        primitiveTypeWrapperClassTbl.put("byte", Byte.class);
        primitiveTypeWrapperClassTbl.put("short", Short.class);
        primitiveTypeWrapperClassTbl.put("int", Integer.class);
        primitiveTypeWrapperClassTbl.put("long", Long.class);
        primitiveTypeWrapperClassTbl.put("float", Float.class);
        primitiveTypeWrapperClassTbl.put("double", Double.class);
    }

    /** an array of labels for the components */
    protected JLabel[] labels;

    /** an array of data entry components */
    protected JComponent[] components;

    /** an array of field specifications */
    protected FieldSpec[] specs;

    /** indicate if a card has been swiped on this screen */
    protected boolean isCardSwiped = false;

    /** indicate if a check micr has been read */
    protected boolean isCheckMICRed = false;

    /** indicate if a field has been scanned */
    protected boolean isScanned = false;

    /** msr model **/
    protected MSRModel msrModel = null;

    /** check model **/
    protected MICRModel chkModel = null;

    protected boolean isMailLetter = true;

    /**
     * Default constructor.
     */
    public DataInputBean()
    {
        super();
    }

    /**
     * Configures the bean. The configurator calls this after it has configured
     * the fields.
     */
    @Override
    public void configure()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);
        if (labels != null && components != null)
        {
            UIUtilities.layoutDataPanel(this, labels, components);
        }
    }

    /**
     * Creates and configures the data entry components and their labels. This
     * method is called by the bean configurator.
     * 
     * @param fieldSpecs an array of field specifications
     */
    public void configureFields(FieldSpec[] fieldSpecs)
    {
        removeAll();
        if (fieldSpecs != null && fieldSpecs.length > 0)
        {
            // reset the components and labels
            specs = fieldSpecs;
            components = new JComponent[specs.length];
            labels = new JLabel[specs.length];

            // loop through the specs and create each component
            for (int i = 0; i < specs.length; i++)
            {
                String type = specs[i].getFieldType();

                // if the type is not blank, use the factory
                if (type != null && !type.equals(""))
                {
                    components[i] = createFactoryField(type, specs[i].getParamList(), specs[i].getParamTypeList());
                }
                // otherwise, use reflection on the class name
                else
                {
                    components[i] = createClassField(specs[i].getClassName());
                }
                // if the component was created successfully,
                // we configure it and create a label
                if (components[i] != null)
                {
                    components[i].setName(specs[i].getFieldName());
                    components[i].setEnabled(specs[i].isEnabled());

                    // create the label
                    String labelTag = specs[i].getLabelTag();
                    String labelText = retrieveText(labelTag, specs[i].getLabelText());

                    labels[i] = uiFactory.createLabel(labelTag, labelText, null, UI_LABEL);

                    // if it is a validating component,
                    // set the required attribute and the label
                    if (components[i] instanceof ValidatingFieldIfc)
                    {
                        ValidatingFieldIfc val = (ValidatingFieldIfc) components[i];

                        val.setLabel(labels[i]);
                        val.setRequired(specs[i].isRequired());
                    }
                }
            }
        }
    }

    /**
     *  Sets the property bundle tags for the component labels.
     *  This will override any settings in the fieldSpecs.
     *  @param propValue a comma delimited string of property tags
     */
    public void setLabelTags(String propValue)
    {
        String[] newTags = UIUtilities.parseDelimitedList(propValue, ",");

        if (newTags != null && newTags.length > 0)
        {
            for (int i = 0; i < newTags.length; i++)
            {
                setLabelText(i, retrieveText(newTags[i], ""));
            }
        }
    }

    /**
     *  Directly sets the text on the component labels. This will override
     *  any settings in the fieldSpecs.
     *  @param propValue a comma delimited string of label text
     */
    public void setLabelTexts(String propValue)
    {
        String[] newTexts = UIUtilities.parseDelimitedList(propValue, ",");

        if (newTexts != null && newTexts.length > 0)
        {
            for (int i = 0; i < newTexts.length; i++)
            {
                setLabelText(i, newTexts[i]);
            }
        }
    }

    /**
     *  Sets the text on the specified component label.
     *  @param pos the location of the label in the label array
     *  @param newText the new text for the label
     */
    public void setLabelText(int pos, String newText)
    {
        if (labels != null && labels.length > pos)
        {
            labels[pos].setText(newText);
        }
    }

    /**
     *  Updates the model to send back to the business logic.
     */
    public void updateModel()
    {
        if (beanModel != null && beanModel instanceof DataInputBeanModel)
        {
            DataInputBeanModel model = (DataInputBeanModel) beanModel;

            for (int i = 0; i < components.length; i++)
            {
                JComponent c = components[i];

                String name = c.getName();

                // get the raw data from the model
                Object data = null;

                // check the supported components
                if (c instanceof EYSDateField)
                {
                    data = ((EYSDateField)c).getDate();
                }
                else if (c instanceof JTextComponent)
                {
                    if(c instanceof BytesRetrievableIfc)
                    {
                        data = ((BytesRetrievableIfc)c).getTextBytes();
                    }
                    else
                    {
                        data = ((JTextComponent) c).getText();
                    }
                }
                else if (c instanceof JScrollPane)
                {
                    JList list = (JList) ((JScrollPane) c).getViewport().getView();
                    data = list.getModel();
                    ((POSListModel) data).setSelectedValue(list.getSelectedValue());

                }
                else if (c instanceof ValidatingComboBox)
                {
                    data = ((ValidatingComboBox) c).getModel();
                }
                else if (c instanceof JComboBox)
                {
                    data = ((JComboBox) c).getModel();
                }

                if (data != null)
                {
                    model.setValue(name, data);
                }
            }
        }
    }

    /**
     *  Creates a component from a class name. This is used for
     *  components that do not have factory methods in the UIFactory.
     *  @param spec a field spec
     *  @return a new JComponent
     */
    protected JComponent createClassField(String className)
    {
        JComponent result = null;

        try
        {
            result = (JComponent) ReflectionUtility.createClass(className);
        }
        catch (Exception e)
        {
            // if the reflection fails we return a null object
            // which will show up as a blank position on the screen
        }
        return result;
    }

    public void connectFields(String sourceField, String targetField)
    {

    }
    /**
     *  Creates a component using the UIFactory.
     *  @param spec a field spec
     *  @return a new JComponent
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected JComponent createFactoryField(String type, String params, String paramTypes)
    {
        JComponent result = null;

        try
        {
            // create the method name
            String method = "create" + type.substring(0, 1).toUpperCase() + type.substring(1);

            // create the parameter arrays
            String[] paramArray = UIUtilities.parseDelimitedList(params, ",");
            String[] paramTypeArray = UIUtilities.parseDelimitedList(paramTypes, ",");

            Class[] paramClasses = null;
            Object[] paramObjects = null;

            if (paramTypeArray != null)
            {
                // If parameter types are explicitly specfied, use these types to make the reflection call.
                paramClasses = new Class[paramTypeArray.length];
                paramObjects = new Object[paramTypeArray.length];

                for (int i = 0; i < paramTypeArray.length; i++)
                {
                    Object primitiveTypeWrapperClassObj = primitiveTypeWrapperClassTbl.get(paramTypeArray[i]);
                    Class paramClass = null;
                    if (primitiveTypeWrapperClassObj != null)
                    {
                        // for a primitive type, use its internally defined Java class.
                        paramClass = (Class)primitiveTypeWrapperClassObj;
                        paramClasses[i] = (Class)paramClass.getField("TYPE").get(null);
                    }
                    else
                    {
                        // for a non primitive type, load the class.
                        paramClasses[i] = paramClass = Class.forName(paramTypeArray[i]);
                    }

                    if (paramClass.getName().equals("java.lang.String"))
                    {
                        // If the parameter type is String, pass it in directly. No conversion is required.
                        paramObjects[i] = paramArray[i];
                    }
                    else
                    {
                        // If the parameter type is not String, call the constructor to convert the string passed in to the desired object.
                        // We assume the parameter class always defines a constructor which takes a String and create an Object out of it.
                        Constructor paramClassConstructor = paramClass.getConstructor(new Class[]{java.lang.String.class});
                        paramObjects[i] = paramClassConstructor.newInstance(new Object[]{paramArray[i]});
                    }
                }
            }
            else if (paramArray != null)
            {
                // If parameter type are not explicitly specified, we assume they are all of String type.
                paramClasses = new Class[paramArray.length];
                paramObjects = new Object[paramArray.length];

                for (int i = 0; i < paramArray.length; i++)
                {
                    paramClasses[i] = paramArray[i].getClass();
                    paramObjects[i] = paramArray[i];
                }
            }
            // call the factory method using reflection
            result = (JComponent) ReflectionUtility.invoke(uiFactory, method, paramClasses, paramObjects);
        }
        catch (Exception e)
        {
            // if the reflection fails we return a null object
            // which will show up as a blank position on the screen
            logger.error("Unable to create field \"" + type + "\" for beanSpecName \"" + getBeanSpecName() + "\".", e);
        }
        return result;

    }

    /**
     *    Updates the screen components with data from the bean model.
     */
    protected void updateBean()
    {
        if (beanModel instanceof DataInputBeanModel)
        {
            DataInputBeanModel model = (DataInputBeanModel) beanModel;

            //Set data model for swiping and msr reading only one per screen of each type will be save
            model.setCardSwiped(isCardSwiped);
            if (isCardSwiped)
            {
                model.setMsrModel(msrModel);
            }

            model.setCheckMICRed(isCheckMICRed);
            if (isCheckMICRed)
            {
                model.setChkModel(chkModel);
            }

            // loop through the components and set their data
            for (JComponent c : components)
            {
                // get the raw data from the model
                Object data = model.getValue(c.getName());

                if (data == null)
                {
                    data = new String("");
                }

                // check the supported components
                if (c instanceof EYSDateField)
                {
                    if (data instanceof String)
                    {
                        // Here we check of the incoming date has a format attached to it in the form <data>:<format>
                        // so that we can use the format properly
                        String sdata = (String)data;
                        StringTokenizer list = new StringTokenizer(sdata, ":");
                        if (sdata.length() > 0)
                        {
                            String date = "", fieldFormat = "";
                            if (list.hasMoreTokens())
                                date = list.nextToken();
                            if (list.hasMoreTokens())
                                fieldFormat = list.nextToken();
                            EYSDate value = new EYSDate();
                            value.initialize(date, fieldFormat);
                            ((EYSDateField)c).setDate(value);
                           }
                        else
                        {
                            ((JTextComponent) c).setText((String) data);
                        }
                    }
                    else
                    {
                        ((EYSDateField)c).setDate((EYSDate) data);
                    }
                }
                else if (c instanceof JFormattedTextField)
                {
                    ((JFormattedTextField) c).setValue((String) data);
                }
                else if (c instanceof JTextComponent)
                {
                    if(c instanceof BytesRetrievableIfc && data instanceof byte[])
                    {
                        // this fix HPQC-1610. clear previous text.
                        byte[] bytes = new byte[((byte[])data).length];
                        System.arraycopy(data, 0, bytes, 0, ((byte[])data).length);
                        ((BytesRetrievableIfc)c).clearTextBytes();
                        // end of the fix.
                        ((BytesRetrievableIfc)c).setTextBytes(bytes);
                    }
                    else if(data instanceof String)
                    {
                        ((JTextComponent) c).setText((String) data);
                    }
                }

                else if (c instanceof JLabel)
                {
                    ((JLabel) c).setText((String) data);
                }
                else if (c instanceof JScrollPane)
                {
                    POSListModel m;

                    if (data instanceof POSListModel)
                    {
                        m = (POSListModel) data;
                    }
                    else
                    {
                        m = new POSListModel();
                    }
                    JList list = (JList) ((JScrollPane) c).getViewport().getView();
                    list.setModel(m);
                    list.setSelectedValue(m.getSelectedValue(), true);
                }
                else if (c instanceof ValidatingComboBox)
                {
                    int indx = model.getSelectionIndex(c.getName());

                    //updated to allow selected index to be set from model.
                    if (data instanceof ValidatingComboBoxModel)
                    {
                        ValidatingComboBoxModel m = (ValidatingComboBoxModel) data;
                        ((ValidatingComboBox) c).setModel(m);
                    }
                    else if (data instanceof POSListModel)
                    {
                        POSListModel m = (POSListModel) data;
                        ((ValidatingComboBox) c).setModel(m);
                    }
                    else if (data instanceof ComboBoxModel)
                    {
                        ComboBoxModel m = (ComboBoxModel) data;
                        ((ValidatingComboBox) c).setModel(m);
                    }

                    ((ValidatingComboBox) c).setSelectedIndex(indx);
                }
            }

            // loop through the labels and set the label arguments
            for (int i = 0; i < labels.length; i++)
            {
                JLabel l = labels[i];
                // get the arguments from the model
                Object[] args = getLabelArgs(i);
                if (args != null && args.length > 0)
                {
                    String text = l.getText();
                    l.setText(LocaleUtilities.formatComplexMessage(text, args, getLocale()));
                }
            }
        }
    }

    /**
     * Method to get the label args if any specified in the model. If the args
     * are of methods to invoke against the fields attributes, invoke and
     * return the results.
     * 
     * @param index the index of the label 
     * @return
     */
    protected Object[] getLabelArgs(int index)
    {
        Object[] args = null;
        JLabel l = labels[index];
        String labelName = l.getName();
        if (labelName != null)
        {
            DataInputBeanModel model = (DataInputBeanModel) beanModel;
            args = model.getLabelArg(labelName);
            if (args != null)
            {
                for (int i = 0; i < args.length; i++)
                {
                    try
                    {
                        if (args[i] instanceof Method)
                        {
                            Method method = (Method)args[i];
                            args[i] = method.invoke(components[index], (Object[])null);
                        }
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return args;
    }

    /**
     *  Updates the bean when the locale changes.
     */
    protected void updatePropertyFields()
    {
        if (specs != null)
        {
            for (int i = 0; i < specs.length; i++)
            {
                String tag = specs[i].getLabelTag();
                String text = specs[i].getLabelText();
                setLabelText(i, retrieveText(tag, text));

                // if it is a validating component,
                // set the required attribute and the label
                if ((components[i] != null) && (components[i] instanceof ValidatingFieldIfc))
                {
                    ValidatingFieldIfc val = (ValidatingFieldIfc) components[i];
                    val.setLabel(labels[i]);
                    components[i] = (JComponent) val;
                }
            }
        }
    }

    /**
     * Sets the MICR data
     * 
     * @param model DeviceModelIfc
     */
    public void setMICRData(DeviceModelIfc model)
    {
        chkModel = (MICRModel) model;
        if (model != null)
        {
            DataInputBeanModel dataModel = (DataInputBeanModel) beanModel;
            //updateModel();
            String fieldName = dataModel.getMicrField();
            if (fieldName != null)
            {
               dataModel.setValue(fieldName, chkModel.getAccountNumber());
            }
            else
            {
                if (currentComponent instanceof JTextComponent)
                {
                    JTextComponent textField = ((JTextComponent) currentComponent);
                    textField.setText(chkModel.getAccountNumber());
                }
            }
            updateBean();

        }
        setCheckMICRed(true);
        if (isMailLetter)
        {
            //send next letter
            UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
        }
    }

    /**
     * Receive MSR data. Called by the UI Framework.
     * 
     * @param data DeviceModelIfc
     */
    public void setMSRData(DeviceModelIfc data)
    {

        msrModel = (MSRModel) data;
        /*
         * Need to set the card swiped attribute to true before calling updateBean.
         */
        setCardSwiped(true);
        if (msrModel != null)
        {
              updateModel();
              DataInputBeanModel dataModel = (DataInputBeanModel) beanModel;

              String fieldName = dataModel.getMsrField();
              if (fieldName != null)
              {
                 dataModel.setValue(fieldName, msrModel.getEncipheredCardData().getTruncatedAcctNumber().getBytes());
              }
              else
              {
                  if(currentComponent instanceof MaskableNumericByteTextField)
                  {
                      MaskableNumericByteTextField textField = ((MaskableNumericByteTextField) currentComponent);
                      textField.setTextBytes(msrModel.getEncipheredCardData().getTruncatedAcctNumber().getBytes());
                  }
                  else if (currentComponent instanceof JTextComponent)
                  {
                      JTextComponent textField = ((JTextComponent) currentComponent);
                      textField.setText(msrModel.getAccountNumber());
                  }
              }
              updateBean();

        }
        //re-enable msr
        try
        {
             DeviceTechnicianIfc dt =
                 (DeviceTechnicianIfc) Gateway.getDispatcher().getLocalTechnician(DeviceTechnicianIfc.TYPE);

             if (dt != null)
             {
                 try
                 {
                     String sessionName = MSRSession.TYPE;
                     MSRSession msrSession = (MSRSession) dt.getDeviceSession(sessionName);
                     msrSession.setEnabled(true);
                 }
                 catch (DeviceException e)
                 {
                     logger.error("DeviceException occurred trying to set MSR data.", e);
                 }
             }
         }
         catch (TechnicianNotFoundException e)
         {
             logger.error("DeviceTechnician not found while trying to set MSR data.", e);
         }
         catch (Exception e)
         {
             logger.error("Error occurred trying to set MSR data.", e);
         }
         if (isMailLetter)
         {
             //send next letter
             UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
         }
    }

    /**
     * Receive scanner data. Called by the UI Framework.
     * 
     * @param data DeviceModelIfc
     */
    public void setScannerData(DeviceModelIfc data)
    {
        ScannerModel scannerModel = (ScannerModel) data;
        String scanData = new String(scannerModel.getScanLabelData());

        if (!Util.isEmpty(scanData))
        {
            updateModel();
            DataInputBeanModel model = (DataInputBeanModel) beanModel;
            String scannedFieldList = model.getScannedFields();
            if (scannedFieldList != null)
            {
                StringTokenizer list = new StringTokenizer(scannedFieldList, ",");

                String fieldName, fieldLength, fieldFormat;
                int current = 0;
                updateModel();
                while (list.hasMoreTokens())
                {
                    fieldName = list.nextToken();

                    try
                    {
                        int length;
                        if (list.hasMoreTokens())
                        {
                            // Determine if the field has a format
                            // specification of the form <length>:<format> attached to it
                            // If there is a format attached pass it on so that when we
                            // access the field it will be duly noted
                            StringTokenizer list2 = new StringTokenizer(list.nextToken(), ":");
                            fieldLength = list2.nextToken();
                            length = current;
                            if (fieldLength != null)
                                length += Integer.parseInt(fieldLength);
                            if (list2.hasMoreTokens())
                            { // If format given then save it with the scanned data in the form <data>:<format>
                                fieldFormat = list2.nextToken();
                                if (fieldFormat.length() > 0)
                                {
                                    model.setValue(fieldName, scanData.substring(current, length) + ":" + fieldFormat);
                                }
                            }
                            else if (length <= scanData.length())
                            {
                                model.setValue(
                                    fieldName,
                                    scanData.substring(current, length));
                            }
                            else
                            {
                                model.setValue(
                                    fieldName,
                                    scanData.substring(current));
                                break;
                            }
                            current = length;
                        }
                        else
                        {
                            // remainder of data goes to the field
                            model.setValue(fieldName, scanData);
                        }
                    }
                    catch (Exception e)
                    {
                        // read remainder of string into this field
                        model.setValue(fieldName, scanData.substring(current));
                        break;
                    }
                }
            }
            else
            {
                // write scanned data to the current focused component
                model.setValue(currentComponent.getName(), scanData);
            }
            model.setScanned(true);

            updateBean();

        }
        //re-enable scans
        try
        {
            DeviceTechnicianIfc dt =
                (DeviceTechnicianIfc) Gateway.getDispatcher().getLocalTechnician(DeviceTechnicianIfc.TYPE);

            if (dt != null)
            {
                try
                {
                    String sessionName = ScannerSession.TYPE;
                    ScannerSession scannerSession = (ScannerSession) dt.getDeviceSession(sessionName);
                    scannerSession.setEnabled(true);
                }
                catch (DeviceException e)
                {
                    logger.error("DeviceException occurred trying to set scanner data.", e);
                }
            }
        }
        catch (TechnicianNotFoundException e)
        {
            logger.error("DeviceTechnician not found while trying to set scanner data.", e);
        }
        catch (Exception e)
        {
            logger.error("Error occurred trying to set scanner data.", e);
        }

        if (isMailLetter)
        {
            //send next letter
            UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
        }
    }

    /**
     *  Main entry point for testing.
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        FieldSpec[] testSpecs = new FieldSpec[2];

        testSpecs[0] = new FieldSpec();
        testSpecs[0].setFieldName("lastName");
        testSpecs[0].setFieldType("alphaNumericField");
        testSpecs[0].setLabelText("Last Name:");
        testSpecs[0].setEnabled("true");
        testSpecs[0].setRequired("false");
        testSpecs[0].setParamList("lastName,1,15");

        testSpecs[1] = new FieldSpec();
        testSpecs[1].setFieldName("firstName");
        testSpecs[1].setFieldType("alphaNumericField");
        testSpecs[1].setLabelText("First Name:");
        testSpecs[1].setEnabled("true");
        testSpecs[1].setRequired("true");
        testSpecs[1].setParamList("firstName,1,15");

        DataInputBean bean = new DataInputBean();
        bean.configureFields(testSpecs);
        bean.configure();

        DataInputBeanModel beanModel = new DataInputBeanModel();
        beanModel.setValue("firstName", "Sam");
        bean.setModel(beanModel);

        UIUtilities.doBeanTest(bean);
    }

    /**
     *  Requests focus on parameter value name field if visible is true.
     *  @param aFlag true if setting visible, false otherwise
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            if(components[0].isEnabled())
            {
               // make sure initial focus goes to the first field
               setCurrentFocus(components[0]);
            }
        }
    }

    /**
     * Set flag to determine if a next letter will be automatically send with
     * scanned input
     * 
     * @return Returns the isMailLetter.
     */
    public boolean isMailLetter()
    {
        return isMailLetter;
    }

    /**
     * Set flag to determine if a next letter will be automatically send with
     * scanned input
     * 
     * @param value The isMailLetter to set.
     */
    public void setMailLetter(String value)
    {
        isMailLetter = new Boolean(value).booleanValue();
    }

    /**
     * Gets flag that indicates if input data was scanned
     * 
     * @return Returns the isScanned.
     */
    public boolean isScanned()
    {
        return isScanned;
    }

    /**
     * Sets flag that indicates if input data was scanned
     * 
     * @param isScanned The isScanned to set.
     */
    public void setScanned(boolean isScanned)
    {
        this.isScanned = isScanned;
    }

    /**
     * Gets flag that indicates if card field was swipe
     * 
     * @return Returns the isCardSwiped.
     */
    public boolean isCardSwiped()
    {
        return isCardSwiped;
    }

    /**
     * Sets flag that indicates if card field was swipe
     * 
     * @param isCardSwiped The isCardSwiped to set.
     */
    public void setCardSwiped(boolean isCardSwiped)
    {
        this.isCardSwiped = isCardSwiped;
    }

    /**
     * Gets flag that indicates if data field was MICRed
     * 
     * @return Returns the isCheckMICRed.
     */
    public boolean isCheckMICRed()
    {
        return isCheckMICRed;
    }

    /**
     * Sets flag to indicate data field was MICRed
     * 
     * @param isCheckMICRed The isCheckMICRed to set.
     */
    public void setCheckMICRed(boolean isCheckMICRed)
    {
        this.isCheckMICRed = isCheckMICRed;
    }
}
