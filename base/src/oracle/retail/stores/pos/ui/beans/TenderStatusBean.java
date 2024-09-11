/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    vbongu 02/08/13 - remove setFont from initComp as already set in plaf
 *    icole  08/29/11 - Use a singleton for OnlineStatusContainer as multiple
 *                      instances resulted in erroneous status.
 *    rrkohl 05/31/11 - increase Balance due label font
 *    rrkohl 05/30/11 - solving alignment issue for POS UI Quickwin
 *    rrkohl 05/20/11 - alignment for pos ui quickwin
 *    rrkohl 05/06/11 - Formatting as per oracle formatter
 *    rrkohl 05/05/11 - POS UI quickwin

 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.OnlineStatusContainer;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.OnlineDeviceStatusListener;

/**
 * This bean is the presentation of tender totals; including total, tendered,
 * and balance due. The fields are presented in a vertical manner. This bean
 * attempts to align the colon of balance dues' label text.
 * 
 * @since 13.4
 */

public class TenderStatusBean extends BaseBeanAdapter
{

  private static final long serialVersionUID = 788869027419049337L;

  /** Maintains the status of each output device and the overall status of the DB connection */
  protected OnlineStatusContainer statusContainer = OnlineStatusContainer.getSharedInstance();

  /** Timer for displaying time and date */
  protected Timer timer = null;

  /** Number of milliseconds to wait between time/date displays */
  protected static final int WAIT_TIME = 60000;

  /** Listener for the devices hashtable */
  OnlineDeviceStatusListener onlineDeviceStatusListener = null;

  /**
   * location of the indicators to use when the current till balance is
   * negative.
   */

  protected JLabel blankSubTotalStaticLabel = null;

  protected JLabel blankSubTotalField = null;

  /** Total label */
  protected JLabel totalStaticLabel = null;

  /** Total field */
  protected JLabel totalField = null;

  /** tendered Label */
  protected JLabel tenderedStaticLabel = null;

  /** tendered field */
  protected JLabel tenderedField = null;

  /** Balance due */
  protected JLabel balanceDueStaticLabel = null;

  /** Balance due field */
  protected JLabel balanceDueField = null;

  protected JLabel blankQtyStaticLabel = null;

  protected JLabel blankQtyField = null;

  /**
   * Constructor
   */
  public TenderStatusBean()
  {
    super();
    UI_PREFIX = "TenderStatusBean";
    initialize();
  }

  /**
   * Initialize the class.
   */
  protected void initialize()
  {
    setName("TenderStatusBean");
    initComponents();
    initLayout();
  }

  /**
   * create all the fields and widgets
   */
  protected void initComponents()
  {
    String prefix = UI_PREFIX + ".field";
    blankQtyStaticLabel = uiFactory.createLabel("qtyStaticLabel", "", null, prefix);
    blankQtyField = uiFactory.createLabel("qtyField", "3", null, prefix);
    blankSubTotalStaticLabel = uiFactory.createLabel("subTotalStaticLabel", "  ", null, prefix);
    blankSubTotalField = uiFactory.createLabel("subTotalField", " ", null, prefix);
    totalStaticLabel = uiFactory.createLabel("totalStaticLabel", "Total :", null, prefix);
    totalField = uiFactory.createLabel("totalField", "5.00", null, prefix);
    tenderedStaticLabel = uiFactory.createLabel("tenderedStaticLabel", "Tendered :", null, prefix);
    tenderedField = uiFactory.createLabel("tenderedField", "2.06", null, prefix);
    balanceDueStaticLabel = uiFactory.createLabel("balanceDueStaticLabel", "Balanced Due :", null, prefix);
    balanceDueField = uiFactory.createLabel("balanceDueField", "27.05", null, prefix);  
    uiFactory.configureUIComponent(this, UI_PREFIX);
  }

