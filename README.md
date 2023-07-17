To launch the project, you will need:
Spring Boot 3+
Java: JDK 17
PostgreSQL 15+

Useful links:

JDK 17 - https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html

PostgreSQL - https://www.postgresql.org/download/

When installing PostgreSQL, the default user is not recommended for use in projects. However, for application testing purposes, you can use the default user "postgres," which usually has the password set as "postgres" for convenience.

Before the first project launch, you need to generate the database. You can use the following script:

psql -U your_username -c "CREATE DATABASE currency_exchange;"

psql -U your_username -d currency_exchange -c "CREATE TABLE exchange_rate (id SERIAL PRIMARY KEY, currency_code VARCHAR(5) NOT NULL, cost NUMERIC(19,8) NOT NULL);"

Make sure to replace "your_username" with your actual PostgreSQL username.

Don't forget to specify your username and password in the application.yml file:
spring:
r2dbc:
username: ${USER_NAME}
password: ${PASSWORD}

Next, you need to obtain an API key from the resource https://openexchangerates.org/.
To do this, you need to register by following this link - https://openexchangerates.org/signup/free. The link leads to the free version, but you can choose any convenient pricing plan.

Insert the API key into the application.yml file:
rate-source:
API-key: ${API_KEY}

You can also set the data update frequency in the database from the specified resource.
To do this, set the value in milliseconds for the "delay" property in application.yml:
rate-source:
delay: ${MILLISECONDS}

After launching the project, the database will be automatically populated and updated every "delay" milliseconds.


# exchange-rate
Тестове завдання:

1.Написати сервіс, який має ходити на зовнішне API,отримувати курс валют та зберігати в базі, з n періодичністю оновлювати данні курси.

2.Для отримування курсів з данного сервіса використоувати REST API.

 - написати ендпоінт для отримання всіх курсів
 - написати ендпоінт для отримання доступних курсів по конкретній валюті
 - написати ендпоінт для пари, наприклад USD -> UAH

4.Покрити тестами. 

5.Код викласти на git hub.

Сервіс має бути написан на springframework з non blocking підходом
за допомогою Spring WebFlux.
