package com.officeten.omc.service.snmp.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * snmp 操作
 * @author Jin
 * @date 2019/6/18
 */
public interface SnmpService {

    /**
     * 根据OID，获取单条消息
     * @param ip
     * @param community
     * @param oid
	 * @return
     */
	List<Map<String, String>> snmpGet(String ip, String community, String oid);

    /**
     * 根据OID列表，一次获取多条OID数据，并且以List形式返回
     * @param ip
     * @param community
     * @param oidList
	 * @return
     */
	List<Map<String, String>> snmpGetList(String ip, String community, List<String> oidList);

    /**
     * 根据OID列表，采用异步方式一次获取多条OID数据，并且以List形式返回
     * @param ip
     * @param community
     * @param oidList
	 * @return
     */
	List<Map<String, String>> snmpAsyncGetList(String ip, String community, List<String> oidList);

    /**
     * 根据targetOID，获取树形数据
     * @param ip
     * @param community
     * @param targetOid
	 * @return
     */
	List<Map<String, String>> snmpWalk(String ip, String community, String targetOid);

    /**
     * 根据targetOID，异步获取树形数据
     * @param ip
     * @param community
     * @param targetOid
	 * @return
     */
	List<Map<String, String>> snmpAsyncWalk(String ip, String community, String targetOid);

    /**
     * 根据OID和指定string来设置设备的数据
     * @param ip
     * @param community
     * @param oid
     * @param val
	 * @return
     */
    Boolean snmpSet(String ip, String community, String oid, String val) throws IOException;
}
