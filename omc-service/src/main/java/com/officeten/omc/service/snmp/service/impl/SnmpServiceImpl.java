package com.officeten.omc.service.snmp.service.impl;

import com.officeten.omc.common.util.SnmpUtil;
import com.officeten.omc.service.snmp.service.SnmpService;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * snmp实现类
 * @author Jin
 * @date 2019/6/18
 */
@Service
@Slf4j
public class SnmpServiceImpl implements SnmpService {

    @Override
    public List<Map<String, String>> snmpGet(String ip, String community, String oid) {

        CommunityTarget target = SnmpUtil.createDefault(ip, community);
        Snmp snmp = null;

        try {
            PDU pdu = new PDU();
            // pdu.add(new VariableBinding(new OID(new int[]
            // {1,3,6,1,2,1,1,2})));
            pdu.add(new VariableBinding(new OID(oid)));

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            log.debug("-------发送PDU-------");
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            log.debug("PeerAddress:" + respEvent.getPeerAddress());
            PDU response = respEvent.getResponse();

			List<Map<String, String>> res = new ArrayList<>();
			if (response == null) {
                log.debug("response is null, request time out");
            } else {

                // Vector<VariableBinding> vbVect = response.getVariableBindings();
                // System.out.println("vb size:" + vbVect.size());
                // if (vbVect.size() == 0) {
                // System.out.println("response vb size is 0 ");
                // } else {
                // VariableBinding vb = vbVect.firstElement();
                // System.out.println(vb.getOid() + " = " + vb.getVariable());
                // }

                log.debug("response pdu size is {}", response.size());

                for (int i = 0; i < response.size(); i++) {
                    VariableBinding vb = response.get(i);
                    log.debug(vb.getOid() + " = " + vb.getVariable());
					Map<String, String> map = new HashMap<>(2);
					map.put(vb.getOid().toDottedString(), vb.getVariable().toString());

					res.add(map);
                }
            }
            log.debug("SNMP GET one OID value finished !");
			return res;
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("SNMP Get Exception:" + e);
			return Collections.emptyList();
        } finally {

            if (snmp != null) {

                try {
                    snmp.close();
                } catch (IOException ex1) {
                    snmp = null;
                }
            }
        }
    }

    @Override
    public List<Map<String, String>> snmpGetList(String ip, String community, List<String> oidList) {
        CommunityTarget target = SnmpUtil.createDefault(ip, community);
        Snmp snmp = null;

        try {
            PDU pdu = new PDU();

            for (String oid : oidList) {
                pdu.add(new VariableBinding(new OID(oid)));
            }
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            // transport.listen();
            snmp = new Snmp(transport);
            snmp.listen();

            log.debug("-------发送消息-------");
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            log.debug("PeerAddress:" + respEvent.getPeerAddress());
            PDU response = respEvent.getResponse();

			List<Map<String, String>> res = new ArrayList<>();
            if (response == null) {
                log.debug("response is null, request time out");
            } else {
                log.debug("response pdu size is " + response.size());

                for (int i = 0; i < response.size(); i++) {
                    VariableBinding vb = response.get(i);
                    log.debug(vb.getOid() + " = " + vb.getVariable());
					Map<String, String> map = new HashMap<>(2);
					map.put(vb.getOid().toDottedString(), vb.getVariable().toString());

					res.add(map);
                }
            }
            log.debug("SNMP GET List OID value finished !");
			return res;
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("SNMP GetList Exception:" + e);
			return Collections.emptyList();
        } finally {

            if (snmp != null) {

                try {
                    snmp.close();
                } catch (IOException ex1) {
                    snmp = null;
                }
            }
        }
    }

