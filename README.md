# İzmir ESHOT Android

İzmir Büyükşehir Belediyesi ESHOT otobüs duraklarını ve yaklaşan otobüsleri gösteren Android uygulaması.

## Özellikler

- 🗺️ Google Maps üzerinde tüm ESHOT durakları
- 🚌 Durağa yaklaşan otobüsleri gerçek zamanlı görüntüleme
- 📍 Konum tabanlı durak bulma
- 🎨 Material Design 3 modern arayüz

## Teknolojiler

- **Kotlin** + **Jetpack Compose**
- **Clean Architecture** + **MVVM**
- **Hilt** - Dependency Injection
- **Retrofit + Moshi** - Networking
- **Google Maps Compose** - Harita
- **Coroutines + StateFlow** - Async

## Kurulum

### Ön Gereksinimler
- Android Studio Hedgehog+
- JDK 17+
- Google Maps API Key

### Adımlar

1. Projeyi klonlayın:
   ```bash
   git clone https://github.com/YOUR_USERNAME/IzmirESHOTAndroid.git
   ```

2. `secrets.properties` dosyası oluşturun:
   ```properties
   MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
   ```

3. Android Studio'da açın ve çalıştırın.

## GitHub Actions CI/CD

Proje otomatik build ve release için yapılandırılmıştır:

### Otomatik Tetikleme
- `v*` formatında tag push edildiğinde (örn: `v1.0.0`)
- Main branch'e push yapıldığında otomatik tag oluşturulur

### Manuel Tetikleme
Actions → Build & Release APK → Run workflow

### Gerekli GitHub Secrets

| Secret | Açıklama |
|--------|----------|
| `MAPS_API_KEY` | Google Maps API anahtarı |

> `GITHUB_TOKEN` otomatik olarak sağlanır.

## Veri Kaynağı

[İzmir Açık Veri Portalı](https://acikveri.bizizmir.com/)

## Lisans

GNU General Public License v3.0
