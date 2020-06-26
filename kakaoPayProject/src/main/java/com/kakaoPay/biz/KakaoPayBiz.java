package com.kakaoPay.biz;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.kakaoPay.constant.Constant;
import com.kakaoPay.dao.KakaoPayMapper;
import com.kakaoPay.util.UtilCommon;
import com.kakaoPay.vo.APIStatus;
import com.kakaoPay.vo.KakaoPaySendSubVo;
import com.kakaoPay.vo.KakaoPaySendVo;

@Component
public class KakaoPayBiz {

	@Autowired
	private KakaoPayMapper mapper;
	
	KakaoPayCommonBiz commonBiz = new KakaoPayCommonBiz();
	
	public String commonBiz(HttpServletRequest request, String jsonStr, String bizCd) {
		
		APIStatus status = new APIStatus();
		System.out.println("-----------------------------------------------------");
		System.out.println("X-ROOM-ID :: " + request.getHeader("X-ROOM-ID"));
		System.out.println("X-USER-ID :: " + request.getHeader("X-USER-ID"));
		System.out.println("jsonStr :: " + jsonStr);
		System.out.println("bizCd :: " + bizCd);
		System.out.println("-----------------------------------------------------");
		
		//--------------------------------------
		// Http header입력값 검증
		//--------------------------------------
		if ( commonBiz.doComValidation(request, status) == false ) {
			return setResponseData(jsonStr, status);
		}
		
		//--------------------------------------
		// BIZ_CD:001 - 뿌리기 API
		//--------------------------------------
		if ( bizCd.equals(Constant.BIZ_CD_101) ) {
			jsonStr = doBiz101(request, jsonStr, status);
		}
		
		//--------------------------------------
		// BIZ_CD:002 - 받기 API
		//--------------------------------------
		else if ( bizCd.equals(Constant.BIZ_CD_102) ) {
			jsonStr = doBiz102(request, jsonStr, status);
		}
		
		//--------------------------------------
		// BIZ_CD:003 - 조회 API
		//--------------------------------------
		else if ( bizCd.equals(Constant.BIZ_CD_103) ) {
			jsonStr = doBiz103(request, jsonStr, status);
		}
		
		return setResponseData(jsonStr, status);
	}
	
	private String setResponseData(String jsonStr, APIStatus status) {
		
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj    = (JSONObject) jsonParser.parse(jsonStr);
			jsonObj.put(Constant.RLST_CD, status.getRLST_CD());
			jsonObj.put(Constant.RLST_MSG, status.getRLST_MSG());
			
			jsonStr = jsonObj.toJSONString();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("jsonStr :: " + jsonStr);
		return jsonStr;
	}


	public String doBiz101(HttpServletRequest request, String jsonStr, APIStatus status) {
		
		try {

			//-------------------------
			// 01. 입력값 검증
			//-------------------------
			if ( commonBiz.do101Validation(jsonStr, status) == false ) {
				return jsonStr;
			}

			//-------------------------
			// 02. token 생성
			//-------------------------
			String token = UtilCommon.getToken(3);
			
			//-------------------------
			// 03. 뿌린금액 DB 등록
			//-------------------------
			jsonStr = insKakaoPaySendData(request, jsonStr, token, status);
			
		} catch(Exception e) {
			status.setAPIStatus(Constant.RLST_CD_9999, Constant.RLST_MSG_9999);
			e.printStackTrace();
		}
		
		return jsonStr;
	}
	

