[Setup]

#define AppName "TranslationUpdater"
#define ResourceDir "C:\Users\***\IdeaProjects\ff14-ru-translation-auto-updater"
; NOTE: Change this variable to your path to the project target folder

AppId={{45C180A9-C819-4472-AE05-5B4854D40499}
AppName={#AppName}
AppVersion=1.0
;AppVerName={#AppName} 1.0
DefaultDirName={pf}\{#AppName}
DefaultGroupName={#AppName}
AllowNoIcons=yes
LicenseFile={#ResourceDir}\installer\license.txt
InfoBeforeFile={#ResourceDir}\installer\before.txt
InfoAfterFile={#ResourceDir}\installer\after.txt
OutputDir={#ResourceDir}\target
OutputBaseFilename={#AppName}Installer
SetupIconFile={#ResourceDir}\target\classes\favicon.ico
Compression=lzma
SolidCompression=yes
DisableProgramGroupPage=true

[Languages]
Name: "russian"; MessagesFile: "compiler:Languages\Russian.isl"

[Files]
Source: "{#ResourceDir}\target\{#AppName}.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#ResourceDir}\target\win32\*"; DestDir: "{app}\win32"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{userstartup}\{#AppName}"; Filename: "{app}\{#AppName}.exe"; WorkingDir: "{app}"

[Run]
Filename: "{app}\win32\associatepmp.bat"; Parameters: "install"; Flags: runhidden
Filename: "{app}\{#AppName}.exe"; Description: "{cm:LaunchProgram,{#AppName}}"; Flags: nowait postinstall skipifsilent

;[UninstallRun] bat file to delete from registry and ftype and assoc