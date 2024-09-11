/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditISODateParameterBean.java /main/18 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nganesh   04/29/09 - Modified updatePropertyFields method to
 *                         internationalize all the propery labels
 *    acadar    04/22/09 - translate date/time labels
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         5/20/2007 7:35:34 PM   Mathews Kochummen use
 *        locale date format
 *   3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse
 *
 *  Revision 1.3  2004/07/17 19:21:23  jdeleau
 *  @scr 5624 Make sure errors are focused on the beans, if an error is found
 *  during validation.
 *
 *  Revision 1.2  2004/04/09 16:56:00  cdb
 *  @scr 4302 Removed double semicolon warnings.
 *
 *  Revision 1.1  2004/03/19 21:02:56  mweis
 *  @scr 4113 Enable ISO_DATE datetype
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java import

// swing imports
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
    This bean enables the editing of a parameter that has a date value.
    @version $Revision: /main/18 $
**/
//---------------------------------------------------------------------
public class EditISODateParameterBean extends ValidatingBean
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/18 $";

    /** Specifies which label is our field's label. */
    public static final int VALUE_LABEL = 2;

    /** Array of label text. */
    public static String[] labelText =
    {
        "Parameter Group:", "Parameter Name:", "Parameter Value ({0}):"
    };

    /** Array of label tags. */
    public static String[] labelTags =
    {
        "ParameterGroupLabel", "ParameterNameLabel", "ParameterDateValueLabel"
    };

    protected JLabel[] labels = null;

    protected ISODateParameterBeanModel beanModel = new ISODateParameterBeanModel();

    protected JLabel parameterGroupField = null;

    protected JLabel parameterNameField = null;

    protected EYSDateField eysDateField = null;

    //protected JList modifiableChoiceList = null;

    protected Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.EditISODateParameterBean.class);

    //---------------------------------------------------------------------
    /**
        Constructs bean.
     */
    //---------------------------------------------------------------------
    public EditISODateParameterBean()
    {
        super();
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the class.
     */
    protected void initialize()
    {
        setName("EditISODateParameterBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the components.
     */
    protected void initComponents()
    {
        labels = new JLabel[labelText.length];

        // create the display labels
        for(int i=0; i<labelText.length; i++)
        {
            labels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
        }

        // create non-enterable display fields
        parameterGroupField =
            uiFactory.createLabel("parameterGroup", null, UI_LABEL);
        parameterNameField  =
            uiFactory.createLabel("parameterName", null, UI_LABEL);

        // create the enterable fields
        eysDateField = uiFactory.createEYSDateField("EYSDateField");
   }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        JComponent[] fields =
        {
            parameterGroupField,
            parameterNameField,
            eysDateField
        };
        UIUtilities.layoutDataPanel(this, labels, fields);
    }


    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before display
     */
    public void activate()
    {
        super.activate();
        eysDateField.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before leaving bean
     */
    public void deactivate()
    {
        super.deactivate();
        eysDateField.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
        Overrides JPanel setVisible() method to request focus. <P>
        @param aFlag Whether to give the parameter the current focus.
    **/
    //---------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(eysDateField);
        }
    }

    //---------------------------------------------------------------------
    /**
     * Returns the field that was marked as a required validating field.
     * @return The field that was marked as a required validating field.
     */
    //---------------------------------------------------------------------
    public EYSDateField getEYSDateField()
    {
        // referenced in parameteruicfg.xml
        return eysDateField;
    }

    //-----------------------------------------------------------------------
    /**
        Returns the POSBaseBeanModel associated with this bean.
        @return the POSBaseBeanModel associated with this bean.
    **/
    //-----------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return(beanModel);
    }

    //---------------------------------------------------------------------
    /**
        Updates the bean model, populated with data from the bean. <P>
    **/
    //---------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setNewValue(eysDateField.getEYSDate());
    }

    //---------------------------------------------------------------------
    /**
        Sets the model data into the bean fields. <P>
        @param model the bean model
    **/
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("An attempt was made to set the EditISODateParameterBean model to null.");
        }
        else if (model instanceof ISODateParameterBeanModel)
        {
            beanModel = (ISODateParameterBeanModel)model;
            updateBean();
        }
        else
        {
            logger.warn("EditISODateParameterBean.setModel does not handle " + beanModel.getClass().getName() + "");
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the bean, populated with data from the model. <P>
    **/
    //---------------------------------------------------------------------
    public void updateBean()
    {
        String group = beanModel.getParameterGroup();
        String name = beanModel.getParameterName();
        parameterGroupField.setText(retrieveText(group,group));
        parameterNameField.setText(retrieveText(name,name));
        Locale locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        eysDateField.setText(beanModel.getOldValue().toFormattedString(locale));
    }

    //---------------------------------------------------------------------
    /**
       Updates fields based on properties.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        //use locale appropriate date label
        String translatedLabel = getTranslatedDatePattern();
        for (int i = 0; i < labelText.length; i++)
        {
            labels[i].setText(retrieveText(labelTags[i], labels[i]));
        }
        labels[VALUE_LABEL].setText(LocaleUtilities.formatComplexMessage(labels[VALUE_LABEL].getText(), translatedLabel));

        eysDateField.setLabel(labels[VALUE_LABEL]);
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: EditISODateParameterBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
        Displays the bean in it's own frame. <p>
        @param args command line arguments
    **/
    //---------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        EditISODateParameterBean bean = new EditISODateParameterBean();
        UIUtilities.doBeanTest(bean);
    }
}
