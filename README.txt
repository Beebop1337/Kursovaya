Для корректной работы программы рекомендуется расположить папку со
сценариями в папке проекта(kursovaya), либо использовать уже готовую папку scenarios.
При запуске своих сценариев, не созданных с помощью GUI, требуется заменить исходную папку
на свою, но с таким же названием.
Jar файл лежит в папке build/libs/, перед запуском jar-файла требуется перебилдить его командой:
./gradlew shadowJar, тесты можно запускать как через intelij, так и через команду ./gradlew test.
Javadoc лежит в папке kursovaya/jdoc
Через ./gralew run программа не работает, запускть либо через интелидж, либо через shadowJar