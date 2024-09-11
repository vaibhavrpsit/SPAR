/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SelectReasonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:42 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         1/25/2006 4:11:46 PM   Brett J. Larsen merge
 *        7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *   3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:25:09 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse
 *:
 *   4    .v700     1.2.1.0     11/17/2005 13:12:29    Deepanshu       CR 6131:
 *        Migrated fix from Gap. Updated method updateBean() and
 *        updatePropertyFields()
 *   3    360Commerce1.2         3/31/2005 15:29:54     Robert Pearse
 *   2    360Commerce1.1         3/10/2005 10:25:09     Robert Pearse
 *   1    360Commerce1.0         2/11/2005 12:14:08     Robert Pearse
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:12:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   May 29 2003 16:28:44   RSachdeva
 * Selecting “Help” while “Suspending” a Transaction locks up the register
 * Resolution for POS SCR-2665: Selecting “Help” while “Suspending” a Transaction locks up the register
 *
 *    Rev 1.4   Apr 10 2003 13:17:06   bwf
 * Remove instanceof UtilityManagerIfc and replaced with UIUtilities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.3   Mar 21 2003 12:28:20   HDyer
 * Removed use of SelectReasonBeanModel and instead standardize on the use of ReasonBeanModel.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.2   Oct 09 2002 14:59:36   jriggins
 * Added setSelectedReason(int) method to take an index. That way we can use the selected index to retrieve the appropriate reason code text bundle string.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Jul 31 2002 11:41:02   baa
 * remove referece to dirty model flag.
 * Resolution for POS SCR-1767: Trans. Suspend Reason Codes missing, pressing Enter crashes system
 *
 *    Rev 1.0   Apr 29 2002 14:48:24   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:36:04   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
 *    This bean displays a list box containing reason codes. It uses
 *    the ReasonBeanModel.
 *    @see oracle.retail.stores.pos.ui.beans.ReasonBeanModel
**/
//----------------------------------------------------------------------------
public class SelectReasonBean extends SelectionListBean
{
    /** revision number supplied by source-code-control system */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** text for the role label */
    public static String REASON_LABEL = "Reason Code:";

    //--------------------------------------------------------------------------
    /**
     *    Default constructor.
     */
    public SelectReasonBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     *    Initializes the bean.
     */
    public void configure()
    {
        super.configure();

        setName("SelectReasonBean");
        setLabelText(REASON_LABEL);
        beanModel = new ReasonBeanModel();
    }

    //--------------------------------------------------------------------------
    /**
     *    Updates the model property value. Called to prepare the model for
     *    return to the business logic.
     */
    public void updateModel()
    {
        if(beanModel instanceof ReasonBeanModel)
        {
            ReasonBeanModel myModel = (ReasonBeanModel)beanModel;

            myModel.setSelected(false);
            int reasonIndex = choiceList.getSelectedIndex();

            if (reasonIndex > -1)
            {
                myModel.setSelectedReasonCode(reasonIndex);
                myModel.setSelected(true);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     *    Update the bean if it's been changed
     */
    protected void updateBean()
    {
        if (beanModel instanceof ReasonBeanModel)
        {
            ReasonBeanModel myModel = (ReasonBeanModel)beanModel;

            // Populate the list model with the I18N strings
            POSListModel listModel = new POSListModel(myModel.getReasonCodes());
            choiceList.setModel(listModel);

            if (myModel.getSelectedIndex() >= 0)
            {
                choiceList.setSelectedIndex(myModel.getSelectedIndex());
            }
            // if no item selected, set to first index
            else
            {
                choiceList.setSelectedIndex(0);
            }

            choiceLabel.setText(labelText);
        }
   }

    //---------------------------------------------------------------------
    /**
        Updates fields based on properties. <P>
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        super.updatePropertyFields();
        setLabelText(retrieveText("ReasonCodeLabel",
                                  REASON_LABEL));
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: SelectReasonBean (Revision " +
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

    //-------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //-------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        SelectReasonBean bean = new SelectReasonBean();

        ReasonBeanModel model = new ReasonBeanModel();
        Vector reasonVector = new Vector();
        reasonVector.add("Reason 1");
        reasonVector.add("Reason 2");
        reasonVector.add("Reason 3");
        reasonVector.add("Reason 4");
        reasonVector.add("Reason 5");
        reasonVector.add("Reason 6");
        reasonVector.add("Reason 7");
        reasonVector.add("Reason 8");
        model.setReasonCodes(reasonVector);
        model.setSelectedReasonCode("Reason 3");
        bean.setModel(model);
        bean.updateBean();
        UIUtilities.doBeanTest(bean);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.SelectionListBean#initLayout()
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this, new JLabel[] { choiceLabel },
                new JComponent[] { scrollPane }, false);
    }
}
