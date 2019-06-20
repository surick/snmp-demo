package com.officeten.omc.common.util;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.Vector;

/**
 * @author Jin
 * @date 2019/6/18
 */
public class SnmpUtils {
    private Snmp snmp = null;
    private Address targetAddress = null;

    public void initComm() throws IOException {
        // 设置Agent方的IP和端口
        targetAddress = GenericAddress.parse("udp:127.0.0.1/161");
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    public ResponseEvent sendPDU(PDU pdu) throws IOException {
        // 设置 target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        // 通信不成功时的重试次数
        target.setRetries(2);
        // 超时时间
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version1);
        // 向Agent发送PDU，并返回Response
        return snmp.send(pdu, target);
    }

    public void setPDU() throws IOException {
        // set PDU
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 2, 1, 1, 5, 0}), new OctetString("SNMPTEST")));
        pdu.setType(PDU.SET);
        sendPDU(pdu);
    }

    public void getPDU() throws IOException {
        // get PDU
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 2, 1, 1, 5, 0})));
        pdu.setType(PDU.GET);
        readResponse(sendPDU(pdu));
    }

    public void readResponse(ResponseEvent respEvnt) {
        // 解析Response
        if (respEvnt != null && respEvnt.getResponse() != null) {
            Vector<? extends VariableBinding> recVBs = respEvnt.getResponse().getVariableBindings();
            for (int i = 0; i < recVBs.size(); i++) {
                VariableBinding recVB = recVBs.elementAt(i);
                System.out.println(recVB.getOid() + " : " + recVB.getVariable());
            }
        }
    }

    public static void main(String[] args) {
        try {
            SnmpUtils util = new SnmpUtils();
            util.initComm();
            util.setPDU();
            util.getPDU();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
