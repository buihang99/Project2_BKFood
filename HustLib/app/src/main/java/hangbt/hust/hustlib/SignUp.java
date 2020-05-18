package hangbt.hust.hustlib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hangbt.hust.hustlib.Model.Common;
import hangbt.hust.hustlib.Model.User;

public class SignUp extends AppCompatActivity {

    Button btnSignUp;
    EditText edtFullname, edtEmail, edtPhone, edtPassword, edtRePass;
    long total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        anhXa();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog nDialog = new ProgressDialog(SignUp.this);
                    nDialog.setMessage("Please waiting... ");
                    nDialog.show();

                    User u = new User();
                    String pass = edtPassword.getText().toString();
                    String repass = edtRePass.getText().toString();
                    String email = edtEmail.getText().toString();
                    String fullname = edtFullname.getText().toString();
                    String phone = edtPhone.getText().toString();

                    if(fullname.equals("")||email.equals("")||pass.equals("")||phone.equals("")){
                        Toast.makeText(SignUp.this,"Nhập thiếu thông tin!!",Toast.LENGTH_SHORT).show();
                    }else{
                        if(pass.length() < 6){
                            Toast.makeText(SignUp.this,"Mật khẩu dài ít nhất 6 kí tự!",Toast.LENGTH_SHORT).show();
                        }else {
                            if(!pass.equals(repass)){
                                Toast.makeText(SignUp.this,"Mật khẩu không khớp!",Toast.LENGTH_SHORT).show();
                            }else {
                                u.setFullname(fullname);
                                u.setEmail(email);
                                u.setPhone(phone);
                                u.setPassword(pass);
                                u.setIsStaff("false");

                                addUser(u);
                            }
                        }
                    }
                }else{
                    Toast.makeText(SignUp.this,"Please check your connection!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addUser(final User user){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(),user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser data = FirebaseAuth.getInstance().getCurrentUser();
                            String id = data.getUid();
                            addUsertoDb(user,id);
                            Toast.makeText(SignUp.this,"Success",Toast.LENGTH_SHORT).show();
                            Intent signup = new Intent(SignUp.this,SignIn.class);
                            startActivity(signup);
                            finish();

                        }else{
                            Toast.makeText(SignUp.this,"Email chưa đúng hoặc đã tồn tại!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void addUsertoDb(User u, String userId){
        DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();
        database.child("User").child(userId).setValue(u);
        //database.child("Cart").child(userId).child("total").setValue(total);
    }

    private void anhXa(){
        btnSignUp = findViewById(R.id.btnSignUp);
        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePass = findViewById(R.id.edtRePass);
    }
}
