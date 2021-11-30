$filnam = "F:\temp\photo\1985\1985-03-21 Ziano2\f19860723_111213.jpg"
( Get-Item $filnam ).CreationTime = "11/30/1980 12:21:11"
( Get-Item $filnam ).LastWriteTime  = "07/23/2000 12:20:11"
