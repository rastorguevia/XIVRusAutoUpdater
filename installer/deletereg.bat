set "ftypename=translationpmp"
set "extension=.pmp"

reg delete HKCU\SOFTWARE\Classes\%extension% /f
reg delete HKCU\SOFTWARE\Classes\%ftypename% /f