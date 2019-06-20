package com.officeten.omc.netty.web;

import com.officeten.omc.common.util.R;
import com.officeten.omc.model.user.vo.UserLoginVO;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jin
 * @date 2019/6/19
 */
@RestController
@RequestMapping("/user")
public class UserTestController {

	@PostMapping("/login")
	public R login(@RequestBody UserLoginVO loginVO) {

		Map<String, String> map = new HashMap<>(2);
		map.put("token", "admin-token");
		return new R<>(map);
	}

	@GetMapping("/info")
	public R info(@RequestParam(name = "token") String token) {

		Map<String, String> res = new HashMap<>(6);
		res.put("role", "admin");
		res.put("introduction", "I am a super administrator");
		res.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
		res.put("name", "Super Admin");

		return new R<>(res);
	}
}
