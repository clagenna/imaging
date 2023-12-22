Set-Location (Split-Path $PSCommandPath)

$zipFile = "imaging.zip"
$mvnCmd = "{0}\bin\mvn.cmd" -f ${Env:\MAVEN_HOME}

if ( Test-Path $zipFile ) {
  Remove-Item -Path $zipFile
}

Start-Process -Wait -FilePath $mvnCmd -ArgumentList 'clean','package','-P','remote'


Get-ChildItem -path "imaging.cmd", "imaging.properties", "target\imaging.jar" |
    Compress-Archive  -CompressionLevel Fastest -DestinationPath $zipFile

