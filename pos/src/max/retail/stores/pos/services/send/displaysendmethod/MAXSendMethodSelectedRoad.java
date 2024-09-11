/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Nov 08, 2016		Ashish Yadav		Home Delivery Send FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.send.displaysendmethod;

// java imports
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Vector;

import org.apache.log4j.Logger;

import max.retail.stores.domain.financial.MAXShippingMethodIfc;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import max.retail.stores.pos.ui.beans.MAXShippingMethodBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.ShippingMethodConstantsIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.send.address.SendCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;


//------------------------------------------------------------------------------
/**
   Retrieves send method selected, adds to transaction totals, journals
   current send information
   $Revision: 16$
**/
//------------------------------------------------------------------------------

public class MAXSendMethodSelectedRoad extends PosLaneActionAdapter
{
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: 16$";
    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(max.retail.stores.pos.services.send.displaysendmethod.MAXSendMethodSelectedRoad.class);
    /**
       zip code extension separator text
    **/
    protected static final String ZIP_EXT_SEPARATOR_TEXT = "-";
    /**
       special instruction label text
    **/
    public static final String SPECIAL_INSTRUCTION_LABEL = "Sp. Inst.:";
    /**
       shipping charge text
     **/
    public static final String SHIPPING_CHARGE_LABEL = "Shipping Charge";
    /**
       non taxable indicator tag
    **/
    public static final String NON_TAXABLE_INDICATOR = "N";
    /**
       shipping to label count text
    **/
    public static final String SHIP_TO_LABEL = "Ship-To ";
    /**
       zero item weight value found
    **/
    public static final String ERROR_ZERO_ITEM_WEIGHT_VALUE_FOUND = " Error: Zero item weight value found";
    /**
      send label
    **/
    public static final String SEND_LABEL = " Send";
    /**
       item label
    **/
    public static final String ITEM_LABEL = "ITEM: ";
    /**
     * customer present label
     */
    public static final String CUSTOMER_PRESENT = "Customer Present: ";
    /**
     * Yes label
     */
    public static final String YES = "Yes";
    /**
     * No label
     */
    public static final String NO = "No";
    /**
       total shipping charge
    **/
    public static final String TOTAL_SHIPPING_CHARGE = "Total Shipping Charge ";

