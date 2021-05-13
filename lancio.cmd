set LUOGO=%~dp0
cd /d "%LUOGO%"
cd
set qta=0

set JAREXE=target\imaging-jar-with-dependencies.jar
if exist "%JAREXE%" (
  call :info  "%JAREXE%"
  set /a qta=%qta%+1
  )

set JAREXE=imaging-jar-with-dependencies.jar
if exist "%JAREXE%" (
  call :info  "%JAREXE%"
  set /a qta=%qta%+1
  )

set JAREXE=target\imaging.jar
if exist "%JAREXE%" (
  call :info  "%JAREXE%"
  set /a qta=%qta%+1
  )

set JAREXE=imaging.jar
if exist "%JAREXE%" (
  call :info  "%JAREXE%"
  set /a qta=%qta%+1
  )

if %qta% equ 0 (
  @echo.
  call :errore Non trovo il *.JAR del programma ?!?
  goto fine
) 
if %qta% gtr 1 (
  @echo.
  call :errore Troppi programmi *.JAR del programma !
  call :errore cancella le versioni piu vecchie
  call :errore e rilancia
  goto fine
) 


set JAREXE=target\imaging-jar-with-dependencies.jar
if exist "%JAREXE%" goto vai
set JAREXE=imaging-jar-with-dependencies.jar
if exist "%JAREXE%" goto vai
set JAREXE=target\imaging.jar
if exist "%JAREXE%" goto vai
@echo Non trovo il *.JAR del programma ?!?
goto fine


:info
@echo off
echo [92m%*[0m
goto :eof

:errore
@echo off
echo [91m%*[0m
goto :eof

:vai
java --enable-preview -jar "%JAREXE%"

:fine