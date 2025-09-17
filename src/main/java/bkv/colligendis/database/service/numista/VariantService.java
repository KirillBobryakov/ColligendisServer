package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Variant;
import bkv.colligendis.rest.dto.NTypeVariantDTO;
import bkv.colligendis.services.AbstractService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VariantService extends AbstractService<Variant, VariantRepository> {

    private final ModelMapper modelMapper;

    public VariantService(VariantRepository repository, ModelMapper modelMapper) {
        super(repository);
        this.modelMapper = modelMapper;
    }

    public Variant findByNid(String nid) {
        Variant mark = repository.findByNid(nid);
        if (mark == null) {
            return repository.save(new Variant(nid));
        }
        return mark;
    }

    public List<Integer> getYearsOfVariantsByIssuerEid(String eid) {

        List<Integer> gregorianYears = repository.getYearsOfVariantsByIssuerEid(eid);
        Set<Integer> years = new HashSet<>(Objects.requireNonNull(gregorianYears));

        List<Variant> variantFlux = repository.getBetweenMinMaxYearsOfVariantsByIssuerEid(eid);

        variantFlux.forEach(variant -> {
            if (variant.getMinYear() != null && variant.getMaxYear() != null) {
                if (variant.getMinYear() != Integer.MIN_VALUE && variant.getMaxYear() != Integer.MAX_VALUE) {
                    for (int i = variant.getMinYear(); i <= variant.getMaxYear(); i++) {
                        years.add(i);
                    }
                }
            }
        });
        return years.stream().sorted(Integer::compareTo).collect(Collectors.toList());
    }

    public List<NTypeVariantDTO> getVariantsByNTypeNid(String nTypeNid) {
        List<Variant> variants = repository.getVariantsByNTypeNid(nTypeNid);
        return variants.stream().map(variant -> modelMapper.map(variant, NTypeVariantDTO.class))
                .collect(Collectors.toList());
    }

    // Statistics

    public Integer countVariantsByCountryNumistaCodeAndCollectibleTypeCode(String numistaCode,
            String collectibleTypeCode) {
        return repository.countVariantsByCountryNumistaCodeAndCollectibleTypeCode(numistaCode, collectibleTypeCode);
    }

    public Integer countVariantsBySubjectNumistaCodeAndCollectibleTypeCode(String numistaCode,
            String collectibleTypeCode) {
        return repository.countVariantsBySubjectNumistaCodeAndCollectibleTypeCode(numistaCode, collectibleTypeCode);
    }

    public Integer countVariantsByIssuerCodeAndCollectibleTypeCode(String code, String collectibleTypeCode) {
        return repository.countVariantsByIssuerCodeAndCollectibleTypeCode(code, collectibleTypeCode);
    }

}