	@Transactional
	private String insKakaoPaySendData(HttpServletRequest request, String jsonStr, String token, APIStatus status) {

		KakaoPaySendVo paySend = new KakaoPaySendVo();
		JSONParser jsonParser   = new JSONParser();
		
		try {
			//-------------------------------
			// 01. KAKAO_PAY_SEND Table 등록
			//-------------------------------
			JSONObject jsonObj = (JSONObject) jsonParser.parse(jsonStr);
			int send_amt = Integer.parseInt(String.valueOf(jsonObj.get("SEND_AMT")));
			int send_cnt = Integer.parseInt(String.valueOf(jsonObj.get("SEND_CNT")));
			
			paySend.setRoomId(request.getHeader("X-ROOM-ID"));
			paySend.setSendDt(UtilCommon.getDate());
			paySend.setToken(token);
			paySend.setSendAmt(send_amt);
			paySend.setSendCnt(send_cnt);
			paySend.setSendTm(UtilCommon.getHHmmss());
			paySend.setUserId(request.getHeader("X-USER-ID"));
			mapper.insKakaoPaySend(paySend);
			
			//-------------------------------
			// 02. KAKAO_PAY_SEND_SUB Table 등록 (뿌린금액/뿌릴인원)
			//-------------------------------
			int recv_amt = send_amt / send_cnt;
			int subSeq = 1;
			for (int i=0 ; i<paySend.getSendCnt(); i++) {
				
				KakaoPaySendSubVo paySendSub = new KakaoPaySendSubVo();
				paySendSub.setRoomId(paySend.getRoomId());
				paySendSub.setSendDt(paySend.getSendDt());
				paySendSub.setToken(token);
				paySendSub.setSubSeq(UtilCommon.fillZeros(5, subSeq+""));
				paySendSub.setRecvAmt(recv_amt);
				paySendSub.setRecvYn("N");
				mapper.insKakaoPaySendSub(paySendSub);

				subSeq++;
			}
			
			//-------------------------------
			// 03. 응답전문 세팅
			//-------------------------------
			JSONObject respnJson = new JSONObject();
			respnJson.put("TOKEN", token);
			jsonStr = respnJson.toJSONString();
			status.setAPIStatus(Constant.RLST_CD_0000, Constant.RLST_MSG_0000);
			
		} catch(Exception e) {
			status.setAPIStatus(Constant.RLST_CD_9999, Constant.RLST_MSG_9999);
			e.printStackTrace();
		}
		return jsonStr;
	}
	
	public String doBiz102(HttpServletRequest request, String jsonStr, APIStatus status) {
		
		try {

			//-------------------------
			// 01. 입력값 검증
			//-------------------------
			if ( commonBiz.doTokenValidation(jsonStr, status) == false ) {
				return jsonStr;
			}
			
			//-------------------------
			// 02. 뿌린금액 DB 수정
			//-------------------------
			jsonStr = updKakaoPaySendData(request, jsonStr, status);
			
		} catch(Exception e) {
			status.setAPIStatus(Constant.RLST_CD_9999, Constant.RLST_MSG_9999);
			e.printStackTrace();
		}
		
		return jsonStr;
	}
	
	private String updKakaoPaySendData(HttpServletRequest request, String jsonStr, APIStatus status) {

		KakaoPaySendVo    paySend    = new KakaoPaySendVo();
		KakaoPaySendSubVo paySendSub = new KakaoPaySendSubVo();
		
		JSONParser jsonParser   = new JSONParser();
		JSONObject respnJson    = new JSONObject();

		try {
			//-------------------------------
			// 01. 받기 API 사전검증
			//-------------------------------
			JSONObject jsonObj = (JSONObject) jsonParser.parse(jsonStr);
			String token = String.valueOf(jsonObj.get("TOKEN"));
			
			paySend.setRoomId(request.getHeader("X-ROOM-ID"));
			paySend.setSendDt(UtilCommon.getDate());
			paySend.setUserId(request.getHeader("X-USER-ID"));
			paySend.setToken(token);
			
			// 조회되지 않은 경우
			KakaoPaySendVo paySendData = mapper.selKakaoPaySendRecv(paySend);
			if ( paySendData == null ) {
				status.setAPIStatus(Constant.RLST_CD_9008, Constant.RLST_MSG_9008);
				return jsonStr;
			}
			// 자신이 뿌린 건은 받아올 수 없음
			if ( paySendData.getUserId().equals(paySend.getUserId()) ) {
				status.setAPIStatus(Constant.RLST_CD_9009, Constant.RLST_MSG_9009);
				return jsonStr;
			}			
			// 뿌린시간이 10분 경과된 경우 
			if ( UtilCommon.addMinute(paySendData.getSendTm(), 10).compareTo(UtilCommon.getHHmmss()) < 0 ) {
				status.setAPIStatus(Constant.RLST_CD_9010, Constant.RLST_MSG_9010);
				return jsonStr;
			}
			
			paySendSub.setRecvTm(UtilCommon.getHHmmss());
			paySendSub.setRecvUserId(request.getHeader("X-USER-ID"));
			paySendSub.setRoomId(request.getHeader("X-ROOM-ID"));
			paySendSub.setSendDt(UtilCommon.getDate());
			paySendSub.setToken(token);
			
			// 이미 받기를 완료한 경우
			if ( mapper.selKakaoPaySendSubChk(paySendSub) != null ) {
				status.setAPIStatus(Constant.RLST_CD_9011, Constant.RLST_MSG_9011);
				return jsonStr;
			}
			
			//-------------------------------
			// 02. 받기완료
			//-------------------------------
			mapper.updKakaoPaySendSub(paySendSub);
			int recv_amt = mapper.selKakaoPaySendSubRecvAmt(paySendSub);
			respnJson.put("RECV_AMT", recv_amt);

			//-------------------------------
			// 03. 응답전문 세팅
			//-------------------------------
			jsonStr = respnJson.toJSONString();
			status.setAPIStatus(Constant.RLST_CD_0000, Constant.RLST_MSG_0000);
			
		} catch(Exception e) {
			status.setAPIStatus(Constant.RLST_CD_9999, Constant.RLST_MSG_9999);
			e.printStackTrace();
		}
		return jsonStr;
	}
	
