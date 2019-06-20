package com.officeten.omc.common.constant;


import org.snmp4j.mp.SnmpConstants;

/**
 * @author Jin
 * @date 2019/6/18
 */
public interface SnmpConstant {

    int DEFAULT_VERSION = SnmpConstants.version2c;

    String DEFAULT_PROTOCOL = "udp";

    int DEFAULT_PORT = 161;

    long DEFAULT_TIMEOUT = 3 * 1000L;

    int DEFAULT_RETRY = 3;
}
