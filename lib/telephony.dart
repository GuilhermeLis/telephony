import 'dart:async';

import 'package:flutter/services.dart';

class Telephony {
  static const MethodChannel _channel = MethodChannel('telephony');

  static Future<Map<String, dynamic>?> get telephony async {
    final Map<String, dynamic>? version =
        await _channel.invokeMapMethod('getPlatformVersion');

    return version;
  }
}
