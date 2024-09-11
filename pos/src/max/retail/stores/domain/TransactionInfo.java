
package max.retail.stores.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "invoiceNo",
    "invoiceDate",
    "invoiceGrossAmount",
    "invoiceNetAmount",
    "modeOfPayment",
    "promoCode"
})
@Generated("jsonschema2pojo")
public class TransactionInfo {

    @JsonProperty("invoiceNo")
    private String invoiceNo;
    @JsonProperty("invoiceDate")
    private String invoiceDate;
    @JsonProperty("invoiceGrossAmount")
    private String invoiceGrossAmount;
    @JsonProperty("invoiceNetAmount")
    private String invoiceNetAmount;
    @JsonProperty("modeOfPayment")
    private List<String> modeOfPayment = null;
    @JsonProperty("promoCode")
    private String promoCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("invoiceNo")
    public String getInvoiceNo() {
        return invoiceNo;
    }

    @JsonProperty("invoiceNo")
    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    @JsonProperty("invoiceDate")
    public String getInvoiceDate() {
        return invoiceDate;
    }

    @JsonProperty("invoiceDate")
    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    @JsonProperty("invoiceGrossAmount")
    public String getInvoiceGrossAmount() {
        return invoiceGrossAmount;
    }

    @JsonProperty("invoiceGrossAmount")
    public void setInvoiceGrossAmount(String invoiceGrossAmount) {
        this.invoiceGrossAmount = invoiceGrossAmount;
    }

    @JsonProperty("invoiceNetAmount")
    public String getInvoiceNetAmount() {
        return invoiceNetAmount;
    }

    @JsonProperty("invoiceNetAmount")
    public void setInvoiceNetAmount(String invoiceNetAmount) {
        this.invoiceNetAmount = invoiceNetAmount;
    }

    @JsonProperty("modeOfPayment")
    public List<String> getModeOfPayment() {
        return modeOfPayment;
    }

    @JsonProperty("modeOfPayment")
    public void setModeOfPayment(List<String> modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    @JsonProperty("promoCode")
    public String getPromoCode() {
        return promoCode;
    }

    @JsonProperty("promoCode")
    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
