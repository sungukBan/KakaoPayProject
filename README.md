# KakaoPayProject
1. Project명
 - kakaoPayProject
 
2. 프로젝트 정보
 - Spring boot
 - h2 dataBase
 - Method : POST
 - MsgType : json

3. 사용방법
 3.1 뿌리기 API
  - URL : /v1/pay/send
  - request  : {"SEND_AMT" : 1000, "SEND_CNT" : 2} 
  - response : {"RLST_MSG":"정상처리","TOKEN":"sKF","RLST_CD":"0000"}
 3.2 받기 API
  - URL : /v1/pay/recv
  - request  : {"TOKEN" : "bwy"}
  - response : {"RECV_AMT":500,"RLST_MSG":"정상처리","RLST_CD":"0000"}
 3.3 조회 API
  - URL : /v1/pay/inq
  - request  : {"TOKEN" : "bwy"}
  - response : {"SEND_AMT":1000,"RLST_MSG":"정상처리","SUCCES_AMT":500,"LIST":[{"RECV_USER_ID":"USER00003","RECV_AMT":500}],"RLST_CD":"0000","SEND_TM":"025347"}
