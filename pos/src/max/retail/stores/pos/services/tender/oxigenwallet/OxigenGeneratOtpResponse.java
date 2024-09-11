
package max.retail.stores.pos.services.tender.oxigenwallet;

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
    "otpDetails"
})
@Generated("jsonschema")
public class OxigenGeneratOtpResponse {

    @JsonProperty("requestHeader")
    private RequestHeader requestHeader;
    @JsonProperty("responseHeader")
    private ResponseHeader responseHeader;
    @JsonProperty("otpDetails")
    private OtpDetails otpDetails;
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

    @JsonProperty("otpDetails")
    public OtpDetails getOtpDetails() {
        return otpDetails;
    }

    @JsonProperty("otpDetails")
    public void setOtpDetails(OtpDetails otpDetails) {
        this.otpDetails = otpDetails;
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
