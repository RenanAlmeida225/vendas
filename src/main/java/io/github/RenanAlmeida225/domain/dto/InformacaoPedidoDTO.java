package io.github.RenanAlmeida225.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformacaoPedidoDTO {
    private Integer id;
    private String cpf;
    private String nomeCLiente;
    private String dataPedido;
    private String status;
    private List<InformacaoItemPedidoDTO> items;
}
