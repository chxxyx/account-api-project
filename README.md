# 요구사항 정의서 
<br>
사용자 계좌에서 잔액 인출 및 입금과 타 계좌로 송금을 할 수 있는 거래 관리 시스템. <br>
사용자와 계좌 정보를 저장하며, 사용자의 거래 및 정보 확인 기능 제공.

## 개발 기간

## ERD
<img width="712" alt="스크린샷 2023-01-28 오전 1 19 00" src="https://user-images.githubusercontent.com/97508297/215273546-6efd079f-c5a9-4fc9-83f9-1c590b4e6e6b.png">

**User**

| id | binary(16) | PK |
| --- | --- | --- |
| ssn | varchar | 주민번호 |
| username | varchar | 아이디 |
| password | varchar | 비밀번호 |
| name | varchar | 이름 |
| role | varchar | 회원 타입(권한) |
| created_at | datetime(6) | 생성일 |
| modified_at | datetime(6) | 수정일 |

**Account**

| account_number | varchar | PK |
| --- | --- | --- |
| user_id | binary(16) | FK |
| account_password | varchar | 계좌 비밀번호 |
| balance | bingint(20) | 계좌 잔액 |
| account_status | varchar | 계좌 상태 |
| created_at | datetime(6) | 계좌 생성일 |
| modified_at | datetime(6) | 계좌 수정일 |
| registerd_at | datetime(6) | 계좌 등록일 |
| unRegisterd_at | datetime(6) | 계좌 정지일 |

**Transaction (거래 테이블 - 입금, 출금)**

| id | bigint(20) | PK |
| --- | --- | --- |
| account_accountNumber | varchar | FK |
| amount | bigint(20) | 거래 금액 |
| balance_snapshot | bigint(20) | 잔액 |
| created_at | datetime(6) | 거래 생성일 |
| updated_at | datetime(6) | 거래 수정일 |
| transated_at | datetime(6) | 거래 날짜 |
| transaction_result_type | varchar | 거래 성공 여부 |
| transaction_type | varchar | 거래 타입 |

**Transfer (송금 테이블)**

| id | bigint(20) | PK |
| --- | --- | --- |
| transaction | bigint(20) | FK |
| sender_name | varchar | 송금 유저 |
| sender_account_number | varchar | 송금 유저 계좌 |
| receiver_name | varchar | 송금 받는 유저 |
| receiver_account_number | varchar | 송금 받을 계좌 |

## 사용 기술스택

- SpringBoot
- Java
- MariaDB
- JPA

## 프로젝트 기능

<aside>
💡 구현 기능 

- [ ]  계좌 검색 기능
- [x]  계좌 관리 (생성 / 삭제 / 금액 인출 / 금액 입금)
- [x]  송금 기능 및 송금 이력 조회
- [x]  계좌 잔액 조회 (기간별)
- [x]  로그인 한 회원에 따른 계좌 접근 허가 기능 구현
- [x]  관리자 (회원 정보 및 계좌 정보 조회, 상태 변경)
</aside>

## 상세 요구 사항

## **[회원]**


<details>
<summary> 회원 관리 API </summary>
<div markdown="1">    


    [ 회원가입 ] 

    - User는 회원가입을 할 수 있다.
        - 회원가입 시 아이디, 비밀번호, 이름, 주민등록번호, 이메일 필요하다.
        - 회원가입시 이미 회원가입된 이메일로 회원가입을 시도하면 에러를 발생한다.
        - 회원 비밀번호, 주민등록 번호는 암호화 처리

    [ 로그인 & 로그아웃 ]

    - User는 가입 정보를 이용하여 로그인을 할수 있다.
        - 로그인시 회원가입한적 없는 이메일을 이용하여 로그인을 시도하면 에러가 발생한다.
        - 로그인시 비밀번호가 일치하지 않는다면 에러가 발생한다.
        - 로그인 한 유저는 본인의 비밀번호를 변경할 수 있다. ( 미구현 )
    - User는 로그아웃을 할 수 있다. 

  </div>
  </details>

## **[계좌 서비스]**

