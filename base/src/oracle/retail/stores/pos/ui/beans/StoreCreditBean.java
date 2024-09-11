/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StoreCreditBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - translate date/time labels
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:30:11 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:25:31 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:14:26 PM  Robert Pearse
 *
 *  Revision 1.4  2004/07/17 19:21:23  jdeleau
 *  @scr 5624 Make sure errors are focused on the beans, if an error is found
 *  during validation.
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Sep 17 2003 17:47:18   jriggins
 * Using the formatComplexMessage() method to fill in the string parameter.
 * Resolution for 2586: "{0}" shows on Store Cr. Info screen
 *
 *    Rev 1.1   Sep 16 2003 17:53:24   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:12:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 07 2002 19:34:26   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:51:48   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:36:20   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:57:44   msg
 * Initial revision.
 *
 *    Rev 1.4   07 Mar 2002 17:04:14   cir
 * Changed updateModel
 * Resolution for POS SCR-935: Expiration Date defaults to 01/01/1970 on Store Credit Info screen when Enter selected with no data
 *
 *    Rev 1.3   Mar 01 2002 10:02:56   mpm
 * Internationalization of tender-related screens
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
 *      Work panel bean for store credit info entry
 */
//---------------------------------------------------------------------
public class StoreCreditBean extends ValidatingBean
{
    // Revision number
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final int CREDIT_ID  = 0;
    public static final int AMOUNT     = 1;
    public static final int EXPIRE     = 2;
    public static final int MAX_FIELDS = 3;

    public static final String[] labelText =
        {"Store Credit Number:", "Amount:", "Expiration Date {0}:"};

    public static final String[] labelTags =
        {"StoreCreditNumberLabel", "AmountLabel", "ExpirationDateLabel"};

    protected JLabel[] labels = null;

    // StoreCreditID value label
    protected JLabel storeCreditIdField = null;

    // Amount field
    protected CurrencyTextField amountField = null;

    // Expiration Date field
    protected EYSDateField expirationDateField = null;

    // Expiration Date Not Required field
    protected JLabel expirationDateNotRequiredLabel = null;

    // The bean model
    protected StoreCreditBeanModel beanModel = null;

    // Flag indicating the model has changed
    // @deprecated as of release 5.5 no longer used.
    protected boolean dirtyModel = true;

    /** Store instance of logger here **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.StoreCreditBean.class);

    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public StoreCreditBean()
    {
        super();
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    public void configure()
    {
        setName("StoreCreditBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    protected void initComponents()
    {
        labels = new JLabel[MAX_FIELDS];

        for(int i=0; i<MAX_FIELDS; i++)
        {
            labels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
        }

        storeCreditIdField              = uiFactory.createLabel("", null, UI_LABEL);
        amountField                     = uiFactory.createCurrencyField("AmountField");
        expirationDateField             = uiFactory.createEYSDateField("ExpirationDateField");
        expirationDateNotRequiredLabel  = uiFactory.createLabel("N/A", null, UI_LABEL);

    }

    protected void initLayout()
    {
        JComponent[] comps =
            new JComponent[]{storeCreditIdField,amountField,expirationDateField};

        UIUtilities.layoutDataPanel(this, labels, comps);

        // overlay the not required label in the same space as the data field
        GridBagLayout layout = (GridBagLayout)this.getLayout();
        GridBagConstraints gbc = layout.getConstraints(expirationDateField);
        add(expirationDateNotRequiredLabel, gbc);
    }

    //---------------------------------------------------------------------
    /**
     *  Activates the bean
     */
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        amountField.addFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
     *  Clear the fields on the screen
     */
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        amountField.setText("");
        expirationDateField.setText("");
        amountField.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
     * Updates the model property
     */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setAmount(amountField.getCurrencyValue());

        // don't set the date if the field is empty
        EYSDate date = expirationDateField.getDate();
        if (date != null)
        {
          beanModel.setExpirationDate(date);
        }
    }

    //---------------------------------------------------------------------
    /**
     * Sets the model property
     * @param model UIModelIfc
     */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set StoreCreditBean model to null");
        }
        if (model instanceof StoreCreditBeanModel)
        {
            beanModel = (StoreCreditBeanModel) model;
             updateBean();
        }
    }

    //---------------------------------------------------------------------
    /**
     * Do actual updating of bean from the model
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
         storeCreditIdField.setText(beanModel.getStoreCreditID());
         if (beanModel.getAmount() != null)
         {
             amountField.setCurrencyValue(beanModel.getAmount());
         }

         if (beanModel.isExpirationDateRequired() && beanModel.getExpirationDate() != null)
         {
             expirationDateField.setDate(beanModel.getExpirationDate());
         }

         if (beanModel.isExpirationDateRequired())
         {
             expirationDateField.setVisible(true);
             expirationDateNotRequiredLabel.setVisible(false);
         }
         else
         {
             setFieldRequired(expirationDateField, false);
             expirationDateField.setVisible(false);
             expirationDateNotRequiredLabel.setVisible(true);
         }
     }


    //-----------------------------------------------------------------------
    /**
        Overiden method from ValidatingBean
        @param visible whether to make this screen visible or hidden
    **/
    //-----------------------------------------------------------------------
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if(visible && !errorFound())
        {
            setCurrentFocus(amountField);
        }
    }

    //-----------------------------------------------------------------------
    /**
       Gets the POSBaseBeanModel associated with this bean.
       @return the POSBaseBeanModel associated with this bean.
    */
    //-----------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //---------------------------------------------------------------------------
    /**
     *  Update property fields.
     */
    //---------------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            labels[i].setText(retrieveText(labelTags[i],
                                           labels[i]));
        }

        //associate labels with fields
        amountField.setLabel(labels[AMOUNT]);

        String translatedLabel = getTranslatedDatePattern();
        labels[EXPIRE].setText(LocaleUtilities.formatComplexMessage(labels[EXPIRE].getText(),translatedLabel));
        expirationDateField.setLabel(labels[EXPIRE]);
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: StoreCreditBean (Revision " +
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

        StoreCreditBean
            bean = new StoreCreditBean();
            bean.configure();

        UIUtilities.doBeanTest(bean);
    }
}
