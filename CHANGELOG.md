## 1.0.4

* Updated AGP to 7 and kotlin to 1.7

## 1.0.3

* TwoPane no longer removes display features from child panes. This allows child panes to know if the device has display features.
* TwoPane has a new property called `allowedOverrides`. This allows for finer control over what layouts are possible on foldable devices.
* Extension method `hinge` was added to MediaQueryData. This allows developers to use `MediaQuery.of(context).hinge` for logic related to the hinge.

## 1.0.2+2

* Removed requirement for flutter 2.11

## 1.0.2

* Added compatibility with current flutter stable channel

## 1.0.1

* Added TwoPane widget

## 1.0.0+3

* Updated Readme

## 1.0.0+2

* `DualScreenInfo.hingeAngleEvents` now caches the last value
* Updated Readme

## 1.0.0

* `DualScreenInfo.hingeAngleEvents`: Broadcast stream of events from the device hinge angle sensor
* `DualScreenInfo.hasHingeAngleSensor`: Future returning true if the device has a hinge angle sensor
