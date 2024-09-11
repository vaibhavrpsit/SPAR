/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReasonCodeBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:41 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:32 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.4  2004/05/21 15:14:02  dfierling
 *   @scr 4963 - Modified for case sensitivity issue
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 02 2003 16:15:42   kll
 * increase length construction parameter
 * Resolution for 2520: Max Length incorrect for Reason Code Name field on Add and Edit Reason Code Screens
 * 
 *    Rev 1.1   Sep 16 2003 17:53:00   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:11:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.5   Jul 16 2003 10:16:50   bwf
 * Added isAlreadyInternationalized method.
 * Resolution for 3174: Editing preexisting Reason Codes display Reason Code with <> on Edit Reason Code
 * 
 *    Rev 1.4   Jul 01 2003 16:53:00   bwf
 * Check if default reason code name "" before internationalizing.
 * Resolution for 2965: Reason Code Name field defaults only <> when adding new reason codes
 * 
 *    Rev 1.3   May 20 2003 10:40:48   adc
 * changes for internationalization
 * Resolution for 2286: Edit Reason Code Screen not displaying correct Name and ID information
 * 
 *    Rev 1.2   May 05 2003 18:52:56   bwf
 * In updatePropertyFields, checked instanceof this to set correct type of field.
 * Resolution for 2297: System locks up while in Units of Measure Reason Code group
 * 
 *    Rev 1.1   Feb 24 2003 10:45:00   HDyer
 * Display localized string.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.0   Apr 29 2002 14:47:58   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:35:44   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:57:06   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 09 2002 10:46:12   mpm
 * More text externalization.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// swing import
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *    This bean enables the editing of a reason code.
 *    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class ReasonCodeBean extends ValidatingBean
{
    // label text constants
    protected static final String RC_GROUP_PROMPT_LABEL = "ListEditorReasonCodeGroupLabel";
    protected static final String RC_PROMPT_LABEL       = "ListEditorReasonCodeNameLabel";
    protected static final String PM_GROUP_PROMPT_LABEL = "ListEditorParameterGroupLabel";
    protected static final String PM_PROMPT_LABEL       = "ListEditorParameterNameLabel";

    protected static final String RC_GROUP_PROMPT = "Reason Code Group:";
    protected static final String RC_PROMPT       = "Reason Code Name:";
    protected static final String PM_GROUP_PROMPT = "Parameter Group:";
    protected static final String PM_PROMPT       = "Parameter Name:";

    public static final int GROUP      = 0;
    public static final int NAME       = 1;
    public static final int DATABASE   = 2;
    public static final int MAX_FIELDS = 3;

    public static final String[] seedText =
        {"Reason Code Group:", "Reason Code Name:", "Database ID:"};

    public static final String[] labelTags =
        {"ListEditorReasonCodeGroupLabel",
         "ListEditorReasonCodeNameLabel",
         "ListEditorDatabaseIDLabel"};

    protected JLabel[] labels = null;

    /** The bean model */
    protected ReasonCode beanModel = null;

    /** Reason group label */
    protected JLabel reasonCodeGroupField = null;

    /** Reason code name field */
    protected ConstrainedTextField reasonCodeNameField = null;

    /** the database id field */
    protected JTextField databaseIdField = null;

    //--------------------------------------------------------------------------
    /**
        Constructs bean.
    **/
    public ReasonCodeBean()
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
        setName("ReasonCodeBean");
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
        labels = new JLabel[MAX_FIELDS];
        //fields = new JComponent[MAX_FIELDS];

        String labelText = null;
        for(int i=0; i<MAX_FIELDS; i++)
        {
            labelText = retrieveText(labelTags[i], seedText[i]);

            labels[i] = uiFactory.createLabel(labelText, null, UI_LABEL);
        }

        reasonCodeGroupField = uiFactory.createLabel("**********", null, UI_LABEL);

        reasonCodeNameField = uiFactory.createConstrainedField("reasonCodeNameField", "1", "40");

        reasonCodeNameField.setHorizontalAlignment(SwingConstants.LEFT);
        reasonCodeNameField.setEmptyAllowed(false);

        databaseIdField = initializeDatabaseIDField();
    }

    //--------------------------------------------------------------------------
    /**
     *    Layout the components.
     */
    public void initLayout()
    {
        UIUtilities.layoutDataPanel(this, labels,
                                    new JComponent[]
                                    {
                                        reasonCodeGroupField,
                                        reasonCodeNameField,
                                        databaseIdField
                                    });
    }

    //--------------------------------------------------------------------------
    /**
        Initializes the class.
    **/
    protected JTextField initializeDatabaseIDField()
    {
        NumericTextField field = uiFactory.createNumericField("databaseIdField","1","8");
        field.setEmptyAllowed(false);
        return field;
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
            throw new NullPointerException("Attempt to set ReasonCodeBean model to null.");
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
        reasonCodeGroupField.setText(retrieveText(beanModel.getReasonCodeGroup(),
                                     beanModel.getReasonCodeGroup()));
        if(beanModel.getReasonCodeName().equals("") ||  // if adding, there is no default.  " " is not a key.
           isAlreadyInternationalized(beanModel.getReasonCodeGroup()))
        {
            reasonCodeNameField.setText(beanModel.getReasonCodeName());
        }
        else
        {
            reasonCodeNameField.setText(retrieveText(beanModel.getReasonCodeName()));
        }

        if (beanModel.getModifyingParameter())
        {
            labels[GROUP].setText(retrieveText(PM_GROUP_PROMPT_LABEL,
                                               PM_GROUP_PROMPT));
            labels[NAME].setText(retrieveText(PM_PROMPT_LABEL,
                                              PM_PROMPT));
            labels[DATABASE].setVisible(false);
            databaseIdField.setVisible(false);
        }
        else
        {
            labels[GROUP].setText(retrieveText(RC_GROUP_PROMPT_LABEL,
                                               RC_GROUP_PROMPT));
            labels[DATABASE].setVisible(true);
            databaseIdField.setVisible(true);
            databaseIdField.setText(beanModel.getDatabaseId());
        }
    }

    //--------------------------------------------------------------------------
    /**
        Updates the bean model, populated with data from the bean. <P>
    **/
    public void updateModel()
    {
        beanModel.setNewReasonCodeName(reasonCodeNameField.getText());

        if (beanModel.getModifyingParameter())
        {
            beanModel.setNewDatabaseId(CodeConstantsIfc.CODE_UNDEFINED);
        }
        else
        {
            beanModel.setNewDatabaseId(databaseIdField.getText());
        }
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
     *    Requests focus on reason code name field if visible is true.
     *    @param aFlag true if setting visible, false otherwise
    **/
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(reasonCodeNameField);
        }
    }
    
    //---------------------------------------------------------------------
    /**
      Activates this bean.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        reasonCodeNameField.addFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
      Deactivates this bean.
    **/
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        reasonCodeNameField.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for(int i = 0; i < MAX_FIELDS; i++)
        {
            labels[i].setText(retrieveText(labelTags[i], seedText[i]));
        }
        reasonCodeNameField.setLabel(labels[NAME]);
        if(this instanceof AlphaReasonCodeBean)
        {
            ((ConstrainedTextField)databaseIdField).setLabel(labels[DATABASE]);
        }
        else
        {
            ((NumericTextField)databaseIdField).setLabel(labels[DATABASE]);
        }
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /** 
        Determines whether reason codes already internationalized by group type.
        @param String group name
        @return boolean internationalized     
     **/
    //---------------------------------------------------------------------
    public boolean isAlreadyInternationalized(String groupName)
    {
        boolean internationalized = false;
        if(groupName.equals("MarkdownPercentReasonCodes") ||
           groupName.equals("MarkdownAmountReasonCodes") ||
           groupName.equals("PreferredCustomerDiscount") ||
           groupName.equals("ItemDiscountByAmount") ||
           groupName.equals("ItemDiscountByPercentage") ||
           groupName.equals("TransactionDiscountByAmount") ||
           groupName.equals("TransactionDiscountByPercentage") ||
           groupName.equals("UnitOfMeasure") ||
           groupName.equals("ShippingMethod") ||
           groupName.equals("Department"))
           {
               internationalized = true;
           }
        
        return(internationalized);
    }
    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: ReasonCodeBean (Revision " +
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
        UIUtilities.doBeanTest(new ReasonCodeBean());

    }
}
