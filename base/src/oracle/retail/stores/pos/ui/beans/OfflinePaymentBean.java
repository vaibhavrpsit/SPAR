/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OfflinePaymentBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mkochumm  02/12/09 - use default locale for dates
 *    asinton   11/19/08 - Changed calls from getScanData to getScanLabelData.
 *
 * ===========================================================================
 * $Log:
 *    5    I18N_P2    1.2.3.1     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    4    I18N_P2    1.2.3.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *         alphanumerice fields for I18N purpose
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:48 PM  Robert Pearse
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
 *    Rev 1.2   Sep 15 2003 08:37:38   rsachdeva
 * Min/Max
 * Resolution for POS SCR-2880: Offline Payment screen has min /max discrepencies to functional req. doc
 *
 *    Rev 1.0.1.0   Sep 10 2003 09:56:46   rsachdeva
 * Customer Name Field Max
 * Resolution for POS SCR-2880: Offline Payment screen has min /max discrepencies to functional req. doc
 *
 *    Rev 1.0   Aug 29 2003 16:11:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.7   10 Jul 2003 00:47:26   baa
 * customerName field type change
 *
 *    Rev 1.6   09 Jul 2003 23:19:18   baa
 * modify screen to get customer name
 *
 *    Rev 1.5   Jul 09 2003 10:38:02   bwf
 * Added deactivate method.
 * Resolution for 2729: At Layaway Offline Payment screen, scaner read number only one time
 *
 *    Rev 1.4   Jul 09 2003 10:32:48   bwf
 * Added document listener.
 * Resolution for 2729: At Layaway Offline Payment screen, scaner read number only one time
 *
 *    Rev 1.3   Aug 28 2002 17:06:18   dfh
 * set labels for error dialog
 * Resolution for POS SCR-1760: Layaway feature updates
 *
 *    Rev 1.2   Aug 14 2002 18:18:04   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:34:24   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:26   msg
 * Initial revision.
 *
 *    Rev 1.5   Mar 06 2002 20:23:08   mpm
 * Externalized text for layaway screens.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
// javax imports


import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.ScannerModel;
import oracle.retail.stores.foundation.manager.device.ScannerSession;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
   This bean is used for displaying the Payment Detail screen or Refund
   Detail screen based on the data from the PaymentDetailBeanModel.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
   @see oracle.retail.stores.pos;ui.beans.PaymentDetailBeanModel
 */
//---------------------------------------------------------------------
public class OfflinePaymentBean extends ValidatingBean implements DocumentListener
{
    /**
     *
     */
    private static final long serialVersionUID = -7956611342846761318L;

    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // field number constants
    public static final int LAYAWAY_NUM = 0;
    public static final int BALANCE_DUE = 1;
    public static final int PAYMENT_AMOUNT = 2;
    public static final int EXPIRATION_DATE = 3;

    // @deprecated as of release6.0 replace by CUSTOMER_NAME
    public static final int FIRST_NAME = 4;
    // @deprecated as of release6.0 replace by CUSTOMER_NAME
    public static final int LAST_NAME = 4;

    public static final int CUSTOMER_NAME = 4;
    public static final int NUM_COMPONENTS = 5;

    /** array of label text */
    protected String[] labelText =
    {
        "Layaway Number:",
        "Balance Due:", "Payment Amount:", "Expiration Date:",
        "Customer Name:"
    };

    /** array of label tags */
    protected String[] labelTags =
    {
        "LayawayNumberLabel",
        "BalanceDueLabel", "PaymentAmountLabel", "LayawayExpirationDateLabel",
        "PaymentCustomerNameLabel"
    };

    /** array of screen components */
    protected JComponent[] components = new JComponent[NUM_COMPONENTS];

    /** array of component labels */
    protected JLabel[] labels = new JLabel[labelText.length];

    /** the bean model */
    protected OfflinePaymentBeanModel beanModel = new OfflinePaymentBeanModel();

    /** zero string */
    protected static final String zero = "0.00";

    /** The logger to which log messages will be sent.    */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.OfflinePaymentBean.class);

//------------------------------------------------------------------------------
/**
 *    Default Constructor.
 */
    public OfflinePaymentBean()
    {
        super();
        initialize();
    }

//------------------------------------------------------------------------------
/**
 *    Initialize the class. Initialize the fields and labels.
 */
    protected void initialize()
    {
        setName("OfflinePaymentDetails");

        uiFactory.configureUIComponent(this, UI_PREFIX);

        for(int i=0; i<labelText.length; i++)
        {
            labels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
        }
        components[LAYAWAY_NUM]     =   uiFactory.createAlphaNumericField("Layaway Number", "1", "14", false);
        components[BALANCE_DUE]     =   uiFactory.createCurrencyField("Balance Due","false","false","false");
        components[PAYMENT_AMOUNT]  =   uiFactory.createCurrencyField("Payment Amount","false","false","false");
        components[EXPIRATION_DATE] =   uiFactory.createEYSDateField("Expiration Date");
        components[CUSTOMER_NAME]   =   uiFactory.createConstrainedField("Customer Name", "2", "60", "37");
        UIUtilities.layoutDataPanel(this, labels, components);
    }

