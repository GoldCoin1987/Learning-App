# kotlinx.serialization keeps generated serializers; keep @Serializable metadata.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class com.studyforge.app.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.studyforge.app.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}
