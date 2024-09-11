/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SpecialOrderDepositBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         8/7/2007 4:45:03 PM    Maisa De Camargo
 *         Updated type of SpecialOrderDepositBeanModel.depositAmountValue
 *         field from String to CurrencyIfc.
 *    5    360Commerce 1.4         1/25/2006 4:11:47 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:46 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     9/19/2005 13:48:09     Jason L. DeLeau Make
 *         sure CurrencyTextFields can have a blank default value.
 *    3    360Commerce1.2         3/31/2005 15:30:07     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:24     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:20     Robert Pearse
 *
 *Log:
 *    6    360Commerce 1.5         8/7/2007 4:45:03 PM    Maisa De Camargo
 *         Updated type of SpecialOrderDepositBeanModel.depositAmountValue
 *         field from String to CurrencyIfc.
 *    5    360Commerce 1.4         1/25/2006 4:11:47 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:46 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
 *: SpecialOrderDepositBean.java,v $
 *Log:
 *    6    360Commerce 1.5         8/7/2007 4:45:03 PM    Maisa De Camargo
 *         Updated type of SpecialOrderDepositBeanModel.depositAmountValue
 *         field from String to CurrencyIfc.
 *    5    360Commerce 1.4         1/25/2006 4:11:47 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:46 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
 *:
 *    5    .v710     1.2.2.1     10/24/2005 14:20:53    Charles Suehs   Merged
 *         from .v700 to fix CR 3965.
 *    4    .v710     1.2.2.0     10/20/2005 18:26:41    Charles Suehs   Merge
 *         from SpecialOrderDepositBean.java, Revision 1.2.1.0
 *    3    360Commerce1.2         3/31/2005 15:30:07     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:24     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:20     Robert Pearse
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
 *    Rev 1.1   Sep 16 2003 17:53:20   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:12:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   17 Aug 2003 22:30:36   baa
 * currency formatting issues
 *
 *    Rev 1.1   Aug 14 2002 18:18:44   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:50:06   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:53:54   msg
 * Initial revision.
 *
 *    Rev 1.8   Mar 02 2002 17:58:56   mpm
 * Internationalized order UI.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.7   11 Feb 2002 17:00:40   baa
 * fix  value field box
 * Resolution for POS SCR-1219: LayawayLegalStmt parameter value field box too small to see data
 *
 *    Rev 1.6   Jan 30 2002 17:10:18   dfh
 * fix setmodel
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.5   Jan 28 2002 09:47:56   dfh
 * test
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.4   Jan 28 2002 09:29:40   dfh
 * use zero currency field - for deposit amount
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   Jan 20 2002 10:45:54   dfh
 * enlarged customer name field and sp order number field
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   Jan 19 2002 10:31:58   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   Dec 12 2001 13:24:16   dfh
 * do not allow negative values for deposit amount
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Dec 04 2001 12:15:44   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports

// javax imports
import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *  This bean is used for displaying the Special Order Deposit
 *  screen. The user enters a deposit amount.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class SpecialOrderDepositBean extends ValidatingBean
{
    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // field number constants
    public static final int CUSTOMER          = 0;
    public static final int SPECIAL_ORDER_NUM = 1;
    public static final int BALANCE_DUE       = 2;
    public static final int MINIMUM_DEPOSIT   = 3;
    public static final int DEPOSIT_AMOUNT    = 4;

    /** array of label text */
    protected static String[] labelText =
    {
        "Customer:", "Special Order Number:", "Balance Due:",
        "Minimum Deposit Due:", "Deposit Amount:"
    };

    protected static String[] labelTags =
    {
        "CustomerLabel", "SpecialOrderNumberLabel", "BalanceDueLabel",
        "MinimumDepositDueLabel", "DepositAmountLabel"
    };

    protected JLabel[] labels;

    /** display field for customer name */
    protected JLabel customerField;

    /** display field for special order number */
    protected JLabel specialOrderNumberField;

    /** display field for balance due */
    protected JLabel balanceDueField;

    /** display field for minimum deposit */
    protected JLabel minimumDepositField;

    /** data entry field for deposit amount */
    protected CurrencyTextField depositAmountField;

    /** the Bean model */
    protected UIModelIfc beanModel;

    //------------------------------------------------------------------------------
    /**
     *  Default Constructor.
     */
    //------------------------------------------------------------------------------
    public SpecialOrderDepositBean()
    {
        super();
    }

    //------------------------------------------------------------------------------
    /**
     *  Configures the class.
     */
    //------------------------------------------------------------------------------
    public void configure()
    {
        beanModel = new SpecialOrderDepositBeanModel();
        setName("SpecialOrderDeposit");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        labels = new JLabel[labelText.length];

        for(int i=0; i<labelText.length; i++)
        {
            labels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
        }

        // create the fields
        customerField = uiFactory.createLabel("CustomerField",null,UI_LABEL);

        specialOrderNumberField = uiFactory.createLabel("SpecialOrderNumberField",null,UI_LABEL);

        balanceDueField = uiFactory.createLabel("BalanceDueField",null,UI_LABEL);

        minimumDepositField = uiFactory.createLabel("MinimumDepositField",null,UI_LABEL);

        depositAmountField =
            uiFactory.createCurrencyField("DepositAmountField", "true", "true", "true");

        UIUtilities.layoutDataPanel(
            this,
            labels,
            new JComponent[] {customerField,
                              specialOrderNumberField,
                              balanceDueField,
                              minimumDepositField,
                              depositAmountField}
        );
    }

    //------------------------------------------------------------------------------
    /**
     *  Returns the base bean model.
     *  @return POSBaseBeanModel
     */
    //------------------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return (POSBaseBeanModel)beanModel;
    }


    //------------------------------------------------------------------------------
    /**
     *  Updates the model from the screen.
     */
    //------------------------------------------------------------------------------
    public void updateModel()
    {
        SpecialOrderDepositBeanModel m = (SpecialOrderDepositBeanModel)beanModel;

        m.setBalanceDueValue(balanceDueField.getText());
        if (depositAmountField.getCurrencyValue() != null)
        {
            m.setDepositAmountValue(depositAmountField.getCurrencyValue());
        }
        else
        {
            m.setDepositAmountValue(DomainGateway.getBaseCurrencyInstance("0.00"));
        }
    }

    //------------------------------------------------------------------------------
    /**
     *  Sets the model property value.
     *  @param model UIModelIfc the new value for the property.
     */
    //------------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set " +
                "SpecialOrderDepositBean model to null");
        }
        else
        {
           if (model instanceof SpecialOrderDepositBeanModel)
            {
                beanModel = (SpecialOrderDepositBeanModel) model;
                updateBean();
            }
        }
    }

    //------------------------------------------------------------------------------
    /**
     *  Updates the information displayed on the screen if the model's
     *  been changed.
     */
    //------------------------------------------------------------------------------
    protected void updateBean()
    {
        SpecialOrderDepositBeanModel m = (SpecialOrderDepositBeanModel)beanModel;

        customerField.setText(m.getCustomerValue());
        specialOrderNumberField.setText(m.getSpecialOrderNumberValue());
        balanceDueField.setText(m.getBalanceDueValue());
        minimumDepositField.setText(m.getMinimumDepositValue());
        depositAmountField.setValue(m.getDepositAmountValue());
    }

    //------------------------------------------------------------------------------
    /**
       Override ValidatingBean setVisible() to request focus. Uses the internal
       focusField attribute.
       @param  aFlag indicates if the component should be visible or not.
    **/
    //--------------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        // ValidatingBean sets the errorFound flag when it finds a
        // validation error. If an error has been found, ValidatingBean
        // sets the focus in the first error field.

        if (aFlag && !getErrorFound())
        {
            setCurrentFocus(depositAmountField);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Activates this bean.
     */
    public void activate()
    {
        super.activate();
        depositAmountField.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *  Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        depositAmountField.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
       Updates fields based on properties.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for (int i = 0; i < labelText.length; i++)
        {
            labels[i].setText(retrieveText(labelTags[i],
                                           labels[i]));
        }
    }                                   // end updatePropertyFields()

    //------------------------------------------------------------------------------
    /**
     *  Returns a string representation of this object.
     *  @return String representation of object
     */
    //------------------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class:  SpecialOrderDepositBean (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }

    //------------------------------------------------------------------------------
    /**
     *  Retrieves the revision number.
     *  @return String representation of revision number
     */
    //------------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }


    //------------------------------------------------------------------------------
    /**
     *  Entry point for testing.
     *  @param args command line parameters
     */
    //------------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();


        SpecialOrderDepositBean
            bean = new SpecialOrderDepositBean();
            bean.configure();
            bean.updateBean();

        UIUtilities.doBeanTest(bean);
    }
}
