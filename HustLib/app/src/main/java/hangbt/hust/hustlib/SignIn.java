package hangbt.hust.hustlib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hangbt.hust.hustlib.Model.Common;
import hangbt.hust.hustlib.Model.Food_Cart;
import hangbt.hust.hustlib.Model.User;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    EditText edtEmail, edtPass, edtForgotPass;
    Button btnSignIn;
    CheckBox checkBox;
    TextView forgotPass;

    FirebaseAuth mAuth;
    DatabaseReference data;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        anhXa();

        Paper.init(this);

        data = FirebaseDatabase.getInstance().getReference();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    String email = edtEmail.getText().toString();
                    String pass = edtPass.getText().toString();
                    if(email.equals("")||pass.equals("")){
                        Toast.makeText(SignIn.this, "Nhập thiếu thông tin", Toast.LENGTH_SHORT).show();
                    }else {
                        //test notification

                        signIn(edtEmail.getText().toString(), edtPass.getText().toString());
                    }
                }else{
                    Toast.makeText(SignIn.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgot();
            }
        });
    }

    private void showForgot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Điền email đặt lại mật khẩu");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.forgot_password,null);

        edtForgotPass = view.findViewById(R.id.email);

        builder.setView(view);
        builder.setCancelable(false);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if(edtForgotPass.getText().toString().equals("")){
                    Toast.makeText(SignIn.this,"Chưa điền email",Toast.LENGTH_SHORT).show();
                }else {
                    String email = edtForgotPass.getText().toString();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignIn.this,"Đã gửi email đặt lại mật khẩu",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(SignIn.this,""+task.getException(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void signIn(String email, String pass){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
                            data.child("User").child(u.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child("isStaff").getValue().toString().equals("false")){
//                                        final ProgressDialog nDialog = new ProgressDialog(SignIn.this);
//                                        nDialog.setMessage("Please waiting... ");
//                                        nDialog.show();
                                        findUser(u.getUid());
                                    }else{
                                        Toast.makeText(SignIn.this,"Vui lòng đăng nhập bằng tài khoản Khách hàng!",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }else{
                            Toast.makeText(SignIn.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void findUser(String id){
        user.setUserId(id);
        data.child("User").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(checkBox.isChecked()) {
                    Paper.book().write(Common.USER, user.getEmail());
                    Paper.book().write(Common.PWUSER, user.getPassword());
                }
                Common.currenUser = user;
                Intent homeIntent = new Intent(SignIn.this, Home.class);
                startActivity(homeIntent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void anhXa(){
        btnSignIn = findViewById(R.id.btnSignIn);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);
        checkBox = findViewById(R.id.ckbRemember);
        forgotPass = findViewById(R.id.forgotpass);
    }

}
