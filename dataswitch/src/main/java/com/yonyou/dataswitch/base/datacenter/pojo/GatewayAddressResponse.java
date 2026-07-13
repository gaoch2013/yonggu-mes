package com.yonyou.dataswitch.base.datacenter.pojo;


import com.yonyou.dataswitch.base.response.OpenApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yonyou
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GatewayAddressResponse extends OpenApiResponse<GatewayAddressResponse.GatewayAddressDTO> implements Serializable {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayAddressDTO {

        private String gatewayUrl;
        private String tokenUrl;

    }
}