/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    rabhaw 07/30/13 - pluitem for different size should be identified by
 *                      size.
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    cgreen 03/01/12 - add system error map to user access cargo
 *    sgu    06/08/10 - fix tab
 *    sgu    06/08/10 - add item # & desc to the screen prompt. fix unknow item
 *                      screen to disable price and quantity for external item
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    aariye 03/25/09 - Checked in files for sim-ws .jar and ItemBasket Not
 *                      Found
 *    aariye 02/02/09 - Added ItemBasketCargo for ItemBasket
 *    aariye 01/28/09 - Adding elemts for Item Basket Feature
 *    vikini 01/21/09 - adding basket DTO and implementing PLUCargoIfc
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.itembasket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.common.PLUCargoIfc;

public class ItemBasketCargo implements CargoIfc, TourCamIfc, PLUCargoIfc
{

    private ArrayList<String> itemIdList = null;
    HashMap<String, PLUItemIfc> pluItems = null;
    private SaleReturnTransactionIfc transaction = null;
    private PLUItemIfc itm = null;
    private ArrayList<String> unknownItemsFound = null;
    private Stack<String> itemStack = null;
    private String pluItemID = null;
    private String storeID = null;
    private String itemSerial = null;
    private boolean isItemScanned = false;
    private int dataExceptionCode = 0;
    private BigDecimal itemQuantity = new BigDecimal(0.0);
    private String departmentID = null;
    private String deptName = null;
    private boolean enableCancel = false;
    private String geoCode = null;
    private EmployeeIfc operator = null;
    private EmployeeIfc employee = null;
    private RegisterIfc register = null;
    private StoreStatusIfc storeStatus = null;
    private TenderLimitsIfc tenderLimits = null;
    private BasketDTO basket = null;
    private String basketId = null;

    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {
    }

    public ArrayList<String> getItemIdList()
    {
        return itemIdList;
    }

    public void setItemIdList(ArrayList<String> itemIdList)
    {
        this.itemIdList = itemIdList;
    }

    public void setPLUItems(HashMap<String, PLUItemIfc> items)
    {
        this.pluItems = items;
    }

    public HashMap<String, PLUItemIfc> getPLUItems()
    {
        return pluItems;
    }

    public void setTransaction(SaleReturnTransactionIfc trn)
    {
        this.transaction = trn;
    }

    public SaleReturnTransactionIfc getTransaction()
    {
        return transaction;
    }

    public void setPLUItem(PLUItemIfc item)
    {
        this.itm = item;
    }

    public PLUItemIfc getPLUItem()
    {
        return itm;
    }

    public void setUnknownItemsFound(ArrayList<String> noItems)
    {
        this.unknownItemsFound = noItems;
    }

    public ArrayList<String> getUnknownItemsFound()
    {
        return unknownItemsFound;
    }

    public void setItemStack(Stack<String> items)
    {
        this.itemStack = items;
    }

    public Stack<String> getItemStack()
    {
        return itemStack;
    }

    public void completeItemNotFound(BusIfc bus)
    {
        // TODO Auto-generated method stub
    }

    public String getDepartmentID()
    {
        return departmentID;
    }

    public String getDepartmentName()
    {
        return deptName;
    }

    public String getGeoCode()
    {
        return geoCode;
    }

    public BigDecimal getItemQuantity()
    {
        return itemQuantity;
    }

    public String getPLUItemID()
    {
        return this.pluItemID;
    }

    public String getStoreID()
    {
        return this.storeID;
    }

    public boolean isEnableCancelItemNotFoundFromReturns()
    {
        return this.enableCancel;
    }

    public void setDepartmentID(String deptID)
    {
        this.departmentID = deptID;
    }

    public void setDepartmentName(String dept)
    {
        this.deptName = dept;
    }

    public void setEnableCancelItemNotFoundFromReturns(boolean boolCancel)
    {
        this.enableCancel = boolCancel;
    }

    public void setGeoCode(String val)
    {
        this.geoCode = val;
    }

    public void setItemQuantity(BigDecimal value)
    {
        itemQuantity = value;
    }

    public void setPLUItemID(String itemID)
    {
        this.pluItemID = itemID;
    }

    public void setStoreID(String currStoreID)
    {
        this.storeID = currStoreID;
    }

    public int getDataExceptionErrorCode()
    {
        return dataExceptionCode;
    }

    public void setDataExceptionErrorCode(int value)
    {
        this.dataExceptionCode = value;
    }

    public String getItemSerial()
    {
        return itemSerial;
    }

    public void setItemSerial(String itemSer)
    {
        this.itemSerial = itemSer;
    }

    public boolean isItemScanned()
    {
        return isItemScanned;
    }

    public void setItemScanned(boolean bool)
    {
        this.isItemScanned = bool;
    }

    public EmployeeIfc getEmployee()
    {
        return employee;
    }

    public void setEmployee(EmployeeIfc emp)
    {
        this.employee = emp;
    }

    public EmployeeIfc getOperator()
    {
        return operator;
    }

    public void setOperator(EmployeeIfc oper)
    {
        this.operator = oper;
    }

    public RegisterIfc getRegister()
    {
        return register;
    }

    public void setRegister(RegisterIfc reg)
    {
        this.register = reg;
    }

    public StoreStatusIfc getStoreStatus()
    {
        return storeStatus;
    }

    public void setStoreStatus(StoreStatusIfc stStatus)
    {
        this.storeStatus = stStatus;
    }

    public TenderLimitsIfc getTenderLimits()
    {
        return tenderLimits;
    }

    public void setTenderLimits(TenderLimitsIfc tendrLmts)
    {
        this.tenderLimits = tendrLmts;
    }

    public BasketDTO getBasket()
    {
        return basket;
    }

    public void setBasket(BasketDTO itemBasket)
    {
        this.basket = itemBasket;
    }

    public String getBasketId()
    {
        return basketId;
    }

    public void setBasketId(String basketId)
    {
        this.basketId = basketId;
    }

    /**
     * Returns the flag indicating if the plu item is from an external order
     * 
     * @return the boolean flag
     */
    public boolean isExternalOrder()
    {
        return false;
    }

    /**
     * Returns the external item price.
     * 
     * @return the CurrencyIfc value
     */
    public CurrencyIfc getItemPrice()
    {
        PLUItemIfc plu = getPLUItem();
        CurrencyIfc price = null;
        if (plu != null)
        {
            price = plu.getPrice();
        }

        return price;
    }

    /**
     * Return the item description
     * 
     * @return the String value
     */
    public String getItemDescription()
    {
        PLUItemIfc plu = getPLUItem();
        String desc = null;
        if (plu != null)
        {
            desc = plu.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
        }

        return desc;
    }
    
    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.pos.services.common.PLUCargoIfc#getPLUItemForSizePrompt
     * ()
     */
    public PLUItemIfc getPLUItemForSizePrompt()
    {
        return itm;
    }
}
