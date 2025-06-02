# RJ Downloader - Musiqi və Video Yükləyici

**Versiya:** 1.0 Beta

[![RJ Downloader Loqosu](https://placehold.co/600x300/5e17eb/ffffff?text=RJ+Downloader&font=arial)](https://ruhidjavadoff.site/app/md/)

**RJ Downloader** müxtəlif platformalardan musiqi və videoları asanlıqla yükləmək üçün hazırlanmış bir Android tətbiqidir. Şəxsi istifadəniz üçün nəzərdə tutulmuşdur.

## 📥 Yükləmə

Ən son APK faylını aşağıdakı linkdən yükləyə bilərsiniz:
[**RJ Downloader APK Yüklə**](https://ruhidjavadoff.site/app/md/)

## 🛡️ Məxfilik Siyasəti

Proqramımızın məxfilik siyasəti ilə buradan tanış ola bilərsiniz:
[Məxfilik Siyasəti](https://ruhidjavadoff.site/app/md/gizliliksertleri)

## ✨ Xüsusiyyətlər (Hazırkı Versiyada)

* **Çoxsaylı Platforma Dəstəyi (Planlaşdırılır):** YouTube kimi platformalardan link vasitəsilə məzmun analizi (hazırda YouTube üçün `NewPipeExtractor` ilə ilkin inteqrasiya mövcuddur).
* **İstifadəçi Dostu İnterfeys:**
    * **Əsas Səhifə:** Link daxil etmə xanası və sürətli axtarış üçün naviqasiya tabları (Axtar, YouTube, Musiqi, Digər).
    * **Axtarış Detalları Səhifəsi:** Link daxil etdikdən və ya axtarış ikonuna kliklədikdən sonra açılan xüsusi səhifə. Bu səhifədə yeni axtarış xanası, populyar sayt ikonları (YouTube, Instagram, Facebook, TikTok, WhatsApp, X) və "Sayt Artır" düyməsi (gələcək funksiya) mövcuddur.
    * **Media Təfərrüatları Səhifəsi:** Uğurlu link analizindən sonra açılır. Burada medianın adı (thumbnail gələcəkdə əlavə olunacaq) və mövcud yükləmə formatları (audio/video) siyahı şəklində göstərilir.
    * **Yükləmələr Səhifəsi:** Davam edən, tamamlanmış və xətalı yükləmələri izləmək və idarə etmək üçün xüsusi səhifə. Fayl adı, yüklənmə faizi, statusu göstərilir. Yükləmələri ləğv etmək, tamamlanmışları açmaq, xətalıları yenidən cəhd etmək və ya silmək mümkündür.
    * **Ayarlar Səhifəsi:** Hələlik boşdur, gələcəkdə tənzimləmələr üçün istifadə olunacaq.
* **Yükləmə İdarəetməsi:**
    * Android-in daxili `DownloadManager`-i ilə etibarlı yükləmə.
    * Yükləmə seçimlərindən (keyfiyyət, format) birini seçmək imkanı.
    * Yükləmə prosesini bildiriş panelində izləmə.
* **Fayl Açma:**
    * Yüklənmiş video faylları cihazın standart video pleyerində açılır.
    * Digər fayl növləri (məsələn, audio) üçün istifadəçinin "SadMusiqialar" tətbiqi (`com.ruhidjavadoff.sadmusiqialar`) yoxlanılır; əgər yüklüdürsə, həmin tətbiq açılır, əks halda Play Store səhifəsinə yönləndirilir.
* **Axtarış Tarixçəsi:** Daxil edilən linklər yadda saxlanılır (gələcəkdə UI-da göstəriləcək).
* **Naviqasiya:** Səhifələr arası rahat keçidlər və geri düyməsi ilə idarəetmə.

## 🖼️ Ekran Görüntüləri (Tezliklə)

*(Buraya proqramınızın bir neçə cəlbedici ekran görüntüsünü əlavə edə bilərsiniz)*
`[Əsas Səhifənin Ekran Görüntüsü]`
`[Media Təfərrüatları Səhifəsinin Ekran Görüntüsü]`
`[Yükləmələr Səhifəsinin Ekran Görüntüsü]`

## 🛠️ İstifadə Olunan Texnologiyalar

* **Proqramlaşdırma Dili:** Kotlin
* **İnkişaf Mühiti:** Android Studio
* **Əsas Kitabxanalar:**
    * Android Jetpack (Lifecycle, Fragment, RecyclerView və s.)
    * **NewPipeExtractor:** YouTube və digər platformalardan məlumat çıxarmaq üçün istifadə olunur. Bu kitabxana **GNU General Public License v3.0** altında lisenziyalıdır. Ətraflı məlumat və lisenziya şərtləri üçün [NewPipeExtractor GitHub](https://github.com/TeamNewPipe/NewPipeExtractor) səhifəsinə baş çəkin.
    * OkHttp: Şəbəkə sorğuları üçün.
    * Gson: JSON emalı üçün.
    * Kotlin Coroutines: Asinxron əməliyyatlar üçün.

## 📜 Lisenziya

Bu proqram, **RJ Downloader - Musiqi və Video**, aşağıdakı şərtlərlə istifadəyə açıqdır:

Bu proqram **MIT Lisenziyası** altında təqdim edilir. Lisenziyanın tam mətni üçün `LICENSE` faylına baxın (əgər layihədə varsa).
Əsas şərt: Bu proqramın hər hansı bir törəmə işində və ya paylanmasında **orijinal müəllifin (Ruhid Javadov) adı qeyd edilməlidir.**


MIT License

Copyright (c) 2024 Ruhid Javadov ruhidjavadoff@gmail.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

ATTRIBUTION: Any redistribution or derivation of this Software must include
prominent attribution to the original author, Ruhid Javadov.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


## 💖 Dəstək Olun

Proqramın inkişafına dəstək olmaq istəyirsinizsə, aşağıdakı üsullarla ianə edə bilərsiniz:

* **Sayt Üzərindən:** [ruhidjavadoff.site/donate/](https://ruhidjavadoff.site/donate/)
* **PayPal:** `ruhidjavadoff@gmail.com`

Dəstəyiniz üçün təşəkkürlər!

## 🚀 Gələcək Planlar (Növbəti Addımlar)

* **Ayarlar Bölməsinin Tamamlanması:** Yükləmə qovluğu seçimi, tema dəyişikliyi və digər tənzimləmələr.
* **Digər Saytlardan Yükləmə:** `NewPipeExtractor`-un dəstəklədiyi digər platformalar üçün dəstəyin artırılması.
* **WhatsApp Status Yükləmə:** WhatsApp statuslarını yükləmə funksiyasının əlavə edilməsi.
* **"Son Axtarışlar" Bölməsinin UI-da Göstərilməsi:** Axtarış tarixçəsinin istifadəçi üçün əlçatan edilməsi.
* **Media Təfərrüatları Səhifəsində Thumbnail Göstərilməsi.**
* **Çox Hissəli Yükləmə (Multi-part Downloading):** Yükləmə sürətini artırmaq üçün araşdırılacaq.
* **UI/UX Təkmilləşdirmələri.**

## 📱 Digər Proqramlarım

Hazırladığım digər Android tətbiqləri ilə buradan tanış ola bilərsiniz:
[ruhidjavadoff.site/app](https://ruhidjavadoff.site/app)

