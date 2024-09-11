/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TransTaxExemptBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *   4    I18N_P2    1.2.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *        alphanumerice fields for I18N purpose
 *   3    360Commerce 1.2         3/31/2005 4:30:37 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:26:28 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:15:19 PM  Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 16:12:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   May 30 2003 08:57:44   baa
 * update certificate field if one exists for the linked customer
 *
 *    Rev 1.3   Apr 10 2003 12:37:08   bwf
 * Remove all instanceof UtilityManagerIfc and replace with UIUtlities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.2   Feb 14 2003 13:54:56   HDyer
 * Modified to show the localized tax exempt reason code strings, and to set the reason code using the index rather than the string.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   Aug 14 2002 18:19:08   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:12   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:36:40   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:52:42   msg
 * Initial revision.
 *
 *    Rev 1.5   Mar 04 2002 14:15:44   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Mar 02 2002 11:23:50   mpm
 * Added internationalization for modifytransaction services.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   22 Feb 2002 12:52:06   jbp
 * added label to certificate field
 * Resolution for POS SCR-1317: Item Discount does not line up under item price on the receipt.  % and
 *
 *    Rev 1.2   08 Feb 2002 18:52:42   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
This bean displays a form for a CertificateNumber and a ReasonCode.
This bean uses a StringWithReasonBeanModel.
@see oracle.retail.stores.pos.ui.beans.StringWithReasonBeanModel;
@version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//---------------------------------------------------------------------
public class TransTaxExemptBean extends ValidatingBean
{


    /** certificateLabel is a JLabel it is the label for the certificate field */
    protected JLabel certificateLabel = null;

    /** reasonCodeLabel is a JLabel used as a label for the reason code */
    protected JLabel reasonCodeLabel = null;

    /** certificateField is a NumericTextField is the
     *  field which holds the cetificate number */
    protected AlphaNumericTextField certificateField = null;

    /** reasonList is a JList is a list of reasonCodes */
    protected JList reasonList = null;

    /** reasonScrollPane is a JScrollPane where the list of reasonCodes reside*/
    protected JScrollPane reasonScrollPane = null;

    /** beanModel is a StringWithReasonBeanModel
     *  it holds the data for display in this screen */
    protected StringWithReasonBeanModel beanModel =
        new StringWithReasonBeanModel();

    /** listModel is a POSListModel it contains the data for the reasonlist */
    protected POSListModel listModel = null;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public TransTaxExemptBean()
    {
        super();
        initialize();
    }
   //------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        setName("TransTaxExemptBean");

        uiFactory.configureUIComponent(this, UI_PREFIX);

        //certificateLabel
        certificateLabel = uiFactory.createLabel("Certificate Number :", null, UI_LABEL);

        //reasonCodeLabel
        reasonCodeLabel = uiFactory.createLabel("Reason Code :", null, UI_LABEL);

        //certificateField
        certificateField = uiFactory.createAlphaNumericField("CertificateField","1","15",false);


        //reasonScrollPane
        reasonScrollPane = uiFactory.createSelectionList("ReasonList", "large");
        reasonList = (JList) reasonScrollPane.getViewport().getView();

        JLabel[] labels = {certificateLabel, reasonCodeLabel};
        JComponent[] comps = {certificateField, reasonScrollPane};

        UIUtilities.layoutDataPanel(this, labels, comps);


    }

    //---------------------------------------------------------------------
    /**
        Returns the bean model. <P>
        @return model object
    **/
    //---------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //---------------------------------------------------------------------
    /**
     * The model is a StringWithReasonBeanModel.
     * @see #setModel
     * @see oracle.retail.stores.pos.ui.beans.StringWithReasonBeanModel;
     */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        // get the text and parse it
        beanModel.setValue(certificateField.getText());
        beanModel.setSelected(false);
        int reasonIndex = reasonList.getSelectedIndex();
        if (reasonIndex >= 0)
        {
            beanModel.setSelectedReasonCode(reasonIndex);
            beanModel.setSelected(true);
        }
    }
    //---------------------------------------------------------------------
    /**
     * Sets the model property (java.lang.Object) value.  The model is a StringWithReasonBeanModel.
     * @param model The new value for the property.
     * @see #updateModel
     * @see oracle.retail.stores.pos.ui.beans.StringWithReasonBeanModel;
     */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException
            ("Attempt to set TransTaxExemptBean model to null");
        }
        UIModelIfc oldValue = beanModel;
        if (model instanceof StringWithReasonBeanModel)
        {
            beanModel = (StringWithReasonBeanModel)model;
            certificateField.setText(beanModel.getValue());

            // Populate the list model with the I18N strings
            POSListModel listModel = new POSListModel(beanModel.getReasonCodes());
            reasonList.setModel(listModel);
            if(beanModel.isSelected())
            {
                // Set selected index on the list to what was selected before.
                // Note, the model knows the key, but not the displayed localized
                // value, so we set what was selected previously by using the index
                reasonList.setSelectedValue(beanModel.getSelectedReason(), true);
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates fields based on properties.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        reasonCodeLabel.setText(retrieveText("ReasonCodeLabel",
                                             reasonCodeLabel));
        certificateLabel.setText(retrieveText("TaxExemptCertificateLabel",
                                              certificateLabel));
        certificateField.setLabel(certificateLabel);
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: TransTaxExemptBean (Revision " +
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
        UIUtilities.doBeanTest(new TransTaxExemptBean());
    }
}
