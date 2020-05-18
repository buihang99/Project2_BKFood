package hangbt.hust.bkfoodserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnSignin;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anhXa();

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signin = new Intent(MainActivity.this, SignIn.class);
                startActivity(signin);
                finish();
            }
        });
    }

    private void anhXa(){
        btnSignin = findViewById(R.id.btnSignIn);
        txtSlogan = findViewById(R.id.txtSlogan);
    }
}
