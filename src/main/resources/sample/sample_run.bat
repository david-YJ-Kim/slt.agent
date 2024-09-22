@echo off
REM 서비스 이름 설정
set SERVICE_NAME=MyJavaService

REM JAVA 실행 파일 경로 설정
set JAVA_EXE=C:\Users\tspsc\AppData\Local\Sellter\test\java-1.8.0-openjdk-1.8.0.332-1.b09.ojdkbuild.windows.x86_64\java-1.8.0-openjdk-1.8.0.332-1.b09.ojdkbuild.windows.x86_64\bin\

REM JAR 파일 경로 설정
set JAR_PATH=C:\Users\tspsc\AppData\Local\Sellter\test\service.jar

REM 로그 파일 저장 경로 설정
set LOG_PATH=C:\path\to\log\service.log

REM Java 옵션 설정 (필요 시 추가)
set JAVA_OPTS=-Xms512m -Xmx1024m

REM 서비스 Property 파일 선택
set SRV_PROP=""



echo Starting %SERVICE_NAME%...

REM JAR 파일 실행
%JAVA_EXE%java.exe %JAVA_OPTS% -jar %JAR_PATH%

echo %SERVICE_NAME% started successfully.
pause
