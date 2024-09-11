/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemInfoBean.java /main/39 2014/06/12 09:33:50 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzhao 09/29/14 - Read Yes or No from properties file. 
 *    yiqzhao 09/22/14 - Reindex the fields in ItemBeanConstantsIfc.
 *    yiqzhao 08/19/14 - Read text from bundles.
 *    abhina 06/11/14 - Miscellaneous Item search related cleanup
 *    cgreen 01/06/14 - added configuration for setting item image size
 *    jswan  10/02/13 - Fixed issue with a data entry bean and header bean
 *                      trying to use the same lable name.
 *    asinto 05/28/13 - fixed Item Messages text for i18n
 *    hyin   11/12/12 - re-work on item search result screen to make it
 *                      editable. So a new search can be performed.
 *    cgreen 10/17/12 - tweak implementation of search field with icon
 *    hyin   09/07/12 - check null
 *    cgreen 08/29/12 - made item description wider because the 16char was too
 *                      short in customer demos
 *    sthall 05/30/12 - Enhanced RPM Integration - Clearance Pricing
 *    ohorne 02/22/11 - ItemNumber can be ItemID or PosItemID
 *    nkgaut 10/15/10 - forward port : pos hangs with the change of some of the
 *                      item parameter values
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    cgreen 09/03/09 - XbranchMerge cgreene_bug8394467-timer from
 *                      rgbustores_13.1x_branch
 *    cgreen 09/03/09 - correct mem leak in Time object and refactored this
 *                      class to use a SwingWorker instead
 *    mchell 04/15/09 - Resize the item image
 *    vikini 04/08/09 - Merged changes after refreshed view to tip
 *    vikini 04/08/09 - refectored the code for image loading
 *    vikini 04/07/09 - added code to load image from file
 *    cgreen 04/06/09 - mark bean as invalid before updating all its widget's
 *                      text and use revalidate to let Swing EDT validate the
 *                      contents for performance reasons
 *    cgreen 03/30/09 - removed item name column from item image table
 *    vikini 03/18/09 - Changed the Item Image loading process
 *    nkgaut 12/30/08 - Fix for item images
 *    nkgaut 12/02/08 - Changes to include ILRM on Item Info Screen
 *    atirke 10/23/08 - changes for error logging
 *    atirke 10/01/08 - added image path
 *    atirke 10/01/08 - modified for item images
 *    atirke 09/29/08 - added logic to display images
 * ===========================================================================
 $Log:
 8    360Commerce 1.7         5/15/2008 5:54:52 AM   Neeraj Gautam
 Clipped description to required length
 7    360Commerce 1.6         10/11/2007 4:33:23 PM  Leona R. Slepetis
 update labels from resource bundle
 6    360Commerce 1.5         7/11/2007 11:07:31 AM  Anda D. Cadar
 removed ISO currency code when using base currency
 5    360Commerce 1.4         1/22/2006 11:45:25 AM  Ron W. Haight
 removed references to com.ibm.math.BigDecimal
 4    360Commerce 1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 Base-lining of 7.1_LA
 3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:22:26 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:11:37 PM  Robert Pearse
 $
 Revision 1.5  2004/04/22 17:36:37  lzhao
 @scr 4383: show/hide size info based on parameter setting.

 Revision 1.4  2004/04/12 15:35:34  lzhao
 @scr 3840: show size label.

 Revision 1.3  2004/03/16 17:15:17  build
 Forcing head revision

 Revision 1.2  2004/02/11 20:56:26  rhafernik
 @scr 0 Log4J conversion and code cleanup

 Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 updating to pvcs 360store-current


 *
 *    Rev 1.1   Dec 17 2003 11:21:44   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Aug 29 2003 16:10:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Mar 10 2003 09:06:00   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Oct 08 2002 09:14:10   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 14 2002 18:17:48   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   21 May 2002 17:33:32   baa
 * ils
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.1   07 May 2002 22:49:48   baa
 * ils
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.0   Mar 18 2002 11:55:32   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 05 2002 19:34:30   mpm
 * Text externalization for inquiry UI artifacts.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.UIManager;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.log4j.Logger;

