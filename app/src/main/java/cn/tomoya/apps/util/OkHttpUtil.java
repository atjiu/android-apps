package cn.tomoya.apps.util;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liygh on 2017/4/8.
 */

public class OkHttpUtil {

  public static void fetchData(final Context context, String url, boolean sendRequest, final Callback callback) {
    if (sendRequest) {
      Request request = new Request.Builder().url(url).build();
      OkHttpClient client = new OkHttpClient();
      Call call = client.newCall(request);
      call.enqueue(new okhttp3.Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
          callback.output(response.body().string());
        }
      });
    } else {
      callback.output(null);
    }
  }
}
