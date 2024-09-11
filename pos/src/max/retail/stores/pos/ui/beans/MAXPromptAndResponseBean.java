/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * 1.0     Bhanu Priya Gupta         Code merging for patch 8_111Scanner_Issue
 * ===========================================================================
 */
package max.retail.stores.pos.ui.beans;

import javax.swing.JTextField;
import oracle.retail.stores.pos.ui.beans.BytesRetrievableIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseBean;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.ResponseTextListener;

public class MAXPromptAndResponseBean extends PromptAndResponseBean
  implements ClearActionListener, ResponseTextListener
{
  private static final long serialVersionUID = -1941732585364956115L;

  public void updateModel()
  {
    if (this.beanModel != null) {
      if (this.promptModel == null) {
        this.promptModel = new PromptAndResponseModel();
      }

      if ((this.activeResponseField instanceof BytesRetrievableIfc)) {
        BytesRetrievableIfc br = (BytesRetrievableIfc)this.activeResponseField;
        byte[] text = br.getTextBytes();
        String value = null;
        if (text != null) {
          value = new String(text);
        }
        if ((text != null) && (value != null) && (value.trim().length() != 0)) {
          byte[] bytes = new byte[text.length];
          System.arraycopy(text, 0, bytes, 0, text.length);
          br.clearTextBytes();
          this.promptModel.setResponseBytes(bytes);
        }
        else {
          this.promptModel.setResponseText("");
        }
      }
      else if (this.activeResponseField.getText().equalsIgnoreCase("")) {
        this.activeResponseField.setText(this.currentResponseText);
        this.promptModel.setResponseText(this.activeResponseField.getText());
      } else {
        this.promptModel.setResponseText(this.activeResponseField.getText());
      }
      this.beanModel.setPromptAndResponseModel(this.promptModel);
    }
  }
}