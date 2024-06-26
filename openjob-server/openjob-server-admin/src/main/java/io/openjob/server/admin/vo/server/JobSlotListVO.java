package io.openjob.server.admin.vo.server;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author riki
 * @since 1.0.0
 */
@Data
@ApiModel(value = "ServerListVO", description = "server list VO")
public class JobSlotListVO {
    @ApiModelProperty(value = "pk")
    private Long id;

    @ApiModelProperty(value = "Server id")
    private Long serverId;

    @ApiModelProperty(value = "Server address")
    private String akkaAddress;

    @ApiModelProperty(value = "Server status")
    private Integer serverStatus;

    @ApiModelProperty(value = "Server create time")
    private Long createTime;

    @ApiModelProperty(value = "Server update time")
    private Long updateTime;
}
