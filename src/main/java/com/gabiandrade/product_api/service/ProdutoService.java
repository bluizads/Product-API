package com.gabiandrade.product_api.service;

import com.gabiandrade.product_api.model.Produto;
import com.gabiandrade.product_api.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Page<Produto> getProdutos(String descricao, Pageable pageable) {
        if (descricao != null && !descricao.isEmpty()) {
            return produtoRepository.findByNomeContainingIgnoreCase(descricao, pageable);
        }
        return produtoRepository.findAll(pageable);
    }

    public Produto criarProduto(Produto produto){
        return produtoRepository.save(produto);
    }

    public Optional<Produto> getProdutoId(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto atualizarProduto(Long id, Produto produto) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            produtoExistente.setNome(produto.getNome());
            produtoExistente.setPreco(produto.getPreco());
            return produtoRepository.save(produtoExistente);
        }).orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
    }

    public Produto atualizarProdutoParcial(Long id, Map<String, Object> campos) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        if (campos.containsKey("nome")) {
            produtoExistente.setNome((String) campos.get("nome"));
        }
        if (campos.containsKey("preco")) {
            Object precoObj = campos.get("preco");
            if (precoObj instanceof Number) {
                produtoExistente.setPreco(((Number) precoObj).doubleValue());
            } else if (precoObj instanceof String) {
                produtoExistente.setPreco(Double.parseDouble((String) precoObj));
            }
        }

        return produtoRepository.save(produtoExistente);
    }

    public void deletarProduto(Long id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Produto não encontrado com id: " + id);
        }
    }

    public long contarProdutos() {
        return produtoRepository.count();
    }

    public Page<Produto> buscarPorPreco (@Param("min") double min, @Param("max") double max, Pageable pageable){
        return produtoRepository.findByPrecoBetween(min, max, pageable);
    }
}
