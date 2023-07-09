package io.github.RenanAlmeida225.domain.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.RenanAlmeida225.domain.dto.ItemPedidoDTO;
import io.github.RenanAlmeida225.domain.dto.PedidoDTO;
import io.github.RenanAlmeida225.domain.entity.ItemPedido;
import io.github.RenanAlmeida225.domain.entity.Pedido;
import io.github.RenanAlmeida225.domain.entity.Produto;
import io.github.RenanAlmeida225.domain.enums.StatusPedido;
import io.github.RenanAlmeida225.domain.exception.PedidoNaoEncontradoException;
import io.github.RenanAlmeida225.domain.exception.RegraNegocioExeption;
import io.github.RenanAlmeida225.domain.repository.ClienteRepository;
import io.github.RenanAlmeida225.domain.repository.ItemPedidoRepository;
import io.github.RenanAlmeida225.domain.repository.PedidoRepository;
import io.github.RenanAlmeida225.domain.repository.ProdutoRepository;
import io.github.RenanAlmeida225.domain.service.PedidoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository repository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    @Override
    @Transactional
    public Pedido save(PedidoDTO dto) {
        var cliente = clienteRepository
                .findById(dto.getCliente())
                .orElseThrow(() -> new RegraNegocioExeption("codigo do cliente invalido."));
        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);
        var itemsPedido = consverItems(pedido, dto.getItems());
        pedido.setItens(itemsPedido);
        repository.save(pedido);
        itemPedidoRepository.saveAll(itemsPedido);
        pedido.setItens(itemsPedido);
        return pedido;
    }

    private List<ItemPedido> consverItems(Pedido pedido, List<ItemPedidoDTO> items) {
        if (items.isEmpty()) {
            throw new RegraNegocioExeption("não é possivel realizar um pedido sem items.");
        }
        return items
                .stream()
                .map(dto -> {
                    Produto produto = produtoRepository
                            .findById(dto.getProduto())
                            .orElseThrow(() -> new RegraNegocioExeption("codigo do Pedido invalido."));
                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setProduto(produto);
                    itemPedido.setPedido(pedido);
                    return itemPedido;
                }).collect(Collectors.toList());
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return repository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizarStatus(Integer id, StatusPedido statusPedido) {
        repository.findById(id).map(pedido -> {
            pedido.setStatus(statusPedido);
            return repository.save(pedido);
        }).orElseThrow(() -> new PedidoNaoEncontradoException());
    }

}
