package com.officeten.omc.model.socket.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jin
 * @date 2019/6/24
 */
@Data
public class LoginRequest implements Serializable {
	private int code;
	private String body;
}
