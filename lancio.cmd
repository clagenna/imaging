if "%*" == "" (
  set /P SORG=Dammi il path completo del dir:
) else (
  set SORG=%*
)
set LUOGO=%~dp0
cd /d "%LUOGO%"
cd
java --enable-preview -jar target\imaging-jar-with-dependencies.jar -src "%SORG%" -dst work -recurse