/**
 * This class shows information for a single item. This bean extends
 * {@link AbstractImageAndMessageBean}.
 * No validation occurs on this class.
 * 
 * @version $Revision: /main/39 $
 */
public class ItemInfoBean extends AbstractItemImageAndMessageBean
{
    private static final long serialVersionUID = -4044972339909865770L;

    protected static final Logger logger = Logger.getLogger(ItemInfoBean.class);

    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/39 $";

    public static String YES = "Yes";
    public static String NO = "No";

    
    /**
     * Initialize the display components.
     */
    protected void initComponents(int maxFields, String[] label)
    {
        labels = new JLabel[maxFields];
        fields = new JLabel[maxFields];

        for (int i = 0; i < maxFields; i++)
        {
            labels[i] = uiFactory.createLabel(label[i] + "label", label[i], null, UI_LABEL);
            fields[i] = uiFactory.createLabel(label[i] + "field", "", null, UI_LABEL);
            if (i == MANUFACTURER || i == PLANOGRAMID)
            {
                labels[i].setVisible(false);
                fields[i].setVisible(false);
            }
        }
    }

    /**
     * Configure the class.
     */
    @Override
    public void configure()
    {
        setName("ItemInfoBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
    }
    
    /**
     * Layout the components.
     */
    public void initLayout()
    {
        UIUtilities.layoutDataPanel(this, labels, fields);
    }

    /**
     * Update the bean if It's been changed. Changes all text then calls
     * {@link #setupLayout()}.
     */
    @Override
    protected void updateBean()
    {
        // re-add everything
        setupLayout();
        // mark as not needed validation up to parent
        invalidate();

        fields[ITEM_NUMBER].setText(beanModel.getItemNumber());
        String desc = beanModel.getItemDescription();
        if (desc == null)
        {
            desc = "";
        }
        fields[DESCRIPTION].setText(makeSafeStringForDisplay(desc,
                MAX_ITM_DESC_DISPLAY_LENGTH));
        fields[DEPARTMENT].setText(beanModel.getItemDept());

        // I18N change: remoe ISO currency code from base currency
        fields[PRICE].setText(getCurrencyService().formatCurrency(beanModel.getPrice(), getDefaultLocale()));
        fields[COLOR].setText(beanModel.getColorDesc());
        fields[TYPE].setText(beanModel.getItemType());
        fields[STYLE].setText(beanModel.getItemStyle());
        
        //clearance indicator 
        if (beanModel.isOnClearance())
            fields[CLEARANCE].setText(retrieveText(YES, YES));
        else
            fields[CLEARANCE].setText(retrieveText(NO, NO));


        // ILRM CR : Display Item Level Information if present
        String itemLevelMessage = addLineBreaks(beanModel.getItemLevelMessage(), MAX_CHARS_BEFORE_LINE_BREAK);
        StringBuilder screenMessage = new StringBuilder("<html><b><body>");
        screenMessage.append(itemLevelMessage).append("</body></b></html>");

        setImageLabelContents(null, ItemListRenderer.getLoadingImage(getParent()));
        if(beanModel.isItemSizeRequired() && beanModel.isUsePlanogramID())
        {
            fields[ITEM_LEVEL_MESSAGE].setText(screenMessage.toString());
            if (Util.isEmpty(itemLevelMessage))
            {
                labels[ITEM_LEVEL_MESSAGE].setVisible(false);
                fields[ITEM_LEVEL_MESSAGE].setVisible(false);
            }
            else
            {
                labels[ITEM_LEVEL_MESSAGE].setVisible(true);
                fields[ITEM_LEVEL_MESSAGE].setVisible(true);
            }
        }
        else if(beanModel.isItemSizeRequired() || beanModel.isUsePlanogramID())
        {
            fields[ITEM_LEVEL_MESSAGE - 1].setText(screenMessage.toString());
            if (Util.isEmpty(itemLevelMessage))
            {
                labels[ITEM_LEVEL_MESSAGE - 1].setVisible(false);
            }
            else
            {
                labels[ITEM_LEVEL_MESSAGE - 1].setVisible(true);
            }
        }
        else
        {
            fields[ITEM_LEVEL_MESSAGE - 2].setText(screenMessage.toString());
            if (Util.isEmpty(itemLevelMessage))
            {
                labels[ITEM_LEVEL_MESSAGE - 2].setVisible(false);
            }
            else
            {
                labels[ITEM_LEVEL_MESSAGE - 2].setVisible(true);
            }
        }

        // Added to display manufacturer
        if (beanModel.isSearchItemByManufacturer())
        {
            fields[MANUFACTURER].setText(beanModel.getItemManufacturer());
            fields[MANUFACTURER].setVisible(true);
            labels[MANUFACTURER].setVisible(true);
        }
        else
        {
            fields[MANUFACTURER].setVisible(false);
            labels[MANUFACTURER].setVisible(false);
        }

        fields[MEASURE].setText(beanModel.getUnitOfMeasure());
        
        if (beanModel.isItemSizeRequired())
        {
            if (beanModel.getItemSize() != null)
            {
                fields[SIZE].setText(beanModel.getItemSize());
            }

            if (beanModel.isTaxable())
                fields[TAX].setText(retrieveText(YES, YES));
            else
                fields[TAX].setText(retrieveText(NO, NO));

            if (beanModel.isDiscountable())
                fields[DISCOUNT].setText(retrieveText(YES, YES));
            else
                fields[DISCOUNT].setText(retrieveText(NO, NO));

            if (beanModel.isUsePlanogramID())
            {
                if (beanModel.getPlanogramID() != null)
                {
                    String[] planogram = beanModel.getPlanogramID();
                    int index = planogram.length;
                    if (index > 0)
                    {
                        StringBuffer sbDispPlanogram = new StringBuffer("<html>");

                        int i = 0;
                        for (i = 0; i < index; i++)
                        {
                            sbDispPlanogram.append(planogram[i] + "<p>");
                        }
                        sbDispPlanogram.append("</html>");
                        String dispPlanogram = sbDispPlanogram.toString();

                        fields[PLANOGRAMID].setText(dispPlanogram);
                        fields[PLANOGRAMID].setVisible(true);
                        labels[PLANOGRAMID].setVisible(true);
                    }
                    else
                    {
                        fields[PLANOGRAMID].setText("");
                        fields[PLANOGRAMID].setVisible(true);
                        labels[PLANOGRAMID].setVisible(true);
                    }
                }
                else
                {
                    fields[PLANOGRAMID].setText("");
                    fields[PLANOGRAMID].setVisible(true);
                    labels[PLANOGRAMID].setVisible(true);
                }

                for (int i = 0; i < labelText.length; i++)
                {
                    labels[i].setText(retrieveText(labelTags[i], labelText[i]));
                }
            }
            else
            {
                for (int i = 0; i < labelTextSizeNoPlanogram.length; i++)
                {
                    labels[i].setText(retrieveText(labelTagsSizeNoPlanogram[i], labelTextSizeNoPlanogram[i]));
                }
            }

        }
        else
        {
            if (beanModel.isTaxable())
                fields[TAX - 1].setText(retrieveText(YES, YES));
            else
                fields[TAX - 1].setText(retrieveText(NO, NO));

            if (beanModel.isDiscountable())
                fields[DISCOUNT - 1].setText(retrieveText(YES, YES));
            else
                fields[DISCOUNT - 1].setText(retrieveText(NO, NO));


            if (beanModel.isUsePlanogramID())
            {
                if (beanModel.getPlanogramID() != null)
                {
                    String[] planogram = beanModel.getPlanogramID();
                    int index = planogram.length;
                    if (index > 0)
                    {
                        StringBuffer sbDispPlanogram = new StringBuffer("<html>");

                        int i = 0;
                        for (i = 0; i < index; i++)
                        {
                            sbDispPlanogram.append(planogram[i] + "<p>");
                        }
                        sbDispPlanogram.append("</html>");
                        String dispPlanogram = sbDispPlanogram.toString();

                        fields[PLANOGRAMID - 1].setText(dispPlanogram);
                        fields[PLANOGRAMID - 1].setVisible(true);
                        labels[PLANOGRAMID - 1].setVisible(true);
                    }
                    else
                    {
                        fields[PLANOGRAMID - 1].setText("");
                        fields[PLANOGRAMID - 1].setVisible(true);
                        labels[PLANOGRAMID - 1].setVisible(true);
                    }
                }
                else
                {
                    fields[PLANOGRAMID - 1].setText("");
                    fields[PLANOGRAMID - 1].setVisible(true);
                    labels[PLANOGRAMID - 1].setVisible(true);
                }

                for (int i = 0; i < labelTextNoSizePlanogram.length; i++)
                {
                    labels[i].setText(retrieveText(labelTagsNoSizePlanogram[i], labelTextNoSizePlanogram[i]));
                }
            }
            else
            {
                for (int i = 0; i < labelTextNoSizeNoPlanogram.length; i++)
                {
                    labels[i].setText(retrieveText(labelTagsNoSizeNoPlanogram[i], labelTextNoSizeNoPlanogram[i]));
                }
            }
        }

        revalidate();
        loadImage(beanModel);
    }

    /**
     * If the item description text string is too wide to fit within the
     * available space allocated in the work panel, specific number characters
     * and "..." will be displayed instead.
     *
     * @param args Item description text string
     * @param displayLength Specified length of description string to be
     *            displayed in the screen
     * @return {@link String} Truncated description string suffixed with "..."
     */
    protected String makeSafeStringForDisplay(String args, int displayLength)
    {
        String clipString = "...";
        args = args.trim();
        if (args.length() > displayLength)
        {
            StringBuffer buffer = new StringBuffer(args.substring(0, displayLength));
            return buffer.append(clipString).toString();
        }
        return args;
    }

    /**
     * Re-do the layout by removing all widgets, re-initializing them and
     * triggering the layout manager. Calls {@link #revalidate()} afterwards.
     * <p>
     * The layout will change based on the size and planogram parameter
     * specified. The parameter can be changed at runtime.
     *
     * @see #initLayout()
     * @see #initComponents(int, String[])
     */
    protected void setupLayout()
    {
        // lay out data panel
        removeAll();
        if (beanModel == null)
        {
            initComponents(MAX_FIELDS, labelText);
        }
        else
        {
            if (beanModel.isItemSizeRequired())
            {
                if (beanModel.isUsePlanogramID())
                {
                    initComponents(MAX_FIELDS, labelText);
                }
                else
                {
                    initComponents(MAX_FIELDS - 1, labelTextSizeNoPlanogram);
                }
            }
            else
            {
                if (beanModel.isUsePlanogramID())
                {
                    initComponents(MAX_FIELDS - 1, labelTextNoSizePlanogram);
                }
                else
                {
                    initComponents(MAX_FIELDS - 2, labelTextNoSizeNoPlanogram);
                }

            }
        }
        initLayout();
        revalidate();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.Component#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = new StringBuilder(Util.getSimpleClassName(getClass()));
        strResult.append("@").append(hashCode());
        return strResult.toString();
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Return the maximum width and height for displaying the item image.
     * See UI plaf properties "Dimension.itemInfoImage".
     *
     * @return
     */
    protected Dimension getImageMaximumSize()
    {
        Dimension maxSize = UIManager.getDimension("itemInfoImage");
        return (maxSize != null)? maxSize : new Dimension(IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT);
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args command line arguments. None are needed.
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        ItemInfoBeanModel model = new ItemInfoBeanModel();
        model.setItemDescription("Chess Set");
        model.setItemNumber("20020012");
        model.setPrice(new BigDecimal(49.99));

        ItemInfoBean bean = new ItemInfoBean();
        bean.setModel(model);

        UIUtilities.doBeanTest(bean);
    }
}