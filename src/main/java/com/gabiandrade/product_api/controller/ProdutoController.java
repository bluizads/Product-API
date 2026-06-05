package com.gabiandrade.product_api.controller;

import com.gabiandrade.product_api.model.Produto;
import com.gabiandrade.product_api.service.ProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // Recurso HTTP /produtos
    // Verbos HTTP
    // GET: Visualizar, listar, pesquisar (NAO MODIFICA NADA NO SERVIDOR, NAO É IDEMPOTENTE) 
    // POST:   MUDA ALGO NO SERVIDOR, NESTE CASO, ADICIONA UM NOVO PRODUTO
    // PUT:    MUDA ALGO NO SERVIDOR, ALTERA COMPLETAMENTE O RECURSO
    // DELETE: MUDA ALGO NO SERVIDOR, DELETA O RECURSO
    // PATCH:  MUDA ALGO NO SERVIDOR, ALTERA PARCIALMENTE O RECURSO

    @GetMapping()
    public ResponseEntity<Page<Produto>> getProdutos(
            @RequestParam(required = false) String descricao,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Ensure size is at most 10
        int validSize = Math.min(size, 10);
        
        // Spring Data pages are 0-indexed, so if user requests page 1, we pass 0
        int validPage = Math.max(0, page - 1);
        
        Pageable pageable = PageRequest.of(validPage, validSize);
        System.out.println(">>>> Alguém pediu a lista de produtos? <<<<");
        System.out.println("Listando produtos... método getProdutos()");
        return ResponseEntity.ok(produtoService.getProdutos(descricao, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> getProdutoId(@PathVariable Long id) {
        Optional<Produto> produto = produtoService.getProdutoId(id);
        System.out.printf(">>>> Alguém pediu o produto com id %d <<<<\n", id);
        return produto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping()
    public ResponseEntity<Produto> criarProduto(@RequestBody Produto produto) {
        Produto criado = produtoService.criarProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id,
                                                    @RequestBody Produto produto) {
        try {
            return ResponseEntity.ok(produtoService.atualizarProduto(id, produto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Produto> atualizarProdutoParcial(@PathVariable Long id,
                                                    @RequestBody Map<String, Object> campos) {
        try {
            return ResponseEntity.ok(produtoService.atualizarProdutoParcial(id, campos));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        try {
            produtoService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/contagem")
    public ResponseEntity<Long> contarProdutos() {
        long total = produtoService.contarProdutos();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/filtro-preco")
    public ResponseEntity<Page<Produto>> getPorPreco(
            @RequestParam double min,
            @RequestParam double max,
            @RequestParam(defaultValue="1") int page) {
        Pageable pageable = PageRequest.of(page-1, 10);
        return ResponseEntity.ok(produtoService.buscarPorPreco(min, max, pageable));
    }

}
