package max.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.math.BigDecimal;
import javax.swing.JLabel;

import max.retail.stores.domain.stock.MAXPLUItemIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.AbstractListRenderer;
import oracle.retail.stores.pos.utility.PLUItemUtility;

public class MAXLineItemRendererWithMRP extends AbstractListRenderer
{
  public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/2 $";
  public static int[] LINE_ITEM_WEIGHTS_MRP = { 35, 10, 20, 20, 12, 20, 8 };
  protected String quantityFormat;
  public static final String CURRENCYIFC_DISPLAYFORMAT = "CurrencyIfc.DisplayFormat";
  public static final String SALELINEITEM_RENDERER_QUANTITYFORMAT = "SaleLineItemRenderer.QuantityFormat";
  public static final String RENDERER_GIFT_CARD_LABEL = "Renderer.GiftCardLabel";
  public static final String RENDERER_SALE_ASSOC_LABEL = "Renderer.SalesAssociateLabel";
  public static final String RENDERER_GIFT_REGISTRY_LABEL = "Renderer.GiftRegistryLabel";
  public static final String RENDERER_SERIAL_LABEL = "Renderer.SerialLabel";
  public static final String RENDERER_SEND_LABEL = "Renderer.SendLabel";
  public static final String RENDERER_GIFT_RECEIPT_LABEL = "Renderer.GiftReceiptLabel";
  public static final String RENDERER_KIT_LABEL = "Renderer.KitLabel";
  protected String giftCardLabel = "Gift Card ID:";

  protected String giftRegLabel = "GiftReg.#";

  protected String salesAssocLabel = "Sales Assoc:";

  protected String serialLabel = "Serial #";

  protected String sendLabel = "Send";

  protected String giftReceiptLabel = "Gift Receipt";

  protected String kitLabel = "Kit";

  protected String taxModeTag = "TaxModeChar.";

  protected static final String OVERRIDE_MARKER = DomainUtil.retrieveOverrideMarker("panel");

  protected static final String DEAL_ITEM_MARKER = PLUItemUtility.retrieveDealItemMarker();

  public static int DESCRIPTION = 0;

  public static int QUANTITY = 1;

  public static int MAXIMUM_RETAIL_PRICE = 2;

  public static int PRICE = 3;

  public static int DISCOUNT = 4;

  public static int EXT_PRICE = 5;

  public static int TAX = 6;

  public static int STOCK_NUM = 7;

  public static int SALE_INFO = 8;

  public static int MAX_FIELDS = 9;

  public MAXLineItemRendererWithMRP()
  {
    setName("LineItemRendererWithMRP");

    this.firstLineWeights = LINE_ITEM_WEIGHTS_MRP;
    this.fieldCount = MAX_FIELDS;
    this.lineBreak = TAX;
    initialize();
  }

  protected void initOptions()
  {
    this.labels[DESCRIPTION].setHorizontalAlignment(2);
    this.labels[STOCK_NUM].setHorizontalAlignment(2);
    this.labels[PRICE].setHorizontalAlignment(0);
    this.labels[DISCOUNT].setHorizontalAlignment(4);
    this.labels[EXT_PRICE].setHorizontalAlignment(0);
    this.labels[MAXIMUM_RETAIL_PRICE].setHorizontalAlignment(0);

    GridBagConstraints constraints = this.uiFactory.getConstraints("Renderer");

    constraints.gridy = 1;
    constraints.weightx = 0.0D;
    add(this.labels[STOCK_NUM], constraints);

    constraints.gridx = 1;
    constraints.gridwidth = 0;
    add(this.labels[SALE_INFO], constraints);
  }

  public void setData(Object value)
  {
    SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)value;

    String dealItemMarker = "";
    if (lineItem.getItemPrice().getBestDealDiscount() != null)
    {
      dealItemMarker = DEAL_ITEM_MARKER;
    }

    String overrideMarker = "";
    if (lineItem.getItemPrice().isPriceOverride())
    {
      overrideMarker = OVERRIDE_MARKER;
    }