//------------------------------------------------------------------------------
/**
 *    Configures one of the bean components. Sets the column size and editable
 *  flag to false. Turns off negative values for currency fields. Disables all
 *  fields.
 *    @param idx the index of the component in the component array
 *    @param c the component
 *  @deprecated as of release 5.5 this configuration has to be done by the factory
 */
    protected void configureComponent(int idx, JComponent c, String name)
    {
        // if it's a text field, set the columns
        if(c instanceof JTextField)
        {
            ((JTextField)c).setColumns(17);
            ((JTextField)c).setEditable(true);
        }
        // if it's a currency field, set the negative allowed flag to false
        if(c instanceof CurrencyTextField)
        {
            ((CurrencyTextField)c).setNegativeAllowed(false);
            ((CurrencyTextField)c).setEmptyAllowed(false);
            ((CurrencyTextField)c).setErrorMessage(name);
        }

        c.setEnabled(false);

        // add it to the component array
        components[idx] = c;
    }


//------------------------------------------------------------------------------
/**
 *    Returns the base bean model.
 *    @return POSBaseBeanModel
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
        ((JTextField)components[LAYAWAY_NUM]).getDocument().addDocumentListener(this);

        updateBean();
    }

//------------------------------------------------------------------------------
/**
 *    Updates the model from the screen. Sets the layaway creation fee, payment,
 *  balance due, location, and deletion fee, if applicable.
 */
    public void updateModel()
    {
        String layNum = ((JTextField)components[LAYAWAY_NUM]).getText();

        for (int i=0; i<layNum.length();i++)
        {
            char j = layNum.charAt(i);
            if(!Character.isDigit(j))
            {
                if(Character.isLowerCase(j))
                {
                    char k = Character.toUpperCase(j);
                    layNum = layNum.replace(j,k);
                }
            }
        }

        String pay = ((JTextField)components[PAYMENT_AMOUNT]).getText();
        String due = ((JTextField)components[BALANCE_DUE]).getText();
        EYSDate expDate = ((EYSDateField)components[EXPIRATION_DATE]).getEYSDate();
        String customerName = ((JTextField)components[CUSTOMER_NAME]).getText();

        beanModel.setLayawayNumber(layNum);

        beanModel.setPaymentAmount(
            DomainGateway.getBaseCurrencyInstance(pay));

        beanModel.setBalanceDue(
            DomainGateway.getBaseCurrencyInstance(due));

        beanModel.setExpirationDate(expDate);

        beanModel.setCustomerName(customerName);
    }


//------------------------------------------------------------------------------
/**
 *    Sets the model property value and updates the bean with the new values.
 *    @param model UIModelIfc the new value for the property.
 */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set OfflinePaymentBeanModel " +
                "model to null");
        }
        else
        {
            if (model instanceof OfflinePaymentBeanModel)
            {
                beanModel = (OfflinePaymentBeanModel)model;
                updateBean();
            }
        }
    }

//------------------------------------------------------------------------------
/**
 *    Updates the information displayed on the screen with the model's
 *    data.
 */
