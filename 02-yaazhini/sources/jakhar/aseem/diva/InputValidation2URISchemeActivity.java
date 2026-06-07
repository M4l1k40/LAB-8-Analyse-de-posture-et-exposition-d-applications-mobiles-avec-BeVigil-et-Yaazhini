package jakhar.aseem.diva;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

public class InputValidation2URISchemeActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_input_validation2_urischeme);
        ((WebView) findViewById(R.id.ivi2wview)).getSettings().setJavaScriptEnabled(true);
    }

    public void get(View view) {
        ((WebView) findViewById(R.id.ivi2wview)).loadUrl(((EditText) findViewById(R.id.ivi2uri)).getText().toString());
    }
}
