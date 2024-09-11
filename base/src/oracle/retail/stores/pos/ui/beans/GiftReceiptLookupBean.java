/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GiftReceiptLookupBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       03/11/09 - change text fields to alphanumerice field
 *
 * ===========================================================================
 * $Log:
 *    4    I18N_P2    1.2.1.0     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse
 *
 *   Revision 1.6  2004/05/25 18:51:37  mweis
 *   @scr  5252 Returns via Gift Receipt does not display long descriptions
 *
 *   Revision 1.5  2004/05/25 18:42:35  mweis
 *   @scr 4882 Returns' Gift Receipt 'description' field needs to be required
 *
 *   Revision 1.4  2004/03/26 05:39:05  baa
 *   @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
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
 *    Rev 1.0   Aug 29 2003 16:10:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 14 2002 18:17:46   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:48:16   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:34:44   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:55:18   msg
 * Initial revision.
 *
 *    Rev 1.5   Mar 06 2002 19:30:06   mpm
 * Added text externalization for returns screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Jan 19 2002 10:30:30   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.3   Dec 20 2001 18:32:58   blj
 * Added a comma to Gift Price screen and removed vowels from
 * price code mappings.
 * Resolution for POS SCR-237: Gift Receipt Feature
 *
 *    Rev 1.2   Dec 19 2001 09:43:20   blj
 * promoted to the wrong promotion group, checkout and checked
 * back in to put in development promotion group
 * Resolution for POS SCR-456: Gift recpt Item screen Price Code field accepts number less than 3
 *
 *    Rev 1.1   Dec 19 2001 09:29:56   blj
 * Fixed defects: 455,456,451,453
 * Resolution for POS SCR-456: Gift recpt Item screen Price Code field accepts number less than 3
 *
 *    Rev 1.0   Dec 10 2001 17:35:54   blj
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;

