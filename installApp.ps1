Set-Location (Split-Path $PSCommandPath)

Expand-Archive -DestinationPath ".\imaging" -force -LiteralPath .\imaging.zip

$currentPrincipal = New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())
$isAdmin = $currentPrincipal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

$javaDir = "C:\Program Files\java"
$jdkDir = "C:\Program Files\Java\jdk-21.0.1"
$jfxDir = "C:\Program Files\Java\javafx-sdk-21.0.1"
$isDirs = (Test-Path -Path $javaDir ) -or (Test-Path -Path $jdkDir ) -or (Test-Path -Path $jfxDir  )
if (! $isDirs ) {
    if ( ! $isAdmin) {
        Write-host  "Non sei amministratore!" -ForegroundColor Red
        exit
    }
}
Write-host  "Eseguo come amministratore!" -ForegroundColor green

$addPath = $false
if (! (Test-Path -Path $javaDir )) {
   Write-host  ("Creo dir:" + $javaDir) -ForegroundColor green
   New-Item -Path $javaDir -ItemType Directory -Force | out-null
}
$jdkPath = $null
if (! (Test-Path -Path $jdkDir )) {
    Write-host  ("Creo dir:" + $jdkDir) -ForegroundColor green
    Expand-Archive -DestinationPath $javaDir -force -LiteralPath "openjdk-21.0.1_windows-x64_bin.zip"
    [Environment]::SetEnvironmentVariable("JAVA_HOME", $jdkDir, "Machine")
    $addPath = $true
    $pth = Get-ChildItem -Path $javaDir -Filter "java.exe" -Recurse
    $jdkPath = $pth.Directory.FullName
}

if (! (Test-Path -Path $jfxDir) ) {
    Write-host  ("Creo dir:" + $jfxDir) -ForegroundColor green
    Expand-Archive -DestinationPath $javaDir -force -LiteralPath "openjfx-21.0.1_windows-x64_bin-sdk.zip"
    [Environment]::SetEnvironmentVariable("JAVAFX_HOME", $jfxDir, "Machine")
}
if ( $addPath -and $null -ne $jdkPath) {
    $PathVar = [Environment]::GetEnvironmentVariable("PATH", "Machine")
    if (! $PathVar.contains("jdk-21")) {
       Write-host  ("Aggiungo a PATH il javadir") -ForegroundColor green
       $PathVar = $PathVar  + [IO.Path]::PathSeparator + $jdkPath
      [Environment]::SetEnvironmentVariable( "Path", $PathVar, "Machine" )
    } else {
      Write-host  ("Gia presente in PATH il javadir") -ForegroundColor yellow
    }
}
