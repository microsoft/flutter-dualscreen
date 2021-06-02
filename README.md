# Flutter Dual Screen

This contains Microsoft's offerings to streamline dual-screen and foldable development using Flutter. The plugin will work on any platform, but only Android actually has dual screen and foldable devices.

Flutter already has support for foldable and dual-screen devices in the form of [MediaQuery Display Features](https://docs.microsoft.com/en-us/dual-screen/flutter/mediaquery) and the [TwoPane widget](https://docs.microsoft.com/en-us/dual-screen/flutter/twopane-widget). It does not provide a way to access the hinge angle sensor data.

# Hinge angle sensor

Foldable and dual-screen devices have a hinge between the two moving parts of the screen. This hinge has a sensor, reporting the angle between the parts of the screen. For example, when these parts lay flat to form a continuous surface, the hinge angle reports a 180 deg angle.

> The hinge angle is used to determine the [device posture](https://developer.android.com/guide/topics/ui/foldables#postures). If you're looking to write code that depends on the device posture, we recommend using the functionality provided by [MediaQuery Display Features](https://docs.microsoft.com/en-us/dual-screen/flutter/mediaquery) instead.

## Usage

To use this plugin, add `dual_screen` as a dependency in your pubspec.yaml file.

This will allow you to import DualScreenInfo `import 'package:dual_screen/dual_screen.dart';`

DualScreenInfo exposes 2 static properties:

- `hingeAngleEvents`: Broadcast stream of events from the device hinge angle sensor. If the device is not equipped with a hinge angle sensor, the stream produces no events.
- `hasHingeAngleSensor`: Future returning true if the device has a hinge angle sensor. Alternatively, if your app already uses `MediaQuery.displayFeatures` or `MediaQuery.hinge` to adapt to foldable or dual-screen form factors, you can safely assume the hinge angle sensor exists and that `hingeAngleEvents` produces usable values.

```dart
import 'package:dual_screen/dual_screen_info.dart';

DualScreenInfo.hingeAngleEvents.listen((double hingeAngle) {
  print(hingeAngle);
});

DualScreenInfo.hasHingeAngleSensor.then((bool hasHingeSensor) {
  print(hasHingeSensor);
});
```

## Testing

If you want to test the hinge angle sensor functionality you can either use the [Surface Duo emulator](https://docs.microsoft.com/en-us/dual-screen/android/emulator/get-started) or one of the [foldable emulators available in Android Studio](https://developer.android.com/guide/topics/ui/foldables#emulators). Both emulators provide a hinge angle virtual sensor. The Surface Duo emulator is the only one with two separate screens.

![Surface Duo Emulator Hinge Sensor](https://github.com/microsoft/flutter-dualscreen/blob/main/images/emulator_hinge_angle.jpg)

## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Trademarks

This project may contain trademarks or logos for projects, products, or services. Authorized use of Microsoft 
trademarks or logos is subject to and must follow 
[Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).
Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion or imply Microsoft sponsorship.
Any use of third-party trademarks or logos are subject to those third-party's policies.

<!-- ## Special thanks -->

<!-- The [dual_screen](https://pub.dev/packages/dual_screen) package was previously owned by [Built to Roam](https://pub.dev/publishers/builttoroam.com/) and it initially offered a way to know if your app is running on a dual screen device and if it is spanned across both screens or not. We would like to thank [Nick Randolph](https://github.com/nickrandolph), [Michael Bui](https://github.com/MaikuB) and [Brett Lim](https://github.com/Brett09) for transferring ownership of `dual_screen` to Microsoft. -->