import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
   This class is used to display and gather item numbers and price codes. <p>
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class GiftReceiptLookupBean extends ValidatingBean
{
    /**
        revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Define constants for grid bag layout field positioning on the screen.
     * Max fields is a count of the total number of rows.
    */
    /**
      * item number row
    */
    protected static final int ITEM_NUMBER_ROW = 0;

    /**
     * price code row
   */
   protected static final int ITEM_DESC_ROW = ITEM_NUMBER_ROW + 1;
    /**
      * price code row
    */
    protected static final int PRICE_CODE_ROW = ITEM_DESC_ROW + 1;
    /**
      * maximum fields
    */
    protected static final int MAX_FIELDS     = PRICE_CODE_ROW + 1; //add one because of 0 index!
    /**
     * label text
    */
    protected static String labelText[] =
    {
        "Item Number",
        "Item Description",
        "Price Code",
    };
    /**
     * label tags
    */
    protected static String labelTags[] =
    {
        "ItemNumberLabel",
        "ItemDescriptionLabel",
        "PriceCodeLabel",
    };

    /**
      * field labels
    */
    protected JLabel[] fieldLabels = new JLabel[MAX_FIELDS];
    /**
      * GiftReceiptLookupBeanModel model
    */
    protected GiftReceiptLookupBeanModel beanModel = null;
    /**
      * item number field
    */
    protected ConstrainedTextField itemNumberField = null;
    /**
      * price code field
    */
    protected PriceCodeTextField priceCodeField   = null;

    protected ConstrainedTextField itemDescriptionField = null;
    /**
      * dirty model flag
    */
    protected boolean dirtyModel = false;

    /**
     * Maximum length that is displayed for the "description" field.  Expressed as a String.
     */
    private static final String DESC_LENGTH = "60";

    /**
     * Maximum length that is displayed for the "description" field.  Expressed as an int.
     */
    private static int DESC_LENGTH_INT = 60;
    static {
        // Convert whatever string is in DESC_LENGTH to an integer
        try {
            DESC_LENGTH_INT = Integer.parseInt(DESC_LENGTH);
        } catch (Exception ignored) {}
    }


    //----------------------------------------------------------------------------
    /**
     * Default Constructor
     */
    //----------------------------------------------------------------------------
    public GiftReceiptLookupBean()
    {
        super();
    }

    //------------------------------------------------------------------------
    /**
       Return the POSBaseBeanModel.
       @return posBaseBeanModel as POSBaseBeanModel
    */
    //------------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
       return beanModel;
    }

    //----------------------------------------------------------------------------
    /**
     * Configures the class.
     */
    //----------------------------------------------------------------------------
    public void configure()
    {
        setName("GiftReceiptLookupBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        for(int cnt = 0; cnt < MAX_FIELDS; cnt++)
        {
            fieldLabels[cnt] =
                uiFactory.createLabel(labelText[cnt] + ":", null, UI_LABEL);
        }

        itemNumberField = uiFactory.createAlphaNumericField("itemNumberField", "1", "14", false);
        itemDescriptionField = uiFactory.createConstrainedField("itemDescriptionField", "1", DESC_LENGTH, "14");


        priceCodeField = new PriceCodeTextField("", 3, 8);
        priceCodeField.setName("priceCodeField");
        uiFactory.configureUIComponent(priceCodeField, "ValidatingField");
        priceCodeField.setColumns(10);

        UIUtilities.layoutDataPanel(this,
                                    fieldLabels,
                                    new JComponent[]{itemNumberField,itemDescriptionField, priceCodeField});

    }

    //------------------------------------------------------------------------
    /**
     * Updates the model for the current settings of this bean.
     */
    //------------------------------------------------------------------------
    public void updateModel()
    {
        //beanModel.setItemNumber(itemNumberField.getText());
        beanModel.setDescription(itemDescriptionField.getText());
        beanModel.setPriceCode(priceCodeField.getText());
    }
    //------------------------------------------------------------------------
    /**
     * Sets the model to be used with the GiftReceiptLookupBean.
     * @param model the model for this bean
    */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set GiftReceiptLookupBeanModel" +
                                           " to null");
        }
        else
        {
            if (model instanceof GiftReceiptLookupBeanModel)
            {
                beanModel = (GiftReceiptLookupBeanModel) model;
                dirtyModel = true;
                updateBean();
            }
        }
    }
    //---------------------------------------------------------------------
    /**
     * Update the bean from the model
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if(dirtyModel)
        {
            // Lock in the item number field
            if(beanModel.getItemNumber() == null || beanModel.getItemNumber().equals(""))
            {
                itemNumberField.setText("");
                itemNumberField.setEnabled(true);
            }
            else
            {
                itemNumberField.setText(beanModel.getItemNumber());
                itemNumberField.setEnabled(false);
            }

            // Allow description to change
            String description = beanModel.getDescription();
            if (description == null)
            {
                itemDescriptionField.setText("");
            }
            else
            {
                if (description.length() > DESC_LENGTH_INT)
                {
                    // If the official description is too long, chop it to the maximum length allowed.
                    description = description.substring(0, DESC_LENGTH_INT);
                }
                itemDescriptionField.setText(description);
            }
            boolean bEnable = (description == null || description.trim().equals(""));
            itemDescriptionField.setEnabled(bEnable);

            // Always allow prioe code to change
            if(beanModel.getPriceCode() == null)
            {
                priceCodeField.setText("");
            }
            else
            {
                priceCodeField.setText(beanModel.getPriceCode());
            }
            priceCodeField.setEnabled(true);

            dirtyModel = false;
        } // end dirtyModel condition
    } // end method updateModel

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i],
                                                labelText[i]));
        }
        itemNumberField.setLabel(fieldLabels[ITEM_NUMBER_ROW]);
        itemDescriptionField.setLabel(fieldLabels[ITEM_DESC_ROW]);
        priceCodeField.setLabel(fieldLabels[PRICE_CODE_ROW]);
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: GiftReceiptLookupBean (Revision " +
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

    //----------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args String[]
     */
    //----------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        GiftReceiptLookupBean bean = new GiftReceiptLookupBean();
        bean.configure();

        GiftReceiptLookupBeanModel beanModel = new GiftReceiptLookupBeanModel();
        beanModel.setItemNumber("");
        beanModel.setPriceCode("");

        bean.setModel(beanModel);

        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}   ///:~ end class GiftReceiptLookupBean
