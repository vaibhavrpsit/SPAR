
package max.retail.stores.pos.services.tender.oxigenwallet.debitresponse;

import java.util.HashMap;
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
    "requestHeader",
    "responseHeader",
    "appliedAmount",
    "transactionId",
    "appliedDetails"
})
@Generated("jsonschema2pojo")
public class OxigenDebitWalletResponse {

    @JsonProperty("requestHeader")
    private RequestHeader requestHeader;
    @JsonProperty("responseHeader")
    private ResponseHeader responseHeader;
    @JsonProperty("appliedAmount")
    private Integer appliedAmount;
    @JsonProperty("transactionId")
    private String transactionId;
    @JsonProperty("appliedDetails")
    private AppliedDetails appliedDetails;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("requestHeader")
    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    @JsonProperty("requestHeader")
    public void setRequestHeader(RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
    }

    @JsonProperty("responseHeader")
    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    @JsonProperty("responseHeader")
    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    @JsonProperty("appliedAmount")
    public Integer getAppliedAmount() {
        return appliedAmount;
    }

    @JsonProperty("appliedAmount")
    public void setAppliedAmount(Integer appliedAmount) {
        this.appliedAmount = appliedAmount;
    }

    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty("transactionId")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty("appliedDetails")
    public AppliedDetails getAppliedDetails() {
        return appliedDetails;
    }

    @JsonProperty("appliedDetails")
    public void setAppliedDetails(AppliedDetails appliedDetails) {
        this.appliedDetails = appliedDetails;
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
