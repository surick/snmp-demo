package com.officeten.omc.model.snmp.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jin
 * @date 2019/6/19
 */
@Getter
@Setter
public class SnmpQueryVO {
	private String ip;
	private String community;
	private String oid;
	private String oids;
	private String value;
}
