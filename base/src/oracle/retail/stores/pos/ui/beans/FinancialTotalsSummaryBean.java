/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/FinancialTotalsSummaryBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:05 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.0   Aug 29 2003 16:10:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:44   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:53:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:53:20   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Feb 2002 17:31:26   baa
 * display end of day summary
 * Resolution for POS SCR-1413: Financial info missing from EOD Summary screen
 *
 *    Rev 1.1   Jan 19 2002 10:30:20   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:37:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:17:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JList;
import javax.swing.JScrollPane;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;

//------------------------------------------------------------------------------
/**
    This class is the user-interface bean for displaying financial totals
    summary data to the screen.  It is, in essence, a report summarizing
    financial totals data.  Its initial implementation was designed to display
    entries for store data, but the bean could be used to display financial
    totals data at any level. <P>
    The FinancialTotalsSummaryBeanModel is to package the data in
    FinancialTotals for this bean.  FinancialTotalsSummaryRenderer
    builds the display for each line. <P>
    @see FinancialTotals, FinancialTotalsSummaryBeanModel
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated as of release 5.0.0
**/
//------------------------------------------------------------------------------
public class FinancialTotalsSummaryBean extends CycleRootPanel

{
    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        bean model
    **/
    protected FinancialTotalsSummaryBeanModel beanModel = null;
    /**
        header bean
    **/
        protected FinancialTotalsSummaryHeader totalsHeader = null;
    /**
        list component
    **/
        protected JList summaryList = null;

    //---------------------------------------------------------------------
    /**
        Constructs FinancialTotalsSummaryBean object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsSummaryBean()
    {
        super();
        beanModel = new FinancialTotalsSummaryBeanModel();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Activates any settings made by this bean to external entities. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public void activate()
    {                                   // begin activate()
        super.activate();
          summaryList.requestFocus();
    }                                   // end activate()

    //---------------------------------------------------------------------
    /**
        Configures the bean propterties read from POSModel properties. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public void configure()
    {                                   // begin configure()
    }                                   // end configure()

    //---------------------------------------------------------------------
    /**
        Deactivates any settings made by this bean to external entities. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public void deactivate()
    {                                   // begin deactivate()
        super.deactivate();
    }                                   // end deactivate()

    //---------------------------------------------------------------------
    /**
        Initialize the bean. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    protected void initialize()
    {                                   // begin initialize()
        GridBagConstraints gbc = new GridBagConstraints();
        setName("FinancialTotalsSummaryBean");
        setLayout(new GridBagLayout());

        totalsHeader = new FinancialTotalsSummaryHeader();
        totalsHeader.setName("totalsHeader");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        add(totalsHeader, gbc);

                summaryList = new JList();
        summaryList.setName("summaryList");
                summaryList.setCellRenderer(new FinancialTotalsSummaryRenderer());
        JScrollPane jsp = new JScrollPane(summaryList);
        //jsp.setBorder(new LineBorder(Color.red));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        add(jsp, gbc);
    }                                   // end initialize()

    //---------------------------------------------------------------------
    /**
        Gets the model for the current settings of this bean. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return the model for the current values of this bean
    **/
    //---------------------------------------------------------------------
    public void updateModel()
    {                                   // begin getModel()
    }                                   // end getModel()

    //---------------------------------------------------------------------
    /**
        Sets the model for the current settings of this bean. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param the model for the current values of this bean
    **/
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {                                   // begin setModel()
        // check for null pointer
        if (model == null)
        {
            throw new NullPointerException("Attempt to set FinancialTotalsSummaryBean" +
                                           " model to null");
        }

        // get data from bean model and load into list
        if (model instanceof FinancialTotalsSummaryBeanModel)
        {
            beanModel = (FinancialTotalsSummaryBeanModel) model;
            summaryList.setModel(beanModel.getListModel());
        }
    }                                   // end setModel()

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: FinancialTotalsSummaryBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        This class does not have to implement any of this methods belonging
        to TableBeanIfc. This bean used to implement TableBeanIfc.
        because it essentally a report that doesn't do any of them. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public void addItem(int index,Object item){}
    public void deleteItem(int index){}
    public void modifyItem(int index,Object item){}
    public int getSelectedRow(){return 0;}
    public void setHighlight(boolean highlighted){}
    public void inputAreaLengthChanged(int strlen){}

    //---------------------------------------------------------------------
    /**
        Main test method. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param args String[]
    **/
    //---------------------------------------------------------------------
    public static void main(String[] args)
    {                                   // begin main90

        javax.swing.JFrame frame = new

        javax.swing.JFrame("FinancialTotalsSummaryBean");

        FinancialTotalsSummaryBean bean = new FinancialTotalsSummaryBean();
        frame.setSize(560, 375);
        frame.getContentPane().add(bean);
        frame.show();

    }                                   // end main()
}
