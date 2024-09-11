/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NoSaleReasonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/10 - Fixed issues with displaying text and drop down
 *                         fields on screen with a single lable.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/29/2007 7:33:23 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         4    .v8x      1.2.1.0     3/11/2007 1:52:42 PM   Brett J. Larsen
 *         CR 4530
 *         - default reason code not being displayed (when default isn't the
 *         1st in the list)
 *    3    360Commerce 1.2         3/31/2005 4:29:09 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:42 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:45 PM  Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 16:11:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jul 19 2003 07:57:14   jgs
 * The setSelectIndex was not being called; therefore when the application returned from help screen, it did not know which entry in the list to set the focus on.
 * Resolution for 3031: Selecting Enter on No Sale Reason after returning from Help hangs POS
 *
 *    Rev 1.3   Apr 10 2003 13:16:26   bwf
 * Remove instanceof UtilityManagerIfc and replaced with UIUtilities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.2   Feb 14 2003 14:43:32   HDyer
 * Use ReasonBeanModel instead of deprecated NoSaleReasonBeanModel. Populate list with localized strings.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   27 Jun 2002 10:57:10   jbp
 * removed dirty model
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Apr 29 2002 14:56:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:22   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 09 2002 12:25:34   mpm
 * More text externalization.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *    This displays a reason list for the No Sale function.
 *    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class NoSaleReasonBean extends SelectionListBean
{
    /** text for the role label */
    public static String LABEL_TEXT = "Reason Code:";

    //--------------------------------------------------------------------------
    /**
     *     Default constructor.
     */
    public NoSaleReasonBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     *    Configures the bean.
     */
    public void configure()
    {
        super.configure();

        setName("NoSaleReasonBean");
        setLabelText(LABEL_TEXT);
        setLabelTags(SelectionListBean.REASON_CODE_LABEL);
        beanModel = new ReasonBeanModel();
    }

    /**
     * Creates the layout and lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this, new JLabel[] { choiceLabel },
                new JComponent[] { scrollPane }, false);
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

            // get the the selected index and update the model with it
            myModel.setSelected(false);
            int selectedIndex = choiceList.getSelectedIndex();
            if (selectedIndex >= 0)
            {
                myModel.setSelectedReasonCode(selectedIndex);
                myModel.setSelected(true);
            }
        }
    }

    //---------------------------------------------------------------------
    /**
     * Sets the model property (java.lang.Object) value.
     * @param model The new value for the property.
     * @see #getModel
     * @see oracle.retail.stores.pos.ui.beans.ReasonBeanModel
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if(beanModel instanceof ReasonBeanModel)
        {
            ReasonBeanModel myModel = (ReasonBeanModel)beanModel;

            // Populate the list model with the localized strings from the reason code keys
            POSListModel listModel = new POSListModel(myModel.getReasonCodes());
            choiceList.setModel(listModel);

            // If something is selected, update the UI list
            if (myModel.isSelected() && myModel.getSelectedIndex() >= 0)
            {
                // Set selected index on the list to what was selected before.
                // Note, the model knows the key, but not the displayed localized
                // value, so we set what was selected previously by using the index
                choiceList.setSelectedIndex(myModel.getSelectedIndex());
                // and then make sure that part of the scrollpane is visible
                choiceList.ensureIndexIsVisible(myModel.getSelectedIndex());
            }
            else
            {
                choiceList.setSelectedIndex(myModel.getDefaultIndex());
                choiceList.ensureIndexIsVisible(myModel.getDefaultIndex());
            }
        }
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

        NoSaleReasonBean bean = new NoSaleReasonBean();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
