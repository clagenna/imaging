# ( Get-Item "C:\temp\tribfe\oe55555\errate\vattelapesca.txt").CreationTime="2021-11-30 10:11:12"


$startDir = "C:\temp\tribfe"
$startDate = (Get-date).AddHours(-20)
Get-ChildItem -Path $startDir -Recurse -ErrorAction SilentlyContinue -Filter * |
  ForEach-Object {
    if ( Test-Path $_.FullName -PathType Leaf) {
       if ( ($_.FullName).IndexOf("errate") -gt 0 ) {
         if ( $_.CreationTime -lt $startDate ) {
            Write-Output $_.fullName
            $tempd = (Split-Path $_.FullName | split-path) + "\temp"
            if ( ! ( Test-Path $tempd ) ) {
              New-Item -ItemType Directory -Path $tempd
            }
            $destFil = "{0}\{1}" -f $tempd, $_.Name
            # Write-Output ("move {0}  {1}" -f $_.FullName, $destFil)
            Move-Item -Path $_.FullName -Destination $destFil
         }
       }
     }
  }