# Banking System API (Spring Boot)

Bu proje, **müşteri (Customer)**, **hesap (Account)** ve **işlem (Transaction)** yönetimini içeren basit bir **bankacılık sistemi** uygulamasıdır.  
Java 21 ve Spring Boot 3 ile geliştirilmiş olup, **unit test coverage**'ı yüksektir.

> 🚧 **Not:** Bu proje hala **geliştirme aşamasındadır**.
![WorkInProgress](https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExam14YnljMTB4a2U0OHF5MjBwbWx2Ymt1OGpjNDc3am9wNzY0eXJidSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/6SPT4vjEWBPjECMXwr/giphy.gif)

---

## 🚀 Özellikler

- **Customer**: Soft delete (silme).
- **Account**: Hesap açma, bakiye kontrolü.
- **Transaction**: Para yatırma, para çekme, hesaplar arası transfer.
- **Exception Handling**: Özel exception sınıfları ile hataların yönetimi.
- **Unit Tests**: Servis katmanı için kapsamlı testler yazıldı.

---

## ⚙️ Kullanılan Teknolojiler

- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- RabbitMQ
- Docker & Docker Compose
- Swagger (OpenAPI)
- JUnit & Mockito 
---
## ⚙️ Konfigürasyon

Projede güvenlik nedeniyle `application.properties` dosyası repoya dahil edilmemiştir.

Projeyi çalıştırmadan önce:

1. `src/main/resources/application-example.properties` dosyasını kopyalayın.
2. Aynı klasöre `application.properties` adıyla kaydedin.
3. İçindeki veritabanı ve RabbitMQ bilgilerini kendi ortamınıza göre düzenleyin.

Örnek:

```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/my-database
spring.datasource.username=my-user
spring.datasource.password=passw
spring.rabbitmq.host=rabbitmq
spring.rabbitmq.port=5672

```

---


## ▶️ Çalıştırma

Projeyi local’de çalıştırmak için:

```bash
git clone https://github.com/deryayildirimm/banking-demo.git
cd banking-demo 
docker-compose up --build

```

