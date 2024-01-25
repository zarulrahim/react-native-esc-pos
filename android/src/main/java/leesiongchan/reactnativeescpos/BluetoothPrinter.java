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

  public void open() {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        try {
          BluetoothDevice device = adapter.getRemoteDevice(address);
          BluetoothSocket socket = device.createRfcommSocketToServiceRecord(
            SPP_UUID
          );
          socket.connect();
          printer = socket.getOutputStream();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }
    }
      .execute();
  }

  public void write(final byte[] command) {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        try {
          if (printer != null) {
            printer.write(command);
          } else {
            return null;
          }
        } catch (IOException e) {
          e.printStackTrace();
          return null;
        }
        return null;
      }
    }
      .execute();
  }

  public void close() {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        try {
          if (printer != null) {
            printer.close();
          } else {
            // Handle the case where the printer is not available
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }
    }
      .execute();
  }
}
