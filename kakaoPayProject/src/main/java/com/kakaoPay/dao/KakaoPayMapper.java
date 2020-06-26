package com.kakaoPay.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.kakaoPay.vo.KakaoPaySendSubVo;
import com.kakaoPay.vo.KakaoPaySendVo;

@Mapper
public interface KakaoPayMapper {
	
	void insKakaoPaySend(KakaoPaySendVo paySend);
	void insKakaoPaySendSub(KakaoPaySendSubVo paySendSub);
	
	KakaoPaySendVo selKakaoPaySendRecv(KakaoPaySendVo paySend);
	String selKakaoPaySendSubChk(KakaoPaySendSubVo paySendSub);
	int updKakaoPaySendSub(KakaoPaySendSubVo paySendSub);
	int selKakaoPaySendSubRecvAmt(KakaoPaySendSubVo paySendSub);

	KakaoPaySendVo selKakaoPaySend(KakaoPaySendVo paySend);
	List<KakaoPaySendSubVo> selKakaoPaySendSubList(KakaoPaySendSubVo paySend);
	
}
