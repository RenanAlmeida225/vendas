package io.github.RenanAlmeida225.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.RenanAlmeida225.domain.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

}
