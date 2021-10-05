package com.ruru.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.ruru.ifood.R;
import com.ruru.ifood.helper.ConfiguracaoFirebase;
import com.ruru.ifood.helper.UsuarioFirebase;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private FirebaseAuth autenticacao;
    private LinearLayout linearTipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        inicializarComponentes();
        verificarUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){//empresa
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                }else{//usuario
                    linearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                    if (!textoEmail.isEmpty()){
                        if (!textoSenha.isEmpty()){
                            if(tipoAcesso.isChecked()){//cadastro
                                autenticacao.createUserWithEmailAndPassword(textoEmail, textoSenha)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            String tipoUsuario = getTipoUsuario();
                                            UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                            abrirTelaPrincipal(tipoUsuario);
                                        }else{
                                            String excesao = "";
                                            try{
                                                throw task.getException();
                                            }catch(FirebaseAuthWeakPasswordException e){
                                                excesao = "Digite uma senha mais forte!";
                                            }catch(FirebaseAuthInvalidCredentialsException e){
                                                excesao = "Digite um email válido!";
                                            }catch (FirebaseAuthUserCollisionException e){
                                                excesao = "Essa conta já foi cadastrada!";
                                            }catch(Exception e){
                                                excesao = "Erro ao cadastrar usuário!" + e.getMessage();
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(AutenticacaoActivity.this, excesao, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }else{//login
                                autenticacao.signInWithEmailAndPassword(textoEmail, textoSenha)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            String tipoUsuario = task.getResult().getUser().getDisplayName();
                                            abrirTelaPrincipal(tipoUsuario);
                                        }else{
                                            Toast.makeText(AutenticacaoActivity.this, "Erro ao fazer login: " + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }else{
                            Toast.makeText(AutenticacaoActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(AutenticacaoActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
                    }

            }
        });
    }

    private void abrirTelaPrincipal(String tipoUsuario){
    if(tipoUsuario.equals("E")){//empresa
        startActivity(new Intent(getApplicationContext(), PedidosActivity.class));
    }else{//usuario
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}

    private void verificarUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if(usuarioAtual != null){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
}

    private String getTipoUsuario(){
        return tipoUsuario.isChecked() ? "E" : "U";
    }

    private void inicializarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }
}