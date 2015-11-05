@echo off
java -cp .;d:{path to deploy base}\lib\*;d:{path to deploy base} com/qlik/automation/CronTrigger
pause

exit /b