for %%A in ("%~dp0.") do set "folder=%%~dpA"

set "ftypename=release"
set "extension=.pmp"
set "pathtoexe=%folder%TranslationUpdater.exe"
set "pathtoicon=%~dp0file_type_favicon.ico"

reg add HKCU\SOFTWARE\Classes\%extension% /ve /d "%ftypename%" /f
reg add HKCU\SOFTWARE\Classes\%ftypename%\DefaultIcon /ve /d "%pathtoicon%" /f
reg add HKCU\SOFTWARE\Classes\%ftypename%\Shell\Open\Command /ve /d "\"%SystemRoot%\\System32\\cmd.exe\" /c cd /d \"%folder%" && start \"\" \"%pathtoexe%\" \"%%1\"" /f
