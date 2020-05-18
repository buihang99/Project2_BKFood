package hangbt.hust.bkfoodserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import hangbt.hust.bkfoodserver.Model.Common;
import hangbt.hust.bkfoodserver.Model.User;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    EditText edtEmail, edtPass;
    Button btnSignIn;
    CheckBox checkBox;

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
                        final ProgressDialog nDialog = new ProgressDialog(SignIn.this);
                        nDialog.setMessage("Please waiting... ");
                        nDialog.show();

                        signIn(edtEmail.getText().toString(), edtPass.getText().toString());
                    }
                }else{
                    Toast.makeText(SignIn.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                                    if(dataSnapshot.child("isStaff").getValue().toString().equals("true")){
                                        findUser(u.getUid());
                                    }else{
                                        Toast.makeText(SignIn.this,"Vui lòng đăng nhập bằng tài khoản Nhân viên!",Toast.LENGTH_SHORT).show();
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
        data.child("User").child(id).addValueEventListener(new ValueEventListener() {
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
    }

}