    //--------------------------------------------------------------------------
    /**
        Retrieves selected shipping method and calculate totals.
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
       SendCargo cargo = (SendCargo) bus.getCargo();
       POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

       // Initialize bean model values
       MAXShippingMethodBeanModel model = (MAXShippingMethodBeanModel) ui.getModel();
       SaleReturnTransactionIfc transaction = cargo.getTransaction();

       MAXTransactionTotalsIfc totals = (MAXTransactionTotalsIfc) transaction.getTransactionTotals();

       model.getSelectedShipMethod().setCalculatedShippingCharge(model.getShippingCharge());
       // Changes start for Rev 1.0 (Send)
       MAXShippingMethodIfc selectedShipMethod = (MAXShippingMethodIfc) model.getSelectedShipMethod();
       // Changes start for Rev 1.0 (Send)
       if(selectedShipMethod instanceof MAXShippingMethodIfc)
       {
    	   ((MAXShippingMethodIfc)selectedShipMethod).setExpectedDeliveryDate(model.getExpectedDeliveryDate());
    	   ((MAXShippingMethodIfc)selectedShipMethod).setExpectedDeliveryTime(model.getExpectedDeliveryTime());
       }
       
       if ( cargo.isItemUpdate() )
       {
           transaction.updateSendPackageInfo(cargo.getSendIndex()-1,
           		model.getSelectedShipMethod(),
           		cargo.getShipToInfo());
       }
       else
       {
          // Changes starts for Rev 1.0 (Ashish : Send (commented below line as it is adding shipping method again)
           //Add send packages info
           /*transaction.addSendPackageInfo(model.getSelectedShipMethod(),
           							cargo.getShipToInfo());*/
           //Assign Send label count on Sale Return Line Items
    	// Changes ends for Rev 1.0 (Ashish : Send (commented below line as it is adding shipping method again)
           SaleReturnLineItemIfc[] items = cargo.getLineItems();
           for (int i = 0; i < items.length; i++)
           {
               items[i].setItemSendFlag(true);
               // Changes start for rev 1.0 (Send)(commented below line as it throwing error need to check again
               //items[i].setSendLabelCount(totals.getItemSendPackagesCount());
               // Changes start for rev 1.0 (Send)
           }
       }

       transaction.updateTransactionTotals();	// Must do this to force tax recalculation
       totals.setBalanceDue(totals.getGrandTotal());

       if ( cargo.getOperator() == null )
           journalCurrentSend(bus, transaction, transaction.getSalesAssociateID());
       else
           journalCurrentSend(bus, transaction, cargo.getOperator().getLoginID());
    }


   //--------------------------------------------------------------------------
    /**
        Journal current send information
        @param bus service bus
        @param transaction sale return transaction reference
        @param loginID login id
    **/
    //--------------------------------------------------------------------------
    public void journalCurrentSend(BusIfc bus, SaleReturnTransactionIfc transaction, String loginID)
    {
       // print journal
       JournalManagerIfc journal = (JournalManagerIfc)Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

       if (journal != null)
       {
           String customerPresentInfo = CUSTOMER_PRESENT;
           if (transaction.isCustomerPhysicallyPresent())
           {
               customerPresentInfo += YES;
           }
           else
           {
               customerPresentInfo += NO;
           }
           journal.journal(loginID, transaction.getTransactionID(), customerPresentInfo);
           journal.journal(loginID, transaction.getTransactionID(), journalShippingInfo(bus, transaction));
           if (logger.isInfoEnabled()) logger.info( "Journal Send msg");
       }
       else
       {
           logger.error( "No JournalManager found");
       }
   }


   //----------------------------------------------------------------------------
   /**
       Prints ship to info for the current send <P>
       @param bus service bus
       @param transaction sale return transaction reference
       @return String shipping to info
    **/
    //----------------------------------------------------------------------------
    protected String journalShippingInfo(BusIfc bus,
                                         SaleReturnTransactionIfc transaction)
    {
        JournalFormatterManagerIfc formatterManager =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        SendCargo cargo = (SendCargo) bus.getCargo();
        ParameterManagerIfc parameterManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        SaleReturnLineItemIfc[] items = cargo.getLineItems();
        // Retrieve shipping charge parameter
        StringBuffer journalBuffer =
            new StringBuffer(formatterManager.journalShippingInfo(transaction, items, parameterManager));

        //journal sub totals for transaction level send
        if (((MAXTransactionTotalsIfc) transaction.getTransactionTotals()).isTransactionLevelSendAssigned())
        {
            journalBuffer.append(journalSubTotalsForTransactionLevelSend(transaction,
                                                                         bus));
        }
        return journalBuffer.toString();
    }

    //----------------------------------------------------------------------------
    /**
       Journals shipping items for current send <P>
       @param currentSendCount current send count
       @param bus service bus
       @return String shipping items labeled
       @deprecated as of 12.0.  This information is journaled within the JournalFromatterManagerIfc.journalShippingInfo().
    **/
    //----------------------------------------------------------------------------
    protected String journalShippingItems(int currentSendCount,
                                          BusIfc bus)
    {
        StringBuffer journalBuffer = new StringBuffer("");
        SendCargo cargo = (SendCargo) bus.getCargo();
        SaleReturnLineItemIfc[] items = cargo.getLineItems();

        // Retrieve shipping charge parameter
        String shippingCalculation = getShippingCalculationType(bus);

        for (int i = 0; i < items.length; i++)
        {
            journalBuffer.append(ITEM_LABEL) .append(items[i].getItemID().trim())
                         .append(Util.EOL)
                         .append(SEND_LABEL).append(Integer.toString(currentSendCount)).append(Util.EOL);
            if (shippingCalculation.equals(ShippingMethodConstantsIfc.WEIGHT) &&
                (items[i].getPLUItem().getItemWeight().doubleValue() <= 0.00))
            {
                journalBuffer.append(ERROR_ZERO_ITEM_WEIGHT_VALUE_FOUND).append(Util.EOL);
            }
         }
        return journalBuffer.toString();
    }



    //----------------------------------------------------------------------
    /**
       Returns as String, the shipping calculation method from
       the parameter file.
       @param bus service
       @return String calculation type
    **/
    //----------------------------------------------------------------------
    private String getShippingCalculationType(BusIfc bus)
    {
        // get paramenter manager
        ParameterManagerIfc pm  = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        String  type = new String(ShippingMethodConstantsIfc.WEIGHT);
        try
        {
            type = pm.getStringValue(ShippingMethodConstantsIfc.SHIPPING_CALCULATION);
            type.trim();
            if (logger.isInfoEnabled()) logger.info("Parameter read: " + ShippingMethodConstantsIfc.SHIPPING_CALCULATION + " = [" + type + "]");
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }
        return(type);
    }

    //--------------------------------------------------------------------------
    /**
       Formats a postal code based on the data provided in the AddressIfc parameter
       @param addr AddressIfc object that provides the data needed to format the postal code.
       @return String that represents the formatted zip code.
       @deprecated as of 12.0.  This is moved to SaleReturnTransactionJournalFormatterIfc.
    **/
    //--------------------------------------------------------------------------
    protected String formatZipCode(AddressIfc addr)
    {
        // Get the zip code extension if available
        String zipExtension = addr.getPostalCodeExtension();
        String zipExtSeparator = "";
        if (zipExtension != null && zipExtension.trim().length() > 0)
        {
            zipExtSeparator = ZIP_EXT_SEPARATOR_TEXT;
        }
        // Put it all together
        String cityStateZip =
          addr.getCity() + ", "+  addr.getState()
            +" "+ addr.getPostalCode() +" "+zipExtSeparator +" "+ zipExtension + Util.EOL + "  "+ addr.getCountry();

        return cityStateZip;

    }

    //--------------------------------------------------------------------------
    /**
        Journal shipping charge
        @param shippingChargeString shipping charge string
        @return String the shipping charge journal entry
        @deprecated As of 12.0, use JournalFormatterManagerIfc.journalShippingInfo
        instead.
    **/
    //--------------------------------------------------------------------------
    public String journalShippingCharge(String shippingChargeString)
    {
        StringBuffer journalBuffer = new StringBuffer("");
        int numSpaces = 22;
        // Journal the shipping charge
        if (shippingChargeString != null)
        {
            journalBuffer.append(Util.EOL)
                         .append(SHIPPING_CHARGE_LABEL);
            numSpaces = 22;
            journalBuffer.append(Util.SPACES.substring(SHIPPING_CHARGE_LABEL.length(), numSpaces))
                         .append(shippingChargeString);
        }

        return (journalBuffer.toString());
    }

    //--------------------------------------------------------------------------
    /**
        Journal transaction totals
        @param  bus service bus
        @param  transaction sale return transaction reference
        @param  loginID login id
        @param  model shipping method bean model
        @deprecated As of release 7.0.0, replaced by
                    {@link #journalCurrentSend(BusIfc, SaleReturnTransactionIfc, String)}
    **/
    //--------------------------------------------------------------------------
    public void journalTransactionTotals(BusIfc bus, SaleReturnTransactionIfc transaction, String loginID,
                                         ShippingMethodBeanModel model)
    {
       // print journal
       JournalManagerIfc journal;
       journal = (JournalManagerIfc)Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

       if (journal != null)
       {
             //
             //  Write an entry to the journal
             // /
                // Adding send message to journal

                StringBuffer sb = new StringBuffer("");
                Vector items = transaction.getLineItemsVector();

                // Retrieve shipping charge parameter
                String shippingCalculation = getShippingCalculationType(bus);

                for (int i = 0; i < items.size(); i++)
                {
                     if (((SaleReturnLineItemIfc)items.elementAt(i)).getItemSendFlag() &&
                         !((SaleReturnLineItemIfc)items.elementAt(i)).isReturnLineItem())

                     {
                         sb.append(Util.EOL)
                           .append("ITEM: ") .append(((SaleReturnLineItemIfc)items.elementAt(i)).getItemID().trim())
                           .append(Util.EOL)
                           .append(" Send") . append(Util.EOL);

                        if (shippingCalculation.equals(ShippingMethodConstantsIfc.WEIGHT) &&
                            ((SaleReturnLineItemIfc)items.elementAt(i)).getPLUItem().getItemWeight().doubleValue() <= 0.00)
                        {
                            sb.append(" Error: Zero item weight value found") . append(Util.EOL);
                        }
                     }
                }
                sb.append("\nSp. Inst.:" + model.getInstructions());

                sb.append(journalSubTotals(transaction));



                journal.journal(loginID, transaction.getTransactionID(), sb.toString());
                if (logger.isInfoEnabled()) logger.info( "Journal Send msg");
       }
       else
       {
                logger.error( "No JournalManager found");
       }
   }


    //--------------------------------------------------------------------------
    /**     journal tax subtotals
            @param transaction
            @return String the customer info
            @deprecated As of release 7.0.0, replaced by
                        {@link com._360commerce.pos.services.sale.validate.ValidateAmountsAisle#journalTotals
                        (com._360commerce.pos.services.sale.SaleCargoIfc, SaleReturnTransactionIfc, boolean)}
    **/
    //--------------------------------------------------------------------------
   public String journalSubTotals(SaleReturnTransactionIfc transaction)
   {
        StringBuffer sb = new StringBuffer("");

        // journaling sub-totals
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
        TransactionTotalsIfc totals = transaction.getTransactionTotals();
        String subtotalString =totals.getSubtotal().subtract(totals.getDiscountTotal()).toGroupFormattedString(locale);
        double taxRate = transaction.getTransactionTax().getDefaultRate();
        String taxTotalString = totals.getTaxTotal().toGroupFormattedString(locale);
        String totalString = totals.getGrandTotal().toGroupFormattedString(locale);


        sb.append(Util.EOL).append(Util.EOL)
            .append("Subtotal");
        int numSpaces = 29;

        sb.append(Util.SPACES.substring(subtotalString.length(), numSpaces))
            .append(subtotalString)
            .append(Util.EOL).append(Util.EOL)
            .append("Tax ") ;
        numSpaces = 34;
        sb.append(Util.SPACES.substring(taxTotalString.length(), numSpaces))
          .append(taxTotalString);

        // journal the restocking fee total
        CurrencyIfc restockingFeeTotal = totals.getRestockingFeeTotal();
        sb.append(journalRestockingFee(restockingFeeTotal));

         // Journal the shipping charge
        String shippingChargeString = ((MAXTransactionTotalsIfc) totals).getCalculatedShippingCharge().toFormattedString();
        //sb.append(journalShippingCharge(shippingChargeString))
        // .append(Util.EOL).append("  ").append(totals.getShippingMethod().getShippingType());

        return (sb.toString());
    }

    //--------------------------------------------------------------------------
    /**     journal restocking fee
            @param restockingFeeTotal restocking fee total
            @return String restocking fee journal
            @deprecated As of release 7.0.0, replaced by
                        {@link com._360commerce.pos.services.sale.validate.ValidateAmountsAisle#journalTotals
                        (com._360commerce.pos.services.sale.SaleCargoIfc, SaleReturnTransactionIfc, boolean)}
    **/
    //--------------------------------------------------------------------------
    public String journalRestockingFee(CurrencyIfc restockingFeeTotal)
    {

       StringBuffer sb = new StringBuffer("");
       int numSpaces = 18;
       if (restockingFeeTotal != null)
       {
            if (restockingFeeTotal.compareTo(DomainGateway.getBaseCurrencyInstance())!= CurrencyIfc.EQUALS)
            {
                String restockingFeeTotalString = restockingFeeTotal.multiply(new BigDecimal(-1)).toGroupFormattedString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                sb.append(Util.EOL).append(Util.EOL);

                sb.append("Total Restocking Fee")
                  .append(Util.SPACES.substring(restockingFeeTotalString.length(), numSpaces))
                  .append(restockingFeeTotalString);
            }
        }

        return (sb.toString());
    }

    //--------------------------------------------------------------------------
    /**
       Journal sub totals for transaction level send.
       Journalling for Subtotals is being done now since
       now we have the total shipping charges for transaction level send
       @param transaction sale return transaction reference
       @param bus service bus reference
       @return String journal string
    **/
    //--------------------------------------------------------------------------
    public String journalSubTotalsForTransactionLevelSend(SaleReturnTransactionIfc transaction,
                                                         BusIfc bus)
    {
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)bus.getManager(JournalFormatterManagerIfc.TYPE);
        ParameterManagerIfc parameterManager =
            (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        return formatter.journalSubTotalsForTransactionLevelSend(transaction, parameterManager);
   }

     //---------------------------------------------------------------------
    /**
     Builds a line with the left and right strings separated by spaces.
     @param left StringBuffer
     @param right StringBuffer
     @return StrinBuffer formatted line
     */
    //---------------------------------------------------------------------
    protected StringBuffer blockLine(StringBuffer left, StringBuffer right)
    {
        int lineLength = 35;
        StringBuffer s = new StringBuffer(lineLength);
        s.append(left);

        // pad with spaces
        for (int i = left.length(); i < lineLength - right.length(); i++)
        {
            s.append(" ");
        }

        s.append(right);

        return s;
    }
    //---------------------------------------------------------------------
    /**
     * Round the 5 decimal digit currency value to 2 decimal digit precision.
     * <P>
     *
     * @param input
     *            currency value to be rounded
     * @return CurrencyIfc Rounded currency value
     */
    //---------------------------------------------------------------------
    protected CurrencyIfc roundCurrency(CurrencyIfc input)
    {
        // Adjust the precision Need to do rounding in two steps, starting from
        // the 3rd decimal digit first, then round again at the 2nd decimal
        // digit.
        BigDecimal bd = new BigDecimal(input.getStringValue());
        BigDecimal bOne = new BigDecimal(1);
        bd = bd.divide(bOne, 3, BigDecimal.ROUND_HALF_UP);
        CurrencyIfc roundedCurrency = DomainGateway.getBaseCurrencyInstance(bd);

        BigDecimal bd2 = new BigDecimal(roundedCurrency.getStringValue());
        bd2 = bd2.divide(bOne, TransactionTotalsIfc.UI_PRINT_TAX_DISPLAY_SCALE, BigDecimal.ROUND_HALF_UP);

        roundedCurrency = DomainGateway.getBaseCurrencyInstance(bd2);
        return (roundedCurrency);
    }
}