    this.labels[DESCRIPTION].setText(lineItem.getItemDescription(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    this.labels[PRICE].setText(lineItem.getSellingPrice().toGroupFormattedString(getLocale()) + dealItemMarker + overrideMarker);
    this.labels[DISCOUNT].setText(getDiscountTotal(lineItem));
    this.labels[EXT_PRICE].setText(lineItem.getExtendedDiscountedSellingPrice().toGroupFormattedString(getLocale()));
    this.labels[MAXIMUM_RETAIL_PRICE].setText(((MAXPLUItemIfc) lineItem.getPLUItem()).getMaximumRetailPrice().toGroupFormattedString(getLocale()));

    this.labels[STOCK_NUM].setText(lineItem.isKitHeader() ? lineItem.getItemID() + " " + this.kitLabel + " " : lineItem.getItemID());

    setOptionalData(value);
  }

  public void setOptionalData(Object value)
  {
    SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)value;

    String taxStatus = lineItem.getTaxStatusDescriptor();

    if (lineItem.isUnitOfMeasureItem())
    {
      this.labels[QUANTITY].setText(LocaleUtilities.formatDecimal(lineItem.getItemQuantityDecimal(), getLocale()));
    }
    else
    {
      this.labels[QUANTITY].setText(LocaleUtilities.formatDecimalForWholeNumber(lineItem.getItemQuantityDecimal(), getLocale()));
    }

    this.labels[TAX].setText(UIUtilities.retrieveCommonText(this.taxModeTag + taxStatus, taxStatus));
    this.labels[SALE_INFO].setText(getSaleInfoText(lineItem));
  }

  public String getRevisionNumber()
  {
    return Util.parseRevisionNumber("$Revision: /rgbustores_12.0.9in_branch/2 $");
  }

  protected String getDiscountTotal(SaleReturnLineItemIfc lineItem)
  {
    String result = "";

    CurrencyIfc discountTotal = null;
    if (lineItem.isKitHeader())
    {
      discountTotal = ((KitHeaderLineItemIfc)lineItem).getKitDiscountTotal();
    }
    else
    {
      discountTotal = lineItem.getItemDiscountTotal();
    }

    if (discountTotal.signum() != 0)
    {
      result = discountTotal.toGroupFormattedString(getLocale());
    }
    return result;
  }

  protected String getGiftRegistryText(SaleReturnLineItemIfc item)
  {
    String theID = new String("");

    if (item.getRegistry() != null)
    {
      theID = item.getRegistry().getID().toString();

      if (!theID.equals(""))
      {
        theID = this.giftRegLabel + theID;
      }
    }
    return theID;
  }

  protected String getSaleInfoText(SaleReturnLineItemIfc item)
  {
    StringBuffer buf = new StringBuffer();

    if ((item.getPLUItem() instanceof GiftCardPLUItemIfc))
    {
      GiftCardIfc giftCard = ((GiftCardPLUItemIfc)item.getPLUItem()).getGiftCard();
      if ((giftCard != null) && (giftCard.getCardNumber() != null) && (!giftCard.getCardNumber().equals("")))
      {
        buf.append(this.giftCardLabel + giftCard.getCardNumber());
      }
    }

    return buf.toString();
  }

  public Object createPrototype()
  {
    SaleReturnLineItemIfc cell = DomainGateway.getFactory().getSaleReturnLineItemInstance();

    PLUItemIfc plu = DomainGateway.getFactory().getPLUItemInstance();
    plu.setDescription(LocaleMap.getLocale(LocaleMap.DEFAULT), "XXXXXXXXXXXXXXX");
    plu.setItemID("12345678901234");
    cell.setPLUItem(plu);

    CurrencyIfc testPrice = DomainGateway.getBaseCurrencyInstance("888888.88");

    ItemPriceIfc price = DomainGateway.getFactory().getItemPriceInstance();
    price.setSellingPrice(testPrice);
    price.setItemQuantity(new BigDecimal("888.88"));
    price.setItemDiscountTotal(testPrice);
    price.setExtendedSellingPrice(testPrice);
    cell.setItemPrice(price);

    EmployeeIfc emp = DomainGateway.getFactory().getEmployeeInstance();
    cell.setSalesAssociate(emp);

    return cell;
  }

  protected void setPropertyFields()
  {
    if (this.props != null)
    {
      this.quantityFormat = DomainGateway.getDecimalFormat(getLocale()).toString();
    }

    this.giftCardLabel = (UIUtilities.retrieveCommonText("Renderer.GiftCardLabel") + " ");

    this.salesAssocLabel = UIUtilities.retrieveCommonText("Renderer.SalesAssociateLabel");

    this.giftRegLabel = UIUtilities.retrieveCommonText("Renderer.GiftRegistryLabel");

    this.serialLabel = UIUtilities.retrieveCommonText("Renderer.SerialLabel");

    this.sendLabel = UIUtilities.retrieveCommonText("Renderer.SendLabel");

    this.giftReceiptLabel = UIUtilities.retrieveCommonText("Renderer.GiftReceiptLabel");

    this.kitLabel = UIUtilities.retrieveCommonText("Renderer.KitLabel");
  }
}