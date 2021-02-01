![img](https://user-images.githubusercontent.com/47640521/106484721-b571ab00-64d1-11eb-8edb-a9091830c552.jpg)


**Step 1. Add the JitPack repository to your build file**
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2. Add the dependency**
```
dependencies {
	        implementation 'com.github.uztechie:SimCard:1.0'
	}
```

**Add these permissions to your Manifest file**
```
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.CALL_PHONE"/>

```
**Usage of the Class**
```
SimCard simCard = new SimCard(this);
        simCard.setTitle("Choose simcard");
        simCard.applyUssd("*100");
```



