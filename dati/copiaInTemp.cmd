set SRC=%~dp0
cd /d "%SRC%"
cd
rem pause
@echo robocopy photo c:\temp\photo\ /mir
robocopy photo c:\temp\photo\ /mir
pause