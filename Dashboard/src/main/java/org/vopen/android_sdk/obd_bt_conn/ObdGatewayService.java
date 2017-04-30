package org.vopen.android_sdk.obd_bt_conn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.inject.Inject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import pt.lighthouselabs.obd.commands.ObdCommand;
import pt.lighthouselabs.obd.commands.protocol.EchoOffObdCommand;
import pt.lighthouselabs.obd.commands.protocol.LineFeedOffObdCommand;
import pt.lighthouselabs.obd.commands.protocol.ObdResetCommand;
import pt.lighthouselabs.obd.commands.protocol.SelectProtocolObdCommand;
import pt.lighthouselabs.obd.commands.protocol.TimeoutObdCommand;
import pt.lighthouselabs.obd.commands.temperature.AmbientAirTemperatureObdCommand;
import pt.lighthouselabs.obd.enums.ObdProtocols;
//import pt.lighthouselabs.obd.reader.R;
//import pt.lighthouselabs.obd.reader.activity.ConfigActivity;
//import pt.lighthouselabs.obd.reader.activity.MainActivity;
import org.vopen.android_sdk.obd_bt_conn.ObdCommandJob.ObdCommandJobState;
import org.vopen.android_sdk.obd_bt_conn.ObdConfig;

/**
 * This service is primarily responsible for establishing and maintaining a
 * permanent connection between the device where the application runs and a more
 * OBD Bluetooth interface.
 * <p/>
 * Secondarily, it will serve as a repository of ObdCommandJobs and at the same
 * time the application state-machine.
 */
public class ObdGatewayService extends AbstractGatewayService {

  public static final String BLUETOOTH_LIST_KEY = "bluetooth_list_preference";
  public static final String UPLOAD_URL_KEY = "upload_url_preference";
  public static final String UPLOAD_DATA_KEY = "upload_data_preference";
  public static final String OBD_UPDATE_PERIOD_KEY = "obd_update_period_preference";
  public static final String VEHICLE_ID_KEY = "vehicle_id_preference";
  public static final String ENGINE_DISPLACEMENT_KEY = "engine_displacement_preference";
  public static final String VOLUMETRIC_EFFICIENCY_KEY = "volumetric_efficiency_preference";
  public static final String IMPERIAL_UNITS_KEY = "imperial_units_preference";
  public static final String COMMANDS_SCREEN_KEY = "obd_commands_screen";
  public static final String PROTOCOLS_LIST_KEY = "obd_protocols_preference";
  public static final String ENABLE_GPS_KEY = "enable_gps_preference";
  public static final String GPS_UPDATE_PERIOD_KEY = "gps_update_period_preference";
  public static final String GPS_DISTANCE_PERIOD_KEY = "gps_distance_period_preference";
  public static final String ENABLE_BT_KEY = "enable_bluetooth_preference";
  public static final String MAX_FUEL_ECON_KEY = "max_fuel_econ_preference";
  public static final String CONFIG_READER_KEY = "reader_config_preference";
  private static final String TAG = ObdGatewayService.class.getName();
  /*
   * http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
   * #createRfcommSocketToServiceRecord(java.util.UUID)
   *
   * "Hint: If you are connecting to a Bluetooth serial board then try using the
   * well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if you
   * are connecting to an Android peer then please generate your own unique
   * UUID."
   */
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private final IBinder binder = new ObdGatewayServiceBinder();
  @Inject
  SharedPreferences prefs;

  private BluetoothDevice dev = null;
  private BluetoothSocket sock = null;
  private BluetoothSocket sockFallback = null;

  public static ArrayList<ObdCommand> getObdCommands(SharedPreferences prefs) {
    ArrayList<ObdCommand> cmds = ObdConfig.getCommands();
    ArrayList<ObdCommand> ucmds = new ArrayList<ObdCommand>();
    for (int i = 0; i < cmds.size(); i++) {
      ObdCommand cmd = cmds.get(i);
      boolean selected = prefs.getBoolean(cmd.getName(), true);
      if (selected)
        ucmds.add(cmd);
    }
    return ucmds;
  }
  public void startService() throws IOException {
    Log.d(TAG, "Starting service..");

    // get the remote Bluetooth device
    final String remoteDevice = prefs.getString(BLUETOOTH_LIST_KEY, null);
    if (remoteDevice == null || "".equals(remoteDevice)) {
      Toast.makeText(ctx, "No Bluetooth device selected", Toast.LENGTH_LONG).show();

      // log error
      Log.e(TAG, "No Bluetooth device has been selected.");

      // TODO kill this service gracefully
      stopService();
      throw new IOException();
      } else {

    final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    dev = btAdapter.getRemoteDevice(remoteDevice);


    /*
     * Establish Bluetooth connection
     *
     * Because discovery is a heavyweight procedure for the Bluetooth adapter,
     * this method should always be called before attempting to connect to a
     * remote device with connect(). Discovery is not managed by the Activity,
     * but is run as a system service, so an application should always call
     * cancel discovery even if it did not directly request a discovery, just to
     * be sure. If Bluetooth state is not STATE_ON, this API will return false.
     *
     * see
     * http://developer.android.com/reference/android/bluetooth/BluetoothAdapter
     * .html#cancelDiscovery()
     */
    Log.d(TAG, "Stopping Bluetooth discovery.");
    btAdapter.cancelDiscovery();

    //TODO remove notification - not needed
    //showNotification("Tap to open OBD-Reader", "Starting OBD connection..", R.drawable.ic_launcher, true, true, false);

    try {
      startObdConnection();
    } catch (Exception e) {
      Log.e(
          TAG,
          "There was an error while establishing connection. -> "
              + e.getMessage()
      );

      // in case of failure, stop this service.
      stopService();
      throw new IOException();
    }
    }

     /*
     * TODO clean
     *
     * Get more preferences
     */
          boolean imperialUnits = prefs.getBoolean(IMPERIAL_UNITS_KEY,
                  false);
          ArrayList<ObdCommand> cmds = getObdCommands(prefs);

  }

