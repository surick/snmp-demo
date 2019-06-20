package com.officeten.omc.netty.web;

import com.officeten.omc.common.util.R;
import com.officeten.omc.model.snmp.vo.SnmpQueryVO;
import com.officeten.omc.service.snmp.service.SnmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jin
 * @date 2019/6/18
 */
@RestController
@RequestMapping("/snmp")
public class SnmpTestController {

    @Autowired
    SnmpService snmpService;

    @PostMapping("/get")
    public R snmpGet(@RequestBody SnmpQueryVO snmpQueryVO) {

		return new R<>(snmpService.snmpGet(snmpQueryVO.getIp(), snmpQueryVO.getCommunity(), snmpQueryVO.getOid()));
    }

    @PostMapping("/getList")
    public R snmpGetList(@RequestBody SnmpQueryVO snmpQueryVO) {

		List<String> oidList = Arrays.asList(snmpQueryVO.getOids().split(","));
		return new R<>(snmpService.snmpGetList(snmpQueryVO.getIp(), snmpQueryVO.getCommunity(), oidList));
    }

    @PostMapping("/getListAsync")
    public R snmpAsyncGetList(@RequestBody SnmpQueryVO snmpQueryVO) {

		List<String> oidList = Arrays.asList(snmpQueryVO.getOids().split(","));
		return new R<>(snmpService.snmpAsyncGetList(snmpQueryVO.getIp(), snmpQueryVO.getCommunity(), oidList));
    }

    @PostMapping("/getTree")
    public R snmpWalk(@RequestBody SnmpQueryVO snmpQueryVO) {

		return new R<>(snmpService.snmpWalk(snmpQueryVO.getIp(), snmpQueryVO.getCommunity(), snmpQueryVO.getOid()));
    }

    @PostMapping("/getTreeAsync")
    public R snmpAsyncWalk(@RequestBody SnmpQueryVO snmpQueryVO) {

		return new R<>(snmpService.snmpAsyncWalk(snmpQueryVO.getIp(), snmpQueryVO.getCommunity(), snmpQueryVO.getOid()));
    }

    @PostMapping("/set")
    public R snmpSet(@RequestBody SnmpQueryVO snmpQueryVO) throws IOException {

		return new R<>(snmpService.snmpSet(snmpQueryVO.getIp(), snmpQueryVO.getCommunity(), snmpQueryVO.getOid(), snmpQueryVO.getValue()));
    }
}
