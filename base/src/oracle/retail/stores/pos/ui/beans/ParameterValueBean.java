/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ParameterValueBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    I18N_P2    1.2.1.1     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    4    I18N_P2    1.2.1.0     12/18/2007 3:09:14 PM  Sandy Gu        static
 *          text fix for POS
 *    3    360Commerce 1.2         3/31/2005 4:29:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:59 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 16 2003 17:52:54   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:11:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:20   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:55:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   15 Apr 2002 09:35:42   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:56:48   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 04 2002 14:15:40   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   16 Feb 2002 10:17:16   baa
 * fix required field logic
 * Resolution for POS SCR-1306: Invalid Data Notice missing after selecting Enter with no data for text area fields
 *
 *    Rev 1.1   04 Feb 2002 13:50:38   KAC
 * Setting up required fields
 * Resolution for POS SCR-1012: Edit Parameter screen missing * for required info
 *
 *    Rev 1.0   22 Jan 2002 13:39:14   KAC
 * Initial revision.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// swing import
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
    This bean enables the adding or editing of a parameter value.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ParameterValueBean extends ValidatingBean
{
    // label text constants
    public static final int VALUE_LABEL = 1;

    public static final int GROUP      = 0;
    public static final int NAME       = 1;
    public static final int MAX_FIELDS = 2;

    public static final String[] seedText =
        {"ParameterName:", "Enter New Value:"};

    public static final String[] labelTags =
        {"ParameterNameLabel", "ParameterEnterNewValueLabel"};

    protected JLabel[] labels = null;

    /** The bean model */
    protected ReasonCode beanModel = null;

    /** Reason group label */
    protected JLabel parameterNameField = null;

    /** parameter value name field */
    protected ConstrainedTextField parameterValueField = null;

    //--------------------------------------------------------------------------
    /**
        Constructs bean.
    **/
    public ParameterValueBean()
    {
        super();
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
        Initializes the class.
    **/
    protected void initialize()
    {
        setName("ParameterValueBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    //--------------------------------------------------------------------------
    /**
        Initializes the class.
    **/
    protected void initComponents()
    {
        labels = new JLabel[seedText.length];

        // create the display labels
        for(int i=0; i < seedText.length; i++)
        {
            labels[i] = uiFactory.createLabel(seedText[i], null, UI_LABEL);
        }

        parameterNameField =
            uiFactory.createLabel("parameterVName", null, UI_LABEL);

        parameterValueField =
            uiFactory.createConstrainedField("parameterValueField", "1", "30", false);
        parameterValueField.setHorizontalAlignment(SwingConstants.LEFT);
    }

    //--------------------------------------------------------------------------
    /**
     *  Layout the components.
     */
    public void initLayout()
    {
        UIUtilities.layoutDataPanel(this, labels,
                                    new JComponent[]
                                    {
                                        parameterNameField,
                                        parameterValueField
                                    });
    }


    //--------------------------------------------------------------------------
    /**
        Sets the model data into the bean fields. <P>
        @param model the bean model
     */
    public void setModel(UIModelIfc model)
    {                                   // begin setModel()
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ParameterValueBean model to null.");
        }
        if (model instanceof ReasonCode)
        {
            beanModel = (ReasonCode)model;
            updateBean();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Update the bean if the model has changed
     */
    protected void updateBean()
    {
        parameterNameField.setText(beanModel.getReasonCodeGroup());
        parameterValueField.setText(beanModel.getReasonCodeName());
    }

    //--------------------------------------------------------------------------
    /**
        Updates the bean model, populated with data from the bean. <P>
    **/
    public void updateModel()
    {
        beanModel.setNewReasonCodeName(parameterValueField.getText());
        beanModel.setNewDatabaseId(CodeConstantsIfc.CODE_UNDEFINED);
    }

    //--------------------------------------------------------------------------
    /**
       Gets the POSBaseBeanModel associated with this bean.
       @return the POSBaseBeanModel associated with this bean.
    */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before display
     */
    public void activate()
    {
        super.activate();
        parameterValueField.addFocusListener(this); 
    }
    
    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before leaving bean
     */
    public void deactivate()
    {
        super.deactivate();
        parameterValueField.removeFocusListener(this);
    } 

    //--------------------------------------------------------------------------
    /**
     *  Requests focus on parameter value name field if visible is true.
     *  @param aFlag true if setting visible, false otherwise
    **/
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(parameterValueField);
        }
    }

    //---------------------------------------------------------------------
    /**
       Updates fields based on properties.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for (int i = 0; i < seedText.length; i++)
        {
            labels[i].setText(retrieveText(labelTags[i],
                                           labels[i]));
        }
        // associate fields with labels
        parameterValueField.setLabel(labels[VALUE_LABEL]);
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: ParameterValueBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
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
        return(Util.parseRevisionNumber(revisionNumber));
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
        UIUtilities.doBeanTest(new ParameterValueBean());

    }
}
