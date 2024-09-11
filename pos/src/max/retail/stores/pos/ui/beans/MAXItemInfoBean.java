/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev 1.0		Dec 27, 2016		Mansi Goel		Changes for Advanced Search
 *
 ********************************************************************************/

package max.retail.stores.pos.ui.beans;
// java imports
import java.math.BigDecimal;

import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.BaseBeanAdapter;
import oracle.retail.stores.pos.ui.beans.ItemInfoBean;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;


public class MAXItemInfoBean extends BaseBeanAdapter
{
   
    private static final long serialVersionUID = 2756692196419644592L;
	public static String YES = null;
    public static String NO  = null;

    // label and field placeholder constants
    public static final int ITEM_NUMBER  = 0;
    public static final int DESCRIPTION  = 1;
    // manufacturer
    public static final int MANUFACTURER  = 2;
    public static final int DEPARTMENT   = 3;
    public static final int PRICE        = 4;
    public static final int MAXIMUM_RETAIL_PRICE=5;
    public static final int SIZE         = 6;   
    public static final int MEASURE      = 7;
    public static final int TAX          = 8;
    public static final int DISCOUNT     = 9;
    public static final int PLANOGRAMID  = 10;
    /*India Localization Changes*/
    public static final int MULTIPLE_MAXIMUM_RETAIL_PRICE=11;    
   // public static final int RETAIL_LESS_THAN_MRP=12;
	public static int MAX_FIELDS   = 12;
    
    // Display Length of Item Description in the item enquiry screen
    public static final int MAX_ITM_DESC_DISPLAY_LENGTH   = 16;

    public static final String[] labelText =
    {
        "Item Number:","Description:","Manufacturer:","Department:","Price:","MRP:",
        "Size:", "Unit Of Measure:","Taxable:","Discountable:","Planogram ID:","Multiple MRP:",
    };
    
    public static final String[] labelTags =    {
        "ItemNumberLabel","DescriptionLabel","ManufacturerLabel","DepartmentLabel","PriceLabel",
        "PriceInquiryItemSizeLabel", "UnitOfMeasureLabel","TaxableLabel","DiscountableLabel","PlanogramIDLabel","MultipleMRP","MRPLabel"
    };
    
    public static final String[] labelTextWithOutMRP =
    {
        "Item Number:","Description:","Manufacturer:","Department:","Price:",
        "Size:", "Unit Of Measure:","Taxable:","Discountable:","Planogram ID:"
    };
    
    public static final String[] labelTagsWithOutMRP =
    {
        "ItemNumberLabel","DescriptionLabel","ManufacturerLabel","DepartmentLabel","PriceLabel",
        "PriceInquiryItemSizeLabel", "UnitOfMeasureLabel","TaxableLabel","DiscountableLabel","PlanogramIDLabel"
    };
    

   
    public static final String[] labelTextNoSizePlanogram =
    {
                "Item Number:","Description:","Manufacturer:","Department:","Price:","MRP:",
                "Unit Of Measure:","Taxable:","Discountable:","Planogram ID:"
    };

    public static final String[] labelTagsNoSizePlanogram =
    {
                "ItemNumberLabel","DescriptionLabel","ManufacturerLabel","DepartmentLabel","PriceLabel",
                "UnitOfMeasureLabel","TaxableLabel","DiscountableLabel","PlanogramIDLabel","MultipleMRP","MRPLabel"
    };
    public static final String[] labelTextSizeNoPlanogram =
    {
                "Item Number:","Description:","Manufacturer:","Department:","Price:","MRP:",
                "Size:","Unit Of Measure:","Taxable:","Discountable:"
    };
    public static final String[] labelTagsSizeNoPlanogram =
    {
                "ItemNumberLabel","DescriptionLabel","ManufacturerLabel","DepartmentLabel","PriceLabel",
                "PriceInquiryItemSizeLabel","UnitOfMeasureLabel","TaxableLabel","DiscountableLabel","MultipleMRP","MRPLabel"
    };
    public static final String[] labelTextNoSizeNoPlanogram =
    {
                "Item Number:","Description:","Manufacturer:","Department:","Price:","MRP:",
                "Unit Of Measure:","Taxable:","Discountable:"
    };
    public static final String[] labelTagsNoSizeNoPlanogram =
    {
                "ItemNumberLabel","DescriptionLabel","ManufacturerLabel","DepartmentLabel","PriceLabel",
                "UnitOfMeasureLabel","TaxableLabel","DiscountableLabel","MultipleMRP","MRPLabel"
    };
    
    /** array of labels */
    protected JLabel[] labels = null;

    /** array of display fields */
    protected JLabel[] fields = null;

    /** the bean model */
    protected MAXItemInfoBeanModel beanModel = null;
    
    
   
    //------------------------------------------------------------------------
    /**
       Constructor
    */
    //------------------------------------------------------------------------
    public MAXItemInfoBean()
    {
        super();
    }
    
    
    

