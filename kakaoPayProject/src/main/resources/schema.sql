CREATE TABLE KAKAO_PAY_SEND
(
    roomId  varchar(50) not null,
    sendDt  varchar(8) not null,
    token    char(3) not null,
    sendAmt integer,
    sendCnt integer,
    sendTm  varchar(6),
    userId  varchar(50),
    PRIMARY KEY(roomId, sendDt, token)
);

CREATE TABLE KAKAO_PAY_SEND_SUB
(
    roomId  varchar(50) not null,
    sendDt  varchar(8) not null,
    token   char(3) not null,
    subSeq  varchar(5) not null,
    recvAmt integer,
    recvTm  varchar(6),
    recvUserId varchar(50),
    recvYn char(1),
    PRIMARY KEY(roomId, sendDt, token, subSeq)
);