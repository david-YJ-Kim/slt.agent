' WScript.Shell 객체 생성
Set WshShell = CreateObject("WScript.Shell")

' 배치 파일을 백그라운드에서 실행 (0은 창을 숨김)
WshShell.Run "C:\Users\tspsc\AppData\Local\Sellter\test\sample_run.bat", 0

' WScript.Shell 객체 해제
Set WshShell = Nothing
