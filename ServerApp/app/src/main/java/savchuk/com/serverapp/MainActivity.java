package savchuk.com.serverapp;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class MainActivity extends Activity {
    Server server;
    TextView ipInfo, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipInfo = (TextView) findViewById(R.id.ipInfo);
        message = (TextView) findViewById(R.id.message);
        server = new Server(this);
        ipInfo.setText(server.getIpAddress() + ":" + server.getPort());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }
}
