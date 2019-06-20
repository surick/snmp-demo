package com.officeten.omc.service.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

import static com.officeten.omc.common.constant.SnmpConstant.*;

/**
 * 模拟Trap发送消息
 * @author Jin
 * @date 2019/6/18
 */
public class SnmpTrapSend {

    private Snmp snmp = null;
    private CommunityTarget target = null;

    public void init() throws IOException {
        System.out.println("---- 初始 Trap 的IP和端口 ----");
        target = createTarget4Trap("udp:127.0.0.1/162");
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    /**
     * 向接收器发送Trap 信息
     *
     * @throws IOException
     */
    public void sendPDU() throws IOException {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(
                new OID(".1.3.6.1.2.1.1.1.0"),
                new OctetString("SNMP Trap Test.")));
        pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(
                new UnsignedInteger32(System.currentTimeMillis() / 1000)
                        .getValue())));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(
                ".1.3.6.1.6.3.1.1.4.3")));

        // 向Agent发送PDU
        pdu.setType(PDU.TRAP);
        snmp.send(pdu, target);
        System.out.println("---- Trap Send END ----");
    }

    /**
     * 创建对象communityTarget
     *
     * @param address
     * @return CommunityTarget
     */
    public static CommunityTarget createTarget4Trap(String address) {
        CommunityTarget target = new CommunityTarget();
        target.setAddress(GenericAddress.parse(address));
        target.setVersion(DEFAULT_VERSION);
        target.setTimeout(DEFAULT_TIMEOUT);
        target.setRetries(DEFAULT_RETRY);
        return target;
    }

    public static void main(String[] args) {

    	try {
            SnmpTrapSend demo = new SnmpTrapSend();
            demo.init();
            demo.sendPDU();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
