# Solstice Prayer Times

A modern, open-source Adhan (أذان) — prayer times application, built natively for Android with Jetpack Compose and Material 3 Expressive (Material You).

> Forked from [Al-Azan](https://github.com/meypod/al-azan-compose/) (originally a rewrite of the React Native [al-azan](https://github.com/meypod/al-azan/) app). This fork modernizes the UI with full Material You, adds 25+ adhan recitations, predictive back, adaptive navigation, and a redesigned onboarding.

## Features

* **Full Material You** — Material 3 Expressive with dynamic color, AMOLED black mode, custom seed color picker

* **25+ Adhan recitations** — from Masjid al-Haram, Masjid al-Aqsa, Al-Azhar, Süleymaniye, and more, with category filters and preview

* **Predictive back** — modern Android back gesture animations throughout

* **Adaptive navigation** — bottom bar on phones, navigation rail on tablets/foldables

* **Redesigned onboarding** — 3 streamlined steps instead of 7

* **Ad-Free**

* **No internet permission**: the app can never access the internet

* **Doesn't use any kind of trackers**

* **Open-source** (AGPL-3.0)

* You can search for your location offline Or use GPS

* **25+ bundled adhan recitations** organised by category (Mosques / Muezzins / Styles).
  Add custom audio or pick any sound from your device.

* **Adaptive navigation** — Navigation rail on tablets, bottom bar on phones

* **AMOLED true-black theme** for OLED battery savings

* **Custom seed color** override for Material You accents

* Select different Adhan audio for Fajr namaz

* In addition to five daily prayers, it has settings for Sunrise, Sunset, Midnight and Night Prayer (Tahajjud)

* Many options for Adhan (اذان) calculation

* Light, Dark, AMOLED Black and Classic themes, with Material You dynamic colors

* Monthly prayer times view

* Hide times you don't need

* Set reminders before or after a prayer time

* Automatically silence your phone after Adhan for a duration you choose (Do Not Disturb)

* View upcoming alarms, and skip the ones you don't want

* Homescreen and notification Widgets

* Qibla finder (map and compass)

* Qada counter

* Backup and restore your settings

* Is localized in English, Arabic, Persian, Turkish, Indonesian, French, Urdu, Hindi, German, Bosnian, Vietnamese, Bangla, Kiswahili

## Screenshots

<table style="width:100%">
  <tr>
    <td><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2-main-light.png"/></td>
    <td><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3-main-dark.png"/></td>
    <td><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5-schedule-muezzin-light.png"/></td>
    <td><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/8-notification-widget-light.png"/></td>
  </tr>
</table>

## How to build this project

Requirements:

* JDK 17+
* Android SDK (or just Android Studio)

1. Clone the project:

```bash
git clone git@github.com:meypod/al-azan.git
```

1. Build:

```bash
# debug build
./gradlew :app:assembleDebug

# release build
./gradlew :app:assembleRelease
```

Or open the project in Android Studio and run it from there.

To uninstall the app while keeping its data:

```bash
adb shell cmd package uninstall -k com.github.meypod.al_azan
```

## Translations

Help translate this app to your language on Weblate:

<a href="https://hosted.weblate.org/engage/al-azan/">
<img src="https://hosted.weblate.org/widget/al-azan/287x66-grey.png" alt="Translation status" />
</a>

## Thanks to

* [adhan-kotlin](https://github.com/batoulapps/adhan-kotlin), which provides the prayer times calculations used by this app.
* [Hotpot.ai](https://hotpot.ai/templates/google-play-feature-graphic), used to create the feature graphic.
* The many open-source projects that this app is built with.
* Everyone who has helped this project grow.

## License

This project is licensed under the [GNU Affero General Public License v3.0](LICENSE) (AGPL-3.0).

## Donate

Donations are appreciated. But I can only accept in crypto, here are my wallet addresses:

Bitcoin:
bc1q2y6fng33tzhc8qefsy2pht057q2rmfx09qyx6v

Ethereum:
0x1a1407f549cb52658a3ed6Eac9C5e850dED4DB2b

Solana:
CBK8ySxbVWrCkb1CQYoR1jYa4hEiMgpnVfJjGLCfBSJ1

Litecoin:
Lbgz2X6TG9ANLGamNpdmhyoMc4q4wBHaVQ

Tron:
THjtLAdihH57mbeaVmBfx3wAAXkpxAnqmJ

Bitcoin cash:
qqgjknfejs4zf4udsalsej2qkwt5es5ym5fwusgvx3

Monero:
4AdNVLzrPrUMkY7A4sXNGVDLgbC6HgNM1NCr8uptD9bhMwDhNshzrsaXD11d5kjzPkcGqCUz6rLRx4WSMKfjcbU346kMW8U
