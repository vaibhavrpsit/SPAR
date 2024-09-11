/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/FinancialTotalsSummaryRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/12/10 - use default locale for display of currency
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         6/12/2007 8:48:21 PM   Anda D. Cadar   SCR
 *        27207: Receipt changes -  proper alignment for amounts
 *   3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:21:42 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.0   Aug 29 2003 16:10:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Sep 24 2002 14:10:20   baa
 * i18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:17:44   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:48:02   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:55:12   msg
 * Initial revision.
 *
 *    Rev 1.2   25 Feb 2002 17:31:30   baa
 * display end of day summary
 * Resolution for POS SCR-1413: Financial info missing from EOD Summary screen
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports


import javax.swing.JLabel;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *  This class renders the lines for the FinancialTotalsSummaryBean.
 *
 *  @see FinancialTotalsSummaryBean, FinancialTotalsSummaryEntry,
 *      FinancialTotals.
 *  @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 */
//------------------------------------------------------------------------------
public class FinancialTotalsSummaryRenderer extends AbstractListRenderer
{
    /**
     *
     */
    private static final long serialVersionUID = 6716668825827709745L;

    /** revision number supplied by source-code-control system */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static int TYPE       = 0;
    public static int ENTERED    = 1;
    public static int EXPECTED   = 2;
    public static int MAX_FIELDS = 3;

    public static int[] FINANCIAL_WEIGHTS = {50,25,25};

    //--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public FinancialTotalsSummaryRenderer()
    {
        super();
        setName("FinancialTotalsSummaryRenderer");

        // set default in case lookup fails
        firstLineWeights = FINANCIAL_WEIGHTS;
        // look up the label weights
        setFirstLineWeights("financialTotalsRendererWeights");

        fieldCount = MAX_FIELDS;
        lineBreak = EXPECTED;

        initialize();
    }


    //--------------------------------------------------------------------------
    /**
     *  Initializes this renderer's components.
     */
    protected void initOptions()
    {
        labels[TYPE].setHorizontalAlignment(JLabel.LEFT);
        labels[ENTERED].setHorizontalAlignment(JLabel.RIGHT);
        labels[EXPECTED].setHorizontalAlignment(JLabel.RIGHT);
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets data for this renderer.
     *
     *  @param the summary data object to render
     */
    public void setData(Object value)
    {
        FinancialTotalsSummaryEntryIfc entry =
            (FinancialTotalsSummaryEntryIfc)value;



        // set fields
        labels[TYPE].setText(entry.getType());
        labels[ENTERED].setText(entry.getEntered().toFormattedString());

        if (entry.getDisplayExpected())
        {
            labels[EXPECTED].setText(entry.getExpected().toFormattedString());
        }
        else
        {
            labels[EXPECTED].setText(" ");
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the ui properties.
     */
    protected void setPropertyFields()
    {
        // Get the format string spec from the UI model properties.

        if (props != null)
        {
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  creates the prototype cell to speed updates
     *  @return SaleReturnLineItem the prototype renderer
     */
    public Object createPrototype()
    {
        FinancialTotalsSummaryEntry prototype = new FinancialTotalsSummaryEntry();

        prototype.setType("XXXXXXXXXXXXXXXXXXXXXX");

        return prototype;
    }

    //--------------------------------------------------------------------------
    /**
     *  Retrieves the Team Connection revision number.
     *  @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //--------------------------------------------------------------------------
    /**
     *  Main test method.
     *
     *  @param args String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        FinancialTotalsSummaryRenderer renderer =
            new FinancialTotalsSummaryRenderer();

        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
