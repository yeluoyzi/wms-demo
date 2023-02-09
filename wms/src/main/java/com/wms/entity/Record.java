package com.wms.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wms
 * @since 2022-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Record对象", description="")
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "货品id")
    @TableField("goodsId")
    private Integer goodsid;

    @ApiModelProperty(value = "货品名字")
    @TableField("goodsName")
    private String goodsname;

    @ApiModelProperty(value = "取货人/补货人id")
    @TableField("userId")
    private Integer userid;

    @ApiModelProperty(value = "取货人/补货人名字")
    @TableField("adminName")
    private String adminname;

    @ApiModelProperty(value = "数量")
    private Integer count;

    @ApiModelProperty(value = "出/入库")
    private String type;

//    @ApiModelProperty(value = "操作时间")
//    private Date createtime;

    @ApiModelProperty(value = "备注")
    private String remark;


}
