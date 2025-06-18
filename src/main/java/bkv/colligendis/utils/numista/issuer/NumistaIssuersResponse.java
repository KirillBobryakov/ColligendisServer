package bkv.colligendis.utils.numista.issuer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumistaIssuersResponse {
    private List<NumistaIssuerResultItem> results;
    private NumistaIssuerPaginationInfo pagination;
}


