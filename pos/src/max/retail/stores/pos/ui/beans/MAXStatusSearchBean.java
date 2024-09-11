package max.retail.stores.pos.ui.beans;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DateDocument;
import oracle.retail.stores.pos.ui.beans.EYSDateField;
import oracle.retail.stores.pos.ui.beans.StatusComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;

public class MAXStatusSearchBean extends ValidatingBean
  implements DocumentListener
{
  protected static final int START_DATE_ROW = 0;
  protected static final int END_DATE_ROW = 1;
  protected static final int STATUS_ROW = 2;
  protected static final int MAX_FIELDS = 3;
  protected static final String VOID_STATUS = "Voided";
  protected static String[] labelText = { "Start Date ({0}):", "End Date ({0}):", "Status:" };

  protected static String[] labelTags = { "StartDateLabel", "EndDateLabel", "StatusLabel" };

  protected JLabel[] fieldLabels = new JLabel[3];
  protected EYSDateField startDateField = null;
  protected EYSDateField endDateField = null;

  protected ValidatingComboBox statusField = null;
  public static final String revisionNumber = "$Revision: 3$";

  public void activate()
  {
    updateBean();
    super.activate();
    this.startDateField.getDocument().addDocumentListener(this);
    this.startDateField.addFocusListener(this);
    this.endDateField.getDocument().addDocumentListener(this);
  }

  public void setVisible(boolean value)
  {
    super.setVisible(value);
    setRequiredFields();
    if ((value) && (!errorFound()))
    {
      setCurrentFocus(this.startDateField);
    }
  }

  public void configure()
  {
    setName("MAXStatusSearchBean");
    this.uiFactory.configureUIComponent(this, this.UI_PREFIX);

    for (int cnt = 0; cnt < 3; cnt++)
    {
      this.fieldLabels[cnt] = this.uiFactory.createLabel(labelText[cnt], null, this.UI_LABEL);
    }

    this.startDateField = this.uiFactory.createEYSDateField("startDateField");

    this.endDateField = this.uiFactory.createEYSDateField("endDateField");

    this.statusField = this.uiFactory.createValidatingComboBox("statusField");
    this.statusField.setEditable(false);

    UIUtilities.layoutDataPanel(this, this.fieldLabels, new JComponent[] { this.startDateField, this.endDateField, this.statusField });
  }

  public void updateModel()
  {
    if ((this.beanModel instanceof MAXStatusSearchBeanModel))
    {
      MAXStatusSearchBeanModel model = (MAXStatusSearchBeanModel)this.beanModel;

      if ((this.startDateField.isInputValid()) && (this.startDateField.getText() != ""))
      {
        model.setStartDate(this.startDateField.getEYSDate());
      }
      if ((this.endDateField.isInputValid()) && (this.endDateField.getText() != ""))
      {
        model.setEndDate(this.endDateField.getEYSDate());
      }

      model.setStatusIndex(this.statusField.getSelectedIndex());
    }
  }

  protected void updateBean()
  {
    if ((this.beanModel instanceof MAXStatusSearchBeanModel))
    {
      MAXStatusSearchBeanModel model = (MAXStatusSearchBeanModel)this.beanModel;
      this.startDateField.setText("");
      this.endDateField.setText("");

      DefaultComboBoxModel smodel = (DefaultComboBoxModel)this.statusField.getModel();
      smodel.removeAllElements();
      initializeOrderStatusDescriptors(model.getOrderStatusDescriptors());
    }
    setCurrentFocus(this.startDateField);
  }

  protected void initializeOrderStatusDescriptors(String[] orderStatusDesc)
  {
    for (int i = 0; i < orderStatusDesc.length; i++)
    {
      if (orderStatusDesc[i].equalsIgnoreCase("Voided"))
        continue;
      this.statusField.addItem(UIUtilities.retrieveCommonText(orderStatusDesc[i], orderStatusDesc[i]));
    }

    this.statusField.setSelectedIndex(0);
  }

  protected void updatePropertyFields()
  {
    for (int i = 0; i < labelText.length; i++)
    {
      this.fieldLabels[i].setText(retrieveText(labelTags[i], this.fieldLabels[i]));
    }

    DateDocument doc = (DateDocument)this.startDateField.getDocument();
    this.fieldLabels[0].setText(LocaleUtilities.formatComplexMessage(this.fieldLabels[0].getText(), doc.getLocalizedFormat().toUpperCase(getLocale())));

    this.fieldLabels[1].setText(LocaleUtilities.formatComplexMessage(this.fieldLabels[1].getText(), doc.getLocalizedFormat().toUpperCase(getLocale())));

    this.endDateField.setLabel(this.fieldLabels[1]);
    this.startDateField.setLabel(this.fieldLabels[0]);
  }

  public void deactivate()
  {
    super.deactivate();

    this.startDateField.getDocument().removeDocumentListener(this);
    this.startDateField.removeFocusListener(this);
    this.endDateField.getDocument().removeDocumentListener(this);
  }

  public void changedUpdate(DocumentEvent e)
  {
    setRequiredFields();
  }

  public void insertUpdate(DocumentEvent e)
  {
    setRequiredFields();
  }

  public void removeUpdate(DocumentEvent e)
  {
    setRequiredFields();
  }

  protected void setRequiredFields()
  {
    if ((!this.startDateField.getText().equals("")) || (!this.endDateField.getText().equals("")))
    {
      setFieldRequired(this.startDateField, true);
      setFieldRequired(this.endDateField, true);
    }
    else
    {
      setFieldRequired(this.startDateField, false);
      setFieldRequired(this.endDateField, false);
    }
  }

  public String toString()
  {
    String strResult = new String("Class: MAXStatusSearchBean (Revision " + getRevisionNumber() + ") @" + hashCode());

    return strResult;
  }

  public String getRevisionNumber()
  {
    return Util.parseRevisionNumber("$Revision: 3$");
  }

  public static void main(String[] args)
  {
    UIUtilities.setUpTest();

    MAXStatusSearchBeanModel beanModel = new MAXStatusSearchBeanModel();
    beanModel.setStartDate(DomainGateway.getFactory().getEYSDateInstance());
    beanModel.setEndDate(DomainGateway.getFactory().getEYSDateInstance());
    beanModel.setStatus((String)new StatusComboBox().getItemAt(0));

    MAXStatusSearchBean bean = new MAXStatusSearchBean();
    bean.configure();
    bean.setModel(beanModel);
    bean.activate();

    UIUtilities.doBeanTest(bean);
  }
}