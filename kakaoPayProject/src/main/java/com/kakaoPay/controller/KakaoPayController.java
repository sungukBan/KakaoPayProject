package com.kakaoPay.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.kakaoPay.biz.KakaoPayBiz;
import com.kakaoPay.constant.Constant;

@RestController
public class KakaoPayController extends KakaoPayBiz {
    
	@PostMapping("/v1/pay/send")
	public String Send(HttpServletRequest request, @RequestBody String jsonStr) {
		return commonBiz(request, jsonStr, Constant.BIZ_CD_101);
	}

	@PostMapping("/v1/pay/recv")
	public String Recv(HttpServletRequest request,  @RequestBody String jsonStr) {
		return commonBiz(request, jsonStr, Constant.BIZ_CD_102);
	}
	
	@PostMapping("/v1/pay/inq")
	public String Inq(HttpServletRequest request,  @RequestBody String jsonStr) {
		return commonBiz(request, jsonStr, Constant.BIZ_CD_103);
	}
}
