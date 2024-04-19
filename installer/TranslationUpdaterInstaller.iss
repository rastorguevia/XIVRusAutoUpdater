#ifndef Version
  #define Version = '0.0.0.0';
#endif

#define AppName "TranslationUpdater"
#define ResourceDir "C:\Users\rasto\IdeaProjects\ff14-ru-translation-auto-updater"

[Setup]
AppId={{45C180A9-C819-4472-AE05-5B4854D40499}
AppName={#AppName}
AppVersion={#Version}
AppVerName={#AppName} {#Version}
VersionInfoVersion={#Version}
DefaultDirName={pf}\{#AppName}
DefaultGroupName={#AppName}
AllowNoIcons=yes
LicenseFile={#ResourceDir}\installer\license.txt
InfoBeforeFile={#ResourceDir}\installer\before.txt
InfoAfterFile={#ResourceDir}\installer\after.txt
SetupIconFile={#ResourceDir}\target\classes\favicon.ico
WizardImageFile={#ResourceDir}\installer\WizardImageFile.bmp
WizardSmallImageFile={#ResourceDir}\installer\WizardSmallImageFile.bmp
OutputDir={#ResourceDir}\target
OutputBaseFilename={#AppName}Installer

Compression=lzma
SolidCompression=yes
DisableProgramGroupPage=true
AlwaysRestart=yes



[Languages]
Name: "russian"; MessagesFile: "compiler:Languages\Russian.isl"

[Files]
Source: "{#ResourceDir}\target\{#AppName}.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#ResourceDir}\target\README.md"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#ResourceDir}\target\config.yaml"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#ResourceDir}\target\win32\*"; DestDir: "{app}\win32"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{userstartup}\{#AppName}"; Filename: "{app}\{#AppName}.exe"; WorkingDir: "{app}"

[Run]
Filename: "{app}\win32\associatepmp.bat"; Parameters: "install"; Flags: runhidden
Filename: "{app}\{#AppName}.exe"; Description: "{cm:LaunchProgram,{#AppName}}"; Flags: nowait postinstall skipifsilent

[UninstallRun]
Filename: "{app}\win32\deletereg.bat"; Flags: runhidden