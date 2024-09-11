/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LayawayItemRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:42 mszekely Exp $
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
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    mkochumm  02/12/09 - use default locale for dates
 *    ddbaker   11/20/08 - Updates for clipping problems
 *
 * ===========================================================================
 * $Log:
 * 6    360Commerce 1.5         6/18/2007 2:46:11 PM   Anda D. Cadar   cleanup
 *      - removed commented out code
 * 5    360Commerce 1.4         6/12/2007 8:48:22 PM   Anda D. Cadar   SCR
 *      27207: Receipt changes -  proper alignment for amounts
 * 4    360Commerce 1.3         5/8/2007 11:32:27 AM   Anda D. Cadar   currency
 *       changes for I18N
 * 3    360Commerce 1.2         3/31/2005 4:28:50 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:23:02 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse
 *
 *Revision 1.4  2004/04/08 20:33:02  cdb
 *@scr 4206 Cleaned up class headers for logs and revisions.
 *
 *Revision 1.3  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:27  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Apr 09 2003 16:50:04   bwf
 * Database Internationalization CleanUp
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.4   Mar 24 2003 14:34:08   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.3   Sep 06 2002 17:25:26   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 14 2002 18:17:58   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:34:18   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:50:44   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Mar 2002 17:34:28   dfh
 * removde Dollar sign from currency format
 * Resolution for POS SCR-365:  appears on several screens, not to specification
 * Resolution for POS SCR-1445: Dollar signs are showing up on the receipt
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// javax imports
import javax.swing.JLabel;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *  This is the renderer for the Layaway Lists.  It displays
 *  Layaway items.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class LayawayItemRenderer extends AbstractListRenderer
{
    /**
     *
     */
    private static final long serialVersionUID = 3852486453921375796L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // format constants
    //@deprecated as of release 5.5
   //public static final String CURRENCY_FORMAT = "#,##0.00;(#,##0.00)";
    //@deprecated as of release 5.5
    public static final String DATE_FORMAT = "MM/dd/yyyy";

    public static int LAYAWAY = 0;
    public static int STATUS = 1;
    public static int DATEDUE = 2;
    public static int BALANCE = 3;

    public static int BLANK = 4;
    public static int ITEM = 5;

    public static int MAX_FIELDS = 6;

    /** the default weights that layout the first display line */
    public static int[] LAYAWAY_WEIGHTS = {28,24,24,24}; // {30,28,14,14,14};
    /** the default weights that layout the second display line */
    public static int[] LAYAWAY_WEIGHTS2 = {28,72};

    /** the default widths that layout the first display line */
    public static int[] LAYAWAY_WIDTHS = {1,1,1,1};
    /** the default widths that layout the second display line */
    public static int[] LAYAWAY_WIDTHS2 = {1,3};

    public static final String FIRST_ITEM_LABEL = "FirstItemLabel";



    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public LayawayItemRenderer()
    {
        super();
        setName("LayawayItemRenderer");

        // set default in case lookup fails
        firstLineWeights = LAYAWAY_WEIGHTS;
        secondLineWeights = LAYAWAY_WEIGHTS2;
        firstLineWidths = LAYAWAY_WIDTHS;
        secondLineWidths = LAYAWAY_WIDTHS2;
        // look up the label weights
        setFirstLineWeights("layawayItemRendererWeights");
        setSecondLineWeights("layawayItemRendererWeights2");
        setFirstLineWidths("layawayItemRendererWidths");
        setSecondLineWidths("layawayItemRendererWidths2");

        fieldCount = MAX_FIELDS;
        lineBreak = BALANCE;
        secondLineBreak = ITEM;

        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes this renderer's components.
     */
    protected void initOptions()
    {
        labels[LAYAWAY].setHorizontalAlignment(JLabel.LEFT);
        labels[ITEM].setHorizontalAlignment(JLabel.LEFT);
        labels[STATUS].setHorizontalAlignment(JLabel.LEFT);
        labels[BALANCE].setHorizontalAlignment(JLabel.RIGHT);
    }

//------------------------------------------------------------------------------
/**
 *  Sets the data on the labels from the given object.
 *  @param value the object that will be dispayed in the lables
 */
    public void setData(Object value)
    {
        LayawaySummaryEntryIfc ls = (LayawaySummaryEntryIfc)value;

        // get correct status

        String status = LayawayConstantsIfc.STATUS_DESCRIPTORS[ls.getStatus()];

        labels[LAYAWAY].setText(ls.getLayawayID());
        String descriptionLabel = UIUtilities.retrieveText("LayawayListSpec", "layawayText", FIRST_ITEM_LABEL);
        labels[ITEM].setText(descriptionLabel + " " + ls.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
        labels[BLANK].setText("");

        labels[STATUS].setText(UIUtilities.retrieveCommonText(status,status));
        //use default locale for date/time and currency display
        labels[DATEDUE].setText(ls.getExpirationDate().toFormattedString());
        labels[BALANCE].setText(ls.getBalanceDue().toFormattedString());
    }

    //---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     * @return TransactionSummaryIfc the prototype renderer
     */
    //---------------------------------------------------------------------
    public Object createPrototype()
    {
        // Build objects that go into a transaction summary.
        LayawaySummaryEntryIfc layaway = DomainGateway.getFactory().getLayawaySummaryEntryInstance();

        layaway.setLayawayID("050201225");
        layaway.setExpirationDate(new EYSDate(2002, 12, 12));
        LocalizedTextIfc descriptions = DomainGateway.getFactory().getLocalizedText();
        descriptions.initialize(LocaleMap.getSupportedLocales(), "Test Item");
        layaway.setLocalizedDescriptions(descriptions);
        layaway.setStatus(LayawayConstantsIfc.STATUS_COMPLETED);
        layaway.setBalanceDue(DomainGateway.getBaseCurrencyInstance("50.00"));

        return(layaway);
    }

    //---------------------------------------------------------------------
    /**
       Sets the format for printing out currency.
    */
    //---------------------------------------------------------------------
    protected void setPropertyFields()
    {

    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  LayawayItemRenderer (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
       main entrypoint - starts the part when it is run as an application
       @param args String[]
     */
    //---------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        LayawayItemRenderer renderer = new LayawayItemRenderer();
        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
