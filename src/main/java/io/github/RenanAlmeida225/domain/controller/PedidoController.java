package io.github.RenanAlmeida225.domain.controller;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import io.github.RenanAlmeida225.domain.dto.AtualizacaoStatusPedidoDTO;
import io.github.RenanAlmeida225.domain.dto.InformacaoItemPedidoDTO;
import io.github.RenanAlmeida225.domain.dto.InformacaoPedidoDTO;
import io.github.RenanAlmeida225.domain.dto.PedidoDTO;
import io.github.RenanAlmeida225.domain.entity.ItemPedido;
import io.github.RenanAlmeida225.domain.entity.Pedido;
import io.github.RenanAlmeida225.domain.enums.StatusPedido;
import io.github.RenanAlmeida225.domain.service.PedidoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("pedido")
public class PedidoController {

    @Autowired
    private PedidoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer save(@RequestBody @Valid PedidoDTO dto) {
        var pedido = service.save(dto);
        return pedido.getId();
    }

    @GetMapping("{id}")
    public InformacaoPedidoDTO getById(@PathVariable Integer id) {
        return service
                .obterPedidoCompleto(id)
                .map(p -> converter(p))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable Integer id, @RequestBody AtualizacaoStatusPedidoDTO dto) {
        service.atualizarStatus(id, StatusPedido.valueOf(dto.getNovoStatus()));
    }

    private InformacaoPedidoDTO converter(Pedido pedido) {
        return InformacaoPedidoDTO
                .builder()
                .id(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCLiente(pedido.getCliente().getNome())
                .status(pedido.getStatus().name())
                .items(converter(pedido.getItens())).build();
    }

    private List<InformacaoItemPedidoDTO> converter(List<ItemPedido> itens) {
        if (CollectionUtils.isEmpty(itens)) {
            return Collections.emptyList();
        }

        return itens.stream().map(item -> InformacaoItemPedidoDTO
                .builder()
                .descricaoProduto(item.getProduto().getDescricao())
                .precoUnitario(item.getProduto().getPreco())
                .quantidade(item.getQuantidade())
                .build()).collect(Collectors.toList());
    }
}
