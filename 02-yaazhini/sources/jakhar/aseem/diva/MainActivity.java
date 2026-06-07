package jakhar.aseem.diva;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startChallenge(View view) {
        if (view == findViewById(R.id.d1button)) {
            startActivity(new Intent(this, LogActivity.class));
        } else if (view == findViewById(R.id.d2button)) {
            startActivity(new Intent(this, HardcodeActivity.class));
        } else if (view == findViewById(R.id.d3button)) {
            startActivity(new Intent(this, InsecureDataStorage1Activity.class));
        } else if (view == findViewById(R.id.d4button)) {
            startActivity(new Intent(this, InsecureDataStorage2Activity.class));
        } else if (view == findViewById(R.id.d5button)) {
            startActivity(new Intent(this, InsecureDataStorage3Activity.class));
        } else if (view == findViewById(R.id.d6button)) {
            startActivity(new Intent(this, InsecureDataStorage4Activity.class));
        } else if (view == findViewById(R.id.d7button)) {
            startActivity(new Intent(this, SQLInjectionActivity.class));
        } else if (view == findViewById(R.id.d8button)) {
            startActivity(new Intent(this, InputValidation2URISchemeActivity.class));
        } else if (view == findViewById(R.id.d9button)) {
            startActivity(new Intent(this, AccessControl1Activity.class));
        } else if (view == findViewById(R.id.d10button)) {
            startActivity(new Intent(this, AccessControl2Activity.class));
        } else if (view == findViewById(R.id.d11button)) {
            startActivity(new Intent(this, AccessControl3Activity.class));
        } else if (view == findViewById(R.id.d12button)) {
            startActivity(new Intent(this, Hardcode2Activity.class));
        } else if (view == findViewById(R.id.d13button)) {
            startActivity(new Intent(this, InputValidation3Activity.class));
        }
    }
}
