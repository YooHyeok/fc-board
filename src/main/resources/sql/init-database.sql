show databases;

-- board 데이터베이스(스키마) 생성
create database board;

-- yjh 1234 user생성
create user 'yjh'@'localhost' identified by '1234';

-- mysql의user로 부터 user검색! - yjh 여부 확인
select `user` from mysql.user;

-- 권한 검색 :GRANT USAGE ON *.* TO `yjh`@`localhost` - 현재 아무런 권한이 없다
show grants for 'yjh'@'localhost';

-- yjh계정에 권한 부여 - board 데이터베이스 모든 테이블에 대해 모든 권한 부여
-- with grant option yjh계정으로 로그인 한 상태에서 다른 계정에게도 동일한 권한을 전수할 수 있는 기능 추가
grant all on `board`.* to 'yjh'@'localhost' with grant option;

-- 권한 재 확인
show grants for 'yjh'@'localhost';

-- 설정한 권한이 실제로 데이터베이스 권한 정책에 반영되기 위해 권한(GRANT) 테이블을 실제로 DB가 다시읽게끔 해주는 명령어
flush privileges;