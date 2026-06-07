package jakhar.aseem.diva;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class APICreds2Activity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_apicreds2);
        TextView apicview = (TextView) findViewById(R.id.apic2TextView);
        EditText pintext = (EditText) findViewById(R.id.aci2pinText);
        Button vbutton = (Button) findViewById(R.id.aci2button);
        if (!getIntent().getBooleanExtra(getString(R.string.chk_pin), true)) {
            apicview.setText("TVEETER API Key: secrettveeterapikey\nAPI User name: diva2\nAPI Password: p@ssword2");
            return;
        }
        apicview.setText("Register yourself at http://payatu.com to get your PIN and then login with that PIN!");
        pintext.setVisibility(0);
        vbutton.setVisibility(0);
    }

    public void viewCreds(View view) {
        Toast.makeText(this, "Invalid PIN. Please try again", 0).show();
    }
}
