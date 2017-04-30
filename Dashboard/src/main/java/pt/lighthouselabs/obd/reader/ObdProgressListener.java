package pt.lighthouselabs.obd.reader;

import org.vopen.android_sdk.obd_service.ObdCommandJob;

/**
 * TODO put description
 */
public interface ObdProgressListener {

  void stateUpdate(final ObdCommandJob job);

}