    //--------------------------------------------------------------------------
    /**
     * Configure the class.
     */
    public void configure()
    {
        setName("ItemInfoBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents(MAX_FIELDS, labelText);
        initLayout();
    }

    //--------------------------------------------------------------------------
    /**
     * Initialize the display components.
     */
    protected void initComponents(int maxFields, String[] label)
    {
        labels = new JLabel[maxFields];
        fields = new JLabel[maxFields];

        for(int i=0; i<maxFields; i++)
        {
            labels[i] = uiFactory.createLabel(label[i] + "label",label[i], null, UI_LABEL);
            fields[i] = uiFactory.createLabel(label[i] + "field","", null, UI_LABEL);
            if(i==MANUFACTURER || i==PLANOGRAMID||i==MAXIMUM_RETAIL_PRICE||i==MULTIPLE_MAXIMUM_RETAIL_PRICE)
            {
            	labels[i].setVisible(false);
            	fields[i].setVisible(false);
            }
           
       }
        
    }

    //--------------------------------------------------------------------------
    /**
     *    Layout the components.
     */
    public void initLayout()
    {
        UIUtilities.layoutDataPanel(this, labels, fields);
    }

    //------------------------------------------------------------------------
    /**
     *  Update the model associated with the current screen information.
     *  @return the model for the information currently in the bean
     */
    //------------------------------------------------------------------------
    public void updateModel()
    {
    }
    //------------------------------------------------------------------------
    /**
     *  Sets the information to be shown by this bean.
     *  @param model the model to be shown.  The runtime type should be
     *  ItemInfoBeanModel
     */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set ItemInfoBean " +
                                           "model to null");
        }
        if (model instanceof MAXItemInfoBeanModel)
        {
            beanModel = (MAXItemInfoBeanModel)model;
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
        setupLayout();
        fields[ITEM_NUMBER].setText(beanModel.getItemNumber());
        fields[DESCRIPTION].setText(makeSafeStringForDisplay(beanModel.getItemDescription(), MAX_ITM_DESC_DISPLAY_LENGTH));
        fields[DEPARTMENT].setText(beanModel.getItemDept());
        //I18N change: remoe ISO currency code from base currency
        fields[PRICE].setText(getCurrencyService().formatCurrency(beanModel.getPrice(),
                              getLocale()));
       /*Check if MRP Enabled*/
        if(beanModel.isMaximumRetailPriceEnabled()){
        	fields[MAXIMUM_RETAIL_PRICE].setText(getCurrencyService().formatCurrency(beanModel.getMaximumRetailPrice(),
                    getLocale()));
          
            fields[MULTIPLE_MAXIMUM_RETAIL_PRICE].setText(beanModel.isMultipleMaximumRetailPriceFlag()?YES:NO);
            
        	fields[MAXIMUM_RETAIL_PRICE].setVisible(true);        
        	fields[MULTIPLE_MAXIMUM_RETAIL_PRICE].setVisible(true);
        	
          	labels[MAXIMUM_RETAIL_PRICE].setVisible(true);       
          	labels[MULTIPLE_MAXIMUM_RETAIL_PRICE].setVisible(true);
        }
        // Added to display manufacturer
        if(beanModel.isSearchItemByManufacturer())
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
       
              
        if ( beanModel.isItemSizeRequired() )
        {
            if ( beanModel.getItemSize() != null )
            {
                fields[SIZE].setText(beanModel.getItemSize());
            }
            fields[MEASURE].setText(beanModel.getUnitOfMeasure());

            if (beanModel.isTaxable())
                fields[TAX].setText(YES);
            else
                fields[TAX].setText(NO);

            if (beanModel.isDiscountable())
                fields[DISCOUNT].setText(YES);
            else
                fields[DISCOUNT].setText(NO);
            
            if(beanModel.isUsePlanogramID())
            {
                if(beanModel.getPlanogramID()!=null)
                {
                    String[] planogram = beanModel.getPlanogramID();
                    int index = planogram.length;
                    if(planogram!=null && index > 0)
                    {
                        StringBuffer sbDispPlanogram = new StringBuffer("<html>");
                        
                        int i=0;
                        for(i=0;i<index;i++)
                        {
                            sbDispPlanogram.append(planogram[i]+ "<p>");
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
            }
                   
        }
        else
        {
            fields[MEASURE-1].setText(beanModel.getUnitOfMeasure());

            if (beanModel.isTaxable())
                fields[TAX-1].setText(YES);
            else
                fields[TAX-1].setText(NO);

            if (beanModel.isDiscountable())
                fields[DISCOUNT-1].setText(YES);
            else
                fields[DISCOUNT-1].setText(NO); 
            if(beanModel.isUsePlanogramID())
            {
                if(beanModel.getPlanogramID()!=null)
                {
                    String[] planogram = beanModel.getPlanogramID();
                    int index = planogram.length;
                    if(planogram!=null && index > 0)
                    {
                        StringBuffer sbDispPlanogram = new StringBuffer("<html>");
                        
                        int i=0;
                        for(i=0;i<index;i++)
                        {
                            sbDispPlanogram.append(planogram[i]+ "<p>");
                        }
                        sbDispPlanogram.append("</html>");
                        String dispPlanogram = sbDispPlanogram.toString();
                        
                        fields[PLANOGRAMID-1].setText(dispPlanogram);
                        fields[PLANOGRAMID-1].setVisible(true);
                        labels[PLANOGRAMID-1].setVisible(true);
                    }
                    else
                    {
                        fields[PLANOGRAMID-1].setText("");
                        fields[PLANOGRAMID-1].setVisible(true);
                        labels[PLANOGRAMID-1].setVisible(true);
                    }
                }
                else
                {
                    fields[PLANOGRAMID-1].setText("");
                    fields[PLANOGRAMID-1].setVisible(true);
                    labels[PLANOGRAMID-1].setVisible(true);
                }
            }
                     
        }
  
    }
    
    //--------------------------------------------------------------------
    /**
     * If the item description text string is too wide to fit within the available
     * space allocated in the work panel, specific number characters and "..." will be
     * displayed instead.
     * @param args Item description text string
     * @param displayLength Specified length of description string to be displayed in the screen
     * @return {@link String} Truncated description string suffixed with "..."
     */
    //--------------------------------------------------------------------
    private String makeSafeStringForDisplay(String args, int displayLength) {
    	String clipString = "...";
    	args = args.trim();
    	if(args.length()>displayLength){
    		StringBuffer buffer = new StringBuffer(args.substring(0, displayLength));
    		return buffer.append(clipString).toString();
		}
    	return args;
	}

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        if ( beanModel != null )
        {
            if ( beanModel.isItemSizeRequired() )
            {
                if(beanModel.isUsePlanogramID())
                {
                    for (int i = 0; i < labelText.length; i++)
                    {
                        labels[i].setText(retrieveText(labelTags[i],
                        		labelText[i]));
                    } 
                }
                else
                {
                    for (int i = 0; i < labelTextSizeNoPlanogram.length; i++)
                    {
                        labels[i].setText(retrieveText(labelTagsSizeNoPlanogram[i],
                                labelTextSizeNoPlanogram[i]));
                    } 
                }
                
            }
            else
            {
                if(beanModel.isUsePlanogramID())
                {
                    for (int i = 0; i < labelTextNoSizePlanogram.length; i++)
                    {
                        labels[i].setText(retrieveText(labelTagsNoSizePlanogram[i],
                                labelTextNoSizePlanogram[i]));
                    }
                }
                else
                {
                    for (int i = 0; i < labelTextNoSizeNoPlanogram.length; i++)
                    {
                        labels[i].setText(retrieveText(labelTagsNoSizeNoPlanogram[i],
                                labelTextNoSizeNoPlanogram[i]));
                    }
                }
                
            }
            
            if(!beanModel.isMaximumRetailPriceEnabled()){
            	for (int i = 0; i < labelTextWithOutMRP.length; i++)
                {
                    labels[i].setText(retrieveText(labelTagsWithOutMRP[i],
                            labelTextWithOutMRP[i]));
                }
            }
        }

        YES = retrieveText("YesItemLabel","Yes");
        NO = retrieveText("NoItemLabel","No");
    }                                   // end updatePropertyFields()


    //---------------------------------------------------------------------
    /**
     Updates the components and redo the layout. The layout will change based on
     the size and planogram parameter specified. 
     The parameter can be changed at runtime.
     **/
    //---------------------------------------------------------------------
    protected void setupLayout()
    {
        // lay out data panel
        removeAll();
        if ( beanModel == null )
        {
            initComponents(MAX_FIELDS, labelText);
        }
        else
        {
            if ( beanModel.isItemSizeRequired() )
            {
                if(beanModel.isUsePlanogramID())
                {
                    initComponents(MAX_FIELDS, labelText);
                }
                else
                {
                    initComponents(MAX_FIELDS-1, labelTextSizeNoPlanogram);
                }
                
            }
            else
            {
                if(beanModel.isUsePlanogramID())
                {
                    initComponents(MAX_FIELDS - 1, labelTextNoSizePlanogram);
                }
                else
                {
                    initComponents(MAX_FIELDS - 2, labelTextNoSizeNoPlanogram);
                }
               
            }
            
            if(!beanModel.isMaximumRetailPriceEnabled()){
            	initComponents(MAX_FIELDS - 3, labelTextWithOutMRP);
            }
        }
        initLayout();
        invalidate();
        validate();
    }
    //---------------------------------------------------------------------
    /**
     *  Returns default display string. <P>
     *  @return String representation of object
     */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: ItemInfoBean (Revision " +
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
        return(Util.parseRevisionNumber(revisionNumber));
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

        ItemInfoBeanModel model = new ItemInfoBeanModel();
        model.setItemDescription("Chess Set");
        model.setItemNumber("20020012");
        model.setPrice(new BigDecimal(49.99));

        ItemInfoBean bean = new ItemInfoBean();        
        bean.setModel(model);

        UIUtilities.doBeanTest(bean);
    }
}
