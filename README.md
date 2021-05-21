این اپ به عنوان تسک کافه بازار برای استخدام توسعه داده شده است
شرح زبان و تکنولوژی های استفاده شده به شرح زیر است:

kotlin language
mvvm architecture
rxjava and livedata
kotlin coroutines for multithreading
Room library for caching the data
retrofit for network calls
and other components such as butterknife, calligraphy and etc... .

توسعه‌ی تدریجی بر روی گیت انجام شده است.

روش کار به صورت زیر از ابتدا تا انتها بوده است:

در این موارد که در زیر لیست شده اند همان طور که مشاهده می شود سعی شده است استخوان بندی تک تک لایه ها تکمیل شوند و هر کدام فقط وظیفه ی خودشان را انجام دهند و در انتها به لایه ی ویو برسیم.

۱. آماده‌سازی کلیات پروژه و انتخاب معماری مناسب

۲. اضافه کردن کامپوننت‌های مورد نیاز جهت پیشبرد تسک

۳. پکیج‌بندی مناسب کلاس‌ها و ایجاد وابستگی بین آن ها با dagger

۴. بررسی foursquare api و ایجاد مدل‌های آن و لایه شبکه

۵. observing location change using Livedata

۶. retrofit call foursquare interface using rxjava in Repository

۷. adding viewmodel

۸. foursquare api test response failure and success livedata handling

۹. adding room and model, DAO and other configs

۱۰. adding sharedprefs for caching last location, offset and last updated time

۱۱. پیاده سازی منطق اصلی برنامه در ویومدل و ریپازیتوری

۱۲. داخل کردن room  در منطق اصلی

۱۳. نمایش جزئیات مکان

۱۴. تست و ریفکتور

فایل apk در روت اصلی پروژه موجود است

سپاس از این تسک کامل و جامع
