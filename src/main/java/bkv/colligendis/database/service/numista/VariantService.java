package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Variant;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VariantService extends AbstractService<Variant, VariantRepository> {
   public VariantService(VariantRepository repository) {
      super(repository);
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

   public List<Variant> getVariantsByNTypeNid(String nid) {
      return repository.getVariantsByNTypeNid(nid);
   }

}
