package jakhar.aseem.diva;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class InputValidation3Activity extends AppCompatActivity {
    private DivaJni djni;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_input_validation3);
        this.djni = new DivaJni();
    }

    public void push(View view) {
        if (this.djni.initiateLaunchSequence(((EditText) findViewById(R.id.ivi3CodeText)).getText().toString()) != 0) {
            Toast.makeText(this, "Launching in T - 10 ...", 0).show();
        } else {
            Toast.makeText(this, "Access denied!", 0).show();
        }
    }
}