	public String doBiz103(HttpServletRequest request, String jsonStr, APIStatus status) {
		
		try {

			//-------------------------
			// 01. 입력값 검증
			//-------------------------
			if ( commonBiz.doTokenValidation(jsonStr, status) == false ) {
				return jsonStr;
			}
			
			//-------------------------
			// 02. 뿌린금액 DB 조회
			//-------------------------
			jsonStr = selKakaoPaySendDataList(request, jsonStr, status);
			
		} catch(Exception e) {
			status.setAPIStatus(Constant.RLST_CD_9999, Constant.RLST_MSG_9999);
			e.printStackTrace();
		}
		
		return jsonStr;
	}
	
	private String selKakaoPaySendDataList(HttpServletRequest request, String jsonStr, APIStatus status) {

		KakaoPaySendVo    paySend    = new KakaoPaySendVo();
		KakaoPaySendSubVo paySendSub = new KakaoPaySendSubVo();
		
		JSONParser jsonParser   = new JSONParser();
		JSONObject respnJson    = new JSONObject();
		JSONArray  jsonArray    = new JSONArray();

		try {
			JSONObject jsonObj = (JSONObject) jsonParser.parse(jsonStr);
			String token = String.valueOf(jsonObj.get("TOKEN"));
			
			paySend.setRoomId(request.getHeader("X-ROOM-ID"));
			paySend.setUserId(request.getHeader("X-USER-ID"));
			paySend.setInq_start_dt(UtilCommon.getBeforeDate(7));
			paySend.setInq_end_dt(UtilCommon.getDate());
			paySend.setToken(token);
			
			//-------------------------------
			// 01. 뿌린결과 조회
			//-------------------------------
			KakaoPaySendVo paySendData = mapper.selKakaoPaySend(paySend);
			if ( paySendData == null ) {
				status.setAPIStatus(Constant.RLST_CD_9012, Constant.RLST_MSG_9012);
				return jsonStr;
			}
			
			respnJson.put("SEND_TM", paySendData.getSendTm());
			respnJson.put("SEND_AMT", paySendData.getSendAmt());
			respnJson.put("SUCCES_AMT", paySendData.getSuccesAmt());
			
			//-------------------------------
			// 02. 받은결과 조회
			//-------------------------------
			paySendSub.setRoomId(paySendData.getRoomId());
			paySendSub.setSendDt(paySendData.getSendDt());
			paySendSub.setToken(paySendData.getToken());
			List<KakaoPaySendSubVo> paySendSubList = mapper.selKakaoPaySendSubList(paySendSub);

			for (int i=0 ; i<paySendSubList.size(); i++) {
				JSONObject subJson    = new JSONObject();
				
				subJson.put("RECV_AMT", paySendSubList.get(i).getRecvAmt());
				subJson.put("RECV_USER_ID", paySendSubList.get(i).getRecvUserId());
				jsonArray.add(i, subJson);
			}
			
			//-------------------------------
			// 03. 응답전문 세팅
			//-------------------------------
			respnJson.put("LIST", jsonArray);
			jsonStr = respnJson.toJSONString();
			status.setAPIStatus(Constant.RLST_CD_0000, Constant.RLST_MSG_0000);
			
		} catch(Exception e) {
			status.setAPIStatus(Constant.RLST_CD_9999, Constant.RLST_MSG_9999);
			e.printStackTrace();
		}
		return jsonStr;
	}
}
