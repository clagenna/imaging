set LUOGO=%~dp0
cd /d "%LUOGO%"
cd
set qta=0

set JARTEST=target\imaging-jar-with-dependencies.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
  set /a qta=%qta%+1
  )

set JARTEST=imaging-jar-with-dependencies.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
  set /a qta=%qta%+1
  )

set JARTEST=target\imaging.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
  set /a qta=%qta%+1
  )

set JARTEST=imaging.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
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
goto vai



:info
@echo off
echo [92m%*[0m
goto :eof

:errore
@echo off
echo [91m%*[0m
goto :eof

:vai
@echo on
set MODPATH=C:\Program Files\javafx-sdk-17.0.1\lib
set MODS=javafx.controls
set MODS=%MODS%,javafx.base
set MODS=%MODS%,javafx.fxml
set MODS=%MODS%,javafx.graphics
set MODS=%MODS%,javafx.web
rem set MODS=%MODS%,javafx.media

java --module-path "%MODPATH%" --add-modules="%MODS%" -jar "%JAREXE%"


:fine