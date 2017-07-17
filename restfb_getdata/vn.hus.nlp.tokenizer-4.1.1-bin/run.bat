@echo off

echo %date%
echo %time%
set curTimestamp=%date:~10,4%%date:~4,2%%date:~7,2%_%time:~0,2%%time:~3,2%%time:~6,2%%time:~9,2%
echo %curTimestamp% 

set SAVEDIR=outputT%curTimestamp%


If not exist %SAVEDIR% (
echo %SAVEDIR%
mkdir %SAVEDIR%
)
 
@call  vnTokenizer.bat -i .\outputF\  -o .\%SAVEDIR%\