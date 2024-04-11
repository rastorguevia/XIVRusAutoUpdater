for %%A in ("%~dp0.") do set "folder=%%~dpA"

set "ftypename=translation_pmp"
set "extension=.pmp"
set "pathtoexe=%folder%TranslationUpdater.exe"
set "pathtoicon=%~dp0file_type_favicon.ico"

REG ADD HKEY_CURRENT_USER\SOFTWARE\Classes\%extension%\ /t REG_SZ /d "%ftypename%" /f
REG ADD HKCU\SOFTWARE\Classes\%ftypename%\DefaultIcon\ /t REG_SZ /d "%pathtoicon%" /f

ftype %ftypename%=%pathtoexe% "%%1" %%*
assoc %extension%=%ftypename%