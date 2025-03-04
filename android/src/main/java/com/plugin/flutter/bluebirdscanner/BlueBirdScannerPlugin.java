package com.plugin.flutter.bluebirdscanner;

import android.content.Context;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** BlueBirdScannerPlugin */
public class BlueBirdScannerPlugin implements MethodCallHandler, ScannerCallBack {

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    new BlueBirdScannerPlugin(registrar);
  }

  static final String _METHOD_CHANNEL = "bluebirdscanner";
  static final String _GET_PLATFORM_VERSION = "getPlatformVersion";
  static final String _INIT_SCANNER = "initScanner";
  static final String _START_SCANNER = "startScanner";
  static final String _RESUME_SCANNER = "resumeScanner";
  static final String _PAUSE_SCANNER = "pauseScanner";
  static final String _STOP_SCANNER = "stopScanner";
  static final String _SOFT_SCANNER_ON = "softScanOn";
  static final String _SOFT_SCANNER_OFF = "softScanOff";
  static final String _ON_DECODED = "onDecoded";
  static final String _ON_ERROR = "onError";


  private MethodChannel channel;
  private Context context;
  private BlueBirdScanner scanner;

  public BlueBirdScannerPlugin(Registrar registrar)
  {
    context = registrar.context();
    channel = new MethodChannel(registrar.messenger(), _METHOD_CHANNEL);
    channel.setMethodCallHandler(this);
  }

  private void scannerNotInitialized(Result result){
    result.error("Scanner has not been initialized.", null, null);
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    try
    {
      switch(call.method){
        case _GET_PLATFORM_VERSION:
          result.success(" Android " + android.os.Build.VERSION.RELEASE);
          break;

        case _INIT_SCANNER:
          try
          {
            scanner = BlueBirdModel.valueOf(String.valueOf(call.arguments)).getScanner(context);
            scanner.setScannerCallBack(this);
            result.success(true);
          }
          catch(Exception e)
          {
            e.printStackTrace();
            result.error(e.getMessage(), null, null);
          }
          break;

        case _START_SCANNER:
          if(scanner != null){
            scanner.startScanner();
            result.success(true);
          } else scannerNotInitialized(result);
          break;
        case _RESUME_SCANNER:
          if(scanner != null){
            scanner.resumeScanner();
            result.success(true);
          } else scannerNotInitialized(result);
          break;
        case _PAUSE_SCANNER:
          if(scanner != null){
            scanner.pauseScanner();
            result.success(true);
          } else scannerNotInitialized(result);
          break;
        case _STOP_SCANNER:
          if(scanner != null){
            scanner.stopScanner();
            result.success(true);
          } else scannerNotInitialized(result);
          break;
        case _SOFT_SCANNER_ON:
          if(scanner != null){
            scanner.softScanOn();
            result.success(true);
          } else scannerNotInitialized(result);
          break;
        case _SOFT_SCANNER_OFF:
          if(scanner != null){
            scanner.softScanOff();
            result.success(true);
          } else scannerNotInitialized(result);
          break;
        default:
          result.notImplemented();
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      result.error(e.getMessage(), null, null);
    }
  }

  /**
   * Called when decoder has successfully decoded the code
   * <br>
   * Note that this method always called on a worker thread
   *
   * @param code Encapsulates the result of decoding a barcode within an image
   */
  @Override
  public void onDecoded(String code)
  {
    channel.invokeMethod(_ON_DECODED, code);
  }

  /**
   * Called when error has occurred
   * <br>
   * Note that this method always called on a worker thread
   *
   * @param error Exception that has been thrown
   */
  @Override
  public void onError(Exception error)
  {
    channel.invokeMethod(_ON_ERROR, error.getMessage());
  }

}
