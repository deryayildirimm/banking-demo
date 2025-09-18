# Banking System API (Spring Boot)

Bu proje, **müşteri (Customer)**, **hesap (Account)** ve **işlem (Transaction)** yönetimini içeren basit bir **bankacılık sistemi** uygulamasıdır.  
Java 21 ve Spring Boot 3 ile geliştirilmiş olup, **unit test coverage**'ı yüksektir.


![WorkInProgress](https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExam14YnljMTB4a2U0OHF5MjBwbWx2Ymt1OGpjNDc3am9wNzY0eXJidSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/6SPT4vjEWBPjECMXwr/giphy.gif)

> 🚧 **Not:** Bu proje hala **geliştirme aşamasındadır**.
---

## 🚀 Özellikler

- **Customer**: Soft delete (silme).
- **Account**: Hesap açma, bakiye kontrolü.
- **Transaction**: Para yatırma, para çekme, hesaplar arası transfer.
- **Exception Handling**: Özel exception sınıfları ile hataların yönetimi.
- **Unit Tests**: Servis katmanı için kapsamlı testler yazıldı.

---

## ⚙️ Kullanılan Teknolojiler

- **Java 21**
- **Spring Boot 3**
- **Spring Data JPA**
- **JUnit 5, Mockito**

---

## ▶️ Çalıştırma

Projeyi local’de çalıştırmak için:

```bash
git clone https://github.com/deryayildirimm/banking-demo.git
cd banking-demo 
./mvnw spring-boot:run