    @Override
    public List<Map<String, String>> snmpAsyncGetList(String ip, String community,
                                         List<String> oidList) {
        CommunityTarget target = SnmpUtil.createDefault(ip, community);
        Snmp snmp = null;

        try {
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            PDU pdu = new PDU();

            for (String oid : oidList) {
                pdu.add(new VariableBinding(new OID(oid)));
            }
            final CountDownLatch latch = new CountDownLatch(1);
			List<Map<String, String>> res = new ArrayList<>();
            ResponseListener listener = new ResponseListener() {
                @Override
                public void onResponse(ResponseEvent event) {
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    PDU response = event.getResponse();
                    PDU request = event.getRequest();
                    log.debug("[request]:" + request);

                    if (response == null) {
                        log.debug("[ERROR]: response is null");
                    } else if (response.getErrorStatus() != 0) {
                        log.debug("[ERROR]: response status"
                                + response.getErrorStatus() + " Text:"
                                + response.getErrorStatusText());
                    } else {
                        log.debug("Received response Success!");

                        for (int i = 0; i < response.size(); i++) {
                            VariableBinding vb = response.get(i);
                            log.debug(vb.getOid() + " = " + vb.getVariable());
							Map<String, String> map = new HashMap<>(2);
							map.put(vb.getOid().toDottedString(), vb.getVariable().toString());

							res.add(map);
                        }
                        log.debug("SNMP Async GetList OID finished. ");
                        latch.countDown();
                    }
                }
            };

            pdu.setType(PDU.GET);
            snmp.send(pdu, target, null, listener);
            log.debug("async send pdu wait for response...");

            boolean wait = latch.await(30, TimeUnit.SECONDS);
            log.debug("latch.await =:" + wait);

            snmp.close();
			return res;
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("SNMP Async GetList Exception:" + e);
			return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, String>> snmpWalk(String ip, String community, String targetOid) {
        CommunityTarget target = SnmpUtil.createDefault(ip, community);
        TransportMapping transport = null;
        Snmp snmp = null;

        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            // transport.listen();
            snmp.listen();

            PDU pdu = new PDU();
            OID targetOID = new OID(targetOid);
            pdu.add(new VariableBinding(targetOID));

            boolean finished = false;
            log.debug("----> demo start <----");

			List<Map<String, String>> res = new ArrayList<>();
            while (!finished) {
                VariableBinding vb = null;
                ResponseEvent respEvent = snmp.getNext(pdu, target);

                PDU response = respEvent.getResponse();

                if (null == response) {
                    log.debug("responsePDU == null");
                    break;
                } else {
                    vb = response.get(0);
                }

                // check finish
                finished = checkWalkFinished(targetOID, pdu, vb);

                if (!finished) {
                    log.debug("==== walk each value :");
                    log.debug(vb.getOid() + " = " + vb.getVariable());
                    Map<String, String> map = new HashMap<>(2);
					map.put(vb.getOid().toDottedString(), vb.getVariable().toString());

					res.add(map);

                    // Set up the variable binding for the next entry.
                    pdu.setRequestID(new Integer32(0));
                    pdu.set(0, vb);
                } else {
                    log.debug("SNMP walk OID has finished.");
                    snmp.close();
                }
            }
            log.debug("----> demo end <----");
			return res;
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("SNMP walk Exception: " + e);
			return Collections.emptyList();
        } finally {

            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex1) {
                    snmp = null;
                }
            }
        }
    }

    private static boolean checkWalkFinished(OID targetOID, PDU pdu,
                                             VariableBinding vb) {
        boolean finished = false;

        if (pdu.getErrorStatus() != 0) {
            log.debug("[true] responsePDU.getErrorStatus() != 0 ");
            log.debug(pdu.getErrorStatusText());
            finished = true;
        } else if (vb.getOid() == null) {
            log.debug("[true] vb.getOid() == null");
            finished = true;
        } else if (vb.getOid().size() < targetOID.size()) {
            log.debug("[true] vb.getOid().size() < targetOID.size()");
            finished = true;
        } else if (targetOID.leftMostCompare(targetOID.size(), vb.getOid()) != 0) {
            log.debug("[true] targetOID.leftMostCompare() != 0");
            finished = true;
        } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
            log.debug("[true] Null.isExceptionSyntax(vb.getVariable().getSyntax())");
            finished = true;
        } else if (vb.getOid().compareTo(targetOID) <= 0) {
            log.debug("[true] Variable received is not "
                    + "lexicographic successor of requested " + "one:");
            log.debug(vb.toString() + " <= " + targetOID);
            finished = true;
        }

        return finished;
    }

    @Override
    public List<Map<String, String>> snmpAsyncWalk(String ip, String community, String targetOid) {
        final CommunityTarget target = SnmpUtil.createDefault(ip, community);
        Snmp snmp = null;

        try {
            log.debug("----> demo start <----");
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();

            final PDU pdu = new PDU();
            final OID targetOID = new OID(targetOid);
            final CountDownLatch latch = new CountDownLatch(1);
            pdu.add(new VariableBinding(targetOID));

			List<Map<String, String>> res = new ArrayList<>();
            ResponseListener listener = new ResponseListener() {
                @Override
                public void onResponse(ResponseEvent event) {
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);

                    try {
                        PDU response = event.getResponse();
                        // PDU request = event.getRequest();
                        // System.out.println("[request]:" + request);

                        if (response == null) {
                            log.debug("[ERROR]: response is null");
                        } else if (response.getErrorStatus() != 0) {
                            log.debug("[ERROR]: response status"
                                    + response.getErrorStatus() + " Text:"
                                    + response.getErrorStatusText());
                        } else {
                            log.debug("Received Walk response value :");
                            VariableBinding vb = response.get(0);

                            boolean finished = checkWalkFinished(targetOID,
                                    pdu, vb);

                            if (!finished) {
                                log.debug(vb.getOid() + " = "
                                        + vb.getVariable());
								Map<String, String> map = new HashMap<>(2);
								map.put(vb.getOid().toDottedString(), vb.getVariable().toString());
								res.add(map);

                                pdu.setRequestID(new Integer32(0));
                                pdu.set(0, vb);
                                ((Snmp) event.getSource()).getNext(pdu, target,
                                        null, this);
                            } else {
                                log.debug("SNMP Async walk OID value success !");
                                latch.countDown();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        latch.countDown();
                    }

                }
            };

            snmp.getNext(pdu, target, null, listener);
            log.debug("pdu 已发送,等到异步处理结果...");

            boolean wait = latch.await(30, TimeUnit.SECONDS);
            log.debug("latch.await =:" + wait);
            snmp.close();

            log.debug("----> demo end <----");
			return res;
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("SNMP Async Walk Exception:" + e);
			return Collections.emptyList();
        }
    }

    @Override
    public Boolean snmpSet(String ip, String community, String oid, String val)
            throws IOException {
        CommunityTarget target = SnmpUtil.createDefault(ip, community);
        Snmp snmp = null;
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid), new OctetString(val)));
        pdu.setType(PDU.SET);

        DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);

		try {
			snmp.listen();
			log.debug("-------> 发送PDU <-------");
			snmp.send(pdu, target);
			snmp.close();

			return true;
		} catch (Exception e) {
			log.debug("send pdu error:" + e);
			e.printStackTrace();
			return false;
		}
    }
}
