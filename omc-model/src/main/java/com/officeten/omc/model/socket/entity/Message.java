package com.officeten.omc.model.socket.entity;

import lombok.Data;

import java.util.Map;

/**
 * @author Jin
 * @date 2019/6/24
 */
@Data
public class Message {
	private String type;
	private Map<String, Object> payload;
}
