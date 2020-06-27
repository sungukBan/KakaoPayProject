1. Project명
 - kakaoPayProject
 
2. 프로젝트 정보
 - Spring boot
 - h2 dataBase
 - Method : POST
 - MsgType : json
 
3. Http header 정의
 - X-ROOM-ID : 대화방 식별값
 - X-USER-ID : 사용자 식별값

4. 입/출력 전문정의
 - SEND_AMT : 뿌리는 금액
 - SEND_CNT : 뿌리는 건수
 - TOKEN : 고유 token값
 - RLST_CD : 응답코드
 - RLST_MSG : 응답메시지
 - RECV_AMT : 받은금액
 - SEND_TM : 뿌린시간
 - SUCCES_AMT : 받기 완료된 금액
 - RECV_USER_ID : 받은 사용자 아아디

5. 사용방법-뿌리기 API
 - URL : /v1/pay/send
 - request  : {"SEND_AMT" : 1000, "SEND_CNT" : 2} 
 - response : {"RLST_MSG":"정상처리","TOKEN":"sKF","RLST_CD":"0000"}

6. 사용방법-받기 API
  - URL : /v1/pay/recv
  - request  : {"TOKEN" : "bwy"}
  - response : {"RECV_AMT":500,"RLST_MSG":"정상처리","RLST_CD":"0000"}

7. 사용방법-조회 API
  - URL : /v1/pay/inq
  - request  : {"TOKEN" : "bwy"}
  - response : {"SEND_AMT":1000,"RLST_MSG":"정상처리","SUCCES_AMT":500,"LIST":[{"RECV_USER_ID":"USER00003","RECV_AMT":500}],"RLST_CD":"0000","SEND_TM":"025347"}
