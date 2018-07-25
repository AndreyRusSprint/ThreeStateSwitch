package ir.oveissi.threestateswitch.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ir.oveissi.threestateswitch.ThreeStateSwitch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ThreeStateSwitch threeState = (ThreeStateSwitch) findViewById(R.id.threeState);

        threeState.setOnChangeListener(new ThreeStateSwitch.OnStateChangeListener() {
            @Override
            public void onStateChangeListener(int currentState) {
                Toast.makeText(MainActivity.this, String.valueOf(currentState), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
