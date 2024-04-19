# XIVRusAutoUpdater
[![CI](https://github.com/rastorguevia/XIVRusAutoUpdater/actions/workflows/maven.yml/badge.svg)](https://github.com/rastorguevia/XIVRusAutoUpdater/actions/workflows/maven.yml)

## Использование

* Загрузите [TranslationUpdaterInstaller.exe](https://github.com/rastorguevia/XIVRusAutoUpdater/releases/) со страницы последнего релиза.
* Следуйте инструкциям, приведенным в установщике.
* ГОТОВО!

### Примечание
* После успешной установки, будет запрошена перезагрузка компьютера - не обязательный шаг, но необходимый для отображения кастомных иконок для файлов с расширением `.pmp`
* Если вам необходимо просто обновлять перевод до последней релизной версии - дальше можно не читать.

## Что может приложение
* ***Автозапуск.*** При установке будет создан ярлык в ``%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup``
* ***Автоматическое обновление перевода.*** Больше не нужно следить за фиксами при обновлении игры, или делать что то руками.
* ***Логирование действий приложения.*** Для отладки и нахождения проблем.
* ***Уведомления.*** Об обновлениях и ошибках будут приходить уведомления Windows в трей системы.

### Уведомления Windows
На уведомления добавлены ивенты:
* ***При обновлении*** - открывается сайт команды перевода.
* ***При обновлении из файла*** - открывается папка с копиями последних установленных файлов обновления с форматом имени `name_version_date_time.pmp`
* ***При ошибке*** - открывается `.log` файл для получения детальной информации и создания [задачи](https://github.com/rastorguevia/XIVRusAutoUpdater/issues)
на странице данного проекта. Перед созданием задачи ознакомьтесь с [правилами](https://github.com/rastorguevia/XIVRusAutoUpdater/blob/master/CONTRIBUTING.md).

### Функционал для переводчиков
* ***Обновление перевода из файла.*** При двойном нажатии на файл перевода, происходит обновление из файла.
* ***Конфигурационный файл.*** В папке с установленной программой присутствует `config.yaml` с комментариями для каждого параметра.

## Если собираем сами
* Java 22 
* Maven 3.9.6
* Inno Setup 5

#### Если хотим собрать без установщика, запускаем
```console
mvn clean install
```
#### Если хотим собрать с установщиком, 
cначала указываем [путь к ISCC.exe](https://github.com/rastorguevia/XIVRusAutoUpdater/blob/master/pom.xml) (компилятор Inno Setup)
```xml
  <properties>
    <!--Прописать путь к ISCC.exe-->
    <inno.exe.path>*\ISCC.exe</inno.exe.path>
  </properties>
```
затем указываем [путь к проекту](https://github.com/rastorguevia/XIVRusAutoUpdater/blob/master/installer/TranslationUpdaterInstaller.iss) на вашем компьютере
```iss
#define ResourceDir "C:\Users\***\IdeaProjects\ff14-ru-translation-auto-updater"
```
и наконец запускаем сборку с включеным профилем
```console
mvn clean install -P compile-installer
```