  /**
   * Start and configure the connection to the OBD interface.
   * <p/>
   * See http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3/18786701#18786701
   *
   * @throws IOException
   */
  private void startObdConnection() throws IOException {
    Log.d(TAG, "Starting OBD connection..");
    isRunning = true;
    try {
      // Instantiate a BluetoothSocket for the remote device and connect it.
      sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
      sock.connect();
    } catch (Exception e1) {
      Log.e(TAG, "There was an error while establishing Bluetooth connection. Falling back..", e1);
      Class<?> clazz = sock.getRemoteDevice().getClass();
      Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
      try {
        Method m = clazz.getMethod("createRfcommSocket", paramTypes);
        Object[] params = new Object[]{Integer.valueOf(1)};
        sockFallback = (BluetoothSocket) m.invoke(sock.getRemoteDevice(), params);
        sockFallback.connect();
        sock = sockFallback;
      } catch (Exception e2) {
        Log.e(TAG, "Couldn't fallback while establishing Bluetooth connection. Stopping app..", e2);
        stopService();
        throw new IOException();
      }
    }

    // Let's configure the connection.
    Log.d(TAG, "Queing jobs for connection configuration..");
    queueJob(new ObdCommandJob(new ObdResetCommand()));
    queueJob(new ObdCommandJob(new EchoOffObdCommand()));

    /*
     * Will send second-time based on tests.
     *
     * TODO this can be done w/o having to queue jobs by just issuing
     * command.run(), command.getResult() and validate the result.
     */
    queueJob(new ObdCommandJob(new EchoOffObdCommand()));
    queueJob(new ObdCommandJob(new LineFeedOffObdCommand()));
    queueJob(new ObdCommandJob(new TimeoutObdCommand(62)));

    // Get protocol from preferences
    String protocol = prefs.getString(PROTOCOLS_LIST_KEY,"AUTO");
    queueJob(new ObdCommandJob(new SelectProtocolObdCommand(ObdProtocols.valueOf(protocol))));

    // Job for returning dummy data
    queueJob(new ObdCommandJob(new AmbientAirTemperatureObdCommand()));

    queueCounter = 0L;
    Log.d(TAG, "Initialization jobs queued.");


  }

  /**
   * Runs the queue until the service is stopped
   */
  protected void executeQueue() throws InterruptedException{
    Log.d(TAG, "Executing queue..");
    while (!Thread.currentThread().isInterrupted()) {
      ObdCommandJob job = null;
      try {
        job = jobsQueue.take();

        // log job
        Log.d(TAG, "Taking job[" + job.getId() + "] from queue..");

        if (job.getState().equals(ObdCommandJobState.NEW)) {
          Log.d(TAG, "Job state is NEW. Run it..");
          job.setState(ObdCommandJobState.RUNNING);
          job.getCommand().run(sock.getInputStream(), sock.getOutputStream());
        } else
          // log not new job
          Log.e(TAG,
              "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
            } catch (InterruptedException i) {
                Thread.currentThread().interrupt();
      } catch (Exception e) {
        job.setState(ObdCommandJobState.EXECUTION_ERROR);
        Log.e(TAG, "Failed to run command. -> " + e.getMessage());
      }

      if (job != null) {
        final ObdCommandJob job2=job;
        /*TODO find out how to return result to caller thread
        ((MainActivity) ctx).runOnUiThread(new Runnable() {
          @Override
          public void run() {
            ((MainActivity) ctx).stateUpdate(job2);
          }
        });*/
      }
    }
  }

  /**
  * Stop OBD connection and queue processing.
  */
  public void stopService() {
    Log.d(TAG, "Stopping service..");

    notificationManager.cancel(NOTIFICATION_ID);
    jobsQueue.removeAll(jobsQueue); // TODO is this safe?
    isRunning = false;

    if (sock != null)
      // close socket
      try {
        sock.close();
      } catch (IOException e) {
        Log.e(TAG, e.getMessage());
      }

    // kill service
    stopSelf();
  }

  public boolean isRunning() {
    return isRunning;
  }

  public class ObdGatewayServiceBinder extends Binder {
    public ObdGatewayService getService() {
      return ObdGatewayService.this;
    }
  }

}