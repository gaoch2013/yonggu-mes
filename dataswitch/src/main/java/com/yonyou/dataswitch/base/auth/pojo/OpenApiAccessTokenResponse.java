package com.yonyou.dataswitch.base.auth.pojo;


import com.yonyou.dataswitch.base.response.OpenApiAccessToken;
import com.yonyou.dataswitch.base.response.OpenApiResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OpenApiAccessTokenResponse extends OpenApiResponse<OpenApiAccessToken.OpenApiAccessTokenDTO> {

}