<details>
<summary> 계좌 관리 API </summary>
<div markdown="1">    

    [ 계좌 생성하기 ]

    - 계좌는 로그인 한 유저만 생성할 수 있다.
        - 유저가 로그인하지 않았다면 에러를 발생한다.
    - 계좌 사용을 위한 계좌 비밀번호가 필요하다.

    [ 계좌 삭제하기 ]

    - 계좌는 로그인 한 유저만 삭제할 수 있다.
        - 삭제는 본인의 계좌만 삭제할 수 있다.
        - 삭제한 계좌에 유저는 접근할 수 없다.
    - 계좌 삭제 전 본인인증이 필요하다.

    [ 계좌 정보 수정하기 ]

    * 계좌 정보 수정은 비밀번호만 수정할 수 있다는 가정 하에 구현.

    - 계좌 수정은 본인의 계좌만 수정할 수 있다.
    - 계좌 비밀번호를 수정할 수 있다.
        - 수정 전 유저 본인 확인 필요하다.(회원 비밀번호, 계좌 비밀번호 입력 필요)
        - 수정할 계좌 비밀번호를 입력해야 한다.
        - 수정 후 유저 정보와 수정된 계좌, 비밀번호를 확인할 수 있다.

  </div>
  </details>


<details>
<summary> 거래 API </summary>
<div markdown="1">    

    * 출금과 입금은 본인 계좌에서만 입금, 출금이 가능하다는 가정 하에 구현.

    [ 출금하기 ] 

    - 인출 거래는 로그인 한 유저만 인출할 수 있다.
        - 본인의 계좌만 인출 가능하다.
    - 계좌 거래 진행 전, 계좌 비밀번호가 필요하다.

    [ 입금하기 ]

    - 로그인 한 유저만 입금할 수 있다.
    - 입금 거래는 유저 본인의 계좌에 입금 가능하다.
        - 입금 전, 거래 진행을 위한 계좌 비밀번호가 필요하다.
        - 입금 금액이 통장 잔액 보다 큰 금액을 입금하려는 경우 에러 발생

    [ 다른 계좌로 송금하기 ]

    - 송금 거래는 로그인한 유저만 거래할 수 있다.
        - 송금 거래 전, 송금하려는 상대 계좌 번호와 이름 확인이 필요하다.
        - 거래 진행을 위한 유저의 계좌 비밀번호가 필요하다.
            - 송금할 계좌 유효한지 체크 ( 정지 계좌는 송금할 수 없다.)
            - 송금할 때 잔액보다 큰 금액을 입금하려는 경우 에러 발생
            - 상대방 계좌를 조회할 수 없을 때 에러 발생

</div>
</details> 

<details>
<summary> 계좌 및 거래 내역 조회 API </summary>
<div markdown="1">    

    [ 내 거래 내역 조회하기  (입금, 출금, 송금 내역 조회) ]

    - 내 거래 내역 조회는 로그인 한 유저만 확인할 수 있다.
        - 본인 계좌만 거래 내역을 조회할 수 있다.
        - 조회 전 계좌 비밀번호가 필요하다.
    - 전체 거래 내역 조회가 가능하다.     
    - 기간 별로 이력 조회가 가능하다.
    - 거래 종류 별로 조회가 가능하다. ( 미구현 )
    - 계좌번호, 거래 상태, 거래 종류, 거래 금액, 잔액, 거래 일시를 확인할 수 있다.

    [ 계좌 및 잔액 조회 ]

    - 유저의 보유 계좌와 계좌 잔액 조회가 가능하다.
        - 계좌 잔액 조회 전, 해당 계좌 비밀번호가 필요하다. (비밀번호 입력 횟수 제한)
    - 유저가 보유한 전체 계좌 조회가 가능하다.

</div>
</details>


## [관리자]

<details>
<summary> 전체 사용자목록 확인 </summary>
<div markdown="1">   

    - 관리자는 유저 전체 목록 확인이 가능하다. (Pageable 처리)
        - 사용자 정보(주민번호, 아이디, 비밀번호, 이름, 생성일, 정보 수정일, 사용자 상태)를 확인할 수 있다.
    - 관리자는 사용자의 정보를 삭제할 수 있다.  (탈퇴 기능)

</div>
</details>

<details>
<summary> 전체 계좌목록 확인 </summary>
<div markdown="1">   

    - 관리자는 유저의 계좌 목록 확인이 가능하다. (Pageable 처리)
        - 사용자 아이디, 이름, 계좌 번호, 계좌 비밀번호를 확인할 수 있다.
        - 사용자 계좌를 정지할 수 있다.

</div>
</details>


<details>
<summary> 계좌 검색 기능 ( 미구현 ) </summary>
<div markdown="1">   

    - 유저의 계좌 검색이 가능하다.
        - 유저의 이름, 아이디, 계좌번호로 검색할 수 있다.

</div>
</details>

<details>
<summary> 관리자 CRUD ( 미구현 )</summary>
<div markdown="1">   

    - 관리자 목록을 조회를 통해 관리자 추가와 삭제가 가능하다.
        - 관리자 아이디, 이름, 비밀번호 등의 정보 조회 할 수 있다.
</div>
</details>