//------------------------------------------------------------------------------
    protected void updateBean()
    {

        enableAndRequire(true, new int[]{LAYAWAY_NUM ,BALANCE_DUE,
                                        PAYMENT_AMOUNT,EXPIRATION_DATE,
                                        FIRST_NAME,LAST_NAME} );

        ((JTextField)components[LAYAWAY_NUM]).setText(
            beanModel.getLayawayNumber());



        if ( beanModel.getPaymentAmount() != null )
        {
            ((JTextField)components[PAYMENT_AMOUNT]).setText(
                beanModel.getPaymentAmount().toFormattedString());
        }
        else
        if ( beanModel.isFirstRun() )
        {
            ((JTextField)components[PAYMENT_AMOUNT]).setText("");
        }


        if (beanModel.getExpirationDate() != null &&
           beanModel.getExpirationDate().after(DomainGateway.getFactory().getEYSDateInstance()))
        {
            ((JTextField)components[EXPIRATION_DATE]).setText(
                beanModel.getExpirationDate().toFormattedString());
        }
        else
        {
            ((JTextField)components[EXPIRATION_DATE]).setText("");
        }

        if ( beanModel.getBalanceDue() != null )
        {
            ((JTextField)components[BALANCE_DUE]).setText(
                beanModel.getBalanceDue().toFormattedString());
        }
        else
        if ( beanModel.isFirstRun() )
        {
            ((JTextField)components[BALANCE_DUE]).setText("");
        }
        ((JTextField)components[CUSTOMER_NAME]).setText(
            beanModel.getCustomerName());

        beanModel.setFirstRun(false);

    }

    //----------------------------------------------------------------------------
    /**
     * deactivate any settings made by this bean to external entities
     */
    //----------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        ((JTextField)components[LAYAWAY_NUM]).getDocument().removeDocumentListener(this);
    }

    //------------------------------------------------------------------------------
    /**
    *    Enables and sets the required status of a set of components.
    *    @param enable true to enable and set required, false otherwise
    *    @param indices list of component indicies
    */
    protected void enableAndRequire(boolean enable, int[] indices)
    {
        for(int i=0; i<indices.length; i++)
        {
            JComponent comp = components[indices[i]];

            if(comp instanceof ValidatingTextField)
            {
                ((ValidatingTextField)comp).setEditable(enable);
                ((ValidatingTextField)comp).setEnabled(enable);
                setFieldRequired(((ValidatingTextField)comp), enable);
            }
            else if(comp instanceof ValidatingComboBox)
            {
                comp.setEnabled(enable);
                ((ValidatingComboBox)comp).setRequired(enable);
            }
            else if (comp instanceof JLabel)
            {
                ((JLabel)comp).setEnabled(enable);
            }
        }
    }
    //---------------------------------------------------------------------
    /**
       Receive scanner data. Called by the UI Framework.
       @param data DeviceModelIfc
    **/
    //---------------------------------------------------------------------
    public void setScannerData(DeviceModelIfc data)
    {
        ScannerModel scannerModel = (ScannerModel) data;

        // Strip any leading alpha characters from the JPOS scanner input. UPC codes are always numeric
        int index = 0;
        String temp = new String(scannerModel.getScanLabelData());

        while (Character.isLetter(temp.charAt(index)))
        {
            index++;
        }

        final String numeric = temp.substring(index);

        // current behavior - set start and end transaction fields only
        ((JTextField)components[LAYAWAY_NUM]).setText(numeric);

        updateModel();
    }
    //------------------------------------------------------------------------------
    /**
       Implementation of DocumentListener interface.
       @param e a document event
    **/
    //  ------------------------------------------------------------------------------
    public void changedUpdate(DocumentEvent e)
    {

    }
    //------------------------------------------------------------------------------
    /**
       Implementation of DocumentListener interface.
       @param e a document event
    **/
    //  ------------------------------------------------------------------------------
    public void insertUpdate(DocumentEvent e)
    {
    }

    //------------------------------------------------------------------------------
    /**
       Implementation of DocumentListener interface.
       @param e a document event
    **/
    //  ------------------------------------------------------------------------------
    public void removeUpdate(DocumentEvent de)
    {
        if(((JTextField)components[LAYAWAY_NUM]).getText().equals(""))
        {
            try
            {
                DeviceTechnicianIfc dt = (DeviceTechnicianIfc)
                        Gateway.getDispatcher().getLocalTechnician(DeviceTechnicianIfc.TYPE);
                if ( dt != null)
                {
                    try
                    {
                        String sessionName = ScannerSession.TYPE;
                        ScannerSession scannerSession = (ScannerSession) dt.getDeviceSession(sessionName);
                        scannerSession.setEnabled(true);
                    }
                    catch (DeviceException e)
                    {
                        logger.error( "setScannerData: deviceException=" + e + "");
                    }
                }
            }
            catch (TechnicianNotFoundException e)
            {
                logger.error( "setScannerData: can't get deviceTechnician=" + e + "");
            }
            catch (Exception e)
            {
                logger.error( "setScannerData: can't get deviceTechnician=" + e + "");
            }
        }
    }
    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for (int i = 0; i < NUM_COMPONENTS; i++)
        {
            labels[i].setText(retrieveText(labelTags[i],
                                           labelText[i]));
        }

        ((AlphaNumericTextField)components[LAYAWAY_NUM]).setLabel(labels[LAYAWAY_NUM]);
        ((CurrencyTextField)components[PAYMENT_AMOUNT]).setLabel(labels[PAYMENT_AMOUNT]);
        ((CurrencyTextField)components[BALANCE_DUE]).setLabel(labels[BALANCE_DUE]);
        ((EYSDateField)components[EXPIRATION_DATE]).setLabel(labels[EXPIRATION_DATE]);
        ((ConstrainedTextField)components[CUSTOMER_NAME]).setLabel(labels[CUSTOMER_NAME]);
    }                                   // end updatePropertyFields()


//------------------------------------------------------------------------------
/**
 *  Returns a string representation of this object.
 *  @return String representation of object
 */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  OfflinePaymentBean (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number.
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

//------------------------------------------------------------------------------
/**
 *  Entry point for testing.
 *  @param args command line parameters
 */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new PaymentDetailBean());
    }
}