  /**
   * Lays out this bean's components.
   */
  protected void initLayout()
  {

    setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints;

    // start in upper left corner moving right and layout children

    // add blank quantity panel
    JPanel qtyPanel = new JPanel(new GridLayout(2, 1, 2, 2));
    qtyPanel.setOpaque(false);
    qtyPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, getForeground()));
    qtyPanel.add(blankQtyStaticLabel);
    qtyPanel.add(blankQtyStaticLabel);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.4;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    add(qtyPanel, gridBagConstraints);

    // add blank subtotal label
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.4;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    JPanel cell_1_0 = new JPanel(new GridBagLayout());
    cell_1_0.setOpaque(false);
    cell_1_0.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getForeground()));
    cell_1_0.add(blankSubTotalStaticLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, 0,
        new Insets(0, 2, 0, 2), 0, 0));
    add(cell_1_0, gridBagConstraints);

    // add blank subtotal field
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    JPanel cell_2_0 = new JPanel(new GridBagLayout());
    cell_2_0.setOpaque(false);
    cell_2_0.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getForeground()));
    cell_2_0.add(blankSubTotalField, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, 0,
        new Insets(0, 2, 0, 2), 0, 0));
    add(cell_2_0, gridBagConstraints);

    // add Total label
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.4;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    JPanel cell_1_1 = new JPanel(new GridBagLayout());
    cell_1_1.setOpaque(false);
    cell_1_1.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getForeground()));
    cell_1_1.add(totalStaticLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, 0, new Insets(
        0, 2, 0, 2), 0, 0));
    add(cell_1_1, gridBagConstraints);

    // add Total field
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    JPanel cell_2_1 = new JPanel(new GridBagLayout());
    cell_2_1.setOpaque(false);
    cell_2_1.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getForeground()));
    cell_2_1.add(totalField, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, 0, new Insets(0, 2,
        0, 2), 0, 0));
    add(cell_2_1, gridBagConstraints);

    // add Tendered label
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.weightx = 0.4;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    JPanel cell_1_2 = new JPanel(new GridBagLayout());
    cell_1_2.setOpaque(false);
    cell_1_2.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getForeground()));
    cell_1_2.add(tenderedStaticLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, 0,
        new Insets(0, 2, 0, 2), 0, 0));
    add(cell_1_2, gridBagConstraints);

    // add Tendered field
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    JPanel cell_2_2 = new JPanel(new GridBagLayout());
    cell_2_2.setOpaque(false);
    cell_2_2.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getForeground()));
    cell_2_2.add(tenderedField, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, 0, new Insets(0,
        2, 0, 2), 0, 0));
    add(cell_2_2, gridBagConstraints);

    // the bottom row doesn't need any extra grid painting

    // add Balance Due label
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.weightx = 0.4;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.insets = new Insets(0, 2, 0, 2);
    gridBagConstraints.anchor = GridBagConstraints.EAST;
    add(balanceDueStaticLabel, gridBagConstraints);

    // add Balance due field
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.insets = new Insets(0, 2, 0, 2);
    gridBagConstraints.anchor = GridBagConstraints.EAST;
    add(balanceDueField, gridBagConstraints);
  }

  /**
   * Sets the bean fields from the model
   * 
   * @param model UIModelIfc
   */
  @Override
  public void setModel(UIModelIfc model)
  {
    if (model == null)
    {
      throw new NullPointerException("Attempt to set EmployeeMasterBeanModel to null");
    }

    if (model instanceof POSBaseBeanModel)
    {
      // POSBaseBeanModel pModel = (POSBaseBeanModel)model;

      TotalsBeanModel tModel = null;
      if (model instanceof TenderBeanModel)
      {
        TenderBeanModel tenderModel = (TenderBeanModel) model;
        tModel = tenderModel.getTotalsModel();
      }
      if (tModel != null)
      {
        totalField.setText(tModel.getGrandTotal());
        tenderedField.setText(tModel.getTendered());
        balanceDueField.setText(tModel.getBalanceDue());
      }

    }
  }

  /**
   * Sets the properties object.
   * 
   * @param props the properties object.
   */
  public void setProps(Properties props)
  {
    this.props = props;
    if (statusContainer != null)
    {
      statusContainer.setProps(props);
    }
    updatePropertyFields();
  }

  /**
   * Update property fields.
   */
  protected void updatePropertyFields()
  {
    tenderedStaticLabel.setText(retrieveText("TenderedStaticLabel", tenderedStaticLabel));
    totalStaticLabel.setText(retrieveText("TotalSLabel", totalStaticLabel));
    balanceDueStaticLabel.setText(retrieveText("BalanceDueStaticLabel", balanceDueStaticLabel));
  }

  /**
   * main entrypoint - starts the part when it is run as an application
   * 
   * @param args java.lang.String[]
   */
  public static void main(String[] args)
  {
    UIUtilities.setUpTest();
    TenderStatusBean bean = new TenderStatusBean();
    UIUtilities.doBeanTest(bean);
  }

}
