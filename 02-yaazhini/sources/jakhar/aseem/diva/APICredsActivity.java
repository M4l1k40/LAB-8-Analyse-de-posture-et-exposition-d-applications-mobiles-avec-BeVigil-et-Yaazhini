package jakhar.aseem.diva;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class APICredsActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_apicreds);
        ((TextView) findViewById(R.id.apicTextView)).setText("API Key: 123secretapikey123\nAPI User name: diva\nAPI Password: p@ssword");
    }
}
