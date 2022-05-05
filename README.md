# Flutter Dual Screen

This contains Microsoft's offerings to streamline foldable and dual-screen development using Flutter. This plugin will work on any platform, but only Android actually has foldable and dual screen devices.

Flutter already has support for foldable and dual-screen devices in the form of [MediaQuery Display Features](https://docs.microsoft.com/en-us/dual-screen/flutter/mediaquery). In addition to that, this plugin offers:

- The [TwoPane widget](#twopane-widget), which relies on display features coming from MediaQuery.
- Access to the [hinge angle sensor data](#hinge-angle-sensor), which for performance concerns, was not included in MediaQuery.

# TwoPane Widget

This layout has two child panes, which can be shown side-by-side, above-and-below, or a single pane can be prioritized. The relative size of the two pane widgets can be adjusted proportionally; and on dual-screen devices the boundary snaps to the hinge area.

## TwoPane API

```dart
class TwoPane {
  const TwoPane({
    Widget startPane,
    Widget endPane,
    double paneProportion,
    TwoPanePriority panePriority,
    Axis direction,
    TextDirection? textDirection,
    VerticalDirection verticalDirection,
    EdgeInsets padding,
    Set<TwoPaneAllowedOverrides> allowedOverrides,
  });
}
```

Properties of TwoPane:

- `startPane` - Start pane, which can sit on the left for left-to-right layouts, or at the top for top-to-bottom layouts. If `panePriority` is `start` and there is no hinge, this is the only visible pane.
- `endPane` - End pane, which can sit on the right for left-to-right layouts, or at the bottom for top-to-bottom layouts. If `panePriority` is `end`, and there is no hinge, this is the only visible pane.
- `paneProportion` - Proportion of the screen occupied by the start pane. The end pane takes over the rest of the space. A value of 0.5 will make the two panes equal. This property is ignored for displays with a hinge, in which case each pane takes over one screen.
- `panePriority` - Whether to show only one the start pane, end pane, or both. This property is ignored for displays with a hinge, in which case `both` panes are visible.
- `direction` - Whether to stack the two panes verticaly or horizontaly, similar to [Flex direction](https://api.flutter.dev/flutter/widgets/Flex/direction.html). This property is ignored for displays with a hinge, in which case the direction is `horizontal` for vertical hinges and `vertical` for horizontal hinges.
- `textDirection` - When panes are laid out horizontally, this determines which one goes on the left. Behaves the same as [Flex textDirection](https://api.flutter.dev/flutter/widgets/Flex/textDirection.html)
- `verticalDirection` - When panes are laid out vertically, this determines which one goes at the top. Behaves the same as [Flex verticalDirection](https://api.flutter.dev/flutter/widgets/Flex/verticalDirection.html)
- `padding` - The padding between TwoPane and the edges of the screen. If there is spacing between TwoPane and the root MediaQuery, `padding` is used to correctly align the two panes to the hinge.
- `allowedOverrides` - The parameters that TwoPane is allowed to override when a separating display feature is found. By default, it contains `paneProportion`, `direction` and `panePriority`, allowing all possible overrides.

> Most of the parameters provided to TwoPane are ignored when the device has a hinge. This means that you can focus on how the layout works on large screens like tablets and desktops, while also having it adapt well to the dual-screen form factor by default.

## TwoPane example

```dart
Widget build(BuildContext context) {
  return TwoPane(
    startPane: _widgetA(),
    endPane: _widgetB(),
    paneProportion: 0.3,
    panePriority: MediaQuery.of(context).size.width > 500 ? TwoPanePriority.both :TwoPanePriority.pane1,
  );
}
```

This sample code produces the results at the beginning of this article:

- On **Surface Duo**, widget A and widget B both take one screen.
  ![Flutter TwoPaneView on Surface Duo](https://github.com/microsoft/flutter-dualscreen/blob/main/images/twopaneview-surfaceduo-simple.png)
- On a **tablet** or **desktop**, widget A takes 30% of the screen while widget B takes the remaining 70%.
  ![Flutter TwoPaneView on desktop](https://github.com/microsoft/flutter-dualscreen/blob/main/images/twopaneview-desktop-simple.png)
- On a **small phone** which is less than 500 logical pixels wide, only widget A is visible.
  ![Flutter TwoPaneView on a candybar phone](https://github.com/microsoft/flutter-dualscreen/blob/main/images/twopaneview-phone-simple.png)

# Hinge angle sensor

Foldable and dual-screen devices have a hinge between the two moving parts of the screen. This hinge has a sensor, reporting the angle between the parts of the screen. For example, when these parts lay flat to form a continuous surface, the hinge angle reports a 180 deg angle.

> The hinge angle is used to determine the [device posture](https://developer.android.com/guide/topics/ui/foldables#postures). If you're looking to write code that depends on the device posture, we recommend using the functionality provided by [MediaQuery Display Features](https://docs.microsoft.com/en-us/dual-screen/flutter/mediaquery) instead.

## Hinge angle API

To use this plugin, add `dual_screen` as a dependency in your pubspec.yaml file.

This will allow you to import DualScreenInfo `import 'package:dual_screen/dual_screen.dart';`

DualScreenInfo exposes 2 static properties:

- `hingeAngleEvents`: Broadcast stream of events from the device hinge angle sensor. If the device is not equipped with a hinge angle sensor, the stream produces no events.
- `hasHingeAngleSensor`: Future returning true if the device has a hinge angle sensor. Alternatively, if your app already uses `MediaQuery.displayFeatures` or `MediaQuery.hinge` to adapt to foldable or dual-screen form factors, you can safely assume the hinge angle sensor exists and that `hingeAngleEvents` produces usable values.

## Hinge angle example

```dart
import 'package:dual_screen/dual_screen.dart';

DualScreenInfo.hingeAngleEvents.listen((double hingeAngle) {
  print(hingeAngle);
});

DualScreenInfo.hasHingeAngleSensor.then((bool hasHingeSensor) {
  print(hasHingeSensor);
});
```

## Testing

Examples on how to mock the hinge angle or display features can be found in the [test](https://github.com/microsoft/flutter-dualscreen/tree/main/test) folder.

Testing the hinge angle sensor functionality can be done using the [Surface Duo emulator](https://docs.microsoft.com/en-us/dual-screen/android/emulator/get-started) or one of the [foldable emulators available in Android Studio](https://developer.android.com/guide/topics/ui/foldables#emulators). Both emulators provide a hinge angle virtual sensor. The Surface Duo emulator is the only one with two separate screens.

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