## Journey
 Автоматизация тестирования комплексного сервиса, взаимодействующего с СУБД и API Банка.

### Начало работы
1. Создать Gradle проект и сохранить на вашем ПК в папке.
2. В папке проекта правой кнопкой мыши открыть дополнителные параметры и выбрать инструмент Open Git Bash here 
3. С помощью команды `git init` создать локальный репозиторий.
4. С помощью команды `git clone https://github.com/ElenaVSkr/New-journey.git` клонировать проект с удаленного репозитория.
   
### Для формирования окружения необходимы:
* IntelliJ IDEA
* GIT
* NodeJS
* Docker 
* Google Chrome

### Шаги для запуска
1. Запустить Docker Desktop.
2. Открыть проект в IntelliJ IDEA.
3. В терминале с помощью команды `docker-compose up` запустить контейнеры.
4. Запустить сервис с указанием пути к базе данных командой для mysql:
* java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar 
для postgresql:
* java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar       
5. Проверить работоспособность сервиса (http://localhost:8080/).
6. В терминале запустить тесты с помощью команды для mysql:  
* ./gradlew clean test "-Ddb.url=jdbc:mysql://localhost:3306/app"
 для postgresql:
* ./gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"         

### Документация 
* [План тестирования](documentation/Plan.md)
* [Проделанная работа](documentation/Summary.md)
* [Отчет](documentation/Report.md)