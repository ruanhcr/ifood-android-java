package com.ruru.ifood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ruru.ifood.R;
import com.ruru.ifood.helper.ConfiguracaoFirebase;
import com.ruru.ifood.helper.UsuarioFirebase;
import com.ruru.ifood.model.Empresa;
import com.ruru.ifood.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Adicionar novo produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inicializarComponentes();
    }

    public void validarDadosProduto(View view){
        String nome = editProdutoNome.getText().toString();
        String preco = editProdutoPreco.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();

        if(!nome.isEmpty()){
            if(!descricao.isEmpty()){
                if(!preco.isEmpty()){
                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.salvar();
                    finish();
                    exibirMensagem("Produto salvo com sucesso!");

                }else{
                    exibirMensagem("Digite um preço para o produto!");
                }
            }else{
                exibirMensagem("Digite uma descrição para o produto!");
            }
        }else{
            exibirMensagem("Digite um nome para o produto!");
        }
    }
    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes(){
        editProdutoNome = findViewById(R.id.editNomeProduto);
        editProdutoDescricao = findViewById(R.id.editDescricaoProduto);
        editProdutoPreco = findViewById(R.id.editPrecoProduto);
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
    }
}