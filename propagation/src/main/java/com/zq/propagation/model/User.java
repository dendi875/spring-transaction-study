package com.zq.propagation.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("user")
public class User {

	@TableId(type = IdType.AUTO)
	private Integer id;

	private String name;

	private Integer age;
}
