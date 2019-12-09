[![Release](https://jitpack.io/v/AndreyRusSprint/ThreeStateSwitch.svg)](https://jitpack.io/#AndreyRusSprint/ThreeStateSwitch)

## ThreeStateSwitch

A simple three-state switch view for Android.

**Please Note:** This library has not been fully tested, so use with a little caution, and submit an issue or better, a pull request if you notice any issues at all.

**Project Setup and Dependencies**
- MinSDK 14

**Highlights**
- supports showing text on both sides of view
- supports customization in color or size


# Preview

![](https://raw.githubusercontent.com/AndreyRusSprint/ThreeStateSwitch/master/assets/demo.gif)

# Setup
## 1. Provide the gradle dependency

Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```
Add the dependency:
```gradle
dependencies {
	compile 'com.github.AndreyRusSprint:ThreeStateSwitch:1.1.0'
}
```

## 2. How to use

Add the ThreeStateSwitch in your layout file and customize it the way you like it.
```xml
<andreyrussprint.threestateswitch.ThreeStateSwitch
        android:id="@+id/threeStateSwitch"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:background_middle_color="@android:color/darker_gray"
        app:background_left_color="@android:color/holo_red_light"
        app:background_right_color="@android:color/holo_green_light"
        app:text_selected_left_color="@android:color/holo_red_light"
        app:text_selected_right_color="@android:color/holo_green_light"
        app:text_normal_size="36sp"
        app:text_selected_size="36sp"
	app:text_left="-"
	app:text_right="+"
/>
```
You can set a listener for state changes
```java
threeStateSwitch.setOnChangeListener(new ThreeStateSwitch.OnStateChangeListener() {
            @Override
            public void onStateChangeListener(SwitchStates currentState) {
                Toast.makeText(MainActivity.this, currentState.toString(), Toast.LENGTH_SHORT).show();
            }
});
```
Get the current state. 
```java
// state = LEFT_STATE  MIDDLE_STATE  RIGHT_STATE
threeStateSwitch.getState();
```
## attributes

| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
|background_left_color|Color|#cc2900|Switch background color when left state selected|
|background_right_color|Color|#5ab72e|Switch background color when right state selected|
|background_middle_color|Color|#bfbfbf|Switch background color when middle state selected|
|text_normal_color|Color|#646464|Text color when middle state selected|
|text_selected_left_color|Color|#cc2900|Left text color when left state selected|
|text_selected_right_color|Color|#5bb434|Right text color when right state selected|
|text_left|String||Text to the left of switch|
|text_right|String||Text to the right of switch|
|text_normal_size|Dp or Sp|16sp|Text size when middle state selected|
|text_selected_size|Dp or Sp|16sp|Text size when left or right state selected|

# Bugs and features

For bugs, feature requests, and discussion please use GitHub Issues.

# Participants

Developed by:
* Abbas Oveissi - [@abbas_oveissi](https://twitter.com/abbas_oveissi)

Improved by:
* AndreyRusSprint - [@andreyrussprint](https://t.me/andreyrussprint)

# License

    Copyright 2017 Abbas Oveissi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
