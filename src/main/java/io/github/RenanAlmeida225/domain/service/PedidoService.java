package io.github.RenanAlmeida225.domain.service;

import java.util.Optional;

import io.github.RenanAlmeida225.domain.dto.PedidoDTO;
import io.github.RenanAlmeida225.domain.entity.Pedido;
import io.github.RenanAlmeida225.domain.enums.StatusPedido;

public interface PedidoService {
    Pedido save(PedidoDTO dto);

    Optional<Pedido> obterPedidoCompleto(Integer id);

    void atualizarStatus(Integer id, StatusPedido statusPedido);
}
