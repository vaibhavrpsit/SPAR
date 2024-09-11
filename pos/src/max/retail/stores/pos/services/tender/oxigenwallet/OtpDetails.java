
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
    "otpType",
    "refNo",
    "otp"
})
@Generated("jsonschema")
public class OtpDetails {

    @JsonProperty("otpType")
    private String otpType;
    @JsonProperty("refNo")
    private String refNo;
    @JsonProperty("otp")
    private String otp;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("otpType")
    public String getOtpType() {
        return otpType;
    }

    @JsonProperty("otpType")
    public void setOtpType(String otpType) {
        this.otpType = otpType;
    }

    @JsonProperty("refNo")
    public String getRefNo() {
        return refNo;
    }

    @JsonProperty("refNo")
    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @JsonProperty("otp")
    public String getOtp() {
        return otp;
    }

    @JsonProperty("otp")
    public void setOtp(String otp) {
        this.otp = otp;
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
