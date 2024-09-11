/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GiftPriceBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/10 - Fixed issues with displaying text and drop down
 *                         fields on screen with a single lable.
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/06/10 - use default locale when displaying currency
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         7/11/2007 11:07:31 AM  Anda D. Cadar
 *         removed ISO currency code when using base currency
 *    4    360Commerce 1.3         1/22/2006 11:45:24 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:57 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.0   Aug 29 2003 16:10:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 10 2003 09:06:00   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:17:46   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:48:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:55:16   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 05 2002 19:34:28   mpm
 * Text externalization for inquiry UI artifacts.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   Feb 05 2002 16:43:46   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.1   Jan 19 2002 10:30:28   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Oct 29 2001 11:43:04   blj
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:36:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:17:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import java.math.BigDecimal;


//----------------------------------------------------------------------------
/**
 *  This class shows information for a single item.
 *     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//----------------------------------------------------------------------------
public class GiftPriceBean extends BaseBeanAdapter
{
    /**
     *   Revision Number furnished by TeamConnection. <P>
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final String DEFAULT_LABEL = "Extended Price:";

    public static final String DEFAULT_LABEL_TAG = "ExtendedPriceLabel";
     /** local copy of bean model */
    protected GiftPriceBeanModel beanModel = null;

    /** the price display field */
    protected JLabel priceField = null;

    /** label for  price field */
    protected JLabel priceLabel = null;

    /** text for price label */
    protected String priceLabelText = DEFAULT_LABEL;

    //------------------------------------------------------------------------
    /**
     *  Default Constructor.
     */
    public GiftPriceBean()
    {
        super();
    }



    //------------------------------------------------------------------------
    /**
     *  Configures the class.
     */
    public void configure()
    {
        setName("GiftPriceBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        priceLabel = uiFactory.createLabel(priceLabelText, null, UI_LABEL);
        priceLabel.setText("Extended Price:");

        priceField = uiFactory.createDisplayField("PriceField");

        UIUtilities.layoutDataPanel(this,
                                    new JLabel[] {priceLabel},
                                    new JComponent[] {priceField}, false);
    }

    //---------------------------------------------------------------------------
    /**
     *  Update property fields.
     */
    //---------------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        priceLabel.setText(retrieveText(DEFAULT_LABEL_TAG,
                                        DEFAULT_LABEL));
    }

    //------------------------------------------------------------------------
    /**
     *  Sets the information to be shown by this bean.
     *  @param model the model to be shown.  The runtime type should be
     *  GiftPriceBeanModel
     */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set GiftPriceBean " +
                                           "model to null");
        }
        if (model instanceof GiftPriceBeanModel)
        {
            beanModel = (GiftPriceBeanModel)model;
            updateBean();
        }
    }
    //---------------------------------------------------------------------
    /**
     * Update the bean if It's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {


        //I18N change - remove ISO code from base currency
        String price = getCurrencyService().formatCurrency(beanModel.getPrice(), getDefaultLocale());
        priceField.setText(price);
    }

    //------------------------------------------------------------------------
    /**
     *  main entrypoint - starts the part when it is run as an application
     *  @param args command line arguments. None are needed.
     */
    //------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        GiftPriceBeanModel model = new GiftPriceBeanModel();
        model.setPrice(new BigDecimal(49.99));

        GiftPriceBean bean  = new GiftPriceBean();
        bean.configure();
        bean.setModel(model);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
    //---------------------------------------------------------------------
    /**
     *  Returns default display string. <P>
     *  @return String representation of object
     */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: GiftPriceBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }
    //---------------------------------------------------------------------
    /**
     *  Retrieves the Team Connection revision number. <P>
     *  @return String representation of revision number
     */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
