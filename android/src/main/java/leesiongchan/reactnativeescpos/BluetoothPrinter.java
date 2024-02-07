package leesiongchan.reactnativeescpos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import io.github.escposjava.print.Printer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothPrinter implements Printer {

  private static final UUID SPP_UUID = UUID.fromString(
    "00001101-0000-1000-8000-00805F9B34FB"
  );
  private BluetoothAdapter adapter;

  private OutputStream printer = null;
  private final String address;

  public BluetoothPrinter(String address) {
    adapter = BluetoothAdapter.getDefaultAdapter();
    this.address = address;
  }

  private class ConnectTask extends AsyncTask<Void, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Void... params) {
      try {
        BluetoothDevice device = adapter.getRemoteDevice(address);
        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(
          SPP_UUID
        );

        // This is a blocking operation, so move it to a background thread
        socket.connect();
        printer = socket.getOutputStream();
        return true;
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
    }

    @Override
    protected void onPostExecute(Boolean success) {
      if (!success) {
        // Handle connection failure if needed
      }
    }
  }

  public void open() {
    new ConnectTask().execute();
  }

  private class WriteTask extends AsyncTask<byte[], Void, Void> {

    @Override
    protected Void doInBackground(byte[]... params) {
      try {
        if (printer != null) {
          printer.write(params[0]);
        }
      } catch (IOException e) {
        // Handle the exception as needed
        e.printStackTrace();
      }
      return null;
    }
  }

  public void write(byte[] command) {
    new WriteTask().execute(command);
  }

  private class CloseTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
      try {
        if (printer != null) {
          printer.close();
        }
      } catch (IOException e) {
        // Handle the exception as needed
        e.printStackTrace();
      }
      return null;
    }
  }

  public void close() {
    new CloseTask().execute();
  }
}
