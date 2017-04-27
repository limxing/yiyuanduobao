#---------------------------------1.实体类---------------------------------

-keep class com.yuyou.yiyuanduobao.bean.** { *; }

#-------------------------------------------------------------------------

#---------------------------------Bugly---------------------------------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#---------------------------------1.实体类---------------------------------
-dontwarn com.unicom.**
-keep class com.unicom.**{*;}
######--butterknife--#######
-keep class butterknife.** { *; }
 -dontwarn butterknife.internal.**
 -keep class **$$ViewBinder { *; }
 -keepclasseswithmembernames class * {
  @butterknife.* <fields>;
 }
 -keepclasseswithmembernames class * {
 @butterknife.* <methods>;
 }

#### 短信验证 ####
 -keep class cn.smssdk.**{*;}
 -keep class com.mob.**{*;}
 -dontwarn com.mob.**
 -dontwarn cn.smssdk.**

#### 后端云 ####
 -ignorewarnings

 -keepattributes Signature,*Annotation*

 # keep BmobSDK
 -dontwarn cn.bmob.v3.**
 -keep class cn.bmob.v3.** {*;}

 # 确保JavaBean不被混淆-否则gson将无法将数据解析成具体对象
 -keep class * extends cn.bmob.v3.BmobObject {
     *;
 }
 -keep class com.example.bmobexample.bean.BankCard{*;}
 -keep class com.example.bmobexample.bean.GameScore{*;}
 -keep class com.example.bmobexample.bean.MyUser{*;}
 -keep class com.example.bmobexample.bean.Person{*;}
 -keep class com.example.bmobexample.file.Movie{*;}
 -keep class com.example.bmobexample.file.Song{*;}
 -keep class com.example.bmobexample.relation.Post{*;}
 -keep class com.example.bmobexample.relation.Comment{*;}

 # keep BmobPush
 -dontwarn  cn.bmob.push.**
 -keep class cn.bmob.push.** {*;}

 # keep okhttp3、okio
 -dontwarn okhttp3.**
 -keep class okhttp3.** { *;}
 -keep interface okhttp3.** { *; }
 -dontwarn okio.**

 # keep rx
 -dontwarn sun.misc.**
 -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
  long producerIndex;
  long consumerIndex;
 }
 -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
  rx.internal.util.atomic.LinkedQueueNode producerNode;
 }
 -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
  rx.internal.util.atomic.LinkedQueueNode consumerNode;
 }

 # 如果你需要兼容6.0系统，请不要混淆org.apache.http.legacy.jar
 -dontwarn android.net.compatibility.**
 -dontwarn android.net.http.**
 -dontwarn com.android.internal.http.multipart.**
 -dontwarn org.apache.commons.**
 -dontwarn org.apache.http.**
 -keep class android.net.compatibility.**{*;}
 -keep class android.net.http.**{*;}
 -keep class com.android.internal.http.multipart.**{*;}
 -keep class org.apache.commons.**{*;}
 -keep class org.apache.http.**{*;}
 #### butterknife ####
    -keep class butterknife.** { *; }
      -dontwarn butterknife.internal.**
      -keep class **$$ViewBinder { *; }

      -keepclasseswithmembernames class * {
          @butterknife.* <fields>;
      }

      -keepclasseswithmembernames class * {
          @butterknife.* <methods>;
      }
      #####FastJson####
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }

# #  ######## greenDao混淆  ##########
# # -------------------------------------------
-keep class de.greenrobot.dao.** {*;}
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static Java.lang.String TABLENAME;
}
-keep class **$Properties