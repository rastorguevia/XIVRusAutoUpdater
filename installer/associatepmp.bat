for %%A in ("%~dp0.") do set "folder=%%~dpA"

set "ftypename=release"
set "extension=.pmp"
set "pathtoexe=%folder%TranslationUpdater.exe"
set "pathtoicon=%~dp0file_type_favicon.ico"

REG ADD HKEY_CURRENT_USER\SOFTWARE\Classes%extension% /ve /d "%ftypename%" /f
REG ADD HKCU\SOFTWARE\Classes%ftypename%\DefaultIcon /ve /d "%pathtoicon%" /f

reg add HKCR.pmp /ve /d "%ftypename%" /f
reg add HKCR%ftypename%\Shell\Open\Command /ve "cd /d "%~dp0"; "%pathtoexe%" "%%1"" /f

ftype %ftypename%="%pathtoexe%" "%%1" %%*
assoc %extension%=%ftypename%

pause