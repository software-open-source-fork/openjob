package io.openjob.server.admin.request.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author inhere
 * @since 1.0.0
 */
@Data
@ApiModel(value = "AdminUserLoginRequest", description = "AdminUserLoginRequest")
public class AdminUserLoginRequest {

    @NotBlank
    @Size(min = 2)
    @ApiModelProperty(value = "User name")
    private String username;

    @NotBlank
    @Size(min = 6)
    @ApiModelProperty(value = "Password, md5 hashed before submit")
    private String password;

    @NotNull
    @ApiModelProperty(value = "Keep Login, if false will expired on after 30min")
    private Boolean keepLogin = false;

}

