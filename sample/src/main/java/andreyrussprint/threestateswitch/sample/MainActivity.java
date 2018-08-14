package andreyrussprint.threestateswitch.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import andreyrussprint.threestateswitch.ThreeStateSwitch;
import andreyrussprint.threestateswitch.states.SwitchStates;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ThreeStateSwitch threeStateSwitch = (ThreeStateSwitch) findViewById(R.id.threeStateSwitch);

        threeStateSwitch.setOnChangeListener(new ThreeStateSwitch.OnStateChangeListener() {
            @Override
            public void onStateChangeListener(SwitchStates currentState) {
                Toast.makeText(MainActivity.this, currentState.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
